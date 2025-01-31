package burp.n0ptex.neoburp.helpers;

public class BodyFormatter {

    public static String formatJson(String jsonString) {
        StringBuilder formattedJson = new StringBuilder();
        int indentationLevel = 0;
        boolean insideString = false;

        for (int i = 0; i < jsonString.length(); i++) {
            char currentChar = jsonString.charAt(i);
            char nextChar = (i + 1 < jsonString.length()) ? jsonString.charAt(i + 1) : ' ';
            char prevChar = (i - 1 >= 0) ? jsonString.charAt(i - 1) : ' ';

            if (currentChar == '"' && (i == 0 || jsonString.charAt(i - 1) != '\\')) {
                insideString = !insideString;
            }

            if (insideString) {
                formattedJson.append(currentChar);
            } else {
                if ((currentChar == '{' || currentChar == '[') && nextChar != '\n') {
                    formattedJson.append(currentChar);
                    formattedJson.append("\n");
                    indentationLevel++;
                    addIndentation(formattedJson, indentationLevel);
                } else if ((currentChar == '}' || currentChar == ']') && (nextChar != '\n' && prevChar != '\n')) {
                    formattedJson.append("\n");
                    indentationLevel--;
                    addIndentation(formattedJson, indentationLevel);
                    formattedJson.append(currentChar);
                } else if (currentChar == ',' && nextChar != '\n') {
                    formattedJson.append(currentChar);
                    formattedJson.append("\n");
                    addIndentation(formattedJson, indentationLevel);
                } else if (currentChar != ' ' || (currentChar == ' ' && (nextChar != ' ' && nextChar != ','))) {
                    formattedJson.append(currentChar);
                }
            }
        }

        return formattedJson.toString();
    }

    private static void addIndentation(StringBuilder builder, int level) {
        for (int i = 0; i < level; i++) {
            builder.append("  ");
        }
    }

}
