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
public class InputConfig implements Serializable{
    private int maxLenTxtArea;
    private int maxLenTxtLand;
    private int maxLenTxtLandParcelNumerator;
    private int maxLenTxtLandParcelDenominator;
    
    private char delimiter1;
    private char delimiter2;
    
    private HashMap<String, Integer> conversionMap;
    private HashMap<Integer, String> areaClearMap;

    public int getMaxLenTxtArea() {
        return maxLenTxtArea;
    }

    public void setMaxLenTxtArea(int MaxLenField1) {
        this.maxLenTxtArea = MaxLenField1;
    }

    public int getMaxLenTxtLand() {
        return maxLenTxtLand;
    }

    public void setMaxLenTxtLand(int MaxLenField2) {
        this.maxLenTxtLand = MaxLenField2;
    }

    public int getMaxLenTxtLandParcelNumerator() {
        return maxLenTxtLandParcelNumerator;
    }

    public void setMaxLenTxtLandParcelNumerator(int MaxLenField3) {
        this.maxLenTxtLandParcelNumerator = MaxLenField3;
    }

    public int getMaxLenTxtLandParcelDenominator() {
        return maxLenTxtLandParcelDenominator;
    }

    public void setMaxLenTxtLandParcelDenominator(int MaxLenField4) {
        this.maxLenTxtLandParcelDenominator = MaxLenField4;
    }

    public char getDelimiter1() {
        return delimiter1;
    }
    
    public String getDelimiter1String() {
        return String.valueOf(delimiter1);
    }

    public void setDelimiter1(char Delimiter1) {
        this.delimiter1 = Delimiter1;
    }

    public char getDelimiter2() {
        return delimiter2;
    }
    
    public String getDelimiter2String() {
        return String.valueOf(delimiter2);
    }

    public void setDelimiter2(char Delimiter2) {
        this.delimiter2 = Delimiter2;
    }

    public HashMap<String, Integer> getConversionMap() {
        return conversionMap;
    }

    public void setConversionMap(HashMap<String, Integer> Umsetzungstabelle) {
        this.conversionMap = Umsetzungstabelle;
    }

    public HashMap<Integer, String> getAreaClearMap() {
        return areaClearMap;
    }

    public void setAreaClearMap(HashMap<Integer, String> GemarkungKlartext) {
        this.areaClearMap = GemarkungKlartext;
    }
}
