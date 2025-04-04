package com.lbycpd2.archieestimator.file.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.model.CostItem;

import java.io.IOException;
import java.math.BigDecimal;

public class CostRowDeserializer extends JsonDeserializer<CostRow> {

    @Override
    public CostRow deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        CostItem costItem = jsonParser.getCodec().treeToValue(node.get("costItem"), CostItem.class);
        CostRow costRow = new CostRow(costItem);
        costRow.setCostItemMaterialUnitCost(new BigDecimal(node.get("costItemMaterialUnitCost").asText()));
        costRow.setCostItemLaborUnitCost(new BigDecimal(node.get("costItemLaborUnitCost").asText()));
        costRow.setCostItemQuantity(new BigDecimal(node.get("costItemQuantity").asText()));
        return costRow;
    }
}
