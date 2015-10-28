/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.CustomToStringConverter;

import static de.cismet.cids.custom.tostringconverter.wunda_blau.Alb_flurstueck_kickerToStringConverter.HISTORISCH;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class FlurstueckToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        final CidsBean gemarkungBean = (CidsBean)cidsBean.getProperty("gemarkungs_nr");
        if (gemarkungBean != null) {
            result.append(gemarkungBean.getProperty("gemarkungsnummer"));
        }
        result.append("-");
        result.append(cidsBean.getProperty("flur"));
        result.append("-");
        result.append(cidsBean.getProperty("fstnr_z"));
        final Object nenner = cidsBean.getProperty("fstnr_n");
        result.append("/");
        if (nenner != null) {
            result.append(nenner);
        } else {
            result.append("0");
        }

        final Object hist_date = cidsBean.getProperty("historisch");
        if (hist_date != null) {
            result.append(HISTORISCH);
        }

        return result.toString();
    }
}
