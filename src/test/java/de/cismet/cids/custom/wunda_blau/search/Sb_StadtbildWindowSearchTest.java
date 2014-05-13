package de.cismet.cids.custom.wunda_blau.search;

import com.vividsolutions.jts.geom.Geometry;
import de.cismet.cids.server.search.MetaObjectNodeServerSearch;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Gilles Baatz
 */
public class Sb_StadtbildWindowSearchTest {

    public Sb_StadtbildWindowSearchTest() {
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

    @Test
    public void testFancyIntervall_normalIntervall() {
        Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();
        try {
            final Object[] resultArray = instance.setFancyIntervalInSearch("004711", "004713");
            assertEquals(false, resultArray[0]);

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("004711");
            expectedNumbers.add("004712");
            expectedNumbers.add("004713");
            assertEquals(expectedNumbers, resultArray[1]);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter() {
        Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();
        try {
            final Object[] resultArray = instance.setFancyIntervalInSearch("N04711", "N04713");
            assertEquals(false, resultArray[0]);

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711");
            expectedNumbers.add("N04712");
            expectedNumbers.add("N04713");
            assertEquals(expectedNumbers, resultArray[1]);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_lastNumberHasSuffixLetter() {
        Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();
        try {
            final Object[] resultArray = instance.setFancyIntervalInSearch("N04711", "N04713c");
            assertEquals(true, resultArray[0]);

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711");
            expectedNumbers.add("N04711a");
            expectedNumbers.add("N04711b");
            expectedNumbers.add("N04711c");
            expectedNumbers.add("N04712");
            expectedNumbers.add("N04712a");
            expectedNumbers.add("N04712b");
            expectedNumbers.add("N04712c");
            expectedNumbers.add("N04713");
            expectedNumbers.add("N04713a");
            expectedNumbers.add("N04713b");
            expectedNumbers.add("N04713c");
            assertEquals(expectedNumbers, resultArray[1]);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_FirstNumberHasSuffixLetter() {
        Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();
        try {
            final Object[] resultArray = instance.setFancyIntervalInSearch("N04711y", "N04713");
            assertEquals(true, resultArray[0]);

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711y");
            expectedNumbers.add("N04711z");
            expectedNumbers.add("N04712");
            expectedNumbers.add("N04712y");
            expectedNumbers.add("N04712z");
            expectedNumbers.add("N04713");
            assertEquals(expectedNumbers, resultArray[1]);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_BothNumbersHaveSuffixLetter() {
        Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();
        try {
            final Object[] resultArray = instance.setFancyIntervalInSearch("N04711a", "N04713c");
            assertEquals(true, resultArray[0]);

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711a");
            expectedNumbers.add("N04711b");
            expectedNumbers.add("N04711c");
            expectedNumbers.add("N04712a");
            expectedNumbers.add("N04712b");
            expectedNumbers.add("N04712c");
            expectedNumbers.add("N04713a");
            expectedNumbers.add("N04713b");
            expectedNumbers.add("N04713c");
            assertEquals(expectedNumbers, resultArray[1]);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
