/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.sql.Timestamp;

import java.text.DateFormat;

import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.custom.deprecated.JLoadDots;

import de.cismet.cids.tools.metaobjectrenderer.CoolPanel;

import de.cismet.tools.gui.RoundedPanel;

/**
 * de.cismet.cids.objectrenderer.CoolKaufvertragRenderer.
 *
 * <p>Renderer speziell fuer Kaufvertraege und deren Unterklassen.</p>
 *
 * @author   nhaffke
 * @version  $Revision$, $Date$
 */
public class KaufvertragRenderer extends CoolPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String VERTRAG = "Vertrag";

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("NAME")
    public String name = "";

    @CidsAttribute("VERTRAGSTYP")
    public String vertragstyp = "";

    @CidsAttribute("REG_BEZ")
    public String reg_bez = "";

    @CidsAttribute("REG_JAHR")
    public String reg_jahr = "";

    @CidsAttribute("VERKAUFSDATUM.FROMTS")
    public Timestamp verkaufsdatFrom = null;
//
//    @CidsAttribute("VERKAUFSDATUM.TOTS")
//    public Timestamp verkaufsdatTo = null;

    @CidsAttribute("ADRESSE")
    public String adresse = "";

    @CidsAttribute("OBJEKTART")
    public String objektart = "";

    @CidsAttribute("TEILMARKT")
    public String teilmarkt = "";

    @CidsAttribute("GESAMTFLAECHE")
    public Float gesamtfl;

    @CidsAttribute("GESAMTTEILFLAECHE")
    public Float gesamtteilfl;

    @CidsAttribute("KAUFPREIS_PRO_QM")
    public Double kaufpreisQm;

    @CidsAttribute("KAUFPREIS_ABSOLUT")
    public Double kaufpreisAbs;

    @CidsAttribute("ZUR_AUSWERTUNNG_GEEIGNET")
    public String auswertung = "";

    @CidsAttribute("SACHWERTE")
    public Object sachwerte = null;

    @CidsAttribute("SACHWERTE.ROHERTRAG")
    public Float sachRohertrag;

    @CidsAttribute("SACHWERTE.BEREINIGTER_KAUFPREIS_WF_NF")
    public Float sachKaufWfNf;

    @CidsAttribute("SACHWERTE.BEREINIGTER_KAUFPREIS_ROHERTRAG")
    public Float sachKaufRohertrag;

    @CidsAttribute("SACHWERTE.MARKTANPASSUNGSFAKTOR")
    public Float sachMarktanp;

    @CidsAttribute("SACHWERTE.LIEGENSCHAFTSZINSSATZ")
    public Float sachLiegenschaft;

    @CidsAttribute("FLURSTUECKE")
    public Object flurstuecke = null;

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.GEMARKUNGSNAME")
    public Vector<String> flurName = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.GEMARKUNGS_NR")
    public Vector<String> flurNr = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.FLUR")
    public Vector<String> flurFlur = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.FSTNR_Z")
    public Vector<String> flurZ = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.FSTNR_N")
    public Vector<String> flurN = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.FLAECHE_FLURSTUECK")
    public Vector<Float> flurFlaeche = new Vector();

    @CidsAttribute("FLURSTUECKE[].FLURSTUECK.TEILFLAECHE_FLURSTUECK")
    public Vector<Float> flurTeilflaeche = new Vector();

    @CidsAttribute("GEBAUEDE")
    public Object gebaeude = null;

    @CidsAttribute("GEBAUEDE[].GEBAEUDE_ID.BEZEICHNUNG")
    public Vector<String> gebBez = new Vector();

    @CidsAttribute("GEBAUEDE[].GEBAEUDE_ID.BAUJAHR")
    public Vector<Integer> gebBaujahr = new Vector();

    @CidsAttribute("GEBAUEDE[].GEBAEUDE_ID.WF_GEBAEUDE_QM")
    public Vector<Float> gebWfqm = new Vector();

    @CidsAttribute("GEBAUEDE[].GEBAEUDE_ID.NF_GEBAEUDE_QM")
    public Vector<Float> gebNfqm = new Vector();

    @CidsAttribute("GEBAUEDE[].GEBAEUDE_ID.WF_NF")
    public Vector<Float> gebWfnf = new Vector();

    @CidsAttribute("TEILEIGENTUM")
    public Object teileigentum = null;

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.ANZAHL_GESCHOSSE")
    public Vector<Integer> eigAnzGesch = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.REL_KAUFPREIS")
    public Vector<Float> eigRelKauf = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.TEILMARKT")
    public Vector<String> eigTeilmarkt = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.VERMIETUNGSSITUATION")
    public Vector<String> eigVermietung = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.AUSSTATTUNGSTSANDARD")
    public Vector<String> eigAusstattung = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.MODERNISIERUNGSJAHR")
    public Vector<Integer> eigModern = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.RAUMZAHL")
    public Vector<Integer> eigRaumzahl = new Vector();

    @CidsAttribute("TEILEIGENTUM[].TEILEIGENTUM_ID.WOHNLAGE")
    public Vector<String> eigWohnlage = new Vector();

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geometry = null;

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblAdresse;
    private javax.swing.JLabel lblAuswertung;
    private javax.swing.JLabel lblGesamtflaeche;
    private javax.swing.JLabel lblGesamtteilflaeche;
    private javax.swing.JLabel lblKaufpreisAbs;
    private javax.swing.JLabel lblKaufpreisQm;
    private javax.swing.JLabel lblObjektart;
    private javax.swing.JLabel lblRegBez;
    private javax.swing.JLabel lblRegJahr;
    private javax.swing.JLabel lblSachKaufpreis;
    private javax.swing.JLabel lblSachKaufpreisRoh;
    private javax.swing.JLabel lblSachMarktanp;
    private javax.swing.JLabel lblSachRohertrag;
    private javax.swing.JLabel lblSachZinssatz;
    private javax.swing.JLabel lblTeilmarkt;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblVerkaufsdat;
    private javax.swing.JPanel panFlurstuecke;
    private javax.swing.JPanel panGebaeude;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSachwerte;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTeileigentum;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Konstruktor.
     */
    public KaufvertragRenderer() {
        initComponents();
        setPanContent(panMain);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Uebergibt das Geometrieobjekt an die Oberklasse.
     *
     * @param  geometry  GeoReferenz des Kaufvertrags
     */
    @Override
    public void setGeometry(final Geometry geometry) {
        super.setGeometry(geometry);
    }

    @Override
    public void assignAggregation() {
    }

    @Override
    public void assignSingle() {
        if (!name.equals("")) {
            if (!vertragstyp.equals("")) {
                lblTitle.setText(vertragstyp + " - " + name);
            } else {
                lblTitle.setText(VERTRAG + " - " + name);
            }
        } else {
            lblTitle.setText(VERTRAG);
        }

        if (!reg_bez.equals("")) {
            lblRegBez.setText(reg_bez);
        } else {
            lblRegBez.setVisible(false);
            jLabel1.setVisible(false);
        }

        if (!reg_jahr.equals("")) {
            lblRegJahr.setText(reg_jahr);
        } else {
            lblRegJahr.setVisible(false);
            jLabel2.setVisible(false);
        }

        // BUG BUG BUG
        if (verkaufsdatFrom != null) {
            lblVerkaufsdat.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                    verkaufsdatFrom));
        } else {
            lblVerkaufsdat.setVisible(false);
            jLabel3.setVisible(false);
        }

        if ((adresse != null) && !adresse.equals("")) {
            lblAdresse.setText(adresse);
        } else {
            lblAdresse.setVisible(false);
            jLabel4.setVisible(false);
        }

        if (!objektart.equals("")) {
            lblObjektart.setText(objektart);
        } else {
            lblObjektart.setVisible(false);
            jLabel5.setVisible(false);
        }

        if (!teilmarkt.equals("")) {
            lblTeilmarkt.setText(teilmarkt);
        } else {
            lblTeilmarkt.setVisible(false);
            jLabel6.setVisible(false);
        }

        if ((gesamtfl != null) && (gesamtfl > -1.0f)) {
            lblGesamtflaeche.setText(gesamtfl.toString());
        } else {
            lblGesamtflaeche.setVisible(false);
            jLabel7.setVisible(false);
        }

        if ((gesamtteilfl != null) && (gesamtteilfl > -1.0f)) {
            lblGesamtteilflaeche.setText(gesamtteilfl.toString());
        } else {
            lblGesamtteilflaeche.setVisible(false);
            jLabel8.setVisible(false);
        }

        if ((kaufpreisQm != null) && (kaufpreisQm > -1.0d)) {
            lblKaufpreisQm.setText(kaufpreisQm.toString());
        } else {
            lblKaufpreisQm.setVisible(false);
            jLabel9.setVisible(false);
        }

        if ((kaufpreisAbs != null) && (kaufpreisAbs > -1.0d)) {
            lblKaufpreisAbs.setText(kaufpreisAbs.toString());
        } else {
            lblKaufpreisAbs.setVisible(false);
            jLabel10.setVisible(false);
        }

        if ((auswertung != null) && !auswertung.equals("")) {
            lblAuswertung.setText(auswertung);
        } else {
            lblAuswertung.setVisible(false);
            jLabel11.setVisible(false);
        }

        // Sachwerte-Panel fuellen
        if ((sachwerte != null) && (sachRohertrag != null)) {
            if (sachRohertrag > -1.0f) {
                lblSachRohertrag.setText(sachRohertrag.toString());
            } else {
                lblSachRohertrag.setVisible(false);
                jLabel13.setVisible(false);
            }

            if ((sachKaufWfNf != null) && (sachKaufWfNf > -1.0f)) {
                lblSachKaufpreis.setText(sachKaufWfNf.toString());
            } else {
                lblSachKaufpreis.setVisible(false);
                jLabel14.setVisible(false);
            }

            if ((sachKaufRohertrag != null) && (sachKaufRohertrag > -1.0f)) {
                lblSachKaufpreisRoh.setText(sachKaufRohertrag.toString());
            } else {
                lblSachKaufpreisRoh.setVisible(false);
                jLabel15.setVisible(false);
            }

            if ((sachMarktanp != null) && (sachMarktanp > -1.0f)) {
                lblSachMarktanp.setText(sachMarktanp.toString());
            } else {
                lblSachMarktanp.setVisible(false);
                jLabel16.setVisible(false);
            }

            if ((sachLiegenschaft != null) && (sachLiegenschaft > -1.0f)) {
                lblSachZinssatz.setText(sachLiegenschaft.toString());
            } else {
                lblSachZinssatz.setVisible(false);
                jLabel17.setVisible(false);
            }
        } else {
            panSachwerte.setVisible(false);
            jLabel12.setVisible(false);
        }

        if ((flurstuecke != null) && (flurName.size() > 0)) {
            final int anzahl = flurName.size();
            if ((anzahl % 2) == 0) {
                panFlurstuecke.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panFlurstuecke.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (int i = 0; i < anzahl; ++i) {
                panFlurstuecke.add(createFlurstueckPanel(i));
            }
        } else {
            panFlurstuecke.setVisible(false);
            jLabel18.setVisible(false);
        }

        if ((gebaeude != null) && (gebBez.size() > 0)) {
            final int anzahl = gebBez.size();
            if ((anzahl % 2) == 0) {
                panGebaeude.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panGebaeude.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (int i = 0; i < anzahl; ++i) {
                panGebaeude.add(createGebaeudePanel(i));
            }
        } else {
            panGebaeude.setVisible(false);
            jLabel19.setVisible(false);
        }

        if ((teileigentum != null) && (eigWohnlage.size() > 0)) {
            final int anzahl = eigWohnlage.size();
            if ((anzahl % 2) == 0) {
                panTeileigentum.setLayout(new GridLayout(anzahl / 2, 2, 5, 5));
            } else {
                panTeileigentum.setLayout(new GridLayout((anzahl + 1) / 2, 2, 5, 5));
            }
            for (int i = 0; i < anzahl; ++i) {
                panTeileigentum.add(createTeileigentumPanel(i));
            }
        } else {
            panTeileigentum.setVisible(false);
            jLabel20.setVisible(false);
        }

        if (geometry != null) {
            setGeometry(geometry);
        }
    }

    /**
     * Gibt das Verhaeltnis der Breite des Renderers zur Breite des internen Browsers aus.
     *
     * @return  Verhaeltnis Renderers / interner Browser
     */
    @Override
    public double getWidthRatio() {
        return 1.0;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panInter = new javax.swing.JPanel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panMain = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblRegBez = new javax.swing.JLabel();
        lblRegJahr = new javax.swing.JLabel();
        lblVerkaufsdat = new javax.swing.JLabel();
        lblAdresse = new javax.swing.JLabel();
        lblObjektart = new javax.swing.JLabel();
        lblTeilmarkt = new javax.swing.JLabel();
        lblGesamtflaeche = new javax.swing.JLabel();
        lblGesamtteilflaeche = new javax.swing.JLabel();
        lblKaufpreisQm = new javax.swing.JLabel();
        lblKaufpreisAbs = new javax.swing.JLabel();
        lblAuswertung = new javax.swing.JLabel();
        panSachwerte = new RoundedPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblSachKaufpreis = new javax.swing.JLabel();
        lblSachRohertrag = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblSachKaufpreisRoh = new javax.swing.JLabel();
        lblSachMarktanp = new javax.swing.JLabel();
        lblSachZinssatz = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        panFlurstuecke = new javax.swing.JPanel();
        panGebaeude = new javax.swing.JPanel();
        panTeileigentum = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panInter.setOpaque(false);

        final javax.swing.GroupLayout panInterLayout = new javax.swing.GroupLayout(panInter);
        panInter.setLayout(panInterLayout);
        panInterLayout.setHorizontalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                600,
                Short.MAX_VALUE));
        panInterLayout.setVerticalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                20,
                Short.MAX_VALUE));

        add(panInter, java.awt.BorderLayout.SOUTH);

        panMap.setOpaque(false);
        panMap.setLayout(new java.awt.GridBagLayout());

        panSpinner.setMaximumSize(new java.awt.Dimension(100, 100));
        panSpinner.setMinimumSize(new java.awt.Dimension(100, 100));
        panSpinner.setOpaque(false);

        final javax.swing.GroupLayout panSpinnerLayout = new javax.swing.GroupLayout(panSpinner);
        panSpinner.setLayout(panSpinnerLayout);
        panSpinnerLayout.setHorizontalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                79,
                Short.MAX_VALUE));
        panSpinnerLayout.setVerticalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                100,
                Short.MAX_VALUE));

        panMap.add(panSpinner, new java.awt.GridBagConstraints());

        add(panMap, java.awt.BorderLayout.CENTER);

        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Kaufvertrag - 2130 / 1996");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    355,
                    Short.MAX_VALUE)));
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(panTitle, java.awt.BorderLayout.NORTH);

        panMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 20));
        panMain.setOpaque(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Reg_Bez:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Reg_Jahr:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Verkaufsdatum:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Adresse:");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Objektart:");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Teilmarkt:");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Gesamtfläche:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Gesamtteilfläche:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Kaufpreis pro m²:");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("Zur Auswertung geeignet:");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Kaufpreis absolut:");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Sachwerte:");

        lblRegBez.setText("2130 / 1996");

        lblRegJahr.setText("1996");

        lblVerkaufsdat.setText("bla");

        lblAdresse.setText("Hardtbacher H28");

        lblObjektart.setText("unbebautes baureifes Land");

        lblTeilmarkt.setText("unbebaute Grundst");

        lblGesamtflaeche.setText("2500.0");

        lblGesamtteilflaeche.setText("0.0");

        lblKaufpreisQm.setText("54.31");

        lblKaufpreisAbs.setText("135775.0");

        lblAuswertung.setText("ja");

        panSachwerte.setOpaque(false);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setText("Rohertrag:");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Bereinigter Kaufpreis:");

        lblSachKaufpreis.setText("blubb");

        lblSachRohertrag.setText("bla");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Bereinigter Kaufpreis Rohertrag:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Marktanpassungsfaktor:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setText("Liegenschaftszinssatz:");

        lblSachKaufpreisRoh.setText("wert");

        lblSachMarktanp.setText("nochn");

        lblSachZinssatz.setText("wert");

        final javax.swing.GroupLayout panSachwerteLayout = new javax.swing.GroupLayout(panSachwerte);
        panSachwerte.setLayout(panSachwerteLayout);
        panSachwerteLayout.setHorizontalGroup(
            panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panSachwerteLayout.createSequentialGroup().addContainerGap().addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel13).addComponent(jLabel14).addComponent(jLabel15).addComponent(jLabel16).addComponent(
                        jLabel17)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblSachKaufpreis).addComponent(lblSachRohertrag).addComponent(lblSachKaufpreisRoh).addComponent(
                        lblSachMarktanp).addComponent(lblSachZinssatz)).addContainerGap(43, Short.MAX_VALUE)));
        panSachwerteLayout.setVerticalGroup(
            panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panSachwerteLayout.createSequentialGroup().addContainerGap().addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel13).addComponent(lblSachRohertrag)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel14).addComponent(lblSachKaufpreis)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel15).addComponent(lblSachKaufpreisRoh)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel16).addComponent(lblSachMarktanp)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panSachwerteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel17).addComponent(lblSachZinssatz)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("Flurstücke:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel19.setText("Gebäude:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel20.setText("Teileigentum:");

        panFlurstuecke.setOpaque(false);

        final javax.swing.GroupLayout panFlurstueckeLayout = new javax.swing.GroupLayout(panFlurstuecke);
        panFlurstuecke.setLayout(panFlurstueckeLayout);
        panFlurstueckeLayout.setHorizontalGroup(
            panFlurstueckeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                78,
                Short.MAX_VALUE));
        panFlurstueckeLayout.setVerticalGroup(
            panFlurstueckeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                14,
                Short.MAX_VALUE));

        panGebaeude.setOpaque(false);

        final javax.swing.GroupLayout panGebaeudeLayout = new javax.swing.GroupLayout(panGebaeude);
        panGebaeude.setLayout(panGebaeudeLayout);
        panGebaeudeLayout.setHorizontalGroup(
            panGebaeudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                86,
                Short.MAX_VALUE));
        panGebaeudeLayout.setVerticalGroup(
            panGebaeudeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                14,
                Short.MAX_VALUE));

        panTeileigentum.setOpaque(false);

        final javax.swing.GroupLayout panTeileigentumLayout = new javax.swing.GroupLayout(panTeileigentum);
        panTeileigentum.setLayout(panTeileigentumLayout);
        panTeileigentumLayout.setHorizontalGroup(
            panTeileigentumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                97,
                Short.MAX_VALUE));
        panTeileigentumLayout.setVerticalGroup(
            panTeileigentumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                14,
                Short.MAX_VALUE));

        final javax.swing.GroupLayout panMainLayout = new javax.swing.GroupLayout(panMain);
        panMain.setLayout(panMainLayout);
        panMainLayout.setHorizontalGroup(
            panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panMainLayout.createSequentialGroup().addContainerGap().addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel1).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel4).addComponent(
                        jLabel5).addComponent(jLabel6).addComponent(jLabel7).addComponent(jLabel8).addComponent(
                        jLabel9).addComponent(jLabel10).addComponent(jLabel11).addComponent(jLabel12).addComponent(
                        jLabel18).addComponent(jLabel19).addComponent(jLabel20)).addGap(38, 38, 38).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        panTeileigentum,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        panGebaeude,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                        panFlurstuecke,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblAuswertung).addComponent(
                        lblKaufpreisAbs).addComponent(lblKaufpreisQm).addComponent(lblGesamtteilflaeche).addComponent(
                        lblObjektart).addComponent(
                        panSachwerte,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblRegJahr).addComponent(lblAdresse)
                                .addComponent(lblRegBez).addComponent(lblGesamtflaeche).addComponent(lblTeilmarkt)
                                .addComponent(lblVerkaufsdat)).addContainerGap(14, Short.MAX_VALUE)));
        panMainLayout.setVerticalGroup(
            panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panMainLayout.createSequentialGroup().addContainerGap().addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel1).addComponent(lblRegBez)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel2).addComponent(lblRegJahr)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel3).addComponent(lblVerkaufsdat)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel4).addComponent(lblAdresse)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel5).addComponent(lblObjektart)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel6).addComponent(lblTeilmarkt)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel7).addComponent(lblGesamtflaeche)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel8).addComponent(lblGesamtteilflaeche)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel9).addComponent(lblKaufpreisQm)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel10).addComponent(lblKaufpreisAbs)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel11).addComponent(lblAuswertung)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel12).addComponent(
                        panSachwerte,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel18).addComponent(
                        panFlurstuecke,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel19).addComponent(
                        panGebaeude,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel20).addComponent(
                        panTeileigentum,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE)).addContainerGap()));

        add(panMain, java.awt.BorderLayout.WEST);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createFlurstueckPanel(final int i) {
        int c = 6;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel flurstueck = new JPanel();
        flurstueck.setOpaque(false);
        flurstueck.setLayout(new GridLayout(6, 2, 20, 5));

        final JLabel gemName = new JLabel();
        gemName.setFont(new Font("Tahoma", 1, 11));
        gemName.setText("Gemarkungsname:");

        final JLabel gemNr = new JLabel();
        gemNr.setFont(new Font("Tahoma", 1, 11));
        gemNr.setText("Gemarkungsnr:");

        final JLabel flur = new JLabel();
        flur.setFont(new Font("Tahoma", 1, 11));
        flur.setText("Flur:");

        final JLabel flurstZN = new JLabel();
        flurstZN.setFont(new Font("Tahoma", 1, 11));
        flurstZN.setText("Z\u00E4hler / Nenner:");

        final JLabel flaeche = new JLabel();
        flaeche.setFont(new Font("Tahoma", 1, 11));
        flaeche.setText("Fl\u00E4che:");

        final JLabel teilflaeche = new JLabel();
        teilflaeche.setFont(new Font("Tahoma", 1, 11));
        teilflaeche.setText("Teilfl\u00E4che:");

        if ((flurName.size() > i) && (flurName.get(i) != null) && !flurName.get(i).equals("")) {
            flurstueck.add(gemName);
            flurstueck.add(new JLabel(flurName.get(i)));
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }
        if ((flurNr.size() > i) && (flurNr.get(i) != null) && !flurNr.get(i).equals("")
                    && !flurNr.get(i).equals("null")) {
            flurstueck.add(gemNr);
            flurstueck.add(new JLabel(flurNr.get(i)));
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }
        if ((flurFlur.size() > i) && (flurFlur.get(i) != null) && !flurFlur.get(i).equals("")) {
            flurstueck.add(flur);
            flurstueck.add(new JLabel(flurFlur.get(i)));
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }
        if ((flurZ.size() > i) && (flurZ.get(i) != null) && !flurZ.get(i).equals("")) {
            if ((flurN.size() > i) && (flurN.get(i) != null) && !flurN.get(i).equals("")) {
                flurstueck.add(flurstZN);
                flurstueck.add(new JLabel(flurZ.get(i) + "/" + flurN.get(i)));
            } else {
                flurstueck.add(flurstZN);
                flurstueck.add(new JLabel(flurZ.get(i)));
            }
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }
        if ((flurFlaeche.size() > i) && (flurFlaeche.get(i) != null) && (flurFlaeche.get(i) > -1.0f)) {
            flurstueck.add(flaeche);
            flurstueck.add(new JLabel(flurFlaeche.get(i).toString()));
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }
        if ((flurTeilflaeche.size() > i) && (flurTeilflaeche.get(i) != null) && (flurTeilflaeche.get(i) > -1.0f)) {
            flurstueck.add(teilflaeche);
            flurstueck.add(new JLabel(flurTeilflaeche.get(i).toString()));
        } else {
            ((GridLayout)flurstueck.getLayout()).setRows(--c);
        }

        round.add(flurstueck, BorderLayout.CENTER);
        return round;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createGebaeudePanel(final int i) {
        int c = 5;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel geb = new JPanel();
        geb.setOpaque(false);
        geb.setLayout(new GridLayout(c, 2, 20, 5));

        final JLabel bez = new JLabel();
        bez.setFont(new Font("Tahoma", 1, 11));
        bez.setText("Bezeichnung:");

        final JLabel baujahr = new JLabel();
        baujahr.setFont(new Font("Tahoma", 1, 11));
        baujahr.setText("Baujahr:");

        final JLabel wf = new JLabel();
        wf.setFont(new Font("Tahoma", 1, 11));
        wf.setText("Wohnfl\u00E4che:");

        final JLabel nf = new JLabel();
        nf.setFont(new Font("Tahoma", 1, 11));
        nf.setText("Nutzfl\u00E4che:");

        final JLabel wf_nf = new JLabel();
        wf_nf.setFont(new Font("Tahoma", 1, 11));
        wf_nf.setText("Wohnfl\u00E4che/Nutzfl\u00E4che:");

        if ((gebBez.size() > i) && (gebBez.get(i) != null) && !gebBez.get(i).equals("")) {
            geb.add(bez);
            geb.add(new JLabel(gebBez.get(i)));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }
        if ((gebBaujahr.size() > i) && (gebBaujahr.get(i) != null) && (gebBaujahr.get(i) > -1)) {
            geb.add(baujahr);
            geb.add(new JLabel(gebBaujahr.get(i).toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }
        if ((gebWfqm.size() > i) && (gebWfqm.get(i) != null) && (gebWfqm.get(i) > -1.0f)) {
            geb.add(wf);
            geb.add(new JLabel(gebWfqm.get(i).toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }
        if ((gebNfqm.size() > i) && (gebNfqm.get(i) != null) && (gebNfqm.get(i) > -1.0f)) {
            geb.add(nf);
            geb.add(new JLabel(gebNfqm.get(i).toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }
        if ((gebWfnf.size() > i) && (gebWfnf.get(i) != null) && (gebWfnf.get(i) > -1.0f)) {
            geb.add(wf_nf);
            geb.add(new JLabel(gebWfnf.get(i).toString()));
        } else {
            ((GridLayout)geb.getLayout()).setRows(--c);
        }

        round.add(geb, BorderLayout.CENTER);

        return round;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createTeileigentumPanel(final int i) {
        int c = 8;
        final RoundedPanel round = new RoundedPanel();
        round.setLayout(new FlowLayout(FlowLayout.LEADING));
        round.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        final JPanel teil = new JPanel();
        teil.setOpaque(false);
        teil.setLayout(new GridLayout(c, 2, 20, 5));

        final JLabel geschosse = new JLabel();
        geschosse.setFont(new Font("Tahoma", 1, 11));
        geschosse.setText("Anzahl Geschosse:");

        final JLabel kaufpreis = new JLabel();
        kaufpreis.setFont(new Font("Tahoma", 1, 11));
        kaufpreis.setText("Rel. Kaufpreis:");

        final JLabel tmarkt = new JLabel();
        tmarkt.setFont(new Font("Tahoma", 1, 11));
        tmarkt.setText("Teilmarkt:");

        final JLabel vermietung = new JLabel();
        vermietung.setFont(new Font("Tahoma", 1, 11));
        vermietung.setText("Vermietungssituation:");

        final JLabel ausstattung = new JLabel();
        ausstattung.setFont(new Font("Tahoma", 1, 11));
        ausstattung.setText("Ausstattungsstandard:");

        final JLabel modernisierung = new JLabel();
        modernisierung.setFont(new Font("Tahoma", 1, 11));
        modernisierung.setText("Modernisierungsjahr:");

        final JLabel raumzahl = new JLabel();
        raumzahl.setFont(new Font("Tahoma", 1, 11));
        raumzahl.setText("Raumanzahl:");

        final JLabel wohnlage = new JLabel();
        wohnlage.setFont(new Font("Tahoma", 1, 11));
        wohnlage.setText("Wohnlage:");

        if ((eigAnzGesch.size() > i) && (eigAnzGesch.get(i) != null) && (eigAnzGesch.get(i) > -1)) {
            teil.add(geschosse);
            teil.add(new JLabel(eigAnzGesch.get(i).toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigRelKauf.size() > i) && (eigRelKauf.get(i) != null) && (eigRelKauf.get(i) > -1.0f)) {
            teil.add(kaufpreis);
            teil.add(new JLabel(eigRelKauf.get(i).toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigTeilmarkt.size() > i) && (eigTeilmarkt.get(i) != null) && !eigTeilmarkt.get(i).equals("")) {
            teil.add(tmarkt);
            teil.add(new JLabel(eigTeilmarkt.get(i).trim()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigVermietung.size() > i) && (eigVermietung.get(i) != null) && !eigVermietung.get(i).equals("")
                    && !eigVermietung.get(i).equals("null")) {
            teil.add(vermietung);
            teil.add(new JLabel(eigVermietung.get(i)));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigAusstattung.size() > i) && (eigAusstattung.get(i) != null) && !eigAusstattung.get(i).equals("")
                    && !eigAusstattung.get(i).equals("null")) {
            teil.add(ausstattung);
            teil.add(new JLabel(eigAusstattung.get(i)));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigModern.size() > i) && (eigModern.get(i) != null) && (eigModern.get(i) > -1)) {
            teil.add(modernisierung);
            teil.add(new JLabel(eigModern.get(i).toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigRaumzahl.size() > i) && (eigRaumzahl.get(i) != null) && (eigRaumzahl.get(i) > -1)) {
            teil.add(raumzahl);
            teil.add(new JLabel(eigRaumzahl.get(i).toString()));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }
        if ((eigWohnlage.size() > i) && (eigWohnlage.get(i) != null) && !eigWohnlage.get(i).equals("")
                    && !eigWohnlage.get(i).equals("null")) {
            teil.add(wohnlage);
            teil.add(new JLabel(eigWohnlage.get(i)));
        } else {
            ((GridLayout)teil.getLayout()).setRows(--c);
        }

        round.add(teil, BorderLayout.CENTER);

        return round;
    }
}
