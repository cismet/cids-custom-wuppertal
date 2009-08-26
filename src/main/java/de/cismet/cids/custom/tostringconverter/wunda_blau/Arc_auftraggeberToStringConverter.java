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
public class Arc_auftraggeberToStringConverter extends CustomToStringConverter {

    @Override
    public String createString() {
        return String.valueOf(cidsBean.getProperty("auftraggeber"));
    }
}
