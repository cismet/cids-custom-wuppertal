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
package de.cismet.cids.custom.objecteditors.wunda_blau.treppe;

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEditor;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeHandlaeufePanel extends javax.swing.JPanel implements Disposable, ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeHandlaeufePanel.class);

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
            treppeHandlaufPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.treppe.TreppeHandlaufPanel();
        }
        btnAddArt1 = new JButton();

        final FormListener formListener = new FormListener();

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
            treppeHandlaufPanel1.setName("treppeHandlaufPanel1"); // NOI18N
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            jPanel1.add(treppeHandlaufPanel1, gridBagConstraints);
        }

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
        jScrollPane1.getViewport().setOpaque(false);

        btnAddArt1.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddArt1.setBorderPainted(false);
        btnAddArt1.setContentAreaFilled(false);
        btnAddArt1.setName("btnAddArt1");                                                                      // NOI18N
        btnAddArt1.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        add(btnAddArt1, gridBagConstraints);
    }

    /**
     * Code for dispatching events from components to event handlers.
     *
     * @version  $Revision$, $Date$
     */
    private class FormListener implements ActionListener {

        /**
         * Creates a new FormListener object.
         */
        FormListener() {
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (evt.getSource() == btnAddArt1) {
                TreppeHandlaeufePanel.this.btnAddArt1ActionPerformed(evt);
            }
        }
    } // </editor-fold>//GEN-END:initComponents

    //~ Instance fields --------------------------------------------------------

    private final boolean netbeansDesignDummy;
    private List<CidsBean> cidsBeans;
    private final boolean editable;
    private final ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnAddArt1;
    Box.Filler filler1;
    JPanel jPanel1;
    de.cismet.cids.custom.objecteditors.wunda_blau.treppe.TreppeHandlaufPanel treppeHandlaufPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeHandlaeufePanel object.
     */
    public TreppeHandlaeufePanel() {
        this(ConnectionContext.createDeprecated());
    }

    /**
     * Creates a new TreppeHandlaeufePanel object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeHandlaeufePanel(final ConnectionContext connectionContext) {
        this(true, true, connectionContext);
    }

    /**
     * Creates a new TreppeHandlaeufePanel object.
     *
     * @param  editable           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppeHandlaeufePanel(final boolean editable, final ConnectionContext connectionContext) {
        this(editable, false, connectionContext);
    }

    /**
     * Creates new form TreppeHandlaeufePanel.
     *
     * @param  editable             DOCUMENT ME!
     * @param  netbeansDesignDummy  DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public TreppeHandlaeufePanel(final boolean editable,
            final boolean netbeansDesignDummy,
            final ConnectionContext connectionContext) {
        this.netbeansDesignDummy = netbeansDesignDummy;
        this.editable = editable;
        this.connectionContext = connectionContext;
        initComponents();
        btnAddArt1.setVisible(editable);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        jPanel1.removeAll();

        this.cidsBeans = cidsBeans;

        if (cidsBeans != null) {
            Collections.sort(cidsBeans, new TreppeEditor.TeilementComparator("nummer"));
            for (final CidsBean cidsBean : cidsBeans) {
                addHandlaufPanel(cidsBean);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void addHandlaufPanel(final CidsBean cidsBean) {
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
                    final TreppeHandlaufPanel panel = new TreppeHandlaufPanel(editable);
                    panel.setCidsBean(cidsBean);
                    panel.setParent(TreppeHandlaeufePanel.this);
                    return panel;
                }

                @Override
                protected void done() {
                    try {
                        final JPanel panel = get();

                        final GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.gridx = 0;
                        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                        gridBagConstraints.weightx = 1.0;
                        gridBagConstraints.gridy = cidsBeans.indexOf(cidsBean);
                        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
                        jPanel1.add(panel, gridBagConstraints);

                        jPanel1.repaint();
                    } catch (final Exception ex) {
                        final String message = "Fehler beim Hinzufügen des Handlaufs.";
                        LOG.error(message, ex);
                        ObjectRendererUtils.showExceptionWindowToUser(message, ex, TreppeHandlaeufePanel.this);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeHandlaufPanel(final TreppeHandlaufPanel panel) {
        if (panel != null) {
            cidsBeans.remove(panel.getCidsBean());
            jPanel1.remove(panel);
            jPanel1.repaint();
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
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddArt1ActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnAddArt1ActionPerformed
        try {
            final CidsBean cidsBean = CidsBean.createNewCidsBeanFromTableName(
                    "WUNDA_BLAU",
                    "TREPPE_HANDLAUF",
                    getConnectionContext());
            cidsBean.setProperty(
                "zustand",
                CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE_ZUSTAND", getConnectionContext()));
            addHandlaufPanel(cidsBean);
            cidsBeans.add(cidsBean);
        } catch (final Exception ex) {
            final String message = "Fehler beim Erzeugen des Handlaufs.";
            LOG.error(message, ex);
            ObjectRendererUtils.showExceptionWindowToUser(message, ex, this);
        }
    }                                                               //GEN-LAST:event_btnAddArt1ActionPerformed

    @Override
    public void dispose() {
        for (final Component comp : jPanel1.getComponents()) {
            if (comp instanceof TreppeHandlaufPanel) {
                final TreppeHandlaufPanel panel = (TreppeHandlaufPanel)comp;
                panel.dispose();
                jPanel1.remove(panel);
            }
        }
    }
}