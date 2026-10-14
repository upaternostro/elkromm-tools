package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.tools.model.Model;

public class PanelReader extends AbstractPanelAction implements ModelReader
{
    public PanelReader(ElkrommFacade param)
    {
        super(param);
    }

    @Override
    public Model read() throws ElkrommException, IOException
    {
        Model   model = new Model();

        model.peripheralUnits = facade.getPeripheralUnitsAddresses();
        model.expansions = facade.getExpansions();
        if (model.peripheralUnits.getKeypadNum() > 0) model.keypads = facade.getKeyboards();
        if (model.peripheralUnits.getReaderNum() > 0) model.readers = facade.getReaders();
        model.timeProgrammer = facade.getTimeProgrammer();
        model.areasAndPartitions = facade.getAreasAndPartitions();
        model.phoneParameters = facade.getPhoneParameters();
        model.phoneNumbersSendingCodes = facade.getPhoneNumbersSendingCodes();
        model.c200bParameters = facade.getC200bParameters();
        model.sMSs = facade.getSMSs();
        model.pstnGsm = facade.getPSTNGSM();
        model.users = facade.getKeys();
        model.keys = facade.getKeys();
        model.parametersEnablings = facade.getParametersEnablings();

        return model;
    }
}
