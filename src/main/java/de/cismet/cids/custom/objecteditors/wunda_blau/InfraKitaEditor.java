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
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
//import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.Exceptions;
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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.HttpURLConnection;
import java.net.URL;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.DefaultFormatter;


//import com.vividsolutions.jts.geom.PrecisionModel;
import de.cismet.cids.custom.objecteditors.utils.InspireUtils;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
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

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class InfraKitaEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(InfraKitaEditor.class);

    public static final String FIELD__VERSION_KITA = "version_kita";                    // infra_kita
    public static final String FIELD__VERSIONNR = "versionnr";                          // inspire_infra_kita_version
    public static final String FIELD__ONLINE_STELLEN = "online_stellen";                // infra_kita
    public static final String FIELD__ENDLIFESPANVERSION = "endlifespanversion";        // inspire_infra_kita_version
    public static final String FIELD__BEGINLIFESPANVERSION = "beginlifespanversion";    // inspire_infra_kita_version
    public static final String FIELD__GEOREFERENZ = "georeferenz";                      // infra_kita
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field"; // infra_kita
    public static final String FIELD__URL = "url";                                      // infra_kita
    public static final String FIELD__TELEFEON = "telefon";                             // infra_kita
    public static final String FIELD__INSPIRE_ID = "inspire_id";                        // infra_kita
    public static final String FIELD__GEO_FIELD = "geo_field";                          // geom
    public static final String FIELD__POINT = "point";                                  // inspire_infra_kita_version
    public static final String FIELD__WEBSITE = "website";                              // inspire_infra_kita_version
    public static final String FIELD__TELEPHONEVOICE = "telephonevoice";                // inspire_infra_kita_version
    public static final String FIELD__NAME = "name";                                    // infra_kita
    public static final String TABLE_NAME = "infra_kita";
    public static final String TABLE_GEOM = "geom";
    public static final String TABLE_NAME_VERSION = "inspire_infra_kita_version";

    public static final Coordinate RATHAUS_POINT = new Coordinate(374420, 5681660);

    public static final Pattern TEL_FILLING_PATTERN = Pattern.compile("(|\\+(-|[0-9])*)");
    public static final Pattern TEL_MATCHING_PATTERN = Pattern.compile("\\+[0-9]{1,3}(-[0-9]+){1,}");

    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;
    private String urlAttribute;
    private String telAttribute;
    private String onlineAttribute;
    private String geomAttribute;
    private Object versionAttribute;

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOK = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));
    /*final private ImageIcon statusDefault = new ImageIcon(
     *   getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-offline.png"));*/
    private final ImageIcon inspired = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/inspire_logo_en_100x100px.png"));
    private final ImageIcon notinspired = new ImageIcon(
            getClass().getResource(
                "/de/cismet/cids/custom/objecteditors/wunda_blau/inspire_logo_en_100x100px_soft.png"));
    // final private DefaultListModel dLModel = new DefaultListModel();

    private final RegexPatternFormatter telPatternFormatter = new RegexPatternFormatter(
            TEL_FILLING_PATTERN,
            TEL_MATCHING_PATTERN);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnMenOkName;
    private DefaultBindableReferenceCombo cbAlter;
    private JComboBox cbGeom;
    private DefaultBindableReferenceCombo cbStunden;
    private DefaultBindableReferenceCombo cbTraegertyp;
    private JCheckBox chFamilienzentrzum;
    private JCheckBox chInklusion;
    private JCheckBox chOnline;
    private JDialog dlgChangeKitaName;
    private JFormattedTextField jFormattedTextField1;
    private JLabel jLabel1;
    private JTextField jTextField1;
    private JXHyperlink jXHyperlink1;
    private JLabel lblAdresse;
    private JLabel lblAlter;
    private JLabel lblBemerkung;
    private JLabel lblFamilienzentrum;
    private JLabel lblGeom;
    private JLabel lblInklusion;
    private JLabel lblInspire;
    private JLabel lblInspireKita;
    private JLabel lblKarte;
    private JLabel lblLeitung;
    private JLabel lblName;
    private JLabel lblOnline;
    private JLabel lblPlaetze;
    private JLabel lblPlz;
    private JLabel lblStunden;
    private JLabel lblTelefon;
    private JLabel lblTraegertyp;
    private JLabel lblUrl;
    private JLabel lblUrlCheck;
    private JLabel lblWarningTextJa;
    private JLabel lblWarningTextNein;
    private JPanel panAdresse;
    private JPanel panChangeKitaName;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFiller;
    private JPanel panFillerRechtsLage;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panLage;
    private JPanel panMenButtonsName;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUrl;
    private RoundedPanel rpKarte;
    private SemiRoundedPanel semiRoundedPanel7;
    private JSeparator sepOnline;
    private JTextArea taName;
    private JTextArea taNein;
    private JTextField txtAdresse;
    private JTextField txtBemerkung;
    private JTextField txtLeitung;
    private JTextField txtName;
    private JTextField txtPlaetze;
    private JTextField txtPlz;
    private JTextField txtUrl;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public InfraKitaEditor() {
    }

    /**
     * Creates a new InfraKitaEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public InfraKitaEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        if (isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
            dlgChangeKitaName.pack();
            dlgChangeKitaName.getRootPane().setDefaultButton(btnMenOkName);
        }
        setReadOnly();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshValidTel() {
        jFormattedTextField1.setValue(telPatternFormatter.getLastValid());
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

        dlgChangeKitaName = new JDialog();
        panChangeKitaName = new JPanel();
        lblWarningTextNein = new JLabel();
        taName = new JTextArea();
        panMenButtonsName = new JPanel();
        btnMenOkName = new JButton();
        lblWarningTextJa = new JLabel();
        taNein = new JTextArea();
        lblInspireKita = new JLabel();
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panFillerUnten1 = new JPanel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panFillerRechtsLage = new JPanel();
        panAdresse = new JPanel();
        lblName = new JLabel();
        lblGeom = new JLabel();
        txtName = new JTextField();
        if (isEditor) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        txtAdresse = new JTextField();
        lblAdresse = new JLabel();
        lblInspire = new JLabel();
        jLabel1 = new JLabel();
        jTextField1 = new JTextField();
        panDaten = new JPanel();
        txtPlz = new JTextField();
        txtPlaetze = new JTextField();
        txtLeitung = new JTextField();
        lblLeitung = new JLabel();
        lblTelefon = new JLabel();
        lblPlz = new JLabel();
        lblPlaetze = new JLabel();
        lblInklusion = new JLabel();
        lblStunden = new JLabel();
        lblAlter = new JLabel();
        lblTraegertyp = new JLabel();
        panFiller = new JPanel();
        sepOnline = new JSeparator();
        chInklusion = new JCheckBox();
        txtBemerkung = new JTextField();
        lblOnline = new JLabel();
        lblBemerkung = new JLabel();
        lblUrl = new JLabel();
        panUrl = new JPanel();
        jXHyperlink1 = new JXHyperlink();
        txtUrl = new JTextField();
        lblUrlCheck = new JLabel();
        cbTraegertyp = new DefaultBindableReferenceCombo(true);
        cbAlter = new DefaultBindableReferenceCombo(true);
        cbStunden = new DefaultBindableReferenceCombo(true);
        chOnline = new JCheckBox();
        jFormattedTextField1 = new JFormattedTextField(telPatternFormatter);
        lblFamilienzentrum = new JLabel();
        chFamilienzentrzum = new JCheckBox();

        dlgChangeKitaName.setTitle("Ist dies eine neue Kita?");
        dlgChangeKitaName.setMinimumSize(new Dimension(215, 200));
        dlgChangeKitaName.setModal(true);

        panChangeKitaName.setMaximumSize(new Dimension(200, 150));
        panChangeKitaName.setMinimumSize(new Dimension(200, 150));
        panChangeKitaName.setPreferredSize(new Dimension(200, 150));
        panChangeKitaName.setLayout(new GridBagLayout());

        lblWarningTextNein.setText("Nicht: ");
        lblWarningTextNein.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new Insets(15, 10, 10, 10);
        panChangeKitaName.add(lblWarningTextNein, gridBagConstraints);

        taName.setColumns(20);
        taName.setRows(3);
        taName.setText("Löschen Sie diese Kita\nzuerst und legen dann \neine neue Kita an.");
        taName.setMinimumSize(new Dimension(140, 50));
        taName.setOpaque(false);
        taName.setPreferredSize(new Dimension(140, 60));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panChangeKitaName.add(taName, gridBagConstraints);

        panMenButtonsName.setLayout(new GridBagLayout());

        btnMenOkName.setText("Ok");
        btnMenOkName.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenOkNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsName.add(btnMenOkName, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panChangeKitaName.add(panMenButtonsName, gridBagConstraints);

        lblWarningTextJa.setText("Wenn ja: ");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panChangeKitaName.add(lblWarningTextJa, gridBagConstraints);

        taNein.setColumns(20);
        taNein.setRows(3);
        taNein.setText("Dann korrigieren Sie ja\nnur einen Tippfehler....");
        taNein.setToolTipText("");
        taNein.setMinimumSize(new Dimension(140, 50));
        taNein.setOpaque(false);
        taNein.setPreferredSize(new Dimension(140, 60));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panChangeKitaName.add(taNein, gridBagConstraints);

        lblInspireKita.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"))); // NOI18N
        lblInspireKita.setToolTipText("Warnung");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panChangeKitaName.add(lblInspireKita, gridBagConstraints);

        dlgChangeKitaName.getContentPane().add(panChangeKitaName, BorderLayout.CENTER);

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
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

        panContent.setMaximumSize(new Dimension(450, 2147483647));
        panContent.setMinimumSize(new Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(450, 961));
        panContent.setLayout(new GridBagLayout());

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
        panContent.add(panFillerUnten1, gridBagConstraints);

        panLage.setMinimumSize(new Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new GridBagLayout());

        panPreviewMap.setMinimumSize(new Dimension(600, 600));
        panPreviewMap.setPreferredSize(new Dimension(500, 300));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 10, 5);
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
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        final GroupLayout panFillerRechtsLageLayout = new GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(panFillerRechtsLageLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerRechtsLageLayout.setVerticalGroup(panFillerRechtsLageLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panLage.add(panFillerRechtsLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 9.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panContent.add(panLage, gridBagConstraints);

        panAdresse.setOpaque(false);
        panAdresse.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblName, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblGeom, gridBagConstraints);

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
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(txtName, gridBagConstraints);

        if (isEditor) {
            if (isEditor) {
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(
                    AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    ELProperty.create("${cidsBean.georeferenz}"),
                    cbGeom,
                    BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 4, 2, 2);
            panAdresse.add(cbGeom, gridBagConstraints);
        }

        txtAdresse.setToolTipText("");

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.adresse}"),
                txtAdresse,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(txtAdresse, gridBagConstraints);

        lblAdresse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAdresse.setText("Adresse:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblAdresse, gridBagConstraints);

        lblInspire.setIcon(inspired);
        lblInspire.setToolTipText("Der Datensatz ist inspired.");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panAdresse.add(lblInspire, gridBagConstraints);

        jLabel1.setFont(new Font("Tahoma", 1, 11));
        jLabel1.setText("eindeutiger Kurzname:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(jLabel1, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.eindeutiger_kurzname}"),
                jTextField1,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(jTextField1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panAdresse, gridBagConstraints);

        panDaten.setMinimumSize(new Dimension(374, 190));
        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtPlz, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.plaetze}"),
                txtPlaetze,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtPlaetze, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.leitung}"),
                txtLeitung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtLeitung, gridBagConstraints);

        lblLeitung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLeitung.setText("Leitung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblLeitung, gridBagConstraints);

        lblTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblTelefon, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        lblPlaetze.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlaetze.setText("Plätze:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panDaten.add(lblPlaetze, gridBagConstraints);

        lblInklusion.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblInklusion.setText("Inklusion:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblInklusion, gridBagConstraints);

        lblStunden.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStunden.setText("h/Woche:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblStunden, gridBagConstraints);

        lblAlter.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAlter.setText("Alter:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblAlter, gridBagConstraints);

        lblTraegertyp.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTraegertyp.setText("Trägertyp:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblTraegertyp, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);
        panFiller.setPreferredSize(new Dimension(20, 0));

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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDaten.add(panFiller, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 4, 0, 2);
        panDaten.add(sepOnline, gridBagConstraints);

        chInklusion.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.plaetze_fuer_behinderte}"),
                chInklusion,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(4, 0, 4, 0);
        panDaten.add(chInklusion, gridBagConstraints);

        txtBemerkung.setToolTipText("");

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtBemerkung, gridBagConstraints);

        lblOnline.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblOnline, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        lblUrl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("Homepage:");
        lblUrl.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblUrl, gridBagConstraints);

        panUrl.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.url}"),
                jXHyperlink1,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panUrl.add(jXHyperlink1, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.url}"),
                txtUrl,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panUrl.add(txtUrl, gridBagConstraints);

        lblUrlCheck.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDaten.add(panUrl, gridBagConstraints);

        cbTraegertyp.setMaximumSize(new Dimension(200, 23));
        cbTraegertyp.setMinimumSize(new Dimension(150, 23));
        cbTraegertyp.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.traegertyp}"),
                cbTraegertyp,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbTraegertyp, gridBagConstraints);

        cbAlter.setMaximumSize(new Dimension(200, 23));
        cbAlter.setMinimumSize(new Dimension(150, 23));
        cbAlter.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.alter}"),
                cbAlter,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbAlter, gridBagConstraints);

        cbStunden.setMaximumSize(new Dimension(200, 23));
        cbStunden.setMinimumSize(new Dimension(150, 23));
        cbStunden.setPreferredSize(new Dimension(150, 23));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.stunden}"),
                cbStunden,
                BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbStunden, gridBagConstraints);

        chOnline.setToolTipText("");

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.online_stellen}"),
                chOnline,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(4, 0, 4, 0);
        panDaten.add(chOnline, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.telefon}"),
                jFormattedTextField1,
                BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        jFormattedTextField1.addFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(final FocusEvent evt) {
                    jFormattedTextField1FocusLost(evt);
                }
            });
        jFormattedTextField1.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    jFormattedTextField1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(jFormattedTextField1, gridBagConstraints);

        lblFamilienzentrum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFamilienzentrum.setText("Familienzentrum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblFamilienzentrum, gridBagConstraints);

        chFamilienzentrzum.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.familienzentrum}"),
                chFamilienzentrzum,
                BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(4, 0, 4, 0);
        panDaten.add(chFamilienzentrzum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        panContent.add(panDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkNameActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkNameActionPerformed
        dlgChangeKitaName.setVisible(false);
    }                                                                 //GEN-LAST:event_btnMenOkNameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jFormattedTextField1FocusLost(final FocusEvent evt) { //GEN-FIRST:event_jFormattedTextField1FocusLost
        refreshValidTel();
    }                                                                  //GEN-LAST:event_jFormattedTextField1FocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jFormattedTextField1ActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_jFormattedTextField1ActionPerformed
        refreshValidTel();
    }                                                                         //GEN-LAST:event_jFormattedTextField1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        try {
            BrowserLauncher.openURL(jXHyperlink1.getText());
        } catch (final Exception e) {
            LOG.fatal("Problem during opening url", e);
            final ErrorInfo ei = new ErrorInfo(
                    "Fehler beim Aufrufen der Url",
                    "Beim Aufrufen der Url ist ein Fehler aufgetreten",
                    null,
                    null,
                    e,
                    Level.SEVERE,
                    null);
            JXErrorPane.showDialog(this, ei);
        }
    }                                                                 //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  versionBean  DOCUMENT ME!
     * @param  timestamp    DOCUMENT ME!
     */
    private void finishVersion(final CidsBean versionBean, final Timestamp timestamp) {
        try {
            versionBean.setProperty(FIELD__ENDLIFESPANVERSION, timestamp);
        } catch (final Exception e) {
            LOG.fatal("Problem during closing kita", e);
            final ErrorInfo ei = new ErrorInfo(
                    "Fehler beim Löschen",
                    "Beim Löschen der Version dieser Kita ist ein Fehler aufgetreten",
                    null,
                    null,
                    e,
                    Level.SEVERE,
                    null);
            JXErrorPane.showDialog(this, ei);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getLastVersion() {
        CidsBean dateCidsBean = null;
        int version = 0;
        final Object o = cidsBean.getProperty(FIELD__VERSION_KITA);
        if (o instanceof Collection) {
            try {
                final Collection<CidsBean> col = (Collection)o;
                for (final CidsBean bean : col) {
                    if (version < (int)bean.getProperty(FIELD__VERSIONNR)) {
                        version = (int)bean.getProperty(FIELD__VERSIONNR);
                        dateCidsBean = bean;
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        return dateCidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void checkInspireID() {
        try {
            if (cidsBean.getProperty(FIELD__INSPIRE_ID) == null) {
                lblInspire.setIcon(notinspired);
                lblInspire.setToolTipText("Der Datensatz ist nicht inspired.");
            } else {
                lblInspire.setIcon(inspired);
                lblInspire.setToolTipText("Der Datensatz ist inspired.");
            }
        } catch (final Exception e) {
            lblInspire.setIcon(inspired);
            lblInspire.setToolTipText("Der Datensatz ist inspired.");
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void testUrlAndShowResult() {
        try {
            final URL url = new URL(txtUrl.getText());
            if (checkURL(url)) {
                lblUrlCheck.setIcon(statusOK);
            } else {
                lblUrlCheck.setIcon(statusFalsch);
            }
        } catch (final Exception e) {
            lblUrlCheck.setIcon(statusFalsch);
        }
    }

    /**
     * DOCUMENT ME! The WebAccessManager should be used. See <code>EmobLadestationEditor.checkUrl(String, JLabel)</code>
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkURL(final URL url) {
        try {
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            final int responseCode = connection.getResponseCode();
            return (responseCode >= 200) && (responseCode < 400);
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean newVersion = false;

        // adresse vorhanden
        try {
            if (txtAdresse.getText().trim().isEmpty()) {
                LOG.warn("No adress specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        InfraKitaEditor.class,
                        "InfraKitaEditor.prepareForSave().noAdresse"));
            }
        } catch (final Exception ex) {
            LOG.warn("Adress not given.", ex);
            save = false;
        }

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        InfraKitaEditor.class,
                        "InfraKitaEditor.prepareForSave().noName"));
            }
        } catch (final Exception ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }

        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        InfraKitaEditor.class,
                        "InfraKitaEditor.prepareForSave().noGeom"));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(
                            InfraKitaEditor.class,
                            "InfraKitaEditor.prepareForSave().wrongGeom"));
                } else {
                    if (!(setNotNull(cidsBean.getProperty(FIELD__GEOREFERENZ)).equals(geomAttribute))
                                && (cidsBean.getMetaObject().getStatus() != MetaObject.NEW)) {
                        newVersion = true;
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        // Änderung der url
        if (!(setNotNull(cidsBean.getProperty(FIELD__URL)).equals(urlAttribute))
                    && (cidsBean.getMetaObject().getStatus() != MetaObject.NEW)) {
            newVersion = true;
        }

        // Änderung des Telefons
        if (!(setNotNull(cidsBean.getProperty(FIELD__TELEFEON)).equals(telAttribute))
                    && (cidsBean.getMetaObject().getStatus() != MetaObject.NEW)) {
            newVersion = true;
        }

        // Soll eine neue Version erstellt werden?
        if (newVersion && chOnline.isSelected() && (versionAttribute != null)
                    && onlineAttribute.equals(setNotNull(chOnline.isSelected()))) {
            createNewVersion();
        }

        // Erzeugung einer neuen eindeutigen uuid und Anlegen der ersten Version
        try {
            String uuid;
            if ((this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) && chOnline.isSelected()) {
                uuid = InspireUtils.generateUuid(getConnectionContext());
                InspireUtils.writeUuid(uuid, cidsBean, FIELD__INSPIRE_ID, TABLE_NAME, getConnectionContext());
                createFirstVersion(1, new Timestamp(new Date().getTime()));
            } else {
                if ((cidsBean.getMetaObject().getStatus() != MetaObject.NEW)
                            && !(setNotNull(cidsBean.getProperty(FIELD__ONLINE_STELLEN)).equals(onlineAttribute))
                            && chOnline.isSelected()) {
                    uuid = InspireUtils.generateUuid(getConnectionContext());
                    InspireUtils.writeUuid(uuid, cidsBean, FIELD__INSPIRE_ID, TABLE_NAME, getConnectionContext());
                    createFirstVersion(1, new Timestamp(new Date().getTime()));
                } else {
                    if ((cidsBean.getMetaObject().getStatus() != MetaObject.NEW)
                                && !(setNotNull(cidsBean.getProperty(FIELD__ONLINE_STELLEN)).equals(onlineAttribute))
                                && !(chOnline.isSelected())) {
                        LOG.warn("Offline specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(
                                InfraKitaEditor.class,
                                "InfraKitaEditor.prepareForSave().wrongOffline"));
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("inspireid not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    InfraKitaEditor.class,
                    "InfraKitaEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            InfraKitaEditor.class,
                            "InfraKitaEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(InfraKitaEditor.class,
                    "InfraKitaEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  versionnr  DOCUMENT ME!
     * @param  timestamp  DOCUMENT ME!
     */
    private void createFirstVersion(final int versionnr, final Timestamp timestamp) {
        final MetaClass versionMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME_VERSION,
                getConnectionContext());
        final CidsBean newVersionBean = versionMetaClass.getEmptyInstance(getConnectionContext()).getBean();
        try {
            newVersionBean.setProperty(FIELD__VERSIONNR, versionnr);
            newVersionBean.setProperty(FIELD__BEGINLIFESPANVERSION, timestamp);
            final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
            if ((geom_pos != null)
                        && ((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                final Coordinate geom_point = ((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getCoordinate();
                final Double point_x = geom_point.x;
                final Double point_y = geom_point.y;
                newVersionBean.setProperty(FIELD__POINT, point_x + " " + point_y);
            }
            if (cidsBean.getProperty(FIELD__URL) != null) {
                newVersionBean.setProperty(FIELD__WEBSITE, cidsBean.getProperty(FIELD__URL).toString());
            }
            if (cidsBean.getProperty(FIELD__TELEFEON) != null) {
                newVersionBean.setProperty(FIELD__TELEPHONEVOICE, cidsBean.getProperty(FIELD__TELEFEON).toString());
            }

            cidsBean = TableUtils.addBeanToCollection(cidsBean, FIELD__VERSION_KITA, newVersionBean);
        } catch (final Exception ex) {
            LOG.warn("inspireversion not created.", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void createNewVersion() {
        final Timestamp timestamp = new Timestamp(new Date().getTime());
        final CidsBean versionBean = getLastVersion();
        finishVersion(versionBean, timestamp);
        final int version = (int)versionBean.getProperty(FIELD__VERSIONNR);
        createFirstVersion(version + 1, timestamp);
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
                LOG.info("remove propchange str_adr_strasse: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange str_adr_str: " + this.cidsBean);
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
            testUrlAndShowResult();
            saveFirstAttributes();
            checkInspireID();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void saveFirstAttributes() {
        urlAttribute = setNotNull(cidsBean.getProperty(FIELD__URL));
        telAttribute = setNotNull(cidsBean.getProperty(FIELD__TELEFEON));
        onlineAttribute = setNotNull(cidsBean.getProperty(FIELD__ONLINE_STELLEN));
        geomAttribute = setNotNull(cidsBean.getProperty(FIELD__GEOREFERENZ));
        versionAttribute = cidsBean.getProperty(FIELD__VERSION_KITA);
    }
    /**
     * COALESCE.
     *
     * @param   notNullString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String setNotNull(final Object notNullString) {
        if (notNullString == null) {
            return "";
        }
        return notNullString.toString();
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            cbStunden.setEnabled(false);
            cbTraegertyp.setEnabled(false);
            cbAlter.setEnabled(false);
            chInklusion.setEnabled(false);
            chFamilienzentrzum.setEnabled(false);
            chOnline.setEnabled(false);
            txtAdresse.setEnabled(false);
            txtBemerkung.setEnabled(false);
            txtLeitung.setEnabled(false);
            txtName.setEnabled(false);
            jTextField1.setEnabled(false);
            txtPlaetze.setEnabled(false);
            txtPlz.setEnabled(false);
            jFormattedTextField1.setEnabled(false);
            txtUrl.setEnabled(false);
            lblGeom.setVisible(false);
        }
        jXHyperlink1.setVisible(!isEditor);
        txtUrl.setVisible(isEditor);
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
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
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            dlgChangeKitaName.dispose();
        }
    }

    @Override
    public String getTitle() {
        return cidsBean.toString();
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

        if (evt.getPropertyName().equals(FIELD__URL)) {
            testUrlAndShowResult();
        }
        if (evt.getPropertyName().equals(FIELD__NAME)) {
            if (cidsBean.getProperty(FIELD__INSPIRE_ID) != null) {
                StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(InfraKitaEditor.this),
                    dlgChangeKitaName,
                    true);
            }
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
    }
}
