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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXDatePicker;

import org.jfree.chart.JFreeChart;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractCellEditor;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleMessungenTablePanel.MesswertTableCellEditor;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.server.GrundwassermessstelleMessungenSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GrundwassermessstelleMessungenTablePanel extends JPanel implements ConnectionContextStore, CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(GrundwassermessstelleMessungenTablePanel.class);

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private CidsBean cidsBean;
    private final Map<String, CidsBean> kategorieMap = new HashMap<>();
    private final Map<String, CidsBean> stoffMap = new HashMap<>();

    private final boolean editable;
    private boolean loading = true;
    private boolean messungenEnabled = false;

    private final ListSelectionListener listSelectionListener = new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent evt) {
                if (evt.getValueIsAdjusting()) {
                    return;
                }
                jXTable1.requestFocusInWindow();
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnRemove;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleMesswerteDiagrammPanel
        grundwassermessstelleMesswerteDiagrammPanel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private org.jdesktop.swingx.JXBusyLabel jxLBusyMeasure;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new GrundwassermessstelleTablePanel object.
     */
    public GrundwassermessstelleMessungenTablePanel() {
        this(false);
    }

    /**
     * Creates new form NewJPanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public GrundwassermessstelleMessungenTablePanel(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMessungenEnabled() {
        return messungenEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  loading  DOCUMENT ME!
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        refreshTabPane();
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    if (!loading) {
                        getDiagrammPanel().refreshChart();
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getStoffBean(final String schluessel) {
        return stoffMap.get(schluessel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getKategorieBean(final String schluessel) {
        return kategorieMap.get(schluessel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        grundwassermessstelleMesswerteDiagrammPanel1 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleMesswerteDiagrammPanel(
                getConnectionContext());
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        jPanel3 = new javax.swing.JPanel();
        btnRemove = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel5 = new javax.swing.JPanel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jxLBusyMeasure = new JXBusyLabel(new Dimension(64, 64));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new WrapLayout(WrapLayout.LEFT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(jPanel2, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.CardLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jXTable1.setModel(new MesswerteTableModel());
        jXTable1.setOpaque(false);
        jXTable1.setRowFilter(new MesswertRowFilter());
        jXTable1.setTerminateEditOnFocusLost(false);
        jXTable1.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusGained(final java.awt.event.FocusEvent evt) {
                    jXTable1FocusGained(evt);
                }
            });
        jXTable1.addKeyListener(new java.awt.event.KeyAdapter() {

                @Override
                public void keyTyped(final java.awt.event.KeyEvent evt) {
                    jXTable1KeyTyped(evt);
                }
            });
        jScrollPane1.setViewportView(jXTable1);
        jXTable1.setDefaultRenderer(Double.class, new MesswertTableCellRenderer());
        jXTable1.setDefaultEditor(Date.class, new DatumTableCellEditor());
        jXTable1.setDefaultEditor(Double.class, new MesswertTableCellEditor());

        jXTable1.getSelectionModel().addListSelectionListener(listSelectionListener);
        jXTable1.getColumnModel().getSelectionModel().addListSelectionListener(listSelectionListener);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        btnRemove.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel3.add(btnRemove, gridBagConstraints);

        btnAdd.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(btnAdd, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel1.add(jPanel3, gridBagConstraints);
        jPanel3.setVisible(editable);

        jPanel4.add(jPanel1, "table");

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(filler2, gridBagConstraints);

        jxLBusyMeasure.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jxLBusyMeasure.setBusy(true);
        jxLBusyMeasure.setPreferredSize(new java.awt.Dimension(64, 64));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel5.add(jxLBusyMeasure, gridBagConstraints);

        jPanel4.add(jPanel5, "progress");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(jPanel4, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddActionPerformed
        try {
            final CidsBean messungBean = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    "grundwassermessstelle_messung",
                    getConnectionContext());
            messungBean.setProperty("messstelle_id", cidsBean.getProperty("id"));
            if (getModel().getKategorieBean() != null) {
                messungBean.setProperty(
                    "kategorie_schluessel",
                    getModel().getKategorieBean().getProperty("schluessel"));
            }
            getModel().addMessung(messungBean);
            final int rowIndex = getModel().getRowIndex(messungBean);
            jXTable1.setRowSelectionInterval(rowIndex, rowIndex);
            jXTable1.scrollRowToVisible(rowIndex);
        } catch (final Exception ex) {
            LOG.error("error while creating new messung", ex);
        }
    }                                                                          //GEN-LAST:event_btnAddActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveActionPerformed
        final int rowIndex = (jXTable1.getSelectedRow() >= 0)
            ? jXTable1.convertRowIndexToModel(jXTable1.getSelectedRow()) : -1;
        if ((rowIndex >= 0)) {
            final CidsBean messungBean = getModel().getMessungBean(rowIndex);
            getModel().removeMessung(messungBean);
        }
    }                                                                             //GEN-LAST:event_btnRemoveActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXTable1FocusGained(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_jXTable1FocusGained
        final int row = jXTable1.getSelectedRow();
        final int col = jXTable1.getSelectedColumn();

        jXTable1.changeSelection(row, col, false, false);
        jXTable1.editCellAt(row, col);

        if (jXTable1.getCellEditor() instanceof MesswertTableCellEditor) {
            final MesswertTableCellEditor editor = (MesswertTableCellEditor)jXTable1.getCellEditor();
            final JTextField textField = editor.getFormattedTextField();
            textField.requestFocusInWindow();
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        textField.selectAll();
                    }
                });
        } else if (jXTable1.getCellEditor() instanceof DatumTableCellEditor) {
            final DatumTableCellEditor editor = (DatumTableCellEditor)jXTable1.getCellEditor();
            final JTextField textField = editor.getDatePicker().getEditor();
            textField.requestFocusInWindow();
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        textField.selectAll();
                    }
                });
        } else if (jXTable1.getCellEditor() instanceof DefaultCellEditor) {
            final DefaultCellEditor editor = (DefaultCellEditor)jXTable1.getCellEditor();
            final JTextField textField = (JTextField)editor.getComponent();
            textField.requestFocusInWindow();
            SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        textField.selectAll();
                    }
                });
        }
    } //GEN-LAST:event_jXTable1FocusGained

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXTable1KeyTyped(final java.awt.event.KeyEvent evt) { //GEN-FIRST:event_jXTable1KeyTyped
//        final TableCellEditor cellEditor = jXTable1.getCellEditor();
//        if ((KeyEvent.VK_TAB == evt.getKeyCode()) && (cellEditor != null)) {
//            cellEditor.stopCellEditing();
//        }
    } //GEN-LAST:event_jXTable1KeyTyped

    /**
     * DOCUMENT ME!
     */
    private void refreshTabPane() {
        if (loading) {
            ((CardLayout)jPanel4.getLayout()).show(jPanel4, "progress");
        } else {
            ((CardLayout)jPanel4.getLayout()).show(jPanel4, "table");
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void loadKategorien() throws Exception {
        setLoading(true);
        kategorieMap.clear();
        stoffMap.clear();
        jPanel2.removeAll();
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final MetaClass kategorieMc = CidsBean.getMetaClassFromTableName(
                            "WUNDA_BLAU",
                            "grundwassermessstelle_kategorie",
                            getConnectionContext());
                    final User user = SessionManager.getSession().getUser();
                    final MetaObject[] kategorieMos = SessionManager.getProxy()
                                .getAllLightweightMetaObjectsForClass(kategorieMc.getId(),
                                    user,
                                    new String[] { "id", "name" },
                                    "%1$2s",
                                    getConnectionContext());
                    final List<CidsBean> kategorieBeans = new ArrayList<>(kategorieMos.length);
                    for (final MetaObject kategorieMo : kategorieMos) {
                        if (kategorieMo != null) {
                            kategorieBeans.add(kategorieMo.getBean());
                        }
                    }

                    kategorieBeans.sort(new Comparator<CidsBean>() {

                            @Override
                            public int compare(final CidsBean cidsBean1, final CidsBean cidsBean2) {
                                final Integer o1 = (cidsBean1 != null) ? (Integer)cidsBean1.getProperty("reihenfolge")
                                                                       : null;
                                final Integer o2 = (cidsBean2 != null) ? (Integer)cidsBean2.getProperty("reihenfolge")
                                                                       : null;
                                if (o2 == null) {
                                    return 1;
                                }
                                if (o1 == null) {
                                    return -1;
                                }
                                return Integer.compare(o1, o2);
                            }
                        });
                    return kategorieBeans;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> kategorieBeans = get();
                        for (final CidsBean kategorieBean : kategorieBeans) {
                            kategorieMap.put((String)kategorieBean.getProperty("schluessel"), kategorieBean);
                            for (final CidsBean stoffBean : kategorieBean.getBeanCollectionProperty("stoffe")) {
                                stoffMap.put((String)stoffBean.getProperty("schluessel"), stoffBean);
                            }
                        }

                        final ButtonGroup bg = new ButtonGroup();
                        StoffgruppeButton firstButton = null;
                        CidsBean firstKategorieBean = null;
                        final List<String> kategorieSchluessels = new ArrayList<>(kategorieMap.keySet());
                        kategorieSchluessels.sort(new Comparator<String>() {

                                @Override
                                public int compare(final String schluessel1, final String schluessel2) {
                                    final CidsBean kategorie1 = getKategorieBean(schluessel1);
                                    final CidsBean kategorie2 = getKategorieBean(schluessel2);

                                    final Integer reihenfolge1 = (kategorie1 != null)
                                        ? (Integer)kategorie1.getProperty("reihenfolge") : null;
                                    final Integer reihenfolge2 = (kategorie2 != null)
                                        ? (Integer)kategorie2.getProperty("reihenfolge") : null;

                                    if ((reihenfolge1 == null) && (reihenfolge2 == null)) {
                                        return 0;
                                    } else if (reihenfolge1 == null) {
                                        return -1;
                                    } else if (reihenfolge2 == null) {
                                        return 1;
                                    } else {
                                        return Integer.compare(reihenfolge1, reihenfolge2);
                                    }
                                }
                            });
                        for (final String kategorieSchluessel : kategorieSchluessels) {
                            final CidsBean kategorieBean = getKategorieBean(kategorieSchluessel);
                            final StoffgruppeButton button = new StoffgruppeButton(kategorieBean);
                            if (firstButton == null) {
                                firstButton = button;
                                firstKategorieBean = kategorieBean;
                            }
                            jPanel2.add(button);
                            bg.add(button);
                        }

                        jPanel1.repaint();
                        if (firstButton != null) {
                            firstButton.setSelected(true);
                        }
                        setKategorie(firstKategorieBean);
                    } catch (final Exception ex) {
                        LOG.error("error while loading kategorie bean", ex);
                    } finally {
                        setLoading(false);
                    }
                }
            }.execute();
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            messungenEnabled = ObjectRendererUtils.hasUserPermissionOnMetaClass(CidsBean.getMetaClassFromTableName(
                        "WUNDA_BLAU",
                        "GRUNDWASSERMESSSTELLE_MESSUNG",
                        getConnectionContext()),
                    SessionManager.getSession().getUser(),
                    ObjectRendererUtils.PermissionType.READ);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        initComponents();
        if (messungenEnabled) {
            try {
                loadKategorien();
            } catch (Exception ex) {
                LOG.error("error while initializing context", ex);
            }
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MesswerteTableModel getModel() {
        return (MesswerteTableModel)jXTable1.getModel();
    }

    /**
     * DOCUMENT ME!
     */
    private void loadMessungen() {
        if (cidsBean != null) {
            setLoading(true);
            new SwingWorker<List, Void>() {

                    @Override
                    protected List doInBackground() throws Exception {
                        final List<CidsBean> messungBeans = new ArrayList<>();
                        final Integer messstelleId = (Integer)cidsBean.getProperty("id");
                        if (messstelleId != null) {
                            final GrundwassermessstelleMessungenSearch search =
                                new GrundwassermessstelleMessungenSearch(messstelleId);
                            final Collection<MetaObjectNode> messungMons = SessionManager.getProxy()
                                        .customServerSearch(search, getConnectionContext());

                            for (final MetaObjectNode messungMon : messungMons) {
                                final MetaObject messungMo = SessionManager.getProxy()
                                            .getMetaObject(messungMon.getObjectId(),
                                                messungMon.getClassId(),
                                                "WUNDA_BLAU",
                                                getConnectionContext());
                                final CidsBean messungBean = (CidsBean)messungMo.getBean();
                                messungBeans.add(messungBean);
                            }
                        }
                        return messungBeans;
                    }

                    @Override
                    protected void done() {
                        try {
                            final List<CidsBean> messungBeans = get();
                            setMessungBeans(messungBeans);
                        } catch (final Exception ex) {
                            LOG.error("error while loading messung beans", ex);
                        } finally {
                            setLoading(false);
                        }
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public GrundwassermessstelleMesswerteDiagrammPanel getDiagrammPanel() {
        return grundwassermessstelleMesswerteDiagrammPanel1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JFreeChart getChart() {
        return grundwassermessstelleMesswerteDiagrammPanel1.createChartPanel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  messungBeans  DOCUMENT ME!
     */
    private void setMessungBeans(final List<CidsBean> messungBeans) {
        getModel().setMessungBeans(messungBeans);
        grundwassermessstelleMesswerteDiagrammPanel1.refreshChart();
        if (jXTable1.getRowSorter().getSortKeys().isEmpty()) {
            jXTable1.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        loadMessungen();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kategorieBean  DOCUMENT ME!
     */
    private void setKategorie(final CidsBean kategorieBean) {
        getModel().setKategorieBean(kategorieBean);
        grundwassermessstelleMesswerteDiagrammPanel1.setMessungBeans(getMessungBeans());
        grundwassermessstelleMesswerteDiagrammPanel1.setStoffBeans(getKategorieBean().getBeanCollectionProperty(
                "stoffe"));
        grundwassermessstelleMesswerteDiagrammPanel1.refreshChart();
        if (jXTable1.getRowSorter().getSortKeys().isEmpty()) {
            jXTable1.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getKategorieBean() {
        return getModel().getKategorieBean();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMessungBeans() {
        final List<CidsBean> selectedMessungBeans = new ArrayList<>();
//        for (final int rowIndex : jXTable1.getSelectedRows()) {
//            final CidsBean messungBean = getModel().getMessungBean(jXTable1.convertRowIndexToModel(rowIndex));
//            selectedMessungBeans.add(messungBean);
//        }
        for (int rowIndex = 0; rowIndex < jXTable1.getRowCount(); rowIndex++) {
            final CidsBean messungBean = getModel().getMessungBean(jXTable1.convertRowIndexToModel(rowIndex));
            selectedMessungBeans.add(messungBean);
        }
        return selectedMessungBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JPanel getButtonPanel() {
        return jPanel2;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class StoffgruppeButton extends JToggleButton {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StoffgruppeButton object.
         *
         * @param  kategorieBean  DOCUMENT ME!
         */
        StoffgruppeButton(final CidsBean kategorieBean) {
            super(kategorieBean.toString());
            addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        setKategorie(kategorieBean);
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswertRowFilter extends RowFilter<TableModel, Integer> {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean include(final Entry<? extends TableModel, ? extends Integer> entry) {
            final CidsBean messungBean = getModel().getMessungBeans().get(entry.getIdentifier());
            final CidsBean kategorieBean = getModel().getKategorieBean();
            return ((kategorieBean != null)
                            && kategorieBean.equals(
                                getKategorieBean((String)messungBean.getProperty("kategorie_schluessel"))));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswerteTableModel extends AbstractTableModel {

        //~ Instance fields ----------------------------------------------------

        private List<CidsBean> messungBeans;
        private CidsBean kategorieBean;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MesswerteTableModel object.
         */
        public MesswerteTableModel() {
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  messungBeans  DOCUMENT ME!
         */
        public void setMessungBeans(final List<CidsBean> messungBeans) {
            this.messungBeans = messungBeans;
            fireTableDataChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  kategorieBean  DOCUMENT ME!
         */
        public void setKategorieBean(final CidsBean kategorieBean) {
            this.kategorieBean = kategorieBean;
            fireTableStructureChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @param   messungBean  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getRowIndex(final CidsBean messungBean) {
            return messungBeans.indexOf(messungBean);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  messungBean  DOCUMENT ME!
         */
        public void addMessung(final CidsBean messungBean) {
            messungBeans.add(messungBean);
            fireTableDataChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  messungBean  DOCUMENT ME!
         */
        public void removeMessung(final CidsBean messungBean) {
            messungBeans.remove(messungBean);
            fireTableDataChanged();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getKategorieBean() {
            return kategorieBean;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public List<CidsBean> getMessungBeans() {
            return messungBeans;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private boolean showBemerkung() {
            return Boolean.TRUE.equals(kategorieBean.getProperty("show_bemerkung_messung"));
        }

        @Override
        public String getColumnName(final int columnIndex) {
            if (columnIndex == 0) {
                return "Datum";
            } else if (showBemerkung() && (columnIndex == (getColumnCount() - 1))) {
                return "Bemerkung";
            } else {
                final CidsBean stoffBean = getStoffBean(columnIndex);
                return (String)stoffBean.getProperty("name");
            }
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return editable;
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == 0) {
                return Date.class;
            } else if (showBemerkung() && (columnIndex == (getColumnCount() - 1))) {
                return String.class;
            } else {
                return Double.class;
            }
        }

        @Override
        public int getRowCount() {
            return (messungBeans != null) ? messungBeans.size() : 0;
        }

        @Override
        public int getColumnCount() {
            return (kategorieBean != null)
                ? (kategorieBean.getBeanCollectionProperty("stoffe").size() + 1 + ((showBemerkung()) ? 1 : 0)) : 0;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   columnIndex  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getStoffBean(final int columnIndex) {
            final int stoffIndex = columnIndex - 1;
            return kategorieBean.getBeanCollectionProperty("stoffe").get(stoffIndex);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   rowIndex  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getMessungBean(final int rowIndex) {
            return messungBeans.get(rowIndex);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   rowIndex     DOCUMENT ME!
         * @param   columnIndex  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public CidsBean getMesswertBean(final int rowIndex, final int columnIndex) {
            if ((columnIndex > 0) && (columnIndex < (getColumnCount() - ((showBemerkung()) ? 1 : 0)))) {
                final CidsBean stoffBean = getStoffBean(columnIndex);
                final CidsBean messungBean = getMessungBean(rowIndex);
                for (final CidsBean messwertBean : messungBean.getBeanCollectionProperty("messwerte")) {
                    if (stoffBean.equals(
                                    GrundwassermessstelleMessungenTablePanel.this.getStoffBean(
                                        (String)messwertBean.getProperty("stoff_schluessel")))) {
                        return messwertBean;
                    }
                }
                return null;
            } else {
                return null;
            }
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean messungBean = getMessungBean(rowIndex);
            if (columnIndex == 0) {
                return messungBean.getProperty("datum");
            } else if (showBemerkung() && (columnIndex == (getColumnCount() - 1))) {
                return messungBean.getProperty("bemerkung");
            } else {
                final CidsBean messwertBean = getMesswertBean(rowIndex, columnIndex);
                return (messwertBean != null) ? (Double)messwertBean.getProperty("wert") : null;
            }
        }

        @Override
        public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
            super.setValueAt(value, rowIndex, columnIndex);

            try {
                final CidsBean messungBean = getMessungBean(rowIndex);
                if (columnIndex == 0) {
                    messungBean.setProperty("datum", new java.sql.Date(((Date)value).getTime()));
                } else if (showBemerkung() && (columnIndex == (getColumnCount() - 1))) {
                    messungBean.setProperty("bemerkung", (String)value);
                } else {
                    final CidsBean foundMesswertBean = getMesswertBean(rowIndex, columnIndex);
                    if (foundMesswertBean != null) {
                        if (value != null) {
                            foundMesswertBean.setProperty("wert", (Double)value);
                        } else {
                            messungBean.getBeanCollectionProperty("messwerte").remove(foundMesswertBean);
                        }
                    } else {
                        try {
                            final CidsBean stoffBean = getStoffBean(columnIndex);
                            if (value != null) {
                                final CidsBean messwertBean = CidsBean.createNewCidsBeanFromTableName(
                                        "WUNDA_BLAU",
                                        "grundwassermessstelle_messwert",
                                        getConnectionContext());
                                if (stoffBean != null) {
                                    messwertBean.setProperty("stoff_schluessel", stoffBean.getProperty("schluessel"));
                                }
                                messwertBean.setProperty("wert", (Double)value);
                                messungBean.getBeanCollectionProperty("messwerte").add(messwertBean);
                            }
                        } catch (final Exception ex) {
                            LOG.warn("error while creating messwertBean", ex);
                        }
                    }
                }
                getDiagrammPanel().refreshChart();
            } catch (final Exception ex) {
                LOG.warn("could not update value", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswertTableCellRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(
                final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int rowIndex,
                final int columnIndex) {
            final JLabel label = (JLabel)super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    rowIndex,
                    columnIndex);

            final CidsBean messungBean = getModel().getMessungBeans()
                        .get(jXTable1.getRowSorter().convertRowIndexToModel(rowIndex));
            final CidsBean stoffBean = getModel().getStoffBean(columnIndex);

            for (final CidsBean messwertBean : messungBean.getBeanCollectionProperty("messwerte")) {
                if (stoffBean.equals(getStoffBean((String)messwertBean.getProperty("stoff_schluessel")))) {
                    final Double wert = (Double)messwertBean.getProperty("wert");
                    final MesswertNumberFormat format = new MesswertNumberFormat((Integer)stoffBean.getProperty(
                                "nachkommastellen"));
                    label.setHorizontalAlignment(SwingConstants.TRAILING);
                    final String einheit = (String)stoffBean.getProperty("einheit");
                    label.setText((wert != null) ? (format.format(wert) + ((einheit != null) ? (" " + einheit) : ""))
                                                 : null);
                    break;
                }
            }
            return label;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswertNumberFormat extends NumberFormat {

        //~ Instance fields ----------------------------------------------------

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MesswertNumberFormat object.
         */
        MesswertNumberFormat() {
            this(null);
        }

        /**
         * Creates a new MesswertNumberFormat object.
         *
         * @param  nachkommastellen  DOCUMENT ME!
         */
        MesswertNumberFormat(final Integer nachkommastellen) {
            if (nachkommastellen != null) {
                nf.setMinimumFractionDigits(nachkommastellen);
                nf.setMaximumFractionDigits(nachkommastellen);
            }
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
            return new StringBuffer(NumberFormat.getNumberInstance(Locale.GERMAN).format(number, toAppendTo, pos)
                            .toString().replaceFirst("-", "< "));
        }

        @Override
        public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
            return new StringBuffer(NumberFormat.getNumberInstance(Locale.GERMAN).format(number, toAppendTo, pos)
                            .toString().replaceFirst("-", "< "));
        }

        @Override
        public Number parse(final String source, final ParsePosition parsePosition) {
            final String newSource;
            if (source == null) {
                newSource = null;
            } else if (source.trim().startsWith("< ")) {
                newSource = source.trim().replaceFirst("< ", "-");
            } else if (source.trim().startsWith("<")) {
                newSource = source.trim().replaceFirst("<", "-");
            } else if (source.trim().isEmpty()) {
                return null;
            } else {
                newSource = source.trim();
            }
            return NumberFormat.getNumberInstance(Locale.GERMAN).parse(newSource, parsePosition);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswertNumberFormatter extends NumberFormatter {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MesswertNumberFormatter object.
         */
        public MesswertNumberFormatter() {
            super(new MesswertNumberFormat());
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object stringToValue(final String string) throws ParseException {
            if ((string == null) || string.trim().isEmpty()) {
                return null;
            }
            return super.stringToValue(string);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class MesswertTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        //~ Instance fields ----------------------------------------------------

        private final JFormattedTextField formattedTextField;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MesswertTableCellEditor object.
         */
        public MesswertTableCellEditor() {
            formattedTextField = new JFormattedTextField(new MesswertNumberFormatter());
            formattedTextField.setHorizontalAlignment(JFormattedTextField.RIGHT);
            formattedTextField.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        stopCellEditing();
                    }
                });
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final int rowIndex,
                final int columnIndex) {
            formattedTextField.setValue((value != null) ? ((Number)value).doubleValue() : null);
            return formattedTextField;
        }

        @Override
        public boolean isCellEditable(final EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent)anEvent).getClickCount() >= 2;
            }
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                formattedTextField.commitEdit();
            } catch (final ParseException ex) {
            }
            return (formattedTextField.getValue() != null) ? ((Number)formattedTextField.getValue()).doubleValue()
                                                           : null;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public JFormattedTextField getFormattedTextField() {
            return formattedTextField;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class DatumTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        //~ Instance fields ----------------------------------------------------

        final JXDatePicker datePicker = new JXDatePicker();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DatumTableCellEditor object.
         */
        public DatumTableCellEditor() {
            datePicker.getEditor().addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        stopCellEditing();
                    }
                });
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellEditorComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final int rowIndex,
                final int vColIndex) {
            final Date date = (Date)value;
            datePicker.setDate(date);
            return datePicker;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                datePicker.commitEdit();
            } catch (ParseException ex) {
            }
            return datePicker.getDate();
        }

        @Override
        public boolean isCellEditable(final EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                return ((MouseEvent)anEvent).getClickCount() >= 2;
            }
            return true;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public JXDatePicker getDatePicker() {
            return datePicker;
        }
    }
}
