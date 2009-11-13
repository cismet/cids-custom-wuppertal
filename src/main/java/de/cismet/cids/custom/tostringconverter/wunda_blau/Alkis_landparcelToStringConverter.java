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
public class Alkis_landparcelToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        final Object bezeichnung = cidsBean.getProperty("bezeichnung");
        if (bezeichnung!=null){
            return bezeichnung.toString();
        }
        return "-";
    }
}
