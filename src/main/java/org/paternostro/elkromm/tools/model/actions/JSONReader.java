package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.InputStream;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

public class JSONReader extends AbstractStreamReader implements ModelReader
{
    public JSONReader(InputStream param)
    {
        super(param);
    }

    @Override
    public Model read() throws ElkrommException, IOException
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'read'");
        // FIXME!!!
    }
}
