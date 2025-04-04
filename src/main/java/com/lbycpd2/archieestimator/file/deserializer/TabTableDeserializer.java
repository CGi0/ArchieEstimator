package com.lbycpd2.archieestimator.file.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.service.CostTableControllerService;
import com.lbycpd2.archieestimator.table.TabTable;
import javafx.scene.control.TreeItem;

import java.io.IOException;

public class TabTableDeserializer extends JsonDeserializer<TabTable> {

    @Override
    public TabTable deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = mapper.readTree(jsonParser);
        TabTable tabTable = new TabTable(rootNode.get("tabName").asText(), CostTableControllerService.getInstance().getCostTableController());
        for (JsonNode rowNode : rootNode.get("costRows")) {
            CostRow costRow = mapper.treeToValue(rowNode, CostRow.class);
            TreeItem<CostRow> costRowTreeItem = new TreeItem<>(costRow);
            tabTable.getTreeTableView().getRoot().getChildren().add(costRowTreeItem);
        }
        return tabTable;
    }
}

