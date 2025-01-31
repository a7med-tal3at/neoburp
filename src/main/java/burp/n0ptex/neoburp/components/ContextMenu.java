package burp.n0ptex.neoburp.components;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

public class ContextMenu {

    public static JPopupMenu create(JTextArea textArea) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(evt -> textArea.copy());

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(evt -> textArea.paste());

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(evt -> textArea.cut());

        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(cutItem);

        return popupMenu;
    }
}
