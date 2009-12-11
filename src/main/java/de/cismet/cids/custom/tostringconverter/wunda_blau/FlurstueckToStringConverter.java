/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *
 * @author srichter
 */
public class FlurstueckToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        final StringBuilder result = new StringBuilder();
        final CidsBean gemarkungBean = (CidsBean) cidsBean.getProperty("gemarkungs_nr");
        if (gemarkungBean != null) {
            result.append(gemarkungBean.getProperty("gemarkungsnummer"));
        }
        result.append("-");
        result.append(cidsBean.getProperty("flur"));
        result.append("-");
        result.append(cidsBean.getProperty("fstnr_z"));
        Object nenner = cidsBean.getProperty("fstnr_n");
        if (nenner != null) {
            result.append("/");
            result.append(nenner);
        }
        return result.toString();
    }
}
