package com.lbycpd2.archieestimator.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lbycpd2.archieestimator.cell.CostRow;
import com.lbycpd2.archieestimator.file.deserializer.CostItemDeserializer;
import com.lbycpd2.archieestimator.file.deserializer.CostRowDeserializer;
import com.lbycpd2.archieestimator.file.deserializer.TabTableDeserializer;
import com.lbycpd2.archieestimator.model.CostItem;
import com.lbycpd2.archieestimator.table.TabTable;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JsonReader {
    private final ObjectMapper objectMapper;

    public JsonReader() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CostItem.class, new CostItemDeserializer());
        module.addDeserializer(CostRow.class, new CostRowDeserializer());
        module.addDeserializer(TabTable.class, new TabTableDeserializer());
        objectMapper.registerModule(module);
    }

    public List<TabTable> readJson(String filePath) throws IOException {
        return objectMapper.readValue(new File(filePath), objectMapper.getTypeFactory().constructCollectionType(List.class, TabTable.class));
    }
}
