package org.paternostro.elkromm.tools.model.actions;

import java.io.OutputStream;

/**
 * Base class of the writers that write to a stream. The stream stays open: closing it is up to
 * the caller.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public abstract class AbstractStreamWriter {
    /**
     * The stream to write to.
     */
    protected OutputStream  os;

    AbstractStreamWriter(OutputStream param)
    {
        this.os = param;
    }
}
