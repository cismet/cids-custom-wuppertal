/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.commons.lang.StringUtils;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.lang.ref.SoftReference;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.commons.concurrency.CismetConcurrency;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildUtils.class);

    private static final String[] IMAGE_FILE_FORMATS = { "jpg", "tiff" };

    public static BufferedImage ERROR_IMAGE;
    public static BufferedImage PLACEHOLDER_IMAGE;

    private static final CidsBean WUPPERTAL;
    private static final CidsBean R102;

    private static final int CACHE_SIZE = 100;

    /** A cache whose key is a bildnummer and the value is the corresponding image. */
    private static final ConcurrentLRUCache<String, SoftReference<BufferedImage>> IMAGE_CACHE =
        new ConcurrentLRUCache<String, SoftReference<BufferedImage>>(CACHE_SIZE);
    /** A map with bildnummern (image numbers) which could not be loaded. */
    private static final ConcurrentHashMap<String, String> FAILED_IMAGES = new ConcurrentHashMap<String, String>();

    private static final PriorityExecutor unboundUEHThreadPoolExecutor;
    public static final int HIGH_PRIORITY = 1;
    public static final int NORMAL_PRIORITY = 0;
    public static final int LOW_PRIORITY = -1;

    static {
        WUPPERTAL = getOrtWupertal();
        R102 = getLagerR102();

        try {
            ERROR_IMAGE = ImageIO.read(Sb_stadtbildUtils.class.getResourceAsStream(
                        "/de/cismet/cids/custom/objecteditors/wunda_blau/no_image.png"));
        } catch (IOException ex) {
            LOG.error("Could not fetch ERROR_IMAGE", ex);
        }

        try {
            PLACEHOLDER_IMAGE = ImageIO.read(Sb_stadtbildUtils.class.getResourceAsStream(
                        "/de/cismet/cids/custom/objecteditors/wunda_blau/wait_image.png"));
        } catch (IOException ex) {
            LOG.error("Could not fetch ERROR_IMAGE", ex);
        }

        final SecurityManager s = System.getSecurityManager();
        final ThreadGroup parent = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

        final ThreadGroup threadGroup = new ThreadGroup(parent, "stadtbilderAggregationRendererDownload");
        final ThreadFactory factory = new CismetConcurrency.CismetThreadFactory(
                threadGroup,
                "stadtbilderAggregationRendererDownload",
                null);

        final BlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();

        unboundUEHThreadPoolExecutor = new PriorityExecutor(
                10,
                10,
                180, // shrink in size after 3 minutes again
                TimeUnit.SECONDS,
                queue,
                factory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Get the CidsBean of the Sb_Ort with the name 'Wuppertal'. Might be null.
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getWUPPERTAL() {
        return WUPPERTAL;
    }

    /**
     * DOCUMENT ME!
     */
    public static void simulateGC() {
        final Set<String> keys = IMAGE_CACHE.map.keySet();
        for (final String key : keys) {
            IMAGE_CACHE.get(key).clear();
        }
    }

    /**
     * Get the CidsBean of the Sb_Lager with the name 'R102'. Might be null.
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getR102() {
        return R102;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getOrtWupertal() {
        try {
            final MetaClass ortClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "sb_ort");
            if (ortClass != null) {
                final StringBuffer wuppertalQuery = new StringBuffer("select ").append(ortClass.getId())
                            .append(", ")
                            .append(ortClass.getPrimaryKey())
                            .append(" from ")
                            .append(ortClass.getTableName())
                            .append(" where name ilike 'Wuppertal'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: wuppertalQuery:" + wuppertalQuery.toString());
                }
                final MetaObject[] wuppertal;
                try {
                    wuppertal = SessionManager.getProxy().getMetaObjectByQuery(wuppertalQuery.toString(), 0);
                    if (wuppertal.length > 0) {
                        return wuppertal[0].getBean();
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("The Location Wuppertal could not be loaded.", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getLagerR102() {
        try {
            final MetaClass lagerClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "sb_lager");
            if (lagerClass != null) {
                final StringBuffer r102Query = new StringBuffer("select ").append(lagerClass.getId())
                            .append(", ")
                            .append(lagerClass.getPrimaryKey())
                            .append(" from ")
                            .append(lagerClass.getTableName())
                            .append(" where name = 'R102'");
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: r102Query:" + r102Query.toString());
                }
                final MetaObject[] r102;
                try {
                    r102 = SessionManager.getProxy().getMetaObjectByQuery(r102Query.toString(), 0);
                    if (r102.length > 0) {
                        return r102[0].getBean();
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error("The storage location R102 could not be loaded.", ex);
        }
        return null;
    }

    /**
     * Fills the model of combobox with the LightweightMetaObjects for all elements of a certain cids-class. The method
     * creates a temporary FastBindableReferenceCombo and set its MetaClass to the class given as argument. Then the
     * model of the FastBindableReferenceCombo is set as the model of the combobox. Finally the combobox is decorated
     * with the AutoCompleteDecorator.
     *
     * @param  combobox   The model of that combobox will be replaced
     * @param  className  the cids class name. The elements of this class will be fetched.
     */
    public static void setModelForComboBoxesAndDecorateIt(final JComboBox combobox, final String className) {
        final MetaClass metaClass = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", className);
        if (metaClass != null) {
            final DefaultComboBoxModel comboBoxModel;
            try {
                final FastBindableReferenceCombo tempFastBindableCombo = new FastBindableReferenceCombo();
                tempFastBindableCombo.setSorted(true);
                tempFastBindableCombo.setNullable(true);
                tempFastBindableCombo.setMetaClass(metaClass);
                comboBoxModel = (DefaultComboBoxModel)tempFastBindableCombo.getModel();
                combobox.setModel(comboBoxModel);
            } catch (Exception ex) {
                LOG.error(ex, ex);
            }
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(combobox);
        } else {
            LOG.warn("MetaClass is null. Probably the permissions for the class " + className + " are missing.");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageNumber  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static URL getURLOfLowResPicture(final String imageNumber) {
        final char firstCharacter = imageNumber.charAt(0);
        final String locationOfPreviewImage = "VB/" + firstCharacter + "/VB_" + imageNumber;
        for (final String fileEnding : IMAGE_FILE_FORMATS) {
            try {
                final String urlName = "http://s102x003/archivar/" + locationOfPreviewImage + "." + fileEnding;
                final URL url = new URL(urlName);
                final boolean accessible = WebAccessManager.getInstance().checkIfURLaccessible(url);
                if (accessible) {
                    return url;
                }
            } catch (MalformedURLException ex) {
                LOG.warn(ex, ex);
            }
        }
        return null;
    }

    /**
     * Gets a Stadtbild-imageNumber as argument and checks if a high-res image for that number exists. This check is
     * done by sending a HEAD-request to different URLS, whose difference is the file ending. The file endings are taken
     * from IMAGE_FILE_FORMATS.
     *
     * @param   imageNumber  an imageNumber for a Stadtbild
     *
     * @return  if a high-res image exists, then its file ending. Otherwise null.
     */
    public static String getFormatOfHighResPicture(final String imageNumber) {
        final char firstCharacter = imageNumber.charAt(0);
        final String locationOfPreviewImage = "SB/" + firstCharacter + "/SB_" + imageNumber;
        for (final String fileEnding : IMAGE_FILE_FORMATS) {
            try {
                final String urlName = "http://s102x003/archivar/" + locationOfPreviewImage + "." + fileEnding;
                final URL url = new URL(urlName);
                final boolean accessible = WebAccessManager.getInstance().checkIfURLaccessible(url);
                if (accessible) {
                    return fileEnding;
                }
            } catch (MalformedURLException ex) {
                LOG.warn(ex, ex);
            }
        }
        return null;
    }

    /**
     * Fetches an image of a bildnummer. Receives an image number as argument and checks if its image is already in the
     * cache. If this is the case the cached image is returned. If not the image corresponding to the number is
     * downloaded and returned.
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  java.lang.Exception
     */
    public static BufferedImage downloadImageForBildnummer(final String bildnummer) throws Exception {
        if (isBildnummerInCacheOrFailed(bildnummer)) {
            final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(bildnummer);
            if (cachedImageRef != null) {
                return cachedImageRef.get();
            } else {
                return null;
            }
        }

        final URL urlLowResImage = Sb_stadtbildUtils.getURLOfLowResPicture(bildnummer);
        if (urlLowResImage != null) {
            InputStream is = null;
            try {
                is = WebAccessManager.getInstance().doRequest(urlLowResImage);
                final BufferedImage img = ImageIO.read(is);
                if (img != null) {
                    IMAGE_CACHE.put(bildnummer, new SoftReference<BufferedImage>(img));
                } else {
                    FAILED_IMAGES.put(
                        bildnummer,
                        "The image for "
                                + bildnummer
                                + " returned from the server was apparently null.");
                }
                return img;
            } catch (Exception ex) {
                FAILED_IMAGES.put(bildnummer, ex.getMessage());
                throw ex;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        LOG.warn("Error during closing InputStream.", ex);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Fetches an image of a bildnummer. Receives an image number as argument and checks if its image is already in the
     * cache. If this is the case the cached image is returned. If not the image corresponding to the number is
     * downloaded. In that case a Future&lt;Image&gt; is returned.
     *
     * @param   statdbildserie  DOCUMENT ME!
     * @param   bildnummer      DOCUMENT ME!
     * @param   priority        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object fetchImageForBildnummer(final CidsBean statdbildserie,
            final String bildnummer,
            final int priority) {
        if (isBildnummerInCacheOrFailed(bildnummer)) {
            final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(bildnummer);
            if (cachedImageRef != null) {
                final Object ret = cachedImageRef.get();
                if (ret != null) {
                    return ret;
                }
            }
        }
        final Future futureImage = unboundUEHThreadPoolExecutor.submit(new FetchImagePriorityCallable(
                    statdbildserie,
                    bildnummer),
                priority);
        return futureImage;
    }

    /**
     * Checks if the bildnummer has an entry in the image cache, or if the bildnummer could not be loaded.
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isBildnummerInCacheOrFailed(final String bildnummer) {
        return (IMAGE_CACHE.containsKey(bildnummer) && (IMAGE_CACHE.get(bildnummer).get() != null))
                    || FAILED_IMAGES.containsKey(bildnummer);
    }

    /**
     * Checks if the bildnummer could not be loaded.
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isBildnummerInFailedSet(final String bildnummer) {
        return FAILED_IMAGES.containsKey(bildnummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bildnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getErrorMessageForFailedImage(final String bildnummer) {
        String message = FAILED_IMAGES.get(bildnummer);
        if (StringUtils.isBlank(message)) {
            message = "No message for image " + bildnummer;
        }
        return message;
    }

    /**
     * Checks if the Stadtbilder are in the cache. If not they will be downloaded. Returns immediately.
     *
     * @param  stadtbildserie  DOCUMENT ME!
     * @param  stadtbilder     DOCUMENT ME!
     */
    public static void cacheImagesForStadtbilder(final CidsBean stadtbildserie, final List<CidsBean> stadtbilder) {
        for (int i = 0; (i < CACHE_SIZE) && (i < stadtbilder.size()); i++) {
            final String bildnummer = (String)stadtbilder.get(i).getProperty("bildnummer");
            try {
                fetchImageForBildnummer(stadtbildserie, bildnummer, NORMAL_PRIORITY);
            } catch (Exception ex) {
                LOG.error("Problem while loading image " + bildnummer);
            }
        }
    }

    /**
     * Removes a bildnummer from the image cache, and also its entry in the failed set.
     *
     * @param  bildnummer  cidsBean DOCUMENT ME!
     */
    public static void removeBildnummerFromImageCacheAndFailedSet(final String bildnummer) {
        IMAGE_CACHE.remove(bildnummer);
        FAILED_IMAGES.remove(bildnummer);
    }

    /**
     * Removes a bildnummer from the failed set.
     *
     * @param  bildnummer  cidsBean DOCUMENT ME!
     */
    public static void removeBildnummerFromFailedSet(final String bildnummer) {
        FAILED_IMAGES.remove(bildnummer);
    }

    /**
     * Scales the image such that it fits in an element of the jGrid. If showWholePicture is true, then the image will
     * be scaled such that it will be shown completely in the element, thus a border may be there. If showWholePicture
     * is false the element of the grid will be filled up completely with the image, but the image may be cut off. This
     * is due to that the ratio of the image is preserved.
     *
     * <p>Tries to use GraphicsUtilities.createThumbnail(), as it works only if the image is smaller.</p>
     *
     * @param   toScale           DOCUMENT ME!
     * @param   dimension         DOCUMENT ME!
     * @param   showWholePicture  DOCUMENT ME!
     *
     * @return  a scaled image
     */
    public static Image scaleImage(final Image toScale, final int dimension, final boolean showWholePicture) {
        if (toScale instanceof BufferedImage) {
            if ((toScale.getWidth(null) > dimension) && (toScale.getHeight(null) > dimension)) {
                if (showWholePicture) {
                    return GraphicsUtilities.createThumbnail((BufferedImage)toScale, dimension);
                } else {
                    final double reduce = Math.min(toScale.getWidth(null), toScale.getHeight(null)) * 1d / dimension;
                    final int newWidth = (int)Math.round(toScale.getWidth(null) / reduce);
                    final int newHeight = (int)Math.round(toScale.getHeight(null) / reduce);
                    final BufferedImage thumbnail = GraphicsUtilities.createThumbnail((BufferedImage)toScale,
                            newWidth,
                            newHeight);
                    try {
                        return thumbnail.getSubimage((thumbnail.getWidth() - dimension) / 2,
                                (thumbnail.getHeight()
                                            - dimension)
                                        / 2,
                                dimension,
                                dimension);
                    } catch (Exception ex) {
                        LOG.error("Something went wrong while cropping the image.", ex);
                        return GraphicsUtilities.createThumbnail((BufferedImage)toScale,
                                dimension,
                                dimension);
                    }
                }
            } else if ((toScale.getWidth(null) < dimension) || (toScale.getHeight(null) < dimension)) {
                return oldScaleImage(toScale, dimension, showWholePicture);
            } else {
                return toScale;
            }
        } else {
            return toScale;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   toScale           DOCUMENT ME!
     * @param   dimension         DOCUMENT ME!
     * @param   showWholePicture  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Image oldScaleImage(final Image toScale, final int dimension, final boolean showWholePicture) {
        Image toReturn = toScale;
        if (toReturn instanceof BufferedImage) {
            if ((toScale.getHeight(null) > toScale.getWidth(null)) ^ showWholePicture) {
                toReturn = ((BufferedImage)toReturn).getScaledInstance(dimension, -1, Image.SCALE_SMOOTH);
            } else {
                toReturn = ((BufferedImage)toReturn).getScaledInstance(-1, dimension, Image.SCALE_SMOOTH);
            }
        }
        return GraphicsUtilities.convertToBufferedImage(toReturn);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class ComparableFutureTask<T> extends FutureTask<T> implements Comparable<ComparableFutureTask<T>> {

        //~ Instance fields ----------------------------------------------------

        volatile int priority = 0;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ComparableFutureTask object.
         *
         * @param  callable  DOCUMENT ME!
         * @param  priority  DOCUMENT ME!
         */
        public ComparableFutureTask(final Callable<T> callable, final int priority) {
            super(callable);
            this.priority = priority;
        }

        /**
         * Creates a new ComparableFutureTask object.
         *
         * @param  runnable  DOCUMENT ME!
         * @param  result    DOCUMENT ME!
         * @param  priority  DOCUMENT ME!
         */
        public ComparableFutureTask(final Runnable runnable, final T result, final int priority) {
            super(runnable, result);
            this.priority = priority;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int compareTo(final ComparableFutureTask<T> o) {
            return Integer.valueOf(priority).compareTo(o.priority);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class FetchImagePriorityCallable implements Callable<Image> {

        //~ Instance fields ----------------------------------------------------

        CidsBean stadtbildserie;
        String bildnummer;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PriorityCallable object.
         *
         * @param  stadtbildserie  DOCUMENT ME!
         * @param  bildnummer      DOCUMENT ME!
         */
        public FetchImagePriorityCallable(final CidsBean stadtbildserie, final String bildnummer) {
            this.stadtbildserie = stadtbildserie;
            this.bildnummer = bildnummer;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Image call() throws Exception {
            if (!Sb_RestrictionLevelUtils.determineRestrictionLevelForStadtbildserie(stadtbildserie)
                        .isPreviewAllowed()) {
                FAILED_IMAGES.put(bildnummer, "The user is not allowed to see the image "
                            + bildnummer);
                return null;
            }

            // the image might have already been fetched by a previous thread
            if (isBildnummerInCacheOrFailed(bildnummer)) {
                final SoftReference<BufferedImage> cachedImageRef = IMAGE_CACHE.get(bildnummer);
                if (cachedImageRef != null) {
                    return cachedImageRef.get();
                } else {
                    return null;
                }
            }

            final URL urlLowResImage = Sb_stadtbildUtils.getURLOfLowResPicture(bildnummer);
            if (urlLowResImage != null) {
                InputStream is = null;
                try {
                    is = WebAccessManager.getInstance().doRequest(urlLowResImage);

                    final BufferedImage img = ImageIO.read(is);

                    if (img != null) {
                        IMAGE_CACHE.put(bildnummer, new SoftReference<BufferedImage>(img));
                    } else {
                        FAILED_IMAGES.put(
                            bildnummer,
                            "The image "
                                    + bildnummer
                                    + " returned from the server was apparently null.");
                    }
                    return img;
                } catch (Exception ex) {
                    FAILED_IMAGES.put(bildnummer, ex.getMessage());
                    throw ex;
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ex) {
                            LOG.warn("Error during closing InputStream.", ex);
                        }
                    }
                }
            }
            FAILED_IMAGES.put(bildnummer, "No url exists to retrieve the image " + bildnummer + ".");
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class PriorityExecutor extends ThreadPoolExecutor {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PriorityExecutor object.
         *
         * @param  corePoolSize     DOCUMENT ME!
         * @param  maximumPoolSize  DOCUMENT ME!
         * @param  keepAliveTime    DOCUMENT ME!
         * @param  unit             DOCUMENT ME!
         * @param  workQueue        DOCUMENT ME!
         * @param  threadFactory    DOCUMENT ME!
         */
        public PriorityExecutor(final int corePoolSize,
                final int maximumPoolSize,
                final long keepAliveTime,
                final TimeUnit unit,
                final BlockingQueue<Runnable> workQueue,
                final ThreadFactory threadFactory) {
            this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new AbortPolicy());
        }

        /**
         * @see  ThreadPoolExecutor#ThreadPoolExecutor(int, int, long, java.util.concurrent.TimeUnit,
         *       java.util.concurrent.BlockingQueue, java.util.concurrent.ThreadFactory,
         *       java.util.concurrent.RejectedExecutionHandler)
         */
        public PriorityExecutor(final int corePoolSize,
                final int maximumPoolSize,
                final long keepAliveTime,
                final TimeUnit unit,
                final BlockingQueue<Runnable> workQueue,
                final ThreadFactory threadFactory,
                final RejectedExecutionHandler rejectHandler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, rejectHandler);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Submit with New comparable task.
         *
         * @param   task      DOCUMENT ME!
         * @param   priority  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Future<?> submit(final Runnable task, final int priority) {
            return super.submit(new ComparableFutureTask(task, null, priority));
        }

        /**
         * DOCUMENT ME!
         *
         * @param   <T>       DOCUMENT ME!
         * @param   task      DOCUMENT ME!
         * @param   priority  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  NullPointerException  DOCUMENT ME!
         */
        public <T> Future<T> submit(final Callable<T> task, final int priority) {
            if (task == null) {
                throw new NullPointerException();
            }
            final RunnableFuture<T> ftask = new ComparableFutureTask(task, priority);
            execute(ftask);
            return ftask;
        }

        /**
         * execute with New comparable task.
         *
         * @param  command   DOCUMENT ME!
         * @param  priority  DOCUMENT ME!
         */
        public void execute(final Runnable command, final int priority) {
            super.execute(new ComparableFutureTask(command, null, priority));
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
            return (RunnableFuture<T>)callable;
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
            return (RunnableFuture<T>)runnable;
        }

        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            if ((t == null) && (r instanceof Future)) {
                Throwable thrown = null;
                try {
                    if (!((Future)r).isCancelled()) {
                        ((Future)r).get();
                    }
                } catch (final InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (final ExecutionException ex) {
                    thrown = ex.getCause();
                } catch (final Throwable tw) {
                    thrown = tw;
                }

                if (thrown != null) {
                    // the current thread is actually the one that executes the task
                    final Thread thread = Thread.currentThread();
                    final Thread.UncaughtExceptionHandler handler = thread.getUncaughtExceptionHandler();
                    if (handler == null) {
                        final Thread.UncaughtExceptionHandler groupHandler = thread.getThreadGroup();
                        if (groupHandler == null) {
                            final Thread.UncaughtExceptionHandler defHandler = Thread
                                        .getDefaultUncaughtExceptionHandler();
                            if (defHandler != null) {
                                defHandler.uncaughtException(thread, thrown);
                            }
                        } else {
                            groupHandler.uncaughtException(thread, thrown);
                        }
                    } else {
                        handler.uncaughtException(thread, thrown);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class ConcurrentLRUCache<Key, Value> {

        //~ Instance fields ----------------------------------------------------

        private final int maxSize;
        private ConcurrentHashMap<Key, Value> map;
        private ConcurrentLinkedQueue<Key> queue;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ConcurrentLRUCache object.
         *
         * @param  maxSize  DOCUMENT ME!
         */
        public ConcurrentLRUCache(final int maxSize) {
            this.maxSize = maxSize;
            map = new ConcurrentHashMap<Key, Value>(maxSize);
            queue = new ConcurrentLinkedQueue<Key>();
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  key    - may not be null!
         * @param  value  - may not be null!
         */
        public void put(final Key key, final Value value) {
            queue.remove(key); // remove the key from the FIFO queue

            while (queue.size() >= maxSize) {
                final Key oldestKey = queue.poll();
                if (null != oldestKey) {
                    map.remove(oldestKey);
                }
            }
            queue.add(key);
            map.put(key, value);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   key  - may not be null!
         *
         * @return  the value associated to the given key or null
         */
        public Value get(final Key key) {
            if (queue.remove(key)) {
                queue.add(key);
            }
            return map.get(key);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   bildnummer  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private boolean containsKey(final Key bildnummer) {
            return map.containsKey(bildnummer);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  key  DOCUMENT ME!
         */
        private void remove(final Key key) {
            queue.remove(key);
            map.get(key);
        }
    }
}
