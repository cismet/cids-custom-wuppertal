/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wupp.client.alkis;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author mroncoroni
 */
public class ParcelInputFieldConfig implements Serializable{
    private int maxLenDistrictNumberField;
    private int maxLenParcelNumberField;
    private int maxLenParcelNumeratorField;
    private int maxLenParcelDenominatorField;
    
    private char delimiter1;
    private char delimiter2;
    
    private HashMap<String, Integer> conversionMap;
    private HashMap<Integer, String> districtNamesMap;

    public int getMaxLenDistrictNumberField() {
        return maxLenDistrictNumberField;
    }

    public void setMaxLenDistrictNumberField(int maxLenDistrictNumberField) {
        this.maxLenDistrictNumberField = maxLenDistrictNumberField;
    }

    public int getMaxLenParcelNumberField() {
        return maxLenParcelNumberField;
    }

    public void setMaxLenParcelNumberField(int maxLenParcelNumberField) {
        this.maxLenParcelNumberField = maxLenParcelNumberField;
    }

    public int getMaxLenParcelNumeratorField() {
        return maxLenParcelNumeratorField;
    }

    public void setMaxLenParcelNumeratorField(int maxLenParcelNumeratorField) {
        this.maxLenParcelNumeratorField = maxLenParcelNumeratorField;
    }

    public int getMaxLenParcelDenominatorField() {
        return maxLenParcelDenominatorField;
    }

    public void setMaxLenParcelDenominatorField(int maxLenParcelDenominatorField) {
        this.maxLenParcelDenominatorField = maxLenParcelDenominatorField;
    }

    public char getDelimiter1() {
        return delimiter1;
    }
    
    public String getDelimiter1AsString() {
        return String.valueOf(delimiter1);
    }

    public void setDelimiter1(char delimiter1) {
        this.delimiter1 = delimiter1;
    }

    public char getDelimiter2() {
        return delimiter2;
    }
    
    public String getDelimiter2AsString() {
        return String.valueOf(delimiter2);
    }

    public void setDelimiter2(char delimiter2) {
        this.delimiter2 = delimiter2;
    }

    public HashMap<String, Integer> getConversionMap() {
        return conversionMap;
    }

    public void setConversionMap(HashMap<String, Integer> conversionMap) {
        this.conversionMap = conversionMap;
    }

    public HashMap<Integer, String> getDistrictNamesMap() {
        return districtNamesMap;
    }

    public void setDistrictNamesMap(HashMap<Integer, String> districtNames) {
        this.districtNamesMap = districtNames;
    }
}
