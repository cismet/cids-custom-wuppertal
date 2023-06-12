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
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXBusyLabel;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.*;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.BparkConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.wunda_blau.search.server.BparkFotosLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.RasterfariDocumentLoaderPanel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import org.openide.util.Exceptions;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BparkZoneEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    RequestsFullSizeComponent,
    BeforeSavingHook,
    PropertyChangeListener,
    RasterfariDocumentLoaderPanel.Listener {

    //~ Static fields/initializers ---------------------------------------------

    private static String FOTOS;
    private static String THEMA;
    private static String RASTERFARI;
    private static String MAPURL;
    private static Double BUFFER;
    public static final String GEOMTYPE = "Polygon";
    private static final String FOTOS_TOSTRING_TEMPLATE = "%s";
    private static final String[] FOTOS_TOSTRING_FIELDS = { "name" };

    private static final Logger LOG = Logger.getLogger(BparkZoneEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = { "zone", "nummer", "id" };
    public static final String REDUNDANT_TABLE = "bpark_zone";

    public static final String FIELD__ID = "id";                                        
    public static final String FIELD__ZONE = "zone";                                        
    public static final String FIELD__NUMMER = "nummer";                     
    public static final String FIELD__FOTONAME = "name";                                   
    public static final String FIELD__GEOM = "fk_geom";                             
    public static final String FIELD__GEO_FIELD = "geo_field";                              
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field";         
    public static final String TABLE_NAME = "bpark_zone";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_FOTOS = "bpark_fotos";

    public static final String BUNDLE_NOZONE = "BparkZoneEditor.isOkForSaving().noZone";
    public static final String BUNDLE_ZONEFALSE = "BparkZoneEditor.isOkForSaving().ZoneFalse";
    public static final String BUNDLE_DUPLICATEZONE = "BparkZoneEditor.isOkForSaving().duplicateZone";
    public static final String BUNDLE_NUMMERFALSE = "BparkZoneEditor.isOkForSaving().NummerFalse";
    public static final String BUNDLE_NOGEOM = "BparkZoneEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BparkZoneEditor.isOkForSaving().wrongGeom";
    public static final String BUNDLE_PANE_PREFIX = "BparkZoneEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BparkZoneEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BparkZoneEditor.isOkForSaving().JOptionPane.title";
    
    private static final String TITLE_NEW_ZONE = "eine neue Zone anlegen...";
    private static Color colorAlarm = new java.awt.Color(255, 0, 0);
    private static Color colorNormal = new java.awt.Color(0, 0, 0);

    /** DOCUMENT ME! */
    public static String nummerPattern = ""; // [0-9a-zA-Z\\s\\-\\_\\ä\\ö\\ü\\ß]{1,}";
    public static String zonePattern = "";     // [0-9a-zA-Z\\-\\_]{1,}";

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
    private final BparkFotosLightweightSearch searchFotos;
    private Boolean redundantZoneNummer = false;


    private final boolean editor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox cbGeom;
    private JCheckBox chkVeroeffentlicht;
    private Box.Filler filler3;
    private JLabel jLabel2;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanelAllgemein;
    private JPanel jPanelFotoAuswahl;
    private JPanel jPanelFotos;
    JTabbedPane jTabbedPane;
    private JXBusyLabel jxLBusy;
    private JLabel lblBemerkung;
    private JLabel lblGeom;
    private JLabel lblHeaderDocument;
    private JLabel lblHeaderListe;
    private JLabel lblHeaderPages;
    private JLabel lblHinweis;
    private JLabel lblKarte;
    private JLabel lblKeineFotos;
    private JLabel lblNummer;
    private JLabel lblVeroeffentlicht;
    private JLabel lblZone;
    private JList lstFotos;
    private JList lstPages;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panGebiet;
    private JPanel panHinweis;
    private JPanel panLage;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel pnlBild;
    private JPanel pnlCard1;
    private RoundedPanel pnlDocument;
    private SemiRoundedPanel pnlHeaderDocument;
    private SemiRoundedPanel pnlHeaderListe;
    private SemiRoundedPanel pnlHeaderPages;
    private RoundedPanel pnlListe;
    private RoundedPanel pnlPages;
    private RasterfariDocumentLoaderPanel rasterfariDocumentLoaderPanel1;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpFotos;
    private JScrollPane scpHinweis;
    private JScrollPane scpPages;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextArea taBemerkung;
    private JTextArea taHinweis;
    private JTextField txtNummer;
    private JTextField txtZone;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BparkZoneEditor() {
        this(true);
    }

    /**
     * Creates a new BparkZoneEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BparkZoneEditor(final boolean boolEditor) {
        this.editor = boolEditor;
        searchFotos = new BparkFotosLightweightSearch(
                FOTOS_TOSTRING_TEMPLATE,
                FOTOS_TOSTRING_FIELDS);
    }

    //~ Methods ----------------------------------------------------------------


    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initProperties();
        initComponents();
        zonePattern = BparkConfProperties.getInstance().getZonePattern();
        nummerPattern = BparkConfProperties.getInstance().getNummerPattern();

        lstFotos.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty(FIELD__FOTONAME);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoName = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoName.setForeground(Color.black);
                    return compoName;
                }
            });
        

        if (lstFotos != null) {
            lstFotos.setSelectedIndex(0);
        }
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
        panGebiet = new JPanel();
        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panDaten = new JPanel();
        lblZone = new JLabel();
        txtZone = new JTextField();
        lblNummer = new JLabel();
        txtNummer = new JTextField();
        lblGeom = new JLabel();
        if (isEditor()){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHinweis = new JLabel();
        panHinweis = new JPanel();
        scpHinweis = new JScrollPane();
        taHinweis = new JTextArea();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panFiller = new JPanel();
        lblVeroeffentlicht = new JLabel();
        chkVeroeffentlicht = new JCheckBox();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        jPanelFotos = new JPanel();
        jPanelFotoAuswahl = new JPanel();
        pnlDocument = new RoundedPanel();
        pnlHeaderDocument = new SemiRoundedPanel();
        lblHeaderDocument = new JLabel();
        pnlBild = new JPanel();
        jPanel1 = new JPanel();
        rasterfariDocumentLoaderPanel1 = new RasterfariDocumentLoaderPanel(
            RASTERFARI,
            this,
            getConnectionContext()
        );
        jPanel2 = new JPanel();
        jxLBusy = new JXBusyLabel(new Dimension(64,64));
        jPanel3 = new JPanel();
        lblKeineFotos = new JLabel();
        jPanel4 = new JPanel();
        jLabel2 = new JLabel();
        pnlPages = new RoundedPanel();
        pnlHeaderPages = new SemiRoundedPanel();
        lblHeaderPages = new JLabel();
        scpPages = new JScrollPane();
        lstPages = rasterfariDocumentLoaderPanel1.getLstPages();
        pnlListe = new RoundedPanel();
        pnlHeaderListe = new SemiRoundedPanel();
        lblHeaderListe = new JLabel();
        scpFotos = new JScrollPane();
        lstFotos = new JList();

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panGebiet.setOpaque(false);
        panGebiet.setLayout(new GridBagLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new GridBagLayout());

        jPanelAllgemein.setOpaque(false);
        jPanelAllgemein.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblZone.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblZone.setText("Zone:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblZone, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.zone}"), txtZone, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtZone, gridBagConstraints);

        lblNummer.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblNummer.setText("Nummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblNummer, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.nummer}"), txtNummer, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtNummer, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (isEditor()){
            if (editor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 4;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHinweis.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHinweis.setText("Hinweis:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHinweis, gridBagConstraints);

        panHinweis.setOpaque(false);
        panHinweis.setLayout(new GridBagLayout());

        taHinweis.setColumns(20);
        taHinweis.setLineWrap(true);
        taHinweis.setRows(2);
        taHinweis.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hinweis}"), taHinweis, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpHinweis.setViewportView(taHinweis);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panHinweis.add(scpHinweis, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panHinweis, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        panDaten.add(panFiller, gridBagConstraints);

        lblVeroeffentlicht.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVeroeffentlicht.setText("Veröffentlicht:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblVeroeffentlicht, gridBagConstraints);

        chkVeroeffentlicht.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.veroeffentlicht}"), chkVeroeffentlicht, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 2);
        panDaten.add(chkVeroeffentlicht, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
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

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelFotos.setOpaque(false);
        jPanelFotos.setLayout(new GridBagLayout());

        jPanelFotoAuswahl.setOpaque(false);
        jPanelFotoAuswahl.setLayout(new GridBagLayout());

        pnlDocument.setLayout(new GridBagLayout());

        pnlHeaderDocument.setBackground(Color.darkGray);
        pnlHeaderDocument.setLayout(new GridBagLayout());

        lblHeaderDocument.setForeground(Color.white);
        lblHeaderDocument.setText("Foto");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        pnlHeaderDocument.add(lblHeaderDocument, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        pnlDocument.add(pnlHeaderDocument, gridBagConstraints);

        pnlBild.setOpaque(false);
        pnlBild.setLayout(new CardLayout());

        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(rasterfariDocumentLoaderPanel1, BorderLayout.CENTER);

        pnlBild.add(jPanel1, "DOCUMENT");

        jPanel2.setLayout(new BorderLayout());

        jxLBusy.setHorizontalAlignment(SwingConstants.CENTER);
        jxLBusy.setPreferredSize(new Dimension(64, 64));
        jPanel2.add(jxLBusy, BorderLayout.CENTER);

        pnlBild.add(jPanel2, "BUSY");

        jPanel3.setLayout(new BorderLayout());

        lblKeineFotos.setHorizontalAlignment(SwingConstants.CENTER);
        lblKeineFotos.setText("Für dieses Gebiet sind beim nächtlichen Abgleich keine Fotos vorhanden gewesen.");
        jPanel3.add(lblKeineFotos, BorderLayout.CENTER);

        pnlBild.add(jPanel3, "NO_DOCUMENT");

        jPanel4.setLayout(new BorderLayout());

        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Das Foto für dieses Gebiet kann nicht geladen werden.");
        jPanel4.add(jLabel2, BorderLayout.CENTER);

        pnlBild.add(jPanel4, "ERROR");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new Insets(0, 0, 8, 0);
        pnlDocument.add(pnlBild, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlDocument, gridBagConstraints);

        pnlHeaderPages.setBackground(new Color(51, 51, 51));
        pnlHeaderPages.setLayout(new FlowLayout());

        lblHeaderPages.setForeground(new Color(255, 255, 255));
        lblHeaderPages.setText(NbBundle.getMessage(BparkZoneEditor.class, "VermessungRissEditor.lblHeaderPages.text")); // NOI18N
        pnlHeaderPages.add(lblHeaderPages);

        pnlPages.add(pnlHeaderPages, BorderLayout.NORTH);

        scpPages.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scpPages.setMinimumSize(new Dimension(31, 75));
        scpPages.setOpaque(false);
        scpPages.setPreferredSize(new Dimension(85, 75));

        lstPages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstPages.setFixedCellWidth(75);
        scpPages.setViewportView(lstPages);

        pnlPages.add(scpPages, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlPages, gridBagConstraints);

        pnlListe.setMinimumSize(new Dimension(200, 49));

        pnlHeaderListe.setBackground(new Color(51, 51, 51));
        pnlHeaderListe.setLayout(new FlowLayout());

        lblHeaderListe.setForeground(new Color(255, 255, 255));
        lblHeaderListe.setText("Fotoliste");
        pnlHeaderListe.add(lblHeaderListe);

        pnlListe.add(pnlHeaderListe, BorderLayout.NORTH);

        scpFotos.setPreferredSize(new Dimension(80, 130));

        lstFotos.setModel(new DefaultListModel<>());
        lstFotos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstFotos.setFixedCellWidth(75);
        lstFotos.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent evt) {
                lstFotosValueChanged(evt);
            }
        });
        scpFotos.setViewportView(lstFotos);

        pnlListe.add(scpFotos, BorderLayout.CENTER);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new Insets(10, 0, 0, 5);
        jPanelFotoAuswahl.add(pnlListe, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        jPanelFotos.add(jPanelFotoAuswahl, gridBagConstraints);

        jTabbedPane.addTab("Fotos", jPanelFotos);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGebiet.add(pnlCard1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(panGebiet, gridBagConstraints);

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
    private void lstFotosValueChanged(final ListSelectionEvent evt) {//GEN-FIRST:event_lstFotosValueChanged
        showFoto();
    }//GEN-LAST:event_lstFotosValueChanged

    public boolean isEditor() {
        return this.editor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cbList  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer getActiveBeans(final List<CidsBean> cbList) {
        Integer anzahl = 0;
        if (cbList != null) {
            for (final CidsBean bean : cbList) {
                if (bean.getMetaObject().getStatus() != MetaObject.TO_DELETE) {
                    anzahl += 1;
                }
            }
        }
        return anzahl;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  card  DOCUMENT ME!
     */
    private void showDocumentCard(final DocumentCard card) {
        ((CardLayout)pnlBild.getLayout()).show(pnlBild, card.toString());
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange bpark_zone: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange bpark_zone: " + getCidsBean());
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
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
        loadFotoList();
        showFoto();
    }


    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            lblGeom.setVisible(isEditor());
            RendererTools.makeReadOnly(txtNummer);
            RendererTools.makeReadOnly(txtZone);
            RendererTools.makeReadOnly(taHinweis);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(chkVeroeffentlicht);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void loadFotoList() {
        try {
            searchFotos.setGebietId(getCidsBean().getPrimaryKeyValue());
            searchFotos.setTableName(TABLE_FOTOS);
            searchFotos.setRepresentationFields(FOTOS_TOSTRING_FIELDS);
            final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                        .customServerSearch(searchFotos,
                            getConnectionContext());
            final List<CidsBean> beansFotos = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansFotos.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            getConnectionContext()).getBean());
                }
                for (final CidsBean fotoBean : beansFotos) {
                    ((DefaultListModel)lstFotos.getModel()).addElement(fotoBean);
                }
                lstFotos.setSelectedIndex(0);
                lstFotos.addMouseMotionListener(new MouseMotionAdapter() {

                        @Override
                        public void mouseMoved(final MouseEvent e) {
                            final JList l = (JList)e.getSource();
                            final ListModel m = l.getModel();
                            final int index = l.locationToIndex(e.getPoint());
                            if (index > -1) {
                                l.setToolTipText(m.getElementAt(index).toString());
                            }
                        }
                    });
            }
        } catch (ConnectionException ex) {
            LOG.error("Error during loading fotos", ex);
        }
    }

    
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
    private void initProperties() {
        try {
            FOTOS = BparkConfProperties.getInstance().getOrdnerFotos();
            RASTERFARI = BparkConfProperties.getInstance().getUrlRasterfari();
            THEMA = BparkConfProperties.getInstance().getOrdnerThema();
            BUFFER = BparkConfProperties.getInstance().getBufferMeter();
            MAPURL = BparkConfProperties.getInstance().getUrlMap();
        } catch (final Exception ex) {
            LOG.warn("Get no conf properties.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void showFoto() {
        final String id;
        final String fotoUrl;
        final String fotoName;
        if (!lstFotos.isSelectionEmpty()) {
            try {
                fotoName = lstFotos.getSelectedValue().toString();
                id = cidsBean.getPrimaryKeyValue().toString();
                fotoUrl = THEMA + "/" + FOTOS + "/" + id + "/"  + fotoName;
                rasterfariDocumentLoaderPanel1.setDocument(fotoUrl);
            } catch (final Exception ex) {
                LOG.warn("Get no foto.", ex);
            }
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
            return TITLE_NEW_ZONE;
        } else {
            return getCidsBean().toString();
        }
    }


    
    @Override
    public void dispose() {
        panPreviewMap.dispose();

        if (isEditor()) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            if (getCidsBean() != null) {
                LOG.info("remove propchange bpark_zone: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(this);
            }
        } 
        lstFotos.removeAll();
        rasterfariDocumentLoaderPanel1.dispose();
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
        if (evt.getPropertyName().equals(FIELD__GEOM)){
            setMapWindow();
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // zone vorhanden
        try {
            if (txtZone.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_NOZONE));
                save = false;
            } else {
                if (redundantZoneNummer) {
                    LOG.warn("False zone specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_DUPLICATEZONE));
                    save = false;
                } else {
                    // korrekte Zeichen
                    if (!txtZone.getText().matches(zonePattern)) {
                        LOG.warn("False zone specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(PoiZoomkeyEditor.class, BUNDLE_ZONEFALSE));
                        save = false;
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Zone not given.", ex);
            save = false;
        }
        // nummer, wenn vorhanden ok
        try {
            if (!txtNummer.getText().trim().isEmpty()) {
                if (!txtNummer.getText().matches(nummerPattern)) {
                    LOG.warn("No aktenzeichen specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_NUMMERFALSE));
                    save = false;
                }
            
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Aktenzeichen not given.", ex);
            save = false;
        }

        // georeferenz muss gefüllt sein
        try {
            if (getCidsBean().getProperty(FIELD__GEOM) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_NOGEOM));
                save = false;
            } else {
                final CidsBean geom_pos = (CidsBean)getCidsBean().getProperty(FIELD__GEOM);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_WRONGGEOM));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BparkZoneEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    @Override
    public void beforeSaving() {
        final RedundantObjectSearch zoneSearch = new RedundantObjectSearch(
                REDUNDANT_TOSTRING_TEMPLATE,
                REDUNDANT_TOSTRING_FIELDS,
                null,
                REDUNDANT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        conditions.add(FIELD__ZONE + " ilike '" + txtZone.getText().trim() + "'");
        if(txtNummer.getText().trim().isEmpty()){
            conditions.add("((" + FIELD__NUMMER + " ilike '" + txtNummer.getText().trim() + "') OR" 
                          + "(" + FIELD__NUMMER + " is null))");
        }else{
            conditions.add(FIELD__NUMMER + " ilike '" + txtNummer.getText().trim() + "'");
        }
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        zoneSearch.setWhere(conditions);
        try {
            redundantZoneNummer =
                !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        zoneSearch,
                        getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in beforeSaving.", ex);
        }
    }

    @Override
    public void showMeasureIsLoading() {
        showDocumentCard(DocumentCard.BUSY);
    }

    @Override
    public void showMeasurePanel() {
        showDocumentCard(DocumentCard.DOCUMENT);
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


}
