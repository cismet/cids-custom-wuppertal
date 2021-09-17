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

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
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

import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;


import javax.swing.*;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;


import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.custom.wunda_blau.search.server.AdresseLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.AfterClosingHook;
import de.cismet.cids.editors.hooks.AfterSavingHook;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import javax.swing.JList;
import javax.swing.plaf.basic.ComboPopup;
import lombok.Getter;
import lombok.Setter;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumGebietEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    SaveVetoable,
    AfterSavingHook,
    AfterClosingHook,
    BeforeSavingHook,
    TitleComponentProvider,
    PropertyChangeListener,
    BaumParentPanel{

    //~ Static fields/initializers ---------------------------------------------
    
    public static final String GEOMTYPE = "Polygon";
    public static boolean azGeneriert = false;
    Collection<CidsBean> beansMeldung = new ArrayList<>();
    private BaumChildrenLoader.Listener loadChildrenListener;
    public static final String ADRESSE_TOSTRING_TEMPLATE = "%s";
    public static final String[] ADRESSE_TOSTRING_FIELDS = {
            AdresseLightweightSearch.Subject.HNR.toString()
        };
    
    public static final String CHILD_TOSTRING_TEMPLATE = "%s";
    public static final String[] CHILD_TOSTRING_FIELDS = {"datum"};
    public static final String CHILD_TABLE = "baum_meldung";
    public static final String CHILD_FK = "fk_meldung";
    
    private static final Logger LOG = Logger.getLogger(BaumGebietEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = {"name", "id"};
    public static final String REDUNDANT_TABLE = "baum_gebiet";

    public static final String FIELD__NAME = "name";                            // baum_gebiet
    public static final String FIELD__AZ = "aktenzeichen";                      // baum_gebiet
    public static final String FIELD__STRASSE = "fk_strasse.strassenschluessel";// baum_gebiet
    public static final String FIELD__HNR = "fk_adresse";                       // baum_gebiet
    public static final String FIELD__HAUSNUMMER = "hausnummer";                // baum_adresse
    public static final String FIELD__ID = "id";                                // baum_gebiet
    public static final String FIELD__GEOREFERENZ = "fk_geom";                  // baum_gebiet
    public static final String FIELD__DATUM = "datum";                          // baum_meldung
    public static final String FIELD__ABG = "abgenommen";                          // baum_meldung
    public static final String FIELD__GEBIET = "fk_gebiet";                     // baum_meldung
    public static final String FIELD__SCHADEN = "fk_schaden";                     // baum_ersatz/fest
    public static final String FIELD__MELDUNG = "fk_meldung";                     // baum_ot/schaden
    public static final String FIELD__STRASSE_NAME = "name";                    // strasse
    public static final String FIELD__STRASSE_KEY = "strassenschluessel";       // strasse
    public static final String FIELD__GEO_FIELD = "geo_field";                  // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // baum_gebiet_geombaum_gebiet
    public static final String TABLE_NAME = "baum_gebiet";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_MELDUNG = "baum_meldung";
    public static final String TABLE_ADRESSE = "adresse";

    public static final String BUNDLE_NOLOAD = "BaumGebietEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NONAME = "BaumGebietEditor.prepareForSave().noName";
    public static final String BUNDLE_NOAZ = "BaumGebietEditor.prepareForSave().noAz";
    public static final String BUNDLE_DUPLICATEAZ = "BaumGebietEditor.prepareForSave().duplicateAz";
    public static final String BUNDLE_NOSTREET = "BaumGebietEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOGEOM = "BaumGebietEditor.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "BaumGebietEditor.prepareForSave().wrongGeom";
    public static final String BUNDLE_NOAZCREATE = "BaumGebietEditor.btnCreateAktenzeichenActionPerformed().noCreateAz";
    public static final String BUNDLE_AZQUESTION = "BaumGebietEditor.btnCreateAktenzeichenActionPerformed().CreateAzQuest";
    public static final String BUNDLE_AZWRITE = "BaumGebietEditor.btnCreateAktenzeichenActionPerformed().CreateAzWrite";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumGebietEditor.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_PANE_TITLE_PERSIST = "BaumGebietEditor.editorClose().JOptionPane.title";
    public static final String BUNDLE_PANE_PREFIX_MELDUNG = "BaumGebietEditor.editorClose().JOptionPane.errorMeldung";
    public static final String BUNDLE_PANE_PREFIX_ORT = "BaumGebietEditor.editorClose().JOptionPane.errorOrt";
    public static final String BUNDLE_PANE_PREFIX_SCHADEN = "BaumGebietEditor.editorClose().JOptionPane.errorSchaden";
    public static final String BUNDLE_PANE_PREFIX_ERSATZ = "BaumGebietEditor.editorClose().JOptionPane.errorErsatz";
    public static final String BUNDLE_PANE_PREFIX_FEST = "BaumGebietEditor.editorClose().JOptionPane.errorFest";
    public static final String BUNDLE_PANE_KONTROLLE = "BaumGebietEditor.editorClose().JOptionPane.kontrolle";
    public static final String BUNDLE_PANE_ADMIN = "BaumGebietEditor.editorClose().JOptionPane.admin";
    public static final String BUNDLE_PANE_TITLE_MELDUNG=
            "BaumGebietEditor.btnRemoveMeldungActionPerformed().JOptionPane.title";
    public static final String BUNDLE_DEL_MELDUNG =
        "BaumGebietEditor.btnRemoveMeldungActionPerformed().JOptionPane.message";
    public static final String BUNDLE_LOAD_ERROR =
    "BaumGebietEditor.loadChildren().error";
    private static final String TITLE_NEW_GEBIET = "ein neues Gebiet (mit Meldung) anlegen...";
    private static Color colorAlarm = new java.awt.Color(255, 0, 0);
    private static Color colorNormal = new java.awt.Color(0, 0, 0);
    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
//~ Enum constants -----------------------------------------------------
    

    //~ Instance fields --------------------------------------------------------
    @Getter @Setter private static Integer counterMeldung = -1;
    
    private final AdresseLightweightSearch hnrSearch = new AdresseLightweightSearch(
            AdresseLightweightSearch.Subject.HNR,
            ADRESSE_TOSTRING_TEMPLATE,
            ADRESSE_TOSTRING_FIELDS);
    
    private Boolean redundantName = false;

    @Getter private boolean editor = true;
    private boolean areChildrenLoad = false;
    @Getter private final BaumChildrenLoader baumChildrenLoader = new BaumChildrenLoader(this);
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BaumLagePanel baumLagePanel;
    private BaumMeldungPanel baumMeldungPanel;
    private JButton btnAddNewMeldung;
    private JButton btnCreateAktenzeichen;
    private JButton btnMenAbortMeldung;
    private JButton btnMenOkMeldung;
    private JButton btnRemoveMeldung;
    private JButton btnReport;
    private JComboBox cbGeom;
    private FastBindableReferenceCombo cbHNr;
    FastBindableReferenceCombo cbStrasse;
    private DefaultBindableDateChooser dcMeldung;
    private JDialog dlgAddMeldung;
    private Box.Filler filler3;
    private JPanel jPanelAllgemein;
    private JPanel jPanelMeldungen;
    JTabbedPane jTabbedPane;
    private JLabel lblAktenzeichen;
    private JLabel lblAuswaehlenMeldung;
    private JLabel lblBemerkung;
    private JLabel lblGeom;
    private JLabel lblHNrRenderer;
    private JLabel lblHnr;
    private JLabel lblLadenMeldung;
    private JLabel lblName;
    private JLabel lblStrasse;
    private JLabel lblTitle;
    private JList lstMeldungen;
    private JPanel panAddMeldung;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panControlsNewMeldungen;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerUnten4;
    private JPanel panGebiet;
    private JPanel panGeometrie;
    private JPanel panMeldung;
    private JPanel panMeldungenMain;
    private JPanel panMenButtonsMeldung;
    private JPanel panTitle;
    private JPanel panZusatz;
    private JPanel pnlCard1;
    private JScrollPane scpBemerkung;
    private JScrollPane scpLaufendeMeldungen;
    private JTextArea taBemerkung;
    private JTextField txtAktenzeichen;
    private JTextField txtName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumGebietEditor() {
    }

    /**
     * Creates a new BaumGebietEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumGebietEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        lstMeldungen.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty(FIELD__DATUM);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoDatum = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                    compoDatum.setForeground(Color.black);
                    return compoDatum;
                }
            });

        dlgAddMeldung.pack();
        dlgAddMeldung.getRootPane().setDefaultButton(btnMenOkMeldung);
        loadChildrenListener = new LoaderListener();
        getBaumChildrenLoader().addListener(loadChildrenListener);
        
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

        dlgAddMeldung = new JDialog();
        panAddMeldung = new JPanel();
        lblAuswaehlenMeldung = new JLabel();
        panMenButtonsMeldung = new JPanel();
        btnMenAbortMeldung = new JButton();
        btnMenOkMeldung = new JButton();
        dcMeldung = new DefaultBindableDateChooser();
        panTitle = new JPanel();
        lblTitle = new JLabel();
        btnReport = new JButton();
        panContent = new RoundedPanel();
        panGebiet = new JPanel();
        pnlCard1 = new JPanel();
        jTabbedPane = new JTabbedPane();
        jPanelAllgemein = new JPanel();
        panGeometrie = new JPanel();
        baumLagePanel = new BaumLagePanel();
        panDaten = new JPanel();
        lblAktenzeichen = new JLabel();
        txtAktenzeichen = new JTextField();
        lblGeom = new JLabel();
        if (editor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblStrasse = new JLabel();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        if (isEditor()){
            cbHNr = new FastBindableReferenceCombo(
                hnrSearch,
                hnrSearch.getRepresentationPattern(),
                hnrSearch.getRepresentationFields()
            );
        }
        lblName = new JLabel();
        txtName = new JTextField();
        panZusatz = new JPanel();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panFiller = new JPanel();
        cbStrasse = new FastBindableReferenceCombo();
        btnCreateAktenzeichen = new JButton();
        if (!editor){
            lblHNrRenderer = new JLabel();
        }
        jPanelMeldungen = new JPanel();
        panMeldung = new JPanel();
        panMeldungenMain = new JPanel();
        baumMeldungPanel = baumMeldungPanel = new BaumMeldungPanel(this.getBaumChildrenLoader());
        lblLadenMeldung = new JLabel();
        scpLaufendeMeldungen = new JScrollPane();
        lstMeldungen = new JList();
        panControlsNewMeldungen = new JPanel();
        btnAddNewMeldung = new JButton();
        btnRemoveMeldung = new JButton();
        panFillerUnten4 = new JPanel();

        dlgAddMeldung.setTitle("Meldungsdatum");
        dlgAddMeldung.setModal(true);

        panAddMeldung.setLayout(new GridBagLayout());

        lblAuswaehlenMeldung.setText("Bitte das Meldungsdatum auswählen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddMeldung.add(lblAuswaehlenMeldung, gridBagConstraints);

        panMenButtonsMeldung.setLayout(new GridBagLayout());

        btnMenAbortMeldung.setText("Abbrechen");
        btnMenAbortMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenAbortMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsMeldung.add(btnMenAbortMeldung, gridBagConstraints);

        btnMenOkMeldung.setText("Ok");
        btnMenOkMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenOkMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsMeldung.add(btnMenOkMeldung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddMeldung.add(panMenButtonsMeldung, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAddMeldung.add(dcMeldung, gridBagConstraints);

        dlgAddMeldung.getContentPane().add(panAddMeldung, BorderLayout.CENTER);

        panTitle.setOpaque(false);
        panTitle.setLayout(new GridBagLayout());

        lblTitle.setFont(new Font("DejaVu Sans", 1, 18)); // NOI18N
        lblTitle.setForeground(new Color(255, 255, 255));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        btnReport.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        btnReport.setToolTipText("PDF-Bericht zum aktuell betrachteten Gebiet erstellen");
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setFocusPainted(false);
        btnReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnReportActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

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

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGeometrie.add(baumLagePanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 10, 10, 10);
        jPanelAllgemein.add(panGeometrie, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblAktenzeichen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAktenzeichen.setText("Aktenzeichen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAktenzeichen, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.aktenzeichen}"), txtAktenzeichen, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtAktenzeichen, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

        if (editor){
            if (editor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (editor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 5;
            gridBagConstraints.gridwidth = 4;
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
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        if (isEditor()){
            cbHNr.setMaximumRowCount(20);
            cbHNr.setEnabled(false);

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse}"), cbHNr, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if (isEditor()){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbHNr, gridBagConstraints);
        }

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        panZusatz.setOpaque(false);
        panZusatz.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panZusatz, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
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
        gridBagConstraints.gridy = 3;
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
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStrasse, gridBagConstraints);

        btnCreateAktenzeichen.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/textblatt.png"))); // NOI18N
        btnCreateAktenzeichen.setToolTipText("Aktenzeichen automatisch generieren");
        btnCreateAktenzeichen.setMaximumSize(new Dimension(66, 50));
        btnCreateAktenzeichen.setMinimumSize(new Dimension(20, 19));
        btnCreateAktenzeichen.setPreferredSize(new Dimension(33, 24));
        btnCreateAktenzeichen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCreateAktenzeichenActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(btnCreateAktenzeichen, gridBagConstraints);
        btnCreateAktenzeichen.setVisible(editor);

        if (!editor){
            lblHNrRenderer.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_adresse.hausnummer}"), lblHNrRenderer, BeanProperty.create("text"));
            bindingGroup.addBinding(binding);

        }
        if (!editor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipady = 10;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 5, 2, 5);
            panDaten.add(lblHNrRenderer, gridBagConstraints);
        }

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 5, 10);
        jPanelAllgemein.add(panDaten, gridBagConstraints);

        jTabbedPane.addTab("Allgemeine Informationen", jPanelAllgemein);

        jPanelMeldungen.setOpaque(false);
        jPanelMeldungen.setLayout(new GridBagLayout());

        panMeldung.setOpaque(false);
        panMeldung.setLayout(new GridBagLayout());

        panMeldungenMain.setOpaque(false);
        panMeldungenMain.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstMeldungen, ELProperty.create("${selectedElement}"), baumMeldungPanel, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMeldungenMain.add(baumMeldungPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panMeldung.add(panMeldungenMain, gridBagConstraints);

        lblLadenMeldung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLadenMeldung.setForeground(new Color(153, 153, 153));
        lblLadenMeldung.setText(NbBundle.getMessage(BaumGebietEditor.class, "BaumMeldungPanel.lblLadenOrt.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panMeldung.add(lblLadenMeldung, gridBagConstraints);

        lstMeldungen.setModel(new DefaultListModel<>());
        lstMeldungen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstMeldungen.setFixedCellWidth(75);
        scpLaufendeMeldungen.setViewportView(lstMeldungen);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMeldung.add(scpLaufendeMeldungen, gridBagConstraints);

        panControlsNewMeldungen.setOpaque(false);
        panControlsNewMeldungen.setLayout(new GridBagLayout());

        btnAddNewMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewMeldung.setEnabled(false);
        btnAddNewMeldung.setMaximumSize(new Dimension(39, 20));
        btnAddNewMeldung.setMinimumSize(new Dimension(39, 20));
        btnAddNewMeldung.setPreferredSize(new Dimension(39, 25));
        btnAddNewMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddNewMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewMeldungen.add(btnAddNewMeldung, gridBagConstraints);

        btnRemoveMeldung.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveMeldung.setEnabled(false);
        btnRemoveMeldung.setMaximumSize(new Dimension(39, 20));
        btnRemoveMeldung.setMinimumSize(new Dimension(39, 20));
        btnRemoveMeldung.setPreferredSize(new Dimension(39, 25));
        btnRemoveMeldung.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveMeldungActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewMeldungen.add(btnRemoveMeldung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        panMeldung.add(panControlsNewMeldungen, gridBagConstraints);

        panFillerUnten4.setName(""); // NOI18N
        panFillerUnten4.setOpaque(false);

        GroupLayout panFillerUnten4Layout = new GroupLayout(panFillerUnten4);
        panFillerUnten4.setLayout(panFillerUnten4Layout);
        panFillerUnten4Layout.setHorizontalGroup(panFillerUnten4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten4Layout.setVerticalGroup(panFillerUnten4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panMeldung.add(panFillerUnten4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        jPanelMeldungen.add(panMeldung, gridBagConstraints);

        jTabbedPane.addTab("Meldungen", jPanelMeldungen);

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
    private void btnMenAbortMeldungActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortMeldungActionPerformed
        dlgAddMeldung.setVisible(false);
    }//GEN-LAST:event_btnMenAbortMeldungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkMeldungActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkMeldungActionPerformed
        try {
            //meldungsBean erzeugen und vorbelegen:
            final CidsBean beanMeldung = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE_MELDUNG,
                    getConnectionContext());
            beanMeldung.setProperty(FIELD__GEBIET, this.cidsBean);
            
            final java.util.Date selDate = dcMeldung.getDate();
            java.util.Calendar cal = Calendar.getInstance();
            cal.setTime(selDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.sql.Date beanDate = new java.sql.Date(cal.getTime().getTime());
            
            beanMeldung.setProperty(FIELD__DATUM, beanDate);
            beanMeldung.setProperty(FIELD__ABG, false);
            beanMeldung.setProperty(FIELD__ID, getCounterMeldung());
            setCounterMeldung(getCounterMeldung()-1);
            //Meldungen erweitern:
            if (editor){
                getBaumChildrenLoader().addMeldung(cidsBean.getPrimaryKeyValue(), beanMeldung);
            }
            //Fuegt die Meldung zu mapMeldung hinzu
            ((DefaultListModel)lstMeldungen.getModel()).addElement(beanMeldung);
            
            //Refresh:
            lstMeldungen.setSelectedValue(beanMeldung, true);
            cidsBean.setArtificialChangeFlag(true);
            
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddMeldung.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkMeldungActionPerformed


    private String toString(final Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }
   
    
    private void btnAddNewMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewMeldungActionPerformed
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumGebietEditor.this), dlgAddMeldung, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumMeldung object", e);
        }
    }//GEN-LAST:event_btnAddNewMeldungActionPerformed

    private void btnRemoveMeldungActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveMeldungActionPerformed
        final Object selectedObject = lstMeldungen.getSelectedValue();
        
        if (selectedObject instanceof CidsBean) {
            final Integer idMeldung = ((CidsBean) selectedObject).getPrimaryKeyValue();
            if(getBaumChildrenLoader().getMapValueOrt(idMeldung)== null && 
                    getBaumChildrenLoader().getMapValueSchaden(idMeldung)== null){
                    List<CidsBean> listMeldungen = getBaumChildrenLoader().getMapValueMeldung(this.cidsBean.getPrimaryKeyValue());
                    if(((CidsBean)selectedObject).getMetaObject().getStatus() == MetaObject.NEW){
                        getBaumChildrenLoader().removeMeldung(cidsBean.getPrimaryKeyValue(), (CidsBean)selectedObject);
                    } else{
                        for(final CidsBean beanMeldung:listMeldungen){
                            if(beanMeldung.equals(selectedObject)){
                                try {
                                    beanMeldung.delete();
                                } catch (Exception ex) {
                                    LOG.warn("problem in delete meldung: not removed.", ex);
                                }
                                break;
                            }
                        }
                        getBaumChildrenLoader().getMapMeldung().replace(this.cidsBean.getPrimaryKeyValue(), listMeldungen);
                    }
                    ((DefaultListModel)lstMeldungen.getModel()).removeElement(selectedObject);
                    if (getActiveBeans(listMeldungen) > 0) {
                        lstMeldungen.setSelectedIndex(0);
                    }
                    cidsBean.setArtificialChangeFlag(true);
                }else {
                    //Meldung, Meldung hat Unterobjekte
                    JOptionPane.showMessageDialog(
                            StaticSwingTools.getParentFrame(this),
                            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_DEL_MELDUNG),
                            NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_PANE_TITLE_MELDUNG),
                            JOptionPane.WARNING_MESSAGE);
                
            }
        }
    }//GEN-LAST:event_btnRemoveMeldungActionPerformed

    private void cbStrasseActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cbStrasseActionPerformed
        if (isEditor() && cidsBean != null && this.cidsBean.getProperty(FIELD__STRASSE) != null){
            cbHNr.setSelectedItem(null);
            cbHNr.setEnabled(true);
            refreshHnr();
        }
    }//GEN-LAST:event_cbStrasseActionPerformed

    private void btnCreateAktenzeichenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAktenzeichenActionPerformed
        if (this.cidsBean != null){
            String aktenzeichen;
            String hnr = "x";
            if (this.cidsBean.getProperty(FIELD__HNR) != null) {
                hnr = this.cidsBean.getProperty(FIELD__HNR).toString();
                hnr = hnr.trim();
                hnr = hnr.replace(" ", "-");
            }
            if (this.cidsBean.getProperty(FIELD__STRASSE) != null && !this.cidsBean.getProperty(FIELD__NAME).toString().isEmpty()){
                aktenzeichen = this.cidsBean.getProperty(FIELD__STRASSE).toString()
                        + "_" + hnr
                        + "_" + this.cidsBean.getProperty(FIELD__NAME).toString();
                if (this.cidsBean.getProperty(FIELD__AZ) != null) {
                    final Object[] options = { "Ja, AZ überschreiben", "Abbrechen" };
                    final int result = JOptionPane.showOptionDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_AZQUESTION),
                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_AZWRITE),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);
                if ((result == JOptionPane.CLOSED_OPTION) || (result == 1)) {
                 return;
                }          
            }
            try {
                this.cidsBean.setProperty(FIELD__AZ, aktenzeichen);
                lblAktenzeichen.setForeground(colorNormal);
                azGeneriert = true;
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            } 
                   
            } else {
                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOAZCREATE)
                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE),
                        JOptionPane.WARNING_MESSAGE);
            }
        }
        
    }//GEN-LAST:event_btnCreateAktenzeichenActionPerformed

    private void btnReportActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnReportActionPerformed
        
    }//GEN-LAST:event_btnReportActionPerformed

    
    private Integer getActiveBeans(final List<CidsBean> cbList){
        Integer anzahl = 0;
        if (cbList != null){
            for(final CidsBean bean:cbList){
                if(bean.getMetaObject().getStatus() != MetaObject.TO_DELETE){
                    anzahl += 1;
                }
            }     
        }
        return anzahl;
    }
    
    private void prepareMeldung(){
        if (getBaumChildrenLoader().getMapMeldung() != null && 
                getActiveBeans(getBaumChildrenLoader().getMapValueMeldung(
                        this.cidsBean.getPrimaryKeyValue())) > 0) {
            lstMeldungen.setSelectedIndex(0);
        }
        
        lstMeldungen.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value,
                    final int index,
                    final boolean isSelected,
                    final boolean cellHasFocus) {
                Object newValue = value;

                if (value instanceof CidsBean) {
                    final CidsBean bean = (CidsBean)value;
                    newValue = bean.getProperty(FIELD__DATUM);

                    if (newValue == null) {
                        newValue = "unbenannt";
                    }
                }
                final Component compoDatum = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                compoDatum.setForeground(new Color(9,68,9));
                return compoDatum;
            }
        }); 
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    private void setMeldungBeans(final List<CidsBean> cidsBeans) {
        try {
            baumMeldungPanel.setCidsBean(null);
            ((DefaultListModel)lstMeldungen.getModel()).clear();
            if(cidsBeans != null){
                for(final Object bean:cidsBeans){
                    if (bean instanceof CidsBean && ((CidsBean)bean).getMetaObject().getStatus()!= MetaObject.TO_DELETE){
                        ((DefaultListModel)lstMeldungen.getModel()).addElement(bean);
                    }
                }
            }
            prepareMeldung();
        } catch (final Exception ex) {
                LOG.warn("meldung list not cleared.", ex);
        }
    }
    

    private void zeigeKinderMeldung(){
        setMeldungBeans(getBaumChildrenLoader().getMapValueMeldung(cidsBean.getPrimaryKeyValue()));
    }

    private void refreshHnr() { 
        if (cidsBean != null && cidsBean.getProperty(FIELD__STRASSE) != null){
            String schluessel = cidsBean.getProperty(FIELD__STRASSE).toString();
            if (schluessel != null){

                hnrSearch.setKeyId(Integer.parseInt(schluessel.replaceFirst("0*","")));
                
                hnrSearch.setKeyId(Integer.parseInt(schluessel));
                initComboboxHnr();
            }
        }
    }
    
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        if (editor && (this.cidsBean != null)) {
            LOG.info("remove propchange baum_gebiet: " + this.cidsBean);
            this.cidsBean.removePropertyChangeListener(this);
        }
        bindingGroup.unbind();
        this.cidsBean = cb;
        if (editor && (this.cidsBean != null)) {
            LOG.info("add propchange baum_gebiet: " + this.cidsBean);
            this.cidsBean.addPropertyChangeListener(this);
        }
        if (this.cidsBean != null){
            zeigeKinderMeldung();
        }
        // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
        // evtl. kann dies verbessert werden.
        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
        setMapWindow();
        bindingGroup.bind();
        if(this.cidsBean != null){
            loadChildren(this.cidsBean.getPrimaryKeyValue());
        }
        if(editor){
            if(this.cidsBean != null && this.cidsBean.getProperty(FIELD__STRASSE) != null){
                cbHNr.setEnabled(true);
            }
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbHNr);
            {
                final JList pop = ((ComboPopup)cbHNr.getUI().getAccessibleChild(cbHNr, 0))
                            .getList();
                final JTextField txt = (JTextField)cbHNr.getEditor().getEditorComponent();
                cbHNr.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            final Object selectedValue = pop.getSelectedValue();
                            txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
                        }
                    });
            }
            refreshHnr();
        }
        setTitle(getTitle());
        btnReport.setVisible(false);
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            getBaumChildrenLoader().setLoadingCompletedWithoutError(true);
            allowAddRemove();
        }
    }
       
    
    /**
     * DOCUMENT ME!
     */
    @SuppressWarnings("empty-statement")
    private void setReadOnly() {
        if (!(editor)) {
            RendererTools.makeReadOnly(txtAktenzeichen);
            RendererTools.makeReadOnly(cbStrasse);
            RendererTools.makeReadOnly(cbHNr);
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(taBemerkung);
            lblGeom.setVisible(editor);
            panControlsNewMeldungen.setVisible(editor);;
        }
    }
    
    private void setMapWindow() {
        baumLagePanel.setMapWindow(getCidsBean(), getConnectionContext());
    }
    
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
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_GEBIET;
        } else {
            return cidsBean.toString();
        }
    }

    private void clearBaumChildrenLoader(){
        getBaumChildrenLoader().clearAllMaps();
        getBaumChildrenLoader().setLoadingCompletedWithoutError(false);
        getBaumChildrenLoader().removeListener(loadChildrenListener);
    }
    
    private void allowAddRemove(){
        if (getBaumChildrenLoader().getLoadingCompletedWithoutError()){
            if (editor){   
                btnAddNewMeldung.setEnabled(true);
                btnRemoveMeldung.setEnabled(true);
            }
                lblLadenMeldung.setVisible(false);
        }
     } 
    
    @Override
    public void dispose() {
        baumLagePanel.dispose();
        dlgAddMeldung.dispose();
        if (editor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        } else {
            clearBaumChildrenLoader();
        }
        baumMeldungPanel.dispose();
    }
    
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        }
        lblTitle.setText(title);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ)) {
            setMapWindow();
        }
        if (evt.getPropertyName().equals(FIELD__AZ) || 
                evt.getPropertyName().equals(FIELD__STRASSE) || 
                evt.getPropertyName().equals(FIELD__HNR) || 
                evt.getPropertyName().equals(FIELD__NAME)){
            if (this.cidsBean.getMetaObject().getStatus() != MetaObject.NEW || azGeneriert){
                lblAktenzeichen.setForeground(colorAlarm);
            }
        }
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public boolean isOkForSaving() {
        if (!areChildrenLoad){
            return false;
        }
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean noErrorOccured = true;
        for (final CidsBean meldungBean : 
                getBaumChildrenLoader().getMapValueMeldung(this.cidsBean.getPrimaryKeyValue())){
            try {
                noErrorOccured = baumMeldungPanel.isOkayForSaving(meldungBean);
                if(!noErrorOccured) {
                    break;
                }
            } catch (final Exception ex) {
                noErrorOccured = false;
                LOG.error(ex, ex);
            }
        }
        
        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NONAME));
                save = false;
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        //aktenzeichen vorhanden und nicht redundant
        try {
            if (txtAktenzeichen.getText().trim().isEmpty()) {
                LOG.warn("No aktenzeichen specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOAZ));
                save = false;
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_DUPLICATEAZ));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Aktenzeichen not given.", ex);
            save = false;
        }
        // Straße muss angegeben werden
        try {
            if (cbStrasse.getSelectedItem() == null) {
                LOG.warn("No strasse specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOSTREET));
                save = false;
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("strasse not given.", ex);
            save = false;
        }
       

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_WRONGGEOM));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save && noErrorOccured;
    }

    @Override
    public void afterSaving(AfterSavingHook.Event event) {
        try{
            if(AfterSavingHook.Status.SAVE_SUCCESS == event.getStatus()){
                final List<CidsBean> listMeldung = getBaumChildrenLoader().getMapValueMeldung(this.cidsBean.getPrimaryKeyValue());
                for (CidsBean meldungBean : listMeldung) {
                    try {
                        meldungBean.setProperty(FIELD__GEBIET, event.getPersistedBean());
                        final List<CidsBean> listOrt = getBaumChildrenLoader().getMapValueOrt(meldungBean.getPrimaryKeyValue());
                        final List<CidsBean> listSchaden = getBaumChildrenLoader().getMapValueSchaden(meldungBean.getPrimaryKeyValue());
                        try{
                            meldungBean = meldungBean.persist(getConnectionContext());
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX_MELDUNG)
                                        + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_KONTROLLE)
                                        + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_ADMIN)
                                        + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                                NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                JOptionPane.ERROR_MESSAGE);
                        }
                        //Ortstermine persisten
                        if (listOrt != null && !(listOrt.isEmpty())){
                            for (final CidsBean ortBean : listOrt) {
                                try {
                                    ortBean.setProperty(FIELD__MELDUNG, meldungBean);
                                    ortBean.persist(getConnectionContext());
                                } catch (final Exception ex) {
                                    LOG.error(ex, ex);
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX_ORT)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_KONTROLLE)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_ADMIN)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        //Schaeden persisten
                        if (listSchaden != null && !(listSchaden.isEmpty())){
                            for (CidsBean schadenBean : listSchaden) {
                                try {
                                    schadenBean.setProperty(FIELD__MELDUNG, meldungBean);
                                    final List<CidsBean> listErsatz = getBaumChildrenLoader().getMapValueErsatz(schadenBean.getPrimaryKeyValue());
                                    final List<CidsBean> listFest = getBaumChildrenLoader().getMapValueFest(schadenBean.getPrimaryKeyValue());
                                    schadenBean = schadenBean.persist(getConnectionContext());
                                    //Ersatzpflanzungen persisten
                                    if (listErsatz != null && !(listErsatz.isEmpty())){
                                        for (final CidsBean ersatzBean : listErsatz) {
                                            try {
                                                ersatzBean.setProperty(FIELD__SCHADEN, schadenBean);
                                                ersatzBean.persist(getConnectionContext());
                                            } catch (final Exception ex) {
                                                LOG.error(ex, ex);
                                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX_ERSATZ)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_KONTROLLE)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_ADMIN)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                                                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                                    JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                    //Festsetzungen persisten
                                    if (listFest != null && !(listFest.isEmpty())){
                                        for (final CidsBean festBean : listFest) {
                                            try {
                                                festBean.setProperty(FIELD__SCHADEN, schadenBean);
                                                festBean.persist(getConnectionContext());
                                            } catch (final Exception ex) {
                                                LOG.error(ex, ex);
                                                JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX_FEST)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_KONTROLLE)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_ADMIN)
                                                            + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                                                    NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                                    JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    }
                                } catch (final Exception ex) {
                                    LOG.error(ex, ex);
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_PREFIX_SCHADEN)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_KONTROLLE)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_ADMIN)
                                                + NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_SUFFIX),
                                        NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_PANE_TITLE_PERSIST),
                                        JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOG.warn("problem in persist children.", ex);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("problem in afterSaving.", ex);
        }
    }

    @Override
    public void afterClosing(AfterClosingHook.Event event) {
        clearBaumChildrenLoader();
    }

    @Override
    public void beforeSaving() {
        RedundantObjectSearch gebietSearch = new RedundantObjectSearch(
            REDUNDANT_TOSTRING_TEMPLATE,
            REDUNDANT_TOSTRING_FIELDS,
            null,
            REDUNDANT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        conditions.add(FIELD__AZ + " ilike '" + txtAktenzeichen.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + cidsBean.getProperty(FIELD__ID));
        gebietSearch.setWhere(conditions);
        try {
            redundantName = !(SessionManager.getProxy().customServerSearch(
                    SessionManager.getSession().getUser(),
                    gebietSearch,
                    getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in beforeSaving.", ex);
        }
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
    
    private void loadChildren(final Integer id) {
        new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return getBaumChildrenLoader().loadChildrenMeldung(id, getConnectionContext());
                }

                @Override
                protected void done() {
                    try {
                        areChildrenLoad = get();
                            getBaumChildrenLoader().setLoadingCompletedWithoutError(areChildrenLoad);
                        if (!areChildrenLoad){
                            setTitle(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_LOAD_ERROR));
                        } else{
                            if (editor){
                                btnAddNewMeldung.setEnabled(true);
                                btnRemoveMeldung.setEnabled(true);
                            }
                        }
                    } catch (final InterruptedException | ExecutionException ex) {
                        LOG.error(ex, ex);
                    }  
                }
            }.execute();
    }
    
   
    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......"});
        }
    } 
    
    class MustSetModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MustSetModelCb object.
         */
        public MustSetModelCb() {
            super(new String[] { "Die Daten bitte zuweisen......"});
        }
    }
    
    class LoaderListener implements BaumChildrenLoader.Listener{

        @Override
        public void loadingCompleteSchaden(Integer idMeldung) {
            
        }

        @Override
        public void loadingCompleteOrt(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorOrt(Integer idMeldung) {
            
        }

        @Override
        public void loadingErrorSchaden(Integer idMeldung) {
             
        }

        @Override
        public void loadingComplete() {
            allowAddRemove();
        }

        @Override
        public void loadingCompleteFest(Integer idSchaden) {
        }

        @Override
        public void loadingCompleteErsatz(Integer idSchaden) {
        }

        @Override
        public void loadingErrorFest(Integer idMeldung) {
        }

        @Override
        public void loadingErrorErsatz(Integer idMeldung) {
        }

        @Override
        public void loadingCompleteMeldung() {
            if (cidsBean != null){
                lblLadenMeldung.setVisible(false);
                zeigeKinderMeldung();
            }
        }
        
    }
}
