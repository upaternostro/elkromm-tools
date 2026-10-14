package org.paternostro.elkromm.tools.model.actions;

import java.io.IOException;
import java.io.OutputStream;

import org.paternostro.elkromm.ElkrommException;
import org.paternostro.elkromm.tools.model.Model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONWriter extends AbstractStreamWriter implements ModelWriter
{
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
