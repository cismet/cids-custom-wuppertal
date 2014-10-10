package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.commons.concurrency.CismetExecutors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Gilles Baatz
 */
@Ignore
public class ObjectRendererUtilsTest {

    public ObjectRendererUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost("WUNDA_BLAU",
                "Administratoren",
                "admin", "kif");
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCheckActionTagInternalUse_internalUsage_once() {
        System.out.println("checkActionTag_internalUsage_once");
        String tagToCheck = "custom.stadtbilder.internalUse.internalUsage";
        boolean expResult = true;
        boolean result = ObjectRendererUtils.checkActionTag(tagToCheck);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkActionTag method, of class ObjectRendererUtils.
     */
    @Test
    public void testCheckActionTagInternalUse_externalUsage_once() {
        System.out.println("checkActionTag_externalUsage_once");
        String tagToCheck = "custom.stadtbilder.internalUse.externalUsage";
        boolean expResult = false;
        boolean result = ObjectRendererUtils.checkActionTag(tagToCheck);
        assertEquals(expResult, result);
    }

    @Test
    public void testCheckActionTagInternalUse_externalUsage_multi() {
        System.out.println("checkActionTag_externalUsage_multi");
        String tagToCheck = "custom.stadtbilder.internalUse.externalUsage";
        boolean expResult = false;
        for (int i = 0; i < 1000; i++) {
            boolean result = ObjectRendererUtils.checkActionTag(tagToCheck);
            assertEquals(expResult, result);
        }
    }
    
    @Test
    public void testCheckActionTagInternalUse_externalUsage_multiThread() throws InterruptedException {
        System.out.println("checkActionTag_externalUsage_internalUsage_multiThread");
        final String tagToCheckexternalUsage = "custom.stadtbilder.internalUse.externalUsage";
        final boolean expResultExternal = false;
        final AtomicBoolean externalFailed = new AtomicBoolean(false);
        ExecutorService executorService = CismetExecutors.newCachedThreadPool();
        final int maxIndex = 500;
        final Semaphore externalSemaphore = new Semaphore(1);
        externalSemaphore.acquire();

        for (int i = 0; i <= maxIndex; i++) {
            final int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    boolean result = ObjectRendererUtils.checkActionTag(tagToCheckexternalUsage);
                    if (expResultExternal != result) {
                        System.out.println("fetch externalUsage failed");
                        externalFailed.set(true);
                    }
                    assertEquals(expResultExternal, result);
                    if (index == maxIndex) {
                        externalSemaphore.release();
                    }
                }
            });
        }

        externalSemaphore.acquire();
        assertEquals(false, externalFailed.get());
    }

    @Test
    public void testCheckActionTagInternalUse_externalUsage_internalUsage_multiThread() throws InterruptedException {
        System.out.println("checkActionTag_externalUsage_internalUsage_multiThread");
        final String tagToCheckexternalUsage = "custom.stadtbilder.internalUse.externalUsage";
        final String tagToCheckinternalUsage = "custom.stadtbilder.internalUse.internalUsage";
        final boolean expResultExternal = false;
        final boolean expResultInternal = true;
        final AtomicBoolean externalFailed = new AtomicBoolean(false);
        final AtomicBoolean internalFailed = new AtomicBoolean(false);
        ExecutorService executorService = CismetExecutors.newFixedThreadPool(50);
        final int maxIndex = 500;
        final Semaphore externalSemaphore = new Semaphore(1);
        externalSemaphore.acquire();
        final Semaphore internalSemaphore = new Semaphore(1);
        internalSemaphore.acquire();

        final AtomicInteger wrongResultExternal = new AtomicInteger(0);
        final AtomicInteger wrongResultInternal = new AtomicInteger(0);

        for (int i = 0; i <= maxIndex; i++) {
            final int index = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    boolean result = ObjectRendererUtils.checkActionTag(tagToCheckexternalUsage);
                    if (expResultExternal != result) {
                        System.out.println("fetch externalUsage failed");
                        wrongResultExternal.incrementAndGet();
                        externalFailed.set(true);
                    }
                    assertEquals(expResultExternal, result);
                    if (index == maxIndex) {
                        externalSemaphore.release();
                    }
                }
            });
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    boolean result = ObjectRendererUtils.checkActionTag(tagToCheckinternalUsage);
                    if (expResultInternal != result) {
                        System.out.println("fetch internalUsage failed");
                        wrongResultInternal.incrementAndGet();
                        internalFailed.set(true);
                    }
                    assertEquals(expResultInternal, result);
                    if (index == maxIndex) {
                        internalSemaphore.release();
                    }
                }
            });
        }

        boolean internalOrExternalFailed = false;
        externalSemaphore.acquire();
        System.out.println("Wrong external values: " + wrongResultExternal.get());
        if (externalFailed.get()) {
            internalOrExternalFailed = true;
        }
        internalSemaphore.acquire();
        System.out.println("Wrong internal values: " + wrongResultInternal.get());
        if (internalFailed.get()) {
            internalOrExternalFailed = true;
        }

        assertEquals(false, internalOrExternalFailed);
    }

}
