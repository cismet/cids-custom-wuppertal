/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Paint;
import java.awt.Stroke;

import java.net.URL;

import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.featurerenderer.*;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;

/**
 * DOCUMENT ME!
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Bplan_verfahrenFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Bplan_verfahrenFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    ImageIcon errorimage = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/tools/metaobjectrenderer/examples/error.png"));
    Properties properties = new Properties();

    private String status;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel lblImagePreview;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BebauungsplanVerfahrenfeatureRenderer object.
     */
    public Bplan_verfahrenFeatureRenderer() {
        initComponents();
        setOpaque(false);
        setPreferredSize(new Dimension(150, 150));
        try {
            properties.load(getClass().getResourceAsStream("/renderer.properties"));
        } catch (Exception e) {
            LOG.warn("Fehler beim Laden der Properties", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public JComponent getInfoComponent(final Refreshable arg0) {
        super.getInfoComponent(arg0);
        return null;
    }

    @Override
    public void assign() {
        cidsBean = metaObject.getBean();
        if (cidsBean != null) {
            status = (String)cidsBean.getProperty("status");
            final String nummer = (String)cidsBean.getProperty("nummer");
            final Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                String subpath = "bplaene/images/nicht_rechtskraeftig/";
                                final String url = properties.getProperty("bebauungsplanurl");
                                ImageIcon i;
                                if (status.equals("rechtskraeftig")) {
                                    subpath = "bplaene/images/nicht_rechtskraeftig/";
                                }
                                if (url == null) {
                                    i = new ImageIcon(
                                            new URL(
                                                "http://s10221.wuppertal-intra.de:80"
                                                        + subpath
                                                        + "B"
                                                        + nummer
                                                        + "_TEXT.gif"));
                                } else {
                                    final String newUrl = url.replaceAll("<cismet::nummer>", nummer);
                                    i = new ImageIcon(new URL(newUrl));
                                }

                                final ImageIcon icon = i;
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            lblImagePreview.setIcon(icon);
                                            Bplan_verfahrenFeatureRenderer.this.setPreferredSize(
                                                new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                                            Bplan_verfahrenFeatureRenderer.this.setSize(
                                                new Dimension(icon.getIconWidth(), icon.getIconHeight()));
                                            revalidate();
                                        }
                                    });
                            } catch (Exception e) {
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            lblImagePreview.setIcon(errorimage);
                                        }
                                    });
                            }
                        }
                    });
            t.start();
        }
    }

    @Override
    public float getTransparency() {
        return 1.0f;
    }

    @Override
    public Paint getFillingStyle() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Bplan_verfahrenFeatureRenderer GetFillingStyle " + status);
        }
        if (status == null) {
            return new Color(0, 0, 0, 255);
        } else {
            if (status.equals("rechtskräftig")) {
                return new Color(0, 255, 0, 50);
            }
            return new Color(255, 0, 0, 50);
        }
    }

    @Override
    public Stroke getLineStyle() {
        final String qualitaet = (String)cidsBean.getProperty("qualitaet");
        if (LOG.isDebugEnabled()) {
            LOG.debug("Bplan_verfahrenFeatureRenderer GetLineStyle " + qualitaet);
        }
        if (qualitaet == null) {
            return new BasicStroke(20.0f);
        } else {
            if (qualitaet.equals("geprüft")) {
                return new CustomFixedWidthStroke(5.0f);
            }
            return new CustomFixedWidthStroke(10.0f);
        }
    }

    @Override
    public Paint getLinePaint() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Bplan_verfahrenFeatureRenderer GetLinePaint " + status);
        }
        if (status == null) {
            return new Color(0, 0, 0, 255);
        } else {
            if (status.equals("rechtskräftig")) {
                return new Color(0, 255, 0, 50);
            }
            return new Color(255, 0, 0, 50);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        lblImagePreview = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        setPreferredSize(new java.awt.Dimension(100, 100));
        setLayout(new java.awt.BorderLayout());

        lblImagePreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreview.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/tools/metaobjectrenderer/examples/load.png"))); // NOI18N
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

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Höhere Auflösung durch Mausklick.");
        add(jLabel12, java.awt.BorderLayout.PAGE_END);
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
        try {
            final String url = properties.getProperty("luftbildschraegaufnahmenservicefull");
            final String newUrl = null;
//
//            if (url==null) {
//                newUrl="http://s10220:8098/luft/tiffer?bnr="+status+"&scale=1&format=JPG";
//            } else{
//                newUrl=url.replaceAll("<cismet::nummer>",status);
//
//            }
//            BrowserLauncher.openURL(newUrl);
        } catch (Exception e) {
        }
    }                                                                               //GEN-LAST:event_lblImagePreviewMouseClicked
}
