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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.actiontag.ActionTagProtected;
import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Dimension;

import java.net.URL;

import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBillingSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class BillingWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    SearchControlListener,
    ActionTagProtected,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BillingWindowSearch.class);
    private static final String ACTION_TAG = "custom.billing.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private SearchControlPanel pnlSearchCancel;
    private ImageIcon icon;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cboAbgerechnet;
    private javax.swing.JComboBox cboAbrechnungsturnus;
    private javax.swing.JComboBox cboBenutzer;
    private javax.swing.JCheckBox cboKostenfrei;
    private javax.swing.JCheckBox cboKostenpflichtig;
    private javax.swing.JCheckBox cboNichtAbgerechnet;
    private javax.swing.JCheckBox cboNichtStorniert;
    private javax.swing.JCheckBox cboStorniert;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAbgerechnetInformation;
    private javax.swing.JPanel panCommand;
    private javax.swing.JPanel pnlAbgerechnetStorniert;
    private javax.swing.JPanel pnlKostentyp;
    private de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel pnlTimeFilters;
    private de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel pnlVerwendungszweck;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextField txtGeschaeftsbuchnummer;
    private javax.swing.JTextField txtProjekt;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BillingWindowSearch.
     */
    public BillingWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();
            if (ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext())) {
                // do only if really needed because this is time consuming
                setAbrechnungsturnusIntoComboBox();
                setUsersIntoComboBox();
            }

            URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/billing_search.png");
            if (urlToIcon == null) {
                urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png");
            }

            if (urlToIcon != null) {
                icon = new ImageIcon(urlToIcon);
            } else {
                icon = new ImageIcon(new byte[] {});
            }

            pnlSearchCancel = new SearchControlPanel(this, getConnectionContext());
            final Dimension max = pnlSearchCancel.getMaximumSize();
            final Dimension min = pnlSearchCancel.getMinimumSize();
            final Dimension pre = pnlSearchCancel.getPreferredSize();
            pnlSearchCancel.setMaximumSize(new java.awt.Dimension(
                    new Double(max.getWidth()).intValue(),
                    new Double(max.getHeight() + 5).intValue()));
            pnlSearchCancel.setMinimumSize(new java.awt.Dimension(
                    new Double(min.getWidth()).intValue(),
                    new Double(min.getHeight() + 5).intValue()));
            pnlSearchCancel.setPreferredSize(new java.awt.Dimension(
                    new Double(pre.getWidth() + 6).intValue(),
                    new Double(pre.getHeight() + 5).intValue()));
            panCommand.add(pnlSearchCancel);
        } catch (Throwable e) {
            LOG.warn("Error in Constructor of BillingWindowSearch. Search will not work properly.", e);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        pnlAbgerechnetStorniert = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        lblAbgerechnetInformation = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 30),
                new java.awt.Dimension(0, 30),
                new java.awt.Dimension(32767, 30));
        jPanel5 = new javax.swing.JPanel();
        cboAbgerechnet = new javax.swing.JCheckBox();
        cboNichtAbgerechnet = new javax.swing.JCheckBox();
        cboStorniert = new javax.swing.JCheckBox();
        cboNichtStorniert = new javax.swing.JCheckBox();
        pnlKostentyp = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        cboKostenpflichtig = new javax.swing.JCheckBox();
        cboKostenfrei = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlVerwendungszweck = new de.cismet.cids.custom.objectrenderer.utils.billing.VerwendungszweckPanel();
        pnlTimeFilters = new de.cismet.cids.custom.objectrenderer.utils.billing.TimeFilterPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboAbrechnungsturnus = new javax.swing.JComboBox();
        txtCustomerName = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtGeschaeftsbuchnummer = new javax.swing.JTextField();
        txtProjekt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cboBenutzer = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        panCommand = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 14, 7, 14));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        pnlAbgerechnetStorniert.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BillingWindowSearch.class,
                    "BillingWindowSearch.pnlAbgerechnetStorniert.border.title"))); // NOI18N
        pnlAbgerechnetStorniert.setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAbgerechnetInformation,
            org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.lblAbgerechnetInformation.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel4.add(lblAbgerechnetInformation, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        jPanel4.add(filler2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridLayout(2, 2));

        cboAbgerechnet.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboAbgerechnet,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.cboAbgerechnet.text")); // NOI18N
        cboAbgerechnet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboAbgerechnetActionPerformed(evt);
                }
            });
        jPanel5.add(cboAbgerechnet);

        cboNichtAbgerechnet.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboNichtAbgerechnet,
            org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.cboNichtAbgerechnet.text")); // NOI18N
        cboNichtAbgerechnet.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboNichtAbgerechnetActionPerformed(evt);
                }
            });
        jPanel5.add(cboNichtAbgerechnet);

        cboStorniert.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboStorniert,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.cboStorniert.text")); // NOI18N
        cboStorniert.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboStorniertActionPerformed(evt);
                }
            });
        jPanel5.add(cboStorniert);

        cboNichtStorniert.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboNichtStorniert,
            org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.cboNichtStorniert.text")); // NOI18N
        cboNichtStorniert.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboNichtStorniertActionPerformed(evt);
                }
            });
        jPanel5.add(cboNichtStorniert);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel5, gridBagConstraints);

        pnlAbgerechnetStorniert.add(jPanel4, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(pnlAbgerechnetStorniert, gridBagConstraints);

        pnlKostentyp.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BillingWindowSearch.class,
                    "BillingWindowSearch.pnlKostentyp.border.title"))); // NOI18N
        pnlKostentyp.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        cboKostenpflichtig.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenpflichtig,
            org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.cboKostenpflichtig.text")); // NOI18N
        cboKostenpflichtig.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenpflichtigActionPerformed(evt);
                }
            });
        jPanel1.add(cboKostenpflichtig);

        cboKostenfrei.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            cboKostenfrei,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.cboKostenfrei.text")); // NOI18N
        cboKostenfrei.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboKostenfreiActionPerformed(evt);
                }
            });
        jPanel1.add(cboKostenfrei);

        pnlKostentyp.add(jPanel1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(pnlKostentyp, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler1, gridBagConstraints);

        pnlVerwendungszweck.initVerwendungszweckCheckBoxes(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(pnlVerwendungszweck, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel2.add(pnlTimeFilters, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createTitledBorder(
                    org.openide.util.NbBundle.getMessage(
                        BillingWindowSearch.class,
                        "BillingWindowSearch.jPanel3.border.outsideBorder.title")),
                javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(jLabel1, gridBagConstraints);

        cboAbrechnungsturnus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboAbrechnungsturnusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        jPanel3.add(cboAbrechnungsturnus, gridBagConstraints);

        txtCustomerName.setText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtCustomerName.text"));        // NOI18N
        txtCustomerName.setToolTipText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtCustomerName.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel3.add(txtCustomerName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 3);
        jPanel3.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 0, 6);
        jPanel3.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        jPanel3.add(jLabel5, gridBagConstraints);

        txtGeschaeftsbuchnummer.setText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtGeschaeftsbuchnummer.text"));        // NOI18N
        txtGeschaeftsbuchnummer.setToolTipText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtGeschaeftsbuchnummer.toolTipText")); // NOI18N
        txtGeschaeftsbuchnummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtGeschaeftsbuchnummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(13, 2, 0, 0);
        jPanel3.add(txtGeschaeftsbuchnummer, gridBagConstraints);

        txtProjekt.setText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtProjekt.text"));        // NOI18N
        txtProjekt.setToolTipText(org.openide.util.NbBundle.getMessage(
                BillingWindowSearch.class,
                "BillingWindowSearch.txtProjekt.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        jPanel3.add(txtProjekt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        jPanel3.add(jLabel4, gridBagConstraints);

        cboBenutzer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboBenutzerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 0, 0);
        jPanel3.add(cboBenutzer, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        jPanel3.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jPanel3, gridBagConstraints);

        panCommand.setLayout(new javax.swing.BoxLayout(panCommand, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        jPanel2.add(panCommand, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel2);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
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
    private void cboBenutzerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboBenutzerActionPerformed
    }                                                                               //GEN-LAST:event_cboBenutzerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboKostenfreiActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboKostenfreiActionPerformed
        if (!cboKostenfrei.isSelected() && !cboKostenpflichtig.isSelected()) {
            cboKostenpflichtig.setSelected(true);
        }
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
    }                                                                                      //GEN-LAST:event_cboKostenpflichtigActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboAbrechnungsturnusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboAbrechnungsturnusActionPerformed
    }                                                                                        //GEN-LAST:event_cboAbrechnungsturnusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboAbgerechnetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboAbgerechnetActionPerformed
        if (!cboAbgerechnet.isSelected() && !cboNichtAbgerechnet.isSelected()) {
            cboAbgerechnet.setSelected(true);
        }
        showOrHideInformationLabel();
    }                                                                                  //GEN-LAST:event_cboAbgerechnetActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboStorniertActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboStorniertActionPerformed
        if (!cboStorniert.isSelected() && !cboNichtStorniert.isSelected()) {
            cboStorniert.setSelected(true);
        }
        showOrHideInformationLabel();
    }                                                                                //GEN-LAST:event_cboStorniertActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboNichtAbgerechnetActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboNichtAbgerechnetActionPerformed
        if (!cboAbgerechnet.isSelected() && !cboNichtAbgerechnet.isSelected()) {
            cboAbgerechnet.setSelected(true);
        }
        showOrHideInformationLabel();
    }                                                                                       //GEN-LAST:event_cboNichtAbgerechnetActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboNichtStorniertActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboNichtStorniertActionPerformed
        if (!cboStorniert.isSelected() && !cboNichtStorniert.isSelected()) {
            cboStorniert.setSelected(true);
        }
        showOrHideInformationLabel();
    }                                                                                     //GEN-LAST:event_cboNichtStorniertActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void showOrHideInformationLabel() {
        if (!cboAbgerechnet.isSelected() && !cboStorniert.isSelected()) {
            lblAbgerechnetInformation.setVisible(true);
        } else {
            lblAbgerechnetInformation.setVisible(false);
        }
    }

    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        final CidsBillingSearchStatement billingSearch = new CidsBillingSearchStatement(txtCustomerName.getText()
                        .trim());

        final Object abrechnungsturnus = cboAbrechnungsturnus.getSelectedItem();
        String abrechnungsturnusID = "";
        if (abrechnungsturnus instanceof CidsBean) {
            abrechnungsturnusID = ((CidsBean)abrechnungsturnus).getProperty("id").toString();
        }
        billingSearch.setAbrechnungsturnusID(abrechnungsturnusID);

        billingSearch.setGeschaeftsbuchnummer(txtGeschaeftsbuchnummer.getText());
        billingSearch.setProjekt(txtProjekt.getText());

        final Object user = cboBenutzer.getSelectedItem();
        String userID = "";
        if (user instanceof CidsBean) {
            userID = ((CidsBean)user).getProperty("id").toString();
        }
        billingSearch.setUserID(userID);

        final Date[] fromDate_tillDate = pnlTimeFilters.chooseDates();
        billingSearch.setFrom(fromDate_tillDate[0]);
        billingSearch.setTill(fromDate_tillDate[1]);

        billingSearch.setVerwendungszweckKeys(
            pnlVerwendungszweck.createSelectedVerwendungszweckKeysStringArray());

        billingSearch.setKostentyp(chooseKostentyp());

        if ((cboAbgerechnet.isSelected() && cboNichtAbgerechnet.isSelected())) {
            billingSearch.setShowAbgerechneteBillings(null);
        } else if (cboAbgerechnet.isSelected()) {
            billingSearch.setShowAbgerechneteBillings(true);
        } else if (cboNichtAbgerechnet.isSelected()) {
            billingSearch.setShowAbgerechneteBillings(false);
        } else {
            billingSearch.setShowAbgerechneteBillings(null);
        }

        if ((cboStorniert.isSelected() && cboNichtStorniert.isSelected())) {
            billingSearch.setShowStornierteBillings(null);
        } else if (cboStorniert.isSelected()) {
            billingSearch.setShowStornierteBillings(true);
        } else if (cboNichtStorniert.isSelected()) {
            billingSearch.setShowStornierteBillings(false);
        } else {
            billingSearch.setShowStornierteBillings(null);
        }

        return billingSearch;
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        return getServerSearch();
    }

    @Override
    public void searchStarted() {
    }

    @Override
    public void searchDone(final int numberOfResults) {
    }

    @Override
    public void searchCanceled() {
    }

    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    private void setAbrechnungsturnusIntoComboBox() {
        try {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "billing_abrechnungsturnus",
                    getConnectionContext());
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " \n";
            query += "FROM " + MB_MC.getTableName();
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(query.toString(), 0, getConnectionContext());
            for (final MetaObject abrechnungsturnus : metaObjects) {
                cboAbrechnungsturnus.addItem(abrechnungsturnus.getBean());
            }
        } catch (ConnectionException ex) {
            LOG.error(ex, ex);
        }
        ((DefaultComboBoxModel)cboAbrechnungsturnus.getModel()).insertElementAt(" ", 0);
        cboAbrechnungsturnus.setSelectedIndex(0);
    }

    /**
     * DOCUMENT ME!
     */
    private void setUsersIntoComboBox() {
        try {
            final MetaClass MB_MC = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "billing_kunden_logins",
                    getConnectionContext());
            String query = "SELECT " + MB_MC.getID() + ", " + MB_MC.getPrimaryKey() + " \n";
            query += "FROM " + MB_MC.getTableName() + "  order by lower(name)";
            final MetaObject[] metaObjects = SessionManager.getProxy()
                        .getMetaObjectByQuery(query, 0, getConnectionContext());
            for (final MetaObject abrechnungsturnus : metaObjects) {
                cboBenutzer.addItem(abrechnungsturnus.getBean());
            }
        } catch (ConnectionException ex) {
            LOG.error(ex, ex);
        }
        ((DefaultComboBoxModel)cboBenutzer.getModel()).insertElementAt(" ", 0);
        cboBenutzer.setSelectedIndex(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBillingSearchStatement.Kostentyp chooseKostentyp() {
        if (cboKostenfrei.isSelected() == cboKostenpflichtig.isSelected()) {
            return CidsBillingSearchStatement.Kostentyp.IGNORIEREN;
        } else if (cboKostenfrei.isSelected()) {
            return CidsBillingSearchStatement.Kostentyp.KOSTENFREI;
        } else {
            return CidsBillingSearchStatement.Kostentyp.KOSTENPFLICHTIG;
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(BillingWindowSearch.class, "BillingWindowSearch.name");
    }

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
