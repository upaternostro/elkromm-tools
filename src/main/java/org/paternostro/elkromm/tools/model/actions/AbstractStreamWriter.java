package org.paternostro.elkromm.tools.model.actions;

import java.io.OutputStream;

public abstract class AbstractStreamWriter {
    protected OutputStream  os;

    AbstractStreamWriter(OutputStream param)
    {
        this.os = param;
    }
}
