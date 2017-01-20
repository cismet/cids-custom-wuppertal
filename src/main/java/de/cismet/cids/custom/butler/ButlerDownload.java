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

import org.openide.util.Exceptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Properties;
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

import de.cismet.commons.security.exceptions.BadHttpStatusCodeException;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerDownload extends HttpDownload {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SERVER_ACTION = "butler1Query";
    private static final String TITLE = "Butler Download";
    private static final long WAIT_PERIOD = 5000;
    private static String BUTLER_SERVER_BASE_PATH;
    private static String TIF_RESULT_DIR;
    private static String PDF_RESULT_DIR;
    private static String SHAPE_RESULT_DIR;
    private static String DXF_RESULT_DIR;

    static {
        final Properties prop = new Properties();
        try {
            prop.load(ButlerDownload.class.getResourceAsStream("butlerDownload.properties"));
            BUTLER_SERVER_BASE_PATH = prop.getProperty("butlerBasePath");
            TIF_RESULT_DIR = prop.getProperty("tifResultDir");
            PDF_RESULT_DIR = prop.getProperty("pdfResultDir");
            SHAPE_RESULT_DIR = prop.getProperty("shapeResultDir");
            DXF_RESULT_DIR = prop.getProperty("dxfResultDir");
        } catch (Exception ex) {
        }
    }

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
    private boolean isEtrsRahmenkarte = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Constructor for a Butler 2 request.
     *
     * @param  directory          DOCUMENT ME!
     * @param  orderId            DOCUMENT ME!
     * @param  product            DOCUMENT ME!
     * @param  isEtrsRahmenkarte  DOCUMENT ME!
     * @param  boxSize            DOCUMENT ME!
     * @param  middleX            DOCUMENT ME!
     * @param  middleY            DOCUMENT ME!
     */
    public ButlerDownload(final String directory,
            final String orderId,
            final ButlerProduct product,
            final boolean isEtrsRahmenkarte,
            final String boxSize,
            final double middleX,
            final double middleY) {
        isButler2 = true;
        this.directory = directory;
        this.orderId = orderId;
        this.product = product;
        this.isEtrsRahmenkarte = isEtrsRahmenkarte;
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
            log.error("could not register butler request.");
            error(new IllegalStateException("Fehler beim Senden des Butler Auftrags."));
            this.status = State.COMPLETED_WITH_ERROR;
            stateChanged();
            return;
        }

        /*
         * Phase 2 : poll the result
         */
        ArrayList<URL> result = null;
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        if (!downloadFuture.isCancelled()) {
            pollingFuture = executor.submit(new ButlerPollingRunnable(requestId));
        } else {
            doCancellationHandling(true, false);
        }
        try {
            if (!downloadFuture.isCancelled()) {
                result = (ArrayList<URL>)pollingFuture.get(1, TimeUnit.HOURS);
            } else {
                doCancellationHandling(true, true);
            }
        } catch (InterruptedException ex) {
            doCancellationHandling(true, true);
            Thread.currentThread().interrupt();
            log.error("Butler Download was interrupted", ex);
            error(ex);
            return;
        } catch (ExecutionException ex) {
            log.error("could not execute butler download", ex);
            error(ex);
        } catch (TimeoutException ex) {
            log.error("the maximum timeout for butler download is exceeded", ex);
            error(new TimeoutException(
                    org.openide.util.NbBundle.getMessage(ButlerDownload.class, "ButlerDownload.timeoutErrorMessage")));
        }

        if ((result == null) || (result.isEmpty())) {
            log.error("error during butler download");
            this.status = State.COMPLETED_WITH_ERROR;
            stateChanged();
        }
        /*
         * Phase 3: save the files
         */
        if (!downloadFuture.isCancelled()) {
            if (useZipFile || (result.size() > 1)) {
                // the server returned more than 1 file, this is the case for tif and shp format..
                // we need to zip all these files and save them as zip
                saveZipFileOfUnzippedFileCollection(result, fileToSaveTo);
            } else {
                saveFile(result);
            }
        } else {
            doCancellationHandling(false, true);
        }
        removeRequestFromServer();
        status = State.COMPLETED;
        stateChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  result  DOCUMENT ME!
     */
    private void saveFile(final ArrayList<URL> result) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSaveTo);
            final URL fileUrl = result.get(0);
            final InputStream is = getUrlInputStreamWithWebAcessManager(fileUrl);
            downloadStream(is, out);
        } catch (Exception ex) {
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
    }

    /**
     * DOCUMENT ME!
     *
     * @param  unzippedFiles  DOCUMENT ME!
     * @param  zipFile        DOCUMENT ME!
     */
    private void saveZipFileOfUnzippedFileCollection(
            final ArrayList<URL> unzippedFiles,
            final File zipFile) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(fos);
            for (final URL url : unzippedFiles) {
                final StringBuilder extensionBuilder = new StringBuilder();
                final int extennstionIndex = url.getFile().lastIndexOf(".");
                extensionBuilder.append(url.getFile().substring(extennstionIndex));
                final String fileEntryName = orderId + extensionBuilder.toString();
                zos.putNextEntry(new ZipEntry(fileEntryName));
                final InputStream is = getUrlInputStreamWithWebAcessManager(url);
                downloadStream(is, zos);
                zos.closeEntry();
            }
        } catch (Exception ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
            return;
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
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
            removeRequestFromServer();
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
            final ServerActionParameter etrsRahmenkarte = new ServerActionParameter(
                    ButlerQueryAction.PARAMETER_TYPE.ETRS_BLATTSCHNITT.toString(),
                    this.isEtrsRahmenkarte);
            try {
                return (String)SessionManager.getProxy()
                            .executeTask(
                                    SERVER_ACTION,
                                    "WUNDA_BLAU",
                                    null,
                                    paramMethod,
                                    paramOrderId,
                                    paramProduct,
                                    etrsRahmenkarte,
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
    private void removeRequestFromServer() {
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
    private final class ButlerPollingRunnable implements Callable<ArrayList<URL>> {

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

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private ArrayList<URL> getDownloadURLs() {
            final ArrayList<URL> result = new ArrayList<URL>();
            final StringBuilder baseUrl = new StringBuilder();
            final ArrayList<String> fileExtensions = new ArrayList<String>();
            final String format = product.getFormat().getKey();
            try {
                baseUrl.append(BUTLER_SERVER_BASE_PATH);
                if (format.equals("dxf")) {
                    baseUrl.append(DXF_RESULT_DIR);
                    fileExtensions.add(".dxf");
                } else if (format.equals("shp")) {
                    baseUrl.append(SHAPE_RESULT_DIR);
                    fileExtensions.add(".shp");
                    fileExtensions.add(".prj");
                    fileExtensions.add(".dbf");
                    fileExtensions.add(".shx");
                } else if (format.equals("tif")) {
                    baseUrl.append(TIF_RESULT_DIR);
                    fileExtensions.add(".tif");
                } else if (format.equals("geotif")) {
                    baseUrl.append(TIF_RESULT_DIR);
                    fileExtensions.add(".tif");
                    fileExtensions.add(".tfw");
                } else {
                    // this must be true here: format.equals("pdf")
                    baseUrl.append(PDF_RESULT_DIR);
                    fileExtensions.add(".pdf");
                }
                baseUrl.append("/");
                baseUrl.append(URLEncoder.encode(requestId, "UTF8"));
                for (final String fileExtension : fileExtensions) {
                    try {
                        result.add(new URL(baseUrl.toString() + fileExtension));
                    } catch (MalformedURLException ex) {
                        // should not happen
                        log.error("Missformed Download URL");
                    }
                }
            } catch (UnsupportedEncodingException ex) {
                log.error("Unsupported Encoding", ex); // never thrown
            }
            return result;
        }

        @Override
        public ArrayList<URL> call() throws Exception {
            final ArrayList<URL> urls = getDownloadURLs();
            if (urls.isEmpty()) {
                log.error("Could not determine Download URLS");
                return null;
            }
            final URL url = urls.get(0);
            boolean fileExists = false;
            while (!fileExists) {
                try {
                    fileExists = (WebAccessManager.getInstance().doRequest(url) != null);
                } catch (BadHttpStatusCodeException e) {
                    if (e.getStatuscode() == 404) {
                        if (log.isDebugEnabled()) {
                            log.debug("Resultfile for order " + requestId + " not exists. Trying again");
                        }
                    } else {
                        return null;
                    }
                } catch (final Exception ex) {
                    log.warn("unknown download error", ex);
                    return null;
                }
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
            }
            return urls;
        }
    }
}
