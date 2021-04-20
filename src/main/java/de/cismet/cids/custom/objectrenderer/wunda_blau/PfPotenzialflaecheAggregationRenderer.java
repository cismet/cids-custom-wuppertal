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

import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.localserver.attribute.MemberAttributeInfo;
import Sirius.server.middleware.types.MetaObjectNode;

import com.google.common.collect.Lists;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheAggregationRenderer.class);

    public static final List<Integer> COLUMN_SIZES = new CopyOnWriteArrayList<Integer>();
    public static final Color COLOR_TXT_BACK = new Color(230, 230, 230);
    public static final Color COLOR_TBL_SECOND = new Color(210, 210, 210);

    //~ Instance fields --------------------------------------------------------

    private QuerySearchResultsAction csvAction = new QuerySearchResultsAction() {

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
                    for (final String attrKey
                                : cidsBeansTableActionPanel1.getAttributesToDisplay().get(
                                    cidsBeansTableActionPanel1.getMetaClass())) {
                        final MemberAttributeInfo mai = (MemberAttributeInfo)cidsBeansTableActionPanel1
                                    .getMetaClass().getMemberAttributeInfos().get(attrKey);
                        columnNames.add(cidsBeansTableActionPanel1.getAttributeNames().get(attrKey));
                        fields.add(mai.getFieldName());
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
    private de.cismet.cids.search.CidsBeansTableActionPanel cidsBeansTableActionPanel1;
    private javax.swing.JButton jButton1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXTable jXTable1;
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
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();

        jDialog1.setTitle("Tabellen-Export");
        jDialog1.setModal(true);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        cidsBeansTableActionPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(cidsBeansTableActionPanel1, gridBagConstraints);

        jDialog1.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

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

        jXTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null },
                    { null, null, null, null }
                },
                new String[] { "Title 1", "Title 2", "Title 3", "Title 4" }));
        jScrollPane1.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        StaticSwingTools.showDialog(jDialog1);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    @Override
    public Collection<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    @Override
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
//        bindingGroup.unbind();
        this.cidsBeans = cidsBeans;
        if (cidsBeans != null) {
            cidsBeansTableActionPanel1.setCidsBeans(Lists.newArrayList(cidsBeans));
//            bindingGroup.bind();
        }
        ((CidsBeansTableModel)jXTable1.getModel()).setCidsBeans(new ArrayList<>(cidsBeans));
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
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
                new String[] { "nummer", "bezeichnung" },
                new String[] { "Nummer", "Bezeichnung" },
                new Class[] { String.class, String.class },
                false));

        try {
            cidsBeansTableActionPanel1.setMetaClass(CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "PF_POTENZIALFLAECHE",
                    getConnectionContext()));
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }

        jDialog1.pack();
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
