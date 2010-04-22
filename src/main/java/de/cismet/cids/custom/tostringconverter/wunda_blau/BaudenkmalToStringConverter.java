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
import org.apache.log4j.Logger;

/**
 *de.cismet.cids.toStringConverter.BplanVerfahrenToStringConverter
 * @author verkennis
 */
public class BaudenkmalToStringConverter extends CustomToStringConverter{

private final Logger log = Logger.getLogger(this.getClass());
    @CidsAttribute("Denkmalnummer")

    public Integer nummer = null;
    public String createString() {
        log.debug("BaudenkmalToStringConverter Denkmalnummer:"+ nummer);
        return nummer.toString();
        
    }
}
