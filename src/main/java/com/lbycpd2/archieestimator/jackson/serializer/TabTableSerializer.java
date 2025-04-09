package com.lbycpd2.archieestimator.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lbycpd2.archieestimator.table.TabTable;
import com.lbycpd2.archieestimator.cell.CostRow;

import java.io.IOException;

public class TabTableSerializer extends JsonSerializer<TabTable> {
    @Override
    public void serialize(TabTable tabTable, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("tabName", tabTable.getTab().getText());
        jsonGenerator.writeArrayFieldStart("costRows");
        for (CostRow costRow : tabTable.getCostRowsExportList()) {
            jsonGenerator.writeObject(costRow);
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}

