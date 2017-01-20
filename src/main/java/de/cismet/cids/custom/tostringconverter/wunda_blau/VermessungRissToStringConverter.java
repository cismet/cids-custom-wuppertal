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
public class VermessungRissToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        final Object schluessel = cidsBean.getProperty("schluessel");
        final Object flur = cidsBean.getProperty("flur");
        final Object blatt = cidsBean.getProperty("blatt");

        if ((schluessel instanceof String) && (((String)schluessel).trim().length() > 0)) {
            result.append(schluessel);
        } else {
            result.append("unbekannter Schlüssel");
        }
        result.append(" - ");

        String gemarkung = "unbekannte Gemarkung";
        if ((cidsBean.getProperty("gemarkung") instanceof CidsBean)
                    && (cidsBean.getProperty("gemarkung.name") instanceof String)) {
            final String gemarkungFromBean = (String)cidsBean.getProperty("gemarkung.name");

            if (gemarkungFromBean.trim().length() > 0) {
                gemarkung = gemarkungFromBean;
            }
        }
        result.append(gemarkung);
        result.append(" - ");

        if ((flur instanceof String) && (((String)flur).trim().length() > 0)) {
            result.append(flur);
        } else {
            result.append("unbekannte Flur");
        }
        result.append(" - ");

        if ((blatt instanceof String) && (((String)blatt).trim().length() > 0)) {
            result.append(blatt);
        } else {
            result.append("unbekannte Blattnummer");
        }

        return result.toString();
    }
}
