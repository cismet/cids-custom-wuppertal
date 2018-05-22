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


import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.tools.CustomToStringConverter;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class StrAdrStrasseSchluesselToStringConverter extends CustomToStringConverter {

    //~ Methods ----------------------------------------------------------------
   
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String myString() {
        String myname = "";
        myname = String.valueOf(cidsBean.getProperty("name"));
        final String myWhere = " where strasse ilike '" + myname + "'";
        CidsBean mybean;
        String myschluessel = "";
        
        mybean = TableUtils.getOtherTableValue("str_adr_strasse", myWhere, cidsBean.getConnectionContext());
        if (mybean != null){
            myschluessel = mybean.getProperty("name").toString();
        }
        
        if (myschluessel.length() == 0){
            switch (myname){
                case "05000":
                    myschluessel = "keine kreuzende/abgehende Strasse";
                    break;
                case "05999":
                    myschluessel = "keine kreuzende/abgehende Strasse";
                    break;
                case "07999":
                    myschluessel = "hier ist die Stadtgrenze";
                    break;
                default:
                    myschluessel = "noch nicht benannt";
            } 
        }
        return myschluessel + "\u0009" + "\t" + (char)9 + "  "  + myname;
    }
    

    @Override
    public String createString() {
        /**
         *
         */

        return myString();
    }
}
