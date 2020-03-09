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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.treeicons.wunda_blau.VzkatSchildIconFactory;
import de.cismet.cids.custom.utils.vzkat.VzkatUtils;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatZeichenLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatStandortSchildPanel extends javax.swing.JPanel implements ConnectionContextStore, CidsBeanStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VzkatStandortSchildPanel.class);
    private static final ImageIcon ERROR_ICON = new ImageIcon(VzkatSchildIconFactory.class.getResource(
                "/res/16/vzkat_error.png"));
    private static final String ICON_URL_TEMPLATE =
        "http://dokumente.s10222.wuppertal-intra.de/vzkat-bilder/128x128/%s.png";
    private static final Map<String, ImageIcon> ICONS = new HashMap<>();

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private MetaClass mcVzkatStvo = null;
    private final VzkatZeichenLightweightSearch verkehrszeichenSearch = new VzkatZeichenLightweightSearch();

    private boolean cbStvoActionListenerEnabled = true;
    private SwingWorker<ImageIcon, Void> iconLoadingWorker = null;

    private final VzkatStandortEditor parentEditor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbStvo;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbVerkehrszeichen;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private javax.swing.Box.Filler fillBeschreibungBodyLeft;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JLabel lblVerkehrszeichen;
    private javax.swing.JPanel panBeschreibungBody;
    private javax.swing.JPanel panBeschreibungBodyLeft;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.RoundedPanel panBild;
    private javax.swing.JTextArea txtBemerkung;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form VzkatStandortSchildPanel.
     */
    public VzkatStandortSchildPanel() {
        this(null, false);
    }

    /**
     * Creates a new VzkatStandortSchildPanel object.
     *
     * @param  parentEditor  DOCUMENT ME!
     * @param  editable      DOCUMENT ME!
     */
    public VzkatStandortSchildPanel(final VzkatStandortEditor parentEditor, final boolean editable) {
        this.editable = editable;
        this.parentEditor = parentEditor;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  selected  DOCUMENT ME!
     */
    public void setSelected(final boolean selected) {
        jPanel4.setOpaque(selected);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
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

        panBild = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jLabel2 = new javax.swing.JLabel();
        panBeschreibungBody = new javax.swing.JPanel();
        panBeschreibungBodyLeft = new javax.swing.JPanel();
        lblVerkehrszeichen = new javax.swing.JLabel();
        fillBeschreibungBodyLeft = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblBemerkung = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblIcon = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblPosition = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel3 = new javax.swing.JPanel();
        cbStvo = new DefaultBindableReferenceCombo(mcVzkatStvo, true, false);
        cbVerkehrszeichen = new de.cismet.cids.editors.FastBindableReferenceCombo(
                verkehrszeichenSearch,
                verkehrszeichenSearch.getRepresentationPattern(),
                verkehrszeichenSearch.getRepresentationFields());
        jPanel4 = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        panBild.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton3.text"));                                          // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton3.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton3.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        panBeschreibungTitle.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());

        jButton4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton4.text"));                                             // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton4.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton4.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panBeschreibungTitle.add(jButton4, gridBagConstraints);
        jButton4.setVisible(isEditable());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panBeschreibungTitle.add(filler3, gridBagConstraints);

        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBild.add(panBeschreibungTitle, gridBagConstraints);

        panBeschreibungBody.setOpaque(false);
        panBeschreibungBody.setLayout(new java.awt.GridBagLayout());

        panBeschreibungBodyLeft.setOpaque(false);
        panBeschreibungBodyLeft.setLayout(new java.awt.GridBagLayout());

        lblVerkehrszeichen.setFont(lblVerkehrszeichen.getFont().deriveFont(
                lblVerkehrszeichen.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVerkehrszeichen,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblVerkehrszeichen.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblVerkehrszeichen, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungBodyLeft.add(fillBeschreibungBodyLeft, gridBagConstraints);

        lblBemerkung.setFont(lblBemerkung.getFont().deriveFont(lblBemerkung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBemerkung,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblBemerkung, gridBagConstraints);

        txtBemerkung.setColumns(20);
        txtBemerkung.setRows(3);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panBeschreibungBodyLeft.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblIcon,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblIcon.text")); // NOI18N
        lblIcon.setMaximumSize(new java.awt.Dimension(128, 128));
        lblIcon.setMinimumSize(new java.awt.Dimension(128, 128));
        lblIcon.setPreferredSize(new java.awt.Dimension(128, 128));
        lblIcon.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblIconMouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBodyLeft.add(lblIcon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBodyLeft.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBodyLeft.add(filler2, gridBagConstraints);

        lblPosition.setFont(new java.awt.Font("Ubuntu", 1, 13)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblPosition,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblPosition.text"));   // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblPosition, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton2.text")); // NOI18N
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        jPanel1.add(jButton2, gridBagConstraints);
        jButton2.setVisible(isEditable());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.reihenfolge}"),
                jLabel1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton1.text")); // NOI18N
        jButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 5);
        jPanel1.add(jButton1, gridBagConstraints);
        jButton1.setVisible(isEditable());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel2.add(jPanel1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_richtung}"),
                defaultBindableReferenceCombo1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        defaultBindableReferenceCombo1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    defaultBindableReferenceCombo1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel2.add(defaultBindableReferenceCombo1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBeschreibungBodyLeft.add(jPanel2, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        cbStvo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbStvoActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(cbStvo, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zeichen}"),
                cbVerkehrszeichen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbVerkehrszeichen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbVerkehrszeichenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel3.add(cbVerkehrszeichen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBeschreibungBodyLeft.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungBody.add(panBeschreibungBodyLeft, gridBagConstraints);

        jPanel4.setBackground(java.awt.SystemColor.activeCaption);
        jPanel4.setMaximumSize(new java.awt.Dimension(5, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(5, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panBeschreibungBody.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBild.add(panBeschreibungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panBild, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        parentEditor.removeSchildPanel(VzkatStandortSchildPanel.this);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        parentEditor.addSchildPanel(VzkatStandortSchildPanel.this);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        parentEditor.downSchildPanel(VzkatStandortSchildPanel.this);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        parentEditor.upSchildPanel(VzkatStandortSchildPanel.this);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStvoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStvoActionPerformed
        if (cbStvoActionListenerEnabled) {
            final CidsBean stvoBean = (CidsBean)cbStvo.getSelectedItem();
            verkehrszeichenSearch.setStvoId((stvoBean != null) ? (Integer)stvoBean.getProperty("id") : null);
            cbVerkehrszeichen.setSelectedItem(null);
            new SwingWorker<Void, Void>() {

                    @Override
                    protected Void doInBackground() throws Exception {
                        cbVerkehrszeichen.refreshModel();
                        return null;
                    }
                }.execute();
        }
    } //GEN-LAST:event_cbStvoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbVerkehrszeichenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbVerkehrszeichenActionPerformed
        final JTextField txt = (JTextField)cbVerkehrszeichen.getEditor().getEditorComponent();
        final CidsBean selectedZeichen = (CidsBean)cbVerkehrszeichen.getSelectedItem();

        final String text;
        if (selectedZeichen != null) {
            text = VzkatUtils.createZeichenToString(selectedZeichen);

            final CidsBean selectedStvo = (cbStvo.getSelectedItem() instanceof CidsBean)
                ? (CidsBean)cbStvo.getSelectedItem() : null;
            final CidsBean stvoOfSelectedZeichen = (CidsBean)selectedZeichen.getProperty("fk_stvo");
            if (!Objects.equals(stvoOfSelectedZeichen, selectedStvo)) {
                try {
                    cbStvoActionListenerEnabled = false;
                    cbStvo.setSelectedItem(stvoOfSelectedZeichen);
                } finally {
                    cbStvoActionListenerEnabled = true;
                }
            }

            refreshIcon(VzkatUtils.createZeichenKey(selectedZeichen));
        } else {
            text = "";
            refreshIcon(null);
        }
        txt.setText(text);
    } //GEN-LAST:event_cbVerkehrszeichenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void defaultBindableReferenceCombo1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_defaultBindableReferenceCombo1ActionPerformed
        parentEditor.richtungUpdate();
    }                                                                                                  //GEN-LAST:event_defaultBindableReferenceCombo1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblIconMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblIconMouseClicked
        parentEditor.setSelectedSchildBean(cidsBean);
        parentEditor.refreshSchildPanels();
    }                                                                       //GEN-LAST:event_lblIconMouseClicked

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        if (!editable) {
            RendererTools.makeReadOnly(defaultBindableReferenceCombo1);
            RendererTools.makeReadOnly(cbVerkehrszeichen);
            RendererTools.makeReadOnly(cbStvo);
            RendererTools.makeReadOnly(txtBemerkung);
        } else {
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbVerkehrszeichen);
        }

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    mcVzkatStvo = ClassCacheMultiple.getMetaClass(
                            "WUNDA_BLAU",
                            "VZKAT_STVO",
                            connectionContext);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        cbStvo.setMetaClass(mcVzkatStvo);
                        cbStvo.reload(true);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            panBeschreibungTitle.setBackground((MetaObject.NEW == cidsBean.getMetaObject().getStatus())
                    ? java.awt.SystemColor.info : java.awt.Color.darkGray);
            final CidsBean stvoOfSelectedZeichen = (CidsBean)cidsBean.getProperty("fk_zeichen.fk_stvo");
            try {
                cbStvoActionListenerEnabled = false;
                cbStvo.setSelectedItem(stvoOfSelectedZeichen);
            } finally {
                cbStvoActionListenerEnabled = true;
            }

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                this.cidsBean,
                getConnectionContext());
        }
        bindingGroup.bind();

        refreshIcon((cidsBean != null) ? VzkatUtils.createZeichenKey((CidsBean)cidsBean.getProperty("fk_zeichen"))
                                       : null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static ImageIcon loadZeichenIcon(final String key) throws Exception {
        final String urlString = String.format(ICON_URL_TEMPLATE, key);
        final InputStream is = WebAccessManager.getInstance().doRequest(new URL(urlString));
        return new ImageIcon(ImageIO.read(is));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    private void refreshIcon(final String key) {
        if (key != null) {
            iconLoadingWorker = new SwingWorker<ImageIcon, Void>() {

                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        return loadZeichenIcon(key);
                    }

                    @Override
                    protected void done() {
                        if (this.equals(iconLoadingWorker)) {
                            try {
                                final ImageIcon icon = get();
                                ICONS.put(key, icon);
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                                ICONS.put(key, ERROR_ICON);
                            }
                            lblIcon.setIcon(ICONS.get(key));
                        }
                    }
                };
            iconLoadingWorker.execute();
        } else {
            lblIcon.setIcon(null);
        }
    }
}
