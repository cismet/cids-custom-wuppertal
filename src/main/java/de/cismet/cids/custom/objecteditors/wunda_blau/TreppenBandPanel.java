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

import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ScrollPaneConstants;

import de.cismet.cids.custom.wunda_blau.band.DummyBandMember;
import de.cismet.cids.custom.wunda_blau.band.ElementResizedEvent;
import de.cismet.cids.custom.wunda_blau.band.ElementResizedListener;
import de.cismet.cids.custom.wunda_blau.band.HandlaufBand;
import de.cismet.cids.custom.wunda_blau.band.HandlaufBandMember;
import de.cismet.cids.custom.wunda_blau.band.LaufBand;
import de.cismet.cids.custom.wunda_blau.band.LaufBandMember;
import de.cismet.cids.custom.wunda_blau.band.LeitelementBand;
import de.cismet.cids.custom.wunda_blau.band.LeitelementBandMember;
import de.cismet.cids.custom.wunda_blau.band.PodestBandMember;
import de.cismet.cids.custom.wunda_blau.band.StuetzmauerBand;
import de.cismet.cids.custom.wunda_blau.band.StuetzmauerBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppenBand;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.jbands.BandModelEvent;
import de.cismet.tools.gui.jbands.JBand;
import de.cismet.tools.gui.jbands.SimpleBandModel;
import de.cismet.tools.gui.jbands.interfaces.BandMember;
import de.cismet.tools.gui.jbands.interfaces.BandMemberSelectable;
import de.cismet.tools.gui.jbands.interfaces.BandModelListener;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TreppenBandPanel extends javax.swing.JPanel implements ConnectionContextStore, Disposable, CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            TreppenBandPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final JBand jband = new JBand();
    private final TreppenBand laufBand = new LaufBand(
            TreppenBand.Side.NONE,
            "Treppenläufe/Podeste",
            jband);
    private final TreppenBand handlaufRightBand = new HandlaufBand(
            TreppenBand.Side.RIGHT,
            "Handläufe links",
            jband);
    private final TreppenBand handlaufLeftBand = new HandlaufBand(
            TreppenBand.Side.LEFT,
            "Handläufe rechts",
            jband);
    private final TreppenBand leitelementRightBand = new LeitelementBand(
            TreppenBand.Side.RIGHT,
            "Leitelemente rechts",
            jband);
    private final TreppenBand leitelementLeftBand = new LeitelementBand(
            TreppenBand.Side.LEFT,
            "Leitelemente links",
            jband);
    private final TreppenBand stuetzmauerLinksBand = new StuetzmauerBand(
            TreppenBand.Side.LEFT,
            "Stützmauer links",
            jband);
    private final TreppenBand stuetzmauerRechtsBand = new StuetzmauerBand(
            TreppenBand.Side.RIGHT,
            "Stützmauer rechts",
            jband);
    private final BandModelListener modelListener = new TreppenBandModelListener();
    private final SimpleBandModel sbm = new SimpleBandModel();
    private TreppeLaufPanel treppelaufPanel;
    private TreppeHandlaufPanel treppeHandlaufPanel;
    private TreppeLeitelementPanel treppeLeitelementpanel;
    private TreppePodestPanel treppePodestPanel;
    private TreppeStuetzmauerPanel treppeStuetzmauerPanel;
    private ConnectionContext connectionContext;
    private CidsBean cidsBean;
    private List<CidsBean> laufList = new ArrayList<CidsBean>();
    private List<CidsBean> leitelementList = new ArrayList<CidsBean>();
    private List<CidsBean> handlaufList = new ArrayList<CidsBean>();
    private List<CidsBean> stuetzmauerList = new ArrayList<CidsBean>();
    private TreppenElementResizedListener resizedListener = new TreppenElementResizedListener();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel panBand;
    private javax.swing.JPanel panChooser;
    private javax.swing.JPanel panControls;
    private javax.swing.JPanel panEmpty;
    private javax.swing.JPanel panHandlaeufe;
    private javax.swing.JPanel panHeader;
    private javax.swing.JPanel panHeaderInfo;
    private javax.swing.JPanel panInfoContent;
    private javax.swing.JPanel panLeitelemente;
    private javax.swing.JPanel panPodeste;
    private javax.swing.JPanel panStuetzmauern;
    private javax.swing.JPanel panStufe;
    private javax.swing.JSlider sldZoom;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TreppenBandPanel.
     */
    public TreppenBandPanel() {
        this(false, ConnectionContext.createDeprecated());
    }

    /**
     * Creates new form TreppenBandPanel.
     *
     * @param  readOnly           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppenBandPanel(final boolean readOnly, final ConnectionContext connectionContext) {
        jband.setReadOnly(readOnly);
        this.connectionContext = connectionContext;
        treppelaufPanel = new TreppeLaufPanel(!readOnly, connectionContext);
        treppeHandlaufPanel = new TreppeHandlaufPanel(!readOnly);
        treppeLeitelementpanel = new TreppeLeitelementPanel(!readOnly);
        treppePodestPanel = new TreppePodestPanel(!readOnly, connectionContext);
        treppeStuetzmauerPanel = new TreppeStuetzmauerPanel(!readOnly);
        initComponents();
        stuetzmauerLinksBand.setReadOnly(readOnly);
        handlaufLeftBand.setReadOnly(readOnly);
        handlaufRightBand.setReadOnly(readOnly);
        laufBand.setReadOnly(readOnly);
        leitelementRightBand.setReadOnly(readOnly);
        leitelementLeftBand.setReadOnly(readOnly);
        stuetzmauerRechtsBand.setReadOnly(readOnly);
        sbm.addBand(stuetzmauerLinksBand);
        sbm.addBand(leitelementLeftBand);
        sbm.addBand(handlaufLeftBand);
        sbm.addBand(laufBand);
        sbm.addBand(handlaufRightBand);
        sbm.addBand(leitelementRightBand);
        sbm.addBand(stuetzmauerRechtsBand);
        jband.setModel(sbm);

        panBand.add(jband, BorderLayout.CENTER);
        jband.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sbm.addBandModelListener(modelListener);

        sldZoom.setPaintTrack(false);

        panStufe.add(treppelaufPanel, BorderLayout.CENTER);
        panPodeste.add(treppePodestPanel, BorderLayout.CENTER);
        panLeitelemente.add(treppeLeitelementpanel, BorderLayout.CENTER);
        panStuetzmauern.add(treppeStuetzmauerPanel, BorderLayout.CENTER);
        panHandlaeufe.add(treppeHandlaufPanel, BorderLayout.CENTER);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public void setConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return this.connectionContext;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panInfoContent = new javax.swing.JPanel();
        panStufe = new javax.swing.JPanel();
        panPodeste = new javax.swing.JPanel();
        panLeitelemente = new javax.swing.JPanel();
        panHandlaeufe = new javax.swing.JPanel();
        panStuetzmauern = new javax.swing.JPanel();
        panChooser = new javax.swing.JPanel();
        panEmpty = new javax.swing.JPanel();
        panHeader = new javax.swing.JPanel();
        panHeaderInfo = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        sldZoom = new javax.swing.JSlider();
        panControls = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        panBand = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(1050, 650));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1050, 650));
        setLayout(new java.awt.GridBagLayout());

        panInfoContent.setMinimumSize(new java.awt.Dimension(640, 310));
        panInfoContent.setOpaque(false);
        panInfoContent.setPreferredSize(new java.awt.Dimension(640, 310));
        panInfoContent.setLayout(new java.awt.CardLayout());

        panStufe.setOpaque(false);
        panStufe.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panStufe, "stufen");

        panPodeste.setOpaque(false);
        panPodeste.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panPodeste, "podeste");

        panLeitelemente.setOpaque(false);
        panLeitelemente.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panLeitelemente, "leitelemente");

        panHandlaeufe.setOpaque(false);
        panHandlaeufe.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panHandlaeufe, "handlaeufe");

        panStuetzmauern.setOpaque(false);
        panStuetzmauern.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panStuetzmauern, "stuetzmauern");

        panChooser.setOpaque(false);
        panChooser.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panChooser, "chooser");

        panEmpty.setOpaque(false);
        panEmpty.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panEmpty, "empty");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 5, 5);
        add(panInfoContent, gridBagConstraints);

        panHeader.setOpaque(false);
        panHeader.setLayout(new java.awt.GridBagLayout());

        panHeaderInfo.setMinimumSize(new java.awt.Dimension(531, 102));
        panHeaderInfo.setOpaque(false);
        panHeaderInfo.setPreferredSize(new java.awt.Dimension(532, 50));
        panHeaderInfo.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    panHeaderInfoMouseClicked(evt);
                }
            });
        panHeaderInfo.setLayout(null);

        jLabel5.setFont(new java.awt.Font("Lucida Sans", 0, 18)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, "Zoom:");
        jLabel5.setMaximumSize(new java.awt.Dimension(92, 22));
        jLabel5.setMinimumSize(new java.awt.Dimension(92, 22));
        jLabel5.setPreferredSize(new java.awt.Dimension(92, 22));
        panHeaderInfo.add(jLabel5);
        jLabel5.setBounds(10, 10, 80, 20);

        sldZoom.setMaximum(200);
        sldZoom.setValue(0);
        sldZoom.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    sldZoomStateChanged(evt);
                }
            });
        panHeaderInfo.add(sldZoom);
        sldZoom.setBounds(110, 10, 350, 16);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        panHeader.add(panHeaderInfo, gridBagConstraints);

        panControls.setOpaque(false);
        panControls.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panControls.add(jPanel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panControls.add(jPanel2, gridBagConstraints);

        jPanel4.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        panControls.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        panHeader.add(panControls, gridBagConstraints);

        panBand.setOpaque(false);
        panBand.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panHeader.add(panBand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panHeader, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(1050, 1));
        jPanel3.setOpaque(false);

        jLabel4.setMaximumSize(new java.awt.Dimension(1050, 1));
        jLabel4.setMinimumSize(new java.awt.Dimension(1050, 1));
        jLabel4.setPreferredSize(new java.awt.Dimension(1050, 1));

        final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                1050,
                Short.MAX_VALUE).addGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                    jPanel3Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
                        jLabel4,
                        javax.swing.GroupLayout.PREFERRED_SIZE,
                        1040,
                        javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 10, Short.MAX_VALUE))));
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1, Short.MAX_VALUE)
                        .addGroup(
                            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                                jPanel3Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
                                    jLabel4,
                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                    javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(jPanel3, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void sldZoomStateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_sldZoomStateChanged
        final double zoom = sldZoom.getValue() / 10d;
        jband.setZoomFactor(zoom);
    }                                                                           //GEN-LAST:event_sldZoomStateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void panHeaderInfoMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_panHeaderInfoMouseClicked
        System.out.println("click");
    }                                                                             //GEN-LAST:event_panHeaderInfoMouseClicked

    /**
     * Switch the sub editor panel to the given form.
     *
     * @param  id  the id of the form
     */
    private void switchToForm(final String id) {
        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    ((CardLayout)panInfoContent.getLayout()).show(panInfoContent, id);
                }
            };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }

    /**
     * dispose all sub editors.
     */
    void disposeSubeditor() {
        treppelaufPanel.dispose();
        treppeHandlaufPanel.dispose();
        treppeLeitelementpanel.dispose();
        treppePodestPanel.dispose();
        treppeStuetzmauerPanel.dispose();
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext cc) {
        this.connectionContext = cc;
    }

    @Override
    public void dispose() {
        disposeSubeditor();
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        this.cidsBean = cb;
        switchToForm("empty");

        if (cidsBean != null) {
            setNamesAndBands();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setNamesAndBands() {
        final double from = 0;
        final double till = 10;
//        sbm.setMin(from);
//        sbm.setMax(till);
//
//        jband.setMinValue(from);
//        jband.setMaxValue(till);

        // extract geschuetzte Arten
        final List<CidsBean> podest = cidsBean.getBeanCollectionProperty("podeste");
        final List<CidsBean> treppe = cidsBean.getBeanCollectionProperty("treppenlaeufe");
        final List<CidsBean> handlaeufe = cidsBean.getBeanCollectionProperty("handlaeufe");
        final List<CidsBean> stuetzmauer = cidsBean.getBeanCollectionProperty("stuetzmauer");
        final List<CidsBean> absturzsicherung = cidsBean.getBeanCollectionProperty("absturzsicherungen");
        laufList = new ArrayList<CidsBean>();
        leitelementList = new ArrayList<CidsBean>();
        handlaufList = new ArrayList<CidsBean>();
        stuetzmauerList = new ArrayList<CidsBean>();

        if (podest != null) {
            laufList.addAll(podest);
        }
        if (treppe != null) {
            laufList.addAll(treppe);
        }
        if (absturzsicherung != null) {
            leitelementList.addAll(absturzsicherung);
        }
        if (handlaeufe != null) {
            handlaufList.addAll(handlaeufe);
        }
        if (stuetzmauer != null) {
            stuetzmauerList.addAll(stuetzmauer);
        }

        laufList = ObservableCollections.observableList(laufList);
        leitelementList = ObservableCollections.observableList(leitelementList);
        handlaufList = ObservableCollections.observableList(handlaufList);
        stuetzmauerList = ObservableCollections.observableList(stuetzmauerList);

//        stuetzmauerLinksBand.setCidsBeans(stuetzmauerList);
        handlaufLeftBand.setCidsBeans(handlaufList);
        handlaufRightBand.setCidsBeans(handlaufList);
        laufBand.setCidsBeans(laufList);

        handlaufRightBand.addElementResizedListener(resizedListener);
        handlaufLeftBand.addElementResizedListener(resizedListener);
        laufBand.addElementResizedListener(resizedListener);
        leitelementLeftBand.addElementResizedListener(resizedListener);
        leitelementRightBand.addElementResizedListener(resizedListener);
        stuetzmauerLinksBand.addElementResizedListener(resizedListener);
        stuetzmauerRechtsBand.addElementResizedListener(resizedListener);

        refreshAllBands(true);
        jband.bandModelChanged(new BandModelEvent());
//        leitelementRightBand.setCidsBeans(leitelementList);
//        leitelementLeftBand.setCidsBeans(leitelementList);
//        stuetzmauerRechtsBand.setCidsBeans(stuetzmauerList);

        // todo: add listener to synchonize the object list with the cidsBean
// ((ObservableList<CidsBean>)laufList).addObservableListListener(new MassnBezugListListener(
// GupPlanungsabschnittEditor.GUP_UFER_RECHTS,
// cidsBean,
// COLLECTION_PROPERTY));

    }

    /**
     * DOCUMENT ME!
     *
     * @param  withDummies  DOCUMENT ME!
     */
    private void refreshAllBands(final boolean withDummies) {
        laufBand.refresh(withDummies);
        handlaufLeftBand.refresh(withDummies);
        handlaufRightBand.refresh(withDummies);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TreppenElementResizedListener implements ElementResizedListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void elementResized(final ElementResizedEvent e) {
            if ((TreppeBandMember)e.getBandMember() instanceof LaufBandMember) {
                final TreppeBandMember member = (TreppeBandMember)e.getBandMember();
                final double oldValue = e.getOldValue();
                final double newValue = e.getNewValue();
                final double diff = newValue - oldValue;
                double max = Double.MIN_VALUE;

                final List<List<CidsBean>> beans = new ArrayList<List<CidsBean>>();
                beans.add(laufList);
                // beans.add(leitelementList);
                beans.add(handlaufList);
                // beans.add(stuetzmauerList);

                for (int listIndex = 0; listIndex < beans.size(); ++listIndex) {
                    final List<CidsBean> beanList = beans.get(listIndex);

                    for (final CidsBean bean : beanList) {
                        if (!bean.equals(member.getCidsBean())) {
                            double from = (Double)bean.getProperty("position.von");
                            double till = (Double)bean.getProperty("position.bis");

                            if (from > till) {
                                try {
                                    bean.setProperty("position.von", till);
                                    bean.setProperty("position.bis", from);
                                } catch (Exception ex) {
                                    LOG.error("Error while adjust element sizes", ex);
                                }
                                from = (Double)bean.getProperty("position.von");
                                till = (Double)bean.getProperty("position.bis");
                            }

                            if (e.isMax()) {
                                if (from >= oldValue) {
                                    try {
                                        bean.setProperty("position.von", from + diff);
                                    } catch (Exception ex) {
                                        LOG.error("Error while adjust element sizes", ex);
                                    }
                                }
                                if (till >= oldValue) {
                                    try {
                                        bean.setProperty("position.bis", till + diff);
                                    } catch (Exception ex) {
                                        LOG.error("Error while adjust element sizes", ex);
                                    }
                                }

                                if ((Double)bean.getProperty("position.bis") > max) {
                                    max = (Double)bean.getProperty("position.bis");
                                }
                            } else {
                            }
                        }
                    }
                }

                // if (jband.getMaxValue() < max) {
                jband.setMaxValue(max + 1);
                refreshAllBands(false);
                // sbm.setMax(max);
                jband.bandModelChanged(new BandModelEvent());
                // }
                refreshAllBands(true);
                jband.bandModelChanged(new BandModelEvent());

                final BandMember selectedBandMember = jband.getSelectedBandMember();

                modelListener.bandModelChanged(null);

                if (selectedBandMember instanceof BandMemberSelectable) {
                    jband.setSelectedMember((BandMemberSelectable)selectedBandMember);
                }
            } else {
                refreshAllBands(true);
                jband.bandModelChanged(new BandModelEvent());
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TreppenBandModelListener implements BandModelListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void bandModelChanged(final BandModelEvent e) {
            System.out.println("Band model changed");
        }

        @Override
        public void bandModelSelectionChanged(final BandModelEvent e) {
            final BandMember bm;

            bm = jband.getSelectedBandMember();
            jband.setRefreshAvoided(true);
            disposeSubeditor();

            if (bm != null) {
                switchToForm("empty");

                if (bm instanceof PodestBandMember) {
                    // PodestBandMember is also a LaufBandMember. So the test on Podest must be first
                    switchToForm("podeste");
                    treppePodestPanel.setCidsBean(((PodestBandMember)bm).getCidsBean());
                } else if (bm instanceof LaufBandMember) {
                    switchToForm("stufen");
                    treppelaufPanel.setCidsBean(((LaufBandMember)bm).getCidsBean());
                } else if (bm instanceof LeitelementBandMember) {
                    switchToForm("leitelemente");
                    treppeLeitelementpanel.setCidsBean(((LeitelementBandMember)bm).getCidsBean());
                } else if (bm instanceof HandlaufBandMember) {
                    switchToForm("handlaeufe");
                    treppeHandlaufPanel.setCidsBean(((HandlaufBandMember)bm).getCidsBean());
                } else if (bm instanceof StuetzmauerBandMember) {
                    switchToForm("stuetzmauern");
                    treppeStuetzmauerPanel.setCidsBean(((StuetzmauerBandMember)bm).getCidsBean());
                } else if (bm instanceof DummyBandMember) {
                    panChooser.removeAll();
                    panChooser.add(((DummyBandMember)bm).getObjectChooser());
                    switchToForm("chooser");
                }
            } else {
                switchToForm("empty");
            }

            jband.setRefreshAvoided(false);
            jband.bandModelChanged(null);
        }

        @Override
        public void bandModelValuesChanged(final BandModelEvent e) {
            System.out.println("band mdel changed");
        }
    }
}
