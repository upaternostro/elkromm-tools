package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFactory;
import org.paternostro.elkromm.ElkronCommand;
import org.paternostro.elkromm.packet.ElkrommPacket;
import org.paternostro.elkromm.tools.CopyConfig;
import org.paternostro.elkromm.tools.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupReader extends AbstractStreamReader implements ModelReader
{
    public static final Logger logger = LoggerFactory.getLogger(BackupReader.class);

    public BackupReader(InputStream param)
    {
        super(param);
    }

    @Override
    public Model read() throws ElkrommException, IOException
    {
        Model   retval = null;
        byte[]  magic = new byte[8];

        if (is.read(magic) == 8) {
            if (CopyConfig.MAGIC.equals(new String(magic))) {
                Map<ElkronCommand, SortedSet<ElkrommPacket>>    cmdPayloads = new HashMap<>();
                ElkrommPacket                                   packet = null;
                byte[]                                          payload;
                ElkrommFactory                                  factory = ElkrommFactory.getFactory();

                retval = new Model();

                // try {
                //     retval.plantCode = cmd.getParsedOptionValue(pCode);
                // } catch (ParseException e) {
                //     logger.error("Error parsing pCode parameter", e);
                //     printHelpAndExit(options, 1, e.getMessage());
                // }

                while (is.available() > 0) {
                    packet = ElkrommPacket.deserialize(is);

                    logger.debug(packet.toString());

                    if ((payload = packet.cachePayload(cmdPayloads)) != null) {
                        // Deserializzazione e memorizzazione nel modello
                        switch (packet.getCommand()) {
                            case PERIPHERAL_UNITS_ADDRESSES:
                                retval.peripheralUnits = factory.getPeripheralUnitsSerializer().deserialize(payload);
                                break;
                            case EXPANSIONS_PROGRAMMING:
                                retval.expansions = factory.getExpansionsSerializer().deserialize(payload);
                                break;
                            case KEYPADS_PROGRAMMING:
                                retval.keypads = factory.getKeyboardsSerializer().deserialize(payload);
                                break;
                            case READER_PROGRAMMING:
                                retval.readers = factory.getReadersSerializer().deserialize(payload);
                                break;
                            case SET_TIME_PROGRAMMER:
                                retval.timeProgrammer = factory.getTimeProgrammerSerializer().deserialize(payload);
                                break;
                            case SET_PARTITIONS_AND_AREAS:
                                retval.areasAndPartitions = factory.getAreasAndPartitionsSerializer().deserialize(payload);
                                break;
                            case SET_PHONE_PARAMETERS:
                                retval.phoneParameters = factory.getPhoneParametersSerializer().deserialize(payload);
                                break;
                            case SET_PHONE_NUMBERS:
                                retval.phoneNumbersSendingCodes = factory.getPhoneNumbersSendingCodesSerializer().deserialize(payload);
                                break;
                            case SET_C200B:
                                retval.c200bParameters = factory.getC200bParametersSerializer().deserialize(payload);
                                break;
                            case SET_SMS:
                                retval.sMSs = factory.getSMSsSerializer().deserialize(payload);
                                break;
                            case SET_PSTN_GSM:
                                retval.pstnGsm = factory.getPSTNGSMSerializer().deserialize(payload);
                                break;
                            case SET_USERS:
                                retval.users = factory.getUsersSerializer().deserialize(payload);
                                break;
                            case SET_KEYS:
                                retval.keys = factory.getKeysSerializer().deserialize(payload);
                                break;
                            case SET_PARAMETERS_ENABLINGS:
                                retval.parametersEnablings = factory.getParametersEnablingsSerializer().deserialize(payload);
                                break;
                            default:
                                logger.warn("Unexpected command " + packet.getCommand());
                                break;
                        }
                    }
                }
            }
        } else {
            logger.error("Cannot read file signature");
            throw new ElkrommException("Missing " + CopyConfig.MAGIC + " signature");
        }

        return retval;
    }
}
