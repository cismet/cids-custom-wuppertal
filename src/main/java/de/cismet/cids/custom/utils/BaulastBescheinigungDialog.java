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

import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;
import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisBuchungsblattRenderer;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.alkis.SOAPAccessProvider;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

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

    private static final Map<CidsBean, Buchungsblatt> buchungsblattCache = new HashMap<CidsBean, Buchungsblatt>();

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
    private final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap =
        new HashMap<CidsBean, Set<CidsBean>>();
    private final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap =
        new HashMap<CidsBean, Set<CidsBean>>();

    private SwingWorker worker;
    private Collection<CidsBean> flurstuecke;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        protokollPane = new de.cismet.commons.gui.progress.BusyLoggingTextPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(500, 300));
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        protokollPane.setEditable(false);
        jScrollPane1.setViewportView(protokollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(jScrollPane1, gridBagConstraints);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(jPanel2, gridBagConstraints);

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
        flurstueckeToBaulastenBelastetMap.clear();
        flurstueckeToBaulastenBeguenstigtMap.clear();

        jTextField2.setText(new SimpleDateFormat("yy").format(new Date()) + "-");

        this.flurstuecke = flurstueckeList;

        prepareDownload(flurstueckeList);

        StaticSwingTools.showDialog(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        doDownload();
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     */
    public void prepareDownload(final List<CidsBean> flurstuecke) {
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

            worker = new SwingWorker<Collection<ProductGroupAmount>, Void>() {

                    @Override
                    protected Collection<ProductGroupAmount> doInBackground() throws Exception {
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

                        final Map<String, Set<CidsBean>> grundstueckeToFlurstueckeMap =
                            createGrundstueckeToFlurstueckeMap(flurstuecke);

                        fillFlurstueckeToBaulastenMaps(
                            flurstuecke,
                            flurstueckeToBaulastenBelastetMap,
                            flurstueckeToBaulastenBeguenstigtMap);

                        final Collection<ProductGroupAmount> prodAmounts = createBilling(
                                grundstueckeToFlurstueckeMap,
                                flurstueckeToBaulastenBelastetMap,
                                flurstueckeToBaulastenBeguenstigtMap);
                        return prodAmounts;
                    }

                    @Override
                    protected void done() {
                        try {
                            prodAmounts.addAll(get());
                            jButton1.setEnabled(true);
                        } catch (final Exception ex) {
                            if (ex.getCause() instanceof BaBeException) {
                                addError(ex.getCause().getMessage());
                            } else {
                                LOG.error(ex, ex);
                                addError("Es ist ein Fehler aufgetreten: " + ex.getMessage());
                            }
                        } finally {
                            protokollPane.setBusy(false);
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
                            "blab_besch",
                            "no.yet",
                            (Geometry)null,
                            prodAmounts.toArray(new ProductGroupAmount[0]))) {
                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(BaulastBescheinigungDialog.this)) {
                    final Download download = generateDownload(
                            projectdescription,
                            jobnumber,
                            flurstuecke,
                            flurstueckeToBaulastenBelastetMap,
                            flurstueckeToBaulastenBeguenstigtMap);
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
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        setVisible(false);
        if ((worker != null) && !worker.isDone()) {
            worker.cancel(true);
        }
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

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
//        final MetaClass mcFlurstueck = ClassCacheMultiple.getMetaClass(
//                "WUNDA_BLAU",
//                "alb_flurstueck_kicker");

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
                            "Zu den an gegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltene Baulasten im Bearbeitungszugriff befinden.");
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

            boolean teileigentumAlreadyCounted = false;

            for (final CidsBean buchungsblattBean : buchungsblaetter) {
                if (buchungsblattBean != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    addMessage(" * analysiere Buchunbgsblatt " + buchungsblattBean + " ...");
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

                            if ("Wohnungs-/Teileigentum".equals(buchungsart)) {
                                if (teileigentumAlreadyCounted) {
                                    addMessage("   -> ignoriere " + key + " aufgrund der Buchungsart ("
                                                + buchungsart
                                                + ")");
                                    continue;
                                } else {
                                    teileigentumAlreadyCounted = true;
                                }
                            }

                            if (!grundstueckeToFlurstueckeMap.containsKey(key)) {
                                grundstueckeToFlurstueckeMap.put(key, new HashSet<CidsBean>());
                            }

                            final String buchungsartSuffix = "Grundstück".equals(buchungsart)
                                ? "" : (" (" + buchungsart + ")");
                            addMessage("   => füge Flurstück " + flurstueckBean + " zu Grundstück " + key + "hinzu"
                                        + buchungsartSuffix);
                            grundstueckeToFlurstueckeMap.get(key).add(flurstueckBean);
                        }
                    }
                }
            }
        }
        return grundstueckeToFlurstueckeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bescheinigungsGruppe  DOCUMENT ME!
     * @param   jobname               DOCUMENT ME!
     * @param   number                projectname DOCUMENT ME!
     * @param   max                   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Download createBescheinigungPdf(final BescheinigungsGruppeBean bescheinigungsGruppe,
            final String jobname,
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

        final String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/baulastbescheinigung.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                "Bescheinigung "
                        + dateString
                        + " "
                        + number
                        + "/"
                        + max,
                "bescheinigung_"
                        + dateString
                        + "_"
                        + number);

        return download;
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
            if (numOfBaulasten == 0) {
                addMessage(" * Grundstück " + key + " => Negativ-Bescheinigung");
                anzahlNegativ++;
            } else if (numOfBaulasten == 1) {
                addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für eine Baulast (" + baulastenString
                            + ")");
                anzahlPositiv1++;
            } else if (numOfBaulasten == 2) {
                addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für zwei Baulasten (" + baulastenString
                            + ")");
                anzahlPositiv2++;
            } else {
                addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für drei oder mehr Baulasten ("
                            + baulastenString + ")");
                anzahlPositiv3++;
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
            buchungsblatt = buchungsblattCache.get(buchungsblattBean);
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

                    buchungsblattCache.put(buchungsblattBean, buchungsblatt);
                }
            }
        }

        return buchungsblatt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   projectdescription                    DOCUMENT ME!
     * @param   jobnumber                             DOCUMENT ME!
     * @param   flurstuecke                           DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public Download generateDownload(final String projectdescription,
            final String jobnumber,
            final Collection<CidsBean> flurstuecke,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenBeguenstigtMap) throws Exception {
        final String jobname = DownloadManagerDialog.getInstance().getJobName();
        final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
            new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                @Override
                public Collection<? extends Download> fetchDownloads() throws Exception {
                    final Collection<Download> downloads = new ArrayList<Download>();
                    try {
                        // Bescheinigungsgruppen ermitteln
                        final Set<BescheinigungsGruppeBean> bescheinigungsgruppen = createBescheinigungsGruppen(
                                flurstuecke,
                                flurstueckeToBaulastenBeguenstigtMap,
                                flurstueckeToBaulastenBelastetMap);

                        downloads.add(new TxtDownload(
                                protokollPane.getText(),
                                jobname,
                                "Baulastbescheinigung-Protokoll",
                                "baulastbescheinigung_protokoll",
                                ".txt"));

                        if (bescheinigungsgruppen != null) {
                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = bescheinigungsgruppen.size();
                            for (final BescheinigungsGruppeBean bescheinigungsGruppe : bescheinigungsgruppen) {
                                downloads.add(createBescheinigungPdf(bescheinigungsGruppe, jobname, ++number, max));
                            }
                        }

                        // alle Baulasten ermitteln
                        final Set<CidsBean> allBaulasten = new HashSet<CidsBean>();
                        for (final Set<CidsBean> baulasten : flurstueckeToBaulastenBeguenstigtMap.values()) {
                            allBaulasten.addAll(baulasten);
                        }
                        for (final Set<CidsBean> baulasten : flurstueckeToBaulastenBelastetMap.values()) {
                            allBaulasten.addAll(baulasten);
                        }
                        if (!allBaulasten.isEmpty()) {
                            // Download: Bericht für alle Baulasten
                            downloads.addAll(BaulastenReportGenerator.generateRasterDownloads(
                                    jobname,
                                    allBaulasten,
                                    jobnumber,
                                    projectdescription));
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
    public static class BaulastBean {

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
        BaulastBean(final CidsBean baulast) {
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

        private final Set<FlurstueckBean> flurstuecke;
        private final Set<BaulastBean> baulastenBeguenstigt;
        private final Set<BaulastBean> baulastenBelastet;

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
            this.flurstuecke = new HashSet<FlurstueckBean>();
            for (final CidsBean flurstueck : flurstuecke) {
                this.flurstuecke.add(new FlurstueckBean(flurstueck));
            }
            this.baulastenBeguenstigt = new HashSet<BaulastBean>();
            for (final CidsBean baulastBeguenstigt : baulastenBeguenstigt) {
                this.baulastenBeguenstigt.add(new BaulastBean(baulastBeguenstigt));
            }
            this.baulastenBelastet = new HashSet<BaulastBean>();
            for (final CidsBean baulastBelastet : baulastenBelastet) {
                this.baulastenBelastet.add(new BaulastBean(baulastBelastet));
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer();

            final List<BaulastBean> sortedBeguenstigt = new ArrayList<BaulastBean>(baulastenBeguenstigt);
            Collections.sort(sortedBeguenstigt, new BaulastBeanComparator());

            boolean first = true;
            for (final BaulastBean baulast : sortedBeguenstigt) {
                if (!first) {
                    sb.append(", ");
                    first = false;
                }
                sb.append(baulast.toString());
            }

            sb.append("|");

            final List<BaulastBean> sortedBelastet = new ArrayList<BaulastBean>(baulastenBelastet);
            Collections.sort(sortedBelastet, new BaulastBeanComparator());

            first = true;
            for (final BaulastBean baulast : sortedBelastet) {
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
    static class BaulastBeanComparator implements Comparator<BaulastBean> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final BaulastBean o1, final BaulastBean o2) {
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

        private final String gemarkung;
        private final String flur;
        private final String nummer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FlurstueckBean object.
         *
         * @param  flurstueck  DOCUMENT ME!
         */
        public FlurstueckBean(final CidsBean flurstueck) {
            this.gemarkung = (String)flurstueck.getProperty("gemarkung");
            this.flur = (String)flurstueck.getProperty("flur");
            final String nenner = (String)flurstueck.getProperty("fstck_nenner");
            this.nummer = (String)flurstueck.getProperty("fstck_zaehler") + ((nenner != null) ? ("/"
                                + nenner) : "");
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
