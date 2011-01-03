/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alkis_adresseToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        final Object strasse = cidsBean.getProperty("strasse");
        final Object nummer = cidsBean.getProperty("nummer");
        String result = "";
        if (strasse != null) {
            result = strasse.toString();
            if (nummer != null) {
                result += " " + nummer;
            }
        }
        return result;
    }
}
