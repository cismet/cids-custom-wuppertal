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

import org.apache.log4j.Logger;

import java.awt.Component;
import java.awt.GridBagConstraints;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.Disposable;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeLaeufePanel extends javax.swing.JPanel implements Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeLaeufePanel.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean netbeansDesignDummy;
    private List<CidsBean> cidsBeans;
    private final boolean editable;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArt1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaufPanel treppeLaufPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeLaeufePanel object.
     */
    public TreppeLaeufePanel() {
        this(true, true);
    }

    /**
     * Creates a new TreppeLaeufePanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppeLaeufePanel(final boolean editable) {
        this(editable, false);
    }

    /**
     * Creates a new TreppeLaeufePanel object.
     *
     * @param  editable             DOCUMENT ME!
     * @param  netbeansDesignDummy  DOCUMENT ME!
     */
    public TreppeLaeufePanel(final boolean editable, final boolean netbeansDesignDummy) {
        this.netbeansDesignDummy = netbeansDesignDummy;
        this.editable = editable;
        initComponents();
        btnAddArt1.setVisible(editable);
        jScrollPane1.getViewport().setOpaque(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final List<CidsBean> cidsBeans) {
        jPanel1.removeAll();

        this.cidsBeans = cidsBeans;

        if (cidsBeans != null) {
            for (final CidsBean cidsBean : cidsBeans) {
                addLaufPanel(cidsBean);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    private void addLaufPanel(final CidsBean cidsBean) {
        jPanel1.remove(filler1);
        new SwingWorker<JPanel, Void>() {

                @Override
                protected JPanel doInBackground() throws Exception {
                    final TreppeLaufPanel panel = new TreppeLaufPanel(editable);
                    panel.setCidsBean(cidsBean);
                    panel.setParent(TreppeLaeufePanel.this);
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
                        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
                        jPanel1.add(panel, gridBagConstraints);

                        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
                        gridBagConstraints.weighty = 1.0;
                        jPanel1.add(filler1, gridBagConstraints);

                        jPanel1.repaint();
                    } catch (final Exception ex) {
                        LOG.error("error while adding panel", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeLaufPanel(final TreppeLaufPanel panel) {
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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        if (netbeansDesignDummy) {
            treppeLaufPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaufPanel();
        }
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        btnAddArt1 = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());
        if (netbeansDesignDummy) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
            jPanel1.add(treppeLaufPanel1, gridBagConstraints);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        btnAddArt1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddArt1.setBorderPainted(false);
        btnAddArt1.setContentAreaFilled(false);
        btnAddArt1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddArt1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(btnAddArt1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddArt1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddArt1ActionPerformed
        try {
            final CidsBean cidsBean = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE_TREPPENLAUF");
            cidsBean.setProperty("zustand", CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE_ZUSTAND"));
            addLaufPanel(cidsBean);
            cidsBeans.add(cidsBean);
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                              //GEN-LAST:event_btnAddArt1ActionPerformed

    @Override
    public void dispose() {
        for (final Component comp : jPanel1.getComponents()) {
            if (comp instanceof TreppeLaufPanel) {
                ((TreppeLaufPanel)comp).dispose();
            }
        }
    }
}
