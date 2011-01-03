/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * de.cismet.cids.toStringConverter.ZaehlungswetterToStringConverter.
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ZaehlungswetterToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("beschreibung")
    public String beschreibung = null;

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        if (beschreibung != null) {
            return beschreibung;
        } else {
            return "keine Wetterr ausgew\u00E4hlt";
        }
    }
}
