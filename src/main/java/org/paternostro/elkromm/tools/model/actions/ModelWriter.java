package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

/**
 * Writes a {@link Model} to a {@link org.paternostro.elkromm.tools.Medium}.
 * <p>
 * Implementations get what they need to reach the medium (a stream, a logged-in facade) when
 * they are created, and never close it: whoever opened it closes it.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public interface ModelWriter {
    /**
     * Writes the model.
     *
     * @param model the model to write
     * @throws ElkrommException if the model cannot be encoded or the panel refuses it
     * @throws IOException if writing fails
     */
    void write(Model model) throws ElkrommException, IOException;
}
