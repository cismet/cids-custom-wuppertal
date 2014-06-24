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
public class ZaehlungsstadtteilToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final String stadtteil = (String)cidsBean.getProperty("stadtteil");
        return stadtteil;
    }
}
