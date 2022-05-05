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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final Boolean[] columnEditables;
    private final Set<Integer> selectedRowIndices;

    private final Boolean allColumnsEditable;
    private final Boolean allRowsEditable;

    private List<CidsBean> cidsBeans;
    private final List<Integer> editableRowIndices = new ArrayList<>();
    private boolean loading = false;

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
        this(columnProperties, columnNames, columnClasses, false, false);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     * @param  rowsSelectable    DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean rowsSelectable) {
        this(columnProperties, columnNames, columnClasses, false, rowsSelectable);
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
        this(columnProperties, columnNames, columnClasses, columnEditable, true, false);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     * @param  columnEditable    DOCUMENT ME!
     * @param  rowsSelectable    DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final Boolean[] columnEditable,
            final boolean rowsSelectable) {
        this(columnProperties, columnNames, columnClasses, columnEditable, true, rowsSelectable);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties    DOCUMENT ME!
     * @param  columnNames         DOCUMENT ME!
     * @param  columnClasses       DOCUMENT ME!
     * @param  allColumnsEditable  DOCUMENT ME!
     * @param  rowsSelectable      DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean allColumnsEditable,
            final boolean rowsSelectable) {
        this(columnProperties, columnNames, columnClasses, allColumnsEditable, true, rowsSelectable);
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties  DOCUMENT ME!
     * @param  columnNames       DOCUMENT ME!
     * @param  columnClasses     DOCUMENT ME!
     * @param  columnEditables   DOCUMENT ME!
     * @param  allRowsEditable   DOCUMENT ME!
     * @param  rowsSelectable    DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final Boolean[] columnEditables,
            final boolean allRowsEditable,
            final boolean rowsSelectable) {
        this.columnProperties = columnProperties;
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnEditables = columnEditables;
        this.allColumnsEditable = null;
        this.allRowsEditable = allRowsEditable;
        selectedRowIndices = rowsSelectable ? new HashSet<Integer>() : null;
    }

    /**
     * Creates a new CidsBeansTableModel object.
     *
     * @param  columnProperties    DOCUMENT ME!
     * @param  columnNames         DOCUMENT ME!
     * @param  columnClasses       DOCUMENT ME!
     * @param  allColumnsEditable  DOCUMENT ME!
     * @param  allRowsEditable     DOCUMENT ME!
     * @param  rowsSelectable      DOCUMENT ME!
     */
    public CidsBeansTableModel(final String[] columnProperties,
            final String[] columnNames,
            final Class[] columnClasses,
            final boolean allColumnsEditable,
            final boolean allRowsEditable,
            final boolean rowsSelectable) {
        this.columnProperties = columnProperties;
        this.columnNames = columnNames;
        this.columnClasses = columnClasses;
        this.columnEditables = null;
        this.allColumnsEditable = allColumnsEditable;
        this.allRowsEditable = allRowsEditable;
        selectedRowIndices = rowsSelectable ? new HashSet<Integer>() : null;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the loading
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loading  the loading to set
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        setCidsBeans(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isColumnWithinBounds(final int columnIndex) {
        return (columnProperties != null) && (columnIndex >= 0) && (columnIndex < getColumnCount());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isRowWithinBounds(final int rowIndex) {
        return (rowIndex >= 0) && (rowIndex < getRowCount());
    }

    /**
     * DOCUMENT ME!
     *
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getColumnProperty(final int columnIndex) {
        if ((columnIndex == 0) && isRowsSelectable()) {
            return null;
        }
        return isColumnWithinBounds(columnIndex) ? columnProperties[columnIndex - (isRowsSelectable() ? 1 : 0)] : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        loading = false;
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
     * @return  DOCUMENT ME!
     */
    public boolean isRowsSelectable() {
        return selectedRowIndices != null;
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
        if ((columnIndex == 0) && isRowsSelectable()) {
            return " ";
        }
        return isColumnWithinBounds(columnIndex) ? columnNames[columnIndex - (isRowsSelectable() ? 1 : 0)] : null;
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        if (!isColumnWithinBounds(columnIndex) || !isRowWithinBounds(rowIndex)) {
            return false; // out of range
        } else if (Boolean.TRUE.equals(allColumnsEditable) && Boolean.TRUE.equals(allRowsEditable)) {
            return true;
        } else if (Boolean.FALSE.equals(allRowsEditable) && !editableRowIndices.contains(rowIndex)) {
            return false;
        }

        if ((columnIndex == 0) && isRowsSelectable()) {
            return true;
        }
        final boolean columnCondition = Boolean.TRUE.equals(allColumnsEditable)
                    || ((columnEditables != null) && Boolean.TRUE.equals(columnEditables[columnIndex]));
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
        if (loading) {
            return 1;
        } else {
            return (cidsBeans != null) ? cidsBeans.size() : 0;
        }
    }

    @Override
    public int getColumnCount() {
        if (columnProperties == null) {
            return -1;
        }
        return columnProperties.length + (isRowsSelectable() ? 1 : 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean(final int rowIndex) {
        return isRowWithinBounds(rowIndex) ? cidsBeans.get(rowIndex) : null;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (loading) {
            if (getColumnClass(columnIndex).equals(String.class)) {
                return "Wird geladen";
            } else {
                return null;
            }
        } else {
            if (!isRowWithinBounds(rowIndex) || !isColumnWithinBounds(columnIndex)) {
                return null;
            }
            if ((columnIndex == 0) && isRowsSelectable()) {
                return selectedRowIndices.contains(rowIndex);
            }

            final CidsBean cidsBean = getCidsBean(rowIndex);
            final String columnProperty = getColumnProperty(columnIndex);

            return ((cidsBean != null) && (columnProperty != null)) ? cidsBean.getProperty(columnProperty) : null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<Integer> getSelectedRowIndices() {
        return selectedRowIndices;
    }

    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        if (!loading) {
            final CidsBean cidsBean = getCidsBean(rowIndex);
            if (cidsBean == null) {
                return;
            }
            try {
                if (isColumnWithinBounds(columnIndex)) {
                    if ((columnIndex == 0) && isRowsSelectable()) {
                        final Boolean selected = (Boolean)value;
                        if (selected) {
                            selectedRowIndices.add(rowIndex);
                        } else if (selectedRowIndices.contains(rowIndex)) {
                            selectedRowIndices.remove(rowIndex);
                        }
                    } else {
                        final Object convertedValue;
                        final String columnProperty = getColumnProperty(columnIndex);
                        if (columnProperty != null) {
                            final Class columnClass = getColumnClass(columnIndex);
                            if ((value instanceof java.util.Date) && (columnClass != null)
                                        && java.sql.Date.class.equals(columnClass)) {
                                convertedValue = new java.sql.Date(((java.util.Date)value).getTime());
                            } else {
                                convertedValue = value;
                            }
                            cidsBean.setProperty(columnProperty, convertedValue);
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
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
        if ((columnIndex == 0) && isRowsSelectable()) {
            return Boolean.class;
        }
        return isColumnWithinBounds(columnIndex) ? columnClasses[columnIndex - (isRowsSelectable() ? 1 : 0)] : null;
    }
}
