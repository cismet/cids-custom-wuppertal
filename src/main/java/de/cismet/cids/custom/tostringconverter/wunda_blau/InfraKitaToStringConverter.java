/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   lat-lon
 * @version  $Revision$, $Date$
 */
public class InfraKitaToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    @Override
    public String createString() {
        String mystrasse;
        final String myname;
        mystrasse = String.valueOf(cidsBean.getProperty("adresse"));
        mystrasse = mystrasse.trim();
        myname = String.valueOf(cidsBean.getProperty("name"));

        return myname + ", " + mystrasse;
    }
}