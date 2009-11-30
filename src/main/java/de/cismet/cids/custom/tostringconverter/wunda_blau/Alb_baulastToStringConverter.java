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
public class Alb_baulastToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        final Object laufendeNummerObj = cidsBean.getProperty("laufende_nummer");
        if (laufendeNummerObj != null) {
            return String.valueOf(laufendeNummerObj);
        } else {
            return "Keine laufende Nummer";
        }
    }
}

