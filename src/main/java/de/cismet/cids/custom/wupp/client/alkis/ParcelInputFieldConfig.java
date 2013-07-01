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
package de.cismet.cids.custom.wupp.client.alkis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.Serializable;

import java.util.HashMap;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@XmlRootElement
@JsonIgnoreProperties(value = {"delimiter1AsString", "delimiter2AsString"})
public class ParcelInputFieldConfig implements Serializable {
    public static final ParcelInputFieldConfig FallbackConfig = new ParcelInputFieldConfig('-', '/', 4, 3, 5, 4);

    //~ Instance fields --------------------------------------------------------

    private int maxLenDistrictNumberField;
    private int maxLenParcelNumberField;
    private int maxLenParcelNumeratorField;
    private int maxLenParcelDenominatorField;

    private char delimiter1;
    private char delimiter2;

    private HashMap<String, Integer> conversionMap;
    private HashMap<Integer, String> districtNamesMap;

    private ParcelInputFieldConfig(char delimiter1, char delimiter2, int lenDistrictField, int lenParcelNumberField, int lenParcelNumeratorField, int lenParcelDenominatorField) {
        maxLenDistrictNumberField = lenDistrictField;
        maxLenParcelNumberField = lenParcelNumberField;
        maxLenParcelNumeratorField = lenParcelNumeratorField;
        maxLenParcelDenominatorField = lenParcelDenominatorField;
        this.delimiter1 = delimiter1;
        this.delimiter2 = delimiter2;
        this.conversionMap = new HashMap<String, Integer>();
        this.districtNamesMap = new HashMap<Integer, String>();
    }
    
    public ParcelInputFieldConfig() {}

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenDistrictNumberField() {
        return maxLenDistrictNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenDistrictNumberField  DOCUMENT ME!
     */
    public void setMaxLenDistrictNumberField(final int maxLenDistrictNumberField) {
        this.maxLenDistrictNumberField = maxLenDistrictNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelNumberField() {
        return maxLenParcelNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelNumberField  DOCUMENT ME!
     */
    public void setMaxLenParcelNumberField(final int maxLenParcelNumberField) {
        this.maxLenParcelNumberField = maxLenParcelNumberField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelNumeratorField() {
        return maxLenParcelNumeratorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelNumeratorField  DOCUMENT ME!
     */
    public void setMaxLenParcelNumeratorField(final int maxLenParcelNumeratorField) {
        this.maxLenParcelNumeratorField = maxLenParcelNumeratorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getMaxLenParcelDenominatorField() {
        return maxLenParcelDenominatorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxLenParcelDenominatorField  DOCUMENT ME!
     */
    public void setMaxLenParcelDenominatorField(final int maxLenParcelDenominatorField) {
        this.maxLenParcelDenominatorField = maxLenParcelDenominatorField;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public char getDelimiter1() {
        return delimiter1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDelimiter1AsString() {
        return String.valueOf(delimiter1);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  delimiter1  DOCUMENT ME!
     */
    public void setDelimiter1(final char delimiter1) {
        this.delimiter1 = delimiter1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public char getDelimiter2() {
        return delimiter2;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDelimiter2AsString() {
        return String.valueOf(delimiter2);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  delimiter2  DOCUMENT ME!
     */
    public void setDelimiter2(final char delimiter2) {
        this.delimiter2 = delimiter2;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Integer> getConversionMap() {
        return conversionMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  conversionMap  DOCUMENT ME!
     */
    public void setConversionMap(final HashMap<String, Integer> conversionMap) {
        this.conversionMap = conversionMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Integer, String> getDistrictNamesMap() {
        return districtNamesMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  districtNames  DOCUMENT ME!
     */
    public void setDistrictNamesMap(final HashMap<Integer, String> districtNames) {
        this.districtNamesMap = districtNames;
    }
}
