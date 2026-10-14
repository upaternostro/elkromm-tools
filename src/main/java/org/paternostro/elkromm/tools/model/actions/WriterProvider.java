package org.paternostro.elkromm.tools.model.actions;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.Medium;

/**
 * Builds a {@link ModelWriter} from the parameter required by its {@link Medium}.
 *
 * @param <W> type of that parameter
 */
public interface WriterProvider<W> {
    ModelWriter create(W param) throws ElkrommException;
}
