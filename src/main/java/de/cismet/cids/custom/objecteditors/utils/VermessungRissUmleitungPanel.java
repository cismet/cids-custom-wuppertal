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
package de.cismet.cids.custom.objecteditors.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import java.net.URL;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.VermessungRissUmleitungPanel.MODE;
import de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastUmleitungPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungRissEditor;
import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissPictureFinder;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.netutil.Proxy;

import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungRissUmleitungPanel extends javax.swing.JPanel implements DocumentListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungRissUmleitungPanel.class);
    private static final String PLATZHALTER_DOC_NAME = "000000-00";
    private static final String VERMESSUNG_DIRECTORY;
    private static final String GRENZNIEDERSCHRIFT_DIRECTORY;
    private static final String WEB_DAV_USER;
    private static final String WEB_DAV_PASSWORD;
    private static final String VERMESSUNG_PREFIX = "VR_";
    private static final String GRENZNIEDERSCHRIFT_PREFIX = "GR_";

    static {
        final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
        String pass = bundle.getString("password");

        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
            pass = PasswordEncrypter.decryptString(pass);
        }

        WEB_DAV_PASSWORD = pass;
        WEB_DAV_USER = bundle.getString("user");
        VERMESSUNG_DIRECTORY = bundle.getString("url_vermessungsrisse");
        GRENZNIEDERSCHRIFT_DIRECTORY = bundle.getString("url_grenzniederschriften");
    }

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum MODE {

        //~ Enum constants -----------------------------------------------------

        VERMESSUNGSRISS, GRENZNIEDERSCHRIFT
    }

    //~ Instance fields --------------------------------------------------------

    private MODE mode;
    private VermessungRissEditor editor;
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
    public VermessungRissUmleitungPanel() {
        this(MODE.VERMESSUNGSRISS, null);
    }

    /**
     * Creates a new Alb_baulastUmleitungPanel object.
     *
     * @param  m       DOCUMENT ME!
     * @param  editor  DOCUMENT ME!
     */
    public VermessungRissUmleitungPanel(final MODE m, final VermessungRissEditor editor) {
        this.mode = m;
        this.editor = editor;
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
        final String text = (this.mode == mode.VERMESSUNGSRISS) ? VERMESSUNG_PREFIX : GRENZNIEDERSCHRIFT_PREFIX;
        lblMode.setText(text);
    }

    /**
     * DOCUMENT ME!
     */
    private void showError() {
        editor.handleNoDocumentFound();
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
                            editor.successAlert();
                            tfName.getDocument().removeDocumentListener(VermessungRissUmleitungPanel.this);
                            final String rawUrl = file.toString();
                            final int startPos = rawUrl.indexOf("_") + 1;
                            final int endPos = (rawUrl.lastIndexOf("_") != (startPos - 1)) ? rawUrl.lastIndexOf("_")
                                                                                           : rawUrl.lastIndexOf(".");
                            tfName.setText(rawUrl.substring(startPos, endPos));
                            tfName.getDocument().addDocumentListener(VermessungRissUmleitungPanel.this);
                            editor.reloadPictureFromUrl(file);
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
                    final List<URL> res;
                    final String[] splittedInput = input.split("-");
                    if (splittedInput.length != 4) {
                        return null;
                    }
                    final String schluessel = splittedInput[0];
                    final Integer gemarkung = Integer.parseInt(splittedInput[1]);
                    final String flur = StringUtils.leftPad(splittedInput[2], 3, '0');
                    final String blatt = StringUtils.leftPad(splittedInput[3], 8, '0');
                    // check if we need to format the flur and the blatt
                    if (mode == MODE.VERMESSUNGSRISS) {
                        res = VermessungsrissPictureFinder.findVermessungsrissPicture(
                                schluessel,
                                gemarkung,
                                flur,
                                blatt);
                    } else {
                        res = VermessungsrissPictureFinder.findGrenzniederschriftPicture(
                                schluessel,
                                gemarkung,
                                flur,
                                blatt);
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
     * @param   vermessungrissNummer  baulastnr DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isNummerConsistent(final String vermessungrissNummer) {
        return vermessungrissNummer.matches("(\\d{3})-(\\d{4})-(\\d{1,3})-(\\d{1,8})");
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
                                    VermessungRissUmleitungPanel.this),
                                ei);
                            editor.handleEscapePressed();
                            if (escapeText != null) {
                                tfName.setText(escapeText);
                            } else {
                                tfName.setText("");
                            }
                        } else {
                            editor.handleUmleitungDeleted();
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
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                VermessungRissUmleitungPanel.this),
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
                    final String filename = createFilename();
                    final File f = File.createTempFile(filename, ".txt");
                    final FileWriter fw = new FileWriter(f);
                    final BufferedWriter bfw = new BufferedWriter(fw);
                    bfw.write(getLinkDocument(), 0, getLinkDocument().length());
                    bfw.flush();
                    bfw.close();
                    webDavHelper.uploadFileToWebDAV(
                        filename
                                + ".txt",
                        f,
                        createDirName(),
                        editor);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        editor.handleUmleitungCreated(lastCheckedURL);
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
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                VermessungRissUmleitungPanel.this),
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
        final CidsBean vermessungBean = editor.getCidsBean();
        final CidsBean gemarkungBean = (CidsBean)vermessungBean.getProperty("gemarkung");
        Integer gemarkung = 0;
        if (gemarkungBean != null) {
            gemarkung = (Integer)gemarkungBean.getProperty("id");
        }
        return VermessungsrissPictureFinder.getFolder(mode == MODE.GRENZNIEDERSCHRIFT, gemarkung) + "/";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String createFilename() {
        final CidsBean vermessungBean = editor.getCidsBean();
        final String schluessel = vermessungBean.getProperty("schluessel").toString();
        final CidsBean gemarkungBean = (CidsBean)vermessungBean.getProperty("gemarkung");
        Integer gemarkung = 0;
        if (gemarkungBean != null) {
            gemarkung = (Integer)gemarkungBean.getProperty("id");
        }
        final String flur = vermessungBean.getProperty("flur").toString();
        final String blatt = vermessungBean.getProperty("blatt").toString();

        return VermessungsrissPictureFinder.getObjectFilename(
                false,
                mode
                        == MODE.GRENZNIEDERSCHRIFT,
                schluessel,
                gemarkung,
                flur,
                blatt);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLinkDocument() {
        return tfName.getText().trim();
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
        lblMode = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        btnPlatzhalter = new javax.swing.JButton();
        pnlControls = new javax.swing.JPanel();
        pnlEmpty = new javax.swing.JPanel();
        pnlOkButton = new javax.swing.JPanel();
        btnCreateDocument = new javax.swing.JButton();
        pnlBusyLabel = new javax.swing.JPanel();
        jXBusyLabel1 = new JXBusyLabel(new Dimension(16, 16));
        pnlError = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDateiname,
            org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.lblDateiname.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        add(lblDateiname, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblMode,
            org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.lblMode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(lblMode, gridBagConstraints);

        tfName.setText(org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.tfName.text")); // NOI18N
        tfName.setMinimumSize(new java.awt.Dimension(180, 27));
        tfName.setPreferredSize(new java.awt.Dimension(180, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(tfName, gridBagConstraints);

        btnPlatzhalter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/icon-file.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPlatzhalter,
            org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.btnPlatzhalter.text"));                            // NOI18N
        btnPlatzhalter.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.btnPlatzhalter.toolTipText"));                     // NOI18N
        btnPlatzhalter.setBorderPainted(false);
        btnPlatzhalter.setContentAreaFilled(false);
        btnPlatzhalter.setFocusPainted(false);
        btnPlatzhalter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPlatzhalterActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(btnPlatzhalter, gridBagConstraints);

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
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.btnCreateDocument.text"));                                   // NOI18N
        btnCreateDocument.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.btnCreateDocument.toolTipText"));                            // NOI18N
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
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.jXBusyLabel1.text")); // NOI18N
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
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.jLabel1.text"));                                           // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungRissUmleitungPanel.class,
                "VermessungRissUmleitungPanel.jLabel1.toolTipText"));                                    // NOI18N
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
    private void btnPlatzhalterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPlatzhalterActionPerformed
        tfName.getDocument().removeDocumentListener(this);
        tfName.setText(PLATZHALTER_DOC_NAME);
        tfName.getDocument().addDocumentListener(this);
        createLinkFile();
    }                                                                                  //GEN-LAST:event_btnPlatzhalterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println("Testing WebDav-Acces for Vermessungsrisse");
        try {
            DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "kif");
            final WebDavHelper webDavHelper = new WebDavHelper(Proxy.fromPreferences(),
                    WEB_DAV_USER,
                    WEB_DAV_PASSWORD,
                    true);
            final InputStream is = webDavHelper.getFileFromWebDAV(
                    "3001/VR_505-3001-001-00000001.jpg",
                    VERMESSUNG_DIRECTORY);

            final String filename = "cismet_test_file";
            final File f = File.createTempFile(filename, ".txt");
            final FileWriter fw = new FileWriter(f);
            final BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write("test", 0, "test".length());
            bfw.flush();
            bfw.close();
            webDavHelper.uploadFileToWebDAV(
                filename
                        + ".txt",
                f,
                VERMESSUNG_DIRECTORY
                        + "3001/",
                null);

            webDavHelper.deleteFileFromWebDAV("cismet_test_file.txt", VERMESSUNG_DIRECTORY + "3001/");
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
    }
}
