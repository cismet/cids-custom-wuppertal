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
public class WerbetraegerToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String bez = (String)cidsBean.getProperty("bezeichnung");
        final String art = (String)cidsBean.getProperty("art.bezeichnung");

        String ret = "";
        if (bez != null) {
            ret = bez;
        } else {
            ret = "keine Bezeichnung";
        }
        ret += "(" + art + ")";
        return ret;
    }
}
