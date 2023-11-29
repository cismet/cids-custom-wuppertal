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

import java.net.URL;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.*;


//import com.vividsolutions.jts.geom.PrecisionModel;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;

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
    SaveVetoable,
    BindingGroupStore,
    PropertyChangeListener{

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
    
    
    public static final String BUNDLE_NOKITA = "InfraKitaEditor.isOkForSaving().noNewKita";
    public static final String BUNDLE_NOGEOM = "InfraKitaEditor.isOkForSaving().noGeom";
    public static final String BUNDLE_WRONGGEOM = "InfraKitaEditor.isOkForSaving().wrongGeom";
    public static final String BUNDLE_PANE_PREFIX = "InfraKitaEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "InfraKitaEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "InfraKitaEditor.isOkForSaving().JOptionPane.title";

    public static final Coordinate RATHAUS_POINT = new Coordinate(374420, 5681660);


    //~ Instance fields --------------------------------------------------------

    private boolean isEditor = true;
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



    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private Box.Filler filler1;
    private Box.Filler filler2;
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
    private JPanel panAdresse;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panDatenZuordnung;
    private JPanel panFiller;
    private JPanel panFillerRechtsLage;
    private JPanel panLage;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panUnten;
    private JPanel panUrl;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private SemiRoundedPanel semiRoundedPanel7;
    private JSeparator sepOnline;
    JTextArea taBemerkung;
    private JTextField txtAdresse;
    private JTextField txtKurzname;
    private JTextField txtLeitung;
    private JTextField txtName;
    private JTextField txtPlaetze;
    private JTextField txtPlz;
    private JTextField txtStellvertretung;
    private JTextField txtTelefon;
    private JTextField txtTraeger;
    private JTextField txtTraegertyp;
    private JTextField txtU3;
    private JTextField txtUe3;
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
        panAdresse = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblKurzname = new JLabel();
        txtKurzname = new JTextField();
        lblGeom = new JLabel();
        if (isEditor){
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setAllowedGeometryTypes(new Class[] { Point.class});
        }
        lblAdresse = new JLabel();
        txtAdresse = new JTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblInspire = new JLabel();
        lblUrl = new JLabel();
        panUrl = new JPanel();
        jXHyperlink1 = new JXHyperlink();
        lblUrlCheck = new JLabel();
        panUnten = new JPanel();
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
        txtTelefon = new JTextField();
        lblFamilienzentrum = new JLabel();
        chFamilienzentrum = new JCheckBox();
        lblInklusion = new JLabel();
        chInklusion = new JCheckBox();
        txtPlaetze = new JTextField();
        txtU3 = new JTextField();
        txtUe3 = new JTextField();
        lblPlaetze = new JLabel();
        sepOnline = new JSeparator();
        lblOnline = new JLabel();
        chOnline = new JCheckBox();
        panDatenZuordnung = new JPanel();
        lbl25h = new JLabel();
        lbl35h = new JLabel();
        lbl45h = new JLabel();
        lbU2 = new JLabel();
        chU2_25 = new JCheckBox();
        chU2_35 = new JCheckBox();
        chU2_45 = new JCheckBox();
        chUe3_35 = new JCheckBox();
        chUe2_35 = new JCheckBox();
        chUe3_25 = new JCheckBox();
        chUe2_25 = new JCheckBox();
        chUe3_45 = new JCheckBox();
        chUe2_45 = new JCheckBox();
        filler1 = new Box.Filler(new Dimension(0, 5), new Dimension(0, 5), new Dimension(32767, 5));
        lbUe3h = new JLabel();
        lbU3 = new JLabel();
        lblUe3 = new JLabel();
        lblU3 = new JLabel();
        panFiller = new JPanel();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panLage = new JPanel();
        rpKarte = new RoundedPanel();
        panPreviewMap = new DefaultPreviewMapPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblKarte = new JLabel();
        panFillerRechtsLage = new JPanel();

        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panAdresse.setOpaque(false);
        panAdresse.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAdresse.add(lblName, gridBagConstraints);

        txtName.setToolTipText("");

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAdresse.add(txtName, gridBagConstraints);

        lblKurzname.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKurzname.setText("eindeutiger Name:");
        lblKurzname.setPreferredSize(new Dimension(120, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAdresse.add(lblKurzname, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.eindeutiger_kurzname}"), txtKurzname, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAdresse.add(txtKurzname, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 5);
        panAdresse.add(lblGeom, gridBagConstraints);

        if (isEditor){
            if (isEditor){
                cbGeom.setFont(new Font("Dialog", 0, 12)); // NOI18N
            }
            cbGeom.setPreferredSize(new Dimension(39, 27));

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.georeferenz}"), cbGeom, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 4;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panAdresse.add(cbGeom, gridBagConstraints);
        }

        lblAdresse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAdresse.setText("Adresse:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAdresse.add(lblAdresse, gridBagConstraints);

        txtAdresse.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.adresse}"), txtAdresse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAdresse.add(txtAdresse, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAdresse.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAdresse.add(scpBemerkung, gridBagConstraints);

        lblInspire.setIcon(inspired);
        lblInspire.setToolTipText("Der Datensatz ist inspired.");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panAdresse.add(lblInspire, gridBagConstraints);

        lblUrl.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUrl.setText("Homepage:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panAdresse.add(lblUrl, gridBagConstraints);

        panUrl.setOpaque(false);
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
        gridBagConstraints.insets = new Insets(2, 0, 2, 2);
        panUrl.add(jXHyperlink1, gridBagConstraints);

        lblUrlCheck.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAdresse.add(panUrl, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panAdresse, gridBagConstraints);

        panUnten.setOpaque(false);
        panUnten.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblTraegertyp.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTraegertyp.setText("Trägertyp:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtTraegertyp, gridBagConstraints);

        lblLeitung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblLeitung.setText("Leitung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblLeitung, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.leitung}"), txtLeitung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtLeitung, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plz}"), txtPlz, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtPlz, gridBagConstraints);

        lblTraeger.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTraeger.setText("Träger:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTraeger, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.traeger}"), txtTraeger, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtTraeger, gridBagConstraints);

        lblStellvertretung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStellvertretung.setText("Stellvertretung:");
        lblStellvertretung.setMinimumSize(new Dimension(120, 14));
        lblStellvertretung.setPreferredSize(new Dimension(120, 14));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStellvertretung, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.stellvertretung}"), txtStellvertretung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtStellvertretung, gridBagConstraints);

        lblTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblTelefon, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.telefon}"), txtTelefon, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtTelefon, gridBagConstraints);

        lblFamilienzentrum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFamilienzentrum.setText("Familienzentrum:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblFamilienzentrum, gridBagConstraints);

        chFamilienzentrum.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.familienzentrum}"), chFamilienzentrum, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chFamilienzentrum, gridBagConstraints);

        lblInklusion.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblInklusion.setText("Inklusion:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblInklusion, gridBagConstraints);

        chInklusion.setContentAreaFilled(false);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_fuer_behinderte}"), chInklusion, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chInklusion, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze}"), txtPlaetze, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtPlaetze, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_unter3}"), txtU3, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtU3, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plaetze_ueber3}"), txtUe3, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtUe3, gridBagConstraints);

        lblPlaetze.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlaetze.setText("Plätze:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPlaetze, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 0, 5, 0);
        panDaten.add(sepOnline, gridBagConstraints);

        lblOnline.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOnline.setText("Online:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOnline, gridBagConstraints);

        chOnline.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.online_stellen}"), chOnline, BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(chOnline, gridBagConstraints);

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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDatenZuordnung.add(chU2_45, gridBagConstraints);

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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDatenZuordnung.add(chUe3_25, gridBagConstraints);

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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDatenZuordnung.add(chUe2_45, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 20, 0, 20);
        panDatenZuordnung.add(filler1, gridBagConstraints);

        lbUe3h.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbUe3h.setText("über 3");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 20);
        panDatenZuordnung.add(lbUe3h, gridBagConstraints);

        lbU3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lbU3.setText("über 2");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 5, 2, 20);
        panDatenZuordnung.add(lbU3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panDatenZuordnung, gridBagConstraints);

        lblUe3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblUe3.setText("über 3:");
        lblUe3.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblUe3, gridBagConstraints);

        lblU3.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblU3.setText("unter 3:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblU3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panUnten.add(panDaten, gridBagConstraints);

        panFiller.setOpaque(false);

        GroupLayout panFillerLayout = new GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(panFillerLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panUnten.add(panFiller, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panUnten.add(filler2, gridBagConstraints);

        panLage.setName(""); // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 5);
        panUnten.add(panLage, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panContent.add(panUnten, gridBagConstraints);

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
        final Object o = getCidsBean().getProperty(FIELD__VERSION_KITA);
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
            final URL url = new URL(jXHyperlink1.getText());
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
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        boolean newVersion = false;
        
        if (cidsBean != null && cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
           save = false;
           LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        InfraKitaEditor.class,
                        BUNDLE_NOKITA));
        }
        // georeferenz muss gefüllt sein
        try {
            if (cidsBean.getProperty(FIELD__GEOREFERENZ) == null) {
                save = false;
                LOG.warn("No new Kita. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        InfraKitaEditor.class,
                        BUNDLE_NOGEOM));
            } else {
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                if (!((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")) {
                    save = false;
                    LOG.warn("Wrong geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(
                            InfraKitaEditor.class,
                            BUNDLE_WRONGGEOM));
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

        // Soll eine neue Version erstellt werden?
        if (newVersion && (versionAttribute != null)) {
            createNewVersion();
        }

        

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    InfraKitaEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            InfraKitaEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(InfraKitaEditor.class,
                    BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
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
            if (getLastVersion().getProperty(FIELD__ENDLIFESPANVERSION) != null){
                lblGeom.setVisible(false);
                if(isEditor){
                    cbGeom.setVisible(false);
                }
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void saveFirstAttributes() {
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
            RendererTools.makeReadOnly(txtTelefon);
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

    //~ Inner Classes ----------------------------------------------------------


}
