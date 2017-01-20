/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ZaehlungwetterToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String beschreibung = (String)cidsBean.getProperty("beschreibung");
        if (beschreibung != null) {
            return beschreibung;
        } else {
            return "kein Wetter ausgew\u00E4hlt";
        }
    }
}
