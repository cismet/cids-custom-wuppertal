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
public class Alb_baulast_artToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        final Object baulastArt = cidsBean.getProperty("baulast_art");
        if (baulastArt != null) {
            return String.valueOf(baulastArt);
        } else {
            return "Keine Art angegeben";
        }
    }
}
