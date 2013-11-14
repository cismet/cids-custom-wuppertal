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

import java.io.Serializable;

import java.util.HashMap;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class AbstractInputFieldConfig implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private int maxLenDistrictNumberField;
    private char delimiter1;
    private int defaultAbbreviationLength;
    private HashMap<String, Integer> conversionMap;
    private HashMap<Integer, String> districtNamesMap;
    private HashMap<Character, Integer> alternativeAbbreviationLength;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractInputFieldConfig object.
     */
    public AbstractInputFieldConfig() {
    }

    /**
     * Creates a new AbstractInputFieldConfig object.
     *
     * @param  maxLenDistrictNumberField  DOCUMENT ME!
     * @param  delimiter1                 DOCUMENT ME!
     */
    public AbstractInputFieldConfig(final int maxLenDistrictNumberField, final char delimiter1) {
        this.maxLenDistrictNumberField = maxLenDistrictNumberField;
        this.delimiter1 = delimiter1;
        this.conversionMap = new HashMap<String, Integer>();
        this.districtNamesMap = new HashMap<Integer, String>();
        this.alternativeAbbreviationLength = new HashMap<Character, Integer>();
    }

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
     * the length after which a district abbreviation is automatically resolved.
     *
     * @return  DOCUMENT ME!
     */
    public int getDefaultAbbreviationLength() {
        return defaultAbbreviationLength;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  defaultAbbreviationLength  DOCUMENT ME!
     */
    public void setDefaultAbbreviationLength(final int defaultAbbreviationLength) {
        this.defaultAbbreviationLength = defaultAbbreviationLength;
    }

    /**
     * For some districts, which begins with the same characters, it is not clear how their abbreviation can be
     * resolved. Therefor their abbreviations need a longer length than that given in defaultAbbreviationMaxLength. An
     * example for this are the districts Gennebeck (abbr: g, ge) and Gevelsberg (abbr: gev). To resolve Gevelsberg the
     * abbreviation length needs to be 3. Whereas it is normally set to 2. Thus an exception for the character 'g' is
     * needed whose AbbreviationLength needs to be set to 3.
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<Character, Integer> getAlternativeAbbreviationLength() {
        return alternativeAbbreviationLength;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  alternativeAbbreviationLength  DOCUMENT ME!
     */
    public void setAlternativeAbbreviationLength(final HashMap<Character, Integer> alternativeAbbreviationLength) {
        this.alternativeAbbreviationLength = alternativeAbbreviationLength;
    }
}
