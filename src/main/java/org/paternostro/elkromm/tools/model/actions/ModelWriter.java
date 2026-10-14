package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

public interface ModelWriter {
    void write(Model model) throws ElkrommException, IOException;
}
