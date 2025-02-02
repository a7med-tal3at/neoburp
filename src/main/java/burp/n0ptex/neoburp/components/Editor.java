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
import javax.swing.JEditorPane;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;
import javax.swing.text.Element;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.LabelView;
import javax.swing.text.View;
import javax.swing.text.ParagraphView;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.IconView;

import burp.api.montoya.MontoyaApi;
import burp.n0ptex.neoburp.AutoCompletion.AutoCompletion;
import burp.n0ptex.neoburp.helpers.BodyFormatter;
import burp.n0ptex.neoburp.helpers.SimpleDocumentListener;
import javax.swing.SwingUtilities;

public class Editor extends Component {

    private final JPanel editorPanel;
    private final JTextPane textPane;
    private final JScrollPane scrollPane;
    private final SuggestionsPopup suggestionsPopup;
    private final UndoManager undoManager;
    private final MontoyaApi api;
    private HttpSyntaxHighlighter syntaxHighlighter;

    public Editor(MontoyaApi api) {
        this.api = api;
        this.editorPanel = new JPanel(new BorderLayout());
        this.textPane = new JTextPane();
        this.scrollPane = new JScrollPane(textPane);
        this.suggestionsPopup = new SuggestionsPopup(textPane, new AutoCompletion(), api);
        this.undoManager = new UndoManager();
        this.syntaxHighlighter = new HttpSyntaxHighlighter(isUsingDarkTheme());

        NumberLine numberLine = new NumberLine(this.textPane, api);
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

        // Configure word wrap
        textPane.setEditorKit(new WrapEditorKit());
        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        editorPanel.add(scrollPane, BorderLayout.CENTER);
    }

    public void setTextAreaContent(String str) {
        api.logging().logToOutput("Setting text...");
        String formattedText = formatRequestBody(str);
        textPane.setText(formattedText);
        syntaxHighlighter.highlight((StyledDocument) textPane.getDocument());
        textPane.setCaretPosition(0);
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    public String getTextAreaContent() {
        return textPane.getText();
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
        Color backgroundColor = UIManager.getColor("TextPane.background");
        Color foregroundColor = UIManager.getColor("TextPane.foreground");
        Color caretColor = UIManager.getColor("TextPane.caretForeground");

        textPane.setBackground(backgroundColor != null ? backgroundColor : Color.WHITE);
        textPane.setForeground(foregroundColor != null ? foregroundColor : Color.BLACK);
        textPane.setCaretColor(caretColor != null ? caretColor : Color.BLACK);

        syntaxHighlighter = new HttpSyntaxHighlighter(isUsingDarkTheme());
        syntaxHighlighter.highlight((StyledDocument) textPane.getDocument());

        suggestionsPopup.applyLookAndFeelTheme();
    }

    private void configureTextArea() {
        textPane.setFont(api.userInterface().currentEditorFont());
        textPane.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                syntaxHighlighter.highlight((StyledDocument) textPane.getDocument());
            }
        });

        textPane.addKeyListener(new KeyAdapter() {
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
        textPane.getDocument().addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));
    }

    private void configureContextMenu() {
        textPane.addMouseListener(new MouseAdapter() {
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
                    JPopupMenu popupMenu = ContextMenu.create(textPane);
                    popupMenu.show(textPane, e.getX(), e.getY());
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
        Color bg = UIManager.getColor("TextPane.background");
        if (bg == null)
            return false;
        double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return luminance < 0.5;
    }

    private static class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory = new WrapColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    private static class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                switch (kind) {
                    case AbstractDocument.ContentElementName:
                        return new WrapLabelView(elem);
                    case AbstractDocument.ParagraphElementName:
                        return new ParagraphView(elem);
                    case AbstractDocument.SectionElementName:
                        return new BoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName:
                        return new ComponentView(elem);
                    case StyleConstants.IconElementName:
                        return new IconView(elem);
                }
            }
            return new LabelView(elem);
        }
    }

    private static class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }

}
