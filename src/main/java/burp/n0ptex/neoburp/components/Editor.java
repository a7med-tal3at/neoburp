package burp.n0ptex.neoburp.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.undo.UndoManager;

import burp.api.montoya.MontoyaApi;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletion;
import burp.n0ptex.neoburp.helpers.BodyFormatter;

public class Editor extends Component {

    private final JPanel editorPanel;
    private final JTextArea textArea;
    private final JScrollPane scrollPane;
    private final SuggestionsPopup suggestionsPopup;
    private final UndoManager undoManager;
    private final MontoyaApi api;

    public Editor(MontoyaApi api) {
        this.api = api;
        this.editorPanel = new JPanel(new BorderLayout());
        this.textArea = new JTextArea();
        this.scrollPane = new JScrollPane(textArea);
        this.suggestionsPopup = new SuggestionsPopup(textArea, new AutoCompletion(), api);
        this.undoManager = new UndoManager();

        NumberLine numberLine = new NumberLine(this.textArea, api);
        scrollPane.setRowHeaderView(numberLine);

        applyLookAndFeelTheme();
        configureTextArea();
        configureUndoManager();
        configureContextMenu();

        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(false);
        editorPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void setTextAreaContent(String str) {
        textArea.setText(formatRequestBody(str));
    }

    public String getTextAreaContent() {
        return textArea.getText();
    }

    private String formatRequestBody(String rawRequest) {
        String[] parts = rawRequest.split("\r\n\r\n", 2);

        api.logging().logToOutput(parts[1]);

        String headers = parts[0];
        String body = parts[1];

        try {
            body = BodyFormatter.formatJson(body);
        } catch (Exception e) {
            body = parts[1];
        }

        return headers + "\r\n\r\n" + body;
    }

    public Component ui() {
        return editorPanel;
    }

    public void refreshTheme() {
        applyLookAndFeelTheme();
    }

    private void applyLookAndFeelTheme() {

        Color backgroundColor = UIManager.getColor("TextArea.background");
        Color foregroundColor = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");

        textArea.setBackground(backgroundColor != null ? backgroundColor : Color.WHITE);
        textArea.setForeground(foregroundColor != null ? foregroundColor : Color.BLACK);
        textArea.setCaretColor(caretColor != null ? caretColor : Color.BLACK);

        suggestionsPopup.applyLookAndFeelTheme();
    }

    private void configureTextArea() {
        textArea.setFont(api.userInterface().currentEditorFont());
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    undo();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                    redo();
                } else if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
                    suggestionsPopup.showSuggestions();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsPopup.hide();
                } else {
                    suggestionsPopup.changeFocusableState(false);
                }
            }
        });

        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (suggestionsPopup.getVisibility()) {
                    suggestionsPopup.handleKeyEvent(e);
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
                    suggestionsPopup.showSuggestions();
                } else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    suggestionsPopup.hide();
                }
            }
        });

    }

    private void configureUndoManager() {
        textArea.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
    }

    private void configureContextMenu() {
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showContextMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showContextMenu(e);
            }

            private void showContextMenu(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = ContextMenu.create(textArea);
                    popupMenu.show(textArea, e.getX(), e.getY());
                }
            }
        });
    }

    private void undo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
        }
    }

    private void redo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        }
    }
}
