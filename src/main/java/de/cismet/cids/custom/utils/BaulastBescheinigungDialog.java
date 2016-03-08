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
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.LandParcel;

import lombok.AccessLevel;
import lombok.Getter;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;

import java.awt.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisBuchungsblattRenderer;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.alkis.SOAPAccessProvider;
import de.cismet.cids.custom.wunda_blau.search.actions.BaulastBescheinigungPruefungServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.commons.gui.progress.BusyLoggingTextPane;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import static de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator.createFertigungsVermerk;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastBescheinigungDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    public static final Logger LOG = Logger.getLogger(BaulastBescheinigungDialog.class);

    private static BaulastBescheinigungDialog INSTANCE;

    private static final Map<CidsBean, Buchungsblatt> BUCHUNGSBLATT_CACHE = new HashMap<CidsBean, Buchungsblatt>();

    private static final String PARAMETER_JOBNUMBER = "JOBNUMBER";
    private static final String PARAMETER_PROJECTNAME = "PROJECTNAME";
    private static final String PARAMETER_HAS_BELASTET = "HAS_BELASTET";
    private static final String PARAMETER_HAS_BEGUENSTIGT = "HAS_BEGUENSTIGT";
    private static final String PARAMETER_FABRICATIONNOTICE = "FABRICATIONNOTICE";

    private static final SOAPAccessProvider SOAP_PROVIDER;
    private static final ALKISInfoServices INFO_SERVICE;

    static {
        SOAPAccessProvider soapProvider = null;
        ALKISInfoServices infoService = null;
        if (!AlkisUtils.validateUserShouldUseAlkisSOAPServerActions()) {
            try {
                soapProvider = new SOAPAccessProvider();
                infoService = soapProvider.getAlkisInfoService();
            } catch (final Exception ex) {
                LOG.warn("error while creating ALKISInfoServices", ex);
            }
        }
        SOAP_PROVIDER = soapProvider;
        INFO_SERVICE = infoService;
    }

    //~ Instance fields --------------------------------------------------------

    private final Collection<ProductGroupAmount> prodAmounts = new ArrayList<ProductGroupAmount>();
    private final Set<BescheinigungsGruppeBean> bescheinigungsgruppen = new HashSet<BescheinigungsGruppeBean>();

    private SwingWorker worker;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.commons.gui.progress.BusyStatusPanel busyStatusPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private de.cismet.commons.gui.progress.BusyLoggingTextPane protokollPane;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BaulastBescheinigungDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    private BaulastBescheinigungDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BaulastBescheinigungDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BaulastBescheinigungDialog(null, true);
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    public static void addMessage(final String message) {
        getInstance().protokollPane.addMessage(message, BusyLoggingTextPane.Styles.INFO);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    public void addError(final String message) {
        protokollPane.addMessage(message, BusyLoggingTextPane.Styles.ERROR);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        busyStatusPanel1 = new de.cismet.commons.gui.progress.BusyStatusPanel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        protokollPane = new de.cismet.commons.gui.progress.BusyLoggingTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 4);
        jPanel1.add(jLabel2, gridBagConstraints);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jTextField1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jTextField1, gridBagConstraints);

        jTextField2.setText(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jTextField2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jTextField2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel2.add(jButton2);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        jPanel2.add(jButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(jPanel4, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel5.setMinimumSize(new java.awt.Dimension(450, 31));
        jPanel5.setPreferredSize(new java.awt.Dimension(450, 31));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        busyStatusPanel1.setStatusMessage(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.busyStatusPanel1.statusMessage")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(busyStatusPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(jPanel5, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(500, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 250));

        protokollPane.setEditable(false);
        jScrollPane1.setViewportView(protokollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     * @param  parent       DOCUMENT ME!
     */
    public void show(final Collection<CidsBean> flurstuecke, final Component parent) {
        final List<CidsBean> flurstueckeList = new ArrayList<CidsBean>(new HashSet<CidsBean>(flurstuecke));
        prodAmounts.clear();
        bescheinigungsgruppen.clear();

        jTextField2.setText(new SimpleDateFormat("yy").format(new Date()) + "-");

        prepareDownload(flurstueckeList);

        StaticSwingTools.showDialog(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        doDownload();
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     */
    public void prepareDownload(final List<CidsBean> flurstuecke) {
        String checkProtokollPane = null;
        try {
            checkProtokollPane = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                                "baulast.bescheinigung.protokollpane_enabled");
        } catch (ConnectionException ex) {
        }
        jScrollPane1.setVisible(checkProtokollPane != null);

        try {
            if ((worker != null) && !worker.isDone()) {
                worker.cancel(true);
            }

            try {
                protokollPane.getDocument().remove(0, protokollPane.getDocument().getLength());
            } catch (BadLocationException ex) {
                LOG.error("Could not clear Protokoll Pane", ex);
            }
            jButton1.setEnabled(false);
            protokollPane.setBusy(true);
            busyStatusPanel1.setBusy(true);

            pack();

            setStatusMessage("Bescheinigung wird vorbereitet...");

            worker = new SwingWorker<Collection<ProductGroupAmount>, Void>() {

                    @Override
                    protected Collection<ProductGroupAmount> doInBackground() throws Exception {
                        final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap =
                            new HashMap<CidsBean, Set<CidsBean>>();
                        final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap =
                            new HashMap<CidsBean, Set<CidsBean>>();

                        addMessage("Baulastbescheinigungs-Protokoll für "
                                    + ((flurstuecke.size() == 1) ? "folgendes Flurstück" : "folgende Flurstücke")
                                    + ":");

                        Collections.sort(flurstuecke, new Comparator<CidsBean>() {

                                @Override
                                public int compare(final CidsBean o1, final CidsBean o2) {
                                    final String s1 = (o1 == null) ? "" : (String)o1.getProperty("alkis_id");
                                    final String s2 = (o2 == null) ? "" : (String)o2.getProperty("alkis_id");
                                    return s1.compareTo(s2);
                                }
                            });

                        for (final CidsBean flurstueck : flurstuecke) {
                            addMessage(" * " + flurstueck);
                        }

                        setStatusMessage("Buchungsblätter werden analysiert...");

                        final Map<String, Set<CidsBean>> grundstueckeToFlurstueckeMap =
                            createGrundstueckeToFlurstueckeMap(flurstuecke);

                        setStatusMessage("Baulasten werden gesucht...");

                        fillFlurstueckeToBaulastenMaps(
                            flurstuecke,
                            flurstueckeToBaulastenBelastetMap,
                            flurstueckeToBaulastenBeguenstigtMap);

                        setStatusMessage("Gebühr wird berechnet...");

                        final Collection<ProductGroupAmount> prodAmounts = createBilling(
                                grundstueckeToFlurstueckeMap,
                                flurstueckeToBaulastenBelastetMap,
                                flurstueckeToBaulastenBeguenstigtMap);

                        bescheinigungsgruppen.addAll(createBescheinigungsGruppen(
                                flurstuecke,
                                flurstueckeToBaulastenBeguenstigtMap,
                                flurstueckeToBaulastenBelastetMap));

                        return prodAmounts;
                    }

                    @Override
                    protected void done() {
                        boolean errorOccurred = false;
                        try {
                            prodAmounts.addAll(get());
                        } catch (final Exception ex) {
                            errorOccurred = true;
                            final String errMessage;
                            final Throwable exception;
                            if (ex.getCause() instanceof BaBeException) {
                                exception = ex.getCause();
                                errMessage = exception.getMessage();
                                addError(errMessage);
                            } else {
                                exception = ex;
                                LOG.error(ex, ex);
                                errMessage = exception.getMessage();
                                addError("Es ist ein Fehler aufgetreten: " + errMessage);
                            }
                            if (!(ex instanceof CancellationException)) {
                                JOptionPane.showMessageDialog(
                                    BaulastBescheinigungDialog.this,
                                    errMessage,
                                    "Es ist ein Fehler aufgetreten.",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } finally {
                            if (errorOccurred) {
                                setStatusMessage("Es ist ein Fehler aufgetreten.");
                            } else {
                                setStatusMessage("Die Bescheinigung kann jetzt erzeugt werden.");
                            }
                            busyStatusPanel1.setBusy(false);
                            protokollPane.setBusy(false);
                            jButton1.setEnabled(!errorOccurred);
                        }
                    }
                };
            worker.execute();
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                           DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static void fillFlurstueckeToBaulastenMaps(final Collection<CidsBean> flurstuecke,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap) throws Exception {
        addMessage("\n===");

        // belastete Baulasten pro Flurstück
        flurstueckeToBaulastenBelastetMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, true));

        // begünstigte Baulasten pro Flurstück
        flurstueckeToBaulastenBeguenstigtMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, false));
    }

    /**
     * DOCUMENT ME!
     */
    public void doDownload() {
        final String projectdescription = jTextField1.getText();
        final String jobnumber = jTextField2.getText();

        try {
            if (BillingPopup.doBilling(
                            "blab_be",
                            "no.yet",
                            (Geometry)null,
                            prodAmounts.toArray(new ProductGroupAmount[0]))) {
                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(BaulastBescheinigungDialog.this)) {
                    final String berechnung = BillingPopup.getInstance().getBerechnungsProtokoll();
                    if ((berechnung != null) && !berechnung.trim().isEmpty()) {
                        addMessage("\n===\n\nGebührenberechnung:\n");
                        addMessage(berechnung);
                    }
                    final Download download = generateDownload(
                            projectdescription,
                            jobnumber,
                            bescheinigungsgruppen);
                    DownloadManager.instance().add(download);
                }
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
        if ((worker != null) && !worker.isDone()) {
            worker.cancel(true);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BaulastBescheinigungDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BaulastBescheinigungDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BaulastBescheinigungDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BaulastBescheinigungDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        try {
            DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost("WUNDA_BLAU", null, "admin", "");
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
            System.exit(1);
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final BaulastBescheinigungDialog dialog = BaulastBescheinigungDialog.getInstance();
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });
                    dialog.show(BaulastBescheinigungDialog.createTestFlurstuecke(), new javax.swing.JFrame());
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Set<CidsBean> createTestFlurstuecke() {
        final String query = "select %d, id \n"
                    + "from alkis_landparcel \n"
                    + "where gemarkung ilike 'Barmen' and flur ilike '224' and fstck_zaehler ilike '0012%%'";

        final MetaClass mcFlurstueck = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "alkis_landparcel");

        final Set<CidsBean> flurstuecke = new HashSet<CidsBean>();
        try {
            final MetaObject[] mos = SessionManager.getProxy()
                        .getMetaObjectByQuery(String.format(
                                query,
                                mcFlurstueck.getID(),
                                mcFlurstueck.getPrimaryKey()),
                            0);
            for (final MetaObject mo : mos) {
                flurstuecke.add(mo.getBean());
            }
        } catch (ConnectionException ex) {
            LOG.fatal(ex, ex);
        }

        addMessage("Anzahl Flurstücke: " + flurstuecke.size());

        return flurstuecke;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke  DOCUMENT ME!
     * @param   belastet     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Map<CidsBean, Set<CidsBean>> createFlurstueckeToBaulastenMap(final Collection<CidsBean> flurstuecke,
            final boolean belastet) throws Exception {
        final String queryBeguenstigt = "SELECT %d, alb_baulast.%s \n"
                    + "FROM alb_baulast_flurstuecke_beguenstigt, alb_baulast, alb_flurstueck_kicker, flurstueck \n"
                    + "WHERE alb_baulast.id = alb_baulast_flurstuecke_beguenstigt.baulast_reference \n"
                    + "AND alb_baulast_flurstuecke_beguenstigt.flurstueck = alb_flurstueck_kicker.id \n"
                    + "AND alb_flurstueck_kicker.fs_referenz = flurstueck.id \n"
                    + "AND flurstueck.alkis_id ilike '%s' \n"
                    + "AND alb_baulast.geschlossen_am is null AND alb_baulast.loeschungsdatum is null";

        final String queryBelastet = "SELECT %d, alb_baulast.%s \n"
                    + "FROM alb_baulast_flurstuecke_belastet, alb_baulast, alb_flurstueck_kicker, flurstueck \n"
                    + "WHERE alb_baulast.id = alb_baulast_flurstuecke_belastet.baulast_reference \n"
                    + "AND alb_baulast_flurstuecke_belastet.flurstueck = alb_flurstueck_kicker.id \n"
                    + "AND alb_flurstueck_kicker.fs_referenz = flurstueck.id \n"
                    + "AND flurstueck.alkis_id ilike '%s' \n"
                    + "AND alb_baulast.geschlossen_am is null AND alb_baulast.loeschungsdatum is null";

        final MetaClass mcBaulast = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "alb_baulast");

        final String query = belastet ? queryBelastet : queryBeguenstigt;

        addMessage("\nSuche der " + ((belastet) ? "belastenden" : "begünstigenden") + " Baulasten von:");
        final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenMap = new HashMap<CidsBean, Set<CidsBean>>();
        for (final CidsBean flurstueck : flurstuecke) {
            addMessage(" * Flurstück: " + flurstueck + " ...");
            final Set<CidsBean> baulasten = new HashSet<CidsBean>();
            try {
                final BaulastSearchInfo searchInfo = new BaulastSearchInfo();
                final Integer gemarkung = Integer.parseInt(((String)flurstueck.getProperty("alkis_id")).substring(
                            2,
                            6));
                final String flur = (String)flurstueck.getProperty("flur");
                final String zaehler = Integer.toString(Integer.parseInt(
                            (String)flurstueck.getProperty("fstck_zaehler")));
                final String nenner = (flurstueck.getProperty("fstck_nenner") == null)
                    ? "0" : Integer.toString(Integer.parseInt((String)flurstueck.getProperty("fstck_nenner")));

                final FlurstueckInfo fsi = new FlurstueckInfo(gemarkung, flur, zaehler, nenner);
                searchInfo.setFlurstuecke(Arrays.asList(fsi));
                searchInfo.setResult(CidsBaulastSearchStatement.Result.BAULAST);
                searchInfo.setBelastet(belastet);
                searchInfo.setBeguenstigt(!belastet);
                searchInfo.setBlattnummer("");
                searchInfo.setArt("");
                final CidsBaulastSearchStatement search = new CidsBaulastSearchStatement(
                        searchInfo,
                        mcBaulast.getId(),
                        -1);

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                final Collection<MetaObjectNode> mons = SessionManager.getProxy().customServerSearch(search);
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = SessionManager.getProxy()
                                .getMetaObject(mon.getObjectId(), mon.getClassId(), "WUNDA_BLAU");
                    if ((mo.getBean() != null) && (mo.getBean() != null)
                                && (mo.getBean().getProperty("loeschungsdatum") != null)) {
                        continue;
                    }
                    if (mon.getName().startsWith("indirekt: ")) {
                        throw new BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                }

                final String alkisId = (String)flurstueck.getProperty("alkis_id");

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                final MetaObject[] mos = SessionManager.getProxy()
                            .getMetaObjectByQuery(String.format(
                                    query,
                                    mcBaulast.getID(),
                                    mcBaulast.getPrimaryKey(),
                                    alkisId),
                                0);
                for (final MetaObject mo : mos) {
                    final CidsBean baulast = mo.getBean();
                    final Boolean geprueft = (Boolean)baulast.getProperty("geprueft");
                    if ((geprueft == null) || (geprueft == false)) {
                        throw new BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                    addMessage("   => Baulast: " + baulast);
                    baulasten.add(baulast);
                }
                flurstueckeToBaulastenMap.put(flurstueck, baulasten);
            } catch (ConnectionException ex) {
                LOG.fatal(ex, ex);
            }
        }
        return flurstueckeToBaulastenMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     */
    private void setStatusMessage(final String message) {
        busyStatusPanel1.setStatusMessage(message);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                           DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Set<BescheinigungsGruppeBean> createBescheinigungsGruppen(final Collection<CidsBean> flurstuecke,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap) {
        final Map<String, BescheinigungsGruppeBean> gruppeMap = new HashMap<String, BescheinigungsGruppeBean>();
        for (final CidsBean flurstueck : flurstuecke) {
            final Set<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(flurstueck);
            final Set<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
            final BescheinigungsGruppeBean newGruppe = new BescheinigungsGruppeBean(
                    baulastenBeguenstigt,
                    baulastenBelastet);
            final String gruppeKey = newGruppe.toString();
            if (!gruppeMap.containsKey(gruppeKey)) {
                gruppeMap.put(gruppeKey, newGruppe);
            }
            final BescheinigungsGruppeBean gruppe = gruppeMap.get(gruppeKey);
            gruppe.getFlurstuecke().add(new FlurstueckBean(flurstueck));
        }

        final Set<BescheinigungsGruppeBean> bescheinigungsgruppen = new HashSet<BescheinigungsGruppeBean>(
                gruppeMap.values());

        addMessage("Anzahl Bescheinigungsgruppen: " + bescheinigungsgruppen.size());
        for (final BescheinigungsGruppeBean gruppe : bescheinigungsgruppen) {
            addMessage(" * " + gruppe.toString());
        }
        return bescheinigungsgruppen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception             DOCUMENT ME!
     * @throws  InterruptedException  DOCUMENT ME!
     */
    private static Map<String, Set<CidsBean>> createGrundstueckeToFlurstueckeMap(final Collection<CidsBean> flurstuecke)
            throws Exception {
        addMessage("\n===\n\nZuordnung der Flurstücke zu Grundstücken...");

        final Map<String, Set<CidsBean>> grundstueckeToFlurstueckeMap = new HashMap<String, Set<CidsBean>>();

        for (final CidsBean flurstueckBean : flurstuecke) {
            final List<CidsBean> buchungsblaetter = new ArrayList<CidsBean>(
                    flurstueckBean.getBeanCollectionProperty("buchungsblaetter"));
            if (buchungsblaetter.size() == 1) {
                addMessage("\nFlurstück: " + flurstueckBean + " (1 Buchungsblatt):");
            } else {
                addMessage("\nFlurstück: " + flurstueckBean + " (" + buchungsblaetter.size()
                            + " Buchungsblätter):");
            }
            Collections.sort(buchungsblaetter, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        final String s1 = (o1 == null) ? "" : (String)o1.getProperty("buchungsblattcode");
                        final String s2 = (o2 == null) ? "" : (String)o2.getProperty("buchungsblattcode");
                        return s1.compareTo(s2);
                    }
                });

//            boolean teileigentumAlreadyCounted = false;
            boolean grundstueckFound = false;
            for (final CidsBean buchungsblattBean : buchungsblaetter) {
                if (grundstueckFound) {
                    break; // we are done
                }
                if (buchungsblattBean != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    addMessage(" * analysiere Buchungsblatt " + buchungsblattBean + " ...");
                    final Buchungsblatt buchungsblatt = getBuchungsblatt(buchungsblattBean);

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    final List<Buchungsstelle> buchungsstellen = Arrays.asList(buchungsblatt.getBuchungsstellen());
                    Collections.sort(buchungsstellen, new Comparator<Buchungsstelle>() {

                            @Override
                            public int compare(final Buchungsstelle o1, final Buchungsstelle o2) {
                                final String s1 = (o1 == null) ? "" : o1.getSequentialNumber();
                                final String s2 = (o2 == null) ? "" : o2.getSequentialNumber();
                                return s1.compareTo(s2);
                            }
                        });

                    for (final Buchungsstelle buchungsstelle : buchungsstellen) {
                        if (grundstueckFound) {
                            break; // we are done
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        boolean flurstueckPartOfStelle = true;
                        final LandParcel[] landparcels = buchungsstelle.getLandParcel();
                        if (landparcels != null) {
                            flurstueckPartOfStelle = false;
                            for (final LandParcel lp : landparcels) {
                                if (((String)flurstueckBean.getProperty("alkis_id")).equals(
                                                lp.getLandParcelCode())) {
                                    flurstueckPartOfStelle = true;
                                    break;
                                }
                            }
                        }
                        if (flurstueckPartOfStelle) {
                            final String key = buchungsstelle.getSequentialNumber() + "/"
                                        + buchungsblatt.getBuchungsblattCode();

                            final String buchungsart = buchungsstelle.getBuchungsart();
                            if ("Erbbaurecht".equals(buchungsart)) {
                                addMessage("   -> ignoriere " + key + " aufgrund der Buchungsart ("
                                            + buchungsart + ")");
                                continue;
                            }

//                            if ("Wohnungs-/Teileigentum".equals(buchungsart)) {
//                                if (teileigentumAlreadyCounted) {
//                                    addMessage("   -> ignoriere " + key + " aufgrund der Buchungsart ("
//                                                + buchungsart
//                                                + ")");
//                                    continue;
//                                } else {
//                                    teileigentumAlreadyCounted = true;
//                                }
//                            }
                            if (!grundstueckeToFlurstueckeMap.containsKey(key)) {
                                grundstueckeToFlurstueckeMap.put(key, new HashSet<CidsBean>());
                            }

                            final String buchungsartSuffix = "Grundstück".equals(buchungsart)
                                ? "" : (" (" + buchungsart + ")");
                            addMessage("   => füge Flurstück " + flurstueckBean + " zu Grundstück " + key + "hinzu"
                                        + buchungsartSuffix);
                            grundstueckeToFlurstueckeMap.get(key).add(flurstueckBean);
                            grundstueckFound = true;
                        }
                    }
                }
            }
        }
//
//        boolean first = true;
//
//        final Set<String> keys = new HashSet<String>(grundstueckeToFlurstueckeMap.keySet());
//        for (final String key : keys) {
//            final Set<CidsBean> flurstueckeToCheck = new HashSet<CidsBean>(grundstueckeToFlurstueckeMap.get(key));
//            for (final String innerKey : keys) {
//                if (flurstueckeToCheck.isEmpty()) {
//                    break;
//                }
//                if (!innerKey.equals(key)) {
//                    if (grundstueckeToFlurstueckeMap.containsKey(innerKey)) {
//                        for (final CidsBean flurstueck : grundstueckeToFlurstueckeMap.get(innerKey)) {
//                            if (flurstueckeToCheck.contains(flurstueck)) {
//                                flurstueckeToCheck.remove(flurstueck);
//                                if (flurstueckeToCheck.isEmpty()) {
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            // all flurstueck found in another grundstueck(e);
//            if (flurstueckeToCheck.isEmpty()) {
//                if (first) {
//                    addMessage("\n");
//                    first = false;
//                }
//                addMessage("Entferne Grundstück " + key
//                            + " da alle enthaltenen Flurstücke bereits anderen Grundstücken zugeordnet wurden.");
//                grundstueckeToFlurstueckeMap.remove(key);
//            }
//        }

        return grundstueckeToFlurstueckeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bescheinigungsGruppe  DOCUMENT ME!
     * @param   jobname               DOCUMENT ME!
     * @param   jobnumber             DOCUMENT ME!
     * @param   projectName           DOCUMENT ME!
     * @param   number                projectname DOCUMENT ME!
     * @param   max                   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Download createBescheinigungPdf(final BescheinigungsGruppeBean bescheinigungsGruppe,
            final String jobname,
            final String jobnumber,
            final String projectName,
            final int number,
            final int max) throws Exception {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    try {
                        final Collection<BescheinigungsGruppeBean> reportBeans = Arrays.asList(
                                new BescheinigungsGruppeBean[] { bescheinigungsGruppe });
                        final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                        return dataSource;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    try {
                        final HashMap parameters = new HashMap();
                        parameters.put(PARAMETER_JOBNUMBER, jobnumber);
                        parameters.put(PARAMETER_PROJECTNAME, projectName);

                        parameters.put(PARAMETER_HAS_BELASTET, !bescheinigungsGruppe.getBaulastenBelastet().isEmpty());
                        parameters.put(
                            PARAMETER_HAS_BEGUENSTIGT,
                            !bescheinigungsGruppe.getBaulastenBeguenstigt().isEmpty());
                        parameters.put(
                            PARAMETER_FABRICATIONNOTICE,
                            createFertigungsVermerk(SessionManager.getSession().getUser()));
                        return parameters;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final Collection<FlurstueckBean> fls = bescheinigungsGruppe.getFlurstuecke();
        final String title = "Bescheinigung " + fls.iterator().next().getAlkisId() + ((fls.size() > 1) ? " (ua)" : "")
                    + " " + number + "/" + max;
        final String fileName = "bescheinigung_" + fls.iterator().next().getAlkisId().replace("/", "--")
                    + ((fls.size() > 1) ? ".ua" : "")
                    + "_" + number;

        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/baulastbescheinigung.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                title,
                fileName);

        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s1  DOCUMENT ME!
     * @param   s2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int compareString(final String s1, final String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (s1.equals(s2)) {
            return 0;
        } else {
            return s1.compareTo(s2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   grundstueckeToFlurstueckeMap          flurstuecke flurstueckeToBaulastengrundstueckMap DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Collection<ProductGroupAmount> createBilling(
            final Map<String, Set<CidsBean>> grundstueckeToFlurstueckeMap,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap) {
        final List<String> keys = new ArrayList<String>(grundstueckeToFlurstueckeMap.keySet());
        Collections.sort(keys);

        final int anzahlGrundstuecke = grundstueckeToFlurstueckeMap.size();
        if (anzahlGrundstuecke == 1) {
            addMessage("\n===\n\nBescheinigungsart des Grundstücks:");
        } else {
            addMessage("\n===\n\nBescheinigungsarten der " + anzahlGrundstuecke + " ermittelten Grundstücke:");
        }

        final Collection<ProductGroupAmount> prodAmounts = new ArrayList<ProductGroupAmount>();

        int anzahlNegativ = 0;
        int anzahlPositiv1 = 0;
        int anzahlPositiv2 = 0;
        int anzahlPositiv3 = 0;

        for (final String key : keys) {
            if (grundstueckeToFlurstueckeMap.containsKey(key)) {
                boolean first = true;

                final Set<CidsBean> flurstuecke = grundstueckeToFlurstueckeMap.get(key);

                final Set<CidsBean> baulasten = new HashSet<CidsBean>();
                for (final CidsBean flurstueck : flurstuecke) {
                    final Set<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
                    final Set<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(flurstueck);
                    baulasten.addAll(baulastenBelastet);
                    baulasten.addAll(baulastenBeguenstigt);
                }

                final StringBuffer sb = new StringBuffer();
                for (final CidsBean baulast : baulasten) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(baulast);
                }
                final String baulastenString = sb.toString();

                final int numOfBaulasten = baulasten.size();
                switch (numOfBaulasten) {
                    case 0:
                        addMessage(" * Grundstück " + key + " => Negativ-Bescheinigung");
                        anzahlNegativ++;
                        break;
                    case 1:
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für eine Baulast (" + baulastenString
                                + ")");
                        anzahlPositiv1++;
                        break;
                    case 2:
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für zwei Baulasten ("
                                + baulastenString
                                + ")");
                        anzahlPositiv2++;
                        break;
                    default:
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für drei oder mehr Baulasten ("
                                + baulastenString + ")");
                        anzahlPositiv3++;
                        break;
                }
            }
        }

        if (anzahlNegativ > 0) {
            if (anzahlNegativ > 10) {
                prodAmounts.add(new ProductGroupAmount("ea_blab_neg_ab_10", 1));
            } else {
                prodAmounts.add(new ProductGroupAmount("ea_blab_neg", anzahlNegativ));
            }
        }
        if (anzahlPositiv1 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_1", anzahlPositiv1));
        }
        if (anzahlPositiv2 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_2", anzahlPositiv2));
        }
        if (anzahlPositiv3 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_3", anzahlPositiv3));
        }

        return prodAmounts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Buchungsblatt getBuchungsblatt(final CidsBean buchungsblattBean) throws Exception {
        Buchungsblatt buchungsblatt = null;

        if (buchungsblattBean != null) {
            buchungsblatt = BUCHUNGSBLATT_CACHE.get(buchungsblattBean);
            if (buchungsblatt == null) {
                final String buchungsblattcode = String.valueOf(buchungsblattBean.getProperty("buchungsblattcode"));
                if ((buchungsblattcode != null) && (buchungsblattcode.length() > 5)) {
                    if (INFO_SERVICE != null) {
                        final String[] uuids = INFO_SERVICE.translateBuchungsblattCodeIntoUUIds(
                                SOAP_PROVIDER.getIdentityCard(),
                                SOAP_PROVIDER.getService(),
                                AlkisBuchungsblattRenderer.fixBuchungslattCode(buchungsblattcode));
                        buchungsblatt = INFO_SERVICE.getBuchungsblattWithUUID(SOAP_PROVIDER.getIdentityCard(),
                                SOAP_PROVIDER.getService(),
                                uuids[0],
                                true);
                    } else {
                        buchungsblatt = AlkisUtils.getBuchungsblattFromAlkisSOAPServerAction(
                                AlkisBuchungsblattRenderer.fixBuchungslattCode(buchungsblattcode));
                    }

                    BUCHUNGSBLATT_CACHE.put(buchungsblattBean, buchungsblatt);
                }
            }
        }

        return buchungsblatt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   projectdescription     DOCUMENT ME!
     * @param   jobnumber              DOCUMENT ME!
     * @param   bescheinigungsgruppen  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public Download generateDownload(final String projectdescription,
            final String jobnumber,
            final Set<BescheinigungsGruppeBean> bescheinigungsgruppen) throws Exception {
        final String jobname = DownloadManagerDialog.getInstance().getJobName();
        final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
            new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                @Override
                public Collection<? extends Download> fetchDownloads() throws Exception {
                    final Collection<Download> downloads = new ArrayList<Download>();
                    try {
                        downloads.add(new TxtDownload(
                                protokollPane.getText(),
                                jobname,
                                "Baulastbescheinigung-Protokoll",
                                "baulastbescheinigung_protokoll",
                                ".txt"));

                        if (bescheinigungsgruppen != null) {
                            final Set<CidsBean> allBaulasten = new HashSet<CidsBean>();

                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = bescheinigungsgruppen.size();
                            for (final BescheinigungsGruppeBean bescheinigungsGruppe : bescheinigungsgruppen) {
                                downloads.add(createBescheinigungPdf(
                                        bescheinigungsGruppe,
                                        jobname,
                                        jobnumber,
                                        projectdescription,
                                        ++number,
                                        max));
                                // alle Baulasten ermitteln
                                for (final BaulastInfoBean baulast : bescheinigungsGruppe.getBaulastenBelastet()) {
                                    allBaulasten.add(baulast.getBaulast());
                                }
                                for (final BaulastInfoBean baulast : bescheinigungsGruppe.getBaulastenBeguenstigt()) {
                                    allBaulasten.add(baulast.getBaulast());
                                }
                            }

                            if (!allBaulasten.isEmpty()) {
                                // Download: Bericht für alle Baulasten
                                downloads.addAll(BaulastenReportGenerator.generateRasterDownloads(
                                        jobname,
                                        allBaulasten,
                                        jobnumber,
                                        projectdescription));
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    }

                    return downloads;
                }
            };
        return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    public static class BaulastInfoBean {

        //~ Instance fields ----------------------------------------------------

        private final String blattnummer;
        private final String laufende_nummer;
        private final String arten;
        @Getter(AccessLevel.PRIVATE)
        private final CidsBean baulast;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BaulastBean object.
         *
         * @param  baulast  DOCUMENT ME!
         */
        BaulastInfoBean(final CidsBean baulast) {
            this.baulast = baulast;
            this.blattnummer = (String)baulast.getProperty("blattnummer");
            this.laufende_nummer = (String)baulast.getProperty("laufende_nummer");

            final StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (final CidsBean art : baulast.getBeanCollectionProperty("art")) {
                if (!first) {
                    sb.append(", ");
                    first = false;
                }
                sb.append(art.getProperty("baulast_art"));
            }
            this.arten = sb.toString();
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return baulast.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    public static class BescheinigungsGruppeBean {

        //~ Instance fields ----------------------------------------------------

        private final List<FlurstueckBean> flurstuecke;
        private final List<BaulastInfoBean> baulastenBeguenstigt;
        private final List<BaulastInfoBean> baulastenBelastet;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BescheinigungsGruppe object.
         *
         * @param  baulastenBeguenstigt  DOCUMENT ME!
         * @param  baulastenBelastet     DOCUMENT ME!
         */
        public BescheinigungsGruppeBean(final Set<CidsBean> baulastenBeguenstigt,
                final Set<CidsBean> baulastenBelastet) {
            this(new HashSet<CidsBean>(), baulastenBeguenstigt, baulastenBelastet);
        }

        /**
         * Creates a new BescheinigungsGruppeBean object.
         *
         * @param  flurstuecke           DOCUMENT ME!
         * @param  baulastenBeguenstigt  DOCUMENT ME!
         * @param  baulastenBelastet     DOCUMENT ME!
         */
        public BescheinigungsGruppeBean(final Set<CidsBean> flurstuecke,
                final Set<CidsBean> baulastenBeguenstigt,
                final Set<CidsBean> baulastenBelastet) {
            this.flurstuecke = new ArrayList<FlurstueckBean>();
            for (final CidsBean flurstueck : flurstuecke) {
                this.flurstuecke.add(new FlurstueckBean(flurstueck));
            }

            this.baulastenBeguenstigt = new ArrayList<BaulastInfoBean>();
            for (final CidsBean baulastBeguenstigt : baulastenBeguenstigt) {
                this.baulastenBeguenstigt.add(new BaulastInfoBean(baulastBeguenstigt));
            }
            this.baulastenBelastet = new ArrayList<BaulastInfoBean>();
            for (final CidsBean baulastBelastet : baulastenBelastet) {
                this.baulastenBelastet.add(new BaulastInfoBean(baulastBelastet));
            }

            Collections.sort(this.flurstuecke, new Comparator<FlurstueckBean>() {

                    @Override
                    public int compare(final FlurstueckBean o1, final FlurstueckBean o2) {
                        final int compareGemarkung = compareString(o1.getGemarkung(), o2.getGemarkung());
                        if (compareGemarkung != 0) {
                            return compareGemarkung;
                        } else {
                            final int compareFlur = compareString(o1.getFlur(), o2.getFlur());
                            if (compareFlur != 0) {
                                return compareFlur;
                            } else {
                                final int compareNummer = compareString(o1.getNummer(), o2.getNummer());
                                if (compareNummer != 0) {
                                    return compareNummer;
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }
                });

            final Comparator<BaulastInfoBean> baulastBeanComparator = new Comparator<BaulastInfoBean>() {

                    @Override
                    public int compare(final BaulastInfoBean o1, final BaulastInfoBean o2) {
                        final int compareBlattnummer = compareString(o1.getBlattnummer(), o2.getBlattnummer());
                        if (compareBlattnummer != 0) {
                            return compareBlattnummer;
                        } else {
                            final int compareLaufendenummer = compareString(o1.getLaufende_nummer(),
                                    o2.getLaufende_nummer());
                            if (compareLaufendenummer != 0) {
                                return compareLaufendenummer;
                            } else {
                                return 0;
                            }
                        }
                    }
                };

            Collections.sort(this.baulastenBeguenstigt, baulastBeanComparator);
            Collections.sort(this.baulastenBelastet, baulastBeanComparator);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();

            final List<BaulastInfoBean> sortedBeguenstigt = new ArrayList<BaulastInfoBean>(baulastenBeguenstigt);
            Collections.sort(sortedBeguenstigt, new BaulastBeanComparator());

            boolean first = true;
            for (final BaulastInfoBean baulast : sortedBeguenstigt) {
                if (!first) {
                    sb.append(", ");
                    first = false;
                }
                sb.append(baulast.toString());
            }

            sb.append("|");

            final List<BaulastInfoBean> sortedBelastet = new ArrayList<BaulastInfoBean>(baulastenBelastet);
            Collections.sort(sortedBelastet, new BaulastBeanComparator());

            first = true;
            for (final BaulastInfoBean baulast : sortedBelastet) {
                if (!first) {
                    sb.append(";");
                    first = false;
                }
                sb.append(baulast.toString());
            }

            return sb.toString();
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof BescheinigungsGruppeBean) {
                return toString().equals(other.toString());
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class BaulastBeanComparator implements Comparator<BaulastInfoBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final BaulastInfoBean o1, final BaulastInfoBean o2) {
            final String s1 = (o1 == null) ? "" : o1.toString(); // NOI18N
            final String s2 = (o2 == null) ? "" : o2.toString(); // NOI18N

            return (s1).compareToIgnoreCase(s2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    public static class FlurstueckBean {

        //~ Instance fields ----------------------------------------------------

        private final String alkisId;
        private final String gemarkung;
        private final String flur;
        private final String nummer;
        private final String lage;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckBean object.
         *
         * @param  flurstueck  DOCUMENT ME!
         */
        public FlurstueckBean(final CidsBean flurstueck) {
            this.alkisId = (String)flurstueck.getProperty("alkis_id");
            this.gemarkung = (String)flurstueck.getProperty("gemarkung");
            this.flur = (String)flurstueck.getProperty("flur");
            final String nenner = (String)flurstueck.getProperty("fstck_nenner");
            this.nummer = (String)flurstueck.getProperty("fstck_zaehler") + ((nenner != null) ? ("/"
                                + nenner) : "");
            final Collection<CidsBean> adressen = flurstueck.getBeanCollectionProperty("adressen");
            if (adressen.isEmpty()) {
                this.lage = "";
            } else {
                final Set<String> strassen = new HashSet<String>();
                final Map<String, Collection<String>> hausnummernMap = new HashMap<String, Collection<String>>();
                for (final CidsBean adresse : adressen) {
                    final String strasse = (String)adresse.getProperty("strasse");
                    final String hausnummer = (String)adresse.getProperty("nummer");
                    strassen.add(strasse);
                    if (hausnummer != null) {
                        if (!hausnummernMap.containsKey(strasse)) {
                            hausnummernMap.put(strasse, new ArrayList<String>());
                        }
                        final List<String> hausnummern = (List)hausnummernMap.get(strasse);
                        hausnummern.add(hausnummer);
                    }
                }
                final String strasse = strassen.iterator().next();
                final StringBuffer sb = new StringBuffer(strasse);
                boolean first = true;
                final List<String> hausnummern = (List)hausnummernMap.get(strasse);
                if (hausnummern != null) {
                    Collections.sort(hausnummern);
                    sb.append(" ");
                    for (final String hausnummer : hausnummern) {
                        if (!first) {
                            sb.append(", ");
                        }
                        sb.append(hausnummer);
                        first = false;
                    }
                }
                if (strassen.size() > 1) {
                    sb.append(" u.a.");
                }
                this.lage = sb.toString();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class BaBeException extends Exception {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BaBeException object.
         *
         * @param  message  DOCUMENT ME!
         */
        public BaBeException(final String message) {
            super(message);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TxtDownload extends AbstractDownload {

        //~ Instance fields ----------------------------------------------------

        private final String content;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TxtDownload object.
         *
         * @param  content    DOCUMENT ME!
         * @param  directory  DOCUMENT ME!
         * @param  title      DOCUMENT ME!
         * @param  filename   DOCUMENT ME!
         * @param  extension  DOCUMENT ME!
         */
        public TxtDownload(
                final String content,
                final String directory,
                final String title,
                final String filename,
                final String extension) {
            this.content = content;
            this.directory = directory;
            this.title = title;

            status = State.WAITING;

            determineDestinationFile(filename, extension);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (status != State.WAITING) {
                return;
            }

            status = State.RUNNING;

            stateChanged();

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileToSaveTo, false));
                writer.write(content);
            } catch (Exception ex) {
                error(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        log.warn("Exception occured while closing file.", e);
                    }
                }
            }

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }
        }
    }
}
