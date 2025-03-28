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

import Sirius.navigator.ui.ComponentRegistry;

import de.aedsicad.aaaweb.rest.model.Buchungsblatt;
import de.aedsicad.aaaweb.rest.model.Buchungsstelle;
import de.aedsicad.aaaweb.rest.model.Owner;

import java.awt.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import de.cismet.cids.custom.clientutils.AlkisClientUtils;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.StyleListCellRenderer;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisRestUtils;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.alkis.AlkisSOAPWorkerService;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.StaticDebuggingTools;

import de.cismet.tools.collections.TypeSafeCollections;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlkisEigentuemerPanel extends javax.swing.JPanel implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisEigentuemerPanel.class);

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final Map<String, CidsBean> gotoBeanMap;
    private RetrieveBuchungsblaetterWorker retrieveBuchungsblaetterWorker;
    private final Map<CidsBean, Buchungsblatt> buchungsblaetter;
    private boolean continueInBackground = false;
    private final boolean demoMode = StaticDebuggingTools.checkHomeForFile("demoMode");
    private Map<CidsBean, Collection<CidsBean>> buchungsblaetterToFlurstuecke = new HashMap<>();

    private Listener listener;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane epInhaltBuchungsblatt;
    private javax.swing.JLabel lblBuchungsblaetter;
    private javax.swing.JLabel lblEnthalteneFlurstuecke;
    private javax.swing.JLabel lblInhalt;
    private javax.swing.JList lstBuchungsblaetter;
    private javax.swing.JList lstBuchungsblattFlurstuecke;
    private javax.swing.JPanel panInhaltBuchungsblatt;
    private javax.swing.JScrollPane scpBuchungsblaetter;
    private javax.swing.JScrollPane scpBuchungsblattFlurstuecke;
    private javax.swing.JScrollPane scpInhaltBuchungsblatt;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form AlkisEigentuemerPanel2.
     */
    public AlkisEigentuemerPanel() {
        buchungsblaetter = TypeSafeCollections.newConcurrentHashMap();
        gotoBeanMap = TypeSafeCollections.newHashMap();
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

        scpBuchungsblaetter = new javax.swing.JScrollPane();
        lstBuchungsblaetter = new javax.swing.JList();
        scpBuchungsblattFlurstuecke = new javax.swing.JScrollPane();
        lstBuchungsblattFlurstuecke = new javax.swing.JList();
        lblBuchungsblaetter = new javax.swing.JLabel();
        lblInhalt = new javax.swing.JLabel();
        lblEnthalteneFlurstuecke = new javax.swing.JLabel();
        panInhaltBuchungsblatt = new javax.swing.JPanel();
        scpInhaltBuchungsblatt = new javax.swing.JScrollPane();
        epInhaltBuchungsblatt = new javax.swing.JEditorPane();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        scpBuchungsblaetter.setMaximumSize(new java.awt.Dimension(140, 200));
        scpBuchungsblaetter.setMinimumSize(new java.awt.Dimension(140, 200));
        scpBuchungsblaetter.setOpaque(false);
        scpBuchungsblaetter.setPreferredSize(new java.awt.Dimension(140, 200));

        lstBuchungsblaetter.setOpaque(false);
        lstBuchungsblaetter.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lstBuchungsblaetterMouseClicked(evt);
                }
            });
        lstBuchungsblaetter.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstBuchungsblaetterValueChanged(evt);
                }
            });
        scpBuchungsblaetter.setViewportView(lstBuchungsblaetter);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        add(scpBuchungsblaetter, gridBagConstraints);

        scpBuchungsblattFlurstuecke.setMaximumSize(new java.awt.Dimension(200, 200));
        scpBuchungsblattFlurstuecke.setMinimumSize(new java.awt.Dimension(200, 200));
        scpBuchungsblattFlurstuecke.setOpaque(false);
        scpBuchungsblattFlurstuecke.setPreferredSize(new java.awt.Dimension(200, 200));

        lstBuchungsblattFlurstuecke.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstBuchungsblattFlurstuecke.setOpaque(false);
        scpBuchungsblattFlurstuecke.setViewportView(lstBuchungsblattFlurstuecke);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 10);
        add(scpBuchungsblattFlurstuecke, gridBagConstraints);

        lblBuchungsblaetter.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBuchungsblaetter,
            org.openide.util.NbBundle.getMessage(
                AlkisEigentuemerPanel.class,
                "AlkisEigentuemerPanel.lblBuchungsblaetter.text"));      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(lblBuchungsblaetter, gridBagConstraints);

        lblInhalt.setFont(new java.awt.Font("Tahoma", 1, 11));                                                          // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInhalt,
            org.openide.util.NbBundle.getMessage(AlkisEigentuemerPanel.class, "AlkisEigentuemerPanel.lblInhalt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(lblInhalt, gridBagConstraints);

        lblEnthalteneFlurstuecke.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblEnthalteneFlurstuecke,
            org.openide.util.NbBundle.getMessage(
                AlkisEigentuemerPanel.class,
                "AlkisEigentuemerPanel.lblEnthalteneFlurstuecke.text"));      // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        add(lblEnthalteneFlurstuecke, gridBagConstraints);

        panInhaltBuchungsblatt.setOpaque(false);
        panInhaltBuchungsblatt.setLayout(new java.awt.BorderLayout());

        scpInhaltBuchungsblatt.setBorder(null);
        scpInhaltBuchungsblatt.setMaximumSize(new java.awt.Dimension(250, 200));
        scpInhaltBuchungsblatt.setMinimumSize(new java.awt.Dimension(250, 200));
        scpInhaltBuchungsblatt.setOpaque(false);
        scpInhaltBuchungsblatt.setPreferredSize(new java.awt.Dimension(250, 200));

        epInhaltBuchungsblatt.setEditable(false);
        epInhaltBuchungsblatt.setBorder(null);
        epInhaltBuchungsblatt.setContentType("text/html");            // NOI18N
        epInhaltBuchungsblatt.setText(org.openide.util.NbBundle.getMessage(
                AlkisEigentuemerPanel.class,
                "AlkisEigentuemerPanel.epInhaltBuchungsblatt.text")); // NOI18N
        epInhaltBuchungsblatt.setMaximumSize(new java.awt.Dimension(250, 200));
        epInhaltBuchungsblatt.setMinimumSize(new java.awt.Dimension(250, 200));
        epInhaltBuchungsblatt.setOpaque(false);
        epInhaltBuchungsblatt.setPreferredSize(new java.awt.Dimension(250, 200));
        scpInhaltBuchungsblatt.setViewportView(epInhaltBuchungsblatt);

        panInhaltBuchungsblatt.add(scpInhaltBuchungsblatt, java.awt.BorderLayout.CENTER);
        scpInhaltBuchungsblatt.getViewport().setOpaque(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 10, 15);
        add(panInhaltBuchungsblatt, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstBuchungsblaetterMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lstBuchungsblaetterMouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = lstBuchungsblaetter.getSelectedValue();
            if (selObject instanceof CidsBean) {
                final CidsBean selBean = (CidsBean)selObject;
                ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObject(selBean.getMetaObject(), "");
            }
        }
    }                                                                                   //GEN-LAST:event_lstBuchungsblaetterMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstBuchungsblaetterValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstBuchungsblaetterValueChanged
        if (!evt.getValueIsAdjusting()) {
            final Object[] selectedObjs = lstBuchungsblaetter.getSelectedValues();
            if ((selectedObjs != null) && (selectedObjs.length > 0)) {
                final Collection<CidsBean> buchungsblatter = TypeSafeCollections.newArrayList(selectedObjs.length);
                for (final Object buchungsblatt : selectedObjs) {
                    if (buchungsblatt instanceof CidsBean) {
                        buchungsblatter.add((CidsBean)buchungsblatt);
                    }
                }

                final RetrieveBuchungsblaetterWorker oldWorker = retrieveBuchungsblaetterWorker;
                if (oldWorker != null) {
                    AlkisSOAPWorkerService.cancel(oldWorker);
                }
                retrieveBuchungsblaetterWorker = new RetrieveBuchungsblaetterWorker(buchungsblatter);
                AlkisSOAPWorkerService.execute(retrieveBuchungsblaetterWorker);

                final DefaultListModel<CidsBean> model = new DefaultListModel<>();
                for (final CidsBean buchungsblatt : buchungsblatter) {
                    for (final CidsBean flurstueck : buchungsblatt.getBeanCollectionProperty("landparcels")) {
                        model.addElement(flurstueck);
                    }
                }
                lstBuchungsblattFlurstuecke.setModel(model);
            }
        }
    } //GEN-LAST:event_lstBuchungsblaetterValueChanged

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        lstBuchungsblaetter.setCellRenderer(new StyleListCellRenderer());
        lstBuchungsblattFlurstuecke.setCellRenderer(new StyleListCellRenderer());
        epInhaltBuchungsblatt.addHyperlinkListener(new HyperlinkListener() {

                @Override
                public void hyperlinkUpdate(final HyperlinkEvent e) {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        final CidsBean blatt = gotoBeanMap.get(e.getDescription());
                        if (blatt != null) {
                            continueInBackground = true;
                            ComponentRegistry.getRegistry()
                                    .getDescriptionPane()
                                    .gotoMetaObject(blatt.getMetaObject(), "");
                        } else {
                            LOG.warn("Could not find buchungsblatt bean in gotoMap");
                        }
                    }
                }
            });

        final StyleSheet css = ((HTMLEditorKit)epInhaltBuchungsblatt.getEditorKit()).getStyleSheet();
        final Font font = UIManager.getFont("Label.font");
        final String bodyRule = "body { font-family: " + font.getFamily() + "; "
                    + "font-size: " + font.getSize() + "pt; }";
        final String tableRule = "td { padding-right : 15px; }";
        final String tableHeadRule = "th { padding-right : 15px; }";
        css.addRule(bodyRule);
        css.addRule(tableRule);
        css.addRule(tableHeadRule);
        // Change scroll behaviour: avoid autoscrolls on setText(...)
        final Caret caret = epInhaltBuchungsblatt.getCaret();
        if (caret instanceof DefaultCaret) {
            final DefaultCaret dCaret = (DefaultCaret)caret;
            dCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isContinueInBackground() {
        return continueInBackground;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  continueInBackground  DOCUMENT ME!
     */
    public void setContinueInBackground(final boolean continueInBackground) {
        this.continueInBackground = continueInBackground;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     */
    private void initGotoBeanMap(final Collection<CidsBean> flurstuecke) {
        gotoBeanMap.clear();
        if (flurstuecke != null) {
            for (final CidsBean flurstueck : flurstuecke) {
                final Object buchungsblaetterCollectionObj = flurstueck.getProperty("buchungsblaetter");
                if (buchungsblaetterCollectionObj instanceof List) {
                    final List<CidsBean> blaetterList = (List<CidsBean>)buchungsblaetterCollectionObj;
                    for (final CidsBean blatt : blaetterList) {
                        gotoBeanMap.put(blatt.getMetaObject().getMetaClass().getID()
                                    + AlkisProducts.LINK_SEPARATOR_TOKEN
                                    + blatt.getMetaObject().getID(),
                            blatt);
                    }
                } else {
                    LOG.error("Fehler bei initGotoMap. buchungsbaetter = " + buchungsblaetterCollectionObj);
                }
                final Object adressenCollectionObj = flurstueck.getProperty("adressen");
                if (adressenCollectionObj instanceof List) {
                    final List<CidsBean> adressenList = (List<CidsBean>)adressenCollectionObj;
                    for (final CidsBean adresse : adressenList) {
                        gotoBeanMap.put(adresse.getMetaObject().getMetaClass().getID()
                                    + AlkisProducts.LINK_SEPARATOR_TOKEN
                                    + adresse.getMetaObject().getID(),
                            adresse);
                    }
                } else {
                    LOG.error("Fehler bei initGotoMap. adressen = " + buchungsblaetterCollectionObj);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void cancelWorker() {
        AlkisSOAPWorkerService.cancel(retrieveBuchungsblaetterWorker);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<String, CidsBean> getGotoBeanMap() {
        return gotoBeanMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     */
    public void setFlurstuecke(final Collection<CidsBean> flurstuecke) {
        setFlurstuecke(flurstuecke, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     * @param  listener     DOCUMENT ME!
     */
    public void setFlurstuecke(final Collection<CidsBean> flurstuecke, final Listener listener) {
        this.listener = listener;

        initGotoBeanMap(flurstuecke);

        buchungsblaetterToFlurstuecke.clear();
        final List<CidsBean> buchungsblaetter = new ArrayList<>();
        if (flurstuecke != null) {
            for (final CidsBean flurstueck : flurstuecke) {
                for (final CidsBean buchungsblatt : flurstueck.getBeanCollectionProperty("buchungsblaetter")) {
                    final Collection<CidsBean> flurstueckeBB = buchungsblaetterToFlurstuecke.containsKey(buchungsblatt)
                        ? buchungsblaetterToFlurstuecke.get(buchungsblatt) : new ArrayList<CidsBean>();
                    buchungsblaetterToFlurstuecke.put(buchungsblatt, flurstueckeBB);
                    flurstueckeBB.add(flurstueck);
                }
            }
        }
        buchungsblaetter.addAll(buchungsblaetterToFlurstuecke.keySet());
        Collections.sort(buchungsblaetter, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean t, final CidsBean t1) {
                    return t.toString().compareTo(t1.toString());
                }
            });

        final DefaultListModel listModel = new DefaultListModel();
        for (final CidsBean buchungsblatt : buchungsblaetter) {
            listModel.addElement(buchungsblatt);
        }
        lstBuchungsblaetter.setModel(listModel);

        final int anzahlBuchungsblaetter = lstBuchungsblaetter.getModel().getSize();

        if (anzahlBuchungsblaetter < 5) {
            lblBuchungsblaetter.setVisible(false);
            scpBuchungsblaetter.setVisible(false);
            lblEnthalteneFlurstuecke.setVisible(false);
            scpBuchungsblattFlurstuecke.setVisible(false);
            lblInhalt.setVisible(false);
            final int[] selection = new int[anzahlBuchungsblaetter];
            for (int i = 0; i < selection.length; ++i) {
                selection[i] = i;
            }
            lstBuchungsblaetter.setSelectedIndices(selection);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Buchungsblatt getBuchungsblatt(final CidsBean buchungsblattBean) throws Exception {
        Buchungsblatt buchungsblatt = null;

        if (buchungsblattBean != null) {
            buchungsblatt = buchungsblaetter.get(buchungsblattBean);
            if (buchungsblatt == null) {
                final String buchungsblattcode = String.valueOf(buchungsblattBean.getProperty("buchungsblattcode"));
                if ((buchungsblattcode != null) && (buchungsblattcode.length() > 5)) {
                    if (!demoMode) {
                        buchungsblatt = ClientAlkisRestUtils.getBuchungsblatt(AlkisClientUtils.fixBuchungslattCode(
                                    buchungsblattcode),
                                getConnectionContext());
                    } else {
                        final Owner o = new Owner();
                        o.setForeName("***");
                        o.setSurName("***");
                        buchungsblatt = new Buchungsblatt();
                        buchungsblatt.setBlattart("****");
                        buchungsblatt.setBlattartCode("****");
                        buchungsblatt.setBuchungsblattCode("****");
                        buchungsblatt.setBuchungsstellen(new ArrayList<>());
                        buchungsblatt.setDescriptionOfRechtsgemeinschaft(Arrays.asList("****"));
                        buchungsblatt.setId("****");
                        buchungsblatt.setOffices(null);
                        buchungsblatt.setOwners(Arrays.asList(o));
                        buchungsblatt.setBuchungsblattCode(buchungsblattcode);
                    }

                    buchungsblaetter.put(buchungsblattBean, buchungsblatt);
                }
            }
        }

        return buchungsblatt;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface Listener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        void loadingStarted();
        /**
         * DOCUMENT ME!
         */
        void loadingDone();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class RetrieveBuchungsblaetterWorker extends SwingWorker<String, String> {

        //~ Static fields/initializers -----------------------------------------

        private static final String LOAD_TEXT = "Weitere werden geladen...";

        //~ Instance fields ----------------------------------------------------

        private final Collection<CidsBean> buchungsblaetterBeans;
        private final StringBuilder currentInfoText;
        private int current;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieveBuchungsblaetterWorker object.
         *
         * @param  buchungsblatterBeans  DOCUMENT ME!
         */
        public RetrieveBuchungsblaetterWorker(final Collection<CidsBean> buchungsblatterBeans) {
            this.buchungsblaetterBeans = buchungsblatterBeans;
            this.currentInfoText = new StringBuilder();
            if (listener != null) {
                listener.loadingStarted();
            }
            epInhaltBuchungsblatt.setText("Wird geladen... (" + buchungsblatterBeans.size() + ")");
            current = 1;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected String doInBackground() throws Exception {
            for (final CidsBean buchungsblattBean : buchungsblaetterBeans) {
                if (buchungsblattBean != null) {
                    final Buchungsblatt buchungsblatt = getBuchungsblatt(buchungsblattBean);
                    if (buchungsblatt.getBuchungsstellen() != null) {
                        for (final Buchungsstelle stelle : buchungsblatt.getBuchungsstellen()) {
                            stelle.getFraction();
                        }
                    }
                    currentInfoText.append(AlkisProducts.buchungsblattToHtml(
                            buchungsblaetterToFlurstuecke.get(buchungsblattBean).iterator().next(),
                            buchungsblatt,
                            buchungsblattBean));
                    if (isCancelled()) {
                        return currentInfoText.toString();
                    }
                    publish(currentInfoText.toString());
                }
            }
            return currentInfoText.toString();
        }

        /**
         * DOCUMENT ME!
         *
         * @param  chunks  DOCUMENT ME!
         */
        @Override
        protected void process(final List<String> chunks) {
            if (!isCancelled()) {
                final StringBuilder infos = new StringBuilder(chunks.get(chunks.size() - 1));
                infos.append(LOAD_TEXT)
                        .append(" (")
                        .append((current += chunks.size()))
                        .append(" / ")
                        .append(buchungsblaetterBeans.size())
                        .append(")");
                epInhaltBuchungsblatt.setText("<table>" + infos.toString() + "</table>");
//                epInhaltBuchungsblatt.setText("<font face=\"" + FONT + "\" size=\"11\">" + "<table>" + infos.toString() + "</table>" + "</font>");
//                epInhaltBuchungsblatt.setText("<pre>" + infos.toString() + "</pre>");
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public String toString() {
            return super.toString() + " " + buchungsblaetterBeans;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            if (!isCancelled()) {
                try {
                    if (listener != null) {
                        listener.loadingDone();
                    }
                    epInhaltBuchungsblatt.setText(get());
//                    epInhaltBuchungsblatt.setText("<pre>" + get() + "</pre>");
                } catch (InterruptedException ex) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(ex, ex);
                    }
                } catch (Exception ex) {
                    epInhaltBuchungsblatt.setText("Fehler beim Empfangen.");
                    if (!demoMode) {
                        ObjectRendererUtils.showExceptionWindowToUser(
                            "Fehler beim Empfangen",
                            ex,
                            AlkisEigentuemerPanel.this);
                    }
                    LOG.error(ex, ex);
                }
            }
        }
    }
}
