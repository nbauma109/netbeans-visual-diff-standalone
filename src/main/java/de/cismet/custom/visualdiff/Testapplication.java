/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.custom.visualdiff;

import org.netbeans.api.diff.Diff;
import org.netbeans.api.diff.DiffView;
import org.netbeans.api.diff.Difference;
import org.netbeans.api.diff.StreamSource;

import java.awt.BorderLayout;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * This is a test which demonstrates the use of the VisualDiff component.
 *
 * <p>In order to run this test application, make sure that the directory <code>\META-INF\services</code> in the target
 * directory or built jar file contains</p>
 *
 * <ul>
 *   <li><code>org.netbeans.api.diff.Diff</code> containing <code>
 *     org.netbeans.modules.diff.builtin.DefaultDiff</code></li>
 *   <li><code>org.netbeans.spi.diff.DiffControllerProvider</code> containing <code>
 *     org.netbeans.modules.diff.builtin.DefaultDiffControllerProvider</code></li>
 *   <li><code>org.netbeans.spi.diff.DiffVisualizer</code> containing <code>
 *     org.netbeans.modules.diff.builtin.visualizer.TextDiffVisualizer
 *     org.netbeans.modules.diff.builtin.visualizer.editable.EditableDiffVisualizer</code></li>
 * </ul>
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Testapplication extends javax.swing.JFrame {

    //~ Static fields/initializers ---------------------------------------------

    // private static final String MIMETYPE = "text/html";
    // private static final String MIMETYPE = "text/x-java";
    private static final String MIMETYPE = "text/javascript";
    // private static final String MIMETYPE = "text/plain";

    //~ Instance fields --------------------------------------------------------

    private DiffView view;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNextDifference;
    private javax.swing.JButton btnPrevDifference;
    private javax.swing.JPanel pnlControls;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Testapplication object.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public Testapplication() throws Exception {
        initComponents();

        // final String filename1 = "E:\\Projekte\\visualdiff\\filestodiff\\html1.html";
        // final String filename2 = "E:\\Projekte\\visualdiff\\filestodiff\\html2.html";
        // final String filename1 = "E:\\Projekte\\visualdiff\\filestodiff\\java1.xjava";
        // final String filename2 = "E:\\Projekte\\visualdiff\\filestodiff\\java2.xjava";
        final String filename1 = "E:\\Projekte\\visualdiff\\filestodiff\\json1.json";
        final String filename2 = "E:\\Projekte\\visualdiff\\filestodiff\\json2.json";
        // final String filename1 = "E:\\Projekte\\visualdiff\\filestodiff\\text1.txt"; final String filename2 =
        // "E:\\Projekte\\visualdiff\\filestodiff\\text2.txt";

        final File file1 = new File(filename1);
        final File file2 = new File(filename2);

        final StreamSource source1 = new StreamSource() {

                @Override
                public String getName() {
                    return filename1;
                }

                @Override
                public String getTitle() {
                    return file1.getName();
                }

                @Override
                public String getMIMEType() {
                    return MIMETYPE;
                }

                @Override
                public Reader createReader() throws IOException {
                    return new FileReader(file1);
                }

                @Override
                public Writer createWriter(final Difference[] conflicts) throws IOException {
                    return null;
                }
            };

        final StreamSource source2 = new StreamSource() {

                @Override
                public String getName() {
                    return filename2;
                }

                @Override
                public String getTitle() {
                    return file2.getName();
                }

                @Override
                public String getMIMEType() {
                    return MIMETYPE;
                }

                @Override
                public Reader createReader() throws IOException {
                    return new FileReader(file2);
                }

                @Override
                public Writer createWriter(final Difference[] conflicts) throws IOException {
                    return null;
                }
            };

        view = Diff.getDefault().createDiff(source1, source2);
        getContentPane().add(view.getComponent(), BorderLayout.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        pnlControls = new javax.swing.JPanel();
        btnPrevDifference = new javax.swing.JButton();
        btnNextDifference = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(Testapplication.class, "Testapplication.title")); // NOI18N

        btnPrevDifference.setText(org.openide.util.NbBundle.getMessage(
                Testapplication.class,
                "Testapplication.btnPrevDifference.text")); // NOI18N
        btnPrevDifference.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPrevDifferenceActionPerformed(evt);
                }
            });
        pnlControls.add(btnPrevDifference);

        btnNextDifference.setText(org.openide.util.NbBundle.getMessage(
                Testapplication.class,
                "Testapplication.btnNextDifference.text")); // NOI18N
        btnNextDifference.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNextDifferenceActionPerformed(evt);
                }
            });
        pnlControls.add(btnNextDifference);

        getContentPane().add(pnlControls, java.awt.BorderLayout.SOUTH);

        final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - 729) / 2, (screenSize.height - 706) / 2, 729, 706);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * The action handler for the 'next difference' button. Increases the 'current difference' property of the view.
     *
     * @param  evt  The event to handle.
     */
    private void btnNextDifferenceActionPerformed(final java.awt.event.ActionEvent evt) {      //GEN-FIRST:event_btnNextDifferenceActionPerformed
        view.setCurrentDifference((view.getCurrentDifference() + 1) % view.getDifferenceCount());
    }                                                                                          //GEN-LAST:event_btnNextDifferenceActionPerformed

    /**
     * The action handler for the 'previous difference' button. Decreases the 'current difference' property of the view.
     *
     * @param  evt  The event to handle.
     */
    private void btnPrevDifferenceActionPerformed(final java.awt.event.ActionEvent evt) {          //GEN-FIRST:event_btnPrevDifferenceActionPerformed
        view.setCurrentDifference(((view.getCurrentDifference() == 0) ? (view.getDifferenceCount() - 1)
                                                                      : (view.getCurrentDifference() - 1))
                    % view.getDifferenceCount());
    }                                                                                              //GEN-LAST:event_btnPrevDifferenceActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        new Testapplication().setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
    }
}