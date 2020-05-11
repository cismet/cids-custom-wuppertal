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
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
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

import java.text.DecimalFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.utils.EmobConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
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
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobLadestationEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TEXT_OPEN = "24 Stunden / 7 Tage";
    public static final String GEOMTYPE = "Point";
    public static final int COLUMN_WIDTH = 180;
    public static final int FOTO_WIDTH = 150;

    private static final Logger LOG = Logger.getLogger(EmobLadestationEditor.class);

    public static final String FIELD__NAME = "standort";                            // emob_ladestation
    public static final String FIELD__ID = "id";                                    // emob_ladestation
    public static final String FIELD__ZUGANG = "arr_zugangsarten";                  // emob_ladestation
    public static final String FIELD__VERSATZ = "fk_versatz";                       // emob_ladestation
    public static final String FIELD__GEOREFERENZ = "fk_geom";                      // emob_ladestation
    public static final String FIELD__SCHLUESSEL = "schluessel";                    // emob_versatz
    public static final String VERSATZ_ZENTRAL_SCHLUESSEL = "0";                    // emob_versatz.schluessel
    public static final String FIELD__GEO_FIELD = "geo_field";                      // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "fk_geom.geo_field"; // emob_ladestation_geom
    public static final String FIELD__FOTO = "foto";                                // emob_ladestation
    public static final String FIELD__STECKDOSE = "n_steckdosen";                   // emob_ladestation
    public static final String FIELD__LADEKOSTEN = "fk_abrechnungsart";             // emob_ladestation
    public static final String FIELD__LADEKOSTEN_NAME = "fk_abrechnungsart.name";   // emob_abrechnung_name
    public static final String FIELD__STECKDOSE_LADESTATION = "fk_ladestation";     // emob_steckdose
    public static final String FIELD__STECKDOSE_SPANNUNG = "spannung";              // emob_steckdose
    public static final String FIELD__STECKDOSE_STROM = "strom";                    // emob_steckdose
    public static final String FIELD__STECKDOSE_LEISTUNG = "leistung";              // emob_steckdose
    public static final String FIELD__STECKDOSE_TYP = "fk_typ";                     // emob_steckdose
    public static final String FIELD__STECKDOSE_ANZAHL = "anzahl";                  // emob_steckdose
    public static final String TABLE_NAME = "emob_ladestation";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME_VERSATZ = "emob_versatz";
    public static final String TABLE_NAME_STECKDOSE = "emob_steckdose";
    public static final String TABLE_NAME_STECKDOSENTYP = "emob_steckdosentyp";

    public static final String BUNDLE_NOLOAD = "EmobLadestationEditor.loadPictureWithUrl().noLoad";
    public static final String BUNDLE_NONAME = "EmobLadestationEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "EmobLadestationEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "EmobLadestationEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_NOCOUNT = "EmobLadestationEditor.prepareForSave().noAnzahl";
    public static final String BUNDLE_WRONGCOUNT = "EmobLadestationEditor.prepareForSave().wrongAnzahl";
    public static final String BUNDLE_NOOPEN = "EmobLadestationEditor.prepareForSave().noOpen";
    public static final String BUNDLE_NOGEOM = "EmobLadestationEditor.prepareForSave().noGeom";
    public static final String BUNDLE_WRONGGEOM = "EmobLadestationEditor.prepareForSave().wrongGeom";
    public static final String BUNDLE_NOOPERATOR = "EmobLadestationEditor.prepareForSave().noBetreiber";
    public static final String BUNDLE_TWICESOCKET = "EmobLadestationEditor.prepareForSave().twiceSocket";
    public static final String BUNDLE_NOVOLTAGE = "EmobLadestationEditor.prepareForSave().noVoltage";
    public static final String BUNDLE_NOCURRENT = "EmobLadestationEditor.prepareForSave().noCurrent";
    public static final String BUNDLE_NOPOWER = "EmobLadestationEditor.prepareForSave().noPower";
    public static final String BUNDLE_NOSOCKETTYPE = "EmobLadestationEditor.prepareForSave().noSocketType";
    public static final String BUNDLE_NOSOCKETCOUNT = "EmobLadestationEditor.prepareForSave().noSocketCount";
    public static final String BUNDLE_PANE_PREFIX = "EmobLadestationEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "EmobLadestationEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EmobLadestationEditor.prepareForSave().JOptionPane.title";

    public static final String BUNDLE_REMZUG_QUESTION =
        "EmobLadestationEditor.btnRemoveZugangActionPerformed().question";
    public static final String BUNDLE_REMZUG_TITLE = "EmobLadestationEditor.btnRemoveZugangActionPerformed().title";
    public static final String BUNDLE_REMZUG_ERRORTITLE =
        "EmobLadestationEditor.btnRemoveZugangActionPerformed().errortitle";
    public static final String BUNDLE_REMZUG_ERRORTEXT =
        "EmobLadestationEditor.btnRemoveZugangActionPerformed().errortext";

    private static final String[] STECKDOSEN_COL_NAMES = new String[] { "kW", "A", "V", "Anz", "Typ" };
    private static final String[] STECKDOSEN_PROP_NAMES = new String[] {
            "leistung",
            "strom",
            "spannung",
            "anzahl",
            "fk_typ"
        };
    private static final Class[] STECKDOSEN_PROP_TYPES = new Class[] {
            Double.class,
            Integer.class,
            Integer.class,
            Integer.class,
            CidsBean.class
        };

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum otherTableCases {

        //~ Enum constants -----------------------------------------------------

        setValue, redundantAttName
    }

    //~ Instance fields --------------------------------------------------------

    private MetaClass steckdosentypMetaClass;
    private Boolean redundantName = false;

    private boolean isEditor = true;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddSteckdose;
    private JButton btnAddZugang;
    private JButton btnMenAbortZugang;
    private JButton btnMenOkZugang;
    private JButton btnRemSteckdose;
    private JButton btnRemoveZugang;
    private JComboBox<String> cbAbrechnung;
    private DefaultBindableReferenceCombo cbBetreiber;
    private JComboBox cbGeom;
    private DefaultBindableReferenceCombo cbStromart;
    private DefaultBindableReferenceCombo cbVersatz;
    private JComboBox cbZugang;
    private JCheckBox chBarrierefrei;
    private JCheckBox chGruen;
    private JCheckBox chHalb;
    private JCheckBox chOnline;
    private JCheckBox chParkhaus;
    private JCheckBox chSchnell;
    private JCheckBox chWasserstoff;
    private JDialog dlgAddZugang;
    private Box.Filler filler1;
    private Box.Filler filler2;
    private Box.Filler filler3;
    private Box.Filler filler4;
    private JFormattedTextField ftxtAnzahl;
    private JPanel jPanel1;
    private JScrollPane jScrollPaneSteckdose;
    private JLabel lblAbrechnung;
    private JLabel lblAnzahl;
    private JLabel lblAuswaehlenZugang;
    private JLabel lblBarrierefrei;
    private JLabel lblBemerkung;
    private JLabel lblBetreiber;
    private JLabel lblFoto;
    private JLabel lblFotoAnzeigen;
    private JLabel lblGeom;
    private JLabel lblGruen;
    private JLabel lblHalb;
    private JLabel lblHnr;
    private JLabel lblKarte;
    private JLabel lblName;
    private JLabel lblOffen;
    private JLabel lblOnline;
    private JLabel lblParkgebuehr;
    private JLabel lblParkhaus;
    private JLabel lblSchnell;
    private JLabel lblSteckdose;
    private JLabel lblStrasse;
    private JLabel lblStromart;
    private JLabel lblUrlCheck;
    private JLabel lblVersatz;
    private JLabel lblWasserstoff;
    private JLabel lblZugang;
    private JLabel lblZusatz;
    private JList lstZugang;
    private JPanel panAbrechnung;
    private JPanel panAddZugang;
    private JPanel panBemerkung;
    private JPanel panButtonsZugang;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panFillerUntenFoto;
    private JPanel panGeometrie;
    private JPanel panLage;
    private JPanel panMenButtonsZugang;
    private JPanel panOffen;
    private JPanel panOnline;
    private JPanel panParkgebuehr;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panSteckdose;
    private JPanel panSteckdoseAdd;
    private JPanel panStecker;
    private JPanel panUrl;
    private JPanel panZugang;
    private JPanel panZusatz;
    private RoundedPanel rpKarte;
    private JScrollPane scpAbrechnung;
    private JScrollPane scpBemerkung;
    private JScrollPane scpLstZugang;
    private JScrollPane scpOffen;
    private JScrollPane scpParkgebuehr;
    private JScrollPane scpZusatz;
    private SemiRoundedPanel semiRoundedPanel7;
    private JSeparator sepOnline;
    private JTextArea taAbrechnung;
    private JTextArea taBemerkung;
    private JTextArea taOffen;
    private JTextArea taParkgebuehr;
    private JTextArea taZusatz;
    private JTextField txtFoto;
    private JTextField txtHnr;
    private JTextField txtName;
    private JTextField txtStrasse;
    private JXTable xtSteckdose;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EmobLadestationEditor() {
    }

    /**
     * Creates a new EmobLadestationEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EmobLadestationEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        steckdosentypMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME_STECKDOSENTYP,
                connectionContext);

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

        dlgAddZugang.pack();
        dlgAddZugang.getRootPane().setDefaultButton(btnMenOkZugang);

        if (isEditor) {
            ((DefaultBindableScrollableComboBox)this.cbAbrechnung).setNullable(true);
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

        dlgAddZugang = new JDialog();
        panAddZugang = new JPanel();
        lblAuswaehlenZugang = new JLabel();
        final MetaObject[] zugang = ObjectRendererUtils.getLightweightMetaObjectsForTable(
                "emob_zugangsart",
                new String[] { "name" },
                getConnectionContext());
        if (zugang != null) {
            Arrays.sort(zugang);
            cbZugang = new JComboBox(zugang);
            panMenButtonsZugang = new JPanel();
            btnMenAbortZugang = new JButton();
            btnMenOkZugang = new JButton();
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
            lblBetreiber = new JLabel();
            cbBetreiber = new DefaultBindableReferenceCombo(true);
            lblHalb = new JLabel();
            chHalb = new JCheckBox();
            lblOffen = new JLabel();
            panOffen = new JPanel();
            scpOffen = new JScrollPane();
            taOffen = new JTextArea();
            lblZusatz = new JLabel();
            panZusatz = new JPanel();
            scpZusatz = new JScrollPane();
            taZusatz = new JTextArea();
            lblBemerkung = new JLabel();
            panBemerkung = new JPanel();
            scpBemerkung = new JScrollPane();
            taBemerkung = new JTextArea();
            lblParkgebuehr = new JLabel();
            panParkgebuehr = new JPanel();
            scpParkgebuehr = new JScrollPane();
            taParkgebuehr = new JTextArea();
            lblFoto = new JLabel();
            txtFoto = new JTextField();
            panUrl = new JPanel();
            lblUrlCheck = new JLabel();
            lblFotoAnzeigen = new JLabel();
            lblAnzahl = new JLabel();
            ftxtAnzahl = new JFormattedTextField();
            filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
            lblSchnell = new JLabel();
            chSchnell = new JCheckBox();
            lblBarrierefrei = new JLabel();
            chBarrierefrei = new JCheckBox();
            lblGruen = new JLabel();
            chGruen = new JCheckBox();
            lblParkhaus = new JLabel();
            chParkhaus = new JCheckBox();
            lblWasserstoff = new JLabel();
            chWasserstoff = new JCheckBox();
            lblStromart = new JLabel();
            cbStromart = new DefaultBindableReferenceCombo(true);
            panFillerUntenFoto = new JPanel();
            lblAbrechnung = new JLabel();
            panAbrechnung = new JPanel();
            scpAbrechnung = new JScrollPane();
            taAbrechnung = new JTextArea();
            if (isEditor) {
                cbAbrechnung = new DefaultBindableScrollableComboBox();
            }
            panFiller = new JPanel();
            lblZugang = new JLabel();
            lblSteckdose = new JLabel();
            panZugang = new JPanel();
            scpLstZugang = new JScrollPane();
            lstZugang = new JList();
            panButtonsZugang = new JPanel();
            btnAddZugang = new JButton();
            btnRemoveZugang = new JButton();
            filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
            panStecker = new JPanel();
            panSteckdose = new JPanel();
            jScrollPaneSteckdose = new JScrollPane();
            xtSteckdose = new JXTable();
            panSteckdoseAdd = new JPanel();
            btnAddSteckdose = new JButton();
            btnRemSteckdose = new JButton();
            filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
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
            lblVersatz = new JLabel();
            cbVersatz = new DefaultBindableReferenceCombo(true);
            panOnline = new JPanel();
            sepOnline = new JSeparator();
            lblOnline = new JLabel();
            chOnline = new JCheckBox();
            panFillerUnten = new JPanel();

            dlgAddZugang.setTitle("Zugangsart");
            dlgAddZugang.setModal(true);

            panAddZugang.setLayout(new GridBagLayout());

            lblAuswaehlenZugang.setText("Bitte Zugangsart auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddZugang.add(lblAuswaehlenZugang, gridBagConstraints);
        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddZugang.add(cbZugang, gridBagConstraints);

        panMenButtonsZugang.setLayout(new GridBagLayout());

        btnMenAbortZugang.setText("Abbrechen");
        btnMenAbortZugang.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenAbortZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsZugang.add(btnMenAbortZugang, gridBagConstraints);

        btnMenOkZugang.setText("Ok");
        btnMenOkZugang.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenOkZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsZugang.add(btnMenOkZugang, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddZugang.add(panMenButtonsZugang, gridBagConstraints);

        dlgAddZugang.getContentPane().add(panAddZugang, BorderLayout.CENTER);

        setLayout(new GridBagLayout());

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
                GroupLayout.Alignment.LEADING).addGap(0, 532, Short.MAX_VALUE));
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
                ELProperty.create("${cidsBean.standort}"),
                txtName,
                BeanProperty.create("text"));
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
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtStrasse, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hausnummer:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
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
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblBetreiber.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBetreiber.setText("Betreiber:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBetreiber, gridBagConstraints);

        cbBetreiber.setNullable(false);
        cbBetreiber.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbBetreiber.setMaximumRowCount(6);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_betreiber}"),
                cbBetreiber,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbBetreiber, gridBagConstraints);

        lblHalb.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHalb.setText("halb-öffentlich:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHalb, gridBagConstraints);

        chHalb.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.halb_oeffentlich}"),
                chHalb,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chHalb.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent evt) {
                    chHalbStateChanged(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chHalb, gridBagConstraints);

        lblOffen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOffen.setText("Öffnungszeiten:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.oeffnungszeiten}"),
                taOffen,
                BeanProperty.create("text"));
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panOffen, gridBagConstraints);

        lblZusatz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblZusatz.setText("Zusatzinfo:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.zusatzinfo}"),
                taZusatz,
                BeanProperty.create("text"));
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 8;
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
        gridBagConstraints.gridy = 9;
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
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblParkgebuehr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblParkgebuehr.setText("Parkgebühr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblParkgebuehr, gridBagConstraints);

        panParkgebuehr.setOpaque(false);
        panParkgebuehr.setLayout(new GridBagLayout());

        taParkgebuehr.setLineWrap(true);
        taParkgebuehr.setRows(2);
        taParkgebuehr.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.parkgebuehr}"),
                taParkgebuehr,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpParkgebuehr.setViewportView(taParkgebuehr);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panParkgebuehr.add(scpParkgebuehr, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panParkgebuehr, gridBagConstraints);

        lblFoto.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFoto.setText("Foto:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblFoto, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.foto}"),
                txtFoto,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtFoto, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panUrl, gridBagConstraints);

        lblFotoAnzeigen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 4);
        panDaten.add(lblFotoAnzeigen, gridBagConstraints);

        lblAnzahl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAnzahl.setText("Anzahl:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAnzahl, gridBagConstraints);

        ftxtAnzahl.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.anzahl_ladeplaetze}"),
                ftxtAnzahl,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(ftxtAnzahl, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler4, gridBagConstraints);

        lblSchnell.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSchnell.setText("Schnellladung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSchnell, gridBagConstraints);

        chSchnell.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.schnellladestation}"),
                chSchnell,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chSchnell, gridBagConstraints);

        lblBarrierefrei.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBarrierefrei.setText("Barrierefrei:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBarrierefrei, gridBagConstraints);

        chBarrierefrei.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.barrierefrei}"),
                chBarrierefrei,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chBarrierefrei, gridBagConstraints);

        lblGruen.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGruen.setText("Grüner Strom:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGruen, gridBagConstraints);

        chGruen.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.gruener_strom}"),
                chGruen,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chGruen, gridBagConstraints);

        lblParkhaus.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblParkhaus.setText("Parkhaus:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblParkhaus, gridBagConstraints);

        chParkhaus.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.parkhaus}"),
                chParkhaus,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chParkhaus, gridBagConstraints);

        lblWasserstoff.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblWasserstoff.setText("Wasserstoff:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblWasserstoff, gridBagConstraints);

        chWasserstoff.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.wasserstoff}"),
                chWasserstoff,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chWasserstoff, gridBagConstraints);

        lblStromart.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStromart.setText("Stromart:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStromart, gridBagConstraints);

        cbStromart.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_stromart}"),
                cbStromart,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStromart, gridBagConstraints);

        panFillerUntenFoto.setName(""); // NOI18N
        panFillerUntenFoto.setOpaque(false);

        final GroupLayout panFillerUntenFotoLayout = new GroupLayout(panFillerUntenFoto);
        panFillerUntenFoto.setLayout(panFillerUntenFotoLayout);
        panFillerUntenFotoLayout.setHorizontalGroup(panFillerUntenFotoLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUntenFotoLayout.setVerticalGroup(panFillerUntenFotoLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panFillerUntenFoto, gridBagConstraints);

        lblAbrechnung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAbrechnung.setText("Ladekosten:");
        lblAbrechnung.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblAbrechnung, gridBagConstraints);

        panAbrechnung.setOpaque(false);
        panAbrechnung.setLayout(new GridBagLayout());

        taAbrechnung.setEditable(false);
        taAbrechnung.setLineWrap(true);
        taAbrechnung.setRows(2);
        taAbrechnung.setToolTipText("");
        taAbrechnung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_abrechnungsart.name}"),
                taAbrechnung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpAbrechnung.setViewportView(taAbrechnung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panAbrechnung.add(scpAbrechnung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panAbrechnung, gridBagConstraints);

        if (isEditor) {
            cbAbrechnung.setFont(new Font("Dialog", 0, 12)); // NOI18N
            if (isEditor) {
                cbAbrechnung.setPreferredSize(new Dimension(100, 24));
            }

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.fk_abrechnungsart}"),
                    cbAbrechnung,
                    BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 26;
            gridBagConstraints.gridwidth = 8;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbAbrechnung, gridBagConstraints);
        }

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

        lblZugang.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblZugang.setText("Zugang:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblZugang, gridBagConstraints);

        lblSteckdose.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSteckdose.setText("Steckdose:");
        lblSteckdose.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSteckdose, gridBagConstraints);

        panZugang.setLayout(new GridBagLayout());

        scpLstZugang.setMinimumSize(new Dimension(258, 66));

        lstZugang.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstZugang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstZugang.setVisibleRowCount(4);

        final ELProperty eLProperty = ELProperty.create("${cidsBean.arr_zugangsarten}");
        final JListBinding jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstZugang);
        bindingGroup.addBinding(jListBinding);

        scpLstZugang.setViewportView(lstZugang);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panZugang.add(scpLstZugang, gridBagConstraints);

        panButtonsZugang.setOpaque(false);
        panButtonsZugang.setLayout(new GridBagLayout());

        btnAddZugang.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddZugang.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsZugang.add(btnAddZugang, gridBagConstraints);

        btnRemoveZugang.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveZugang.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemoveZugangActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsZugang.add(btnRemoveZugang, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsZugang.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panZugang.add(panButtonsZugang, gridBagConstraints);
        panButtonsZugang.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panZugang, gridBagConstraints);

        panStecker.setLayout(new GridBagLayout());

        panSteckdose.setMinimumSize(new Dimension(26, 80));
        panSteckdose.setLayout(new GridBagLayout());

        xtSteckdose.setVisibleRowCount(4);
        jScrollPaneSteckdose.setViewportView(xtSteckdose);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panSteckdose.add(jScrollPaneSteckdose, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStecker.add(panSteckdose, gridBagConstraints);

        panSteckdoseAdd.setAlignmentX(0.0F);
        panSteckdoseAdd.setAlignmentY(1.0F);
        panSteckdoseAdd.setFocusable(false);
        panSteckdoseAdd.setLayout(new GridBagLayout());

        btnAddSteckdose.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddSteckdose.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnAddSteckdoseActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panSteckdoseAdd.add(btnAddSteckdose, gridBagConstraints);

        btnRemSteckdose.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemSteckdose.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnRemSteckdoseActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panSteckdoseAdd.add(btnRemSteckdose, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panSteckdoseAdd.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panStecker.add(panSteckdoseAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panStecker, gridBagConstraints);

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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_versatz}"),
                cbVersatz,
                BeanProperty.create("selectedItem"));
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
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        jPanel1.add(panGeometrie, gridBagConstraints);

        panOnline.setOpaque(false);
        panOnline.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 0, 5, 0);
        panOnline.add(sepOnline, gridBagConstraints);

        lblOnline.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panOnline.add(lblOnline, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.online}"),
                chOnline,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOnline.add(chOnline, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        jPanel1.add(panOnline, gridBagConstraints);

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

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddZugangActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(EmobLadestationEditor.this), dlgAddZugang, true);
    }                                                                 //GEN-LAST:event_btnAddZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemoveZugangActionPerformed
        final Object selection = lstZugang.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_REMZUG_QUESTION),
                    NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_REMZUG_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__ZUGANG, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMZUG_ERRORTITLE,
                            BUNDLE_REMZUG_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }                                                                    //GEN-LAST:event_btnRemoveZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortZugangActionPerformed
        dlgAddZugang.setVisible(false);
    }                                                                      //GEN-LAST:event_btnMenAbortZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkZugangActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkZugangActionPerformed
        try {
            final Object selItem = cbZugang.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__ZUGANG,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__ZUGANG);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddZugang.setVisible(false);
        }
    }                                                                   //GEN-LAST:event_btnMenOkZugangActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddSteckdoseActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddSteckdoseActionPerformed
        TableUtils.addObjectToTable(xtSteckdose, TABLE_NAME_STECKDOSE, getConnectionContext());
    }                                                                    //GEN-LAST:event_btnAddSteckdoseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemSteckdoseActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnRemSteckdoseActionPerformed
        TableUtils.removeObjectsFromTable(xtSteckdose);
    }                                                                    //GEN-LAST:event_btnRemSteckdoseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chHalbStateChanged(final ChangeEvent evt) { //GEN-FIRST:event_chHalbStateChanged
        isOpen();
    }                                                        //GEN-LAST:event_chHalbStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  propName  DOCUMENT ME!
     */
    private void sortListNew(final String propName) {
        bindingGroup.unbind();
        final List<CidsBean> changeCol = CidsBeanSupport.getBeanCollectionFromProperty(
                cidsBean,
                propName);
        Collections.sort(changeCol, AlphanumComparator.getInstance());
        bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     */
    private void doWithFotoUrl() {
        final String foto = EmobConfProperties.getInstance().getFotoUrlAutos().concat(txtFoto.getText());
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
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
            otherTableCases.redundantAttName);
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_DUPLICATENAME));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Betreiber muss angegeben werden
        try {
            if (cbBetreiber.getSelectedItem() == null) {
                LOG.warn("No operator specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOOPERATOR));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Operator not given.", ex);
            save = false;
        }
        // Straße muss angegeben werden
        try {
            if (txtStrasse.getText().trim().isEmpty()) {
                LOG.warn("No street specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOSTREET));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Street not given.", ex);
            save = false;
        }
        // Anzahl muss angegeben werden
        try {
            if (ftxtAnzahl.getText().trim().isEmpty()) {
                LOG.warn("No count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOCOUNT));
            } else {
                try {
                    Integer.parseInt(ftxtAnzahl.getText());
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong count specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_WRONGCOUNT));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Countl not given.", ex);
            save = false;
        }
        // Öffnungszeiten müssen angegeben werden, wenn halb-öffentlich
        try {
            if (taOffen.getText().trim().isEmpty() && chHalb.isSelected()) {
                LOG.warn("No open specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOOPEN));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Open not given.", ex);
            save = false;
        }
        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE)) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_WRONGGEOM));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        // Steckdose prüfen
        switch (checkValuesForSocket()) {
            case 1: {
                LOG.warn("Twice socket specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_TWICESOCKET));
                break;
            }
            case 2: {
                LOG.warn("No voltage specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOVOLTAGE));
                break;
            }
            case 3: {
                LOG.warn("No current specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOCURRENT));
                break;
            }
            case 4: {
                LOG.warn("No power specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOPOWER));
                break;
            }
            case 5: {
                LOG.warn("No socket type specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOSOCKETTYPE));
                break;
            }
            case 6: {
                LOG.warn("No socket count specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOSOCKETCOUNT));
                break;
            }
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private int checkValuesForSocket() {
        try {
            final List<CidsBean> listSocketBeans = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__STECKDOSE);

            if ((listSocketBeans != null) && (listSocketBeans.size() > 0)) {
                for (int i = 0; i < listSocketBeans.size(); i++) {
                    // .......Überprüfen ob alle Einträge gefüllt.......
                    if ((null == listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_SPANNUNG))
                                || "".equals(
                                    listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_SPANNUNG).toString())) {
                        return 2;
                    }
                    if ((null == listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_STROM))
                                || "".equals(listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_STROM).toString())) {
                        return 3;
                    }
                    if ((null == listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_LEISTUNG))
                                || "".equals(
                                    listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_LEISTUNG).toString())) {
                        return 4;
                    }
                    if ((null == listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_TYP))
                                || "".equals(listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_TYP).toString())) {
                        return 5;
                    }
                    if ((null == listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_ANZAHL))
                                || "".equals(listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_ANZAHL).toString())) {
                        return 6;
                    }
                    // Redundante Einträge
                    if (listSocketBeans.size() > (i + 1)) {
                        for (int j = i + 1; j < listSocketBeans.size(); j++) {
                            if (
                                listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_SPANNUNG).equals(
                                            listSocketBeans.get(j).getProperty(FIELD__STECKDOSE_SPANNUNG))
                                        && listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_STROM).equals(
                                            listSocketBeans.get(j).getProperty(FIELD__STECKDOSE_STROM))
                                        && listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_LEISTUNG).equals(
                                            listSocketBeans.get(j).getProperty(FIELD__STECKDOSE_LEISTUNG))
                                        && listSocketBeans.get(i).getProperty(FIELD__STECKDOSE_TYP).equals(
                                            listSocketBeans.get(j).getProperty(FIELD__STECKDOSE_TYP))) {
                                return 1;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return 0;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
        try {
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("remove propchange emob_ladestation: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange emob_ladestation: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            // Damit die Zugänge sortiert in der Liste erscheinen.
            final List<CidsBean> zugangCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__ZUGANG);
            Collections.sort(zugangCol, AlphanumComparator.getInstance());

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            isOpen();
            setDefaultVersatz();
            final DivBeanTable steckdoseModel = new DivBeanTable(
                    isEditor,
                    cidsBean,
                    FIELD__STECKDOSE,
                    STECKDOSEN_COL_NAMES,
                    STECKDOSEN_PROP_NAMES,
                    STECKDOSEN_PROP_TYPES);
            xtSteckdose.setModel(steckdoseModel);
            xtSteckdose.getColumn(4).setCellEditor(new DefaultBindableComboboxCellEditor(steckdosentypMetaClass));
            xtSteckdose.getColumn(4).setPreferredWidth(COLUMN_WIDTH);
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void setDefaultVersatz() {
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
                otherTableCases.setValue);
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
            RendererTools.makeReadOnly(cbBetreiber);
            RendererTools.makeReadOnly(chHalb);
            RendererTools.makeReadOnly(taOffen);
            RendererTools.makeReadOnly(taZusatz);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taParkgebuehr);
            RendererTools.makeReadOnly(txtFoto);
            RendererTools.makeReadOnly(ftxtAnzahl);
            RendererTools.makeReadOnly(chBarrierefrei);
            RendererTools.makeReadOnly(chParkhaus);
            RendererTools.makeReadOnly(chGruen);
            RendererTools.makeReadOnly(chSchnell);
            RendererTools.makeReadOnly(chWasserstoff);
            RendererTools.makeReadOnly(cbStromart);
            RendererTools.makeReadOnly(cbAbrechnung);
            RendererTools.makeReadOnly(lstZugang);
            panButtonsZugang.setVisible(isEditor);
            RendererTools.makeReadOnly(xtSteckdose);
            panSteckdoseAdd.setVisible(isEditor);
            lblGeom.setVisible(isEditor);
            RendererTools.makeReadOnly(cbVersatz);
            RendererTools.makeReadOnly(chOnline);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void isOpen() {
        final boolean isNotOpen = chHalb.isSelected();

        if (isEditor) {
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
        dlgAddZugang.dispose();
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
                            showLabel.setText(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOLOAD));
                            showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setText(NbBundle.getMessage(EmobLadestationEditor.class, BUNDLE_NOLOAD));
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("load picture Problem in Worker.", e);
                    }
                }
            };
        worker.execute();
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
                        check = get();
                        if (check != null) {
                            switch (fall) {
                                case setValue: {         // set default value
                                    try {
                                        cidsBean.setProperty(
                                            propertyName,
                                            check);
                                    } catch (Exception ex) {
                                        LOG.warn("setVersatz: Versatz not set.", ex);
                                    }
                                    break;
                                }
                                case redundantAttName: { // check redundant name
                                    redundantName = true;
                                    break;
                                }
                            }
                        } else {
                            switch (fall) {
                                case redundantAttName: { // check redundant name
                                    redundantName = false;
                                    break;
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                }
            };
        worker.execute();
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
