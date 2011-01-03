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

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alb_flurstueck_kickerToStringConverter extends CustomToStringConverter {

    //~ Static fields/initializers ---------------------------------------------

    public static final String HISTORISCH = " (hist.)";

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        result.append(cidsBean.getProperty("gemarkung"));
        result.append("-");
        result.append(cidsBean.getProperty("flur"));
        result.append("-");
        result.append(cidsBean.getProperty("zaehler"));
        final Object nenner = cidsBean.getProperty("nenner");
        result.append("/");
        if (nenner != null) {
            result.append(nenner);
        } else {
            result.append("0");
        }
        final Object real_flurstueck = cidsBean.getProperty("fs_referenz");
        if (real_flurstueck instanceof CidsBean) {
            final CidsBean fsBean = (CidsBean)real_flurstueck;
            final Object hist_date = fsBean.getProperty("historisch");
            if (hist_date != null) {
                result.append(HISTORISCH);
            }
        } else {
            result.append(HISTORISCH);
        }
        return result.toString();
    }
}
