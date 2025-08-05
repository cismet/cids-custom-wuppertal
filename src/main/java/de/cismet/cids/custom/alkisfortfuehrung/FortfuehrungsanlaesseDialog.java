/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.alkisfortfuehrung;

import Sirius.navigator.connection.SessionManager;

import Sirius.util.collections.MultiMap;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.BrowserLauncher;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class FortfuehrungsanlaesseDialog extends javax.swing.JDialog implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(FortfuehrungsanlaesseDialog.class);
    private static final float FLURSTUECKBUFFER_FOR_KASSENZEICHEN_GEOMSEARCH = -0.2f;
    private static final int SRID = 25832;

    //~ Instance fields --------------------------------------------------------

    private boolean lockDateButtons = false;
    private final WKTReader wktreader = new WKTReader();
    private final MultiMap geomsMap = new MultiMap();

    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCloseDialog;
    private javax.swing.JToggleButton btnLastMonth;
    private javax.swing.JToggleButton btnLastWeek;
    private javax.swing.JButton btnRefreshAnlaesse;
    private javax.swing.JToggleButton btnThisMonth;
    private javax.swing.JToggleButton btnThisWeek;
    private javax.swing.ButtonGroup buttonGroup1;
    private org.jdesktop.swingx.JXDatePicker dpiFrom;
    private org.jdesktop.swingx.JXDatePicker dpiTo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblDokumentLink;
    private javax.swing.JPanel panDetail;
    private javax.swing.JPanel panMaster;
    private javax.swing.JPanel panMasterDetail;
    private javax.swing.JPanel panPeriod;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form FortfuehrungsanlaesseDialog.
     *
     * @param  parent             DOCUMENT ME!
     * @param  modal              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    protected FortfuehrungsanlaesseDialog(final Frame parent,
            final boolean modal,
            final ConnectionContext connectionContext) {
        super(parent, modal);
        this.connectionContext = connectionContext;

        initComponents();

        final Highlighter istAbgearbeitetHighlighter = new IstAbgearbeitetHighlighter();
        jXTable1.setHighlighters(istAbgearbeitetHighlighter);

        jXTable1.setModel(new FortfuehrungenTableModel());

        jXTable1.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {

                @Override
                public Component getTableCellRendererComponent(final JTable table,
                        final Object value,
                        final boolean isSelected,
                        final boolean hasFocus,
                        final int row,
                        final int column) {
                    final Component res = super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column);

                    if ((value instanceof Date) && (res instanceof JLabel)) {
                        ((JLabel)res).setText(new SimpleDateFormat("dd.MM.yyyy").format((Date)value));
                        return res;
                    }

                    return res;
                }
            });
        jXTable1.getColumnModel().getColumn(1).setCellRenderer(jXTable1.getDefaultRenderer(String.class));
        jXTable1.getColumnModel().getColumn(2).setCellRenderer(jXTable1.getDefaultRenderer(String.class));
        jXTable1.getColumnModel().getColumn(3).setCellRenderer(jXTable1.getDefaultRenderer(String.class));

        jXTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
        jXTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jXTable1.getColumnModel().getColumn(2).setPreferredWidth(200);
        jXTable1.getColumnModel().getColumn(3).setPreferredWidth(200);

        jXTable1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jXTable1.setDragEnabled(false);

        jXTable1.getTableHeader().setResizingAllowed(true);
        jXTable1.getTableHeader().setReorderingAllowed(false);
        jXTable1.setSortOrder(0, SortOrder.ASCENDING);

        jXTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    // If cell selection is enabled, both row and column change events are fired
                    if ((e.getSource() == jXTable1.getSelectionModel()) && jXTable1.getRowSelectionAllowed()) {
                        fortfuehrungsTableListSelectionChanged(e);
                    }
                }
            });

        jProgressBar1.setVisible(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panPeriod = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dpiFrom = new org.jdesktop.swingx.JXDatePicker();
        dpiTo = new org.jdesktop.swingx.JXDatePicker();
        jPanel2 = new javax.swing.JPanel();
        btnThisWeek = new javax.swing.JToggleButton();
        btnLastWeek = new javax.swing.JToggleButton();
        btnThisMonth = new javax.swing.JToggleButton();
        btnLastMonth = new javax.swing.JToggleButton();
        jPanel7 = new javax.swing.JPanel();
        btnRefreshAnlaesse = new javax.swing.JButton();
        panMasterDetail = new javax.swing.JPanel();
        panMaster = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
        panDetail = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblDokumentLink = new javax.swing.JLabel();
        jPanel11 = getObjectsPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnCloseDialog = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel9 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(900, 643));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panPeriod.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.panPeriod.border.title"))); // NOI18N
        panPeriod.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panPeriod.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panPeriod.add(jLabel2, gridBagConstraints);

        dpiFrom.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dpiFromPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 10);
        panPeriod.add(dpiFrom, gridBagConstraints);

        dpiTo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dpiToPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 10);
        panPeriod.add(dpiTo, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridLayout(2, 3, 5, 5));

        buttonGroup1.add(btnThisWeek);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnThisWeek,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnThisWeek.text")); // NOI18N
        btnThisWeek.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnThisWeekActionPerformed(evt);
                }
            });
        jPanel2.add(btnThisWeek);

        buttonGroup1.add(btnLastWeek);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnLastWeek,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnLastWeek.text")); // NOI18N
        btnLastWeek.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLastWeekActionPerformed(evt);
                }
            });
        jPanel2.add(btnLastWeek);

        buttonGroup1.add(btnThisMonth);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnThisMonth,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnThisMonth.text")); // NOI18N
        btnThisMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnThisMonthActionPerformed(evt);
                }
            });
        jPanel2.add(btnThisMonth);

        buttonGroup1.add(btnLastMonth);
        org.openide.awt.Mnemonics.setLocalizedText(
            btnLastMonth,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnLastMonth.text")); // NOI18N
        btnLastMonth.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnLastMonthActionPerformed(evt);
                }
            });
        jPanel2.add(btnLastMonth);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panPeriod.add(jPanel2, gridBagConstraints);

        jPanel7.setPreferredSize(new java.awt.Dimension(50, 10));

        final javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                50,
                Short.MAX_VALUE));
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                86,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        panPeriod.add(jPanel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnRefreshAnlaesse,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnRefreshAnlaesse.text")); // NOI18N
        btnRefreshAnlaesse.setEnabled(false);
        btnRefreshAnlaesse.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRefreshAnlaesseActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panPeriod.add(btnRefreshAnlaesse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panPeriod, gridBagConstraints);

        panMasterDetail.setLayout(new java.awt.GridBagLayout());

        panMaster.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.panMaster.border.title"))); // NOI18N
        panMaster.setLayout(new java.awt.GridBagLayout());

        jXTable1.setEnabled(false);
        jScrollPane1.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        panMaster.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        panMasterDetail.add(panMaster, gridBagConstraints);

        panDetail.setBorder(null);
        panDetail.setVerifyInputWhenFocusTarget(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    FortfuehrungsanlaesseDialog.class,
                    "FortfuehrungsanlaesseDialog.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDokumentLink,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.lblDokumentLink.text")); // NOI18N
        lblDokumentLink.setEnabled(false);
        lblDokumentLink.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblDokumentLinkMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel3.add(lblDokumentLink, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panDetail.add(jPanel3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panDetail.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panMasterDetail.add(panDetail, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panMasterDetail, gridBagConstraints);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jPanel6, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCloseDialog,
            org.openide.util.NbBundle.getMessage(
                FortfuehrungsanlaesseDialog.class,
                "FortfuehrungsanlaesseDialog.btnCloseDialog.text")); // NOI18N
        btnCloseDialog.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCloseDialogActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(btnCloseDialog, gridBagConstraints);

        jProgressBar1.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel8.add(jProgressBar1, gridBagConstraints);

        jPanel9.setOpaque(false);

        final javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                549,
                Short.MAX_VALUE));
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                29,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRefreshAnlaesseActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRefreshAnlaesseActionPerformed
        refreshFortfuehrungsList();
    }                                                                                      //GEN-LAST:event_btnRefreshAnlaesseActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnThisWeekActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnThisWeekActionPerformed
        final Calendar calendar = Calendar.getInstance();

        final Date toDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnThisWeekActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLastWeekActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLastWeekActionPerformed
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final Date toDate = calendar.getTime();

        calendar.add(Calendar.DATE, -7);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnLastWeekActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnThisMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnThisMonthActionPerformed
        final Calendar calendar = Calendar.getInstance();

        final Date toDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnThisMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnLastMonthActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnLastMonthActionPerformed
        final Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date toDate = calendar.getTime();

        calendar.add(Calendar.MONTH, -1);
        final Date fromDate = calendar.getTime();

        try {
            lockDateButtons = true;
            dpiFrom.setDate(fromDate);
            dpiTo.setDate(toDate);
        } finally {
            lockDateButtons = false;
        }

        periodChanged();
        refreshFortfuehrungsList();
    } //GEN-LAST:event_btnLastMonthActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dpiFromPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dpiFromPropertyChange
        if (!lockDateButtons) {
            manualPeriodChangePerformed();
        }
    }                                                                              //GEN-LAST:event_dpiFromPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dpiToPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dpiToPropertyChange
        if (!lockDateButtons) {
            manualPeriodChangePerformed();
        }
    }                                                                            //GEN-LAST:event_dpiToPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCloseDialogActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCloseDialogActionPerformed
        dispose();
    }                                                                                  //GEN-LAST:event_btnCloseDialogActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  title      DOCUMENT ME!
     * @param  message    DOCUMENT ME!
     * @param  exception  DOCUMENT ME!
     */
    protected void showError(final String title, final String message, final Exception exception) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblDokumentLinkMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblDokumentLinkMouseClicked
        final String url = lblDokumentLink.getToolTipText();
        try {
            BrowserLauncher.openURLorFile(url);
        } catch (final Exception ex) {
            showError(
                "Fehler beim öffnen des Dokumentes",
                "<html>Das Dokument mit der URL "
                        + url
                        + "<br/>konnte nicht geöffnet werden.",
                ex);
        }
    }                                                                               //GEN-LAST:event_lblDokumentLinkMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  objects  DOCUMENT ME!
     */
    protected abstract void setObjects(final Collection objects);

    /**
     * DOCUMENT ME!
     */
    private void manualPeriodChangePerformed() {
        buttonGroup1.clearSelection();
        periodChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected FortfuehrungItem getSelectedFortfuehrungItem() {
        final int selectedIndex = jXTable1.getSelectedRow();
        if (selectedIndex >= 0) {
            final int rowIndex = jXTable1.convertRowIndexToModel(selectedIndex);
            return ((FortfuehrungenTableModel)jXTable1.getModel()).getItem(rowIndex);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected FortfuehrungenTableModel getFFTableModel() {
        return (FortfuehrungenTableModel)jXTable1.getModel();
    }

    /**
     * DOCUMENT ME!
     */
    protected void searchDone() {
        jProgressBar1.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void fortfuehrungsTableListSelectionChanged(final ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        final FortfuehrungItem selectedFortfuehrungItem = getSelectedFortfuehrungItem();
        setDetailEnabled(false);
        setObjects(null);
        setDokumentLink(null);

        if (selectedFortfuehrungItem != null) {
            jProgressBar1.setVisible(true);
            final Collection<Geometry> geoms = (Collection<Geometry>)geomsMap.get(selectedFortfuehrungItem.getFfn());
            final List<Geometry> bufferedGeoms = new ArrayList<Geometry>(geoms.size());
            for (final Geometry geom : geoms) {
                bufferedGeoms.add(geom.buffer(FLURSTUECKBUFFER_FOR_KASSENZEICHEN_GEOMSEARCH));
            }
            searchObjects(new GeometryCollection(
                    GeometryFactory.toGeometryArray(bufferedGeoms),
                    bufferedGeoms.get(0).getFactory()));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    protected abstract void searchObjects(final Geometry geom);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract JPanel getObjectsPanel();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getLinkFormat();

    /**
     * DOCUMENT ME!
     *
     * @param  dokumentUrl  DOCUMENT ME!
     */
    protected void setDokumentLink(final String dokumentUrl) {
        if (dokumentUrl != null) {
            lblDokumentLink.setText("<html><a href=\"" + dokumentUrl + "\">Dokument im Browser anzeigen</a>");
            lblDokumentLink.setEnabled(true);
            lblDokumentLink.setToolTipText(dokumentUrl);
            lblDokumentLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            lblDokumentLink.setEnabled(false);
            lblDokumentLink.setToolTipText(null);
            lblDokumentLink.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void periodChanged() {
        final boolean anlaesseEnabled = (dpiFrom.getDate() != null) && (dpiTo.getDate() != null);
        btnRefreshAnlaesse.setEnabled(anlaesseEnabled);
        jXTable1.setEnabled(anlaesseEnabled);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fromDate  DOCUMENT ME!
     * @param   toDate    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract CidsServerSearch createFortfuehrungItemSearch(final Date fromDate, final Date toDate);

    /**
     * DOCUMENT ME!
     *
     * @param   rawItem  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract FortfuehrungItem createFortfuehrungItem(final Object[] rawItem);

    /**
     * DOCUMENT ME!
     */
    protected void refreshFortfuehrungsList() {
        geomsMap.clear();
        new SwingWorker<Collection<FortfuehrungItem>, Void>() {

                @Override
                protected Collection<FortfuehrungItem> doInBackground() throws Exception {
                    btnRefreshAnlaesse.setEnabled(false);
                    jProgressBar1.setVisible(true);
                    jXTable1.setEnabled(false);

                    final CidsServerSearch itemSearch = createFortfuehrungItemSearch(dpiFrom.getDate(),
                            dpiTo.getDate());
                    final Collection<Object[]> rawItems = (Collection<Object[]>)SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(),
                                        itemSearch,
                                        getConnectionContext());
                    final Map<Integer, FortfuehrungItem> ffnMap = new HashMap<>();
                    for (final Object[] rawItem : rawItems) {
                        final FortfuehrungItem item = createFortfuehrungItem(rawItem);

                        final Integer id = item.getAnlassId();
                        if (!ffnMap.containsKey(id)) {
                            ffnMap.put(id, item);
                        } else {
                            final FortfuehrungItem ffn = ffnMap.get(id);
                            if (ffn.getFortfuehrungId() == null) {
                                ffn.setFortfuehrungId(item.getFortfuehrungId());
                            }
                        }
                        final Geometry geom = wktreader.read((String)rawItem[6]);
                        geom.setSRID(SRID);

                        final int currentSrid = CrsTransformer.getCurrentSrid();
                        final String currentCrs = CrsTransformer.createCrsFromSrid(currentSrid);
                        final Geometry transformedAlkisLandparcelGeom = CrsTransformer.transformToGivenCrs((Geometry)
                                geom.clone(),
                                currentCrs);
                        transformedAlkisLandparcelGeom.setSRID(currentSrid);

                        geomsMap.put(item.getFfn(), transformedAlkisLandparcelGeom);
                    }
                    final List<FortfuehrungItem> items = new ArrayList<FortfuehrungItem>(ffnMap.values());
                    Collections.sort(items);
                    return items;
                }

                @Override
                protected void done() {
                    Collection<FortfuehrungItem> items = null;
                    try {
                        items = get();

                        jXTable1.getSelectionModel().clearSelection();
                        ((FortfuehrungenTableModel)jXTable1.getModel()).setItems(items.toArray(
                                new FortfuehrungItem[0]));
                    } catch (final Exception ex) {
                        LOG.error("error while loading fortfuehrung items", ex);
                    }
                    btnRefreshAnlaesse.setEnabled(true);
                    jXTable1.setEnabled(true);
                    jProgressBar1.setVisible(false);

                    if ((items == null) || (items.isEmpty())) {
                        JOptionPane.showMessageDialog(
                            rootPane,
                            "<html>Es konnten keine Fortführungsfälle<br/>für den gewählten Zeitraum gefunden werden.",
                            "keine Fortführungsfälle gefunden",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected FortfuehrungenTableModel getTableModel() {
        return (FortfuehrungenTableModel)jXTable1.getModel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enabled  DOCUMENT ME!
     */
    protected abstract void setDetailEnabled(final boolean enabled);

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class IstAbgearbeitetHighlighter implements Highlighter {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component highlight(final Component renderer, final ComponentAdapter adapter) {
            final int displayedIndex = adapter.row;
            final int modelIndex = jXTable1.convertRowIndexToModel(displayedIndex);
            final FortfuehrungItem item = ((FortfuehrungenTableModel)jXTable1.getModel()).getItem(modelIndex);
            final boolean istAbgearbeitet = item.isIst_abgearbeitet();
            renderer.setEnabled(!istAbgearbeitet);
            return renderer;
        }

        @Override
        public void addChangeListener(final ChangeListener l) {
        }

        @Override
        public void removeChangeListener(final ChangeListener l) {
        }

        @Override
        public ChangeListener[] getChangeListeners() {
            return new ChangeListener[0];
        }
    }
}
