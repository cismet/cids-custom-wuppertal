/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.Thema_wmsToStringConverter
 * @author srichter
 */
public class Thema_wmsToStringConverter extends CustomToStringConverter {

    final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    @CidsAttribute("wms_name")
    public String name = null;

    @Override
    public String createString() {
        return name != null ? name : "-";
    }
}
