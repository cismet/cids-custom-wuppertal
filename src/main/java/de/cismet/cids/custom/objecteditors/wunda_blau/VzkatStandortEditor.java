/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObjectNode;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.commons.gui.ScrollablePanel;
import de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatStandortKartePanel;
import de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatStandortSchildPanel;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatSchilderSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatStandortEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore,
    EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VzkatStandortEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private final Collection<CidsBean> schildBeans = new ArrayList<>();
    private final Collection<CidsBean> deletedSchildBeans = new ArrayList<>();
    private final PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                cidsBean.setArtificialChangeFlag(true);
                if ("fk_richtung".equals(evt.getPropertyName())) {
                    redoReihenfolge(createRichtungsLists());
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private javax.swing.JLabel lblBildTitle;
    private javax.swing.JLabel lblLageTitle;
    private de.cismet.tools.gui.RoundedPanel panBild;
    private de.cismet.tools.gui.SemiRoundedPanel panBildTitle;
    private javax.swing.JPanel panLageBody1;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.tools.gui.RoundedPanel panStandortKarte;
    private javax.swing.JPanel panStandortKarteBody;
    private javax.swing.JPanel panTitle;
    private javax.swing.JLabel txtTitle;
    private de.cismet.cids.custom.objecteditors.utils.vzkat.VzkatStandortKartePanel vzkatStandortKartePanel;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatSchildEditor object.
     */
    public VzkatStandortEditor() {
        this(true);
    }

    /**
     * Creates a new VzkatSchildEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public VzkatStandortEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "vzkat_standort",
            1,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        vzkatStandortKartePanel.initWithConnectionContext(connectionContext);
        jXDatePicker1.setDate(new Date());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        txtTitle = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panStandortKarte = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle = new javax.swing.JLabel();
        panStandortKarteBody = new javax.swing.JPanel();
        vzkatStandortKartePanel = new VzkatStandortKartePanel(isEditable());
        panBild = new de.cismet.tools.gui.RoundedPanel();
        panBildTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBildTitle = new javax.swing.JLabel();
        panLageBody1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel3 = new ScrollablePanel(new GridLayout(0, 1, 0, 10));
        jButton3 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panTitle.add(txtTitle, gridBagConstraints);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel3.text")); // NOI18N

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        panStandortKarte.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        lblLageTitle.setFont(lblLageTitle.getFont());
        lblLageTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblLageTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle.add(lblLageTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panStandortKarte.add(panLageTitle, gridBagConstraints);

        panStandortKarteBody.setOpaque(false);
        panStandortKarteBody.setLayout(new java.awt.GridBagLayout());

        vzkatStandortKartePanel.setOpaque(false);

        final javax.swing.GroupLayout vzkatStandortKartePanelLayout = new javax.swing.GroupLayout(
                vzkatStandortKartePanel);
        vzkatStandortKartePanel.setLayout(vzkatStandortKartePanelLayout);
        vzkatStandortKartePanelLayout.setHorizontalGroup(
            vzkatStandortKartePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        vzkatStandortKartePanelLayout.setVerticalGroup(
            vzkatStandortKartePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStandortKarteBody.add(vzkatStandortKartePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panStandortKarte.add(panStandortKarteBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panStandortKarte, gridBagConstraints);

        panBild.setLayout(new java.awt.GridBagLayout());

        panBildTitle.setBackground(java.awt.Color.darkGray);
        panBildTitle.setLayout(new java.awt.GridBagLayout());

        lblBildTitle.setFont(lblBildTitle.getFont());
        lblBildTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBildTitle,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.lblBildTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBildTitle.add(lblBildTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBild.add(panBildTitle, gridBagConstraints);

        panLageBody1.setMinimumSize(new java.awt.Dimension(320, 320));
        panLageBody1.setOpaque(false);
        panLageBody1.setPreferredSize(new java.awt.Dimension(320, 320));
        panLageBody1.setLayout(new java.awt.GridBagLayout());

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody1.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBild.add(panLageBody1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panBild, gridBagConstraints);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setOpaque(false);
        jScrollPane1.getViewport().setOpaque(false);

        ((ScrollablePanel)jPanel3).setScrollableWidth(ScrollablePanel.ScrollableSizeHint.FIT);
        ((ScrollablePanel)jPanel3).setScrollableBlockIncrement(
            ScrollablePanel.VERTICAL,
            ScrollablePanel.IncrementType.PERCENT,
            100);
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel3.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridLayout(0, 1, 0, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel5, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jScrollPane1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jXDatePicker1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXDatePicker1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(jXDatePicker1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(VzkatStandortEditor.class, "VzkatStandortEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel2.add(jLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel2, gridBagConstraints);
        jPanel2.setVisible(false);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXDatePicker1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXDatePicker1ActionPerformed
        reloadShilder();
    }                                                                                 //GEN-LAST:event_jXDatePicker1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        addSchildPanel(null);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void reloadShilder() {
        jPanel5.removeAll();
        jPanel5.add(jLabel3);
        jButton3.setEnabled(false);
        final VzkatSchilderSearch schilderSearch = new VzkatSchilderSearch();
        schilderSearch.setStandortId((Integer)cidsBean.getProperty("id"));
        schilderSearch.setActiveTimestamp((jXDatePicker1.getDate() != null)
                ? new Timestamp(jXDatePicker1.getDate().getTime()) : null);
        new SwingWorker<List, Void>() {

                @Override
                protected List doInBackground() throws Exception {
                    final Collection<MetaObjectNode> mons = (Collection)SessionManager.getProxy()
                                .customServerSearch(schilderSearch, getConnectionContext());
                    final List<CidsBean> schildBeans = new ArrayList<>();
                    for (final MetaObjectNode mon : mons) {
                        schildBeans.add(SessionManager.getProxy().getMetaObject(
                                mon.getObjectId(),
                                mon.getClassId(),
                                "WUNDA_BLAU",
                                getConnectionContext()).getBean());
                    }
                    return schildBeans;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> newSchildBeans = (List)get();
                        redoSchilder(newSchildBeans);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    } finally {
                        jButton3.setEnabled(true);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshSchildPanels() {
        jPanel5.removeAll();

        for (final CidsBean schildBean : schildBeans) {
            final VzkatStandortSchildPanel schildPanel = new VzkatStandortSchildPanel(
                    VzkatStandortEditor.this,
                    isEditable());
            schildPanel.initWithConnectionContext(getConnectionContext());
            schildPanel.setCidsBean(schildBean);
            schildPanel.setOpaque(false);
            jPanel5.add(schildPanel);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeSchildPanel(final VzkatStandortSchildPanel panel) {
        final CidsBean panelBean = panel.getCidsBean();
        try {
            panelBean.setProperty("fk_standort", null);
            deletedSchildBeans.add(panelBean);
            schildBeans.remove(panelBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        cidsBean.setArtificialChangeFlag(true);

        redoReihenfolge(createRichtungsLists());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void addSchildPanel(final VzkatStandortSchildPanel panel) {
        new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    final CidsBean newSchildBean = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "vzkat_schild",
                            getConnectionContext());
                    newSchildBean.setProperty("fk_standort", getCidsBean());
                    newSchildBean.setProperty("gueltig_von", new Timestamp(new Date().getTime()));
                    return newSchildBean;
                }

                @Override
                protected void done() {
                    try {
                        final CidsBean newSchildBean = get();
                        newSchildBean.addPropertyChangeListener(listener);

                        final CidsBean panelBean = (panel != null) ? panel.getCidsBean() : null;

                        if (panelBean != null) {
                            newSchildBean.setProperty("fk_richtung", panelBean.getProperty("fk_richtung"));
                            newSchildBean.setProperty("fk_zeichen", panelBean.getProperty("fk_zeichen"));
                        }

                        final CidsBean richtungBean = (panelBean != null)
                            ? (CidsBean)panelBean.getProperty("fk_richtung") : null;

                        final Map<CidsBean, List> richtungsLists = createRichtungsLists();
                        if (!richtungsLists.containsKey(richtungBean)) {
                            richtungsLists.put(richtungBean, new ArrayList());
                        }
                        final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);

                        final int index = sameRichtungBeans.indexOf(panelBean);
                        richtungsLists.get(richtungBean).add(index + 1, newSchildBean);

                        cidsBean.setArtificialChangeFlag(true);
                        redoReihenfolge(richtungsLists);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, List> createRichtungsLists() {
        final Map<CidsBean, List> richtungsLists = new HashMap<>();
        for (final CidsBean schildBean : schildBeans) {
            final CidsBean richtungBean = (CidsBean)schildBean.getProperty("fk_richtung");
            if (!richtungsLists.containsKey(richtungBean)) {
                richtungsLists.put(richtungBean, new ArrayList<>());
            }

            final List<CidsBean> richtungSchildBeans = richtungsLists.get(richtungBean);
            richtungSchildBeans.add(schildBean);
        }
        return richtungsLists;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  richtungsLists  DOCUMENT ME!
     */
    public void redoReihenfolge(final Map<CidsBean, List> richtungsLists) {
        for (final CidsBean richtung : richtungsLists.keySet()) {
            int reihenfolge = 1;
            for (final CidsBean schildBean : (List<CidsBean>)richtungsLists.get(richtung)) {
                try {
                    schildBean.setProperty("reihenfolge", reihenfolge++);
                } catch (final Exception ex) {
                    LOG.error(ex, ex);
                }
            }
        }

        final List<CidsBean> newSchildBeans = new ArrayList<>();
        for (final List<CidsBean> richtungBeans : richtungsLists.values()) {
            newSchildBeans.addAll(richtungBeans);
        }
        redoSchilder(newSchildBeans);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void upSchildPanel(final VzkatStandortSchildPanel panel) {
        final CidsBean panelBean = panel.getCidsBean();
        final CidsBean richtungBean = (CidsBean)panelBean.getProperty("fk_richtung");

        final Map<CidsBean, List> richtungsLists = createRichtungsLists();
        final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);
        final int index = sameRichtungBeans.indexOf(panelBean);
        if (index > 0) {
            Collections.swap(sameRichtungBeans, index, index - 1);
        }

        redoReihenfolge(richtungsLists);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void downSchildPanel(final VzkatStandortSchildPanel panel) {
        final CidsBean panelBean = panel.getCidsBean();
        final CidsBean richtungBean = (CidsBean)panelBean.getProperty("fk_richtung");

        final Map<CidsBean, List> richtungsLists = createRichtungsLists();
        final List<CidsBean> sameRichtungBeans = richtungsLists.get(richtungBean);
        final int index = sameRichtungBeans.indexOf(panelBean);
        if (index < (sameRichtungBeans.size() - 1)) {
            Collections.swap(sameRichtungBeans, index, index + 1);
        }

        redoReihenfolge(richtungsLists);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  newSchildBeans  DOCUMENT ME!
     */
    private void redoSchilder(final List<CidsBean> newSchildBeans) {
        Collections.sort(newSchildBeans, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final Integer f1 = ((o1 != null) && (o1.getProperty("fk_richtung.id") != null))
                        ? (Integer)o1.getProperty("fk_richtung.id") : -1;
                    final Integer r1 = ((o1 != null) && (o1.getProperty("reihenfolge") != null))
                        ? (Integer)o1.getProperty("reihenfolge") : -1;
                    final Integer f2 = ((o2 != null) && (o2.getProperty("fk_richtung.id") != null))
                        ? (Integer)o2.getProperty("fk_richtung.id") : -1;
                    final Integer r2 = ((o2 != null) && (o2.getProperty("reihenfolge") != null))
                        ? (Integer)o2.getProperty("reihenfolge") : -1;
                    return Integer.compare((f1 * 10000) + r1, (f2 * 10000) + r2);
                }
            });

        for (final CidsBean schildBean : schildBeans) {
            schildBean.removePropertyChangeListener(listener);
        }

        schildBeans.clear();
        schildBeans.addAll(newSchildBeans);

        for (final CidsBean schildBean : schildBeans) {
            schildBean.addPropertyChangeListener(listener);
        }

        refreshSchildPanels();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schildBean  DOCUMENT ME!
     */
    protected void selectSchildBean(final CidsBean schildBean) {
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;

        txtTitle.setText((cidsBean != null) ? getTitle() : null);
        vzkatStandortKartePanel.setCidsBean(cidsBean);

        if (cidsBean != null) {
            reloadShilder();
            if (isEditable()) {
                redoReihenfolge(createRichtungsLists());
            }
        }

        new SwingWorker<Image, Object>() {

                @Override
                protected Image doInBackground() throws Exception {
                    final URL bildURL = new URL(
                            "https://i.pinimg.com/236x/b7/95/67/b79567b505f3101a8a58f1d0f6d10687.jpg");
                    final BufferedImage originalBild = ImageIO.read(WebAccessManager.getInstance().doRequest(bildURL));
                    final int bildZielBreite = (originalBild.getWidth() > originalBild.getHeight()) ? 320 : -1;
                    final int bildZielHoehe = (originalBild.getWidth() > originalBild.getHeight()) ? -1 : 320;
                    final Image skaliertesBild = originalBild.getScaledInstance(
                            bildZielBreite,
                            bildZielHoehe,
                            Image.SCALE_SMOOTH);
                    return skaliertesBild;
                }

                @Override
                protected void done() {
                    final Image skaliertesBild;
                    try {
                        skaliertesBild = get();
                        jLabel2.setIcon(new ImageIcon(skaliertesBild));
                    } catch (final Exception ex) {
                        LOG.error("Bild konnte nicht geladen werden", ex);
                    }
                }
            }.execute();
    }

    @Override
    public String getTitle() {
        final String standort = String.valueOf(cidsBean);
        return String.format("<html>Standort <i>%s</i>", standort);
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
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
    private Collection<VzkatStandortSchildPanel> getSchildPanels() {
        final Collection<VzkatStandortSchildPanel> vzkatSchildPanel = new ArrayList<>();
        for (final Component component : jPanel5.getComponents()) {
            if (component instanceof VzkatStandortSchildPanel) {
                vzkatSchildPanel.add((VzkatStandortSchildPanel)component);
            }
        }
        return vzkatSchildPanel;
    }

    @Override
    public void dispose() {
        for (final VzkatStandortSchildPanel vzkatSchildPanel : getSchildPanels()) {
            vzkatSchildPanel.dispose();
        }
        vzkatStandortKartePanel.dispose();
    }

    @Override
    public boolean prepareForSave() {
        boolean errorOccured = false;
        for (final CidsBean schildBean : schildBeans) {
            try {
                schildBean.persist(getConnectionContext());
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        for (final CidsBean schildBean : deletedSchildBeans) {
            try {
                schildBean.delete();
            } catch (final Exception ex) {
                errorOccured = true;
                LOG.error(ex, ex);
            }
        }
        return !errorOccured;
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }
}
