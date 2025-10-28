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

import com.vividsolutions.jts.geom.GeometryCollection;

import org.openide.util.NbBundle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.cismet.cids.custom.utils.nas.NasProduct;
import de.cismet.cids.custom.wunda_blau.search.actions.NasCsvDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CSVListDownload extends AbstractCancellableDownload implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static String CSV_EXTENSION = ".csv";

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
    private NasProduct product;
    private GeometryCollection geometries;
    private String orderId;
    private String requestId;

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDownload object.
     *
     * @param  title              DOCUMENT ME!
     * @param  filename           DOCUMENT ME!
     * @param  directory          DOCUMENT ME!
     * @param  requestId          DOCUMENT ME!
     * @param  product            template DOCUMENT ME!
     * @param  g                  DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public CSVListDownload(final String title,
            final String filename,
            final String directory,
            final String requestId,
            final NasProduct product,
            final GeometryCollection g,
            final ConnectionContext connectionContext) {
        this.product = product;
        this.connectionContext = connectionContext;
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
        final String extension = CSV_EXTENSION;

        if ((filename != null) && !filename.equals("")) {
            determineDestinationFile(filename, extension);
        } else {
            determineDestinationFile(requestId, extension);
        }
    }

    /**
     * Creates a new NASDownload object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    private CSVListDownload(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        fileToSaveTo = new File("" + System.currentTimeMillis());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public boolean cancel() {
        boolean cancelled = true;
        boolean isDone = false;

        if (downloadFuture != null) {
            isDone = downloadFuture.isDone();
            cancelled = downloadFuture.cancel(true);
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
            final String content;

            if (status != State.WAITING) {
                return;
            }
            /*
             * Phase 1: sending the reqeust to the server
             */
            setTitleForPhase(Phase.DOWNLOAD);
            titleChanged();
            status = State.RUNNING;
            stateChanged();

            if (!downloadFuture.isCancelled()) {
                if (log.isDebugEnabled()) {
                    log.debug("CSV Download: sending request to server");
                }
                content = sendListRequest();
            } else {
                doCancellationHandling(false, false);
                return;
            }

            if ((content == null) || (content.length() <= 0)) {
                log.info("NAS Download: Downloaded content seems to be empty..");

                if ((status == State.RUNNING) && !Thread.interrupted()) {
                    status = State.COMPLETED_WITH_ERROR;
                    stateChanged();
                }
                return;
            }

            /*
             * Phase 2: save the result file
             */
            BufferedWriter out = null;
            try {
                if (!downloadFuture.isCancelled()) {
                    if (log.isDebugEnabled()) {
                        log.debug("NAS Download: Start writing the result to file");
                    }
                    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToSaveTo)));
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
            log.error("Exception during NASDownload " + CSVListDownload.this.filename, ex);
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
            appendix = NbBundle.getMessage(CSVListDownload.class, "NASDownload.requestGenTitle.text");
        } else if (p == Phase.RETRIEVAL) {
            appendix = NbBundle.getMessage(CSVListDownload.class, "NASDownload.resultRetrievalTitle.text");
        } else if (p == Phase.DOWNLOAD) {
            appendix = NbBundle.getMessage(CSVListDownload.class, "NASDownload.downloadTitle.text");
        }

        if ((appendix != null) && !appendix.equals("")) {
            title += "CSV - " + appendix;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String sendListRequest() {
        final ServerActionParameter paramTemplate = new ServerActionParameter(
                NasCsvDataQueryAction.PARAMETER_TYPE.TEMPLATE.toString(),
                product);
        final ServerActionParameter paramGeom = new ServerActionParameter(
                NasCsvDataQueryAction.PARAMETER_TYPE.GEOMETRY_COLLECTION.toString(),
                geometries);
        final ServerActionParameter paramMethod = new ServerActionParameter(
                NasCsvDataQueryAction.PARAMETER_TYPE.METHOD.toString(),
                NasCsvDataQueryAction.METHOD_TYPE.CREATE);
        final ServerActionParameter paramRequest = new ServerActionParameter(
                NasCsvDataQueryAction.PARAMETER_TYPE.AUFTRAGSNUMMER.toString(),
                requestId);
        try {
            final Object answer = SessionManager.getProxy()
                        .executeTask(
                            NasCsvDataQueryAction.TASKNAME,
                            "WUNDA_BLAU",
                            (Object)null,
                            getConnectionContext(),
                            paramTemplate,
                            paramGeom,
                            paramMethod,
                            paramRequest);

            if (answer instanceof byte[]) {
                return new String((byte[])answer, "CP1252");
            } else {
                return null;
            }
        } catch (Exception ex) {
            log.error("error during enqueuing nas server request", ex);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteFile() {
        if (fileToSaveTo.exists() && fileToSaveTo.isFile()) {
            fileToSaveTo.delete();
        }
    }
}
