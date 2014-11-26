/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * BPlanRenderer.java
 *
 * Created on 18. Februar 2008, 16:26
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import java.awt.EventQueue;

import java.io.InputStream;

import java.net.URL;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class Bplan_planRenderer extends JPanel implements CidsBeanRenderer, RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Bplan_planRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;
    private ImageIcon bild;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXImageView jxivBild;
    private javax.swing.JPanel panContent;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form BplaeneObjectRenderer.
     */
    public Bplan_planRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "kif",
            "bplan_plan",
            130,
            "Bebauungsplan",
            1280,
            1024);
    }

    /**
     * DOCUMENT ME!
     */
    private void loadTheImage() {
        final String namePlus = (String)cidsBean.getProperty("nameplus");
        if (namePlus != null) {
            try {
                final String host = "http://s10221.wuppertal-intra.de:80/";
                final String btyp;
                final String plan;
                String path;
                final String r = namePlus;
                btyp = r.substring(0, 1);

                plan = "B" + r.substring(1);
                path = "bplaene/images/rechtskraeftig/";
                if (btyp.equals("N")) {
                    path = "bplaene/images/nicht_rechtskraeftig/";
                }

                final String url = host + path + plan + "_TEXT.gif";

                final Thread loader = new Thread() {

                        @Override
                        public void run() {
                            try {
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            jxivBild.setImage(
                                                new javax.swing.ImageIcon(getClass().getResource("/res/load.png"))
                                                            .getImage());
                                        }
                                    });

                                final InputStream is = WebAccessManager.getInstance().doRequest(new URL(url));
                                bild = new ImageIcon(ImageIO.read(is));
                                EventQueue.invokeLater(new Runnable() {

                                        @Override
                                        public void run() {
                                            final double bh = bild.getIconHeight();
                                            final double bw = bild.getIconWidth();
                                            jxivBild.setImage(bild.getImage());
                                            jxivBild.setScale(0.25);
                                            jxivBild.setVisible(false);
                                            jxivBild.setVisible(true);
                                            revalidate();
                                            final double ch = jxivBild.getHeight();
                                            final double cw = jxivBild.getWidth();
                                            final double cs = jxivBild.getScale();
                                            final double bsh = bh / ch;
                                            final double bsw = bw / cw;
                                            final double sm = Math.max(bsh, bsw);
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("EDT?:" + EventQueue.isDispatchThread());
                                            }
                                            LOG.info(
                                                "bh: "
                                                        + bh
                                                        + " bw: "
                                                        + bw
                                                        + "ch: "
                                                        + ch
                                                        + "cw: "
                                                        + cw
                                                        + "cs:  "
                                                        + cs
                                                        + "sm:"
                                                        + sm);
                                            jxivBild.setEditable(true);
                                            jxivBild.setDragEnabled(false);
                                        }
                                    });
                            } catch (Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    };
                loader.start();
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panContent = new javax.swing.JPanel();
        jxivBild = new org.jdesktop.swingx.JXImageView();

        setLayout(new java.awt.BorderLayout());

        panContent.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panContent.setOpaque(false);
        panContent.setLayout(new java.awt.BorderLayout());

        jxivBild.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jxivBild.addMouseWheelListener(new java.awt.event.MouseWheelListener() {

                @Override
                public void mouseWheelMoved(final java.awt.event.MouseWheelEvent evt) {
                    jxivBildMouseWheelMoved(evt);
                }
            });

        final javax.swing.GroupLayout jxivBildLayout = new javax.swing.GroupLayout(jxivBild);
        jxivBild.setLayout(jxivBildLayout);
        jxivBildLayout.setHorizontalGroup(
            jxivBildLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                436,
                Short.MAX_VALUE));
        jxivBildLayout.setVerticalGroup(
            jxivBildLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                552,
                Short.MAX_VALUE));

        panContent.add(jxivBild, java.awt.BorderLayout.CENTER);

        add(panContent, java.awt.BorderLayout.CENTER);
    } // </editor-fold>//GEN-END:initComponents
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jxivBildMouseWheelMoved(final java.awt.event.MouseWheelEvent evt) { //GEN-FIRST:event_jxivBildMouseWheelMoved
        if (evt.getWheelRotation() < 0) {
            jxivBild.setScale(jxivBild.getScale() * 0.95);
        } else {
            jxivBild.setScale(jxivBild.getScale() * 1.05);
        }
    }                                                                                //GEN-LAST:event_jxivBildMouseWheelMoved

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            loadTheImage();
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        final String nummer = String.valueOf(cidsBean.getProperty("nummer"));
        String shownStatus;
        if ("rechtskräftig".equals(cidsBean.getProperty("status"))) {
            shownStatus = "rechtsverbindlich";
        } else {
            shownStatus = "nicht rechtsverbindlich";
        }
        return "Bebauungsplan - " + nummer + " (" + shownStatus + ")";
    }

    @Override
    public void setTitle(final String title) {
    }
}
