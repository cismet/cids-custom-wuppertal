
/*
 * ListToStringConverter.java
 *
 * Created on 6. Mai 2008, 10:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *de.cismet.cids.toStringConverter.ListToStringConverter
 * @author verkennis
 */
public class Wbf_miet_typToStringConverter extends CustomToStringConverter{
     @CidsAttribute("Value")
    public String value=null;
    /** Creates a new instance of ListToStringConverter */
    public Wbf_miet_typToStringConverter() {
    }

    public String createString() {
        return value;
    }
    
}