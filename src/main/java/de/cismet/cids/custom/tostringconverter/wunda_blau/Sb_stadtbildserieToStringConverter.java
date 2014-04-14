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
public class Sb_stadtbildserieToStringConverter extends CustomToStringConverter {

    //~ Instance fields --------------------------------------------------------

    @CidsAttribute("vorschaubild.bildnummer")
    public String vorschaubild = "kein Vorschaubild";

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return vorschaubild;
    }
}
