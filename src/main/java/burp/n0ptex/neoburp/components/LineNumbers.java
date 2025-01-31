package burp.n0ptex.neoburp.components;

import java.awt.Color;

import javax.swing.JTextArea;

import burp.api.montoya.MontoyaApi;

public class LineNumbers {

    private final JTextArea lineNumberArea;
    private final JTextArea textArea;
    private final MontoyaApi api;

    public LineNumbers(JTextArea textArea, MontoyaApi api) {
        this.textArea = textArea;
        this.api = api;
        this.lineNumberArea = new JTextArea("1");
        configureLineNumberArea();
    }

    public JTextArea getLineNumberArea() {
        return lineNumberArea;
    }

    public void updateLineNumbers() {
        int totalLines = textArea.getLineCount();
        StringBuilder lineNumberText = new StringBuilder();

        for (int i = 1; i <= totalLines; i++) {
            lineNumberText.append(i).append(System.lineSeparator());
        }

        lineNumberArea.setText(lineNumberText.toString());
    }

    public void setBackground(Color c) {
        lineNumberArea.setBackground(c);
    }

    public void setForeground(Color c) {
        lineNumberArea.setForeground(c);
    }

    private void configureLineNumberArea() {
        lineNumberArea.setFont(api.userInterface().currentEditorFont());
        lineNumberArea.setEditable(false);
        lineNumberArea.setColumns(3);
        lineNumberArea.setHighlighter(null);
    }
}
