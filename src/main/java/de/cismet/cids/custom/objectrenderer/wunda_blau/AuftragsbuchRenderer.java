/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import java.sql.Date;

import java.text.DateFormat;

import java.util.Locale;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.annotations.CidsRendererTitle;

import de.cismet.cids.custom.deprecated.JLoadDots;

import de.cismet.cids.tools.metaobjectrenderer.BlurredMapObjectRenderer;

/**
 * de.cismet.cids.objectrenderer.CoolAuftragsbuchRenderer.
 *
 * @author   cschmidt
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class AuftragsbuchRenderer extends BlurredMapObjectRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String AUFTRAGSBUCH = "Auftragsbuch";

    //~ Instance fields --------------------------------------------------------

    @CidsRendererTitle
    public String compTitle = "";

    @CidsAttribute("Auftragsnummer")
    public Integer auftragsnummer = null;

    @CidsAttribute("Auftrag erteilt am")
    public Date auftragsdatum = null;

    @CidsAttribute("Auftragsart")
    public String auftragsart = "";

    @CidsAttribute("Auftraggeber")
    public String auftraggeber = "";

    @CidsAttribute("Aktenzeichen")
    public String aktenzeichen = "";

    @CidsAttribute("Lage")
    public String lage = "";

    @CidsAttribute("Bemerkungen(1)")
    public String bemerkungEins = "";

    @CidsAttribute("Bemerkungen(2)")
    public String bemerkungZwei = "";

    @CidsAttribute("Erledigt am")
    public Date erledigtAm = null;

    @CidsAttribute("Bearbeiter")
    public String bearbeiter = "";

    @CidsAttribute("Hinweise")
    public String hinweise = "";

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geometry = null;

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblAktenzeichen;
    private javax.swing.JLabel lblAuftraggeber;
    private javax.swing.JLabel lblAuftragsart;
    private javax.swing.JLabel lblAuftragsdatum;
    private javax.swing.JLabel lblAuftragsnummer;
    private javax.swing.JLabel lblBearbeiter;
    private javax.swing.JLabel lblBemerkEins;
    private javax.swing.JLabel lblBemerkZwei;
    private javax.swing.JLabel lblErledigtAm;
    private javax.swing.JLabel lblHinweise;
    private javax.swing.JLabel lblLage;
    private javax.swing.JLabel lblTitel;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolAuftragsbuchRenderer.
     */
    public AuftragsbuchRenderer() {
        initComponents();
        setPanContent(panMain);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assignSingle() {
        if (auftragsnummer != null) {
            lblAuftragsnummer.setText(auftragsnummer.toString());
        } else {
            lblAuftragsnummer.setText("Keine g\u00FCltige Auftragsnummmer");
            log.error("auftragsnummer = Null");
        }

        if (auftragsdatum != null) {
            lblAuftragsdatum.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(
                    auftragsdatum));
        } else {
            jLabel3.setVisible(false);
            lblAuftragsdatum.setVisible(false);
            if (log.isDebugEnabled()) {
                log.debug("Auftragdatum nicht in der Datenbank gespeichert");
            }
        }

        if ((auftragsart != null) && !auftragsart.equals("") && !auftragsart.equals("null")) {
            lblAuftragsart.setText(auftragsart);
        } else {
            jLabel4.setVisible(false);
            lblAuftragsart.setVisible(false);
        }

        if ((auftraggeber != null) && !auftraggeber.equals("") && !auftraggeber.equals("null")) {
            lblAuftraggeber.setText(auftraggeber);
        } else {
            jLabel5.setVerifyInputWhenFocusTarget(false);
            lblAuftraggeber.setVisible(false);
        }

        if ((aktenzeichen != null) && !aktenzeichen.equals("") && !aktenzeichen.equals("null")) {
            lblAktenzeichen.setText(aktenzeichen);
        } else {
            jLabel6.setVisible(false);
            lblAktenzeichen.setVisible(false);
        }

        if ((lage != null) && !lage.equals("") && !lage.equals("null")) {
            lblLage.setText(lage);
        } else {
            jLabel7.setVisible(false);
            lblLage.setVisible(false);
        }

        if ((bemerkungEins != null) && !bemerkungEins.equals("") && !bemerkungEins.equals("null")) {
            lblBemerkEins.setText(bemerkungEins);
        } else {
            jLabel8.setVisible(false);
            lblBemerkEins.setVisible(false);
        }

        if ((bemerkungZwei != null) && !bemerkungZwei.equals("") && !bemerkungZwei.equals("null")) {
            lblBemerkZwei.setText(bemerkungZwei);
        } else {
            jLabel9.setVisible(false);
            lblBemerkZwei.setVisible(false);
        }

        if (erledigtAm != null) {
            lblErledigtAm.setText(DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).format(erledigtAm));
        } else {
            jLabel10.setVisible(false);
            lblErledigtAm.setVisible(false);
        }

        if ((bearbeiter != null) && !bearbeiter.equals("") && !bearbeiter.equals("null")) {
            lblBearbeiter.setText(bearbeiter);
        } else {
            jLabel11.setVisible(false);
            lblBearbeiter.setVisible(false);
        }

        if ((hinweise != null) && !hinweise.equals("") && !hinweise.equals("null")) {
            lblHinweise.setText(hinweise);
        } else {
            jLabel12.setVisible(false);
            lblHinweise.setVisible(false);
        }

        if (geometry != null) {
            setGeometry(geometry);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panInter = new javax.swing.JPanel();
        panMain = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblAuftragsnummer = new javax.swing.JLabel();
        lblAuftragsdatum = new javax.swing.JLabel();
        lblAuftragsart = new javax.swing.JLabel();
        lblAuftraggeber = new javax.swing.JLabel();
        lblAktenzeichen = new javax.swing.JLabel();
        lblLage = new javax.swing.JLabel();
        lblBemerkEins = new javax.swing.JLabel();
        lblBemerkZwei = new javax.swing.JLabel();
        lblErledigtAm = new javax.swing.JLabel();
        lblBearbeiter = new javax.swing.JLabel();
        lblHinweise = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        lblTitel = new javax.swing.JLabel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panInter.setOpaque(false);

        final javax.swing.GroupLayout panInterLayout = new javax.swing.GroupLayout(panInter);
        panInter.setLayout(panInterLayout);
        panInterLayout.setHorizontalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                380,
                Short.MAX_VALUE));
        panInterLayout.setVerticalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                20,
                Short.MAX_VALUE));

        add(panInter, java.awt.BorderLayout.SOUTH);

        panMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 20));
        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Auftragsnummer: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Auftrag erteilt am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Auftragsart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Auftraggeber:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Aktenzeichen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Lage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Bemerkung (1):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Bemerkung (2):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Erledigt am:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("Bearbeiter:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel11, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Hinweise:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panMain.add(jLabel12, gridBagConstraints);

        lblAuftragsnummer.setText("662712365");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblAuftragsnummer, gridBagConstraints);

        lblAuftragsdatum.setText("21.01.70");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblAuftragsdatum, gridBagConstraints);

        lblAuftragsart.setText("Bauvermessung");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblAuftragsart, gridBagConstraints);

        lblAuftraggeber.setText("Remmling");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblAuftraggeber, gridBagConstraints);

        lblAktenzeichen.setText("104.24");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblAktenzeichen, gridBagConstraints);

        lblLage.setText("Schusterplatz");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblLage, gridBagConstraints);

        lblBemerkEins.setText("Ankauf einer Teilfläche");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblBemerkEins, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblBemerkZwei, gridBagConstraints);

        lblErledigtAm.setText("1.1.1980");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblErledigtAm, gridBagConstraints);

        lblBearbeiter.setText("Jaeger");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblBearbeiter, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMain.add(lblHinweise, gridBagConstraints);

        add(panMain, java.awt.BorderLayout.WEST);

        panTitle.setOpaque(false);

        lblTitel.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitel.setForeground(new java.awt.Color(255, 255, 255));
        lblTitel.setText("Auftragsbuch");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitel).addContainerGap(
                    248,
                    Short.MAX_VALUE)));
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(
                    lblTitel,
                    javax.swing.GroupLayout.PREFERRED_SIZE,
                    27,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(panTitle, java.awt.BorderLayout.NORTH);

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
    } // </editor-fold>//GEN-END:initComponents
}
