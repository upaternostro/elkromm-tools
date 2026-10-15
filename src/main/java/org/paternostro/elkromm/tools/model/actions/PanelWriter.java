package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Writes a model to a panel ({@link org.paternostro.elkromm.tools.Medium#PANEL}), restoring its
 * configuration.
 * <p>
 * Not implemented yet: {@link #write(Model)} throws {@link UnsupportedOperationException}.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class PanelWriter extends AbstractPanelAction implements ModelWriter
{
    /**
     * Creates the writer.
     *
     * @param param the facade to use, which must be logged in
     * @throws IllegalStateException if the facade is not logged in
     */
    public PanelWriter(ElkrommFacade param)
    {
        super(param);
    }

    @Override
    public void write(Model model) throws ElkrommException, IOException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'write'");
    }
}
