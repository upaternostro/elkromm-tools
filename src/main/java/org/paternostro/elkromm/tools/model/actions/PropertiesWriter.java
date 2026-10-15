package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommUtils;
import org.paternostro.elkromm.dto.Expansion;
import org.paternostro.elkromm.dto.Input.Configuration;
import org.paternostro.elkromm.dto.Output.Type;
import org.paternostro.elkromm.emulator.Config;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Writes the properties file understood by the elkromm emulator
 * ({@link org.paternostro.elkromm.tools.Medium#PROPERTIES}).
 * <p>
 * The file holds the number of peripheral units, areas, partitions, inputs and outputs: the
 * rest of the configuration (users, keys, phone numbers...) cannot be expressed in this format
 * and is left out. Values equal to the emulator defaults are omitted, and unused inputs and
 * outputs are skipped, to keep the file short. The stream is flushed, not closed.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class PropertiesWriter extends AbstractStreamWriter implements ModelWriter
{
    /**
     * Creates the writer.
     *
     * @param param the stream to write to
     */
    public PropertiesWriter(OutputStream param)
    {
        super(param);
    }

    @Override
    public void write(Model model) throws ElkrommException, IOException
    {
        PrintWriter pw = new PrintWriter(os);

        pw.println("# My properties backup");

        pw.println(String.format("%s=%d", Config.K_KEYBOARDS, model.peripheralUnits.getKeypadNum()));
        pw.println(String.format("%s=%d", Config.K_READERS, model.peripheralUnits.getReaderNum()));
        pw.println(String.format("%s=%d", Config.K_EXPANSIONS, model.peripheralUnits.getExpansionNum()));
        pw.println();
        
        // Areas
        pw.println(String.format("%s=%d", Config.K_AREAS, model.areasAndPartitions.getAreaNum()));
        pw.println();

        for (int i = 0; i < model.areasAndPartitions.getAreaNum(); i++) {
            pw.println(String.format(Config.K_AREA_NAME + "=%s", i + 1, model.areasAndPartitions.getArea(i).getName()));
            pw.println(String.format(Config.K_AREA_PARTITIONS + "=%d", i + 1, ElkrommUtils.packPartitions(model.areasAndPartitions.getArea(i).getAssociatedPartitions())));
            pw.println();
        }

        // Partitions
        pw.println(String.format("%s=%d", Config.K_PARTITIONS, model.areasAndPartitions.getPartitionNum()));
        pw.println();

        for (int i = 0; i < model.areasAndPartitions.getPartitionNum(); i++) {
            pw.println(String.format(Config.K_PARTITION_NAME + "=%s", i + 1, model.areasAndPartitions.getPartition(i).getName()));
            if (!Config.D_PARTITION_TYPE[i].equals(model.areasAndPartitions.getPartition(i).getType().toString())) pw.println(String.format(Config.K_PARTITION_TYPE + "=%s", i + 1, model.areasAndPartitions.getPartition(i).getType()));
            if (!Config.D_PARTITION_ENTRY_TIME[i].equals(Integer.toString(model.areasAndPartitions.getPartition(i).getEntryDelay()))) pw.println(String.format(Config.K_PARTITION_ENTRY_TIME + "=%d", i + 1, model.areasAndPartitions.getPartition(i).getEntryDelay()));
            if (!Config.D_PARTITION_EXIT_TIME[i].equals(Integer.toString(model.areasAndPartitions.getPartition(i).getExitDelay()))) pw.println(String.format(Config.K_PARTITION_EXIT_TIME + "=%d", i + 1, model.areasAndPartitions.getPartition(i).getExitDelay()));
            pw.println();
        }

        // Inputs
        for (Expansion e : model.expansions) {
            e.forEachInput(i -> {
                if (i.getLogicNumber() != 0 && i.getConfiguration() != Configuration.IC_NOT_USED) {
                    if (!Config.D_INPUT_CONFIGURATION.equals(i.getConfiguration().toString().substring(3))) pw.println(String.format(Config.K_INPUT_CONFIGURATION + "=%s", i.getLogicNumber(), i.getConfiguration().toString().substring(3)));
                    if (!Config.D_INPUT_SPECIALIZATION.equals(i.getSpecialization().toString().substring(3))) pw.println(String.format(Config.K_INPUT_SPECIALIZATION + "=%s", i.getLogicNumber(), i.getSpecialization().toString().substring(3)));
                    if (!Config.D_INPUT_SENSITIVITY.equals(i.getSensitivity().toString().substring(3))) pw.println(String.format(Config.K_INPUT_SENSITIVITY + "=%s", i.getLogicNumber(), i.getSensitivity().toString().substring(3)));
                    pw.println(String.format(Config.K_INPUT_FLAGS + "=%d", i.getLogicNumber(), i.getFlags()));
                    if (!Config.D_INPUT_VIDEO.equals(i.getVideo().toString().substring(3))) pw.println(String.format(Config.K_INPUT_VIDEO + "=%s", i.getLogicNumber(), i.getVideo().toString().substring(3)));
                    pw.println(String.format(Config.K_INPUT_PARTITIONS + "=%d", i.getLogicNumber(), ElkrommUtils.packPartitions(i.getAssociatedPartitions()) & 0xFF));
                    pw.println(String.format(Config.K_INPUT_NAME + "=%s", i.getLogicNumber(), i.getName()));
                    if (!Config.D_INPUT_DELAY.equals(i.getDelay().toString().substring(3))) pw.println(String.format(Config.K_INPUT_DELAY + "=%s", i.getLogicNumber(), i.getDelay().toString().substring(3)));
                    pw.println();
                }
            });
        }

        // Outputs
        for (Expansion e : model.expansions) {
            e.forEachOutput(o -> {
                if (o.getLogicNumber() != 0 && o.getType() != Type.OT_NOT_USED) {
                    if (!Config.D_OUTPUT_TYPE.equals(o.getType().toString().substring(3))) pw.println(String.format(Config.K_OUTPUT_TYPE + "=%s", o.getLogicNumber(), o.getType().toString().substring(3)));
                    pw.println(String.format(Config.K_OUTPUT_PARTITIONS + "=%d", o.getLogicNumber(), ElkrommUtils.packPartitions(o.getAssociatedPartitions()) & 0xFF));
                    if (!Config.D_OUTPUT_SPECIALIZATION.equals(o.getSpecialization().toString().substring(3))) pw.println(String.format(Config.K_OUTPUT_SPECIALIZATION + "=%s", o.getLogicNumber(), o.getSpecialization().toString().substring(3)));
                    pw.println(String.format(Config.K_OUTPUT_NAME + "=%s", o.getLogicNumber(), o.getName()));
                    pw.println();
                }
            });
        }

        pw.flush();
        // pw.close();
    }
}
