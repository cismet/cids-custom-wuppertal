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
public class Alkis_buchungsblatt_landparcelToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String createString() {
        String result = String.valueOf(cidsBean.getProperty("landparcelcode"));
        final Object flaeche = cidsBean.getProperty("groesse");

        if (flaeche != null) {
            result += " - " + flaeche + " m²";
        }
        return result;
    }
}
