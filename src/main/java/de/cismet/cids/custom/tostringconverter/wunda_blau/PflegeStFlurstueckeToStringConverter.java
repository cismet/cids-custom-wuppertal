/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.tostringconverter.wunda_blau;

import de.cismet.cids.tools.CustomToStringConverter;

/**
 *
 * @author sandra
 */
public class PflegeStFlurstueckeToStringConverter extends CustomToStringConverter {
 
    
    public String myString() {
        String myname="";
        String von="";
        String bis="";
        von = String.valueOf(cidsBean.getProperty("von")); 
        bis = String.valueOf(cidsBean.getProperty("bis")); 
        if (von == "null" && bis == "null"){
            myname=String.valueOf(cidsBean.getProperty("strasse"));
        }else{
            if (von=="null"){
                myname=String.valueOf(cidsBean.getProperty("strasse"))+" ("+ String.valueOf(cidsBean.getProperty("bis"))+")";
                }else{ 
                if (bis=="null"){
                    myname=String.valueOf(cidsBean.getProperty("strasse"))+" ("+ String.valueOf(cidsBean.getProperty("von"))+")";
                }else{
                   myname=String.valueOf(cidsBean.getProperty("strasse"))+" ("+ String.valueOf(cidsBean.getProperty("von"))+"-"+ String.valueOf(cidsBean.getProperty("bis"))+")";
                }
            }
        }
        
        //return String.valueOf(cidsBean.getProperty("strasse"))+" ("+ String.valueOf(cidsBean.getProperty("von"))+"-"+ String.valueOf(cidsBean.getProperty("bis"))+")";
       
        return myname;
    }
    
    @Override
    public String createString() {

    /**
     *
     */
    
        //       //String.valueOf(cidsBean.getProperty("von"));
        
        return myString();
        // //return String.valueOf(cidsBean.getProperty("strasse"))+" ("+ String.valueOf(cidsBean.getProperty("von"))+"-"+ String.valueOf(cidsBean.getProperty("bis"))+")";
    }
}
