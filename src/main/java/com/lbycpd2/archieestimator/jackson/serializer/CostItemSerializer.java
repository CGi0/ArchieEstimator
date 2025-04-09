package com.lbycpd2.archieestimator.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lbycpd2.archieestimator.model.CostItem;

import java.io.IOException;

public class CostItemSerializer extends JsonSerializer<CostItem> {
    @Override
    public void serialize(CostItem costItem, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("costItemID", costItem.getCostItemID());
        jsonGenerator.writeStringField("costItemName", costItem.getCostItemName());
        jsonGenerator.writeNumberField("costCategoryID", costItem.getCostCategoryID());
        jsonGenerator.writeStringField("costItemNotes", costItem.getCostItemNotes());
        jsonGenerator.writeStringField("costItemUnit", costItem.getCostItemUnit());
        jsonGenerator.writeNumberField("costItemMaterialUnitCost", costItem.getCostItemMaterialUnitCost().doubleValue());
        jsonGenerator.writeNumberField("costItemLaborUnitCost", costItem.getCostItemLaborUnitCost().doubleValue());
        jsonGenerator.writeEndObject();
    }
}

