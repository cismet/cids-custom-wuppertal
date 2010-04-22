/*
 * CategoryToStringConverter.java
 *
 * Created on 6. August 2007, 11:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 *de.cismet.cids.toStringConverter.ZaehlungsstadtteilToStringConverter
 * @author verkennis
 */
public class ZaehlungsstadtteilToStringConverter extends CustomToStringConverter{
    @CidsAttribute("Stadtbezirk")
    public String stadtbez=null;

    public String createString() {

        return stadtbez;
        
    }
}
