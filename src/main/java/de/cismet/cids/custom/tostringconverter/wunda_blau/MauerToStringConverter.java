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
 * @author   hell
 * @version  $Revision$, $Date$
 */
public class MauerToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String lagebezeichnung = (String)cidsBean.getProperty("lagebezeichnung");
        if (lagebezeichnung != null) {
            return lagebezeichnung;
        } else {
            return "keine Lagebezeichnung angegeben";
        }
    }
}
