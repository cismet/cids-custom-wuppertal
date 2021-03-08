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

import Sirius.server.middleware.types.LightweightMetaObject;
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
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.KlimaConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

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

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class KlimaStandortEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE_NEW_STANDORT = "einen neuen Standort (mit Angebot) anlegen...";
    private static final Logger LOG = Logger.getLogger(KlimaStandortEditor.class);

    public static final String FIELD__NAME = "name";                                // klima_standort
    public static final String FIELD__ID = "id";                                    // klima_standort
    public static final String FIELD__STANDORT = "fk_standort";                     // klima_standort
    public static final String FIELD__ANGEBOTE = "n_angebote";                      // klima_standort
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // klima_standort
    public static final String FIELD__THEMA = "fk_thema";                           // klima_angebot
    public static final String FIELD__ANGEBOT_DESC = "beschreibung";                // klima_angebot
    public static final String FIELD__ANGEBOT_ONLINE = "online";                    // klima_angebot
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // klima_standort_geom
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME = "klima_standort";
    public static final String TABLE_NAME_ANGEBOT = "klima_angebot";

    public static final String BUNDLE_NONAME = "KlimaStandortEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "KlimaStandortEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "KlimaStandortEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOGEOM = "KlimaStandortEditor.prepareForSave().noGeom";
    public static final String BUNDLE_NODESCRIPTION = "KlimaStandortEditor.prepareForSave().noAngebotsbeschreibung";
    public static final String BUNDLE_PANE_PREFIX = "KlimaStandortEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "KlimaStandortEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "KlimaStandortEditor.prepareForSave().JOptionPane.title";

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> angebotBeans;
    private SwingWorker worker_name;

    private Boolean redundantName = false;

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddNewAngebot;
    private JButton btnMenAbortAngebot;
    private JButton btnMenOkAngebot;
    private JButton btnRemoveAngebot;
    JComboBox cbAngebot;
    private JComboBox cbGeom;
    private JDialog dlgAddAngebot;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private JPanel jPanel1;
    private JPanel jPanel8;
    private KlimaAngebotPanel klimaAngebotPanel;
    private JLabel lblAngebot;
    private JLabel lblAngebote;
    private JLabel lblAuswaehlenAngebot;
    private JLabel lblBemerkung;
    private JLabel lblBeschreibungS;
    private JLabel lblErreichbarkeit;
    private JLabel lblGeom;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblName;
    private JLabel lblOrt;
    private JLabel lblPlz;
    private JLabel lblStrasse;
    private JList lstAngebote;
    private JPanel panAddAngebot;
    private JPanel panAngebot;
    private JPanel panAngeboteMain;
    private JPanel panBemerkung;
    private JPanel panBeschreibungS;
    private JPanel panContent;
    private JPanel panControlsNewAngebote;
    private JPanel panDaten;
    private JPanel panErreichbarkeit;
    private JPanel panFiller;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panGeometrie;
    private JPanel panLage;
    private JPanel panMenButtonsAngebot;
    private DefaultPreviewMapPanel panPreviewMap;
    private RoundedPanel rpAngebotinfo;
    private RoundedPanel rpAngebotliste;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpBeschreibungS;
    private JScrollPane scpErreichbarkeit;
    private JScrollPane scpLaufendeAngebote;
    private SemiRoundedPanel semiRoundedPanel4;
    private SemiRoundedPanel semiRoundedPanel5;
    private SemiRoundedPanel semiRoundedPanel7;
    private JTextArea taBemerkung;
    private JTextArea taBeschreibungS;
    private JTextArea taErreichbarkeit;
    private JTextField txtHnr;
    private JTextField txtName;
    private JTextField txtOrt;
    private JTextField txtPlz;
    private JTextField txtStrasse;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public KlimaStandortEditor() {
    }

    /**
     * Creates a new KlimaStandortEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public KlimaStandortEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        txtName.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn der Name geändert wird, wird dieser überprüft.
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
        dlgAddAngebot.pack();
        dlgAddAngebot.getRootPane().setDefaultButton(btnMenOkAngebot);

        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        }
        setReadOnly();
        lstAngebote.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty(FIELD__THEMA);

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }
                    final Component compoDatum = super.getListCellRendererComponent(
                            list,
                            newValue,
                            index,
                            isSelected,
                            cellHasFocus);
                    compoDatum.setForeground(Color.BLACK);
                    return compoDatum;
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

        dlgAddAngebot = new JDialog();
        panAddAngebot = new JPanel();
        lblAuswaehlenAngebot = new JLabel();
        panMenButtonsAngebot = new JPanel();
        btnMenAbortAngebot = new JButton();
        btnMenOkAngebot = new JButton();
        final MetaObject[] thema = ObjectRendererUtils.getLightweightMetaObjectsForTable(
                "klima_thema",
                new String[] { "name" },
                getConnectionContext());
        if (thema != null) {
            Arrays.sort(thema);
            cbAngebot = new JComboBox(thema);
        }
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        jPanel1 = new JPanel();
        panFillerUnten1 = new JPanel();
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblStrasse = new JLabel();
        txtStrasse = new JTextField();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        txtHnr = new JTextField();
        lblOrt = new JLabel();
        txtOrt = new JTextField();
        lblPlz = new JLabel();
        txtPlz = new JTextField();
        lblBeschreibungS = new JLabel();
        panBeschreibungS = new JPanel();
        scpBeschreibungS = new JScrollPane();
        taBeschreibungS = new JTextArea();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblErreichbarkeit = new JLabel();
        panErreichbarkeit = new JPanel();
        scpErreichbarkeit = new JScrollPane();
        taErreichbarkeit = new JTextArea();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        panFiller = new JPanel();
        panGeometrie = new JPanel();
        lblGeom = new JLabel();
        if (isEditor) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panAngebot = new JPanel();
        rpAngebotliste = new RoundedPanel();
        scpLaufendeAngebote = new JScrollPane();
        lstAngebote = new JList();
        semiRoundedPanel4 = new SemiRoundedPanel();
        lblAngebote = new JLabel();
        jPanel8 = new JPanel();
        panControlsNewAngebote = new JPanel();
        btnAddNewAngebot = new JButton();
        btnRemoveAngebot = new JButton();
        rpAngebotinfo = new RoundedPanel();
        semiRoundedPanel5 = new SemiRoundedPanel();
        lblAngebot = new JLabel();
        panAngeboteMain = new JPanel();
        klimaAngebotPanel = new KlimaAngebotPanel(null, isEditor);

        dlgAddAngebot.setTitle("Thema des Angebots");
        dlgAddAngebot.setModal(true);

        panAddAngebot.setLayout(new GridBagLayout());

        lblAuswaehlenAngebot.setText("Bitte Thema des Angebots auswählen:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddAngebot.add(lblAuswaehlenAngebot, gridBagConstraints);

        panMenButtonsAngebot.setLayout(new GridBagLayout());

        btnMenAbortAngebot.setText("Abbrechen");
        btnMenAbortAngebot.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenAbortAngebotActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsAngebot.add(btnMenAbortAngebot, gridBagConstraints);

        btnMenOkAngebot.setText("Ok");
        btnMenOkAngebot.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenOkAngebotActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsAngebot.add(btnMenOkAngebot, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddAngebot.add(panMenButtonsAngebot, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddAngebot.add(cbAngebot, gridBagConstraints);

        dlgAddAngebot.getContentPane().add(panAddAngebot, BorderLayout.CENTER);

        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGap(0, 0, Short.MAX_VALUE));

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
        panContent.setLayout(new GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(panFillerUnten1, gridBagConstraints);

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

        txtName.setToolTipText("");

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                txtName,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.strasse}"),
                txtStrasse,
                BeanProperty.create("text"));
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
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("HNr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        txtHnr.setName(""); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.hausnummer}"),
                txtHnr,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblOrt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOrt.setText("Ort:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOrt, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.stadt}"),
                txtOrt,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtOrt, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        txtPlz.setName(""); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.plz}"),
                txtPlz,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtPlz, gridBagConstraints);

        lblBeschreibungS.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibungS.setText("Standortbeschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschreibungS, gridBagConstraints);

        panBeschreibungS.setOpaque(false);
        panBeschreibungS.setLayout(new GridBagLayout());

        taBeschreibungS.setColumns(20);
        taBeschreibungS.setLineWrap(true);
        taBeschreibungS.setRows(2);
        taBeschreibungS.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beschreibung}"),
                taBeschreibungS,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBeschreibungS.setViewportView(taBeschreibungS);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungS.add(scpBeschreibungS, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBeschreibungS, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblErreichbarkeit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblErreichbarkeit.setText("Erreichbarkeit:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblErreichbarkeit, gridBagConstraints);

        panErreichbarkeit.setOpaque(false);
        panErreichbarkeit.setLayout(new GridBagLayout());

        taErreichbarkeit.setLineWrap(true);
        taErreichbarkeit.setRows(2);
        taErreichbarkeit.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.erreichbarkeit}"),
                taErreichbarkeit,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpErreichbarkeit.setViewportView(taErreichbarkeit);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panErreichbarkeit.add(scpErreichbarkeit, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panErreichbarkeit, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);

        final GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                20,
                Short.MAX_VALUE));
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

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
        jPanel1.add(panDaten, gridBagConstraints);

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

        if (isEditor) {
            if (isEditor) {
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
        if (isEditor) {
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
        panGeometrie.add(panLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        jPanel1.add(panGeometrie, gridBagConstraints);

        panAngebot.setOpaque(false);
        panAngebot.setLayout(new GridBagLayout());

        rpAngebotliste.setLayout(new GridBagLayout());

        lstAngebote.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final ELProperty eLProperty = ELProperty.create("${cidsBean." + FIELD__ANGEBOTE + "}");
        final JListBinding jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstAngebote);
        bindingGroup.addBinding(jListBinding);

        scpLaufendeAngebote.setViewportView(lstAngebote);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpAngebotliste.add(scpLaufendeAngebote, gridBagConstraints);

        semiRoundedPanel4.setBackground(Color.darkGray);
        semiRoundedPanel4.setMinimumSize(new Dimension(60, 25));
        semiRoundedPanel4.setLayout(new GridBagLayout());

        lblAngebote.setForeground(new Color(255, 255, 255));
        lblAngebote.setText("Angebot");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(lblAngebote, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new Dimension(1, 1));

        final GroupLayout jPanel8Layout = new GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel4.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpAngebotliste.add(semiRoundedPanel4, gridBagConstraints);

        panControlsNewAngebote.setOpaque(false);
        panControlsNewAngebote.setLayout(new GridBagLayout());

        btnAddNewAngebot.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewAngebot.setMaximumSize(new Dimension(39, 20));
        btnAddNewAngebot.setMinimumSize(new Dimension(39, 20));
        btnAddNewAngebot.setPreferredSize(new Dimension(39, 25));
        btnAddNewAngebot.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddNewAngebotActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewAngebote.add(btnAddNewAngebot, gridBagConstraints);

        btnRemoveAngebot.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveAngebot.setMaximumSize(new Dimension(39, 20));
        btnRemoveAngebot.setMinimumSize(new Dimension(39, 20));
        btnRemoveAngebot.setPreferredSize(new Dimension(39, 25));
        btnRemoveAngebot.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemoveAngebotActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewAngebote.add(btnRemoveAngebot, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpAngebotliste.add(panControlsNewAngebote, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panAngebot.add(rpAngebotliste, gridBagConstraints);

        rpAngebotinfo.setLayout(new GridBagLayout());

        semiRoundedPanel5.setBackground(Color.darkGray);
        semiRoundedPanel5.setLayout(new GridBagLayout());

        lblAngebot.setForeground(new Color(255, 255, 255));
        lblAngebot.setText("Ansicht Angebot");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblAngebot, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpAngebotinfo.add(semiRoundedPanel5, gridBagConstraints);

        panAngeboteMain.setOpaque(false);
        panAngeboteMain.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                lstAngebote,
                ELProperty.create("${selectedElement}"),
                klimaAngebotPanel,
                BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAngeboteMain.add(klimaAngebotPanel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpAngebotinfo.add(panAngeboteMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panAngebot.add(rpAngebotinfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 15);
        jPanel1.add(panAngebot, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panContent.add(jPanel1, gridBagConstraints);

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
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddNewAngebotActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddNewAngebotActionPerformed
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(KlimaStandortEditor.this), dlgAddAngebot, true);
        } catch (Exception e) {
            LOG.error("Cannot add new Angebot object", e);
        }
    }                                                                     //GEN-LAST:event_btnAddNewAngebotActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveAngebotActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveAngebotActionPerformed
        final Object selectedObject = lstAngebote.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            if (angebotBeans != null) {
                angebotBeans.remove((CidsBean)selectedObject);
                if (angebotBeans != null) {
                    lstAngebote.setSelectedIndex(0);
                } else {
                    lstAngebote.clearSelection();
                }
            }
        }
    } //GEN-LAST:event_btnRemoveAngebotActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortAngebotActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbortAngebotActionPerformed
        dlgAddAngebot.setVisible(false);
    }                                                                                      //GEN-LAST:event_btnMenAbortAngebotActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkAngebotActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkAngebotActionPerformed
        try {
            // angebotBean erzeugen und vorbelegen:
            final CidsBean beanAngebot = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    TABLE_NAME_ANGEBOT,
                    getConnectionContext());
            final int standortId = cidsBean.getPrimaryKeyValue();
            beanAngebot.setProperty(FIELD__STANDORT, standortId);
            beanAngebot.setProperty(FIELD__ANGEBOT_ONLINE, false);

            final Object selection = cbAngebot.getSelectedItem();
            if (selection instanceof LightweightMetaObject) {
                final CidsBean selectedBean = ((LightweightMetaObject)selection).getBean();
                beanAngebot.setProperty(FIELD__THEMA, selectedBean);
            }

            // Meldungen erweitern:
            angebotBeans.add(beanAngebot);

            // Refresh:

            bindingGroup.unbind();
            bindingGroup.bind();
            lstAngebote.setSelectedValue(beanAngebot, true);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddAngebot.setVisible(false);
        }
    } //GEN-LAST:event_btnMenOkAngebotActionPerformed

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
                    + cidsBean.getProperty(FIELD__ID));
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_DUPLICATENAME));
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
                errorMessage.append(NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_NOSTREET));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Street not given.", ex);
            save = false;
        }

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_NOGEOM));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        // Angebotsbeschreibung muss bei einem online gestelltem Angebot gefllt sein
        try {
            for (final CidsBean angebot : cidsBean.getBeanCollectionProperty(FIELD__ANGEBOTE)) {
                if ((angebot == null)
                            || ((angebot.getProperty(FIELD__ANGEBOT_DESC) == null)
                                && "true".equalsIgnoreCase(angebot.getProperty(FIELD__ANGEBOT_ONLINE).toString()))) {
                    LOG.warn("No Angebotsbeschreibung specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_NODESCRIPTION));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Angebotsbeschreibung not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(KlimaStandortEditor.class, BUNDLE_PANE_TITLE),
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
                LOG.info("remove propchange klima_standort: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (this.cidsBean != null) {
                setAngebotBeans(cidsBean.getBeanCollectionProperty(FIELD__ANGEBOTE));
            }
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange klima_standort: " + this.cidsBean);
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
            if (angebotBeans != null) {
                lstAngebote.setSelectedIndex(0);
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
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
            RendererTools.makeReadOnly(txtOrt);
            RendererTools.makeReadOnly(txtPlz);
            RendererTools.makeReadOnly(taBeschreibungS);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taErreichbarkeit);
            lblGeom.setVisible(isEditor);
            RendererTools.makeReadOnly(lstAngebote);
            panControlsNewAngebote.setVisible(isEditor);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setAngebotBeans(final List<CidsBean> cidsBeans) {
        this.angebotBeans = cidsBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getAngebotBeans() {
        return angebotBeans;
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
            "klima_standort",
            92,
            800,
            600);
    }
    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = KlimaConfProperties.getInstance().getBufferMeter();
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
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_STANDORT;
        } else {
            return cidsBean.toString();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        dlgAddAngebot.dispose();
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
     * @param  tableName    DOCUMENT ME!
     * @param  whereClause  DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName,
            final String whereClause) {
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
                            redundantName = check != null;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: chech name.", e);
                    }
                }
            };
        if (worker_name != null) {
            worker_name.cancel(true);
        }
        worker_name = worker;
        worker_name.execute();
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
