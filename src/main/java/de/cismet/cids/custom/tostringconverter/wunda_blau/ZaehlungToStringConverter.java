/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CategoryToStringConverter.java
 *
 * Created on 6. August 2007, 11:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import java.sql.Timestamp;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ZaehlungToStringConverter.
 *
 * @author   verkennis
 * @version  $Revision$, $Date$
 */
public class ZaehlungToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Datum")
    public Timestamp datum = null;
    @CidsAttribute("Anzahl")
    public Integer anzahl = null;
    @CidsAttribute("Ereignis")
    public String ereignis = null;
    @CidsAttribute("Wetter.Wetter")
    public String wetter = null;

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        if ((datum == null) | (anzahl == null)) {
            return "Hier muss noch etwas eingegeben werden";
        }
        return datum.toString() + " : " + anzahl.toString() + " : " + ereignis + " : " + wetter;
    }
}
