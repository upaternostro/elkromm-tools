package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.Medium;

/**
 * Builds a {@link ModelWriter} from the parameter required by its {@link Medium}.
 *
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @param <W> type of that parameter
 */
public interface WriterProvider<W> {
    /**
     * Creates the writer.
     *
     * @param param what the medium needs: a stream, a logged-in facade...
     * @return the new writer
     * @throws ElkrommException if it cannot be created
     */
    ModelWriter create(W param) throws ElkrommException;
}
