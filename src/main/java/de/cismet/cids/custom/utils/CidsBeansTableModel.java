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

import java.util.ArrayList;
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

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(CidsBeansTableModel.class);

    //~ Instance fields --------------------------------------------------------

    private final Class[] columnClasses;
    private final String[] columnNames;
    private final String[] columnProperties;
    private final Boolean[] columnEditable;

    private final Boolean allColumnsEditable;
    private final Boolean allRowsEditable;

    private List<CidsBean> cidsBeans;
    private final List<Integer> editableRowIndices = new ArrayList<>();

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
        this(columnProperties, columnNames, columnClasses, false);
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
            final Boolean[] columnEditable) {
        this(columnProperties, columnNames, columnClasses, columnEditable, true);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties    DOCUMENT ME!
     * @param  columnNames         DOCUMENT ME!
     * @param  columnClasses       DOCUMENT ME!
     * @param  allColumnsEditable  DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean allColumnsEditable) {
        this(columnProperties, columnNames, columnClasses, allColumnsEditable, true);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     * @param  columnEditable    DOCUMENT ME!
     * @param  allRowsEditable   DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final Boolean[] columnEditable,
            final boolean allRowsEditable) {
        this.columnProperties = columnProperties;
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnEditable = columnEditable;
        this.allColumnsEditable = null;
        this.allRowsEditable = allRowsEditable;
    }
    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties    DOCUMENT ME!
     * @param  columnNames         DOCUMENT ME!
     * @param  columnClasses       DOCUMENT ME!
     * @param  allColumnsEditable  DOCUMENT ME!
     * @param  allRowsEditable     DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean allColumnsEditable,
            final boolean allRowsEditable) {
        this.columnProperties = columnProperties;
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnEditable = null;
        this.allColumnsEditable = allColumnsEditable;
        this.allRowsEditable = allRowsEditable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        setCidsBeans(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    final String getColumnProperty(final int row) {
        if ((row < 0) || (columnProperties == null) || (row >= columnProperties.length)) {
            return null;
        } else {
            return columnProperties[row];
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        this.cidsBeans = cidsBeans;
        fireTableDataChanged();

        editableRowIndices.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editableRowIndices  DOCUMENT ME!
     */
    public void setEditableRowIndices(final List<Integer> editableRowIndices) {
        this.editableRowIndices.clear();
        this.editableRowIndices.addAll(editableRowIndices);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editableObjects  DOCUMENT ME!
     */
    public void setEditableObjects(final List<CidsBean> editableObjects) {
        this.editableRowIndices.clear();
        for (final CidsBean cidsBean : cidsBeans) {
            final int index = getRowIndex(cidsBean);
            if (index >= 0) {
                editableRowIndices.add(index);
            }
        }
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
        if ((columnIndex < 0) || (columnProperties == null) || (columnIndex >= columnProperties.length)
                    || (rowIndex < 0)
                    || (rowIndex >= cidsBeans.size())) {
            return false; // out of range
        } else if (Boolean.TRUE.equals(allColumnsEditable) && Boolean.TRUE.equals(allRowsEditable)) {
            return true;
        } else if (Boolean.FALSE.equals(allRowsEditable) && !editableRowIndices.contains(rowIndex)) {
            return false;
        }

        final boolean columnCondition = Boolean.TRUE.equals(allColumnsEditable)
                    || ((columnEditable != null) && Boolean.TRUE.equals(columnEditable[columnIndex]));
        final boolean rowCondition = Boolean.TRUE.equals(allRowsEditable) || editableRowIndices.contains(rowIndex);

        if (columnCondition && rowCondition) {
            final CidsBean cidsBean = getCidsBean(rowIndex);
            return cidsBean != null;
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
        if (columnProperties == null) {
            return -1;
        }
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
        if ((columnIndex < 0) || (columnProperties == null) || (columnIndex >= columnProperties.length)) {
            return null;
        }
        final CidsBean cidsBean = getCidsBean(rowIndex);

        if (cidsBean != null) {
            return cidsBean.getProperty(columnProperties[columnIndex]);
        } else {
            return null;
        }
    }

    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        final CidsBean cidsBean = getCidsBean(rowIndex);
        if (cidsBean == null) {
            return;
        }
        if ((columnIndex < 0) || (columnProperties == null) || (columnIndex >= columnProperties.length)) {
            return;
        }
        try {
            final Object convertedValue;
            if ((value instanceof java.util.Date) && (columnClasses != null)
                        && java.sql.Date.class.equals(columnClasses[columnIndex])) {
                convertedValue = new java.sql.Date(((java.util.Date)value).getTime());
            } else {
                convertedValue = value;
            }
            cidsBean.setProperty(columnProperties[columnIndex], convertedValue);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
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
