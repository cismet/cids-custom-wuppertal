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

import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.CidsLandParcelSearchStatement;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class LandParcelWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    SearchControlListener,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LandParcelWindowSearch.class);

    //~ Instance fields --------------------------------------------------------

    private MetaClass mc = null;
    private ImageIcon icon;
    private SearchControlPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNewSearch;
    private javax.swing.JCheckBox chkActual;
    private javax.swing.JCheckBox chkHistorical;
    private javax.swing.JCheckBox chkMap;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcFrom;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel panCommand;
    private javax.swing.JPanel panParcelType;
    private javax.swing.JPanel panSearch;
    private javax.swing.JPanel panTime;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form LandParcelWindowSearch.
     */
    public LandParcelWindowSearch() {
        try {
            mc = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "FLURSTUECK"); // TODO ask for correct
            // Name
            icon = new ImageIcon(mc.getIconData());

            initComponents();

            initDateChoosers();

            pnlSearchCancel = new SearchControlPanel(this);
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
            panCommand.add(Box.createHorizontalStrut(5));
            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                final LandParcelSearchGeometryListener landParcelSearchGeometryListener =
                    new LandParcelSearchGeometryListener(mappingComponent, new LandParcelSearchTooltip(icon));
                landParcelSearchGeometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        LandParcelSearchGeometryListener.LAND_PARCEL_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            LandParcelWindowSearch.class,
                            "LandParcelWindowSearch.btnGeoSearch.toolTipText"));
                panCommand.add(btnGeoSearch);
            }
        } catch (Exception exception) {
            log.warn("Error in Constructor of LandParcelWindowSearch", exception);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initDateChoosers() {
        final java.util.Date date = new java.util.Date(System.currentTimeMillis());
        date.setMonth(0);
        date.setDate(1);
        dcFrom.setDate(date);
        dcTo.setDate(new java.util.Date(System.currentTimeMillis()));
    }
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panSearch = new javax.swing.JPanel();
        panParcelType = new javax.swing.JPanel();
        chkActual = new javax.swing.JCheckBox();
        chkHistorical = new javax.swing.JCheckBox();
        panTime = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        dcFrom = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcTo = new de.cismet.cids.editors.DefaultBindableDateChooser();
        panCommand = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        chkMap = new javax.swing.JCheckBox();
        btnNewSearch = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        panSearch.setLayout(new java.awt.GridBagLayout());

        panParcelType.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    LandParcelWindowSearch.class,
                    "LandParcelWindowSearch.panParcelType.border.title"))); // NOI18N
        panParcelType.setLayout(new java.awt.GridBagLayout());

        chkActual.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            chkActual,
            org.openide.util.NbBundle.getMessage(
                LandParcelWindowSearch.class,
                "LandParcelWindowSearch.chkActual.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panParcelType.add(chkActual, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkHistorical,
            org.openide.util.NbBundle.getMessage(
                LandParcelWindowSearch.class,
                "LandParcelWindowSearch.chkHistorical.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panParcelType.add(chkHistorical, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(panParcelType, gridBagConstraints);

        panTime.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    LandParcelWindowSearch.class,
                    "LandParcelWindowSearch.panTime.border.title"))); // NOI18N
        panTime.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(LandParcelWindowSearch.class, "LandParcelWindowSearch.jLabel1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkHistorical,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTime.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(LandParcelWindowSearch.class, "LandParcelWindowSearch.jLabel2.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkHistorical,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTime.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(LandParcelWindowSearch.class, "LandParcelWindowSearch.jLabel3.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkHistorical,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jLabel3,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTime.add(jLabel3, gridBagConstraints);

        dcFrom.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkHistorical,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                dcFrom,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTime.add(dcFrom, gridBagConstraints);

        dcTo.setPreferredSize(new java.awt.Dimension(124, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkHistorical,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                dcTo,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTime.add(dcTo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(panTime, gridBagConstraints);

        panCommand.setLayout(new javax.swing.BoxLayout(panCommand, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(panCommand, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chkMap,
            org.openide.util.NbBundle.getMessage(LandParcelWindowSearch.class, "LandParcelWindowSearch.chkMap.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(chkMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panSearch.add(jPanel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnNewSearch,
            org.openide.util.NbBundle.getMessage(
                LandParcelWindowSearch.class,
                "LandParcelWindowSearch.btnNewSearch.text"));        // NOI18N
        btnNewSearch.setToolTipText(org.openide.util.NbBundle.getMessage(
                LandParcelWindowSearch.class,
                "LandParcelWindowSearch.btnNewSearch.toolTipText")); // NOI18N
        btnNewSearch.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnNewSearchActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panSearch.add(btnNewSearch, gridBagConstraints);

        add(panSearch, java.awt.BorderLayout.NORTH);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnNewSearchActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnNewSearchActionPerformed
        chkActual.setSelected(true);
        chkHistorical.setSelected(false);
        initDateChoosers();
        chkMap.setSelected(false);
    }                                                                                //GEN-LAST:event_btnNewSearchActionPerformed

    @Override
    public JComponent getSearchWindowComponent() {
        return this;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        Geometry searchgeom = null;
        if (geometry != null) {
            searchgeom = CrsTransformer.transformToDefaultCrs(geometry);
        } else {
            if (chkMap.isSelected()) {
                final Geometry g =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
                final Geometry transformed = CrsTransformer.transformToDefaultCrs(g);
                // Damits auch mit -1 funzt:
                transformed.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
                searchgeom = transformed;
            }
        }
        if (chkHistorical.isSelected()) {
            final java.util.Date from = dcFrom.getDate();
            final java.util.Date to = dcTo.getDate();
            return new CidsLandParcelSearchStatement(chkActual.isSelected(),
                    true,
                    new java.sql.Date(from.getTime()),
                    new java.sql.Date(to.getTime()),
                    searchgeom);
        } else {
            return new CidsLandParcelSearchStatement(chkActual.isSelected(), searchgeom);
        }
    }

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
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

    @Override
    public String getName() {
        return "Flurstück-Suche";
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (LandParcelSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search);
            }
        }
    }
}
