package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.OutputStream;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Writes a model as pretty-printed JSON ({@link org.paternostro.elkromm.tools.Medium#JSON}),
 * using Jackson. The stream is not closed.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class JSONWriter extends AbstractStreamWriter implements ModelWriter
{
    /**
     * Creates the writer.
     *
     * @param param the stream to write to
     */
    public JSONWriter(OutputStream param)
    {
        super(param);
    }

    @Override
    public void write(Model model) throws ElkrommException, IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();

        // Disabilita la chiusura automatica del target
        objectMapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

        // pretty print
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        objectMapper.writeValue(os, model);
    }
}
