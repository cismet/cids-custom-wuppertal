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
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;

import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.BillingCalculations;
import de.cismet.cids.custom.objectrenderer.utils.BillingRestrictedReportJButton;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.reports.wunda_blau.PrintBillingReportForCustomer;
import de.cismet.cids.custom.reports.wunda_blau.PrintStatisticsReport;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBillingSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBillingSearchStatement.Kostentyp;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingKundeAggregationRenderer extends javax.swing.JPanel implements RequestsFullSizeComponent,
    CidsBeanAggregationRenderer,
    TitleComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingKundeAggregationRenderer.class);
    // column headers
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Auswahl für Berichte",
            "Kundenname",
            "aggregierter Preis (brutto)",
            "kostenpflichtige Downloads",
        };
    private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    //~ Instance fields --------------------------------------------------------

    private Collection<CidsBean> cidsBeans = null;
    private Collection<Object[]> tableData;
    private List<CidsBean> filteredBillingBeans;
    private Date[] fromDate_tillDate;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blblBusy;
    private javax.swing.JButton btnBuchungsbeleg;
    private javax.swing.JButton btnGeschaeftsstatistik;
    private javax.swing.JButton btnRechnungsanlage;
    private javax.swing.JButton btnShowResults;
    private javax.swing.JCheckBox cboAbgerechnet;
    private javax.swing.JCheckBox cboHideFreeDownloadsBuchungsbeleg;
    private javax.swing.JCheckBox cboHideFreeDownloadsRechnungsanlage;
    private javax.swing.JCheckBox cboKostenfrei;
    private javax.swing.JCheckBox cboKostenpflichtig;
    private javax.swing.JCheckBox cboNichtAbgerechnet;
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
    private javax.swing.JLabel lblAgrTitle;
    private javax.swing.JLabel lblFilterResult;
    private javax.swing.JLabel lblResultHeader;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTitleString;
    private javax.swing.JPanel pnlBusyLable;
    private javax.swing.JPanel pnlFilterResults;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlTable;
    private de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel pnlTimeFilters;
    private de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel pnlVerwendungszweck;
    private de.cismet.tools.gui.SemiRoundedPanel smiplFilter;
    private de.cismet.tools.gui.SemiRoundedPanel smiplTable;
    private javax.swing.JTable tblCustomers;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BillingKundeAggregationRenderer.
     */
    public BillingKundeAggregationRenderer() {
        initComponents();
        setFilterActionInExternalPanels();
        final AggregatedBillingTableModel tableModel = new AggregatedBillingTableModel(new Object[0][],
                AGR_COMLUMN_NAMES);
        tblCustomers.setModel(tableModel);

        if (!ObjectRendererUtils.checkActionTag(BillingRestrictedReportJButton.BILLING_ACTION_TAG_REPORT)) {
            btnRechnungsanlage.setEnabled(false);
            cboHideFreeDownloadsRechnungsanlage.setEnabled(false);

            btnGeschaeftsstatistik.setEnabled(false);

            cboAbgerechnet.setVisible(false);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        panTitleString = new javax.swing.JPanel();
        lblAgrTitle = new javax.swing.JLabel();
        smiplFilter = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        smiplTable = new de.cismet.tools.gui.SemiRoundedPanel();
        lblResultHeader = new javax.swing.JLabel();
        pnlFilters = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnShowResults = new javax.swing.JButton();
        pnlVerwendungszweck = new de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel();
        jPanel1 = new javax.swing.JPanel();
        pnlTimeFilters = new de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        cboAbgerechnet = new javax.swing.JCheckBox();
        cboNichtAbgerechnet = new javax.swing.JCheckBox();
        cboKostenpflichtig = new javax.swing.JCheckBox();
        cboKostenfrei = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        cboHideFreeDownloadsBuchungsbeleg = new javax.swing.JCheckBox();
        cboHideFreeDownloadsRechnungsanlage = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        btnRechnungsanlage = new BillingRestrictedReportJButton();
        btnBuchungsbeleg = new javax.swing.JButton();
        btnGeschaeftsstatistik = new BillingRestrictedReportJButton();
        jPanel7 = new javax.swing.JPanel();
        lblFilterResult = new javax.swing.JLabel();
        pnlTable = new javax.swing.JPanel();
        pnlFilterResults = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomers = new javax.swing.JTable();
        pnlBusyLable = new javax.swing.JPanel();
        blblBusy = new org.jdesktop.swingx.JXBusyLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        panTitleString.setOpaque(false);
        panTitleString.setLayout(new java.awt.GridBagLayout());

        lblAgrTitle.setFont(new java.awt.Font("Tahoma", 1, 18));      // NOI18N
        lblAgrTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAgrTitle,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.lblAgrTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panTitleString.add(lblAgrTitle, gridBagConstraints);

        panTitle.add(panTitleString, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.GridBagLayout());

        smiplFilter.setBackground(new java.awt.Color(51, 51, 51));
        smiplFilter.setLayout(new java.awt.FlowLayout());

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.jLabel2.text")); // NOI18N
        smiplFilter.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(smiplFilter, gridBagConstraints);

        smiplTable.setBackground(new java.awt.Color(51, 51, 51));
        smiplTable.setLayout(new java.awt.FlowLayout());

        lblResultHeader.setBackground(new java.awt.Color(51, 51, 51));
        lblResultHeader.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblResultHeader,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.lblResultHeader.text")); // NOI18N
        smiplTable.add(lblResultHeader);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(smiplTable, gridBagConstraints);

        pnlFilters.setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnShowResults,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.btnShowResults.text")); // NOI18N
        btnShowResults.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnShowResultsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 2);
        jPanel8.add(btnShowResults, gridBagConstraints);

        pnlVerwendungszweck.initVerwendungszweckCheckBoxes(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel8.add(pnlVerwendungszweck, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(pnlTimeFilters, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridLayout(2, 2));

        cboAbgerechnet.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboAbgerechnet,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboAbgerechnet.text")); // NOI18N
        cboAbgerechnet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboAbgerechnetActionPerformed(evt);
                }
            });
        jPanel4.add(cboAbgerechnet);

        cboNichtAbgerechnet.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboNichtAbgerechnet,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboNichtAbgerechnet.text")); // NOI18N
        cboNichtAbgerechnet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboNichtAbgerechnetActionPerformed(evt);
                }
            });
        jPanel4.add(cboNichtAbgerechnet);

        cboKostenpflichtig.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenpflichtig,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboKostenpflichtig.text")); // NOI18N
        cboKostenpflichtig.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenpflichtigActionPerformed(evt);
                }
            });
        jPanel4.add(cboKostenpflichtig);

        cboKostenfrei.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenfrei,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboKostenfrei.text")); // NOI18N
        cboKostenfrei.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenfreiActionPerformed(evt);
                }
            });
        jPanel4.add(cboKostenfrei);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 2);
        jPanel1.add(jPanel3, gridBagConstraints);
        jPanel3.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        BillingKundeAggregationRenderer.class,
                        "BillingKundeAggregationRenderer.jPanel3.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlFilters.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(pnlFilters, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridLayout(2, 3, 10, 0));

        cboHideFreeDownloadsBuchungsbeleg.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboHideFreeDownloadsBuchungsbeleg,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboHideFreeDownloadsBuchungsbeleg.text")); // NOI18N
        jPanel2.add(cboHideFreeDownloadsBuchungsbeleg);

        cboHideFreeDownloadsRechnungsanlage.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboHideFreeDownloadsRechnungsanlage,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.cboHideFreeDownloadsRechnungsanlage.text")); // NOI18N
        jPanel2.add(cboHideFreeDownloadsRechnungsanlage);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.jLabel1.text")); // NOI18N
        jPanel2.add(jLabel1);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnRechnungsanlage,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.btnRechnungsanlage.text")); // NOI18N
        btnRechnungsanlage.setEnabled(false);
        btnRechnungsanlage.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRechnungsanlageActionPerformed(evt);
                }
            });
        jPanel2.add(btnRechnungsanlage);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnBuchungsbeleg,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.btnBuchungsbeleg.text")); // NOI18N
        btnBuchungsbeleg.setEnabled(false);
        btnBuchungsbeleg.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBuchungsbelegActionPerformed(evt);
                }
            });
        jPanel2.add(btnBuchungsbeleg);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnGeschaeftsstatistik,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.btnGeschaeftsstatistik.text")); // NOI18N
        btnGeschaeftsstatistik.setEnabled(false);
        btnGeschaeftsstatistik.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnGeschaeftsstatistikActionPerformed(evt);
                }
            });
        jPanel2.add(btnGeschaeftsstatistik);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel5, gridBagConstraints);

        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFilterResult,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.lblFilterResult.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 7);
        jPanel7.add(lblFilterResult, gridBagConstraints);

        pnlTable.setLayout(new java.awt.CardLayout());

        pnlFilterResults.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(453, 275));

        tblCustomers.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        tblCustomers.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    tblCustomersMouseClicked(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    tblCustomersMouseExited(evt);
                }
            });
        tblCustomers.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseMoved(final java.awt.event.MouseEvent evt) {
                    tblCustomersMouseMoved(evt);
                }
            });
        jScrollPane1.setViewportView(tblCustomers);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlFilterResults.add(jScrollPane1, gridBagConstraints);

        pnlTable.add(pnlFilterResults, "table");

        pnlBusyLable.setLayout(new java.awt.BorderLayout());

        blblBusy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            blblBusy,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.blblBusy.text")); // NOI18N
        pnlBusyLable.add(blblBusy, java.awt.BorderLayout.CENTER);

        pnlTable.add(pnlBusyLable, "busy");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 7, 5, 7);
        jPanel7.add(pnlTable, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel7, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblCustomersMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblCustomersMouseClicked
    }                                                                            //GEN-LAST:event_tblCustomersMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblCustomersMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblCustomersMouseExited
    }                                                                           //GEN-LAST:event_tblCustomersMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tblCustomersMouseMoved(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tblCustomersMouseMoved
    }                                                                          //GEN-LAST:event_tblCustomersMouseMoved

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnShowResultsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnShowResultsActionPerformed
        btnBuchungsbeleg.setEnabled(true);
        btnRechnungsanlage.setEnabled(true);
        btnGeschaeftsstatistik.setEnabled(true);
        filterBuchungen();
    }                                                                                  //GEN-LAST:event_btnShowResultsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBuchungsbelegActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBuchungsbelegActionPerformed
        final HashMap<CidsBean, Collection<CidsBean>> billingsOfCustomers = createBillingsOfCostumersForReports(evt);
        final Set<CidsBean> customers = billingsOfCustomers.keySet();
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnBuchungsbelegActionPerformed().dialog.message"),
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnBuchungsbelegActionPerformed().dialog.title"),
                JOptionPane.ERROR_MESSAGE);
        } else {
            for (final CidsBean kundeBean : customers) {
                new PrintBillingReportForCustomer(
                    kundeBean,
                    getSortedBillingBeans(billingsOfCustomers.get(kundeBean)),
                    fromDate_tillDate,
                    false,
                    this,
                    retrieveShowBillingWithoutCostInReport(evt),
                    new PrintBillingReportForCustomer.BillingDoneListener() {

                        @Override
                        public void billingDone(final boolean isDone) {
                            filterBuchungen();
                        }
                    }).print();
            }
        }
    } //GEN-LAST:event_btnBuchungsbelegActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getSortedBillingBeans(final Collection<CidsBean> billingBeans) {
        final List<CidsBean> sortedFilteredBuchungen = new ArrayList(billingBeans);
        Collections.sort(sortedFilteredBuchungen, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final Date d1 = (Date)o1.getProperty("ts");
                    final Date d2 = (Date)o2.getProperty("ts");
                    if ((d1 != null) && (d2 != null)) {
                        return d1.compareTo(d2);
                    } else if ((d1 == null) && (d2 != null)) {
                        return -1;
                    } else if ((d2 == null) && (d1 != null)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

        return sortedFilteredBuchungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRechnungsanlageActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRechnungsanlageActionPerformed
        final HashMap<CidsBean, Collection<CidsBean>> billingsOfCustomers = createBillingsOfCostumersForReports(evt);
        final Set<CidsBean> customers = billingsOfCustomers.keySet();
        if (customers.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnBuchungsbelegActionPerformed().dialog.message"),
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnBuchungsbelegActionPerformed().dialog.title"),
                JOptionPane.ERROR_MESSAGE);
        } else {
            for (final CidsBean kundeBean : customers) {
                final PrintBillingReportForCustomer printBillingReportForCustomer = new PrintBillingReportForCustomer(
                        kundeBean,
                        getSortedBillingBeans(billingsOfCustomers.get(kundeBean)),
                        fromDate_tillDate,
                        true,
                        this,
                        retrieveShowBillingWithoutCostInReport(evt),
                        null);

                // refresh the table after the report was successfully created and the billings were marked
                printBillingReportForCustomer.setDownloadFinishedObserver(printBillingReportForCustomer.new DownloadFinishedObserver() {

                        @Override
                        public void additionalFunctionalityIfDownloadCompleted() {
                            filterBuchungen();
                        }
                    });

                printBillingReportForCustomer.print();
            }
        }
    } //GEN-LAST:event_btnRechnungsanlageActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnGeschaeftsstatistikActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnGeschaeftsstatistikActionPerformed
        final Collection<CidsBean> billings = createBillingsForStatisticsReport();
        if (billings.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnGeschaeftsstatistikActionPerformed().dialog.message"),
                NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.btnGeschaeftsstatistikActionPerformed().dialog.title"),
                JOptionPane.ERROR_MESSAGE);
        } else {
            new PrintStatisticsReport(fromDate_tillDate, billings).print();
        }
    }                                                                                          //GEN-LAST:event_btnGeschaeftsstatistikActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboKostenfreiActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboKostenfreiActionPerformed
        if (!cboKostenfrei.isSelected() && !cboKostenpflichtig.isSelected()) {
            cboKostenpflichtig.setSelected(true);
        }
        filterSettingsChanged();
    }                                                                                 //GEN-LAST:event_cboKostenfreiActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboAbgerechnetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboAbgerechnetActionPerformed
        if (!cboAbgerechnet.isSelected() && !cboNichtAbgerechnet.isSelected()) {
            cboAbgerechnet.setSelected(true);
        }
        filterSettingsChanged();
    }                                                                                  //GEN-LAST:event_cboAbgerechnetActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboNichtAbgerechnetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboNichtAbgerechnetActionPerformed
        if (!cboAbgerechnet.isSelected() && !cboNichtAbgerechnet.isSelected()) {
            cboAbgerechnet.setSelected(true);
        }
        filterSettingsChanged();
    }                                                                                       //GEN-LAST:event_cboNichtAbgerechnetActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboKostenpflichtigActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboKostenpflichtigActionPerformed
        if (!cboKostenfrei.isSelected() && !cboKostenpflichtig.isSelected()) {
            cboKostenfrei.setSelected(true);
        }
        filterSettingsChanged();
    }                                                                                      //GEN-LAST:event_cboKostenpflichtigActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void setFilterActionInExternalPanels() {
        final Action filterAction = new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    filterSettingsChanged();
                }
            };
        pnlTimeFilters.setFilterSettingChangedAction(filterAction);
        pnlVerwendungszweck.setFilterSettingChangedAction(filterAction);
    }

    /**
     * DOCUMENT ME!
     */
    private void filterSettingsChanged() {
        org.openide.awt.Mnemonics.setLocalizedText(
            lblResultHeader,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.lblResultHeader.text.filterSettingsChanged")); // NOI18N
    }

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        setTitle(null);
    }

    /**
     * DOCUMENT ME!
     */
    private void filterBuchungen() {
        org.openide.awt.Mnemonics.setLocalizedText(
            lblResultHeader,
            org.openide.util.NbBundle.getMessage(
                BillingKundeAggregationRenderer.class,
                "BillingKundeAggregationRenderer.lblResultHeader.text.updated"));

        final ArrayList<MetaObject> kundenMetaObjects = new ArrayList<MetaObject>();
        for (final CidsBean kundeBean : cidsBeans) {
            kundenMetaObjects.add(kundeBean.getMetaObject());
        }
        final CidsBillingSearchStatement cidsBillingSearchStatement = new CidsBillingSearchStatement(
                SessionManager.getSession().getUser(),
                kundenMetaObjects);

        // set filters
        cidsBillingSearchStatement.setVerwendungszweckKeys(
            pnlVerwendungszweck.createSelectedVerwendungszweckKeysStringArray());
        fromDate_tillDate = pnlTimeFilters.chooseDates();
        cidsBillingSearchStatement.setFrom(fromDate_tillDate[0]);
        cidsBillingSearchStatement.setTill(fromDate_tillDate[1]);

        if (cboKostenfrei.isSelected() && cboKostenpflichtig.isSelected()) {
            cidsBillingSearchStatement.setKostentyp(Kostentyp.IGNORIEREN);
        } else if (cboKostenfrei.isSelected()) {
            cidsBillingSearchStatement.setKostentyp(Kostentyp.KOSTENFREI);
        } else if (cboKostenpflichtig.isSelected()) {
            cidsBillingSearchStatement.setKostentyp(Kostentyp.KOSTENPFLICHTIG);
        } else {
            cidsBillingSearchStatement.setKostentyp(Kostentyp.IGNORIEREN);
        }

        if ((cboAbgerechnet.isSelected() && cboNichtAbgerechnet.isSelected())) {
            cidsBillingSearchStatement.setShowAbgerechneteBillings(null);
        } else if (cboAbgerechnet.isSelected()) {
            cidsBillingSearchStatement.setShowAbgerechneteBillings(true);
        } else if (cboNichtAbgerechnet.isSelected()) {
            cidsBillingSearchStatement.setShowAbgerechneteBillings(false);
        } else {
            cidsBillingSearchStatement.setShowAbgerechneteBillings(null);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Query to get the billings: " + cidsBillingSearchStatement.generateQuery());
        }

        blblBusy.setBusy(true);
        ((CardLayout)pnlTable.getLayout()).show(pnlTable, "busy");
        btnBuchungsbeleg.setEnabled(false);
        btnRechnungsanlage.setEnabled(false);
        btnShowResults.setEnabled(false);
        btnGeschaeftsstatistik.setEnabled(false);
        final SwingWorker<Collection<MetaObject>, Void> swingWorker = new SwingWorker<Collection<MetaObject>, Void>() {

                @Override
                protected Collection<MetaObject> doInBackground() throws Exception {
                    // return cidsBillingSearchStatement.performServerSearch();
                    return SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(),
                                    cidsBillingSearchStatement);
                }

                @Override
                protected void done() {
                    try {
                        final Collection<MetaObject> metaObjects = get();

                        if (metaObjects == null) {
                            LOG.error("Billing metaobjects was null.");
                        } else if (metaObjects.isEmpty()) {
                            LOG.info("No Billing metaobjects found.");
                            filteredBillingBeans = new ArrayList<CidsBean>();
                            fillCustomerTable(filteredBillingBeans);
                            lblFilterResult.setText(generateFilterResultText(new ArrayList<CidsBean>()));
                        } else {
                            final List<CidsBean> billingBeans = new ArrayList<CidsBean>(metaObjects.size());
                            for (final MetaObject mo : metaObjects) {
                                billingBeans.add(mo.getBean());
                            }
                            fillCustomerTable(billingBeans);
                            filteredBillingBeans = billingBeans;
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
                        btnGeschaeftsstatistik.setEnabled(true);
                        blblBusy.setBusy(false);
                    }
                }
            };
        swingWorker.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String generateFilterResultText(final Collection<CidsBean> billingBeans) {
        final StringBuilder text = new StringBuilder();
        if (billingBeans.isEmpty()) {
            text.append(NbBundle.getMessage(
                    BillingKundeRenderer.class,
                    "BillingKundeRenderer.generateFilterResultText().noBillings"));
        } else {
            fromDate_tillDate = pnlTimeFilters.chooseDates();
            final Date from = fromDate_tillDate[0];
            final Date till = fromDate_tillDate[1];

            text.append(NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.generateFilterResultText().billings1"));
            if (from == null) {
                text.append(".");
            } else if ((till == null) || from.equals(till)) {
                text.append(NbBundle.getMessage(
                        BillingKundeAggregationRenderer.class,
                        "BillingKundeAggregationRenderer.generateFilterResultText().billings2.oneDate"));
                text.append(DATE_FORMAT.format(from));
                text.append(".");
            } else {
                text.append(NbBundle.getMessage(
                        BillingKundeAggregationRenderer.class,
                        "BillingKundeAggregationRenderer.generateFilterResultText().billings2.twoDates1"));
                text.append(DATE_FORMAT.format(from));
                text.append(NbBundle.getMessage(
                        BillingKundeAggregationRenderer.class,
                        "BillingKundeAggregationRenderer.generateFilterResultText().billings2.twoDates2"));
                text.append(DATE_FORMAT.format(till));
                text.append(".");
            }
        }
        if (cboNichtAbgerechnet.isSelected()) {
            text.append(NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.generateFilterResultText().suffix.hideAbgerechnteAndStornierte"));
        } else {
            text.append(NbBundle.getMessage(
                    BillingKundeAggregationRenderer.class,
                    "BillingKundeAggregationRenderer.generateFilterResultText().suffix.hideStornierte"));
        }

        return text.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  billingBeans  DOCUMENT ME!
     */
    private void fillCustomerTable(final Collection<CidsBean> billingBeans) {
        final HashMap<CidsBean, Object[]> aggregatedData = aggregateData(billingBeans);
        tableData = aggregatedData.values();

        final AggregatedBillingTableModel tableModel = new AggregatedBillingTableModel(
                tableData.toArray(new Object[tableData.size()][]),
                AGR_COMLUMN_NAMES);
        tblCustomers.setModel(tableModel);
        tblCustomers.getColumnModel().getColumn(2).setCellRenderer(new EuroFormatterRenderer());
        if (!tableData.isEmpty()) {
            final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tblCustomers.getModel());
            tblCustomers.setRowSorter(sorter);
        } else {
            tblCustomers.setRowSorter(null);
        }
    }

    /**
     * Evaluates the billings of each customer and returns an HashMap with an entry for each customer. The value of the
     * HashMap is an array with the following values: {boolean isShownInTheReports, CidsBean customerBean, BigDecimal
     * brutto sum, int amount of reports which have costs}. The array is used by the table to represent one row.
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private HashMap<CidsBean, Object[]> aggregateData(final Collection<CidsBean> billingBeans) {
        final HashMap<CidsBean, Object[]> aggregatedData = new HashMap<CidsBean, Object[]>();
        final HashMap<CidsBean, Collection<CidsBean>> billingsOfCustomers =
            new HashMap<CidsBean, Collection<CidsBean>>();

        for (final CidsBean billingBean : billingBeans) {
            // fill the Arrays inside the HashMap (except for brutto sum)
            final CidsBean kundeBean = (CidsBean)billingBean.getProperty("angelegt_durch.kunde");
            final double netto_sum = (Double)billingBean.getProperty("netto_summe");
            if (aggregatedData.containsKey(kundeBean)) {
                final Object[] dataForCustomer = aggregatedData.get(kundeBean);
                if (netto_sum > 0) {
                    final int amountOfBillingsWithCosts = (Integer)dataForCustomer[3];
                    dataForCustomer[3] = amountOfBillingsWithCosts + 1;
                }
            } else {
                final Boolean useInReports = Boolean.TRUE;
                int amountOfBillingsWithCosts = 0;
                if (netto_sum > 0) {
                    amountOfBillingsWithCosts = 1;
                }
                final Object[] dataForCustomer = {
                        useInReports,
                        kundeBean,
                        null,
                        amountOfBillingsWithCosts
                    };
                aggregatedData.put(kundeBean, dataForCustomer);
            }

            // match the billings and the customers, this is later on used to calculate the brutto sum
            if (billingsOfCustomers.containsKey(kundeBean)) {
                billingsOfCustomers.get(kundeBean).add(billingBean);
            } else {
                final ArrayList<CidsBean> list = new ArrayList<CidsBean>();
                list.add(billingBean);
                billingsOfCustomers.put(kundeBean, list);
            }
        }

        // calculate the brutto sum
        for (final CidsBean kundeBean : billingsOfCustomers.keySet()) {
            final BigDecimal brutto_sum = BillingCalculations.calculateBruttoSumFromBillings(billingsOfCustomers.get(
                        kundeBean));
            final Object[] dataForCustomer = aggregatedData.get(kundeBean);
            dataForCustomer[2] = brutto_sum;
        }

        return aggregatedData;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   evt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private HashMap<CidsBean, Collection<CidsBean>> createBillingsOfCostumersForReports(final ActionEvent evt) {
        final HashMap<CidsBean, Collection<CidsBean>> billingsOfCustomers =
            new HashMap<CidsBean, Collection<CidsBean>>();
        for (final CidsBean billingBean : filteredBillingBeans) {
            final CidsBean kundeBean = (CidsBean)billingBean.getProperty("angelegt_durch.kunde");
            if (isCustomerSelectedToBeIncludedIntoReport(kundeBean)) {
                if (billingsOfCustomers.containsKey(kundeBean)) {
                    billingsOfCustomers.get(kundeBean).add(billingBean);
                } else {
                    final ArrayList<CidsBean> billings = new ArrayList<CidsBean>();
                    billings.add(billingBean);
                    billingsOfCustomers.put(kundeBean, billings);
                }
            }
        }
        return billingsOfCustomers;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   evt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean retrieveShowBillingWithoutCostInReport(final ActionEvent evt) {
        final JButton source = (JButton)evt.getSource();
        if (source.equals(btnBuchungsbeleg)) {
            return !cboHideFreeDownloadsBuchungsbeleg.isSelected();
        } else if (source.equals(btnRechnungsanlage)) {
            return !cboHideFreeDownloadsRechnungsanlage.isSelected();
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> createBillingsForStatisticsReport() {
        final Collection<CidsBean> billlings = new ArrayList<CidsBean>();
        for (final CidsBean billingBean : filteredBillingBeans) {
            final CidsBean kundeBean = (CidsBean)billingBean.getProperty("angelegt_durch.kunde");
            if (isCustomerSelectedToBeIncludedIntoReport(kundeBean)) {
                billlings.add(billingBean);
            }
        }
        return billlings;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kundeBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isCustomerSelectedToBeIncludedIntoReport(final CidsBean kundeBean) {
        final AggregatedBillingTableModel model = (AggregatedBillingTableModel)tblCustomers.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            final CidsBean kundeFromTable = (CidsBean)model.getValueAt(i, 1);
            if (kundeFromTable.equals(kundeBean)) {
                return (Boolean)model.getValueAt(i, 0);
            }
        }
        return false;
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return lblAgrTitle.getText();
    }

    @Override
    public void setTitle(final String title) {
        String desc = "Kundenaggregationsrenderer: ";
        final Collection<CidsBean> beans = cidsBeans;
        if ((beans != null) && (beans.size() > 0)) {
            desc += beans.size() + " Kunden ausgewählt";
        }
        lblAgrTitle.setText(desc);
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JLabel getTitleLabel() {
        return lblAgrTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                    "WUNDA_BLAU",
                    "Administratoren",
                    "admin",
                    "kif",
                    "billing_kunde",
                    60);

            DevelopmentTools.createAggregationRendererInFrameFromRMIConnectionOnLocalhost(Arrays.asList(beans),
                "Ausgewählte Kunden",
                1024,
                800);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static final class AggregatedBillingTableModel extends DefaultTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PointTableModel object.
         *
         * @param  data    DOCUMENT ME!
         * @param  labels  DOCUMENT ME!
         */
        public AggregatedBillingTableModel(final Object[][] data, final String[] labels) {
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
            if (column == 0) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   column  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Class getColumnClass(final int column) {
            for (int row = 0; row < getRowCount(); row++) {
                final Object o = getValueAt(row, column);
                if (o != null) {
                    return o.getClass();
                }
            }
            return Object.class;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class EuroFormatterRenderer extends DefaultTableCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private NumberFormat euroFormatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EuroFormatterRenderer object.
         */
        public EuroFormatterRenderer() {
            this.setHorizontalAlignment(SwingConstants.RIGHT);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  value  DOCUMENT ME!
         */
        @Override
        protected void setValue(final Object value) {
            if ((value == null) || !(value instanceof Number)) {
                setText(ObjectRendererUtils.propertyPrettyPrint(value));
            } else {
                setText(euroFormatter.format(value));
            }
        }
    }
}
