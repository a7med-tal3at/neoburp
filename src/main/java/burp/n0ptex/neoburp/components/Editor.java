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
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.undo.UndoManager;
import javax.swing.text.StyledDocument;

import burp.api.montoya.MontoyaApi;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletion;
import burp.n0ptex.neoburp.helpers.BodyFormatter;
import burp.n0ptex.neoburp.helpers.SimpleDocumentListener;
import javax.swing.SwingUtilities;

public class Editor extends Component {

    private final JPanel editorPanel;
    private final JTextPane textArea;
    private final JScrollPane scrollPane;
    private final SuggestionsPopup suggestionsPopup;
    private final UndoManager undoManager;
    private final MontoyaApi api;
    private HttpSyntaxHighlighter syntaxHighlighter;

    public Editor(MontoyaApi api) {
        this.api = api;
        this.editorPanel = new JPanel(new BorderLayout());
        this.textArea = new JTextPane();
        this.scrollPane = new JScrollPane(textArea);
        this.suggestionsPopup = new SuggestionsPopup(textArea, new AutoCompletion(), api);
        this.undoManager = new UndoManager();
        this.syntaxHighlighter = new HttpSyntaxHighlighter(isUsingDarkTheme());

        NumberLine numberLine = new NumberLine(this.textArea, api);
        scrollPane.setRowHeaderView(numberLine);

        // Add theme change listener
        UIManager.addPropertyChangeListener(evt -> {
            if ("lookAndFeel".equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(this::applyLookAndFeelTheme);
            }
        });

        applyLookAndFeelTheme();
        configureTextArea();
        configureUndoManager();
        configureContextMenu();

        this.textArea.setEditorKit(new javax.swing.text.StyledEditorKit());
        editorPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void setTextAreaContent(String str) {
        api.logging().logToError("Setting text...");
        String formattedText = formatRequestBody(str);
        textArea.setText(formattedText);
        syntaxHighlighter.highlight((StyledDocument) textArea.getDocument());
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

        syntaxHighlighter = new HttpSyntaxHighlighter(isUsingDarkTheme());
        syntaxHighlighter.highlight((StyledDocument) textArea.getDocument());

        suggestionsPopup.applyLookAndFeelTheme();
    }

    private void configureTextArea() {
        textArea.setFont(api.userInterface().currentEditorFont());

        textArea.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                syntaxHighlighter.highlight((StyledDocument) textArea.getDocument());
            }
        });

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == KeyEvent.VK_Z) {
                        undo();
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_Y) {
                        redo();
                        e.consume();
                    }
                }

                if (suggestionsPopup.getVisibility()) {
                    suggestionsPopup.handleKeyEvent(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '.') {
                    suggestionsPopup.showSuggestions();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsPopup.hide();
                } else {
                    suggestionsPopup.changeFocusableState(false);
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

    private boolean isUsingDarkTheme() {
        Color bg = UIManager.getColor("TextArea.background");
        if (bg == null)
            return false;
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return luminance < 0.5;
    }

}
