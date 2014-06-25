/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BahnVerfahrenToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Object aktenzeichen = this.cidsBean.getProperty("aktenzeichen");
        final Object lagebezeichnung = this.cidsBean.getProperty("lagebezeichnung");
        return String.valueOf(lagebezeichnung) + " : " + String.valueOf(aktenzeichen);
    }
}
