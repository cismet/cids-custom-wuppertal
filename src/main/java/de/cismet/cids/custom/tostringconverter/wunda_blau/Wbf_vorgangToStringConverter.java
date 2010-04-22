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
public class Wbf_vorgangToStringConverter extends CustomToStringConverter {

    private transient final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    @Override
    public String createString() {
        String ret="";
        try {
            Object vergabenummer = cidsBean.getProperty("vergabenummer");
            Object folgenummer = cidsBean.getProperty("folgenummer");
            if (vergabenummer != null) {
                ret=vergabenummer.toString();
            } 
            if (folgenummer != null && !folgenummer.toString().trim().equals("0")) {
                ret+="/"+folgenummer;
            }

            return ret;
        } catch (Exception e) {
            log.error("Fehler in toStringConverter", e);
            return "Vorgang ";
        }
    }
}
