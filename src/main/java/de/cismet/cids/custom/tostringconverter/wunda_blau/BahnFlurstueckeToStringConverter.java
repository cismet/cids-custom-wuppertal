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
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BahnFlurstueckeToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        final CidsBean gemarkungBean = (CidsBean)cidsBean.getProperty("gemarkung");
        if (gemarkungBean != null) {
            result.append(gemarkungBean.getProperty("gemarkungsnummer"));
        }
        result.append("-");
        result.append(cidsBean.getProperty("flur"));
        result.append("-");
        result.append(cidsBean.getProperty("zaehler"));
        final Object nenner = cidsBean.getProperty("nenner");
        result.append("-");
        if (nenner != null) {
            result.append(nenner);
        } else {
            result.append("0");
        }
        return result.toString();
    }
}
