package burp.n0ptex.neoburp.components;

import javax.swing.text.*;
import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpSyntaxHighlighter {
  private static final Pattern HTTP_METHOD_PATTERN = Pattern
      .compile("^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS|TRACE|CONNECT)\\s", Pattern.MULTILINE);
  private static final Pattern HTTP_VERSION_PATTERN = Pattern.compile("HTTP/[0-9.]+$", Pattern.MULTILINE);
  private static final Pattern HEADER_PATTERN = Pattern.compile("^([^:\\s]+):\\s*(.*)$", Pattern.MULTILINE);
  private static final Pattern JSON_KEY_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:");
  private static final Pattern JSON_STRING_PATTERN = Pattern.compile(":\\s*\"([^\"]+)\"");
  private static final Pattern JSON_NUMBER_PATTERN = Pattern.compile(":\\s*(\\d+(?:\\.\\d+)?)");

  private final StyleContext styleContext;
  private final AttributeSet methodStyle;
  private final AttributeSet httpVersionStyle;
  private final AttributeSet headerNameStyle;
  private final AttributeSet headerValueStyle;
  private final AttributeSet jsonKeyStyle;
  private final AttributeSet jsonStringStyle;
  private final AttributeSet jsonNumberStyle;
  private final AttributeSet defaultStyle;

  public HttpSyntaxHighlighter(boolean isDarkTheme) {
    styleContext = StyleContext.getDefaultStyleContext();

    Color methodColor;
    Color httpVersionColor;
    Color headerNameColor;
    Color headerValueColor;
    Color jsonKeyColor;
    Color jsonStringColor;
    Color jsonNumberColor;
    Color defaultColor;

    if (isDarkTheme) {
      methodColor = new Color(255, 128, 128); // Light red
      httpVersionColor = new Color(128, 255, 128); // Light green
      headerNameColor = new Color(128, 128, 255); // Light blue
      headerValueColor = new Color(192, 192, 192); // Light gray
      jsonKeyColor = new Color(255, 215, 0); // Gold
      jsonStringColor = new Color(144, 238, 144); // Light green
      jsonNumberColor = new Color(255, 165, 0); // Orange
      defaultColor = Color.WHITE;
    } else {
      methodColor = new Color(204, 0, 0); // Dark red
      httpVersionColor = new Color(0, 153, 0); // Dark green
      headerNameColor = new Color(0, 0, 204); // Dark blue
      headerValueColor = new Color(102, 102, 102); // Dark gray
      jsonKeyColor = new Color(153, 0, 85); // Purple
      jsonStringColor = new Color(0, 136, 0); // Green
      jsonNumberColor = new Color(0, 102, 204); // Blue
      defaultColor = Color.BLACK;
    }

    methodStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, methodColor);
    httpVersionStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, httpVersionColor);
    headerNameStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, headerNameColor);
    headerValueStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, headerValueColor);
    jsonKeyStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, jsonKeyColor);
    jsonStringStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, jsonStringColor);
    jsonNumberStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, jsonNumberColor);
    defaultStyle = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, defaultColor);
  }

  public void highlight(StyledDocument doc) {
    try {
      String text = doc.getText(0, doc.getLength());

      // Reset all styling to default
      doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

      // Highlight HTTP method
      Matcher methodMatcher = HTTP_METHOD_PATTERN.matcher(text);
      while (methodMatcher.find()) {
        doc.setCharacterAttributes(methodMatcher.start(), methodMatcher.group().length(), methodStyle, false);
      }

      // Highlight HTTP version
      Matcher versionMatcher = HTTP_VERSION_PATTERN.matcher(text);
      while (versionMatcher.find()) {
        doc.setCharacterAttributes(versionMatcher.start(), versionMatcher.group().length(), httpVersionStyle, false);
      }

      // Highlight headers
      Matcher headerMatcher = HEADER_PATTERN.matcher(text);
      while (headerMatcher.find()) {
        doc.setCharacterAttributes(headerMatcher.start(1), headerMatcher.group(1).length(), headerNameStyle, false);
        doc.setCharacterAttributes(headerMatcher.start(2), headerMatcher.group(2).length(), headerValueStyle, false);
      }

      // Find JSON content and highlight it
      int jsonStart = text.indexOf("\r\n\r\n") + 4;
      if (jsonStart > 3 && jsonStart < text.length()) {
        String jsonContent = text.substring(jsonStart);

        // Highlight JSON keys
        Matcher keyMatcher = JSON_KEY_PATTERN.matcher(jsonContent);
        while (keyMatcher.find()) {
          doc.setCharacterAttributes(jsonStart + keyMatcher.start(1), keyMatcher.group(1).length(), jsonKeyStyle,
              false);
        }

        // Highlight JSON strings
        Matcher stringMatcher = JSON_STRING_PATTERN.matcher(jsonContent);
        while (stringMatcher.find()) {
          doc.setCharacterAttributes(jsonStart + stringMatcher.start(1), stringMatcher.group(1).length(),
              jsonStringStyle, false);
        }

        // Highlight JSON numbers
        Matcher numberMatcher = JSON_NUMBER_PATTERN.matcher(jsonContent);
        while (numberMatcher.find()) {
          doc.setCharacterAttributes(jsonStart + numberMatcher.start(1), numberMatcher.group(1).length(),
              jsonNumberStyle, false);
        }
      }
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }
}