package com.lbycpd2.archieestimator.jackson.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.lbycpd2.archieestimator.model.CostItem;

import java.io.IOException;
import java.math.BigDecimal;

public class CostItemDeserializer extends JsonDeserializer<CostItem> {
    @Override
    public CostItem deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);

        CostItem costItem = new CostItem();

        costItem.setCostItemID(node.get("costItemID").asInt());
        costItem.setCostItemName(node.get("costItemName").asText());
        costItem.setCostItemUnit(node.get("costItemUnit").asText());
        costItem.setCostItemNotes("");
        costItem.setCostCategoryID(node.get("costCategoryID").asInt());
        costItem.setCostItemNotes(node.get("costItemNotes").asText());
        costItem.setCostItemMaterialUnitCost(new BigDecimal(node.get("costItemMaterialUnitCost").asText()));
        costItem.setCostItemLaborUnitCost(new BigDecimal(node.get("costItemLaborUnitCost").asText()));
        return costItem;
    }
}
