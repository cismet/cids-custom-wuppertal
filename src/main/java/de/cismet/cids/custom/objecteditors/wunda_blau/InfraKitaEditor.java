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
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;

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
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.net.MalformedURLException;
import java.util.MissingResourceException;

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
    private JComboBox cbGeom;
    private JCheckBox chFamilienzentrum;
    private JCheckBox chInklusion;
    private JCheckBox chOnline;
    private JCheckBox chU2_25;
    private JCheckBox chU2_35;
    private JCheckBox chU2_45;
    private JCheckBox chUe2_25;
    private JCheckBox chUe2_35;
    private JCheckBox chUe2_45;
    private JCheckBox chUe3_25;
    private JCheckBox chUe3_35;
    private JCheckBox chUe3_45;
    private JDialog dlgChangeKitaName;
    private Box.Filler filler1;
    private JFormattedTextField ftxtTelefon;
    private JXHyperlink jXHyperlink1;
    private JLabel lbU2;
    private JLabel lbU3;
    private JLabel lbUe3h;
    private JLabel lbl25h;
    private JLabel lbl35h;
    private JLabel lbl45h;
    private JLabel lblAdresse;
    private JLabel lblBemerkung;
    private JLabel lblFamilienzentrum;
    private JLabel lblGeom;
    private JLabel lblInklusion;
    private JLabel lblInspire;
    private JLabel lblInspireKita;
    private JLabel lblKarte;
    private JLabel lblKurzname;
    private JLabel lblLeitung;
    private JLabel lblName;
    private JLabel lblOnline;
    private JLabel lblPlaetze;
    private JLabel lblPlz;
    private JLabel lblStellvertretung;
    private JLabel lblTelefon;
    private JLabel lblTraeger;
    private JLabel lblTraegertyp;
    private JLabel lblU3;
    private JLabel lblUe3;
    private JLabel lblUrl;
    private JLabel lblUrlCheck;
    private JLabel lblWarningTextJa;
    private JLabel lblWarningTextNein;
    private JPanel panAdresse;
    private JPanel panChangeKitaName;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panDatenZuordnung;
    private JPanel panFiller;
    private JPanel panFillerRechtsLage;
    private JPanel panLage;
    private JPanel panMenButtonsName;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUrl;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private SemiRoundedPanel semiRoundedPanel7;
    private JSeparator sepOnline;
    JTextArea taBemerkung;
    private JTextArea taName;
    private JTextArea taNein;
    private JTextField txtAdresse;
    private JTextField txtKurzname;
    private JTextField txtLeitung;
    private JTextField txtName;
    private JTextField txtPlaetze;
    private JTextField txtPlz;
    private JTextField txtStellvertretung;
    private JTextField txtTraeger;
    private JTextField txtTraegertyp;
    private JTextField txtU3;
    private JTextField txtUe3;
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
        ftxtTelefon.setValue(telPatternFormatter.getLastValid());
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
        panContent = new RoundedPanel();
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panFillerRechtsLage = new JPanel();
        panAdresse = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblKurzname = new JLabel();
        txtKurzname = new JTextField();
        lblGeom = new JLabel();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblAdresse = new JLabel();
        txtAdresse = new JTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblInspire = new JLabel();
        panDaten = new JPanel();
        lblTraegertyp = new JLabel();
        txtTraegertyp = new JTextField();
        lblLeitung = new JLabel();
        txtLeitung = new JTextField();
        lblPlz = new JLabel();
        txtPlz = new JTextField();
        lblTraeger = new JLabel();
        txtTraeger = new JTextField();
        lblStellvertretung = new JLabel();
        txtStellvertretung = new JTextField();
        lblTelefon = new JLabel();
        ftxtTelefon = new JFormattedTextField(telPatternFormatter);
        lblUrl = new JLabel();
        panUrl = new JPanel();
        jXHyperlink1 = new JXHyperlink();
        txtUrl = new JTextField();
        lblUrlCheck = new JLabel();
        lblFamilienzentrum = new JLabel();
        chFamilienzentrum = new JCheckBox();
        lblInklusion = new JLabel();
        chInklusion = new JCheckBox();
        sepOnline = new JSeparator();
        lblOnline = new JLabel();
        chOnline = new JCheckBox();
        panFiller = new JPanel();
        panDatenZuordnung = new JPanel();
        lbl25h = new JLabel();
        lbl35h = new JLabel();
        lbl45h = new JLabel();
        lbU2 = new JLabel();
        chU2_25 = new JCheckBox();
        chU2_35 = new JCheckBox();
        chU2_45 = new JCheckBox();
        lblPlaetze = new JLabel();
        lbU3 = new JLabel();
        chUe3_35 = new JCheckBox();
        chUe2_35 = new JCheckBox();
        chUe3_25 = new JCheckBox();
        lbUe3h = new JLabel();
        chUe2_25 = new JCheckBox();
        chUe3_45 = new JCheckBox();
        chUe2_45 = new JCheckBox();
        txtPlaetze = new JTextField();
        lblU3 = new JLabel();
        txtU3 = new JTextField();
        lblUe3 = new JLabel();
        txtUe3 = new JTextField();
        filler1 = new Box.Filler(new Dimension(0, 5), new Dimension(0, 5), new Dimension(32767, 5));

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
            public void actionPerformed(ActionEvent evt) {
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

        lblInspireKita.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"))); // NOI18N
        lblInspireKita.setToolTipText("Warnung");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        panChangeKitaName.add(lblInspireKita, gridBagConstraints);

        dlgChangeKitaName.getContentPane().add(panChangeKitaName, BorderLayout.CENTER);

        setAutoscrolls(true);
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panContent.setMaximumSize(new Dimension(450, 2147483647));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        GroupLayout panFillerRechtsLageLayout = new GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(panFillerRechtsLageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerRechtsLageLayout.setVerticalGroup(panFillerRechtsLageLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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
        gridBagConstraints.weighty = 1.0;
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

        txtName.setToolTipText("");

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(txtName, gridBagConstraints);

        lblKurzname.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKurzname.setText("eindeutiger Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblKurzname, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.eindeutiger_kurzname}"), txtKurzname, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(txtKurzname, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblGeom, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.georeferenz}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 4, 2, 2);
            panAdresse.add(cbGeom, gridBagConstraints);
        }

        lblAdresse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAdresse.setText("Adresse:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panAdresse.add(lblAdresse, gridBagConstraints);

        txtAdresse.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.adresse}"), txtAdresse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(txtAdresse, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panAdresse.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panAdresse.add(scpBemerkung, gridBagConstraints);

        lblInspire.setIcon(inspired);
        lblInspire.setToolTipText("Der Datensatz ist inspired.");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panAdresse.add(lblInspire, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panAdresse, gridBagConstraints);

        panDaten.setLayout(new GridBagLayout());

        lblTraegertyp.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTraegertyp.setText("Trägertyp:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblTraegertyp, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.traegertyp.name}"), txtTraegertyp, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtTraegertyp, gridBagConstraints);

        lblLeitung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLeitung.setText("Leitung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblLeitung, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.leitung}"), txtLeitung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtLeitung, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plz}"), txtPlz, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtPlz, gridBagConstraints);

        lblTraeger.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTraeger.setText("Träger:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblTraeger, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.traeger}"), txtTraeger, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtTraeger, gridBagConstraints);

        lblStellvertretung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStellvertretung.setText("Stellvertretung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblStellvertretung, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stellvertretung}"), txtStellvertretung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(txtStellvertretung, gridBagConstraints);

        lblTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(ftxtTelefon, gridBagConstraints);

        lblUrl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("Homepage:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblUrl, gridBagConstraints);

        panUrl.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.url}"), jXHyperlink1, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
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

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.url}"), txtUrl, BeanProperty.create("text"));
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

        lblUrlCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDaten.add(panUrl, gridBagConstraints);

        lblFamilienzentrum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFamilienzentrum.setText("Familienzentrum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblFamilienzentrum, gridBagConstraints);

        chFamilienzentrum.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.familienzentrum}"), chFamilienzentrum, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(chFamilienzentrum, gridBagConstraints);

        lblInklusion.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblInklusion.setText("Inklusion:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblInklusion, gridBagConstraints);

        chInklusion.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_fuer_behinderte}"), chInklusion, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(chInklusion, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 0, 5, 0);
        panDaten.add(sepOnline, gridBagConstraints);

        lblOnline.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDaten.add(lblOnline, gridBagConstraints);

        chOnline.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.online_stellen}"), chOnline, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDaten.add(chOnline, gridBagConstraints);

        panFiller.setMinimumSize(new Dimension(20, 0));
        panFiller.setOpaque(false);
        panFiller.setPreferredSize(new Dimension(20, 0));

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 172, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panDaten.add(panFiller, gridBagConstraints);

        panDatenZuordnung.setOpaque(false);
        panDatenZuordnung.setLayout(new GridBagLayout());

        lbl25h.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbl25h.setText("25 Stunden");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(12, 4, 6, 10);
        panDatenZuordnung.add(lbl25h, gridBagConstraints);

        lbl35h.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbl35h.setText("35 Stunden");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(12, 4, 6, 10);
        panDatenZuordnung.add(lbl35h, gridBagConstraints);

        lbl45h.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbl45h.setText("45 Stunden");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(12, 4, 6, 10);
        panDatenZuordnung.add(lbl45h, gridBagConstraints);

        lbU2.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbU2.setText("unter 2");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 20);
        panDatenZuordnung.add(lbU2, gridBagConstraints);

        chU2_25.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.u2_25}"), chU2_25, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chU2_25, gridBagConstraints);

        chU2_35.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.u2_35}"), chU2_35, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chU2_35, gridBagConstraints);

        chU2_45.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.u2_45}"), chU2_45, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chU2_45, gridBagConstraints);

        lblPlaetze.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlaetze.setText("Plätze:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDatenZuordnung.add(lblPlaetze, gridBagConstraints);

        lbU3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbU3.setText("über 2");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 20);
        panDatenZuordnung.add(lbU3, gridBagConstraints);

        chUe3_35.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue3_35}"), chUe3_35, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe3_35, gridBagConstraints);

        chUe2_35.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue2_35}"), chUe2_35, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe2_35, gridBagConstraints);

        chUe3_25.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue3_25}"), chUe3_25, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe3_25, gridBagConstraints);

        lbUe3h.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbUe3h.setText("ueber 3");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 20);
        panDatenZuordnung.add(lbUe3h, gridBagConstraints);

        chUe2_25.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue2_25}"), chUe2_25, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe2_25, gridBagConstraints);

        chUe3_45.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue3_45}"), chUe3_45, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe3_45, gridBagConstraints);

        chUe2_45.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ue2_45}"), chUe2_45, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(chUe2_45, gridBagConstraints);

        txtPlaetze.setPreferredSize(new Dimension(40, 27));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze}"), txtPlaetze, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(txtPlaetze, gridBagConstraints);

        lblU3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblU3.setText("unter 3:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDatenZuordnung.add(lblU3, gridBagConstraints);

        txtU3.setPreferredSize(new Dimension(40, 27));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_unter3}"), txtU3, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(txtU3, gridBagConstraints);

        lblUe3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUe3.setText("über 3:");
        lblUe3.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panDatenZuordnung.add(lblUe3, gridBagConstraints);

        txtUe3.setPreferredSize(new Dimension(40, 27));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_ueber3}"), txtUe3, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panDatenZuordnung.add(txtUe3, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 20, 0, 20);
        panDatenZuordnung.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDaten.add(panDatenZuordnung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
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
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkNameActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkNameActionPerformed
        dlgChangeKitaName.setVisible(false);
    }//GEN-LAST:event_btnMenOkNameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtTelefonFocusLost(final FocusEvent evt) {//GEN-FIRST:event_ftxtTelefonFocusLost
        refreshValidTel();
    }//GEN-LAST:event_ftxtTelefonFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtTelefonActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_ftxtTelefonActionPerformed
        refreshValidTel();
    }//GEN-LAST:event_ftxtTelefonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
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
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

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
        } catch (final MalformedURLException e) {
            lblUrlCheck.setIcon(statusFalsch);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkURL(final URL url) {
        return WebAccessManager.getInstance().checkIfURLaccessible(url);
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
        } catch (final MissingResourceException ex) {
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
        } catch (final MissingResourceException ex) {
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
        } catch (final MissingResourceException ex) {
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
        } catch (final MissingResourceException ex) {
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
            RendererTools.makeReadOnly(txtAdresse);
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtKurzname);
            RendererTools.makeReadOnly(txtTraegertyp);
            RendererTools.makeReadOnly(txtTraeger);
            RendererTools.makeReadOnly(txtLeitung);
            RendererTools.makeReadOnly(txtStellvertretung);
            RendererTools.makeReadOnly(txtPlaetze);
            RendererTools.makeReadOnly(txtU3);
            RendererTools.makeReadOnly(txtUe3);
            RendererTools.makeReadOnly(txtPlz);
            RendererTools.makeReadOnly(ftxtTelefon);
            RendererTools.makeReadOnly(txtUrl);
            RendererTools.makeReadOnly(chInklusion);
            RendererTools.makeReadOnly(chFamilienzentrum);
            RendererTools.makeReadOnly(chU2_25);
            RendererTools.makeReadOnly(chU2_35);
            RendererTools.makeReadOnly(chU2_45);
            RendererTools.makeReadOnly(chUe2_25);
            RendererTools.makeReadOnly(chUe2_35);
            RendererTools.makeReadOnly(chUe2_45);
            RendererTools.makeReadOnly(chUe3_25);
            RendererTools.makeReadOnly(chUe3_35);
            RendererTools.makeReadOnly(chUe3_45);
            RendererTools.makeReadOnly(chOnline);
        if (!(isEditor)) { 
            lblGeom.setVisible(false);   
            lblBemerkung.setVisible(false);
            scpBemerkung.setVisible(false);
            taBemerkung.setVisible(false);          
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
