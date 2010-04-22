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
import java.sql.Timestamp;

/**
 *de.cismet.cids.toStringConverter.ZaehlungToStringConverter
 * @author verkennis
 */
public class ZaehlungToStringConverter extends CustomToStringConverter{
    @CidsAttribute("Datum")
    public  Timestamp datum = null;
    @CidsAttribute("Anzahl")
    public  Integer anzahl = null;
    @CidsAttribute("Ereignis")
    public  String ereignis = null;
    @CidsAttribute("Wetter.Wetter")
    public  String wetter = null;

    public String createString() {

        if ( datum == null | anzahl == null ) return "Hier muss noch etwas eingegeben werden";
        return datum.toString() + " : " + anzahl.toString() + " : "  + ereignis+ " : "  + wetter;
        
    }
}
