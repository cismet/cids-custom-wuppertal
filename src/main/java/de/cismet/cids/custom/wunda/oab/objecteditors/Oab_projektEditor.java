/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.objecteditors;

import Sirius.navigator.ui.RequestsFullSizeComponent;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.wunda.oab.AbstractCidsBeanRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.converters.SqlDateToUtilDateConverter;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_projektEditor extends AbstractCidsBeanRenderer implements RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Instance fields --------------------------------------------------------

    private final ListSelectionListener condMeasSelL = new ConditionMeasureSelectionL();
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooserAlkis;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooserFinishedOn;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooserVerdis;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceComboContractor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblAlkis;
    private javax.swing.JLabel lblConditionMeasureTitle;
    private javax.swing.JLabel lblConditionsMeasures;
    private javax.swing.JLabel lblContractor;
    private javax.swing.JLabel lblDEM;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblFinishedOn;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblSewerNetworkModel;
    private javax.swing.JLabel lblVerdis;
    private javax.swing.JList lstConditionsMeasures;
    private de.cismet.cids.custom.wunda.oab.objecteditors.Oab_zustand_massnahmeEditor oab_zustand_massnahmeEditor;
    private javax.swing.JPanel pnlConditionAndMeasures;
    private javax.swing.JPanel pnlConditionMeasure;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlEmbeddedConditionMeasure;
    private javax.swing.JScrollPane scpConditionsMeasures;
    private javax.swing.JScrollPane scpDescription;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelConditionMeasure;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelConditionsAndMeasures;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelData;
    private javax.swing.JTextPane txpDescription;
    private javax.swing.JTextField txtDEM;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtSewerNetworkModel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form OABProjectEditor.
     */
    public Oab_projektEditor() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        bindingGroup.unbind();

        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());

            bindingGroup.bind();

            initConditionMeasuresList();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void initConditionMeasuresList() {
        final List<CidsBean> c = cidsBean.getBeanCollectionProperty("zustaende_massnahmen"); // NOI18N

        if ((c == null) || c.isEmpty()) {
            throw new IllegalStateException("no conditions or measures found: " + cidsBean); // NOI18N
        }

        Collections.sort(c, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) || !(o1.getProperty("name") instanceof String) || (o2 == null) // NOI18N
                                || !(o2.getProperty("name") instanceof String)) {                   // NOI18N
                        throw new IllegalStateException(
                            "bean without valid name [obj1="                                        // NOI18N
                                    + o1
                                    + "|obj2="                                                      // NOI18N
                                    + o2
                                    + "]");                                                         // NOI18N
                    }

                    return ((String)o1.getProperty("name")).compareTo( // NOI18N
                            (String)o2.getProperty("name"));           // NOI18N
                }
            });

        final DefaultListModel<CidsBean> dlm = new DefaultListModel<>();
        dlm.setSize(c.size());
        for (int i = 0; i < c.size(); ++i) {
            dlm.set(i, c.get(i));
        }

        lstConditionsMeasures.setModel(dlm);
        lstConditionsMeasures.setSelectedIndex(0);
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

        pnlConditionMeasure = new javax.swing.JPanel();
        semiRoundedPanelConditionMeasure = new de.cismet.tools.gui.SemiRoundedPanel();
        lblConditionMeasureTitle = new javax.swing.JLabel();
        pnlEmbeddedConditionMeasure = new javax.swing.JPanel();
        oab_zustand_massnahmeEditor = new de.cismet.cids.custom.wunda.oab.objecteditors.Oab_zustand_massnahmeEditor();
        pnlData = new javax.swing.JPanel();
        semiRoundedPanelData = new de.cismet.tools.gui.SemiRoundedPanel();
        lblData = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        scpDescription = new javax.swing.JScrollPane();
        txpDescription = new javax.swing.JTextPane();
        lblFinishedOn = new javax.swing.JLabel();
        defaultBindableDateChooserFinishedOn = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblContractor = new javax.swing.JLabel();
        defaultBindableReferenceComboContractor = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblDEM = new javax.swing.JLabel();
        txtDEM = new javax.swing.JTextField();
        lblAlkis = new javax.swing.JLabel();
        defaultBindableDateChooserAlkis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblVerdis = new javax.swing.JLabel();
        defaultBindableDateChooserVerdis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblSewerNetworkModel = new javax.swing.JLabel();
        txtSewerNetworkModel = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlConditionAndMeasures = new javax.swing.JPanel();
        semiRoundedPanelConditionsAndMeasures = new de.cismet.tools.gui.SemiRoundedPanel();
        lblConditionsMeasures = new javax.swing.JLabel();
        scpConditionsMeasures = new javax.swing.JScrollPane();
        lstConditionsMeasures = new javax.swing.JList();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlConditionMeasure.setOpaque(false);
        pnlConditionMeasure.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelConditionMeasure.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelConditionMeasure.setLayout(new java.awt.GridBagLayout());

        lblConditionMeasureTitle.setFont(new java.awt.Font("Lucida Grande", 0, 14));                          // NOI18N
        lblConditionMeasureTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblConditionMeasureTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblConditionMeasureTitle,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblConditionMeasureTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelConditionMeasure.add(lblConditionMeasureTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlConditionMeasure.add(semiRoundedPanelConditionMeasure, gridBagConstraints);

        pnlEmbeddedConditionMeasure.setBorder(javax.swing.BorderFactory.createLineBorder(
                new java.awt.Color(51, 51, 51)));
        pnlEmbeddedConditionMeasure.setOpaque(false);
        pnlEmbeddedConditionMeasure.setLayout(new java.awt.BorderLayout());
        pnlEmbeddedConditionMeasure.add(oab_zustand_massnahmeEditor, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlConditionMeasure.add(pnlEmbeddedConditionMeasure, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlConditionMeasure, gridBagConstraints);

        pnlData.setOpaque(false);
        pnlData.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelData.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelData.setLayout(new java.awt.GridBagLayout());

        lblData.setFont(new java.awt.Font("Lucida Grande", 0, 14));                          // NOI18N
        lblData.setForeground(new java.awt.Color(255, 255, 255));
        lblData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblData,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblData.text")); // NOI18N
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
            lblName,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblName, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(txtName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDescription,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblDescription, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung}"),
                txpDescription,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpDescription.setViewportView(txpDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(scpDescription, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFinishedOn,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblFinishedOn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblFinishedOn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.abschluss}"),
                defaultBindableDateChooserFinishedOn,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new SqlDateToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(defaultBindableDateChooserFinishedOn, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblContractor,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblContractor.text")); // NOI18N
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
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.auftragnehmer}"),
                defaultBindableReferenceComboContractor,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(defaultBindableReferenceComboContractor, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDEM,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblDEM.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblDEM, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_dgm}"),
                txtDEM,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(txtDEM, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAlkis,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblAlkis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblAlkis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_alkis}"),
                defaultBindableDateChooserAlkis,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new SqlDateToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(defaultBindableDateChooserAlkis, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVerdis,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblVerdis.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblVerdis, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand_verdis}"),
                defaultBindableDateChooserVerdis,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new SqlDateToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(defaultBindableDateChooserVerdis, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblSewerNetworkModel,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblSewerNetworkModel.text")); // NOI18N
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
                txtSewerNetworkModel,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(txtSewerNetworkModel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(jLabel1, gridBagConstraints);

        defaultBindableReferenceCombo1.setMaximumSize(new java.awt.Dimension(300, 300));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.berechnungsverfahren}"),
                defaultBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(defaultBindableReferenceCombo1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlData, gridBagConstraints);

        pnlConditionAndMeasures.setOpaque(false);
        pnlConditionAndMeasures.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelConditionsAndMeasures.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelConditionsAndMeasures.setLayout(new java.awt.GridBagLayout());

        lblConditionsMeasures.setFont(new java.awt.Font("Lucida Grande", 0, 14));                          // NOI18N
        lblConditionsMeasures.setForeground(new java.awt.Color(255, 255, 255));
        lblConditionsMeasures.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblConditionsMeasures,
            NbBundle.getMessage(Oab_projektEditor.class, "Oab_projektEditor.lblConditionsMeasures.text")); // NOI18N
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

        lstConditionsMeasures.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "Ist", "Prognose", "Sanierung 1", "Sanierung 2", "Sanierung 3" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        scpConditionsMeasures.setViewportView(lstConditionsMeasures);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlConditionAndMeasures.add(scpConditionsMeasures, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(pnlConditionAndMeasures, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        oab_zustand_massnahmeEditor.initWithConnectionContext(connectionContext);
        lstConditionsMeasures.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                condMeasSelL,
                lstConditionsMeasures));
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
    private final class ConditionMeasureSelectionL implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                final CidsBean bean = (CidsBean)lstConditionsMeasures.getSelectedValue();
                if (bean == null) {
                    throw new IllegalStateException("no condition or measure selected, this is illegal"); // NOI18N
                }

                oab_zustand_massnahmeEditor.setCidsBean(bean);
            }
        }
    }
}
