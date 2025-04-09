package com.lbycpd2.archieestimator.jackson.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Map;

public class ParametersSerializer extends StdSerializer<Map<String, Object>> {

    public ParametersSerializer() {
        super((Class<Map<String, Object>>) (Class<?>) Map.class); // Cast required for raw type
    }

    @Override
    public void serialize(Map<String, Object> map, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            gen.writeFieldName(entry.getKey());

            gen.writeStartObject();
            gen.writeStringField("type", entry.getValue().getClass().getName());
            gen.writeObjectField("value", entry.getValue());
            gen.writeEndObject();
        }

        gen.writeEndObject();
    }


}


