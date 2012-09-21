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
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class NivellementPunktToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();

        result.append(cidsBean.getProperty("dgk_blattnummer").toString());

        final String laufendeNummer = cidsBean.getProperty("laufende_nummer").toString();
        if (laufendeNummer.trim().length() == 2) {
            result.append('0');
        } else if (laufendeNummer.trim().length() == 1) {
            result.append("00");
        }
        result.append(laufendeNummer);

        return result.toString();
    }
}
