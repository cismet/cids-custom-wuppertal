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

import de.cismet.cids.custom.nas.NASDownload;
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
    private static final String EXTENSION = ".tif";

    //~ Instance fields --------------------------------------------------------

    private String orderId;
    private ButlerProduct product;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

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
        determineDestinationFile(orderId, EXTENSION);
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

        final String requestId = sendRequest();
        if (requestId == null) {
            // log that something went terribly wrong...
        }

        /*
         * Phase 2 : poll the result
         */
        final ArrayList<byte[]> result = getResult(requestId);

        /*
         * Phase 3: save the files
         */
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSaveTo);
            out.write(result.get(0));
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
     * @param   requestId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ArrayList<byte[]> getResult(final String requestId) {
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
                System.out.println("Result for " + requestId + " is not finished, try later again");
                try {
                    Thread.sleep(2000);
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
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }
}
