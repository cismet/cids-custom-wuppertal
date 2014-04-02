/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.annotations.CidsAttribute;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("bildnummer")
    public String bildnummer = "Bildnummer nicht bekannt";

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return bildnummer;
    }
}
