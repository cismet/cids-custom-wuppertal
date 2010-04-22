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
 *de.cismet.cids.toStringConverter.WerbetraegerToStringConverter
 * @author hell
 */
public class WerbetraegerToStringConverter extends CustomToStringConverter{
    @CidsAttribute("Bezeichnung")
    public String bez=null;
//    @CidsAttribute("momentane Werbung")
    public String werb="";
    @CidsAttribute("Art.Bezeichnung")
    public String art="";

    public String createString() {
        String ret="";
        if (bez!=null) {
            ret=bez;
        }
        else {
            ret="keine Bezeichnung";
        }
        ret+="("+art+")";//, "+werb+")";
        return ret;
        
    }
}
