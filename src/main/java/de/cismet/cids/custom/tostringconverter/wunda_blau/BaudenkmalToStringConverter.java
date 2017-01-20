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
public class BaudenkmalToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Integer nummer = (Integer)cidsBean.getProperty("denkmalnummer");
        if (nummer != null) {
            return nummer.toString();
        } else {
            return null;
        }
    }
}
