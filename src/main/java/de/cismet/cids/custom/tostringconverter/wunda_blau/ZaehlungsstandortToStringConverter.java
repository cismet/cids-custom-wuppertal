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
 * @author   verkennis
 * @version  $Revision$, $Date$
 */
public class ZaehlungsstandortToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String lage = (String)cidsBean.getProperty("lage");
        final Integer standort = (Integer)cidsBean.getProperty("standort");

        String s = null;
        if (standort != null) {
            s = standort.toString();
            switch (s.length()) {
                case 1: {
                    s = "00" + s;
                    break;
                }
                case 2: {
                    s = "0" + s;
                    break;
                }
            }
        }

        return s + " " + lage;
    }
}
