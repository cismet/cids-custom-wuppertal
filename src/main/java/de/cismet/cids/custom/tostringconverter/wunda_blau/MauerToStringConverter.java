/*
 * MauerToStringConverter.java
 *
 * Created on 8. November 2007, 17:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *
 * @author hell
 */
//de.cismet.cids.toStringConverter.MauerToStringConverter
public class MauerToStringConverter extends CustomToStringConverter{
    @CidsAttribute("Lagebezeichnung")
    public String s = "keine Lagebezeichnung angegeben";

    public String createString() {
        return s;
    }
    
}
