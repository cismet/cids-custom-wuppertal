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

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ButlerDownload object.
     *
     * @param  orderId  DOCUMENT ME!
     * @param  product  DOCUMENT ME!
     * @param  minX     DOCUMENT ME!
     * @param  minY     DOCUMENT ME!
     * @param  maxX     DOCUMENT ME!
     * @param  maxY     DOCUMENT ME!
     */
    public ButlerDownload(final String orderId,
            final ButlerProduct product,
            final double minX,
            final double minY,
            final double maxX,
            final double maxY) {
        this.orderId = orderId;
        this.product = product;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        status = State.WAITING;
        this.title = TITLE;
        final ButlerFormat format = product.getFormat();
        determineDestinationFile(orderId, "." + format.getKey());
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
        this.title = TITLE;
        final ButlerFormat format = product.getFormat();
        determineDestinationFile(userOrderId, "." + format.getKey());
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
            doCancellationHandling(false);
        }
        if (requestId == null) {
            // log that something went terribly wrong...
            return;
        }

        /*
         * Phase 2 : poll the result
         */
        ArrayList<byte[]> result = null;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        if (!downloadFuture.isCancelled()) {
            pollingFuture = executor.submit(new ButlerPollingRunnable(requestId));
        } else {
            doCancellationHandling(false);
        }
        try {
            if (!downloadFuture.isCancelled()) {
                result = (ArrayList<byte[]>)pollingFuture.get(1, TimeUnit.HOURS);
            } else {
                doCancellationHandling(true);
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            log.warn("could not execute butler download", ex);
        } catch (TimeoutException ex) {
            log.warn("the maximum timeout for butler download is exceeded", ex);
        }

        /*
         * Phase 3: save the files
         */
        FileOutputStream out = null;
        try {
            if (!downloadFuture.isCancelled()) {
                out = new FileOutputStream(fileToSaveTo);
                out.write(result.get(0));
            } else {
                doCancellationHandling(true);
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
     * @param  cancelPollingThread  DOCUMENT ME!
     */
    private void doCancellationHandling(final boolean cancelPollingThread) {
        log.warn("Butler Download was interuppted");
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
        return null;
    }

    @Override
    public boolean cancel() {
        boolean cancelled = false;
        if (downloadFuture != null) {
            cancelled = downloadFuture.cancel(true);
        } else {
            doCancellationHandling(false);
        }
        status = State.ABORTED;
        stateChanged();
        return cancelled;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ButlerPollingRunnable implements Callable<ArrayList<byte[]>> {

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
        public ArrayList<byte[]> call() throws Exception {
            final ServerActionParameter paramMethod = new ServerActionParameter(ButlerQueryAction.PARAMETER_TYPE.METHOD
                            .toString(),
                    ButlerQueryAction.METHOD_TYPE.GET);
            final ServerActionParameter paramRequestId = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.REQUEST_ID.toString(),
                    requestId);
            final ServerActionParameter paramProduct = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.BUTLER_PRODUCT.toString(),
                    product);
            ArrayList<byte[]> files = null;
            try {
                files = (ArrayList<byte[]>)SessionManager.getProxy()
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
                        Exceptions.printStackTrace(ex);
                    }
                    files = (ArrayList<byte[]>)SessionManager.getProxy()
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
