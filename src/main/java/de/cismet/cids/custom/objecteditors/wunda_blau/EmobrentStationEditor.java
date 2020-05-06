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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.EmobConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.regex.Pattern;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobrentStationEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TEXT_OPEN = "24 Stunden / 7 Tage";
    public static final String GEOMTYPE = "Point";
    public static final int FOTO_WIDTH = 150;

    private static final Logger LOG = Logger.getLogger(EmobrentStationEditor.class);

    public static final String FIELD__NAME = "station";                             // emobrent_station
    public static final String FIELD__ID = "id";                                    // emobrent_station
    public static final String FIELD__VERSATZ = "fk_versatz";                       // emobrent_station
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // emobrent_station
    public static final String FIELD__SCHLUESSEL = "schluessel";                    // emob_versatz
    public static final String VERSATZ_ZENTRAL_SCHLUESSEL = "0";                    // emob_versatz.schluessel
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // emobrent_station_geom
    public static final String FIELD__FOTO = "foto";                                // emobrent_station
    public static final String FIELD__LEIHGEBUEHR = "fk_leihgebuehr";               // emobrent_station
    public static final String FIELD__LEIHGEBUEHR_NAME = "fk_leihgebuehr.name";     // emobrent_leihgebuehr
    public static final String LABEL__NAME_HP = "lblUrlHpCheck";
    public static final String TABLE_NAME = "emobrent_station";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME_VERSATZ = "emob_versatz";

    public static final String BUNDLE_NOLOAD = "EmobrentStationEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NONAME = "EmobrentStationEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "EmobrentStationEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "EmobrentStationEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOGEOM = "EmobrentStationEditor.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "EmobrentStationEditor.prepareForSave().wrongGeom";
    public static final String BUNDLE_PANE_PREFIX = "EmobrentStationEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "EmobrentStationEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EmobrentStationEditor.prepareForSave().JOptionPane.title";

    public static final Pattern TEL_FILLING_PATTERN = Pattern.compile("(|\\+(-|[0-9])*)");
    public static final Pattern TEL_MATCHING_PATTERN = Pattern.compile("\\+[0-9]{1,3}(-[0-9]+){1,}");
    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum otherTableCases {

        //~ Enum constants -----------------------------------------------------

        SETVALUE, REDUNDANTATTNAME
    }

    //~ Instance fields --------------------------------------------------------
    
    private SwingWorker worker_loadFoto;
    private SwingWorker worker_name;
    private SwingWorker worker_hp;
    private SwingWorker worker_foto;
    private SwingWorker worker_versatz;
    

    private Boolean redundantName = false;

    private boolean isEditor = true;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    
    private final RegexPatternFormatter telPatternFormatter = new RegexPatternFormatter(
            TEL_FILLING_PATTERN,
            TEL_MATCHING_PATTERN);
    
    private final RegexPatternFormatter faxPatternFormatter = new RegexPatternFormatter(
            TEL_FILLING_PATTERN,
            TEL_MATCHING_PATTERN);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JComboBox cbGeom;
    private JComboBox<String> cbLeihgebuehr;
    private DefaultBindableReferenceCombo cbVersatz;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private Box.Filler filler5;
    private JFormattedTextField ftxtTelefax;
    private JFormattedTextField ftxtTelefon;
    private JLabel lblBemerkung;
    private JLabel lblEBike;
    private JLabel lblFoto;
    private JLabel lblFotoAnzeigen;
    private JLabel lblGeom;
    private JLabel lblHnr;
    private JLabel lblHomepage;
    private JLabel lblKarte;
    private JLabel lblLeihgebuehr;
    private JLabel lblLrad;
    private JLabel lblMail;
    private JLabel lblName;
    private JLabel lblOffen;
    private JLabel lblPedelec;
    private JLabel lblSPedelec;
    private JLabel lblStrasse;
    private JLabel lblTelefax;
    private JLabel lblTelefon;
    private JLabel lblUrlCheck;
    private JLabel lblUrlHpCheck;
    private JLabel lblVersatz;
    private JLabel lblWeitere;
    private JLabel lblZusatz;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panFillerUntenFoto;
    private JPanel panGeometrie;
    private JPanel panLage;
    private JPanel panLeihgebuehr;
    private JPanel panOffen;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUrl;
    private JPanel panUrlHp;
    private JPanel panWeitere;
    private JPanel panZusatz;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpLeihgebuehr;
    private JScrollPane scpOffen;
    private JScrollPane scpWeitere;
    private JScrollPane scpZusatz;
    private SemiRoundedPanel semiRoundedPanel7;
    JSpinner spEBike;
    JSpinner spLrad;
    JSpinner spPedelec;
    JSpinner spSPedelec;
    private JTextArea taBemerkung;
    private JTextArea taLeihgebuehr;
    private JTextArea taOffen;
    private JTextArea taWeitere;
    private JTextArea taZusatz;
    private JTextField txtFoto;
    private JTextField txtHnr;
    private JTextField txtHomepage;
    private JTextField txtMail;
    private JTextField txtName;
    private JTextField txtStrasse;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EmobrentStationEditor() {
    }

    /**
     * Creates a new EmobrentStationEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EmobrentStationEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    
        
    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        
        txtName.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn der Name der Station geändert wird, wird dieser überprüft.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    checkName();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkName();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkName();
                }
            });

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
        
        txtHomepage.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn die Homepage geändert wird, wird diese überprüft.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    doWithHpUrl();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    doWithHpUrl();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    doWithHpUrl();
                }
            });

        
        if (isEditor) {
            ((DefaultBindableScrollableComboBox)this.cbLeihgebuehr).setNullable(true);
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
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

        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panFillerUnten1 = new JPanel();
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblStrasse = new JLabel();
        txtStrasse = new JTextField();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        txtHnr = new JTextField();
        lblTelefon = new JLabel();
        ftxtTelefon = new JFormattedTextField(telPatternFormatter);
        lblTelefax = new JLabel();
        ftxtTelefax = new JFormattedTextField(faxPatternFormatter);
        lblMail = new JLabel();
        txtMail = new JTextField();
        lblHomepage = new JLabel();
        txtHomepage = new JTextField();
        panUrlHp = new JPanel();
        lblUrlHpCheck = new JLabel();
        lblFoto = new JLabel();
        txtFoto = new JTextField();
        panUrl = new JPanel();
        lblUrlCheck = new JLabel();
        filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblFotoAnzeigen = new JLabel();
        lblPedelec = new JLabel();
        spPedelec = new JSpinner();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblSPedelec = new JLabel();
        spSPedelec = new JSpinner();
        lblEBike = new JLabel();
        spEBike = new JSpinner();
        lblLrad = new JLabel();
        spLrad = new JSpinner();
        lblLeihgebuehr = new JLabel();
        panLeihgebuehr = new JPanel();
        scpLeihgebuehr = new JScrollPane();
        taLeihgebuehr = new JTextArea();
        if(isEditor){
            cbLeihgebuehr = new DefaultBindableScrollableComboBox();
        }
        lblZusatz = new JLabel();
        panZusatz = new JPanel();
        scpZusatz = new JScrollPane();
        taZusatz = new JTextArea();
        lblWeitere = new JLabel();
        panWeitere = new JPanel();
        scpWeitere = new JScrollPane();
        taWeitere = new JTextArea();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblOffen = new JLabel();
        panOffen = new JPanel();
        scpOffen = new JScrollPane();
        taOffen = new JTextArea();
        panFillerUntenFoto = new JPanel();
        panFiller = new JPanel();
        panGeometrie = new JPanel();
        lblGeom = new JLabel();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        lblVersatz = new JLabel();
        cbVersatz = new DefaultBindableReferenceCombo(true) ;

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

        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panContent.add(panFillerUnten1, gridBagConstraints);

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.station}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.strasse}"), txtStrasse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtStrasse, gridBagConstraints);
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hausnummer}"), txtHnr, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTelefon, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.telefon}"), ftxtTelefon, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        ftxtTelefon.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                ftxtTelefonFocusLost(evt);
            }
        });
        ftxtTelefon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ftxtTelefonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(ftxtTelefon, gridBagConstraints);

        lblTelefax.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefax.setText("Fax:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTelefax, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.telefax}"), ftxtTelefax, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        ftxtTelefax.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                ftxtTelefaxFocusLost(evt);
            }
        });
        ftxtTelefax.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ftxtTelefaxActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(ftxtTelefax, gridBagConstraints);

        lblMail.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMail.setText("Email:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMail, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.email}"), txtMail, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtMail, gridBagConstraints);

        lblHomepage.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHomepage.setText("Homepage:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHomepage, gridBagConstraints);

        txtHomepage.setName(""); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.homepage}"), txtHomepage, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHomepage, gridBagConstraints);

        panUrlHp.setOpaque(false);
        panUrlHp.setLayout(new GridBagLayout());

        lblUrlHpCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        lblUrlHpCheck.setName("lblUrlHpCheck"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrlHp.add(lblUrlHpCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panUrlHp, gridBagConstraints);

        lblFoto.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFoto.setText("Foto:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblFoto, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.foto}"), txtFoto, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtFoto, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        lblUrlCheck.setName("lblUrlCheck"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panUrl, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 10, 2, 10);
        panDaten.add(filler5, gridBagConstraints);

        lblFotoAnzeigen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 4);
        panDaten.add(lblFotoAnzeigen, gridBagConstraints);

        lblPedelec.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPedelec.setText("Pedelecs:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPedelec, gridBagConstraints);

        spPedelec.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spPedelec.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spPedelec.setName("spLrad"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl_pedelec}"), spPedelec, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spPedelec, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblSPedelec.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSPedelec.setText("SPedelecs:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSPedelec, gridBagConstraints);

        spSPedelec.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spSPedelec.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spSPedelec.setName("spLrad"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl_spedelec}"), spSPedelec, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spSPedelec, gridBagConstraints);

        lblEBike.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblEBike.setText("EBikes:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblEBike, gridBagConstraints);

        spEBike.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spEBike.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spEBike.setName("spLrad"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl_ebike}"), spEBike, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spEBike, gridBagConstraints);

        lblLrad.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLrad.setText("Lastenräder:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblLrad, gridBagConstraints);

        spLrad.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spLrad.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spLrad.setName("spLrad"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.anzahl_lastenrad}"), spLrad, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(spLrad, gridBagConstraints);

        lblLeihgebuehr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLeihgebuehr.setText("Leihgebühr:");
        lblLeihgebuehr.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblLeihgebuehr, gridBagConstraints);

        panLeihgebuehr.setOpaque(false);
        panLeihgebuehr.setLayout(new GridBagLayout());

        taLeihgebuehr.setEditable(false);
        taLeihgebuehr.setLineWrap(true);
        taLeihgebuehr.setRows(2);
        taLeihgebuehr.setToolTipText("");
        taLeihgebuehr.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_leihgebuehr.name}"), taLeihgebuehr, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpLeihgebuehr.setViewportView(taLeihgebuehr);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panLeihgebuehr.add(scpLeihgebuehr, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panLeihgebuehr, gridBagConstraints);

        if(isEditor){
            cbLeihgebuehr.setFont(new Font("Dialog", 0, 12)); // NOI18N
            if(isEditor){
                cbLeihgebuehr.setPreferredSize(new Dimension(100, 24));
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_leihgebuehr}"), cbLeihgebuehr, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if(isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 18;
            gridBagConstraints.gridwidth = 5;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbLeihgebuehr, gridBagConstraints);
        }

        lblZusatz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblZusatz.setText("Zusatzinfo:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblZusatz, gridBagConstraints);

        panZusatz.setOpaque(false);
        panZusatz.setLayout(new GridBagLayout());

        taZusatz.setColumns(20);
        taZusatz.setLineWrap(true);
        taZusatz.setRows(2);
        taZusatz.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.zusatzinfo}"), taZusatz, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpZusatz.setViewportView(taZusatz);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusatz.add(scpZusatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panZusatz, gridBagConstraints);

        lblWeitere.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblWeitere.setText("Weitere Infos:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblWeitere, gridBagConstraints);

        panWeitere.setOpaque(false);
        panWeitere.setLayout(new GridBagLayout());

        taWeitere.setLineWrap(true);
        taWeitere.setRows(2);
        taWeitere.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.weitereinfo}"), taWeitere, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpWeitere.setViewportView(taWeitere);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panWeitere.add(scpWeitere, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panWeitere, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 25;
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
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblOffen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOffen.setText("Öffnungszeiten:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 28;
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
        gridBagConstraints.gridy = 28;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panOffen, gridBagConstraints);

        panFillerUntenFoto.setOpaque(false);

        GroupLayout panFillerUntenFotoLayout = new GroupLayout(panFillerUntenFoto);
        panFillerUntenFoto.setLayout(panFillerUntenFotoLayout);
        panFillerUntenFotoLayout.setHorizontalGroup(panFillerUntenFotoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenFotoLayout.setVerticalGroup(panFillerUntenFotoLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panFillerUntenFoto, gridBagConstraints);

        panFiller.setOpaque(false);

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        panDaten.add(panFiller, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panContent.add(panDaten, gridBagConstraints);

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 10, 2, 5);
        panGeometrie.add(lblGeom, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_geom}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 10);
            panGeometrie.add(cbGeom, gridBagConstraints);
        }

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 10, 0);
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
        panGeometrie.add(panLage, gridBagConstraints);

        lblVersatz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVersatz.setText("Versatz:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 10, 2, 5);
        panGeometrie.add(lblVersatz, gridBagConstraints);

        cbVersatz.setNullable(false);
        cbVersatz.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbVersatz.setMaximumSize(new Dimension(200, 23));
        cbVersatz.setMinimumSize(new Dimension(150, 23));
        cbVersatz.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_versatz}"), cbVersatz, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 10);
        panGeometrie.add(cbVersatz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 0, 5);
        panContent.add(panGeometrie, gridBagConstraints);

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

    private void ftxtTelefonFocusLost(FocusEvent evt) {//GEN-FIRST:event_ftxtTelefonFocusLost
        refreshValidTel();
    }//GEN-LAST:event_ftxtTelefonFocusLost

    private void ftxtTelefonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ftxtTelefonActionPerformed
        refreshValidTel();
    }//GEN-LAST:event_ftxtTelefonActionPerformed

    private void ftxtTelefaxFocusLost(FocusEvent evt) {//GEN-FIRST:event_ftxtTelefaxFocusLost
        refreshValidFax();
    }//GEN-LAST:event_ftxtTelefaxFocusLost

    private void ftxtTelefaxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ftxtTelefaxActionPerformed
        refreshValidFax();
    }//GEN-LAST:event_ftxtTelefaxActionPerformed

    

    /**
     * DOCUMENT ME!
     */
    private void doWithFotoUrl() {
        final String foto = EmobConfProperties.getInstance().getFotoUrlVerleih().concat(txtFoto.getText());
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
    }
    
    /**
     * DOCUMENT ME!
     */
    private void doWithHpUrl() {
        final String url = txtHomepage.getText();
        // Worker Aufruf, grün/rot
        checkUrl(url, lblUrlHpCheck);
    }
       /**
     * DOCUMENT ME!
     */
    private void refreshValidTel() {
        ftxtTelefon.setValue(telPatternFormatter.getLastValid());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  okValue  DOCUMENT ME!
     */
    private void saveValidTel(final Object okValue) {
        telPatternFormatter.setLastValid(okValue);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  okValue  DOCUMENT ME!
     */
    private void saveValidFax(final Object okValue) {
        faxPatternFormatter.setLastValid(okValue);
    }
    
       /**
     * DOCUMENT ME!
     */
    private void refreshValidFax() {
        ftxtTelefax.setValue(faxPatternFormatter.getLastValid());
    }

    /**
     * DOCUMENT ME!
     */
    private void checkName() {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(
            TABLE_NAME,
            " where "
                    + FIELD__NAME
                    + " ilike '"
                    + txtName.getText().trim()
                    + "' and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            FIELD__NAME,
            otherTableCases.REDUNDANTATTNAME);
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_DUPLICATENAME));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        
        // Straße muss angegeben werden
        try {
            if (txtStrasse.getText().trim().isEmpty()) {
                LOG.warn("No street specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_NOSTREET));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Street not given.", ex);
            save = false;
        }
        
        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_WRONGGEOM));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

              

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    
    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
        try {
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("remove propchange emobrent_station: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange emobrent_station: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            setDefaultVersatz();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }
    
    public void setDefaultVersatz(){
        
        if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            // Aufruf worker um default values zu setzen
            valueFromOtherTable(
                TABLE_NAME_VERSATZ,
                " where "
                        + FIELD__SCHLUESSEL
                        + " ilike '"
                        + VERSATZ_ZENTRAL_SCHLUESSEL
                        + "'",
                FIELD__VERSATZ,
                otherTableCases.SETVALUE);
        }

    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtHnr);
            RendererTools.makeReadOnly(ftxtTelefon);
            RendererTools.makeReadOnly(ftxtTelefax);
            RendererTools.makeReadOnly(txtMail);
            RendererTools.makeReadOnly(txtHomepage);
            RendererTools.makeReadOnly(txtFoto);
            RendererTools.makeDoubleSpinnerWithoutButtons(spPedelec, 0);
            RendererTools.makeReadOnly(spPedelec);
            RendererTools.makeDoubleSpinnerWithoutButtons(spSPedelec, 0);
            RendererTools.makeReadOnly(spSPedelec);
            RendererTools.makeDoubleSpinnerWithoutButtons(spEBike, 0);
            RendererTools.makeReadOnly(spEBike);
            RendererTools.makeDoubleSpinnerWithoutButtons(spLrad, 0);
            RendererTools.makeReadOnly(spLrad);
            RendererTools.makeReadOnly(cbLeihgebuehr);
            RendererTools.makeReadOnly(taZusatz);
            RendererTools.makeReadOnly(taWeitere);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taOffen);
            lblGeom.setVisible(isEditor);
            RendererTools.makeReadOnly(cbVersatz);
        }
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
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = EmobConfProperties.getInstance().getBufferMeter();
            if (cb.getProperty(FIELD__GEOREFERENZ) != null) {
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, bufferMeter);
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
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD, bufferMeter);
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    @Override
    public String getTitle() {
        return cidsBean.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // To change body of generated methods, choose Tools | Templates.
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ)) {
            setMapWindow();
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
                        if (!isCancelled()) {
                            check = get();
                            if (check) {
                                showLabel.setIcon(statusOk);
                                showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            } else {
                                showLabel.setIcon(statusFalsch);
                                showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("URL Check Problem in Worker.", e);
                    }
                }
            };
        if (showLabel.getName().equals(LABEL__NAME_HP)){
            if (worker_hp != null) {
                worker_hp.cancel(true);
            }
            worker_hp = worker;
            worker_hp.execute();
        } else{
           if (worker_foto != null) {
                worker_foto.cancel(true);
            }
            worker_foto = worker;
            worker_foto.execute(); 
        }
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
                        if (!isCancelled()) {
                            check = get();
                            if (check != null) {
                                showLabel.setIcon(check);
                                showLabel.setText("");
                                showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                            } else {
                                showLabel.setIcon(null);
                                showLabel.setText(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_NOLOAD));
                                showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setText(NbBundle.getMessage(EmobrentStationEditor.class, BUNDLE_NOLOAD));
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("load picture Problem in Worker.", e);
                    }
                }
            };
            if (worker_loadFoto != null) {
                worker_loadFoto.cancel(true);
            }
            worker_loadFoto = worker;
            worker_loadFoto.execute();
    }
    /**
     * DOCUMENT ME!
     *
     * @param  tableName     DOCUMENT ME!
     * @param  whereClause   DOCUMENT ME!
     * @param  propertyName  DOCUMENT ME!
     * @param  fall          DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName,
            final String whereClause,
            final String propertyName,
            final otherTableCases fall) {
        final SwingWorker<CidsBean, Void> worker = new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    return getOtherTableValue(tableName, whereClause, getConnectionContext());
                }

                @Override
                protected void done() {
                    final CidsBean check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            if (check != null) {
                                switch (fall) {
                                    case SETVALUE: {         // set default value
                                        try {
                                            cidsBean.setProperty(
                                                propertyName,
                                                check);
                                        } catch (Exception ex) {
                                            LOG.warn("setVersatz: Versatz not set.", ex);
                                        }
                                        break;
                                    }
                                    case REDUNDANTATTNAME: { // check redundant name
                                        redundantName = true;
                                        break;
                                    }
                                }
                            } else {
                                switch (fall) {
                                    case REDUNDANTATTNAME: { // check redundant name
                                        redundantName = false;
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                  }
            };
        if (fall.equals(otherTableCases.REDUNDANTATTNAME)){
            if (worker_name != null) {
                worker_name.cancel(true);
            }
            worker_name = worker;
            worker_name.execute();
        } else{
            if (worker_versatz != null) {
                worker_versatz.cancel(true);
            }
            worker_versatz = worker;
            worker_versatz.execute(); 
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

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RegexPatternFormatter object.
         *
         * @param  fillingRegex   DOCUMENT ME!
         * @param  matchingRegex  DOCUMENT ME!
         */
        public RegexPatternFormatter(final Pattern fillingRegex, final Pattern matchingRegex) {
            setOverwriteMode(false);
            fillingMatcher = fillingRegex.matcher("");
            matchingMatcher = matchingRegex.matcher("");
        }

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

        /**
         * DOCUMENT ME!
         *
         * @param  okValue  DOCUMENT ME!
         */
        public void setLastValid(final Object okValue) {
            if (lastValid == null) {
                lastValid = okValue;
            }
        }
    }
}
