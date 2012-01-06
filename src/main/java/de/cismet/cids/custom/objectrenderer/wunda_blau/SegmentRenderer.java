/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.custom.deprecated.JLoadDots;

import de.cismet.cids.tools.metaobjectrenderer.BlurredMapObjectRenderer;

import de.cismet.tools.gui.RoundedPanel;

/**
 * de.cismet.cids.objectrenderer.CoolSegmentRenderer.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class SegmentRenderer extends BlurredMapObjectRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SEGMENT = "Segment";

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("NAME")
    public String name = "";

    @CidsAttribute("OBJEKT_ID")
    public String objektID = "";

    @CidsAttribute("KMNR")
    public Integer kilometerNr;

    @CidsAttribute("SEGNR")
    public Integer segmentNr;

    @CidsAttribute("STRASSENSCHLUESSEL.NAME")
    public String strasse = "";

    @CidsAttribute("LAENGE")
    public Integer laenge;

    @CidsAttribute("BREITE")
    public Integer breite;

    @CidsAttribute("HOEHE_ANFANG")
    public Float hoeheAnfang;

    @CidsAttribute("HOEHE_ENDE")
    public Float hoeheEnde;

    @CidsAttribute("NEIGUNG")
    public Float neigung;

    @CidsAttribute("KLASSE")
    public Integer klasse;

    @CidsAttribute("LOCATION")
    public String location = "";

    @CidsAttribute("KNOTEN_ANFANG.NAME")
    public String knotenAName = "";

    @CidsAttribute("KNOTEN_ENDE.NAME")
    public String knotenEName = "";

    @CidsAttribute("KNOTEN_ANFANG.HOEHE")
    public Integer knotenAHoehe;

    @CidsAttribute("KNOTEN_ENDE.HOEHE")
    public Integer knotenEHoehe;

    @CidsAttribute("SEGMENTSEITE_L.NAME")
    public String segmentLName = "";

    @CidsAttribute("SEGMENTSEITE_R.NAME")
    public String segmentRName = "";

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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblHoeheAn;
    private javax.swing.JLabel lblHoeheEn;
    private javax.swing.JLabel lblKlasse;
    private javax.swing.JLabel lblKmnr;
    private javax.swing.JLabel lblKnAHoehe;
    private javax.swing.JLabel lblKnAName;
    private javax.swing.JLabel lblKnEHoehe;
    private javax.swing.JLabel lblKnEName;
    private javax.swing.JLabel lblLaengeBreite;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblNeigung;
    private javax.swing.JLabel lblObjectID;
    private javax.swing.JLabel lblSegLName;
    private javax.swing.JLabel lblSegRName;
    private javax.swing.JLabel lblSegnr;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panInhalt;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panKnotenAn;
    private javax.swing.JPanel panKnotenEn;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TestRenderer.
     */
    public SegmentRenderer() {
        initComponents();
        setPanContent(panInhalt);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }

    //~ Methods ----------------------------------------------------------------

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
            lblTitle.setText(name);
        } else {
            lblTitle.setText(SEGMENT);
        }

        if (!objektID.equals("")) {
            lblObjectID.setText(objektID);
        } else {
            lblObjectID.setVisible(false);
            jLabel1.setVisible(false);
        }

        if (kilometerNr > -1) {
            lblKmnr.setText(kilometerNr.toString());
        } else {
            lblKmnr.setVisible(false);
            jLabel2.setVisible(false);
        }

        if (segmentNr > -1) {
            lblSegnr.setText(segmentNr.toString());
        } else {
            lblSegnr.setVisible(false);
            jLabel3.setVisible(false);
        }

        if ((strasse != null) && !strasse.equals("")) {
            lblStrasse.setText(strasse);
        } else {
            lblStrasse.setVisible(false);
            jLabel4.setVisible(false);
        }

        if ((laenge > -1) && (breite > -1)) {
            lblLaengeBreite.setText(laenge + " x " + breite);
        } else {
            lblLaengeBreite.setVisible(false);
            jLabel5.setVisible(false);
        }

        if (hoeheAnfang > -1.0f) {
            lblHoeheAn.setText(hoeheAnfang.toString());
            log.info("H\u00F6he Anfang: " + hoeheAnfang.toString());
        } else {
            lblHoeheAn.setVisible(false);
            jLabel6.setVisible(false);
        }

        if (hoeheEnde > -1.0f) {
            lblHoeheEn.setText(hoeheEnde.toString());
            log.info("H\u00F6he Ende: " + hoeheEnde.toString());
        } else {
            lblHoeheEn.setVisible(false);
            jLabel7.setVisible(false);
        }

        if (neigung > -1.0f) {
            lblNeigung.setText(neigung.toString());
        } else {
            lblNeigung.setVisible(false);
            jLabel8.setVisible(false);
        }

        if (klasse > -1) {
            lblKlasse.setText(klasse.toString());
        } else {
            lblKlasse.setVisible(false);
            jLabel9.setVisible(false);
        }

        if (!location.equals("")) {
            lblLocation.setText(location);
        } else {
            lblLocation.setVisible(false);
            jLabel10.setVisible(false);
        }

        if (!knotenAName.equals("") && (knotenAHoehe > -1)) {
            lblKnAName.setText(knotenAName);
            lblKnAHoehe.setText(knotenAHoehe.toString());
        } else {
            panKnotenAn.setVisible(false);
            jLabel11.setVisible(false);
        }

        if (!knotenEName.equals("") && (knotenEHoehe > -1)) {
            lblKnEName.setText(knotenEName);
            lblKnEHoehe.setText(knotenEHoehe.toString());
        } else {
            panKnotenEn.setVisible(false);
            jLabel14.setVisible(false);
        }

        if (!segmentRName.equals("")) {
            lblSegRName.setText(segmentRName);
        } else {
            lblSegRName.setVisible(false);
            jLabel17.setVisible(false);
        }

        if (!segmentLName.equals("")) {
            lblSegLName.setText(segmentLName);
        } else {
            lblSegLName.setVisible(false);
            jLabel18.setVisible(false);
        }

        if (geometry != null) {
            setGeometry(geometry);
        }
    }

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
        java.awt.GridBagConstraints gridBagConstraints;

        panInter = new javax.swing.JPanel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();
        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panInhalt = new javax.swing.JPanel();
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
        lblObjectID = new javax.swing.JLabel();
        lblKmnr = new javax.swing.JLabel();
        lblSegnr = new javax.swing.JLabel();
        lblStrasse = new javax.swing.JLabel();
        lblLaengeBreite = new javax.swing.JLabel();
        lblHoeheAn = new javax.swing.JLabel();
        lblHoeheEn = new javax.swing.JLabel();
        lblNeigung = new javax.swing.JLabel();
        lblKlasse = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        panKnotenAn = new RoundedPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblKnAName = new javax.swing.JLabel();
        lblKnAHoehe = new javax.swing.JLabel();
        panKnotenEn = new RoundedPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        lblKnEHoehe = new javax.swing.JLabel();
        lblKnEName = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblSegRName = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblSegLName = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panInter.setOpaque(false);

        final javax.swing.GroupLayout panInterLayout = new javax.swing.GroupLayout(panInter);
        panInter.setLayout(panInterLayout);
        panInterLayout.setHorizontalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                497,
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
                100,
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
        lblTitle.setText("Segment - 7871.4");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    328,
                    Short.MAX_VALUE)));
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(panTitle, java.awt.BorderLayout.NORTH);

        panInhalt.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 20));
        panInhalt.setOpaque(false);
        panInhalt.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Objekt-ID:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Kilometerqu.-Nr.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Segment-Nr.:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Strassenname:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Länge x Breite:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Höhe Anfang:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Höhe Ende:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Neigung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Klasse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel9, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("Knoten Anfang:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel11, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel10, gridBagConstraints);

        lblObjectID.setText("000BLO");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblObjectID, gridBagConstraints);

        lblKmnr.setText("7871");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblKmnr, gridBagConstraints);

        lblSegnr.setText("4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblSegnr, gridBagConstraints);

        lblStrasse.setText("Wupperstr.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblStrasse, gridBagConstraints);

        lblLaengeBreite.setText("1089 x 26");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblLaengeBreite, gridBagConstraints);

        lblHoeheAn.setText("105.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblHoeheAn, gridBagConstraints);

        lblHoeheEn.setText("102.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblHoeheEn, gridBagConstraints);

        lblNeigung.setText("0.2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblNeigung, gridBagConstraints);

        lblKlasse.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblKlasse, gridBagConstraints);

        lblLocation.setText("0196000000018ee2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblLocation, gridBagConstraints);

        panKnotenAn.setOpaque(false);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Name:");

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setText("Höhe:");

        lblKnAName.setText("Knoten 7871.2");

        lblKnAHoehe.setText("105");

        final javax.swing.GroupLayout panKnotenAnLayout = new javax.swing.GroupLayout(panKnotenAn);
        panKnotenAn.setLayout(panKnotenAnLayout);
        panKnotenAnLayout.setHorizontalGroup(
            panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panKnotenAnLayout.createSequentialGroup().addContainerGap().addGroup(
                    panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel13).addComponent(jLabel12)).addGap(35, 35, 35).addGroup(
                    panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblKnAName).addComponent(lblKnAHoehe)).addContainerGap(17, Short.MAX_VALUE)));
        panKnotenAnLayout.setVerticalGroup(
            panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panKnotenAnLayout.createSequentialGroup().addContainerGap().addGroup(
                    panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel12,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        14,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(lblKnAName)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panKnotenAnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel13).addComponent(lblKnAHoehe)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(panKnotenAn, gridBagConstraints);

        panKnotenEn.setOpaque(false);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Name:");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Höhe:");

        lblKnEHoehe.setText("102");

        lblKnEName.setText("Knoten 7871.36");

        final javax.swing.GroupLayout panKnotenEnLayout = new javax.swing.GroupLayout(panKnotenEn);
        panKnotenEn.setLayout(panKnotenEnLayout);
        panKnotenEnLayout.setHorizontalGroup(
            panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panKnotenEnLayout.createSequentialGroup().addContainerGap().addGroup(
                    panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        jLabel15).addComponent(jLabel16)).addGap(36, 36, 36).addGroup(
                    panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        lblKnEName).addComponent(lblKnEHoehe)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));
        panKnotenEnLayout.setVerticalGroup(
            panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panKnotenEnLayout.createSequentialGroup().addContainerGap().addGroup(
                    panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel15).addComponent(lblKnEName)).addPreferredGap(
                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                    panKnotenEnLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        jLabel16).addComponent(lblKnEHoehe)).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(panKnotenEn, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Knoten Ende:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel14, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setText("Segmentseite Rechts:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel17, gridBagConstraints);

        lblSegRName.setText("Segmentseite 7871.4 Rechts");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblSegRName, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("Segmentseite Links:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel18, gridBagConstraints);

        lblSegLName.setText("Segmentseite 7871.4 Links");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblSegLName, gridBagConstraints);

        add(panInhalt, java.awt.BorderLayout.WEST);
    } // </editor-fold>//GEN-END:initComponents
}
