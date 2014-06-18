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
 * @version  $Revision$, $Date$
 */
public class Fnp_aenderungenToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String verfahrensnummer = (String)cidsBean.getProperty("Verfahrensnummer");
        if (verfahrensnummer != null) {
            return verfahrensnummer;
        } else {
            return "";
        }
    }
}
