package org.paternostro.elkromm.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.invoke.MethodHandles;
import java.net.InetAddress;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.TextHelpAppendable;
import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.ElkrommFactory;
import org.paternostro.elkromm.tools.model.Model;
import org.paternostro.elkromm.tools.model.actions.ModelActionFactory;
import org.paternostro.elkromm.tools.model.actions.ModelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyConfig {
    public static final Logger  logger = LoggerFactory.getLogger(CopyConfig.class);
    
    public static final String  MAGIC = "MP508V02";
    public static final int     DEFAULT_PORT = 8030;
    public static final int     DEFAULT_PLANT_CODE = 55555555;
    public static final String  ENV_PIN_NAME = "ELKROMM_PIN";

    public static void main(String[] args) throws IOException, ElkrommException
    {
        Options     options = new Options();

        // source options group
        OptionGroup optionGroup = new OptionGroup();
        Option      help = Option.builder("h")
                                .longOpt("help")
                                .desc("prints this help and exits")
                                .get();
        Option      file = Option.builder("f")
                                .longOpt("file")
                                .hasArg()
                                .desc("file to read (use \"-\" to read from standard input)")
                                .type(FileInputStream.class)
                                .get();
        Option      host = Option.builder("H")
                                .longOpt("host")
                                .hasArg()
                                .desc("IP address or hostname of the panel")
                                .get();

        optionGroup.addOption(help);
        optionGroup.addOption(file);
        optionGroup.addOption(host);
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);

        // optional options
        Option      pCode = Option.builder("c")
                                .longOpt("plant-code")
                                .hasArg()
                                .desc("plant code (default: " + DEFAULT_PLANT_CODE + ")")
                                .type(Integer.class)
                                .get();
        
        options.addOption(pCode);

        // optional options (dependant on source)
        Option      port = Option.builder("P")
                                .longOpt("port")
                                .hasArg()
                                .type(Integer.class)
                                .desc("TCP port of the panel (default: " + DEFAULT_PORT + ")")
                                .get();
        Option      tPin = Option.builder("t")
                                .longOpt("tech-pin")
                                .hasArg()
                                .desc("technician user PIN (if omitted, read from env " + ENV_PIN_NAME + " or prompted)")
                                .type(Integer.class)
                                .get();

        options.addOption(port);
        options.addOption(tPin);

        // outputs options
        Option      backup = Option.builder("b")
                                .longOpt("backup")
                                .hasArg()
                                .desc("file to write (use \"-\" to write to standard output)")
                                .type(File.class)
                                .get();
        Option      json = Option.builder("j")
                                .longOpt("json")
                                .hasArg()
                                .desc("JSON file to write (use \"-\" to write to standard output)")
                                .type(File.class)
                                .get();
        Option      props = Option.builder("p")
                                .longOpt("properties")
                                .hasArg()
                                .desc("properties file to write (use \"-\" to write to standard output)")
                                .type(File.class)
                                .get();

        options.addOption(backup)
                .addOption(json)
                .addOption(props);

        CommandLineParser   parser = new DefaultParser();
        CommandLine         cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error parsing parameters", e);
            printHelpAndExit(options, 1, e.getMessage());
        }

        if (cmd.hasOption(help)) {
            printHelpAndExit(options, 0, null);
        }

        Option[]        outputs = { backup, json, props };
        int             outputCount = 0;
        int             stdoutUsage = 0;

        for (Option o : outputs) {
            if (cmd.hasOption(o)) {
                outputCount++;

                if ("-".equals(cmd.getOptionValue(o))) {
                    stdoutUsage++;
                }
            }
        }

        if (outputCount == 0) {
            logger.error("No output specified");
            printHelpAndExit(options, 1, "No output specified");
        }

        if (stdoutUsage > 1) {
            logger.error("Cannot use standard output more than once");
            printHelpAndExit(options, 1, "Cannot use standard output more than once");
        }

        int plantCode = DEFAULT_PLANT_CODE;

        if (cmd.hasOption(pCode)) {
            try {
                plantCode = cmd.getParsedOptionValue(pCode);
            } catch (ParseException e) {
                logger.error("Error parsing --\" + pCode.getLongOpt() + \" parameter", e);
                printHelpAndExit(options, 1, e.getMessage());
            }
        }

        ModelActionFactory  maf = ModelActionFactory.getFactory();
        ModelReader         reader;

        if (cmd.hasOption(host)) {
            // modalità connessione diretta: --pin è l'unico davvero obbligatorio
            // (o ricavabile da env/prompt), --port ha il default
            int pin = -1;

            if (cmd.hasOption(tPin)) {
                try {
                    pin = cmd.getParsedOptionValue(tPin);
                } catch (ParseException e) {
                    logger.error("Error parsing --\" + tPin.getLongOpt() + \" parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            } else {
                try {
                    pin = Integer.parseInt(System.getenv(ENV_PIN_NAME));
                } catch (Exception e) {
                    System.err.print("Insert technical user PIN: ");
                    pin = Integer.parseInt(new String(System.console().readPassword()));
                }
            }

            if (pin == -1) {
                logger.error("No PIN specified");
                printHelpAndExit(options, 1, "No PIN specified");
            }

            int portNum = DEFAULT_PORT;

            if (cmd.hasOption(port)) {
                try {
                    portNum = cmd.getParsedOptionValue(port);
                } catch (ParseException e) {
                    logger.error("Error parsing --\" + port.getLongOpt() + \" parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            }

            ElkrommFacade facade = ElkrommFactory.getFactory()
                                                .getElkrommFacade(
                                                    InetAddress.getByName(cmd.getOptionValue(host)), 
                                                    portNum, 
                                                    plantCode
                                                );

            facade.connect();;
            facade.login(plantCode, pin);

            reader = maf.getReader(Medium.PANEL, facade);
        } else {
            // modalità file: host/port/pin non hanno senso
            Option[] illegalOptions = {port, tPin};

            for (Option o : illegalOptions) {
                if (cmd.hasOption(o)) {
                    printHelpAndExit(options, 1, "--" + o.getLongOpt() + " requires --host");
                }
            }

            InputStream         is = null;

            if ("-".equals(cmd.getOptionValue(file))) {
                is = System.in;
            } else {
                try {
                    is = cmd.getParsedOptionValue(file);
                } catch (ParseException e) {
                    logger.error("Error parsing --\" + file.getLongOpt() + \" parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            }

            reader = maf.getReader(Medium.BACKUP, is);
        }

        Model           model = reader.read(); // se l'input non è da host (sto leggendo un file o stdin) e questa read fallisce, forse l'input è JSON e non backup binario

        try {
            model.plantCode = cmd.getParsedOptionValue(pCode);
        } catch (ParseException e) {
            logger.error("Error parsing --\" + pCode.getLongOpt() + \" parameter", e);
            printHelpAndExit(options, 1, e.getMessage());
        }

        OutputStream    os = null;

        for (Option o : outputs) {
            if (cmd.hasOption(o)) {
                if ("-".equals(cmd.getOptionValue(o))) {
                    os = System.out;
                } else {
                    try {
                        os = new FileOutputStream((File)cmd.getParsedOptionValue(o));
                    } catch (ParseException e) {
                        logger.error("Error parsing --" + o.getLongOpt() + " parameter", e);
                        printHelpAndExit(options, 1, e.getMessage());
                    }
                }

                switch (o.getLongOpt()) {
                    case "backup":
                        // Scrittura binaria a partire dal modello deserializzato
                        maf.getWriter(Medium.BACKUP, os).write(model);
                        break;
                    case "json":
                        // Scrittura JSON
                        maf.getWriter(Medium.JSON, os).write(model);
                        break;
                    case "properties":
                        // Scrittura properties ad uso emulatore
                        maf.getWriter(Medium.PROPERTIES, os).write(model);
                        break;
                }

                if (!"-".equals(cmd.getOptionValue(o))) {
                    os.flush();
                    os.close();
                }
            }
        }

        // if (!"-".equals(cmd.getOptionValue(file))) {
        //     is.close();
        // }
    }

    private static void printHelpAndExit(Options options, int status, String message) throws IOException {
        PrintStream    ps = status == 0 ? System.out : System.err;

        if (message != null) ps.println(message);
        
        HelpFormatter   formatter = HelpFormatter.builder()
                                                .setShowSince(false)
                                                .setHelpAppendable(new TextHelpAppendable(ps))
                                                .get();
        
        formatter.printHelp(MethodHandles.lookup().lookupClass().getSimpleName(), "Copies Elkron MP-508 configuration", options, "Just one between -b, -j and -p may be redirected to standard output", true);
        System.exit(status);
    }
}
