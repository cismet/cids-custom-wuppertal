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
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class Mauer_BilderToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return "" + cidsBean.getProperty("url");
    }
}
