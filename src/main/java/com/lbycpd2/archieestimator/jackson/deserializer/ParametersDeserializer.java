package com.lbycpd2.archieestimator.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Locale;

@Slf4j
public class ParametersDeserializer extends JsonDeserializer<Object> {

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode rootNode = parser.getCodec().readTree(parser);

        // Extract "type" and "value" fields directly
        String type = rootNode.get("type").asText();
        JsonNode valueContent = rootNode.get("value");

        // Determine the value based on the "type"
        return switch (type) {
            case "java.math.BigDecimal" -> valueContent.decimalValue();
            case "java.lang.String" -> valueContent.asText();
            case "java.lang.Integer" -> valueContent.asInt();
            case "java.lang.Double" -> valueContent.asDouble();
            case "java.lang.Boolean" -> valueContent.asBoolean();
            case "java.util.Locale" -> {
                String[] localeParts = valueContent.asText().split("_");
                if (localeParts.length == 2) {
                    yield new Locale(localeParts[0], localeParts[1]);
                } else {
                    throw new IllegalArgumentException("Invalid locale format: " + valueContent.asText());
                }
            }
            default -> throw new IllegalStateException("Unexpected type: " + type);
        };

    }
}
