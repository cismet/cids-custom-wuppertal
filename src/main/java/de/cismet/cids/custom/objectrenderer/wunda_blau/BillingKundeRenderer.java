/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;

import java.io.UnsupportedEncodingException;

import java.math.BigDecimal;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.PrintBillingReportForCustomer;
import de.cismet.cids.custom.objectrenderer.utils.billing.Usage;
import de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel;
import de.cismet.cids.custom.reports.wunda_blau.BillingBuchungsbelegReport;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBillingSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBillingSearchStatement.Kostenart;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingKundeRenderer extends javax.swing.JPanel implements CidsBeanRenderer, TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingKundeRenderer.class);
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    // column headers
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Geschäftsbuchnummer",
            "Projektbezeichnung",
            "Verwendung",
            "Produkt",
            "Preis (netto)",
            "MwSt-Satz",
            "Datum",
            "Benutzer"
        };
    // property names
    private static final String[] AGR_PROPERTY_NAMES = new String[] {
            "geschaeftsbuchnummer",
            "projektbezeichnung",
            "verwendungskey",
            "produktbezeichnung",
            "netto_summe",
            "mwst_satz",
            "ts",
            "angelegt_durch.name"
        };

    //~ Instance fields --------------------------------------------------------

    private BillingTableModel tableModel;
    private CidsBean cidsBean;
    private String title;
    private List<CidsBean> filteredBuchungen;
    private Date[] fromDate_tillDate;
    private BigDecimal totalSum;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blblBusy;
    private javax.swing.JButton btnBuchungsbeleg;
    private javax.swing.JButton btnRechnungsanlage;
    private javax.swing.JButton btnShowResults;
    private javax.swing.JComboBox cboBenutzer;
    private javax.swing.JCheckBox cboBillDownloads;
    private javax.swing.JCheckBox cboKostenfrei;
    private javax.swing.JCheckBox cboKostenpflichtig;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFilterResult;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel pnlBusyLable;
    private javax.swing.JPanel pnlFilterResults;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlKostenart;
    private javax.swing.JPanel pnlTable;
    private de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel pnlTimeFilters;
    private de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel pnlVerwendungszweck;
    private de.cismet.tools.gui.SemiRoundedPanel smiplFilter;
    private de.cismet.tools.gui.SemiRoundedPanel smiplTable;
    private javax.swing.JTable tblBillings;
    private javax.swing.JTextField txtGeschaeftsbuchnummer;
    private javax.swing.JTextField txtProjekt;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KundenRenderer.
     */
    public BillingKundeRenderer() {
        this(true);
    }

    /**
     * Creates a new KundeRenderer object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public BillingKundeRenderer(final boolean editable) {
        initComponents();
        tableModel = new BillingTableModel(new Object[0][], AGR_COMLUMN_NAMES);
        tblBillings.setModel(tableModel);
        setFilterActionInExternalPanels();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  request  DOCUMENT ME!
     */
    private void doDownload(final String request) {
        try {
            final URL url = new URL(request);
            String filename = "alkis_druck";
            try {
                final Map<String, String> urlQuery = splitQuery(url);
                filename = urlQuery.get("product") + "." + urlQuery.get("landparcel").replace("/", "--");
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }

            final HttpDownload download = new HttpDownload(
                    url,
                    "",
                    DownloadManagerDialog.getJobname(),
                    "ALKIS-Druck",
                    filename,
                    ".pdf");
            DownloadManager.instance().add(download);
        } catch (MalformedURLException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  UnsupportedEncodingException  DOCUMENT ME!
     */
    private static Map<String, String> splitQuery(final URL url) throws UnsupportedEncodingException {
        final Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        final String query = url.getQuery();
        final String[] pairs = query.split("&");
        for (final String pair : pairs) {
            final int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        pnlFilters = new javax.swing.JPanel();
        pnlTimeFilters = new de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtGeschaeftsbuchnummer = new javax.swing.JTextField();
        txtProjekt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboBenutzer = new javax.swing.JComboBox();
        pnlKostenart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cboKostenfrei = new javax.swing.JCheckBox();
        cboKostenpflichtig = new javax.swing.JCheckBox();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlVerwendungszweck = new de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel();
        btnShowResults = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnBuchungsbeleg = new javax.swing.JButton();
        btnRechnungsanlage = new javax.swing.JButton();
        cboBillDownloads = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        smiplFilter = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        smiplTable = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        pnlTable = new javax.swing.JPanel();
        pnlFilterResults = new javax.swing.JPanel();
        lblFilterResult = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBillings = new javax.swing.JTable();
        pnlBusyLable = new javax.swing.JPanel();
        blblBusy = new org.jdesktop.swingx.JXBusyLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        setLayout(new java.awt.GridBagLayout());

        pnlFilters.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pnlFilters.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlFilters.add(pnlTimeFilters, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 3, 6);
        jPanel3.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 3, 6);
        jPanel3.add(jLabel5, gridBagConstraints);

        txtGeschaeftsbuchnummer.setText(org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.txtGeschaeftsbuchnummer.text"));        // NOI18N
        txtGeschaeftsbuchnummer.setToolTipText(org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.txtGeschaeftsbuchnummer.toolTipText")); // NOI18N
        txtGeschaeftsbuchnummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGeschaeftsbuchnummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        jPanel3.add(txtGeschaeftsbuchnummer, gridBagConstraints);
        txtGeschaeftsbuchnummer.getDocument().addDocumentListener(new FilterBuchungenDocumentListener());

        txtProjekt.setText(org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.txtProjekt.text"));        // NOI18N
        txtProjekt.setToolTipText(org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.txtProjekt.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(txtProjekt, gridBagConstraints);
        txtProjekt.getDocument().addDocumentListener(new FilterBuchungenDocumentListener());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 8, 0, 6);
        jPanel3.add(jLabel4, gridBagConstraints);

        cboBenutzer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboBenutzerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        jPanel3.add(cboBenutzer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlFilters.add(jPanel3, gridBagConstraints);

        pnlKostenart.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BillingKundeRenderer.class,
                    "BillingKundeRenderer.pnlKostenart.border.title"))); // NOI18N
        pnlKostenart.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.PAGE_AXIS));

        cboKostenfrei.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenfrei,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.cboKostenfrei.text")); // NOI18N
        cboKostenfrei.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenfreiActionPerformed(evt);
                }
            });
        jPanel1.add(cboKostenfrei);

        cboKostenpflichtig.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenpflichtig,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.cboKostenpflichtig.text")); // NOI18N
        cboKostenpflichtig.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenpflichtigActionPerformed(evt);
                }
            });
        jPanel1.add(cboKostenpflichtig);

        pnlKostenart.add(jPanel1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        pnlFilters.add(pnlKostenart, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlFilters.add(filler5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        pnlFilters.add(pnlVerwendungszweck, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnShowResults,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.btnShowResults.text")); // NOI18N
        btnShowResults.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnShowResultsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 2);
        pnlFilters.add(btnShowResults, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(pnlFilters, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnBuchungsbeleg,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.btnBuchungsbeleg.text")); // NOI18N
        btnBuchungsbeleg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBuchungsbelegActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel2.add(btnBuchungsbeleg, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnRechnungsanlage,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.btnRechnungsanlage.text")); // NOI18N
        btnRechnungsanlage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRechnungsanlageActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel2.add(btnRechnungsanlage, gridBagConstraints);

        cboBillDownloads.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboBillDownloads,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.cboBillDownloads.text")); // NOI18N
        cboBillDownloads.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboBillDownloadsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        jPanel2.add(cboBillDownloads, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        add(jPanel2, gridBagConstraints);

        smiplFilter.setBackground(new java.awt.Color(51, 51, 51));
        smiplFilter.setLayout(new java.awt.FlowLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel2.text")); // NOI18N
        smiplFilter.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(smiplFilter, gridBagConstraints);

        smiplTable.setBackground(new java.awt.Color(51, 51, 51));
        smiplTable.setLayout(new java.awt.FlowLayout());

        jLabel1.setBackground(new java.awt.Color(51, 51, 51));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel1.text")); // NOI18N
        smiplTable.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(smiplTable, gridBagConstraints);

        pnlTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pnlTable.setLayout(new java.awt.CardLayout());

        pnlFilterResults.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFilterResult,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.lblFilterResult.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        pnlFilterResults.add(lblFilterResult, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(453, 275));

        tblBillings.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        tblBillings.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    tblBillingsMouseClicked(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    tblBillingsMouseExited(evt);
                }
            });
        tblBillings.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseMoved(final java.awt.event.MouseEvent evt) {
                    tblBillingsMouseMoved(evt);
                }
            });
        jScrollPane1.setViewportView(tblBillings);
        tblBillings.getColumnModel()
                .getColumn(0)
                .setHeaderValue(org.openide.util.NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.tblBillings.columnModel.title0")); // NOI18N
        tblBillings.getColumnModel()
                .getColumn(1)
                .setHeaderValue(org.openide.util.NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.tblBillings.columnModel.title1")); // NOI18N
        tblBillings.getColumnModel()
                .getColumn(2)
                .setHeaderValue(org.openide.util.NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.tblBillings.columnModel.title2")); // NOI18N
        tblBillings.getColumnModel()
                .getColumn(3)
                .setHeaderValue(org.openide.util.NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.tblBillings.columnModel.title3")); // NOI18N
        tblBillings.setDefaultRenderer(Usage.class, new UsageRenderer());
        tblBillings.setDefaultRenderer(DateRequestTuple.class, new DateRequestTupleRenderer());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        pnlFilterResults.add(jScrollPane1, gridBagConstraints);

        pnlTable.add(pnlFilterResults, "table");

        pnlBusyLable.setLayout(new java.awt.BorderLayout());

        blblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            blblBusy,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.blblBusy.text")); // NOI18N
        pnlBusyLable.add(blblBusy, java.awt.BorderLayout.CENTER);

        pnlTable.add(pnlBusyLable, "busy");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        add(pnlTable, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGeschaeftsbuchnummerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtGeschaeftsbuchnummerActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtGeschaeftsbuchnummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboKostenfreiActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboKostenfreiActionPerformed
        if (!cboKostenfrei.isSelected() && !cboKostenpflichtig.isSelected()) {
            cboKostenpflichtig.setSelected(true);
        }
        filterBuchungen_placeHolder();
    }                                                                                 //GEN-LAST:event_cboKostenfreiActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboKostenpflichtigActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboKostenpflichtigActionPerformed
        if (!cboKostenfrei.isSelected() && !cboKostenpflichtig.isSelected()) {
            cboKostenfrei.setSelected(true);
        }
        filterBuchungen_placeHolder();
    }                                                                                      //GEN-LAST:event_cboKostenpflichtigActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboBenutzerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboBenutzerActionPerformed
        filterBuchungen_placeHolder();
    }                                                                               //GEN-LAST:event_cboBenutzerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblBillingsMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblBillingsMouseClicked
        final int row = tblBillings.convertRowIndexToModel(tblBillings.getSelectedRow());
        final int column = tblBillings.convertColumnIndexToModel(tblBillings.getSelectedColumn());
        if (column == 6) {
            final DateRequestTuple bt = (DateRequestTuple)tblBillings.getModel().getValueAt(row, column);
            final String request = bt.getRequest();
            if ((request != null) && !request.equals("")) {
                doDownload(request);
            }
        }
    }                                                                           //GEN-LAST:event_tblBillingsMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblBillingsMouseMoved(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblBillingsMouseMoved
        final int row = tblBillings.convertRowIndexToModel(tblBillings.rowAtPoint(evt.getPoint()));
        final int column = tblBillings.convertColumnIndexToModel(tblBillings.columnAtPoint(evt.getPoint()));
        if (column == 6) {
            final DateRequestTuple bt = (DateRequestTuple)tblBillings.getModel().getValueAt(row, column);
            final String request = bt.getRequest();
            if ((request != null) && !request.equals("")) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }                                                                         //GEN-LAST:event_tblBillingsMouseMoved

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblBillingsMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblBillingsMouseExited
        setCursor(Cursor.getDefaultCursor());
    }                                                                          //GEN-LAST:event_tblBillingsMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnShowResultsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnShowResultsActionPerformed
        filterBuchungen(false);
    }                                                                                  //GEN-LAST:event_btnShowResultsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRechnungsanlageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRechnungsanlageActionPerformed
        new PrintBillingReportForCustomer(
            cidsBean,
            filteredBuchungen,
            fromDate_tillDate,
            true,
            cboBillDownloads.isSelected()).print();
    }                                                                                      //GEN-LAST:event_btnRechnungsanlageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBuchungsbelegActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBuchungsbelegActionPerformed
        new PrintBillingReportForCustomer(
            cidsBean,
            filteredBuchungen,
            fromDate_tillDate,
            false,
            cboBillDownloads.isSelected()).print();
    }                                                                                    //GEN-LAST:event_btnBuchungsbelegActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboBillDownloadsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboBillDownloadsActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cboBillDownloadsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kundeBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean kundeBean) {
        if (kundeBean != null) {
            cidsBean = kundeBean;

            cboBenutzer.setModel(new DefaultComboBoxModel());
            cboBenutzer.addItem("");
            for (final CidsBean benutzerBean : cidsBean.getBeanCollectionProperty("benutzer_n")) {
                cboBenutzer.addItem(benutzerBean);
            }

            filterBuchungen(true);
            this.title = NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.lblTitle.prefix") + " "
                        + kundeBean.toString();
            lblTitle.setText(this.title);
        }
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return String.valueOf(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
        this.title = NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.lblTitle.prefix") + title;
        lblTitle.setText(this.title);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
//        final JFrame frame = new JFrame();
//        frame.add(new KundeRenderer());
//        frame.pack();
//        frame.setVisible(true);
        DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "billing_kunde",
            15,
            "Foo",
            1280,
            1024);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  billingBeans  DOCUMENT ME!
     */
    private void fillBillingTable(final Collection<CidsBean> billingBeans) {
        final List<Object[]> tableData = new ArrayList<Object[]>();

        for (final CidsBean punktBean : billingBeans) {
            tableData.add(cidsBean2Row(punktBean));
        }
        tableModel = new BillingTableModel(tableData.toArray(new Object[tableData.size()][]), AGR_COMLUMN_NAMES);
        tblBillings.setModel(tableModel);
        if (!tableData.isEmpty()) {
            final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tblBillings.getModel());
            tblBillings.setRowSorter(sorter);
        } else {
            tblBillings.setRowSorter(null);
        }
    }

    /**
     * Extracts the date from a CidsBean into an Object[] -> table row. (Collection attributes are flatened to
     * comaseparated lists)
     *
     * @param   billingBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Object[] cidsBean2Row(final CidsBean billingBean) {
        if (billingBean != null) {
            final Object[] result = new Object[AGR_COMLUMN_NAMES.length];
            for (int i = 0; i < AGR_PROPERTY_NAMES.length; ++i) {
                final Object property = billingBean.getProperty(AGR_PROPERTY_NAMES[i]);
                if (AGR_PROPERTY_NAMES[i].equals("verwendungskey")) {
                    result[i] = VerwendungszweckPanel.getUsages().get((String)property);
                } else if (AGR_PROPERTY_NAMES[i].equals("ts")) {
                    final String request = (String)billingBean.getProperty("request");
                    final DateRequestTuple dateRequestTuple = new DateRequestTuple((Date)property, request);
                    result[i] = dateRequestTuple;
                } else {
                    final String propertyString;
                    propertyString = ObjectRendererUtils.propertyPrettyPrint(property);
                    result[i] = propertyString;
                }
            }
            return result;
        }
        return new Object[0];
    }

    /**
     * DOCUMENT ME!
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String generateFilterResultText(final Collection<CidsBean> billingBeans) {
        if (billingBeans.isEmpty()) {
            totalSum = new BigDecimal("0");
            return NbBundle.getMessage(
                    BillingKundeRenderer.class,
                    "BillingKundeRenderer.generateFilterResultText().noBillings");
        } else {
            final int amountBillings = billingBeans.size();
            fromDate_tillDate = pnlTimeFilters.chooseDates();
            final Date from = fromDate_tillDate[0];
            final Date till = fromDate_tillDate[1];
            totalSum = calculateTotalSumFromBillings(billingBeans);
            final NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

            final StringBuilder text = new StringBuilder(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings1"));
            text.append(euroFormatter.format(totalSum));
            text.append(NbBundle.getMessage(
                    BillingKundeRenderer.class,
                    "BillingKundeRenderer.generateFilterResultText().billings2"));
            if (amountBillings == 1) {
                text.append(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings3.oneBilling"));
            } else {
                text.append(amountBillings);
                text.append(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings3.moreBillings"));
            }
            if ((till == null) || from.equals(till)) {
                text.append(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings4.oneDate"));
                text.append(DATE_FORMAT.format(from));
                text.append(".");
            } else {
                text.append(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings4.twoDates1"));
                text.append(DATE_FORMAT.format(from));
                text.append(NbBundle.getMessage(
                        BillingKundeRenderer.class,
                        "BillingKundeRenderer.generateFilterResultText().billings4.twoDates2"));
                text.append(DATE_FORMAT.format(till));
                text.append(".");
            }
            return text.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private BigDecimal calculateTotalSumFromBillings(final Collection<CidsBean> billingBeans) {
        final HashMap<BigDecimal, BigDecimal> mwstSatz_nettoSum = new HashMap<BigDecimal, BigDecimal>();

        // calculate the netto sum of every mwst_satz
        for (final CidsBean billing : billingBeans) {
            BigDecimal netto_summe;
            BigDecimal mwst_satz;

            final Double netto_summe_bean = (Double)billing.getProperty("netto_summe");
            if (netto_summe_bean != null) {
                netto_summe = new BigDecimal(netto_summe_bean.toString());
            } else {
                netto_summe = new BigDecimal("0");
            }

            final Double mwst_satz_bean = (Double)billing.getProperty("mwst_satz");
            if (mwst_satz_bean != null) {
                mwst_satz = new BigDecimal(mwst_satz_bean.toString());
            } else {
                mwst_satz = new BigDecimal("0");
            }

            if (mwstSatz_nettoSum.containsKey(mwst_satz)) {
                final BigDecimal subtotal = mwstSatz_nettoSum.get(mwst_satz);
                final BigDecimal newSubtotal = subtotal.add(netto_summe);
                mwstSatz_nettoSum.put(mwst_satz, newSubtotal);
            } else {
                mwstSatz_nettoSum.put(mwst_satz, netto_summe);
            }
        }

        // calculate the brutto sum from each netto_sum
        totalSum = new BigDecimal("0");
        for (final BigDecimal mwst_satz : mwstSatz_nettoSum.keySet()) {
            final BigDecimal nettoSum = mwstSatz_nettoSum.get(mwst_satz);
            final BigDecimal percent = mwst_satz.divide(new BigDecimal("100"));
            final BigDecimal mwstValue = nettoSum.multiply(percent);
            BigDecimal bruttoSum = nettoSum.add(mwstValue);
            bruttoSum = bruttoSum.setScale(2, BigDecimal.ROUND_HALF_UP);
            totalSum = totalSum.add(bruttoSum);
        }
        return totalSum;
    }

    /**
     * a placeholder which can be used if the filterBuchungen has to be executed in a SwingWorker etc...
     */
    private void filterBuchungen_placeHolder() {
        // filterBuchungen();
    }

    /**
     * DOCUMENT ME!
     */
    private void filterBuchungen() {
        filterBuchungen(false);
    }

    /**
     * Runs a query to get the billings, which match the filters, and adds them to the table. If <code>
     * ignoreFilters</code> is true, then the filters will be ignored and the default values will be used.
     *
     * @param  ignoreFilters  DOCUMENT ME!
     */
    private void filterBuchungen(final boolean ignoreFilters) {
        final CidsBillingSearchStatement cidsBillingSearchStatement = new CidsBillingSearchStatement(
                SessionManager.getSession().getUser(),
                cidsBean.getMetaObject());
        if (!ignoreFilters) {
            cidsBillingSearchStatement.setGeschaeftsbuchnummer(txtGeschaeftsbuchnummer.getText());
            cidsBillingSearchStatement.setProjekt(txtProjekt.getText());

            final Object user = cboBenutzer.getSelectedItem();
            String userID = "";
            if (user instanceof CidsBean) {
                userID = ((CidsBean)user).getProperty("id").toString();
            }
            cidsBillingSearchStatement.setUserID(userID);

            cidsBillingSearchStatement.setVerwendungszweckKeys(
                pnlVerwendungszweck.createSelectedVerwendungszweckKeysStringArray());
            cidsBillingSearchStatement.setKostenart(chooseKostenart());
            fromDate_tillDate = pnlTimeFilters.chooseDates();
            cidsBillingSearchStatement.setFrom(fromDate_tillDate[0]);
            cidsBillingSearchStatement.setTill(fromDate_tillDate[1]);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Query to get the billings: " + cidsBillingSearchStatement.generateQuery());
        }

        blblBusy.setBusy(true);
        ((CardLayout)pnlTable.getLayout()).show(pnlTable, "busy");
        btnBuchungsbeleg.setEnabled(false);
        btnRechnungsanlage.setEnabled(false);
        btnShowResults.setEnabled(false);
        final SwingWorker<Collection<MetaObject>, Void> swingWorker = new SwingWorker<Collection<MetaObject>, Void>() {

                @Override
                protected Collection<MetaObject> doInBackground() throws Exception {
                    // return cidsBillingSearchStatement.performServerSearch();
                    return SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(), cidsBillingSearchStatement);
                }

                @Override
                protected void done() {
                    try {
                        final Collection<MetaObject> metaObjects = get();

                        if (metaObjects == null) {
                            LOG.error("Billing metaobjects was null.");
                        } else if (metaObjects.isEmpty()) {
                            LOG.info("No Billing metaobjects found.");
                            filteredBuchungen = new ArrayList<CidsBean>();
                            fillBillingTable(filteredBuchungen);
                            lblFilterResult.setText(generateFilterResultText(new ArrayList<CidsBean>()));
                        } else {
                            final List<CidsBean> billingBeans = new ArrayList<CidsBean>(metaObjects.size());
                            for (final MetaObject mo : metaObjects) {
                                billingBeans.add(mo.getBean());
                            }
                            filteredBuchungen = billingBeans;
                            fillBillingTable(billingBeans);
                            lblFilterResult.setText(generateFilterResultText(billingBeans));
                        }
                    } catch (InterruptedException ex) {
                        LOG.error("Error while filtering the billings.", ex);
                    } catch (ExecutionException ex) {
                        LOG.error("Error while filtering the billings.", ex);
                    } finally {
                        ((CardLayout)pnlTable.getLayout()).show(pnlTable, "table");
                        btnBuchungsbeleg.setEnabled(true);
                        btnRechnungsanlage.setEnabled(true);
                        btnShowResults.setEnabled(true);
                        blblBusy.setBusy(false);
                    }
                }
            };
        swingWorker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Kostenart chooseKostenart() {
        if (cboKostenfrei.isSelected() == cboKostenpflichtig.isSelected()) {
            return Kostenart.IGNORIEREN;
        } else if (cboKostenfrei.isSelected()) {
            return Kostenart.KOSTENFREI;
        } else {
            return Kostenart.KOSTENPFLICHTIG;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setFilterActionInExternalPanels() {
        final Action filterAction = new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    filterBuchungen_placeHolder();
                }
            };
        pnlTimeFilters.setFilterAction(filterAction);
        pnlVerwendungszweck.setFilterAction(filterAction);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DateRequestTuple implements Comparable<DateRequestTuple> {

        //~ Instance fields ----------------------------------------------------

        Date date;
        String request;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DateRequestTuple object.
         *
         * @param  date     DOCUMENT ME!
         * @param  request  DOCUMENT ME!
         */
        public DateRequestTuple(final Date date, final String request) {
            this.date = date;
            this.request = request;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private String getRequest() {
            return request;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Date getDate() {
            return date;
        }

        @Override
        public int compareTo(final DateRequestTuple o) {
            return date.compareTo(o.getDate());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static final class BillingTableModel extends DefaultTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PointTableModel object.
         *
         * @param  data    DOCUMENT ME!
         * @param  labels  DOCUMENT ME!
         */
        public BillingTableModel(final Object[][] data, final String[] labels) {
            super(data, labels);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   row     DOCUMENT ME!
         * @param   column  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public boolean isCellEditable(final int row, final int column) {
            return false;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   columnIndex  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return getValueAt(0, columnIndex).getClass();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class FilterBuchungenDocumentListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void insertUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void removeUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void changedUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class UsageRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  value  DOCUMENT ME!
         */
        @Override
        protected void setValue(final Object value) {
            if (value == null) {
                setText(ObjectRendererUtils.propertyPrettyPrint(value));
            } else {
                final Usage usage = (Usage)value;
                setText(ObjectRendererUtils.propertyPrettyPrint(usage.getKey()));
                setToolTipText(usage.getName());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DateRequestTupleRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  value  DOCUMENT ME!
         */
        @Override
        public void setValue(final Object value) {
            if (value == null) {
                setText(ObjectRendererUtils.propertyPrettyPrint(value));
            } else {
                final DateRequestTuple dateRequestTuple = (DateRequestTuple)value;
                final String formattedDate = DATE_FORMAT.format(dateRequestTuple.getDate());
                final String text = ObjectRendererUtils.propertyPrettyPrint(formattedDate);

                final String request = dateRequestTuple.getRequest();
                if ((request != null) && request.startsWith("http://")) {
                    // url is just a placeholder
                    setText("<html><a href=\"http://www.cismet.de\">" + text + "</a></html>");
                } else {
                    setText(text);
                }
            }
        }
    }
}
