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

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
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

import de.cismet.cids.custom.utils.nas.NasProductTemplate;
import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class NASDownload extends AbstractDownload implements Cancellable {

    //~ Static fields/initializers ---------------------------------------------

    private static String SEVER_ACTION = "nasDataQuery";
    private static String EXTENSION = ".xml";
    private static String STANDARD_FILE_NAME = "nas-result";
    private static final String BASE_TITLE;

    static {
        BASE_TITLE = NbBundle.getMessage(
                NASDownload.class,
                "NASDownload.basetitle.text");
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
    private NasProductTemplate template;
    private Geometry geometry;
    private String orderId;
    private transient byte[] content;
    private boolean omitSendingRequest = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDownload object.
     *
     * @param  orderId  DOCUMENT ME!
     */
    public NASDownload(final String orderId) {
        omitSendingRequest = true;
        this.orderId = orderId;
        template = null;
        geometry = null;
        this.title = BASE_TITLE;
        status = State.WAITING;
        this.directory = "";
        filename = orderId;
        fileToSaveTo = new File("" + System.currentTimeMillis());
//        determineDestinationFile(orderId, EXTENSION);
    }

    /**
     * Creates a new NASDownload object.
     *
     * @param  title      DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  template   DOCUMENT ME!
     * @param  g          DOCUMENT ME!
     */
    public NASDownload(final String title,
            final String directory,
            final NasProductTemplate template,
            final Geometry g) {
        this.template = template;
        geometry = g;
        this.title = title;
        this.directory = directory;
        status = State.WAITING;
        fileToSaveTo = new File("" + System.currentTimeMillis());
//        determineDestinationFile(STANDARD_FILE_NAME, EXTENSION);
    }

    /**
     * Creates a new NASDownload object.
     *
     * @param  title      DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  template   DOCUMENT ME!
     * @param  g          DOCUMENT ME!
     */
    public NASDownload(final String title,
            final String filename,
            final String directory,
            final NasProductTemplate template,
            final Geometry g) {
        this.template = template;
        geometry = g;
        this.title = title;
        this.directory = directory;
        status = State.WAITING;
        fileToSaveTo = new File("" + System.currentTimeMillis());
        this.filename = filename;
//        determineDestinationFile(filename, EXTENSION);
    }

    /**
     * Creates a new NASDownload object.
     */
    private NASDownload() {
        fileToSaveTo = new File("" + System.currentTimeMillis());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean cancel() {
        downloadFuture.cancel(true);
        status = State.ABORTED;
        stateChanged();
        return downloadFuture.isCancelled();
    }

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }
        /*
         * Phase 1: sending the reqeust to the server
         */
        setTitleForPhase(Phase.REQEUST_GEN);
        status = State.RUNNING;
        stateChanged();
        if (!omitSendingRequest) {
            if (Thread.interrupted()) {
                log.warn("NAS Download was interuppted");
                return;
            }
            orderId = sendNasRequest();
            if (orderId == null) {
                log.error("nas server request returned no orderId, cannot continue with NAS download");
                this.status = State.COMPLETED_WITH_ERROR;
                stateChanged();
                return;
            }
            if (filename == null) {
                filename = orderId;
            }
        }
        setTitleForPhase(Phase.RETRIEVAL);
        stateChanged();

        /*
         * Phase 2: retrive the result from the cids server
         */
        if (Thread.interrupted()) {
            log.warn("NAS Download was interuppted");
            cancelNasRequest();
            return;
        }
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<ByteArrayWrapper> pollingFuture = executor.submit(new ServerPollingRunnable());
        try {
            content = pollingFuture.get(1, TimeUnit.HOURS).getByteArray();
        } catch (InterruptedException ex) {
            log.warn("NAS Download was interuppted", ex);
            cancelNasRequest();
            return;
        } catch (ExecutionException ex) {
            log.warn("could not execute nas download", ex);
            Exceptions.printStackTrace(ex);
        } catch (TimeoutException ex) {
            log.warn("the maximum timeout for nas download is exceeded", ex);
        }

        setTitleForPhase(Phase.DOWNLOAD);
        stateChanged();

        if ((content == null) || (content.length <= 0)) {
            log.info("Downloaded content seems to be empty..");

            if (status == State.RUNNING) {
                status = State.COMPLETED_WITH_ERROR;
                stateChanged();
            }
            return;
        }
        /*
         * Phase 3: save the result file
         */
        if (Thread.interrupted()) {
            log.warn("NAS Download was interuppted");
            cancelNasRequest();
            return;
        }

        if (filename == null) {
            determineDestinationFile(STANDARD_FILE_NAME, EXTENSION);
        } else {
            determineDestinationFile(filename, EXTENSION);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSaveTo);
            out.write(content);
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

        setTitleForPhase(Phase.DONE);
        status = State.COMPLETED;
        stateChanged();
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
        title = BASE_TITLE + " - " + appendix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String sendNasRequest() {
        final ServerActionParameter paramTemplate = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.TEMPLATE
                        .toString(),
                template);
        final ServerActionParameter paramGeom = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.GEOMETRY
                        .toString(),
                geometry);
        final ServerActionParameter paramMethod = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                NasDataQueryAction.METHOD_TYPE.ADD);
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                SEVER_ACTION,
                                "WUNDA_BLAU",
                                null,
                                paramTemplate,
                                paramGeom,
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
            while (result == null) {
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
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        log.error("result fetching thread was interrupted", ex);
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
