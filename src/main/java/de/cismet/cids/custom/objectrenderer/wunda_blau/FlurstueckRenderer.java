/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.cismet.cids.annotations.AggregationRenderer;
import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.annotations.CidsAttributeVector;

import de.cismet.cids.custom.deprecated.IndentLabel;
import de.cismet.cids.custom.deprecated.JLoadDots;

import de.cismet.cids.tools.metaobjectrenderer.CoolPanel;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.RoundedPanel;

/**
 * de.cismet.cids.objectrenderer.CoolFlurstueckRenderer.
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
@AggregationRenderer
public class FlurstueckRenderer extends CoolPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE = "Flurst\u00FCck";
    private static final String TITLE_AGR = "Flurst\u00FCcke";

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("GEMARKUNGS_NR.NAME")
    public String gemarkungsName = "";

    @CidsAttribute("FLUR")
    public String flur = "";

    @CidsAttribute("FSTNR_Z")
    public Integer zaehler;

    @CidsAttribute("FSTNR_N")
    public Integer nenner;

    @CidsAttribute("X")
    public Float x;

    @CidsAttribute("Y")
    public Float y;

    @CidsAttribute("FLAECHE_ALB")
    public Float flaecheALB;

    @CidsAttribute("FLAECHE_ALK")
    public Float flaecheALK;

    @CidsAttribute("GRUNDBUCH")
    public Integer grundbuch;

    @CidsAttribute("VERW_DIENST")
    public Integer dienststelle;

    @CidsAttribute("LOCATION")
    public String location = "";

    @CidsAttribute("Georeferenz.GEO_STRING")
    public Geometry geometry = null;

    // Aggregation-Zuweisungen
    @CidsAttributeVector("GEMARKUNGS_NR.NAME")
    public Vector<String> nameAgr = new Vector();

    @CidsAttributeVector("FLUR")
    public Vector<String> flurAgr = new Vector();

    @CidsAttributeVector("FSTNR_Z")
    public Vector<Integer> zaehlerAgr = new Vector();

    @CidsAttributeVector("FSTNR_N")
    public Vector<Integer> nennerAgr = new Vector();

    @CidsAttributeVector("FLAECHE_ALB")
    public Vector<Float> albAgr = new Vector();

    @CidsAttributeVector("FLAECHE_ALK")
    public Vector<Float> alkAgr = new Vector();

    @CidsAttributeVector("Grundbuch")
    public Vector<Integer> grundbuchAgr = new Vector();

    @CidsAttributeVector("Georeferenz.GEO_STRING")
    public Vector<Geometry> geoAgr = new Vector();
    public Geometry allGeom;
    private final Logger log = Logger.getLogger(this.getClass());

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
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXHyperlink jxhALB;
    private javax.swing.JLabel lblALB;
    private javax.swing.JLabel lblALK;
    private javax.swing.JLabel lblAgrTitle;
    private javax.swing.JLabel lblDienststelle;
    private javax.swing.JLabel lblFlur;
    private javax.swing.JLabel lblGrundbuch;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblX;
    private javax.swing.JLabel lblY;
    private javax.swing.JLabel lblZaehlerNenner;
    private javax.swing.JPanel panAggregation;
    private javax.swing.JPanel panAgrContent;
    private javax.swing.JPanel panAgrTitle;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolFlurstueckRenderer.
     */
    public FlurstueckRenderer() {
        initComponents();
        setPanContent(panContent);
        setPanInter(panInter);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
        extraAggregationRendererComponent = panAggregation;
        allGeom = null;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assignSingle() {
        if (geometry != null) {
            setGeometry(geometry);
        }

        if (gemarkungsName != null) {
            lblTitle.setText(TITLE + " (" + gemarkungsName + ")");
        } else {
            lblTitle.setText(TITLE);
        }

        if (flur != null) {
            if (gemarkungsName != null) {
                lblTitle.setText(TITLE + " (" + gemarkungsName + ")" + " - " + flur);
            } else {
                lblTitle.setText(TITLE + " - " + flur);
            }
            lblFlur.setText(flur);
        } else {
            jLabel1.setVisible(false);
            lblFlur.setVisible(false);
        }

        if ((zaehler != null) && (nenner != null)) {
            lblZaehlerNenner.setText(zaehler + " / " + nenner);
        } else {
            jLabel2.setVisible(false);
            lblZaehlerNenner.setVisible(false);
        }

        if (x != null) {
            lblX.setText(x.toString());
        } else {
            jLabel3.setVisible(false);
            lblX.setVisible(false);
        }

        if (y != null) {
            lblY.setText(y.toString());
        } else {
            jLabel4.setVisible(false);
            lblY.setVisible(false);
        }

        if (flaecheALB != null) {
            lblALB.setText(flaecheALB.toString());
        } else {
            jLabel5.setVisible(false);
            lblALB.setVisible(false);
        }

        if (flaecheALK != null) {
            lblALK.setText(flaecheALK.toString());
        } else {
            jLabel6.setVisible(false);
            lblALK.setVisible(false);
        }

        if (grundbuch != null) {
            lblGrundbuch.setText(grundbuch.toString());
        } else {
            jLabel7.setVisible(false);
            lblGrundbuch.setVisible(false);
        }

        if (dienststelle != null) {
            lblDienststelle.setText(dienststelle.toString());
        } else {
            jLabel8.setVisible(false);
            lblDienststelle.setVisible(false);
        }

        if (location != null) {
            lblLocation.setText(location);
        } else {
            jLabel9.setVisible(false);
            lblLocation.setVisible(false);
        }
    }

    @Override
    public void assignAggregation() {
        // GridLayout der Anzahl Objekte anpassen
        if ((nameAgr.size() % 3) == 0) {
            ((GridLayout)panAgrContent.getLayout()).setRows(nameAgr.size() / 3);
        } else if ((nameAgr.size() % 3) == 1) {
            ((GridLayout)panAgrContent.getLayout()).setRows((nameAgr.size() + 2) / 3);
        } else {
            ((GridLayout)panAgrContent.getLayout()).setRows((nameAgr.size() + 1) / 3);
        }

        lblAgrTitle.setText(nameAgr.size() + " " + TITLE_AGR);

        // Im Schleife alle Objekte erzeugen und einfuegen
        for (int i = 0; i < nameAgr.size(); i++) {
            if ((allGeom != null) && (geoAgr.get(i) != null)) {
                allGeom = allGeom.union(geoAgr.get(i));
            } else if ((allGeom == null) && (geoAgr.get(i) != null)) {
                allGeom = geoAgr.get(i);
            }
            final Font bold = new Font("Tahoma", Font.BOLD, 11);
            final JLabel jLabel10 = new JLabel("Gemarkungsnr.:");
            jLabel10.setFont(bold);
            final JLabel jLabel11 = new JLabel("Flur:");
            jLabel11.setFont(bold);
            final JLabel jLabel12 = new JLabel("Z\u00E4hler / Nenner:");
            jLabel12.setFont(bold);
            final JLabel jLabel13 = new JLabel("Fl\u00E4che (ALB):");
            jLabel13.setFont(bold);
            final JLabel jLabel14 = new JLabel("Fl\u00E4che (ALK):");
            jLabel14.setFont(bold);
            final JLabel jLabel15 = new JLabel("Grundbuch:");
            jLabel15.setFont(bold);

            final int index = i;

            final Thread t = new Thread(new Runnable() {

//            EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            final RoundedPanel rnd = new RoundedPanel(new BorderLayout());
                            rnd.setLayout(new BorderLayout());

                            final JPanel panTemp = new JPanel();
                            panTemp.setLayout(new GridBagLayout());
                            panTemp.setOpaque(false);

                            panTemp.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 5, 5));

                            final GridBagConstraints c = new GridBagConstraints();
                            c.anchor = GridBagConstraints.NORTHWEST;
                            c.insets = new Insets(0, 0, 5, 30);
                            panTemp.add(jLabel10, c);

                            c.gridx = 0;
                            c.gridy = 1;
                            panTemp.add(jLabel11, c);

                            c.gridx = 0;
                            c.gridy = 2;
                            panTemp.add(jLabel12, c);

                            c.gridx = 0;
                            c.gridy = 3;
                            panTemp.add(jLabel13, c);

                            c.gridx = 0;
                            c.gridy = 4;
                            panTemp.add(jLabel14, c);

                            c.gridx = 0;
                            c.gridy = 5;
                            panTemp.add(jLabel15, c);

                            final JLabel lblNameAgr = new JLabel(nameAgr.get(index));
                            c.gridx = 1;
                            c.gridy = 0;
                            c.insets = new java.awt.Insets(0, 0, 5, 0);
                            panTemp.add(lblNameAgr, c);

                            final JLabel lblFlurAgr = new JLabel(flurAgr.get(index));
                            c.gridx = 1;
                            c.gridy = 1;
                            panTemp.add(lblFlurAgr, c);

                            final JLabel lblZNAgr = new JLabel(zaehlerAgr.get(index) + " / " + nennerAgr.get(index));
                            c.gridx = 1;
                            c.gridy = 2;
                            panTemp.add(lblZNAgr, c);

                            if (albAgr.get(index) != null) {
                                final JLabel lblFlALBAgr = new JLabel(albAgr.get(index).toString());
                                c.gridx = 1;
                                c.gridy = 3;
                                panTemp.add(lblFlALBAgr, c);
                            }

                            if (alkAgr.get(index) != null) {
                                final JLabel lblFlALKAgr = new JLabel(alkAgr.get(index).toString());
                                c.gridx = 1;
                                c.gridy = 4;
                                panTemp.add(lblFlALKAgr, c);
                            }

                            if (grundbuchAgr.get(index) != null) {
                                final JLabel lblGrundbuchAgr = new JLabel(grundbuchAgr.get(index).toString());
                                c.gridx = 1;
                                c.gridy = 5;
                                panTemp.add(lblGrundbuchAgr, c);
                            }

                            rnd.add(panTemp, BorderLayout.CENTER);
                            final JPanel finalPan = rnd;

                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        panAgrContent.add(finalPan);
                                        panAgrContent.validate();
                                    }
                                });
                        }
                    });
            t.start();
        }
        ((CoolPanel)panAggregation).setGeometry(allGeom);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panAggregation = new CoolPanel();
        panAgrTitle = new javax.swing.JPanel();
        lblAgrTitle = new IndentLabel();
        panAgrContent = new javax.swing.JPanel();
        panTitle = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panContent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblFlur = new javax.swing.JLabel();
        lblZaehlerNenner = new javax.swing.JLabel();
        lblX = new javax.swing.JLabel();
        lblY = new javax.swing.JLabel();
        lblALB = new javax.swing.JLabel();
        lblALK = new javax.swing.JLabel();
        lblGrundbuch = new javax.swing.JLabel();
        lblDienststelle = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        panInter = new javax.swing.JPanel();
        jxhALB = new org.jdesktop.swingx.JXHyperlink();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();

        panAggregation.setOpaque(false);
        panAggregation.setLayout(new java.awt.BorderLayout());

        panAgrTitle.setOpaque(false);

        lblAgrTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblAgrTitle.setForeground(new java.awt.Color(51, 51, 51));
        lblAgrTitle.setText("5 Flurst\u00FCcke");

        final javax.swing.GroupLayout panAgrTitleLayout = new javax.swing.GroupLayout(panAgrTitle);
        panAgrTitle.setLayout(panAgrTitleLayout);
        panAgrTitleLayout.setHorizontalGroup(
            panAgrTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panAgrTitleLayout.createSequentialGroup().addContainerGap().addComponent(
                    lblAgrTitle,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    319,
                    Short.MAX_VALUE).addContainerGap()));
        panAgrTitleLayout.setVerticalGroup(
            panAgrTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                panAgrTitleLayout.createSequentialGroup().addComponent(
                    lblAgrTitle,
                    javax.swing.GroupLayout.DEFAULT_SIZE,
                    33,
                    Short.MAX_VALUE).addContainerGap()));

        panAggregation.add(panAgrTitle, java.awt.BorderLayout.NORTH);

        panAgrContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 15, 15));
        panAgrContent.setOpaque(false);
        panAgrContent.setLayout(new java.awt.GridLayout(1, 3, 10, 10));
        panAggregation.add(panAgrContent, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.BorderLayout());

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 15));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Flurst\u00FCck - Barmen (3001) - 10");
        jPanel1.add(lblTitle);

        panTitle.add(jPanel1, java.awt.BorderLayout.CENTER);

        add(panTitle, java.awt.BorderLayout.NORTH);

        panContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 20));
        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Flurnummer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Flurst\u00FCckz\u00E4hler / -nenner:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("X-Koordinate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Y-Koordinate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Fl\u00E4che (ALB):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Fl\u00E4che (ALK):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Grundbuch:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("Verwaltende Dienststelle:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Location:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel9, gridBagConstraints);

        lblFlur.setText("10");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblFlur, gridBagConstraints);

        lblZaehlerNenner.setText("100 / 0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblZaehlerNenner, gridBagConstraints);

        lblX.setText("2582490.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblX, gridBagConstraints);

        lblY.setText("5685030.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblY, gridBagConstraints);

        lblALB.setText("4335.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblALB, gridBagConstraints);

        lblALK.setText("4345.96");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblALK, gridBagConstraints);

        lblGrundbuch.setText("5462");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblGrundbuch, gridBagConstraints);

        lblDienststelle.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblDienststelle, gridBagConstraints);

        lblLocation.setText("136800002001a4da6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblLocation, gridBagConstraints);

        add(panContent, java.awt.BorderLayout.WEST);

        panInter.setOpaque(false);
        panInter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 10));

        jxhALB.setForeground(new java.awt.Color(255, 255, 255));
        jxhALB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/html.png"))); // NOI18N
        jxhALB.setText("Flurst\u00FCck im ALB \u00F6ffnen");
        jxhALB.setClickedColor(new java.awt.Color(204, 204, 204));
        jxhALB.setFocusPainted(false);
        jxhALB.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jxhALB.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jxhALBActionPerformed(evt);
                }
            });
        panInter.add(jxhALB);

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
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxhALBActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jxhALBActionPerformed
        try {
            BrowserLauncher.openURL("http://kif/web/alb/ALB.htm");
        } catch (Exception e) {
            log.error("Konnte Flurst\u00FCck nicht im ALB-Browser \u00F6ffnen", e);
        }
    }                                                                          //GEN-LAST:event_jxhALBActionPerformed
}
