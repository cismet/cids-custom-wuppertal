/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryCollection;

import org.openide.util.NbBundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.cismet.cids.custom.utils.nas.NasProduct;
import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class NASDownload extends AbstractCancellableDownload {

    //~ Static fields/initializers ---------------------------------------------

    private static String SEVER_ACTION = "nasDataQuery";
    private static String XML_EXTENSION = ".xml";
    private static String ZIP_EXTENSION = ".zip";
    private static String DXF_EXTENSION = ".dxf";
    private static final String BASE_TITLE_NAS;
    private static final String BASE_TITLE_DXF;

    static {
        BASE_TITLE_NAS = NbBundle.getMessage(
                NASDownload.class,
                "NASDownload.basetitle.nas.text");
        BASE_TITLE_DXF = NbBundle.getMessage(
                NASDownload.class,
                "NASDownload.basetitle.dxf.text");
    }

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private enum Phase {

        //~ Enum constants -----------------------------------------------------

        REQEUST_GEN, RETRIEVAL, DOWNLOAD, DONE
    }

    //~ Instance fields --------------------------------------------------------

    protected String filename = null;
    private Future<ByteArrayWrapper> pollingFuture;
    private NasProduct product;
    private GeometryCollection geometries;
    private String orderId;
    private transient byte[] content;
    private boolean omitSendingRequest = false;
    private String requestId;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDownload object.
     *
     * @param  orderId     DOCUMENT ME!
     * @param  isSplitted  DOCUMENT ME!
     * @param  isDxf       DOCUMENT ME!
     * @param  requestId   DOCUMENT ME!
     */
    public NASDownload(final String orderId, final boolean isSplitted, final boolean isDxf, final String requestId) {
        omitSendingRequest = true;
        this.orderId = orderId;
        product = new NasProduct();
        product.setFormat(isDxf ? NasProduct.Format.DXF.toString() : NasProduct.Format.NAS.toString());
        geometries = null;
        this.title = isDxf ? BASE_TITLE_DXF : BASE_TITLE_NAS;
        status = State.WAITING;
        this.requestId = requestId;
        this.directory = "";
//        filename = orderId;
        if ((requestId != null) && !requestId.equals("")) {
            fileToSaveTo = new File("" + requestId);
        } else {
            fileToSaveTo = new File("" + orderId);
        }
        String extension = XML_EXTENSION;
        if (product.getFormat().equals(NasProduct.Format.DXF.toString())) {
            extension = DXF_EXTENSION;
        } else if (isSplitted) {
            extension = ZIP_EXTENSION;
        }
        if ((filename != null) && !filename.equals("")) {
            determineDestinationFile(filename, extension);
        } else {
            determineDestinationFile(requestId, extension);
        }
    }

    /**
     * Creates a new NASDownload object.
     *
     * @param  title      DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  requestId  DOCUMENT ME!
     * @param  product    template DOCUMENT ME!
     * @param  g          DOCUMENT ME!
     */
// public NASDownload(final String title,
// final String directory,
// final NasProductTemplate template,
// final Geometry g) {
// this.template = template;
// geometry = g;
// this.title = title;
// this.directory = directory;
// status = State.WAITING;
// fileToSaveTo = new File("" + System.currentTimeMillis());
////        determineDestinationFile(STANDARD_FILE_NAME, EXTENSION);
//    }
    /**
     * Creates a new NASDownload object.
     *
     * @param  title      DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  requestId  DOCUMENT ME!
     * @param  product    template DOCUMENT ME!
     * @param  g          DOCUMENT ME!
     */
    public NASDownload(final String title,
            final String filename,
            final String directory,
            final String requestId,
            final NasProduct product,
            final GeometryCollection g) {
        this.product = product;
        geometries = g;
        this.title = title;
        this.directory = directory;
        this.requestId = requestId;
        status = State.WAITING;
        if ((requestId != null) && !requestId.equals("")) {
            fileToSaveTo = new File("" + requestId);
        } else {
            fileToSaveTo = new File("" + System.currentTimeMillis());
        }
        this.filename = filename;
        String extension;
        if (product.getFormat().equals(NasProduct.Format.DXF.toString())) {
            extension = DXF_EXTENSION;
        } else {
            extension = isOrderSplitted(g) ? ZIP_EXTENSION : XML_EXTENSION;
        }
        if ((filename != null) && !filename.equals("")) {
            determineDestinationFile(filename, extension);
        } else {
            determineDestinationFile(requestId, extension);
        }
    }

    /**
     * Creates a new NASDownload object.
     */
    private NASDownload() {
        fileToSaveTo = new File("" + System.currentTimeMillis());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   geoms  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isOrderSplitted(final GeometryCollection geoms) {
        final Envelope env = geoms.getEnvelopeInternal();
        final double xSize = env.getMaxX() - env.getMinX();
        final double ySize = env.getMaxY() - env.getMinY();

        if ((xSize > 500) && (ySize > 500)) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    private void setAbortedStatus() {
        status = State.ABORTED;
        stateChanged();
    }

    @Override
    public boolean cancel() {
        boolean cancelled = true;
        boolean isDone = false;
        if (downloadFuture != null) {
            isDone = downloadFuture.isDone();
            cancelled = downloadFuture.cancel(true);
        }
        if (pollingFuture != null) {
            pollingFuture.cancel(true);
        }
        if (cancelled || isDone) {
            status = State.ABORTED;
            stateChanged();
        }

        return downloadFuture.isCancelled();
    }

    @Override
    public void run() {
        try {
            if (status != State.WAITING) {
                return;
            }
            /*
             * Phase 1: sending the reqeust to the server
             */
            setTitleForPhase(Phase.REQEUST_GEN);
            titleChanged();
            status = State.RUNNING;
            stateChanged();
            final String format = product.getFormat().equalsIgnoreCase(NasProduct.Format.DXF.toString()) ? "DXF"
                                                                                                         : "NAS";
            if (!omitSendingRequest) {
                if (!downloadFuture.isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug(format + " Download: sending request to server");
                    }
                    orderId = sendNasRequest();
                } else {
                    doCancellationHandling(false, false);
                    return;
                }
                if (orderId == null) {
                    log.error("nas server request returned no orderId, cannot continue with NAS download");
                    this.status = State.COMPLETED_WITH_ERROR;
                    stateChanged();
                    return;
                }
                if ((filename == null) && (requestId != null)) {
//                filename = orderId;
                    filename = requestId;
                }
            }

            if (!downloadFuture.isCancelled()) {
                setTitleForPhase(Phase.RETRIEVAL);
                titleChanged();
            }

            /*
             * Phase 2: retrive the result from the cids server
             */
            if (log.isDebugEnabled()) {
                log.debug("NAS Download: Request correctly sended start polling the result from server (max 1 hour)");
            }
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            if (!downloadFuture.isCancelled()) {
                pollingFuture = executor.submit(new ServerPollingRunnable());
            } else {
                doCancellationHandling(true, false);
                return;
            }
            try {
                if (!downloadFuture.isCancelled() && (pollingFuture != null)) {
                    content = pollingFuture.get().getByteArray();
                    if (log.isDebugEnabled()) {
                        log.debug("NAS Download: Polling is finished.");
                    }
                } else {
                    doCancellationHandling(true, true);
                    return;
                }
            } catch (InterruptedException ex) {
                log.error("The polling thread was interrupted.", ex);
                doCancellationHandling(true, true);
                Thread.currentThread().interrupt();
                error(ex);
            } catch (ExecutionException ex) {
                log.warn("could not execute nas download", ex);
                error(ex);
            }
//            catch (TimeoutException ex) {
            // log.error("the maximum timeout for butler download is exceeded", ex);
            // title = BASE_TITLE + " - "
            // + org.openide.util.NbBundle.getMessage(NASDownload.class,
            // "NASDownload.timeoutErrorTitle");
            // error(new TimeoutException(
            // org.openide.util.NbBundle.getMessage(NASDownload.class, "NASDownload.timeoutErrorMessage")));
            // }
            catch (Exception ex) {
                log.error("Exception during waiting / polling on NAS Result", ex);
                error(ex);
            }

            if (!downloadFuture.isCancelled()) {
                setTitleForPhase(Phase.DOWNLOAD);
                titleChanged();
            }

            if ((content == null) || (content.length <= 0)) {
                log.info("NAS Download: Downloaded content seems to be empty..");

                if ((status == State.RUNNING) && !Thread.interrupted()) {
                    status = State.COMPLETED_WITH_ERROR;
                    stateChanged();
                }
                return;
            }
            /*
             * Phase 3: save the result file
             */
            FileOutputStream out = null;
            try {
                if (!downloadFuture.isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("NAS Download: Start writing the result to file");
                    }
                    out = new FileOutputStream(fileToSaveTo);
                    out.write(content);
                } else {
                    doCancellationHandling(false, false);
                    return;
                }
            } catch (final IOException ex) {
                log.error("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
                error(ex);
                return;
            } catch (final Exception ex) {
                log.error("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
                error(ex);
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e) {
                        log.error("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", e);
                        error(e);
                    }
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("NAS Download: done.");
            }
            if (!downloadFuture.isCancelled()) {
                setTitleForPhase(Phase.DONE);
                status = State.COMPLETED;
                stateChanged();
            }
        } catch (Exception ex) {
            log.error("Exception during NASDownload " + NASDownload.this.filename, ex);
            error(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cancelServerRequest  DOCUMENT ME!
     * @param  cancelPollingThread  DOCUMENT ME!
     */
    private void doCancellationHandling(final boolean cancelServerRequest, final boolean cancelPollingThread) {
        log.warn("NAS Download was interuppted");
        if (cancelServerRequest) {
            cancelNasRequest();
        }
        if (cancelPollingThread && (pollingFuture != null)) {
            pollingFuture.cancel(true);
        }
//        setAbortedStatus();
        deleteFile();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  p  DOCUMENT ME!
     */
    private void setTitleForPhase(final Phase p) {
        String appendix = "";
        if (p == Phase.REQEUST_GEN) {
            appendix = NbBundle.getMessage(NASDownload.class, "NASDownload.requestGenTitle.text");
        } else if (p == Phase.RETRIEVAL) {
            appendix = NbBundle.getMessage(NASDownload.class, "NASDownload.resultRetrievalTitle.text");
        } else if (p == Phase.DOWNLOAD) {
            appendix = NbBundle.getMessage(NASDownload.class, "NASDownload.downloadTitle.text");
        }
        title = product.getFormat().equalsIgnoreCase(NasProduct.Format.DXF.toString()) ? BASE_TITLE_DXF
                                                                                       : BASE_TITLE_NAS;
        if ((appendix != null) && !appendix.equals("")) {
            title += " - " + appendix;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String sendNasRequest() {
        final ServerActionParameter paramTemplate = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.TEMPLATE
                        .toString(),
                product);
        final ServerActionParameter paramGeom = new ServerActionParameter(
                NasDataQueryAction.PARAMETER_TYPE.GEOMETRY_COLLECTION.toString(),
                geometries);
        final ServerActionParameter paramMethod = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                NasDataQueryAction.METHOD_TYPE.ADD);
        final ServerActionParameter paramRequest = new ServerActionParameter(
                NasDataQueryAction.PARAMETER_TYPE.REQUEST_ID.toString(),
                requestId);
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                SEVER_ACTION,
                                "WUNDA_BLAU",
                                null,
                                paramTemplate,
                                paramGeom,
                                paramRequest,
                                paramMethod);
        } catch (Exception ex) {
            log.error("error during enqueuing nas server request", ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    private void cancelNasRequest() {
        final ServerActionParameter paramOrderId = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.ORDER_ID
                        .toString(),
                orderId);
        final ServerActionParameter paramMethod = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                NasDataQueryAction.METHOD_TYPE.CANCEL);
        try {
            SessionManager.getProxy().executeTask(
                SEVER_ACTION,
                "WUNDA_BLAU",
                null,
                paramOrderId,
                paramMethod);
        } catch (Exception ex) {
            log.error("error during enqueuing nas server request", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        if (fileToSaveTo.exists() && fileToSaveTo.isFile()) {
            fileToSaveTo.delete();
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ServerPollingRunnable implements Callable<ByteArrayWrapper> {

        //~ Methods ------------------------------------------------------------

        @Override
        public ByteArrayWrapper call() {
            final ServerActionParameter paramMethod = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.METHOD
                            .toString(),
                    NasDataQueryAction.METHOD_TYPE.GET);

            final ServerActionParameter paramOrderId = new ServerActionParameter(
                    NasDataQueryAction.PARAMETER_TYPE.ORDER_ID.toString(),
                    orderId);
            byte[] result = null;
            while ((result == null) || (result.length == 0)) {
                if (Thread.interrupted()) {
                    log.info("result fetching thread was interrupted");
                    return null;
                }
                try {
                    result = (byte[])SessionManager.getProxy()
                                .executeTask(
                                        SEVER_ACTION,
                                        "WUNDA_BLAU",
                                        null,
                                        paramOrderId,
                                        paramMethod);
                } catch (ConnectionException ex) {
                    log.error("error during pulling nas result from server", ex);
                }
                if (result == null) {
                    // there went something wrong on server side so abort the download
                    return null;
                } else if (result.length == 0) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        log.info("result fetching thread was interrupted", ex);
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
            }
            final ByteArrayWrapper baw = new ByteArrayWrapper(result);
            return baw;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ByteArrayWrapper {

        //~ Instance fields ----------------------------------------------------

        private byte[] byteArray;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ByteArrayWrapper object.
         *
         * @param  ba  DOCUMENT ME!
         */
        private ByteArrayWrapper(final byte[] ba) {
            byteArray = ba;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public byte[] getByteArray() {
            return byteArray;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  b  DOCUMENT ME!
         */
        public void setByteArray(final byte[] b) {
            this.byteArray = b;
        }
    }
}
