/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultListModel;

import de.cismet.cids.custom.clientutils.Sb_RestrictionLevelUtils;
import de.cismet.cids.custom.clientutils.StadtbilderUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
/**
 * A container for a stadtbild and a Sb_stadtbildserieGridObject. The Sb_stadtbildserieGridObject is the gridObject used
 * in the Vorschau and the Bin for a Stadtbildserie which contains the stadtbild.
 *
 * @version  $Revision$, $Date$
 */
public class Sb_SingleStadtbildGridObject extends Sb_AbstractPictureGridObject {

    //~ Static fields/initializers ---------------------------------------------

    private static final ExecutorService highResAvailableThreadPool = CismetExecutors.newFixedThreadPool(20);

    //~ Instance fields --------------------------------------------------------

    private final CidsBean stadtbild;
    private final Sb_stadtbildserieGridObject locationOfStadtbild;
    private AtomicBoolean imageAvailableInHighRes;
    private DefaultListModel gridModel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GridObject object.
     *
     * @param  stadtbild            DOCUMENT ME!
     * @param  locationOfStadtbild  DOCUMENT ME!
     * @param  gridModel            DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public Sb_SingleStadtbildGridObject(final CidsBean stadtbild,
            final Sb_stadtbildserieGridObject locationOfStadtbild,
            final DefaultListModel gridModel,
            final ConnectionContext connectionContext) {
        super(connectionContext);

        this.stadtbild = stadtbild;
        this.locationOfStadtbild = locationOfStadtbild;

        imageAvailableInHighRes = new AtomicBoolean(true);

        this.gridModel = gridModel;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    void startThreadToDetermineIfHighResImageAvailable() {
        highResAvailableThreadPool.submit(new Runnable() {

                @Override
                public void run() {
                    final CidsBean stadtbildSerie = locationOfStadtbild.getStadtbildserie();
                    final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                            stadtbildSerie,
                            stadtbild);
                    imageAvailableInHighRes.set(StadtbilderUtils.getFormatOfHighResPicture(stadtbildInfo)
                                != null);
                    Sb_SingleStadtbildGridObject.this.notifyModel();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isHighResAvailable() {
        return imageAvailableInHighRes.get();
    }

    @Override
    protected StadtbilderUtils.StadtbildInfo getStadtbildInfo() {
        final StadtbilderUtils.StadtbildInfo stadtbildInfo = new StadtbilderUtils.StadtbildInfo(
                locationOfStadtbild.getStadtbildserie(),
                stadtbild);
        return stadtbildInfo;
    }

    @Override
    protected int getDownloadPrority() {
        return StadtbilderUtils.NORMAL_PRIORITY;
    }

    @Override
    protected void notifyModel() {
        final int position = gridModel.indexOf(this);
        if ((position >= 0) && (position < gridModel.getSize())) {
            // adds itself to the gridModel at the same position. to update the gridModel
            gridModel.setElementAt(
                this,
                position);
        }
    }

    /**
     * Gets the scaled image from the super implementation and adds an overlay, if needed. The overlay indicates that no
     * high resolution image of the current stadtbild is available.
     *
     * <p>Never returns null. If an error occurs, an error image is returned.</p>
     *
     * @param   cellDimension  DOCUMENT ME!
     * @param   invert         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Image getImage(final int cellDimension, final boolean invert) {
        final Image image = super.getImage(cellDimension, invert);
        if (imageAvailableInHighRes.get() || (image == StadtbilderUtils.PLACEHOLDER_IMAGE)) {
            setPreview(false);
            return image;
        } else if (image == null) {
            final BufferedImage imageToShow = new BufferedImage(
                    cellDimension,
                    cellDimension,
                    BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = (Graphics2D)imageToShow.getGraphics();
            final Image scaledErrorImage = StadtbilderUtils.scaleImage(
                    StadtbilderUtils.ERROR_IMAGE,
                    cellDimension,
                    invert);
            // heuristic to center the error image
            g.drawImage(scaledErrorImage, 0, cellDimension / 12, null);
            setPreview(false);
            return imageToShow;
        } else {
            setPreview(true);
            return image;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (11 * hash) + ((this.stadtbild != null) ? this.stadtbild.hashCode() : 0);
        hash = (11 * hash) + ((this.locationOfStadtbild != null) ? this.locationOfStadtbild.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sb_SingleStadtbildGridObject other = (Sb_SingleStadtbildGridObject)obj;
        if ((this.stadtbild != other.stadtbild)
                    && ((this.stadtbild == null) || !this.stadtbild.equals(other.stadtbild))) {
            return false;
        }
        if ((this.locationOfStadtbild != other.locationOfStadtbild)
                    && ((this.locationOfStadtbild == null)
                        || !this.locationOfStadtbild.equals(other.locationOfStadtbild))) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean isPreviewAllowed() {
        final CidsBean stadtbildserie = locationOfStadtbild.getCidsBean();
        return Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(
                    stadtbildserie,
                    getConnectionContext())
                    .isPreviewAllowed();
    }

    @Override
    protected CidsBean getStadtbildserie() {
        return locationOfStadtbild.getStadtbildserie();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Sb_stadtbildserieGridObject getLocationOfStadtbild() {
        return locationOfStadtbild;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getStadtbild() {
        return stadtbild;
    }
}

//    /**
//     * DOCUMENT ME!
//     *
//     * @version  $Revision$, $Date$
//     */
//    private class SingleStadtbildGridRenderer extends javax.swing.JLabel implements GridCellRenderer {
//
//        //~ Instance fields ----------------------------------------------------
//
//        private Image image;
//
//        //~ Methods ------------------------------------------------------------
//
//        @Override
//        public Component getGridCellRendererComponent(final JGrid grid,
//                final Object value,
//                final int index,
//                final boolean isSelected,
//                final boolean cellHasFocus) {
//            image = null;
//            if (value instanceof SingleStadtbildGridObject) {
//                image = ((SingleStadtbildGridObject)value).getImage(grid.getFixedCellDimension(), false);
//            }
//
//            if (image != null) {
//                this.setIcon(new ImageIcon(image));
//            } else {
//                final Image scaledErrorImage = Sb_stadtbildUtils.scaleImage(
//                        Sb_stadtbildUtils.ERROR_IMAGE,
//                        grid.getFixedCellDimension(),
//                        false);
//                this.setIcon(new ImageIcon(scaledErrorImage));
//            }
//
//            return this;
//        }
