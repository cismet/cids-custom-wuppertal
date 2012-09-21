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
public class VermessungFlurstueckKickerToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();

        if (cidsBean.getProperty("gemarkung") != null) {
            final Object gemarkung = cidsBean.getProperty("gemarkung.name");

            if ((gemarkung instanceof String) && (((String)gemarkung).trim().length() > 0)) {
                result.append(gemarkung);
            } else {
                result.append(cidsBean.getProperty("gemarkung.id"));
            }
        } else {
            result.append("Unbekannte Gemarkung");
        }

        result.append("-");
        result.append(cidsBean.getProperty("flur"));
        result.append("-");
        result.append(cidsBean.getProperty("zaehler"));
        final Object nenner = cidsBean.getProperty("nenner");
        result.append('/');
        if (nenner != null) {
            result.append(nenner);
        } else {
            result.append('0');
        }

        return result.toString();
    }
}
