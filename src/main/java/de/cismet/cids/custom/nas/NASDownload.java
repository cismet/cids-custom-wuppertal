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

import java.io.FileOutputStream;
import java.io.IOException;

import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class NASDownload extends AbstractDownload {

    //~ Static fields/initializers ---------------------------------------------

    private static String SEVER_ACTION = "nasDataQuery";
    private static String EXTENSION = ".xml";
    private static String STANDARD_FILE_NAME = "nas-result";

    //~ Instance fields --------------------------------------------------------

    private NasDataQueryAction.PRODUCT_TEMPLATE template;
    private Geometry geometry;
    private String orderId;
    private transient byte[] content;
    private boolean omitSendingRequest = false;
    private String filename = null;

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
        this.title = "NAS Download";
        status = State.WAITING;
        this.directory = "";
        filename = orderId;
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
            final NasDataQueryAction.PRODUCT_TEMPLATE template,
            final Geometry g) {
        this.template = template;
        geometry = g;
        this.title = title;
        this.directory = directory;
        status = State.WAITING;
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
            final NasDataQueryAction.PRODUCT_TEMPLATE template,
            final Geometry g) {
        this.template = template;
        geometry = g;
        this.title = title;
        this.directory = directory;
        status = State.WAITING;
        this.filename = filename;
//        determineDestinationFile(filename, EXTENSION);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();
        if (!omitSendingRequest) {
            orderId = sendNasRequest();
            if (filename == null) {
                filename = orderId;
            }
        }

        status = State.WAITING;
        stateChanged();

        content = getNasResult();

        status = State.RUNNING;
        stateChanged();

        if ((content == null) || (content.length <= 0)) {
            log.info("Downloaded content seems to be empty..");

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }
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

        if (status == State.RUNNING) {
            status = State.COMPLETED;
            stateChanged();
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
        } catch (ConnectionException ex) {
            log.error("error during enqueuing nas server request", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private byte[] getNasResult() {
        final ServerActionParameter paramMethod = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.METHOD
                        .toString(),
                NasDataQueryAction.METHOD_TYPE.GET);

        final ServerActionParameter paramOrderId = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.ORDER_ID
                        .toString(),
                orderId);
        byte[] result = null;
        try {
            while (result == null) {
                result = (byte[])SessionManager.getProxy()
                            .executeTask(
                                    SEVER_ACTION,
                                    "WUNDA_BLAU",
                                    null,
                                    paramOrderId,
                                    paramMethod);
            }
        } catch (ConnectionException ex) {
            log.error("error during pulling nas result from server", ex);
        }
        return result;
    }
}
