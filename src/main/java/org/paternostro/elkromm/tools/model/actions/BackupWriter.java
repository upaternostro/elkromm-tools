package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.ElkrommFactory;
import org.paternostro.elkromm.ElkrommUtils;
import org.paternostro.elkromm.ElkronCommand;
import org.paternostro.elkromm.packet.ElkrommPacket;
import org.paternostro.elkromm.tools.CopyConfig;
import org.paternostro.elkromm.tools.model.Model;

public class BackupWriter extends AbstractStreamWriter implements ModelWriter
{
    public BackupWriter(OutputStream param)
    {
        super(param);
    }

    @Override
    public void write(Model model) throws ElkrommException, IOException
    {
        ElkrommFactory  factory = ElkrommFactory.getFactory();
        List<Byte>      bcdPlantCode = ElkrommUtils.bcd(model.plantCode, 4);
        byte            plantCode12 = bcdPlantCode.get(0);
        byte            plantCode34 = bcdPlantCode.get(1);

        os.write(CopyConfig.MAGIC.getBytes());
        writePayload(ElkronCommand.PERIPHERAL_UNITS_ADDRESSES, plantCode12, plantCode34, factory.getPeripheralUnitsSerializer().serialize(model.peripheralUnits), os);
        writePayload(ElkronCommand.EXPANSIONS_PROGRAMMING, plantCode12, plantCode34, factory.getExpansionsSerializer().serialize(model.expansions), os);
        if (model.keypads != null) writePayload(ElkronCommand.KEYPADS_PROGRAMMING, plantCode12, plantCode34, factory.getKeyboardsSerializer().serialize(model.keypads), os);
        if (model.readers != null) writePayload(ElkronCommand.READER_PROGRAMMING, plantCode12, plantCode34, factory.getReadersSerializer().serialize(model.readers), os);
        writePayload(ElkronCommand.SET_TIME_PROGRAMMER, plantCode12, plantCode34, factory.getTimeProgrammerSerializer().serialize(model.timeProgrammer), os);
        writePayload(ElkronCommand.SET_PARTITIONS_AND_AREAS, plantCode12, plantCode34, factory.getAreasAndPartitionsSerializer().serialize(model.areasAndPartitions), os);
        writePayload(ElkronCommand.SET_PHONE_PARAMETERS, plantCode12, plantCode34, factory.getPhoneParametersSerializer().serialize(model.phoneParameters), os);
        writePayload(ElkronCommand.SET_PHONE_NUMBERS, plantCode12, plantCode34, factory.getPhoneNumbersSendingCodesSerializer().serialize(model.phoneNumbersSendingCodes), os);
        writePayload(ElkronCommand.SET_C200B, plantCode12, plantCode34, factory.getC200bParametersSerializer().serialize(model.c200bParameters), os);
        writePayload(ElkronCommand.SET_SMS, plantCode12, plantCode34, factory.getSMSsSerializer().serialize(model.sMSs), os);
        writePayload(ElkronCommand.SET_PSTN_GSM, plantCode12, plantCode34, factory.getPSTNGSMSerializer().serialize(model.pstnGsm), os);
        writePayload(ElkronCommand.SET_USERS, plantCode12, plantCode34, factory.getUsersSerializer().serialize(model.users), os);
        writePayload(ElkronCommand.SET_KEYS, plantCode12, plantCode34, factory.getKeysSerializer().serialize(model.keys), os);
        writePayload(ElkronCommand.SET_PARAMETERS_ENABLINGS, plantCode12, plantCode34, factory.getParametersEnablingsSerializer().serialize(model.parametersEnablings), os);
    }
    
    private void writePayload(ElkronCommand cmd, int plantCode12, int plantCode34, byte[] payload, OutputStream os) throws ElkrommException, IOException
    {
        byte[]  payloadPart;

        // split payload
        int totalPackets = ((payload == null ? 0 : payload.length) - 1) / ElkrommFacade.MAX_DATA_LENGTH; // numero di pacchetti totali (base 0), meno uno perché 140 byte entrano tutti nel primo pacchetto
        int from = 0;
        int to;
        int index = 0;

        if (totalPackets < 0) {
            // fix per payload vuoto/nullo
            totalPackets = 0;
        }

        while (index <= totalPackets) {
            to = Math.min(from + ElkrommFacade.MAX_DATA_LENGTH, payload == null ? 0 : payload.length);
            payloadPart = payload != null ? Arrays.copyOfRange(payload, from, to) : null;
            ElkrommPacket.packetFactoryAllocate(cmd, plantCode12, plantCode34, totalPackets, index, payload == null ? 0 : payloadPart.length, payloadPart)
                .serialize(os);
            from = to;
            index++;
        }
    }
}
