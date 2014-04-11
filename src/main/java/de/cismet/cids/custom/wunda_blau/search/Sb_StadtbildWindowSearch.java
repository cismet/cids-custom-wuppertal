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
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.swingx.JXList;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditor;
import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditorAddSuchwortDialog;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.MetaObjectNodesStadtbildSerieSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class Sb_StadtbildWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_StadtbildWindowSearch.class);
    private static final String ACTION_TAG = "custom.stadtbilder.search@WUNDA_BLAU";
    private static final CidsBean WUPPERTAL = getOrtWupertal();

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private ImageIcon icon;
    private boolean geoSearchEnabled;
    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSuchwort;
    private javax.swing.JButton btnRemoveSuchwort;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.JComboBox cboImageNrFrom;
    private javax.swing.JComboBox cboImageNrTo;
    private javax.swing.JComboBox cboOrt;
    private javax.swing.JComboBox cboStreet;
    private javax.swing.JCheckBox chbOutsideWuppertal;
    private javax.swing.JCheckBox chboBodennaheAufnahme;
    private javax.swing.JCheckBox chboLuftbildschraegaufnahme;
    private javax.swing.JCheckBox chboLuftbildsenkrechtaufnahme;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblOrtsname;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JList lstSuchworte;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlCtrlButtons1;
    private javax.swing.JPanel pnlFooter;
    private javax.swing.JPanel pnlImageNumber;
    private javax.swing.JPanel pnlKindOfImage;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchWords;
    private javax.swing.JPanel pnlStrassenzuordnung;
    private de.cismet.cids.custom.wunda_blau.search.Sb_StadtbildTimeTabs sb_StadtbilderTimeTabs;
    private javax.swing.JTextField txtHausnummer;
    private javax.swing.JTextField txtImageNrFrom;
    private javax.swing.JTextField txtImageNrTo;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_StadtbildWindowSearch.
     */
    public Sb_StadtbildWindowSearch() {
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
            final Sb_StadtbildserieCreateSearchGeometryListener stadtbildserieCreateSearchGeometryListener =
                new Sb_StadtbildserieCreateSearchGeometryListener(mappingComponent,
                    new MauernSearchTooltip(icon));
            stadtbildserieCreateSearchGeometryListener.addPropertyChangeListener(this);
            btnGeoSearch = new GeoSearchButton(
                    Sb_StadtbildserieCreateSearchGeometryListener.STADTBILDSERIE_CREATE_SEARCH_GEOMETRY,
                    mappingComponent,
                    null,
                    org.openide.util.NbBundle.getMessage(
                        MauernWindowSearch.class,
                        "MauernWindowSearch.btnGeoSearch.toolTipText")); // NOI18N
            pnlButtons.add(btnGeoSearch);
        }

        bindingGroup.unbind();

        try {
            cidsBean = CidsBeanSupport.createNewCidsBeanFromTableName("sb_stadtbildserie");
            LOG.fatal("Started binding for StadtbildWindowSearch");
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);
            LOG.fatal("Ended binding for StadtbildWindowSearch");
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        bindingGroup.bind();

        cboOrt.setSelectedItem(WUPPERTAL);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlKindOfImage = new javax.swing.JPanel();
        chboLuftbildschraegaufnahme = new javax.swing.JCheckBox();
        chboLuftbildsenkrechtaufnahme = new javax.swing.JCheckBox();
        chboBodennaheAufnahme = new javax.swing.JCheckBox();
        pnlImageNumber = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cboImageNrFrom = new FastBindableReferenceCombo("%1$2s", new String[] { "BILDNUMMER" });
        jLabel2 = new javax.swing.JLabel();
        cboImageNrTo = new FastBindableReferenceCombo("%1$2s", new String[] { "BILDNUMMER" });
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        txtImageNrTo = new javax.swing.JTextField();
        txtImageNrFrom = new javax.swing.JTextField();
        pnlSearchWords = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSuchworte = new Sb_stadtbildserieEditor.JXListBugFixes();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlCtrlButtons1 = new javax.swing.JPanel();
        btnAddSuchwort = new javax.swing.JButton();
        btnRemoveSuchwort = new javax.swing.JButton();
        sb_StadtbilderTimeTabs = new de.cismet.cids.custom.wunda_blau.search.Sb_StadtbildTimeTabs();
        pnlStrassenzuordnung = new javax.swing.JPanel();
        lblStrasse = new javax.swing.JLabel();
        cboStreet = new FastBindableReferenceCombo();
        chbOutsideWuppertal = new javax.swing.JCheckBox();
        lblOrtsname = new javax.swing.JLabel();
        cboOrt = new FastBindableReferenceCombo();
        lblHausnummer = new javax.swing.JLabel();
        txtHausnummer = new javax.swing.JTextField();
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
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlKindOfImage.border.title"))); // NOI18N
        pnlKindOfImage.setLayout(new javax.swing.BoxLayout(pnlKindOfImage, javax.swing.BoxLayout.PAGE_AXIS));

        chboLuftbildschraegaufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildschraegaufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboLuftbildschraegaufnahme.text")); // NOI18N
        pnlKindOfImage.add(chboLuftbildschraegaufnahme);

        org.openide.awt.Mnemonics.setLocalizedText(
            chboLuftbildsenkrechtaufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboLuftbildsenkrechtaufnahme.text")); // NOI18N
        chboLuftbildsenkrechtaufnahme.setEnabled(false);
        pnlKindOfImage.add(chboLuftbildsenkrechtaufnahme);

        chboBodennaheAufnahme.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chboBodennaheAufnahme,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chboBodennaheAufnahme.text")); // NOI18N
        pnlKindOfImage.add(chboBodennaheAufnahme);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlKindOfImage, gridBagConstraints);

        pnlImageNumber.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlImageNumber.border.title"))); // NOI18N
        pnlImageNumber.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlImageNumber.add(jLabel1, gridBagConstraints);

        ((FastBindableReferenceCombo)cboImageNrFrom).setSorted(true);
        cboImageNrFrom.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 20);
        pnlImageNumber.add(cboImageNrFrom, gridBagConstraints);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cboImageNrFrom);
        cboImageNrFrom.setVisible(false);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlImageNumber.add(jLabel2, gridBagConstraints);

        ((FastBindableReferenceCombo)cboImageNrTo).setSorted(true);
        cboImageNrTo.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        pnlImageNumber.add(cboImageNrTo, gridBagConstraints);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cboImageNrTo);
        cboImageNrTo.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlImageNumber.add(filler2, gridBagConstraints);

        txtImageNrTo.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtImageNrTo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 20);
        pnlImageNumber.add(txtImageNrTo, gridBagConstraints);

        txtImageNrFrom.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtImageNrFrom.text")); // NOI18N
        txtImageNrFrom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtImageNrFromActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 20);
        pnlImageNumber.add(txtImageNrFrom, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlImageNumber, gridBagConstraints);

        pnlSearchWords.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlSearchWords.border.title"))); // NOI18N
        pnlSearchWords.setLayout(new java.awt.GridBagLayout());

        lstSuchworte.setModel(new DefaultListModel());
        jScrollPane2.setViewportView(lstSuchworte);
        ((JXList)lstSuchworte).setAutoCreateRowSorter(true);
        ((JXList)lstSuchworte).setSortOrder(SortOrder.ASCENDING);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 20);
        pnlSearchWords.add(jScrollPane2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlSearchWords.add(filler3, gridBagConstraints);

        pnlCtrlButtons1.setOpaque(false);
        pnlCtrlButtons1.setLayout(new java.awt.GridBagLayout());

        btnAddSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnAddSuchwort,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnAddSuchwort.text"));                                              // NOI18N
        btnAddSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnAddSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 10);
        pnlCtrlButtons1.add(btnAddSuchwort, gridBagConstraints);

        btnRemoveSuchwort.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnRemoveSuchwort,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.btnRemoveSuchwort.text"));                                              // NOI18N
        btnRemoveSuchwort.setPreferredSize(new java.awt.Dimension(46, 21));
        btnRemoveSuchwort.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveSuchwortActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 5, 10);
        pnlCtrlButtons1.add(btnRemoveSuchwort, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        pnlSearchWords.add(pnlCtrlButtons1, gridBagConstraints);

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
        pnlScrollPane.add(sb_StadtbilderTimeTabs, gridBagConstraints);

        pnlStrassenzuordnung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    Sb_StadtbildWindowSearch.class,
                    "Sb_StadtbildWindowSearch.pnlStrassenzuordnung.border.title"))); // NOI18N
        pnlStrassenzuordnung.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrasse,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(lblStrasse, gridBagConstraints);

        ((FastBindableReferenceCombo)cboStreet).setSorted(true);
        cboStreet.setEditable(true);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                cboStreet,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(cboStreet, gridBagConstraints);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cboStreet);

        org.openide.awt.Mnemonics.setLocalizedText(
            chbOutsideWuppertal,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.chbOutsideWuppertal.text")); // NOI18N
        chbOutsideWuppertal.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chbOutsideWuppertalStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(chbOutsideWuppertal, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblOrtsname,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblOrtsname.text")); // NOI18N
        lblOrtsname.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(lblOrtsname, gridBagConstraints);

        ((FastBindableReferenceCombo)cboOrt).setSorted(true);
        cboOrt.setEditable(true);
        cboOrt.setEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_ONCE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ort}"),
                cboOrt,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(cboOrt, gridBagConstraints);
        StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cboOrt);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblHausnummer,
            org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.lblHausnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlStrassenzuordnung.add(lblHausnummer, gridBagConstraints);

        txtHausnummer.setText(org.openide.util.NbBundle.getMessage(
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.txtHausnummer.text")); // NOI18N
        txtHausnummer.setPreferredSize(new java.awt.Dimension(56, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlStrassenzuordnung.add(txtHausnummer, gridBagConstraints);

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
                Sb_StadtbildWindowSearch.class,
                "Sb_StadtbildWindowSearch.cbMapSearch.text")); // NOI18N
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

        bindingGroup.bind();
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
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddSuchwortActionPerformed
        final Sb_stadtbildserieEditorAddSuchwortDialog dialog = new Sb_stadtbildserieEditorAddSuchwortDialog((Frame)
                SwingUtilities.getWindowAncestor(this),
                true);
        final CidsBean newSuchwort = dialog.showDialog();
        if (newSuchwort != null) {
            final DefaultListModel dlm = (DefaultListModel)lstSuchworte.getModel();
            dlm.addElement(newSuchwort);
        }
    }                                                                                  //GEN-LAST:event_btnAddSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveSuchwortActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveSuchwortActionPerformed
        final DefaultListModel dlm = (DefaultListModel)lstSuchworte.getModel();

        if (this.lstSuchworte.getSelectedIndices().length > 0) {
            final int[] selectedIndices = lstSuchworte.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                dlm.removeElementAt(selectedIndices[i]);
            }
        }
    } //GEN-LAST:event_btnRemoveSuchwortActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtImageNrFromActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtImageNrFromActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtImageNrFromActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chbOutsideWuppertalStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_chbOutsideWuppertalStateChanged
        if (chbOutsideWuppertal.isSelected()) {
            // outside of Wuppertal
            cboStreet.setEnabled(false);
            cboStreet.setSelectedItem(null);
            lblStrasse.setEnabled(false);
            txtHausnummer.setEnabled(false);
            txtHausnummer.setText("");
            lblHausnummer.setEnabled(false);

            cboOrt.setEnabled(true);
            lblOrtsname.setEnabled(true);
        } else {
            // inside of Wuppertal
            cboStreet.setEnabled(true);
            lblStrasse.setEnabled(true);
            txtHausnummer.setEnabled(true);
            lblHausnummer.setEnabled(true);

            cboOrt.setEnabled(false);
            cboOrt.setSelectedItem(WUPPERTAL);
            lblOrtsname.setEnabled(false);
        }
    } //GEN-LAST:event_chbOutsideWuppertalStateChanged

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
            final JScrollPane jsp = new JScrollPane(new Sb_StadtbildWindowSearch());
            DevelopmentTools.showTestFrame(jsp, 800, 1000);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        final MetaObjectNodesStadtbildSerieSearchStatement stadtbildSerieSearchStatement =
            new MetaObjectNodesStadtbildSerieSearchStatement(SessionManager.getSession().getUser());

        final ArrayList<MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp> bildtyp =
            new ArrayList<MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp>();
        if (chboBodennaheAufnahme.isSelected()) {
            bildtyp.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.BODENNAH);
        }
        if (chboLuftbildschraegaufnahme.isSelected()) {
            bildtyp.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.LUFTSCHRAEG);
        }
        if (chboLuftbildsenkrechtaufnahme.isSelected()) {
            bildtyp.add(MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.LUFTSENK);
        }
        stadtbildSerieSearchStatement.setBildtypen(bildtyp);

        final ArrayList<Integer> suchwortIDs = new ArrayList<Integer>();
        for (final Object object : ((DefaultListModel<CidsBean>)lstSuchworte.getModel()).toArray()) {
            final Integer id = ((CidsBean)object).getPrimaryKeyValue();
            suchwortIDs.add(id);
        }
        stadtbildSerieSearchStatement.setSuchwoerterIDs(suchwortIDs);

        final Date[] fromDate_tillDate = sb_StadtbilderTimeTabs.chooseDates();
        stadtbildSerieSearchStatement.setFrom(fromDate_tillDate[0]);
        stadtbildSerieSearchStatement.setTill(fromDate_tillDate[1]);

        final CidsBean strasse = (CidsBean)cboStreet.getSelectedItem();
        if (strasse != null) {
            stadtbildSerieSearchStatement.setStreetID(strasse.getPrimaryKeyValue().toString());
        }

        final CidsBean ort = (CidsBean)cboOrt.getSelectedItem();
        if (ort != null) {
            stadtbildSerieSearchStatement.setOrtID(ort.getPrimaryKeyValue().toString());
        }

        final String hausnummer = txtHausnummer.getText();
        stadtbildSerieSearchStatement.setHausnummer(hausnummer);

        final String imageNrTo = txtImageNrTo.getText();
        stadtbildSerieSearchStatement.setImageNrTo(imageNrTo);

        final String imageNrFrom = txtImageNrFrom.getText();
        stadtbildSerieSearchStatement.setImageNrFrom(imageNrFrom);

        System.out.println(stadtbildSerieSearchStatement.generateQuery());
        return stadtbildSerieSearchStatement;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(Sb_StadtbildWindowSearch.class, "Sb_StadtbildWindowSearch.name");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        return getServerSearch();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void searchStarted() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  numberOfResults  DOCUMENT ME!
     */
    @Override
    public void searchDone(final int numberOfResults) {
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void searchCanceled() {
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (Sb_StadtbildserieCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
//                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
//                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getOrtWupertal() {
        final MetaClass ortClass = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "sb_ort");
        final StringBuffer wuppertalQuery = new StringBuffer("select ").append(ortClass.getId())
                    .append(", ")
                    .append(ortClass.getPrimaryKey())
                    .append(" from ")
                    .append(ortClass.getTableName())
                    .append(" where name ilike 'Wuppertal'");
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: wuppertalQuery:" + wuppertalQuery.toString());
        }
        final MetaObject[] wuppertal;
        try {
            wuppertal = SessionManager.getProxy().getMetaObjectByQuery(wuppertalQuery.toString(), 0);
            if (wuppertal.length > 0) {
                return wuppertal[0].getBean();
            }
        } catch (ConnectionException ex) {
            LOG.error(ex, ex);
        }
        return null;
    }
}
