package org.paternostro.elkromm.tools.serializers;

import java.io.IOException;

import org.paternostro.elkromm.dto.AreasAndPartitions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Writes {@link org.paternostro.elkromm.dto.AreasAndPartitions} as JSON: the number of areas and
 * the array of the areas, then the number of partitions and the array of the partitions.
 * <p>
 * Copyright Ugo Paternostro 2026. Licensed under the EUPL-1.2 or later.
 */
public class AreasAndPartitionsSerializer extends JsonSerializer<AreasAndPartitions>
{
    @Override
    public void serialize(AreasAndPartitions value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeNumberField("areaNum", value.getAreaNum());

        jgen.writeArrayFieldStart("areas");
        for (int i = 0; i < value.getAreaNum(); i++) {
            jgen.writeObject(value.getArea(i));
        }
        jgen.writeEndArray();

        jgen.writeNumberField("partitionNum", value.getPartitionNum());

        // int selfExclusionMask = 0;
        // int armingBlockMask = 0;

        // for (int i = 0; i < ElkrommFacade.MAX_PARTITIONS; i++) {
        //     switch (value.getPartition(i).getType()) {
        //         case STANDARD:
        //             break;
        //         case SELF_EXCLUSION:
        //             selfExclusionMask |= ElkrommFacade.Partition.values()[i].getValue();
        //             break;
        //         case ARMING_BLOCK:
        //             armingBlockMask|= ElkrommFacade.Partition.values()[i].getValue();
        //             break;
        //         case UNKNOWN:
        //             break;
        //     }
        // }

        // jgen.writeNumberField("partitionSelfExclusion", selfExclusionMask);
        // jgen.writeNumberField("partitionArmingBlock", armingBlockMask);
        
        jgen.writeArrayFieldStart("partitions");
        for (int i = 0; i < value.getPartitionNum(); i++) {
            jgen.writeObject(value.getPartition(i));
        }
        jgen.writeEndArray();
        jgen.writeEndObject();    
    }
}
