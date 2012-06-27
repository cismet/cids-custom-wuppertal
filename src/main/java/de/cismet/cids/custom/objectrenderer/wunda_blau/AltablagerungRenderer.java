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

/**
 * de.cismet.cids.objectrenderer.CoolAltablagerungRenderer.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class AltablagerungRenderer extends BlurredMapObjectRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE = "Altablagerung";

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("NAME")
    public String name = "";

    @CidsAttribute("DESCRIPTION.OBJECT_NAME")
    public String url_name = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.PROT_PREFIX")
    public String url_prefix = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.SERVER")
    public String url_server = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.PATH")
    public String url_path = "";

    @CidsAttribute("ISBA_NUMMER")
    public String isba = "";

    @CidsAttribute("BEGINN_ABLAGERUNG")
    public String beginn = "";

    @CidsAttribute("ENDE_ABLAGERUNG")
    public String ende = "";

    @CidsAttribute("FLAECHENKUERZEL")
    public String kuerzel = "";

    @CidsAttribute("QUELLE")
    public String quelle = "";

    @CidsAttribute("BEMERKUNG")
    public String bemerkung = "";

    @CidsAttribute("FLAECHENTYP")
    public String typ = "";

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geom = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblBeginn;
    private javax.swing.JLabel lblBem;
    private javax.swing.JLabel lblEnde;
    private javax.swing.JLabel lblISBA;
    private javax.swing.JLabel lblKuerzel;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblQuelle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTyp;
    private javax.swing.JLabel lblURL;
    private javax.swing.JPanel panInhalt;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolAltablagerungRenderer.
     */
    public AltablagerungRenderer() {
        initComponents();
        setPanContent(panInhalt);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    @Override
    public void assignSingle() {
        if (geom != null) {
            super.setGeometry(geom);
        }

        if (name != null) {
            lblName.setText(name);
            lblTitle.setText(name);
        } else {
            jLabel1.setVisible(false);
            lblName.setVisible(false);
            lblTitle.setText(TITLE);
        }
        if ((url_prefix != null) && (url_server != null) && (url_path != null) && (url_name != null)) {
            lblURL.setText(url_prefix + url_server + url_path + url_name);
        } else {
            jLabel2.setVisible(false);
            lblURL.setVisible(false);
        }
        if (isba != null) {
            lblISBA.setText(isba);
        } else {
            jLabel3.setVisible(false);
            lblISBA.setVisible(false);
        }
        if (beginn != null) {
            lblBeginn.setText(beginn);
        } else {
            jLabel4.setVisible(false);
            lblBeginn.setVisible(false);
        }
        if (ende != null) {
            lblEnde.setText(ende);
        } else {
            jLabel5.setVisible(false);
            lblEnde.setVisible(false);
        }
        if (kuerzel != null) {
            lblKuerzel.setText(kuerzel);
        } else {
            jLabel6.setVisible(false);
            lblKuerzel.setVisible(false);
        }
        if (quelle != null) {
            lblQuelle.setText(quelle);
        } else {
            jLabel7.setVisible(false);
            lblQuelle.setVisible(false);
        }
        if ((bemerkung != null) && !bemerkung.equals("null")) {
            lblBem.setText(bemerkung);
        } else {
            jLabel8.setVisible(false);
            lblBem.setVisible(false);
        }
        if (typ != null) {
            lblTyp.setText(typ);
        } else {
            jLabel9.setVisible(false);
            lblTyp.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panInter = new javax.swing.JPanel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();
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
        lblName = new javax.swing.JLabel();
        lblURL = new javax.swing.JLabel();
        lblISBA = new javax.swing.JLabel();
        lblBeginn = new javax.swing.JLabel();
        lblEnde = new javax.swing.JLabel();
        lblKuerzel = new javax.swing.JLabel();
        lblQuelle = new javax.swing.JLabel();
        lblBem = new javax.swing.JLabel();
        lblTyp = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(313, 257));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(313, 257));
        setLayout(new java.awt.BorderLayout());

        panTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Altablagerung");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    171,
                    Short.MAX_VALUE)));
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE)));

        add(panTitle, java.awt.BorderLayout.NORTH);

        panInter.setOpaque(false);

        final javax.swing.GroupLayout panInterLayout = new javax.swing.GroupLayout(panInter);
        panInter.setLayout(panInterLayout);
        panInterLayout.setHorizontalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                313,
                Short.MAX_VALUE));
        panInterLayout.setVerticalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                14,
                Short.MAX_VALUE));

        add(panInter, java.awt.BorderLayout.SOUTH);

        panMap.setOpaque(false);
        panMap.setLayout(new java.awt.GridBagLayout());

        panSpinner.setMaximumSize(new java.awt.Dimension(100, 100));
        panSpinner.setMinimumSize(new java.awt.Dimension(100, 100));
        panSpinner.setOpaque(false);
        panSpinner.setPreferredSize(new java.awt.Dimension(100, 100));
        panSpinner.setRequestFocusEnabled(false);

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

        panInhalt.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 20));
        panInhalt.setOpaque(false);
        panInhalt.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("ISBA-Nummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Beginn Ablagerung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Ende Ablagerung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Flächenkürzel:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Quelle:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Flächentyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panInhalt.add(jLabel9, gridBagConstraints);

        lblName.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblName, gridBagConstraints);

        lblURL.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblURL, gridBagConstraints);

        lblISBA.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblISBA, gridBagConstraints);

        lblBeginn.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblBeginn, gridBagConstraints);

        lblEnde.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblEnde, gridBagConstraints);

        lblKuerzel.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblKuerzel, gridBagConstraints);

        lblQuelle.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblQuelle, gridBagConstraints);

        lblBem.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblBem, gridBagConstraints);

        lblTyp.setText("irgendwas");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblTyp, gridBagConstraints);

        add(panInhalt, java.awt.BorderLayout.WEST);
    } // </editor-fold>//GEN-END:initComponents
}
