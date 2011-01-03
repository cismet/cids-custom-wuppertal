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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class for SOAP call scheduling and load balancing.
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class AlkisSOAPWorkerService {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisSOAPWorkerService.class);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    // leave one processor for other things on a multicore machine
    private static final int USED_PROCESSORS = Math.max(AVAILABLE_PROCESSORS - 1, 1);
    private static final ThreadPoolExecutor SOAP_EXEC_SERVICE = new ThreadPoolExecutor(
            USED_PROCESSORS,
            USED_PROCESSORS,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisSOAPWorkerService object.
     *
     * @throws  AssertionError  DOCUMENT ME!
     */
    private AlkisSOAPWorkerService() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  worker  DOCUMENT ME!
     */
    public static void cancel(final Runnable worker) {
        if (worker != null) {
            if (worker instanceof RunnableFuture) {
                ((RunnableFuture<?>)worker).cancel(true);
            }
            SOAP_EXEC_SERVICE.getQueue().remove(worker);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  worker  DOCUMENT ME!
     */
    public static void execute(final Runnable worker) {
        if (worker != null) {
            SOAP_EXEC_SERVICE.execute(worker);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public static void clearWaitingQueue() {
        SOAP_EXEC_SERVICE.getQueue().clear();
    }
}
