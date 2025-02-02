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
            return jsonString;
        }
    }

}
