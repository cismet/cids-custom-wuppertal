/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.awt.Image;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.clientutils.StadtbilderUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class Sb_AbstractPictureGridObject implements ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private SwingWorker<Image, Void> worker;
    /** The image which was shown the last time. This is a small cache for Sb_AbstractPictureGridObject; */
    private LastShownImage lastShownImage;
    private boolean preview;

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_AbstractPictureGridObject object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public Sb_AbstractPictureGridObject(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract StadtbilderUtils.StadtbildInfo getStadtbildInfo();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract int getDownloadPrority();

    /**
     * Returns a boolean, if true the preview images can be shown.
     *
     * @return  DOCUMENT ME!
     */
    protected abstract boolean isPreviewAllowed();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract CidsBean getStadtbildserie();

    /**
     * DOCUMENT ME!
     */
    protected abstract void notifyModel();

    /**
     * Get a scaled image for a Stadtbild. The exact stadtbild is provided by the method <code>getBildnummer()</code>.
     * If the image has to be loaded, a Placeholder image will be returned. If something went wrong, e.g. while loading
     * the image, null will be returned.
     *
     * @param   cellDimension  DOCUMENT ME!
     * @param   invert         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImage(final int cellDimension, final boolean invert) {
        final StadtbilderUtils.StadtbildInfo stadtbildInfo = getStadtbildInfo();

        if ((lastShownImage != null) && lastShownImage.stadtbildInfo.equals(stadtbildInfo)) {
            final Image imageToReturn = lastShownImage.image;
            if (lastShownImage.image == null) {
                return null;
            } else {
                return StadtbilderUtils.scaleImage(imageToReturn, cellDimension, invert);
            }
        }

        final int priority = getDownloadPrority();

        final Object mightBeAnImage = StadtbilderUtils.fetchImageForBildnummer(
                getStadtbildserie(),
                stadtbildInfo,
                priority,
                getConnectionContext());
        if (mightBeAnImage instanceof Image) {
            lastShownImage = new LastShownImage(stadtbildInfo, (Image)mightBeAnImage);
            final Image toReturn = (Image)mightBeAnImage;
            return StadtbilderUtils.scaleImage(toReturn, cellDimension, invert);
        } else if (mightBeAnImage instanceof Future) {
            retrieveFutureImage((Future<Image>)mightBeAnImage, stadtbildInfo);
            return StadtbilderUtils.scaleImage(StadtbilderUtils.PLACEHOLDER_IMAGE, cellDimension, invert);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  preview  DOCUMENT ME!
     */
    protected void setPreview(final boolean preview) {
        this.preview = preview;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isPreview() {
        return preview;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  futureImage    DOCUMENT ME!
     * @param  stadtbildInfo  DOCUMENT ME!
     */
    public void retrieveFutureImage(final Future<Image> futureImage,
            final StadtbilderUtils.StadtbildInfo stadtbildInfo) {
        if (worker != null) {
            worker.cancel(true);
        }

        final SwingWorker<Image, Void> worker = new SwingWorker<Image, Void>() {

                @Override
                protected Image doInBackground() throws Exception {
                    return futureImage.get();
                }

                @Override
                protected void done() {
                    try {
                        final Image image = get();
                        if (image != null) {
                            lastShownImage = new LastShownImage(stadtbildInfo, image);
                        } else {
                            lastShownImage = new LastShownImage(stadtbildInfo, null);
                        }
                    } catch (InterruptedException ex) {
                        lastShownImage = new LastShownImage(stadtbildInfo, null);
                    } catch (ExecutionException ex) {
                        lastShownImage = new LastShownImage(stadtbildInfo, null);
                    } catch (CancellationException ex) {
                    } finally {
                        notifyModel();
                    }
                }
            };
        this.worker = worker;
        worker.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void clearLastShownImage() {
        if (lastShownImage != null) {
            lastShownImage.stadtbildInfo = null;
            lastShownImage.image = null;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class LastShownImage {

        //~ Instance fields ----------------------------------------------------

        StadtbilderUtils.StadtbildInfo stadtbildInfo;
        Image image;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LastShownImage object.
         *
         * @param  stadtbildInfo  bildnummer DOCUMENT ME!
         * @param  image          DOCUMENT ME!
         */
        public LastShownImage(final StadtbilderUtils.StadtbildInfo stadtbildInfo, final Image image) {
            this.stadtbildInfo = stadtbildInfo;
            this.image = image;
        }
    }
}
