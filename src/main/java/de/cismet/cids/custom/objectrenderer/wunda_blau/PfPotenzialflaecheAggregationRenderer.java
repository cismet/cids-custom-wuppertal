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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import de.cismet.cids.custom.objecteditors.wunda_blau.PfPotenzialflaecheReportGenerator;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.CidsBeansTableModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.search.QuerySearchResultsAction;

import de.cismet.cids.server.actions.CsvExportServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanAggregationRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheAggregationRenderer extends javax.swing.JPanel implements CidsBeanAggregationRenderer,
    ConnectionContextStore,
    TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheAggregationRenderer.class);

    public static final List<Integer> COLUMN_SIZES = new CopyOnWriteArrayList<Integer>();
    public static final Color COLOR_TXT_BACK = new Color(230, 230, 230);
    public static final Color COLOR_TBL_SECOND = new Color(210, 210, 210);

    //~ Instance fields --------------------------------------------------------

    private final QuerySearchResultsAction csvAction = new QuerySearchResultsAction() {

            @Override
            public String getName() {
                return "nach CSV exportieren";
            }

            @Override
            public void doAction() {
                final String title = cidsBeansTableActionPanel1.getMetaClass().getName();

                if (DownloadManagerDialog.showAskingForUserTitle(
                                StaticSwingTools.getParentFrame(PfPotenzialflaecheAggregationRenderer.this))) {
                    final List<String> columnNames = new ArrayList<>(
                            cidsBeansTableActionPanel1.getAttributeNames().size());
                    final List<String> fields = new ArrayList<>(
                            cidsBeansTableActionPanel1.getAttributeNames().size());
                    final List<String> keys = cidsBeansTableActionPanel1.getKeys();
                    if (keys != null) {
                        for (final String attrKey : keys) {
                            final MemberAttributeInfo mai = (MemberAttributeInfo)
                                cidsBeansTableActionPanel1.getMetaClass().getMemberAttributeInfos().get(attrKey);
                            columnNames.add(cidsBeansTableActionPanel1.getAttributeNames().get(attrKey));
                            fields.add(mai.getFieldName());
                        }
                    }

                    final List<MetaObjectNode> mons = new ArrayList<>();
                    for (final CidsBean cidsBean : cidsBeansTableActionPanel1.getCidsBeans()) {
                        mons.add(new MetaObjectNode(cidsBean));
                    }

                    final ServerActionParameter[] params = new ServerActionParameter[] {
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.COLUMN_NAMES.toString(),
                                columnNames),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.FIELDS.toString(),
                                fields),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.MONS.toString(),
                                mons),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.DATE_FORMAT.toString(),
                                "dd.MM.yy"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.BOOLEAN_YES.toString(),
                                "ja"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.BOOLEAN_NO.toString(),
                                "nein"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.DISTINCT_ON.toString(),
                                "id"),
                            new ServerActionParameter<>(
                                CsvExportServerAction.ParameterType.CHARSET.toString(),
                                "LATIN9"),
                        };
                    DownloadManager.instance()
                            .add(
                                new ByteArrayActionDownload(
                                    "WUNDA_BLAU",
                                    CsvExportServerAction.TASKNAME,
                                    cidsBeansTableActionPanel1.getMetaClass().getTableName(),
                                    params,
                                    title,
                                    DownloadManagerDialog.getInstance().getJobName(),
                                    title,
                                    ".csv",
                                    ConnectionContext.createDeprecated()));
                    final DownloadManagerDialog downloadManagerDialog = DownloadManagerDialog.getInstance();
                    StaticSwingTools.showDialog(
                        StaticSwingTools.getParentFrame(PfPotenzialflaecheAggregationRenderer.this),
                        downloadManagerDialog,
                        true);
                }
            }
        };

    private Collection<CidsBean> cidsBeans = null;

    private ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReport;
    private de.cismet.cids.search.CidsBeansTableActionPanel cidsBeansTableActionPanel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JPanel panTitle;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form PfPotenzialflaecheAggregationRenderer.
     */
    public PfPotenzialflaecheAggregationRenderer() {
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

        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        cidsBeansTableActionPanel1 = new de.cismet.cids.search.CidsBeansTableActionPanel(Arrays.asList(csvAction),
                true);
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panTitle = new javax.swing.JPanel();
        btnReport = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();

        jDialog1.setTitle("Tabellen-Export");
        jDialog1.setModal(true);
        jDialog1.getContentPane().setLayout(new java.awt.CardLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        cidsBeansTableActionPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(cidsBeansTableActionPanel1, gridBagConstraints);

        jDialog1.getContentPane().add(jPanel1, "exporter");

        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheAggregationRenderer.class,
                "PfPotenzialflaecheAggregationRenderer.jLabel1.text")); // NOI18N
        jPanel2.add(jLabel1, new java.awt.GridBagConstraints());

        jDialog1.getContentPane().add(jPanel2, "loader");

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheAggregationRenderer.class,
                "PfPotenzialflaecheAggregationRenderer.btnReport.text"));                  // NOI18N
        btnReport.setToolTipText("Steckbriefe zu den ausgewählten Potenzialflächen erzeugen.");
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setFocusPainted(false);
        btnReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(filler1, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheAggregationRenderer.class,
                "PfPotenzialflaecheAggregationRenderer.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        add(jButton1, gridBagConstraints);

        jXTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jXTable1.setSortable(false);
        jScrollPane1.setViewportView(jXTable1);
        jXTable1.getTableHeader().setEnabled(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getSelectedBeans() {
        final Collection<CidsBean> selectedBeans = new ArrayList<>();
        for (final Integer rowIndex : ((CidsBeansTableModel)jXTable1.getModel()).getSelectedRowIndices()) {
            final CidsBean cidsBean = ((CidsBeansTableModel)jXTable1.getModel()).getCidsBean(rowIndex);
            if (cidsBean != null) {
                selectedBeans.add(cidsBean);
            }
        }
        return selectedBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        if (cidsBeans != null) {
            ((CardLayout)jDialog1.getContentPane().getLayout()).show(jDialog1.getContentPane(), "loader");
            final Collection<CidsBean> selectedBeans = getSelectedBeans();
            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        final List<CidsBean> viewBeans = new ArrayList<>();
                        for (final CidsBean cidsBean : selectedBeans) {
                            if (cidsBean != null) {
                                final MetaObject mo = cidsBean.getMetaObject();
                                final MetaObject viewMo = SessionManager.getConnection()
                                            .getMetaObject(SessionManager.getSession().getUser(),
                                                mo.getId(),
                                                cidsBeansTableActionPanel1.getMetaClass().getId(),
                                                cidsBeansTableActionPanel1.getMetaClass().getDomain(),
                                                getConnectionContext());
                                if (viewMo != null) {
                                    viewBeans.add(viewMo.getBean());
                                }
                            }
                        }
                        return viewBeans;
                    }

                    @Override
                    protected void done() {
                        try {
                            cidsBeansTableActionPanel1.setCidsBeans(get());
                            ((CardLayout)jDialog1.getContentPane().getLayout()).show(jDialog1.getContentPane(),
                                "exporter");
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
            StaticSwingTools.showDialog(this, jDialog1, true);
        }
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        PfPotenzialflaecheReportGenerator.startDownloadForFlaechen(getSelectedBeans(), getConnectionContext());
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
//        bindingGroup.unbind();
        this.cidsBeans = cidsBeans;
        if (cidsBeans != null) {
//            bindingGroup.bind();
        }
        ((CidsBeansTableModel)jXTable1.getModel()).setCidsBeans((cidsBeans != null) ? new ArrayList<>(cidsBeans)
                                                                                    : null);
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        final Collection<CidsBean> cidsBeans = getCidsBeans();
        return String.format("%d Potenzialflächen", (cidsBeans != null) ? cidsBeans.size() : 0);
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();
        jXTable1.setModel(new CidsBeansTableModel(
                new String[] { "nummer", "bezeichnung", "kampagne.bezeichnung" },
                new String[] { "Nummer", "Bezeichnung", "Kampagne" },
                new Class[] { String.class, String.class, String.class },
                false,
                true));
        jXTable1.getColumnModel().getColumn(0).setMinWidth(25);
        jXTable1.getColumnModel().getColumn(0).setPreferredWidth(25);
        jXTable1.getColumnModel().getColumn(0).setMaxWidth(25);

        final CheckBoxHeader cbh = new CheckBoxHeader();
        cbh.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    for (int i = 0; i < jXTable1.getRowCount(); i++) {
                        jXTable1.setValueAt(cbh.isSelected(), i, 0);
                    }
                    jXTable1.repaint();
                }
            });

        jXTable1.getColumnModel().getColumn(0).setHeaderRenderer(cbh);

        jDialog1.pack();

        try {
            cidsBeansTableActionPanel1.setMetaClass(CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "VIEW_POTENZIALFLAECHE_CSV",
                    getConnectionContext()));
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CheckBoxHeader extends JCheckBox implements TableCellRenderer, MouseListener {

        //~ Instance fields ----------------------------------------------------

        private int column;
        private boolean mousePressed = false;

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(
                final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            if (table != null) {
                final JTableHeader header = table.getTableHeader();
                if (header != null) {
                    header.addMouseListener(this);
                }
            }
            setColumn(column);
            setHorizontalAlignment(JLabel.CENTER);
            setBorder(BorderFactory.createEtchedBorder());
            return this;
        }
        /**
         * DOCUMENT ME!
         *
         * @param  column  DOCUMENT ME!
         */
        protected void setColumn(final int column) {
            this.column = column;
        }
        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getColumn() {
            return column;
        }
        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void mouseClicked(final MouseEvent e) {
            if (mousePressed) {
                mousePressed = false;
                final JTableHeader header = (JTableHeader)(e.getSource());
                final JTable tableView = header.getTable();
                final TableColumnModel columnModel = tableView.getColumnModel();
                final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                final int column = tableView.convertColumnIndexToModel(viewColumn);

                if ((viewColumn == this.column) && (e.getClickCount() == 1)) {
                    doClick();
                }
                repaint();
            }
        }
        @Override
        public void mousePressed(final MouseEvent e) {
            mousePressed = true;
        }
        @Override
        public void mouseReleased(final MouseEvent e) {
        }
        @Override
        public void mouseEntered(final MouseEvent e) {
        }
        @Override
        public void mouseExited(final MouseEvent e) {
        }
    }
}
