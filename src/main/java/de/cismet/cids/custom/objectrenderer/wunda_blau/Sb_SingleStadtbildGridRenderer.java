/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.guigarage.jgrid.JGrid;
import com.guigarage.jgrid.renderer.GridCellRenderer;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.JLabel;

import de.cismet.cids.custom.clientutils.StadtbilderUtils;

/**
 * A JGrid renderer for a Stadtbildserie. It is used in the Vorschau and Bin panel of the
 * {@link Sb_stadtbildserieAggregationRenderer}.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_SingleStadtbildGridRenderer extends javax.swing.JPanel implements GridCellRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_SingleStadtbildGridRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private Image image;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lblHighRes;
    private org.jdesktop.swingx.JXImagePanel pnlImage;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_stadtbildserieGridPanel.
     */
    public Sb_SingleStadtbildGridRenderer() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLayeredPane1 = new javax.swing.JLayeredPane();
        lblHighRes = new JLabel() {

                @Override
                protected void paintComponent(final Graphics g) {
                    final Graphics2D g2d = (Graphics2D)g.create();
                    g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setComposite(AlphaComposite.getInstance(
                            AlphaComposite.SRC_OVER,
                            0.75f));
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
        pnlImage = new org.jdesktop.swingx.JXImagePanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setPreferredSize(new java.awt.Dimension(64, 64));
        setLayout(new java.awt.GridBagLayout());

        jLayeredPane1.setLayout(new java.awt.GridBagLayout());

        lblHighRes.setBackground(new java.awt.Color(190, 187, 182));
        lblHighRes.setForeground(new java.awt.Color(0, 0, 0));
        lblHighRes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHighRes,
            org.openide.util.NbBundle.getMessage(
                Sb_SingleStadtbildGridRenderer.class,
                "Sb_SingleStadtbildGridRenderer.lblHighRes.text")); // NOI18N
        lblHighRes.setMaximumSize(new java.awt.Dimension(51, 16));
        lblHighRes.setMinimumSize(new java.awt.Dimension(51, 16));
        lblHighRes.setPreferredSize(new java.awt.Dimension(51, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 3;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        jLayeredPane1.add(lblHighRes, gridBagConstraints);
        jLayeredPane1.setLayer(lblHighRes, 2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jLayeredPane1.add(pnlImage, gridBagConstraints);
        jLayeredPane1.setLayer(pnlImage, 0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        jLayeredPane1.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jLayeredPane1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public Component getGridCellRendererComponent(final JGrid grid,
            final Object value,
            final int index,
            final boolean isSelected,
            final boolean cellHasFocus) {
        image = null;
        final int fixedCellDimension = grid.getFixedCellDimension();
        pnlImage.setPreferredSize(new Dimension(fixedCellDimension, fixedCellDimension));

        if (value instanceof Sb_SingleStadtbildGridObject) {
            final Sb_SingleStadtbildGridObject gridObject = ((Sb_SingleStadtbildGridObject)value);
            image = gridObject.getImage(fixedCellDimension, false);
            final boolean highResAvailable = gridObject.isHighResAvailable();
            final boolean isPreviewAvailable = gridObject.isPreview();
            if (highResAvailable) {
                lblHighRes.setVisible(false);
            } else {
                lblHighRes.setVisible(true);
                if (isPreviewAvailable) {
                    lblHighRes.setText("nur Vorschaubild");
                } else {
                    lblHighRes.setText("nicht Digital");
                }
            }
        }

        if (image != null) {
            pnlImage.setImage(image);
        } else {
            final Image scaledErrorImage = StadtbilderUtils.scaleImage(
                    StadtbilderUtils.ERROR_IMAGE,
                    grid.getFixedCellDimension(),
                    false);
            image = scaledErrorImage;
            pnlImage.setImage(scaledErrorImage);
        }

        return this;
    }
}
