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
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.text.BadLocationException;
import javax.swing.SwingUtilities;

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
    private VimState vimState;
    private JLabel modeLabel;

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

        this.vimState = new VimState();
        this.modeLabel = new JLabel("NORMAL");
        this.modeLabel.setOpaque(true);
        this.modeLabel.setBackground(Color.LIGHT_GRAY);
        this.modeLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(modeLabel, BorderLayout.EAST);
        editorPanel.add(statusPanel, BorderLayout.SOUTH);

        configureVimKeyBindings();
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
                // Only handle these events in INSERT mode
                if (vimState.getCurrentMode() != VimState.Mode.INSERT) {
                    return; // Exit early if not in insert mode
                }

                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                    undo();
                } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                    redo();
                } else if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
                    suggestionsPopup.showSuggestions();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsPopup.hide();
                    vimState.setMode(VimState.Mode.NORMAL);
                    modeLabel.setText("NORMAL");
                    e.consume();
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

    private void configureVimKeyBindings() {
        KeyAdapter vimKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (vimState.getCurrentMode() == VimState.Mode.NORMAL) {
                    handleNormalMode(e);
                    e.consume();
                    suggestionsPopup.hide(); // Force hide suggestions in normal mode
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (vimState.getCurrentMode() == VimState.Mode.NORMAL) {
                    e.consume(); // Prevent key typing in normal mode
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (vimState.getCurrentMode() == VimState.Mode.NORMAL) {
                    e.consume(); // Prevent key release events in normal mode
                }
            }
        };

        // Add the vim key adapter first, so it gets priority
        textArea.addKeyListener(vimKeyAdapter);
    }

    private void handleNormalMode(KeyEvent e) {
        e.consume(); // Consume the event first

        switch (e.getKeyChar()) {
            case 'i':
                SwingUtilities.invokeLater(() -> {
                    vimState.setMode(VimState.Mode.INSERT);
                    modeLabel.setText("INSERT");
                });
                break;
            case 'h':
                moveCursor(-1);
                break;
            case 'l':
                moveCursor(1);
                break;
            case 'j':
                moveVertical(1);
                break;
            case 'k':
                moveVertical(-1);
                break;
            case 'x':
                deleteCharAtCursor();
                break;
        }
    }

    private void handleInsertMode(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            vimState.setMode(VimState.Mode.NORMAL);
            modeLabel.setText("NORMAL");
            moveCursor(-1); // Move cursor back one position
            e.consume();
        }
    }

    private void handleVisualMode(KeyEvent e) {
        // Implement visual mode handling
    }

    private void moveCursor(int offset) {
        int newPosition = textArea.getCaretPosition() + offset;
        if (newPosition >= 0 && newPosition < textArea.getText().length()) {
            textArea.setCaretPosition(newPosition);
        }
    }

    private void moveVertical(int lines) {
        try {
            int currentLine = textArea.getLineOfOffset(textArea.getCaretPosition());
            int targetLine = currentLine + lines;
            if (targetLine >= 0 && targetLine < textArea.getLineCount()) {
                int targetOffset = textArea.getLineStartOffset(targetLine);
                textArea.setCaretPosition(targetOffset);
            }
        } catch (BadLocationException ex) {
            // Handle exception
        }
    }

    private void deleteCharAtCursor() {
        try {
            int pos = textArea.getCaretPosition();
            if (pos < textArea.getText().length()) {
                textArea.getDocument().remove(pos, 1);
            }
        } catch (BadLocationException ex) {
            // Handle exception
        }
    }
}
