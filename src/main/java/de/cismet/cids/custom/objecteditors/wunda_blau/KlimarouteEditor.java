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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.MissingResourceException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.KlimarouteConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;

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
import java.util.ArrayList;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class KlimarouteEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    BeforeSavingHook,
    PropertyChangeListener,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    public static final String GEOMTYPE = "LineString";

    private static final Logger LOG = Logger.getLogger(KlimarouteEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = { "name", "id" };
    public static final String REDUNDANT_TABLE = "klimaroute";



    public static final String FIELD__NAME = "name";                                        // klimaroute
    public static final String FIELD__KEY = "key";                                          // klimaroute
    public static final String FIELD__ID = "id";                                            // klimaroute
    public static final String FIELD__DIFICULTY = "fk_schwierigkeitsgrad";                  // klimaroute
    public static final String FIELD__WAY = "fk_wegeart";                                   // klimaroute
    public static final String FIELD__DISTANCE = "distanz";                                 // klimaroute
    public static final String FIELD__GEOM = "geom";                                        // klimaroute
    public static final String FIELD__DAUER = "dauer";                                      // klimaroute
    public static final String FIELD__PUBLISH = "to_publish";                               // klimaroute
    public static final String FIELD__GEO_FIELD = "geo_field";                              // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "geom.geo_field";            // klimaroute.geom

    public static final String TABLE_NAME = "klimaroute";
    public static final String TABLE_GEOM = "geom";

    public static final String BUNDLE_NONAME = "KlimarouteEditor.isOkForSaving().noName";
    public static final String BUNDLE_DUPLICATENAME = "KlimarouteEditor.isOkForSaving().duplicateName";
    public static final String BUNDLE_NOKEY = "KlimarouteEditor.isOkForSaving().noKey";
    public static final String BUNDLE_DUPLICATEKEY = "KlimarouteEditor.isOkForSaving().duplicateKey";
    public static final String BUNDLE_LENGTHKEY = "KlimarouteEditor.isOkForSaving().lengthKey";
    public static final String BUNDLE_WRONGKEY = "KlimarouteEditor.isOkForSaving().wrongKey";
    public static final String BUNDLE_NODIFICULTY = "KlimarouteEditor.isOkForSaving().noDificulty";
    public static final String BUNDLE_NOWAY = "KlimarouteEditor.isOkForSaving().noWay";
    public static final String BUNDLE_WRONGGEOM = "KlimarouteEditor.isOkForSaving().wrongGeom";
    public static final String BUNDLE_PANE_PREFIX = "KlimarouteEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "KlimarouteEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "KlimarouteEditor.isOkForSaving().JOptionPane.title";
    private static final String TITLE_NEW_KLIMAROUTE = "eine neue Klimaroute anlegen...";
    private static Color colorAlarm = new java.awt.Color(255, 0, 0);

    //~ Instance fields --------------------------------------------------------

    ChangeListener listener = new ChangeListener() {

            @Override
            public void stateChanged(final ChangeEvent e) {
                writeDauer();
            }
        };

    /** DOCUMENT ME! */
    private final boolean editor;
    @Getter@Setter private static String keyPattern = "";
    @Getter@Setter private Integer keyLength = 0;
    private Boolean keyRedundant = false;
    private Boolean nameRedundant = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox cbGeom;
    private DefaultBindableReferenceCombo cbSchwierigkeit;
    private DefaultBindableReferenceCombo cbWegeart;
    private JCheckBox chkVeroeffentlicht;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private JPanel jPanelAllgemein;
    private JLabel lblBeschreibung;
    private JLabel lblDauer;
    private JLabel lblDistanz;
    private JLabel lblDistanzRO;
    private JLabel lblGeom;
    private JLabel lblKarte;
    private JLabel lblKey;
    private JLabel lblMeter;
    private JLabel lblMinute;
    private JLabel lblName;
    private JLabel lblSchwierigkeit;
    private JLabel lblStunde;
    private JLabel lblUrl;
    private JLabel lblVeroeffentlicht;
    private JLabel lblWegeart;
    private JPanel panBeschreibung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFillerUnten;
    private JPanel panLage;
    private JPanel panLinks;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panRechts;
    private RoundedPanel rpKarte;
    private JScrollPane scpBeschreibung;
    private SemiRoundedPanel semiRoundedPanel7;
    JSpinner spMinute;
    JSpinner spStunde;
    private JTextArea taBeschreibung;
    private JTextField txtKey;
    private JTextField txtName;
    private JTextField txtUrl;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public KlimarouteEditor() {
        this(true);
    }

    /**
     * Creates a new KlimarouteEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public KlimarouteEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        if (isEditor()) {
            spMinute.addChangeListener(listener);
            spStunde.addChangeListener(listener);
        }
        setReadOnly();
        setKeyPattern(KlimarouteConfProperties.getInstance().getKeyPattern());
        setKeyLength(KlimarouteConfProperties.getInstance().getKeyLength());
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

        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        jPanelAllgemein = new JPanel();
        panDaten = new JPanel();
        panLinks = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblDauer = new JLabel();
        spStunde = new JSpinner();
        lblStunde = new JLabel();
        spMinute = new JSpinner();
        lblMinute = new JLabel();
        lblDistanz = new JLabel();
        lblDistanzRO = new JLabel();
        lblMeter = new JLabel();
        lblSchwierigkeit = new JLabel();
        cbSchwierigkeit = new DefaultBindableReferenceCombo(true) ;
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblWegeart = new JLabel();
        cbWegeart = new DefaultBindableReferenceCombo(true) ;
        lblVeroeffentlicht = new JLabel();
        chkVeroeffentlicht = new JCheckBox();
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblUrl = new JLabel();
        txtUrl = new JTextField();
        lblKey = new JLabel();
        txtKey = new JTextField();
        panRechts = new JPanel();
        panBeschreibung = new JPanel();
        scpBeschreibung = new JScrollPane();
        taBeschreibung = new JTextArea();
        lblBeschreibung = new JLabel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();

        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(840, 298));
        panContent.setLayout(new GridBagLayout());

        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        panLinks.setOpaque(false);
        panLinks.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(txtName, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        panLinks.add(filler3, gridBagConstraints);

        lblDauer.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDauer.setText("Dauer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblDauer, gridBagConstraints);

        spStunde.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spStunde.setModel(new SpinnerNumberModel(0, 0, 20, 1));
        spStunde.setPreferredSize(new Dimension(48, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(spStunde, gridBagConstraints);

        lblStunde.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStunde.setText("h");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 5);
        panLinks.add(lblStunde, gridBagConstraints);

        spMinute.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spMinute.setModel(new SpinnerNumberModel(0, 0, 59, 1));
        spMinute.setPreferredSize(new Dimension(48, 20));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(spMinute, gridBagConstraints);

        lblMinute.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMinute.setText("min");
        lblMinute.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 5);
        panLinks.add(lblMinute, gridBagConstraints);

        lblDistanz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDistanz.setText("Distanz:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblDistanz, gridBagConstraints);

        lblDistanzRO.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.distanz}"), lblDistanzRO, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panLinks.add(lblDistanzRO, gridBagConstraints);

        lblMeter.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMeter.setText("km");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 5);
        panLinks.add(lblMeter, gridBagConstraints);

        lblSchwierigkeit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSchwierigkeit.setText("Schwierigkeit:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblSchwierigkeit, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_schwierigkeitsgrad}"), cbSchwierigkeit, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(cbSchwierigkeit, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cbSchwierigkeit).setNullable(true);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 10, 0, 10);
        panLinks.add(filler4, gridBagConstraints);

        lblWegeart.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblWegeart.setText("Wegeart:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblWegeart, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_wegeart}"), cbWegeart, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(cbWegeart, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cbSchwierigkeit).setNullable(true);

        lblVeroeffentlicht.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVeroeffentlicht.setText("Veröffentlicht:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblVeroeffentlicht, gridBagConstraints);

        chkVeroeffentlicht.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.to_publish}"), chkVeroeffentlicht, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panLinks.add(chkVeroeffentlicht, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblGeom, gridBagConstraints);

        if (isEditor()){
            if (editor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 8;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 5;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panLinks.add(cbGeom, gridBagConstraints);
        }

        lblUrl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("URL:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblUrl, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.url}"), txtUrl, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(txtUrl, gridBagConstraints);

        lblKey.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKey.setText("Key:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panLinks.add(lblKey, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.key}"), txtKey, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panLinks.add(txtKey, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panLinks, gridBagConstraints);

        panRechts.setOpaque(false);
        panRechts.setLayout(new GridBagLayout());

        panBeschreibung.setOpaque(false);
        panBeschreibung.setLayout(new GridBagLayout());

        taBeschreibung.setColumns(20);
        taBeschreibung.setLineWrap(true);
        taBeschreibung.setRows(2);
        taBeschreibung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beschreibung}"), taBeschreibung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBeschreibung.setViewportView(taBeschreibung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(scpBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panRechts.add(panBeschreibung, gridBagConstraints);

        lblBeschreibung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung.setText("Beschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panRechts.add(lblBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panDaten.add(panRechts, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 10, 0, 10);
        jPanelAllgemein.add(panDaten, gridBagConstraints);

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpKarte.add(panPreviewMap, gridBagConstraints);

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

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLage.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelAllgemein.add(panLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panContent.add(jPanelAllgemein, gridBagConstraints);

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
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    public void writeDauer() {
        String minute = spMinute.getValue().toString();
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        final String stunde = spStunde.getValue().toString();
        final String dauer = stunde + ":" + minute;
        try {
            getCidsBean().setProperty(FIELD__DAUER, dauer);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("dauer not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void readDauer() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__DAUER) != null)) {
            final String dauer = getCidsBean().getProperty(FIELD__DAUER).toString();
            final String[] values = dauer.split(":");
            spMinute.setValue(Integer.parseInt(values[1]));
            spStunde.setValue(Integer.parseInt(values[0]));
        }
    }
    
    @Override
    public void beforeSaving() {
        final RedundantObjectSearch krSearch = new RedundantObjectSearch(
                REDUNDANT_TOSTRING_TEMPLATE,
                REDUNDANT_TOSTRING_FIELDS,
                null,
                REDUNDANT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        // redundanter name
        conditions.add(FIELD__NAME + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        krSearch.setWhere(conditions);
        try {
            setNameRedundant(
                !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        krSearch,
                        getConnectionContext())).isEmpty());
        } catch (ConnectionException ex) {
            LOG.warn("problem in check name: load values.", ex);
        }
        // redundanter Key
        conditions.clear();
        conditions.add(FIELD__KEY + " ilike '" + txtKey.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        krSearch.setWhere(conditions);
        try {
            setKeyRedundant(
                !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        krSearch,
                        getConnectionContext())).isEmpty());
        } catch (ConnectionException ex) {
            LOG.warn("problem in check key: load values.", ex);
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditor() {
        return this.editor;
    }
    
    public boolean isKeyRedundant() {
        return this.keyRedundant;
    }
    
    public void setKeyRedundant(boolean ok){
        this.keyRedundant = ok;
    }
    public boolean isNameRedundant() {
        return this.nameRedundant;
    }
    
    public void setNameRedundant(boolean ok){
        this.nameRedundant = ok;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange klimaroute: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange klimaroute: " + getCidsBean());
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
            readDauer();
            setTitle(getTitle());
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                try {
                    getCidsBean().setProperty(FIELD__PUBLISH, false);
                } catch (Exception e) {
                    LOG.error("Cannot set default values", e);
                }
            }
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = KlimarouteConfProperties.getInstance().getBufferMeter();
            final String mapUrl = KlimarouteConfProperties.getInstance().getMapUrl();
            if (cb.getProperty(FIELD__GEOM) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, bufferMeter, mapUrl);
            } else {
                final int srid = CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode());
                final BoundingBox initialBoundingBox = CismapBroker.getInstance()
                            .getMappingComponent()
                            .getMappingModel()
                            .getInitialBoundingBox();
                final Point centerPoint = initialBoundingBox.getGeometry(srid).getCentroid();

                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_GEOM,
                        getConnectionContext());
                final CidsBean newGeom = geomMetaClass.getEmptyInstance(getConnectionContext()).getBean();
                newGeom.setProperty(FIELD__GEO_FIELD, centerPoint);
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, bufferMeter);
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeDoubleSpinnerWithoutButtons(spStunde, 0);
            RendererTools.makeReadOnly(spStunde);
            RendererTools.makeDoubleSpinnerWithoutButtons(spMinute, 0);
            RendererTools.makeReadOnly(spMinute);
            RendererTools.makeReadOnly(cbSchwierigkeit);
            RendererTools.makeReadOnly(txtUrl);
            RendererTools.makeReadOnly(taBeschreibung);
            RendererTools.makeReadOnly(cbWegeart);
            RendererTools.makeReadOnly(chkVeroeffentlicht);
            RendererTools.makeReadOnly(txtKey);
            lblGeom.setVisible(isEditor());
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
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_KLIMAROUTE;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void dispose() {
        panPreviewMap.dispose();

        if (isEditor()) {
            if (cbGeom != null) {
                ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
                ((DefaultCismapGeometryComboBoxEditor)cbGeom).setCidsMetaObject(null);
                cbGeom = null;
            }
            spMinute.removeChangeListener(listener);
            spStunde.removeChangeListener(listener);
            if (getCidsBean() != null) {
                LOG.info("remove propchangeklimaroute: " + getCidsBean());
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
        if (evt.getPropertyName().equals(FIELD__GEOM)) {
            setMapWindow();
            setDistance();
            /*if (evt.getOldValue() == null){
             *  if(evt.getNewValue() != null){     setMapWindow();     setDistance(); } } else { if(evt.getNewValue() ==
             * null){     setMapWindow();     setDistance(); } else {     if (evt.getOldValue() != evt.getNewValue()){
             *       setMapWindow();         setDistance();     } }}*/
        }
       if (evt.getPropertyName().equals(FIELD__KEY)) {
            if (getCidsBean().getMetaObject().getStatus() != MetaObject.NEW) {
                lblKey.setForeground(colorAlarm);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setDistance() {
        double length = 0.0;
        if (this.getCidsBean() != null) {
            if (this.getCidsBean().getProperty(FIELD__GEOM) != null) {
                final CidsBean geom_pos = (CidsBean)getCidsBean().getProperty(FIELD__GEOM);
                length = Math.round(((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getLength()) / 1000.0;
            }
        }
        try {
            getCidsBean().setProperty(FIELD__DISTANCE, length);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("distanz not set.", ex);
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                //name redundant
                if (isNameRedundant()){
                    LOG.warn("Duplicate Name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_DUPLICATENAME));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // schwierigkeitsgrad vorhanden
        try {
            if (getCidsBean().getProperty(FIELD__DIFICULTY) == null) {
                LOG.warn("No schwierigkeitsgrad specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_NODIFICULTY));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("schwierigkeitsgrad not given.", ex);
            save = false;
        }

        // wegeart vorhanden
        try {
            if (getCidsBean().getProperty(FIELD__WAY) == null) {
                LOG.warn("No wegeart specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_NOWAY));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("wegeart not given.", ex);
            save = false;
        }

        // georeferenz muss, wenn gefüllt. ein Punkt sein
        try {
            if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__GEOM) != null)) {
                final CidsBean geom_pos = (CidsBean)getCidsBean().getProperty(FIELD__GEOM);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_WRONGGEOM));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom wrong.", ex);
            save = false;
        }
        
        // key vorhanden
        try {
            if (txtKey.getText().trim().isEmpty()) {
                LOG.warn("No key specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_NOKEY));
                save = false;
            } else{
                //key entspricht pattern
                if (!txtKey.getText().matches(keyPattern)){
                    LOG.warn("Wrong key specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_WRONGKEY));
                    save = false;
                } else{
                    //key redundant
                    if (isKeyRedundant()){
                        LOG.warn("Wrong key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_DUPLICATEKEY));
                        save = false;
                    } else{
                    //key length
                        if (getCidsBean().getProperty(FIELD__KEY).toString().length() > keyLength){
                            LOG.warn("Wrong key length specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_LENGTHKEY));
                            save = false;
                        }
                    }
                } 
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Key not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(KlimarouteEditor.class, BUNDLE_PANE_TITLE),
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
