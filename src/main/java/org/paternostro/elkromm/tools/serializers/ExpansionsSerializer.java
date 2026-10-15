package org.paternostro.elkromm.tools.serializers;

import java.io.IOException;

import org.paternostro.elkromm.dto.Expansion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Writes an array of {@link org.paternostro.elkromm.dto.Expansion} as JSON: for each expansion
 * its address, version and name, and the arrays of its inputs and outputs.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class ExpansionsSerializer extends JsonSerializer<Expansion[]>
{
    @Override
    public void serialize(Expansion[] value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        for (Expansion exp : value) {
            jgen.writeStartObject();
            jgen.writeNumberField("address", exp.getAddress());
            jgen.writeStringField("version", exp.getVersion());
            jgen.writeStringField("name", exp.getName());

            jgen.writeArrayFieldStart("inputs");
            for (int i = 0; i < exp.getInputNum(); i++) {
                jgen.writeObject(exp.getInput(i));
            }
            jgen.writeEndArray();

            jgen.writeArrayFieldStart("outputs");
            for (int i = 0; i < exp.getOutputNum(); i++) {
                jgen.writeObject(exp.getOutput(i));
            }
            jgen.writeEndArray();
            jgen.writeEndObject();    
        }
        jgen.writeEndArray();
    }
}
