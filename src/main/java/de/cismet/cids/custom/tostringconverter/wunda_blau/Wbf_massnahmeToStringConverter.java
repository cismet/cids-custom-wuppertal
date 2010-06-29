/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ThemaWartungsvertragToStringConverter
 * @author srichter
 */
public class Wbf_massnahmeToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        return cidsBean.getProperty("kuerzel")+ " -- " + cidsBean.getProperty("beschreibung");
    }
}
