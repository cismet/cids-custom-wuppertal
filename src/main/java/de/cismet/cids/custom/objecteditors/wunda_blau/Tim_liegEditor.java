/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Tim_liegEditor.java
 *
 * Created on 26.05.2009, 14:25:07
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import org.openide.util.Exceptions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BeanInitializer;
import de.cismet.cids.editors.BeanInitializerProvider;
import de.cismet.cids.editors.DefaultBeanInitializer;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.Titled;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class Tim_liegEditor extends DefaultCustomObjectEditor implements Titled,
    BeanInitializerProvider,
    PropertyChangeListener {

    //~ Instance fields --------------------------------------------------------

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBodenschaetzung_ueb_von;
    private javax.swing.JButton btnSchlusspruefung_von;
    private javax.swing.JComboBox cboGeom;
    private javax.swing.JButton cmdAddKart;
    private javax.swing.JButton cmdAddSgk;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnAlk_bod;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnAlk_geb;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnAlk_nutz;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnAlk_top;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnAlkis_rel;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnCity_rel;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnDgk_rel;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnFreizeit_rel;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnStadt_rel;
    private de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo jnUeber_rel;
    private org.jdesktop.swingx.JXDatePicker jxdAlk_beginn_dat;
    private org.jdesktop.swingx.JXDatePicker jxdAlk_ent_dat;
    private org.jdesktop.swingx.JXDatePicker jxdAlk_ueb_dat;
    private org.jdesktop.swingx.JXDatePicker jxdBodenschaetzung_ueb_am;
    private org.jdesktop.swingx.JXDatePicker jxdCity_ueb_dat;
    private org.jdesktop.swingx.JXDatePicker jxdComliste_dat;
    private org.jdesktop.swingx.JXDatePicker jxdEin_dat;
    private org.jdesktop.swingx.JXDatePicker jxdFreizeit_ueb_dat;
    private org.jdesktop.swingx.JXDatePicker jxdLaufzettel_erled_dat;
    private org.jdesktop.swingx.JXDatePicker jxdLinkbase_ueb_am;
    private org.jdesktop.swingx.JXDatePicker jxdLoe_dat;
    private org.jdesktop.swingx.JXDatePicker jxdPruef_gebeinmess_abgabe;
    private org.jdesktop.swingx.JXDatePicker jxdPruef_gebeinmess_rueckgabe;
    private org.jdesktop.swingx.JXDatePicker jxdRealn_ueb_am;
    private org.jdesktop.swingx.JXDatePicker jxdScan_dat;
    private org.jdesktop.swingx.JXDatePicker jxdSchlusspruefung_am;
    private org.jdesktop.swingx.JXDatePicker jxdStadt_ent_dat;
    private org.jdesktop.swingx.JXDatePicker jxdStadt_ueb_dat;
    private org.jdesktop.swingx.JXDatePicker jxdTim_erled_dat;
    private org.jdesktop.swingx.JXDatePicker jxdTop_aussen_beendet;
    private org.jdesktop.swingx.JXDatePicker jxdTop_aussen_beginn;
    private org.jdesktop.swingx.JXDatePicker jxdTop_grund_beendet;
    private org.jdesktop.swingx.JXDatePicker jxdTop_grund_beginn;
    private org.jdesktop.swingx.JXDatePicker jxdUeber_ueb_dat;
    private org.jdesktop.swingx.JXDatePicker jxdUebersicht_liste_erled_dat;
    private javax.swing.JLabel lblAbschluss_registrierung_bem;
    private javax.swing.JLabel lblAlk_beginn_dat;
    private javax.swing.JLabel lblAlk_bem;
    private javax.swing.JLabel lblAlk_bod;
    private javax.swing.JLabel lblAlk_ent_beab;
    private javax.swing.JLabel lblAlk_ent_dat;
    private javax.swing.JLabel lblAlk_geb;
    private javax.swing.JLabel lblAlk_luft;
    private javax.swing.JLabel lblAlk_nutz;
    private javax.swing.JLabel lblAlk_prio;
    private javax.swing.JLabel lblAlk_projekt_name;
    private javax.swing.JLabel lblAlk_son1;
    private javax.swing.JLabel lblAlk_son2;
    private javax.swing.JLabel lblAlk_top;
    private javax.swing.JLabel lblAlk_ueb_beab;
    private javax.swing.JLabel lblAlk_ueb_dat;
    private javax.swing.JLabel lblAlk_ver;
    private javax.swing.JLabel lblAlkis_rel;
    private javax.swing.JLabel lblAuftraggeber;
    private javax.swing.JLabel lblBereich_lage_riss;
    private javax.swing.JLabel lblBodenschaetzung_ueb_am;
    private javax.swing.JLabel lblBodenschaetzung_ueb_von;
    private javax.swing.JLabel lblCity_bem;
    private javax.swing.JLabel lblCity_rel;
    private javax.swing.JLabel lblCity_ueb_beab;
    private javax.swing.JLabel lblCity_ueb_dat;
    private javax.swing.JLabel lblComliste_dat;
    private javax.swing.JLabel lblDgk_rel;
    private javax.swing.JLabel lblEin_beab;
    private javax.swing.JLabel lblEin_dat;
    private javax.swing.JLabel lblFreizeit_bem;
    private javax.swing.JLabel lblFreizeit_rel;
    private javax.swing.JLabel lblFreizeit_ueb_beab;
    private javax.swing.JLabel lblFreizeit_ueb_dat;
    private javax.swing.JLabel lblGeom;
    private javax.swing.JLabel lblHinweis;
    private javax.swing.JLabel lblJumpAuftrag;
    private javax.swing.JLabel lblJumpAuftrag1;
    private javax.swing.JLabel lblJumpAuftrag2;
    private javax.swing.JLabel lblJumpAuftrag3;
    private javax.swing.JLabel lblJumpAuftrag4;
    private javax.swing.JLabel lblJumpSchlusspruefung;
    private javax.swing.JLabel lblLaufzettel_erled_dat;
    private javax.swing.JLabel lblLinkbase_ueb_am;
    private javax.swing.JLabel lblLinkbase_ueb_von;
    private javax.swing.JLabel lblLoe_beab;
    private javax.swing.JLabel lblLoe_dat;
    private javax.swing.JLabel lblLoe_grund;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPruef_gebeinmess_abgabe;
    private javax.swing.JLabel lblPruef_gebeinmess_bem;
    private javax.swing.JLabel lblPruef_gebeinmess_rueckgabe;
    private javax.swing.JLabel lblPruefung_von;
    private javax.swing.JLabel lblRealn_ueb_am;
    private javax.swing.JLabel lblRealn_ueb_von;
    private javax.swing.JLabel lblScan_dat;
    private javax.swing.JLabel lblSchlusspruefung_am;
    private javax.swing.JLabel lblSchlusspruefung_von;
    private javax.swing.JLabel lblStadt_bem;
    private javax.swing.JLabel lblStadt_ent_beab;
    private javax.swing.JLabel lblStadt_ent_dat;
    private javax.swing.JLabel lblStadt_rel;
    private javax.swing.JLabel lblStadt_ueb_beab;
    private javax.swing.JLabel lblStadt_ueb_dat;
    private javax.swing.JLabel lblTim_erled_dat;
    private javax.swing.JLabel lblTop_aussen_beendet;
    private javax.swing.JLabel lblTop_aussen_beginn;
    private javax.swing.JLabel lblTop_grund_beendet;
    private javax.swing.JLabel lblTop_grund_beginn;
    private javax.swing.JLabel lblTop_grund_bem;
    private javax.swing.JLabel lblUeber_bem;
    private javax.swing.JLabel lblUeber_rel;
    private javax.swing.JLabel lblUeber_ueb_beab;
    private javax.swing.JLabel lblUeber_ueb_dat;
    private javax.swing.JLabel lblUebern_innen_bem;
    private javax.swing.JLabel lblUebersicht_liste_erled_dat;
    private javax.swing.JLabel lbl_Bereich_lage_riss_nummer;
    private javax.swing.JPanel panKartographie;
    private javax.swing.JPanel panStadtgrundkarte;
    private javax.swing.JScrollPane scpKartographie;
    private javax.swing.JScrollPane scpStadtgrundkarte;
    private javax.swing.JTabbedPane tbpAdditionalInfo;
    private javax.swing.JTextField txtAlk_ent_beab;
    private javax.swing.JTextField txtAlk_luft;
    private javax.swing.JTextField txtAlk_prio;
    private javax.swing.JTextField txtAlk_projekt_name;
    private javax.swing.JTextField txtAlk_son2;
    private javax.swing.JTextField txtAlk_ueb_beab;
    private javax.swing.JTextField txtAlk_ver;
    private javax.swing.JTextField txtAuftraggeber;
    private javax.swing.JTextField txtBereich_lage_riss;
    private javax.swing.JTextField txtBodenschaetzung_ueb_von;
    private javax.swing.JTextField txtCity_bem;
    private javax.swing.JTextField txtCity_ueb_beab;
    private javax.swing.JTextField txtEin_beab;
    private javax.swing.JTextField txtFreizeit_bem;
    private javax.swing.JTextField txtFreizeit_ueb_beab;
    private javax.swing.JTextField txtHinweis;
    private javax.swing.JTextField txtLinkbase_ueb_von;
    private javax.swing.JTextField txtLoe_beab;
    private javax.swing.JTextField txtLoe_grund;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtPruefung_von;
    private javax.swing.JTextField txtRealn_ueb_von;
    private javax.swing.JTextField txtSchlusspruefung_von;
    private javax.swing.JTextField txtStadt_bem;
    private javax.swing.JTextField txtStadt_ent_beab;
    private javax.swing.JTextField txtStadt_ueb_beab;
    private javax.swing.JTextField txtUeber_bem;
    private javax.swing.JTextField txtUeber_ueb_beab;
    private javax.swing.JTextField txt_Bereich_lage_riss_nummer;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Tim_liegEditor.
     */
    public Tim_liegEditor() {
        initComponents();
        scpKartographie.getVerticalScrollBar().setUnitIncrement(26);
        scpStadtgrundkarte.getVerticalScrollBar().setUnitIncrement(26);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        super.setCidsBean(cidsBean);
        if (cidsBean.getProperty("alkis") == null) {
            tbpAdditionalInfo.remove(panStadtgrundkarte);
            cmdAddSgk.setVisible(true);
        } else {
            cmdAddSgk.setVisible(false);
        }

        if (cidsBean.getProperty("kart") == null) {
            tbpAdditionalInfo.remove(panKartographie);
            cmdAddKart.setVisible(true);
        } else {
            cmdAddKart.setVisible(false);
        }
        cidsBean.addPropertyChangeListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblHinweis = new javax.swing.JLabel();
        txtHinweis = new javax.swing.JTextField();
        lblEin_beab = new javax.swing.JLabel();
        txtEin_beab = new javax.swing.JTextField();
        lblEin_dat = new javax.swing.JLabel();
        jxdEin_dat = new org.jdesktop.swingx.JXDatePicker();
        lblLoe_beab = new javax.swing.JLabel();
        txtLoe_beab = new javax.swing.JTextField();
        lblLoe_dat = new javax.swing.JLabel();
        jxdLoe_dat = new org.jdesktop.swingx.JXDatePicker();
        lblLoe_grund = new javax.swing.JLabel();
        txtLoe_grund = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        tbpAdditionalInfo = new javax.swing.JTabbedPane();
        panStadtgrundkarte = new javax.swing.JPanel();
        scpStadtgrundkarte = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        lblAlkis_rel = new javax.swing.JLabel();
        jnAlkis_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblAuftraggeber = new javax.swing.JLabel();
        txtAuftraggeber = new javax.swing.JTextField();
        lblAlk_ent_beab = new javax.swing.JLabel();
        txtAlk_ent_beab = new javax.swing.JTextField();
        lblAlk_ent_dat = new javax.swing.JLabel();
        jxdAlk_ent_dat = new org.jdesktop.swingx.JXDatePicker();
        lblAlk_prio = new javax.swing.JLabel();
        txtAlk_prio = new javax.swing.JTextField();
        lblBereich_lage_riss = new javax.swing.JLabel();
        txtBereich_lage_riss = new javax.swing.JTextField();
        lbl_Bereich_lage_riss_nummer = new javax.swing.JLabel();
        txt_Bereich_lage_riss_nummer = new javax.swing.JTextField();
        lblAlk_projekt_name = new javax.swing.JLabel();
        txtAlk_projekt_name = new javax.swing.JTextField();
        lblAlk_bem = new javax.swing.JLabel();
        lblAlk_top = new javax.swing.JLabel();
        jnAlk_top = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblAlk_geb = new javax.swing.JLabel();
        jnAlk_geb = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblAlk_nutz = new javax.swing.JLabel();
        jnAlk_nutz = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblAlk_bod = new javax.swing.JLabel();
        jnAlk_bod = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblAlk_son1 = new javax.swing.JLabel();
        lblAlk_ver = new javax.swing.JLabel();
        txtAlk_ver = new javax.swing.JTextField();
        lblTop_aussen_beginn = new javax.swing.JLabel();
        jxdTop_aussen_beginn = new org.jdesktop.swingx.JXDatePicker();
        lblTop_aussen_beendet = new javax.swing.JLabel();
        jxdTop_aussen_beendet = new org.jdesktop.swingx.JXDatePicker();
        lblAlk_luft = new javax.swing.JLabel();
        txtAlk_luft = new javax.swing.JTextField();
        lblAlk_son2 = new javax.swing.JLabel();
        txtAlk_son2 = new javax.swing.JTextField();
        lblTop_grund_beginn = new javax.swing.JLabel();
        jxdTop_grund_beginn = new org.jdesktop.swingx.JXDatePicker();
        lblTop_grund_beendet = new javax.swing.JLabel();
        jxdTop_grund_beendet = new org.jdesktop.swingx.JXDatePicker();
        lblPruef_gebeinmess_abgabe = new javax.swing.JLabel();
        jxdPruef_gebeinmess_abgabe = new org.jdesktop.swingx.JXDatePicker();
        lblPruef_gebeinmess_rueckgabe = new javax.swing.JLabel();
        jxdPruef_gebeinmess_rueckgabe = new org.jdesktop.swingx.JXDatePicker();
        lblAlk_ueb_beab = new javax.swing.JLabel();
        txtAlk_ueb_beab = new javax.swing.JTextField();
        lblAlk_beginn_dat = new javax.swing.JLabel();
        jxdAlk_beginn_dat = new org.jdesktop.swingx.JXDatePicker();
        lblAlk_ueb_dat = new javax.swing.JLabel();
        jxdAlk_ueb_dat = new org.jdesktop.swingx.JXDatePicker();
        lblRealn_ueb_von = new javax.swing.JLabel();
        txtRealn_ueb_von = new javax.swing.JTextField();
        lblRealn_ueb_am = new javax.swing.JLabel();
        jxdRealn_ueb_am = new org.jdesktop.swingx.JXDatePicker();
        lblLinkbase_ueb_von = new javax.swing.JLabel();
        txtLinkbase_ueb_von = new javax.swing.JTextField();
        lblLinkbase_ueb_am = new javax.swing.JLabel();
        jxdLinkbase_ueb_am = new org.jdesktop.swingx.JXDatePicker();
        lblDgk_rel = new javax.swing.JLabel();
        jnDgk_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblScan_dat = new javax.swing.JLabel();
        jxdScan_dat = new org.jdesktop.swingx.JXDatePicker();
        lblPruefung_von = new javax.swing.JLabel();
        txtPruefung_von = new javax.swing.JTextField();
        lblComliste_dat = new javax.swing.JLabel();
        jxdComliste_dat = new org.jdesktop.swingx.JXDatePicker();
        lblTim_erled_dat = new javax.swing.JLabel();
        jxdTim_erled_dat = new org.jdesktop.swingx.JXDatePicker();
        lblLaufzettel_erled_dat = new javax.swing.JLabel();
        jxdLaufzettel_erled_dat = new org.jdesktop.swingx.JXDatePicker();
        lblUebersicht_liste_erled_dat = new javax.swing.JLabel();
        jxdUebersicht_liste_erled_dat = new org.jdesktop.swingx.JXDatePicker();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblJumpSchlusspruefung = new javax.swing.JLabel();
        lblJumpAuftrag = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        lblTop_grund_bem = new javax.swing.JLabel();
        lblPruef_gebeinmess_bem = new javax.swing.JLabel();
        lblUebern_innen_bem = new javax.swing.JLabel();
        lblAbschluss_registrierung_bem = new javax.swing.JLabel();
        lblBodenschaetzung_ueb_von = new javax.swing.JLabel();
        txtBodenschaetzung_ueb_von = new javax.swing.JTextField();
        btnBodenschaetzung_ueb_von = new javax.swing.JButton();
        lblBodenschaetzung_ueb_am = new javax.swing.JLabel();
        jxdBodenschaetzung_ueb_am = new org.jdesktop.swingx.JXDatePicker();
        lblSchlusspruefung_von = new javax.swing.JLabel();
        txtSchlusspruefung_von = new javax.swing.JTextField();
        btnSchlusspruefung_von = new javax.swing.JButton();
        lblSchlusspruefung_am = new javax.swing.JLabel();
        jxdSchlusspruefung_am = new org.jdesktop.swingx.JXDatePicker();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        panKartographie = new javax.swing.JPanel();
        scpKartographie = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        lblStadt_rel = new javax.swing.JLabel();
        jnStadt_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblStadt_ent_beab = new javax.swing.JLabel();
        txtStadt_ent_beab = new javax.swing.JTextField();
        lblStadt_ent_dat = new javax.swing.JLabel();
        jxdStadt_ent_dat = new org.jdesktop.swingx.JXDatePicker();
        lblStadt_bem = new javax.swing.JLabel();
        txtStadt_bem = new javax.swing.JTextField();
        lblStadt_ueb_beab = new javax.swing.JLabel();
        txtStadt_ueb_beab = new javax.swing.JTextField();
        lblStadt_ueb_dat = new javax.swing.JLabel();
        jxdStadt_ueb_dat = new org.jdesktop.swingx.JXDatePicker();
        lblCity_rel = new javax.swing.JLabel();
        jnCity_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblCity_bem = new javax.swing.JLabel();
        txtCity_bem = new javax.swing.JTextField();
        lblCity_ueb_beab = new javax.swing.JLabel();
        txtCity_ueb_beab = new javax.swing.JTextField();
        lblCity_ueb_dat = new javax.swing.JLabel();
        jxdCity_ueb_dat = new org.jdesktop.swingx.JXDatePicker();
        lblUeber_rel = new javax.swing.JLabel();
        jnUeber_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblUeber_bem = new javax.swing.JLabel();
        txtUeber_bem = new javax.swing.JTextField();
        lblUeber_ueb_beab = new javax.swing.JLabel();
        txtUeber_ueb_beab = new javax.swing.JTextField();
        lblUeber_ueb_dat = new javax.swing.JLabel();
        jxdUeber_ueb_dat = new org.jdesktop.swingx.JXDatePicker();
        lblFreizeit_rel = new javax.swing.JLabel();
        jnFreizeit_rel = new de.cismet.cids.custom.objecteditors.commons.JaNeinNullCombo();
        lblFreizeit_bem = new javax.swing.JLabel();
        txtFreizeit_bem = new javax.swing.JTextField();
        lblFreizeit_ueb_beab = new javax.swing.JLabel();
        txtFreizeit_ueb_beab = new javax.swing.JTextField();
        lblFreizeit_ueb_dat = new javax.swing.JLabel();
        jxdFreizeit_ueb_dat = new org.jdesktop.swingx.JXDatePicker();
        lblJumpAuftrag1 = new javax.swing.JLabel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        lblJumpAuftrag2 = new javax.swing.JLabel();
        lblJumpAuftrag3 = new javax.swing.JLabel();
        lblJumpAuftrag4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        cboGeom = new DefaultCismapGeometryComboBoxEditor();
        lblGeom = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        cmdAddSgk = new javax.swing.JButton();
        cmdAddKart = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        jLabel1.setText("jLabel1");

        setLayout(new java.awt.GridBagLayout());

        lblName.setText("Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndName");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtName, gridBagConstraints);

        lblHinweis.setText("Hinweise");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblHinweis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hinweis}"),
                txtHinweis,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndHinweis");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtHinweis, gridBagConstraints);

        lblEin_beab.setText("von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblEin_beab, gridBagConstraints);

        txtEin_beab.setEditable(false);
        txtEin_beab.setPreferredSize(new java.awt.Dimension(200, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ein_beab}"),
                txtEin_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndEin_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtEin_beab, gridBagConstraints);

        lblEin_dat.setText("angelegt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblEin_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ein_dat}"),
                jxdEin_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndEin_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jxdEin_dat, gridBagConstraints);

        lblLoe_beab.setText("von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(lblLoe_beab, gridBagConstraints);

        txtLoe_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.loe_beab}"),
                txtLoe_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndLoe_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtLoe_beab, gridBagConstraints);

        lblLoe_dat.setText("erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblLoe_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.loe_dat}"),
                jxdLoe_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndLoe_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jxdLoe_dat, gridBagConstraints);

        lblLoe_grund.setText("Bemerkung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblLoe_grund, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.loe_grund}"),
                txtLoe_grund,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndLoe_grund");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(txtLoe_grund, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        add(jButton1, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        add(jButton2, gridBagConstraints);

        tbpAdditionalInfo.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        tbpAdditionalInfo.setPreferredSize(new java.awt.Dimension(1000, 103));

        panStadtgrundkarte.setFocusable(false);
        panStadtgrundkarte.setOpaque(false);
        panStadtgrundkarte.setLayout(new java.awt.BorderLayout());

        scpStadtgrundkarte.setOpaque(false);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblAlkis_rel.setText("ALKIS relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlkis_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alkis_rel}"),
                jnAlkis_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndAlkis_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnAlkis_rel, gridBagConstraints);

        lblAuftraggeber.setText("Auftraggeber");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAuftraggeber, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.auftraggeber}"),
                txtAuftraggeber,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAuftraggeber");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAuftraggeber, gridBagConstraints);

        lblAlk_ent_beab.setText("Objekt angelegt von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_ent_beab, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_ent_beab}"),
                txtAlk_ent_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_ent_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_ent_beab, gridBagConstraints);

        lblAlk_ent_dat.setText("Eingang am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_ent_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_ent_dat}"),
                jxdAlk_ent_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndAlk_ent_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdAlk_ent_dat, gridBagConstraints);

        lblAlk_prio.setText("Priorität");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_prio, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_prio}"),
                txtAlk_prio,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_prio");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_prio, gridBagConstraints);

        lblBereich_lage_riss.setText("Bereich, Lagebezeichnung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblBereich_lage_riss, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.bereich_lage_riss}"),
                txtBereich_lage_riss,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndBereich_lage_riss");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtBereich_lage_riss, gridBagConstraints);

        lbl_Bereich_lage_riss_nummer.setText("Riss-Nummer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lbl_Bereich_lage_riss_nummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.riss_nummer}"),
                txt_Bereich_lage_riss_nummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txt_Bereich_lage_riss_nummer, gridBagConstraints);

        lblAlk_projekt_name.setText("ALKIS-Projektname");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_projekt_name, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_bem}"),
                txtAlk_projekt_name,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_bem");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_projekt_name, gridBagConstraints);

        lblAlk_bem.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_bem, gridBagConstraints);

        lblAlk_top.setText("Topographie");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_top, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_top}"),
                jnAlk_top,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndAlk_top");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnAlk_top, gridBagConstraints);

        lblAlk_geb.setText("Gebäude");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_geb, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_geb}"),
                jnAlk_geb,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndAlk_geb");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnAlk_geb, gridBagConstraints);

        lblAlk_nutz.setText("Nutzungsarten");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_nutz, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_nutz}"),
                jnAlk_nutz,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndAlk_nutz");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnAlk_nutz, gridBagConstraints);

        lblAlk_bod.setText("Bodenschätzung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_bod, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_bod}"),
                jnAlk_bod,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndAlk_bod");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnAlk_bod, gridBagConstraints);

        lblAlk_son1.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_son1, gridBagConstraints);

        lblAlk_ver.setText("Außendienst Bearbeiter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_ver, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_ver}"),
                txtAlk_ver,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_ver");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_ver, gridBagConstraints);

        lblTop_aussen_beginn.setText("begonnen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTop_aussen_beginn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_aussen_beginn}"),
                jxdTop_aussen_beginn,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndTop_aussen_beginn");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdTop_aussen_beginn, gridBagConstraints);

        lblTop_aussen_beendet.setText("beendet am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTop_aussen_beendet, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_aussen_beendet}"),
                jxdTop_aussen_beendet,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndTop_aussen_beendet");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdTop_aussen_beendet, gridBagConstraints);

        lblAlk_luft.setText("Luftbildauswertung / sonstige Erfassung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_luft, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_luft}"),
                txtAlk_luft,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_luft");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 22;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_luft, gridBagConstraints);

        lblAlk_son2.setText("Bearbeiter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_son2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_son2}"),
                txtAlk_son2,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_son2");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_son2, gridBagConstraints);

        lblTop_grund_beginn.setText("begonnen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTop_grund_beginn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_grund_beginn}"),
                jxdTop_grund_beginn,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndTop_grund_beginn");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 23;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdTop_grund_beginn, gridBagConstraints);

        lblTop_grund_beendet.setText("beendet am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTop_grund_beendet, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_grund_beendet}"),
                jxdTop_grund_beendet,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndTop_grund_beendet");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 24;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdTop_grund_beendet, gridBagConstraints);

        lblPruef_gebeinmess_abgabe.setText("Prüfung Geb.-Einm., Abgabe am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblPruef_gebeinmess_abgabe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.pruef_gebeinmess_abgabe}"),
                jxdPruef_gebeinmess_abgabe,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndPruef_gebeinmess_abgabe");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdPruef_gebeinmess_abgabe, gridBagConstraints);

        lblPruef_gebeinmess_rueckgabe.setText("Prüfung Geb.-Einm., Rückgabe am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblPruef_gebeinmess_rueckgabe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.pruef_gebeinmess_rueckgabe}"),
                jxdPruef_gebeinmess_rueckgabe,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndPruef_gebeinmess_rueckgabe");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 29;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdPruef_gebeinmess_rueckgabe, gridBagConstraints);

        lblAlk_ueb_beab.setText("Topografie übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 34;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_ueb_beab, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_ueb_beab}"),
                txtAlk_ueb_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndAlk_ueb_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 34;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtAlk_ueb_beab, gridBagConstraints);

        lblAlk_beginn_dat.setText("Topografie begonnen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 35;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_beginn_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_beginn_dat}"),
                jxdAlk_beginn_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndAlk_beginn_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 35;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdAlk_beginn_dat, gridBagConstraints);

        lblAlk_ueb_dat.setText("Topografie erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 36;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAlk_ueb_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.alk_ueb_dat}"),
                jxdAlk_ueb_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndAlk_ueb_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 36;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdAlk_ueb_dat, gridBagConstraints);

        lblRealn_ueb_von.setText("Tatsächliche Nutzung übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 40;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblRealn_ueb_von, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.realn_ueb_von}"),
                txtRealn_ueb_von,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndRealn_ueb_von");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 40;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtRealn_ueb_von, gridBagConstraints);

        lblRealn_ueb_am.setText("Tatsächliche Nutzung übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 41;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblRealn_ueb_am, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.realn_ueb_am}"),
                jxdRealn_ueb_am,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndRealn_ueb_am");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 41;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdRealn_ueb_am, gridBagConstraints);

        lblLinkbase_ueb_von.setText("Übernahme in Rissarchiv von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 46;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblLinkbase_ueb_von, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.linkbase_ueb_von}"),
                txtLinkbase_ueb_von,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndLinkbase_ueb_von");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 46;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtLinkbase_ueb_von, gridBagConstraints);

        lblLinkbase_ueb_am.setText("Übernahme in Rissarchiv am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 47;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblLinkbase_ueb_am, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.linkbase_ueb_am}"),
                jxdLinkbase_ueb_am,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndLinkbase_ueb_am");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 47;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdLinkbase_ueb_am, gridBagConstraints);

        lblDgk_rel.setText("ABK relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 48;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblDgk_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.dgk_rel}"),
                jnDgk_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndDgk_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 48;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jnDgk_rel, gridBagConstraints);

        lblScan_dat.setText("gescannt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 51;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblScan_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.scan_dat}"),
                jxdScan_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndScan_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 51;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdScan_dat, gridBagConstraints);

        lblPruefung_von.setText("Abschlussprüfung von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 55;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblPruefung_von, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.pruefung_von}"),
                txtPruefung_von,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndPruefung_von");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 55;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtPruefung_von, gridBagConstraints);

        lblComliste_dat.setText("Projektarchivierung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 56;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblComliste_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.comliste_dat}"),
                jxdComliste_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndComliste_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 56;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdComliste_dat, gridBagConstraints);

        lblTim_erled_dat.setText("TIM erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 57;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTim_erled_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.tim_erled_dat}"),
                jxdTim_erled_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndTim_erled_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 57;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdTim_erled_dat, gridBagConstraints);

        lblLaufzettel_erled_dat.setText("Laufzettel erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 58;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblLaufzettel_erled_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.laufzettel_erled_dat}"),
                jxdLaufzettel_erled_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndLaufzettel_erled_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 58;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdLaufzettel_erled_dat, gridBagConstraints);

        lblUebersicht_liste_erled_dat.setText("Übersicht und Liste erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 59;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblUebersicht_liste_erled_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.uebersicht_liste_erled_dat}"),
                jxdUebersicht_liste_erled_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndUebersicht_liste_erled_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 59;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdUebersicht_liste_erled_dat, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel2.setText("<html>Topographie Erfassung<br>Außendienst</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel2.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel3.setText("<html>Topographie Erfassung<br>andere Grundlagen</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel2.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel4.setText("<html>Prüfung<br>Gebäudeeinmessung</html>");
        jLabel4.setPreferredSize(new java.awt.Dimension(140, 44));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 28;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel2.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        jLabel5.setText("<html>Übernahme<br>Innendienst</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 33;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel2.add(jLabel5, gridBagConstraints);

        lblJumpSchlusspruefung.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpSchlusspruefung.setText("<html>Abschluss und<br />Registrierung</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 55;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblJumpSchlusspruefung, gridBagConstraints);

        lblJumpAuftrag.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpAuftrag.setText("Auftrag");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel2.add(lblJumpAuftrag, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));
        jPanel3.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 27;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jPanel4, gridBagConstraints);

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));
        jPanel5.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 54;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jPanel5, gridBagConstraints);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        jPanel6.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel6.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 32;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jPanel6, gridBagConstraints);

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));
        jPanel7.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel7.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jPanel7, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton3, gridBagConstraints);

        jButton4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 34;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton4, gridBagConstraints);

        jButton5.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 46;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton5, gridBagConstraints);

        jButton7.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton7ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 40;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton7, gridBagConstraints);

        jButton9.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton9ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 55;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton9, gridBagConstraints);

        jButton10.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton10.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton10ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 21;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(jButton10, gridBagConstraints);

        lblTop_grund_bem.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblTop_grund_bem, gridBagConstraints);

        lblPruef_gebeinmess_bem.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblPruef_gebeinmess_bem, gridBagConstraints);

        lblUebern_innen_bem.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 52;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblUebern_innen_bem, gridBagConstraints);

        lblAbschluss_registrierung_bem.setText("Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 60;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblAbschluss_registrierung_bem, gridBagConstraints);

        lblBodenschaetzung_ueb_von.setText("Bodenschätzung übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 42;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblBodenschaetzung_ueb_von, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.bodenschaetzung_ueb_von}"),
                txtBodenschaetzung_ueb_von,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 42;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtBodenschaetzung_ueb_von, gridBagConstraints);

        btnBodenschaetzung_ueb_von.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        btnBodenschaetzung_ueb_von.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBodenschaetzung_ueb_vonActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 42;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(btnBodenschaetzung_ueb_von, gridBagConstraints);

        lblBodenschaetzung_ueb_am.setText("Bodenschätzung übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 43;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblBodenschaetzung_ueb_am, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.bodenschaetzung_ueb_am}"),
                jxdBodenschaetzung_ueb_am,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 43;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdBodenschaetzung_ueb_am, gridBagConstraints);

        lblSchlusspruefung_von.setText("Schlussprüfung von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 44;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblSchlusspruefung_von, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.schlusspruefung_von}"),
                txtSchlusspruefung_von,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 44;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(txtSchlusspruefung_von, gridBagConstraints);

        btnSchlusspruefung_von.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        btnSchlusspruefung_von.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnSchlusspruefung_vonActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 44;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel2.add(btnSchlusspruefung_von, gridBagConstraints);

        lblSchlusspruefung_am.setText("Schlussprüfung am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 45;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(lblSchlusspruefung_am, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.schlusspruefung_am}"),
                jxdSchlusspruefung_am,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 45;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jxdSchlusspruefung_am, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_aussen_bem}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.top_grund_bem}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 25;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane2, gridBagConstraints);

        jTextArea3.setColumns(20);
        jTextArea3.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.uebernahme_innendienst_bem}"),
                jTextArea3,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(jTextArea3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 52;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        jTextArea4.setColumns(20);
        jTextArea4.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.abschluss_registrierung_bem}"),
                jTextArea4,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane4.setViewportView(jTextArea4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 60;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane4, gridBagConstraints);

        jTextArea5.setColumns(20);
        jTextArea5.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.pruef_gebeinmess_bem}"),
                jTextArea5,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane5.setViewportView(jTextArea5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 30;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane5, gridBagConstraints);

        jTextArea6.setColumns(20);
        jTextArea6.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis.auftrag_bemerkungen}"),
                jTextArea6,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane6.setViewportView(jTextArea6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel2.add(jScrollPane6, gridBagConstraints);

        scpStadtgrundkarte.setViewportView(jPanel2);

        panStadtgrundkarte.add(scpStadtgrundkarte, java.awt.BorderLayout.CENTER);

        tbpAdditionalInfo.addTab(
            "ALKIS",
            new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wbf_vorgang.png")),
            panStadtgrundkarte); // NOI18N

        panKartographie.setOpaque(false);
        panKartographie.setLayout(new java.awt.BorderLayout());

        scpKartographie.setOpaque(false);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        lblStadt_rel.setText("Stadtkarte relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_rel}"),
                jnStadt_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndStadt_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jnStadt_rel, gridBagConstraints);

        lblStadt_ent_beab.setText("Stadtkarte entschieden von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_ent_beab, gridBagConstraints);

        txtStadt_ent_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_ent_beab}"),
                txtStadt_ent_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndStadt_ent_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtStadt_ent_beab, gridBagConstraints);

        lblStadt_ent_dat.setText("Stadtkarte entschieden am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_ent_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_ent_dat}"),
                jxdStadt_ent_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndStadt_ent_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jxdStadt_ent_dat, gridBagConstraints);

        lblStadt_bem.setText("Stadtkarte Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_bem, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_bem}"),
                txtStadt_bem,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndStadt_bem");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtStadt_bem, gridBagConstraints);

        lblStadt_ueb_beab.setText("Stadtkarte übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_ueb_beab, gridBagConstraints);

        txtStadt_ueb_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_ueb_beab}"),
                txtStadt_ueb_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndStadt_ueb_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtStadt_ueb_beab, gridBagConstraints);

        lblStadt_ueb_dat.setText("Stadtkarte übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblStadt_ueb_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.stadt_ueb_dat}"),
                jxdStadt_ueb_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndStadt_ueb_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jxdStadt_ueb_dat, gridBagConstraints);

        lblCity_rel.setText("Citypläne relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblCity_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.city_rel}"),
                jnCity_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndCity_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jnCity_rel, gridBagConstraints);

        lblCity_bem.setText("Citypläne Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblCity_bem, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.city_bem}"),
                txtCity_bem,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndCity_bem");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtCity_bem, gridBagConstraints);

        lblCity_ueb_beab.setText("Citypläne übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblCity_ueb_beab, gridBagConstraints);

        txtCity_ueb_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.city_ueb_beab}"),
                txtCity_ueb_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndCity_ueb_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtCity_ueb_beab, gridBagConstraints);

        lblCity_ueb_dat.setText("Citypläne übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblCity_ueb_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.city_ueb_dat}"),
                jxdCity_ueb_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndCity_ueb_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jxdCity_ueb_dat, gridBagConstraints);

        lblUeber_rel.setText("Übersichtspläne relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblUeber_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.ueber_rel}"),
                jnUeber_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndUeber_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jnUeber_rel, gridBagConstraints);

        lblUeber_bem.setText("Übersichtspläne Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblUeber_bem, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.ueber_bem}"),
                txtUeber_bem,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndUeber_bem");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtUeber_bem, gridBagConstraints);

        lblUeber_ueb_beab.setText("Übersichtspläne übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblUeber_ueb_beab, gridBagConstraints);

        txtUeber_ueb_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.ueber_ueb_beab}"),
                txtUeber_ueb_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndUeber_ueb_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtUeber_ueb_beab, gridBagConstraints);

        lblUeber_ueb_dat.setText("Übersichtspläne übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblUeber_ueb_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.ueber_ueb_dat}"),
                jxdUeber_ueb_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndUeber_ueb_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jxdUeber_ueb_dat, gridBagConstraints);

        lblFreizeit_rel.setText("Freizeitkarte relevant");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblFreizeit_rel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.freizeit_rel}"),
                jnFreizeit_rel,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndFreizeit_rel");
        binding.setSourceNullValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jnFreizeit_rel, gridBagConstraints);

        lblFreizeit_bem.setText("Freizeitkarte Bemerkungen");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblFreizeit_bem, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.freizeit_bem}"),
                txtFreizeit_bem,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndFreizeit_bem");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtFreizeit_bem, gridBagConstraints);

        lblFreizeit_ueb_beab.setText("Freizeitkarte übernommen von");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblFreizeit_ueb_beab, gridBagConstraints);

        txtFreizeit_ueb_beab.setEditable(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.freizeit_ueb_beab}"),
                txtFreizeit_ueb_beab,
                org.jdesktop.beansbinding.BeanProperty.create("text"),
                "bndFreizeit_ueb_beab");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(txtFreizeit_ueb_beab, gridBagConstraints);

        lblFreizeit_ueb_dat.setText("Freizeitkarte übernommen am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(lblFreizeit_ueb_dat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kart.freizeit_ueb_dat}"),
                jxdFreizeit_ueb_dat,
                org.jdesktop.beansbinding.BeanProperty.create("date"),
                "bndFreizeit_ueb_dat");
        binding.setConverter(new de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 20;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jxdFreizeit_ueb_dat, gridBagConstraints);

        lblJumpAuftrag1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpAuftrag1.setText("Citypläne");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel1.add(lblJumpAuftrag1, gridBagConstraints);

        jButton11.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton11.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton11ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 2, 2);
        jPanel1.add(jButton11, gridBagConstraints);

        jButton12.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton12.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton12ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 3);
        jPanel1.add(jButton12, gridBagConstraints);

        jButton13.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton13.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton13ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 3);
        jPanel1.add(jButton13, gridBagConstraints);

        jButton14.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton14.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton14ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 3);
        jPanel1.add(jButton14, gridBagConstraints);

        jButton15.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/user_green.png"))); // NOI18N
        jButton15.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton15ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 3, 3);
        jPanel1.add(jButton15, gridBagConstraints);

        lblJumpAuftrag2.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpAuftrag2.setText("Übersichtspläne");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel1.add(lblJumpAuftrag2, gridBagConstraints);

        lblJumpAuftrag3.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpAuftrag3.setText("Freizeitkarte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel1.add(lblJumpAuftrag3, gridBagConstraints);

        lblJumpAuftrag4.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        lblJumpAuftrag4.setText("Stadtkarte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 20);
        jPanel1.add(lblJumpAuftrag4, gridBagConstraints);

        jPanel8.setBackground(new java.awt.Color(51, 51, 51));
        jPanel8.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel8.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jPanel8, gridBagConstraints);

        jPanel9.setBackground(new java.awt.Color(51, 51, 51));
        jPanel9.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel9.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jPanel9, gridBagConstraints);

        jPanel11.setBackground(new java.awt.Color(51, 51, 51));
        jPanel11.setMinimumSize(new java.awt.Dimension(10, 3));
        jPanel11.setPreferredSize(new java.awt.Dimension(100, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel1.add(jPanel11, gridBagConstraints);

        scpKartographie.setViewportView(jPanel1);

        panKartographie.add(scpKartographie, java.awt.BorderLayout.CENTER);

        tbpAdditionalInfo.addTab(
            " Kartographie ",
            new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wbf_vorgang.png")),
            panKartographie); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(tbpAdditionalInfo, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"),
                cboGeom,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"),
                "bndGeometrie");
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cboGeom).getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(cboGeom, gridBagConstraints);

        lblGeom.setText("erledigt am");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(lblGeom, gridBagConstraints);

        jPanel10.setOpaque(false);

        cmdAddSgk.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        cmdAddSgk.setText("ALKIS");
        cmdAddSgk.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddSgkActionPerformed(evt);
                }
            });
        jPanel10.add(cmdAddSgk);

        cmdAddKart.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        cmdAddKart.setText("Kartographie");
        cmdAddKart.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAddKartActionPerformed(evt);
                }
            });
        jPanel10.add(cmdAddKart);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(jPanel10, gridBagConstraints);

        jCheckBox1.setText("Straße neu / Umbenennung");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse_neu_umbenennung}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"),
                "bndStrasse_neu_umbenennung");
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(jCheckBox1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getCurrentUser() {
        return SessionManager.getSession().getUser().getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        txtEin_beab.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        txtLoe_beab.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        txtAlk_ent_beab.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        txtAlk_ueb_beab.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        txtLinkbase_ueb_von.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton7ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton7ActionPerformed
        txtRealn_ueb_von.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton7ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton9ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton9ActionPerformed
        txtPruefung_von.setText(getCurrentUser());
    }                                                                            //GEN-LAST:event_jButton9ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton10ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton10ActionPerformed
        txtAlk_son2.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton10ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddKartActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddKartActionPerformed
        try {
            cidsBean.fillEmptyFieldWithEmptySubInstance("kart");
        } catch (Exception e) {
            log.error("Error beim Erzeugen eines Kart Objektes", e);
        }
    }                                                                              //GEN-LAST:event_cmdAddKartActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAddSgkActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAddSgkActionPerformed
        try {
            cidsBean.fillEmptyFieldWithEmptySubInstance("alkis");
        } catch (Exception e) {
            log.error("Error beim Erzeugen eines Alkis Objektes", e);
        }
    }                                                                             //GEN-LAST:event_cmdAddSgkActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton11ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton11ActionPerformed
        txtStadt_ent_beab.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton11ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton12ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton12ActionPerformed
        txtStadt_ueb_beab.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton12ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton13ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton13ActionPerformed
        txtCity_ueb_beab.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton13ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton14ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton14ActionPerformed
        txtUeber_ueb_beab.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton14ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton15ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton15ActionPerformed
        txtFreizeit_ueb_beab.setText(getCurrentUser());
    }                                                                             //GEN-LAST:event_jButton15ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBodenschaetzung_ueb_vonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBodenschaetzung_ueb_vonActionPerformed
        txtBodenschaetzung_ueb_von.setText(getCurrentUser());
    }                                                                                              //GEN-LAST:event_btnBodenschaetzung_ueb_vonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnSchlusspruefung_vonActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnSchlusspruefung_vonActionPerformed
        txtSchlusspruefung_von.setText(getCurrentUser());
    }                                                                                          //GEN-LAST:event_btnSchlusspruefung_vonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return "TIM Liegenschaftskarte";
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(final String title) {
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        super.dispose();
        ((DefaultCismapGeometryComboBoxEditor)cboGeom).dispose();
        cidsBean.removePropertyChangeListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "kif",
                "tim_lieg",
                2003,
                1200,
                800);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        log.fatal("getBeanInitializer");
        return new CompleteBeanInitializer(cidsBean);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("kart")) {
            if ((evt.getOldValue() == null) && (evt.getNewValue() != null)) {
                tbpAdditionalInfo.addTab(
                    " Kartographie ",
                    new javax.swing.ImageIcon(
                        getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wbf_vorgang.png")),
                    panKartographie);    // NOI18N
                cmdAddKart.setVisible(false);
            }
        } else if (evt.getPropertyName().equals("alkis")) {
            if ((evt.getOldValue() == null) && (evt.getNewValue() != null)) {
                tbpAdditionalInfo.addTab(
                    " ALKIS ",
                    new javax.swing.ImageIcon(
                        getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/wbf_vorgang.png")),
                    panStadtgrundkarte); // NOI18N
                cmdAddSgk.setVisible(false);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CompleteBeanInitializer extends DefaultBeanInitializer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CompleteBeanInitializer object.
         *
         * @param  template  DOCUMENT ME!
         */
        public CompleteBeanInitializer(final CidsBean template) {
            super(template);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected void processComplexProperty(final CidsBean beanToInit,
                final String propertyName,
                final CidsBean complexValueToProcess) throws Exception {
            if (complexValueToProcess != null) {
                final CompleteBeanInitializer subInitializer = new CompleteBeanInitializer(complexValueToProcess);
                final CidsBean newBean = complexValueToProcess.getMetaObject()
                            .getMetaClass()
                            .getEmptyInstance()
                            .getBean();
                subInitializer.initializeBean(newBean);
                beanToInit.setProperty(propertyName, newBean);
            }
        }
    }
}
