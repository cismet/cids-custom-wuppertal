/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ThemaDienstToStringConverter
 * @author srichter
 */
public class ThemaDienstToStringConverter extends CustomToStringConverter {

    final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    @CidsAttribute("dienstname")
    public String string = null;

    @Override
    public String createString() {
        return string != null ? string : "-";
    }
}
