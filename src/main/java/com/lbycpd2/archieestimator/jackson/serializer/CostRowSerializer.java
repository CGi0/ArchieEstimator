package com.lbycpd2.archieestimator.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lbycpd2.archieestimator.cell.CostRow;

import java.io.IOException;

public class CostRowSerializer extends JsonSerializer<CostRow> {
    @Override
    public void serialize(CostRow costRow, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("costItem", costRow.getCostItem());
        jsonGenerator.writeNumberField("costItemMaterialUnitCost", costRow.getCostItemMaterialUnitCost());
        jsonGenerator.writeNumberField("costItemLaborUnitCost", costRow.getCostItemLaborUnitCost());
        jsonGenerator.writeNumberField("costItemQuantity", costRow.getCostItemQuantity());
        jsonGenerator.writeEndObject();
    }
}

