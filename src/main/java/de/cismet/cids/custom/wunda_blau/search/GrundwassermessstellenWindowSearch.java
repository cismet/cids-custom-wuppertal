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

import java.util.Arrays;
import java.util.Collection;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.CidsGrundwassermessstelleSearch;
import de.cismet.cids.custom.wunda_blau.search.server.GrundwassermessstelleStoffeByKategorieLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

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
public class GrundwassermessstellenWindowSearch extends javax.swing.JPanel implements CidsWindowSearch,
    ActionTagProtected,
    SearchControlListener,
    PropertyChangeListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(GrundwassermessstellenWindowSearch.class);
    // End of variables declaration
    private static final String ACTION_TAG = "custom.grundwassermessstelle.search@WUNDA_BLAU";
    private static final MetaClass MC__GRUNDWASSERMESSSTELLE_KATEGORIE;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                GrundwassermessstellenWindowSearch.class.getSimpleName());
        MC__GRUNDWASSERMESSSTELLE_KATEGORIE = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "GRUNDWASSERMESSSTELLE_KATEGORIE",
                connectionContext);
    }

    //~ Instance fields --------------------------------------------------------

    private MetaClass metaClass;
    private ImageIcon icon;
    private JPanel pnlSearchCancel;
    private GeoSearchButton btnGeoSearch;
    private MappingComponent mappingComponent;
    private boolean geoSearchEnabled;
    private final GrundwassermessstelleStoffeByKategorieLightweightSearch stoffeByKategorieSearch;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbMapSearch;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefBis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcPruefVon;
    de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    de.cismet.cids.editors.FastBindableReferenceCombo fastBindableReferenceCombo1;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblFiller;
    private javax.swing.JLabel lblFiller5;
    private javax.swing.JLabel lblFiller6;
    private javax.swing.JLabel lblNotenBis4;
    private javax.swing.JLabel lblNotenFrom6;
    private javax.swing.JLabel lblPruefTil;
    private javax.swing.JLabel lblPruefVon;
    private javax.swing.JLabel lblStuetzmauern;
    private javax.swing.JPanel pnlButtons;
    private javax.swing.JPanel pnlMessung;
    private javax.swing.JPanel pnlNoten;
    private javax.swing.JPanel pnlScrollPane;
    private javax.swing.JPanel pnlSearchMode;
    private javax.swing.JRadioButton rbAll;
    private javax.swing.JRadioButton rbOne;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GrundwassermessstellenWindowSearch.
     */
    public GrundwassermessstellenWindowSearch() {
        this.stoffeByKategorieSearch = new GrundwassermessstelleStoffeByKategorieLightweightSearch(
                "%1$2s",
                new String[] { "NAME" });

        stoffeByKategorieSearch.setKategorieId(-1);
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
                    "grundwassermessstelle",
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
                final GrundwassermessstellenCreateSearchGeometryListener listener =
                    new GrundwassermessstellenCreateSearchGeometryListener(
                        mappingComponent,
                        new TreppenSearchTooltip(icon));
                listener.addPropertyChangeListener(this);
                btnGeoSearch = new GeoSearchButton(
                        GrundwassermessstellenCreateSearchGeometryListener.GRUNDWASSERMESSSTELLEN_CREATE_SEARCH_GEOMETRY,
                        mappingComponent,
                        null,
                        org.openide.util.NbBundle.getMessage(
                            GrundwassermessstellenWindowSearch.class,
                            "TreppenWindowSearch.btnGeoSearch.toolTipText"));
                pnlButtons.add(btnGeoSearch);
            }
        } catch (final Throwable e) {
            LOG.warn("Error in Constructor of GrundwassermessstellenWindowSearch. Search will not work properly.", e);
        }

        fastBindableReferenceCombo1.setMetaClassFromTableName("WUNDA_BLAU", "grundwassermessstelle_stoff");
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        pnlScrollPane = new javax.swing.JPanel();
        pnlNoten = new javax.swing.JPanel();
        lblStuetzmauern = new javax.swing.JLabel();
        lblNotenFrom6 = new javax.swing.JLabel();
        lblNotenBis4 = new javax.swing.JLabel();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo(
                MC__GRUNDWASSERMESSSTELLE_KATEGORIE,
                true,
                false);
        fastBindableReferenceCombo1 = new de.cismet.cids.editors.FastBindableReferenceCombo(
                stoffeByKategorieSearch,
                stoffeByKategorieSearch.getRepresentationPattern(),
                stoffeByKategorieSearch.getRepresentationFields());
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();
        pnlSearchMode = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        rbAll = new javax.swing.JRadioButton();
        rbOne = new javax.swing.JRadioButton();
        lblFiller5 = new javax.swing.JLabel();
        pnlMessung = new javax.swing.JPanel();
        lblPruefVon = new javax.swing.JLabel();
        lblPruefTil = new javax.swing.JLabel();
        dcPruefVon = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcPruefBis = new de.cismet.cids.editors.DefaultBindableDateChooser();
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

        pnlNoten.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    GrundwassermessstellenWindowSearch.class,
                    "GrundwassermessstellenWindowSearch.pnlNoten.border.title"))); // NOI18N
        pnlNoten.setLayout(new java.awt.GridBagLayout());

        lblStuetzmauern.setText("Stoff:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblStuetzmauern, gridBagConstraints);

        lblNotenFrom6.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "TreppenWindowSearch.lblNotenFrom5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlNoten.add(lblNotenFrom6, gridBagConstraints);

        lblNotenBis4.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "TreppenWindowSearch.lblNotenBis2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 5);
        pnlNoten.add(lblNotenBis4, gridBagConstraints);

        defaultBindableReferenceCombo1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    defaultBindableReferenceCombo1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(defaultBindableReferenceCombo1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        pnlNoten.add(fastBindableReferenceCombo1, gridBagConstraints);

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.##########"))));
        jFormattedTextField1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField1.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.jFormattedTextField1.text")); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                fastBindableReferenceCombo1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem != null}"),
                jFormattedTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlNoten.add(jFormattedTextField1, gridBagConstraints);

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.##########"))));
        jFormattedTextField2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jFormattedTextField2.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.jFormattedTextField2.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                fastBindableReferenceCombo1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem != null}"),
                jFormattedTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlNoten.add(jFormattedTextField2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 20);
        pnlScrollPane.add(pnlNoten, gridBagConstraints);
        pnlNoten.getAccessibleContext().setAccessibleName("Messwerte");

        pnlSearchMode.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlSearchMode.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(rbAll);
        rbAll.setSelected(true);
        rbAll.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.rbAll.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        pnlSearchMode.add(rbAll, gridBagConstraints);

        buttonGroup1.add(rbOne);
        rbOne.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.rbOne.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlSearchMode.add(rbOne, gridBagConstraints);

        lblFiller5.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.lblFiller5.text")); // NOI18N
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
                    GrundwassermessstellenWindowSearch.class,
                    "GrundwassermessstellenWindowSearch.pnlMessung.border.title"))); // NOI18N
        pnlMessung.setLayout(new java.awt.GridBagLayout());

        lblPruefVon.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.lblPruefVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        pnlMessung.add(lblPruefVon, gridBagConstraints);

        lblPruefTil.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.lblPruefTil.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        pnlMessung.add(lblPruefTil, gridBagConstraints);

        dcPruefVon.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlMessung.add(dcPruefVon, gridBagConstraints);

        dcPruefBis.setPreferredSize(new java.awt.Dimension(124, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        pnlMessung.add(dcPruefBis, gridBagConstraints);

        lblFiller.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.lblFiller.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
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
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.cbMapSearch.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 25, 0, 25);
        pnlScrollPane.add(cbMapSearch, gridBagConstraints);

        lblFiller6.setText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstellenWindowSearch.class,
                "GrundwassermessstellenWindowSearch.lblFiller6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlScrollPane.add(lblFiller6, gridBagConstraints);

        jScrollPane1.setViewportView(pnlScrollPane);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableReferenceCombo1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_defaultBindableReferenceCombo1ActionPerformed
        refreshMaterialTyp();
        fastBindableReferenceCombo1.setSelectedItem(null);
    }                                                                                                  //GEN-LAST:event_defaultBindableReferenceCombo1ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void refreshMaterialTyp() {
        stoffeByKategorieSearch.setKategorieId((Integer)((CidsBean)defaultBindableReferenceCombo1.getSelectedItem())
                    .getProperty("id"));

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    fastBindableReferenceCombo1.refreshModel();
                    return null;
                }
            }.execute();
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

//        final Date messungVon,
//            final Date messungBis,
//            final Collection<CidsGrundwassermessstelleSearch.StoffFilter> wertePairs

        final CidsBean selectedStoff = (CidsBean)fastBindableReferenceCombo1.getSelectedItem();

        final Collection<CidsGrundwassermessstelleSearch.StoffFilter> stoffFilters;
        if (selectedStoff != null) {
            final Double vonValue = (jFormattedTextField1.getValue() != null)
                ? new Double(jFormattedTextField1.getValue().toString()) : null;
            final Double bisValue = (jFormattedTextField2.getValue() != null)
                ? new Double(jFormattedTextField2.getValue().toString()) : null;
            stoffFilters = Arrays.asList(
                    new CidsGrundwassermessstelleSearch.StoffFilter[] {
                        new CidsGrundwassermessstelleSearch.StoffFilter(
                            (String)selectedStoff.getProperty("schluessel"),
                            vonValue,
                            bisValue)
                    });
        } else {
            stoffFilters = null;
        }
        return new CidsGrundwassermessstelleSearch(
                transformedBoundingBox,
                dcPruefVon.getDate(),
                dcPruefBis.getDate(),
                stoffFilters);
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
        return "Grundwassermessstellen";
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (GrundwassermessstellenCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @version  $Revision$, $Date$
//     */
//    class FilterPropAppender {
//
//        //~ Instance fields ----------------------------------------------------
//
//        private final HashMap<FilterKey, Object> filterProps = new HashMap<>();
//
//        //~ Methods ------------------------------------------------------------
//
//        /**
//         * DOCUMENT ME!
//         *
//         * @param   key   DOCUMENT ME!
//         * @param   text  DOCUMENT ME!
//         *
//         * @return  DOCUMENT ME!
//         */
//        public FilterPropAppender appendDouble(final FilterKey key, final String text) {
//            if ((text != null) && !text.equals("")) {
//                try {
//                    final double value = Double.parseDouble(text.replace(',', '.'));
//                    filterProps.put(key, value);
//                } catch (NumberFormatException ex) {
//                    LOG.warn("Could not read Double value from " + text, ex);
//                }
//            }
//            return this;
//        }
//
//        /**
//         * DOCUMENT ME!
//         *
//         * @param   key    DOCUMENT ME!
//         * @param   value  DOCUMENT ME!
//         *
//         * @return  DOCUMENT ME!
//         */
//        public FilterPropAppender appendDate(final FilterKey key, final Date value) {
//            if (value != null) {
//                filterProps.put(key, value);
//            }
//            return this;
//        }
//
//        /**
//         * DOCUMENT ME!
//         *
//         * @return  DOCUMENT ME!
//         */
//        public HashMap<FilterKey, Object> getFilterProps() {
//            return filterProps;
//        }
//    }
}
