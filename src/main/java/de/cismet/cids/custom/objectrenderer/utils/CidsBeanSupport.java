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
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.newuser.User;
import com.vividsolutions.jts.geom.Geometry;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class CidsBeanSupport {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsBeanSupport.class);
    public static final String DOMAIN_NAME = "WUNDA_BLAU";
    public static final String TABLE__GEOM = "Geom";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeanSupport object.
     *
     * @throws  AssertionError  DOCUMENT ME!
     */
    private CidsBeanSupport() {
        throw new AssertionError();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   tableName          DOCUMENT ME!
     * @param   initialProperties  DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewCidsBeanFromTableName(final String tableName,
            final Map<String, Object> initialProperties,
            final ConnectionContext connectionContext) throws Exception {
        final CidsBean newBean = createNewCidsBeanFromTableName(tableName, connectionContext);
        for (final Entry<String, Object> property : initialProperties.entrySet()) {
            newBean.setProperty(property.getKey(), property.getValue());
        }
        return newBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tableName          DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createNewCidsBeanFromTableName(final String tableName,
            final ConnectionContext connectionContext) throws Exception {
        if (tableName != null) {
            final MetaClass metaClass = ClassCacheMultiple.getMetaClass(DOMAIN_NAME, tableName, connectionContext);
            if (metaClass != null) {
                return metaClass.getEmptyInstance(connectionContext).getBean();
            }
        }
        throw new Exception("Could not find MetaClass for table " + tableName);
    }
    
    /**
     * Die Datentypen sollten bei Verwendung vorher getestet werden.
     *
     * @param bean
     * @param conCon 
     * @param table 
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean cloneBean (final CidsBean bean, final ConnectionContext conCon, final String table){
        CidsBean beanClone;
        try {
            beanClone = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    table,
                    conCon);
            for (final String propertyName : beanClone.getPropertyNames()) {
                if (!propertyName.toLowerCase().equals("id")) {
                    final Object obj = bean.getProperty(propertyName);
                    if (obj != null) {
                        if (obj instanceof CidsBean) {
                            if (obj.getClass().getSimpleName().equals(TABLE__GEOM)){
                                CidsBean beanGeom = CidsBeanSupport.cloneBean(
                                    (CidsBean)obj,
                                    conCon,
                                    TABLE__GEOM);
                                beanClone.setProperty(propertyName, beanGeom);
                            } else {
                                beanClone.setProperty(propertyName, (CidsBean)obj);
                            }
                        } else if (obj instanceof Geometry){
                            beanClone.setProperty(propertyName, (Geometry)obj);
                        } else if (obj instanceof Integer) {
                            beanClone.setProperty(propertyName, new Integer(obj.toString()));
                        } else if ( obj instanceof Long) {
                            beanClone.setProperty(propertyName, new Long(obj.toString()));
                        } else if (obj instanceof Double) {
                            beanClone.setProperty(propertyName, new Double(obj.toString()));
                        } else if (obj instanceof Boolean) {
                            beanClone.setProperty(propertyName, Boolean.valueOf(obj.toString()));
                        } else if (obj instanceof String) {
                            beanClone.setProperty(propertyName, obj.toString());
                        } else if (obj instanceof Collection){
                            final List<CidsBean> listArray = (List<CidsBean>)obj;
                            List<CidsBean> listArrayClone = (List)beanClone.getProperty(propertyName);
                            for (CidsBean beanListClone : listArray) {
                                listArrayClone.add(beanListClone);
                            }
                        } else {
                            LOG.error("unknown property type: " + obj.getClass().getName());
                        }
                    }           
                }
            }
            return beanClone;
        } catch (Exception ex) {
            LOG.error("Cannot clone object", ex);
        }
        return null;                               
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean                DOCUMENT ME!
     * @param   collectionProperty  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<CidsBean> getBeanCollectionFromProperty(final CidsBean bean, final String collectionProperty) {
        if ((bean != null) && (collectionProperty != null)) {
            final Object colObj = bean.getProperty(collectionProperty);
            if (colObj instanceof Collection) {
                return (List<CidsBean>)colObj;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean checkWritePermission(final CidsBean bean) {
        final User user = SessionManager.getSession().getUser();
        return bean.getHasWritePermission(user) && bean.hasObjectWritePermission(user);
    }
}
