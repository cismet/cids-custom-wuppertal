/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * WTBauartToString.java
 *
 * Created on 31. August 2007, 14:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.WTBauartToString.
 *
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class Wt_bauartToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("Bezeichnung")
    public String bauart = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of WTBauartToString.
     */
    public Wt_bauartToStringConverter() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return bauart;
    }
}
