/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import java.awt.Component;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieEditorAddSuchwortDialog extends javax.swing.JDialog {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Sb_stadtbildserieEditorAddSuchwortDialog.class);

    private static Sb_stadtbildserieEditorAddSuchwortDialog INSTANCE;

    //~ Instance fields --------------------------------------------------------

    private final Collection<CidsBean> beansToReturn = new ArrayList<CidsBean>();

    private TableRowSorter<TableModel> sorter;
    private MetaObject[] mos;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Sb_stadtbildserieEditorAddSuchwortDialog.
     *
     * @param  parent  DOCUMENT ME!
     * @param  modal   DOCUMENT ME!
     */
    private Sb_stadtbildserieEditorAddSuchwortDialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);

        try {
            loadListItems();
        } catch (final ConnectionException exception) {
            LOG.error("could not load list items", exception);
        }

        initComponents();

        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    jButton2.setEnabled(jTable1.getSelectedRowCount() > 0);
                }
            });

        getRootPane().setDefaultButton(jButton2);

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    doFilter();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    doFilter();
                }

                @Override
                public void insertUpdate(final DocumentEvent e) {
                    doFilter();
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Sb_stadtbildserieEditorAddSuchwortDialog getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Sb_stadtbildserieEditorAddSuchwortDialog(null, true);
        }
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     */
    private void doFilter() {
        if (jTextField1.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(new RowFilter<TableModel, Integer>() {

                    @Override
                    public boolean include(final RowFilter.Entry<? extends TableModel, ? extends Integer> entry) {
                        return ((LightweightMetaObject)entry.getValue(0)).toString()
                                    .toLowerCase()
                                    .contains(jTextField1.getText().toLowerCase());
                    }
                });
        }

        if ((sorter.getViewRowCount() == 1) && (jTable1.getSelectedRowCount() == 0)) {
            jTable1.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private TableModel createTableModel() {
        final Collection<MetaObject[]> dataList = new ArrayList<MetaObject[]>();
        for (final MetaObject mo : mos) {
            dataList.add(new MetaObject[] { mo });
        }
        final Object[][] data = dataList.toArray(new MetaObject[0][0]);
        final String[] columnNames = { null };
        return new DefaultTableModel(data, columnNames) {

                @Override
                public Class<?> getColumnClass(final int column) {
                    return getValueAt(0, column).getClass();
                }
            };
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    private void loadListItems() throws ConnectionException {
        final MetaClass metaClass = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "SB_SUCHWORT");
        if (metaClass != null) {
            mos = SessionManager.getProxy()
                        .getAllLightweightMetaObjectsForClass(metaClass.getID(),
                                SessionManager.getSession().getUser(),
                                new String[] { "NAME" },
                                "%1$2s");
        } else {
            LOG.warn("MetaClass is null. Probably the permissions for the class SB_SUCHWORT are missing.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditorAddSuchwortDialog.class,
                "Sb_stadtbildserieEditorAddSuchwortDialog.title")); // NOI18N
        setPreferredSize(new java.awt.Dimension(275, 400));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditorAddSuchwortDialog.class,
                "Sb_stadtbildserieEditorAddSuchwortDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel7.add(jButton1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditorAddSuchwortDialog.class,
                "Sb_stadtbildserieEditorAddSuchwortDialog.jButton2.text")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        jPanel7.add(jButton2, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(jPanel7, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditorAddSuchwortDialog.class,
                "Sb_stadtbildserieEditorAddSuchwortDialog.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel6.add(jLabel4, gridBagConstraints);

        jTextField1.setText(org.openide.util.NbBundle.getMessage(
                Sb_stadtbildserieEditorAddSuchwortDialog.class,
                "Sb_stadtbildserieEditorAddSuchwortDialog.jTextField1.text")); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jTextField1ActionPerformed(evt);
                }
            });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    jTextField1KeyPressed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jTextField1, gridBagConstraints);

        jTable1.setModel(createTableModel());
        sorter = new TableRowSorter<TableModel>(jTable1.getModel());

        final RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {

                @Override
                public boolean include(final RowFilter.Entry entry) {
                    return entry.getValue(0).toString().contains(jTextField1.getText());
                }
            };

        sorter.setRowFilter(filter);
        jTable1.setIntercellSpacing(new java.awt.Dimension(0, 0));
        jTable1.setRowSorter(sorter);
        jTable1.setShowHorizontalLines(false);
        jTable1.setShowVerticalLines(false);
        jTable1.setTableHeader(null);
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyPressed(final java.awt.event.KeyEvent evt) {
                    jTable1KeyPressed(evt);
                }
            });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jPanel6, gridBagConstraints);

        jProgressBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jProgressBar1.setBorderPainted(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jProgressBar1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel5, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        if (jButton2.isEnabled()) {
            jProgressBar1.setMaximum(jTable1.getSelectedRowCount());
            jProgressBar1.setValue(0);
            jTextField1.setEnabled(false);
            jTable1.setEnabled(false);
            jButton2.setEnabled(false);
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        int number = 0;
                        for (final int viewIndex : jTable1.getSelectedRows()) {
                            final int value = number++;
                            final int modelIndex = jTable1.convertRowIndexToModel(viewIndex);
                            if (modelIndex >= 0) {
                                final Object selectedItem = jTable1.getModel().getValueAt(modelIndex, 0);
                                if (selectedItem instanceof LightweightMetaObject) {
                                    beansToReturn.add(((LightweightMetaObject)selectedItem).getBean());
                                    SwingUtilities.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                jProgressBar1.setValue(value);
                                            }
                                        });
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        jProgressBar1.setValue(0);
                        jTextField1.setEnabled(true);
                        jTable1.setEnabled(true);
                        jButton2.setEnabled(true);
                        setVisible(false);
                    }
                }.execute();
        }
    } //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jTextField1ActionPerformed
        jButton2ActionPerformed(evt);
    }                                                                               //GEN-LAST:event_jTextField1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTextField1KeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_jTextField1KeyPressed
        final Component source = (Component)evt.getSource();
        if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE)) {
            final JTextField f = (JTextField)source;
            if (f.getText().isEmpty()) {
                setVisible(false);
            } else {
                f.setText("");
            }
        }
    }                                                                       //GEN-LAST:event_jTextField1KeyPressed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTable1KeyPressed(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_jTable1KeyPressed
        if ((evt.getKeyCode() != KeyEvent.VK_UP) && (evt.getKeyCode() != KeyEvent.VK_DOWN)
                    && (evt.getKeyCode() != KeyEvent.VK_SHIFT)
                    && (evt.getKeyCode() != KeyEvent.VK_CONTROL)) {
            jTextField1.requestFocus();
        }
    }                                                                   //GEN-LAST:event_jTable1KeyPressed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> showDialog() {
        beansToReturn.clear();
        StaticSwingTools.showDialog(this);
        jTable1.getSelectionModel().clearSelection();
        jTextField1.setText("");
        return new ArrayList<CidsBean>(beansToReturn);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  the command line arguments
     */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Sb_stadtbildserieEditorAddSuchwortDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Sb_stadtbildserieEditorAddSuchwortDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Sb_stadtbildserieEditorAddSuchwortDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Sb_stadtbildserieEditorAddSuchwortDialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final Sb_stadtbildserieEditorAddSuchwortDialog dialog =
                        new Sb_stadtbildserieEditorAddSuchwortDialog(new javax.swing.JFrame(), true);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                            @Override
                            public void windowClosing(final java.awt.event.WindowEvent e) {
                                System.exit(0);
                            }
                        });

                    dialog.setVisible(true);
                }
            });
    }
}
