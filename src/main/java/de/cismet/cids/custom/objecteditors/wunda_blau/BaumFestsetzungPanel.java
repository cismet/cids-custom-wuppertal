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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Getter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Date;
import java.util.MissingResourceException;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.text.SimpleDateFormat;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumFestsetzungPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumFestsetzungPanel.class);

    private static final MetaClass MC__ART;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumFestsetzungPanel.class.getSimpleName());
        MC__ART = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_ART",
                connectionContext);
    }

    public static final String FIELD__ID = "id";                 // baum_festsetzung
    public static final String FIELD__ART = "fk_art";            // baum_festsetzung
    public static final String FIELD__UMFANG = "umfang";         // baum_festsetzung
    public static final String FIELD__DATUM = "datum";           // baum_festsetzung
    public static final String FIELD__GEOM = "fk_geom";          // baum_festsetzung
    public static final String FIELD__BEMERKUNG = "bemerkung";   // baum_festsetzung
    public static final String FIELD__FK_SCHADEN = "fk_schaden"; // baum_festsetzung
    public static final String FIELD__FK_MELDUNG = "fk_meldung"; // baum_schaden
    public static final String FIELD__MDATUM = "datum";          // baum_meldung

    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_festsetzung_geom

    public static final String TABLE_GEOM = "geom";
    public static final String TABLE__NAME = "baum_festsetzung";
    public static final String BUNDLE_NOART = "BaumFestsetzungPanel.isOkForSaving().noArt";
    public static final String BUNDLE_NOUMFANG = "BaumFestsetzungPanel.isOkForSaving().noUmfang";
    public static final String BUNDLE_NOGEOM = "BaumFestsetzungPanel.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumFestsetzungPanel.isOkForSaving().wrongGeom";
    public static final String BUNDLE_NODATE = "BaumFestsetzungPanel.isOkForSaving().noDatum";
    public static final String BUNDLE_WHICH = "BaumFestsetzungPanel.isOkForSaving().welcheFest";
    public static final String BUNDLE_FAULT = "BaumFestsetzungPanel.isOkForSaving().welcherSchaden";
    public static final String BUNDLE_MESSAGE = "BaumFestsetzungPanel.isOkForSaving().welcheMeldung";
    public static final String BUNDLE_PANE_PREFIX = "BaumFestsetzungPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumFestsetzungPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumFestsetzungPanel.isOkForSaving().JOptionPane.title";
    public static final String GEOMTYPE = "Point";

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    private CidsBean cidsBean;
    private Integer saveGeom;
    private Date saveDatum;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__DATUM: {
                        if (evt.getNewValue() != saveDatum) {
                            setChangeFlag();
                        }
                    }
                    case FIELD__GEOM: {
                        if (evt.getNewValue() != saveGeom) {
                            setChangeFlag();
                        }
                        setMapWindow();
                        break;
                    }
                    default: {
                        setChangeFlag();
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    BaumLagePanel baumLagePanel;
    JComboBox<String> cbArtF;
    JComboBox cbGeomFest;
    DefaultBindableDateChooser dcDatum;
    JLabel lblArt;
    JLabel lblBemerkung;
    JLabel lblDatum;
    JLabel lblGeom;
    JLabel lblHoehe;
    JLabel lblUmfang;
    JPanel panContent;
    JPanel panFest;
    JPanel panGeometrie;
    JScrollPane scpBemerkung;
    JSpinner spHoeheF;
    JSpinner spUmfangF;
    JTextArea taBemerkungF;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumFestsetzungPanel object.
     */
    public BaumFestsetzungPanel() {
        this(null);
    }

    /**
     * Creates new form BaumFestsetzungPanel.
     *
     * @param  bclInstance  DOCUMENT ME!
     */
    public BaumFestsetzungPanel(final BaumChildrenLoader bclInstance) {
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null) {
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).setLocalRenderFeatureString(FIELD__GEOM);
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
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panContent = new JPanel();
        panFest = new JPanel();
        lblHoehe = new JLabel();
        spHoeheF = new JSpinner();
        lblUmfang = new JLabel();
        spUmfangF = new JSpinner();
        lblArt = new JLabel();
        cbArtF = new DefaultBindableReferenceCombo(MC__ART);
        ;
        lblGeom = new JLabel();
        if (isEditor()) {
            cbGeomFest = new DefaultCismapGeometryComboBoxEditor();
        }
        lblDatum = new JLabel();
        dcDatum = new DefaultBindableDateChooser();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkungF = new JTextArea();
        panGeometrie = new JPanel();
        baumLagePanel = new BaumLagePanel();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(205, 400));
        panContent.setLayout(new GridBagLayout());

        panFest.setName("panFest"); // NOI18N
        panFest.setOpaque(false);
        panFest.setLayout(new GridBagLayout());

        lblHoehe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblHoehe, "Höhe [m]:");
        lblHoehe.setName("lblHoehe");                // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblHoehe, gridBagConstraints);

        spHoeheF.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spHoeheF.setModel(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
        spHoeheF.setEnabled(false);
        spHoeheF.setName("spHoeheF");                // NOI18N
        spHoeheF.setPreferredSize(new Dimension(75, 20));

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.hoehe}"),
                spHoeheF,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panFest.add(spHoeheF, gridBagConstraints);

        lblUmfang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblUmfang, "Umfang [cm]:");
        lblUmfang.setName("lblUmfang");               // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panFest.add(lblUmfang, gridBagConstraints);

        spUmfangF.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spUmfangF.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        spUmfangF.setEnabled(false);
        spUmfangF.setName("spUmfangF");               // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.umfang}"),
                spUmfangF,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panFest.add(spUmfangF, gridBagConstraints);

        lblArt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblArt, "Art:");
        lblArt.setName("lblArt");                  // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblArt, gridBagConstraints);

        cbArtF.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbArtF.setMaximumRowCount(15);
        cbArtF.setEnabled(false);
        cbArtF.setName("cbArtF");                  // NOI18N
        cbArtF.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_art}"),
                cbArtF,
                BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panFest.add(cbArtF, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblGeom, "Geometrie:");
        lblGeom.setName("lblGeom");                 // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblGeom, gridBagConstraints);

        if (isEditor()) {
            cbGeomFest.setFont(new Font("Dialog", 0, 12)); // NOI18N
            cbGeomFest.setEnabled(false);
            cbGeomFest.setName("cbGeomFest");              // NOI18N

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_geom}"),
                    cbGeomFest,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomFest).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panFest.add(cbGeomFest, gridBagConstraints);
        }

        lblDatum.setFont(new Font("Tahoma", 1, 11));                                                             // NOI18N
        lblDatum.setText(NbBundle.getMessage(BaumFestsetzungPanel.class, "BaumFestsetzungPanel.lblDatum.text")); // NOI18N
        lblDatum.setName("lblDatum");                                                                            // NOI18N
        lblDatum.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 4, 5);
        panFest.add(lblDatum, gridBagConstraints);

        dcDatum.setEnabled(false);
        dcDatum.setName("dcDatum"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.datum}"),
                dcDatum,
                BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panFest.add(dcDatum, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung");            // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panFest.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkungF.setLineWrap(true);
        taBemerkungF.setRows(2);
        taBemerkungF.setWrapStyleWord(true);
        taBemerkungF.setEnabled(false);
        taBemerkungF.setName("taBemerkungF"); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkungF,
                BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkungF);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panFest.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panContent.add(panFest, gridBagConstraints);

        panGeometrie.setMinimumSize(new Dimension(50, 200));
        panGeometrie.setName("panGeometrie"); // NOI18N
        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        baumLagePanel.setName("baumLagePanel"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(baumLagePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 2);
        panContent.add(panGeometrie, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            TABLE__NAME,
            1,
            800,
            600);
    }
    @Override
    public ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
            ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    @Override
    public void dispose() {
        baumLagePanel.dispose();
        cidsBean = null;
        if (isEditor() && (cbGeomFest != null)) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).setCidsMetaObject(null);
            cbGeomFest = null;
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isEditor() {
        return this.editor;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeDoubleSpinnerWithoutButtons(spUmfangF, 0);
            RendererTools.makeReadOnly(spUmfangF);
            RendererTools.makeDoubleSpinnerWithoutButtons(spHoeheF, 1);
            RendererTools.makeReadOnly(spHoeheF);
            RendererTools.makeReadOnly(cbGeomFest);
            cbArtF.setEnabled(false);
            RendererTools.makeReadOnly(dcDatum);
            RendererTools.makeReadOnly(taBemerkungF);
            lblGeom.setVisible(isEditor());
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveGeom = (getCidsBean().getProperty(FIELD__GEOM) != null)
            ? ((CidsBean)getCidsBean().getProperty(FIELD__GEOM)).getPrimaryKeyValue() : null;
        saveDatum = (getCidsBean().getProperty(FIELD__DATUM) != null) ? (Date)getCidsBean().getProperty(FIELD__DATUM)
                                                                      : null;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))) {
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            try {
                bindingGroup.unbind();
                this.cidsBean = cidsBean;
                if ((getCidsBean() != null) && isEditor()) {
                    setSaveValues();
                }
                if (getCidsBean() != null) {
                    DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
                    // Wenn mit mehreren Geoms(Liste) gearbeitet wird
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).setCidsMetaObject(getCidsBean()
                                    .getMetaObject());
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).initForNewBinding();
                    }
                } else {
                    if (isEditor()) {
                        ((DefaultCismapGeometryComboBoxEditor)cbGeomFest).initForNewBinding();
                        cbGeomFest.setSelectedIndex(-1);
                    }
                }

                setMapWindow();
                bindingGroup.bind();
                if (isEditor() && (getCidsBean() != null)) {
                    getCidsBean().addPropertyChangeListener(changeListener);
                }
                if (isEditor()) {
                    cbGeomFest.updateUI();
                }
            } catch (final Exception ex) {
                LOG.warn("problem in setCidsBean.", ex);
            }
        }
        setReadOnly();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        cbGeomFest.setEnabled(edit);
        dcDatum.setEnabled(edit);
        spHoeheF.setEnabled(edit);
        spUmfangF.setEnabled(edit);
        cbArtF.setEnabled(edit);
        taBemerkungF.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        if ((getBaumChildrenLoader() != null)
                    && (getBaumChildrenLoader().getParentOrganizer() != null)
                    && (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param   saveFestsetzungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkayForSaving(final CidsBean saveFestsetzungBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        // Art muss angegeben werden
        try {
            if (saveFestsetzungBean.getProperty(FIELD__ART) == null) {
                LOG.warn("No art specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_NOART));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Art not given.", ex);
            save = false;
        }
        // Umfang muss vorhanden sein
        try {
            if ((saveFestsetzungBean.getProperty(FIELD__UMFANG) == null)
                        || ((Integer)saveFestsetzungBean.getProperty(FIELD__UMFANG) == 0)) {
                LOG.warn("No umfang specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_NOUMFANG));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Umfang not given.", ex);
            save = false;
        }
        // georeferenz muss gefüllt sein
        try {
            if (saveFestsetzungBean.getProperty(FIELD__GEOM) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_NOGEOM));
                save = false;
            } else {
                final CidsBean geom_pos = (CidsBean)saveFestsetzungBean.getProperty(FIELD__GEOM);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_WRONGGEOM));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        // Datum muss angegeben werden
        try {
            if (saveFestsetzungBean.getProperty(FIELD__DATUM) == null) {
                LOG.warn("No datum specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungEditor.class, BUNDLE_NODATE));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Datum not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            if (baumChildrenLoader.getParentOrganizer() instanceof BaumSchadenEditor) {
                errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_WHICH))
                        .append(saveFestsetzungBean.getPrimaryKeyValue());
            } else {
                if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor) {
                    SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                    errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_WHICH))
                            .append(saveFestsetzungBean.getPrimaryKeyValue());
                    final CidsBean schadenBean = (CidsBean)saveFestsetzungBean.getProperty(FIELD__FK_SCHADEN);
                    errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_FAULT))
                            .append(schadenBean.getPrimaryKeyValue());
                    final CidsBean meldungBean = (CidsBean)schadenBean.getProperty(FIELD__FK_MELDUNG);
                    errorMessage.append(NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_MESSAGE))
                            .append(formatTag.format(meldungBean.getProperty(FIELD__MDATUM)));
                }
            }
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumFestsetzungPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }
    /**
     * DOCUMENT ME!
     */
    private void setMapWindow() {
        baumLagePanel.setMapWindow(getCidsBean(), getConnectionContext());
    }
}
