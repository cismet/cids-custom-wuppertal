/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import org.apache.commons.lang.StringUtils;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_auftraggeberToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        String name = (String)cidsBean.getProperty("name");
        final String amt = (String)cidsBean.getProperty("amt.name");
        name = String.valueOf(name);
        if (StringUtils.isNotBlank(amt)) {
            name += " (" + amt + ")";
        }
        return name;
    }
}
