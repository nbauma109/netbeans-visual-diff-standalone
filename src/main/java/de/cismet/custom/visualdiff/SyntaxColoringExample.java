package de.cismet.custom.visualdiff;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.netbeans.modules.editor.java.JavaKit;
import org.openide.text.CloneableEditorSupport;

public class SyntaxColoringExample {

    public static void main(String[] args) {

        JFrame f = new JFrame("JAVA Syntax Coloring");

        JEditorPane pane = new JEditorPane();
        pane.setEditorKit(CloneableEditorSupport.getEditorKit(JavaKit.JAVA_MIME_TYPE));
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        pane.getDocument().insertString(0, text, null);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(new JScrollPane(pane));
        f.setSize(400, 300);
        f.setVisible(true);
    }

    public static final String text = "public class Hello {} }";

}
