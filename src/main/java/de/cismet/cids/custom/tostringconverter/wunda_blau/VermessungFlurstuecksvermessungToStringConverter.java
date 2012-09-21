/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungFlurstuecksvermessungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();

        final CidsBean flurstueck = (CidsBean)cidsBean.getProperty("flurstueck");
        if (flurstueck != null) {
            if (flurstueck.getProperty("gemarkung") != null) {
                final Object gemarkung = flurstueck.getProperty("gemarkung.name");

                if ((gemarkung instanceof String) && (((String)gemarkung).trim().length() > 0)) {
                    result.append(gemarkung);
                } else {
                    result.append(flurstueck.getProperty("gemarkung.id"));
                }
            } else {
                result.append("Unbekannte Gemarkung");
            }

            result.append("-");
            result.append(flurstueck.getProperty("flur"));
            result.append("-");
            result.append(flurstueck.getProperty("zaehler"));
            final Object nenner = flurstueck.getProperty("nenner");
            result.append('/');
            if (nenner != null) {
                result.append(nenner);
            } else {
                result.append('0');
            }
        }

        if (cidsBean.getProperty("veraenderungsart") != null) {
            result.append(" (");

            final Object vermessungsart = cidsBean.getProperty("veraenderungsart.name");
            if ((vermessungsart instanceof String) && (((String)vermessungsart).trim().length() > 0)) {
                result.append(vermessungsart);
            } else {
                result.append(cidsBean.getProperty("veraenderungsart.code"));
            }

            result.append(')');
        }

        return result.toString();
    }
}
