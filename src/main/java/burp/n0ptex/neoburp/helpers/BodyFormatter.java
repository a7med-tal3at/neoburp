package burp.n0ptex.neoburp.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public class BodyFormatter {

    public static String formatJson(String jsonString) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = gson.fromJson(jsonString, JsonElement.class);
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            // If parsing fails, return the original string
            return jsonString;
        }
    }

    private static void addIndentation(StringBuilder builder, int level) {
        for (int i = 0; i < level; i++) {
            builder.append("  ");
        }
    }

}
