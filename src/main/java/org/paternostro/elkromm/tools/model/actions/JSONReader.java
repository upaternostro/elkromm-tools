package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.InputStream;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Reads a model from JSON ({@link org.paternostro.elkromm.tools.Medium#JSON}).
 * <p>
 * Not implemented yet: {@link #read()} throws {@link UnsupportedOperationException}.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class JSONReader extends AbstractStreamReader implements ModelReader
{
    /**
     * Creates the reader.
     *
     * @param param the stream to read from
     */
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
