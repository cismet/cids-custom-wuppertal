/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TestRule;

/**
 *
 * @author Gilles Baatz
 */
public class VermessungRissReportScriptletTest {

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    public VermessungRissReportScriptletTest() {
    }

    @BeforeClass
    public static void setUpClass() {
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

    /**
     * Test of isImageAvailable method, of class VermessungRissReportScriptlet.
     */
    @Test
    public void testIsImageAvailable_Vermessungsrisse() {
        System.out.println("testIsImageAvailable_Vermessungsrisse");
        String host = "http://s102x003/Vermessungsrisse/{1,number,0000}/VR_{0}-{1,number,0000}-{2}-{3,number,00000000}";
        String schluessel = "508";
        Integer gemarkung = 3001;
        String flur = "005";
        String blatt = "750";
        Boolean expResult = true;
        Boolean result = VermessungRissReportScriptlet.isImageAvailable(host, schluessel, gemarkung, flur, blatt);
        assertEquals(expResult, result);
    }

    /**
     * Test of isImageAvailable_head method, of class VermessungRissReportScriptlet.
     */
    @Test
    public void testIsImageAvailable_head_Vermessungsrisse() {
        System.out.println("testIsImageAvailable_head_Vermessungsrisse");
        String host = "http://s102x003/Vermessungsrisse/{1,number,0000}/VR_{0}-{1,number,0000}-{2}-{3,number,00000000}";
        String schluessel = "508";
        Integer gemarkung = 3001;
        String flur = "005";
        String blatt = "750";
        Boolean expResult = true;
        Boolean result = VermessungRissReportScriptlet.isImageAvailable_head(host, schluessel, gemarkung, flur, blatt);
        assertEquals(expResult, result);
    }

    /**
     * Test of isImageAvailable method, of class VermessungRissReportScriptlet.
     */
    @Test
    public void testIsImageAvailable_Grenzniederschriften() {
        System.out.println("testIsImageAvailable_Grenzniederschriften");
        String host = "http://s102x003/Grenzniederschriften/{1,number,0000}/GN_{0}-{1,number,0000}-{2}-{3,number,00000000}";
        String schluessel = "508";
        Integer gemarkung = 3001;
        String flur = "005";
        String blatt = "752";
        Boolean expResult = true;
        Boolean result = VermessungRissReportScriptlet.isImageAvailable(host, schluessel, gemarkung, flur, blatt);
        assertEquals(expResult, result);
    }

    /**
     * Test of isImageAvailable_head method, of class VermessungRissReportScriptlet.
     */
    @Test
    public void testIsImageAvailable_head_Grenzniederschriften() {
        System.out.println("testIsImageAvailable_head_Grenzniederschriften");
        String host = "http://s102x003/Grenzniederschriften/{1,number,0000}/GN_{0}-{1,number,0000}-{2}-{3,number,00000000}";
        String schluessel = "508";
        Integer gemarkung = 3001;
        String flur = "005";
        String blatt = "752";
        Boolean expResult = true;
        Boolean result = VermessungRissReportScriptlet.isImageAvailable_head(host, schluessel, gemarkung, flur, blatt);
        assertEquals(expResult, result);
    }

    @BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
    @Test
    public void directCompare() {
        System.out.println("directCompare");
        String host_verm = "http://s102x003/Vermessungsrisse/{1,number,0000}/VR_{0}-{1,number,0000}-{2}-{3,number,00000000}";
        String host_grenz = "http://s102x003/Grenzniederschriften/{1,number,0000}/GN_{0}-{1,number,0000}-{2}-{3,number,00000000}";

        ArrayList<ConnectionTuple> connectionTuples = new ArrayList<ConnectionTuple>();
        connectionTuples.add(new ConnectionTuple("508", 3001, "005", "750"));
        connectionTuples.add(new ConnectionTuple("502", 3485, "000", "40"));
        connectionTuples.add(new ConnectionTuple("504", 3267, "000", "9"));
        connectionTuples.add(new ConnectionTuple("506", 3277, "000", "17"));
        connectionTuples.add(new ConnectionTuple("506", 3277, "000", "1"));
        connectionTuples.add(new ConnectionTuple("600", 3279, "008", "1735"));
        connectionTuples.add(new ConnectionTuple("600", 3279, "008", "1820"));
        connectionTuples.add(new ConnectionTuple("600", 3279, "008", "1836"));
        connectionTuples.add(new ConnectionTuple("600", 3279, "008", "1899"));
        connectionTuples.add(new ConnectionTuple("508", 3135, "042", "41"));
        connectionTuples.add(new ConnectionTuple("508", 3135, "042", "45"));
        connectionTuples.add(new ConnectionTuple("508", 3135, "042", "46"));

        Boolean result = true;
        for (ConnectionTuple c : connectionTuples) {

            boolean normal_verm = VermessungRissReportScriptlet.isImageAvailable(host_verm, c.schluessel, c.gemarkung, c.flur, c.blatt);
            boolean head_verm = VermessungRissReportScriptlet.isImageAvailable_head(host_verm, c.schluessel, c.gemarkung, c.flur, c.blatt);

            boolean normal_grenz = VermessungRissReportScriptlet.isImageAvailable(host_grenz, c.schluessel, c.gemarkung, c.flur, c.blatt);
            boolean head_grenz = VermessungRissReportScriptlet.isImageAvailable_head(host_grenz, c.schluessel, c.gemarkung, c.flur, c.blatt);

            if (normal_verm != head_verm || normal_grenz != head_grenz) {
                result = false;
                break;
            }
        }
        assertTrue(result);
    }

    /**
     * Test of rotate method, of class VermessungRissReportScriptlet.
     */
    public void testRotate() {
        System.out.println("rotate");
        BufferedImage imageToRotate = null;
        BufferedImage expResult = null;
        BufferedImage result = VermessungRissReportScriptlet.rotate(imageToRotate);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    private class ConnectionTuple {

        String schluessel;
        Integer gemarkung;
        String flur;
        String blatt;

        public ConnectionTuple(String schluessel, Integer gemarkung, String flur, String blatt) {
            this.schluessel = schluessel;
            this.gemarkung = gemarkung;
            this.flur = flur;
            this.blatt = blatt;
        }
    }
}