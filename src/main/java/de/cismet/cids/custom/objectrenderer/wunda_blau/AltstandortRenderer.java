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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.custom.deprecated.JLoadDots;

import de.cismet.cids.tools.metaobjectrenderer.BlurredMapObjectRenderer;

import de.cismet.tools.gui.RoundedPanel;

/**
 * de.cismet.cids.objectrenderer.CoolAltstandortRenderer.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class AltstandortRenderer extends BlurredMapObjectRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE = "Altstandort";

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("NAME")
    public String name = "";

    @CidsAttribute("DESCRIPTION.OBJECT_NAME")
    public String urlName = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.PROT_PREFIX")
    public String urlPrefix = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.SERVER")
    public String urlServer = "";

    @CidsAttribute("DESCRIPTION.URL_BASE_ID.PATH")
    public String urlPath = "";

    @CidsAttribute("ISBA_NUMMER")
    public String isba = "";

    @CidsAttribute("FLAECHENGROESSE_IN_QM")
    public Integer flaeche;

    @CidsAttribute("PLANQUADRAT")
    public String planquadrat;

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geom = null;

    // Betriebevektoren
    @CidsAttribute("BETRIEBE[].BETRIEB.NAME")
    public Vector<String> bName = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.DESCRIPTION.OBJECT_NAME")
    public Vector<String> bUrlName = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.DESCRIPTION.URL_BASE_ID.PROT_PREFIX")
    public Vector<String> bUrlPrefix = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.DESCRIPTION.URL_BASE_ID.SERVER")
    public Vector<String> bUrlServer = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.DESCRIPTION.URL_BASE_ID.PATH")
    public Vector<String> bUrlPath = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.BETRIEBSNUMMER")
    public Vector<String> bBetriebsnr = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.STRASSENNAME")
    public Vector<String> bStrasse = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.HAUSNUMMERN")
    public Vector<String> bHausnr = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.NAME_DES_INHABERS")
    public Vector<String> bInhaber = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.BETRIEBSBEZEICHNUNG")
    public Vector<String> bBez = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.BETRIEBSBEGINN")
    public Vector<String> bBeginn = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.BETRIEBSENDE")
    public Vector<String> bEnde = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.STRASSENNAME_AUS_ADRESSBUCH")
    public Vector<String> bStrasseAdr = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.BRANCHE_AUS_ADRESSBUCH")
    public Vector<String> bBranche = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.IST_ALTSTANDORT")
    public Vector<String> bAltstandort = new Vector();

    @CidsAttribute("BETRIEBE[].BETRIEB.STADTTEIL")
    public Vector<String> bStadtteil = new Vector();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel lblFlaeche;
    private javax.swing.JLabel lblISBA;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPlanquadrat;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblURL;
    private javax.swing.JPanel panBetriebe;
    private javax.swing.JPanel panInhalt;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolAltstandortRenderer.
     */
    public AltstandortRenderer() {
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
        if ((urlPrefix != null) && (urlServer != null) && (urlPath != null) && (urlName != null)) {
            lblURL.setText(urlPrefix + urlServer + urlPath + urlName);
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
        if (flaeche != null) {
            lblFlaeche.setText(flaeche.toString());
        } else {
            jLabel4.setVisible(false);
            lblFlaeche.setVisible(false);
        }
        if (planquadrat != null) {
            lblPlanquadrat.setText(planquadrat);
        } else {
            jLabel5.setVisible(false);
            lblPlanquadrat.setVisible(false);
        }

        if (bName.size() > 0) {
            final int anzahl = bName.size();
            panBetriebe.setLayout(new GridLayout(anzahl, 1, 5, 5));
            for (int i = 0; i < anzahl; ++i) {
                panBetriebe.add(createBetriebPanel(i));
            }
        } else {
            panBetriebe.setVisible(false);
            jLabel6.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   i  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private RoundedPanel createBetriebPanel(final int i) {
        int y = 0;
        final Font bold = new Font("Tahoma", 1, 11);
        final RoundedPanel rnd = new RoundedPanel(new BorderLayout());
        rnd.setLayout(new BorderLayout());
        rnd.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final JPanel betrieb = new JPanel();
        betrieb.setOpaque(false);
        betrieb.setLayout(new GridBagLayout());

        final GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(0, 0, 5, 30);

        final JLabel name = new JLabel();
        name.setFont(bold);
        name.setText("Name:");

        final JLabel url = new JLabel();
        url.setFont(bold);
        url.setText("URL:");

        final JLabel betriebsNr = new JLabel();
        betriebsNr.setFont(bold);
        betriebsNr.setText("Betriebsnummer:");

        final JLabel strasse = new JLabel();
        strasse.setFont(bold);
        strasse.setText("Stra\u00DFenname:");

        final JLabel hausnr = new JLabel();
        hausnr.setFont(bold);
        hausnr.setText("Hausnummern:");

        final JLabel inhaber = new JLabel();
        inhaber.setFont(bold);
        inhaber.setText("Name des Inhabers:");

        final JLabel bez = new JLabel();
        bez.setFont(bold);
        bez.setText("Betriebsbezeichnung:");

        final JLabel beginn = new JLabel();
        beginn.setFont(bold);
        beginn.setText("Betriebsbeginn:");

        final JLabel ende = new JLabel();
        ende.setFont(bold);
        ende.setText("Betriebsende:");

        final JLabel strasseAdr = new JLabel();
        strasseAdr.setFont(bold);
        strasseAdr.setText("Stra\u00DFenname im Adressbuch:");

        final JLabel branche = new JLabel();
        branche.setFont(bold);
        branche.setText("Branche aus Adressbuch:");

        final JLabel altstandort = new JLabel();
        altstandort.setFont(bold);
        altstandort.setText("Ist Altstandort ?");

        final JLabel stadtteil = new JLabel();
        stadtteil.setFont(bold);
        stadtteil.setText("Stadtteil:");

        if ((bName.size() > i) && (bName.get(i) != null)) {
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(name, c);
            c.gridx = 1;
            c.insets = new java.awt.Insets(0, 0, 5, 0);
            betrieb.add(new JLabel(bName.get(i)), c);
        }

        if ((bUrlPrefix.size() > i) && (bUrlPrefix.get(i) != null)
                    && (bUrlServer.size() > i)
                    && (bUrlServer.get(i) != null)
                    && (bUrlPath.size() > i)
                    && (bUrlPath.get(i) != null)
                    && (bUrlName.size() > i)
                    && (bUrlName.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(url, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bUrlPrefix.get(i) + bUrlServer.get(i) + bUrlPath.get(i) + bUrlName.get(i)), c);
        }

        if ((bBetriebsnr.size() > i) && (bBetriebsnr.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(betriebsNr, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bBetriebsnr.get(i)), c);
        }

        if ((bStrasse.size() > i) && (bStrasse.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(strasse, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bStrasse.get(i)), c);
        }

        if ((bHausnr.size() > i) && (bHausnr.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(hausnr, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bHausnr.get(i)), c);
        }

        if ((bInhaber.size() > i) && (bInhaber.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(inhaber, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bInhaber.get(i)), c);
        }

        if ((bBez.size() > i) && (bBez.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(bez, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bBez.get(i)), c);
        }

        if ((bBeginn.size() > i) && (bBeginn.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(beginn, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bBeginn.get(i)), c);
        }

        if ((bEnde.size() > i) && (bEnde.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(ende, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bEnde.get(i)), c);
        }

        if ((bStrasseAdr.size() > i) && (bStrasseAdr.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(strasseAdr, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bStrasseAdr.get(i)), c);
        }

        if ((bBranche.size() > i) && (bBranche.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(branche, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bBranche.get(i)), c);
        }

        if ((bAltstandort.size() > i) && (bAltstandort.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y++;
            betrieb.add(altstandort, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bAltstandort.get(i)), c);
        }

        if ((bStadtteil.size() > i) && (bStadtteil.get(i) != null)) {
            c.insets = new Insets(0, 0, 5, 30);
            c.gridx = 0;
            c.gridy = y;
            betrieb.add(stadtteil, c);
            c.insets = new Insets(0, 0, 5, 0);
            c.gridx = 1;
            betrieb.add(new JLabel(bStadtteil.get(i)), c);
        }
        rnd.add(betrieb, BorderLayout.CENTER);
        return rnd;
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
        lblName = new javax.swing.JLabel();
        lblURL = new javax.swing.JLabel();
        lblISBA = new javax.swing.JLabel();
        lblFlaeche = new javax.swing.JLabel();
        lblPlanquadrat = new javax.swing.JLabel();
        panBetriebe = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(313, 206));
        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Altstandort");

        final javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panTitleLayout.createSequentialGroup().addContainerGap().addComponent(lblTitle).addContainerGap(
                    198,
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("URL:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("ISBA-Nummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Fläche in m²:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Planquadrat:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Betriebe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel6, gridBagConstraints);

        lblName.setText("Bergstrasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblName, gridBagConstraints);

        lblURL.setText("18");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblURL, gridBagConstraints);

        lblISBA.setText("66123");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblISBA, gridBagConstraints);

        lblFlaeche.setText("123/67");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblFlaeche, gridBagConstraints);

        lblPlanquadrat.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblPlanquadrat, gridBagConstraints);

        panBetriebe.setOpaque(false);

        final javax.swing.GroupLayout panBetriebeLayout = new javax.swing.GroupLayout(panBetriebe);
        panBetriebe.setLayout(panBetriebeLayout);
        panBetriebeLayout.setHorizontalGroup(
            panBetriebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panBetriebeLayout.setVerticalGroup(
            panBetriebeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(panBetriebe, gridBagConstraints);

        add(panInhalt, java.awt.BorderLayout.WEST);
    } // </editor-fold>//GEN-END:initComponents
}
