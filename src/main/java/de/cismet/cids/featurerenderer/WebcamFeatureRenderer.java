/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * LSAFeatureRenderer.java
 *
 * Created on 1. Juni 2007, 10:15
 */
package de.cismet.cids.featurerenderer;

import com.sun.media.jai.codecimpl.PNGCodec;

import edu.umd.cs.piccolo.PNode;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;

import java.net.URL;

import java.util.Properties;

import javax.swing.ImageIcon;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.tools.BrowserLauncher;

/**
 * de.cismet.cids.featurerenderer.WebcamFeatureRenderer.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class WebcamFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Url")
    public String url;

    @CidsAttribute("refresh")
    public Integer refresh;

    ImageIcon errorimage = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));

    /** Creates new form LSAFeatureRenderer. */
    Properties properties = new Properties();
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblImagePreview;
    private javax.swing.JProgressBar prbLoad;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WebcamFeatureRenderer object.
     */
    public WebcamFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(340, 250));

        prbLoad.setVisible(false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        // prbLoad.setVisible(true);
        final Thread t = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (true && (refreshable != null) && (refreshable instanceof PNode)
                                    && ((PNode)refreshable).getVisible()) {
                            if (isVisible()) {
                                try {
                                    if (log.isDebugEnabled()) {
                                        log.debug("load" + refreshable);
                                    }
                                    final ImageIcon i = new ImageIcon(new URL(url + "?" + System.currentTimeMillis()));
                                    final ImageIcon icon = i;
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                lblImagePreview.setIcon(icon);
                                                lblImagePreview.repaint();
                                                WebcamFeatureRenderer.this.setPreferredSize(
                                                    new Dimension(icon.getIconWidth() + 10, icon.getIconHeight() + 10));
                                                // WebcamFeatureRenderer.this.setSize(new
                                                // Dimension(icon.getIconWidth(),icon.getIconHeight()));
                                                revalidate();

                                                if (refreshable instanceof PNode) {
                                                    ((PNode)refreshable).repaint();
                                                }
                                                // prbLoad.setVisible(false);
                                            }
                                        });
                                    Thread.sleep(1000 * refresh);
                                } catch (Exception e) {
                                    log.error("Fehler", e);
                                    EventQueue.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                lblImagePreview.setIcon(errorimage);
//                                    prbLoad.setVisible(false);
                                            }
                                        });
                                }
                            } else {
                                // log.debug("not visible)");
                                try {
                                    Thread.sleep(100 * refresh);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                });
        t.start();
//        //((TitledBorder)getBorder()).setTitle(title);
    }

    @Override
    public float getTransparency() {
        return 0.9f;
    }

    @Override
    public Paint getFillingStyle() {
        return new Color(100, 100, 100, 50);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        prbLoad = new javax.swing.JProgressBar();
        lblImagePreview = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setLayout(new java.awt.BorderLayout());

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new java.awt.Dimension(100, 100));
        prbLoad.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        prbLoad.setBorderPainted(false);
        prbLoad.setIndeterminate(true);
        prbLoad.setPreferredSize(new java.awt.Dimension(100, 5));
        add(prbLoad, java.awt.BorderLayout.SOUTH);

        lblImagePreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreview.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/load.png")));
        lblImagePreview.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseClicked(evt);
                }
                @Override
                public void mouseEntered(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseEntered(evt);
                }
                @Override
                public void mouseExited(final java.awt.event.MouseEvent evt) {
                    lblImagePreviewMouseExited(evt);
                }
            });

        add(lblImagePreview, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseExited(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseExited
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }                                                                              //GEN-LAST:event_lblImagePreviewMouseExited

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseEntered(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseEntered
        setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }                                                                               //GEN-LAST:event_lblImagePreviewMouseEntered

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblImagePreviewMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblImagePreviewMouseClicked
//        try {
//            String url=properties.getProperty("luftbildschraegaufnahmenservicefull");
//            String newUrl=null;
//            if (url==null) {
//                newUrl="http://s10220:8098/luft/tiffer?bnr="+nummer+"&scale=1&format=JPG";
//            } else{
//                newUrl=url.replaceAll("<cismet::nummer>",nummer);
//
//            }
//            BrowserLauncher.openURL(newUrl);
//        } catch (Exception e) {
//
//        }
    } //GEN-LAST:event_lblImagePreviewMouseClicked
}
