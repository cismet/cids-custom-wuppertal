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

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;
import de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder;

import de.cismet.netutil.Proxy;

import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.StaticSwingTools;

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
        String user = "";
        String pass = "";
        String dir = "";

        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
            pass = bundle.getString("password");
            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }
            user = bundle.getString("user");
            dir = bundle.getString("url_baulasten");
        } catch (final Exception ex) {
            LOG.warn("could not load props from bundle", ex);
        }

        WEB_DAV_PASSWORD = pass;
        WEB_DAV_USER = user;
        BAULASTEN_DIRECTORY = dir;
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
                    final CardLayout cl = (CardLayout)pnlControls.getLayout();
                    cl.show(pnlControls, "card2");
                    jXBusyLabel1.setBusy(true);
                    if ((getLinkDocument() != null) && !getLinkDocument().isEmpty()) {
                        checkIfLinkDocumentExists();
                    } else {
                        cl.show(pnlControls, "card3");
                    }
                }
            });
    private long lastChange = 0;
    private WebDavHelper webDavHelper;
    private boolean firstDocumentChange = true;
    private URL lastCheckedURL;
    private String escapeText;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateDocument;
    private javax.swing.JButton btnPlatzhalter;
    private javax.swing.JLabel jLabel1;
    private org.jdesktop.swingx.JXBusyLabel jXBusyLabel1;
    private javax.swing.JLabel lblDateiname;
    private javax.swing.JLabel lblMode;
    private javax.swing.JPanel pnlBusyLabel;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlEmpty;
    private javax.swing.JPanel pnlError;
    private javax.swing.JPanel pnlOkButton;
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
        jXBusyLabel1.setSize(16, 16);
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
    public void setLinkDocumentText(final String text) {
        tfName.getDocument().removeDocumentListener(this);
        escapeText = text;
        tfName.setText(text);
        final CardLayout cl = (CardLayout)pnlControls.getLayout();
        cl.show(pnlControls, "card1");
        tfName.getDocument().addDocumentListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  m  DOCUMENT ME!
     */
    public void setMode(final MODE m) {
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
    private void showError() {
        picturePan.handleNoDocumentFound();
        final CardLayout cl = (CardLayout)pnlControls.getLayout();
        cl.show(pnlControls, "card4");
    }

    /**
     * DOCUMENT ME!
     */
    private void checkIfLinkDocumentExists() {
        final SwingWorker<URL, Void> worker = new SwingWorker<URL, Void>() {

                @Override
                protected void done() {
                    try {
                        final URL file = get();
                        jXBusyLabel1.setBusy(false);
                        if (file != null) {
                            picturePan.successAlert();
                            picturePan.reloadPictureFromUrl(file);
                            lastCheckedURL = file;
                            final CardLayout cl = (CardLayout)pnlControls.getLayout();
                            cl.show(pnlControls, "card3");
                        } else {
                            showError();
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Worker Thread interrupter", ex);
                        showError();
                    } catch (ExecutionException ex) {
                        LOG.error("Execution error", ex);
                        showError();
                    }
                }

                @Override
                protected URL doInBackground() throws Exception {
                    final String input = getLinkDocument();
                    if (!isNummerConsistent(input)) {
                        return null;
                    }
                    final String blattnummer = input.contains("-") ? input.substring(0, input.indexOf("-"))
                                                                   : input.substring(0, 7);
                    final String lfdNummer = input.substring(input.length() - 2, input.length());
                    final List<URL> res;
                    if (mode == MODE.LAGEPLAN) {
                        res = BaulastenPictureFinder.findPlanPicture(blattnummer, lfdNummer);
                    } else {
                        res = BaulastenPictureFinder.findTextblattPicture(blattnummer, lfdNummer);
                    }
                    if ((res == null) || res.isEmpty()) {
                        return null;
                    }
                    return res.get(0);
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastnr  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isNummerConsistent(final String baulastnr) {
        if (!baulastnr.matches("\\d{6}-\\d{2}") && !baulastnr.matches("\\d{6}[a-zA-Z]\\d{2}")) {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    final String filename = createFilename();
                    final File f = File.createTempFile(filename, ".txt");
                    return webDavHelper.deleteFileFromWebDAV(
                            filename
                                    + ".txt",
                            createDirName());
                }

                @Override
                protected void done() {
                    try {
                        if (!get()) {
                            final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                    org.openide.util.NbBundle.getMessage(
                                        Alb_baulastUmleitungPanel.class,
                                        "Alb_baulastUmleitungPanel.errorDialog.title"),
                                    org.openide.util.NbBundle.getMessage(
                                        Alb_baulastUmleitungPanel.class,
                                        "Alb_baulastUmleitungPanel.errorDialog.delete.message"),
                                    null,
                                    null,
                                    null,
                                    Level.ALL,
                                    null);
                            JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                    Alb_baulastUmleitungPanel.this),
                                ei);
                            picturePan.handleEscapePressed();
                            if (escapeText != null) {
                                tfName.setText(escapeText);
                            } else {
                                tfName.setText("");
                            }
                        } else {
                            picturePan.handleUmleitungDeleted();
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Deleting link file worker was interrupted", ex);
                    } catch (ExecutionException ex) {
                        LOG.error("Error in deleting link file worker", ex);
                        final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                org.openide.util.NbBundle.getMessage(
                                    Alb_baulastUmleitungPanel.class,
                                    "Alb_baulastUmleitungPanel.errorDialog.title"),
                                org.openide.util.NbBundle.getMessage(
                                    Alb_baulastUmleitungPanel.class,
                                    "Alb_baulastUmleitungPanel.errorDialog.delete.message"),
                                ex.getMessage(),
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(Alb_baulastUmleitungPanel.this),
                            ei);
                    }
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
                    final String filename = createFilename().toLowerCase();
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
                    try {
                        get();
                        picturePan.handleUmleitungCreated(lastCheckedURL);
                    } catch (InterruptedException ex) {
                        LOG.error("Create Link File Worker was interrupted.", ex);
                    } catch (ExecutionException ex) {
                        LOG.error("Error in Create Link File worker", ex);
                        final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                org.openide.util.NbBundle.getMessage(
                                    Alb_baulastUmleitungPanel.class,
                                    "Alb_baulastUmleitungPanel.errorDialog.title"),
                                org.openide.util.NbBundle.getMessage(
                                    Alb_baulastUmleitungPanel.class,
                                    "Alb_baulastUmleitungPanel.errorDialog.create.message"),
                                ex.getMessage(),
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(Alb_baulastUmleitungPanel.this),
                            ei);
                    }
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
        return tfName.getText().toLowerCase();
    }

    /**
     * DOCUMENT ME!
     */
    public void reset() {
        firstDocumentChange = true;
        escapeText = null;
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
        if (mode == MODE.LAGEPLAN) {
            btnPlatzhalter = new javax.swing.JButton();
        }
        lblMode = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        pnlEmpty = new javax.swing.JPanel();
        pnlOkButton = new javax.swing.JPanel();
        btnCreateDocument = new javax.swing.JButton();
        pnlBusyLabel = new javax.swing.JPanel();
        jXBusyLabel1 = new JXBusyLabel(new Dimension(16, 16));
        pnlError = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(2147483647, 50));
        setMinimumSize(new java.awt.Dimension(383, 50));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(802, 50));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDateiname,
            org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.lblDateiname.text"));                                     // NOI18N
        lblDateiname.setAutoscrolls(true);
        lblDateiname.setPreferredSize(new java.awt.Dimension(580, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        add(lblDateiname, gridBagConstraints);
        lblDateiname.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        Alb_baulastUmleitungPanel.class,
                        "Alb_baulastUmleitungPanel.lblDateiname.AccessibleContext.accessibleName")); // NOI18N

        tfName.setBackground(new Color(255, 255, 255));
        tfName.setText(org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.tfName.text")); // NOI18N
        tfName.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        tfName.setMinimumSize(new java.awt.Dimension(82, 23));
        tfName.setPreferredSize(new java.awt.Dimension(82, 23));
        tfName.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    tfNameKeyPressed(evt);
                }
                @Override
                public void keyReleased(final java.awt.event.KeyEvent evt) {
                    tfNameKeyReleased(evt);
                }
                @Override
                public void keyTyped(final java.awt.event.KeyEvent evt) {
                    tfNameKeyTyped(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(tfName, gridBagConstraints);

        if (mode == MODE.LAGEPLAN) {
            btnPlatzhalter.setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/icon-file.png"))); // NOI18N
            org.openide.awt.Mnemonics.setLocalizedText(
                btnPlatzhalter,
                org.openide.util.NbBundle.getMessage(
                    Alb_baulastUmleitungPanel.class,
                    "Alb_baulastUmleitungPanel.btnPlatzhalter.text"));                               // NOI18N
            btnPlatzhalter.setToolTipText(org.openide.util.NbBundle.getMessage(
                    Alb_baulastUmleitungPanel.class,
                    "Alb_baulastUmleitungPanel.btnPlatzhalter.toolTipText"));                        // NOI18N
            btnPlatzhalter.setBorderPainted(false);
            btnPlatzhalter.setContentAreaFilled(false);
            btnPlatzhalter.setFocusPainted(false);
            btnPlatzhalter.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        btnPlatzhalterActionPerformed(evt);
                    }
                });
        }
        if (mode == MODE.LAGEPLAN) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            add(btnPlatzhalter, gridBagConstraints);
        }

        org.openide.awt.Mnemonics.setLocalizedText(
            lblMode,
            org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.lblMode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(lblMode, gridBagConstraints);

        pnlControls.setOpaque(false);
        pnlControls.setLayout(new java.awt.CardLayout());

        pnlEmpty.setOpaque(false);
        pnlControls.add(pnlEmpty, "card1");

        pnlOkButton.setOpaque(false);
        pnlOkButton.setPreferredSize(new java.awt.Dimension(32, 32));
        pnlOkButton.setLayout(new java.awt.GridBagLayout());

        btnCreateDocument.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/glyphicons_206_ok_2.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnCreateDocument,
            org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.btnCreateDocument.text"));                                      // NOI18N
        btnCreateDocument.setToolTipText(org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.btnCreateDocument.toolTipText"));                               // NOI18N
        btnCreateDocument.setBorderPainted(false);
        btnCreateDocument.setContentAreaFilled(false);
        btnCreateDocument.setFocusPainted(false);
        btnCreateDocument.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCreateDocumentActionPerformed(evt);
                }
            });
        pnlOkButton.add(btnCreateDocument, new java.awt.GridBagConstraints());

        pnlControls.add(pnlOkButton, "card3");

        pnlBusyLabel.setOpaque(false);
        pnlBusyLabel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jXBusyLabel1,
            org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.jXBusyLabel1.text")); // NOI18N
        jXBusyLabel1.setFocusable(false);
        jXBusyLabel1.setMaximumSize(new java.awt.Dimension(16, 16));
        jXBusyLabel1.setMinimumSize(new java.awt.Dimension(16, 16));
        pnlBusyLabel.add(jXBusyLabel1, new java.awt.GridBagConstraints());

        pnlControls.add(pnlBusyLabel, "card2");

        pnlError.setFocusable(false);
        pnlError.setOpaque(false);
        pnlError.setLayout(new java.awt.GridBagLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/icon-warning-sign.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.jLabel1.text"));                                              // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(
                Alb_baulastUmleitungPanel.class,
                "Alb_baulastUmleitungPanel.jLabel1.toolTipText"));                                       // NOI18N
        jLabel1.setFocusable(false);
        pnlError.add(jLabel1, new java.awt.GridBagConstraints());

        pnlControls.add(pnlError, "card4");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        add(pnlControls, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPlatzhalterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPlatzhalterActionPerformed
//        tfName.getDocument().removeDocumentListener(this);
        tfName.setText(PLATZHALTER_DOC_NAME);
//        tfName.getDocument().addDocumentListener(this);
//        createLinkFile();
    } //GEN-LAST:event_btnPlatzhalterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreateDocumentActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCreateDocumentActionPerformed
        if ((getLinkDocument() == null) || getLinkDocument().isEmpty()) {
            deleteFile();
        } else {
            createLinkFile();
        }
    }                                                                                     //GEN-LAST:event_btnCreateDocumentActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tfNameKeyTyped(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_tfNameKeyTyped
    }                                                                //GEN-LAST:event_tfNameKeyTyped

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tfNameKeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_tfNameKeyPressed
    }                                                                  //GEN-LAST:event_tfNameKeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tfNameKeyReleased(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_tfNameKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            firstDocumentChange = true;
            picturePan.handleEscapePressed();
            if (escapeText != null) {
                tfName.setText(escapeText);
            } else {
                tfName.setText("");
            }
        }
    }                                                                   //GEN-LAST:event_tfNameKeyReleased
}
