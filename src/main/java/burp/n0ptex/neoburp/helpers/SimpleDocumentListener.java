package burp.n0ptex.neoburp.helpers;

public abstract class SimpleDocumentListener implements javax.swing.event.DocumentListener {

    public abstract void update();

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        update();
    }
}
