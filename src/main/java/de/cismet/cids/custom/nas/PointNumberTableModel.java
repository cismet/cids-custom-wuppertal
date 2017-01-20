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
package de.cismet.cids.custom.nas;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PointNumberTableModel extends AbstractTableModel {

    //~ Static fields/initializers ---------------------------------------------

    private static final int COLUMNS = 2;

    //~ Instance fields --------------------------------------------------------

    private final List<CheckListItem> punktnummern;
    private int selectedCidsBeans = 0;
    private final boolean showDatum;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PunktNummerTableModel object.
     *
     * @param  punktnummern  DOCUMENT ME!
     * @param  showDatum     DOCUMENT ME!
     */
    public PointNumberTableModel(final List<CheckListItem> punktnummern, final boolean showDatum) {
        this.punktnummern = punktnummern;
        this.showDatum = showDatum;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int getRowCount() {
        if (punktnummern == null) {
            return 0;
        }
        return punktnummern.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int getColumnCount() {
        if (punktnummern == null) {
            return 0;
        }
        return COLUMNS;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   rowIndex     DOCUMENT ME!
     * @param   columnIndex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        if (punktnummern == null) {
            return null;
        }
        final CheckListItem bean = punktnummern.get(rowIndex);
        if (columnIndex == 0) {
            return bean.isSelected();
        } else {
            return "<html>" + bean.getPnr() + (showDatum ? (" [<i>" + bean.getAblaufdatum() + "</i>] ") : "");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  value   DOCUMENT ME!
     * @param  row     DOCUMENT ME!
     * @param  column  DOCUMENT ME!
     */
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        if (column != 0) {
            return;
        }

        final CheckListItem item = punktnummern.get(row);
        item.setSelected(!item.isSelected());
        if (item.isSelected()) {
            selectedCidsBeans++;
        } else {
            selectedCidsBeans--;
        }

        fireTableRowsUpdated(row, row);
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
        if (columnIndex == 0) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   row     DOCUMENT ME!
     * @param   column  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isCellEditable(final int row, final int column) {
        return column == 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedValues() {
        return selectedCidsBeans;
    }
}
