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

import org.apache.commons.lang.StringUtils;

import org.openide.util.Exceptions;

import java.awt.GridBagConstraints;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeStuetzmauernPanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private List<CidsBean> cidsBeans;
    private final boolean editable;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauerPanel treppeStuetzmauerPanel1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeStuetzmauernPanel object.
     */
    public TreppeStuetzmauernPanel() {
        this(true);
    }

    /**
     * Creates new form TreppeStuetzmauernPanel.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppeStuetzmauernPanel(final boolean editable) {
        this.editable = editable;
        initComponents();
        jLabel1.setVisible(editable);
        jScrollPane1.getViewport().setOpaque(false);

        try {
            new CidsBeanDropTarget(jLabel1);
        } catch (final Exception ex) {
        }
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
                final TreppeStuetzmauerPanel panel = new TreppeStuetzmauerPanel(editable);
                panel.setCidsBean(cidsBean);
                panel.setParent(this);
                addMauerPanel(cidsBean, null);
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
        final TreppeStuetzmauerPanel panel = new TreppeStuetzmauerPanel(editable);
        panel.setMauerBean(mauerBean);
        try {
            final CidsBean zustandBean = CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "TREPPE_ZUSTAND");
            zustandBean.setProperty("verkehrssicherheit", 2);
            zustandBean.setProperty("dauerhaftigkeit", 1);
            zustandBean.setProperty("standsicherheit", 2);
            zustandBean.setProperty("sanierungsmassnahmen", "");
            zustandBean.setProperty("kosten", 0.0d);
            panel.setZustandBean(zustandBean);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        panel.setCidsBean(cidsBean);
        panel.setParent(this);

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
    }

    /**
     * DOCUMENT ME!
     *
     * @param  panel  DOCUMENT ME!
     */
    public void removeMauerPanel(final TreppeStuetzmauerPanel panel) {
        if (panel != null) {
            final CidsBean cidsBean = panel.getCidsBean();
            cidsBeans.remove(cidsBean);
            panel.setCidsBean(null);
            panel.setParent(null);
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
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        treppeStuetzmauerPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauerPanel();
        jLabel1 = new DroppedLabel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(treppeStuetzmauerPanel1, gridBagConstraints);

        jScrollPane1.setViewportView(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cismap/commons/gui/metasearch/mauer.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                TreppeStuetzmauernPanel.class,
                "TreppeStuetzmauernPanel.jLabel1.text"));                                       // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 20;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jLabel1, gridBagConstraints);
    }                                                                                           // </editor-fold>//GEN-END:initComponents

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
                            final CidsBean cidsBean = CidsBean.createNewCidsBeanFromTableName(
                                    "WUNDA_BLAU",
                                    "TREPPE_STUETZMAUER");

                            addMauerPanel(cidsBean, droppedBean);
                            cidsBeans.add(cidsBean);
                        } catch (Exception ex) {
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
