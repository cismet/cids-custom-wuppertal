/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import java.sql.Timestamp;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   verkennis
 * @version  $Revision$, $Date$
 */
public class ZaehlungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Timestamp datum = (Timestamp)cidsBean.getProperty("datum");
        final Integer anzahl = (Integer)cidsBean.getProperty("anzahl");
        final String ereignis = (String)cidsBean.getProperty("ereignis");
        final String wetter = (String)cidsBean.getProperty("wetter.wetter");

        if ((datum == null) || (anzahl == null)) {
            return "Hier muss noch etwas eingegeben werden";
        }
        return datum.toString() + " : " + anzahl.toString() + " : " + ereignis + " : " + wetter;
    }
}
