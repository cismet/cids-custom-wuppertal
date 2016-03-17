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

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class BerechtigungspruefungToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String createString() {
        return new SimpleDateFormat("dd.MM.yyyy").format((Timestamp)cidsBean.getProperty("anfrage_timestamp")) + " ("
                    + (String)cidsBean.getProperty("schluessel") + ")";
    }
}
