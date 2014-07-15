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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.utils.Sb_stadtbildUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.commons.concurrency.CismetConcurrency;
import de.cismet.commons.concurrency.CismetExecutors;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor.adjustScale;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieGridObject implements CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final ExecutorService unboundUEHThreadPoolExecutor;

    static {
        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup parent = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        final ThreadGroup threadGroup = new ThreadGroup(parent, "stadtbilderAggregationRendererDownload");
        final ThreadFactory factory = new CismetConcurrency.CismetThreadFactory(
                threadGroup,
                "stadtbilderAggregationRendererDownload",
                null);

        unboundUEHThreadPoolExecutor = new CismetExecutors.UEHThreadPoolExecutor(
                10,
                10,
                180, // shrink in size after 3 minutes again
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                factory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieGridObject.class);

    //~ Instance fields --------------------------------------------------------

    private CidsBean stadtbildserie;

    private float fraction;

    private int index;
    private int amountImages = 1;

    private boolean marker;

    private SwingWorker<Image, Void> worker;
    private final DefaultListModel model;
    private ImageForBildnummer imageForBildnummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieGridObject object.
     *
     * @param  model  DOCUMENT ME!
     */
    public Sb_stadtbildserieGridObject(final DefaultListModel model) {
        this.model = model;
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
     * @param   componentToShowImage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImage(final JComponent componentToShowImage) {
        final String bildnummer;
        final List<CidsBean> images = stadtbildserie.getBeanCollectionProperty("stadtbilder_arr");
        if (!images.isEmpty() && (index < images.size())) {
            bildnummer = (String)images.get(index).getProperty("bildnummer");
        } else {
            bildnummer = (String)stadtbildserie.getProperty("vorschaubild.bildnummer");
        }

        LOG.fatal(bildnummer);
        if ((imageForBildnummer == null) || !imageForBildnummer.bildnummer.equals(bildnummer)) {
            LOG.fatal("fetch image");
            final Future<Image> futureImage = unboundUEHThreadPoolExecutor.submit(new Callable<Image>() {

                        @Override
                        public Image call() throws Exception {
                            try {
                                final BufferedImage image = Sb_stadtbildUtils.downloadImageForBildnummer(bildnummer);
                                return (Image)adjustScale(image, componentToShowImage, 0, 0);
                            } catch (Exception ex) {
                                return (Image)adjustScale(Sb_stadtbildUtils.ERROR_IMAGE, componentToShowImage, 0, 0);
                            }
                        }
                    });

            if (futureImage.isDone()) {
                LOG.fatal("future is done");
                try {
                    return futureImage.get();
                } catch (InterruptedException ex) {
                    return Sb_stadtbildUtils.ERROR_IMAGE;
                } catch (ExecutionException ex) {
                    return Sb_stadtbildUtils.ERROR_IMAGE;
                }
            } else {
                retrieveFutureImage(futureImage, bildnummer);
                LOG.fatal("return Placeholder");
                return Sb_stadtbildUtils.PLACEHOLDER_IMAGE;
            }
        } else {
            LOG.fatal("already got image");
            return imageForBildnummer.image;
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
                        LOG.fatal("got the image");
                        imageForBildnummer = new ImageForBildnummer(image, bildnummer);
                        // adds itself to the model at the same position. to update the model
                        model.setElementAt(
                            Sb_stadtbildserieGridObject.this,
                            model.indexOf(Sb_stadtbildserieGridObject.this));
                    } catch (InterruptedException ex) {
                        // do nothing
                    } catch (ExecutionException ex) {
                    } catch (CancellationException ex) {
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ImageForBildnummer {

        //~ Instance fields ----------------------------------------------------

        Image image;
        String bildnummer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageForBildnummer object.
         *
         * @param  image       DOCUMENT ME!
         * @param  bildnummer  DOCUMENT ME!
         */
        public ImageForBildnummer(final Image image, final String bildnummer) {
            this.image = image;
            this.bildnummer = bildnummer;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int hashCode() {
            int hash = 5;
            hash = (17 * hash) + ((this.bildnummer != null) ? this.bildnummer.hashCode() : 0);
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
            final ImageForBildnummer other = (ImageForBildnummer)obj;
            if ((this.bildnummer == null) ? (other.bildnummer != null) : (!this.bildnummer.equals(other.bildnummer))) {
                return false;
            }
            return true;
        }
    }
}
