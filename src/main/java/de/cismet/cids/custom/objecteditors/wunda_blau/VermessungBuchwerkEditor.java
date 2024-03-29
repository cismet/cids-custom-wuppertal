/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.VermessungPictureFinderClientUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungBuchwerkEditor extends AbstractVermessungEditor {

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbFormat;
    private javax.swing.JComboBox cmbGemarkung;
    private javax.swing.JComboBox cmbGeometrie;
    private javax.swing.JComboBox cmbGeometrieStatus;
    private javax.swing.Box.Filler gluGeneralInformationGap;
    private javax.swing.JLabel lblFormat;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JLabel lblGeometrie;
    private javax.swing.JLabel lblGeometrieStatus;
    private javax.swing.JPanel pnlInformation;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungBuchwerkEditor object.
     */
    public VermessungBuchwerkEditor() {
        this(false);
    }

    /**
     * Creates new form VermessungBuchwerkEditor.
     *
     * @param  readOnly  DOCUMENT ME!
     */
    public VermessungBuchwerkEditor(final boolean readOnly) {
        super(readOnly);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        initComponents();

        super.initWithConnectionContext(connectionContext);

        if (isReadOnly()) {
            lblGemarkung.setVisible(false);
            cmbGemarkung.setVisible(false);
            cmbFormat.setEditable(false);
            cmbFormat.setEnabled(false);
            cmbGeometrieStatus.setEditable(false);
            cmbGeometrieStatus.setEnabled(false);
            lblGeometrie.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pnlInformation = new javax.swing.JPanel();
        cmbFormat = new DefaultBindableReferenceCombo();
        if (!isReadOnly()) {
            cmbGeometrie = new DefaultCismapGeometryComboBoxEditor();
        }
        cmbGeometrieStatus = new DefaultBindableReferenceCombo();
        lblGeometrieStatus = new javax.swing.JLabel();
        gluGeneralInformationGap = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblGemarkung = new javax.swing.JLabel();
        lblFormat = new javax.swing.JLabel();
        cmbGemarkung = new DefaultBindableReferenceCombo();
        lblGeometrie = new javax.swing.JLabel();

        pnlInformation.setOpaque(false);
        pnlInformation.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.format}"),
                cmbFormat,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlInformation.add(cmbFormat, gridBagConstraints);

        if (!isReadOnly()) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cmbGeometrie,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cmbGeometrie).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (!isReadOnly()) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 0.5;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
            pnlInformation.add(cmbGeometrie, gridBagConstraints);
        }

        cmbGeometrieStatus.setRenderer(new GeometrieStatusRenderer(cmbGeometrieStatus.getRenderer()));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie_status}"),
                cmbGeometrieStatus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cmbGeometrieStatus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmbGeometrieStatusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 7);
        pnlInformation.add(cmbGeometrieStatus, gridBagConstraints);

        lblGeometrieStatus.setText(org.openide.util.NbBundle.getMessage(
                VermessungBuchwerkEditor.class,
                "VermessungBuchwerkEditor.lblGeometrieStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInformation.add(lblGeometrieStatus, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlInformation.add(gluGeneralInformationGap, gridBagConstraints);

        lblGemarkung.setLabelFor(cmbGemarkung);
        lblGemarkung.setText(org.openide.util.NbBundle.getMessage(
                VermessungBuchwerkEditor.class,
                "VermessungBuchwerkEditor.lblGemarkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInformation.add(lblGemarkung, gridBagConstraints);

        lblFormat.setText(org.openide.util.NbBundle.getMessage(
                VermessungBuchwerkEditor.class,
                "VermessungBuchwerkEditor.lblFormat.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInformation.add(lblFormat, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gemarkung}"),
                cmbGemarkung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        pnlInformation.add(cmbGemarkung, gridBagConstraints);

        lblGeometrie.setText(org.openide.util.NbBundle.getMessage(
                VermessungBuchwerkEditor.class,
                "VermessungBuchwerkEditor.lblGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlInformation.add(lblGeometrie, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmbGeometrieStatusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmbGeometrieStatusActionPerformed
        if (cmbGeometrieStatus.getSelectedItem() instanceof CidsBean) {
            final CidsBean geometrieStatus = (CidsBean)cmbGeometrieStatus.getSelectedItem();

            if (geometrieStatus.getProperty("id") instanceof Integer) {
                cmbGeometrieStatus.setBackground(COLORS_GEOMETRIE_STATUS.get(
                        (Integer)geometrieStatus.getProperty("id")));
            }
        }
    } //GEN-LAST:event_cmbGeometrieStatusActionPerformed

    @Override
    protected String getVermessungName() {
        return "Buchwerk";
    }
    @Override
    protected JPanel getInformationPanel() {
        return pnlInformation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    protected String getDocumentFilename() {
        final CidsBean cidsBean = getCidsBean();
        final Integer gemarkung = (Integer)cidsBean.getProperty("gemarkung.id");
        final String schluessel = (String)cidsBean.getProperty("schluessel");
        final Integer steuerbezirk = (Integer)cidsBean.getProperty("steuerbezirk");
        final String bezeichner = (String)cidsBean.getProperty("bezeichner");
        final boolean historisch = Boolean.TRUE.equals(cidsBean.getProperty("historisch"));
        return VermessungPictureFinderClientUtils.getBuchwerkPictureFilename(
                schluessel,
                gemarkung,
                steuerbezirk,
                bezeichner,
                historisch);
    }

    @Override
    protected String findPicture() {
        final CidsBean cidsBean = getCidsBean();
        final Integer gemarkung = (Integer)cidsBean.getProperty("gemarkung.id");
        final String schluessel = (String)cidsBean.getProperty("schluessel");
        final Integer steuerbezirk = (Integer)cidsBean.getProperty("steuerbezirk");
        final String bezeichner = (String)cidsBean.getProperty("bezeichner");
        final boolean historisch = Boolean.TRUE.equals(cidsBean.getProperty("historisch"));

        return VermessungPictureFinderClientUtils.findBuchwerkPicture(
                schluessel,
                gemarkung,
                steuerbezirk,
                bezeichner,
                historisch);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();

        super.setCidsBean(cidsBean);
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();

            if ((cidsBean.getProperty("geometrie_status") instanceof CidsBean)
                        && (cidsBean.getProperty("geometrie_status.id") instanceof Integer)) {
                cmbGeometrieStatus.setBackground(COLORS_GEOMETRIE_STATUS.get(
                        (Integer)cidsBean.getProperty("geometrie_status.id")));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    protected String generateTitle() {
        final CidsBean cidsBean = getCidsBean();
        final String schluessel = (String)cidsBean.getProperty("schluessel");
        final Integer steuerbezirk = (Integer)cidsBean.getProperty("steuerbezirk");
        final String bezeichner = (String)cidsBean.getProperty("bezeichner");
        final String gemarkung = (String)cidsBean.getProperty("gemarkung.name");

        return new StringBuilder().append("Schlüssel ")
                    .append(((schluessel != null) && (schluessel.trim().length() > 0)) ? schluessel : "unbekannt")
                    .append(" - ")
                    .append("Gemarkung ")
                    .append(((gemarkung != null) && (gemarkung.trim().length() > 0)) ? gemarkung : "unbekannt")
                    .append(" - ")
                    .append("Steuerbezirk ")
                    .append((steuerbezirk != null) ? steuerbezirk : "unbekannt")
                    .append(" - ")
                    .append("Bezeichner ")
                    .append(((bezeichner != null) && (bezeichner.trim().length() > 0)) ? bezeichner : "unbekannt")
                    .toString();
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        super.dispose();
        bindingGroup.unbind();
        if (!isReadOnly()) {
            ((DefaultCismapGeometryComboBoxEditor)cmbGeometrie).dispose();
        }
    }
}
