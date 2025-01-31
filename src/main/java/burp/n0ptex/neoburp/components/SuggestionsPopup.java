package burp.n0ptex.neoburp.components;

import java.awt.Rectangle;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import burp.n0ptex.neoburp.AutoCompletion.AutoCompletion;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletionWords;

class SuggestionsPopup {

    private final JPopupMenu popupMenu;
    private final JTextArea textArea;
    private final AutoCompletion autoCompletion;
    private int selectedIndex = -1;

    public SuggestionsPopup(JTextArea textArea, AutoCompletion autoCompletion) {
        this.textArea = textArea;
        this.autoCompletion = autoCompletion;
        this.popupMenu = new JPopupMenu();
        popupMenu.setFocusable(false);
    }

    public void showSuggestions() {
        applyLookAndFeelTheme();
        popupMenu.removeAll();
        String text = textArea.getText();
        int caretPosition = textArea.getCaretPosition();
        int caretLine = getCaretLine(caretPosition);

        List<String> words = new AutoCompletionWords().getWords();

        autoCompletion.updateAutoCompletionWords(caretLine == 1 ? words.subList(0, 9) : words);

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
        item.addActionListener(e -> replaceWordWithSuggestion(prefix, suggestion));
        return item;
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

    private void replaceWordWithSuggestion(String prefix, String suggestion) {
        try {
            String modSug = suggestion.startsWith("%") ? suggestion.substring(suggestion.indexOf(":") + 1) : suggestion;

            int caretPosition = textArea.getCaretPosition();
            textArea.getDocument().remove(caretPosition - prefix.length(), prefix.length());
            textArea.getDocument().insertString(caretPosition - prefix.length(), modSug, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private int getCaretLine(int caretPosition) {
        try {
            int line = textArea.getLineOfOffset(caretPosition);
            return line + 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
