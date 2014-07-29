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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieGridObject implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridObject.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean stadtbildserie;

    private float fraction;

    private int index;
    private int amountImages = 1;

    private boolean marker;

    private SwingWorker<Image, Void> worker;
    private DefaultListModel gridModel;
    private LastShownImage lastShowImage;

    private final HashSet<CidsBean> selectedBildnummernOfSerie = new HashSet<CidsBean>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieGridObject object.
     *
     * @param  model  DOCUMENT ME!
     */
    public Sb_stadtbildserieGridObject(final DefaultListModel model) {
        this.gridModel = model;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public CidsBean getCidsBean() {
        return stadtbildserie;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        stadtbildserie = cidsBean;
        final List images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty()) {
            amountImages = images.size();
        } else {
            amountImages = 1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cellDimension  componentToShowImage DOCUMENT ME!
     * @param   invert         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImage(final int cellDimension, final boolean invert) {
        final String bildnummer;
        final List<CidsBean> images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty() && (index < images.size())) {
            bildnummer = (String)images.get(index).getProperty("bildnummer");
        } else {
            bildnummer = (String)stadtbildserie.getProperty("vorschaubild.bildnummer");
        }

        if ((lastShowImage != null) && lastShowImage.bildnummer.equals(bildnummer)) {
            final Image imageToReturn = lastShowImage.image;
            if (lastShowImage.image == Sb_stadtbildUtils.ERROR_IMAGE) {
                lastShowImage.image = null;
            }
            return scaleImage(imageToReturn, cellDimension, invert);
        }

        int priority;
        if (marker) {
            priority = Sb_stadtbildUtils.HIGH_PRIORITY;
        } else {
            priority = Sb_stadtbildUtils.LOW_PRIORITY;
        }

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
     * Returns the statdbild under the marker, if such an image does not exist the vorschaubild will be returned.
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getStadtbildUnderMarker() {
        CidsBean stadtbild;
        final List<CidsBean> images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty() && (index < images.size())) {
            stadtbild = images.get(index);
        } else {
            stadtbild = (CidsBean)stadtbildserie.getProperty("vorschaubild");
        }
        return stadtbild;
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
                        lastShowImage = new LastShownImage(bildnummer, Sb_stadtbildUtils.ERROR_IMAGE);
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
     *
     * @param  marker  DOCUMENT ME!
     */
    public void setMarker(final boolean marker) {
        if (marker) {
            Sb_stadtbildUtils.cacheImagesForStadtbilder(stadtbildserie.getBeanCollectionProperty("stadtbilder_arr"));
        }
        this.marker = marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMarker() {
        return marker;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getIndex() {
        return index;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public float getFraction() {
        return fraction;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fraction  DOCUMENT ME!
     */
    public void setFraction(final float fraction) {
        this.fraction = Math.max(0.0f, Math.min(1.0f, fraction));
        this.index = (int)(this.fraction * (float)(amountImages));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmountImages() {
        return amountImages;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmountSelectedImages() {
        return selectedBildnummernOfSerie.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<CidsBean> getSelectedBildnummernOfSerie() {
        return selectedBildnummernOfSerie;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void addSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        selectedBildnummernOfSerie.add(bildnummer);
        notifyModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void removeSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        selectedBildnummernOfSerie.remove(bildnummer);
        notifyModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isStadtbildSelected(final CidsBean bildnummer) {
        return selectedBildnummernOfSerie.contains(bildnummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bildnummer  DOCUMENT ME!
     */
    public void addOrRemoveSelectedBildnummerOfSerie(final CidsBean bildnummer) {
        if (selectedBildnummernOfSerie.contains(bildnummer)) {
            removeSelectedBildnummerOfSerie(bildnummer);
        } else {
            addSelectedBildnummerOfSerie(bildnummer);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void notifyModel() {
        // adds itself to the gridModel at the same position. to update the gridModel
        gridModel.setElementAt(
            this,
            gridModel.indexOf(this));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public DefaultListModel getModel() {
        return gridModel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  model  DOCUMENT ME!
     */
    public void setModel(final DefaultListModel model) {
        this.gridModel = model;
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
