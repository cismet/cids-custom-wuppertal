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

import com.vividsolutions.jts.geom.Geometry;

import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Converter;

import java.awt.event.MouseAdapter;

import de.cismet.cids.custom.objecteditors.utils.IntegerNumberConverter;
import de.cismet.cids.custom.objecteditors.utils.NumberConverter;
import de.cismet.cids.custom.utils.alkisconstants.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.tools.StaticDecimalTools;
//import javax.swing.JOptionPane;
//import de.cismet.cids.custom.objectrenderer.wunda_blau.SignaturListCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class PflegeStFlurstueckeEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    BindingGroupStore {

    //~ Static fields/initializers ---------------------------------------------

    // private Converter DefaultCismapGeometryComboBoxEditor;
    protected static final Converter<Integer, String> CONVERTER_LEER = new IntegerNumberConverter();
    protected static final Converter<Float, String> CONVERTER_LEERD = new NumberConverter();

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean = null;
    private boolean isEditor = true;
    private Geometry geom;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbPrioritaet;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbZustaendig;
    private javax.swing.JCheckBox chkAltbestand;
    private javax.swing.JCheckBox chkBefAsphalt;
    private javax.swing.JCheckBox chkBefAsphaltNatur;
    private javax.swing.JCheckBox chkBefBasament;
    private javax.swing.JCheckBox chkBefBoeschung;
    private javax.swing.JCheckBox chkBefGittersteine;
    private javax.swing.JCheckBox chkBefGrosspflaster;
    private javax.swing.JCheckBox chkBefHalbschalen;
    private javax.swing.JCheckBox chkBefKleinpflaster;
    private javax.swing.JCheckBox chkBefSchotter;
    private javax.swing.JCheckBox chkBefUnbefestigt;
    private javax.swing.JCheckBox chkGraben;
    private javax.swing.JCheckBox chkMaehen;
    private javax.swing.JCheckBox chkMaehenHandschnitt;
    private javax.swing.JCheckBox chkReinAusbaggern;
    private javax.swing.JCheckBox chkReinGrabenschleuder;
    private javax.swing.JCheckBox chkReinHandreinigung;
    private javax.swing.JCheckBox chkReinKranwagen;
    private javax.swing.JCheckBox chkReinPrivatreinigung;
    private javax.swing.JCheckBox chkReinWildrautbuerste;
    private javax.swing.JCheckBox chkSchneiden;
    private javax.swing.JCheckBox chkSchneidenHandschnitt;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcAufnahme;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcletztePflege;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelFlaeche;
    private javax.swing.JLabel jLabelm2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelGeom;
    private javax.swing.JPanel panFiller10;
    private javax.swing.JPanel panFiller11;
    private javax.swing.JPanel panFiller12;
    private javax.swing.JPanel panFiller2;
    private javax.swing.JPanel panFiller7;
    private javax.swing.JPanel panFiller8;
    private javax.swing.JPanel panFiller_mitte;
    private javax.swing.JPanel panFiller_mitte1;
    private javax.swing.JPanel panFiller_mitte2;
    private javax.swing.JPanel panFiller_mitte3;
    private javax.swing.JPanel panFiller_rechts;
    private javax.swing.JPanel panFiller_rechts1;
    private javax.swing.JPanel panFiller_rechts2;
    private javax.swing.JPanel panFiller_rechts3;
    private javax.swing.JPanel panFiller_zwischen;
    private javax.swing.JPanel panFiller_zwischen1;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private javax.swing.JTextField txtBemerkung;
    private javax.swing.JTextField txtBis;
    private javax.swing.JTextField txtBreite;
    private javax.swing.JTextField txtDurchlaesse;
    private javax.swing.JTextField txtEinlaeufe;
    private javax.swing.JTextField txtLaenge;
    private javax.swing.JTextField txtMaehenIntervall;
    private javax.swing.JTextField txtSchneidenIntervall;
    private javax.swing.JTextField txtStadtbezirk;
    private javax.swing.JTextField txtStrasse;
    private javax.swing.JTextField txtTiefe;
    private javax.swing.JTextField txtVon;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form pflege_st_flurstuecke.
     */
    public PflegeStFlurstueckeEditor() {
        initComponents();
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString("georeferenz");
    }

    /**
     * Creates a new PflegeStFlurstueckeEditor object.
     *
     * @param  createEditor  DOCUMENT ME!
     */
    public PflegeStFlurstueckeEditor(final boolean createEditor) {
        this.isEditor = createEditor;
        initComponents();
        if (!isEditor) {
            txtStadtbezirk.setEditable(false);
            txtStrasse.setEditable(false);
            txtVon.setEditable(false);
            txtBis.setEditable(false);
            txtLaenge.setEditable(false);
            txtTiefe.setEditable(false);
            txtBreite.setEditable(false);
            txtMaehenIntervall.setEditable(false);
            txtSchneidenIntervall.setEditable(false);
            txtEinlaeufe.setEditable(false);
            txtDurchlaesse.setEditable(false);
            txtBemerkung.setEditable(false);

            dcletztePflege.setEditable(false);
            dcAufnahme.setEditable(false);
        }
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

        if (!isEditor) {
            jPanel12 = new javax.swing.JPanel();
        }
        jPanel8 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtStadtbezirk = new javax.swing.JTextField();
        txtStrasse = new javax.swing.JTextField();
        txtVon = new javax.swing.JTextField();
        txtBis = new javax.swing.JTextField();
        txtLaenge = new javax.swing.JTextField();
        txtTiefe = new javax.swing.JTextField();
        txtBreite = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        chkGraben = new javax.swing.JCheckBox();
        chkBefUnbefestigt = new javax.swing.JCheckBox();
        chkBefHalbschalen = new javax.swing.JCheckBox();
        chkBefAsphaltNatur = new javax.swing.JCheckBox();
        chkBefGrosspflaster = new javax.swing.JCheckBox();
        chkBefAsphalt = new javax.swing.JCheckBox();
        chkBefGittersteine = new javax.swing.JCheckBox();
        chkBefSchotter = new javax.swing.JCheckBox();
        chkBefBoeschung = new javax.swing.JCheckBox();
        chkBefBasament = new javax.swing.JCheckBox();
        chkBefKleinpflaster = new javax.swing.JCheckBox();
        panFiller8 = new javax.swing.JPanel();
        txtEinlaeufe = new javax.swing.JTextField();
        txtDurchlaesse = new javax.swing.JTextField();
        panFiller_rechts = new javax.swing.JPanel();
        panFiller_mitte = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        chkMaehen = new javax.swing.JCheckBox();
        chkMaehenHandschnitt = new javax.swing.JCheckBox();
        txtMaehenIntervall = new javax.swing.JTextField();
        panFiller_rechts1 = new javax.swing.JPanel();
        panFiller_mitte1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        chkSchneiden = new javax.swing.JCheckBox();
        chkSchneidenHandschnitt = new javax.swing.JCheckBox();
        txtSchneidenIntervall = new javax.swing.JTextField();
        panFiller_rechts2 = new javax.swing.JPanel();
        panFiller_mitte2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        chkReinPrivatreinigung = new javax.swing.JCheckBox();
        chkReinKranwagen = new javax.swing.JCheckBox();
        chkReinWildrautbuerste = new javax.swing.JCheckBox();
        chkReinAusbaggern = new javax.swing.JCheckBox();
        chkReinGrabenschleuder = new javax.swing.JCheckBox();
        chkReinHandreinigung = new javax.swing.JCheckBox();
        panFiller_rechts3 = new javax.swing.JPanel();
        panFiller_mitte3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        dcletztePflege = new de.cismet.cids.editors.DefaultBindableDateChooser();
        panFiller_zwischen1 = new javax.swing.JPanel();
        panFiller7 = new javax.swing.JPanel();
        panFiller_zwischen = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        chkAltbestand = new javax.swing.JCheckBox();
        txtBemerkung = new javax.swing.JTextField();
        cbPrioritaet = new DefaultBindableReferenceCombo(true);
        cbZustaendig = new DefaultBindableReferenceCombo(true);
        dcAufnahme = new de.cismet.cids.editors.DefaultBindableDateChooser();
        panFiller11 = new javax.swing.JPanel();
        jPanelGeom = new javax.swing.JPanel();
        if (isEditor) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        jLabelm2 = new javax.swing.JLabel();
        jLabelFlaeche = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel39 = new javax.swing.JLabel();
        panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
        panFiller2 = new javax.swing.JPanel();
        panFiller10 = new javax.swing.JPanel();
        panFiller12 = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(1200, 650));
        setPreferredSize(new java.awt.Dimension(1200, 650));
        setLayout(new java.awt.GridBagLayout());

        if (!isEditor) {
            jPanel12.setOpaque(false);
        }
        if (!isEditor) {
            jPanel12.setPreferredSize(new java.awt.Dimension(1200, 670));
        }

        if (!isEditor) {
            final javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
            jPanel12.setLayout(jPanel12Layout);
            jPanel12Layout.setHorizontalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                    0,
                    1200,
                    Short.MAX_VALUE));
            jPanel12Layout.setVerticalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                    0,
                    770,
                    Short.MAX_VALUE));
        }

        if (!isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
            add(jPanel12, gridBagConstraints);
            jPanel12.addMouseListener(new MouseAdapter() {
                });
        }

        jPanel8.setMinimumSize(new java.awt.Dimension(1200, 770));
        jPanel8.setName(""); // NOI18N
        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new java.awt.Dimension(1200, 670));
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel1.setMinimumSize(new java.awt.Dimension(500, 110));
        jPanel1.setName(""); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 180));
        jPanel1.setRequestFocusEnabled(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Stadtbezirk:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Straße:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel5, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("von:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Länge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel11, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel13.setText("Tiefe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel13, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Breite:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel1.add(jLabel15, gridBagConstraints);

        txtStadtbezirk.setName(""); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stadtbezirk}"),
                txtStadtbezirk,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtStadtbezirk, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                txtStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.von}"),
                txtVon,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtVon, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bis}"),
                txtBis,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge}"),
                txtLaenge,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(CONVERTER_LEERD);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtLaenge, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.tiefe}"),
                txtTiefe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(CONVERTER_LEERD);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtTiefe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.breite}"),
                txtBreite,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(CONVERTER_LEERD);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(txtBreite, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 2, 0);
        jPanel8.add(jPanel1, gridBagConstraints);

        jPanel2.setMinimumSize(new java.awt.Dimension(500, 270));
        jPanel2.setName(""); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(500, 290));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                "Graben",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel4.setMinimumSize(new java.awt.Dimension(370, 282));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(200, 287));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel31.setText("Graben:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel4.add(jLabel31, gridBagConstraints);

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel30.setText("Befestigung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 4);
        jPanel4.add(jLabel30, gridBagConstraints);

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel29.setText("unbefestigt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel29, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel28.setText("Halbschalen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel28, gridBagConstraints);

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel27.setText("Asphalt & Naturstein:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel27, gridBagConstraints);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel26.setText("Großpflaster:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel26, gridBagConstraints);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel25.setText("Asphalt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel25, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel24.setText("Gittersteine:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel24, gridBagConstraints);

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel23.setText("Schotter/Grobschl.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel23, gridBagConstraints);

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel22.setText("Böschung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel22, gridBagConstraints);

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel21.setText("Basamentpflaster:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel21, gridBagConstraints);

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel20.setText("Kleinpflaster");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 0, 4);
        jPanel4.add(jLabel20, gridBagConstraints);

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel19.setText("Einläufe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 4);
        jPanel4.add(jLabel19, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("Durchlässe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 4);
        jPanel4.add(jLabel18, gridBagConstraints);

        chkGraben.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.graben}"),
                chkGraben,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkGraben.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chkGrabenStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkGraben, gridBagConstraints);

        chkBefUnbefestigt.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_unbefestigt}"),
                chkBefUnbefestigt,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        istGraben();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefUnbefestigt, gridBagConstraints);

        chkBefHalbschalen.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_halbschalen}"),
                chkBefHalbschalen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefHalbschalen, gridBagConstraints);

        chkBefAsphaltNatur.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_asphalt_natur}"),
                chkBefAsphaltNatur,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefAsphaltNatur, gridBagConstraints);

        chkBefGrosspflaster.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_grosspflaster}"),
                chkBefGrosspflaster,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefGrosspflaster, gridBagConstraints);

        chkBefAsphalt.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_asphalt}"),
                chkBefAsphalt,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefAsphalt, gridBagConstraints);

        chkBefGittersteine.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_gittersteine}"),
                chkBefGittersteine,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefGittersteine, gridBagConstraints);

        chkBefSchotter.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_schotter}"),
                chkBefSchotter,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefSchotter, gridBagConstraints);

        chkBefBoeschung.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_boeschung}"),
                chkBefBoeschung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefBoeschung, gridBagConstraints);

        chkBefBasament.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_basament}"),
                chkBefBasament,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefBasament, gridBagConstraints);

        chkBefKleinpflaster.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bef_kleinpflaster}"),
                chkBefKleinpflaster,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel4.add(chkBefKleinpflaster, gridBagConstraints);

        final javax.swing.GroupLayout panFiller8Layout = new javax.swing.GroupLayout(panFiller8);
        panFiller8.setLayout(panFiller8Layout);
        panFiller8Layout.setHorizontalGroup(
            panFiller8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller8Layout.setVerticalGroup(
            panFiller8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel4.add(panFiller8, gridBagConstraints);

        txtEinlaeufe.setMinimumSize(new java.awt.Dimension(30, 19));
        txtEinlaeufe.setName(""); // NOI18N
        txtEinlaeufe.setPreferredSize(new java.awt.Dimension(30, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.einlaeufe}"),
                txtEinlaeufe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(CONVERTER_LEER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 4);
        jPanel4.add(txtEinlaeufe, gridBagConstraints);

        txtDurchlaesse.setMinimumSize(new java.awt.Dimension(30, 19));
        txtDurchlaesse.setPreferredSize(new java.awt.Dimension(30, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.durchlaesse}"),
                txtDurchlaesse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(CONVERTER_LEER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 4);
        jPanel4.add(txtDurchlaesse, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_rechtsLayout = new javax.swing.GroupLayout(panFiller_rechts);
        panFiller_rechts.setLayout(panFiller_rechtsLayout);
        panFiller_rechtsLayout.setHorizontalGroup(
            panFiller_rechtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_rechtsLayout.setVerticalGroup(
            panFiller_rechtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 15;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel4.add(panFiller_rechts, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_mitteLayout = new javax.swing.GroupLayout(panFiller_mitte);
        panFiller_mitte.setLayout(panFiller_mitteLayout);
        panFiller_mitteLayout.setHorizontalGroup(
            panFiller_mitteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_mitteLayout.setVerticalGroup(
            panFiller_mitteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(panFiller_mitte, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        jPanel2.add(jPanel4, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(293, 278));
        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(292, 278));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                "Mähen",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel6.setMinimumSize(new java.awt.Dimension(143, 93));
        jPanel6.setOpaque(false);
        jPanel6.setPreferredSize(new java.awt.Dimension(150, 100));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("Mähen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel6.add(jLabel2, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel4.setText("Intervall:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel6.add(jLabel4, gridBagConstraints);

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel32.setText("Handschnitt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel6.add(jLabel32, gridBagConstraints);

        chkMaehen.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.maehen}"),
                chkMaehen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkMaehen.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chkMaehenStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel6.add(chkMaehen, gridBagConstraints);

        chkMaehenHandschnitt.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.maehen_handschnitt}"),
                chkMaehenHandschnitt,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        istMaehen();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel6.add(chkMaehenHandschnitt, gridBagConstraints);

        txtMaehenIntervall.setMinimumSize(new java.awt.Dimension(30, 19));
        txtMaehenIntervall.setPreferredSize(new java.awt.Dimension(30, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.maehen_intervall}"),
                txtMaehenIntervall,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(CONVERTER_LEER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel6.add(txtMaehenIntervall, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_rechts1Layout = new javax.swing.GroupLayout(panFiller_rechts1);
        panFiller_rechts1.setLayout(panFiller_rechts1Layout);
        panFiller_rechts1Layout.setHorizontalGroup(
            panFiller_rechts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_rechts1Layout.setVerticalGroup(
            panFiller_rechts1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel6.add(panFiller_rechts1, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_mitte1Layout = new javax.swing.GroupLayout(panFiller_mitte1);
        panFiller_mitte1.setLayout(panFiller_mitte1Layout);
        panFiller_mitte1Layout.setHorizontalGroup(
            panFiller_mitte1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_mitte1Layout.setVerticalGroup(
            panFiller_mitte1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(panFiller_mitte1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel5.add(jPanel6, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                "Schneiden",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel7.setMinimumSize(new java.awt.Dimension(143, 93));
        jPanel7.setOpaque(false);
        jPanel7.setPreferredSize(new java.awt.Dimension(150, 100));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel33.setText("Schneiden:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel7.add(jLabel33, gridBagConstraints);

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel34.setText("Intervall:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel7.add(jLabel34, gridBagConstraints);

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel35.setText("Handschnitt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel7.add(jLabel35, gridBagConstraints);

        chkSchneiden.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schneiden}"),
                chkSchneiden,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        chkSchneiden.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    chkSchneidenStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel7.add(chkSchneiden, gridBagConstraints);

        chkSchneidenHandschnitt.setBackground(new java.awt.Color(245, 245, 245));
        chkSchneidenHandschnitt.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schneiden_handschnitt}"),
                chkSchneidenHandschnitt,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        istSchneiden();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel7.add(chkSchneidenHandschnitt, gridBagConstraints);

        txtSchneidenIntervall.setMinimumSize(new java.awt.Dimension(30, 19));
        txtSchneidenIntervall.setPreferredSize(new java.awt.Dimension(30, 19));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schneiden_intevall}"),
                txtSchneidenIntervall,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(CONVERTER_LEER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 2, 4, 4);
        jPanel7.add(txtSchneidenIntervall, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_rechts2Layout = new javax.swing.GroupLayout(panFiller_rechts2);
        panFiller_rechts2.setLayout(panFiller_rechts2Layout);
        panFiller_rechts2Layout.setHorizontalGroup(
            panFiller_rechts2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_rechts2Layout.setVerticalGroup(
            panFiller_rechts2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel7.add(panFiller_rechts2, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_mitte2Layout = new javax.swing.GroupLayout(panFiller_mitte2);
        panFiller_mitte2.setLayout(panFiller_mitte2Layout);
        panFiller_mitte2Layout.setHorizontalGroup(
            panFiller_mitte2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_mitte2Layout.setVerticalGroup(
            panFiller_mitte2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(panFiller_mitte2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel5.add(jPanel7, gridBagConstraints);

        jPanel9.setBackground(new java.awt.Color(245, 245, 245));
        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(
                null,
                "Reinigung",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel9.setMinimumSize(new java.awt.Dimension(215, 129));
        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(215, 129));
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel6.setText("Privatreinigung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel9.add(jLabel6, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel12.setText("Ausbaggern & Grabenschleuder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel9.add(jLabel12, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel8.setText("Kranwagen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel9.add(jLabel8, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel10.setText("Wildkrautbürste:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel9.add(jLabel10, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel14.setText("Grabenschleuder:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 4);
        jPanel9.add(jLabel14, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jLabel16.setText("Handreinigung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(4, 10, 0, 4);
        jPanel9.add(jLabel16, gridBagConstraints);

        chkReinPrivatreinigung.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_privatreinigung}"),
                chkReinPrivatreinigung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinPrivatreinigung, gridBagConstraints);

        chkReinKranwagen.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_kranwagen}"),
                chkReinKranwagen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinKranwagen, gridBagConstraints);

        chkReinWildrautbuerste.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_wildkrautbuerste}"),
                chkReinWildrautbuerste,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinWildrautbuerste, gridBagConstraints);

        chkReinAusbaggern.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_ausbaggern}"),
                chkReinAusbaggern,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinAusbaggern, gridBagConstraints);

        chkReinGrabenschleuder.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_grabenschleuder}"),
                chkReinGrabenschleuder,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinGrabenschleuder, gridBagConstraints);

        chkReinHandreinigung.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rein_handreinigung}"),
                chkReinHandreinigung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        jPanel9.add(chkReinHandreinigung, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_rechts3Layout = new javax.swing.GroupLayout(panFiller_rechts3);
        panFiller_rechts3.setLayout(panFiller_rechts3Layout);
        panFiller_rechts3Layout.setHorizontalGroup(
            panFiller_rechts3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_rechts3Layout.setVerticalGroup(
            panFiller_rechts3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel9.add(panFiller_rechts3, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_mitte3Layout = new javax.swing.GroupLayout(panFiller_mitte3);
        panFiller_mitte3.setLayout(panFiller_mitte3Layout);
        panFiller_mitte3Layout.setHorizontalGroup(
            panFiller_mitte3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_mitte3Layout.setVerticalGroup(
            panFiller_mitte3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(panFiller_mitte3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        jPanel5.add(jPanel9, gridBagConstraints);

        jPanel10.setMinimumSize(new java.awt.Dimension(293, 25));
        jPanel10.setOpaque(false);
        jPanel10.setPreferredSize(new java.awt.Dimension(217, 40));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setText("letzte Pflege:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 4, 0, 0);
        jPanel10.add(jLabel38, gridBagConstraints);

        dcletztePflege.setMaximumSize(new java.awt.Dimension(80, 25));
        dcletztePflege.setMinimumSize(new java.awt.Dimension(65, 25));
        dcletztePflege.setPreferredSize(new java.awt.Dimension(65, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zuletzt_gepflegt}"),
                dcletztePflege,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcletztePflege.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 5, 0);
        jPanel10.add(dcletztePflege, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 5, 0);
        jPanel5.add(jPanel10, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_zwischen1Layout = new javax.swing.GroupLayout(panFiller_zwischen1);
        panFiller_zwischen1.setLayout(panFiller_zwischen1Layout);
        panFiller_zwischen1Layout.setHorizontalGroup(
            panFiller_zwischen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_zwischen1Layout.setVerticalGroup(
            panFiller_zwischen1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel5.add(panFiller_zwischen1, gridBagConstraints);

        final javax.swing.GroupLayout panFiller7Layout = new javax.swing.GroupLayout(panFiller7);
        panFiller7.setLayout(panFiller7Layout);
        panFiller7Layout.setHorizontalGroup(
            panFiller7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller7Layout.setVerticalGroup(
            panFiller7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel5.add(panFiller7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        jPanel2.add(jPanel5, gridBagConstraints);

        final javax.swing.GroupLayout panFiller_zwischenLayout = new javax.swing.GroupLayout(panFiller_zwischen);
        panFiller_zwischen.setLayout(panFiller_zwischenLayout);
        panFiller_zwischenLayout.setHorizontalGroup(
            panFiller_zwischenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller_zwischenLayout.setVerticalGroup(
            panFiller_zwischenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(panFiller_zwischen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        jPanel8.add(jPanel2, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(218, 200));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(500, 190));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setText("Altbestand:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 4);
        jPanel3.add(jLabel36, gridBagConstraints);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel37.setText("Aufnahmedatum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel3.add(jLabel37, gridBagConstraints);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel3.add(jLabel40, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel42.setText("Zuständig:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel3.add(jLabel42, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel44.setText("Priorität:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel3.add(jLabel44, gridBagConstraints);

        chkAltbestand.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.altbestand}"),
                chkAltbestand,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        jPanel3.add(chkAltbestand, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(txtBemerkung, gridBagConstraints);

        cbPrioritaet.setMaximumSize(new java.awt.Dimension(200, 23));
        cbPrioritaet.setMinimumSize(new java.awt.Dimension(150, 23));
        cbPrioritaet.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.prioritaet}"),
                cbPrioritaet,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cbPrioritaet, gridBagConstraints);

        cbZustaendig.setMaximumSize(new java.awt.Dimension(200, 23));
        cbZustaendig.setMinimumSize(new java.awt.Dimension(150, 23));
        cbZustaendig.setPreferredSize(new java.awt.Dimension(150, 23));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zustaendig}"),
                cbZustaendig,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cbZustaendig, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufnahmedatum}"),
                dcAufnahme,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcAufnahme.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 1, 2, 2);
        jPanel3.add(dcAufnahme, gridBagConstraints);

        final javax.swing.GroupLayout panFiller11Layout = new javax.swing.GroupLayout(panFiller11);
        panFiller11.setLayout(panFiller11Layout);
        panFiller11Layout.setHorizontalGroup(
            panFiller11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller11Layout.setVerticalGroup(
            panFiller11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(panFiller11, gridBagConstraints);

        jPanelGeom.setLayout(new java.awt.GridBagLayout());

        if (isEditor) {
            cbGeom.setName(""); // NOI18N
            if (isEditor) {
            }

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
            jPanelGeom.add(cbGeom, gridBagConstraints);
        }

        jLabelm2.setText("m²");
        jLabelm2.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
        jPanelGeom.add(jLabelm2, gridBagConstraints);

        jLabelFlaeche.setText("0.0");
        jLabelFlaeche.setToolTipText("");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        jPanelGeom.add(jLabelFlaeche, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(jPanelGeom, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        if (isEditor) {
            jLabel17.setText("Geometrie:");
        } else {
            jLabel17.setText("Fläche:");
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 4);
        jPanel3.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 0);
        jPanel8.add(jPanel3, gridBagConstraints);

        jPanel11.setMinimumSize(new java.awt.Dimension(680, 770));
        jPanel11.setName(""); // NOI18N
        jPanel11.setOpaque(false);
        jPanel11.setPreferredSize(new java.awt.Dimension(680, 670));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel7.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel7.setMinimumSize(new java.awt.Dimension(44, 25));
        semiRoundedPanel7.setPreferredSize(new java.awt.Dimension(44, 25));
        semiRoundedPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Lage");
        jLabel39.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(jLabel39, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 2);
        jPanel11.add(semiRoundedPanel7, gridBagConstraints);

        panPreviewMap.setMinimumSize(new java.awt.Dimension(100, 500));
        panPreviewMap.setPreferredSize(new java.awt.Dimension(100, 450));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 20, 3);
        jPanel11.add(panPreviewMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        jPanel8.add(jPanel11, gridBagConstraints);

        panFiller2.setBackground(new java.awt.Color(245, 245, 245));
        panFiller2.setOpaque(false);

        final javax.swing.GroupLayout panFiller2Layout = new javax.swing.GroupLayout(panFiller2);
        panFiller2.setLayout(panFiller2Layout);
        panFiller2Layout.setHorizontalGroup(
            panFiller2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFiller2Layout.setVerticalGroup(
            panFiller2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(panFiller2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel8, gridBagConstraints);

        panFiller10.setName(""); // NOI18N
        panFiller10.setOpaque(false);

        final javax.swing.GroupLayout panFiller10Layout = new javax.swing.GroupLayout(panFiller10);
        panFiller10.setLayout(panFiller10Layout);
        panFiller10Layout.setHorizontalGroup(
            panFiller10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                170,
                Short.MAX_VALUE));
        panFiller10Layout.setVerticalGroup(
            panFiller10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                670,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        add(panFiller10, gridBagConstraints);

        panFiller12.setName(""); // NOI18N
        panFiller12.setOpaque(false);

        final javax.swing.GroupLayout panFiller12Layout = new javax.swing.GroupLayout(panFiller12);
        panFiller12.setLayout(panFiller12Layout);
        panFiller12Layout.setHorizontalGroup(
            panFiller12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                1201,
                Short.MAX_VALUE));
        panFiller12Layout.setVerticalGroup(
            panFiller12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                295,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        add(panFiller12, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkGrabenStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_chkGrabenStateChanged
        // TODO add your handling code here:
        istGraben();
    } //GEN-LAST:event_chkGrabenStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkMaehenStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_chkMaehenStateChanged
        // TODO add your handling code here:
        istMaehen();
    } //GEN-LAST:event_chkMaehenStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkSchneidenStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_chkSchneidenStateChanged
        // TODO add your handling code here:
        istSchneiden();
    } //GEN-LAST:event_chkSchneidenStateChanged
    /**
     * DOCUMENT ME!
     */
    private void istGraben() {
        final boolean istGraben = chkGraben.isSelected();

        chkBefUnbefestigt.setEnabled(istGraben);
        chkBefHalbschalen.setEnabled(istGraben);
        chkBefAsphaltNatur.setEnabled(istGraben);
        chkBefGrosspflaster.setEnabled(istGraben);
        chkBefAsphalt.setEnabled(istGraben);
        chkBefGittersteine.setEnabled(istGraben);
        chkBefSchotter.setEnabled(istGraben);
        chkBefBoeschung.setEnabled(istGraben);
        chkBefBasament.setEnabled(istGraben);
        chkBefKleinpflaster.setEnabled(istGraben);
        txtEinlaeufe.setEnabled(istGraben);
        txtDurchlaesse.setEnabled(istGraben);
        if (istGraben == false) {
            chkBefUnbefestigt.setSelected(istGraben);
            chkBefHalbschalen.setSelected(istGraben);
            chkBefAsphaltNatur.setSelected(istGraben);
            chkBefGrosspflaster.setSelected(istGraben);
            chkBefAsphalt.setSelected(istGraben);
            chkBefGittersteine.setSelected(istGraben);
            chkBefSchotter.setSelected(istGraben);
            chkBefBoeschung.setSelected(istGraben);
            chkBefBasament.setSelected(istGraben);
            chkBefKleinpflaster.setSelected(istGraben);
            txtEinlaeufe.setText("");
            txtDurchlaesse.setText("");
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void istMaehen() {
        final boolean istMaehen = chkMaehen.isSelected();

        txtMaehenIntervall.setEnabled(istMaehen);
        chkMaehenHandschnitt.setEnabled(istMaehen);
        if (istMaehen == false) {
            txtMaehenIntervall.setText("");
            chkMaehenHandschnitt.setSelected(istMaehen);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void istSchneiden() {
        final boolean istSchneiden = chkSchneiden.isSelected();

        txtSchneidenIntervall.setEnabled(istSchneiden);
        chkSchneidenHandschnitt.setEnabled(istSchneiden);
        if (istSchneiden == false) {
            txtSchneidenIntervall.setText("");
            chkSchneidenHandschnitt.setSelected(istSchneiden);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  g  DOCUMENT ME!
     */
    public void setGeometry(final Geometry g) {
        geom = g;
        jLabelFlaeche.setText(getFlaeche());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getFlaeche() {
        if (geom == null) {
            return "0.0";
        }
        return StaticDecimalTools.round("0.0##", geom.getArea());
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann wird ein neu gezeichnetes Polygon nicht erkannt.
        bindingGroup.unbind();

        if (cb != null) {
            this.cidsBean = cb;
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen für Zuständigkeit und Priorität nicht gefüllt
            // werden evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb);
            panPreviewMap.initMap(cb, "georeferenz.geo_field");

            bindingGroup.bind();
        }
        setGeometry(CrsTransformer.transformToGivenCrs(
                (Geometry)cidsBean.getProperty(
                    "georeferenz.geo_field"),
                AlkisConstants.COMMONS.SRS_SERVICE));
    }

    /*  public void setCidsBean(CidsBean cb) {
     *    //dispose();  Wenn Aufruf hier, dann wird ein neu gezeichnetes Polygon nicht erkannt.   bindingGroup.unbind();
     * this.cidsBean=cb;   //8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen für Zuständigkeit und Priorität
     * nicht gefüllt werden   //evtl. kann dies verbessert werden.
     * DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
     * bindingGroup,           cb);   bindingGroup.bind();}*/

    @Override
    public void dispose() {
        super.dispose();
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
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
}
