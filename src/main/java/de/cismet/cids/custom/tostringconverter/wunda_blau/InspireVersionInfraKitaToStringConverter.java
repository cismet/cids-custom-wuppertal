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
import javax.swing.JOptionPane;

/**
 * DOCUMENT ME!
 *
 * @author   lat-lon
 * @version  $Revision$, $Date$
 */
public class InspireVersionInfraKitaToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String myString() {
        String mykita = "";
        String myversion = "";
        mykita = String.valueOf(cidsBean.getProperty("infra_kita_reference"));
        myversion = String.valueOf(cidsBean.getProperty("versionnr"));
        return mykita + ", " + myversion;
    }

    @Override
    public String createString() {
        /**
         *
         */

        return myString();
    }
}
