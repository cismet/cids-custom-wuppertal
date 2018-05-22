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
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import javax.swing.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.tools.gui.RoundedPanel;

import de.cismet.tools.gui.StaticSwingTools;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static java.lang.Thread.sleep;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class StrAdrStrasseEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener, 
    RequestsFullSizeComponent{

    @Override
    public void initWithConnectionContext(ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext); //To change body of generated methods, choose Tools | Templates.
        makeIt();
    }

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(StrAdrStrasseEditor.class);

    public static final String FIELD__GEOREFERENZ = "georeferenz";                     
    public static final String FIELD__GEOREFERENZ__GEO_FIELD = "georeferenz.geo_field"; 
    public static final String FIELD__GEOREFERENZ_BBOX = "georeferenz_bbox";
    public static final String FIELD__GEOREFERENZ_BBOX__GEO_FIELD = "georeferenz_bbox.geo_field";
    public static final String FIELD__NAME = "name";
    public static final String FIELD__SCHLUESSEL = "schluessel";
    public static final String FIELD__SCHLUESSEL__NAME = "schluessel.name";
    public static final String FIELD__LAGE_BIS = "lage_bis";
    public static final String FIELD__LAGE_VON = "lage_von";
    public static final String FIELD__STADTBEZIRK = "stadtbezirk";
    public static final String FIELD__KMQUADRAT = "kmquadrat";
    public static final String FIELD__STRASSE = "strasse";
    public static final String FIELD__DATUM = "datum";
    public static final String FIELD__RWERT_BSA = "rwert_bsa";
    public static final String FIELD__HWERT_BSA = "hwert_bsa";
    public static final String FIELD__GEO_FIELD = "geo_field";
    public static final String FIELD__BOUNDING_BOX_X1 = "bounding_box_x1";
    public static final String FIELD__BOUNDING_BOX_X2 = "bounding_box_x2";
    public static final String FIELD__BOUNDING_BOX_Y1 = "bounding_box_y1";
    public static final String FIELD__BOUNDING_BOX_Y2 = "bounding_box_y2";
    public static final String FIELD__ENTNENNDAT = "entnenndat";
    public static final String FIELD__MOTIV = "motiv";
    public static final String TABLE_STR_ADR_STRASSE = "str_adr_strasse";
    public static final String TABLE_GEOM = "geom";
    //~ Instance fields --------------------------------------------------------

    
    private CidsBean cidsBean = null;
    private boolean isEditor = true;
    private boolean newObject = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddKmQuadrat;
    private javax.swing.JButton btnAddStadtbezirk;
    private javax.swing.JButton btnMenAbort;
    private javax.swing.JButton btnMenAbortKm;
    private javax.swing.JButton btnMenOk;
    private javax.swing.JButton btnMenOkKm;
    private javax.swing.JButton btnRemoveKmQuadrat;
    private javax.swing.JButton btnRemoveStadtbezirk;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbBeschlussB;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbBeschlussE;
    private javax.swing.JComboBox cbGeom;
    private javax.swing.JComboBox cbGeomBBox;
    private javax.swing.JComboBox cbKmQuadrat;
    private javax.swing.JComboBox<String> cbMotiv;
    private javax.swing.JComboBox cbSchluessel;
    private javax.swing.JComboBox cbStadtbezirk;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBenenndat;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcEntnenndat;
    private javax.swing.JDialog dlgAddKmQuadrat;
    private javax.swing.JDialog dlgAddStadtbezirk;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel lblAuswaehlen;
    private javax.swing.JLabel lblAuswaehlenKm;
    private javax.swing.JLabel lblBenenndat;
    private javax.swing.JLabel lblBeschlussB;
    private javax.swing.JLabel lblBeschlussE;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblEntnenndat;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblGeomBBox;
    private javax.swing.JLabel lblKmQuadrat;
    private javax.swing.JLabel lblLageBis;
    private javax.swing.JLabel lblLageVon;
    private javax.swing.JLabel lblMotiv;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPlz;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JLabel lblStadtbezirk;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblStrasseBis;
    private javax.swing.JLabel lblStrasseVon;
    private javax.swing.JList lstKmQuadrat;
    private javax.swing.JList lstPlz;
    private javax.swing.JList lstStadtbezirk;
    private javax.swing.JPanel panAddKmQuadrat;
    private javax.swing.JPanel panAddStadtbezirk;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panButtonsKm;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panFillerRechtsLage;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUnten1;
    private javax.swing.JPanel panLage;
    private javax.swing.JPanel panMenButtons;
    private javax.swing.JPanel panMenButtonsKm;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private de.cismet.tools.gui.RoundedPanel rpKarte;
    private javax.swing.JScrollPane scpLstKmQuadrat;
    private javax.swing.JScrollPane scpLstPlz;
    private javax.swing.JScrollPane scpLstStadtbezirk;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter sqlDateToStringConverter;
    private javax.swing.JTextField txtDatum;
    private javax.swing.JTextField txtLageBis;
    private javax.swing.JTextField txtLageVon;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public StrAdrStrasseEditor() {
        /*initComponents();
        dlgAddStadtbezirk.pack();
        dlgAddKmQuadrat.pack();
        dlgAddStadtbezirk.getRootPane().setDefaultButton(btnMenOk);
        dlgAddKmQuadrat.getRootPane().setDefaultButton(btnMenOkKm);
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString("georeferenz");
        ((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).setLocalRenderFeatureString("georeferenz_bbox");*/
       // makeIt();
    }

    /**
     * Creates a new StrAdrStrasseEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public StrAdrStrasseEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
        //makeIt();
    }
    
    public void makeIt(){
        initComponents();
        dlgAddStadtbezirk.pack();
        dlgAddKmQuadrat.pack();
        dlgAddStadtbezirk.getRootPane().setDefaultButton(btnMenOk);
        dlgAddKmQuadrat.getRootPane().setDefaultButton(btnMenOkKm);
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString(FIELD__GEOREFERENZ);
        ((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).setLocalRenderFeatureString(FIELD__GEOREFERENZ_BBOX);
        //setReadOnly();
    }
    

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        dlgAddStadtbezirk = new javax.swing.JDialog();
        panAddStadtbezirk = new javax.swing.JPanel();
        lblAuswaehlen = new javax.swing.JLabel();
        final MetaObject[] stadtbezirk = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils.getLightweightMetaObjectsForTable("kst_stadtbezirk", new String[]{"name"}, getConnectionContext());
        if(stadtbezirk != null) {
            Arrays.sort(stadtbezirk);
            cbStadtbezirk = new javax.swing.JComboBox(stadtbezirk)
            //new javax.swing.JComboBox()
            ;
            panMenButtons = new javax.swing.JPanel();
            btnMenAbort = new javax.swing.JButton();
            btnMenOk = new javax.swing.JButton();
            dlgAddKmQuadrat = new javax.swing.JDialog();
            panAddKmQuadrat = new javax.swing.JPanel();
            lblAuswaehlenKm = new javax.swing.JLabel();
            final MetaObject[] kmquadrat = de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils.getLightweightMetaObjectsForTable("str_adr_strasse_kmquadrat", new String[]{"name"}, getConnectionContext());
            if(kmquadrat != null) {
                Arrays.sort(kmquadrat);
                cbKmQuadrat = new javax.swing.JComboBox(kmquadrat);
                panMenButtonsKm = new javax.swing.JPanel();
                btnMenAbortKm = new javax.swing.JButton();
                btnMenOkKm = new javax.swing.JButton();
                sqlDateToStringConverter = new de.cismet.cids.custom.objectrenderer.converter.SQLDateToStringConverter();
                panFillerUnten = new javax.swing.JPanel();
                panContent = new RoundedPanel();
                lblStadtbezirk = new javax.swing.JLabel();
                scpLstStadtbezirk = new javax.swing.JScrollPane();
                lstStadtbezirk = new javax.swing.JList();
                panButtons = new javax.swing.JPanel();
                btnAddStadtbezirk = new javax.swing.JButton();
                btnRemoveStadtbezirk = new javax.swing.JButton();
                lblStrasseVon = new javax.swing.JLabel();
                txtLageVon = new javax.swing.JTextField();
                lblLageVon = new javax.swing.JLabel();
                txtLageBis = new javax.swing.JTextField();
                lblLageBis = new javax.swing.JLabel();
                lblStrasseBis = new javax.swing.JLabel();
                scpLstKmQuadrat = new javax.swing.JScrollPane();
                lstKmQuadrat = new javax.swing.JList();
                lblKmQuadrat = new javax.swing.JLabel();
                panButtonsKm = new javax.swing.JPanel();
                btnAddKmQuadrat = new javax.swing.JButton();
                btnRemoveKmQuadrat = new javax.swing.JButton();
                scpLstPlz = new javax.swing.JScrollPane();
                lstPlz = new javax.swing.JList();
                lblPlz = new javax.swing.JLabel();
                txtDatum = new javax.swing.JTextField();
                lblName = new javax.swing.JLabel();
                cbSchluessel =
                new FastBindableReferenceCombo(
                    "select ss.id, s.schluessel, s.name, s.strasse, coalesce(s.name, '--') || ' (' || ss.name || ')' as anzeige "
                    + "from str_adr_strasse_schluessel ss left join str_adr_strasse s"
                    + " on ss.name = s.strasse"
                    + " order by ss.name ",
                    "%1$2s",
                    new String [] {"anzeige","id"})
                ;
                lblGeom = new javax.swing.JLabel();
                cbGeom = new DefaultCismapGeometryComboBoxEditor();
                cbBeschlussE = new DefaultBindableScrollableComboBox() ;
                cbGeomBBox = new DefaultCismapGeometryComboBoxEditor();
                lblStrasse = new javax.swing.JLabel();
                panFillerUnten1 = new javax.swing.JPanel();
                txtName = new javax.swing.JTextField();
                lblSchluessel = new javax.swing.JLabel();
                lblGeomBBox = new javax.swing.JLabel();
                cbBeschlussB = new DefaultBindableScrollableComboBox() ;
                dcBenenndat = new de.cismet.cids.editors.DefaultBindableDateChooser();
                dcEntnenndat = new de.cismet.cids.editors.DefaultBindableDateChooser();
                lblBenenndat = new javax.swing.JLabel();
                lblBeschlussB = new javax.swing.JLabel();
                lblEntnenndat = new javax.swing.JLabel();
                lblBeschlussE = new javax.swing.JLabel();
                lblMotiv = new javax.swing.JLabel();
                lblDatum = new javax.swing.JLabel();
                cbMotiv = new DefaultBindableScrollableComboBox();
                panLage = new javax.swing.JPanel();
                rpKarte = new de.cismet.tools.gui.RoundedPanel();
                panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
                semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
                jLabel17 = new javax.swing.JLabel();
                panFillerRechtsLage = new javax.swing.JPanel();

                dlgAddStadtbezirk.setModal(true);

                panAddStadtbezirk.setMaximumSize(new java.awt.Dimension(200, 120));
                panAddStadtbezirk.setMinimumSize(new java.awt.Dimension(200, 120));
                panAddStadtbezirk.setPreferredSize(new java.awt.Dimension(200, 120));
                panAddStadtbezirk.setLayout(new java.awt.GridBagLayout());

                lblAuswaehlen.setText("Bitte Thema auswählen:");
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
                panAddStadtbezirk.add(lblAuswaehlen, gridBagConstraints);

            }
            cbStadtbezirk.setMaximumSize(new java.awt.Dimension(120, 20));
            cbStadtbezirk.setMinimumSize(new java.awt.Dimension(120, 20));
            cbStadtbezirk.setPreferredSize(new java.awt.Dimension(120, 20));
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(5, 18, 5, 18);
            panAddStadtbezirk.add(cbStadtbezirk, gridBagConstraints);

            panMenButtons.setLayout(new java.awt.GridBagLayout());

            btnMenAbort.setText("Abbrechen");
            btnMenAbort.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnMenAbortActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtons.add(btnMenAbort, gridBagConstraints);

            btnMenOk.setText("Ok");
            btnMenOk.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnMenOkActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panMenButtons.add(btnMenOk, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
            panAddStadtbezirk.add(panMenButtons, gridBagConstraints);

            dlgAddStadtbezirk.getContentPane().add(panAddStadtbezirk, java.awt.BorderLayout.CENTER);

            dlgAddKmQuadrat.setModal(true);

            panAddKmQuadrat.setMaximumSize(new java.awt.Dimension(180, 120));
            panAddKmQuadrat.setMinimumSize(new java.awt.Dimension(180, 120));
            panAddKmQuadrat.setPreferredSize(new java.awt.Dimension(180, 120));
            panAddKmQuadrat.setLayout(new java.awt.GridBagLayout());

            lblAuswaehlenKm.setText("Bitte Thema auswählen:");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
            panAddKmQuadrat.add(lblAuswaehlenKm, gridBagConstraints);

        }
        cbKmQuadrat.setMaximumSize(new java.awt.Dimension(100, 20));
        cbKmQuadrat.setMinimumSize(new java.awt.Dimension(100, 20));
        cbKmQuadrat.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddKmQuadrat.add(cbKmQuadrat, gridBagConstraints);

        panMenButtonsKm.setLayout(new java.awt.GridBagLayout());

        btnMenAbortKm.setText("Abbrechen");
        btnMenAbortKm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenAbortKmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtonsKm.add(btnMenAbortKm, gridBagConstraints);

        btnMenOkKm.setText("Ok");
        btnMenOkKm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenOkKmActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtonsKm.add(btnMenOkKm, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddKmQuadrat.add(panMenButtonsKm, gridBagConstraints);

        dlgAddKmQuadrat.getContentPane().add(panAddKmQuadrat, java.awt.BorderLayout.CENTER);

        setAutoscrolls(true);
        setMinimumSize(new java.awt.Dimension(600, 646));
        setPreferredSize(new java.awt.Dimension(600, 737));
        setLayout(new java.awt.GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        javax.swing.GroupLayout panFillerUntenLayout = new javax.swing.GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 5.0E-4;
        add(panFillerUnten, gridBagConstraints);

        panContent.setMaximumSize(new java.awt.Dimension(450, 2147483647));
        panContent.setMinimumSize(new java.awt.Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new java.awt.Dimension(450, 961));
        panContent.setLayout(new java.awt.GridBagLayout());

        lblStadtbezirk.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblStadtbezirk.setText("Stadtbezirk(e):");
        lblStadtbezirk.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblStadtbezirk, gridBagConstraints);

        scpLstStadtbezirk.setName(""); // NOI18N

        lstStadtbezirk.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lstStadtbezirk.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stadtbezirk}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstStadtbezirk);
        bindingGroup.addBinding(jListBinding);

        scpLstStadtbezirk.setViewportView(lstStadtbezirk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(scpLstStadtbezirk, gridBagConstraints);

        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        btnAddStadtbezirk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddStadtbezirk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(btnAddStadtbezirk, gridBagConstraints);

        btnRemoveStadtbezirk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveStadtbezirk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveStadtbezirkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(btnRemoveStadtbezirk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(panButtons, gridBagConstraints);

        lblStrasseVon.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblStrasseVon.setText("von Strasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblStrasseVon, gridBagConstraints);

        txtLageVon.setMinimumSize(new java.awt.Dimension(50, 19));
        txtLageVon.setPreferredSize(new java.awt.Dimension(50, 19));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lage_von}"), txtLageVon, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(txtLageVon, gridBagConstraints);

        lblLageVon.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLageVon.setText("Straße am Anfang:");
        lblLageVon.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblLageVon, gridBagConstraints);

        txtLageBis.setMinimumSize(new java.awt.Dimension(50, 19));
        txtLageBis.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lage_bis}"), txtLageBis, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(txtLageBis, gridBagConstraints);

        lblLageBis.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLageBis.setText("Straße am Ende:");
        lblLageBis.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblLageBis, gridBagConstraints);

        lblStrasseBis.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblStrasseBis.setText("bis Strasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblStrasseBis, gridBagConstraints);

        lstKmQuadrat.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kmquadrat}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstKmQuadrat);
        bindingGroup.addBinding(jListBinding);

        scpLstKmQuadrat.setViewportView(lstKmQuadrat);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(scpLstKmQuadrat, gridBagConstraints);

        lblKmQuadrat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblKmQuadrat.setText("KM-Quadrat(e):");
        lblKmQuadrat.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblKmQuadrat, gridBagConstraints);

        panButtonsKm.setOpaque(false);
        panButtonsKm.setLayout(new java.awt.GridBagLayout());

        btnAddKmQuadrat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddKmQuadrat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddKmQuadratActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtonsKm.add(btnAddKmQuadrat, gridBagConstraints);

        btnRemoveKmQuadrat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveKmQuadrat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveKmQuadratActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtonsKm.add(btnRemoveKmQuadrat, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panContent.add(panButtonsKm, gridBagConstraints);

        scpLstPlz.setName(""); // NOI18N

        lstPlz.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lstPlz.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.plz}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstPlz);
        bindingGroup.addBinding(jListBinding);

        scpLstPlz.setViewportView(lstPlz);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(scpLstPlz, gridBagConstraints);

        lblPlz.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("Postleitzahl(en):");
        lblPlz.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblPlz, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum}"), txtDatum, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(sqlDateToStringConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(txtDatum, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblName, gridBagConstraints);

        ((FastBindableReferenceCombo)cbSchluessel).setSorted(false);

        //((FastBindableReferenceCombo)cbSchluessel).setLocale(Locale.GERMAN);
        cbSchluessel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"), cbSchluessel, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbSchluessel, gridBagConstraints);

        lblGeom.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeom.setText("Geometrie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblGeom, gridBagConstraints);

        cbGeom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"), cbGeom, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbGeom, gridBagConstraints);

        cbBeschlussE.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschluss_e}"), cbBeschlussE, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbBeschlussE, gridBagConstraints);

        cbGeomBBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz_bbox}"), cbGeomBBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbGeomBBox, gridBagConstraints);

        lblStrasse.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lblStrasse.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"), lblStrasse, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblStrasse, gridBagConstraints);

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        javax.swing.GroupLayout panFillerUnten1Layout = new javax.swing.GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 5.0E-4;
        panContent.add(panFillerUnten1, gridBagConstraints);

        txtName.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"), txtName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(txtName, gridBagConstraints);

        lblSchluessel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSchluessel.setText("Schlüssel:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblSchluessel, gridBagConstraints);

        lblGeomBBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGeomBBox.setText("Umschreibendes Rechteck:");
        lblGeomBBox.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblGeomBBox, gridBagConstraints);

        cbBeschlussB.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschluss_b}"), cbBeschlussB, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbBeschlussB, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.benenndat}"), dcBenenndat, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcBenenndat.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(dcBenenndat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entnenndat}"), dcEntnenndat, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcEntnenndat.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(dcEntnenndat, gridBagConstraints);

        lblBenenndat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBenenndat.setText("Datum der Benennung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblBenenndat, gridBagConstraints);

        lblBeschlussB.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBeschlussB.setText("Beschlussorgan der Benennung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblBeschlussB, gridBagConstraints);

        lblEntnenndat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEntnenndat.setText("Datum der Entnennung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblEntnenndat, gridBagConstraints);

        lblBeschlussE.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBeschlussE.setText("Beschlussorgan der Entnennung:");
        lblBeschlussE.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblBeschlussE, gridBagConstraints);

        lblMotiv.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMotiv.setText("Motivgruppe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblMotiv, gridBagConstraints);

        lblDatum.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDatum.setText("Änderungsdatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(lblDatum, gridBagConstraints);

        cbMotiv.setMaximumSize(new java.awt.Dimension(150, 32767));
        cbMotiv.setPreferredSize(new java.awt.Dimension(150, 24));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.motiv}"), cbMotiv, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panContent.add(cbMotiv, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        panLage.setMinimumSize(new java.awt.Dimension(300, 142));
        panLage.setOpaque(false);
        panLage.setLayout(new java.awt.GridBagLayout());

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new java.awt.GridBagLayout());

        panPreviewMap.setMinimumSize(new java.awt.Dimension(600, 600));
        panPreviewMap.setPreferredSize(new java.awt.Dimension(500, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        rpKarte.add(panPreviewMap, gridBagConstraints);

        semiRoundedPanel7.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Lage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        panLage.add(rpKarte, gridBagConstraints);

        panFillerRechtsLage.setName(""); // NOI18N
        panFillerRechtsLage.setOpaque(false);

        javax.swing.GroupLayout panFillerRechtsLageLayout = new javax.swing.GroupLayout(panFillerRechtsLage);
        panFillerRechtsLage.setLayout(panFillerRechtsLageLayout);
        panFillerRechtsLageLayout.setHorizontalGroup(
            panFillerRechtsLageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerRechtsLageLayout.setVerticalGroup(
            panFillerRechtsLageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panLage.add(panFillerRechtsLage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        add(panLage, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddStadtbezirkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStadtbezirkActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(StrAdrStrasseEditor.this),
            dlgAddStadtbezirk,
            true);
    }//GEN-LAST:event_btnAddStadtbezirkActionPerformed

    private void btnRemoveStadtbezirkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveStadtbezirkActionPerformed
        final Object selection = lstStadtbezirk.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                StaticSwingTools.getParentFrame(this),
                "Soll der Stadtbezirk wirklich gelöscht werden?",
                "Stadtbezirk entfernen",
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    deleteItemFromList(FIELD__STADTBEZIRK, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        "Fehler beim Löschen",
                        "Beim Löschen des Stadtbezirks ist ein Fehler aufgetreten",
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

    private void btnMenAbortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbortActionPerformed
        dlgAddStadtbezirk.setVisible(false);
    }//GEN-LAST:event_btnMenAbortActionPerformed

    private void btnMenOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOkActionPerformed
        try {
            final Object selItem = cbStadtbezirk.getSelectedItem();
            if (selItem instanceof MetaObject) {
                addBeanToCollection(FIELD__STADTBEZIRK, ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddStadtbezirk.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkActionPerformed

    private void btnAddKmQuadratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddKmQuadratActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(StrAdrStrasseEditor.this),
            dlgAddKmQuadrat,
            true);
    }//GEN-LAST:event_btnAddKmQuadratActionPerformed

    private void btnRemoveKmQuadratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveKmQuadratActionPerformed
        final Object selection = lstKmQuadrat.getSelectedValue();
        if (selection != null) {
            final int answer = JOptionPane.showConfirmDialog(
                StaticSwingTools.getParentFrame(this),
                "Soll das Km-Quadrat wirklich gelöscht werden?",
                "Km-Quadrat entfernen",
                JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    deleteItemFromList(FIELD__KMQUADRAT, selection, false);
                } catch (Exception ex) {
                    final ErrorInfo ei = new ErrorInfo(
                        "Fehler beim Löschen",
                        "Beim Löschen des Km-Quadrats ist ein Fehler aufgetreten",
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

    private void btnMenAbortKmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbortKmActionPerformed
        dlgAddKmQuadrat.setVisible(false);
    }//GEN-LAST:event_btnMenAbortKmActionPerformed

    private void btnMenOkKmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOkKmActionPerformed
        try {
            final Object selItem = cbKmQuadrat.getSelectedItem();
            if (selItem instanceof MetaObject) {
                addBeanToCollection(FIELD__KMQUADRAT, ((MetaObject)selItem).getBean());
            }
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddKmQuadrat.setVisible(false);
        }
    }//GEN-LAST:event_btnMenOkKmActionPerformed

    
    
    private String getStreetNeighbour(String strasse){     
        String sName = ""; 
        final CidsBean myCB = this.getCidsBean();
        if (myCB.getProperty(strasse) != null){  
            final String sVonBis = myCB.getProperty(strasse).toString();
            if (sVonBis.trim().length()== 5){
                try {
                    if (sVonBis.length() > 0){
                        switch (sVonBis){
                            case "05000":
                                sName = "keine kreuzende/abgehende Strasse";
                                break;
                            case "05999":
                                sName = "keine kreuzende/abgehende Strasse";
                                break;
                            case "07999":
                                sName = "hier ist die Stadtgrenze";
                                break;
                            default:
                                //JOptionPane.showMessageDialog(this, "getstreetneiggreater");
                                final String myWhere = " where strasse ilike '" + sVonBis + "'";
                                sName = TableUtils.getOtherTableValue(TABLE_STR_ADR_STRASSE, myWhere, getConnectionContext()).getProperty(FIELD__NAME).toString();
                        } 
                    }
                } catch (Exception e) {
                    LOG.warn("Could not get street. ", e);
                }
            }
        }     
        return sName;
    }
   /**
     * DOCUMENT ME!
     */
    private void checkEdit() {
        try {
            final CidsBean myCB = this.getCidsBean();
            if (myCB.getProperty("kein_edit") != null) {
                final String sEdit = myCB.getProperty("kein_edit").toString();
                // if (sEdit == "true"){
                if ("true".equalsIgnoreCase(sEdit)) {
                    noEdit();
                }
            }
        } catch (Exception e) {
            LOG.warn("Could not determine cidsBeans id. ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  propName     DOCUMENT ME!
     * @param  newTypeBean  DOCUMENT ME!
     */
    private void addBeanToCollection(final String propName, final CidsBean newTypeBean) {
        if ((newTypeBean != null) && (propName != null)) {
            final Object o = cidsBean.getProperty(propName);
            if (o instanceof Collection) {
                try {
                    final Collection<CidsBean> col = (Collection)o;
                    for (final CidsBean bean : col) {
                        if (newTypeBean.equals(bean)) {
                            LOG.info("Bean " + newTypeBean + " already present in " + propName + "!");
                            return;
                        }
                    }
                    col.add(newTypeBean);
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }
    }
   

    /**
     * DOCUMENT ME!
     */
    private void noEdit() {
        if (this.cidsBean.getMetaObject().getStatus() != MetaObject.NEW) {
            lblStrasse.setText("");
        }else{
            //panContent.remove(cbSchluessel);
        }
        
        // Geom nur im Editor
        if (this.isEditor) {
           // cbGeom.setEnabled(false);
        } else {
            // Bemerkung darf im Editor immer geändert werden im Renderer nicht.
           // txtBemerkung.setEnabled(false);
        }
    }




    /**
     * DOCUMENT ME!
     *
     * @param   controlValue   DOCUMENT ME!
     * @param   welchesObject  DOCUMENT ME!
     * @param   fehler         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StringBuilder noSelectedItem(final Object controlValue, final String welchesObject, final String fehler) {
        final StringBuilder errorMessage = new StringBuilder();

        if (controlValue == null) {
            LOG.warn("No '" + welchesObject + "' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave()."
                            + fehler));
        }
        return errorMessage;
    }

  

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();
JOptionPane.showMessageDialog(this, "prepareforsave");
        // Strassenname vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__NAME) == null || txtName.getText().trim().isEmpty() ){              
                    LOG.warn("No Streetname specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noName"));
                }else {
                    final String myWhere = " where name ilike '" + cidsBean.getProperty(FIELD__NAME).toString() + "'";       
                    final CidsBean mybean = TableUtils.getOtherTableValue(TABLE_STR_ADR_STRASSE, myWhere, getConnectionContext());      
                    if (mybean != null){  
                        LOG.warn("Wrong Streetname specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().wrongName"));
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // Strassenschlüssel vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__STRASSE) == null){
                    if (cidsBean.getProperty(FIELD__SCHLUESSEL) == null){
                        LOG.warn("No Streetkey specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                        "StrAdrStrasseEditor.prepareForSave().noSchluessel"));
                    }else{
                        //Straßenschlüssel schon vergeben?
                        if (!(checkSchluessel())){
                            LOG.warn("Wrong Streetkey specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                        "StrAdrStrasseEditor.prepareForSave().wrongSchluessel"));
                        } else{
                            try {
                                //übertragen des ausgewählten ausgewählten schlüssels in den strassenschlüssel
                                final String myStrasse = cbSchluessel.getSelectedItem().toString();
                                final String myIdStrasse = myStrasse.substring(myStrasse.length()-5);
                                if (myIdStrasse != null) {
                                    cidsBean.setProperty(FIELD__STRASSE, myIdStrasse);
                                }else{
                                    LOG.warn("Error Streetkey. Skip persisting.");
                                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                                "StrAdrStrasseEditor.prepareForSave().errorSchluessel"));
                                }
                            } catch (final Exception ex) {
                                LOG.warn("Could not save key.", ex);
                                save = false;
                            }
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("street not given.", ex);
            save = false;
        }
     
        //Änderungsdatum setzen
        try {
            final CidsBean myCB = this.getCidsBean();
            // neues bzw. geändertes Objekt
            if (myCB.getMetaObject().getStatus() == MetaObject.MODIFIED || myCB.getMetaObject().getStatus() == MetaObject.NEW) {
                cidsBean.setProperty(FIELD__DATUM, new java.sql.Date(System.currentTimeMillis()));
            }
        } catch (final Exception ex) {
            LOG.warn("Could not save date of last change.", ex);
            save = false;
             LOG.warn("Error Date. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                        "StrAdrStrasseEditor.prepareForSave().errorDate"));
        }
        
        //georeferenz abfragen und abspeichern
        try {
            final CidsBean myCB = this.getCidsBean();
            // neues bzw. geändertes Objekt
            //if (myCB.getMetaObject().getStatus() == MetaObject.MODIFIED || myCB.getMetaObject().getStatus() == MetaObject.NEW) {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                final CidsBean geom_pos = (CidsBean)myCB.getProperty(FIELD__GEOREFERENZ);
                if (geom_pos != null && ((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals( "Point")){
                    Coordinate geom_point = ((Geometry)geom_pos.getProperty(FIELD__GEO_FIELD)).getCoordinate();
                    Double point_x = geom_point.x;
                    Double point_y = geom_point.y;
                    try{
                        //cidsBean.setProperty("rwert", point_x);
                       // cidsBean.setProperty("hwert", point_y);
                        cidsBean.setProperty(FIELD__RWERT_BSA,(int) point_x.longValue());
                        cidsBean.setProperty(FIELD__HWERT_BSA,((int) point_y.doubleValue()));
                    } catch (Exception e) {
                        LOG.fatal("Problem during setting the point geom", e);
                        LOG.warn("Error 'point' for Geom specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                "StrAdrStrasseEditor.prepareForSave().errorPoint"));
                    }
                }else{
                    //falsche Geometrie ausgewählt
                    LOG.warn("No 'point' for Geom specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave().noPoint"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Could not save point coordinates of last change.", ex);
            save = false;
        }
        
        //georeferenz_bbox abfragen und abspeichern
        try {
            final CidsBean myCB = this.getCidsBean();
            // neues bzw. geändertes Objekt
            //if (myCB.getMetaObject().getStatus() == MetaObject.MODIFIED || myCB.getMetaObject().getStatus() == MetaObject.NEW) {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                final CidsBean geom_bbox = (CidsBean)myCB.getProperty(FIELD__GEOREFERENZ_BBOX);               
                if (geom_bbox != null && ((Geometry)geom_bbox.getProperty(FIELD__GEO_FIELD)).isRectangle()){
                    Envelope env_bbox = ((Geometry)geom_bbox.getProperty(FIELD__GEO_FIELD)).getEnvelopeInternal();
                    double max_x = env_bbox.getMaxX();
                    double max_y = env_bbox.getMaxY();
                    double min_x = env_bbox.getMinX();
                    double min_y = env_bbox.getMinY();
                    try{
                        //ohne Math.round(Math.pow(10.0, 3), wird der Wert nicht gespeichert
                        cidsBean.setProperty(FIELD__BOUNDING_BOX_X2, Math.round(Math.pow(10.0, 3) * max_x) / Math.pow(10.0, 3));
                        cidsBean.setProperty(FIELD__BOUNDING_BOX_Y2, Math.round(Math.pow(10.0, 3) * max_y) / Math.pow(10.0, 3));
                        cidsBean.setProperty(FIELD__BOUNDING_BOX_X1, Math.round(Math.pow(10.0, 3) * min_x) / Math.pow(10.0, 3));
                        cidsBean.setProperty(FIELD__BOUNDING_BOX_Y1, Math.round(Math.pow(10.0, 3) * min_y) / Math.pow(10.0, 3));
                    } catch (Exception e) {
                        LOG.fatal("Problem during setting the bbox geom", e);
                    }
                }else{
                    //falsche Geometrie ausgewählt
                    LOG.warn("No 'rectangle' for GeomBBox specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave().noRectangle"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Could not save bbox coordinates of last change.", ex);
            LOG.warn("Error 'BBox' for Geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave().errorBBox"));
            save = false;
        }
        
        //Liegt der Mittelpunkt innerhalb der BBox
        try { 
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (((CidsBean)cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX) != null) && ((Geometry)(cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX__GEO_FIELD))).isRectangle()){            
                    if ((cidsBean.getProperty(FIELD__GEOREFERENZ)) != null && ((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD)).getGeometryType().equals( "Point")){                  
                        if (!(((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD)).intersects((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX__GEO_FIELD)))){                       
                            LOG.warn("Wrong Geometry Location. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                            "StrAdrStrasseEditor.prepareForSave().locationGeometry"));
                        }
                    }
                }
            }
        }catch (final Exception ex) {
            LOG.warn("Could not compare coordinates.", ex);
            save = false;
        }
       
        // Beschlussdatum vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (dcBenenndat.getDate() == null){               
                    LOG.warn("No beschlussdat specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noBeschlussdat"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Beschlussdat not given.", ex);
            save = false;
        }
        // Beschluss vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cbBeschlussB.getSelectedItem() == null){                
                    LOG.warn("No beschluss_b specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noBeschluss"));
                }else{
                    if (!((cbBeschlussB.getSelectedItem().equals("Rat")) || (cbBeschlussB.getSelectedItem().equals("BV")))){
                        LOG.warn("No beschluss_b specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                        "StrAdrStrasseEditor.prepareForSave().wrongBeschluss"));
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Beschluss not given.", ex);
            save = false;
        }
        // Entnenndatum vorhanden
        try {
            if (dcEntnenndat.getDate() != null){                
                if (cbBeschlussE.getSelectedItem() == null){                  
                LOG.warn("No beschluss_e specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                "StrAdrStrasseEditor.prepareForSave().noEntnenn"));
                }
                final LocalDate entdat = dcEntnenndat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                final LocalDate bedat = dcBenenndat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (entdat.isBefore(bedat)) {                    
                    LOG.warn("Wrong entnenndat specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                            "StrAdrStrasseEditor.prepareForSave().wrongEntnenndat"));
                } 
            }
        } catch (final Exception ex) {
            LOG.warn("Entnenndat not given.", ex);
            save = false;
        }
        // Entnennung vorhanden
        try {
            if (cbBeschlussE.getSelectedItem() != null){
                if (dcEntnenndat.getDate() == null){                  
                    LOG.warn("No beschlussdat specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noEntnenndat"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Beschluss not given.", ex);
            save = false;
        }
        
        //lage_von gesetzt
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__LAGE_VON) == null || txtLageVon.getText().trim().isEmpty() ){              
                    LOG.warn("No lage von specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noLageVon"));
                }else{              
                    if((getStreetNeighbour(FIELD__LAGE_VON)).isEmpty()){
                        LOG.warn("wrong lage von specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().wrongLageVon"));
                    }else{
                        if ((!(cidsBean.getProperty(FIELD__LAGE_VON).toString().equals("05000"))) && (!(cidsBean.getProperty(FIELD__LAGE_VON).toString().equals("07999"))) ) {
                            if (!(checkLage(cidsBean.getProperty(FIELD__LAGE_VON).toString()))){
                                LOG.warn("wrong lage von specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                            "StrAdrStrasseEditor.prepareForSave().locationLageVon"));
                            }   
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("lage von not given.", ex);
            save = false;
        }
        //lage_bis gesetzt
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__LAGE_BIS) == null || txtLageBis.getText().trim().isEmpty() ){             
                    LOG.warn("No lage von specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noLageBis"));
                }else{
                    if((getStreetNeighbour(FIELD__LAGE_BIS)).isEmpty()){
                        LOG.warn("wrong lage bis specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().wrongLageBis"));
                    }else{
                        if ((!(cidsBean.getProperty(FIELD__LAGE_BIS).toString().equals("059999"))) && (!(cidsBean.getProperty(FIELD__LAGE_BIS).toString().equals("07999"))) ) {
                            if (!(checkLage(cidsBean.getProperty(FIELD__LAGE_BIS).toString()))){
                                LOG.warn("wrong lage bis specified. Skip persisting.");
                                    errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                            "StrAdrStrasseEditor.prepareForSave().locationLageBis"));
                            }
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("lage bis not given.", ex);
            save = false;
        }
        
        //Motiv vorhanden
        try {
            if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty("motiv") == null || cbMotiv.getSelectedItem() == null ){              
                    LOG.warn("No motiv specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noMotiv"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Motiv not given.", ex);
            save = false;
        }
        
        //Stadtbezirk ausgewählt
        try {
           if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__STADTBEZIRK) == null || cidsBean.getProperty(FIELD__STADTBEZIRK).toString().equals("[]")){              
                    LOG.warn("No motiv specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noStadtbezirk"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Stadtbezirk not given.", ex);
            save = false;
        }
        
        //KmQuadrat ausgewählt
        try {
           if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                if (cidsBean.getProperty(FIELD__KMQUADRAT) == null || cidsBean.getProperty(FIELD__KMQUADRAT).toString().equals("[]")){              
                    LOG.warn("No motiv specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(StrAdrStrasseEditor.class,
                                    "StrAdrStrasseEditor.prepareForSave().noKmQuadrat"));
                }
            }
        } catch (final Exception ex) {
            LOG.warn("KmQuadrat not given.", ex);
            save = false;
        }
        
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(StrAdrStrasseEditor.class,
                            "StrAdrStrasseEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(StrAdrStrasseEditor.class,
                    "StrAdrStrasseEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  propertyName           DOCUMENT ME!
     * @param  value                  DOCUMENT ME!
     * @param  andDeleteItemFromList  DOCUMENT ME!
     */
    private void deleteItemFromList(final String propertyName,
            final Object value,
            final boolean andDeleteObjectFromDB) {
        if ((value instanceof CidsBean) && (propertyName != null)) {
            final CidsBean bean = (CidsBean)value;
            if (andDeleteObjectFromDB) {
                try {
                    bean.delete();
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            } else {
                final Object coll = cidsBean.getProperty(propertyName);
                if (coll instanceof Collection) {
                    ((Collection)coll).remove(bean);
                }
            }
        }
    }
    
    
    private boolean checkLage(final String schluessel){
        Object geoObj;
        final String myWhere = " where strasse ilike '" + schluessel + "'";       
        final CidsBean mybean = TableUtils.getOtherTableValue(TABLE_STR_ADR_STRASSE, myWhere, getConnectionContext());      
         if (mybean != null){
             //Welche Geometrie ist beim aktiven Objekt hinterlegt (BBOX, Point oder nichts(false))
            if ((cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX__GEO_FIELD) instanceof Geometry) && (((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX__GEO_FIELD)).isRectangle())){ 
                geoObj = cidsBean.getProperty(FIELD__GEOREFERENZ_BBOX__GEO_FIELD);              
            }else{
                if ((cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD) instanceof Geometry) &&(((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD)).getGeometryType().equals("Point"))){
                    geoObj = cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD);                  
                }else{
                    return false;
                }
            }
            //Welche Geometrie ist bei von/bis hinterlegt
            CidsBean geom ;
            geom = (CidsBean)mybean.getProperty(FIELD__GEOREFERENZ_BBOX);            
            if (geom != null && ((Geometry)geom.getProperty(FIELD__GEO_FIELD)).isRectangle()){
                //BBox wird verwendet
            }else{
                geom = (CidsBean)mybean.getProperty(FIELD__GEOREFERENZ_BBOX);
                if (geom != null && ((Geometry)geom.getProperty(FIELD__GEO_FIELD)).getGeometryType().equals("Point")){                   
                    //point wird verwendet
                }else{
                    return false;
                }
            }
            //JOptionPane.showMessageDialog(this,"ergebnis: " + ((Geometry)geom.getProperty("geo_field")).isWithinDistance((Geometry)geoObj, 100.0));
            return ((Geometry)geom.getProperty(FIELD__GEO_FIELD)).isWithinDistance((Geometry)geoObj, 100.0);
         }
         return false;
    }
   /*
    private boolean checkLage(final String schluessel){
        final String myWhere = " where strasse ilike '" + schluessel + "'";
        final CidsBean mybean = TableUtils.getOtherTableValue("str_adr_strasse", myWhere);
         if (mybean != null){
             final Object geoObj = cidsBean.getProperty("georeferenz_bbox.geo_field");
            if (geoObj instanceof Geometry) {
                final CidsBean geom_bbox = (CidsBean)mybean.getProperty("georeferenz_bbox");               
                if (geom_bbox != null && ((Geometry)geom_bbox.getProperty("geo_field")).isRectangle()){
                   JOptionPane.showMessageDialog(this, ((Geometry)geom_bbox.getProperty("geo_field")).isWithinDistance((Geometry)geoObj, 100.0));
                    
                }
            }
         }
         return false;
    } */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
       
        try {
 //JOptionPane.showMessageDialog(this, "cb-status:" + cb.getMetaObject().getStatus()); 
// JOptionPane.showMessageDialog(this, "cb:" + cb);
            if (!(isEditor && (cb == null))) {
                if (cb.getMetaObject().getStatus() != MetaObject.NEW) {
                    cbSchluessel.setVisible(false);
                    newObject = false;
                }    
            }
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
            lblStrasseVon.setText(getStreetNeighbour(FIELD__LAGE_VON));
            lblStrasseBis.setText(getStreetNeighbour(FIELD__LAGE_BIS));
             JOptionPane.showMessageDialog(this, "cb-status:" + cb.getMetaObject().getStatus());
            bindingGroup.bind();
            sleep(5000);
            LOG.fatal(cb.getMOString());
             JOptionPane.showMessageDialog(this, "cb-status:" + cb.getMetaObject().getStatus());
            setReadOnly();
  //JOptionPane.showMessageDialog(this, "cidsbean-status:" + cidsBean.getMetaObject().getStatus()); 
 //JOptionPane.showMessageDialog(this, "cidsbean:" + cidsBean);           
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    private void setReadOnly(){
       // if (cidsBean.getMetaObject().getStatus() != MetaObject.NEW) {
     //  if ((cidsBean != null) || (cidsBean.getMetaObject().getStatus() != MetaObject.NEW)){
     
  //JOptionPane.showMessageDialog(this, "setro-cidsbean-status:" + cidsBean.getMetaObject().getStatus()); 
 //JOptionPane.showMessageDialog(this, "setro-cidsbean:" + cidsBean);
       if ((cidsBean != null) && newObject == false){
            txtName.setEnabled(false);
            lblStrasse.setEnabled(false);
            cbGeom.setEnabled(false);
            //cbGeom.setEditable(false);
            cbGeomBBox.setEnabled(false);
            dcBenenndat.setEnabled(false);
            cbBeschlussB.setEnabled(false);
            txtLageVon.setEnabled(false);
            lblStrasseVon.setEnabled(false);
            txtLageBis.setEnabled(false);
            lblStrasseBis.setEnabled(false);
            cbMotiv.setEnabled(false);
            lstPlz.setEnabled(false);
            scpLstPlz.setEnabled(false);
            panButtonsKm.setEnabled(false);
            btnAddKmQuadrat.setVisible(false);
            btnRemoveKmQuadrat.setVisible(false);
            lstKmQuadrat.setEnabled(false);
            scpLstKmQuadrat.setEnabled(false);
            panButtons.setEnabled(false);
            btnAddStadtbezirk.setVisible(false);
            btnRemoveStadtbezirk.setVisible(false);
            lstStadtbezirk.setEnabled(false);
            scpLstStadtbezirk.setEnabled(false);
            txtDatum.setEnabled(false);
        }
        if (!(isEditor)||cidsBean.getProperty(FIELD__ENTNENNDAT)!= null){
            dcEntnenndat.setEnabled(false);
            cbBeschlussE.setEnabled(false); 
        }
        
    }
    
    public void setMapWindow(){
        CidsBean cb = this.getCidsBean();
        try{
            if (cb.getProperty(FIELD__GEOREFERENZ_BBOX) != null){
                    panPreviewMap.initMap(cb, FIELD__GEOREFERENZ_BBOX__GEO_FIELD);
            }else if (cb.getProperty(FIELD__GEOREFERENZ) != null){
                panPreviewMap.initMap(cb, FIELD__GEOREFERENZ__GEO_FIELD, 50.0);
            }else{
                final GeometryFactory factory = new GeometryFactory(
                                new PrecisionModel(),
                                CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));
                final Point point;
                point = factory.createPoint(new Coordinate(374420,5681660));
                final MetaClass geomMetaClass = ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        TABLE_GEOM);
                final CidsBean newGeom = geomMetaClass.getEmptyInstance().getBean();
                newGeom.setProperty(FIELD__GEO_FIELD, point);
                panPreviewMap.initMap(newGeom, FIELD__GEO_FIELD);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public boolean checkSchluessel(){
        String sSchluessel = String.valueOf(this.cidsBean.getProperty(FIELD__SCHLUESSEL__NAME));
        final String myWhere = " where strasse ilike '" + sSchluessel + "'";
        CidsBean mybean;
        
        mybean = TableUtils.getOtherTableValue(TABLE_STR_ADR_STRASSE, myWhere, getConnectionContext());
        
       return mybean == null;
    }

    @Override
    public void dispose() {
        super.dispose();
        dlgAddStadtbezirk.dispose();
        dlgAddKmQuadrat.dispose();
        //if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
            ((DefaultCismapGeometryComboBoxEditor)cbGeomBBox).dispose();
        //}
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
    public void propertyChange(PropertyChangeEvent evt) {
       //throw new UnsupportedOperationException("Not supported yet.");
        //To change body of generated methods, choose Tools | Templates.
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ)){
           setMapWindow();
        }
        if (evt.getPropertyName().equals(FIELD__GEOREFERENZ_BBOX)){
           setMapWindow();
        }
        
        if (evt.getPropertyName().equals(FIELD__LAGE_VON)){
           // JOptionPane.showMessageDialog(this,"txtLageVon");
            lblStrasseVon.setText(getStreetNeighbour(FIELD__LAGE_VON));
        }
        
        if (evt.getPropertyName().equals(FIELD__LAGE_BIS)){
            //JOptionPane.showMessageDialog(this,"txtLagebis");
            lblStrasseBis.setText(getStreetNeighbour(FIELD__LAGE_BIS));
        }
        
        if (evt.getPropertyName().equals(FIELD__SCHLUESSEL)){
            if (!(checkSchluessel())){
                lblSchluessel.setForeground(new java.awt.Color(255, 0, 0));
            }else {
                lblSchluessel.setForeground(new java.awt.Color(0, 0, 0));
            }
        } 
    }
}


