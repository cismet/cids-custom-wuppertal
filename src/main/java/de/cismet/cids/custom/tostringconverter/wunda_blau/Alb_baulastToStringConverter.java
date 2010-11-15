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
        Object laufendeNummerObj = cidsBean.getProperty("laufende_nummer");
        Object blattObj = cidsBean.getProperty("blattnummer");
        if (blattObj == null) {
            blattObj = "kein Baulastbatt";
        }
        if (laufendeNummerObj == null) {
            laufendeNummerObj = "keine laufende Nummer";
        }
        return blattObj + " / " + laufendeNummerObj;
    }
}
