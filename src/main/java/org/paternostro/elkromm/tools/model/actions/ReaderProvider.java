package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.Medium;

/**
 * Builds a {@link ModelReader} from the parameter required by its {@link Medium}.
 *
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 *
 * @param <R> type of that parameter
 */
public interface ReaderProvider<R> {
    /**
     * Creates the reader.
     *
     * @param param what the medium needs: a stream, a logged-in facade...
     * @return the new reader
     * @throws ElkrommException if it cannot be created
     */
    ModelReader create(R param) throws ElkrommException;
}
