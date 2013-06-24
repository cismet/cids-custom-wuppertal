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
        final CidsBean tmp_lp = (CidsBean)cidsBean.getProperty("tmp_lp_orig");
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
        } else if (tmp_lp != null) {
            if (tmp_lp.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("flurstueck")) {
                if (tmp_lp.getProperty("gemarkungs_nr") != null) {
                    final Object gemarkung = tmp_lp.getProperty("gemarkungs_nr.name");

                    if ((gemarkung instanceof String) && (((String)gemarkung).trim().length() > 0)) {
                        result.append(gemarkung);
                    } else {
                        result.append(flurstueck.getProperty("gemarkungs_nr.gemarkungsnummer"));
                    }
                } else {
                    result.append("Unbekannte Gemarkung");
                }

                result.append("-");
                result.append(tmp_lp.getProperty("flur"));
                result.append("-");
                result.append(tmp_lp.getProperty("fstnr_z"));
                final Object nenner = tmp_lp.getProperty("fstnr_n");
                result.append('/');
                if (nenner != null) {
                    result.append(nenner);
                } else {
                    result.append('0');
                }
            } else if (tmp_lp.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("alkis_landparcel")) {
                result.append(tmp_lp.getProperty("gemarkung"));
                result.append("-");
                result.append(tmp_lp.getProperty("flur"));
                result.append("-");
                result.append(tmp_lp.getProperty("fstck_zaehler"));
                final Object nenner = tmp_lp.getProperty("fstck_nenner");
                result.append('/');
                if (nenner != null) {
                    result.append(nenner);
                } else {
                    result.append('0');
                }
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
