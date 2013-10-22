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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;

import org.openide.util.Exceptions;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingInfo;
import de.cismet.cids.custom.objectrenderer.utils.billing.Usage;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingKundeRenderer extends javax.swing.JPanel implements CidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingKundeRenderer.class);

    private static BillingInfo billingInfo;
    private static ObjectMapper mapper = new ObjectMapper();
    private static final HashMap<String, Usage> usages = new HashMap<String, Usage>();
    private static HashMap<JCheckBox, Usage> mappingJCheckboxToUsages = new HashMap<JCheckBox, Usage>();

    static {
        try {
            billingInfo = mapper.readValue(BillingInfo.class.getResourceAsStream(
                        "/de/cismet/cids/custom/billing/billing.json"),
                    BillingInfo.class);

            final ArrayList<Usage> lu = billingInfo.getUsages();
            for (final Usage u : lu) {
                usages.put(u.getKey(), u);
            }
        } catch (IOException ioException) {
            LOG.error("Error when trying to read the billingInfo.json", ioException);
        }
    }

    // column headers
    private static final String[] AGR_COMLUMN_NAMES = new String[] {
            "Geschäftsbuchnummer",
            "Projektbezeichnung",
            "Verwendung",
            "Produkt",
            "Preis",
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

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    enum Kostenart {

        //~ Enum constants -----------------------------------------------------

        KOSTENPFLICHTIG, KOSTENFREI, IGNORIEREN
    }

    //~ Instance fields --------------------------------------------------------

    private boolean editable;
    private BillingTableModel tableModel;
    private CidsBean cidsBean;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuchungsbeleg;
    private javax.swing.JButton btnRechnungsanlage;
    private javax.swing.ButtonGroup btngTimeFilters;
    private javax.swing.JComboBox cboBenutzer;
    private javax.swing.JCheckBox cboKostenfrei;
    private javax.swing.JCheckBox cboKostenpflichtig;
    private javax.swing.JComboBox cboMonth;
    private javax.swing.JComboBox cboQuarter;
    private javax.swing.JComboBox cboYear_Month;
    private javax.swing.JComboBox cboYear_Quarter;
    private org.jdesktop.swingx.JXDatePicker dpFrom;
    private org.jdesktop.swingx.JXDatePicker dpTill;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFilterResult;
    private javax.swing.JPanel pnlDateRange;
    private javax.swing.JPanel pnlFilters;
    private javax.swing.JPanel pnlKostenart;
    private javax.swing.JPanel pnlMonth;
    private javax.swing.JPanel pnlQuarter;
    private javax.swing.JPanel pnlTimeButtons;
    private javax.swing.JPanel pnlTimeFilterCards;
    private javax.swing.JPanel pnlTimeFilters;
    private javax.swing.JPanel pnlToday;
    private javax.swing.JPanel pnlVerwendungszweck;
    private javax.swing.JPanel pnlVerwendungszweckCheckBoxes;
    private de.cismet.tools.gui.SemiRoundedPanel smiplFilter;
    private de.cismet.tools.gui.SemiRoundedPanel smiplTable;
    private javax.swing.JTable tblBillings;
    private javax.swing.JToggleButton tbtnDateRange;
    private javax.swing.JToggleButton tbtnMonth;
    private javax.swing.JToggleButton tbtnQuarter;
    private javax.swing.JToggleButton tbtnToday;
    private javax.swing.JTextField txtGeschaeftsbuchnummer;
    private javax.swing.JTextField txtProjekt;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
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
        this.editable = editable;
        initComponents();
        initVerwendungszweckCheckBoxes();
        tableModel = new BillingTableModel(new Object[0][], AGR_COMLUMN_NAMES);
        tblBillings.setModel(tableModel);
        setTimeRelatedModels();
        changeVisibleTimeFilterPanel();
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        btngTimeFilters = new javax.swing.ButtonGroup();
        pnlFilters = new javax.swing.JPanel();
        pnlTimeFilters = new javax.swing.JPanel();
        pnlTimeFilterCards = new javax.swing.JPanel();
        pnlToday = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 32767));
        pnlMonth = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cboMonth = new javax.swing.JComboBox();
        cboYear_Month = new javax.swing.JComboBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 32767));
        pnlQuarter = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cboQuarter = new javax.swing.JComboBox();
        cboYear_Quarter = new javax.swing.JComboBox();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 0),
                new java.awt.Dimension(1, 32767));
        pnlDateRange = new javax.swing.JPanel();
        dpTill = new org.jdesktop.swingx.JXDatePicker();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dpFrom = new org.jdesktop.swingx.JXDatePicker();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlTimeButtons = new javax.swing.JPanel();
        tbtnToday = new javax.swing.JToggleButton();
        tbtnMonth = new javax.swing.JToggleButton();
        tbtnDateRange = new javax.swing.JToggleButton();
        tbtnQuarter = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtGeschaeftsbuchnummer = new javax.swing.JTextField();
        txtProjekt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboBenutzer = new javax.swing.JComboBox();
        pnlVerwendungszweck = new javax.swing.JPanel();
        pnlVerwendungszweckCheckBoxes = new javax.swing.JPanel();
        pnlKostenart = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cboKostenfrei = new javax.swing.JCheckBox();
        cboKostenpflichtig = new javax.swing.JCheckBox();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel2 = new javax.swing.JPanel();
        btnBuchungsbeleg = new javax.swing.JButton();
        btnRechnungsanlage = new javax.swing.JButton();
        smiplFilter = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel2 = new javax.swing.JLabel();
        smiplTable = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBillings = new javax.swing.JTable();
        lblFilterResult = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        setLayout(new java.awt.GridBagLayout());

        pnlFilters.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        pnlFilters.setLayout(new java.awt.GridBagLayout());

        pnlTimeFilters.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 2, 0, 2));
        pnlTimeFilters.setLayout(new java.awt.GridBagLayout());

        pnlTimeFilterCards.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlTimeFilterCards.setLayout(new java.awt.CardLayout());

        pnlToday.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        pnlToday.add(jLabel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlToday.add(filler2, gridBagConstraints);

        pnlTimeFilterCards.add(pnlToday, "pnlToday");

        pnlMonth.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 3, 3);
        pnlMonth.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 10, 3);
        pnlMonth.add(jLabel10, gridBagConstraints);

        cboMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Januar", "Februar", "März" }));
        cboMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboMonthActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 2, 3, 0);
        pnlMonth.add(cboMonth, gridBagConstraints);

        cboYear_Month.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2013" }));
        cboYear_Month.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboYear_MonthActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 10, 0);
        pnlMonth.add(cboYear_Month, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlMonth.add(filler1, gridBagConstraints);

        pnlTimeFilterCards.add(pnlMonth, "pnlMonth");

        pnlQuarter.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 4, 3);
        pnlQuarter.add(jLabel11, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 10, 3);
        pnlQuarter.add(jLabel12, gridBagConstraints);

        cboQuarter.setModel(new javax.swing.DefaultComboBoxModel(
                new String[] { "Januar - März", "April - Juni", "Juli - September", "Oktober - Dezember" }));
        cboQuarter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboQuarterActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 3, 4, 0);
        pnlQuarter.add(cboQuarter, gridBagConstraints);

        cboYear_Quarter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2013" }));
        cboYear_Quarter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboYear_QuarterActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 10, 0);
        pnlQuarter.add(cboYear_Quarter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlQuarter.add(filler4, gridBagConstraints);

        pnlTimeFilterCards.add(pnlQuarter, "pnlQuarter");

        pnlDateRange.setLayout(new java.awt.GridBagLayout());

        dpTill.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    dpTillActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 10, 0);
        pnlDateRange.add(dpTill, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 4, 3);
        pnlDateRange.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 10, 3);
        pnlDateRange.add(jLabel8, gridBagConstraints);

        dpFrom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    dpFromActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 3, 4, 0);
        pnlDateRange.add(dpFrom, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 1.0;
        pnlDateRange.add(filler3, gridBagConstraints);

        pnlTimeFilterCards.add(pnlDateRange, "pnlDateRange");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        pnlTimeFilters.add(pnlTimeFilterCards, gridBagConstraints);

        pnlTimeButtons.setLayout(new java.awt.GridBagLayout());

        btngTimeFilters.add(tbtnToday);
        tbtnToday.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            tbtnToday,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.tbtnToday.text")); // NOI18N
        tbtnToday.setMaximumSize(new java.awt.Dimension(500, 25));
        tbtnToday.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnTodayActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        pnlTimeButtons.add(tbtnToday, gridBagConstraints);

        btngTimeFilters.add(tbtnMonth);
        org.openide.awt.Mnemonics.setLocalizedText(
            tbtnMonth,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.tbtnMonth.text")); // NOI18N
        tbtnMonth.setMaximumSize(new java.awt.Dimension(500, 25));
        tbtnMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnMonthActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        pnlTimeButtons.add(tbtnMonth, gridBagConstraints);

        btngTimeFilters.add(tbtnDateRange);
        org.openide.awt.Mnemonics.setLocalizedText(
            tbtnDateRange,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.tbtnDateRange.text")); // NOI18N
        tbtnDateRange.setMaximumSize(new java.awt.Dimension(500, 25));
        tbtnDateRange.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnDateRangeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        pnlTimeButtons.add(tbtnDateRange, gridBagConstraints);

        btngTimeFilters.add(tbtnQuarter);
        org.openide.awt.Mnemonics.setLocalizedText(
            tbtnQuarter,
            org.openide.util.NbBundle.getMessage(BillingKundeRenderer.class, "BillingKundeRenderer.tbtnQuarter.text")); // NOI18N
        tbtnQuarter.setMaximumSize(new java.awt.Dimension(500, 25));
        tbtnQuarter.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    tbtnQuarterActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        pnlTimeButtons.add(tbtnQuarter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        pnlTimeFilters.add(pnlTimeButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
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
                "BillingKundeRenderer.txtGeschaeftsbuchnummer.text")); // NOI18N
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

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.benutzer}");
        final org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJComboBoxBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        cboBenutzer);
        bindingGroup.addBinding(jComboBoxBinding);

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
        pnlFilters.add(jPanel3, gridBagConstraints);

        pnlVerwendungszweck.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                org.openide.util.NbBundle.getMessage(
                    BillingKundeRenderer.class,
                    "BillingKundeRenderer.pnlVerwendungszweck.border.title"),
                javax.swing.border.TitledBorder.LEADING,
                javax.swing.border.TitledBorder.DEFAULT_POSITION)); // NOI18N
        pnlVerwendungszweck.setLayout(new java.awt.BorderLayout());

        pnlVerwendungszweckCheckBoxes.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        pnlVerwendungszweckCheckBoxes.setLayout(new javax.swing.BoxLayout(
                pnlVerwendungszweckCheckBoxes,
                javax.swing.BoxLayout.PAGE_AXIS));
        pnlVerwendungszweck.add(pnlVerwendungszweckCheckBoxes, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlFilters.add(pnlVerwendungszweck, gridBagConstraints);

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
        pnlFilters.add(pnlKostenart, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        pnlFilters.add(filler5, gridBagConstraints);

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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel2.add(btnBuchungsbeleg, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnRechnungsanlage,
            org.openide.util.NbBundle.getMessage(
                BillingKundeRenderer.class,
                "BillingKundeRenderer.btnRechnungsanlage.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel2.add(btnRechnungsanlage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
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

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 6, 6, 6));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(453, 275));

        tblBillings.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 0);
        jPanel5.add(jScrollPane1, gridBagConstraints);

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
        jPanel5.add(lblFilterResult, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel5, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboYear_MonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboYear_MonthActionPerformed
        filterBuchungen_placeHolder();
    }                                                                                 //GEN-LAST:event_cboYear_MonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnTodayActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnTodayActionPerformed
        changeVisibleTimeFilterPanel();
        filterBuchungen_placeHolder();
    }                                                                             //GEN-LAST:event_tbtnTodayActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnMonthActionPerformed
        changeVisibleTimeFilterPanel();
    }                                                                             //GEN-LAST:event_tbtnMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnQuarterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnQuarterActionPerformed
        changeVisibleTimeFilterPanel();
    }                                                                               //GEN-LAST:event_tbtnQuarterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tbtnDateRangeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_tbtnDateRangeActionPerformed
        changeVisibleTimeFilterPanel();
    }                                                                                 //GEN-LAST:event_tbtnDateRangeActionPerformed

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
    private void cboMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboMonthActionPerformed
        filterBuchungen_placeHolder();
    }                                                                            //GEN-LAST:event_cboMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboQuarterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboQuarterActionPerformed
        filterBuchungen_placeHolder();
    }                                                                              //GEN-LAST:event_cboQuarterActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboYear_QuarterActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboYear_QuarterActionPerformed
        filterBuchungen_placeHolder();
    }                                                                                   //GEN-LAST:event_cboYear_QuarterActionPerformed

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
    private void dpFromActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_dpFromActionPerformed
        checkDateRange();
    }                                                                          //GEN-LAST:event_dpFromActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dpTillActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_dpTillActionPerformed
        checkDateRange();
    }                                                                          //GEN-LAST:event_dpTillActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void checkDateRange() {
        final Date from = dpFrom.getDate();
        final Date till = dpTill.getDate();
        if (((from != null) && (till != null) && from.before(till)) || from.equals(till)) {
            filterBuchungen_placeHolder();
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean kundeBean) {
        bindingGroup.unbind();
        if (kundeBean != null) {
            cidsBean = kundeBean;
            bindingGroup.bind();
            filterBuchungen(true);
        }
    }

    @Override
    public void dispose() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTitle() {
        return "Kunden Test";
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTitle(final String title) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * DOCUMENT ME!
     */
    private void setTimeRelatedModels() {
        final String[] months = getMonthStrings();
        cboMonth.setModel(new javax.swing.DefaultComboBoxModel(months));

        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        final Integer[] years = new Integer[100];
        for (int i = 0; i < 100; i++) {
            years[i] = new Integer(currentYear - i);
        }
        cboYear_Month.setModel(new javax.swing.DefaultComboBoxModel<Integer>(years));
        cboYear_Quarter.setModel(new javax.swing.DefaultComboBoxModel<Integer>(years));
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
            1,
            "Foo",
            1280,
            1024);
    }

    /**
     * DOCUMENT ME!
     */
    private void changeVisibleTimeFilterPanel() {
        final CardLayout cardLayout = (CardLayout)pnlTimeFilterCards.getLayout();
        if (tbtnToday.isSelected()) {
            cardLayout.show(pnlTimeFilterCards, "pnlToday");
        } else if (tbtnMonth.isSelected()) {
            cardLayout.show(pnlTimeFilterCards, "pnlMonth");
        } else if (tbtnQuarter.isSelected()) {
            cardLayout.show(pnlTimeFilterCards, "pnlQuarter");
        } else if (tbtnDateRange.isSelected()) {
            cardLayout.show(pnlTimeFilterCards, "pnlDateRange");
        } else {
            LOG.warn("No toggle button, to show a time filter, is selected. This should never happen.");
        }
    }

    /**
     * DateFormatSymbols returns an extra, empty value at the end of the array of months. Remove it.
     *
     * @return  DOCUMENT ME!
     */
    private static String[] getMonthStrings() {
        final String[] months = new java.text.DateFormatSymbols().getMonths();
        final int lastIndex = months.length - 1;

        if ((months[lastIndex] == null)
                    || (months[lastIndex].length() <= 0)) { // last item empty
            final String[] monthStrings = new String[lastIndex];
            System.arraycopy(months, 0,
                monthStrings, 0, lastIndex);
            return monthStrings;
        } else {                                            // last item not empty
            return months;
        }
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
                final String propertyString;
                propertyString = ObjectRendererUtils.propertyPrettyPrint(property);
                result[i] = propertyString;
            }
            return result;
        }
        return new Object[0];
    }

    /**
     * DOCUMENT ME!
     */
    private void initVerwendungszweckCheckBoxes() {
        for (final Usage usage : usages.values()) {
            final JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(true);
            checkBox.setText(usage.getName());
            checkBox.setToolTipText(usage.getKey());
            checkBox.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        boolean noneSelected = true;
                        for (final JCheckBox cb : mappingJCheckboxToUsages.keySet()) {
                            if (cb.isSelected()) {
                                noneSelected = false;
                                break;
                            }
                        }
                        if (noneSelected) {
                            ((JCheckBox)e.getSource()).setSelected(true);
                        } else {
                            filterBuchungen_placeHolder();
                        }
                    }
                });

            mappingJCheckboxToUsages.put(checkBox, usage);
            pnlVerwendungszweckCheckBoxes.add(checkBox);
        }
    }

    /**
     * a placeholder which can be used if the filterBuchungen has to be executed in a SwingWorker etc...
     */
    private void filterBuchungen_placeHolder() {
        filterBuchungen();
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
        try {
            final QueryBuilder queryBuilder = new QueryBuilder();
            if (!ignoreFilters) {
                queryBuilder.setGeschaeftsbuchnummer(txtGeschaeftsbuchnummer.getText());
                queryBuilder.setProjekt(txtProjekt.getText());
                queryBuilder.setUser((CidsBean)cboBenutzer.getSelectedItem());
                queryBuilder.setVerwendungszweckKeys(createSelectedVerwendungszweckKeysStringArray());
                queryBuilder.setKostenart(chooseKostenart());
                final Date[] fromDate_tillDate = chooseDates();
                queryBuilder.setFrom(fromDate_tillDate[0]);
                queryBuilder.setTill(fromDate_tillDate[1]);
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Query to get the billings: " + queryBuilder.generateQuery());
            }

            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(queryBuilder.generateQuery(), 0);

            if (metaObjects == null) {
                LOG.error("Billing metaobjects was null.");
            } else if (metaObjects.length == 0) {
                LOG.info("No Billing metaobjects found.");
                fillBillingTable(new ArrayList<CidsBean>());
            } else {
                final List<CidsBean> billingBeans = new ArrayList<CidsBean>(metaObjects.length);
                for (final MetaObject mo : metaObjects) {
                    billingBeans.add(mo.getBean());
                }
                fillBillingTable(billingBeans);
            }
        } catch (ConnectionException ex) {
            LOG.error("Error while filtering the billings.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ArrayList<String> createSelectedVerwendungszweckKeysStringArray() {
        final ArrayList<String> ret = new ArrayList<String>();
        for (final JCheckBox jCheckBox : mappingJCheckboxToUsages.keySet()) {
            if (jCheckBox.isSelected()) {
                final Usage usage = mappingJCheckboxToUsages.get(jCheckBox);
                ret.add(usage.getKey());
            }
        }
        return ret;
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
     *
     * @return  DOCUMENT ME!
     */
    private Date[] chooseDates() {
        // default value is today
        final Date[] fromDate_tillDate = new Date[] { new Date(), null };
        if (tbtnMonth.isSelected()) {
            final Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.MONTH, cboMonth.getSelectedIndex());
            calendar.set(Calendar.YEAR, (Integer)cboYear_Month.getSelectedItem());
            fromDate_tillDate[0] = calendar.getTime();
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            fromDate_tillDate[1] = calendar.getTime();
        } else if (tbtnQuarter.isSelected()) {
            final Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.MONTH, cboQuarter.getSelectedIndex() * 3);
            calendar.set(Calendar.YEAR, (Integer)cboYear_Quarter.getSelectedItem());
            fromDate_tillDate[0] = calendar.getTime();
            calendar.add(Calendar.MONTH, 3);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            fromDate_tillDate[1] = calendar.getTime();
        } else if (tbtnDateRange.isSelected()) {
            final Date from = dpFrom.getDate();
            if (from != null) {
                fromDate_tillDate[0] = from;
                fromDate_tillDate[1] = dpTill.getDate();
            }
        }
        return fromDate_tillDate;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class QueryBuilder {

        //~ Instance fields ----------------------------------------------------

        String geschaeftsbuchnummer;
        String projekt;
        CidsBean userBean;
        ArrayList<String> verwendungszweckKeys = new ArrayList<String>();
        Kostenart kostenart = Kostenart.IGNORIEREN;
        Date from = new Date();
        Date till;
        StringBuilder query;
        SimpleDateFormat postgresDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String generateQuery() {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "billing_billing");
            query = new StringBuilder();
            query.append("SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " ");
            query.append("FROM " + MB_MC.getTableName());
            appendWhereClauseAndUsernames();
            appendGeschaeftsbuchnummer();
            appendProjekt();
            appendVerwendungszweckKeys();
            appendKostenart();
            appendDates();

            return query.toString();
        }

        /**
         * DOCUMENT ME!
         */
        private void appendWhereClauseAndUsernames() {
            query.append(" WHERE angelegt_durch ");
            if (userBean == null) {
                final List<CidsBean> benutzerBeans = cidsBean.getBeanCollectionProperty("benutzer");
                if (!benutzerBeans.isEmpty()) {
                    final StringBuilder userListString = new StringBuilder("in (");
                    for (final CidsBean benutzer : benutzerBeans) {
                        userListString.append(benutzer.getProperty("id"));
                        userListString.append(",");
                    }
                    // remove last comma
                    userListString.deleteCharAt(userListString.length() - 1);
                    userListString.append(")");
                    query.append(userListString.toString());
                } else {
                    LOG.error("This customer has no users, that should not happen.");
                }
            } else {
                query.append(" = " + userBean.getProperty("id") + " ");
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void appendGeschaeftsbuchnummer() {
            if ((geschaeftsbuchnummer != null) && !geschaeftsbuchnummer.equals("")) {
                query.append("and geschaeftsbuchnummer = '" + geschaeftsbuchnummer + "' ");
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void appendProjekt() {
            if ((projekt != null) && !projekt.equals("")) {
                query.append("and projektbezeichnung ilike '%" + projekt + "%' ");
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void appendVerwendungszweckKeys() {
            if (!verwendungszweckKeys.isEmpty()) {
                final StringBuilder verwendungszweckListString = new StringBuilder("(");
                for (final String verwendungszweckKey : verwendungszweckKeys) {
                    verwendungszweckListString.append(" '");
                    verwendungszweckListString.append(verwendungszweckKey);
                    verwendungszweckListString.append("',");
                }
                // remove last comma
                verwendungszweckListString.deleteCharAt(verwendungszweckListString.length() - 1);
                verwendungszweckListString.append(")");
                query.append("and verwendungskey in " + verwendungszweckListString.toString() + " ");
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void appendKostenart() {
            switch (kostenart) {
                case KOSTENFREI: {
                    query.append("and gebuehrenpflichtig is false");
                    break;
                }
                case KOSTENPFLICHTIG: {
                    query.append("and gebuehrenpflichtig is true");
                    break;
                }
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void appendDates() {
            // check if there is a second date or if they are the same day
            if ((till == null) || postgresDateFormat.format(from).equals(postgresDateFormat.format(till))) {
                query.append(" and date_trunc('day',ts) = '");
                query.append(postgresDateFormat.format(from));
                query.append("' ");
            } else { // create query for a time period
                query.append(" and date_trunc('day',ts) >= '");
                query.append(postgresDateFormat.format(from));
                query.append("' ");
                query.append(" and date_trunc('day',ts) <= '");
                query.append(postgresDateFormat.format(till));
                query.append("' ");
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getGeschaeftsbuchnummer() {
            return geschaeftsbuchnummer;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  geschaeftsbuchnummer  DOCUMENT ME!
         */
        public void setGeschaeftsbuchnummer(final String geschaeftsbuchnummer) {
            this.geschaeftsbuchnummer = geschaeftsbuchnummer;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public String getProjekt() {
            return projekt;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  projekt  DOCUMENT ME!
         */
        public void setProjekt(final String projekt) {
            this.projekt = projekt;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getUser() {
            return userBean;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  user  DOCUMENT ME!
         */
        public void setUser(final CidsBean user) {
            this.userBean = user;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public ArrayList<String> getVerwendungszweckKeys() {
            return verwendungszweckKeys;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  verwendungszweckKeys  DOCUMENT ME!
         */
        public void setVerwendungszweckKeys(final ArrayList<String> verwendungszweckKeys) {
            this.verwendungszweckKeys = verwendungszweckKeys;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Kostenart getKostenart() {
            return kostenart;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  kostenart  DOCUMENT ME!
         */
        public void setKostenart(final Kostenart kostenart) {
            this.kostenart = kostenart;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Date getFrom() {
            return from;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  from  DOCUMENT ME!
         */
        public void setFrom(final Date from) {
            this.from = from;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Date getTill() {
            return till;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  till  DOCUMENT ME!
         */
        public void setTill(final Date till) {
            this.till = till;
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
            return super.getColumnClass(columnIndex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class FilterBuchungenDocumentListener implements DocumentListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void insertUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }

        @Override
        public void removeUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }

        @Override
        public void changedUpdate(final DocumentEvent e) {
            filterBuchungen_placeHolder();
        }
    }
}
