/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.utils;

import java.awt.Component;
import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cidsx.server.search.builtin.legacy.LightweightMetaObjectsSearch;

/**
 * A table cell editor that shows a bindable combobox.
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class FastBindableReferenceComboCellEditor extends AbstractCellEditor implements TableCellEditor {

    //~ Instance fields --------------------------------------------------------

    private final FastBindableReferenceCombo comboBox;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FastBindableReferenceComboCellEditor object.
     *
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    public FastBindableReferenceComboCellEditor() {
        throw new UnsupportedOperationException("Not supported yet.");    // To change body of generated methods, choose
                                                                          // Tools | Templates.
    }

    /**
     * Creates a new DefaultBindableComboboxCellEditor object.
     *
     * @param  lwsSearch  DOCUMENT ME!
     * @param  pattern    DOCUMENT ME!
     * @param  fields     DOCUMENT ME!
     */
    public FastBindableReferenceComboCellEditor(final LightweightMetaObjectsSearch lwsSearch,
            final String pattern,
            final String[] fields) {
        comboBox = new FastBindableReferenceCombo(lwsSearch, pattern, fields);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean isCellEditable(final EventObject anEvent) {
        if (anEvent instanceof MouseEvent) {
            return ((MouseEvent)anEvent).getClickCount() >= 2;
        }
        return true;
    }

    @Override
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table,
            final Object value,
            final boolean isSelected,
            final int row,
            final int column) {
        comboBox.setSelectedItem(value);

        return comboBox;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FastBindableReferenceCombo getComboBox() {
        return comboBox;
    }
}
