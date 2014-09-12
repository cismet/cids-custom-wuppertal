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
     * Returns a boolean, if true the preview images can be shown.
     *
     * @return  DOCUMENT ME!
     */
    protected abstract boolean isPreviewAllowed();

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
        if (!isPreviewAllowed()) {
            return null;
        }

        final String bildnummer = getBildnummer();

        if ((lastShowImage != null) && lastShowImage.bildnummer.equals(bildnummer)) {
            final Image imageToReturn = lastShowImage.image;
            if (lastShowImage.image == null) {
                return null;
            } else {
                return Sb_stadtbildUtils.scaleImage(imageToReturn, cellDimension, invert);
            }
        }

        final int priority = getDownloadPrority();

        final Object mightBeAnImage = Sb_stadtbildUtils.fetchImageForBildnummer(
                bildnummer,
                priority);
        if (mightBeAnImage instanceof Image) {
            lastShowImage = new LastShownImage(bildnummer, (Image)mightBeAnImage);
            final Image toReturn = (Image)mightBeAnImage;
            return Sb_stadtbildUtils.scaleImage(toReturn, cellDimension, invert);
        } else {
            // mightBeAnImage must be a Future<Image>
            retrieveFutureImage((Future<Image>)mightBeAnImage, bildnummer);
            return Sb_stadtbildUtils.scaleImage(Sb_stadtbildUtils.PLACEHOLDER_IMAGE, cellDimension, invert);
        }
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
                        if (image != null) {
                            lastShowImage = new LastShownImage(bildnummer, image);
                        } else {
                            lastShowImage = new LastShownImage(bildnummer, null);
                        }
                    } catch (InterruptedException ex) {
                        lastShowImage = new LastShownImage(bildnummer, null);
                    } catch (ExecutionException ex) {
                        lastShowImage = new LastShownImage(bildnummer, null);
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
