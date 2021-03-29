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

import java.awt.Dimension;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.PotenzialflaecheSearch;

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
public class PotenzialflaechenWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PotenzialflaechenWindowSearch.class);
    private static final String ACTION_TAG = "custom.potenzialflaeche.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private ImageIcon icon;
    private JPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbMapSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblKampagne;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMessung;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchMode;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbOne;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtKampagne;
    private javax.swing.JTextField txtNummer;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PotenzialflaechenWindowSearch object.
     */
    public PotenzialflaechenWindowSearch() {
    }

    //~ Methods ----------------------------------------------------------------

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

            metaClass = ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "pf_potenzialflaeche",
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
                final PotenzialflaechenCreateSearchGeometryListener listener =
                    new PotenzialflaechenCreateSearchGeometryListener(
                        mappingComponent,
                        new TreppenSearchTooltip(icon));
                listener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        PotenzialflaechenCreateSearchGeometryListener.POTENZIALFLAECHEN_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            PotenzialflaechenWindowSearch.class,
                            "PotenzialflaechenWindowSearch.btnGeoSearch.toolTipText"));
                pnlButtons.add(btnGeoSearch);
            }
        } catch (final Throwable e) {
            LOG.warn("Error in Constructor of PotenzialflaechenWindowSearch. Search will not work properly.", e);
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
        pnlSearchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rbAll = new javax.swing.JRadioButton();
        rbOne = new javax.swing.JRadioButton();
        lblFiller5 = new javax.swing.JLabel();
        pnlMessung = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        lblKampagne = new javax.swing.JLabel();
        txtKampagne = new javax.swing.JTextField();
        lblFiller = new javax.swing.JLabel();
        pnlButtons = new javax.swing.JPanel();
        cbMapSearch = new javax.swing.JCheckBox();
        lblFiller6 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(70, 20));
        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        pnlScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlScrollPane.setLayout(new java.awt.GridBagLayout());

        pnlSearchMode.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSearchMode.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.rbAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlSearchMode.add(rbAll, gridBagConstraints);

        buttonGroup1.add(rbOne);
        rbOne.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.rbOne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSearchMode.add(rbOne, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblFiller5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlSearchMode.add(lblFiller5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(pnlSearchMode, gridBagConstraints);

        pnlMessung.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    PotenzialflaechenWindowSearch.class,
                    "PotenzialflaechenWindowSearch.pnlMessung.border.title"))); // NOI18N
        pnlMessung.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlMessung.add(lblBezeichnung, gridBagConstraints);

        txtBezeichnung.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.txtBezeichnung.text")); // NOI18N
        txtBezeichnung.setMinimumSize(new java.awt.Dimension(200, 29));
        txtBezeichnung.setPreferredSize(new java.awt.Dimension(200, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 20);
        pnlMessung.add(txtBezeichnung, gridBagConstraints);

        lblNummer.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlMessung.add(lblNummer, gridBagConstraints);

        txtNummer.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.txtNummer.text")); // NOI18N
        txtNummer.setMinimumSize(new java.awt.Dimension(200, 29));
        txtNummer.setPreferredSize(new java.awt.Dimension(200, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlMessung.add(txtNummer, gridBagConstraints);

        lblKampagne.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblKampagne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlMessung.add(lblKampagne, gridBagConstraints);

        txtKampagne.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.txtKampagne.text")); // NOI18N
        txtKampagne.setMinimumSize(new java.awt.Dimension(200, 29));
        txtKampagne.setPreferredSize(new java.awt.Dimension(200, 29));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlMessung.add(txtKampagne, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlMessung.add(lblFiller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlMessung, gridBagConstraints);
        pnlMessung.getAccessibleContext().setAccessibleName("Messung");

        pnlButtons.setLayout(new javax.swing.BoxLayout(pnlButtons, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlButtons, gridBagConstraints);

        cbMapSearch.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.cbMapSearch.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(cbMapSearch, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(
                PotenzialflaechenWindowSearch.class,
                "PotenzialflaechenWindowSearch.lblFiller6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(lblFiller6, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

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
        final Geometry searchGeometrie;
        if (geometry != null) {
            searchGeometrie = geometry;
        } else {
            if (cbMapSearch.isSelected()) {
                searchGeometrie =
                    ((XBoundingBox)CismapBroker.getInstance().getMappingComponent().getCurrentBoundingBox())
                            .getGeometry();
            } else {
                searchGeometrie = null;
            }
        }
        final Geometry transformedBoundingBox;
        if (searchGeometrie != null) {
            transformedBoundingBox = CrsTransformer.transformToDefaultCrs(searchGeometrie);
            transformedBoundingBox.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedBoundingBox = null;
        }
        final PotenzialflaecheSearch.SearchMode searchMode = rbAll.isSelected() ? PotenzialflaecheSearch.SearchMode.AND
                                                                                : PotenzialflaecheSearch.SearchMode.OR;

        final String nummer = txtNummer.getText().trim().isEmpty() ? null : txtNummer.getText()
                    .trim();
        final String bezeichnung = txtBezeichnung.getText().trim().isEmpty() ? null : txtBezeichnung.getText().trim();
        final String kampagne = txtKampagne.getText().trim().isEmpty() ? null : txtKampagne.getText()
                    .trim();

        final PotenzialflaecheSearch.Configuration searchConfiguration = new PotenzialflaecheSearch.Configuration();
        searchConfiguration.setBezeichnung(bezeichnung);
        searchConfiguration.setNummer(nummer);
        return new PotenzialflaecheSearch(searchMode, searchConfiguration, searchGeometrie);
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
        return "Potenzialflächen";
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (PotenzialflaechenCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }
}
