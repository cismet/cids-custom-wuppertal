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

import org.apache.log4j.Logger;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeStuetzmauernPanel extends javax.swing.JPanel implements Disposable, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeStuetzmauernPanel.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean netbeansDesignDummy;
    private List<CidsBean> cidsBeans;
    private final boolean editable;
    private final HashMap<CidsBean, CidsBean> zustandBeanMap = new HashMap<>();
    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    Box.Filler filler1;
    JPanel jPanel1;
    TreppeStuetzmauerPanel treppeStuetzmauerPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeStuetzmauernPanel object.
     */
    public TreppeStuetzmauernPanel() {
        this(ConnectionContext.createDeprecated());
    }

    /**
     * Creates a new TreppeStuetzmauernPanel object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeStuetzmauernPanel(final ConnectionContext connectionContext) {
        this(true, true, connectionContext);
    }

    /**
     * Creates a new TreppeStuetzmauernPanel object.
     *
     * @param  editable           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeStuetzmauernPanel(final boolean editable, final ConnectionContext connectionContext) {
        this(editable, false, connectionContext);
    }

    /**
     * Creates new form TreppeStuetzmauernPanel.
     *
     * @param  editable             DOCUMENT ME!
     * @param  netbeansDesignDummy  DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public TreppeStuetzmauernPanel(final boolean editable,
            final boolean netbeansDesignDummy,
            final ConnectionContext connectionContext) {
        this.netbeansDesignDummy = netbeansDesignDummy;
        this.editable = editable;
        this.connectionContext = connectionContext;
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<CidsBean> getZustandBeans() {
        return zustandBeanMap.values();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        zustandBeanMap.clear();
        jPanel1.removeAll();

        this.cidsBeans = cidsBeans;

        if (cidsBeans != null) {
            for (final CidsBean cidsBean : cidsBeans) {
                final Integer mauerId = (Integer)cidsBean.getProperty("mauer");
                if ((mauerId != null)) {
                    new SwingWorker<CidsBean, Void>() {

                            @Override
                            protected CidsBean doInBackground() throws Exception {
                                final MetaClass mc = CidsBean.getMetaClassFromTableName(
                                        "WUNDA_BLAU",
                                        "mauer",
                                        getConnectionContext());
                                final MetaObject mo = SessionManager.getProxy()
                                            .getMetaObject(mauerId, mc.getID(), "WUNDA_BLAU", getConnectionContext());
                                final CidsBean mauerBean = mo.getBean();
                                return mauerBean;
                            }

                            @Override
                            protected void done() {
                                try {
                                    final CidsBean mauerBean = get();
                                    addMauerPanel(cidsBean, mauerBean);
                                } catch (final Exception ex) {
                                    final String message = "Fehler beim Laden der Stützmauer. (mauerId: " + mauerId
                                                + ")";
                                    LOG.error(message, ex);
                                    ObjectRendererUtils.showExceptionWindowToUser(
                                        message,
                                        ex,
                                        TreppeStuetzmauernPanel.this);
                                }
                            }
                        }.execute();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean   DOCUMENT ME!
     * @param  mauerBean  DOCUMENT ME!
     */
    private void addMauerPanel(final CidsBean cidsBean, final CidsBean mauerBean) {
        jPanel1.remove(filler1);
        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = cidsBeans.size();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        new SwingWorker<JPanel, Void>() {

                @Override
                protected JPanel doInBackground() throws Exception {
                    final TreppeStuetzmauerPanel panel = new TreppeStuetzmauerPanel(editable);
                    panel.setMauerBean(mauerBean);

                    final Integer kostenGelaender = (Integer)mauerBean.getProperty("san_kosten_gelaender");
                    final Integer kostenGruendung = (Integer)mauerBean.getProperty("san_kosten_gruendung");
                    final Integer kostenverformung = (Integer)mauerBean.getProperty("san_kosten_verformung");
                    final Integer kostenGelaende = (Integer)mauerBean.getProperty("san_kosten_gelaende");
                    final Integer kostenAnsicht = (Integer)mauerBean.getProperty("san_kosten_ansicht");
                    final Integer kostenKopf = (Integer)mauerBean.getProperty("san_kosten_kopf");

                    double summe = 0;
                    summe += (kostenGelaender != null) ? kostenGelaender : 0;
                    summe += (kostenGruendung != null) ? kostenGruendung : 0;
                    summe += (kostenverformung != null) ? kostenverformung : 0;
                    summe += (kostenGelaende != null) ? kostenGelaende : 0;
                    summe += (kostenAnsicht != null) ? kostenAnsicht : 0;
                    summe += (kostenKopf != null) ? kostenKopf : 0;

                    final CidsBean zustandBean = CidsBean.createNewCidsBeanFromTableName(
                            "WUNDA_BLAU",
                            "TREPPE_ZUSTAND",
                            getConnectionContext());
                    zustandBean.setProperty("verkehrssicherheit", null);
                    zustandBean.setProperty("dauerhaftigkeit", null);
                    zustandBean.setProperty("standsicherheit", null);
                    zustandBean.setProperty("sanierungsmassnahmen", "siehe Mauer-Beschreibung");
                    zustandBean.setProperty("gesamt", mauerBean.getProperty("zustand_gesamt"));
                    zustandBean.setProperty("kosten", summe);

                    zustandBeanMap.put(cidsBean, zustandBean);

                    panel.setZustandBean(zustandBean);
                    panel.setCidsBean(cidsBean);
                    panel.setParent(TreppeStuetzmauernPanel.this);

                    return panel;
                }

                @Override
                protected void done() {
                    try {
                        final JPanel panel = get();

                        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.gridx = 0;
                        gridBagConstraints.gridy = cidsBeans.indexOf(cidsBean);
                        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                        gridBagConstraints.weightx = 1.0;
                        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
                        jPanel1.add(panel, gridBagConstraints);

                        jPanel1.repaint();

                        refreshOverview();
                    } catch (final Exception ex) {
                        final String message = "Fehler beim Hinzufügen der Stützmauer.";
                        LOG.error(message, ex);
                        ObjectRendererUtils.showExceptionWindowToUser(message, ex, TreppeStuetzmauernPanel.this);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshOverview() {
        final TreppeEditor treppeEditor = ((TreppeEditor)getParent().getParent().getParent().getParent());
        final TreppeEditor.ZustandOverview overview = treppeEditor.getOverview();
        overview.recalculateStuetzmauern();
        overview.recalculateGesamt();
        overview.refreshView();
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, CidsBean> getMauerBeans() {
        final Map<CidsBean, CidsBean> mauerBeans = new HashMap<>();
        for (final Component comp : jPanel1.getComponents()) {
            if (comp instanceof TreppeStuetzmauerPanel) {
                final TreppeStuetzmauerPanel mauerPanel = (TreppeStuetzmauerPanel)comp;
                mauerBeans.put(mauerPanel.getCidsBean(), mauerPanel.getMauerBean());
            }
        }
        return mauerBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeMauerPanel(final TreppeStuetzmauerPanel panel) {
        if (panel != null) {
            final CidsBean cidsBean = panel.getCidsBean();
            zustandBeanMap.remove(cidsBean);
            cidsBeans.remove(cidsBean);
            jPanel1.remove(panel);
            jPanel1.repaint();
            refreshOverview();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        final JScrollPane jScrollPane1 = new JScrollPane();
        jPanel1 = new JPanel();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        if (netbeansDesignDummy) {
            treppeStuetzmauerPanel1 = new TreppeStuetzmauerPanel();
        }
        final JLabel jLabel1 = new DroppedLabel();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setOpaque(false);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new GridBagLayout());

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        if (netbeansDesignDummy) {
            treppeStuetzmauerPanel1.setName("treppeStuetzmauerPanel1"); // NOI18N
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            jPanel1.add(treppeStuetzmauerPanel1, gridBagConstraints);
        }

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getViewport().setOpaque(false);

        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cismap/commons/gui/metasearch/mauer.png"))); // NOI18N
        Mnemonics.setLocalizedText(
            jLabel1,
            NbBundle.getMessage(TreppeStuetzmauernPanel.class, "TreppeStuetzmauernPanel.jLabel1.text"));              // NOI18N
        jLabel1.setBorder(BorderFactory.createEtchedBorder());
        jLabel1.setName("jLabel1");                                                                                   // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);
        jLabel1.setVisible(editable);
        try {
            new CidsBeanDropTarget(jLabel1);
        } catch (final Exception ex) {
            LOG.warn("error while init CidsBeanDropTarget", ex);
        }
    }                                                                                                                 // </editor-fold>//GEN-END:initComponents

    @Override
    public void dispose() {
        for (final Component comp : jPanel1.getComponents()) {
            if (comp instanceof TreppeStuetzmauerPanel) {
                final TreppeStuetzmauerPanel panel = (TreppeStuetzmauerPanel)comp;
                panel.dispose();
                jPanel1.remove(panel);
            }
        }
        zustandBeanMap.clear();
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DroppedLabel extends JLabel implements CidsBeanDropListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void beansDropped(final ArrayList<CidsBean> droppedBeans) {
            try {
                if (droppedBeans.size() > 1) {
                }
                final CidsBean droppedBean = droppedBeans.get(0);
                if (droppedBean != null) {
                    if (droppedBean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("mauer")) {
                        try {
                            final CidsBean treppeMauerBean = CidsBean.createNewCidsBeanFromTableName(
                                    "WUNDA_BLAU",
                                    "TREPPE_STUETZMAUER",
                                    getConnectionContext());

                            addMauerPanel(treppeMauerBean, droppedBean);
                            cidsBeans.add(treppeMauerBean);
                            treppeMauerBean.setProperty("mauer", droppedBean.getProperty("id"));
                        } catch (Exception ex) {
                        }
                    }
                }
            } catch (final Exception ex) {
                final String message = "Fehler beim Erzeugen der Stützmauer.";
                LOG.error(message, ex);
                ObjectRendererUtils.showExceptionWindowToUser(message, ex, this);
            }
        }
    }
}
