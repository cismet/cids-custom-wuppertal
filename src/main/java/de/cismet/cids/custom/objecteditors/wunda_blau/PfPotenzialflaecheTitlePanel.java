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

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheTitlePanel extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private final PfPotenzialflaecheEditor editor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blWait;
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnReport1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel txtTitle;
    private javax.swing.JLabel txtTitle1;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PfPotenzialflaecheTitlePanel object.
     *
     * @param  editor  DOCUMENT ME!
     */
    public PfPotenzialflaecheTitlePanel(final PfPotenzialflaecheEditor editor) {
        this.editor = editor;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  waiting  DOCUMENT ME!
     */
    public void setWaiting(final boolean waiting) {
        blWait.setVisible(waiting);
        blWait.setBusy(waiting);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTitle() {
        if ((editor != null) && (editor.getCidsBean() != null)) {
            if (editor.getCidsBean().getProperty("bezeichnung") == null) {
                return String.format(
                        "neue Potenzialfläche der Kampagne \"%s\"",
                        (String)editor.getCidsBean().getProperty("kampagne.bezeichnung"));
            } else {
                return String.format(
                        "%s: %s",
                        (String)editor.getCidsBean().getProperty("kampagne.bezeichnung"),
                        (String)editor.getCidsBean().getProperty("bezeichnung"));
            }
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void init() {
        initComponents();
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        btnReport.setEnabled(!editor.isEditable());
        txtTitle.setText(getTitle());
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        txtTitle = new javax.swing.JLabel();
        txtTitle1 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        btnReport = new javax.swing.JButton();
        blWait = new org.jdesktop.swingx.JXBusyLabel();
        btnReport1 = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(txtTitle, gridBagConstraints);

        txtTitle1.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle1.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(txtTitle1, gridBagConstraints);

        jToggleButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock.png")));      // NOI18N
        jToggleButton1.setBorderPainted(false);
        jToggleButton1.setContentAreaFilled(false);
        jToggleButton1.setFocusPainted(false);
        jToggleButton1.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_edit.png"))); // NOI18N
        jToggleButton1.setRolloverSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_go.png")));   // NOI18N
        jToggleButton1.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_open.png"))); // NOI18N
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        add(jToggleButton1, new java.awt.GridBagConstraints());
        jToggleButton1.setVisible(editor.isEditable());

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheTitlePanel.class,
                "PfPotenzialflaecheTitlePanel.btnReport.text"));                           // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheTitlePanel.class,
                "PfPotenzialflaecheTitlePanel.btnReport.toolTipText"));                    // NOI18N
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(btnReport, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        add(blWait, gridBagConstraints);

        btnReport1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/cacheLeeren.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheTitlePanel.class,
                "PfPotenzialflaecheTitlePanel.btnReport1.text"));                         // NOI18N
        btnReport1.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheTitlePanel.class,
                "PfPotenzialflaecheTitlePanel.btnReport1.toolTipText"));                  // NOI18N
        btnReport1.setBorderPainted(false);
        btnReport1.setContentAreaFilled(false);
        btnReport1.setFocusPainted(false);
        btnReport1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReport1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(btnReport1, gridBagConstraints);
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        editor.startDownload();
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        if (editor.isEditable()) {
            editor.toggleUsedInputs(jToggleButton1.isSelected());
        }
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReport1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReport1ActionPerformed
        editor.deleteDownloadCache();
    }                                                                              //GEN-LAST:event_btnReport1ActionPerformed
}
