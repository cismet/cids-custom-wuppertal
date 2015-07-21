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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.wunda.oab.AbstractCidsBeanRenderer;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_zustand_massnahmeEditor extends AbstractCidsBeanRenderer implements RequestsFullSizeComponent {

    //~ Instance fields --------------------------------------------------------

    private final ActionListener editTinL;
    private final ActionListener editBeL;
    private final ListSelectionListener calcSelL;
    private final ItemListener importChkL;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditBe;
    private javax.swing.JButton btnEditTin;
    private javax.swing.JCheckBox chkImportFinished;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private javax.swing.Box.Filler hFillImports;
    private javax.swing.JLabel lblBelongsToProject;
    private javax.swing.JLabel lblBelongsToProjectValue;
    private javax.swing.JLabel lblCalculationDetailTitle;
    private javax.swing.JLabel lblCalculations;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblLayerName;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblWmsCapUrl;
    private javax.swing.JList lstCalculations;
    private de.cismet.cids.custom.wunda.oab.objecteditors.Oab_berechnungEditor oab_berechnungEditor;
    private javax.swing.JPanel pnlCalculationDetail;
    private javax.swing.JPanel pnlCalculations;
    private javax.swing.JPanel pnlData;
    private javax.swing.JPanel pnlEditDialog;
    private javax.swing.JPanel pnlEmbeddedCalculation;
    private javax.swing.JPanel pnlImports;
    private javax.swing.JScrollPane scpCalculations;
    private javax.swing.JScrollPane scpDescription;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelCalculationDetail;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelCalculations;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelData;
    private javax.swing.JSeparator sepImports;
    private javax.swing.JTextPane txpDescription;
    private javax.swing.JTextField txtLayerName;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtWmsCapUrl;
    private javax.swing.Box.Filler vFillEdit;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form OABProjectEditor.
     */
    public Oab_zustand_massnahmeEditor() {
        initComponents();

        editTinL = new EditWMSPropertiesListener(
                "tin", // NOI18N
                NbBundle.getMessage(
                    Oab_zustand_massnahmeEditor.class,
                    "Oab_Zustand_MassnahmeEditor.<init>.editTinL.title")); // NOI18N
        editBeL = new EditWMSPropertiesListener(
                "bruchkanten", // NOI18N
                NbBundle.getMessage(
                    Oab_zustand_massnahmeEditor.class,
                    "Oab_Zustand_MassnahmeEditor.<init>.editBeL.title")); // NOI18N
        calcSelL = new CalculationSelectionL();
        importChkL = new ImportCheckL();

        btnEditTin.addActionListener(WeakListeners.create(ActionListener.class, editTinL, btnEditTin));
        btnEditBe.addActionListener(WeakListeners.create(ActionListener.class, editBeL, btnEditBe));
        lstCalculations.addListSelectionListener(WeakListeners.create(
                ListSelectionListener.class,
                calcSelL,
                lstCalculations));
        chkImportFinished.addItemListener(WeakListeners.create(ItemListener.class, importChkL, chkImportFinished));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void init() {
        bindingGroup.unbind();

        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);

            bindingGroup.bind();

            initCalculationList();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    private void initCalculationList() {
        final List<CidsBean> c = cidsBean.getBeanCollectionProperty("berechnungen"); // NOI18N

        if ((c == null) || c.isEmpty()) {
            throw new IllegalStateException("no calculation found: " + cidsBean); // NOI18N
        }

        Collections.sort(c, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) || !(o1.getProperty("jaehrlichkeit") instanceof Integer) || (o2 == null) // NOI18N
                                || !(o2.getProperty("jaehrlichkeit") instanceof Integer)) {                   // NOI18N
                        throw new IllegalStateException(
                            "bean without valid jaehrlichkeit [obj1="                                         // NOI18N
                                    + o1
                                    + "|obj2="                                                                // NOI18N
                                    + o2
                                    + "]");                                                                   // NOI18N
                    }

                    return ((Integer)o1.getProperty("jaehrlichkeit")).compareTo( // NOI18N
                            (Integer)o2.getProperty("jaehrlichkeit"));           // NOI18N
                }
            });

        final DefaultListModel<CidsBean> dlm = new DefaultListModel<CidsBean>();
        dlm.setSize(c.size());
        for (int i = 0; i < c.size(); ++i) {
            dlm.set(i, c.get(i));
        }

        lstCalculations.setModel(dlm);
        lstCalculations.setSelectedIndex(0);
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

        pnlEditDialog = new javax.swing.JPanel();
        lblWmsCapUrl = new javax.swing.JLabel();
        lblLayerName = new javax.swing.JLabel();
        txtWmsCapUrl = new javax.swing.JTextField();
        txtLayerName = new javax.swing.JTextField();
        vFillEdit = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlCalculationDetail = new javax.swing.JPanel();
        semiRoundedPanelCalculationDetail = new de.cismet.tools.gui.SemiRoundedPanel();
        lblCalculationDetailTitle = new javax.swing.JLabel();
        pnlEmbeddedCalculation = new javax.swing.JPanel();
        oab_berechnungEditor = new de.cismet.cids.custom.wunda.oab.objecteditors.Oab_berechnungEditor();
        pnlData = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        semiRoundedPanelData = new de.cismet.tools.gui.SemiRoundedPanel();
        lblData = new javax.swing.JLabel();
        scpDescription = new javax.swing.JScrollPane();
        txpDescription = new javax.swing.JTextPane();
        lblType = new javax.swing.JLabel();
        lblBelongsToProject = new javax.swing.JLabel();
        lblBelongsToProjectValue = new javax.swing.JLabel();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        pnlCalculations = new javax.swing.JPanel();
        semiRoundedPanelCalculations = new de.cismet.tools.gui.SemiRoundedPanel();
        lblCalculations = new javax.swing.JLabel();
        scpCalculations = new javax.swing.JScrollPane();
        lstCalculations = new javax.swing.JList();
        pnlImports = new javax.swing.JPanel();
        sepImports = new javax.swing.JSeparator();
        chkImportFinished = new javax.swing.JCheckBox();
        hFillImports = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        btnEditTin = new javax.swing.JButton();
        btnEditBe = new javax.swing.JButton();

        pnlEditDialog.setOpaque(false);
        pnlEditDialog.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblWmsCapUrl,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblWmsCapUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlEditDialog.add(lblWmsCapUrl, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLayerName,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblLayerName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlEditDialog.add(lblLayerName, gridBagConstraints);

        txtWmsCapUrl.setText(NbBundle.getMessage(
                Oab_zustand_massnahmeEditor.class,
                "Oab_zustand_massnahmeEditor.txtWmsCapUrl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlEditDialog.add(txtWmsCapUrl, gridBagConstraints);

        txtLayerName.setText(NbBundle.getMessage(
                Oab_zustand_massnahmeEditor.class,
                "Oab_zustand_massnahmeEditor.txtLayerName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlEditDialog.add(txtLayerName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        pnlEditDialog.add(vFillEdit, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlCalculationDetail.setOpaque(false);
        pnlCalculationDetail.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelCalculationDetail.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelCalculationDetail.setLayout(new java.awt.GridBagLayout());

        lblCalculationDetailTitle.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        lblCalculationDetailTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblCalculationDetailTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblCalculationDetailTitle,
            NbBundle.getMessage(
                Oab_zustand_massnahmeEditor.class,
                "Oab_zustand_massnahmeEditor.lblCalculationDetailTitle.text"));       // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        semiRoundedPanelCalculationDetail.add(lblCalculationDetailTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlCalculationDetail.add(semiRoundedPanelCalculationDetail, gridBagConstraints);

        pnlEmbeddedCalculation.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)));
        pnlEmbeddedCalculation.setOpaque(false);
        pnlEmbeddedCalculation.setLayout(new java.awt.BorderLayout());
        pnlEmbeddedCalculation.add(oab_berechnungEditor, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCalculationDetail.add(pnlEmbeddedCalculation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        add(pnlCalculationDetail, gridBagConstraints);

        pnlData.setOpaque(false);
        pnlData.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblName,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblName.text")); // NOI18N
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
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblDescription.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblDescription, gridBagConstraints);

        semiRoundedPanelData.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelData.setLayout(new java.awt.GridBagLayout());

        lblData.setFont(new java.awt.Font("Lucida Grande", 0, 14));                                              // NOI18N
        lblData.setForeground(new java.awt.Color(255, 255, 255));
        lblData.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblData,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblData.text")); // NOI18N
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(scpDescription, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblType,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblType.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblType, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBelongsToProject,
            NbBundle.getMessage(
                Oab_zustand_massnahmeEditor.class,
                "Oab_zustand_massnahmeEditor.lblBelongsToProject.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblBelongsToProject, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.projekt.name}"),
                lblBelongsToProjectValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlData.add(lblBelongsToProjectValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.typ}"),
                defaultBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
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

        pnlCalculations.setOpaque(false);
        pnlCalculations.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanelCalculations.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanelCalculations.setLayout(new java.awt.GridBagLayout());

        lblCalculations.setFont(new java.awt.Font("Lucida Grande", 0, 14));                                              // NOI18N
        lblCalculations.setForeground(new java.awt.Color(255, 255, 255));
        lblCalculations.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            lblCalculations,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.lblCalculations.text")); // NOI18N
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

        lstCalculations.setModel(new javax.swing.AbstractListModel() {

                String[] strings = { "T20", "T30", "T100" };

                @Override
                public int getSize() {
                    return strings.length;
                }
                @Override
                public Object getElementAt(final int i) {
                    return strings[i];
                }
            });
        lstCalculations.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scpCalculations.setViewportView(lstCalculations);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pnlCalculations.add(scpCalculations, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        add(pnlCalculations, gridBagConstraints);

        pnlImports.setOpaque(false);
        pnlImports.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 0, 15);
        pnlImports.add(sepImports, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkImportFinished,
            NbBundle.getMessage(
                Oab_zustand_massnahmeEditor.class,
                "Oab_zustand_massnahmeEditor.chkImportFinished.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.import_vollstaendig}"),
                chkImportFinished,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 10);
        pnlImports.add(chkImportFinished, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlImports.add(hFillImports, gridBagConstraints);

        btnEditTin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda/oab/objecteditors/map_16.png")));              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnEditTin,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.btnEditTin.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlImports.add(btnEditTin, gridBagConstraints);

        btnEditBe.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda/oab/objecteditors/map_16.png")));             // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnEditBe,
            NbBundle.getMessage(Oab_zustand_massnahmeEditor.class, "Oab_zustand_massnahmeEditor.btnEditBe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlImports.add(btnEditBe, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(pnlImports, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ImportCheckL implements ItemListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void itemStateChanged(final ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // intentional invoke later in EDT to not confuse beansbinding
                EventQueue.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            boolean allChecked = true;
                            final List<CidsBean> calculations = cidsBean.getBeanCollectionProperty("berechnungen"); // NOI18N
                            final Iterator<CidsBean> it = calculations.iterator();

                            while (it.hasNext() && allChecked) {
                                final Boolean completed = (Boolean)it.next().getProperty("import_vollstaendig"); // NOI18N
                                if ((completed == null) || !completed) {
                                    allChecked = false;
                                }
                            }

                            if (!allChecked) {
                                final int answer = JOptionPane.showConfirmDialog(
                                        Oab_zustand_massnahmeEditor.this,
                                        NbBundle.getMessage(
                                            Oab_zustand_massnahmeEditor.class,
                                            "Oab_Zustand_MassnahmeEditor.ImportCheckL.itemStateChanged(ItemEvent).batchFinish.message"), // NOI18N
                                        NbBundle.getMessage(
                                            Oab_zustand_massnahmeEditor.class,
                                            "Oab_Zustand_MassnahmeEditor.ImportCheckL.itemStateChanged(ItemEvent).batchFinish.title"), // NOI18N
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE);

                                if (answer == JOptionPane.YES_OPTION) {
                                    for (final CidsBean bean : calculations) {
                                        try {
                                            bean.setProperty("import_vollstaendig", Boolean.TRUE);                       // NOI18N
                                        } catch (final Exception ex) {
                                            throw new IllegalStateException("unexpected exception at set property", ex); // NOI18N
                                        }
                                    }
                                }
                            }
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class CalculationSelectionL implements ListSelectionListener {

        //~ Methods ------------------------------------------------------------

        @Override
        public void valueChanged(final ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                final CidsBean bean = (CidsBean)lstCalculations.getSelectedValue();
                if (bean == null) {
                    throw new IllegalStateException("no calculation selected, this is illegal"); // NOI18N
                }

                oab_berechnungEditor.setCidsBean(bean);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class EditWMSPropertiesListener implements ActionListener {

        //~ Instance fields ----------------------------------------------------

        private final String propPrefix;
        private final String title;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new EditWMSPropertiesListener object.
         *
         * @param  propPrefix  DOCUMENT ME!
         * @param  title       DOCUMENT ME!
         */
        public EditWMSPropertiesListener(final String propPrefix, final String title) {
            this.propPrefix = propPrefix;
            this.title = title;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            txtWmsCapUrl.setText((String)cidsBean.getProperty(propPrefix + "_cap"));        // NOI18N
            txtLayerName.setText((String)cidsBean.getProperty(propPrefix + "_layer_name")); // NOI18N

            final int answer = JOptionPane.showConfirmDialog(
                    Oab_zustand_massnahmeEditor.this,
                    pnlEditDialog,
                    title,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (answer == JOptionPane.OK_OPTION) {
                try {
                    cidsBean.setProperty(propPrefix + "_cap", txtWmsCapUrl.getText());           // NOI18N
                    cidsBean.setProperty(propPrefix + "_layer_name", txtLayerName.getText());    // NOI18N
                } catch (final Exception ex) {
                    throw new IllegalStateException("unexpected exception at set property", ex); // NOI18N
                }
            }
        }
    }
}
