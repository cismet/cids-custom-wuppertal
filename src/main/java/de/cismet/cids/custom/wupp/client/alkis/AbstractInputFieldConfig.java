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
    private HashMap<String, Integer> conversionMap;
    private HashMap<Integer, String> districtNamesMap;

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
}
