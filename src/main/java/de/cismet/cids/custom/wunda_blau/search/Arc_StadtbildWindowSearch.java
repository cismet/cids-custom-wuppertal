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
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class Arc_StadtbildWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Arc_StadtbildWindowSearch.class);

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private ImageIcon icon;
    private boolean geoSearchEnabled;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.wunda_blau.search.Arc_StadtbildTimeTabs arc_StadtbilderTimeTabs1;
    private javax.swing.JButton btnAddSearchWord;
    private javax.swing.JButton btnRemSearchWord;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.JComboBox cboImageNrFrom;
    private javax.swing.JComboBox cboImageNrTo;
    private javax.swing.JCheckBox chboLuftbildschraegaufnahme;
    private javax.swing.JCheckBox chboLuftbildsenkrechtaufnahme;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlImageNumber;
    private javax.swing.JPanel pnlKindOfImage;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchWords;
    private javax.swing.JPanel pnlStrassenzuordnung;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Arc_StadtbildWindowSearch.
     */
    public Arc_StadtbildWindowSearch() {
        initComponents();

        final JPanel pnlSearchCancel = new SearchControlPanel(this);
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
        pnlButtons.add(pnlSearchCancel);

        metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "mauer"); // NOI18N

        byte[] iconDataFromMetaclass = new byte[] {};

        if (metaClass != null) {
            iconDataFromMetaclass = metaClass.getIconData();
        }

        if (iconDataFromMetaclass.length > 0) {
            LOG.info("Using icon from metaclass.");                                                              // NOI18N
            icon = new ImageIcon(metaClass.getIconData());
        } else {
            LOG.warn("Metaclass icon is not set. Trying to load default icon.");                                 // NOI18N
            final URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png"); // NOI18N

            if (urlToIcon != null) {
                icon = new ImageIcon(urlToIcon);
            } else {
                icon = new ImageIcon(new byte[] {});
            }
        }

        pnlButtons.add(Box.createHorizontalStrut(5));

        mappingComponent = CismapBroker.getInstance().getMappingComponent();
        geoSearchEnabled = mappingComponent != null;
        if (geoSearchEnabled) {
            final MauernCreateSearchGeometryListener mauernSearchGeometryListener =
                new MauernCreateSearchGeometryListener(mappingComponent,
                    new MauernSearchTooltip(icon));
            mauernSearchGeometryListener.addPropertyChangeListener(this);
            btnGeoSearch = new GeoSearchButton(
                    MauernCreateSearchGeometryListener.MAUERN_CREATE_SEARCH_GEOMETRY,
                    mappingComponent,
                    null,
                    org.openide.util.NbBundle.getMessage(
                        MauernWindowSearch.class,
                        "MauernWindowSearch.btnGeoSearch.toolTipText")); // NOI18N
            pnlButtons.add(btnGeoSearch);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlKindOfImage = new javax.swing.JPanel();
        chboLuftbildschraegaufnahme = new javax.swing.JCheckBox();
        chboLuftbildsenkrechtaufnahme = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        pnlImageNumber = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboImageNrFrom = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        cboImageNrTo = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlSearchWords = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        btnAddSearchWord = new javax.swing.JButton();
        btnRemSearchWord = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        arc_StadtbilderTimeTabs1 = new de.cismet.cids.custom.wunda_blau.search.Arc_StadtbildTimeTabs();
        pnlStrassenzuordnung = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        pnlFooter = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlKindOfImage.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Arc_StadtbildWindowSearch.class,
                    "Arc_StadtbildWindowSearch.pnlKindOfImage.border.title"))); // NOI18N
        pnlKindOfImage.setLayout(new javax.swing.BoxLayout(pnlKindOfImage, javax.swing.BoxLayout.PAGE_AXIS));

        chboLuftbildschraegaufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildschraegaufnahme,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.chboLuftbildschraegaufnahme.text")); // NOI18N
        pnlKindOfImage.add(chboLuftbildschraegaufnahme);

        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildsenkrechtaufnahme,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.chboLuftbildsenkrechtaufnahme.text")); // NOI18N
        chboLuftbildsenkrechtaufnahme.setEnabled(false);
        pnlKindOfImage.add(chboLuftbildsenkrechtaufnahme);

        jCheckBox3.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox3,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jCheckBox3.text")); // NOI18N
        pnlKindOfImage.add(jCheckBox3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlKindOfImage, gridBagConstraints);

        pnlImageNumber.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Arc_StadtbildWindowSearch.class,
                    "Arc_StadtbildWindowSearch.pnlImageNumber.border.title"))); // NOI18N
        pnlImageNumber.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlImageNumber.add(jLabel1, gridBagConstraints);

        cboImageNrFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "616789" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 20);
        pnlImageNumber.add(cboImageNrFrom, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlImageNumber.add(jLabel2, gridBagConstraints);

        cboImageNrTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "616799" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlImageNumber.add(cboImageNrTo, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlImageNumber.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlImageNumber, gridBagConstraints);

        pnlSearchWords.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Arc_StadtbildWindowSearch.class,
                    "Arc_StadtbildWindowSearch.pnlSearchWords.border.title"))); // NOI18N
        pnlSearchWords.setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Fachwerkhaus", "Denkmal" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        jScrollPane2.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        pnlSearchWords.add(jScrollPane2, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnAddSearchWord.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(btnAddSearchWord, gridBagConstraints);

        btnRemSearchWord.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel1.add(btnRemSearchWord, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlSearchWords.add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlSearchWords.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlSearchWords, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 17, 5, 22);
        pnlScrollPane.add(arc_StadtbilderTimeTabs1, gridBagConstraints);

        pnlStrassenzuordnung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Arc_StadtbildWindowSearch.class,
                    "Arc_StadtbildWindowSearch.pnlStrassenzuordnung.border.title"))); // NOI18N
        pnlStrassenzuordnung.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(jLabel3, gridBagConstraints);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Schellenbecker Str." }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(jComboBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox4,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jCheckBox4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(jCheckBox4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.jLabel4.text")); // NOI18N
        jLabel4.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(jLabel4, gridBagConstraints);

        jComboBox2.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(jComboBox2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlStrassenzuordnung, gridBagConstraints);

        pnlFooter.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            cbMapSearch,
            org.openide.util.NbBundle.getMessage(
                Arc_StadtbildWindowSearch.class,
                "Arc_StadtbildWindowSearch.cbMapSearch.text")); // NOI18N
        cbMapSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbMapSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 5, 0);
        pnlFooter.add(cbMapSearch, gridBagConstraints);

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlFooter.add(pnlButtons, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlFooter.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlFooter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(filler1, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbMapSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbMapSearchActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbMapSearchActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",      // NOI18N
                "Administratoren", // NOI18N
                "admin",           // NOI18N
                "kif");            // NOI18N
            final JScrollPane jsp = new JScrollPane(new Arc_StadtbildWindowSearch());
            DevelopmentTools.showTestFrame(jsp, 800, 1000);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
        return null;
    }

    @Override
    public ImageIcon getIcon() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
        return icon;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(Arc_StadtbildWindowSearch.class, "Arc_StadtbildWindowSearch.name");
    }

    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
        return null;
    }

    @Override
    public void searchStarted() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
    }

    @Override
    public void searchDone(final int numberOfResults) {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
    }

    @Override
    public void searchCanceled() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
    }

    @Override
    public boolean suppressEmptyResultMessage() {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
        return false;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        LOG.fatal("Not supported yet.", new Exception()); // NOI18N
    }

    @Override
    public boolean checkActionTag() {
        LOG.fatal("Arc_StadtbildWindowSearch.checkActionTag: Not supported yet.", new Exception()); // NOI18N
        return true;
    }
}
