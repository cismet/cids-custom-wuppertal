/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.jdesktop.swingx.graphics.ShadowRenderer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.cismet.cids.custom.deprecated.JBreakLabel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.BrowserLauncher;

/**
 * DOCUMENT ME!
 *
 * @author   nh
 * @version  $Revision$, $Date$
 */
public class WerbetraegerRenderer extends JPanel implements CidsBeanRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE = "Werbeanlagen";
    private static final Logger LOG = Logger.getLogger(WerbetraegerRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private org.jdesktop.swingx.JXHyperlink jhxImage1;
    private org.jdesktop.swingx.JXHyperlink jhxImage2;
    private javax.swing.JLabel lblArt;
    private javax.swing.JLabel lblBez;
    private javax.swing.JLabel lblWerbung;
    private javax.swing.JPanel panContent;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form CoolWerbetraegerRenderer.
     */
    public WerbetraegerRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            panPreviewMap.initMap(cidsBean, "geometrie.geo_field");
            bindingGroup.bind();
            fetchImages();
        }
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        final String bezeichnung = (String)cidsBean.getProperty("bezeichnung");
        if (StringUtils.isNotBlank(bezeichnung)) {
            return TITLE + " - " + bezeichnung;
        } else {
            return TITLE;
        }
    }

    @Override
    public void setTitle(final String title) {
    }

    /**
     * DOCUMENT ME!
     */
    private void fetchImages() {
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final String bezeichnung = (String)cidsBean.getProperty("bezeichnung");
                        if (StringUtils.isBlank(bezeichnung)) {
                            jhxImage1.setVisible(false);
                            jhxImage2.setVisible(false);
                            LOG.error("The Werbetraeger does not have a name. The images can therefore not be loaded.");
                            return;
                        }

                        final String urlA = "http://s10221/cismet/res/werbetafeln/200/t_" + bezeichnung + "a.JPG";
                        final String urlB = "http://s10221/cismet/res/werbetafeln/200/t_" + bezeichnung + "b.JPG";
                        final String fullUrlA = "http://s10221/cismet/res/werbetafeln/" + bezeichnung + "a.JPG";
                        final String fullUrlB = "http://s10221/cismet/res/werbetafeln/" + bezeichnung + "b.JPG";
                        try {
                            // 1. Bild erzeugen
                            final ImageIcon a = new ImageIcon(new URL(urlA));
                            final ShadowRenderer renderer = new ShadowRenderer(3, 0.5f, Color.BLACK);
                            final BufferedImage temp = new BufferedImage(
                                    a.getIconWidth(),
                                    a.getIconHeight(),
                                    BufferedImage.TYPE_4BYTE_ABGR);
                            final Graphics2D ag = temp.createGraphics();
                            ag.drawImage(a.getImage(), 0, 0, null);
                            ag.dispose();

                            // Schatten erstellen
                            final BufferedImage shadow = renderer.createShadow(temp);

                            final BufferedImage result = new BufferedImage(
                                    a.getIconWidth()
                                            + 6,
                                    a.getIconHeight()
                                            + 6,
                                    BufferedImage.TYPE_4BYTE_ABGR);
                            final Graphics2D rg = result.createGraphics();
                            rg.drawImage(shadow, 0, 0, null);
                            rg.drawImage(temp, 0, 0, null);
                            rg.setColor(new Color(0, 0, 0, 120));
                            rg.drawRect(0, 0, a.getIconWidth(), a.getIconHeight());
                            rg.dispose();
                            shadow.flush();
                            final ImageIcon afinal = new ImageIcon(result);
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jhxImage1.setIcon(afinal);
                                        jhxImage1.addActionListener(new ActionListener() {

                                                @Override
                                                public void actionPerformed(final ActionEvent e) {
                                                    try {
                                                        BrowserLauncher.openURL(fullUrlA);
                                                    } catch (Exception ex) {
                                                        LOG.error(
                                                            "Fehler beim \u00F6ffnen der URL \""
                                                                    + fullUrlA
                                                                    + "\"",
                                                            ex);
                                                    }
                                                }
                                            });
                                    }
                                });
                        } catch (Exception e) {
                            LOG.error("Konnte Werbetafel-Bild mit URL \"" + urlA + "\" nicht laden.", e);
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jhxImage1.setVisible(false);
                                    }
                                });
                        }

                        try {
                            // 2. Bild erzeugen
                            final ImageIcon b = new ImageIcon(new URL(urlB));
                            final ShadowRenderer renderer = new ShadowRenderer(3, 0.5f, Color.BLACK);
                            final BufferedImage temp = new BufferedImage(
                                    b.getIconWidth(),
                                    b.getIconHeight(),
                                    BufferedImage.TYPE_4BYTE_ABGR);
                            final Graphics2D tg = temp.createGraphics();
                            tg.drawImage(b.getImage(), 0, 0, null);
                            tg.dispose();

                            // Schatten erstellen
                            final BufferedImage shadow = renderer.createShadow(temp);

                            final BufferedImage result = new BufferedImage(
                                    b.getIconWidth()
                                            + 6,
                                    b.getIconHeight()
                                            + 6,
                                    BufferedImage.TYPE_4BYTE_ABGR);
                            final Graphics2D rg = result.createGraphics();
                            rg.drawImage(shadow, 0, 0, null);
                            rg.drawImage(temp, 0, 0, null);
                            rg.setColor(new Color(0, 0, 0, 120));
                            rg.drawRect(0, 0, b.getIconWidth(), b.getIconHeight());
                            rg.dispose();
                            shadow.flush();
                            final ImageIcon bfinal = new ImageIcon(result);
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jhxImage2.setIcon(bfinal);
                                        jhxImage2.addActionListener(new ActionListener() {

                                                @Override
                                                public void actionPerformed(final ActionEvent e) {
                                                    try {
                                                        BrowserLauncher.openURL(fullUrlB);
                                                    } catch (Exception ex) {
                                                        LOG.error(
                                                            "Fehler beim \u00F6ffnen der URL \""
                                                                    + fullUrlB
                                                                    + "\"",
                                                            ex);
                                                    }
                                                }
                                            });
                                    }
                                });
                        } catch (Exception e) {
                            LOG.error("Konnte Werbetafel-Bild mit URL \"" + urlB + "\" nicht laden.", e);
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        jhxImage2.setVisible(false);
                                    }
                                });
                        }
                    }
                });
        t.start();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        panContent = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblBez = new javax.swing.JLabel();
        lblWerbung = new JBreakLabel();
        jLabel3 = new javax.swing.JLabel();
        lblArt = new javax.swing.JLabel();
        jhxImage1 = new org.jdesktop.swingx.JXHyperlink();
        jhxImage2 = new org.jdesktop.swingx.JXHyperlink();
        panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Bezeichnung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel2.setText("momentane Werbung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel2, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                lblBez,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblBez, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.momentane_werbung}"),
                lblWerbung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panContent.add(lblWerbung, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("Art:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 30);
        panContent.add(jLabel3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art.bezeichnung}"),
                lblArt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 30);
        jPanel1.add(panContent, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 5);
        jPanel1.add(panPreviewMap, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
