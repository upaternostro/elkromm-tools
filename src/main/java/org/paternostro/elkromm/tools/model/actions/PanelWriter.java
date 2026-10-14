package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.ElkrommFacade;
import org.paternostro.elkromm.tools.model.Model;

public class PanelWriter extends AbstractPanelAction implements ModelWriter
{
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
