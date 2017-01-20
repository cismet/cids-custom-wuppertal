/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import org.openide.util.NbBundle;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   mscholl
 * @version  $Revision$, $Date$
 */
public class OabMapVisualisationDialog extends javax.swing.JPanel {

    //~ Instance fields --------------------------------------------------------

    private CidsBean featureBean;
    private String tinCapabilitiesUrl;
    private String tinLayername;
    private String beCapabilitiesUrl;
    private String beLayername;
    private String maxWaterCapabilitiesUrl;
    private String maxWaterLayername;
    private String tsWaterCapabilitiesUrl;
    private String tsWaterLayername;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkBE;
    private javax.swing.JCheckBox chkFeature;
    private javax.swing.JCheckBox chkMaxWater;
    private javax.swing.JCheckBox chkTSWater;
    private javax.swing.JCheckBox chkTin;
    private javax.swing.Box.Filler hFill;
    private javax.swing.JLabel lblWhichVis;
    private javax.swing.Box.Filler vFill;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form TinBKDialog.
     */
    public OabMapVisualisationDialog() {
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAddFeature() {
        return chkFeature.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAddTin() {
        return chkTin.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAddBE() {
        return chkBE.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAddMaxWater() {
        return chkMaxWater.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAddTSWater() {
        return chkTSWater.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void setTinVisible(final boolean enable) {
        chkTin.setVisible(enable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void setBeVisible(final boolean enable) {
        chkBE.setVisible(enable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void setMaxWaterVisible(final boolean enable) {
        chkMaxWater.setVisible(enable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  enable  DOCUMENT ME!
     */
    public void setTSWaterVisible(final boolean enable) {
        chkTSWater.setVisible(enable);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTinCapabilitiesUrl() {
        return tinCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tinCapabilitiesUrl  DOCUMENT ME!
     */
    public void setTinCapabilitiesUrl(final String tinCapabilitiesUrl) {
        this.tinCapabilitiesUrl = tinCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBeCapabilitiesUrl() {
        return beCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beCapabilitiesUrl  DOCUMENT ME!
     */
    public void setBeCapabilitiesUrl(final String beCapabilitiesUrl) {
        this.beCapabilitiesUrl = beCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMaxWaterCapabilitiesUrl() {
        return maxWaterCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxWaterCapabilitiesUrl  DOCUMENT ME!
     */
    public void setMaxWaterCapabilitiesUrl(final String maxWaterCapabilitiesUrl) {
        this.maxWaterCapabilitiesUrl = maxWaterCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsWaterCapabilitiesUrl() {
        return tsWaterCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsWaterCapabilitiesUrl  DOCUMENT ME!
     */
    public void setTsWaterCapabilitiesUrl(final String tsWaterCapabilitiesUrl) {
        this.tsWaterCapabilitiesUrl = tsWaterCapabilitiesUrl;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTinLayername() {
        return tinLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tinLayername  DOCUMENT ME!
     */
    public void setTinLayername(final String tinLayername) {
        this.tinLayername = tinLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBeLayername() {
        return beLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beLayername  DOCUMENT ME!
     */
    public void setBeLayername(final String beLayername) {
        this.beLayername = beLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMaxWaterLayername() {
        return maxWaterLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  maxWaterLayername  DOCUMENT ME!
     */
    public void setMaxWaterLayername(final String maxWaterLayername) {
        this.maxWaterLayername = maxWaterLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getTsWaterLayername() {
        return tsWaterLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tsWaterLayername  DOCUMENT ME!
     */
    public void setTsWaterLayername(final String tsWaterLayername) {
        this.tsWaterLayername = tsWaterLayername;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getFeatureBean() {
        return featureBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  featureBean  DOCUMENT ME!
     */
    public void setFeatureBean(final CidsBean featureBean) {
        this.featureBean = featureBean;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        chkFeature = new javax.swing.JCheckBox();
        lblWhichVis = new javax.swing.JLabel();
        chkTin = new javax.swing.JCheckBox();
        chkBE = new javax.swing.JCheckBox();
        hFill = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        vFill = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        chkMaxWater = new javax.swing.JCheckBox();
        chkTSWater = new javax.swing.JCheckBox();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chkFeature,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.chkFeature.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkFeature, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblWhichVis,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.lblWhichVis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(lblWhichVis, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkTin,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.chkTin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkTin, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkBE,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.chkBE.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkBE, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(hFill, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(vFill, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkMaxWater,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.chkMaxWater.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkMaxWater, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkTSWater,
            NbBundle.getMessage(OabMapVisualisationDialog.class, "OabMapVisualisationDialog.chkTSWater.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(chkTSWater, gridBagConstraints);
    }                                                                                                           // </editor-fold>//GEN-END:initComponents
}
