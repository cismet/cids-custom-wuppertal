package de.cismet.cids.custom.wunda_blau.search;

import de.cismet.cids.custom.wunda_blau.search.server.MetaObjectNodesStadtbildSerieSearchStatement.Interval;
import java.util.ArrayList;
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

    Sb_StadtbildWindowSearch instance = new Sb_StadtbildWindowSearch();

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

    //############### eight basic tests
    @Test
    public void testFancyIntervall_normalInterval() {
        try {
            final Interval interval = instance.getIntervalForSearch("004711", "004713");
            assertEquals(new Interval("004711", "004713"), interval);
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_normalInterval_lastNumberHasSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("004711", "004713c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("004711");
            for (char ch = 'a'; ch <= 'z'; ch++) {
                expectedNumbers.add("004711" + ch);
            }
            expectedNumbers.add("004713");
            expectedNumbers.add("004713a");
            expectedNumbers.add("004713b");
            expectedNumbers.add("004713c");

            Interval expectedInterval = new Interval("004712", "004712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_normalInterval_FirstNumberHasSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("004711y", "004713");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("004711y");
            expectedNumbers.add("004711z");
            expectedNumbers.add("004713");

            Interval expectedInterval = new Interval("004712", "004712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_normalInterval_BothNumbersHaveSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("004711a", "004713c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            for (char ch = 'a'; ch <= 'z'; ch++) {
                expectedNumbers.add("004711" + ch);
            }
            expectedNumbers.add("004713");
            expectedNumbers.add("004713a");
            expectedNumbers.add("004713b");
            expectedNumbers.add("004713c");

            Interval expectedInterval = new Interval("004712", "004712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("N04711", "N04713");
            assertEquals(new Interval("N04711", "N04713"), interval);
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_lastNumberHasSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("N04711", "N04713c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711");
            for (char ch = 'a'; ch <= 'z'; ch++) {
                expectedNumbers.add("N04711" + ch);
            }
            expectedNumbers.add("N04713");
            expectedNumbers.add("N04713a");
            expectedNumbers.add("N04713b");
            expectedNumbers.add("N04713c");

            Interval expectedInterval = new Interval("N04712", "N04712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_FirstNumberHasSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("N04711y", "N04713");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04711y");
            expectedNumbers.add("N04711z");
            expectedNumbers.add("N04713");

            Interval expectedInterval = new Interval("N04712", "N04712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_BothNumbersHaveSuffixLetter() {
        try {
            final Interval interval = instance.getIntervalForSearch("N04711a", "N04713c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            for (char ch = 'a'; ch <= 'z'; ch++) {
                expectedNumbers.add("N04711" + ch);
            }
            expectedNumbers.add("N04713");
            expectedNumbers.add("N04713a");
            expectedNumbers.add("N04713b");
            expectedNumbers.add("N04713c");

            Interval expectedInterval = new Interval("N04712", "N04712", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    //############### advanced tests
    @Test
    public void testFancyIntervall_prefixLetter_sameNumberBase_FirstNumberHasSuffixLetter() {
        try {
            // check if alphanumerical in the beginning works
            final Interval interval1 = instance.getIntervalForSearch("N04713y", "N04713");
            final Interval interval2 = instance.getIntervalForSearch("N04713", "N04713y");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04713");
            for (char ch = 'a'; ch <= 'y'; ch++) {
                expectedNumbers.add("N04713" + ch);
            }

            assertEquals(interval1, new Interval(null, null, expectedNumbers));
            assertEquals(interval1, interval2);
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_BothNumbersHaveSuffixLetter_sameNumberBase_firstBigger() {
        try {
            // check if alphanumerical in the beginning works
            final Interval interval1 = instance.getIntervalForSearch("N02308c", "N02308b");
            final Interval interval2 = instance.getIntervalForSearch("N02308b", "N02308c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N02308b");
            expectedNumbers.add("N02308c");

            assertEquals(interval1, new Interval(null, null, expectedNumbers));
            assertEquals(interval1, interval2);
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_second_bigger_than_first_number() {
        try {
            // check if alphanumerical in the beginning works
            final Interval interval1 = instance.getIntervalForSearch("N04715", "N04713");
            final Interval interval2 = instance.getIntervalForSearch("N04713", "N04715");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("N04713");
            expectedNumbers.add("N04715");

            assertEquals(interval1, interval2);
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test(expected = Sb_StadtbildWindowSearch.NotAValidIntervalException.class)
    public void testFancyIntervall_invalid_no_prefix() throws Sb_StadtbildWindowSearch.NotAValidIntervalException {
        final Interval interval = instance.getIntervalForSearch("F04713", "N04714");
        fail("Should throw an exception.");
    }

    @Test
    public void testFancyIntervall_valid_no_prefix() {
        try {
            final Interval interval = instance.getIntervalForSearch("500000", "600000");
            assertEquals(interval, new Interval("500000", "600000"));
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_valid_same_input_simple() {
        try {
            final Interval interval = instance.getIntervalForSearch("500000", "500000");
            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("500000");
            assertEquals(interval, new Interval(null, null, expectedNumbers));
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_valid_same_input_complex() {
        try {
            final Interval interval = instance.getIntervalForSearch("500000a", "500000a");
            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("500000a");
            assertEquals(interval, new Interval(null, null, expectedNumbers));
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test(expected = Sb_StadtbildWindowSearch.NotAValidIntervalException.class)
    public void testFancyIntervall_invalid_different_length() throws Sb_StadtbildWindowSearch.NotAValidIntervalException {
        final Interval interval = instance.getIntervalForSearch("04713c", "047134");
        fail("Should throw an exception.");
    }

    @Test(expected = Sb_StadtbildWindowSearch.NotAValidIntervalException.class)
    public void testFancyIntervall_invalid_wildcard_percent() throws Sb_StadtbildWindowSearch.NotAValidIntervalException {
        final Interval interval = instance.getIntervalForSearch("%", "%");
        fail("Should throw an exception.");
    }

    @Test(expected = Sb_StadtbildWindowSearch.NotAValidIntervalException.class)
    public void testFancyIntervall_invalid_wildcard_underscore() throws Sb_StadtbildWindowSearch.NotAValidIntervalException {
        final Interval interval = instance.getIntervalForSearch("_", "_");
        fail("Should throw an exception.");
    }

    @Test(expected = Sb_StadtbildWindowSearch.NotAValidIntervalException.class)
    public void testFancyIntervall_invalid_not_expected_input() throws Sb_StadtbildWindowSearch.NotAValidIntervalException {
        final Interval interval = instance.getIntervalForSearch("N02308af", "N02309af");
        fail("Should throw an exception.");
    }

    @Test
    public void testFancyIntervall_normalInterval_FirstNumberHasSuffixLetter_biggerInterval() {
        try {
            final Interval interval = instance.getIntervalForSearch("004711y", "005000");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            expectedNumbers.add("004711y");
            expectedNumbers.add("004711z");
            expectedNumbers.add("005000");

            Interval expectedInterval = new Interval("004712", "004999", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

    @Test
    public void testFancyIntervall_prefixLetter_BothNumbersHaveSuffixLetter_biggerInterval() {
        try {
            final Interval interval = instance.getIntervalForSearch("N04711a", "N05000c");

            ArrayList<String> expectedNumbers = new ArrayList<String>();
            for (char ch = 'a'; ch <= 'z'; ch++) {
                expectedNumbers.add("N04711" + ch);
            }
            expectedNumbers.add("N05000");
            expectedNumbers.add("N05000a");
            expectedNumbers.add("N05000b");
            expectedNumbers.add("N05000c");

            Interval expectedInterval = new Interval("N04712", "N04999", expectedNumbers);
            assertEquals(expectedInterval.getIntervalStart(), interval.getIntervalStart());
            assertEquals(expectedInterval.getIntervalEnd(), interval.getIntervalEnd());
            assertArrayEquals(expectedInterval.getAdditionalExactMatches().toArray(), interval.getAdditionalExactMatches().toArray());
        } catch (Sb_StadtbildWindowSearch.NotAValidIntervalException ex) {
            Exceptions.printStackTrace(ex);
            fail("Should not throw an exception.");
        }
    }

}
