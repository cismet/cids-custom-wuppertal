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

import Sirius.server.localserver.attribute.ObjectAttribute;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.StrAdrConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
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


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.FastBindableReferenceCombo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.MissingResourceException;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class StrAdrStrasseEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    
    public static final String GEOMTYPE = "Point";

    private static final Logger LOG = Logger.getLogger(StrAdrStrasseEditor.class);

    
    public static final String FIELD__GEO_FIELD = "geo_field";                          // geom
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field"; // str_adr_strasse_geom
    public static final String FIELD__GEOREFERENZ = "georeferenz";                      // str_adr_strasse
    public static final String FIELD__GEOREFERENZ_BBOX = "georeferenz_bbox";            // str_adr_strasse
    public static final String FIELD__GEOREFERENZ_BBOX__GEO_FIELD = "georeferenz_bbox.geo_field";
    public static final String FIELD__SCHLUESSEL = "schluessel";                        // str_adr_strasse
    public static final String FIELD__SCHLUESSEL_NAME = "schluessel.name";              // str_adr_strasse_schluessel
    public static final String FIELD__SCHLUESSEL_ID = "schluessel.id";                  // str_adr_strasse_schluessel
    public static final String FIELD__PLZ = "plz";                                      // str_adr_strasse
    public static final String FIELD__KMQUADRAT = "kmquadrat";                          // str_adr_strasse
    public static final String FIELD__STADTBEZIRK = "stadtbezirk";                      // str_adr_strasse
    public static final String FIELD__NAME = "name";                                    // str_adr_strasse
    public static final String FIELD__NAME_STRASSE = "str_adr_strasse.name";            // str_adr_strasse
    public static final String FIELD__MOTIV = "motiv";                                  // str_adr_strasse
    public static final String FIELD__MOTIV_NAME = "motiv.name";                        // str_adr_strasse_motiv
    public static final String FIELD__MOTIV_NUMMER = "motiv.nummer";                    // str_adr_strasse_motiv
    public static final String FIELD__ENTDAT = "entnenndat";                            // str_adr_strasse
    public static final String FIELD__ID = "id";                                        // str_adr_strasse
    public static final String FIELD__ID_STRASSE = "str_adr_strasse.id";                // str_adr_strasse
    public static final String FIELD__DATUM = "datum";                                  // str_adr_strasse
    public static final String FIELD__BEMERKUNG = "bemerkung";                          // str_adr_strasse
    public static final String FIELD__BESCHLUSS_B = "beschluss_b";                      // str_adr_strasse
    public static final String FIELD__BESCHLUSS_B_NAME = "beschluss_b.name";            // str_adr_strasse
    public static final String FIELD__BESCHLUSS_E = "beschluss_e";                      // str_adr_strasse
    public static final String FIELD__BESCHLUSS_E_NAME = "beschluss_e.name";            // str_adr_strasse
    public static final String FIELD__KMQ__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field";            // str_adr_strasse_kmquadrat
    public static final String FIELD__SBZ__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field";            // kst_stadtbezirk
    public static final String TABLE_NAME = "str_adr_strasse";
    public static final String TABLE__JOIN_SCHLUESSEL =" left join str_adr_strasse_schluessel on str_adr_strasse.schluessel = str_adr_strasse_schluessel.id";
    public static final String FIELD__JOIN_SCHLUESSEL_NAME = "str_adr_strasse_schluessel.name";            // str_adr_strasse_schluessel
    public static final String TABLE_GEOM = "geom";

    
    public static final String BUNDLE_PANE_PREFIX = "StrAdrStrasseEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "StrAdrStrasseEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "StrAdrStrasseEditor.prepareForSave().JOptionPane.title";
    
    public static final String BUNDLE_NONAME = "StrAdrStrasseEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "StrAdrStrasseEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_NOSTREET = "StrAdrStrasseEditor.prepareForSave().noStrasse";
    public static final String BUNDLE_DUPLICATESTREET = "StrAdrStrasseEditor.prepareForSave().duplicateStrasse";
    public static final String BUNDLE_FORBIDDENSTREET = "StrAdrStrasseEditor.prepareForSave().forbiddenStrasse";
    public static final String BUNDLE_ERRORSTREET = "StrAdrStrasseEditor.prepareForSave().errorStrasse";
    public static final String BUNDLE_ERRORDATE = "StrAdrStrasseEditor.prepareForSave().errorDate";
    public static final String BUNDLE_NOPOINT = "StrAdrStrasseEditor.prepareForSave().noPoint";
    public static final String BUNDLE_NORECTANGLE = "StrAdrStrasseEditor.prepareForSave().noRectangle";
    public static final String BUNDLE_LOCATION_GEOMETRY = "StrAdrStrasseEditor.prepareForSave().locationGeometry";
    public static final String BUNDLE_NOBESCHLUSSDAT = "StrAdrStrasseEditor.prepareForSave().noBeschlussdat";
    public static final String BUNDLE_NOBESCHLUSS = "StrAdrStrasseEditor.prepareForSave().noBeschluss";
    public static final String BUNDLE_WRONGBESCHLUSS = "StrAdrStrasseEditor.prepareForSave().wrongBeschluss";
    public static final String BUNDLE_NOENTNENN = "StrAdrStrasseEditor.prepareForSave().noEntnenn";
    public static final String BUNDLE_WRONGENTNENNDAT = "StrAdrStrasseEditor.prepareForSave().wrongEntnenndat";
    public static final String BUNDLE_WRONGENTNENN = "StrAdrStrasseEditor.prepareForSave().wrongEntnenn";
    public static final String BUNDLE_NOENTNENNDAT = "StrAdrStrasseEditor.prepareForSave().noEntnenndat";
    public static final String BUNDLE_NOMOTIV = "StrAdrStrasseEditor.prepareForSave().noMotiv";
    public static final String BUNDLE_MOTIV = "StrAdrStrasseEditor.prepareForSave().Motiv";
    public static final String BUNDLE_NOSTADTBEZIRK = "StrAdrStrasseEditor.prepareForSave().noStadtbezirk";
    public static final String BUNDLE_WRONGSTADTBEZIRK = "StrAdrStrasseEditor.prepareForSave().wrongStadtbezirk";
    public static final String BUNDLE_NOKMQUADRAT = "StrAdrStrasseEditor.prepareForSave().noKmQuadrat";
    public static final String BUNDLE_WRONGKMQUADRAT = "StrAdrStrasseEditor.prepareForSave().wrongKmQuadrat";

    public static final String BUNDLE_REMKMQ_QUESTION = "StrAdrStrasseEditor.btnRemoveKmQuadratActionPerformed().question";
    public static final String BUNDLE_REMKMQ_TITLE = "StrAdrStrasseEditor.btnRemoveKmQuadratActionPerformed().title";
    public static final String BUNDLE_REMKMQ_ERRORTITLE = "StrAdrStrasseEditor.btnRemoveKmQuadratActionPerformed().errortitle";
    public static final String BUNDLE_REMKMQ_ERRORTEXT =  "StrAdrStrasseEditor.btnRemoveKmQuadratActionPerformed().errortext";

    public static final String BUNDLE_REMPLZ_QUESTION =  "StrAdrStrasseEditor.btnRemovePlzActionPerformed().question";
    public static final String BUNDLE_REMPLZ_TITLE = "StrAdrStrasseEditor.btnRemovePlzActionPerformed().title";
    public static final String BUNDLE_REMPLZ_ERRORTITLE = "StrAdrStrasseEditor.btnRemovePlzActionPerformed().errortitle";
    public static final String BUNDLE_REMPLZ_ERRORTEXT = "StrAdrStrasseEditor.btnRemovePlzActionPerformed().errortext";
    
    public static final String BUNDLE_REMSBZ_QUESTION = "StrAdrStrasseEditor.btnRemoveStadtbezirkActionPerformed().question";
    public static final String BUNDLE_REMSBZ_TITLE = "StrAdrStrasseEditor.btnRemoveStadtbezirkActionPerformed().title";
    public static final String BUNDLE_REMSBZ_ERRORTITLE = "StrAdrStrasseEditor.btnRemoveStadtbezirkActionPerformed().errortitle";
    public static final String BUNDLE_REMSBZ_ERRORTEXT = "StrAdrStrasseEditor.btnRemoveStadtbezirkActionPerformed().errortext";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum OtherTableCases {

        //~ Enum constants -----------------------------------------------------

        REDUNDANT_ATT_NAME, REDUNDANT_ATT_KEY
    }
    
    private static enum AreaCases {

        //~ Enum constants -----------------------------------------------------

        SBZ, KMQ, BBOX
    }
    //~ Instance fields --------------------------------------------------------
    
    private SwingWorker worker_name;
    private SwingWorker worker_key;
    private SwingWorker worker_kmq;
    private SwingWorker worker_sbz;
    private SwingWorker worker_bbox;
    
    private Boolean redundantName = false;
    private Boolean redundantKey = false;
    private Boolean isPoint = false;
    private Boolean isBBox = false;
    private Boolean insideBBox = false;
    private Boolean insideKMQ = false;
    private Boolean insideSBZ = false;
    private Boolean isAmt = false;
    private Boolean isHist = false;
    
    private static String amtlStrGrenze = "04000";
    private static String titleNewStreet = "eine neue Straße anlegen..."; 

    private boolean isEditor = true;   
    
    private static Color colorAlarm = new java.awt.Color(255, 0, 0);            
    private static Color colorNormal = new java.awt.Color(0, 0, 0);
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddKmQuadrat;
    private JButton btnAddPlz;
    private JButton btnAddStadtbezirk;
    private JButton btnMenAbortKmQuadrat;
    private JButton btnMenAbortPlz;
    private JButton btnMenAbortStadtbezirk;
    private JButton btnMenOkKmQuadrat;
    private JButton btnMenOkPlz;
    private JButton btnMenOkStadtbezirk;
    private JButton btnRemoveKmQuadrat;
    private JButton btnRemovePlz;
    private JButton btnRemoveStadtbezirk;
    private JComboBox<String> cbBeschlussB;
    private JComboBox<String> cbBeschlussE;
    private JComboBox cbGeom;
    private JComboBox cbGeomBBox;
    private JComboBox cbKmQuadrat;
    private JComboBox<String> cbMotiv;
    private JComboBox cbPlz;
    private JComboBox cbSchluessel;
    private JComboBox cbStadtbezirk;
    private DefaultBindableDateChooser dcBenenndat;
    private DefaultBindableDateChooser dcEntnenndat;
    private JDialog dlgAddKmQuadrat;
    private JDialog dlgAddPlz;
    private JDialog dlgAddStadtbezirk;
    private Box.Filler filler1;
    private Box.Filler filler5;
    private Box.Filler filler6;
    private JLabel lblAuswaehlenKmQuadrat;
    private JLabel lblAuswaehlenPlz;
    private JLabel lblAuswaehlenStadtbezirk;
    private JLabel lblBemerkung;
    private JLabel lblBenenndat;
    private JLabel lblBeschlussB;
    private JLabel lblBeschlussE;
    private JLabel lblDatum;
    private JLabel lblEntnenndat;
    private JLabel lblGeom;
    private JLabel lblGeomBBox;
    private JLabel lblKarte;
    private JLabel lblKmQuadrat;
    private JLabel lblMotiv;
    private JLabel lblName;
    private JLabel lblPlz;
    private JLabel lblSchluessel;
    private JLabel lblStadtbezirk;
    private JLabel lblStrasse;
    private JList lstKmQuadrat;
    private JList lstPlz;
    private JList lstStadtbezirk;
    private JPanel panAddKmQuadrat;
    private JPanel panAddPlz;
    private JPanel panAddStadtbezirk;
    private JPanel panBemerkung;
    private JPanel panButtonsKmQuadrat;
    private JPanel panButtonsPlz;
    private JPanel panButtonsStadtbezirk;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panGeometrie;
    private JPanel panKmQuadrat;
    private JPanel panLage;
    private JPanel panMenButtonsKmQuadrat;
    private JPanel panMenButtonsPlz;
    private JPanel panMenButtonsStadtbezirk;
    private JPanel panMotiv;
    private JPanel panPlz;
    private DefaultPreviewMapPanel panPreviewMap;
    private JPanel panStadtbezirk;
    private RoundedPanel rpKarte;
    private JScrollPane scpBemerkung;
    private JScrollPane scpLstKmQuadrat;
    private JScrollPane scpLstPlz;
    private JScrollPane scpLstStadtbezirk;
    private JScrollPane scpMotiv;
    private SemiRoundedPanel semiRoundedPanel7;
    private SQLDateToStringConverter sqlDateToStringConverter;
    private JTextArea taBemerkung;
    private JTextArea taMotiv;
    private JTextField txtDatum;
    private JTextField txtName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StrAdrStrasseEditor object.
     *
     * 
     */
    public StrAdrStrasseEditor() {
    }

    /**
     * Creates a new StrAdrStrasseEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public StrAdrStrasseEditor(final boolean boolEditor) {
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
        
        dlgAddKmQuadrat.pack();
        dlgAddKmQuadrat.getRootPane().setDefaultButton(btnMenOkKmQuadrat);
        dlgAddStadtbezirk.pack();
        dlgAddStadtbezirk.getRootPane().setDefaultButton(btnMenOkStadtbezirk);
        
        if (isEditor) {
            ((DefaultBindableScrollableComboBox)this.cbMotiv).setNullable(true);
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
            ((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).setLocalRenderFeatureString(FIELD__GEOREFERENZ_BBOX);
        }
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

        dlgAddKmQuadrat = new JDialog();
        panAddKmQuadrat = new JPanel();
        lblAuswaehlenKmQuadrat = new JLabel();
        final MetaObject[] kmquadrat = ObjectRendererUtils.getLightweightMetaObjectsForTable("str_adr_strasse_kmquadrat", new String[]{"name"}, getConnectionContext());
        if(kmquadrat != null) {
            Arrays.sort(kmquadrat);
            cbKmQuadrat = new JComboBox(kmquadrat);
        }
        panMenButtonsKmQuadrat = new JPanel();
        btnMenAbortKmQuadrat = new JButton();
        btnMenOkKmQuadrat = new JButton();
        dlgAddPlz = new JDialog();
        panAddPlz = new JPanel();
        lblAuswaehlenPlz = new JLabel();
        final MetaObject[] plz = ObjectRendererUtils.getLightweightMetaObjectsForTable("str_adr_strasse_plz", new String[]{"name"}, getConnectionContext());
        if(plz != null) {
            Arrays.sort(plz);//||
            cbPlz = new JComboBox(plz);
        }
        panMenButtonsPlz = new JPanel();
        btnMenAbortPlz = new JButton();
        btnMenOkPlz = new JButton();
        dlgAddStadtbezirk = new JDialog();
        panAddStadtbezirk = new JPanel();
        lblAuswaehlenStadtbezirk = new JLabel();
        final MetaObject[] stadtbezirk = ObjectRendererUtils.getLightweightMetaObjectsForTable("kst_stadtbezirk", new String[]{"name"}, getConnectionContext());
        if(stadtbezirk != null) {
            Arrays.sort(stadtbezirk);
            cbStadtbezirk = new JComboBox(stadtbezirk);
            panMenButtonsStadtbezirk = new JPanel();
            btnMenAbortStadtbezirk = new JButton();
            btnMenOkStadtbezirk = new JButton();
            sqlDateToStringConverter = new SQLDateToStringConverter();
            panFillerUnten = new JPanel();
            panContent = new RoundedPanel();
            panFillerUnten1 = new JPanel();
            panDaten = new JPanel();
            lblName = new JLabel();
            txtName = new JTextField();
            lblSchluessel = new JLabel();
            lblStrasse = new JLabel();
            cbSchluessel =
            new FastBindableReferenceCombo(
                "select ss.id, s.schluessel, s.name, ss.name, coalesce(s.name, '--') || ' (' || ss.name || ')' as anzeige "
                + "from str_adr_strasse_schluessel ss left join str_adr_strasse s"
                + " on ss.id = s.schluessel"
                + " order by ss.name ",
                "%1$2s",
                new String [] {"anzeige","id"})
            ;
            lblGeom = new JLabel();
            if (isEditor){
                cbGeom = new DefaultCismapGeometryComboBoxEditor();
            }
            lblGeomBBox = new JLabel();
            if (isEditor){
                cbGeomBBox = new DefaultCismapGeometryComboBoxEditor();
            }
            lblBenenndat = new JLabel();
            dcBenenndat = new DefaultBindableDateChooser();
            lblBeschlussB = new JLabel();
            cbBeschlussB = new DefaultBindableScrollableComboBox();
            lblEntnenndat = new JLabel();
            dcEntnenndat = new DefaultBindableDateChooser();
            lblBeschlussE = new JLabel();
            cbBeschlussE = new DefaultBindableScrollableComboBox();
            lblMotiv = new JLabel();
            panMotiv = new JPanel();
            scpMotiv = new JScrollPane();
            taMotiv = new JTextArea();
            if(isEditor){
                cbMotiv = new DefaultBindableScrollableComboBox();
            }
            lblBemerkung = new JLabel();
            panBemerkung = new JPanel();
            scpBemerkung = new JScrollPane();
            taBemerkung = new JTextArea();
            lblPlz = new JLabel();
            panPlz = new JPanel();
            scpLstPlz = new JScrollPane();
            lstPlz = new JList();
            panButtonsPlz = new JPanel();
            btnAddPlz = new JButton();
            btnRemovePlz = new JButton();
            filler5 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
            lblKmQuadrat = new JLabel();
            panKmQuadrat = new JPanel();
            scpLstKmQuadrat = new JScrollPane();
            lstKmQuadrat = new JList();
            panButtonsKmQuadrat = new JPanel();
            btnAddKmQuadrat = new JButton();
            btnRemoveKmQuadrat = new JButton();
            filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
            lblStadtbezirk = new JLabel();
            panStadtbezirk = new JPanel();
            scpLstStadtbezirk = new JScrollPane();
            lstStadtbezirk = new JList();
            panButtonsStadtbezirk = new JPanel();
            btnAddStadtbezirk = new JButton();
            btnRemoveStadtbezirk = new JButton();
            filler6 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
            lblDatum = new JLabel();
            txtDatum = new JTextField();
            panGeometrie = new JPanel();
            panLage = new JPanel();
            rpKarte = new RoundedPanel();
            panPreviewMap = new DefaultPreviewMapPanel();
            semiRoundedPanel7 = new SemiRoundedPanel();
            lblKarte = new JLabel();

            dlgAddKmQuadrat.setTitle("Km-Quadrat");
            dlgAddKmQuadrat.setModal(true);

            panAddKmQuadrat.setLayout(new GridBagLayout());

            lblAuswaehlenKmQuadrat.setText("Bitte Kilometer-Quadrat auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddKmQuadrat.add(lblAuswaehlenKmQuadrat, gridBagConstraints);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddKmQuadrat.add(cbKmQuadrat, gridBagConstraints);

            panMenButtonsKmQuadrat.setLayout(new GridBagLayout());

            btnMenAbortKmQuadrat.setText("Abbrechen");
            btnMenAbortKmQuadrat.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenAbortKmQuadratActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsKmQuadrat.add(btnMenAbortKmQuadrat, gridBagConstraints);

            btnMenOkKmQuadrat.setText("Ok");
            btnMenOkKmQuadrat.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenOkKmQuadratActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsKmQuadrat.add(btnMenOkKmQuadrat, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddKmQuadrat.add(panMenButtonsKmQuadrat, gridBagConstraints);

            dlgAddKmQuadrat.getContentPane().add(panAddKmQuadrat, BorderLayout.CENTER);

            dlgAddPlz.setTitle("Postleitzahl");
            dlgAddPlz.setModal(true);

            panAddPlz.setLayout(new GridBagLayout());

            lblAuswaehlenPlz.setText("Bitte Postleitzahl auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddPlz.add(lblAuswaehlenPlz, gridBagConstraints);
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddPlz.add(cbPlz, gridBagConstraints);

            panMenButtonsPlz.setLayout(new GridBagLayout());

            btnMenAbortPlz.setText("Abbrechen");
            btnMenAbortPlz.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenAbortPlzActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsPlz.add(btnMenAbortPlz, gridBagConstraints);

            btnMenOkPlz.setText("Ok");
            btnMenOkPlz.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    btnMenOkPlzActionPerformed(evt);
                }
            });
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panMenButtonsPlz.add(btnMenOkPlz, gridBagConstraints);

            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
            panAddPlz.add(panMenButtonsPlz, gridBagConstraints);

            dlgAddPlz.getContentPane().add(panAddPlz, BorderLayout.CENTER);

            dlgAddStadtbezirk.setTitle("Stadtbezirk");
            dlgAddStadtbezirk.setModal(true);

            panAddStadtbezirk.setLayout(new GridBagLayout());

            lblAuswaehlenStadtbezirk.setText("Bitte Stadtbezirk auswählen:");
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.insets = new Insets(10, 10, 10, 10);
            panAddStadtbezirk.add(lblAuswaehlenStadtbezirk, gridBagConstraints);

        }
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddStadtbezirk.add(cbStadtbezirk, gridBagConstraints);

        panMenButtonsStadtbezirk.setLayout(new GridBagLayout());

        btnMenAbortStadtbezirk.setText("Abbrechen");
        btnMenAbortStadtbezirk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenAbortStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsStadtbezirk.add(btnMenAbortStadtbezirk, gridBagConstraints);

        btnMenOkStadtbezirk.setText("Ok");
        btnMenOkStadtbezirk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnMenOkStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsStadtbezirk.add(btnMenOkStadtbezirk, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddStadtbezirk.add(panMenButtonsStadtbezirk, gridBagConstraints);

        dlgAddStadtbezirk.getContentPane().add(panAddStadtbezirk, BorderLayout.CENTER);

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
        lblName.setText("Straße:");
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblSchluessel.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblSchluessel.setText("Schlüssel:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblSchluessel, gridBagConstraints);

        lblStrasse.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.schluessel.name}"), lblStrasse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(lblStrasse, gridBagConstraints);

        ((FastBindableReferenceCombo)cbSchluessel).setSorted(false);

        //((FastBindableReferenceCombo)cbSchluessel).setLocale(Locale.GERMAN);
        cbSchluessel.setFont(new Font("Dialog", 0, 12)); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.schluessel}"), cbSchluessel, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbSchluessel, gridBagConstraints);

        lblGeom.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeom, gridBagConstraints);

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
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeom, gridBagConstraints);
        }

        lblGeomBBox.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGeomBBox.setText("Umschreibendes Rechteck:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblGeomBBox, gridBagConstraints);

        if (isEditor){
            cbGeomBBox.setFont(new Font("Dialog", 0, 12)); // NOI18N

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.georeferenz_bbox}"), cbGeomBBox, BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).getConverter());
            bindingGroup.addBinding(binding);

        }
        if (isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbGeomBBox, gridBagConstraints);
        }

        lblBenenndat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBenenndat.setText("Datum der Benennung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBenenndat, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.benenndat}"), dcBenenndat, BeanProperty.create("date"));
        binding.setConverter(dcBenenndat.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcBenenndat, gridBagConstraints);

        lblBeschlussB.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschlussB.setText("Beschlussorgan der Benennung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschlussB, gridBagConstraints);

        cbBeschlussB.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbBeschlussB.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beschluss_b}"), cbBeschlussB, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbBeschlussB, gridBagConstraints);

        lblEntnenndat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblEntnenndat.setText("Datum der Entnennung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblEntnenndat, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.entnenndat}"), dcEntnenndat, BeanProperty.create("date"));
        binding.setConverter(dcEntnenndat.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(dcEntnenndat, gridBagConstraints);

        lblBeschlussE.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschlussE.setText("Beschlussorgan der Entnennung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBeschlussE, gridBagConstraints);

        cbBeschlussE.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbBeschlussE.setPreferredSize(new Dimension(100, 24));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.beschluss_e}"), cbBeschlussE, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(cbBeschlussE, gridBagConstraints);

        lblMotiv.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMotiv.setText("Motivgruppe:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMotiv, gridBagConstraints);

        panMotiv.setOpaque(false);
        panMotiv.setLayout(new GridBagLayout());

        taMotiv.setEditable(false);
        taMotiv.setLineWrap(true);
        taMotiv.setRows(2);
        taMotiv.setToolTipText("");
        taMotiv.setWrapStyleWord(true);
        scpMotiv.setViewportView(taMotiv);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panMotiv.add(scpMotiv, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panMotiv, gridBagConstraints);

        if(isEditor){
            cbMotiv.setFont(new Font("Dialog", 0, 12)); // NOI18N
            if(isEditor){
                cbMotiv.setPreferredSize(new Dimension(100, 24));
            }

            binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.motiv}"), cbMotiv, BeanProperty.create("selectedItem"));
            bindingGroup.addBinding(binding);

        }
        if(isEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 13;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panDaten.add(cbMotiv, gridBagConstraints);
        }

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
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
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(panBemerkung, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("Postleitzahl(en):");
        lblPlz.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        panPlz.setLayout(new GridBagLayout());

        scpLstPlz.setMinimumSize(new Dimension(258, 66));

        lstPlz.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstPlz.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstPlz.setVisibleRowCount(2);

        ELProperty eLProperty = ELProperty.create("${cidsBean.plz}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstPlz);
        bindingGroup.addBinding(jListBinding);

        scpLstPlz.setViewportView(lstPlz);

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
        panPlz.add(scpLstPlz, gridBagConstraints);

        panButtonsPlz.setOpaque(false);
        panButtonsPlz.setLayout(new GridBagLayout());

        btnAddPlz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddPlz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddPlzActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsPlz.add(btnAddPlz, gridBagConstraints);

        btnRemovePlz.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemovePlz.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemovePlzActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsPlz.add(btnRemovePlz, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsPlz.add(filler5, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panPlz.add(panButtonsPlz, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panPlz, gridBagConstraints);

        lblKmQuadrat.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKmQuadrat.setText("KM-Quadrat(e):");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblKmQuadrat, gridBagConstraints);

        panKmQuadrat.setLayout(new GridBagLayout());

        scpLstKmQuadrat.setMinimumSize(new Dimension(258, 66));

        lstKmQuadrat.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstKmQuadrat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstKmQuadrat.setVisibleRowCount(3);

        eLProperty = ELProperty.create("${cidsBean.kmquadrat}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstKmQuadrat);
        bindingGroup.addBinding(jListBinding);

        scpLstKmQuadrat.setViewportView(lstKmQuadrat);
        lstKmQuadrat.getAccessibleContext().setAccessibleName("");

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
        panKmQuadrat.add(scpLstKmQuadrat, gridBagConstraints);

        panButtonsKmQuadrat.setOpaque(false);
        panButtonsKmQuadrat.setLayout(new GridBagLayout());

        btnAddKmQuadrat.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddKmQuadrat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddKmQuadratActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsKmQuadrat.add(btnAddKmQuadrat, gridBagConstraints);

        btnRemoveKmQuadrat.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveKmQuadrat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveKmQuadratActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsKmQuadrat.add(btnRemoveKmQuadrat, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsKmQuadrat.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panKmQuadrat.add(panButtonsKmQuadrat, gridBagConstraints);
        panButtonsKmQuadrat.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panKmQuadrat, gridBagConstraints);

        lblStadtbezirk.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStadtbezirk.setText("Stadtbezirk(e):");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStadtbezirk, gridBagConstraints);

        panStadtbezirk.setLayout(new GridBagLayout());

        scpLstStadtbezirk.setMinimumSize(new Dimension(258, 66));

        lstStadtbezirk.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstStadtbezirk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstStadtbezirk.setVisibleRowCount(3);

        eLProperty = ELProperty.create("${cidsBean.stadtbezirk}");
        jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstStadtbezirk);
        bindingGroup.addBinding(jListBinding);

        scpLstStadtbezirk.setViewportView(lstStadtbezirk);

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
        panStadtbezirk.add(scpLstStadtbezirk, gridBagConstraints);

        panButtonsStadtbezirk.setOpaque(false);
        panButtonsStadtbezirk.setLayout(new GridBagLayout());

        btnAddStadtbezirk.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddStadtbezirk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsStadtbezirk.add(btnAddStadtbezirk, gridBagConstraints);

        btnRemoveStadtbezirk.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveStadtbezirk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panButtonsStadtbezirk.add(btnRemoveStadtbezirk, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtonsStadtbezirk.add(filler6, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 2, 2, 2);
        panStadtbezirk.add(panButtonsStadtbezirk, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panStadtbezirk, gridBagConstraints);

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatum.setText("Änderungsdatum:");
        lblDatum.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblDatum, gridBagConstraints);

        txtDatum.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum}"), txtDatum, BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtDatum, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 2, 5, 5);
        panContent.add(panDaten, gridBagConstraints);

        panGeometrie.setOpaque(false);
        panGeometrie.setLayout(new GridBagLayout());

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

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddKmQuadratActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddKmQuadratActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(StrAdrStrasseEditor.this), dlgAddKmQuadrat, true);
    }//GEN-LAST:event_btnAddKmQuadratActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveKmQuadratActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemoveKmQuadratActionPerformed
        final Object selection = lstKmQuadrat.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMKMQ_QUESTION),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMKMQ_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__KMQUADRAT, selection, false);
                    checkKMQ();
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMKMQ_ERRORTITLE,
                            BUNDLE_REMKMQ_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveKmQuadratActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortKmQuadratActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenAbortKmQuadratActionPerformed
        dlgAddKmQuadrat.setVisible(false);
    }//GEN-LAST:event_btnMenAbortKmQuadratActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkKmQuadratActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnMenOkKmQuadratActionPerformed
        try {
            final Object selItem = cbKmQuadrat.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__KMQUADRAT,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__KMQUADRAT);
                checkKMQ();
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddKmQuadrat.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkKmQuadratActionPerformed

    private void btnRemovePlzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemovePlzActionPerformed
        final Object selection = lstPlz.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMPLZ_QUESTION),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMPLZ_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__PLZ, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMPLZ_ERRORTITLE,
                            BUNDLE_REMPLZ_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemovePlzActionPerformed

    private void btnAddPlzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddPlzActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(StrAdrStrasseEditor.this), dlgAddPlz, true);
    }//GEN-LAST:event_btnAddPlzActionPerformed

    private void btnRemoveStadtbezirkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveStadtbezirkActionPerformed
        final Object selection = lstStadtbezirk.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMSBZ_QUESTION),
                    NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_REMSBZ_TITLE),
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    cidsBean = TableUtils.deleteItemFromList(cidsBean, FIELD__STADTBEZIRK, selection, false);
                    checkSBZ();
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                            BUNDLE_REMSBZ_ERRORTITLE,
                            BUNDLE_REMSBZ_ERRORTEXT,
                            null,
                            null,
                            ex,
                            Level.SEVERE,
                            null);
                    JXErrorPane.showDialog(this, ei);
                }
            }
        }
    }//GEN-LAST:event_btnRemoveStadtbezirkActionPerformed

    private void btnAddStadtbezirkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddStadtbezirkActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(StrAdrStrasseEditor.this), dlgAddStadtbezirk, true);
    }//GEN-LAST:event_btnAddStadtbezirkActionPerformed

    private void btnMenOkStadtbezirkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkStadtbezirkActionPerformed
        try {
            final Object selItem = cbStadtbezirk.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__STADTBEZIRK,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__STADTBEZIRK);
                checkSBZ();
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddStadtbezirk.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkStadtbezirkActionPerformed

    private void btnMenAbortStadtbezirkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortStadtbezirkActionPerformed
        dlgAddStadtbezirk.setVisible(false);
    }//GEN-LAST:event_btnMenAbortStadtbezirkActionPerformed

    private void btnMenOkPlzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenOkPlzActionPerformed
        try {
            final Object selItem = cbPlz.getSelectedItem();
            if (selItem instanceof MetaObject) {
                cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                        cidsBean,
                        FIELD__PLZ,
                        ((MetaObject)selItem).getBean());
                sortListNew(FIELD__PLZ);
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddPlz.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkPlzActionPerformed

    private void btnMenAbortPlzActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnMenAbortPlzActionPerformed
        dlgAddPlz.setVisible(false);
    }//GEN-LAST:event_btnMenAbortPlzActionPerformed

    private void setMotiv(){
        String motiv = "kein Motiv ausgewählt";
        if(null != cidsBean.getProperty(FIELD__MOTIV)){
            motiv = cidsBean.getProperty(FIELD__MOTIV_NUMMER).toString() + " " + cidsBean.getProperty(FIELD__MOTIV_NAME).toString();
        }
       
       taMotiv.setText(motiv);
    }
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
    private void checkName() {
        String sBiggerSmaller;
        if (cbSchluessel.getSelectedItem() != null){
            if(isAmt){
                sBiggerSmaller = " < ";
            } else {
                sBiggerSmaller = " > ";
            }
            // Worker Aufruf, ob das Objekt schon existiert
            valueFromOtherTable(
                TABLE_NAME,
                    TABLE__JOIN_SCHLUESSEL +
                " where "
                        + FIELD__NAME_STRASSE
                        + " ilike '"
                        + txtName.getText().trim()
                        + "' and "
                        + FIELD__ID_STRASSE
                        + " <> "
                        + cidsBean.getProperty(FIELD__ID)
                        + " and "
                        + FIELD__JOIN_SCHLUESSEL_NAME
                        + sBiggerSmaller
                        + " '"
                        + amtlStrGrenze
                        + "'",
                //FIELD__NAME,
                OtherTableCases.REDUNDANT_ATT_NAME);
        } else {
            redundantName = false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void checkKey() {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(
            TABLE_NAME,
            " where "
                    + FIELD__SCHLUESSEL
                    + " = "
                    + cbSchluessel.getSelectedIndex()
                    + " and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            //FIELD__SCHLUESSEL,
            OtherTableCases.REDUNDANT_ATT_KEY);
    }
      
        
    private void checkKMQ(){
        if ((!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)) && isAmt){
            try{
                if ((cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD) instanceof Geometry) && (cidsBean.getProperty(FIELD__KMQUADRAT) != null)){
                    final List<CidsBean> listKMQ = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__KMQUADRAT);
                    checkInsideArea(listKMQ, AreaCases.KMQ, FIELD__KMQ__GEOREFERENZ__GEO_FIELD);
                }
            } catch (final Exception ex) {
                LOG.warn("Error look for Coordinates KMQ.", ex);
            }
        }
    }
    
    
    private void checkSBZ(){
        if ((!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)) && isAmt){ 
            try{
                if ((cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD) instanceof Geometry) && (cidsBean.getProperty(FIELD__STADTBEZIRK) != null)){
                    final List<CidsBean> listSBZ = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__STADTBEZIRK);
                    checkInsideArea(listSBZ, AreaCases.SBZ, FIELD__SBZ__GEOREFERENZ__GEO_FIELD);

                }
            } catch (final Exception ex) {
                LOG.warn("Error look for Coordinates SBZ.", ex);
            }
        }
    }
    
    private void checkGeom(){
        if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
            if(isAmt){
                final CidsBean geom_pos = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ);
                isPoint = (geom_pos != null && ((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals(GEOMTYPE));
                final CidsBean geom_bbox = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX);               
                isBBox = (geom_bbox != null && ((Geometry)geom_bbox.getProperty(FIELD__GEO_FIELD)).isRectangle());
                if (isPoint && isBBox){
                    CidsBean bboxBean = (CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX);
                    List<CidsBean> listBBox = new ArrayList<>();
                    listBBox.add(bboxBean);
                    checkInsideArea(listBBox, AreaCases.BBOX, FIELD__GEO_FIELD);
                }
            }
        }
    }
    
   
    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
        //Bei historischen kann nur die Bemerkung geaendert werden.
        if (isHist){
            return true;
        }
        
        // Strassenname vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__NAME) == null || txtName.getText().trim().isEmpty() ){              
                    LOG.warn("No Streetname specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NONAME));
                }else {
                    if (redundantName) {
                        LOG.warn("Duplicate name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_DUPLICATENAME));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Strassenschlüssel vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__SCHLUESSEL) == null || cbSchluessel.getSelectedItem() == null ){              
                    LOG.warn("No Streetkey specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOSTREET));
                }else {
                    if (redundantKey) {
                        LOG.warn("Duplicate streetkey specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_DUPLICATESTREET));
                    } else{
                        if (Arrays.asList(StrAdrConfProperties.getInstance().getStringForbiddenKeys().split(",")).contains(cidsBean.getProperty(FIELD__SCHLUESSEL).toString())){
                            LOG.warn("Forbidden streetkey specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_FORBIDDENSTREET));
                        } 
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("street not given.", ex);
            save = false;
        }
              
        //Änderungsdatum setzen
        try {
            final CidsBean myCB = this.getCidsBean(); 
            boolean changeDate = false;
            // geändertes Objekt
            final ObjectAttribute[] objAtt = cidsBean.getMetaObject().getAttribs();
            for (final ObjectAttribute changedAtt : objAtt){
                if (!(changedAtt.getName().equals(FIELD__BEMERKUNG))){
                    if(changedAtt.isChanged()){
                        changeDate = true;
                    }
                }
            }
            if (changeDate || myCB.getMetaObject().getStatus() == MetaObject.NEW) {
                cidsBean.setProperty(FIELD__DATUM, new java.sql.Date(System.currentTimeMillis()));
            }
        } catch (final Exception ex) {
            LOG.warn("Could not save date of last change.", ex);
            save = false;
            LOG.warn("Error Date. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_ERRORDATE));
        }
        
        //georeferenz abfragen
        try {
            if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                if (isAmt){
                    if (!(isPoint)) {
                        //falsche Geometrie ausgewählt
                        LOG.warn("No 'point' for Geom specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOPOINT));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Could not save point coordinates of last change.", ex);
            save = false;
        }
        
        //georeferenz_bbox abfragen
        try {
            if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                if (isAmt){
                    if (!(isBBox)) {   
                        //falsche Geometrie ausgewählt
                        LOG.warn("No 'rectangle' for GeomBBox specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NORECTANGLE));    
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Error 'BBox' for Geom specified. Skip persisting.");
            save = false;
        }
        
        //Liegt der Mittelpunkt innerhalb der BBox
        try { 
            if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                if (isAmt){
                    if (!(insideBBox)){
                        LOG.warn("Wrong Geometry Location. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_LOCATION_GEOMETRY));
                    }
                }
            }
        }catch (final MissingResourceException ex) {
            LOG.warn("Could not compare coordinates.", ex);
            save = false;
        }
        
        // Benennungsdatum vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (dcBenenndat.getDate() == null){               
                    LOG.warn("No beschlussdat specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOBESCHLUSSDAT));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Beschlussdat not given.", ex);
            save = false;
        }
        
        // Benennung vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cbBeschlussB.getSelectedItem() == null){                
                    LOG.warn("No beschluss_b specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOBESCHLUSS));
                }else{
                    if (cidsBean.getProperty(FIELD__SCHLUESSEL) != null) {
                        if (isAmt){
                            if(!(Arrays.asList(StrAdrConfProperties.getInstance().getStringBenennGross().split(",")).contains(cidsBean.getProperty(FIELD__BESCHLUSS_B_NAME).toString()))){
                                LOG.warn("Wrong beschluss_b specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGBESCHLUSS));
                            }
                        } else{
                            if(!(Arrays.asList(StrAdrConfProperties.getInstance().getStringBenenn().split(",")).contains(cidsBean.getProperty(FIELD__BESCHLUSS_B_NAME).toString()))){
                                LOG.warn("Wrong beschluss_b specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGBESCHLUSS));
                            }
                        }
                    }
                } 
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Beschluss not given.", ex);
            save = false;
        }
        
        // Entnenndatum vorhanden
        try {
            if (isAmt){
                if (dcEntnenndat.getDate() != null){                
                    if (cbBeschlussE.getSelectedItem() == null){                  
                    LOG.warn("No beschluss_e specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOBESCHLUSS));
                    } else {
                        if(!(Arrays.asList(StrAdrConfProperties.getInstance().getStringEntnenn().split(",")).contains(cidsBean.getProperty(FIELD__BESCHLUSS_E_NAME).toString()))){
                            LOG.warn("No beschluss_e specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGENTNENN));
                        }
                    }
                    final LocalDate entdat = dcEntnenndat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    final LocalDate bedat = dcBenenndat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    if (entdat.isBefore(bedat)) {                    
                        LOG.warn("Wrong entnenndat specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGENTNENNDAT));
                    } 
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Entnenndat not given.", ex);
            save = false;
        }
        
         // Entnennung vorhanden
        try {
            if (isAmt){
                if (cbBeschlussE.getSelectedItem() != null){
                    if (dcEntnenndat.getDate() == null){                  
                        LOG.warn("No beschlussdat specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOENTNENNDAT));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Beschluss not given.", ex);
            save = false;
        }
        
        //Motiv vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                    if (isAmt){
                        if (cidsBean.getProperty(FIELD__MOTIV) == null || cbMotiv.getSelectedItem() == null ){              
                            LOG.warn("No motiv specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOMOTIV));
                        }
                    } else{
                        if (!(cidsBean.getProperty(FIELD__MOTIV) == null || cbMotiv.getSelectedItem() == null )){              
                            LOG.warn("Motiv specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_MOTIV));
                        }
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Motiv not given.", ex);
            save = false;
        }
        
        
        //Stadtbezirk ausgewählt
        try {
            if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                if (isAmt){
                    if (cidsBean.getProperty(FIELD__STADTBEZIRK) == null || cidsBean.getProperty(FIELD__STADTBEZIRK).toString().equals("[]")){              
                        LOG.warn("No sbz specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOSTADTBEZIRK));
                    }
                    if(!insideSBZ){
                        LOG.warn("Wrong sbz specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGSTADTBEZIRK));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Stadtbezirk not given.", ex);
            save = false;
        }
        
        
        //KmQuadrat ausgewählt
        try {
            if (!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)){
                if (isAmt){
                    if (cidsBean.getProperty(FIELD__KMQUADRAT) == null || cidsBean.getProperty(FIELD__KMQUADRAT).toString().equals("[]")){              
                        LOG.warn("No kmq specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_NOKMQUADRAT));
                    }
                    if(!insideKMQ){
                        LOG.warn("Wrong kmq specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_WRONGKMQUADRAT));
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("KmQuadrat not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(StrAdrStrasseEditor.class, BUNDLE_PANE_TITLE),
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
                LOG.info("remove propchange str_adr_strasse: " + this.cidsBean);
                this.cidsBean.removePropertyChangeListener(this);
            }
            if (!(isEditor && (cb == null))) {
                if (cb.getMetaObject().getStatus() != MetaObject.NEW) {
                    cbSchluessel.setVisible(false);
                }    
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor && (this.cidsBean != null)) {
                LOG.info("add propchange str_adr_strasse: " + this.cidsBean);
                this.cidsBean.addPropertyChangeListener(this);
            }
            // Damit die Stadtbezirke sortiert in der Liste erscheinen.
            final List<CidsBean> sbzCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__STADTBEZIRK);
            Collections.sort(sbzCol, AlphanumComparator.getInstance());
            
            // Damit die PLZs sortiert in der Liste erscheinen.
            final List<CidsBean> plzCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__PLZ);
            Collections.sort(plzCol, AlphanumComparator.getInstance());
            
            // Damit die KM-Quadrate sortiert in der Liste erscheinen.
            final List<CidsBean> kmqCol = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    FIELD__KMQUADRAT);
            Collections.sort(kmqCol, AlphanumComparator.getInstance());

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            setMapWindow();
            bindingGroup.bind();
            if (!(cidsBean.getProperty(FIELD__ENTDAT) == null)){
                isHist = true;
            } else{
                setAmtStr();
                checkSBZ(); 
                checkKMQ();
                checkGeom();
            }
            setReadOnly();
            setMotiv();
            
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.error("Bean not set.", ex);
        }
    }
    
    private void setAmtStr(){
        if ((!(cidsBean.getProperty(FIELD__SCHLUESSEL) == null)) && (cidsBean.getProperty(FIELD__SCHLUESSEL_NAME).toString().compareTo(amtlStrGrenze) < 0) && cidsBean.getProperty(FIELD__ENTDAT) == null){
            isAmt = true;
        } else {
            isAmt = false;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        RendererTools.makeReadOnly(txtDatum);
        RendererTools.makeReadOnly(lstPlz);
        panButtonsPlz.setVisible(false);
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(cbSchluessel);
            lblGeom.setVisible(isEditor);
            lblGeomBBox.setVisible(isEditor);
            RendererTools.makeReadOnly(dcBenenndat);
            RendererTools.makeReadOnly(cbBeschlussB); 
            RendererTools.makeReadOnly(dcEntnenndat);
            RendererTools.makeReadOnly(cbBeschlussE);
            RendererTools.makeReadOnly(cbMotiv);
            RendererTools.makeReadOnly(lstKmQuadrat);
            panButtonsKmQuadrat.setVisible(isEditor);
            RendererTools.makeReadOnly(lstStadtbezirk);
            panButtonsStadtbezirk.setVisible(isEditor);
            RendererTools.makeReadOnly(taBemerkung);
        } else {
           if(this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
                //Wird anstatt der ComBox angezeigt, also für neue nicht benötigt
                lblStrasse.setVisible(false);
                RendererTools.makeReadOnly(dcEntnenndat);
                RendererTools.makeReadOnly(cbBeschlussE); 
           }else{
                RendererTools.makeReadOnly(txtName);
                RendererTools.makeReadOnly(cbSchluessel);
                RendererTools.makeReadOnly(dcBenenndat);
                RendererTools.makeReadOnly(cbBeschlussB); 
                RendererTools.makeReadOnly(cbMotiv);
                cbMotiv.setVisible(false);
                if (cidsBean.getProperty(FIELD__ENTDAT)!= null){
                    RendererTools.makeReadOnly(cbGeom);
                    RendererTools.makeReadOnly(cbGeomBBox);
                    RendererTools.makeReadOnly(dcEntnenndat);
                    RendererTools.makeReadOnly(cbBeschlussE); 
                    RendererTools.makeReadOnly(dcEntnenndat);
                    RendererTools.makeReadOnly(cbBeschlussE);
                    RendererTools.makeReadOnly(lstKmQuadrat);
                    panButtonsKmQuadrat.setVisible(false);
                    RendererTools.makeReadOnly(lstStadtbezirk);
                    panButtonsStadtbezirk.setVisible(false);
                } else {
                    if (!isAmt){//setAmtStr muss in setCidsBean zuerst aufgerufen werden.
                        RendererTools.makeReadOnly(cbBeschlussE); 
                        RendererTools.makeReadOnly(dcEntnenndat);
                    }
                }
            }
        }
    }

    
    public boolean checkPointInsidePolygon(Point midpoint, Geometry geomArea){
        return midpoint.intersects(geomArea);
    }

    /**
     * DOCUMENT ME!
     */
    public void setMapWindow() {
        final CidsBean cb = this.getCidsBean();
        try {
            final Double bufferMeter = StrAdrConfProperties.getInstance().getBufferMeter();
            if (cb.getProperty(FIELD__GEOREFERENZ_BBOX) != null){
                    panPreviewMap.initMap(cb, FIELD__GEOREFERENZ_BBOX__GEO_FIELD);
            }else{
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
            }
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
            LOG.warn("Map window not set.", ex);
        }
    }

    @Override
    public String getTitle() {
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return titleNewStreet;
        } else {
            return cidsBean.toString();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        dlgAddKmQuadrat.dispose();
        dlgAddStadtbezirk.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).dispose();
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
            if (this.cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX) == null){
                setMapWindow();
            }
            setMapWindow();
            checkKMQ();
            checkSBZ();
            checkGeom();
        }
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ_BBOX)) {
            checkGeom();
            setMapWindow();
        }
        if (evt.getPropertyName().equals(FIELD__MOTIV)) {
            setMotiv();
        }
        if (evt.getPropertyName().equals(FIELD__SCHLUESSEL)) {
            setAmtStr();
            checkKey();
            checkName();
            checkSBZ();
            checkKMQ();
            checkGeom();
        }
        if (evt.getPropertyName().equals(FIELD__ENTDAT)) {
            setAmtStr();
        }
    }
   
    
     private void checkInsideArea(final List <CidsBean> areaList, final AreaCases fall, final String field){
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    for(final CidsBean laufBean:areaList){
                        if (checkPointInsidePolygon((Point)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD),(Geometry)laufBean.getProperty(field))){
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                protected void done() {
                    final Boolean check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            if (check) {
                                switch (fall) {
                                    case KMQ: {
                                        insideKMQ = true;
                                        break;
                                    }
                                    case SBZ: {
                                        insideSBZ = true;
                                    }
                                    case BBOX: {
                                        insideBBox = true;
                                    }
                                }
                            } else {
                                switch (fall) {
                                    case KMQ: {
                                        insideKMQ = false;
                                        break;    
                                    }
                                    case SBZ: {  
                                        insideSBZ = false;
                                        break;
                                    } 
                                    case BBOX: {
                                        insideBBox = false;
                                    }
                                }
                            }//else
                        }//isCancelled
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: check InsideArea.", e);
                    }//catch
                }//done
            };//worker
                
            switch (fall) {
                case KMQ: {
                    if (worker_kmq != null) {
                        worker_kmq.cancel(true);
                    }
                    worker_kmq = worker;
                    worker_kmq.execute();
                    break;
                }
                case SBZ: {
                    if (worker_sbz != null) {
                        worker_sbz.cancel(true);
                    }
                    worker_sbz = worker;
                    worker_sbz.execute();
                    break;
                }
                case BBOX: {
                    if (worker_bbox != null) {
                        worker_bbox.cancel(true);
                    }
                    worker_bbox = worker;
                    worker_bbox.execute();
                    break;
                }
            }
        
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
            final OtherTableCases fall) {
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
                                    case REDUNDANT_ATT_NAME: { // check redundant name
                                        redundantName = true;
                                        break;
                                    }
                                    case REDUNDANT_ATT_KEY: { // check redundant key
                                        redundantKey = true;
                                        lblSchluessel.setForeground(colorAlarm);
                                        break;
                                    }
                                }
                            } else {
                                switch (fall) {
                                    case REDUNDANT_ATT_NAME: { // check redundant name
                                        redundantName = false;
                                        break;
                                    }
                                    case REDUNDANT_ATT_KEY: { // check redundant key
                                        redundantKey = false;
                                        if (Arrays.asList(StrAdrConfProperties.getInstance().getStringForbiddenKeys().split(",")).contains(cidsBean.getProperty(FIELD__SCHLUESSEL).toString())){
                                            lblSchluessel.setForeground(colorAlarm);
                                        } else {
                                            lblSchluessel.setForeground(colorNormal);
                                        }
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
        switch (fall) {
                case REDUNDANT_ATT_NAME: {
                    if (worker_name != null) {
                        worker_name.cancel(true);
                    }
                    worker_name = worker;
                    worker_name.execute();
                    break;
                }
                case REDUNDANT_ATT_KEY: {
                    if (worker_key != null) {
                        worker_key.cancel(true);
                    }
                    worker_key = worker;
                    worker_key.execute();
                    break;
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
