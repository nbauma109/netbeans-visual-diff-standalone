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
import org.netbeans.api.diff.StreamSource;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingWorker;

/**
 * This panel allows the embedding of Netbeans' diff component.
 *
 * @version  $Revision$, $Date$
 */
public class DiffPanel extends javax.swing.JPanel {

    //~ Static fields --------------------------------------------------------
    private static final long serialVersionUID = 1L;
    
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("de/cismet/custom/visualdiff/Bundle", Locale.ENGLISH);

    static {
        CaretRowHighlightOverrides.applyFromTheme();
    }

    //~ Instance fields --------------------------------------------------------
    protected DiffView view;
    protected FileToDiff left;
    protected FileToDiff right;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblWaitingImage;
    private javax.swing.JPanel pnlDiff;
    private javax.swing.JPanel pnlFilesMissing;
    private javax.swing.JPanel pnlWaiting;
    private javax.swing.JLabel txtFilesMissing;
    // End of variables declaration//GEN-END:variables

    private javax.swing.JButton btnNextDifference = new JButton("Next");
    private javax.swing.JButton btnPrevDifference = new JButton("Previous");
    
    private UnaryOperator<Image> imageOperator;
    
    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DiffPanel object.
     * @param frame 
     */
    public DiffPanel(JFrame frame) {
        initComponents(frame);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * The action handler for the 'next difference' button. Increases the
     * 'current difference' property of the view.
     *
     * @param evt The event to handle.
     */
    private void btnNextDifferenceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnNextDifferenceActionPerformed
        if (view != null) {
            if (view.canSetCurrentDifference() && view.getDifferenceCount() > 0) {
                view.setCurrentDifference((view.getCurrentDifference() + 1) % view.getDifferenceCount());
            }
        }
    }//GEN-LAST:event_btnNextDifferenceActionPerformed

    /**
     * The action handler for the 'previous difference' button. Decreases the
     * 'current difference' property of the view.
     *
     * @param evt The event to handle.
     */
    private void btnPrevDifferenceActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnPrevDifferenceActionPerformed
        if (view != null) {
            if (view.canSetCurrentDifference() && view.getDifferenceCount() > 0) {
                view.setCurrentDifference(((view.getCurrentDifference() == 0) ? (view.getDifferenceCount() - 1)
                        : (view.getCurrentDifference() - 1))
                        % view.getDifferenceCount());
            }
        }
    }//GEN-LAST:event_btnPrevDifferenceActionPerformed

    /**
     * Starts the retrieval and embedding of a new diff component in a SwingWorker. While the differences of both files
     * are computed, a "please wait" image is displayed. When the SwingWorker got a new diff component, it's embedded
     * and displayed.
     */
    public void update() {
        if ((left == null) || (right == null)) {
            showFilesMissing();
            System.out.println("At least one file is null. The diff component can't be created.");
            return;
        }

        showWaiting();

        new SwingWorker<DiffView, Void>() {

                @Override
                protected DiffView doInBackground() throws Exception {
                    final StreamSource sourceLeft = new MyStreamSource(left);
                    final StreamSource sourceRight = new MyStreamSource(right);

                    return Diff.getDefault().createDiff(sourceLeft, sourceRight);
                }

                @Override
                protected void done() {
                    try {
                        view = get();
                        pnlDiff.removeAll();
                        pnlDiff.add(view.getComponent(), BorderLayout.CENTER);
                        showDiff();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.execute();
    }

    /**
     * Starts a new Runnable which shows the "files are missing" screen.
     */
    public void showFilesMissing() {
        final Runnable waitRunnable = new ShowCardRunnable(this, "filesMissing"); // NOI18N

        if (EventQueue.isDispatchThread()) {
            waitRunnable.run();
        } else {
            EventQueue.invokeLater(waitRunnable);
        }
    }

    /**
     * Starts a new Runnable which shows the waiting screen.
     */
    public void showWaiting() {
        final Runnable waitRunnable = new ShowCardRunnable(this, "waiting"); // NOI18N

        if (EventQueue.isDispatchThread()) {
            waitRunnable.run();
        } else {
            EventQueue.invokeLater(waitRunnable);
        }
    }

    /**
     * Starts a new Runnable which shows the diff component.
     */
    public void showDiff() {
        final Runnable diffRunnable = new ShowCardRunnable(this, "diff"); // NOI18N

        if (EventQueue.isDispatchThread()) {
            diffRunnable.run();
        } else {
            EventQueue.invokeLater(diffRunnable);
        }
    }

    /**
     * Sets the information for the left part of the diff component. The DiffPanel will be updated.
     *
     * @param  content   The content of the file to be shown on the left side.
     * @param  mimetype  The mimetype of the file to be shown on the left side.
     * @param  title     The title of the file to be shown on the left side.
     */
    public void setLeft(final String content, final String mimetype, final String title) {
        this.left = createFileToDiff(content, mimetype, title);
        update();
    }

    /**
     * Sets the information for the right part of the diff component. The DiffPanel will be updated.
     *
     * @param  content   The content of the file to be shown on the right side.
     * @param  mimetype  The mimetype of the file to be shown on the right side.
     * @param  title     The title of the file to be shown on the right side.
     */
    public void setRight(final String content, final String mimetype, final String title) {
        this.right = createFileToDiff(content, mimetype, title);
        update();
    }

    /**
     * Sets the information for both parts of the diff component. The DiffPanel will be updated.
     *
     * @param  contentLeft    The content of the file to be shown on the left side.
     * @param  mimetypeLeft   The mimetype of the file to be shown on the left side.
     * @param  titleLeft      The title of the file to be shown on the left side.
     * @param  contentRight   The content of the file to be shown on the right side.
     * @param  mimetypeRight  The mimetype of the file to be shown on the right side.
     * @param  titleRight     The title of the file to be shown on the right side.
     */
    public void setLeftAndRight(final String contentLeft,
            final String mimetypeLeft,
            final String titleLeft,
            final String contentRight,
            final String mimetypeRight,
            final String titleRight) {
        this.left = createFileToDiff(contentLeft, mimetypeLeft, titleLeft);
        this.right = createFileToDiff(contentRight, mimetypeRight, titleRight);
        update();
    }

    /**
     * Gives access to Netbeans' diff component.
     *
     * @return  Netbeans' diff component.
     */
    public DiffView getDiffView() {
        return view;
    }

    /**
     * Helper method to verify if a given content is valid. Content is valid, if it contains at least one character.
     *
     * @param   content  The content to verify.
     *
     * @return  A flag indicating whether the given content is valid or not.
     */
    protected static boolean isValidContent(final String content) {
        return (content != null) && (content.trim().length() > 0);
    }

    /**
     * A factory method to create FileToDiff objects. Returns null if the given content is not valid.
     *
     * @param   content   The content of the FileToDiff.
     * @param   mimetype  The mimetype of the FileToDiff.
     * @param   title     The title of the FileToDiff.
     *
     * @return  A FileToDiff object wrapping the given parameters or null if the given content is invalid.
     */
    protected static FileToDiff createFileToDiff(final String content, final String mimetype, final String title) {
        if (isValidContent(content)) {
            return new FileToDiff(content, mimetype, title);
        }

        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     * @param frame 
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents(JFrame frame) {
        pnlFilesMissing = new javax.swing.JPanel();
        txtFilesMissing = new javax.swing.JLabel();
        pnlWaiting = new javax.swing.JPanel();
        lblWaitingImage = new javax.swing.JLabel();
        pnlDiff = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        pnlFilesMissing.setLayout(new java.awt.BorderLayout());

        txtFilesMissing.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txtFilesMissing.setText(BUNDLE.getString(
                "DiffPanel.txtFilesMissing.text")); // NOI18N
        pnlFilesMissing.add(txtFilesMissing, java.awt.BorderLayout.CENTER);

        add(pnlFilesMissing, "filesMissing");

        pnlWaiting.setLayout(new java.awt.BorderLayout());

        lblWaitingImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWaitingImage.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/custom/visualdiff/load.png"))); // NOI18N
        pnlWaiting.add(lblWaitingImage, java.awt.BorderLayout.CENTER);

        add(pnlWaiting, "waiting");

        pnlDiff.setLayout(new java.awt.BorderLayout());
        add(pnlDiff, "diff");
        
        btnNextDifference.addActionListener(this::btnNextDifferenceActionPerformed);
        btnPrevDifference.addActionListener(this::btnPrevDifferenceActionPerformed);
        
        addIconToButton("/de/cismet/custom/visualdiff/next_nav.png", btnNextDifference);
        addIconToButton("/de/cismet/custom/visualdiff/prev_nav.png", btnPrevDifference);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(btnPrevDifference);
        menuBar.add(btnNextDifference);
        frame.setJMenuBar(menuBar);
    } // </editor-fold>//GEN-END:initComponents

    private void addIconToButton(String iconPath, JButton button) {
        try {
            Image next = ImageIO.read(getClass().getResource(iconPath));
            if (imageOperator != null) {
                next = imageOperator.apply(next);
            }
            button.setIcon(new ImageIcon(next));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setImageOperator(UnaryOperator<Image> imageOperator) {
        this.imageOperator = imageOperator;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A helper class to switch between both cards of its parent's layout.
     *
     * @version  $Revision$, $Date$
     */
    protected class ShowCardRunnable implements Runnable {

        //~ Instance fields ----------------------------------------------------

        private Container parent;
        private String cardToShow;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ShowCardRunnable object.
         *
         * @param  parent      The parent of this helper. Should be a DiffPanel object.
         * @param  cardToShow  The card to display.
         */
        public ShowCardRunnable(final Container parent, final String cardToShow) {
            this.parent = parent;
            this.cardToShow = cardToShow;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (parent.getLayout() instanceof CardLayout) {
                ((CardLayout)parent.getLayout()).show(parent, cardToShow);
            }
        }
    }
}
