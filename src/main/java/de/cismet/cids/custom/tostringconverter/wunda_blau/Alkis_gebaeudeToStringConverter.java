/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 *
 * @author srichter
 */
public class Alkis_gebaeudeToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        //todo: find something better as representation
        return String.valueOf(cidsBean.getProperty("alkis_id"));
    }

}
