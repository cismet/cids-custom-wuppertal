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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class TableUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TableUtils.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * Zugiff auf Tabellen.
     *
     * @param   myTable            DOCUMENT ME!
     * @param   myWhere            DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    public static CidsBean getOtherTableValue(final String myTable,
            final String myWhere,
            final ConnectionContext connectionContext) {
        final MetaObject[] myMetaObject = getOtherTableValues(myTable, myWhere, connectionContext);
        if ((myMetaObject != null) && (myMetaObject.length > 0)) {
            return myMetaObject[0].getBean();
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   myTable            DOCUMENT ME!
     * @param   myWhere            DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MetaObject[] getOtherTableValues(final String myTable,
            final String myWhere,
            final ConnectionContext connectionContext) {
        try {
            final MetaClass myClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    myTable,
                    connectionContext);
            if (myClass != null) {
                final StringBuffer myQuery = new StringBuffer("select ").append(myClass.getId())
                            .append(", ")
                            .append(myClass.getPrimaryKey())
                            .append(" from ")
                            .append(myClass.getTableName())
                            .append(myWhere);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: myQuery:" + myQuery.toString());
                }
                final MetaObject[] myMetaObject;
                try {
                    myMetaObject = SessionManager.getProxy()
                                .getMetaObjectByQuery(myQuery.toString(), 0, connectionContext);
                    if (myMetaObject.length > 0) {
                        return myMetaObject;
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error(myWhere + " kann nicht geladen werden in getOtherTableValue.", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   myWhere  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getMyWhere(final String myWhere) {
        return " where name ilike '" + myWhere + "'";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   deleteBean             DOCUMENT ME!
     * @param   propertyName           DOCUMENT ME!
     * @param   value                  DOCUMENT ME!
     * @param   andDeleteObjectFromDB  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean deleteItemFromList(final CidsBean deleteBean,
            final String propertyName,
            final Object value,
            final boolean andDeleteObjectFromDB) {
        if ((value instanceof CidsBean) && (propertyName != null)) {
            final CidsBean bean = (CidsBean)value;
            if (andDeleteObjectFromDB) {
                try {
                    bean.delete();
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            } else {
                final Object coll = deleteBean.getProperty(propertyName);
                if (coll instanceof Collection) {
                    ((Collection)coll).remove(bean);
                }
            }
        }
        return deleteBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   addBean      DOCUMENT ME!
     * @param   propName     DOCUMENT ME!
     * @param   newTypeBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */

    /**
     * DOCUMENT ME!
     *
     * @param   addBean      DOCUMENT ME!
     * @param   propName     DOCUMENT ME!
     * @param   newTypeBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean addBeanToCollection(final CidsBean addBean,
            final String propName,
            final CidsBean newTypeBean) {
        if ((newTypeBean != null) && (propName != null)) {
            final Object o = addBean.getProperty(propName);
            if (o instanceof Collection) {
                try {
                    final Collection<CidsBean> col = (Collection)o;
                    for (final CidsBean bean : col) {
                        if (newTypeBean.equals(bean)) {
                            LOG.info("Bean " + newTypeBean + " already present in " + propName + "!");
                            return addBean;
                        }
                    }
                    col.add(newTypeBean);
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }
        return addBean;
    }
}
