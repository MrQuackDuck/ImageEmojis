import mrquackduck.imageemojis.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilTests {
    @Test
    void mergeJsons_mergesJsons() {
        // Arrange
        String json1 = "{\n" +
                "  \"providers\": [\n" +
                "    {\n" +
                "      \"type\": \"bitmap\",\n" +
                "      \"file\": \"minecraft:font/clueless.png\",\n" +
                "      \"ascent\": 9,\n" +
                "      \"height\": 10,\n" +
                "      \"chars\": [\n" +
                "        \"\\uF008\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String json2 = "{\n" +
                "  \"providers\": [\n" +
                "    {\n" +
                "      \"type\": \"bitmap\",\n" +
                "      \"file\": \"minecraft:font/what.png\",\n" +
                "      \"ascent\": 9,\n" +
                "      \"height\": 10,\n" +
                "      \"chars\": [\n" +
                "        \"\\uF009\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String expected = "{\n" +
                "  \"providers\": [\n" +
                "    {\n" +
                "      \"type\": \"bitmap\",\n" +
                "      \"file\": \"minecraft:font/clueless.png\",\n" +
                "      \"ascent\": 9,\n" +
                "      \"height\": 10,\n" +
                "      \"chars\": [\n" +
                "        \"\\uF008\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"bitmap\",\n" +
                "      \"file\": \"minecraft:font/what.png\",\n" +
                "      \"ascent\": 9,\n" +
                "      \"height\": 10,\n" +
                "      \"chars\": [\n" +
                "        \"\\uF009\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Act
        String result = JsonUtil.mergeJsons(json1, json2);

        // Assert
        assert result != null;
        JsonElement expectedJson = JsonParser.parseString(expected);
        JsonElement resultJson = JsonParser.parseString(result);
        assertEquals(expectedJson, resultJson);
    }

    @Test
    void mergeJsons_returnsFirstArgumentIfSecondIsBroken() {
        // Arrange
        String json1 = "{\n" +
                "  \"providers\": [\n" +
                "    {\n" +
                "      \"type\": \"bitmap\",\n" +
                "      \"file\": \"minecraft:font/clueless.png\",\n" +
                "      \"ascent\": 9,\n" +
                "      \"height\": 10,\n" +
                "      \"chars\": [\n" +
                "        \"\\uF008\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        String json2 = "{broken};";

        String expected = json1;

        // Act
        String result = JsonUtil.mergeJsons(json1, json2);

        // Assert
        assert result != null;
        JsonElement expectedJson = JsonParser.parseString(expected);
        JsonElement resultJson = JsonParser.parseString(result);
        assertEquals(expectedJson, resultJson);
    }
}