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
        assertEquals("0000002", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerShort() {
        System.out.println("setParcelDenominatorShort");
        String buchungsblattnummer = "2";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("0000002", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerLong() {
        System.out.println("setParcelDenominatorLong");
        String buchungsblattnummer = "0002000A";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("0002000A", instance.getBuchungsblattnummer());
    }

    @Test
    public void testSetBuchungsblattnummerLettersNoNumbers() {
        System.out.println("testSetBuchungsblattnummerLettersNoNumbers");
        String buchungsblattnummer = "abced";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("0000000abced", instance.getBuchungsblattnummer());
    }
    
    @Test
    public void testSetBuchungsblattnummerLetters2Numbers() {
        System.out.println("testSetBuchungsblattnummerLetters2Numbers");
        String buchungsblattnummer = "12ab";
        GrundbuchblattInputField instance = field;
        instance.setBuchungsblattnummerInTxtBuchungsblattnummer(buchungsblattnummer);
        assertEquals("0000012ab", instance.getBuchungsblattnummer());
    }

    /**
     * Test of setCurrentParcel method, of class ParcelInputField.
     */
    @Test
    public void testSetCurrentGrundbuchblattnummer() {
        System.out.println("testSetCurrentGrundbuchblattnummer");
        String grundbuchblattnummer = "3001-000A";
        GrundbuchblattInputField instance = field;
        instance.setGrundbuchblattNummerForTest(grundbuchblattnummer);
        assertEquals("053001-0000000A", instance.getGrundbuchblattnummer());
    }
    
    @Test
    public void testSetCurrentGrundbuchblattnummerLong() {
        System.out.println("testSetCurrentGrundbuchblattnummerLong");
        String grundbuchblattnummer = "3001-00000000000A";
        GrundbuchblattInputField instance = field;
        instance.setGrundbuchblattNummerForTest(grundbuchblattnummer);
        assertEquals("053001-00000000000A", instance.getGrundbuchblattnummer());
    }
    
    @Test    
    public void testSetCurrentGrundbuchblattnummerWildcards() {
        System.out.println("testSetCurrentGrundbuchblattnummerLong");
        String grundbuchblattnummer = "3001-00000000000A%";
        GrundbuchblattInputField instance = field;
        instance.setGrundbuchblattNummerForTest(grundbuchblattnummer);
        assertEquals("053001-00000000000A%", instance.getGrundbuchblattnummer());
    }

    @Test
    public void testSetCurrentGrundbuchblattnummerBothTooLong() {
        System.out.println("testSetCurrentGrundbuchblattnummerLong");
        String grundbuchblattnummer = "300100000-50000031A";
        GrundbuchblattInputField instance = field;
        instance.setGrundbuchblattNummerForTest(grundbuchblattnummer);
        assertEquals("053001-50000031A", instance.getGrundbuchblattnummer());
    }
}