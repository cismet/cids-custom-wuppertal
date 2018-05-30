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

import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__DATUM_ANGELEGT;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__ID;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__LAGE;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__FLURSTUECK;

import de.cismet.cids.tools.CustomToStringConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $1.0$, $30.05.2018$
 */
public class QsgebMarkerToStringConverter extends CustomToStringConverter {
        private static final Logger LOG = Logger.getLogger(QsgebMarkerToStringConverter.class);
    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    
    private String getYear(){
        if (cidsBean.getProperty(FIELD__DATUM_ANGELEGT) != null){
            final Date datum = (Date)cidsBean.getProperty(FIELD__DATUM_ANGELEGT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy"); 
            return (String.valueOf(sdf.format(datum)));
        }
        return "";
    }

    @Override
    public String createString() {
        final String myid = String.valueOf(cidsBean.getProperty(FIELD__ID));
        if (myid.equals("-1")){
            return "neuer Marker";
        } else{
            String mylage;
            if ((cidsBean.getProperty(FIELD__LAGE)) == null || String.valueOf(cidsBean.getProperty(FIELD__LAGE)).equals("")){
                mylage = "k.A.";
            }else {
                mylage = String.valueOf(cidsBean.getProperty(FIELD__LAGE));
            }
            final String myflurstueck = cidsBean.getProperty(FIELD__FLURSTUECK).toString(); 
            final String myjahr = getYear();
            
            return myflurstueck + "_" + myjahr + "-" + myid + "_" + mylage;
        }
    }
}
