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
package de.cismet.cids.custom.objectrenderer.utils;

import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumMeldungBeanTable extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumMeldungBeanTable.class);

    //~ Instance fields --------------------------------------------------------

    private final CidsBean bean;
    private final String collectionPropertyName;
    private final String[] names;
    private final String[] attributes;
    private final Class[] types;
    private final boolean editable;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumMeldungBeanTable object.
     *
     * @param  editable                DOCUMENT ME!
     * @param  bean                    DOCUMENT ME!
     * @param  collectionPropertyName  DOCUMENT ME!
     * @param  names                   DOCUMENT ME!
     * @param  attributes              DOCUMENT ME!
     * @param  types                   DOCUMENT ME!
     */
    public BaumMeldungBeanTable(final boolean editable,
            final CidsBean bean,
            final String collectionPropertyName,
            final String[] names,
            final String[] attributes,
            final Class[] types) {
        this.bean = bean;
        this.collectionPropertyName = collectionPropertyName;
        this.names = names;
        this.attributes = attributes;
        this.types = types;
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<CidsBean> getBeanList() {
        if ((bean != null) && (collectionPropertyName != null)) {
            final Object colObj = bean.getProperty(collectionPropertyName);
            if (colObj instanceof Collection) {
                return (List<CidsBean>)colObj;
            }
        }
        return null;
    }

    @Override
    public int getRowCount() {
        final List<CidsBean> beanList = getBeanList();

        return ((beanList == null) ? 0 : beanList.size());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bean  DOCUMENT ME!
     */
    public void addBean(final CidsBean bean) {
        final List<CidsBean> beanList = getBeanList();

        if ((beanList != null) && (bean != null)) {
            beanList.add(bean);
            fireTableDataChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rowToRemove  DOCUMENT ME!
     */
    public void removeRow(final int rowToRemove) {
        final List<CidsBean> beanList = getBeanList();

        if (beanList != null) {
            beanList.remove(rowToRemove);
            fireTableDataChanged();
        }
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return names[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return types[columnIndex];
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return editable;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (attributes.length == 0) {
            return getBeanList().get(rowIndex);
        } else {
            return getBeanList().get(rowIndex).getProperty(attributes[columnIndex]);
        }
    }

    @Override
    public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
        try {
            if ((attributes.length == 0) && (aValue instanceof CidsBean)) {
                getBeanList().remove(rowIndex);
                getBeanList().add(rowIndex, (CidsBean)aValue);
            } else {
                getBeanList().get(rowIndex).setProperty(attributes[columnIndex], aValue);
            }
            bean.setProperty(collectionPropertyName, getBeanList());
        } catch (Exception ex) {
            LOG.error("Cannot set property " + attributes[columnIndex] + " new value: " + String.valueOf(aValue), ex);
        }
    }
}
