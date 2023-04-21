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
package de.cismet.cids.custom.objecteditors.wunda_blau.albo;

import java.awt.Component;
import java.awt.event.KeyEvent;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.custom.wunda_blau.search.server.AbstractMonToLwmoSearch;

import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ComboBoxFilterDialog extends javax.swing.JDialog implements ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    private final JComboBox comboBox;
    private final AbstractMonToLwmoSearch search;

    private final ConnectionContext connectionContext;
    private ComboBoxFilterDialogEnabledFilter filter = null;

    private SwingWorker<Void, Void> refreshWorker;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JComboBox<String> cbSearch;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ComboBoxFilterDialog object.
     */
    public ComboBoxFilterDialog() {
        this(null, null, null, ConnectionContext.createDummy());
    }

    /**
     * Creates a new ComboBoxFilterDialog object.
     *
     * @param  search             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ComboBoxFilterDialog(final AbstractMonToLwmoSearch search, final ConnectionContext connectionContext) {
        this(null, search, null, connectionContext);
    }

    /**
     * Creates a new ComboBoxFilterDialog object.
     *
     * @param  comboBox           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ComboBoxFilterDialog(final JComboBox comboBox, final ConnectionContext connectionContext) {
        this(comboBox, null, null, connectionContext);
    }

    /**
     * Creates a new ComboBoxFilterDialog object.
     *
     * @param  search             DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ComboBoxFilterDialog(final AbstractMonToLwmoSearch search,
            final String title,
            final ConnectionContext connectionContext) {
        this(null, search, title, connectionContext);
    }

    /**
     * Creates a new ComboBoxFilterDialog object.
     *
     * @param  comboBox           DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ComboBoxFilterDialog(final JComboBox comboBox,
            final String title,
            final ConnectionContext connectionContext) {
        this(comboBox, null, title, connectionContext);
    }
    /**
     * Creates new form ComboBoxFilterDialog.
     *
     * @param  comboBox           DOCUMENT ME!
     * @param  search             DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ComboBoxFilterDialog(final JComboBox comboBox,
            final AbstractMonToLwmoSearch search,
            final String title,
            final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.search = search;

        initComponents();

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                private int lastSelectedRow = -1;

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    final int selectedRow = table.getSelectedRow();

                    if (selectedRow > 0) {
                        int step = -1;
                        int index = selectedRow;
                        int indexModel = getRowSorter().convertRowIndexToModel(selectedRow);
                        String selectedValue = String.valueOf(
                                ComboBoxFilterDialog.this.comboBox.getModel().getElementAt(
                                    getRowSorter().convertRowIndexToModel(selectedRow)));

                        if (lastSelectedRow < selectedRow) {
                            step = 1;
                        }
                        while ((filter != null) && !filter.selectionOfDisabledElementsAllowed()
                                    && !filter.isEnabled(selectedValue, indexModel)) {
                            index = index + step;
                            indexModel = ((index != -1) ? getRowSorter().convertRowIndexToModel(index) : -1);
                            selectedValue = String.valueOf(
                                    ComboBoxFilterDialog.this.comboBox.getModel().getElementAt(indexModel));

                            if ((index < 0) || (index >= table.getRowCount())) {
                                index = lastSelectedRow;
                                break;
                            }
                        }
                        if (index != -1) {
                            table.getSelectionModel().setSelectionInterval(index, index);
                        }
                    }
                    lastSelectedRow = table.getSelectedRow();
                }
            });
        this.comboBox = (search != null) ? cbSearch : comboBox;

        setTitle((title != null) ? title : "Auswahlfilter");

        final TableRowSorter sorter = new TableRowSorter(getSelectionTableModel());
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        sorter.setRowFilter(new RowFilter<TableModel, Integer>() {

                @Override
                public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                    final String filterText = txtFilter.getText().trim();
                    if ((entry == null) || (entry.getValue(0) == null)) {
                        return false;
                    } else if (filterText.isEmpty()) {
                        return true;
                    } else {
                        return (entry.getValue(0)).toString().toLowerCase().contains(filterText.toLowerCase());
                    }
                }
            });
        getTable().setRowSorter(sorter);

        getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        btnApply.setEnabled(e.getFirstIndex() >= 0);
                    }
                }
            });

        getTable().getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component c = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);
                    final int modelRow = getRowSorter().convertRowIndexToModel(row);

                    if ((filter != null) && !filter.isEnabled(value, modelRow)) {
                        c.setEnabled(false);

                        if (c instanceof JLabel) {
                            ((JLabel)c).setToolTipText(filter.getTooltip(value, modelRow));
                        }
                    } else if (filter != null) {
                        c.setEnabled(true);

                        if (c instanceof JLabel) {
                            ((JLabel)c).setToolTipText(filter.getTooltip(value, modelRow));
                        }
                    }

                    return c;
                }
            });

        getRootPane().setDefaultButton(btnApply);
        refresh();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (search != null) {
            if (refreshWorker != null) {
                refreshWorker.cancel(true);
            }
            refreshWorker = new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        ((FastBindableReferenceCombo)cbSearch).setMetaClassFromTableName(search.getDomain(),
                            search.getTableName());
                        ((FastBindableReferenceCombo)cbSearch).refreshModel();
                        return null;
                    }

                    @Override
                    protected void done() {
                        getSelectionTableModel().fireTableDataChanged();
                        txtFilter.requestFocus();
                    }
                };
            txtFilter.setText("");
            cbSearch.setModel(new DefaultComboBoxModel<>(new String[] { "Objekte werden geladen..." }));
            refreshWorker.execute();
        } else {
            getSelectionTableModel().fireTableDataChanged();
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TableModel getSelectionTableModel() {
        return (TableModel)table.getModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TableRowSorter getRowSorter() {
        return (TableRowSorter)table.getRowSorter();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ListSelectionModel getSelectionModel() {
        return table.getSelectionModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private JTable getTable() {
        return table;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JComboBox getComboBox() {
        return comboBox;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        cbSearch = (search != null)
            ? new FastBindableReferenceCombo(
                search,
                search.getRepresentationPattern(),
                search.getRepresentationFields()) : new FastBindableReferenceCombo();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        table.setModel(new TableModel());
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setTableHeader(null);
        table.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    tableMouseClicked(evt);
                }
            });
        table.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    tableKeyPressed(evt);
                }
            });
        jScrollPane1.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        txtFilter.setText(org.openide.util.NbBundle.getMessage(
                ComboBoxFilterDialog.class,
                "ComboBoxFilterDialog.txtFilter.text")); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyTyped(final java.awt.event.KeyEvent evt) {
                    txtFilterKeyTyped(evt);
                }
                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    txtFilterKeyPressed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtFilter, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnApply,
            org.openide.util.NbBundle.getMessage(ComboBoxFilterDialog.class, "ComboBoxFilterDialog.btnApply.text")); // NOI18N
        btnApply.setEnabled(false);
        btnApply.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnApplyActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(btnApply, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCancel,
            org.openide.util.NbBundle.getMessage(ComboBoxFilterDialog.class, "ComboBoxFilterDialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel2.add(btnCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tableKeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_tableKeyPressed
        if (KeyEvent.VK_ENTER == evt.getKeyCode()) {
            evt.consume();
            btnApplyActionPerformed(null);
        } else if (KeyEvent.VK_UP == evt.getKeyCode()) {
            if (table.getSelectedRow() == 0) {
                txtFilter.requestFocus();
            }
        } else if (KeyEvent.VK_LEFT == evt.getKeyCode()) {
            txtFilter.requestFocus();
        } else if (KeyEvent.VK_ESCAPE == evt.getKeyCode()) {
            dispose();
        }
    }                                                                 //GEN-LAST:event_tableKeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtFilterKeyTyped(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_txtFilterKeyTyped
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getRowSorter().sort();

                    final boolean singleSelected = getRowSorter().getViewRowCount() == 1;
                    final int rowIndex = singleSelected ? 0 : -1;
                    getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
                    btnApply.setEnabled(singleSelected);
                }
            });
    } //GEN-LAST:event_txtFilterKeyTyped

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtFilterKeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_txtFilterKeyPressed
        if ((KeyEvent.VK_DOWN == evt.getKeyCode()) || (KeyEvent.VK_RIGHT == evt.getKeyCode())) {
            getTable().requestFocus();
            getSelectionModel().setSelectionInterval(0, 0);
        } else if (KeyEvent.VK_ESCAPE == evt.getKeyCode()) {
            dispose();
        }
    }                                                                     //GEN-LAST:event_txtFilterKeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnApplyActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnApplyActionPerformed
        if (getTable().getSelectedRow() >= 0) {
            final RowSorter rowSorter = getRowSorter();
            getComboBox().setSelectedIndex(rowSorter.convertRowIndexToModel(getTable().getSelectedRow()));
            dispose();
        }
    }                                                                            //GEN-LAST:event_btnApplyActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelActionPerformed
        dispose();
    }                                                                             //GEN-LAST:event_btnCancelActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void tableMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_tableMouseClicked
        if (evt.getClickCount() == 2) {
            if (getTable().getSelectedRow() >= 0) {
                final RowSorter rowSorter = getRowSorter();
                getComboBox().setSelectedIndex(rowSorter.convertRowIndexToModel(getTable().getSelectedRow()));
                dispose();
            }
        }
    }                                                                     //GEN-LAST:event_tableMouseClicked

    @Override
    public void setVisible(final boolean b) {
        if (getComboBox() != null) {
            getComboBox().setSelectedIndex(-1);
        }
        super.setVisible(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object showAndGetSelected() {
        if (getComboBox().getSelectedItem() != null) {
            table.requestFocus();
        } else {
            txtFilter.requestFocus();
        }
        StaticSwingTools.showDialog(getParent(), this, true);
        return (getComboBox() != null) ? getComboBox().getSelectedItem() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  filter  DOCUMENT ME!
     */
    public void setEnabledFilter(final ComboBoxFilterDialogEnabledFilter filter) {
        this.filter = filter;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   comboBox           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object showForCombobox(final JComboBox comboBox, final ConnectionContext connectionContext) {
        return showForCombobox(comboBox, null, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   comboBox           DOCUMENT ME!
     * @param   title              DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object showForCombobox(final JComboBox comboBox,
            final String title,
            final ConnectionContext connectionContext) {
        return new ComboBoxFilterDialog(comboBox, title, connectionContext).showAndGetSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   comboBox           DOCUMENT ME!
     * @param   title              DOCUMENT ME!
     * @param   filter             DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object showForCombobox(final JComboBox comboBox,
            final String title,
            final ComboBoxFilterDialogEnabledFilter filter,
            final ConnectionContext connectionContext) {
        final ComboBoxFilterDialog dialog = new ComboBoxFilterDialog(comboBox, title, connectionContext);
        dialog.setEnabledFilter(filter);
        return dialog.showAndGetSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   search             DOCUMENT ME!
     * @param   title              DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Object showForSearch(final AbstractMonToLwmoSearch search,
            final String title,
            final ConnectionContext connectionContext) {
        return new ComboBoxFilterDialog(search, title, connectionContext).showAndGetSelected();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TableModel extends AbstractTableModel {

        //~ Methods ------------------------------------------------------------

        @Override
        public int getRowCount() {
            return (comboBox != null) ? comboBox.getModel().getSize() : 0;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            return (comboBox != null)
                ? ((comboBox.getModel().getElementAt(rowIndex) != null)
                    ? comboBox.getModel().getElementAt(rowIndex).toString() : null) : null;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            return String.class;
        }

        @Override
        public String getColumnName(final int column) {
            return null;
        }
    }
}
