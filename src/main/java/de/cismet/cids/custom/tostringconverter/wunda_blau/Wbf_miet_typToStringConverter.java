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
 * @version  $Revision$, $Date$
 */
public class Wbf_miet_typToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String value = (String)cidsBean.getProperty("typ");
        if (value != null) {
            return value;
        } else {
            return "";
        }
    }
}
