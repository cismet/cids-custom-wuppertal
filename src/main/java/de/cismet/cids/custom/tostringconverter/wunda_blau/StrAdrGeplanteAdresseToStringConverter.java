/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 *
 * @author lat-lon
 */
public class StrAdrGeplanteAdresseToStringConverter extends CustomToStringConverter {
   
    public String myString() {
        String myadress="";
        String myname="";
        String hausnr="";
        String adr_zusatz="";
        myname = String.valueOf(cidsBean.getProperty("fk_strasse_id.name")); 
        hausnr = String.valueOf(cidsBean.getProperty("hausnr")); 
        adr_zusatz = String.valueOf(cidsBean.getProperty("adr_zusatz")); 
        if (adr_zusatz == "null"){
            myadress=myname+" "+hausnr;
        }else{
            myadress=myname+" "+hausnr+" "+adr_zusatz.trim();
        }
        
        
        return myadress;
    }
    
    @Override
    public String createString() {

    /**
     *
     */
    
       
        return myString();
    }
    
}
