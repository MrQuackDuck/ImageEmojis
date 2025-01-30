package mrquackduck.imageemojis.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
    public static String mergeJsons(String... jsons) {
        for (int i = 0; i < jsons.length; i++) {
            jsons[i] = jsons[i].replace("\\u", "|u");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode result;

        // Process first JSON as base
        try {
            result = mapper.readTree(jsons[0]);
        } catch (Exception e) {
            return null; // Return null if base JSON is invalid
        }

        // Process remaining JSONs
        for (int i = 1; i < jsons.length; i++) {
            if (jsons[i] == null) continue;

            try {
                JsonNode current = mapper.readTree(jsons[i]);
                result = mergeNodes(result, current);
            }
            catch (Exception e) { /* Skip invalid JSON */ }
        }

        try {
            String resultStringified = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            resultStringified = resultStringified.replace("|u", "\\u");
            return resultStringified;
        }
        catch (Exception e) { return null; }
    }

    private static JsonNode mergeNodes(JsonNode node1, JsonNode node2) {
        if (node1 == null) return node2;
        if (node2 == null) return node1;

        if (!node1.isObject()) return node1;
        if (!node2.isObject()) return node1;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode merged = mapper.createObjectNode();

        // Combine all fields from both nodes
        node1.fields().forEachRemaining(field -> {
            String fieldName = field.getKey();
            JsonNode value1 = field.getValue();
            JsonNode value2 = node2.get(fieldName);

            if (value2 == null) {
                merged.set(fieldName, value1);
            } else if (value1.isArray() && value2.isArray()) {
                // Merge arrays
                ArrayNode mergedArray = mapper.createArrayNode();
                value1.forEach(mergedArray::add);
                value2.forEach(mergedArray::add);
                merged.set(fieldName, mergedArray);
            } else if (value1.isObject() && value2.isObject()) {
                // Recursively merge objects
                merged.set(fieldName, mergeNodes(value1, value2));
            } else {
                merged.set(fieldName, value1);
            }
        });

        // Add fields from node2 that don't exist in node1
        node2.fields().forEachRemaining(field -> {
            String fieldName = field.getKey();
            if (!merged.has(fieldName)) {
                merged.set(fieldName, field.getValue());
            }
        });

        return merged;
    }
}
