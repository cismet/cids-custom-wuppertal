/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * ListToStringConverter.java
 *
 * Created on 6. Mai 2008, 10:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ListToStringConverter.
 *
 * @author   verkennis
 * @version  $Revision$, $Date$
 */
public class Wbf_miet_typToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Value")
    public String value = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of ListToStringConverter.
     */
    public Wbf_miet_typToStringConverter() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return value;
    }
}
