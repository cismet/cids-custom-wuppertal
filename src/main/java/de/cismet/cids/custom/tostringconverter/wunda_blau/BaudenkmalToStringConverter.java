/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * CategoryToStringConverter.java
 *
 * Created on 6. August 2007, 11:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import org.apache.log4j.Logger;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.BplanVerfahrenToStringConverter.
 *
 * @author   verkennis
 * @version  $Revision$, $Date$
 */
public class BaudenkmalToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Denkmalnummer")
    public Integer nummer = null;

    private final Logger log = Logger.getLogger(this.getClass());

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        if (log.isDebugEnabled()) {
            log.debug("BaudenkmalToStringConverter Denkmalnummer:" + nummer);
        }
        return nummer.toString();
    }
}
