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
 *
 * @author srichter
 */
public final class AlkisSOAPWorkerService {

    private AlkisSOAPWorkerService() {
        throw new AssertionError();
    }
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisSOAPWorkerService.class);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final ThreadPoolExecutor SOAP_EXEC_SERVICE = new ThreadPoolExecutor(AVAILABLE_PROCESSORS, AVAILABLE_PROCESSORS,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static final void cancel(Runnable worker) {
        if (worker != null) {
            if (worker instanceof RunnableFuture) {
                ((RunnableFuture<?>) worker).cancel(true);
            }
            SOAP_EXEC_SERVICE.getQueue().remove(worker);
        }
    }

    public static final void execute(Runnable worker) {
        if (worker != null) {
            SOAP_EXEC_SERVICE.execute(worker);
        }
    }

    public static void clearWaitingQueue() {
        SOAP_EXEC_SERVICE.getQueue().clear();
    }
}
