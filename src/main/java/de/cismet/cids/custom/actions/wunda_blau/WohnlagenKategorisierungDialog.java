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
package de.cismet.cids.custom.actions.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.tools.CacheException;
import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Color;
import java.awt.GridBagConstraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.actions.WohnlagenKategorisierungServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.cidslayer.CidsLayerFeature;

import de.cismet.cismap.commons.featureservice.AbstractFeatureService;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WohnlagenKategorisierungDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    public static final Logger LOG = Logger.getLogger(WohnlagenKategorisierungDialog.class);

    private static final MetaClass MC_WOHNLAGE;
    private static final MetaClass MC_WOHNLAGE_KATEGORIE;

    static {
        MetaClass mcWohnlage = null;
        try {
            mcWohnlage = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "WOHNLAGE");
        } catch (final Exception ex) {
            LOG.error("Could get MetaClass (WOHNLAGE)!", ex);
        }
        MC_WOHNLAGE = mcWohnlage;

        MetaClass mcWohnlageKategorie = null;
        try {
            mcWohnlageKategorie = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "WOHNLAGE_KATEGORIE");
        } catch (final Exception ex) {
            LOG.error("Could get MetaClass (WOHNLAGE_KATEGORIE)!", ex);
        }
        MC_WOHNLAGE_KATEGORIE = mcWohnlageKategorie;
    }

    //~ Instance fields --------------------------------------------------------

    private final Map<ButtonModel, CidsBean> buttonToBeanMap = new HashMap<ButtonModel, CidsBean>();

    private final Collection<CidsLayerFeature> cidsLayerFeatures;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOk;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form WohnlagenKategorisierungDialog.
     *
     * @param  parent             DOCUMENT ME!
     * @param  cidsLayerFeatures  DOCUMENT ME!
     * @param  kategorieToSelect  DOCUMENT ME!
     */
    public WohnlagenKategorisierungDialog(final java.awt.Frame parent,
            final Collection<CidsLayerFeature> cidsLayerFeatures,
            final CidsBean kategorieToSelect) {
        super(parent, true);
        this.cidsLayerFeatures = cidsLayerFeatures;

        initComponents();

        MetaObject[] metaObjects;
        try {
            final String query = "SELECT " + MC_WOHNLAGE_KATEGORIE.getID() + "," + MC_WOHNLAGE_KATEGORIE.getPrimaryKey()
                        + " FROM " + MC_WOHNLAGE_KATEGORIE.getTableName()
                        + " WHERE schluessel != 'keine' ORDER BY reihenfolge";
            metaObjects = MetaObjectCache.getInstance().getMetaObjectsByQuery(query, MC_WOHNLAGE_KATEGORIE.getDomain());
        } catch (final CacheException ex) {
            metaObjects = new MetaObject[0];
        }

        jPanel3.removeAll();
        int count = 0;
        for (final MetaObject metaObject : metaObjects) {
            final CidsBean kategorie = metaObject.getBean();

            // CONSTRUCT
            final JRadioButton btnKategorie = new JRadioButton();
            final JLabel lblKategorieColor = new JLabel();
            final JLabel lblKategorieName = new JLabel();

            // INIT
            btnKategorie.addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        inputChanged();
                    }
                });
            lblKategorieColor.setText(" ");
            lblKategorieColor.setBackground(new Color(((Integer)kategorie.getProperty("farbcode"))));
            lblKategorieColor.setOpaque(true);
            lblKategorieName.setText((String)kategorie.getProperty("name"));

            // CONSTRAINTS
            GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = count;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            jPanel3.add(btnKategorie, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = count;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
            jPanel3.add(lblKategorieColor, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = count;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            jPanel3.add(lblKategorieName, gridBagConstraints);

            count++;

            // SELECTION
            buttonGroup1.add(btnKategorie);
            if ((kategorieToSelect != null)
                        && kategorieToSelect.getProperty("schluessel").equals(kategorie.getProperty("schluessel"))) {
                btnKategorie.setSelected(true);
            }

            // MAP
            buttonToBeanMap.put(btnKategorie.getModel(), kategorie);
        }

        getRootPane().setDefaultButton(btnOk);

        final int size = cidsLayerFeatures.size();
        if (size == 1) {
            jLabel1.setText("<html>Es wurde eine Wohnlage markiert.<br/>Wählen Sie eine der Kategorien:");
        } else {
            jLabel1.setText("<html>Es wurden #ANZAHL# Wohnlagen markiert.<br/>Wählen Sie eine der Kategorien:"
                        .replaceAll("#ANZAHL#", Integer.toString(size)));
        }

        jTextArea1.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    inputChanged();
                }

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    inputChanged();
                }

                @Override
                public void changedUpdate(final DocumentEvent arg0) {
                    inputChanged();
                }
            });
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel6.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel3.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRadioButton1);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton1,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jRadioButton1.text")); // NOI18N
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jRadioButton1, gridBagConstraints);

        jLabel10.setBackground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel10.text")); // NOI18N
        jLabel10.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(jLabel10, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jLabel5, gridBagConstraints);

        buttonGroup1.add(jRadioButton2);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton2,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jRadioButton2.text")); // NOI18N
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jRadioButton2, gridBagConstraints);

        jLabel11.setBackground(new java.awt.Color(255, 153, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel11.text")); // NOI18N
        jLabel11.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(jLabel11, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jLabel7, gridBagConstraints);

        buttonGroup1.add(jRadioButton3);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton3,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jRadioButton3.text")); // NOI18N
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jRadioButton3, gridBagConstraints);

        jLabel12.setBackground(new java.awt.Color(0, 255, 0));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel12.text")); // NOI18N
        jLabel12.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(jLabel12, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jLabel8, gridBagConstraints);

        buttonGroup1.add(jRadioButton4);
        org.openide.awt.Mnemonics.setLocalizedText(
            jRadioButton4,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jRadioButton4.text")); // NOI18N
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jRadioButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jRadioButton4, gridBagConstraints);

        jLabel13.setBackground(new java.awt.Color(51, 153, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel13,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel13.text")); // NOI18N
        jLabel13.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel3.add(jLabel13, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2, 5, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel1.add(jButton2);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnOk,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.btnOk.text")); // NOI18N
        btnOk.setEnabled(false);
        btnOk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOkActionPerformed(evt);
                }
            });
        jPanel1.add(btnOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(jPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, " ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                WohnlagenKategorisierungDialog.class,
                "WohnlagenKategorisierungDialog.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        dispose();
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOkActionPerformed
        final CidsBean kategorie = buttonToBeanMap.get(buttonGroup1.getSelection());

        final Enumeration<AbstractButton> buttonEnumeration = buttonGroup1.getElements();
        while (buttonEnumeration.hasMoreElements()) {
            final AbstractButton button = buttonEnumeration.nextElement();
            button.setEnabled(false);
        }
        btnOk.setEnabled(false);
        jButton2.setEnabled(false);

        jLabel2.setText("Kategorie wird gesetzt...");

        AbstractFeatureService tmpService = null;
        for (final CidsLayerFeature cidsLayerFeature : cidsLayerFeatures) {
            if (tmpService == null) {
                tmpService = (AbstractFeatureService)cidsLayerFeature.getLayerProperties().getFeatureService();
                break;
            }
        }
        final AbstractFeatureService service = tmpService;

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        final Collection<MetaObjectNode> wohnlageNodes = new ArrayList<MetaObjectNode>();
                        for (final CidsLayerFeature cidsLayerFeature : cidsLayerFeatures) {
                            final int wohnlageId = (Integer)cidsLayerFeature.getProperty("id");
                            wohnlageNodes.add(new MetaObjectNode(
                                    MC_WOHNLAGE.getDomain(),
                                    wohnlageId,
                                    MC_WOHNLAGE.getId()));
                            if (kategorie != null) {
                                cidsLayerFeature.setProperty("farbcode", kategorie.getProperty("farbcode"));
                                cidsLayerFeature.setProperty("kategorie_id", kategorie.getProperty("id"));
                            }
                        }
                        if (service != null) {
                            service.refreshFeatures();
                        }
                        final String trimmedTextAreaText = jTextArea1.getText().trim();
                        final String bemerkung = trimmedTextAreaText.isEmpty() ? null : trimmedTextAreaText;

                        final MetaObjectNode kategorieNode = (kategorie == null) ? null : new MetaObjectNode(kategorie);
                        SessionManager.getProxy()
                                .executeTask(
                                    WohnlagenKategorisierungServerAction.TASK_NAME,
                                    "WUNDA_BLAU",
                                    null,
                                    new ServerActionParameter<MetaObjectNode>(
                                        WohnlagenKategorisierungServerAction.ParameterType.KATEGORIE.toString(),
                                        kategorieNode),
                                    new ServerActionParameter<Collection<MetaObjectNode>>(
                                        WohnlagenKategorisierungServerAction.ParameterType.WOHNLAGEN.toString(),
                                        wohnlageNodes),
                                    new ServerActionParameter<String>(
                                        WohnlagenKategorisierungServerAction.ParameterType.BEMERKUNG.toString(),
                                        bemerkung));
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (final Exception ex) {
                        final String title = "Unerwarteter Fehler";
                        final String message =
                            "Beim Setzen der Kategorie ist es zu unerwartetem einem Fehler gekommen.";
                        LOG.error(message, ex);
                        final ErrorInfo info = new ErrorInfo(title, message, null, null, ex, Level.SEVERE, null);
                        JXErrorPane.showDialog(ComponentRegistry.getRegistry().getMainWindow(), info);

                        if (service != null) {
                            service.refresh();
                        }
                    } finally {
                        dispose();
                    }
                }
            }.execute();
    } //GEN-LAST:event_btnOkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton1ActionPerformed
        inputChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton2ActionPerformed
        inputChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton3ActionPerformed
        inputChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jRadioButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jRadioButton4ActionPerformed
        inputChanged();
    }                                                                                 //GEN-LAST:event_jRadioButton4ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void inputChanged() {
        btnOk.setEnabled((buttonGroup1.getSelection() != null) || !jTextArea1.getText().trim().isEmpty());
    }
}
