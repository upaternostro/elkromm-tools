package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.Medium;

/**
 * Builds a {@link ModelReader} from the parameter required by its {@link Medium}.
 *
 * @param <R> type of that parameter
 */
public interface ReaderProvider<R> {
    ModelReader create(R param) throws ElkrommException;
}
