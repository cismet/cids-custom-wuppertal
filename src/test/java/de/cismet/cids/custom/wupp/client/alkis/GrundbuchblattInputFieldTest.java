/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.wupp.client.alkis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mroncoroni
 */
public class GrundbuchblattInputFieldTest {

    private GrundbuchblattInputField field;
    private GrundbuchblattInputFieldConfig config;

    public GrundbuchblattInputFieldTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        try {
            config =
                new ObjectMapper().readValue(GrundbuchblattInputFieldTest.class.getResourceAsStream(
                        "/de/cismet/cids/custom/wunda_blau/res/alkis/GrundbuchblattInputFieldConfig.json"),
                    GrundbuchblattInputFieldConfig.class);
            System.out.println(config.getDelimiter1AsString());
        } catch (IOException ex) {
            config = GrundbuchblattInputFieldConfig.FallbackConfig;
        }

        field = new GrundbuchblattInputField(config);
    }

    @After
    public void tearDown() {
        config = null;
        field = null;
    }

    /**
     * Test of setDistrictNumber method, of class ParcelInputField.
     */
    @Test
    public void testSetDistrictNumber() {
        System.out.println("setDistrictNumber");
        String districtNumber = "3001";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("053001", instance.getDistrictNumber());
    }
    
    @Test
    public void testSetDistrictNumberSpecialCharacter() {
        System.out.println("setDistrictNumberSpecialCharacter");
        String districtNumber = "n\u00E4";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("053487", instance.getDistrictNumber());
    }

    @Test
    public void testSetDistrictNumberShort() {
        System.out.println("setDistrictNumberShort");
        String districtNumber = "30";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals(null, instance.getDistrictNumber());
    }

    @Test
    public void testSetDistrictNumberShort2() {
        System.out.println("setDistrictNumberShort2");
        String districtNumber = "ba";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("053001", instance.getDistrictNumber());
    }
    
    @Test
    public void testSetDistrictNumberShort_samePrefix2Chars() {
        System.out.println("testSetDistrictNumberShort_samePrefix2Chars");
        String districtNumber = "GE";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("051329", instance.getDistrictNumber());
    }
    
    @Test
    public void testSetDistrictNumberShort_samePrefix3Chars() {
        System.out.println("testSetDistrictNumberShort_samePrefix3Chars");
        String districtNumber = "gev";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("051310", instance.getDistrictNumber());
    }

    @Test
    public void testSetDistrictNumberLong() {
        System.out.println("setDistrictNumberLong");
        String districtNumber = "30017435";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals("053001", instance.getDistrictNumber());
    }

    @Test
    public void testSetDistrictNumberWrong() {
        System.out.println("setDistrictNumberWrong");
        String districtNumber = "sadfh";
        GrundbuchblattInputField instance = field;
        instance.setDistrictNumberInTxtDistrict(districtNumber);
        assertEquals(null, instance.getDistrictNumber());
    }
    
     /**
     * Test of setBuchungsblattnummer method, of class GrundbuchblattInputField.
     */
    @Test
    public void testSetBuchungsblattnummer() {
        System.out.println("setParcelDenominator");
        String buchungsblattnummer = "0002";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("000002", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerShort() {
        System.out.println("setParcelDenominatorShort");
        String buchungsblattnummer = "2";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("000002", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerLong() {
        System.out.println("setParcelDenominatorLong");
        String buchungsblattnummer = "0002000A";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerLettersNoNumbers() {
        System.out.println("testSetBuchungsblattnummerLettersNoNumbers");
        String buchungsblattnummer = "abced";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("000000abced", instance.getBuchungsblattnummer());
    }
    
    @Test
    public void testSetBuchungsblattnummerLetters2Numbers() {
        System.out.println("testSetBuchungsblattnummerLetters2Numbers");
        String buchungsblattnummer = "12ab";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("000012ab", instance.getBuchungsblattnummer());
    }

//    /**
//     * Test of setCurrentParcel method, of class ParcelInputField.
//     */
//    @Test
//    public void testSetCurrentParcel() {
//        System.out.println("setCurrentParcel");
//        String currentParcel = "053135-002-00004/0003";
//        ParcelInputField instance = field;
//        instance.setCurrentParcel(currentParcel);
//        assertEquals(currentParcel, instance.getCurrentParcel());
//    }
//
//    @Test
//    public void testSetCurrentParcelOverflow() {
//        System.out.println("setCurrentParcelOverflow");
//        String currentParcel = "3135-002-00004/00030";
//        ParcelInputField instance = field;
//        instance.setCurrentParcel(currentParcel);
//        assertEquals("053135-002-00004/0003", instance.getCurrentParcel());
//    }
//
//    @Test
//    public void testSetCurrentParcelDelimiterAtEnd() {
//        System.out.println("setCurrentParcelDelimiterAtEnd");
//        String currentParcel = "3135-002-00004/00030-";
//        ParcelInputField instance = field;
//        instance.setCurrentParcel(currentParcel);
//        assertEquals("053135-002-00004/0003", instance.getCurrentParcel());
//    }
//
//    @Test
//    public void testSetCurrentParcelShort() {
//        System.out.println("setCurrentParcelShort");
//        String currentParcel = "3135-002-04";
//        ParcelInputField instance = field;
//        instance.setCurrentParcel(currentParcel);
//        assertEquals("053135-002-00004", instance.getCurrentParcel());
//    }
//
//    @Test
//    public void testSetCurrentParcelShort2() {
//        System.out.println("setCurrentParcelShort2");
//        String currentParcel = "ba-1-3";
//        ParcelInputField instance = field;
//        instance.setCurrentParcel(currentParcel);
//        assertEquals("053001-001-00003", instance.getCurrentParcel());
//    }
//
//    /**
//     * Test of setParcelDenominator method, of class ParcelInputField.
//     */
//    @Test
//    public void testSetParcelDenominator() {
//        System.out.println("setParcelDenominator");
//        String parcelDenominator = "0002";
//        ParcelInputField instance = field;
//        instance.setParcelDenominator(parcelDenominator);
//        assertEquals("0002", instance.getParcelDenominator());
//    }
//
//    @Test
//    public void testSetParcelDenominatorShort() {
//        System.out.println("setParcelDenominatorShort");
//        String parcelDenominator = "2";
//        ParcelInputField instance = field;
//        instance.setParcelDenominator(parcelDenominator);
//        assertEquals("0002", instance.getParcelDenominator());
//    }
//
//    @Test
//    public void testSetParcelDenominatorLong() {
//        System.out.println("setParcelDenominatorLong");
//        String parcelDenominator = "00020005";
//        ParcelInputField instance = field;
//        instance.setParcelDenominator(parcelDenominator);
//        assertEquals("0002", instance.getParcelDenominator());
//    }
//
//    @Test
//    public void testSetParcelDenominatorWrong() {
//        System.out.println("setParcelDenominatorWrong");
//        String parcelDenominator = "abced";
//        ParcelInputField instance = field;
//        instance.setParcelDenominator(parcelDenominator);
//        assertEquals("", instance.getParcelDenominator());
//    }
//
//    /**
//     * Test of setParcelNumber method, of class ParcelInputField.
//     */
//    @Test
//    public void testSetParcelNumber() {
//        System.out.println("setParcelNumber");
//        String parcelNumber = "001";
//        ParcelInputField instance = field;
//        instance.setParcelNumber(parcelNumber);
//        assertEquals(parcelNumber, instance.getParcelNumber());
//    }
//
//    @Test
//    public void testSetParcelNumberShort() {
//        System.out.println("setParcelNumberShort");
//        String parcelNumber = "1";
//        ParcelInputField instance = field;
//        instance.setParcelNumber(parcelNumber);
//        assertEquals("001", instance.getParcelNumber());
//    }
//
//    @Test
//    public void testSetParcelNumberShortLong() {
//        System.out.println("setParcelNumberLong");
//        String parcelNumber = "10002";
//        ParcelInputField instance = field;
//        instance.setParcelNumber(parcelNumber);
//        assertEquals("100", instance.getParcelNumber());
//    }
//
//    @Test
//    public void testSetParcelNumberShortWrong() {
//        System.out.println("setParcelNumberWrong");
//        String parcelNumber = "abc";
//        ParcelInputField instance = field;
//        instance.setParcelNumber(parcelNumber);
//        assertEquals("000", instance.getParcelNumber());
//    }
//
//    /**
//     * Test of setParcelNumerator method, of class ParcelInputField.
//     */
//    @Test
//    public void testSetParcelNumerator() {
//        System.out.println("setParcelNumerator");
//        String parcelNumerator = "00003";
//        ParcelInputField instance = field;
//        instance.setParcelNumerator(parcelNumerator);
//        assertEquals(parcelNumerator, instance.getParcelNumerator());
//    }
//
//    @Test
//    public void testSetParcelNumeratorShort() {
//        System.out.println("setParcelNumeratorShort");
//        String parcelNumerator = "3";
//        ParcelInputField instance = field;
//        instance.setParcelNumerator(parcelNumerator);
//        assertEquals("00003", instance.getParcelNumerator());
//    }
//
//    @Test
//    public void testSetParcelNumeratorLong() {
//        System.out.println("setParcelNumeratorLong");
//        String parcelNumerator = "00003674";
//        ParcelInputField instance = field;
//        instance.setParcelNumerator(parcelNumerator);
//        assertEquals("00003", instance.getParcelNumerator());
//    }
//
//    @Test
//    public void testSetParcelNumeratorWrong() {
//        System.out.println("setParcelNumeratorWrong");
//        String parcelNumerator = "abdvr";
//        ParcelInputField instance = field;
//        instance.setParcelNumerator(parcelNumerator);
//        assertEquals("00000", instance.getParcelNumerator());
//    }
}