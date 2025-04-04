package com.lbycpd2.archieestimator.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.file.serializer.CostItemSerializer;
import com.lbycpd2.archieestimator.file.serializer.CostRowSerializer;
import com.lbycpd2.archieestimator.file.serializer.TabTableSerializer;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.table.TabTable;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class JsonWriter {
    public void writeTabsToJson(List<TabTable> tabTableList, String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(TabTable.class, new TabTableSerializer());
        module.addSerializer(CostRow.class, new CostRowSerializer());
        module.addSerializer(CostItem.class, new CostItemSerializer());
        objectMapper.registerModule(module);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(filePath), tabTableList);
            System.out.println("Tabs saved to JSON file: " + filePath);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
