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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import de.cismet.cids.custom.wunda_blau.band.DummyBandMember;
import de.cismet.cids.custom.wunda_blau.band.ElementResizedEvent;
import de.cismet.cids.custom.wunda_blau.band.ElementResizedListener;
import de.cismet.cids.custom.wunda_blau.band.EntwaesserungBand;
import de.cismet.cids.custom.wunda_blau.band.EntwaesserungBandMember;
import de.cismet.cids.custom.wunda_blau.band.HandlaufBand;
import de.cismet.cids.custom.wunda_blau.band.HandlaufBandMember;
import de.cismet.cids.custom.wunda_blau.band.LaufBand;
import de.cismet.cids.custom.wunda_blau.band.LaufBandMember;
import de.cismet.cids.custom.wunda_blau.band.LeitelementBand;
import de.cismet.cids.custom.wunda_blau.band.LeitelementBandMember;
import de.cismet.cids.custom.wunda_blau.band.PodestBandMember;
import de.cismet.cids.custom.wunda_blau.band.Side;
import de.cismet.cids.custom.wunda_blau.band.StuetzmauerBand;
import de.cismet.cids.custom.wunda_blau.band.StuetzmauerBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppeBandMember;
import de.cismet.cids.custom.wunda_blau.band.TreppeObservableListListener;
import de.cismet.cids.custom.wunda_blau.band.TreppenBand;
import de.cismet.cids.custom.wunda_blau.band.actions.AddItem;
import de.cismet.cids.custom.wunda_blau.band.actions.DeleteItem;
import de.cismet.cids.custom.wunda_blau.band.actions.SelectNext;
import de.cismet.cids.custom.wunda_blau.band.actions.SelectPrevious;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

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
public class TreppenBandPanel extends javax.swing.JPanel implements ConnectionContextStore,
    Disposable,
    CidsBeanStore,
    EditorSaveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            TreppenBandPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final JBand jband = new JBand();
    private final TreppenBand laufBand = new LaufBand(
            Side.BOTH,
            "Treppenläufe/Podeste",
            jband);
    private final TreppenBand handlaufRightBand = new HandlaufBand(
            Side.RIGHT,
            "Handläufe rechts",
            jband);
    private final TreppenBand handlaufLeftBand = new HandlaufBand(
            Side.LEFT,
            "Handläufe links",
            jband);
    private final TreppenBand leitelementRightBand = new LeitelementBand(
            Side.RIGHT,
            "Leitelemente rechts",
            jband);
    private final TreppenBand leitelementLeftBand = new LeitelementBand(
            Side.LEFT,
            "Leitelemente links",
            jband);
    private final TreppenBand stuetzmauerLinksBand = new StuetzmauerBand(
            Side.LEFT,
            "Stützmauer links",
            jband);
    private final TreppenBand stuetzmauerRechtsBand = new StuetzmauerBand(
            Side.RIGHT,
            "Stützmauer rechts",
            jband);
    private final TreppenBand entwaesserungBand = new EntwaesserungBand(
            Side.BOTH,
            "Entwässerung",
            jband);
    private final BandModelListener modelListener = new TreppenBandModelListener();
    private final SimpleBandModel sbm = new SimpleBandModel();
    private ConnectionContext connectionContext;
    private CidsBean cidsBean;
    private List<CidsBean> laufList = new ArrayList<>();
    private List<CidsBean> leitelementList = new ArrayList<>();
    private List<CidsBean> handlaufList = new ArrayList<>();
    private List<CidsBean> stuetzmauerList = new ArrayList<>();
    private List<CidsBean> entwaesserungList = new ArrayList<>();
    private final TreppenElementResizedListener resizedListener = new TreppenElementResizedListener();
    private boolean tempReadOnly = false;
    private boolean readOnly = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAddAfter;
    private javax.swing.JButton butAddAfterSecond;
    private javax.swing.JButton butAddBefore;
    private javax.swing.JButton butAddBeforeSecond;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butPrev;
    private javax.swing.JButton butRemove;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel panBand;
    private javax.swing.JPanel panBand1;
    private javax.swing.JPanel panChooser;
    private javax.swing.JPanel panEntwaesserung;
    private javax.swing.JPanel panHandlaeuf;
    private javax.swing.JPanel panHeader;
    private javax.swing.JPanel panInfoContent;
    private javax.swing.JPanel panLeitelement;
    private javax.swing.JPanel panPodest;
    private javax.swing.JPanel panStuetzmauern;
    private javax.swing.JPanel panSummary;
    private javax.swing.JPanel panTreppenlauf;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEntwaesserungPanel treppeEntwaesserungPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaufPanel treppeHandlaufPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaufPanel treppeLaufPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementPanel treppeLeitelementPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestPanel treppePodestPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauerPanel treppeStuetzmauerPanel1;
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
    public TreppenBandPanel(final boolean readOnly,
            final ConnectionContext connectionContext) {
        this.readOnly = readOnly;
        jband.setReadOnly(readOnly);
        this.connectionContext = connectionContext;
        initComponents();
        stuetzmauerLinksBand.setReadOnly(readOnly);
        handlaufLeftBand.setReadOnly(readOnly);
        handlaufRightBand.setReadOnly(readOnly);
        laufBand.setReadOnly(readOnly);
        leitelementRightBand.setReadOnly(readOnly);
        leitelementLeftBand.setReadOnly(readOnly);
        stuetzmauerRechtsBand.setReadOnly(readOnly);
        entwaesserungBand.setReadOnly(readOnly);
        sbm.addBand(stuetzmauerLinksBand);
        sbm.addBand(leitelementLeftBand);
        sbm.addBand(handlaufLeftBand);
        sbm.addBand(laufBand);
        sbm.addBand(handlaufRightBand);
        sbm.addBand(leitelementRightBand);
        sbm.addBand(stuetzmauerRechtsBand);
        sbm.addBand(entwaesserungBand);
        jband.setModel(sbm);

        panBand.add(jband, BorderLayout.CENTER);
        jband.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sbm.addBandModelListener(modelListener);

        switchToForm("summary");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  summaryPanel  DOCUMENT ME!
     */
    public void setZusammenfassung(final JPanel summaryPanel) {
        panSummary.add(summaryPanel, BorderLayout.CENTER);
    }

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

        panHeader = new javax.swing.JPanel();
        panBand = new javax.swing.JPanel();
        panBand1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        butPrev = new javax.swing.JButton();
        butAddBeforeSecond = new javax.swing.JButton();
        butAddBefore = new javax.swing.JButton();
        butAddAfter = new javax.swing.JButton();
        butAddAfterSecond = new javax.swing.JButton();
        butRemove = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        panInfoContent = new javax.swing.JPanel();
        panTreppenlauf = new javax.swing.JPanel();
        treppeLaufPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaufPanel(
                !readOnly,
                getConnectionContext());
        panPodest = new javax.swing.JPanel();
        treppePodestPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestPanel(
                !readOnly,
                getConnectionContext());
        panLeitelement = new javax.swing.JPanel();
        treppeLeitelementPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementPanel(
                !readOnly,
                getConnectionContext());
        panHandlaeuf = new javax.swing.JPanel();
        treppeHandlaufPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaufPanel(
                !readOnly,
                getConnectionContext());
        panStuetzmauern = new javax.swing.JPanel();
        treppeStuetzmauerPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauerPanel(
                !readOnly,
                getConnectionContext());
        panEntwaesserung = new javax.swing.JPanel();
        treppeEntwaesserungPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEntwaesserungPanel(
                !readOnly,
                getConnectionContext());
        panChooser = new javax.swing.JPanel();
        panSummary = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(800, 510));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(800, 510));
        setLayout(new java.awt.GridBagLayout());

        panHeader.setOpaque(false);
        panHeader.setLayout(new java.awt.GridBagLayout());

        panBand.setOpaque(false);
        panBand.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panHeader.add(panBand, gridBagConstraints);

        panBand1.setMaximumSize(new java.awt.Dimension(75, 230));
        panBand1.setMinimumSize(new java.awt.Dimension(75, 230));
        panBand1.setOpaque(false);
        panBand1.setPreferredSize(new java.awt.Dimension(75, 230));
        panBand1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setOpaque(false);

        butPrev.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.SelectPrevious());
        org.openide.awt.Mnemonics.setLocalizedText(
            butPrev,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butPrev.text",
                new Object[] {})); // NOI18N
        butPrev.setAlignmentX(0.5F);
        butPrev.setFocusable(false);
        butPrev.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butPrev.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butPrev);

        butAddBeforeSecond.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.AddItem(false, true));
        org.openide.awt.Mnemonics.setLocalizedText(
            butAddBeforeSecond,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butAddBeforeSecond.text",
                new Object[] {})); // NOI18N
        butAddBeforeSecond.setAlignmentX(0.5F);
        butAddBeforeSecond.setFocusable(false);
        butAddBeforeSecond.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butAddBeforeSecond.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butAddBeforeSecond);

        butAddBefore.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.AddItem(false, false));
        org.openide.awt.Mnemonics.setLocalizedText(
            butAddBefore,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butAddBefore.text",
                new Object[] {})); // NOI18N
        butAddBefore.setAlignmentX(0.5F);
        butAddBefore.setFocusable(false);
        butAddBefore.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butAddBefore.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butAddBefore);

        butAddAfter.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.AddItem(true, false));
        butAddAfter.setAlignmentX(0.5F);
        butAddAfter.setFocusable(false);
        butAddAfter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butAddAfter.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butAddAfter);

        butAddAfterSecond.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.AddItem(true, true));
        org.openide.awt.Mnemonics.setLocalizedText(
            butAddAfterSecond,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butAddAfterSecond.text",
                new Object[] {})); // NOI18N
        butAddAfterSecond.setAlignmentX(0.5F);
        butAddAfterSecond.setFocusable(false);
        butAddAfterSecond.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butAddAfterSecond.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butAddAfterSecond);

        butRemove.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.DeleteItem());
        org.openide.awt.Mnemonics.setLocalizedText(
            butRemove,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butRemove.text",
                new Object[] {})); // NOI18N
        butRemove.setAlignmentX(0.5F);
        butRemove.setFocusable(false);
        butRemove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butRemove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butRemove);

        butNext.setAction(new de.cismet.cids.custom.wunda_blau.band.actions.SelectNext());
        org.openide.awt.Mnemonics.setLocalizedText(
            butNext,
            org.openide.util.NbBundle.getMessage(
                TreppenBandPanel.class,
                "TreppenBandPanel.butNext.text",
                new Object[] {})); // NOI18N
        butNext.setAlignmentX(0.5F);
        butNext.setFocusable(false);
        butNext.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        butNext.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(butNext);

        panBand1.add(jToolBar1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panHeader.add(panBand1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(panHeader, gridBagConstraints);

        panInfoContent.setOpaque(false);
        panInfoContent.setLayout(new java.awt.CardLayout());
        panInfoContent.setLayout(new PageViewer());

        panTreppenlauf.setOpaque(false);
        panTreppenlauf.setLayout(new java.awt.BorderLayout());
        panTreppenlauf.add(treppeLaufPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panTreppenlauf, "stufen");

        panPodest.setOpaque(false);
        panPodest.setLayout(new java.awt.BorderLayout());
        panPodest.add(treppePodestPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panPodest, "podeste");

        panLeitelement.setOpaque(false);
        panLeitelement.setLayout(new java.awt.BorderLayout());
        panLeitelement.add(treppeLeitelementPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panLeitelement, "leitelemente");

        panHandlaeuf.setOpaque(false);
        panHandlaeuf.setLayout(new java.awt.BorderLayout());
        panHandlaeuf.add(treppeHandlaufPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panHandlaeuf, "handlaeufe");

        panStuetzmauern.setOpaque(false);
        panStuetzmauern.setLayout(new java.awt.BorderLayout());
        panStuetzmauern.add(treppeStuetzmauerPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panStuetzmauern, "stuetzmauern");

        panEntwaesserung.setOpaque(false);
        panEntwaesserung.setLayout(new java.awt.BorderLayout());
        panEntwaesserung.add(treppeEntwaesserungPanel1, java.awt.BorderLayout.CENTER);

        panInfoContent.add(panEntwaesserung, "entwaesserung");

        panChooser.setOpaque(false);
        panChooser.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panChooser, "chooser");

        panSummary.setOpaque(false);
        panSummary.setLayout(new java.awt.BorderLayout());
        panInfoContent.add(panSummary, "summary");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 5, 0);
        add(panInfoContent, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

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
                    TreppenBandPanel.this.revalidate();
                    TreppenBandPanel.this.repaint();
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
        treppeLaufPanel1.dispose();
        treppeHandlaufPanel1.dispose();
        treppeLeitelementPanel1.dispose();
        treppePodestPanel1.dispose();
        treppeStuetzmauerPanel1.dispose();
        treppeEntwaesserungPanel1.dispose();
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
        switchToForm("summary");

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
        sbm.setMin(0);
//        sbm.setMax(till);
//
        jband.setMinValue(0);
//        jband.setMaxValue(till);

        // extract geschuetzte Arten
        final List<CidsBean> podest = cidsBean.getBeanCollectionProperty("podeste");
        final List<CidsBean> treppe = cidsBean.getBeanCollectionProperty("treppenlaeufe");
        final List<CidsBean> handlaeufe = cidsBean.getBeanCollectionProperty("handlaeufe");
        final List<CidsBean> stuetzmauer = cidsBean.getBeanCollectionProperty("stuetzmauern");
        final List<CidsBean> absturzsicherung = cidsBean.getBeanCollectionProperty("absturzsicherungen");
        final CidsBean entwaesserung = (CidsBean)cidsBean.getProperty("entwaesserung");
        laufList = new ArrayList<>();
        leitelementList = new ArrayList<>();
        handlaufList = new ArrayList<>();
        stuetzmauerList = new ArrayList<>();
        entwaesserungList = new ArrayList<>();

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
        if (entwaesserung != null) {
            entwaesserungList.add(entwaesserung);
        }

        laufList = ObservableCollections.observableList(laufList);
        leitelementList = ObservableCollections.observableList(leitelementList);
        handlaufList = ObservableCollections.observableList(handlaufList);
        stuetzmauerList = ObservableCollections.observableList(stuetzmauerList);
        entwaesserungList = ObservableCollections.observableList(entwaesserungList);

        handlaufLeftBand.setCidsBeans(handlaufList);
        handlaufRightBand.setCidsBeans(handlaufList);
        laufBand.setCidsBeans(laufList);
        leitelementLeftBand.setCidsBeans(leitelementList);
        leitelementRightBand.setCidsBeans(leitelementList);
        stuetzmauerLinksBand.setCidsBeans(stuetzmauerList);
        stuetzmauerRechtsBand.setCidsBeans(stuetzmauerList);
        entwaesserungBand.setCidsBeans(entwaesserungList);

        handlaufRightBand.addElementResizedListener(resizedListener);
        handlaufLeftBand.addElementResizedListener(resizedListener);
        laufBand.addElementResizedListener(resizedListener);
        leitelementLeftBand.addElementResizedListener(resizedListener);
        leitelementRightBand.addElementResizedListener(resizedListener);
        stuetzmauerLinksBand.addElementResizedListener(resizedListener);
        stuetzmauerRechtsBand.addElementResizedListener(resizedListener);

        refreshAllBands(true);
        jband.bandModelChanged(new BandModelEvent());

        // todo: add listener to synchonize the object list with the cidsBean
        ((ObservableList<CidsBean>)handlaufList).addObservableListListener(new TreppeObservableListListener(
                cidsBean,
                "handlaeufe",
                null));
        ((ObservableList<CidsBean>)leitelementList).addObservableListListener(new TreppeObservableListListener(
                cidsBean,
                "absturzsicherungen",
                null));
        ((ObservableList<CidsBean>)stuetzmauerList).addObservableListListener(new TreppeObservableListListener(
                cidsBean,
                "stuetzmauern",
                null));
        ((ObservableList<CidsBean>)entwaesserungList).addObservableListListener(new TreppeObservableListListener(
                cidsBean,
                "entwaesserung",
                null));
        ((ObservableList<CidsBean>)laufList).addObservableListListener(new TreppeObservableListListener(
                cidsBean,
                "treppenlaeufe",
                "podeste"));
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
        leitelementRightBand.refresh(withDummies);
        leitelementLeftBand.refresh(withDummies);
        stuetzmauerRechtsBand.refresh(withDummies);
        stuetzmauerLinksBand.refresh(withDummies);
        entwaesserungBand.refresh(withDummies);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tempReadOnly  DOCUMENT ME!
     */
    private void setModeTempReadOnly(final boolean tempReadOnly) {
        if (tempReadOnly != this.tempReadOnly) {
            this.tempReadOnly = tempReadOnly;
            jband.setSelectedMember((BandMemberSelectable)null);
            treppeLaufPanel1.setEditable(!tempReadOnly);
            treppeHandlaufPanel1.setEditable(!tempReadOnly);
            treppeLeitelementPanel1.setEditable(!tempReadOnly);
            treppePodestPanel1.setEditable(!tempReadOnly);
            treppeStuetzmauerPanel1.setEditable(!tempReadOnly);
            treppeEntwaesserungPanel1.setEditable(!tempReadOnly);

            final List<TreppenBand> bands = new ArrayList<>();
            bands.add(handlaufLeftBand);
            bands.add(handlaufRightBand);
            bands.add(leitelementRightBand);
            bands.add(leitelementLeftBand);
            bands.add(stuetzmauerRechtsBand);
            bands.add(stuetzmauerLinksBand);
            bands.add(laufBand);
            bands.add(entwaesserungBand);

            if (tempReadOnly) {
                for (int index = 0; index < bands.size(); ++index) {
                    final TreppenBand band = bands.get(index);

                    if (!band.hasCollition()) {
                        band.setReadOnly(true, true);
                    }
                }
            } else {
                for (int index = 0; index < bands.size(); ++index) {
                    final TreppenBand band = bands.get(index);

                    band.setReadOnly(false, false);
                }
            }
            // this does also refreshs the colors in the bands
            jband.setSelectedMember((BandMemberSelectable)null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean hasCollision() {
        final List<TreppenBand> bands = new ArrayList<>();
        bands.add(handlaufLeftBand);
        bands.add(handlaufRightBand);
        bands.add(leitelementRightBand);
        bands.add(leitelementLeftBand);
        bands.add(stuetzmauerRechtsBand);
        bands.add(stuetzmauerLinksBand);
        bands.add(laufBand);
        bands.add(entwaesserungBand);

        for (int index = 0; index < bands.size(); ++index) {
            if (bands.get(index).hasCollition()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        if (tempReadOnly) {
            JOptionPane.showMessageDialog(
                this,
                "Es existiert ein ungültiges (überlappendes) Band.\nSie können  erst speichern, wenn alle Bänder gültig sind",
                "Ungültiges Band gefunden",
                JOptionPane.ERROR_MESSAGE);
        }
        return !tempReadOnly;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class TreppenElementResizedListener implements ElementResizedListener {

        //~ Instance fields ----------------------------------------------------

        private boolean inResize = false;

        //~ Methods ------------------------------------------------------------

        @Override
        public void elementResized(final ElementResizedEvent e) {
            try {
                if (inResize) {
                    return;
                }
                inResize = true;
                if (!e.isRefreshDummiesOnly() && ((TreppeBandMember)e.getBandMember() instanceof LaufBandMember)) {
                    final TreppeBandMember member = (TreppeBandMember)e.getBandMember();
                    final double oldValue = e.getOldValue();
                    final double newValue = e.getNewValue();
                    final double diff = newValue - oldValue;

                    final List<List<CidsBean>> beans = new ArrayList<List<CidsBean>>();
                    beans.add(laufList);
                    beans.add(leitelementList);
                    beans.add(handlaufList);
                    beans.add(stuetzmauerList);

                    for (int listIndex = 0; listIndex < beans.size(); ++listIndex) {
                        final List<CidsBean> beanList = beans.get(listIndex);

                        for (final CidsBean bean : beanList) {
                            if (!bean.equals(member.getCidsBean())
                                        && ((AddItem.exception == null) || !AddItem.exception.equals(bean))) {
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

                                    if (bean.getProperty("position.von").equals(
                                                    (Double)bean.getProperty("position.bis"))) {
                                        try {
                                            bean.setProperty(
                                                "position.von",
                                                (Double)bean.getProperty("position.von")
                                                        - 1.0);
                                        } catch (Exception ex) {
                                            LOG.error("Error while adjust element sizes", ex);
                                        }
                                        try {
                                            bean.setProperty(
                                                "position.bis",
                                                (Double)bean.getProperty("position.bis")
                                                        + 1.0);
                                        } catch (Exception ex) {
                                            LOG.error("Error while adjust element sizes", ex);
                                        }
                                    }
                                } else {
                                }
                            }
                        }
                    }

                    BandMember selectedBandMember = jband.getSelectedBandMember();
                    if (selectedBandMember instanceof DummyBandMember) {
                        selectedBandMember = null;
                    }
                    jband.setMaxValue(getMaxSize());
                    refreshAllBands(false);
                    jband.bandModelChanged(new BandModelEvent());
                    refreshAllBands(true);
                    jband.bandModelChanged(new BandModelEvent());

                    modelListener.bandModelChanged(null);

                    if (selectedBandMember instanceof BandMemberSelectable) {
                        BandMember selectedMember = handlaufRightBand.getMemberByBean(
                                ((TreppeBandMember)selectedBandMember).getCidsBean());
                        if (selectedMember == null) {
                            selectedMember = handlaufLeftBand.getMemberByBean(((TreppeBandMember)selectedBandMember)
                                            .getCidsBean());
                        }
                        if (selectedMember == null) {
                            selectedMember = laufBand.getMemberByBean(((TreppeBandMember)selectedBandMember)
                                            .getCidsBean());
                        }
                        if (selectedMember == null) {
                            selectedMember = leitelementLeftBand.getMemberByBean(((TreppeBandMember)selectedBandMember)
                                            .getCidsBean());
                        }
                        if (selectedMember == null) {
                            selectedMember = leitelementRightBand.getMemberByBean(((TreppeBandMember)selectedBandMember)
                                            .getCidsBean());
                        }
                        if (selectedMember == null) {
                            selectedMember = stuetzmauerLinksBand.getMemberByBean(((TreppeBandMember)selectedBandMember)
                                            .getCidsBean());
                        }
                        if (selectedMember == null) {
                            selectedMember = stuetzmauerRechtsBand.getMemberByBean(
                                    ((TreppeBandMember)selectedBandMember).getCidsBean());
                        }

                        if (selectedMember != null) {
                            jband.setSelectedMember((BandMemberSelectable)selectedMember);
                        }
                    }
                } else {
                    final BandMember selectedBandMember = jband.getSelectedBandMember();
                    laufBand.refresh(false);
                    jband.setMaxValue(getMaxSize());
                    refreshAllBands(true);
                    jband.bandModelChanged(new BandModelEvent());
                    if (selectedBandMember instanceof BandMemberSelectable) {
                        jband.setSelectedMember((BandMemberSelectable)null);
                        ((BandMemberSelectable)selectedBandMember).setSelected(false);
                        jband.setSelectedMember((BandMemberSelectable)selectedBandMember);
                    }
                }

                setModeTempReadOnly(hasCollision());
                inResize = false;
            } catch (Throwable t) {
                LOG.error("Error during resize", t);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private double getMaxSize() {
            final List<List<CidsBean>> beans = new ArrayList<>();
            beans.add(laufList);
            beans.add(leitelementList);
            beans.add(handlaufList);
            beans.add(stuetzmauerList);
            double max = Math.max(Math.max(leitelementList.size(), handlaufList.size()), stuetzmauerList.size());

            for (final CidsBean bean : laufList) {
                final double from = (Double)bean.getProperty("position.von");
                final double till = (Double)bean.getProperty("position.bis");
                final double maxBean = Math.max(from, till);

                if (maxBean > max) {
                    max = maxBean;
                }
            }

            return max;
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
        }

        @Override
        public void bandModelSelectionChanged(final BandModelEvent e) {
            final BandMember bm;

            bm = jband.getSelectedBandMember();
            jband.setRefreshAvoided(true);
            disposeSubeditor();

            if (bm != null) {
                switchToForm("summary");

                if (bm instanceof PodestBandMember) {
                    // PodestBandMember is also a LaufBandMember. So the test on Podest must be first
                    switchToForm("podeste");
                    treppePodestPanel1.setCidsBean(((PodestBandMember)bm).getCidsBean());
                    final String objectName = ((PodestBandMember)bm).getParentBand().getAllowedObjectNames()[0];
                    final String objectTable = ((PodestBandMember)bm).getParentBand().getAllowedObjectTableNames()[0];
                    final String secondObjectName = ((PodestBandMember)bm).getParentBand().getAllowedObjectNames()[1];
                    final String secondObjectTable =
                        ((PodestBandMember)bm).getParentBand().getAllowedObjectTableNames()[1];

                    activateControls((PodestBandMember)bm,
                        objectName,
                        secondObjectName,
                        objectTable,
                        secondObjectTable);
                } else if (bm instanceof LaufBandMember) {
                    switchToForm("stufen");
                    treppeLaufPanel1.setCidsBean(((LaufBandMember)bm).getCidsBean());
                    final String objectName = ((LaufBandMember)bm).getParentBand().getAllowedObjectNames()[0];
                    final String objectTable = ((LaufBandMember)bm).getParentBand().getAllowedObjectTableNames()[0];
                    final String secondObjectName = ((LaufBandMember)bm).getParentBand().getAllowedObjectNames()[1];
                    final String secondObjectTable =
                        ((LaufBandMember)bm).getParentBand().getAllowedObjectTableNames()[1];

                    activateControls((LaufBandMember)bm, objectName, secondObjectName, objectTable, secondObjectTable);
                } else if (bm instanceof LeitelementBandMember) {
                    switchToForm("leitelemente");
                    treppeLeitelementPanel1.setCidsBean(((LeitelementBandMember)bm).getCidsBean());
                    final String objectName = ((LeitelementBandMember)bm).getParentBand().getAllowedObjectNames()[0];
                    final String objectTable =
                        ((LeitelementBandMember)bm).getParentBand().getAllowedObjectTableNames()[0];

                    activateControls((LeitelementBandMember)bm, objectName, null, objectTable, null);
                } else if (bm instanceof HandlaufBandMember) {
                    switchToForm("handlaeufe");
                    treppeHandlaufPanel1.setCidsBean(((HandlaufBandMember)bm).getCidsBean());
                    final String objectName = ((HandlaufBandMember)bm).getParentBand().getAllowedObjectNames()[0];
                    final String objectTable = ((HandlaufBandMember)bm).getParentBand().getAllowedObjectTableNames()[0];

                    activateControls((HandlaufBandMember)bm, objectName, null, objectTable, null);
                } else if (bm instanceof StuetzmauerBandMember) {
                    switchToForm("stuetzmauern");
                    treppeStuetzmauerPanel1.setCidsBean(((StuetzmauerBandMember)bm).getCidsBean());
                    activateControls(null, null, null, null, null);
                    if (!readOnly) {
                        ((DeleteItem)butRemove.getAction()).init((StuetzmauerBandMember)bm);
                    }
                    ((SelectNext)butNext.getAction()).init((StuetzmauerBandMember)bm);
                    ((SelectPrevious)butPrev.getAction()).init((StuetzmauerBandMember)bm);
                } else if (bm instanceof EntwaesserungBandMember) {
                    switchToForm("entwaesserung");
                    treppeEntwaesserungPanel1.setCidsBean(((EntwaesserungBandMember)bm).getCidsBean());
                    activateControls(null, null, null, null, null);
                    if (!readOnly) {
                        ((DeleteItem)butRemove.getAction()).init((EntwaesserungBandMember)bm);
                    }
                } else if (bm instanceof DummyBandMember) {
                    panChooser.removeAll();
                    panChooser.add(((DummyBandMember)bm).getObjectChooser());
                    activateControls(null, null, null, null, null);
                    switchToForm("chooser");
                }
            } else {
                switchToForm("summary");
                activateControls(null, null, null, null, null);
            }

            jband.setRefreshAvoided(false);
            jband.bandModelChanged(null);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  member             DOCUMENT ME!
         * @param  objectName         DOCUMENT ME!
         * @param  secondObjectName   DOCUMENT ME!
         * @param  objectTable        DOCUMENT ME!
         * @param  secondObjectTable  DOCUMENT ME!
         */
        private void activateControls(final TreppeBandMember member,
                final String objectName,
                final String secondObjectName,
                final String objectTable,
                final String secondObjectTable) {
            final boolean active = member != null;

            if (member != null) {
                ((AddItem)butAddAfter.getAction()).init(member, true, objectTable, objectName);
                if (secondObjectName != null) {
                    ((AddItem)butAddAfterSecond.getAction()).init(member, true, secondObjectTable, secondObjectName);
                } else {
                    ((AddItem)butAddAfterSecond.getAction()).deactivate();
                }
                ((AddItem)butAddBefore.getAction()).init(member, false, objectTable, objectName);
                if (secondObjectName != null) {
                    ((AddItem)butAddBeforeSecond.getAction()).init(member, false, secondObjectTable, secondObjectName);
                } else {
                    ((AddItem)butAddBeforeSecond.getAction()).deactivate();
                }

                ((DeleteItem)butRemove.getAction()).init(member);
                ((SelectNext)butNext.getAction()).init(member);
                ((SelectPrevious)butPrev.getAction()).init(member);
            } else {
                ((AddItem)butAddAfter.getAction()).deactivate();
                ((AddItem)butAddAfterSecond.getAction()).deactivate();
                ((AddItem)butAddBefore.getAction()).deactivate();
                ((AddItem)butAddBeforeSecond.getAction()).deactivate();
                ((DeleteItem)butRemove.getAction()).deactivate();
                ((SelectNext)butNext.getAction()).deactivate();
                ((SelectPrevious)butPrev.getAction()).deactivate();
            }

            if (readOnly) {
                ((AddItem)butAddAfter.getAction()).deactivate();
                ((AddItem)butAddAfterSecond.getAction()).deactivate();
                ((AddItem)butAddBefore.getAction()).deactivate();
                ((AddItem)butAddBeforeSecond.getAction()).deactivate();
                ((DeleteItem)butRemove.getAction()).deactivate();
            }
        }

        @Override
        public void bandModelValuesChanged(final BandModelEvent e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class PageViewer extends CardLayout {

        //~ Methods ------------------------------------------------------------

        @Override
        public Dimension preferredLayoutSize(final Container parent) {
            final Component current = findCurrentComponent(parent);
            if (current != null) {
                final Insets insets = parent.getInsets();
                final Dimension pref = current.getPreferredSize();
                pref.width += insets.left + insets.right;
                pref.height += insets.top + insets.bottom;
                return pref;
            }
            return super.preferredLayoutSize(parent);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   parent  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Component findCurrentComponent(final Container parent) {
            for (final Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    return comp;
                }
            }
            return null;
        }
    }
}
