package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.tools.metaobjectrenderer.CoolPanel;
import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.custom.deprecated.JBreakLabel;
import de.cismet.cids.custom.deprecated.JLoadDots;
import de.cismet.tools.BrowserLauncher;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.graphics.ShadowRenderer;

/**
 * de.cismet.cids.objectrenderer.CoolWerbetraegerRenderer
 * @author  nh
 */
public class WerbetraegerRenderer extends CoolPanel {
    private final Logger log = Logger.getLogger(this.getClass());
    
   @CidsAttribute("Objektname")
    public String bez = "";

    @CidsAttribute("Werbung zum Aufnahmezeitpunkt")
    public String werbung = "";

    @CidsAttribute("Art.Bezeichnung")
    public String art = "";

    @CidsAttribute("Geometrie.GEO_STRING")
    public Geometry geometry = null;

    private static final String TITLE = "Werbeanlagen";
    private final ImageIcon errorimage = new ImageIcon(getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));
    
    /** 
     * Creates new form CoolWerbetraegerRenderer
     */
    public WerbetraegerRenderer() {
        initComponents();
        setPanContent(panContent);
        setPanInter(null);
        setPanMap(panMap);
        setPanTitle(panTitle);
        setSpinner(panSpinner);
    }
    
    @Override
    public void assignSingle() {
        if (geometry != null)
            setGeometry(geometry);
        
        if (bez != null) {
            lblTitle.setText(TITLE + " - " + bez);
            lblBez.setText(bez);

        } else {
            lblTitle.setText(TITLE);
            lblBez.setVisible(false);
            jLabel1.setVisible(false);
        }
        
        if (werbung != null) {
            lblWerbung.setText(werbung);
        } else {
            jLabel2.setVisible(false);
            lblWerbung.setVisible(false);
        }
        
        if (art != null) {
            lblArt.setText(art);
        } else {
            jLabel3.setVisible(false);
            lblArt.setVisible(false);
        }
        
        Thread t = new Thread(new Runnable() {
            public void run() {
//                    String urlA = "http://kif/web/werbetafeln/200/t_"+bez+"a.JPG";
//                    String urlB = "http://kif/web/werbetafeln/200/t_"+bez+"b.JPG";
//                    final String fullUrlA = "http://kif/web/werbetafeln/"+bez+"a.JPG";
//                    final String fullUrlB = "http://kif/web/werbetafeln/"+bez+"b.JPG";
               
                    String urlA = "http://s10221/cismet/res/werbetafeln/200/t_"+bez+"a.JPG";
                    String urlB = "http://s10221/cismet/res/werbetafeln/200/t_"+bez+"b.JPG";
                    final String fullUrlA = "http://s10221/cismet/res/werbetafeln/"+bez+"a.JPG";
                    final String fullUrlB = "http://s10221/cismet/res/werbetafeln/"+bez+"b.JPG";
                    try {
                        // 1. Bild erzeugen
                        ImageIcon a = new ImageIcon(new URL(urlA));
                        ShadowRenderer renderer = new ShadowRenderer(3, 0.5f, Color.BLACK);
                        BufferedImage temp = new BufferedImage(a.getIconWidth(),a.getIconHeight(),
                                BufferedImage.TYPE_4BYTE_ABGR);
                        Graphics2D ag = temp.createGraphics();
                        ag.drawImage(a.getImage(),0,0,null);
                        ag.dispose();

                        // Schatten erstellen
                        BufferedImage shadow = renderer.createShadow(temp);

                        BufferedImage result = new BufferedImage(a.getIconWidth()+6,a.getIconHeight()+6,
                                BufferedImage.TYPE_4BYTE_ABGR);
                        Graphics2D rg = result.createGraphics();
                        rg.drawImage(shadow,0,0,null);
                        rg.drawImage(temp,0,0,null);
                        rg.setColor(new Color(0,0,0,120));
                        rg.drawRect(0,0,a.getIconWidth(),a.getIconHeight());
                        rg.dispose();
                        shadow.flush();
                        final ImageIcon afinal = new ImageIcon(result);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                jhxImage1.setIcon(afinal);
                                jhxImage1.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            BrowserLauncher.openURL(fullUrlA);
                                        } catch (Exception ex) {
                                            log.error("Fehler beim \u00F6ffnen der URL \"" + fullUrlA + "\"",ex);
                                        }
                                    }
                                });
                            }
                        });
                    } 
                    catch(Exception e) {
                        log.error("Konnte Werbetafel-Bild mit URL \"" + urlA + "\" nicht laden.", e);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
//                                lblImage1.setIcon(errorimage);
                                jhxImage1.setVisible(false);
                            }
                        });
                    }
                    
                    try {
                        // 2. Bild erzeugen
                        ImageIcon b = new ImageIcon(new URL(urlB));
                        ShadowRenderer renderer = new ShadowRenderer(3, 0.5f, Color.BLACK);
                        BufferedImage temp = new BufferedImage(b.getIconWidth(),b.getIconHeight(),
                                BufferedImage.TYPE_4BYTE_ABGR);
                        Graphics2D tg = temp.createGraphics();
                        tg.drawImage(b.getImage(),0,0,null);
                        tg.dispose();

                        // Schatten erstellen
                        BufferedImage shadow = renderer.createShadow(temp);

                        BufferedImage result = new BufferedImage(b.getIconWidth()+6,b.getIconHeight()+6,
                                BufferedImage.TYPE_4BYTE_ABGR);
                        Graphics2D rg = result.createGraphics();
                        rg.drawImage(shadow,0,0,null);
                        rg.drawImage(temp,0,0,null);
                        rg.setColor(new Color(0,0,0,120));
                        rg.drawRect(0,0,b.getIconWidth(),b.getIconHeight());
                        rg.dispose();
                        shadow.flush();
                        final ImageIcon bfinal = new ImageIcon(result);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                jhxImage2.setIcon(bfinal);
                                jhxImage2.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        try {
                                            BrowserLauncher.openURL(fullUrlB);
                                        } catch (Exception ex) {
                                            log.error("Fehler beim \u00F6ffnen der URL \"" + fullUrlB + "\"",ex);
                                        }
                                    }
                                });
                            }
                        });
                    }
                    catch(Exception e) {
                        log.error("Konnte Werbetafel-Bild mit URL \"" + urlB + "\" nicht laden.", e);
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
//                                lblImage2.setIcon(errorimage);
                                jhxImage2.setVisible(false);
                            }
                        });
                    }
                    
            }
        });
        t.start();
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
        panContent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblBez = new javax.swing.JLabel();
        lblWerbung = new JBreakLabel();
        jLabel3 = new javax.swing.JLabel();
        lblArt = new javax.swing.JLabel();
        jhxImage1 = new org.jdesktop.swingx.JXHyperlink();
        jhxImage2 = new org.jdesktop.swingx.JXHyperlink();
        panInter = new javax.swing.JPanel();
        panMap = new javax.swing.JPanel();
        panSpinner = new JLoadDots();

        setLayout(new java.awt.BorderLayout());

        panTitle.setOpaque(false);

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("Werbetafel");

        javax.swing.GroupLayout panTitleLayout = new javax.swing.GroupLayout(panTitle);
        panTitle.setLayout(panTitleLayout);
        panTitleLayout.setHorizontalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addContainerGap(638, Short.MAX_VALUE))
        );
        panTitleLayout.setVerticalGroup(
            panTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(panTitle, java.awt.BorderLayout.NORTH);

        panContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 15, 10, 20));
        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("Bezeichnung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setText("momentane Werbung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel2, gridBagConstraints);

        lblBez.setText("Werbetafel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblBez, gridBagConstraints);

        lblWerbung.setText("McDonalds");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblWerbung, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setText("Art:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel3, gridBagConstraints);

        lblArt.setText("Stelltafel");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblArt, gridBagConstraints);

        jhxImage1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        jhxImage1.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 30);
        panContent.add(jhxImage1, gridBagConstraints);

        jhxImage2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))); // NOI18N
        jhxImage2.setFocusPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        panContent.add(jhxImage2, gridBagConstraints);

        add(panContent, java.awt.BorderLayout.WEST);

        panInter.setOpaque(false);
        panInter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 20, 10));
        add(panInter, java.awt.BorderLayout.SOUTH);

        panMap.setOpaque(false);
        panMap.setLayout(new java.awt.GridBagLayout());

        panSpinner.setMaximumSize(new java.awt.Dimension(100, 100));
        panSpinner.setMinimumSize(new java.awt.Dimension(100, 100));
        panSpinner.setOpaque(false);
        panSpinner.setPreferredSize(new java.awt.Dimension(100, 100));

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
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private org.jdesktop.swingx.JXHyperlink jhxImage1;
    private org.jdesktop.swingx.JXHyperlink jhxImage2;
    private javax.swing.JLabel lblArt;
    private javax.swing.JLabel lblBez;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWerbung;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panInter;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panSpinner;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables
    
}
