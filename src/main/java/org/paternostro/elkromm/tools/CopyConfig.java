package org.paternostro.elkromm.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import org.paternostro.elkromm.tools.model.actions.ModelWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Command line tool that copies the configuration of an Elkron MP-508 panel to one or more
 * output files.
 * <p>
 * The source is either a Hi-Connect backup file ({@code -f}, or {@code -f -} for standard
 * input) or a live panel reached over the LAN ({@code -H}); the two are mutually exclusive.
 * A file given with {@code -f} must be a Hi-Connect backup: reading JSON is not implemented yet.
 * <p>
 * At least one output must be requested, and each of them receives the whole model:
 * <ul>
 * <li>{@code -b}: a Hi-Connect backup file ({@link Medium#BACKUP})</li>
 * <li>{@code -j}: a pretty-printed JSON file ({@link Medium#JSON})</li>
 * <li>{@code -p}: a properties file in the format used by the elkromm emulator
 * ({@link Medium#PROPERTIES}), which only holds part of the configuration</li>
 * </ul>
 * Use {@code -} as file name to write to standard output, at most once per run.
 * <p>
 * With {@code -H}, the options {@code -P} (TCP port, default {@value #DEFAULT_PORT}) and
 * {@code -t} (technician user PIN) are accepted; if the PIN is not given it is taken from the
 * environment variable {@value #ENV_PIN_NAME} and, failing that, asked on the console. Both
 * options are rejected when reading from a file. The plant code can be set with {@code -c}.
 * <p>
 * When the arguments are not valid the tool prints the usage on standard error and exits with
 * status 1; {@code -h} prints it on standard output and exits with status 0.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class CopyConfig {
    /**
     * Logger of this class.
     */
    public static final Logger  logger = LoggerFactory.getLogger(CopyConfig.class);
    
    /**
     * Signature found at the start of a Hi-Connect backup file.
     */
    public static final String  MAGIC = "MP508V02";
    /**
     * TCP port used to reach the panel when {@code -P} is not given.
     */
    public static final int     DEFAULT_PORT = 8030;
    /**
     * Plant code used when {@code -c} is not given.
     */
    public static final int     DEFAULT_PLANT_CODE = 55555555;
    /**
     * Name of the environment variable that may hold the technician user PIN.
     */
    public static final String  ENV_PIN_NAME = "ELKROMM_PIN";

    // Command line options
    private static final Option CMDLINE_OPT_HELP = Option.builder("h")
                            .longOpt("help")
                            .desc("prints this help and exits")
                            .get();
    private static final Option CMDLINE_OPT_FILE = Option.builder("f")
                            .longOpt("file")
                            .hasArg()
                            .desc("file to read (use \"-\" to read from standard input)")
                            .type(FileInputStream.class)
                            .get();
    private static final Option CMDLINE_OPT_HOST = Option.builder("H")
                            .longOpt("host")
                            .hasArg()
                            .desc("IP address or hostname of the panel")
                            .get();
    private static final Option CMDLINE_OPT_PLANT_CODE = Option.builder("c")
                            .longOpt("plant-code")
                            .hasArg()
                            .desc("plant code (default: " + DEFAULT_PLANT_CODE + ")")
                            .type(Integer.class)
                            .get();
    private static final Option CMDLINE_OPT_PORT = Option.builder("P")
                            .longOpt("port")
                            .hasArg()
                            .type(Integer.class)
                            .desc("TCP port of the panel (default: " + DEFAULT_PORT + ")")
                            .get();
    private static final Option CMDLINE_OPT_TECH_PIN = Option.builder("t")
                            .longOpt("tech-pin")
                            .hasArg()
                            .desc("technician user PIN (if omitted, read from env " + ENV_PIN_NAME + " or prompted)")
                            .type(Integer.class)
                            .get();
    private static final Option CMDLINE_OPT_BACKUP = Option.builder("b")
                            .longOpt("backup")
                            .hasArg()
                            .desc("file to write (use \"-\" to write to standard output)")
                            .type(File.class)
                            .get();
    private static final Option CMDLINE_OPT_JSON = Option.builder("j")
                            .longOpt("json")
                            .hasArg()
                            .desc("JSON file to write (use \"-\" to write to standard output)")
                            .type(File.class)
                            .get();
    private static final Option CMDLINE_OPT_PROPS = Option.builder("p")
                            .longOpt("properties")
                            .hasArg()
                            .desc("properties file to write (use \"-\" to write to standard output)")
                            .type(File.class)
                            .get();
    
    /**
     * Resource manager to cleanup source stream/connection
     */
    private static final class Source implements AutoCloseable {
        final ModelReader reader;
        private final ElkrommFacade facade;  // null when reading a file
        private final InputStream   is;      // null when reading a panel, or standard input

        Source(ModelReader reader, ElkrommFacade facade, InputStream is) { 
            this.reader = reader;
            this.facade = facade;
            this.is     = is;
         }

        @Override
        public void close() throws ElkrommException, IOException {
            try {
                if (facade != null) facade.close();      // logout + disconnect
            } finally {
                if (is != null) is.close();
            }
        }
    }

    /**
     * Resource manager to cleanup destination stream/connection
     */
    private static final class Destination implements AutoCloseable {
        final ModelWriter writer;
        private final ElkrommFacade facade;  // null when writing a file
        private final OutputStream  os;      // null when writing a panel, or standard output

        Destination(ModelWriter writer, ElkrommFacade facade, OutputStream os) { 
            this.writer = writer;
            this.facade = facade;
            this.os     = os;
         }

        @Override
        public void close() throws ElkrommException, IOException {
            try {
                if (facade != null) facade.close();      // logout + disconnect
            } finally {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            }
        }
    }

    /**
     * Entry point of the tool.
     *
     * @param args command line arguments, see the description of this class
     * @throws IOException if reading the source or writing an output fails
     * @throws ElkrommException if the panel cannot be reached or answers unexpectedly, or if the source cannot be decoded
     */
    public static void main(String[] args) throws IOException
    {
        Options             options = new Options();

        // source options group (mutually exclusive)
        OptionGroup         optionGroup = new OptionGroup();

        optionGroup.addOption(CMDLINE_OPT_HELP)
                    .addOption(CMDLINE_OPT_FILE)
                    .addOption(CMDLINE_OPT_HOST)
                    .setRequired(true);
        options.addOptionGroup(optionGroup);

        // plant code
        options.addOption(CMDLINE_OPT_PLANT_CODE);

        // panel parameters
        options.addOption(CMDLINE_OPT_PORT)
                .addOption(CMDLINE_OPT_TECH_PIN);

        // outputs
        options.addOption(CMDLINE_OPT_BACKUP)
                .addOption(CMDLINE_OPT_JSON)
                .addOption(CMDLINE_OPT_PROPS);

        CommandLineParser   parser = new DefaultParser();
        CommandLine         cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.error("Error parsing parameters", e);
            printHelpAndExit(options, 1, e.getMessage());
        }

        if (cmd.hasOption(CMDLINE_OPT_HELP)) {
            printHelpAndExit(options, 0, null);
        }

        Option[]            outputs = { CMDLINE_OPT_BACKUP, CMDLINE_OPT_JSON, CMDLINE_OPT_PROPS };
        int                 outputCount = 0;
        int                 stdoutUsage = 0;

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

        int                 plantCode = DEFAULT_PLANT_CODE;

        if (cmd.hasOption(CMDLINE_OPT_PLANT_CODE)) {
            try {
                plantCode = cmd.getParsedOptionValue(CMDLINE_OPT_PLANT_CODE);
            } catch (ParseException e) {
                logger.error("Error parsing --" + CMDLINE_OPT_PLANT_CODE.getLongOpt() + " parameter", e);
                printHelpAndExit(options, 1, e.getMessage());
            }
        }

        Model               model = null;

        try (Source src = openSource(cmd, options, plantCode)) {
            model = src.reader.read(); // se l'input non è da host (sto leggendo un file o stdin) e questa read fallisce, forse l'input è JSON e non backup binario

            model.plantCode = plantCode;
        } catch (Exception e) {
            logger.error("Cannot read source", e);
        }

        if (model != null) {
            for (Option o : outputs) {
                if (cmd.hasOption(o)) {
                    try (Destination dest = openDestination(cmd, options, o, model)) {
                        dest.writer.write(model);
                    } catch (Exception e) {
                        logger.error("Cannot write destination --" + o.getLongOpt(), e);
                    }
                }
            }
        }
    }

    private static Source openSource(CommandLine cmd, Options options, int plantCode) throws IOException, ElkrommException
    {
        ModelActionFactory  maf = ModelActionFactory.getFactory();
        ModelReader         reader;
        ElkrommFacade       facade = null;
        InputStream         is = null;

        if (cmd.hasOption(CMDLINE_OPT_HOST)) {
            // modalità connessione diretta: --pin è l'unico davvero obbligatorio
            // (o ricavabile da env/prompt), --port ha il default
            int pin = -1;

            if (cmd.hasOption(CMDLINE_OPT_TECH_PIN)) {
                try {
                    pin = cmd.getParsedOptionValue(CMDLINE_OPT_TECH_PIN);
                } catch (ParseException e) {
                    logger.error("Error parsing --" + CMDLINE_OPT_TECH_PIN.getLongOpt() + " parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            } else {
                try {
                    pin = Integer.parseInt(System.getenv(ENV_PIN_NAME));
                } catch (Exception e) {
                    if (System.console() != null) {
                        System.console().printf("Insert technical user PIN: ");
                        try {
                            pin = Integer.parseInt(new String(System.console().readPassword()));
                        } catch (Exception e2) {
                        }
                    }
                }
            }

            if (pin == -1) {
                logger.error("No PIN specified");
                printHelpAndExit(options, 1, "No PIN specified");
            }

            int portNum = DEFAULT_PORT;

            if (cmd.hasOption(CMDLINE_OPT_PORT)) {
                try {
                    portNum = cmd.getParsedOptionValue(CMDLINE_OPT_PORT);
                } catch (ParseException e) {
                    logger.error("Error parsing --" + CMDLINE_OPT_PORT.getLongOpt() + " parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            }

            facade = ElkrommFactory.getFactory()
                                    .getElkrommFacade(
                                        InetAddress.getByName(cmd.getOptionValue(CMDLINE_OPT_HOST)), 
                                        portNum, 
                                        plantCode
                                    );

            try {
                facade.connect();
                facade.login(plantCode, pin);
                reader = maf.getReader(Medium.PANEL, facade);
            } catch (Throwable t) {
                try { facade.close(); } catch (Throwable t2) { t.addSuppressed(t2); }
                throw t;
            }
        } else {
            // modalità file: host/port/pin non hanno senso
            Option[] illegalOptions = {CMDLINE_OPT_PORT, CMDLINE_OPT_TECH_PIN};

            for (Option o : illegalOptions) {
                if (cmd.hasOption(o)) {
                    printHelpAndExit(options, 1, "--" + o.getLongOpt() + " requires --" + CMDLINE_OPT_HOST.getLongOpt());
                }
            }

            if ("-".equals(cmd.getOptionValue(CMDLINE_OPT_FILE))) {
                is = System.in;
            } else {
                try {
                    is = cmd.getParsedOptionValue(CMDLINE_OPT_FILE);
                } catch (ParseException e) {
                    logger.error("Error parsing --" + CMDLINE_OPT_FILE.getLongOpt() + " parameter", e);
                    printHelpAndExit(options, 1, e.getMessage());
                }
            }

            reader = maf.getReader(Medium.BACKUP, is);

            if ("-".equals(cmd.getOptionValue(CMDLINE_OPT_FILE))) {
                // nel caso di stdin passo null a Source in modo che non venga chiuso (intanto Reader ha già memorizzato lo stream)
                is = null;
            }
        }

        return new Source(reader, facade, is);
    }

    private static Destination openDestination(CommandLine cmd, Options options, Option o, Model model) throws FileNotFoundException, IOException, ElkrommException
    {
        ModelActionFactory  maf = ModelActionFactory.getFactory();
        ModelWriter         writer = null;
        ElkrommFacade       facade = null;
        OutputStream        os = null;

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
                writer = maf.getWriter(Medium.BACKUP, os);
                break;
            case "json":
                // Scrittura JSON
                writer = maf.getWriter(Medium.JSON, os);
                break;
            case "properties":
                // Scrittura properties ad uso emulatore
                writer = maf.getWriter(Medium.PROPERTIES, os);
                break;
            // FIXME: aggiungere opzione per scrivere su pannello
            default:
                throw new AssertionError("Unhandled output option --" + o.getLongOpt());
        }

        if ("-".equals(cmd.getOptionValue(o))) {
            // nel caso di stdout passo null a Destination in modo che non venga chiuso (intanto Writer ha già memorizzato lo stream)
            os = null;
        }

        return new Destination(writer, facade, os);
    }

    // Prints the usage (on standard output for status 0, on standard error otherwise) and terminates the JVM
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
