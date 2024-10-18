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

import Sirius.navigator.tools.MetaObjectCache;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.MissingResourceException;

import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TwConfProperties;
import static de.cismet.cids.custom.objecteditors.wunda_blau.EmobLadestationEditor.FOTO_WIDTH;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableLabelsPanel;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.AfterClosingHook;
import de.cismet.cids.editors.hooks.AfterSavingHook;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class TwBrunnenEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    AfterSavingHook,
    AfterClosingHook,
    RequestsFullSizeComponent,
    PropertyChangeListener,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static DefaultBindableReferenceCombo.Option SORTING_OPTION =
        new DefaultBindableReferenceCombo.SortingColumnOption("name");


    private static String MAPURL;
    private static String FOTOS;
    private static Double BUFFER;

    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = { AdresseLightweightSearch.Subject.HNR.toString() };
    

    private static final Logger LOG = Logger.getLogger(TwBrunnenEditor.class);

    public static final String FIELD__ID = "id";
    public static final String FIELD__FREI = "barrierefrei";
    public static final String FIELD__GEB = "gebaeude";
    public static final String FIELD__WARTUNG = "wartung";
    public static final String FIELD__HALB = "halb_oeffentlich";
    public static final String FIELD__LAEUFER = "dauerlaeufer";
    public static final String FIELD__STRASSE_SCHLUESSEL = "fk_strasse.strassenschluessel";
    public static final String FIELD__STRASSE_NAME = "name";                // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";   // strasse
    public static final String FIELD__GEOM = "fk_geom";
    public static final String FIELD__GEO_FIELD = "geo_field";
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field";
    public static final String FIELD__HNR = "fk_adresse";
    public static final String FIELD__HNR_GEOM = "umschreibendes_rechteck"; // adresse
    public static final String TABLE_NAME = "tw_brunnen";
    public static final String TABLE_GEOM = "geom";

   
    public static final String BUNDLE_NOLOAD = "TwBrunnenEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NOGEOM = "TwBrunnenEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_PANE_PREFIX = "TwBrunnenEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "TwBrunnenEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "TwBrunnenEditor.isOkForSaving().JOptionPane.title";
    
    public static final String TEXT_OPEN = "24 Stunden / 7 Tage";
 
    private static final String TITLE_NEW_BRUNNEN = "einen neuen Trinkwasserbrunnen anlegen...";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum DocumentCard {

        //~ Enum constants -----------------------------------------------------

        BUSY, DOCUMENT, NO_DOCUMENT, ERROR
    }


    //~ Instance fields --------------------------------------------------------

  
    private final boolean editor;
    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    private final Collection<DefaultBindableLabelsPanel> labelsPanels = new ArrayList<>();
    
    private final AdresseLightweightSearch hnrSearch = new AdresseLightweightSearch(
            AdresseLightweightSearch.Subject.HNR,
            ADRESSE_TOSTRING_TEMPLATE,
            ADRESSE_TOSTRING_FIELDS);
    private CidsBean beanHNr;
    private final ActionListener hnrActionListener = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0)).getList();
                final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                final Object selectedValue = pop.getSelectedValue();
                txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
            }
        };

 

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private DefaultBindableLabelsPanel blpMassnahmen;
    private DefaultBindableReferenceCombo cbBetreiber;
    private JComboBox cbGeom;
    private FastBindableReferenceCombo cbHNr;
    FastBindableReferenceCombo cbStrasse;
    private JCheckBox chBarrierefrei;
    private JCheckBox chGebaeude;
    private JCheckBox chLaeufer;
    private JCheckBox chOffen;
    private JCheckBox chWartung;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private Box.Filler filler5;
    private JPanel jPanelAllgemein;
    private JLabel lblBarrierefrei;
    private JLabel lblBemerkung;
    private JLabel lblBeschreibung;
    private JLabel lblBetreiber;
    private JLabel lblFoto;
    private JLabel lblFotoAnzeigen;
    private JLabel lblGebaeude;
    private JLabel lblGeom;
    private JLabel lblHalb;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblLaeufer;
    private JLabel lblMassnahmen;
    private JLabel lblName;
    private JLabel lblOffen;
    private JLabel lblStrasse;
    private JLabel lblUrlCheck;
    private JLabel lblWartung;
    private JPanel panBemerkung;
    private JPanel panBeschreibung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panEinsatz;
    private JPanel panFiller;
    private JPanel panGeometrie;
    private JPanel panOffen;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUrl;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpBeschreibung;
    private JScrollPane scpOffen;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextArea taBemerkung;
    private JTextArea taBeschreibung;
    private JTextArea taOffen;
    private JTextField txtFoto;
    private JTextField txtName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public TwBrunnenEditor() {
        this(true);
    }

    /**
     * Creates a new TwBrunnenEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public TwBrunnenEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void showMeasureIsLoading() {
    }

    @Override
    public void showMeasurePanel() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @Override
    public void afterClosing(final AfterClosingHook.Event event) {
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        labelsPanels.clear();
        super.initWithConnectionContext(connectionContext);
        initProperties();
        
        initComponents();
        
        labelsPanels.addAll(Arrays.asList(blpMassnahmen));
        for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
            MetaObjectCache.getInstance().clearCache(labelsPanel.getMetaClass());
            labelsPanel.initWithConnectionContext(getConnectionContext());
        }
        
        setReadOnly();
        txtFoto.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn das Foto geändert wird, wird dieses überprüft und neu geladen.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    doWithFotoUrl();
                }
            });
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
        panEinsatz = new JPanel();
        jPanelAllgemein = new JPanel();
        panGeometrie = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setAllowedGeometryTypes(new Class[] { Point.class});
        }
        lblStrasse = new JLabel();
        cbStrasse = new FastBindableReferenceCombo();
        lblHnr = new JLabel();
        if (isEditor()){
            cbHNr = new FastBindableReferenceCombo(
                hnrSearch,
                hnrSearch.getRepresentationPattern(),
                hnrSearch.getRepresentationFields()
            );
        }
        lblBeschreibung = new JLabel();
        panBeschreibung = new JPanel();
        scpBeschreibung = new JScrollPane();
        taBeschreibung = new JTextArea();
        lblFoto = new JLabel();
        txtFoto = new JTextField();
        panUrl = new JPanel();
        lblUrlCheck = new JLabel();
        lblFotoAnzeigen = new JLabel();
        lblLaeufer = new JLabel();
        chLaeufer = new JCheckBox();
        lblBarrierefrei = new JLabel();
        chBarrierefrei = new JCheckBox();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblBetreiber = new JLabel();
        cbBetreiber = new DefaultBindableReferenceCombo(true) ;
        lblHalb = new JLabel();
        chOffen = new JCheckBox();
        lblWartung = new JLabel();
        chWartung = new JCheckBox();
        lblGebaeude = new JLabel();
        chGebaeude = new JCheckBox();
        lblOffen = new JLabel();
        panOffen = new JPanel();
        scpOffen = new JScrollPane();
        taOffen = new JTextArea();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        panFiller = new JPanel();
        lblMassnahmen = new JLabel();
        blpMassnahmen = new DefaultBindableLabelsPanel(isEditor(), "Massnahmen:", SORTING_OPTION);
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panEinsatz.setOpaque(false);
        panEinsatz.setLayout(new GridBagLayout());

        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

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
        panGeometrie.add(rpKarte, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 10, 10, 10);
        jPanelAllgemein.add(panGeometrie, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        lblName.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (isEditor()){
            cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 11;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStrasse, gridBagConstraints);

        cbStrasse.setMaximumRowCount(20);
        cbStrasse.setModel(new LoadModelCb());

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_strasse}"), cbStrasse, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbStrasse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cbStrasseActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStrasse, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        if (isEditor()){
            cbHNr.setMaximumRowCount(20);
            cbHNr.setEnabled(false);
            cbHNr.setMinimumSize(new Dimension(100, 19));
            cbHNr.setPreferredSize(new Dimension(100, 19));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse}"), cbHNr, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 11;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbHNr, gridBagConstraints);
        }

        lblBeschreibung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung.setText("Beschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschreibung, gridBagConstraints);

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
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(scpBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBeschreibung, gridBagConstraints);

        lblFoto.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFoto.setText("Foto:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblFoto, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bild}"), txtFoto, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtFoto, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panUrl, gridBagConstraints);

        lblFotoAnzeigen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 4);
        panDaten.add(lblFotoAnzeigen, gridBagConstraints);

        lblLaeufer.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLaeufer.setText("Dauerläufer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblLaeufer, gridBagConstraints);

        chLaeufer.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.dauerlaeufer}"), chLaeufer, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chLaeufer, gridBagConstraints);

        lblBarrierefrei.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBarrierefrei.setText("Barrierefrei:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBarrierefrei, gridBagConstraints);

        chBarrierefrei.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.barrierefrei}"), chBarrierefrei, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chBarrierefrei, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblBetreiber.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBetreiber.setText("Betreiber:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBetreiber, gridBagConstraints);

        cbBetreiber.setNullable(false);
        cbBetreiber.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbBetreiber.setMaximumRowCount(6);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_betreiber}"), cbBetreiber, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbBetreiber, gridBagConstraints);

        lblHalb.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHalb.setText("halb-öffentlich:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHalb, gridBagConstraints);

        chOffen.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.halb_oeffentlich}"), chOffen, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chOffen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                chOffenStateChanged(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chOffen, gridBagConstraints);

        lblWartung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblWartung.setText("Wartung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblWartung, gridBagConstraints);

        chWartung.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.wartung}"), chWartung, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chWartung, gridBagConstraints);

        lblGebaeude.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGebaeude.setText("Gebäude:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGebaeude, gridBagConstraints);

        chGebaeude.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.gebaeude}"), chGebaeude, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chGebaeude, gridBagConstraints);

        lblOffen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOffen.setText("Öffnungszeiten:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOffen, gridBagConstraints);

        panOffen.setOpaque(false);
        panOffen.setLayout(new GridBagLayout());

        taOffen.setColumns(20);
        taOffen.setLineWrap(true);
        taOffen.setRows(2);
        taOffen.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.oeffnungszeiten}"), taOffen, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpOffen.setViewportView(taOffen);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOffen.add(scpOffen, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panOffen, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        panDaten.add(panFiller, gridBagConstraints);

        lblMassnahmen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMassnahmen.setText("Massnahmen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMassnahmen, gridBagConstraints);

        blpMassnahmen.setOpaque(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.arr_massnahmen}"), blpMassnahmen, BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(blpMassnahmen, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        lblBemerkung.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
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
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler5, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        jPanelAllgemein.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panEinsatz.add(jPanelAllgemein, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panEinsatz, gridBagConstraints);

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
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrasseActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && (getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    private void chOffenStateChanged(ChangeEvent evt) {//GEN-FIRST:event_chOffenStateChanged
        hatZeiten();
    }//GEN-LAST:event_chOffenStateChanged

   
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
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange Tw_brunnen: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
                cbHNr.removeActionListener(hnrActionListener);
            }
            for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
                if (labelsPanel != null) {
                    labelsPanel.setMetaClass(labelsPanel.getMetaClass());
                }
            }
            labelsPanels.clear();
            //blpMassnahmen.clear();
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange tw_brunnen: " + getCidsBean());
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
            hatZeiten();
            setTitle(getTitle());
            if (getCidsBean() != null) {
                labelsPanels.addAll(Arrays.asList(blpMassnahmen));
            }
            for (final DefaultBindableLabelsPanel labelsPanel : labelsPanels) {
                if (labelsPanel != null) {
                    labelsPanel.reload(true);
                }
            }
            

            if (isEditor()) {
                if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
                    cbHNr.setEnabled(true);
                }
                StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbHNr);
                {
                    final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0)).getList();
                    final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                    cbHNr.addActionListener(hnrActionListener);
                }
                refreshHnr();
                
            }
            beanHNr = ((CidsBean)getCidsBean().getProperty(FIELD__HNR));
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                try {
                    getCidsBean().setProperty(
                        FIELD__HALB,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine halb-öffentlich", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__WARTUNG,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Wartung", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__LAEUFER,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Dauerläufer", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__FREI,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine Barrierefrei", e);
                }
                try {
                    getCidsBean().setProperty(
                        FIELD__GEB,
                        false);
                } catch (Exception e) {
                    LOG.error("Cannot set keine gbaeude", e);
                }
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
            RendererTools.makeReadOnly(taOffen);
            RendererTools.makeReadOnly(cbStrasse);
            // lblHNrRenderer.setVisible(true);
            // RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(blpMassnahmen);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(txtName);
        }
    }

    private void hatZeiten() {
        final boolean isNotOpen = chOffen.isSelected();

        if (isEditor()) {
            taOffen.setEnabled(isNotOpen);
            if (isNotOpen == false) {
                taOffen.setText(TEXT_OPEN);
            } else {
                if (taOffen.getText().equals(TEXT_OPEN)) {
                    taOffen.setText("");
                }
            }
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            if (cb.getProperty(FIELD__GEOM) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, BUFFER, MAPURL);
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
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, BUFFER);
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshHnr() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL) != null)) {
            final String schluessel = getCidsBean().getProperty(FIELD__STRASSE_SCHLUESSEL).toString();
            if (schluessel != null) {
                hnrSearch.setKeyId(Integer.parseInt(schluessel.replaceFirst("0*", "")));

                hnrSearch.setKeyId(Integer.parseInt(schluessel));
                initComboboxHnr();
            }
        }
    }


    /**
     * DOCUMENT ME!
     */
    private void initComboboxHnr() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    cbHNr.refreshModel();
                    return null;
                }

                @Override
                protected void done() {
                }
            }.execute();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  showLabel  DOCUMENT ME!
     */
    private void loadPictureWithUrl(final String url, final JLabel showLabel) {
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {

                @Override
                protected ImageIcon doInBackground() throws Exception {
                    return loadPicture(new URL(url));
                }

                @Override
                protected void done() {
                    final ImageIcon check;
                    try {
                        check = get();
                        if (check != null) {
                            showLabel.setIcon(check);
                            showLabel.setText("");
                            showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            showLabel.setIcon(null);
                            showLabel.setText(NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_NOLOAD));
                            showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setText(NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_NOLOAD));
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("load picture Problem in Worker.", e);
                    }
                }
            };
        worker.execute();
    }
    
    private void doWithFotoUrl() {
        final String foto = FOTOS.concat(txtFoto.getText());
        
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ImageIcon loadPicture(final URL url) {
        try {
            final int bildZielBreite = FOTO_WIDTH;
            final BufferedImage originalBild = ImageIO.read(WebAccessManager.getInstance().doRequest(url));
            final Image skaliertesBild = originalBild.getScaledInstance(bildZielBreite, -1, Image.SCALE_SMOOTH);
            return new ImageIcon(skaliertesBild);
        } catch (final Exception ex) {
            LOG.error("Could not load picture.", ex);
            return null;
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  showLabel  DOCUMENT ME!
     */
    private void checkUrl(final String url, final JLabel showLabel) {
        showLabel.setIcon(statusFalsch);
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return WebAccessManager.getInstance().checkIfURLaccessible(new URL(url));
                }

                @Override
                protected void done() {
                    final Boolean check;
                    try {
                        check = get();
                        if (check) {
                            showLabel.setIcon(statusOk);
                            showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            showLabel.setIcon(statusFalsch);
                            showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("URL Check Problem in Worker.", e);
                    }
                }
            };
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void initProperties() {
        try {
            BUFFER = TwConfProperties.getInstance().getBufferMeter();
            MAPURL = TwConfProperties.getInstance().getUrl();
            FOTOS = TwConfProperties.getInstance().getFotos();
        } catch (final Exception ex) {
            LOG.warn("Get no conf properties.", ex);
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
            return TITLE_NEW_BRUNNEN;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void dispose() {
        panPreviewMap.dispose();

        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            
            cbHNr.removeActionListener(hnrActionListener);
            cbHNr.removeAll();
            
            if (getCidsBean() != null) {
                LOG.info("remove propchange tw_brunnen: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        }
        bindingGroup.unbind();
        if (labelsPanels != null) {
            for (final DefaultBindableLabelsPanel panel : labelsPanels) {
                panel.dispose();
            }
        }
        labelsPanels.clear();

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
        }
        
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();


        // georeferenz muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__GEOM) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_NOGEOM));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(TwBrunnenEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } 
        return save;
    }

    @Override
    public void afterSaving(final AfterSavingHook.Event event) {

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

    
}
