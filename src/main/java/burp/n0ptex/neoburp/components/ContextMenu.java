package burp.n0ptex.neoburp.components;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;

public class ContextMenu {

    public static JPopupMenu create(JTextPane textPane) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(evt -> textPane.copy());

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(evt -> textPane.paste());

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(evt -> textPane.cut());

        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(cutItem);

        return popupMenu;
    }
}
