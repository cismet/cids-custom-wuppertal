/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ThemaWartungsvertragToStringConverter
 * @author srichter
 */
public class WBF_VorgangToStringConverter extends CustomToStringConverter {

    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    @Override
    public String createString() {

        try {
            Object o = cidsBean.getProperty("vergabenummer");
            if (o != null) {
                return o.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Fehler in toStringConverter", e);
            return "Vorgang ";
        }
    }
}
