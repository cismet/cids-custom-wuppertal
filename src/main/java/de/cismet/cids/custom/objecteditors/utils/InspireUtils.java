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
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.server.middleware.types.MetaClass;

import org.apache.log4j.Logger;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class InspireUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(InspireUtils.class);
    public static final String FIELD__INSPIRE_EIND_ID = "inspire_eindeutige_id";
    public static final String FIELD__UUID = "uuid";
    public static final String FIELD__HERKUNFT = "herkunft";

    //~ Methods ----------------------------------------------------------------

    
    
    public static String generateUuid(ConnectionContext connectionContext){
        String uuid;
        String neueUuid;

        uuid = getRandomHex() + getRandomHex() + "-" + getRandomHex() + "-" + getRandomHex() + "-" + getRandomHex() + "-" + getRandomHex() + getRandomHex() + getRandomHex();

        final String myWhere = " where uuid ilike '" + uuid + "'";
        final CidsBean uuidBean = TableUtils.getOtherTableValue(FIELD__INSPIRE_EIND_ID, myWhere, connectionContext);

        if (uuidBean == null){
            return uuid;
        }else{ 
            neueUuid = generateUuid(connectionContext);
        }
        return neueUuid;
    }
    
    //Liefert eine vierstellige zufällige Hexadezimalzahl
    public static String getRandomHex(){
        //8-4-4-4-12; 4:65535 8:4294967295, 12:281474976710655 --> aber nicht möglich
        int zahl;
        String uuid;
        
        zahl = (int)Math.floor(Math.random()* Math.floor(65536));
        uuid = Integer.toHexString(zahl);
        
        while (uuid.length() < 4){
            uuid = "0" + uuid;
        }
        
        return uuid;
    }
    
    //Trägt die uuid des neuen Objektes in die Tabelle inspire_eindeutige_id ein
    public static CidsBean writeUuid(final String uuid, CidsBean classBean, final String propertyValue, final String herkunft,  final ConnectionContext connectionContext){
        try {
            final MetaClass metaClass = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", FIELD__INSPIRE_EIND_ID, connectionContext);        
            if (metaClass != null) {
                CidsBean newInspireBean = metaClass.getEmptyInstance(connectionContext).getBean();
                newInspireBean.setProperty(FIELD__UUID, uuid);
                newInspireBean.setProperty(FIELD__HERKUNFT, herkunft);
                classBean.setProperty(propertyValue, newInspireBean);
            }
        } catch (Exception ex) {
            LOG.error("writeUuid failed", ex);
        }
        return classBean;
    }
}
