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
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.HeadlessException;

import java.util.Collection;

import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdesktop.swingx.JXTable;

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

    public static CidsBean addBeanToCollection(final CidsBean addBean,
            final String propName,
            final CidsBean newTypeBean) {
        return addBeanToCollectionWithMessage(null, addBean, propName, newTypeBean);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   parentComponent  DOCUMENT ME!
     * @param   addBean          DOCUMENT ME!
     * @param   propName         DOCUMENT ME!
     * @param   newTypeBean      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean addBeanToCollectionWithMessage(final Component parentComponent,
            final CidsBean addBean,
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
                            if (parentComponent != null) {
                                JOptionPane.showMessageDialog(

                                    // StaticSwingTools.getParentFrame(this),
                                    parentComponent,
                                    "Das Objekt "
                                            + newTypeBean
                                            + " kann nicht noch einmal hinzugefügt werden.",
                                    "Objekt hinzufügen",
                                    JOptionPane.OK_OPTION);
                            }
                            return addBean;
                        }
                    }
                    col.add(newTypeBean);
                } catch (HeadlessException ex) {
                    LOG.error(ex, ex);
                }
            }
        }
        return addBean;
    }
      
    //Für 1:n-Beziehung 
    public static void addObjectToTable(final JXTable table, final String tableClass, ConnectionContext connectionContext) {
        try {
            final CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName(tableClass, connectionContext);

            ((DivBeanTable)table.getModel()).addBean(bean);
        } catch (Exception e) {
            LOG.error("Cannot add new " + tableClass + " object", e);
        }
    }
    //Für 1:n-Beziehung 
    public static void removeObjectsFromTable(final JXTable table) {
        final int[] selectedRows = table.getSelectedRows();
        final List<Integer> modelRows = new ArrayList<>();

        // The model rows should be in reverse order
        for (final int row : selectedRows) {
            modelRows.add(table.convertRowIndexToModel(row));
        }

        Collections.sort(modelRows, Collections.reverseOrder());

        for (final Integer row : modelRows) {
            ((DivBeanTable)table.getModel()).removeRow(row);
        }
    }
}
