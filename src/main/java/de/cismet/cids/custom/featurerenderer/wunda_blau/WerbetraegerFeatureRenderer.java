/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import org.apache.commons.lang.StringUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;

import java.net.URL;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.tools.BrowserLauncher;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class WerbetraegerFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            WerbetraegerFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    String fullUrlA = null;
    String fullUrlB = null;
    ImageIcon errorimage = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));

    /** Creates new form LSAFeatureRenderer. */
    Properties properties = new Properties();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblImagePreviewA;
    private javax.swing.JLabel lblImagePreviewB;
    private javax.swing.JProgressBar prbLoad;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WerbetraegerFeatureRenderer object.
     */
    public WerbetraegerFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(410, 150));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    @Override
    public void assign() {
        prbLoad.setVisible(true);
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final String bezeichnung = (String)cidsBean.getProperty("bezeichnung");
                            if (StringUtils.isBlank(bezeichnung)) {
                                lblImagePreviewA.setIcon(errorimage);
                                prbLoad.setVisible(false);
                                lblImagePreviewB.setVisible(false);
                                LOG.error(
                                    "The Werbetraeger does not have a name. The images can therefore not be loaded.");
                                return;
                            }

                            final String urlA = "http://s10221/cismet/res/werbetafeln/200/t_" + bezeichnung + "a.JPG";
                            final String urlB = "http://s10221/cismet/res/werbetafeln/200/t_" + bezeichnung + "b.JPG";
                            fullUrlA = "http://s10221/cismet/res/werbetafeln/" + bezeichnung + "a.JPG";
                            fullUrlB = "http://s10221/cismet/res/werbetafeln/" + bezeichnung + "b.JPG";

                            final ImageIcon a = new ImageIcon(new URL(urlA));
                            final ImageIcon b = new ImageIcon(new URL(urlB));
                            final JPanel p = new JPanel();
                            p.setOpaque(false);
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        lblImagePreviewA.setIcon(a);
                                        lblImagePreviewB.setIcon(b);
                                        WerbetraegerFeatureRenderer.this.setPreferredSize(
                                            new Dimension(
                                                a.getIconWidth()
                                                        + b.getIconWidth()
                                                        + 6,
                                                Math.max(a.getIconHeight(), b.getIconHeight())
                                                        + 12));
                                        prbLoad.setVisible(false);
                                    }
                                });
                        } catch (Exception e) {
                            EventQueue.invokeLater(new Runnable() {

                                    @Override
                                    public void run() {
                                        lblImagePreviewA.setIcon(errorimage);
                                        prbLoad.setVisible(false);
                                    }
                                });
                        }
                    }
                });
        t.start();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public float getTransparency() {
        return 0.9f;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Paint getFillingStyle() {
        return new Color(100, 100, 100, 50);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        prbLoad = new javax.swing.JProgressBar();
        lblImagePreviewA = new javax.swing.JLabel();
        lblImagePreviewB = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        jLabel2.setText("jLabel2");

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.GridBagLayout());

        prbLoad.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        prbLoad.setBorderPainted(false);
        prbLoad.setIndeterminate(true);
        prbLoad.setPreferredSize(new java.awt.Dimension(100, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(prbLoad, gridBagConstraints);

        lblImagePreviewA.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreviewA.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/load.png"))); // NOI18N
        lblImagePreviewA.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewAMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewAMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewAMouseExited(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(lblImagePreviewA, gridBagConstraints);

        lblImagePreviewB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreviewB.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/load.png"))); // NOI18N
        lblImagePreviewB.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewBMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewBMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewBMouseExited(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(lblImagePreviewB, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Höhere Auflösung durch Mausklick.");
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(jLabel3, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewAMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewAMouseExited
// TODO add your handling code here:
    } //GEN-LAST:event_lblImagePreviewAMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewAMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewAMouseEntered
// TODO add your handling code here:
    } //GEN-LAST:event_lblImagePreviewAMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewAMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewAMouseClicked
        try {
            BrowserLauncher.openURL(fullUrlA);
        } catch (Exception e) {
        }
    }                                                                                //GEN-LAST:event_lblImagePreviewAMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewBMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewBMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                                                               //GEN-LAST:event_lblImagePreviewBMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewBMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewBMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                                                                //GEN-LAST:event_lblImagePreviewBMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewBMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewBMouseClicked
        try {
            BrowserLauncher.openURL(fullUrlB);
        } catch (Exception e) {
        }
    }                                                                                //GEN-LAST:event_lblImagePreviewBMouseClicked
}
