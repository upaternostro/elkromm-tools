package org.paternostro.elkromm.tools.serializers;

import java.io.IOException;

import org.paternostro.elkromm.dto.C200bParameters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Writes {@link org.paternostro.elkromm.dto.C200bParameters} as JSON: the reserved bytes, the
 * event codes and the array of the input codes.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class C200bParametersSerializer extends JsonSerializer<C200bParameters>
{
    @Override
    public void serialize(C200bParameters value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeArrayFieldStart("reserved");
        for (byte reserved : value.getReserved()) {
            jgen.writeNumber(reserved);
        }
        jgen.writeEndArray();

        jgen.writeObjectField("eventCodes", value.getEventCodes());

        jgen.writeArrayFieldStart("inputCodes");
        for (byte inputCode : value.getInputCodes()) {
            jgen.writeNumber(inputCode);
        }
        jgen.writeEndArray();

        jgen.writeEndObject();    
    }
}
