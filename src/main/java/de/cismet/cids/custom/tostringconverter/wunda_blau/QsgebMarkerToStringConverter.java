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

import java.text.SimpleDateFormat;

import java.util.Date;

import de.cismet.cids.tools.CustomToStringConverter;

import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__DATUM_ANGELEGT;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__FLURSTUECK;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__ID;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__LAGE;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $1.0$, $30.05.2018$
 */
public class QsgebMarkerToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    private String getYear() {
        if (cidsBean.getProperty(FIELD__DATUM_ANGELEGT) != null) {
            final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            return (String.valueOf(sdf.format(datum)));
        }
        return "";
    }

    @Override
    public String createString() {
        final String myid = String.valueOf(cidsBean.getProperty(FIELD__ID));
        if ("-1".equals(myid)) {
            return "neuer Marker";
        } else {
            String mylage;
            if (((cidsBean.getProperty(FIELD__LAGE)) == null)
                        || String.valueOf(cidsBean.getProperty(FIELD__LAGE)).equals("")) {
                mylage = "k.A.";
            } else {
                mylage = String.valueOf(cidsBean.getProperty(FIELD__LAGE));
            }
            final String myflurstueck = cidsBean.getProperty(FIELD__FLURSTUECK).toString();
            final String myjahr = getYear();

            return myflurstueck + "_" + myjahr + "-" + myid + "_" + mylage;
        }
    }
}
