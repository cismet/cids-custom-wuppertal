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
package de.cismet.cids.custom.utils;

import org.apache.log4j.Logger;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CidsBeansTableModel extends AbstractTableModel {

    //~ Instance fields --------------------------------------------------------

    private final Class[] columnClasses;
    private final String[] columnNames;
    private final String[] columnProperties;
    private final boolean[] columnEditable;

    private List<CidsBean> cidsBeans;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses) {
        this(columnProperties, columnNames, columnClasses, null);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     * @param  columnEditable    DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean[] columnEditable) {
        this.columnProperties = columnProperties;
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnEditable = columnEditable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getRowIndex(final CidsBean cidsBean) {
        return cidsBeans.indexOf(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void add(final CidsBean cidsBean) {
        cidsBeans.add(cidsBean);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    public void remove(final CidsBean cidsBean) {
        cidsBeans.remove(cidsBean);
        fireTableDataChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public String getColumnName(final int columnIndex) {
        if ((columnIndex < 0) || (columnIndex >= columnNames.length)) {
            return null;
        }
        return columnNames[columnIndex];
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if ((columnEditable == null) || (columnIndex < 0) || (columnIndex >= columnEditable.length)) {
            return false;
        }
        final CidsBean cidsBean = getCidsBean(rowIndex);

        if (cidsBean != null) {
            return columnEditable[columnIndex];
        } else {
            return false;
        }
    }

    @Override
    public int getRowCount() {
        return (cidsBeans != null) ? cidsBeans.size() : 0;
    }

    @Override
    public int getColumnCount() {
        return columnProperties.length;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean(final int rowIndex) {
        return cidsBeans.get(rowIndex);
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if ((columnIndex < 0) || (columnIndex >= columnProperties.length)) {
            return null;
        }
        final CidsBean cidsBean = getCidsBean(rowIndex);

        if (cidsBean != null) {
            return cidsBean.getProperty(columnProperties[columnIndex]);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        if ((columnIndex < 0) || (columnIndex >= columnClasses.length)) {
            return null;
        }
        return columnClasses[columnIndex];
    }
}
