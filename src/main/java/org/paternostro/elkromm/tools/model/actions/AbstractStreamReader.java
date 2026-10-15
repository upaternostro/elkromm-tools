package org.paternostro.elkromm.tools.model.actions;

import java.io.InputStream;

/**
 * Base class of the readers that read from a stream. The stream stays open: closing it is up to
 * the caller.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public abstract class AbstractStreamReader {
    /**
     * The stream to read from.
     */
    protected InputStream  is;

    AbstractStreamReader(InputStream param)
    {
        this.is = param;
    }
}
