package org.paternostro.elkromm.tools.model.actions;

import java.io.InputStream;

public abstract class AbstractStreamReader {
    protected InputStream  is;

    AbstractStreamReader(InputStream param)
    {
        this.is = param;
    }
}
