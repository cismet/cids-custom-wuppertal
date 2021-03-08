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
import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;
import Sirius.navigator.search.dynamic.SearchControlPanel;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.InputStream;

import java.net.URL;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.vzkat.VzkatUtils;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatSchilderSearch;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatSchilderSearch.SearchMode;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatZeichenLightweightSearch;

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
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class VzkatWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VzkatWindowSearch.class);
    // End of variables declaration
    private static final String ACTION_TAG = "custom.vzkat.search@WUNDA_BLAU";
    private static final ImageIcon ERROR_ICON = new ImageIcon(VzkatWindowSearch.class.getResource(
                "/res/vzkat/error_64.png"));
//    private static final String ICON_URL_TEMPLATE = "http://dokumente.s10222.wuppertal-intra.de/vzkat-bilder/64x64/%s.png";
    public static final String ICON_PATH_TEMPLATE = "/de/cismet/cids/custom/wunda_blau/res/vzkat-bilder/64x64/%s.png";
    private static final Map<String, ImageIcon> ICONS = new WeakHashMap<>();

    //~ Instance fields --------------------------------------------------------

    private SwingWorker<ImageIcon, Void> iconLoadingWorker = null;

    private boolean cbStvoActionListenerEnabled = true;

    private MetaClass metaClass;
    private ImageIcon icon;
    private JPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    private final VzkatZeichenLightweightSearch verkehrszeichenSearch = new VzkatZeichenLightweightSearch();
    private MetaClass mcVzkatStvo = null;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbMapSearch;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbStvo;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbVerkehrszeichen;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlPruefung;
    private javax.swing.JPanel pnlScrollPane;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatStandortWindowSearch object.
     */
    public VzkatWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            initComponents();
            // todo just for debug
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

            metaClass = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "vzkat_standort",
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

            pnlButtons.add(Box.createHorizontalStrut(5));

            mappingComponent = CismapBroker.getInstance().getMappingComponent();
            geoSearchEnabled = mappingComponent != null;
            if (geoSearchEnabled) {
                final VzkatSchildCreateSearchGeometryListener geometryListener =
                    new VzkatSchildCreateSearchGeometryListener(mappingComponent,
                        new VzkatSearchTooltip(icon));
                geometryListener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        VzkatSchildCreateSearchGeometryListener.CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        "Geo-Suche nach Verkehrszeichen");
                pnlButtons.add(btnGeoSearch);
            }

            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        mcVzkatStvo = ClassCacheMultiple.getMetaClass(
                                "WUNDA_BLAU",
                                "VZKAT_STVO",
                                connectionContext);
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            cbStvo.setMetaClass(mcVzkatStvo);
                            cbStvo.reload();
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
        } catch (final Throwable e) {
            LOG.warn("Error in Constructor of VzkatStandortWindowSearch. Search will not work properly.", e);
        }
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlPruefung = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cbStvo = new DefaultBindableReferenceCombo(mcVzkatStvo, true, false);
        cbVerkehrszeichen = new de.cismet.cids.editors.FastBindableReferenceCombo(
                verkehrszeichenSearch,
                verkehrszeichenSearch.getRepresentationPattern(),
                verkehrszeichenSearch.getRepresentationFields());
        lblIcon = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jLabel8 = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        lblFiller6 = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlPruefung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    VzkatWindowSearch.class,
                    "VzkatWindowSearch.pnlPruefung.border.title"))); // NOI18N
        pnlPruefung.setMaximumSize(new java.awt.Dimension(550, 2147483647));
        pnlPruefung.setMinimumSize(new java.awt.Dimension(550, 96));
        pnlPruefung.setPreferredSize(new java.awt.Dimension(550, 96));
        pnlPruefung.setLayout(new java.awt.GridBagLayout());

        jLabel7.setText(org.openide.util.NbBundle.getMessage(
                VzkatWindowSearch.class,
                "VzkatWindowSearch.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPruefung.add(jLabel7, gridBagConstraints);

        cbStvo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbStvoActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlPruefung.add(cbStvo, gridBagConstraints);

        cbVerkehrszeichen.setMetaClassFromTableName("WUNDA_BLAU", "vzkat_zeichen");
        cbVerkehrszeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbVerkehrszeichenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        pnlPruefung.add(cbVerkehrszeichen, gridBagConstraints);

        lblIcon.setText(org.openide.util.NbBundle.getMessage(
                VzkatWindowSearch.class,
                "VzkatWindowSearch.lblIcon.text")); // NOI18N
        lblIcon.setMaximumSize(new java.awt.Dimension(64, 64));
        lblIcon.setMinimumSize(new java.awt.Dimension(64, 64));
        lblIcon.setPreferredSize(new java.awt.Dimension(64, 64));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPruefung.add(lblIcon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlPruefung.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlPruefung.add(filler3, gridBagConstraints);

        jLabel8.setText(org.openide.util.NbBundle.getMessage(
                VzkatWindowSearch.class,
                "VzkatWindowSearch.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlPruefung.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlPruefung, gridBagConstraints);
        pnlPruefung.getAccessibleContext()
                .setAccessibleName(org.openide.util.NbBundle.getMessage(
                        VzkatWindowSearch.class,
                        "VzkatWindowSearch.pnlPruefung.AccessibleContext.accessibleName")); // NOI18N

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlButtons, gridBagConstraints);

        cbMapSearch.setText(org.openide.util.NbBundle.getMessage(
                VzkatWindowSearch.class,
                "VzkatWindowSearch.cbMapSearch.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(cbMapSearch, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(
                VzkatWindowSearch.class,
                "VzkatWindowSearch.lblFiller6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(lblFiller6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlScrollPane.add(filler2, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStvoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStvoActionPerformed
        if (cbStvoActionListenerEnabled) {
            final CidsBean stvoBean = (CidsBean)cbStvo.getSelectedItem();
            verkehrszeichenSearch.setStvoId((stvoBean != null) ? (Integer)stvoBean.getProperty("id") : null);
            cbVerkehrszeichen.setSelectedItem(null);
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        cbVerkehrszeichen.refreshModel();
                        return null;
                    }
                }.execute();
        }
    } //GEN-LAST:event_cbStvoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbVerkehrszeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbVerkehrszeichenActionPerformed
        final JTextField txt = (JTextField)cbVerkehrszeichen.getEditor().getEditorComponent();
        final CidsBean selectedZeichen = (CidsBean)cbVerkehrszeichen.getSelectedItem();

        final String text;
        if (selectedZeichen != null) {
            text = VzkatUtils.createZeichenToString(selectedZeichen);

            final CidsBean selectedStvo = (cbStvo.getSelectedItem() instanceof CidsBean)
                ? (CidsBean)cbStvo.getSelectedItem() : null;
            final CidsBean stvoOfSelectedZeichen = (CidsBean)selectedZeichen.getProperty("fk_stvo");
            if (!Objects.equals(stvoOfSelectedZeichen, selectedStvo)) {
                try {
                    cbStvoActionListenerEnabled = false;
                    cbStvo.setSelectedItem(stvoOfSelectedZeichen);
                } finally {
                    cbStvoActionListenerEnabled = true;
                }
            }

            refreshIcon(VzkatUtils.createZeichenKey(selectedZeichen));
        } else {
            text = "";
            refreshIcon(null);
        }
        txt.setText(text);
    } //GEN-LAST:event_cbVerkehrszeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public ImageIcon loadZeichenIcon(final String key) throws Exception {
//        final String urlString = String.format(ICON_URL_TEMPLATE, key);
//        final InputStream is = WebAccessManager.getInstance().doRequest(new URL(urlString));
        final InputStream is = getClass().getResourceAsStream(String.format(VzkatWindowSearch.ICON_PATH_TEMPLATE, key));
        return new ImageIcon(ImageIO.read(is));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    private void refreshIcon(final String key) {
        if (key != null) {
            iconLoadingWorker = new SwingWorker<ImageIcon, Void>() {

                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return loadZeichenIcon(key);
                    }

                    @Override
                    protected void done() {
                        if (this.equals(iconLoadingWorker)) {
                            try {
                                final ImageIcon icon = get();
                                ICONS.put(key, icon);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                                ICONS.put(key, ERROR_ICON);
                            }
                            lblIcon.setIcon(ICONS.get(key));
                        }
                    }
                };
            iconLoadingWorker.execute();
        } else {
            lblIcon.setIcon(null);
        }
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
    private MetaObjectNodeServerSearch getServerSearch(final Geometry geometry) {
        final SearchMode mode = SearchMode.AND;

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
        final Geometry transformedBoundingBox;
        if (geometryToSearchFor != null) {
            transformedBoundingBox = CrsTransformer.transformToDefaultCrs(geometryToSearchFor);
            transformedBoundingBox.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedBoundingBox = null;
        }

        final VzkatSchilderSearch search = new VzkatSchilderSearch();
        search.setSearchFor(VzkatSchilderSearch.SearchFor.SCHILD);
        search.setGeom(transformedBoundingBox);
        search.setSearchMode(mode);
        search.setZeichenId((cbVerkehrszeichen.getSelectedItem() != null)
                ? (Integer)((CidsBean)cbVerkehrszeichen.getSelectedItem()).getProperty("id") : null);
        return search;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        return getServerSearch(null);
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
    public String getName() {
        return NbBundle.getMessage(VzkatWindowSearch.class, "VzkatStandortWindowSearch.name");
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (VzkatSchildCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }
}
