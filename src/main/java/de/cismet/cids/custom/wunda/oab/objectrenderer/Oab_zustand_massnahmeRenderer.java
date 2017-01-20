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

import java.util.Comparator;

import javax.swing.event.EventListenerList;

import de.cismet.cids.custom.wunda.oab.AbstractCidsBeanRenderer;
import de.cismet.cids.custom.wunda.oab.OabUtilities;
import de.cismet.cids.custom.wunda.oab.mapvis.Oab_Zustand_MassnahmeMapVisualisationProvider;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_zustand_massnahmeRenderer extends AbstractCidsBeanRenderer implements RequestsFullSizeComponent {

    //~ Instance fields --------------------------------------------------------

    // End of variables declaration

    // only to hold strong reference to listeners
    private EventListenerList refHolderList;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGotoProject;
    private javax.swing.Box.Filler hStaticFillCalcList;
    private javax.swing.JLabel lblBelongsToProject;
    private javax.swing.JLabel lblCalculations;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblMapTitle;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameValue;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblTypeValue;
    private de.cismet.cismap.commons.gui.MappingComponent map;
    private javax.swing.JPanel pnlCalcList;
    private javax.swing.JPanel pnlCalculations;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JScrollPane scpDescription;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelCalculations;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelData;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelMap;
    private javax.swing.JTextArea txaDescription;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    // only to hold strong reference to listeners

    /**
     * Creates new form OABProjectEditor.
     */
    public Oab_zustand_massnahmeRenderer() {
        initComponents();

        txaDescription.setBackground(new Color(0, 0, 0, 0));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        cidsBean = OabUtilities.getBean(cidsBean, "projekt"); // NOI18N

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    bindingGroup.unbind();

                    if (cidsBean != null) {
                        refHolderList = new EventListenerList();

                        OabUtilities.initGotoBeanHyperlinkList(
                            cidsBean,
                            "berechnungen",  // NOI18N
                            "jaehrlichkeit", // NOI18N
                            "T",             // NOI18N
                            null,
                            new Comparator<CidsBean>() {

                                @Override
                                public int compare(final CidsBean o1, final CidsBean o2) {
                                    if ((o1 == null) || !(o1.getProperty("jaehrlichkeit") instanceof Integer) // NOI18N
                                                || (o2 == null)
                                                || !(o2.getProperty("jaehrlichkeit") instanceof Integer)) {   // NOI18N
                                        throw new IllegalStateException(
                                            "bean without valid jaehrlichkeit [obj1="                         // NOI18N
                                                    + o1
                                                    + "|obj2="                                                // NOI18N
                                                    + o2
                                                    + "]");                                                   // NOI18N
                                    }

                                    return ((Integer)o1.getProperty("jaehrlichkeit")).compareTo( // NOI18N
                                            (Integer)o2.getProperty("jaehrlichkeit"));           // NOI18N
                                }
                            },
                            pnlCalcList,
                            refHolderList);

                        OabUtilities.initPreviewMap(
                            cidsBean,
                            "umschreibende_geometrie.geo_field",                             // NOI18N
                            map,
                            lblMapTitle,
                            new Oab_Zustand_MassnahmeMapVisualisationProvider().buildAction(cidsBean),
                            OabUtilities.createWMSLayer(
                                (String)cidsBean.getProperty("bruchkanten_cap"),             // NOI18N
                                (String)cidsBean.getProperty("bruchkanten_layer_name")));    // NOI18N

                        final CidsBean projectBean = (CidsBean)cidsBean.getProperty("projekt"); // NOI18N
                        btnGotoProject.setText((String)projectBean.getProperty("name"));        // NOI18N
                        OabUtilities.toGotoBeanHyperlinkButton(btnGotoProject, projectBean, refHolderList);

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
        lblType = new javax.swing.JLabel();
        lblBelongsToProject = new javax.swing.JLabel();
        btnGotoProject = new javax.swing.JButton();
        lblNameValue = new javax.swing.JLabel();
        scpDescription = new javax.swing.JScrollPane();
        txaDescription = new javax.swing.JTextArea();
        lblTypeValue = new javax.swing.JLabel();
        pnlCalculations = new javax.swing.JPanel();
        semiRoundedPanelCalculations = new de.cismet.tools.gui.SemiRoundedPanel();
        lblCalculations = new javax.swing.JLabel();
        pnlCalcList = new javax.swing.JPanel();
        hStaticFillCalcList = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 32767));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlMap.setOpaque(false);
        pnlMap.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelMap.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelMap.setLayout(new java.awt.GridBagLayout());

        lblMapTitle.setFont(new java.awt.Font("Lucida Grande", 0, 14));                                                  // NOI18N
        lblMapTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblMapTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMapTitle,
            NbBundle.getMessage(Oab_zustand_massnahmeRenderer.class, "Oab_zustand_massnahmeRenderer.lblMapTitle.text")); // NOI18N
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
            NbBundle.getMessage(Oab_zustand_massnahmeRenderer.class, "Oab_zustand_massnahmeRenderer.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            NbBundle.getMessage(
                Oab_zustand_massnahmeRenderer.class,
                "Oab_zustand_massnahmeRenderer.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblDescription, gridBagConstraints);

        semiRoundedPanelData.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelData.setLayout(new java.awt.GridBagLayout());

        lblData.setFont(new java.awt.Font("Lucida Grande", 0, 14));                                                  // NOI18N
        lblData.setForeground(new java.awt.Color(255, 255, 255));
        lblData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblData,
            NbBundle.getMessage(Oab_zustand_massnahmeRenderer.class, "Oab_zustand_massnahmeRenderer.lblData.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(
            lblType,
            NbBundle.getMessage(Oab_zustand_massnahmeRenderer.class, "Oab_zustand_massnahmeRenderer.lblType.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblType, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBelongsToProject,
            NbBundle.getMessage(
                Oab_zustand_massnahmeRenderer.class,
                "Oab_zustand_massnahmeRenderer.lblBelongsToProject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblBelongsToProject, gridBagConstraints);

        btnGotoProject.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            btnGotoProject,
            NbBundle.getMessage(
                Oab_zustand_massnahmeRenderer.class,
                "Oab_zustand_massnahmeRenderer.btnGotoProject.text")); // NOI18N
        btnGotoProject.setBorderPainted(false);
        btnGotoProject.setContentAreaFilled(false);
        btnGotoProject.setFocusPainted(false);
        btnGotoProject.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGotoProject.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(btnGotoProject, gridBagConstraints);

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(scpDescription, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.typ.name}"),
                lblTypeValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 28, 5, 5);
        pnlData.add(lblTypeValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlData, gridBagConstraints);

        pnlCalculations.setOpaque(false);
        pnlCalculations.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelCalculations.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelCalculations.setLayout(new java.awt.GridBagLayout());

        lblCalculations.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblCalculations.setForeground(new java.awt.Color(255, 255, 255));
        lblCalculations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblCalculations,
            NbBundle.getMessage(
                Oab_zustand_massnahmeRenderer.class,
                "Oab_zustand_massnahmeRenderer.lblCalculations.text"));     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelCalculations.add(lblCalculations, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCalculations.add(semiRoundedPanelCalculations, gridBagConstraints);

        pnlCalcList.setOpaque(false);
        pnlCalcList.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCalculations.add(pnlCalcList, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        pnlCalculations.add(hStaticFillCalcList, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(pnlCalculations, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents
}
