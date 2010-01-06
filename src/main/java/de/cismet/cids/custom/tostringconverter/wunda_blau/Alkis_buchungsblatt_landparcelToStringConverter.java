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
public class Alkis_buchungsblatt_landparcelToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        String result = String.valueOf(cidsBean.getProperty("landparcelcode"));
        final Object flaeche = cidsBean.getProperty("groesse");

        if (flaeche != null) {
            result += " - "+flaeche + " m²";
        }
        return result;
    }
}
