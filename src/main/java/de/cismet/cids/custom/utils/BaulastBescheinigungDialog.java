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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.LandParcel;

import org.apache.log4j.Logger;

import java.awt.Component;

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
import de.cismet.cids.custom.utils.alkis.SOAPAccessProvider;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungFreigabeInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungAnfrageServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.commons.gui.progress.BusyLoggingTextPane;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastBescheinigungDialog extends javax.swing.JDialog implements CidsServerMessageNotifierListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final Logger LOG = Logger.getLogger(BaulastBescheinigungDialog.class);

    private static BaulastBescheinigungDialog INSTANCE;

    static final Map<CidsBean, Buchungsblatt> BUCHUNGSBLATT_CACHE = new HashMap<CidsBean, Buchungsblatt>();

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
    private final Collection<BerechtigungspruefungBescheinigungGruppeInfo> bescheinigungsgruppen =
        new HashSet<BerechtigungspruefungBescheinigungGruppeInfo>();

    private SwingWorker worker;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.utils.BerechtigungspruefungAnfragePanel berechtigungspruefungAnfragePanel1;
    private de.cismet.commons.gui.progress.BusyStatusPanel busyStatusPanel1;
    private javax.swing.JDialog diaFreigegeben;
    private javax.swing.JDialog diaStorniert;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea2;
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

        diaFreigegeben = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        diaStorniert = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        busyStatusPanel1 = new de.cismet.commons.gui.progress.BusyStatusPanel();
        berechtigungspruefungAnfragePanel1 = new de.cismet.cids.custom.utils.BerechtigungspruefungAnfragePanel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        protokollPane = new de.cismet.commons.gui.progress.BusyLoggingTextPane();

        diaFreigegeben.setTitle(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.diaFreigegeben.title")); // NOI18N
        diaFreigegeben.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jButton4.text")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel5.add(jButton4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jLabel6.text")); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel5.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        diaFreigegeben.getContentPane().add(jPanel5, gridBagConstraints);

        diaStorniert.setTitle(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.diaStorniert.title")); // NOI18N
        diaStorniert.setMinimumSize(new java.awt.Dimension(400, 300));
        diaStorniert.setModal(true);
        diaStorniert.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        jPanel6.add(jButton5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jLabel8, gridBagConstraints);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel6.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        diaStorniert.getContentPane().add(jPanel6, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel1, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(500, 27));
        jPanel4.setPreferredSize(new java.awt.Dimension(500, 27));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel9.setMinimumSize(new java.awt.Dimension(400, 27));
        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(400, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel9, gridBagConstraints);

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel7, gridBagConstraints);

        busyStatusPanel1.setStatusMessage(org.openide.util.NbBundle.getMessage(
                BaulastBescheinigungDialog.class,
                "BaulastBescheinigungDialog.busyStatusPanel1.statusMessage")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(busyStatusPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel4, gridBagConstraints);

        berechtigungspruefungAnfragePanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BaulastBescheinigungDialog.class,
                    "BaulastBescheinigungDialog.berechtigungspruefungAnfragePanel1.border.title"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(berechtigungspruefungAnfragePanel1, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BaulastBescheinigungDialog.class,
                    "BaulastBescheinigungDialog.jPanel8.border.title"))); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        boolean berechtigungspruefung = true;
        try {
            berechtigungspruefung = SessionManager.getConnection()
                        .hasConfigAttr(
                                SessionManager.getSession().getUser(),
                                "berechtigungspruefung_baulastbescheinigung");
        } catch (final ConnectionException ex) {
            LOG.info("could not check config attr", ex);
        }
        if (berechtigungspruefung) {
            berechtigungspruefungAnfragePanel1.setProdukt("baulastbescheinigung");
            berechtigungspruefungAnfragePanel1.setVisible(true);
        } else {
            berechtigungspruefungAnfragePanel1.setVisible(false);
        }
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
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        try {
            if (BillingPopup.doBilling(
                            "blab_be",
                            "no.yet",
                            (Geometry)null,
                            prodAmounts.toArray(new ProductGroupAmount[0]))) {
                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                BaulastBescheinigungDialog.this)) {
                    final String berechnung = BillingPopup.getInstance().getBerechnungsProtokoll();
                    if ((berechnung != null) && !berechnung.trim().isEmpty()) {
                        addMessage("\n===\n\nGebührenberechnung:\n");
                        addMessage(berechnung);
                    }

                    final String produktbezeichnung = jTextField1.getText();
                    final String auftragsnummer = jTextField2.getText();

                    final BerechtigungspruefungBescheinigungInfo bescheinigungsInfo =
                        new BerechtigungspruefungBescheinigungInfo(new Date(),
                            new HashSet<BerechtigungspruefungBescheinigungGruppeInfo>(bescheinigungsgruppen));

                    final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo =
                        new BerechtigungspruefungBescheinigungDownloadInfo(
                            DownloadManagerDialog.getInstance().getJobName(),
                            produktbezeichnung,
                            auftragsnummer,
                            protokollPane.getText(),
                            bescheinigungsInfo);
                    if (SessionManager.getConnection().hasConfigAttr(
                                    SessionManager.getSession().getUser(),
                                    "berechtigungspruefung_baulastbescheinigung")) {
                        berechtigungspruefungAnfragePanel1.doAnfrage(
                            downloadInfo,
                            new BerechtigungspruefungAnfragePanel.Callback() {

                                @Override
                                public void callback(final String anfrageSchluessel) {
                                    JOptionPane.showMessageDialog(
                                        StaticSwingTools.getParentFrame(
                                            BaulastBescheinigungDialog.this),
                                        "<html>Ihre Anfrage wird unter dem Schlüssel \""
                                                + anfrageSchluessel
                                                + "\" bearbeitet."
                                                + "<br/>Sie werden benachrichtigt, sobald sie bearbeitet wurde.",
                                        "Ihre Anfrage wird bearbeitet",
                                        JOptionPane.PLAIN_MESSAGE);
                                }
                            });
                    } else {
                        BaulastBescheinigungUtils.doDownload(downloadInfo);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            // TODO SHOW ERROR
        }

        setVisible(false);
    } //GEN-LAST:event_jButton1ActionPerformed

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
        jPanel8.setVisible(checkProtokollPane != null);

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
                        final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap =
                            new HashMap<CidsBean, Collection<CidsBean>>();
                        final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap =
                            new HashMap<CidsBean, Collection<CidsBean>>();

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

                        final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap =
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
                                createFlurstueckeToGrundstueckeMap(flurstuecke, grundstueckeToFlurstueckeMap),
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
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap) throws Exception {
        addMessage("\n===");

        // belastete Baulasten pro Flurstück
        flurstueckeToBaulastenBelastetMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, true));

        // begünstigte Baulasten pro Flurstück
        flurstueckeToBaulastenBeguenstigtMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, false));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  downloadInfo produktbezeichnung DOCUMENT ME!
     */
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
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        diaFreigegeben.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        diaStorniert.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton5ActionPerformed

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
     * @param   flurstueckeToGrundstueckeMap          flurstuecke DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Set<BerechtigungspruefungBescheinigungGruppeInfo> createBescheinigungsGruppen(
            final Map<CidsBean, Collection<String>> flurstueckeToGrundstueckeMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap) {
        final Map<String, BerechtigungspruefungBescheinigungGruppeInfo> gruppeMap =
            new HashMap<String, BerechtigungspruefungBescheinigungGruppeInfo>();

        for (final CidsBean flurstueck : flurstueckeToGrundstueckeMap.keySet()) {
            final Collection<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(flurstueck);
            final Collection<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
            final BerechtigungspruefungBescheinigungGruppeInfo newGruppe = BaulastBescheinigungUtils.createGruppeInfo(
                    baulastenBeguenstigt,
                    baulastenBelastet);
            final String gruppeKey = newGruppe.toString();
            if (!gruppeMap.containsKey(gruppeKey)) {
                gruppeMap.put(gruppeKey, newGruppe);
            }
            final BerechtigungspruefungBescheinigungGruppeInfo gruppe = gruppeMap.get(gruppeKey);
            gruppe.getFlurstuecke()
                    .add(BaulastBescheinigungUtils.createFlurstueckInfo(
                            flurstueck,
                            flurstueckeToGrundstueckeMap.get(flurstueck)));
        }

        final Set<BerechtigungspruefungBescheinigungGruppeInfo> bescheinigungsgruppen =
            new HashSet<BerechtigungspruefungBescheinigungGruppeInfo>(
                gruppeMap.values());

        addMessage("Anzahl Bescheinigungsgruppen: " + bescheinigungsgruppen.size());
        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppe : bescheinigungsgruppen) {
            addMessage(" * " + gruppe.toString());
        }
        return bescheinigungsgruppen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                   DOCUMENT ME!
     * @param   grundstueckeToFlurstueckeMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Map<CidsBean, Collection<String>> createFlurstueckeToGrundstueckeMap(
            final Collection<CidsBean> flurstuecke,
            final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap) {
        final HashMap<CidsBean, Collection<String>> flurstueckeToGrundstueckeMap =
            new HashMap<CidsBean, Collection<String>>();

        for (final String grundstueck : grundstueckeToFlurstueckeMap.keySet()) {
            for (final CidsBean flurstueck : flurstuecke) {
                if (!flurstueckeToGrundstueckeMap.containsKey(flurstueck)) {
                    flurstueckeToGrundstueckeMap.put(flurstueck, new HashSet<String>());
                }
                final Collection<String> grundstuecke = flurstueckeToGrundstueckeMap.get(flurstueck);
                grundstuecke.add(grundstueck);
            }
        }
        return flurstueckeToGrundstueckeMap;
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
    private static Map<String, Collection<CidsBean>> createGrundstueckeToFlurstueckeMap(
            final Collection<CidsBean> flurstuecke) throws Exception {
        addMessage("\n===\n\nZuordnung der Flurstücke zu Grundstücken...");

        final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap =
            new HashMap<String, Collection<CidsBean>>();

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

        return grundstueckeToFlurstueckeMap;
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
            final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap) {
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

                final Collection<CidsBean> flurstuecke = grundstueckeToFlurstueckeMap.get(key);

                final Set<CidsBean> baulasten = new HashSet<CidsBean>();
                for (final CidsBean flurstueck : flurstuecke) {
                    final Collection<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
                    final Collection<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(
                            flurstueck);
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
                    case 0: {
                        addMessage(" * Grundstück " + key + " => Negativ-Bescheinigung");
                        anzahlNegativ++;
                        break;
                    }
                    case 1: {
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für eine Baulast ("
                                    + baulastenString
                                    + ")");
                        anzahlPositiv1++;
                        break;
                    }
                    case 2: {
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für zwei Baulasten ("
                                    + baulastenString
                                    + ")");
                        anzahlPositiv2++;
                        break;
                    }
                    default: {
                        addMessage(" * Grundstück " + key + " => Positiv-Bescheinigung für drei oder mehr Baulasten ("
                                    + baulastenString + ")");
                        anzahlPositiv3++;
                        break;
                    }
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

    @Override
    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
        try {
            final Map<String, BerechtigungspruefungFreigabeInfo> freigabeInfoMap = (Map)
                new ObjectMapper().readValue((String)event.getMessage().getContent(),
                    new TypeReference<Map<String, BerechtigungspruefungFreigabeInfo<BerechtigungspruefungBescheinigungDownloadInfo>>>() {
                    });

            for (final String schluessel : freigabeInfoMap.keySet()) {
                final BerechtigungspruefungFreigabeInfo freigabeInfo = freigabeInfoMap.get(schluessel);

                if (freigabeInfo.getFreigegeben()) {
                    jLabel6.setText(String.format(
                            org.openide.util.NbBundle.getMessage(
                                BaulastBescheinigungDialog.class,
                                "BaulastBescheinigungDialog.jLabel6.text"),
                            schluessel));
                    diaFreigegeben.pack();
                    StaticSwingTools.showDialog(diaFreigegeben);

                    BaulastBescheinigungUtils.doDownload((BerechtigungspruefungBescheinigungDownloadInfo)
                        freigabeInfo.getBerechtigungspruefungDownloadInfo());
                } else {
                    jLabel8.setText(String.format(
                            org.openide.util.NbBundle.getMessage(
                                BaulastBescheinigungDialog.class,
                                "BaulastBescheinigungDialog.jLabel8.text"),
                            schluessel));
                    jTextArea2.setText(freigabeInfo.getKommentar());
                    diaStorniert.pack();
                    StaticSwingTools.showDialog(diaStorniert);
                }
                try {
                    SessionManager.getProxy()
                            .executeTask(
                                BerechtigungspruefungAnfrageServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                null,
                                new ServerActionParameter<String>(
                                    BerechtigungspruefungAnfrageServerAction.ParameterType.ABGEHOLT.toString(),
                                    schluessel));
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                }
            }
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

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
}
