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
package de.cismet.cids.custom.wunda_blau.search.abfrage;

import Sirius.navigator.actiontag.ActionTagProtected;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.awt.Dimension;

import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.StorableSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @param    <C>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractAbfrageWindowSearch<P extends AbstractAbfragePanel, C extends StorableSearch.Configuration>
        extends javax.swing.JPanel implements CidsWindowSearch,
        ActionTagProtected,
        SearchControlListener,
        ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AbstractAbfrageWindowSearch.class);

    //~ Instance fields --------------------------------------------------------

    private JPanel pnlSearchCancel;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    private GeoSearchButton btnGeoSearch;
    private ImageIcon icon;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    @Getter private final P searchPanel;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbAbfragen;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractAbfrageWindowSearch object.
     *
     * @param  searchPanel  DOCUMENT ME!
     */
    public AbstractAbfrageWindowSearch(final P searchPanel) {
        this.searchPanel = searchPanel;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getTableName() {
        final AbstractAbfragePanel searchPanel = getSearchPanel();
        return (searchPanel != null) ? searchPanel.getTableName() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract GeoSearchButton createGeoSearchButton();

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();

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
            pnlButtons.add(pnlSearchCancel);

            pnlButtons.add(Box.createHorizontalStrut(5));

            pnlSearchCancel = new SearchControlPanel(this, getConnectionContext());

            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                btnGeoSearch = createGeoSearchButton();
                pnlButtons.add(btnGeoSearch);
            }

            final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    getTableName(),
                    getConnectionContext());

            byte[] iconDataFromMetaclass = new byte[] {};

            if (metaClass != null) {
                iconDataFromMetaclass = metaClass.getIconData();
            }

            if (iconDataFromMetaclass.length > 0) {
                LOG.info("Using icon from metaclass.");
                icon = new ImageIcon(metaClass.getIconData());
            } else {
                LOG.warn("Metaclass icon is not set. Trying to load default icon.");
                final URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png");

                if (urlToIcon != null) {
                    icon = new ImageIcon(urlToIcon);
                } else {
                    icon = new ImageIcon(new byte[] {});
                }
            }
        } catch (final Throwable e) {
            LOG.warn("Error while initialization. Search will not work properly.", e);
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

        panTitle = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        pnlMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cbAbfragen = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.NullableOption(),
                new DefaultBindableReferenceCombo.WhereOption(String.format("search_name = '%s'", getTableName())),
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "cs_searchconf",
                        getConnectionContext())));
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = getSearchPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();

        panTitle.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Abfrage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panTitle.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panTitle.add(lblTitle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(filler1, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlMain.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/reload.png"))); // NOI18N
        jButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AbstractAbfrageWindowSearch.class,
                "AbstractAbfrageWindowSearch.jButton1.toolTipText"));                         // NOI18N
        jButton1.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanel2.add(jButton1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel15,
            org.openide.util.NbBundle.getMessage(
                AbstractAbfrageWindowSearch.class,
                "AbstractAbfrageWindowSearch.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel2.add(jLabel15, gridBagConstraints);

        cbAbfragen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbAbfragenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel2.add(cbAbfragen, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/icon-save-floppy.png"))); // NOI18N
        jButton2.setPreferredSize(new java.awt.Dimension(32, 32));
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        jPanel2.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlMain.add(jPanel2, gridBagConstraints);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlMain.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(cbMapSearch, "Nur im aktuellen Kartenausschnitt suchen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlMain.add(cbMapSearch, gridBagConstraints);

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlMain.add(pnlButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(pnlMain, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract String getArtificialId();

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final Object selected = ((DefaultBindableReferenceCombo)cbAbfragen).getSelectedItem();
        try {
            final Boolean updateOrInsert;
            if (selected instanceof CidsBean) {
                final int option = JOptionPane.showConfirmDialog(
                        this,
                        "<html>Möchten Sie die aktuell ausgewählte Abfrage überschreiben?<br>"
                                + "<br>"
                                + "&bull; Ja: ausgewählte Abfrage wird überschrieben.<br>"
                                + "&bull; Nein: eine neue Abfrage wird erzeugt.<br>",
                        "Abfrage überschreiben?",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (option) {
                    case JOptionPane.YES_OPTION: {
                        updateOrInsert = Boolean.TRUE;
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        updateOrInsert = Boolean.FALSE;
                        break;
                    }
                    default: {
                        updateOrInsert = null;
                    }
                }
            } else {
                updateOrInsert = Boolean.FALSE;
            }
            if (updateOrInsert != null) {
                final CidsBean abfrageBean;
                if (updateOrInsert) {
                    abfrageBean = (CidsBean)selected;
                } else {
                    final String name = JOptionPane.showInputDialog(
                            this,
                            "Wählen Sie einen Namen für die neue Abfrage:",
                            "Neue Abfrage",
                            JOptionPane.PLAIN_MESSAGE);
                    if (name == null) {
                        return;
                    }
                    abfrageBean = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "cs_searchconf",
                            getConnectionContext());
                    abfrageBean.setProperty("name", name);
                    abfrageBean.setProperty("search_name", getTableName());
                }
                final String conf_json = getSearchPanel().getConfigurationMapper()
                            .writeValueAsString(getSearchPanel().createConfiguration());
                abfrageBean.setProperty("conf_json", conf_json);
                final CidsBean persisted = abfrageBean.persist(getConnectionContext());

                if (updateOrInsert) {
                    ComponentRegistry.getRegistry()
                            .getCatalogueTree()
                            .requestRefreshNode(String.format(
                                    "%s.%d",
                                    getArtificialId(),
                                    persisted.getMetaObject().getId()));
                } else {
                    ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode(getArtificialId());
                }

                JOptionPane.showMessageDialog(
                    this,
                    "Die Abfrage wurde erfolgreich abgespeichert.",
                    "Abfrage gespeichert.",
                    JOptionPane.INFORMATION_MESSAGE);
                ((DefaultBindableReferenceCombo)cbAbfragen).reload();
                ((DefaultBindableReferenceCombo)cbAbfragen).setSelectedItem(persisted);
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            JOptionPane.showMessageDialog(
                this,
                "Das Abspeichern der Abfrage ist fehlgeschlagen.",
                "Speichern fehlgeschlagen.",
                JOptionPane.ERROR_MESSAGE);
        }
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        ((DefaultBindableReferenceCombo)cbAbfragen).reload();
        ((DefaultBindableReferenceCombo)cbAbfragen).setSelectedItem(null);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAbfragenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbAbfragenActionPerformed
        final Object selected = cbAbfragen.getSelectedItem();
        try {
            if (selected instanceof CidsBean) {
                final CidsBean abfrageBean = (CidsBean)selected;
                final String conf_json = (String)abfrageBean.getProperty("conf_json");
                if (conf_json != null) {
                    getSearchPanel().initFromConfiguration(getSearchPanel().readConfiguration(conf_json));
                } else {
                    getSearchPanel().initFromConfiguration(null);
                }
            } else {
                getSearchPanel().initFromConfiguration(null);
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                              //GEN-LAST:event_cbAbfragenActionPerformed

    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
    }

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
    public final MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        final Geometry geometryToSearchFor;
        if (geometry != null) {
            geometryToSearchFor = geometry;
        } else {
            if (cbMapSearch.isSelected()) {
                geometryToSearchFor =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
            } else {
                geometryToSearchFor = null;
            }
        }
        final Geometry transformedGeometry;
        if (geometryToSearchFor != null) {
            transformedGeometry = CrsTransformer.transformToDefaultCrs(geometryToSearchFor);
            transformedGeometry.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedGeometry = null;
        }
        return createServerSearch(transformedGeometry);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract MetaObjectNodeServerSearch createServerSearch(final Geometry geometry);

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
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
