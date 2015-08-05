/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.objectrenderer;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.event.EventListenerList;

import de.cismet.cids.custom.wunda.oab.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.wunda.oab.OabUtilities;
import de.cismet.cids.custom.wunda.oab.mapvis.Oab_ProjektMapVisualisationProvider;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.RetrievalServiceLayer;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_projektRenderer extends AbstractCidsBeanRenderer implements RequestsFullSizeComponent {

    //~ Instance fields --------------------------------------------------------

    // only to hold strong reference to listeners
    private EventListenerList refHolderList;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGotoCatchment;
    private javax.swing.Box.Filler hStaticFillCMList;
    private javax.swing.JLabel lblBelongsToCatchment;
    private javax.swing.JLabel lblCalculationModel;
    private javax.swing.JLabel lblCalculationModelValue;
    private javax.swing.JLabel lblConditionsMeasures;
    private javax.swing.JLabel lblContractor;
    private javax.swing.JLabel lblContractorValue;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblFinishedOn;
    private javax.swing.JLabel lblFinishedOnValue;
    private javax.swing.JLabel lblMapTitle;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameValue;
    private javax.swing.JLabel lblSewerNetworkModel;
    private javax.swing.JLabel lblSewerNetworkModelValue;
    private javax.swing.JLabel lblStateAlkis;
    private javax.swing.JLabel lblStateAlkisValue;
    private javax.swing.JLabel lblStateDEM;
    private javax.swing.JLabel lblStateDEMValue;
    private javax.swing.JLabel lblStateVerdis;
    private javax.swing.JLabel lblStateVerdisValue;
    private de.cismet.cismap.commons.gui.MappingComponent map;
    private javax.swing.JPanel pnlCMList;
    private javax.swing.JPanel pnlConditionAndMeasures;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JScrollPane scpDescription;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelConditionsAndMeasures;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelData;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelMap;
    private javax.swing.JTextArea txaDescription;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form OABProjectEditor.
     */
    public Oab_projektRenderer() {
        initComponents();

        txaDescription.setBackground(new Color(0, 0, 0, 0));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        cidsBean = OabUtilities.getBean(cidsBean, "gewaessereinzugsgebiet");

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    bindingGroup.unbind();

                    if (cidsBean != null) {
                        refHolderList = new EventListenerList();

                        OabUtilities.initGotoBeanHyperlinkList(
                            cidsBean,
                            "zustaende_massnahmen",
                            pnlCMList,
                            refHolderList);                      // NOI18N
                        OabUtilities.initPreviewMap(
                            cidsBean,
                            "umschreibende_geometrie.geo_field", // NOI18N
                            map,
                            lblMapTitle,
                            new Oab_ProjektMapVisualisationProvider().buildAction(cidsBean),
                            (RetrievalServiceLayer[])null);

                        final CidsBean catchmentBean = (CidsBean)cidsBean.getProperty("gewaessereinzugsgebiet"); // NOI18N
                        btnGotoCatchment.setText((String)catchmentBean.getProperty("name"));                     // NOI18N
                        OabUtilities.toGotoBeanHyperlinkButton(btnGotoCatchment, catchmentBean, refHolderList);

                        bindingGroup.bind();
                    }
                }
            };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
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

        pnlMap = new javax.swing.JPanel();
        semiRoundedPanelMap = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMapTitle = new javax.swing.JLabel();
        map = new de.cismet.cismap.commons.gui.MappingComponent();
        pnlData = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        semiRoundedPanelData = new de.cismet.tools.gui.SemiRoundedPanel();
        lblData = new javax.swing.JLabel();
        lblNameValue = new javax.swing.JLabel();
        scpDescription = new javax.swing.JScrollPane();
        txaDescription = new javax.swing.JTextArea();
        lblFinishedOn = new javax.swing.JLabel();
        lblFinishedOnValue = new javax.swing.JLabel();
        lblContractor = new javax.swing.JLabel();
        lblContractorValue = new javax.swing.JLabel();
        lblBelongsToCatchment = new javax.swing.JLabel();
        btnGotoCatchment = new javax.swing.JButton();
        lblStateDEM = new javax.swing.JLabel();
        lblStateDEMValue = new javax.swing.JLabel();
        lblStateAlkis = new javax.swing.JLabel();
        lblStateAlkisValue = new javax.swing.JLabel();
        lblStateVerdis = new javax.swing.JLabel();
        lblStateVerdisValue = new javax.swing.JLabel();
        lblSewerNetworkModel = new javax.swing.JLabel();
        lblSewerNetworkModelValue = new javax.swing.JLabel();
        lblCalculationModel = new javax.swing.JLabel();
        lblCalculationModelValue = new javax.swing.JLabel();
        pnlConditionAndMeasures = new javax.swing.JPanel();
        semiRoundedPanelConditionsAndMeasures = new de.cismet.tools.gui.SemiRoundedPanel();
        lblConditionsMeasures = new javax.swing.JLabel();
        pnlCMList = new javax.swing.JPanel();
        hStaticFillCMList = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 32767));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlMap.setOpaque(false);
        pnlMap.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelMap.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelMap.setLayout(new java.awt.GridBagLayout());

        lblMapTitle.setFont(new java.awt.Font("Lucida Grande", 0, 14));                              // NOI18N
        lblMapTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblMapTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMapTitle,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblMapTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelMap.add(lblMapTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlMap.add(semiRoundedPanelMap, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMap.add(map, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlMap, gridBagConstraints);

        pnlData.setOpaque(false);
        pnlData.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblName,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblDescription, gridBagConstraints);

        semiRoundedPanelData.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelData.setLayout(new java.awt.GridBagLayout());

        lblData.setFont(new java.awt.Font("Lucida Grande", 0, 14));                              // NOI18N
        lblData.setForeground(new java.awt.Color(255, 255, 255));
        lblData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblData,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblData.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelData.add(lblData, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlData.add(semiRoundedPanelData, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                lblNameValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblNameValue, gridBagConstraints);

        scpDescription.setBorder(null);
        scpDescription.setOpaque(false);

        txaDescription.setEditable(false);
        txaDescription.setColumns(20);
        txaDescription.setLineWrap(true);
        txaDescription.setRows(5);
        txaDescription.setWrapStyleWord(true);
        txaDescription.setBorder(null);
        txaDescription.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txaDescription.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung}"),
                txaDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpDescription.setViewportView(txaDescription);

        scpDescription.getViewport().setOpaque(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(scpDescription, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFinishedOn,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblFinishedOn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblFinishedOn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.abschluss}"),
                lblFinishedOnValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new OabUtilities.DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblFinishedOnValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblContractor,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblContractor.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblContractor, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.auftragnehmer.name}"),
                lblContractorValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblContractorValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBelongsToCatchment,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblBelongsToCatchment.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblBelongsToCatchment, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnGotoCatchment,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.btnGotoCatchment.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(btnGotoCatchment, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStateDEM,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblStateDEM.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateDEM, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_dgm}"),
                lblStateDEMValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateDEMValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStateAlkis,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblStateAlkis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateAlkis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_alkis}"),
                lblStateAlkisValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new OabUtilities.DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateAlkisValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStateVerdis,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblStateVerdis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateVerdis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_verdis}"),
                lblStateVerdisValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new OabUtilities.DateToStringConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblStateVerdisValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblSewerNetworkModel,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblSewerNetworkModel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblSewerNetworkModel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kanalnetzmodell}"),
                lblSewerNetworkModelValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblSewerNetworkModelValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblCalculationModel,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblCalculationModel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblCalculationModel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.berechnungsverfahren.name}"),
                lblCalculationModelValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblCalculationModelValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlData, gridBagConstraints);

        pnlConditionAndMeasures.setOpaque(false);
        pnlConditionAndMeasures.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelConditionsAndMeasures.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelConditionsAndMeasures.setLayout(new java.awt.GridBagLayout());

        lblConditionsMeasures.setFont(new java.awt.Font("Lucida Grande", 0, 14));                              // NOI18N
        lblConditionsMeasures.setForeground(new java.awt.Color(255, 255, 255));
        lblConditionsMeasures.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblConditionsMeasures,
            NbBundle.getMessage(Oab_projektRenderer.class, "Oab_projektRenderer.lblConditionsMeasures.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelConditionsAndMeasures.add(lblConditionsMeasures, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlConditionAndMeasures.add(semiRoundedPanelConditionsAndMeasures, gridBagConstraints);

        pnlCMList.setOpaque(false);
        pnlCMList.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlConditionAndMeasures.add(pnlCMList, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlConditionAndMeasures.add(hStaticFillCMList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(pnlConditionAndMeasures, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
