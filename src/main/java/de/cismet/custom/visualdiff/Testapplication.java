/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 * 
* ... and it just works.
 * 
***************************************************
 */
package de.cismet.custom.visualdiff;

import org.openide.util.Exceptions;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

/**
 * This is a test which demonstrates the use of the VisualDiff component.
 *
 * <p>
 * In order to run this test application, make sure that the directory
 * <code>\META-INF\services</code> in the target directory or built jar file
 * contains</p>
 *
 * <ul>
 * <li><code>org.netbeans.api.diff.Diff</code> containing <code>
 *     org.netbeans.modules.diff.builtin.DefaultDiff</code></li>
 * <li><code>org.netbeans.spi.diff.DiffControllerProvider</code> containing <code>
 *     org.netbeans.modules.diff.builtin.DefaultDiffControllerProvider</code></li>
 * <li><code>org.netbeans.spi.diff.DiffVisualizer</code> containing <code>
 *     org.netbeans.modules.diff.builtin.visualizer.TextDiffVisualizer
 *     org.netbeans.modules.diff.builtin.visualizer.editable.EditableDiffVisualizer</code></li>
 * </ul>
 *
 * @author thorsten
 * @version $Revision$, $Date$
 */
public class Testapplication extends javax.swing.JFrame {

    private static final long serialVersionUID = 1L;

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de/cismet/custom/visualdiff/Bundle", Locale.ENGLISH);
    
    //~ Static fields/initializers ---------------------------------------------
    private static final String MIMETYPE_HTML = "text/html";
    private static final String MIMETYPE_JAVA = "text/x-java";
    private static final String MIMETYPE_JSON = "text/javascript";
    private static final String MIMETYPE_TEXT = "text/plain";

    private static final String FILENAME1_HTML = "src\\test\\resources\\filestodiff\\html1.html";
    private static final String FILENAME2_HTML = "src\\test\\resources\\filestodiff\\html2.html";
    private static final String FILENAME1_JAVA = "src\\test\\resources\\filestodiff\\java1.java";
    private static final String FILENAME2_JAVA = "src\\test\\resources\\filestodiff\\java2.java";
    private static final String FILENAME1_JSON = "src\\test\\resources\\filestodiff\\json1.json";
    private static final String FILENAME2_JSON = "src\\test\\resources\\filestodiff\\json2.json";
    private static final String FILENAME1_TEXT = "src\\test\\resources\\filestodiff\\text1.txt";
    private static final String FILENAME2_TEXT = "src\\test\\resources\\filestodiff\\text2.txt";

    //~ Instance fields --------------------------------------------------------
    private DiffPanel pnlDiff;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDiffHTMLFiles;
    private javax.swing.JButton btnDiffJSONFiles;
    private javax.swing.JButton btnDiffJavaFiles;
    private javax.swing.JButton btnDiffTextFiles;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JSeparator separator;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new Testapplication object.
     */
    public Testapplication() {
        initComponents();
        final File file1 = new File(FILENAME1_TEXT);
        final File file2 = new File(FILENAME2_TEXT);
        try (FileReader reader1 = new FileReader(file1, StandardCharsets.UTF_8);
             FileReader reader2 = new FileReader(file2, StandardCharsets.UTF_8)) {
    
            pnlDiff = new DiffPanel(this);
            pnlDiff.setLeftAndRight(getLines(reader1),
                    MIMETYPE_TEXT,
                    file1.getName(),
                    getLines(reader2),
                    MIMETYPE_TEXT,
                    file2.getName());
            getContentPane().add(pnlDiff, BorderLayout.CENTER);
            setVisible(true);
        } catch (Exception ex) {
            log(ex);
        }
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * A helper method to read the content of a reader and convert it to a
     * String.
     *
     * @param reader The reader to read from.
     *
     * @return A string with all the content provided by reader.
     *
     * @throws IOException DOCUMENT ME!
     */
    private static String getLines(final Reader reader) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlControls = new javax.swing.JPanel();
        separator = new javax.swing.JSeparator();
        btnDiffHTMLFiles = new javax.swing.JButton();
        btnDiffJavaFiles = new javax.swing.JButton();
        btnDiffJSONFiles = new javax.swing.JButton();
        btnDiffTextFiles = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(BUNDLE.getString("Testapplication.title")); // NOI18N

        separator.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        separator.setPreferredSize(new java.awt.Dimension(2, 23));
        pnlControls.add(separator);

        btnDiffHTMLFiles.setText(BUNDLE.getString(
                "Testapplication.btnDiffHTMLFiles.text")); // NOI18N
        btnDiffHTMLFiles.addActionListener(this::btnDiffHTMLFilesActionPerformed);
        pnlControls.add(btnDiffHTMLFiles);

        btnDiffJavaFiles.setText(BUNDLE.getString(
                "Testapplication.btnDiffJavaFiles.text")); // NOI18N
        btnDiffJavaFiles.addActionListener(this::btnDiffJavaFilesActionPerformed);
        pnlControls.add(btnDiffJavaFiles);

        btnDiffJSONFiles.setText(BUNDLE.getString(
                "Testapplication.btnDiffJSONFiles.text")); // NOI18N
        btnDiffJSONFiles.addActionListener(this::btnDiffJSONFilesActionPerformed);
        pnlControls.add(btnDiffJSONFiles);

        btnDiffTextFiles.setText(BUNDLE.getString(
                "Testapplication.btnDiffTextFiles.text")); // NOI18N
        btnDiffTextFiles.addActionListener(this::btnDiffTextFilesActionPerformed);
        pnlControls.add(btnDiffTextFiles);

        getContentPane().add(pnlControls, java.awt.BorderLayout.SOUTH);

        final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 729) / 2, (screenSize.height - 706) / 2, 729, 706);
    } // </editor-fold>//GEN-END:initComponents


    /**
     * The action handler for the 'HTML' button. Diffs two HTML files.
     *
     * @param evt The event to handle.
     */
    private void btnDiffHTMLFilesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiffHTMLFilesActionPerformed
        final File file1 = new File(FILENAME1_HTML);
        final File file2 = new File(FILENAME2_HTML);
        try (FileReader reader1 = new FileReader(file1, StandardCharsets.UTF_8);
             FileReader reader2 = new FileReader(file2, StandardCharsets.UTF_8)) {
            pnlDiff.setLeftAndRight(getLines(reader1),
                    MIMETYPE_HTML,
                    file1.getName(),
                    getLines(reader2),
                    MIMETYPE_HTML,
                    file2.getName());
        } catch (IOException ex) {
            log(ex);
        }
    }//GEN-LAST:event_btnDiffHTMLFilesActionPerformed

    /**
     * The action handler for the 'Java' button. Diffs two Java files.
     *
     * @param evt The event to handle.
     */
    private void btnDiffJavaFilesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiffJavaFilesActionPerformed
        final File file1 = new File(FILENAME1_JAVA);
        final File file2 = new File(FILENAME2_JAVA);
        try (FileReader reader1 = new FileReader(file1, StandardCharsets.UTF_8);
             FileReader reader2 = new FileReader(file2, StandardCharsets.UTF_8)) {
            pnlDiff.setLeftAndRight(getLines(reader1),
                    MIMETYPE_JAVA,
                    file1.getName(),
                    getLines(reader2),
                    MIMETYPE_JAVA,
                    file2.getName());
        } catch (IOException ex) {
            log(ex);
        }
    }//GEN-LAST:event_btnDiffJavaFilesActionPerformed

    /**
     * The action handler for the 'JSON' button. Diffs two JSON files.
     *
     * @param evt The event to handle.
     */
    private void btnDiffJSONFilesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiffJSONFilesActionPerformed
        final File file1 = new File(FILENAME1_JSON);
        final File file2 = new File(FILENAME2_JSON);
        try (FileReader reader1 = new FileReader(file1, StandardCharsets.UTF_8);
             FileReader reader2 = new FileReader(file2, StandardCharsets.UTF_8)) {
            pnlDiff.setLeftAndRight(getLines(reader1),
                    MIMETYPE_JSON,
                    file1.getName(),
                    getLines(reader2),
                    MIMETYPE_JSON,
                    file2.getName());
        } catch (IOException ex) {
            log(ex);
        }
    }//GEN-LAST:event_btnDiffJSONFilesActionPerformed

    /**
     * The action handler for the 'Text' button. Diffs two text files.
     *
     * @param evt The event to handle.
     */
    private void btnDiffTextFilesActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDiffTextFilesActionPerformed
        final File file1 = new File(FILENAME1_TEXT);
        final File file2 = new File(FILENAME2_TEXT);
        try (FileReader reader1 = new FileReader(file1, StandardCharsets.UTF_8);
             FileReader reader2 = new FileReader(file2, StandardCharsets.UTF_8)) {
            pnlDiff.setLeftAndRight(getLines(reader1),
                    MIMETYPE_TEXT,
                    file1.getName(),
                    getLines(reader2),
                    MIMETYPE_TEXT,
                    file2.getName());
        } catch (IOException ex) {
            log(ex);
        }
    }//GEN-LAST:event_btnDiffTextFilesActionPerformed

    private static void log(Exception ex) {
        Exceptions.printStackTrace(ex);
    }

    /**
     * Run with VM arguments :
     * --add-opens java.base/java.net=ALL-UNNAMED 
     * --add-opens java.desktop/javax.swing.text=ALL-UNNAMED 
     * --add-opens java.prefs/java.util.prefs=ALL-UNNAMED
     * --add-opens java.desktop/javax.swing.plaf.basic=ALL-UNNAMED
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(Testapplication::new);
    }
}

