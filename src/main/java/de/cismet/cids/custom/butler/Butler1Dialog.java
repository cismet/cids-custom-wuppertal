/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 * 
* ... and it just works.
 * 
***************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.butler;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicButtonUI;

import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.utils.butler.ButlerProduct;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.tools.gui.downloadmanager.DownloadManager;

/**
 * DOCUMENT ME!
 *
 * @author daniel
 * @version $Revision$, $Date$
 */
public class Butler1Dialog extends javax.swing.JDialog implements DocumentListener {

    //~ Static fields/initializers ---------------------------------------------
    private static final Logger LOG = Logger.getLogger(Butler1Dialog.class);
    //~ Instance fields --------------------------------------------------------
    // End of variables declaration
    JPanel emptyPanel = new JPanel();
    ArrayList<PredefinedBoxes> boxes;
    private MappingComponent map;
    private PredefinedBoxes noSelectionBox=null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreate;
    private de.cismet.cids.custom.butler.Butler1ProductPanel butler1ProductPanel1;
    private javax.swing.JComboBox cbSize;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel lblLowerPosition;
    private javax.swing.JLabel lblRequestNumber;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblUpperPosition;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlMap;
    private javax.swing.JPanel pnlMapSettings;
    private javax.swing.JPanel pnlProductSettings;
    private javax.swing.JPanel pnlUpperBound;
    private javax.swing.JTabbedPane tbpProducts;
    private javax.swing.JTextField tfLowerE;
    private javax.swing.JTextField tfLowerN;
    private javax.swing.JTextField tfOrderId;
    private javax.swing.JTextField tfUpperE;
    private javax.swing.JTextField tfUpperN;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form Butler1Dialog.
     *
     * @param parent DOCUMENT ME!
     * @param modal DOCUMENT ME!
     */
    public Butler1Dialog(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        boxes = PredefinedBoxes.elements;
        initComponents();
        tfLowerE.getDocument().addDocumentListener(this);
        tfLowerN.getDocument().addDocumentListener(this);
        DocumentListener upperTfListeners = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                if (noSelectionBox != null) {
                    cbSize.setSelectedItem(noSelectionBox);
                }
            }
        };

        tfUpperE.getDocument().addDocumentListener(upperTfListeners);
        tfUpperN.getDocument().addDocumentListener(upperTfListeners);
        tbpProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent me) {
                final int tabNr = ((TabbedPaneUI) tbpProducts.getUI()).tabForCoordinate(
                        tbpProducts,
                        me.getX(),
                        me.getY());
                if (tabNr == (tbpProducts.getTabCount() - 1)) {
                    addCloseableTab();
                }
            }
        });
        tbpProducts.setTabComponentAt(0, getTabComponent(false));
        final JPanel addPan = new JPanel();
        addPan.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        addPan.setOpaque(false);
        addPan.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        final JLabel addLabel = new JLabel("+");
        addLabel.setOpaque(false);
        addLabel.setBorder(null);
        addLabel.setFocusable(false);
        addPan.add(addLabel);
        tbpProducts.addTab("+", null);
        tbpProducts.setTabComponentAt(tbpProducts.getTabCount() - 1, addPan);
//        tbpProducts.setTabComponentAt(tbpProducts.getTabCount() - 1, addLabel);
        for (final PredefinedBoxes box : boxes) {
            if (box.getDisplayName().equals("keine Auswahl")) {
                noSelectionBox = box;
                cbSize.setSelectedItem(noSelectionBox);
                break;
            }
        }
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        pnlProductSettings = new javax.swing.JPanel();
        tbpProducts = new javax.swing.JTabbedPane();
        butler1ProductPanel1 = new de.cismet.cids.custom.butler.Butler1ProductPanel();
        lblRequestNumber = new javax.swing.JLabel();
        tfOrderId = new javax.swing.JTextField();
        pnlMapSettings = new javax.swing.JPanel();
        lblLowerPosition = new javax.swing.JLabel();
        tfLowerE = new javax.swing.JTextField();
        tfLowerN = new javax.swing.JTextField();
        lblSize = new javax.swing.JLabel();
        cbSize = new javax.swing.JComboBox();
        pnlMap = new javax.swing.JPanel();
        pnlUpperBound = new javax.swing.JPanel();
        tfUpperE = new javax.swing.JTextField();
        tfUpperN = new javax.swing.JTextField();
        lblUpperPosition = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        pnlControls = new javax.swing.JPanel();
        btnCreate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pnlProductSettings.setMinimumSize(new java.awt.Dimension(450, 300));
        pnlProductSettings.setPreferredSize(new java.awt.Dimension(450, 300));
        pnlProductSettings.setLayout(new java.awt.GridBagLayout());

        tbpProducts.setMinimumSize(new java.awt.Dimension(400, 400));
        tbpProducts.addTab(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.butler1ProductPanel1.TabConstraints.tabTitle"), butler1ProductPanel1); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlProductSettings.add(tbpProducts, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblRequestNumber, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.lblRequestNumber.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 10, 20);
        pnlProductSettings.add(lblRequestNumber, gridBagConstraints);

        tfOrderId.setText(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.tfOrderId.text")); // NOI18N
        tfOrderId.setMinimumSize(new java.awt.Dimension(70, 27));
        tfOrderId.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 5);
        pnlProductSettings.add(tfOrderId, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlProductSettings, gridBagConstraints);

        pnlMapSettings.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lblLowerPosition, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.lblLowerPosition.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnlMapSettings.add(lblLowerPosition, gridBagConstraints);

        tfLowerE.setText(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.tfLowerE.text")); // NOI18N
        tfLowerE.setMinimumSize(new java.awt.Dimension(70, 27));
        tfLowerE.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        pnlMapSettings.add(tfLowerE, gridBagConstraints);

        tfLowerN.setText(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.tfLowerN.text")); // NOI18N
        tfLowerN.setMinimumSize(new java.awt.Dimension(70, 27));
        tfLowerN.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlMapSettings.add(tfLowerN, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblSize, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.lblSize.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlMapSettings.add(lblSize, gridBagConstraints);

        cbSize.setPreferredSize(new java.awt.Dimension(150, 27));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${boxes}");
        org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, cbSize);
        bindingGroup.addBinding(jComboBoxBinding);

        cbSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSizeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlMapSettings.add(cbSize, gridBagConstraints);

        pnlMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlMapLayout = new javax.swing.GroupLayout(pnlMap);
        pnlMap.setLayout(pnlMapLayout);
        pnlMapLayout.setHorizontalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 374, Short.MAX_VALUE)
        );
        pnlMapLayout.setVerticalGroup(
            pnlMapLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 339, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlMapSettings.add(pnlMap, gridBagConstraints);

        pnlUpperBound.setLayout(new java.awt.GridBagLayout());

        tfUpperE.setText(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.tfUpperE.text")); // NOI18N
        tfUpperE.setMinimumSize(new java.awt.Dimension(70, 27));
        tfUpperE.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlUpperBound.add(tfUpperE, gridBagConstraints);

        tfUpperN.setText(org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.tfUpperN.text")); // NOI18N
        tfUpperN.setMinimumSize(new java.awt.Dimension(70, 27));
        tfUpperN.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        pnlUpperBound.add(tfUpperN, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblUpperPosition, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.lblUpperPosition.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        pnlUpperBound.add(lblUpperPosition, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        pnlUpperBound.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlMapSettings.add(pnlUpperBound, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlMapSettings, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnCreate, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.btnCreate.text")); // NOI18N
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlControls.add(btnCreate, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnCancel, org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlControls.add(btnCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(pnlControls, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void btnCreateActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // ToDo: check input values
        final Butler1ProductPanel productPan = (Butler1ProductPanel) tbpProducts.getSelectedComponent();
        final ButlerProduct bp = productPan.getSelectedProduct();
        LOG.info("Create the following Butler product:\n\t orderId: " + tfOrderId.getText()
                + "\n\t productId: " + bp.getKey()
                + "\n\t colorDepth: " + bp.getColorDepth()
                + "\n\t resolution: " + bp.getResolution()
                + "\n\t format: " + bp.getFormat());
        final double minX = 370000d;
        final double minY = 5680000d;
        final double maxX = 370300d;
        final double maxY = 5680300d;
        final ButlerDownload download = new ButlerDownload(tfOrderId.getText(), bp, minX, minY, maxX, maxY);
        DownloadManager.instance().add(download);
//        this.setVisible(false);
        this.dispose();
//        System.exit(0);
    }//GEN-LAST:event_btnCreateActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void cbSizeActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSizeActionPerformed
        final PredefinedBoxes selectedBox = (PredefinedBoxes) cbSize.getSelectedItem();
        if ((selectedBox != null) && !selectedBox.getDisplayName().equals("keine Auswahl")) {
            updatePositionFields();
        } else {
            tfUpperE.setText("");
            tfUpperN.setText("");
        }
    }//GEN-LAST:event_cbSizeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.dispose();                                                           // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void updatePositionFields() {
        final PredefinedBoxes selectedBox = (PredefinedBoxes) cbSize.getSelectedItem();
        if ((selectedBox != null) && !selectedBox.getDisplayName().equals("keine Auswahl")) {
            final double eSize = selectedBox.getEastSize();
            final double nSize = selectedBox.getNorthSize();
            if ((tfLowerE.getText() != null) && !tfLowerE.getText().equals("")) {
                final double lowerE = Double.parseDouble(tfLowerE.getText().replaceAll(",", "."));
                tfUpperE.setText("" + (lowerE + eSize));
            }
            if ((tfLowerN.getText() != null) && !tfLowerN.getText().equals("")) {
                final double lowerN = Double.parseDouble(tfLowerN.getText().replaceAll(",", "."));
                tfUpperN.setText("" + (lowerN + nSize));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        final Runnable mapRunnable = new Runnable() {
            @Override
            public void run() {
                final ActiveLayerModel mappingModel = new ActiveLayerModel();
                mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                mappingModel.addHome(getBoundingBox());

                final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                        AlkisConstants.COMMONS.MAP_CALL_STRING));
                swms.setName("butler-background");

                // add the raster layer to the model
                mappingModel.addLayer(swms);
                // set the model
                map.setMappingModel(mappingModel);
                // initial positioning of the map
                final int duration = map.getAnimationDuration();
                map.setAnimationDuration(0);
                map.gotoInitialBoundingBox();
                // interaction mode
                map.setInteractionMode(MappingComponent.ZOOM);
                // finally when all configurations are done ...
                map.unlock();
                map.setInteractionMode("MUTE");
                map.setAnimationDuration(duration);
            }

            private XBoundingBox getBoundingBox() {
                final XBoundingBox currBb = (XBoundingBox) CismapBroker.getInstance().getMappingComponent()
                        .getCurrentBoundingBox();
                final XBoundingBox result = new XBoundingBox(currBb.getGeometry().buffer(20));

                return result;
            }
        };

        if (EventQueue.isDispatchThread()) {
            mapRunnable.run();
        } else {
            EventQueue.invokeLater(mapRunnable);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList<PredefinedBoxes> getBoxes() {
        return boxes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param boxes DOCUMENT ME!
     */
    public void setBoxes(final ArrayList<PredefinedBoxes> boxes) {
        this.boxes = boxes;
    }

    /**
     * DOCUMENT ME!
     */
    private void addCloseableTab() {
        final Component tabComp = getTabComponent(true);
        final int number = tbpProducts.getTabCount();
        final String title = "Produkt " + number;
        final int tabPos = tbpProducts.getTabCount() - 1;
        final Component comp = new Butler1ProductPanel();
        tbpProducts.insertTab("", null, comp, title, tabPos);
//        tbpProducts.insertTab(null, null, comp, null, tabPos);
        final int tabPosNew = tbpProducts.indexOfComponent(comp);
        tbpProducts.setTabComponentAt(tabPosNew, tabComp);
//        tbpProducts.setT
    }

    /**
     * DOCUMENT ME!
     *
     * @param isCloseableTab DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Component getTabComponent(final boolean isCloseableTab) {
        final int number = tbpProducts.getTabCount();
        return getTabComponent(isCloseableTab, number);
    }

    /**
     * DOCUMENT ME!
     *
     * @param isCloseableTab DOCUMENT ME!
     * @param tabNumber DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Component getTabComponent(final boolean isCloseableTab, final int tabNumber) {
        final JPanel tabComp = new JPanel();
        tabComp.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints;
        tabComp.setOpaque(false);
        // add more space to the top of the component
        tabComp.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        final String title = "Produkt " + tabNumber;
        final JLabel label = new JLabel(title);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        tabComp.add(label, gridBagConstraints);
        // add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        if (isCloseableTab) {
            // tab button
            final JButton button = new JButton("x");
//            button.setIcon();
            button.setPreferredSize(new Dimension(10, 19));
            button.setToolTipText("close this tab");
            // Make the button looks the same for all Laf's
            button.setUI(new BasicButtonUI());
            // Make it transparent
            button.setContentAreaFilled(false);
            // No need to be focusable
            button.setFocusable(false);
            button.setBorder(null);
            button.setBorderPainted(false);
            button.setRolloverEnabled(true);
            // Close the proper tab by clicking the button
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent ae) {
                    final int i = tbpProducts.indexOfTabComponent(tabComp);
                    if (i != -1) {
                        tbpProducts.remove(i);
                    }
                    if (i == tbpProducts.getSelectedIndex()) {
                        tbpProducts.setSelectedIndex(i - 1);
                    }
                    updateTabComponents();
                }
            });
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent me) {
                    button.setBorder(new LineBorder(Color.GRAY));
                    button.setBorderPainted(true);
                }

                @Override
                public void mouseExited(final MouseEvent me) {
                    button.setBorder(null);
                    button.setBorderPainted(false);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            tabComp.add(button, gridBagConstraints);
        }
        return tabComp;
    }

    /**
     * DOCUMENT ME!
     */
    private void updateTabComponents() {
        for (int i = 1; i < (tbpProducts.getTabCount() - 1); i++) {
            tbpProducts.setTabComponentAt(i, getTabComponent(true, i + 1));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (final javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Butler1Dialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Butler1Dialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Butler1Dialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Butler1Dialog.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                final Butler1Dialog dialog = new Butler1Dialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(final java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    @Override
    public void insertUpdate(final DocumentEvent de) {
        updatePositionFields();
    }

    @Override
    public void removeUpdate(final DocumentEvent de) {
        updatePositionFields();
    }

    @Override
    public void changedUpdate(final DocumentEvent de) {
        updatePositionFields();
    }
}
