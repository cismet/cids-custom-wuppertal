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
package de.cismet.cids.custom.butler;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.openide.util.Cancellable;
import org.openide.util.Exceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.cismet.cids.custom.utils.butler.ButlerFormat;
import de.cismet.cids.custom.utils.butler.ButlerProduct;
import de.cismet.cids.custom.wunda_blau.search.actions.ButlerQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerDownload extends AbstractDownload implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SERVER_ACTION = "butler1Query";
    private static final String TITLE = "Butler Download";
    private static final long WAIT_PERIOD = 5000;

    //~ Instance fields --------------------------------------------------------

    private String orderId;
    private ButlerProduct product;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private Future pollingFuture;
    private boolean omitSendingRequest = false;
    private String requestId = null;
    private boolean useZipFile = false;
    private boolean isButler2 = false;
    private String boxSize;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructor for a Butler 2 request.
     *
     * @param  directory  DOCUMENT ME!
     * @param  orderId    DOCUMENT ME!
     * @param  product    DOCUMENT ME!
     * @param  boxSize    DOCUMENT ME!
     * @param  middleX    DOCUMENT ME!
     * @param  middleY    DOCUMENT ME!
     */
    public ButlerDownload(final String directory,
            final String orderId,
            final ButlerProduct product,
            final String boxSize,
            final double middleX,
            final double middleY) {
        isButler2 = true;
        this.directory = directory;
        this.orderId = orderId;
        this.product = product;
        this.minX = middleX;
        this.minY = middleY;
//        this.maxX = maxX;C
//        this.maxY = maxY;
        status = State.WAITING;
        this.boxSize = boxSize;
        this.title = orderId;
        final ButlerFormat format = product.getFormat();
        determineDestinationFile(orderId, "." + format.getKey());
    }

    /**
     * Creates a new ButlerDownload object.
     *
     * @param  directory  DOCUMENT ME!
     * @param  orderId    DOCUMENT ME!
     * @param  product    DOCUMENT ME!
     * @param  minX       DOCUMENT ME!
     * @param  minY       DOCUMENT ME!
     * @param  maxX       DOCUMENT ME!
     * @param  maxY       DOCUMENT ME!
     */
    public ButlerDownload(final String directory,
            final String orderId,
            final ButlerProduct product,
            final double minX,
            final double minY,
            final double maxX,
            final double maxY) {
        this.directory = directory;
        this.orderId = orderId;
        this.product = product;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        status = State.WAITING;
        this.title = orderId;
        final ButlerFormat format = product.getFormat();
        if (format.getKey().equals("shp") || format.getKey().equals("geotif")) {
            determineDestinationFile(orderId, ".zip");
            useZipFile = true;
        } else {
            determineDestinationFile(orderId, "." + format.getKey());
        }
    }

    /**
     * Creates a new ButlerDownload object.
     *
     * @param  requestId    DOCUMENT ME!
     * @param  userOrderId  DOCUMENT ME!
     * @param  product      DOCUMENT ME!
     */
    ButlerDownload(final String requestId, final String userOrderId, final ButlerProduct product) {
        omitSendingRequest = true;
        this.orderId = userOrderId;
        this.requestId = requestId;
        this.product = product;
        status = State.WAITING;
        this.title = orderId;
        final ButlerFormat format = product.getFormat();
        if (format.getKey().equals("shp") || format.getKey().equals("geotif")) {
            determineDestinationFile(orderId, ".zip");
            useZipFile = true;
        } else {
            determineDestinationFile(orderId, "." + format.getKey());
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }
        /*
         * Phase 1: sending the reqeust to the server
         */
        status = State.RUNNING;
        stateChanged();

        if (!downloadFuture.isCancelled()) {
            if (!omitSendingRequest) {
                requestId = sendRequest();
            }
        } else {
            doCancellationHandling(false, false);
        }
        if (requestId == null) {
            // log that something went terribly wrong...
            return;
        }

        /*
         * Phase 2 : poll the result
         */
        Map<String, byte[]> result = null;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        if (!downloadFuture.isCancelled()) {
            pollingFuture = executor.submit(new ButlerPollingRunnable(requestId));
        } else {
            doCancellationHandling(true, false);
        }
        try {
            if (!downloadFuture.isCancelled()) {
                result = (Map<String, byte[]>)pollingFuture.get(1, TimeUnit.HOURS);
            } else {
                doCancellationHandling(true, true);
            }
        } catch (InterruptedException ex) {
            doCancellationHandling(true, true);
            Thread.currentThread().interrupt();
            return;
        } catch (ExecutionException ex) {
            log.warn("could not execute butler download", ex);
        } catch (TimeoutException ex) {
            log.warn("the maximum timeout for butler download is exceeded", ex);
        }

        if ((result == null) || (result.size() == 0)) {
            log.error("error during butler download");
            this.status = State.COMPLETED_WITH_ERROR;
            stateChanged();
        }
        /*
         * Phase 3: save the files
         */
        FileOutputStream out = null;
        try {
            if (!downloadFuture.isCancelled()) {
                if (useZipFile || (result.size() > 1)) {
                    // the server returned more than 1 file, this is the case for tif and shp format..
                    // we need to zip all these files and save them as zip
                    saveZipFileOfUnzippedFileCollection(result, fileToSaveTo);
                } else {
                    out = new FileOutputStream(fileToSaveTo);
                    final byte[] data = (byte[])result.values().toArray()[0];
                    out.write(data);
                }
            } else {
                doCancellationHandling(false, true);
            }
        } catch (final IOException ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }

//        setTitleFCorPhase(NASDownload.Phase.DONE);
        status = State.COMPLETED;
        stateChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  unzippedFiles  DOCUMENT ME!
     * @param  zipFile        DOCUMENT ME!
     */
    private void saveZipFileOfUnzippedFileCollection(
            final Map<String, byte[]> unzippedFiles,
            final File zipFile) {
//        determineDestinationFile(orderId, ".zip");
        final File f = new File(zipFile.getParentFile().getAbsolutePath() + "/test.zip");
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            f.createNewFile();
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            for (final String fn : unzippedFiles.keySet()) {
                final byte[] unzippedFile = unzippedFiles.get(fn);
                final StringBuilder extensionBuilder = new StringBuilder();
                final int extennstionIndex = fn.lastIndexOf(".");
                extensionBuilder.append(fn.substring(extennstionIndex));
                final String fileEntryName = orderId + extensionBuilder.toString();
                zos.putNextEntry(new ZipEntry(fileEntryName));
                zos.write(unzippedFile);
                zos.closeEntry();
            }
        } catch (IOException ex) {
            log.warn("error during creation of butler result zip file", ex);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                log.error("error during creation of butler result zip file", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cancelServerRequest  DOCUMENT ME!
     * @param  cancelPollingThread  DOCUMENT ME!
     */
    private void doCancellationHandling(final boolean cancelServerRequest, final boolean cancelPollingThread) {
        log.warn("Butler Download was interuppted");
        if (cancelServerRequest) {
            cancelRequest();
        }
        if (cancelPollingThread && (pollingFuture != null)) {
            pollingFuture.cancel(true);
        }
        deleteFile();
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        if (fileToSaveTo.exists() && fileToSaveTo.isFile()) {
            fileToSaveTo.delete();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String sendRequest() {
        final ServerActionParameter paramMethod = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                ButlerQueryAction.METHOD_TYPE.ADD);
        final ServerActionParameter paramOrderId = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.ORDER_ID
                        .toString(),
                orderId);
        final ServerActionParameter paramProduct = new ServerActionParameter(
                ButlerQueryAction.PARAMETER_TYPE.BUTLER_PRODUCT.toString(),
                product);

        final ServerActionParameter paramMinX = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.MIN_X
                        .toString(),
                minX);
        final ServerActionParameter paramMinY = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.MIN_Y
                        .toString(),
                minY);
        if (isButler2) {
            final ServerActionParameter paramBoxSize = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.BOX_SIZE.toString(),
                    boxSize);
            final ServerActionParameter paramIsWmps = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.IS_WMPS
                            .toString(),
                    true);
            try {
                return (String)SessionManager.getProxy()
                            .executeTask(
                                    SERVER_ACTION,
                                    "WUNDA_BLAU",
                                    null,
                                    paramMethod,
                                    paramOrderId,
                                    paramProduct,
                                    paramMinX,
                                    paramMinY,
                                    paramBoxSize,
                                    paramIsWmps);
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            final ServerActionParameter paramMaxX = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.MAX_X
                            .toString(),
                    maxX);
            final ServerActionParameter paramMaxY = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.MAX_Y
                            .toString(),
                    maxY);
            try {
                return (String)SessionManager.getProxy()
                            .executeTask(
                                    SERVER_ACTION,
                                    "WUNDA_BLAU",
                                    null,
                                    paramMethod,
                                    paramOrderId,
                                    paramProduct,
                                    paramMinX,
                                    paramMinY,
                                    paramMaxX,
                                    paramMaxY);
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    @Override
    public boolean cancel() {
        boolean cancelled = false;
        if (downloadFuture != null) {
            cancelled = downloadFuture.cancel(true);
        } else {
            doCancellationHandling(false, false);
        }
        status = State.ABORTED;
        stateChanged();
        return cancelled;
    }

    /**
     * DOCUMENT ME!
     */
    private void cancelRequest() {
        final ServerActionParameter paramOrderId = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.REQUEST_ID
                        .toString(),
                requestId);
        final ServerActionParameter paramMethod = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                ButlerQueryAction.METHOD_TYPE.CANCEL);
        try {
            SessionManager.getProxy().executeTask(
                SERVER_ACTION,
                "WUNDA_BLAU",
                null,
                paramOrderId,
                paramMethod);
        } catch (Exception ex) {
            log.error("error during enqueuing nas server request", ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ButlerPollingRunnable implements Callable<Map<String, byte[]>> {

        //~ Instance fields ----------------------------------------------------

        private String requestId;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ButlerPollingRunnable object.
         *
         * @param  requestId  DOCUMENT ME!
         */
        public ButlerPollingRunnable(final String requestId) {
            this.requestId = requestId;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Map<String, byte[]> call() throws Exception {
            final ServerActionParameter paramMethod = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.METHOD
                            .toString(),
                    ButlerQueryAction.METHOD_TYPE.GET);
            final ServerActionParameter paramRequestId = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.REQUEST_ID.toString(),
                    requestId);
            final ServerActionParameter paramProduct = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.BUTLER_PRODUCT.toString(),
                    product);
            Map<String, byte[]> files = null;
            try {
                files = (Map<String, byte[]>)SessionManager.getProxy()
                            .executeTask(
                                    SERVER_ACTION,
                                    "WUNDA_BLAU",
                                    null,
                                    paramMethod,
                                    paramRequestId,
                                    paramProduct);

                while (files == null) {
                    if (Thread.interrupted()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Butler polling future was cancelled");
                        }
                        return null;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Result for " + requestId + " is not finished, try later again");
                    }
                    try {
                        Thread.sleep(WAIT_PERIOD);
                    } catch (InterruptedException ex) {
                        log.info("result fetching thread was interrupted", ex);
                        Thread.currentThread().interrupt();
                        return null;
                    }
                    files = (Map<String, byte[]>)SessionManager.getProxy()
                                .executeTask(
                                        SERVER_ACTION,
                                        "WUNDA_BLAU",
                                        null,
                                        paramMethod,
                                        paramRequestId,
                                        paramProduct);
                }
                return files;
            } catch (ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
            return null;
        }
    }
}
