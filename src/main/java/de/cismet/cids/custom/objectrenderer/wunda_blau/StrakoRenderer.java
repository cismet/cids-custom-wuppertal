package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.tools.metaobjectrenderer.CoolPanel;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.custom.deprecated.JLoadDots;
import java.sql.Timestamp;

/**
 * de.cismet.cids.objectrenderer.CoolStraKoRenderer
 * 
 * @author nh
 */
public class StrakoRenderer extends CoolPanel {
    
    @CidsAttribute("Strako_ID")
    public Integer id;
    
    @CidsAttribute("Betreff")
    public String betreff = "";
    
    @CidsAttribute("Schaden")
    public String schaden = "";
    
    @CidsAttribute("Menge")
    public String menge = "";
    
    @CidsAttribute("Einheit")
    public String einheit = "";
    
    @CidsAttribute("Zust\u00E4ndigkeit")
    public String zust = "";
    
    @CidsAttribute("Prioritaet")
    public String prio = "";
    
    @CidsAttribute("Status")
    public String status = "";
    
    @CidsAttribute("Strasse")
    public String strasse = "";
    
    @CidsAttribute("Strassenschluessel")
    public String strassenschl = "";
    
    @CidsAttribute("von")
    public String von = "";
    
    @CidsAttribute("bis")
    public String bis = "";
    
    @CidsAttribute("Objekt")
    public String obj = "";
    
    @CidsAttribute("bis Hausnummer")
    public String hausnrVon = "";
    
    @CidsAttribute("von Hausnummer")
    public String hausnrBis = "";
    
    @CidsAttribute("Lokaladverb")
    public String lokaladv = "";
    
    @CidsAttribute("Merkmal")
    public String merkmal = "";
    
    @CidsAttribute("Bereich")
    public String bereich = "";
    
    @CidsAttribute("Seite")
    public String seite = "";
    
    @CidsAttribute("Tour")
    public String tour = "";
    
    @CidsAttribute("Bemerkung")
    public String bemerkung = "";
    
    @CidsAttribute("Kontrolleur")
    public String kontrolleur = "";
    
    @CidsAttribute("Datum und Uhrzeit")
    public Timestamp datum;
    
    @CidsAttribute("PunktGeometrie.GEO_STRING")
    public Geometry geom = null;
    
    /**
     * Creates new form CoolAdressenRenderer
     */
    public StrakoRenderer() {
        initComponents();
        setPanContent(panInhalt);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }

    @Override
    public void assignSingle() {
        if (geom != null) {
            super.setGeometry(geom);
        }

        if (!strasse.equals("")) {
            lblTitle.setText("StraKo - " + id + " (" + strasse + ")");
        } else {
            lblTitle.setText("StraKo - (" + id + ")");
        }

        if (!betreff.equals("")) {
            lblBetreff.setText(betreff);
        } else {
            jLabel1.setVisible(false);
            lblBetreff.setVisible(false);
        }

        if (!schaden.equals("")) {
            lblSchaden.setText(schaden);
        } else {
            jLabel2.setVisible(false);
            lblSchaden.setVisible(false);
        }

        if (!menge.equals("")) {
            if (!einheit.equals("")) {
                lblMenge.setText(menge + " " + einheit);
            } else {
                lblMenge.setText(menge);
            }
        } else {
            jLabel3.setVisible(false);
            lblMenge.setVisible(false);
        }

        if (!zust.equals("")) {
            lblZust.setText(zust);
        } else {
            jLabel4.setVisible(false);
            lblZust.setVisible(false);
        }

        if (!prio.equals("")) {
            lblPrio.setText(prio);
        } else {
            jLabel5.setVisible(false);
            lblPrio.setVisible(false);
        }

        if (!status.equals("") && !status.equals("null")) {
            lblStatus.setText(status);
        } else {
            jLabel6.setVisible(false);
            lblStatus.setVisible(false);
        }

        if (!strassenschl.equals("")) {
            lblStrasse.setText(strassenschl);
        } else {
            jLabel7.setVisible(false);
            lblStrasse.setVisible(false);
        }

        if (!von.equals("") && !von.equals("null")) {
            lblVonBis.setText(von);
        } else {
            jLabel8.setVisible(false);
            lblVonBis.setVisible(false);
        }

        if (!bis.equals("") && !bis.equals("null")) {
            jLabel8.setVisible(true);
            lblVonBis.setVisible(true);
            lblVonBis.setText(lblVonBis.getText() + " - " + bis);
        }

        if (!obj.equals("") && !obj.equals("null")) {
            lblObjekt.setText(obj);
        } else {
            jLabel9.setVisible(false);
            lblObjekt.setVisible(false);
        }

        if (!hausnrVon.equals("") && !hausnrVon.equals("null")) {
            lblHausnrVonBis.setText(hausnrVon);
        } else {
            jLabel10.setVisible(false);
            lblHausnrVonBis.setVisible(false);
        }

        if (!hausnrBis.equals("") && !hausnrBis.equals("null")) {
            jLabel10.setVisible(true);
            lblHausnrVonBis.setVisible(true);
            lblHausnrVonBis.setText(lblHausnrVonBis.getText() + " - " + hausnrBis);
        }

        if (!lokaladv.equals("")) {
            lblLokaladv.setText(lokaladv);
        } else {
            jLabel11.setVisible(false);
            lblLokaladv.setVisible(false);
        }

        if (!merkmal.equals("")) {
            lblMerkmal.setText(merkmal);
        } else {
            jLabel12.setVisible(false);
            lblMerkmal.setVisible(false);
        }

        if (!bereich.equals("")) {
            lblBereich.setText(bereich);
        } else {
            jLabel13.setVisible(false);
            lblBereich.setVisible(false);
        }

        if (!seite.equals("")) {
            lblSeite.setText(seite);
        } else {
            jLabel14.setVisible(false);
            lblSeite.setVisible(false);
        }

        if (!tour.equals("")) {
            lblTour.setText(tour);
        } else {
            jLabel15.setVisible(false);
            lblTour.setVisible(false);
        }

        if (!bemerkung.equals("")) {
            lblBemerkung.setText(bemerkung);
        } else {
            jLabel16.setVisible(false);
            lblBemerkung.setVisible(false);
        }

        if (!kontrolleur.equals("")) {
            lblKontrolleur.setText(kontrolleur);
        } else {
            jLabel17.setVisible(false);
            lblKontrolleur.setVisible(false);
        }

        if (datum != null) {
            lblDatum.setText(datum.toString());
        } else {
            jLabel18.setVisible(false);
            lblDatum.setVisible(false);
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblBetreff = new javax.swing.JLabel();
        lblSchaden = new javax.swing.JLabel();
        lblMenge = new javax.swing.JLabel();
        lblZust = new javax.swing.JLabel();
        lblPrio = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblStrasse = new javax.swing.JLabel();
        lblVonBis = new javax.swing.JLabel();
        lblObjekt = new javax.swing.JLabel();
        lblHausnrVonBis = new javax.swing.JLabel();
        lblLokaladv = new javax.swing.JLabel();
        lblMerkmal = new javax.swing.JLabel();
        lblBereich = new javax.swing.JLabel();
        lblSeite = new javax.swing.JLabel();
        lblTour = new javax.swing.JLabel();
        lblBemerkung = new javax.swing.JLabel();
        lblKontrolleur = new javax.swing.JLabel();
        lblDatum = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        panTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("StraKo");

        javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addContainerGap(393, Short.MAX_VALUE))
        );
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(panTitle, java.awt.BorderLayout.NORTH);

        panInter.setOpaque(false);

        javax.swing.GroupLayout panInterLayout = new javax.swing.GroupLayout(panInter);
        panInter.setLayout(panInterLayout);
        panInterLayout.setHorizontalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 469, Short.MAX_VALUE)
        );
        panInterLayout.setVerticalGroup(
            panInterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        add(panInter, java.awt.BorderLayout.SOUTH);

        panMap.setOpaque(false);
        panMap.setLayout(new java.awt.GridBagLayout());

        panSpinner.setMaximumSize(new java.awt.Dimension(100, 100));
        panSpinner.setMinimumSize(new java.awt.Dimension(100, 100));
        panSpinner.setOpaque(false);

        javax.swing.GroupLayout panSpinnerLayout = new javax.swing.GroupLayout(panSpinner);
        panSpinner.setLayout(panSpinnerLayout);
        panSpinnerLayout.setHorizontalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        panSpinnerLayout.setVerticalGroup(
            panSpinnerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        panMap.add(panSpinner, new java.awt.GridBagConstraints());

        add(panMap, java.awt.BorderLayout.CENTER);

        panInhalt.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 15, 10, 20));
        panInhalt.setOpaque(false);
        panInhalt.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Betreff:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("Schaden:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Menge:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel3, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel4.setText("Zuständigkeit:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Priorität:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Status:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel7.setText("Straßenschlüssel:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel8.setText("von / bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel8, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setText("Objekt:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setText("Hausnummer von / bis:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel10, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel11.setText("Lokaladverb:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel11, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel12.setText("Merkmal:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel12, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setText("Bereich:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel13, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText("Seite:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel14, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Tour:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel15, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setText("Bemerkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel16, gridBagConstraints);

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setText("Kontrolleur:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel17, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setText("Datum:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 20);
        panInhalt.add(jLabel18, gridBagConstraints);

        lblBetreff.setText("Bergstrasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblBetreff, gridBagConstraints);

        lblSchaden.setText("18");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblSchaden, gridBagConstraints);

        lblMenge.setText("300 Meter");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblMenge, gridBagConstraints);

        lblZust.setText("123/67");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblZust, gridBagConstraints);

        lblPrio.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblPrio, gridBagConstraints);

        lblStatus.setText("23");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblStatus, gridBagConstraints);

        lblStrasse.setText("Bergstrasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblStrasse, gridBagConstraints);

        lblVonBis.setText("18");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblVonBis, gridBagConstraints);

        lblObjekt.setText("66123");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblObjekt, gridBagConstraints);

        lblHausnrVonBis.setText("123/67");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblHausnrVonBis, gridBagConstraints);

        lblLokaladv.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblLokaladv, gridBagConstraints);

        lblMerkmal.setText("23");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblMerkmal, gridBagConstraints);

        lblBereich.setText("Bergstrasse");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblBereich, gridBagConstraints);

        lblSeite.setText("18");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblSeite, gridBagConstraints);

        lblTour.setText("66123");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblTour, gridBagConstraints);

        lblBemerkung.setText("123/67");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblBemerkung, gridBagConstraints);

        lblKontrolleur.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 16;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblKontrolleur, gridBagConstraints);

        lblDatum.setText("23");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panInhalt.add(lblDatum, gridBagConstraints);

        add(panInhalt, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents
    
    
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
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBereich;
    private javax.swing.JLabel lblBetreff;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblHausnrVonBis;
    private javax.swing.JLabel lblKontrolleur;
    private javax.swing.JLabel lblLokaladv;
    private javax.swing.JLabel lblMenge;
    private javax.swing.JLabel lblMerkmal;
    private javax.swing.JLabel lblObjekt;
    private javax.swing.JLabel lblPrio;
    private javax.swing.JLabel lblSchaden;
    private javax.swing.JLabel lblSeite;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTour;
    private javax.swing.JLabel lblVonBis;
    private javax.swing.JLabel lblZust;
    private javax.swing.JPanel panInhalt;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    
}
