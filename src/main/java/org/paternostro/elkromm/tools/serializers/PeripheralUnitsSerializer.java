package org.paternostro.elkromm.tools.serializers;

import java.io.IOException;

import org.paternostro.elkromm.dto.PeripheralUnits;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Writes {@link org.paternostro.elkromm.dto.PeripheralUnits} as JSON: for keypads, readers and
 * expansions, how many there are and the array of their addresses.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class PeripheralUnitsSerializer extends JsonSerializer<PeripheralUnits>
{
    @Override
    public void serialize(PeripheralUnits value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("keypadNum", value.getKeypadNum());

        jgen.writeArrayFieldStart("keypadAddresses");
        for (int i = 0; i < value.getKeypadNum(); i++) {
            jgen.writeNumber(value.getKeypadAddress(i));
        }
        jgen.writeEndArray();

        jgen.writeNumberField("readerNum", value.getReaderNum());

        jgen.writeArrayFieldStart("readerAddresses");
        for (int i = 0; i < value.getReaderNum(); i++) {
            jgen.writeNumber(value.getReaderAddress(i));
        }
        jgen.writeEndArray();

        jgen.writeNumberField("expansionNum", value.getExpansionNum());

        jgen.writeArrayFieldStart("expansionAddresses");
        for (int i = 0; i < value.getExpansionNum(); i++) {
            jgen.writeNumber(value.getExpansionAddress(i));
        }
        jgen.writeEndArray();

        jgen.writeEndObject();    
    }
}
