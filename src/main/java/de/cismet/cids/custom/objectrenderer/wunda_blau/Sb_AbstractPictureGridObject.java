/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public abstract class Sb_AbstractPictureGridObject {

    //~ Instance fields --------------------------------------------------------

    private SwingWorker<Image, Void> worker;
    private LastShownImage lastShowImage;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getBildnummer();
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract int getDownloadPrority();
    /**
     * DOCUMENT ME!
     */
    protected abstract void notifyModel();

    /**
     * DOCUMENT ME!
     *
     * @param   cellDimension  DOCUMENT ME!
     * @param   invert         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImage(final int cellDimension, final boolean invert) {
        final String bildnummer = getBildnummer();

        if ((lastShowImage != null) && lastShowImage.bildnummer.equals(bildnummer)) {
            final Image imageToReturn = lastShowImage.image;
            if (lastShowImage.image == Sb_stadtbildUtils.ERROR_IMAGE) {
                lastShowImage.image = null;
            }
            return scaleImage(imageToReturn, cellDimension, invert);
        }

        final int priority = getDownloadPrority();

        final Object mightBeAnImage = Sb_stadtbildUtils.fetchImageForBildnummer(
                bildnummer,
                priority);
        if (mightBeAnImage instanceof Image) {
            lastShowImage = new LastShownImage(bildnummer, (Image)mightBeAnImage);
            final Image toReturn = (Image)mightBeAnImage;
            return scaleImage(toReturn, cellDimension, invert);
        } else {
            // mightBeAnImage must be a Future<Image>
            retrieveFutureImage((Future<Image>)mightBeAnImage, bildnummer);
            return scaleImage(Sb_stadtbildUtils.PLACEHOLDER_IMAGE, cellDimension, invert);
        }
    }

    /**
     * Scales the image such that it fits in an element of the jGrid. If showWholePicture is true, then the image will
     * be scaled such that it will be shown completely in the element, thus a border may be there. If showWholePicture
     * is false the element of the grid will be filled up completely with the image, but the image may be cut off. This
     * is due to that the ratio of the image is preserved.
     *
     * @param   toScale           DOCUMENT ME!
     * @param   dimension         DOCUMENT ME!
     * @param   showWholePicture  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Image scaleImage(final Image toScale, final int dimension, final boolean showWholePicture) {
        Image toReturn = toScale;
        if (toReturn instanceof BufferedImage) {
            if ((toScale.getHeight(null) > toScale.getWidth(null)) ^ showWholePicture) {
                toReturn = ((BufferedImage)toReturn).getScaledInstance(dimension, -1, Image.SCALE_SMOOTH);
            } else {
                toReturn = ((BufferedImage)toReturn).getScaledInstance(-1, dimension, Image.SCALE_SMOOTH);
            }
        }
        return toReturn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  futureImage  DOCUMENT ME!
     * @param  bildnummer   DOCUMENT ME!
     */
    public void retrieveFutureImage(final Future<Image> futureImage, final String bildnummer) {
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
                        lastShowImage = new LastShownImage(bildnummer, image);
                    } catch (InterruptedException ex) {
                        lastShowImage = new LastShownImage(bildnummer, Sb_stadtbildUtils.ERROR_IMAGE);
                    } catch (ExecutionException ex) {
                        lastShowImage = new LastShownImage(bildnummer, Sb_stadtbildUtils.ERROR_IMAGE);
                    } catch (CancellationException ex) {
                    } finally {
                        notifyModel();
                    }
                }
            };
        this.worker = worker;
        worker.execute();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class LastShownImage {

        //~ Instance fields ----------------------------------------------------

        String bildnummer;
        Image image;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LastShownImage object.
         *
         * @param  bildnummer  DOCUMENT ME!
         * @param  image       DOCUMENT ME!
         */
        public LastShownImage(final String bildnummer, final Image image) {
            this.bildnummer = bildnummer;
            this.image = image;
        }
    }
}
