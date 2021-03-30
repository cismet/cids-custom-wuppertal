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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.actiontag.ActionTagProtected;
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class AlboFlaecheWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    PropertyChangeListener,
    SearchControlListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlboFlaecheWindowSearch.class);
    private static final String ACTION_TAG = "custom.albo.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private JPanel pnlSearchCancel;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    private GeoSearchButton btnGeoSearch;
    private ImageIcon icon;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.custom.wunda_blau.AlboFlaecheSearchPanel alboFlaecheSearchPanel1;
    private javax.swing.JComboBox<String> cbAbfragen;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AlboFlaecheWindowSearch.
     */
    public AlboFlaecheWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();
            alboFlaecheSearchPanel1.initWithConnectionContext(connectionContext);
            revalidate();

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
                final AlboFlaecheCreateSearchGeometryListener geometryListener =
                    new AlboFlaecheCreateSearchGeometryListener(mappingComponent,
                        new AlboFlaecheSearchTooltip(icon));
                geometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        AlboFlaecheCreateSearchGeometryListener.CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        "Geo-Suche nach Altlasten");
                pnlButtons.add(btnGeoSearch);
            }

            final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "albo_flaeche",
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
            LOG.warn("Error in Constructor of AlboFlaecheWindowSearch. Search will not work properly.", e);
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

        pnlMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        cbAbfragen = new DefaultBindableReferenceCombo(true);
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        alboFlaecheSearchPanel1 = new de.cismet.cids.custom.wunda_blau.AlboFlaecheSearchPanel(true);
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        cbMapSearch = new javax.swing.JCheckBox();
        pnlButtons = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        pnlMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlMain.setLayout(new java.awt.GridBagLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/reload.png"))); // NOI18N
        jButton1.setToolTipText(org.openide.util.NbBundle.getMessage(
                AlboFlaecheWindowSearch.class,
                "AlboFlaecheWindowSearch.jButton1.toolTipText"));                             // NOI18N
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
                AlboFlaecheWindowSearch.class,
                "AlboFlaecheWindowSearch.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel2.add(jLabel15, gridBagConstraints);

        ((DefaultBindableReferenceCombo)cbAbfragen).setMetaClass(ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "cs_searchconf",
                getConnectionContext()));
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

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        alboFlaecheSearchPanel1.setMaximumSize(new java.awt.Dimension(650, 32767));
        alboFlaecheSearchPanel1.setMinimumSize(new java.awt.Dimension(650, 10));
        alboFlaecheSearchPanel1.setOpaque(false);
        alboFlaecheSearchPanel1.setPreferredSize(new java.awt.Dimension(650, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(alboFlaecheSearchPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

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
                }
                final AlboFlaecheSearch.Configuration searchInfo = alboFlaecheSearchPanel1.createConfiguration();
                final String conf_json = AlboFlaecheSearch.OBJECT_MAPPER.writeValueAsString(searchInfo);
                abfrageBean.setProperty("conf_json", conf_json);
                final CidsBean persisted = abfrageBean.persist(getConnectionContext());

                if (updateOrInsert) {
                    ComponentRegistry.getRegistry()
                            .getCatalogueTree()
                            .requestRefreshNode(String.format("albo.abfragen.%d", persisted.getMetaObject().getId()));
                } else {
                    ComponentRegistry.getRegistry().getCatalogueTree().requestRefreshNode("albo.abfragen");
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
        if (selected instanceof CidsBean) {
            final CidsBean abfrageBean = (CidsBean)selected;
            final String conf_json = (String)abfrageBean.getProperty("conf_json");
            if (conf_json != null) {
                try {
                    alboFlaecheSearchPanel1.initFromConfiguration(AlboFlaecheSearch.OBJECT_MAPPER.readValue(
                            conf_json,
                            AlboFlaecheSearch.Configuration.class));
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            } else {
                alboFlaecheSearchPanel1.initFromConfiguration(null);
            }
        } else {
            alboFlaecheSearchPanel1.initFromConfiguration(null);
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
    public MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
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
        return alboFlaecheSearchPanel1.getServerSearch(geometryToSearchFor);
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
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

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (AlboFlaecheCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AlboFlaecheWindowSearch.class, "AlboWindowSearch.name");
    }
}
