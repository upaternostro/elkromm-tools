package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Reads the whole configuration of a panel, or of the elkromm emulator
 * ({@link org.paternostro.elkromm.tools.Medium#PANEL}), through a logged-in facade, asking for one
 * part at a time. Keypads and readers are requested only if the peripheral units say that there
 * are some. {@link Model#plantCode} is not filled in: callers set it themselves.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class PanelReader extends AbstractPanelAction implements ModelReader
{
    /**
     * Creates the reader.
     *
     * @param param the facade to use, which must be logged in
     * @throws IllegalStateException if the facade is not logged in
     */
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
