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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;
import org.apache.log4j.Logger;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;
import de.cismet.tools.gui.FooterComponentProvider;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * DOCUMENT ME!
 *
 * @author daniel
 * @version $Revision$, $Date$
 */
public class MauerEditor extends javax.swing.JPanel implements CidsBeanRenderer, EditorSaveListener, FooterComponentProvider {

    //~ Instance fields --------------------------------------------------------
    private CidsBean cidsBean;
    private String title;
    private final Logger log = Logger.getLogger(MauerEditor.class);
    private MappingComponent map;
    private boolean editable;
    private CardLayout cardLayout;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private de.cismet.cids.editors.DefaultBindableDateChooser defaultBindableDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel lblFiller2;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblLagebeschreibung;
    private javax.swing.JLabel lblLagebeschreibung1;
    private javax.swing.JLabel lblLagebeschreibung2;
    private javax.swing.JLabel lblLagebeschreibung3;
    private javax.swing.JLabel lblLagebeschreibung4;
    private javax.swing.JLabel lblLagebeschreibung5;
    private javax.swing.JLabel lblLagebeschreibung6;
    private javax.swing.JLabel lblLagebeschreibung7;
    private javax.swing.JLabel lblLagebeschreibung8;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel pnlCard1;
    private javax.swing.JPanel pnlCard2;
    private de.cismet.tools.gui.RoundedPanel roundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates new form MauerEditor.
     */
    public MauerEditor() {
        this(true);
    }

    public MauerEditor(boolean editable) {
        this.editable = editable;
        initComponents();
        map = new MappingComponent();
        jPanel1.setLayout(new BorderLayout());
        jPanel1.add(map, BorderLayout.CENTER);

        setEditable();

        LayoutManager layout = getLayout();
        if (layout instanceof CardLayout) {
            cardLayout = (CardLayout) layout;
            cardLayout.show(this, "card1");
        }
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panFooter = new javax.swing.JPanel();
        panLeft = new javax.swing.JPanel();
        lblInfo = new javax.swing.JLabel();
        btnInfo = new javax.swing.JButton();
        panRight = new javax.swing.JPanel();
        btnImages = new javax.swing.JButton();
        lblImages = new javax.swing.JLabel();
        pnlCard1 = new javax.swing.JPanel();
        roundedPanel1 = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        lblLagebeschreibung = new javax.swing.JLabel();
        lblFiller2 = new javax.swing.JLabel();
        lblLagebeschreibung1 = new javax.swing.JLabel();
        lblLagebeschreibung2 = new javax.swing.JLabel();
        lblLagebeschreibung3 = new javax.swing.JLabel();
        lblLagebeschreibung4 = new javax.swing.JLabel();
        lblLagebeschreibung5 = new javax.swing.JLabel();
        lblLagebeschreibung6 = new javax.swing.JLabel();
        lblLagebeschreibung7 = new javax.swing.JLabel();
        lblLagebeschreibung8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        defaultBindableDateChooser1 = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        pnlCard2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        lblInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblInfo.text")); // NOI18N
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png"))); // NOI18N
        btnInfo.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnInfo.text")); // NOI18N
        btnInfo.setBorderPainted(false);
        btnInfo.setContentAreaFilled(false);
        btnInfo.setEnabled(false);
        btnInfo.setFocusPainted(false);
        btnInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInfoActionPerformed(evt);
            }
        });
        panLeft.add(btnInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panFooter.add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);

        btnImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        btnImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.btnImages.text")); // NOI18N
        btnImages.setBorderPainted(false);
        btnImages.setContentAreaFilled(false);
        btnImages.setFocusPainted(false);
        btnImages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImagesActionPerformed(evt);
            }
        });
        panRight.add(btnImages);

        lblImages.setFont(new java.awt.Font("DejaVu Sans", 1, 14)); // NOI18N
        lblImages.setForeground(new java.awt.Color(255, 255, 255));
        lblImages.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblImages.text")); // NOI18N
        panRight.add(lblImages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        setMinimumSize(new java.awt.Dimension(807, 485));
        setVerifyInputWhenFocusTarget(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        roundedPanel1.setPreferredSize(new java.awt.Dimension(600, 430));
        roundedPanel1.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel1.setMinimumSize(new java.awt.Dimension(109, 24));
        semiRoundedPanel1.setPreferredSize(new java.awt.Dimension(109, 24));
        semiRoundedPanel1.setLayout(new java.awt.FlowLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel1.text")); // NOI18N
        semiRoundedPanel1.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        roundedPanel1.add(semiRoundedPanel1, gridBagConstraints);

        lblLagebeschreibung.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung, gridBagConstraints);

        lblFiller2.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblFiller2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(lblFiller2, gridBagConstraints);

        lblLagebeschreibung1.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung1, gridBagConstraints);

        lblLagebeschreibung2.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung2, gridBagConstraints);

        lblLagebeschreibung3.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung3, gridBagConstraints);

        lblLagebeschreibung4.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung4, gridBagConstraints);

        lblLagebeschreibung5.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung5, gridBagConstraints);

        lblLagebeschreibung6.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung6, gridBagConstraints);

        lblLagebeschreibung7.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung7, gridBagConstraints);

        lblLagebeschreibung8.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.lblLagebeschreibung8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        roundedPanel1.add(lblLagebeschreibung8, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(26, 60));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(262, 60));
        jScrollPane1.setRequestFocusEnabled(false);

        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setPreferredSize(new java.awt.Dimension(260, 34));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagebeschreibung}"), jTextArea1, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue("unreadable");
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jScrollPane1, gridBagConstraints);

        jTextField2.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField2.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebung}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField2, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(26, 60));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(262, 60));

        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setPreferredSize(new java.awt.Dimension(260, 34));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.neigung}"), jTextArea2, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jScrollPane2, gridBagConstraints);

        jTextField3.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField3.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauertyp}"), jTextField3, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue("unreadable");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField3, gridBagConstraints);

        jTextField4.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField4.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.materialtyp}"), jTextField4, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField4, gridBagConstraints);

        jTextField5.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField5.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe}"), jTextField5, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField5, gridBagConstraints);

        jTextField6.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField6.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.laenge}"), jTextField6, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField6, gridBagConstraints);

        jTextField7.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField7.setPreferredSize(new java.awt.Dimension(100, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"), jTextField7, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(jTextField7, gridBagConstraints);

        defaultBindableDateChooser1.setPreferredSize(new java.awt.Dimension(124, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hauptpruefung_1}"), defaultBindableDateChooser1, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(defaultBindableDateChooser1.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        roundedPanel1.add(defaultBindableDateChooser1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(roundedPanel1, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlCard1.add(jPanel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel4.text")); // NOI18N
        jPanel2.add(jLabel4, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jPanel2, gridBagConstraints);

        add(pnlCard1, "card1");

        pnlCard2.setOpaque(false);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(MauerEditor.class, "MauerEditor.jLabel2.text")); // NOI18N
        pnlCard2.add(jLabel2);

        add(pnlCard2, "card2");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnImagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImagesActionPerformed

        cardLayout.show(this, "card2");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }//GEN-LAST:event_btnImagesActionPerformed

    private void btnInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInfoActionPerformed
        cardLayout.show(this, "card1");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }//GEN-LAST:event_btnInfoActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;
            bindingGroup.unbind();
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTitle() {
        return String.valueOf(cidsBean);
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        // new WrrlEditorTester("bewirtschaftungsende", BewirtschaftungsendeEditor.class, WRRLUtil.DOMAIN_NAME).run();
        DevelopmentTools.createEditorInFrameFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "kif",
                "mauer",
                1,
                1280,
                1024);
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
//        throw new UnsupportedOperationException("Not supported yet.");
        log.info("editor closed");
    }

    @Override
    public boolean prepareForSave() {
//        throw new UnsupportedOperationException("Not supported yet.");
        log.info("prepare for save");
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        final Object geoObj = cidsBean.getProperty("georeferenz");
        if (geoObj instanceof Geometry) {
            final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry) geoObj,
                    AlkisConstants.COMMONS.SRS_SERVICE);
            final BoundingBox box = new BoundingBox(pureGeom.getEnvelope().buffer(AlkisConstants.COMMONS.GEO_BUFFER));

            final Runnable mapRunnable = new Runnable() {
                @Override
                public void run() {
                    final ActiveLayerModel mappingModel = new ActiveLayerModel();
                    mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);
                    mappingModel.addHome(new XBoundingBox(
                            box.getX1(),
                            box.getY1(),
                            box.getX2(),
                            box.getY2(),
                            AlkisConstants.COMMONS.SRS_SERVICE,
                            true));
                    final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                            AlkisConstants.COMMONS.MAP_CALL_STRING));
                    swms.setName("Mauer");
                    final StyledFeature dsf = new DefaultStyledFeature();
                    dsf.setGeometry(pureGeom);
                    dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));
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
                    map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {
                        @Override
                        public void mouseClicked(final PInputEvent evt) {
                            if (evt.getClickCount() > 1) {
                                final CidsBean bean = cidsBean;
                                ObjectRendererUtils.switchToCismapMap();
                                ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                            }
                        }
                    });
                    map.setInteractionMode("MUTE");
                    map.getFeatureCollection().addFeature(dsf);
                    map.setAnimationDuration(duration);
                }
            };
            if (EventQueue.isDispatchThread()) {
                mapRunnable.run();
            } else {
                EventQueue.invokeLater(mapRunnable);
            }
        }
    }

    private void setEditable() {
        jScrollPane1.getViewport().setOpaque(editable);
        jScrollPane1.setOpaque(editable);
        jScrollPane2.getViewport().setOpaque(editable);
        jScrollPane2.setOpaque(editable);
        setComponentEditable(jTextArea1);
        setComponentEditable(jTextArea2);
        setComponentEditable(jTextField2);
        setComponentEditable(jTextField3);
        setComponentEditable(jTextField4);
        setComponentEditable(jTextField5);
        setComponentEditable(jTextField6);
        setComponentEditable(jTextField7);
        defaultBindableDateChooser1.setEditable(editable);
    }

    /**
     * DOCUMENT ME!
     *
     * @param tComp DOCUMENT ME!
     */
    private void setComponentEditable(final JTextComponent tComp) {
        tComp.setEditable(editable);
        tComp.setOpaque(editable);
        if (!editable) {
            tComp.setBorder(null);
        }
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }
}
