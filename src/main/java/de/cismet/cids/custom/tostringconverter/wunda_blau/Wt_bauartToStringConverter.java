/*
 * WTBauartToString.java
 *
 * Created on 31. August 2007, 14:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *de.cismet.cids.toStringConverter.WTBauartToString
 * @author hell
 */
public class Wt_bauartToStringConverter extends CustomToStringConverter{
     @CidsAttribute("Bezeichnung")
    public String bauart=null;
    /** Creates a new instance of WTBauartToString */
    public Wt_bauartToStringConverter() {
    }

    public String createString() {
        return bauart;
    }
    
}
