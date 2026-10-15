package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Reads a {@link Model} from a {@link org.paternostro.elkromm.tools.Medium}.
 * <p>
 * Implementations get what they need to reach the medium (a stream, a logged-in facade) when
 * they are created, and never close it: whoever opened it closes it.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public interface ModelReader {
    /**
     * Reads the model.
     *
     * @return the model read
     * @throws ElkrommException if the content cannot be decoded or the panel answers unexpectedly
     * @throws IOException if reading fails
     */
    Model read() throws ElkrommException, IOException;
}
