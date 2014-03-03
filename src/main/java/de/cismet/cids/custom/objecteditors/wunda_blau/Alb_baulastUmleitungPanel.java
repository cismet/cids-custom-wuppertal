/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;
import de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder;

import de.cismet.netutil.Proxy;

import de.cismet.tools.PasswordEncrypter;

import static de.cismet.cids.custom.objecteditors.wunda_blau.Alb_picturePanel.BLATTNUMMER_PROPERTY;
import static de.cismet.cids.custom.objecteditors.wunda_blau.Alb_picturePanel.LFDNUMMER_PROPERTY;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class Alb_baulastUmleitungPanel extends javax.swing.JPanel implements DocumentListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(Alb_baulastUmleitungPanel.class);
    private static final String PLATZHALTER_DOC_NAME = "000000-00";
    private static final String LAGEPLAN_ENDUNG = "p";
    private static final String TEXTBLATT_ENDUNG = "b";
    private static final String BAULASTEN_DIRECTORY;
    private static final String WEB_DAV_USER;
    private static final String WEB_DAV_PASSWORD;

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
        String pass = bundle.getString("password");

        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
            pass = PasswordEncrypter.decryptString(pass);
        }

        WEB_DAV_PASSWORD = pass;
        WEB_DAV_USER = bundle.getString("user");
        BAULASTEN_DIRECTORY = bundle.getString("url_baulasten");
    }

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum MODE {

        //~ Enum constants -----------------------------------------------------

        TEXTBLATT, LAGEPLAN
    }

    //~ Instance fields --------------------------------------------------------

    private MODE mode;
    private Alb_picturePanel picturePan;
    private final Timer t = new Timer(1000, new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    t.stop();
                    if ((getLinkDocument() == null) || getLinkDocument().isEmpty()) {
                        deleteFile();
                    } else {
                        checkIfLinkDocumentExists();
                    }
                }
            });
    private long lastChange = 0;
    private WebDavHelper webDavHelper;
    private boolean firstDocumentChange = true;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnPlatzhalter;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel lblDateiname;
    private javax.swing.JLabel lblMode;
    private javax.swing.JTextField tfName;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Alb_baulastUmleitungPanel.
     */
    public Alb_baulastUmleitungPanel() {
        this(MODE.TEXTBLATT, null);
    }

    /**
     * Creates a new Alb_baulastUmleitungPanel object.
     *
     * @param  m             DOCUMENT ME!
     * @param  picturePanel  DOCUMENT ME!
     */
    public Alb_baulastUmleitungPanel(final MODE m, final Alb_picturePanel picturePanel) {
        this.mode = m;
        this.picturePan = picturePanel;
        initComponents();
        setModeLabeltext();
        tfName.getDocument().addDocumentListener(this);
        webDavHelper = new WebDavHelper(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD, true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    void setLinkDocumentText(final String text) {
        tfName.getDocument().removeDocumentListener(this);
        tfName.setText(text);
        tfName.getDocument().addDocumentListener(this);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  m  DOCUMENT ME!
     */
    void setMode(final MODE m) {
        this.mode = m;
        setModeLabeltext();
    }

    /**
     * DOCUMENT ME!
     */
    private void setModeLabeltext() {
        final String text = (this.mode == MODE.LAGEPLAN) ? LAGEPLAN_ENDUNG : TEXTBLATT_ENDUNG;
        lblMode.setText(text);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkIfLinkDocumentExists() {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected void done() {
                    try {
                        final boolean fileExists = get();
                        if (fileExists) {
                            createLinkFile();
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Worker Thread interrupter", ex);
                    } catch (ExecutionException ex) {
                        LOG.error("Execution error", ex);
                    }
                }

                @Override
                protected Boolean doInBackground() throws Exception {
                    final String input = getLinkDocument();
                    final String blattnummer = input.contains("-") ? input.substring(0, input.indexOf("-"))
                                                                   : input.substring(0, 7);
                    final String lfdNummer = input.substring(input.length() - 2, input.length());
                    final List<URL> res;
                    if (mode == MODE.LAGEPLAN) {
                        res = BaulastenPictureFinder.findPlanPicture(blattnummer, lfdNummer);
                    } else {
                        res = BaulastenPictureFinder.findPlanPicture(blattnummer, lfdNummer);
                    }
                    return (res != null) && !res.isEmpty();
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final String filename = createFilename();
                    final File f = File.createTempFile(filename, ".txt");
                    webDavHelper.deleteFileFromWebDAV(
                        filename
                                + ".txt",
                        createDirName());
                    return null;
                }

                @Override
                protected void done() {
                    picturePan.successAlert();
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void createLinkFile() {
        final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    final String filename = createFilename();
                    final File f = File.createTempFile(filename, ".txt");
                    final FileWriter fw = new FileWriter(f);
                    final BufferedWriter bfw = new BufferedWriter(fw);
                    final String linkDocument = getLinkDocument()
                                + ((Alb_baulastUmleitungPanel.this.mode == MODE.LAGEPLAN) ? LAGEPLAN_ENDUNG
                                                                                          : TEXTBLATT_ENDUNG);
                    bfw.write(linkDocument, 0, linkDocument.length());
                    bfw.flush();
                    bfw.close();
                    webDavHelper.uploadFileToWebDAV(
                        filename
                                + ".txt",
                        f,
                        createDirName(),
                        picturePan);
                    return null;
                }

                @Override
                protected void done() {
                    picturePan.successAlert();
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createDirName() {
        final String blattnummer = (String)picturePan.getCidsBean().getProperty(BLATTNUMMER_PROPERTY);
        return BAULASTEN_DIRECTORY
                    + BaulastenPictureFinder.getFolderWihoutPath(
                        BaulastenPictureFinder.getBlattnummer(blattnummer))
                    + "/";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createFilename() {
        final String blattnummer = (String)picturePan.getCidsBean().getProperty(BLATTNUMMER_PROPERTY);
        final String lfdNummer = (String)picturePan.getCidsBean().getProperty(LFDNUMMER_PROPERTY);
        final String filenameSuffix = (this.mode == Alb_baulastUmleitungPanel.MODE.LAGEPLAN) ? "p" : "b";
        return BaulastenPictureFinder.getObjectFilenameWithoutFolder(blattnummer, lfdNummer)
                    + filenameSuffix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLinkDocument() {
        return tfName.getText();
    }

    /**
     * DOCUMENT ME!
     */
    public void reset() {
        firstDocumentChange = true;
        tfName.getDocument().removeDocumentListener(this);
        tfName.setText("");
        tfName.getDocument().addDocumentListener(this);
    }
    /**
     * DOCUMENT ME!
     */
    private void handleDocumentChangedEvent() {
        final long curr = System.currentTimeMillis();
        final long diff = curr - lastChange;
        lastChange = curr;
        if (firstDocumentChange) {
            firstDocumentChange = false;
            t.start();
        }
        if (diff < 800) {
            t.restart();
        }
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        handleDocumentChangedEvent();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        handleDocumentChangedEvent();
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        handleDocumentChangedEvent();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  c  DOCUMENT ME!
     */
    public void setTextColor(final Color c) {
        lblDateiname.setForeground(c);
        lblMode.setForeground(c);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblDateiname = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        if(mode == MODE.LAGEPLAN){
            btnPlatzhalter = new javax.swing.JButton();
        }
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        lblMode = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lblDateiname, org.openide.util.NbBundle.getMessage(Alb_baulastUmleitungPanel.class, "Alb_baulastUmleitungPanel.lblDateiname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 5);
        add(lblDateiname, gridBagConstraints);
        lblDateiname.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(Alb_baulastUmleitungPanel.class, "Alb_baulastUmleitungPanel.lblDateiname.AccessibleContext.accessibleName")); // NOI18N

        tfName.setText(org.openide.util.NbBundle.getMessage(Alb_baulastUmleitungPanel.class, "Alb_baulastUmleitungPanel.tfName.text")); // NOI18N
        tfName.setMinimumSize(new java.awt.Dimension(150, 27));
        tfName.setPreferredSize(new java.awt.Dimension(150, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 0);
        add(tfName, gridBagConstraints);

        if(mode == MODE.LAGEPLAN){
            org.openide.awt.Mnemonics.setLocalizedText(btnPlatzhalter, org.openide.util.NbBundle.getMessage(Alb_baulastUmleitungPanel.class, "Alb_baulastUmleitungPanel.btnPlatzhalter.text")); // NOI18N
            btnPlatzhalter.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnPlatzhalterActionPerformed(evt);
                }
            });
        }
        if(mode == MODE.LAGEPLAN){
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
            add(btnPlatzhalter, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblMode, org.openide.util.NbBundle.getMessage(Alb_baulastUmleitungPanel.class, "Alb_baulastUmleitungPanel.lblMode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        add(lblMode, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(filler3, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPlatzhalterActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlatzhalterActionPerformed
        tfName.setText(PLATZHALTER_DOC_NAME);
        createLinkFile();
    }//GEN-LAST:event_btnPlatzhalterActionPerformed
}
