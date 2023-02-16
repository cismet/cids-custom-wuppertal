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

import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class SgkHinweisEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    RequestsFullSizeComponent,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final MetaClass MC__STATUS;
    private static final MetaClass MC__KATHER;
    private static final MetaClass MC__KATEGORIE;

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                SgkHinweisEditor.class.getSimpleName());
        MC__STATUS = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "SGK_STATUS",
                connectionContext);

        MC__KATHER = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "SGK_KATEGORIEHERKUNFT",
                connectionContext);

        MC__KATEGORIE = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "SGK_KATEGORIE",
                connectionContext);
    }

    private static final Logger LOG = Logger.getLogger(SgkHinweisEditor.class);

    public static final String FIELD__ID = "id";
    public static final String FIELD__ANZEIGEN = "anzeigen";
    public static final String FIELD__GEOREFERENZ = "fk_geom";
    public static final String FIELD__STATUS = "fk_status";
    public static final String FIELD__BDATUM = "beurteilungsdatum";
    public static final String FIELD__MDATUM = "meldungsdatum";
    public static final String FIELD__KATHER = "fk_kategorieherkunft";
    public static final String FIELD__KATEGORIE = "fk_kategorie";
    public static final String FIELD__STATUS_KEY = "fk_status.schluessel";          // status
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // geom.geo_field
    public static final String TABLE_NAME = "sgk_hinweis";
    public static final String TABLE_GEOM = "geom";

    public static final String BUNDLE_NOTITEL = "SgkHinweisEditor.isOkForSaving().noTitle";
    public static final String BUNDLE_NOBESCHREIBUNG = "SgkHinweisEditor.isOkForSaving().noBeschreibung";
    public static final String BUNDLE_NOHERKUNFT = "SgkHinweisEditor.isOkForSaving().noHerkunft";
    public static final String BUNDLE_NOSTATUS = "SgkHinweisEditor.isOkForSaving().noStatus";
    public static final String BUNDLE_NOKATEGORIEH = "SgkHinweisEditor.isOkForSaving().noKategorieH";
    public static final String BUNDLE_NOKATEGORIE = "SgkHinweisEditor.isOkForSaving().noKategorie";
    public static final String BUNDLE_NOBDATUM = "SgkHinweisEditor.isOkForSaving().noBDatum";
    public static final String BUNDLE_NOMDATUM = "SgkHinweisEditor.isOkForSaving().noMDatum";
    public static final String BUNDLE_NOGEOM = "SgkHinweisEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_PANE_PREFIX = "SgkHinweisEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "SgkHinweisEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "SgkHinweisEditor.isOkForSaving().JOptionPane.title";

    //~ Instance fields --------------------------------------------------------

    List<String> STATUS_LIST = Arrays.asList("verifiziert", "abgelehnt");

    private final boolean editor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox cbGeom;
    JComboBox<String> cbKatHerk;
    JComboBox<String> cbKategorie;
    JComboBox<String> cbStatus;
    JCheckBox chAnzeigen;
    DefaultBindableDateChooser dcBeurteilungsdatum;
    DefaultBindableDateChooser dcMeldungsdatum;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private JLabel lblBemerkung;
    private JLabel lblBeschreibung;
    private JLabel lblBeurteilungsdatum;
    private JLabel lblGeom;
    private JLabel lblHerkunfKat;
    private JLabel lblHerkunft;
    private JLabel lblKarte;
    private JLabel lblKategorie;
    private JLabel lblMeldungsdatum;
    private JLabel lblNzeigen;
    private JLabel lblStatus;
    private JLabel lblTitel;
    private JPanel panBemerkung;
    private JPanel panBeschreibung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panHerkunft;
    private DefaultPreviewMapPanel panPreviewMap;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpBeschreibung;
    private JScrollPane scpHerkunft;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextArea taBemerkung;
    private JTextArea taBeschreibung;
    private JTextArea taHerkunft;
    private JTextField txtTitel;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public SgkHinweisEditor() {
        this(true);
    }

    /**
     * Creates a new SgkHinweisEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public SgkHinweisEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        setReadOnly();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panContent = new RoundedPanel();
        panDaten = new JPanel();
        lblBeurteilungsdatum = new JLabel();
        dcBeurteilungsdatum = new DefaultBindableDateChooser();
        lblGeom = new JLabel();
        if (isEditor()) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setAllowedGeometryTypes(
                new Class[] { Polygon.class, MultiPolygon.class });
        }
        lblBeschreibung = new JLabel();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHerkunfKat = new JLabel();
        lblTitel = new JLabel();
        txtTitel = new JTextField();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblStatus = new JLabel();
        lblNzeigen = new JLabel();
        chAnzeigen = new JCheckBox();
        cbStatus = new DefaultBindableReferenceCombo(MC__STATUS);
        ;
        lblHerkunft = new JLabel();
        panBeschreibung = new JPanel();
        scpBeschreibung = new JScrollPane();
        taBeschreibung = new JTextArea();
        panHerkunft = new JPanel();
        scpHerkunft = new JScrollPane();
        taHerkunft = new JTextArea();
        cbKatHerk = new DefaultBindableReferenceCombo(MC__KATHER);
        ;
        lblKategorie = new JLabel();
        cbKategorie = new DefaultBindableReferenceCombo(MC__KATEGORIE);
        ;
        lblMeldungsdatum = new JLabel();
        dcMeldungsdatum = new DefaultBindableDateChooser();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        rpKarte = new RoundedPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panPreviewMap = new DefaultPreviewMapPanel();

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblBeurteilungsdatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeurteilungsdatum.setText("Beurteilungsdatum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeurteilungsdatum, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beurteilungsdatum}"),
                dcBeurteilungsdatum,
                BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcBeurteilungsdatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 0);
        panDaten.add(dcBeurteilungsdatum, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (isEditor()) {
            if (editor) {
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_geom}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor()) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        lblBeschreibung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung.setText("Beschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschreibung, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHerkunfKat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHerkunfKat.setText("Kategorie-Herkunft:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHerkunfKat, gridBagConstraints);

        lblTitel.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTitel.setText("Titel:");
        lblTitel.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTitel, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.titel}"),
                txtTitel,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtTitel, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(4);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblStatus.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStatus.setText("Status:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStatus, gridBagConstraints);

        lblNzeigen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblNzeigen.setText("Anzeigen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblNzeigen, gridBagConstraints);

        chAnzeigen.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzeigen}"),
                chAnzeigen,
                BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chAnzeigen, gridBagConstraints);

        cbStatus.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbStatus.setMaximumRowCount(15);
        cbStatus.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_status}"),
                cbStatus,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStatus, gridBagConstraints);

        lblHerkunft.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHerkunft.setText("Herkunft:");
        lblHerkunft.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHerkunft, gridBagConstraints);

        panBeschreibung.setOpaque(false);
        panBeschreibung.setLayout(new GridBagLayout());

        taBeschreibung.setColumns(20);
        taBeschreibung.setLineWrap(true);
        taBeschreibung.setRows(6);
        taBeschreibung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beschreibung}"),
                taBeschreibung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBeschreibung.setViewportView(taBeschreibung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(scpBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBeschreibung, gridBagConstraints);

        panHerkunft.setOpaque(false);
        panHerkunft.setLayout(new GridBagLayout());

        taHerkunft.setColumns(20);
        taHerkunft.setLineWrap(true);
        taHerkunft.setRows(2);
        taHerkunft.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.herkunft}"),
                taHerkunft,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpHerkunft.setViewportView(taHerkunft);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panHerkunft.add(scpHerkunft, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panHerkunft, gridBagConstraints);

        cbKatHerk.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbKatHerk.setMaximumRowCount(15);
        cbKatHerk.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_kategorieherkunft}"),
                cbKatHerk,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbKatHerk, gridBagConstraints);

        lblKategorie.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKategorie.setText("Kategorie:");
        lblKategorie.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblKategorie, gridBagConstraints);

        cbKategorie.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbKategorie.setMaximumRowCount(15);
        cbKategorie.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_kategorie}"),
                cbKategorie,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbKategorie, gridBagConstraints);

        lblMeldungsdatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMeldungsdatum.setText("Meldungsdatum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMeldungsdatum, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.meldungsdatum}"),
                dcMeldungsdatum,
                BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcMeldungsdatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 0);
        panDaten.add(dcMeldungsdatum, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        panContent.add(panDaten, gridBagConstraints);

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblKarte.setForeground(new Color(255, 255, 255));
        lblKarte.setText("Lage");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(lblKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        panPreviewMap.setPreferredSize(new Dimension(500, 250));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        panContent.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditor() {
        return this.editor;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor()) {
                if (getCidsBean() != null) {
                    LOG.info("remove propchange SgkHinweis: " + getCidsBean());
                    getCidsBean().removePropertyChangeListener(this);
                }
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange SgkHinweis: " + getCidsBean());
                getCidsBean().addPropertyChangeListener(this);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            setTitle(getTitle());
            try {
                if (getCidsBean().getPrimaryKeyValue() == -1) {
                    getCidsBean().setProperty(FIELD__ANZEIGEN, false);
                }
            } catch (Exception ex) {
                LOG.error("default values not set", ex);
            }
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            lblGeom.setVisible(isEditor());
            RendererTools.makeReadOnly(cbGeom);
            RendererTools.makeReadOnly(txtTitel);
            RendererTools.makeReadOnly(dcMeldungsdatum);
            RendererTools.makeReadOnly(cbKategorie);
            RendererTools.makeReadOnly(taBeschreibung);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taHerkunft);
            RendererTools.makeReadOnly(cbKatHerk);
            RendererTools.makeReadOnly(cbStatus);
            RendererTools.makeReadOnly(dcBeurteilungsdatum);
            RendererTools.makeReadOnly(chAnzeigen);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOREFERENZ) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, 20.0);
            } else {
                final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                final BoundingBox initialBoundingBox;
                initialBoundingBox = CismapBroker.getInstance().getMappingComponent().getMappingModel()
                            .getInitialBoundingBox();
                final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_GEOM,
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, 20.0);
            }
        } catch (final Exception ex) {
            LOG.warn("Can't load Overview.", ex);
        }
    }

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
            TABLE_NAME,
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        return getCidsBean().toString();
    }

    @Override
    public void dispose() {
        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            if (getCidsBean() != null) {
                LOG.info("remove propchange SgkHinweis: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();
        super.dispose();
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ)) {
            setMapWindow();
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // titel vorhanden
        try {
            if (txtTitel.getText().trim().isEmpty()) {
                LOG.warn("No titel specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOTITEL));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Titel not given.", ex);
            save = false;
        }

        // beschreibung vorhanden
        try {
            if (taBeschreibung.getText().trim().isEmpty()) {
                LOG.warn("No beschreibung specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOBESCHREIBUNG));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Beschreibung not given.", ex);
            save = false;
        }

        // herkunft vorhanden
        try {
            if (taHerkunft.getText().trim().isEmpty()) {
                LOG.warn("No herkunft specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOHERKUNFT));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("herkunft not given.", ex);
            save = false;
        }

        // Status muss angegeben werden
        try {
            if (getCidsBean().getProperty(FIELD__STATUS) == null) {
                LOG.warn("No status specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOSTATUS));
                save = false;
            } else {
                // wenn dann auch datum
                final String schluessel = getCidsBean().getProperty(FIELD__STATUS_KEY).toString();
                if (STATUS_LIST.contains(schluessel)) {
                    if (getCidsBean().getProperty(FIELD__BDATUM) == null) {
                        LOG.warn("No bearbeitungsdatum specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOBDATUM));
                        save = false;
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Status not given.", ex);
            save = false;
        }

        // Kategorie Herkunft muss angegeben werden
        try {
            if (getCidsBean().getProperty(FIELD__KATHER) == null) {
                LOG.warn("No kategorie herkunft specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOKATEGORIEH));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Kategorie Herkunft not given.", ex);
            save = false;
        }

        // Meldungsdatum muss angegeben werden
        try {
            if (getCidsBean().getProperty(FIELD__MDATUM) == null) {
                LOG.warn("No meldungsdatum specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOMDATUM));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Meldungsdatum not given.", ex);
            save = false;
        }

        // Kategorie muss angegeben werden
        try {
            if (getCidsBean().getProperty(FIELD__KATEGORIE) == null) {
                LOG.warn("No kategorie specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOKATEGORIE));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Kategorie not given.", ex);
            save = false;
        }

        // georeferenz muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_NOGEOM));
                save = false;
            } /*else {
               * final CidsBean geom_pos = (CidsBean)getCidsBean().getProperty(FIELD__GEOREFERENZ); if
               * (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
               * LOG.warn("Wrong geom specified. Skip persisting.");
               * errorMessage.append(NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_WRONGGEOM));   save = false;
               * }}*/
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(SgkHinweisEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class RegexPatternFormatter extends DefaultFormatter {

        //~ Instance fields ----------------------------------------------------

        protected java.util.regex.Matcher fillingMatcher;
        protected java.util.regex.Matcher matchingMatcher;
        private Object lastValid = null;

        //~ Methods ------------------------------------------------------------

        @Override
        public Object stringToValue(final String string) throws java.text.ParseException {
            if ((string == null) || string.isEmpty()) {
                lastValid = null;
                return null;
            }
            fillingMatcher.reset(string);

            if (!fillingMatcher.matches()) {
                throw new java.text.ParseException("does not match regex", 0);
            }

            final Object value = (String)super.stringToValue(string);

            matchingMatcher.reset(string);
            if (matchingMatcher.matches()) {
                lastValid = value;
            }
            return value;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object getLastValid() {
            return lastValid;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......" });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MustSetModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MustSetModelCb object.
         */
        public MustSetModelCb() {
            super(new String[] { "Die Daten bitte zuweisen......" });
        }
    }
}
