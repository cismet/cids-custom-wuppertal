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

import Sirius.navigator.connection.SessionManager;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import com.vividsolutions.jts.geom.Geometry;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__DATUM_ANGELEGT;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__GEOREFERENZ__GEO_FIELD;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__ID;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__LAGE;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__NENNER;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.FIELD__ZAEHLER;
import static de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor.TABLE_NAME_FLUR;
import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cids.tools.CustomToStringConverter;
import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
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

    private String getLandparcel(Geometry myPoint){
        final MetaClass mc = ClassCacheMultiple.getMetaClass(
                            "WUNDA_BLAU",
                            TABLE_NAME_FLUR,
                            cidsBean.getConnectionContext());
        if (mc != null) {
            // Suche Konfigurieren 
            final BufferingGeosearch search = new BufferingGeosearch();
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(myPoint);
            try{
                final ConnectionContext cC = ConnectionContext.create(
            AbstractConnectionContext.Category.DEPRECATED,
                    TABLE_NAME_FLUR);
                // Suche ausführen
                final Collection<MetaObjectNode> mons = SessionManager.getProxy().customServerSearch(
                    SessionManager.getSession().getUser(),
                    search,
                    cC);//cidsBean.getConnectionContext());
                if ((mons != null) && !mons.isEmpty()) {
                    final MetaObjectNode mon = mons.toArray(new MetaObjectNode[0])[0];

                    final MetaObject mo = SessionManager.getProxy()
                                .getMetaObject(mon.getObjectId(),
                                    mon.getClassId(),
                                    mon.getDomain(),
                                    cC);//cidsBean.getConnectionContext());
                    final CidsBean flurstueckBean = mo.getBean();
                    if (flurstueckBean != null){
                        String zaehler = flurstueckBean.getProperty(FIELD__ZAEHLER).toString();
                        if (flurstueckBean.getProperty(FIELD__NENNER) != null){
                            return zaehler + "/" + flurstueckBean.getProperty(FIELD__NENNER).toString();
                        }else{
                            return zaehler;
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.warn("Geom Search Error.", ex);
            }

        } else {
            LOG.error("Could not find MetaClass for alkis_landparcel " );
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
            final String myflurstueck = getLandparcel((Geometry)cidsBean.getProperty(FIELD__GEOREFERENZ__GEO_FIELD));
            final String myjahr = getYear();
            
            //final String kurrLage = mylage.substring(0, 2);
            return myflurstueck + "_" + myjahr + "-" + myid + "_" + mylage;
        }
    }
}
