package burp.n0ptex.neoburp.components;

import java.awt.Rectangle;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;
import javax.swing.text.Element;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;

import burp.api.montoya.MontoyaApi;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletion;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletionWords;

class SuggestionsPopup {

    private final JPopupMenu popupMenu;
    private final JTextComponent textArea;
    private final AutoCompletion autoCompletion;
    private int selectedIndex = -1;
    private MontoyaApi api;

    public SuggestionsPopup(JTextComponent textComponent, AutoCompletion autoCompletion, MontoyaApi api) {
        this.textArea = textComponent;
        this.autoCompletion = autoCompletion;
        this.popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);
        this.api = api;
    }

    public void showSuggestions() {
        applyLookAndFeelTheme();
        popupMenu.removeAll();
        String text = textArea.getText();
        int caretPosition = textArea.getCaretPosition();
        String currentLineContent = getCurrentLineContent();
        List<String> words = new AutoCompletionWords().getWords();

        autoCompletion.updateAutoCompletionWords(
                currentLineContent.matches(".*HTTP/[12](?:\\.1)?.*") ? words.subList(0, 9) : words);

        String prefix = getWordPrefix(text, caretPosition);

        if (prefix.isEmpty()) {
            hide();
            return;
        }

        List<String> suggestions = autoCompletion.suggest(prefix);
        if (suggestions.isEmpty()) {
            hide();
            return;
        }

        selectedIndex = 0;
        for (int i = 0; i < suggestions.size(); i++) {
            String suggestion = suggestions.get(i);
            JMenuItem item = createSuggestionItem(prefix, suggestion);
            popupMenu.add(item);
        }

        try {
            Rectangle caretRect = textArea.modelToView2D(caretPosition).getBounds();
            popupMenu.show(textArea, caretRect.x, caretRect.y + caretRect.height);
            updateSelection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void handleKeyEvent(java.awt.event.KeyEvent e) {
        if (!popupMenu.isVisible()) {
            return;
        }

        int itemCount = popupMenu.getComponentCount();
        if (itemCount == 0) {
            return;
        }

        switch (e.getKeyCode()) {
            case java.awt.event.KeyEvent.VK_DOWN:
                selectedIndex = (selectedIndex + 1) % itemCount;
                updateSelection();
                e.consume();
                break;
            case java.awt.event.KeyEvent.VK_UP:
                selectedIndex = (selectedIndex - 1 + itemCount) % itemCount;
                updateSelection();
                e.consume();
                break;
            case java.awt.event.KeyEvent.VK_ENTER:
                if (selectedIndex != -1) {
                    JMenuItem selectedItem = (JMenuItem) popupMenu.getComponent(selectedIndex);
                    selectedItem.doClick();
                }
                hide();
                e.consume();
                break;
            case java.awt.event.KeyEvent.VK_ESCAPE:
                hide();
                e.consume();
                break;
            default:
                break;
        }
    }

    public void hide() {
        popupMenu.setVisible(false);
    }

    public boolean getVisibility() {
        return popupMenu.isVisible();
    }

    public void changeFocusableState(boolean state) {
        this.popupMenu.setFocusable(state);
    }

    public void applyLookAndFeelTheme() {
        popupMenu.setBackground(UIManager.getColor("PopupMenu.background"));
        popupMenu.setBorder(BorderFactory.createLineBorder(UIManager.getColor("PopupMenu.border")));
    }

    private JMenuItem createSuggestionItem(String prefix, String suggestion) {
        JMenuItem item = new JMenuItem(suggestion);
        item.setFont(textArea.getFont());
        item.setBackground(UIManager.getColor("MenuItem.background"));
        item.setForeground(UIManager.getColor("MenuItem.foreground"));
        item.setOpaque(true);
        item.addActionListener(e -> {
            try {
                int caretPos = textArea.getCaretPosition();
                int prefixStart = caretPos - prefix.length();
                textArea.getDocument().remove(prefixStart, prefix.length());
                textArea.getDocument().insertString(prefixStart, suggestion, null);
                textArea.setCaretPosition(prefixStart + suggestion.length());
                if (textArea instanceof JTextPane) {
                    HttpSyntaxHighlighter highlighter = new HttpSyntaxHighlighter(isUsingDarkTheme());
                    highlighter.highlight((StyledDocument) textArea.getDocument());
                }
                hide();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return item;
    }

    private boolean isUsingDarkTheme() {
        Color bg = UIManager.getColor("TextArea.background");
        if (bg == null)
            return false;
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return luminance < 0.5;
    }

    private String getWordPrefix(String text, int caretPosition) {
        StringBuilder prefix = new StringBuilder();
        int i = caretPosition - 1;

        while (i >= 0 && Character.isLetterOrDigit(text.charAt(i))) {
            prefix.insert(0, text.charAt(i));
            i--;
        }

        return prefix.toString();
    }

    private void updateSelection() {
        for (int i = 0; i < popupMenu.getComponentCount(); i++) {
            JMenuItem item = (JMenuItem) popupMenu.getComponent(i);
            if (i == selectedIndex) {
                item.setBackground(UIManager.getColor("MenuItem.selectionBackground"));
                item.setForeground(UIManager.getColor("MenuItem.selectionForeground"));
            } else {
                item.setBackground(UIManager.getColor("MenuItem.background"));
                item.setForeground(UIManager.getColor("MenuItem.foreground"));
            }
        }
    }

    private String getCurrentLineContent() {
        try {
            int caretPos = textArea.getCaretPosition();
            Element root = textArea.getDocument().getDefaultRootElement();
            int lineNum = root.getElementIndex(caretPos);
            Element line = root.getElement(lineNum);
            int lineStart = line.getStartOffset();
            int lineEnd = line.getEndOffset();
            String content = textArea.getText(lineStart, lineEnd - lineStart);

            return content.replaceAll("\\r\\n|\\r|\\n", "");
        } catch (Exception e) {
            api.logging().logToError("Error getting current line content: " + e.getMessage());
            return "";
        }
    }
}
