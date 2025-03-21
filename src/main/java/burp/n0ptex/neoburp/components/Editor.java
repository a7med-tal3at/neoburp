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
import burp.n0ptex.neoburp.helpers.Enums.AutoCompletionType;

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
        textArea.setCaretPosition(0);
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    public String getTextAreaContent() {
        return textArea.getText();
    }

    private String formatRequestBody(String rawRequest) {
        String[] parts = rawRequest.split("\r\n\r\n", 2);
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

    private void applyLookAndFeelTheme() {

        Color backgroundColor = UIManager.getColor("TextArea.background");
        Color foregroundColor = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");

        textArea.setBackground(backgroundColor != null ? backgroundColor : Color.WHITE);
        textArea.setForeground(foregroundColor != null ? foregroundColor : Color.BLACK);
        textArea.setCaretColor(caretColor != null ? caretColor : Color.BLACK);

        suggestionsPopup.applyLookAndFeelTheme();
    }

    private String getCurrentLineContent() {
        try {
            int caretPos = textArea.getCaretPosition();
            int lineNum = textArea.getLineOfOffset(caretPos);
            int lineStart = textArea.getLineStartOffset(lineNum);
            int lineEnd = textArea.getLineEndOffset(lineNum);
            String content = textArea.getText(lineStart, lineEnd - lineStart);

            return content.replaceAll("\\r\\n|\\r|\\n", "");
        } catch (Exception e) {
            api.logging().logToError("Error getting current line content: " + e.getMessage());
            return "";
        }
    }

    private void configureTextArea() {
        textArea.setFont(api.userInterface().currentEditorFont());
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String currentText = textArea.getText();
                String currentLine = getCurrentLineContent();
                int currentCaretPosition = textArea.getCaretPosition();

                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    undo();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                    redo();

                } else if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
                    suggestionsPopup.showSuggestions(currentLine, currentText, currentCaretPosition,
                            currentLine.matches(".*HTTP/[12](?:\\.1)?.*") ? AutoCompletionType.METHODS
                                    : AutoCompletionType.HEADERS);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsPopup.hide();
                } else {
                    suggestionsPopup.changeFocusableState(false);
                }
            }
        });

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (suggestionsPopup.getVisibility()) {
                    suggestionsPopup.handleKeyEvent(e);
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
