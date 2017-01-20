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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

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

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objecteditors.utils.VermessungUmleitungPanel.MODE;
import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungRissEditor;
import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissWebAccessPictureFinder;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.netutil.Proxy;

import de.cismet.tools.PasswordEncrypter;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungUmleitungPanel extends javax.swing.JPanel implements DocumentListener, CidsBeanDropListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungUmleitungPanel.class);
    public static final String PLATZHALTER_PREFIX = "platzhalter";
    private static final String SEP = "/";
    private static String VERMESSUNG_DIRECTORY;
    private static String GRENZNIEDERSCHRIFT_DIRECTORY;
    private static String WEB_DAV_USER;
    private static String WEB_DAV_PASSWORD;
    private static boolean initError = false;
//    private static final String VERMESSUNG_PREFIX = "VR_";
    private static String GRENZNIEDERSCHRIFT_PREFIX = "GN_";

    static {
        try {
            final ResourceBundle bundle = ResourceBundle.getBundle("WebDav");
            String pass = bundle.getString("password");

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }

            WEB_DAV_PASSWORD = pass;
            WEB_DAV_USER = bundle.getString("user");
            VERMESSUNG_DIRECTORY = bundle.getString("url_vermessungsrisse");
            GRENZNIEDERSCHRIFT_DIRECTORY = bundle.getString("url_grenzniederschriften");
        } catch (Exception ex) {
            LOG.error(
                "Could not read WebDav properties from property file. The umleitungsmechanism for Vermessungrisse will not work",
                ex);
            WEB_DAV_PASSWORD = "";
            WEB_DAV_USER = "";
            VERMESSUNG_DIRECTORY = "";
            GRENZNIEDERSCHRIFT_DIRECTORY = "";
            initError = true;
        }
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
                    final String input = getLinkDocument();
                    if ((input != null) && !input.isEmpty()) {
                        if (isNummerConsistent(input)) {
                            if (!input.contains(PLATZHALTER_PREFIX)) {
                                tfName.getDocument().removeDocumentListener(VermessungUmleitungPanel.this);
                                final String[] props = parsePropertiesFromLink(input);
                                final String correctFormattedNumber = props[0] + "-" + props[1] + "-" + props[2] + "-"
                                            + props[3];
                                tfName.setText(correctFormattedNumber);
                                tfName.getDocument().addDocumentListener(VermessungUmleitungPanel.this);
                            }
                            if (!checkIfRissExists(input)) {
                                // the riss we would like to link to does not exist.
                                cl.show(pnlControls, "card4");
                                editor.handleRissDoesNotExists();
                            } else {
                                checkIfLinkDocumentExists(false);
                            }
                        } else {
                            showError();
                        }
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
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXBusyLabel jXBusyLabel1;
    private javax.swing.JLabel lblMessage;
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
    public VermessungUmleitungPanel() {
        this(MODE.VERMESSUNGSRISS, null);
    }

    /**
     * Creates a new Alb_baulastUmleitungPanel object.
     *
     * @param  m       DOCUMENT ME!
     * @param  editor  DOCUMENT ME!
     */
    public VermessungUmleitungPanel(final MODE m, final VermessungRissEditor editor) {
        this.mode = m;
        this.editor = editor;
        initComponents();
        jXBusyLabel1.setSize(16, 16);
        tfName.getDocument().addDocumentListener(this);
        if (!initError) {
            webDavHelper = new WebDavHelper(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD, true);
        }
        new CidsBeanDropTarget(this);
        new CidsBeanDropTarget(tfName);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setLinkDocumentText(final String text) {
        this.setLinkDocumentText(text, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text       DOCUMENT ME!
     * @param  fireEvent  DOCUMENT ME!
     */
    public void setLinkDocumentText(final String text, final boolean fireEvent) {
        if (!fireEvent) {
            tfName.getDocument().removeDocumentListener(this);
        }
        escapeText = text;
        tfName.setText(text);
        final CardLayout cl = (CardLayout)pnlControls.getLayout();
        cl.show(pnlControls, "card1");
        if (!fireEvent) {
            tfName.getDocument().addDocumentListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  m  DOCUMENT ME!
     */
    public void setMode(final MODE m) {
        this.mode = m;
        if (mode == MODE.VERMESSUNGSRISS) {
            lblMode.setVisible(false);
            btnPlatzhalter.setVisible(false);
        } else {
            lblMode.setVisible(true);
            btnPlatzhalter.setVisible(true);
        }
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
     *
     * @param   rissNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkIfRissExists(final String rissNummer) {
        try {
            if (rissNummer.startsWith(PLATZHALTER_PREFIX)) {
                return true;
            }
            final String[] props = parsePropertiesFromLink(rissNummer);
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "vermessung_riss");
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ";
            query += "FROM " + MB_MC.getTableName();
            query += " WHERE schluessel ilike '" + props[0]
                        + "' and gemarkung=" + props[1]
                        + " and flur ilike '" + props[2]
                        + "' and blatt ilike '" + StringUtils.stripStart(props[3], "0") + "'";
            final MetaObject[] metaObjects = SessionManager.getProxy().getMetaObjectByQuery(query, 0);
            return (metaObjects != null) && (metaObjects.length == 1) && (metaObjects[0] != null);
        } catch (ConnectionException ex) {
            LOG.error("Error while checkig if riss exists", ex);
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void checkState() {
        checkIfLinkDocumentExists(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  createUmleitung  DOCUMENT ME!
     */
    private void checkIfLinkDocumentExists(final boolean createUmleitung) {
        final SwingWorker<URL, Void> worker = new SwingWorker<URL, Void>() {

                @Override
                protected void done() {
                    try {
                        final URL file = get();
                        if (createUmleitung) {
                            VermessungUmleitungPanel.this.createLinkFile();
                            return;
                        }
                        jXBusyLabel1.setBusy(false);
                        final CardLayout cl = (CardLayout)pnlControls.getLayout();
                        cl.show(pnlControls, "card3");
                        if (file != null) {
                            lastCheckedURL = file;
                            editor.successAlert();
                            editor.reloadPictureFromUrl(file);
                        } else {
                            // no file exists we need to show a warning...
                            lastCheckedURL = new URL(VermessungsrissWebAccessPictureFinder.getInstance().getObjectPath(
                                        true,
                                        getLinkDocument()));
                            editor.warnAlert();
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Worker Thread interrupter", ex);
                        showError();
                    } catch (Exception ex) {
                        LOG.error("Execution error", ex);
                        showError();
                    }
                }

                @Override
                protected URL doInBackground() throws Exception {
                    final String input = getLinkDocument();
//                if (!isNummerConsistent(input)) {
//                    return null;
//                }
                    final boolean isPlatzhalter = input.toLowerCase().startsWith(PLATZHALTER_PREFIX);
                    if ((mode == MODE.VERMESSUNGSRISS) && !isPlatzhalter) {
                        return null;
                    }
                    if (isPlatzhalter) {
                        return new URL(VermessungsrissWebAccessPictureFinder.getInstance().getObjectPath(
                                    mode
                                            == MODE.GRENZNIEDERSCHRIFT,
                                    input) + ".jpg");
                    } else {
                        final List<URL> res;
                        final String[] props = parsePropertiesFromLink(input);

                        // check if we need to format the flur and the blatt
                        if (mode == MODE.VERMESSUNGSRISS) {
                            res = VermessungsrissWebAccessPictureFinder.getInstance()
                                        .findVermessungsrissPicture(
                                                props[0],
                                                Integer.parseInt(props[1]),
                                                props[2],
                                                props[3]);
                        } else {
                            res = VermessungsrissWebAccessPictureFinder.getInstance()
                                        .findGrenzniederschriftPicture(
                                                props[0],
                                                Integer.parseInt(props[1]),
                                                props[2],
                                                props[3]);
                        }
                        if ((res == null) || res.isEmpty()) {
                            return null;
                        }
                        return res.get(0);
                    }
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String[] parsePropertiesFromLink(final String link) {
        final String[] splittedInput = link.split("-");
        if (splittedInput.length != 4) {
            return null;
        }
        final String[] res = new String[4];
        res[0] = splittedInput[0];
        res[1] = splittedInput[1];
        res[2] = StringUtils.leftPad(splittedInput[2], 3, '0');
        res[3] = StringUtils.leftPad(splittedInput[3], 8, '0');

        return res;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   vermessungrissNummer  baulastnr DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isNummerConsistent(final String vermessungrissNummer) {
        return vermessungrissNummer.matches("platzhalter.(\\d{3})")
                    || vermessungrissNummer.matches("(\\d{3})-(\\d{4})-(\\d{1,3})-(\\d{1,8})");
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    if (initError) {
                        return false;
                    }
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
                                        VermessungUmleitungPanel.class,
                                        "VermessungUmleitungPanel.errorDialog.title"),
                                    org.openide.util.NbBundle.getMessage(
                                        VermessungUmleitungPanel.class,
                                        "VermessungUmleitungPanel.errorDialog.delete.message"),
                                    null,
                                    null,
                                    null,
                                    Level.ALL,
                                    null);
                            JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                    VermessungUmleitungPanel.this),
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
                                    VermessungUmleitungPanel.class,
                                    "VermessungUmleitungPanell.errorDialog.title"),
                                org.openide.util.NbBundle.getMessage(
                                    VermessungUmleitungPanel.class,
                                    "VermessungUmleitungPanel.errorDialog.delete.message"),
                                ex.getMessage(),
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                VermessungUmleitungPanel.this),
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
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    if (initError) {
                        return false;
                    }
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
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        if (!get()) {
                            final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                    org.openide.util.NbBundle.getMessage(
                                        VermessungUmleitungPanel.class,
                                        "VermessungUmleitungPanel.errorDialog.title"),
                                    org.openide.util.NbBundle.getMessage(
                                        VermessungUmleitungPanel.class,
                                        "VermessungUmleitungPanel.errorDialog.create.message"),
                                    null,
                                    null,
                                    null,
                                    Level.ALL,
                                    null);
                            JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                    VermessungUmleitungPanel.this),
                                ei);
                            showError();
                            return;
                        }
                        editor.handleUmleitungCreated(lastCheckedURL);
                    } catch (InterruptedException ex) {
                        LOG.error("Create Link File Worker was interrupted.", ex);
                    } catch (Exception ex) {
                        LOG.error("Error in Create Link File worker", ex);
                        final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                org.openide.util.NbBundle.getMessage(
                                    VermessungUmleitungPanel.class,
                                    "VermessungUmleitungPanel.errorDialog.title"),
                                org.openide.util.NbBundle.getMessage(
                                    VermessungUmleitungPanel.class,
                                    "VermessungUmleitungPanel.errorDialog.create.message"),
                                ex.getMessage(),
                                null,
                                ex,
                                Level.ALL,
                                null);
                        JXErrorPane.showDialog(StaticSwingTools.getParentFrameIfNotNull(
                                VermessungUmleitungPanel.this),
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
        final StringBuffer buf = new StringBuffer();
        if (mode == MODE.VERMESSUNGSRISS) {
            buf.append(VERMESSUNG_DIRECTORY);
        } else {
            buf.append(GRENZNIEDERSCHRIFT_DIRECTORY);
        }
        buf.append("/");
        buf.append(String.format("%04d", gemarkung));
        buf.append("/");
        return buf.toString();
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

        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .getObjectFilename(
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
        lblMessage.setForeground(c);
        lblMode.setForeground(c);
    }

    @Override
    public void beansDropped(final ArrayList<CidsBean> droppedBeans) {
        try {
            if (droppedBeans.size() > 1) {
                LOG.info(
                    "There were more than one bean dropped on the vermessungs riss umleitungs text field. Just regarding the first one");
            }
            final CidsBean bean = droppedBeans.get(0);
            if (bean != null) {
                if (bean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("vermessung_riss")) {
                    final String schluessel = bean.getProperty("schluessel").toString();
                    final CidsBean gemarkungBean = (CidsBean)bean.getProperty("gemarkung");
                    Integer gemarkung = 0;
                    if (gemarkungBean != null) {
                        gemarkung = (Integer)gemarkungBean.getProperty("id");
                    }
                    final String flur = bean.getProperty("flur").toString();
                    final String blatt = bean.getProperty("blatt").toString();
                    final StringBuffer buf = new StringBuffer();
                    buf.append(StringUtils.leftPad(schluessel, 3, '0'));
                    buf.append("-");
                    buf.append(String.format("%04d", gemarkung));
                    buf.append("-");
                    buf.append(StringUtils.leftPad(flur, 3, '0'));
                    buf.append("-");
                    buf.append(StringUtils.leftPad(blatt, 8, '0'));
                    tfName.setText(buf.toString());
                }
            }
        } catch (Exception ex) {
            LOG.error("Problem when adding the DroppedBeans", ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblMessage = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tfName = new DropAwareTextField();
        lblMode = new javax.swing.JLabel();
        pnlControls = new javax.swing.JPanel();
        pnlEmpty = new javax.swing.JPanel();
        pnlOkButton = new javax.swing.JPanel();
        btnCreateDocument = new javax.swing.JButton();
        pnlBusyLabel = new javax.swing.JPanel();
        jXBusyLabel1 = new JXBusyLabel(new Dimension(16, 16));
        pnlError = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnPlatzhalter = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        setMinimumSize(new java.awt.Dimension(860, 32));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(860, 32));
        setLayout(new java.awt.GridBagLayout());

        lblMessage.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMessage,
            org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.lblMessage.text")); // NOI18N
        lblMessage.setMaximumSize(new java.awt.Dimension(1000, 18));
        lblMessage.setMinimumSize(new java.awt.Dimension(330, 18));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(lblMessage, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        tfName.setText(org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.tfName.text")); // NOI18N
        tfName.setMinimumSize(new java.awt.Dimension(180, 27));
        tfName.setPreferredSize(new java.awt.Dimension(180, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel1.add(tfName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblMode,
            org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.lblMode.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel1.add(lblMode, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(jPanel1, gridBagConstraints);

        pnlControls.setMinimumSize(new java.awt.Dimension(32, 32));
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
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.btnCreateDocument.text"));                                       // NOI18N
        btnCreateDocument.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.btnCreateDocument.toolTipText"));                                // NOI18N
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
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.jXBusyLabel1.text")); // NOI18N
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
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.jLabel1.text"));                                               // NOI18N
        jLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.jLabel1.toolTipText"));                                        // NOI18N
        jLabel1.setFocusable(false);
        pnlError.add(jLabel1, new java.awt.GridBagConstraints());

        pnlControls.add(pnlError, "card4");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        add(pnlControls, gridBagConstraints);

        btnPlatzhalter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/icon-file.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnPlatzhalter,
            org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.btnPlatzhalter.text"));                                // NOI18N
        btnPlatzhalter.setToolTipText(org.openide.util.NbBundle.getMessage(
                VermessungUmleitungPanel.class,
                "VermessungUmleitungPanel.btnPlatzhalter.toolTipText"));                         // NOI18N
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(btnPlatzhalter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        add(filler1, gridBagConstraints);
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
        final String schluessel = (String)editor.getCidsBean().getProperty("schluessel");
        if (schluessel == null) {
            LOG.error("can not read schluessel from vermessungsriss cidsbean");
            return;
        }
        final String url = PLATZHALTER_PREFIX + SEP + StringUtils.leftPad(schluessel, 3, '0');
        tfName.setText(url);
        tfName.getDocument().addDocumentListener(this);
        checkIfLinkDocumentExists(false);
    }                                                                                  //GEN-LAST:event_btnPlatzhalterActionPerformed

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class DropAwareTextField extends JTextField implements CidsBeanDropListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            VermessungUmleitungPanel.this.beansDropped(beans);
        }
    }
}
