/*
 * CategoryToStringConverter.java
 *
 * Created on 6. August 2007, 11:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *de.cismet.cids.toStringConverter.ZaehlungsstandortToStringConverter
 * @author verkennis
 */
public class ZaehlungsstandortToStringConverter extends CustomToStringConverter{
    @CidsAttribute("Lage")
    public String lage=null;
     @CidsAttribute("Standpunkt")
    public Integer standpunkt=null;

    public String createString() {
        String s = null;
        if (standpunkt != null) {
            s = standpunkt.toString();
            switch (s.length())  {
                case 1: s = "00" + s ;break;
                case 2: s = "0" + s ;break;
            }}
       
        return s + " " + lage;
        
    }
}
