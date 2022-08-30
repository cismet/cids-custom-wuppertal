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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.InputStream;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.plaf.basic.ComboPopup;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.utils.vzkat.VzkatUtils;
import de.cismet.cids.custom.wunda_blau.search.server.VzkatZeichenLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.converters.SqlTimestampToUtilDateConverter;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VzkatStandortSchildPanel extends javax.swing.JPanel implements ConnectionContextStore,
    CidsBeanStore,
    Disposable {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VzkatStandortSchildPanel.class);
    private static final ImageIcon ERROR_ICON = new ImageIcon(VzkatStandortSchildPanel.class.getResource(
                "/res/vzkat/error_128.png"));
//    private static final String ICON_URL_TEMPLATE = "http://dokumente.s10222.wuppertal-intra.de/vzkat-bilder/128x128/%s.png";
    private static final String ICON_PATH_TEMPLATE =
        "/de/cismet/cids/custom/wunda_blau/res/vzkat-bilder/128x128/%s.png";
    private static final Map<String, ImageIcon> ICONS = new WeakHashMap<>();

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private final VzkatStandortEditor parentEditor;
    private final VzkatZeichenLightweightSearch verkehrszeichenSearch = new VzkatZeichenLightweightSearch();
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                if ((parentEditor != null) && (parentEditor.getCidsBean() != null)
                            && !"fk_richtung".equals(evt.getPropertyName())) {
                    parentEditor.getCidsBean().setArtificialChangeFlag(true);
                }
            }
        };

    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private MetaClass mcVzkatStvo = null;
    private SwingWorker<ImageIcon, Void> iconLoadingWorker = null;
    private boolean cbStvoActionListenerEnabled = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbSchildPrivat;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbStvo;
    private de.cismet.cids.editors.FastBindableReferenceCombo cbVerkehrszeichen;
    private javax.swing.JCheckBox cbZeichenPrivat;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo defaultBindableReferenceCombo1;
    private javax.swing.Box.Filler fillBemerkungBodyLeft;
    private javax.swing.Box.Filler fillBeschriftungBodyLeft;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker1;
    private org.jdesktop.swingx.JXDatePicker jXDatePicker2;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschriftung;
    private javax.swing.JLabel lblGueltig;
    private javax.swing.JLabel lblGueltig1;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblPosition;
    private javax.swing.JLabel lblVerfuegungsnummer;
    private javax.swing.JLabel lblVerkehrszeichen;
    private de.cismet.tools.gui.RoundedPanel panBeschreibungBody;
    private javax.swing.JPanel panBeschreibungBodyLeft;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextArea txtBeschriftung;
    private javax.swing.JTextField txtVerfuegungsnummer;
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

        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        panBeschreibungBody = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungBodyLeft = new javax.swing.JPanel();
        lblPosition = new javax.swing.JLabel();
        lblVerfuegungsnummer = new javax.swing.JLabel();
        lblVerkehrszeichen = new javax.swing.JLabel();
        lblBeschriftung = new javax.swing.JLabel();
        fillBeschriftungBodyLeft = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        lblBemerkung = new javax.swing.JLabel();
        fillBemerkungBodyLeft = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel3 = new javax.swing.JPanel();
        cbStvo = new DefaultBindableReferenceCombo(mcVzkatStvo, true, false);
        cbVerkehrszeichen = new de.cismet.cids.editors.FastBindableReferenceCombo(
                verkehrszeichenSearch,
                verkehrszeichenSearch.getRepresentationPattern(),
                verkehrszeichenSearch.getRepresentationFields());
        jScrollPane1 = new javax.swing.JScrollPane();
        txtBeschriftung = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        lblIcon = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        defaultBindableReferenceCombo1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        cbZeichenPrivat = new javax.swing.JCheckBox();
        cbSchildPrivat = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        lblGueltig = new javax.swing.JLabel();
        jXDatePicker1 = new org.jdesktop.swingx.JXDatePicker();
        jXDatePicker2 = new org.jdesktop.swingx.JXDatePicker();
        lblGueltig1 = new javax.swing.JLabel();
        txtVerfuegungsnummer = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

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
        jButton3.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton3.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton3.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton3.setRequestFocusEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 4, 10);
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
        jButton4.setMaximumSize(new java.awt.Dimension(16, 16));
        jButton4.setMinimumSize(new java.awt.Dimension(16, 16));
        jButton4.setPreferredSize(new java.awt.Dimension(16, 16));
        jButton4.setRequestFocusEnabled(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 4, 10);
        panBeschreibungTitle.add(jButton4, gridBagConstraints);
        jButton4.setVisible(isEditable());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(jLabel2, gridBagConstraints);

        jLabel3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/circle.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jLabel3.text"));                                 // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 4, 0);
        panBeschreibungTitle.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(panBeschreibungTitle, gridBagConstraints);

        panBeschreibungBody.setCurve(0);
        panBeschreibungBody.setLayout(new java.awt.GridBagLayout());

        panBeschreibungBodyLeft.setOpaque(false);
        panBeschreibungBodyLeft.setLayout(new java.awt.GridBagLayout());

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

        lblVerfuegungsnummer.setFont(lblVerfuegungsnummer.getFont().deriveFont(
                lblVerfuegungsnummer.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVerfuegungsnummer,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblVerfuegungsnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblVerfuegungsnummer, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblVerkehrszeichen, gridBagConstraints);

        lblBeschriftung.setFont(lblBeschriftung.getFont().deriveFont(
                lblBeschriftung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschriftung,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblBeschriftung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblBeschriftung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBodyLeft.add(fillBeschriftungBodyLeft, gridBagConstraints);

        lblBemerkung.setFont(lblBemerkung.getFont().deriveFont(lblBemerkung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBemerkung,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(lblBemerkung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panBeschreibungBodyLeft.add(fillBemerkungBodyLeft, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel3.add(cbStvo, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
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
        jPanel3.add(cbVerkehrszeichen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(jPanel3, gridBagConstraints);

        txtBeschriftung.setColumns(20);
        txtBeschriftung.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschriftung}"),
                txtBeschriftung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtBeschriftung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(jScrollPane1, gridBagConstraints);

        txtBemerkung.setColumns(20);
        txtBemerkung.setRows(3);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panBeschreibungBodyLeft.add(jScrollPane2, gridBagConstraints);

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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panBeschreibungBodyLeft.add(lblIcon, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/down.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton2.text"));                              // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton2.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton2.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton2.setRequestFocusEnabled(false);
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

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/up.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.jButton1.text"));                            // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setMaximumSize(new java.awt.Dimension(24, 24));
        jButton1.setMinimumSize(new java.awt.Dimension(24, 24));
        jButton1.setPreferredSize(new java.awt.Dimension(24, 24));
        jButton1.setRequestFocusEnabled(false);
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panBeschreibungBodyLeft.add(jPanel1, gridBagConstraints);

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(defaultBindableReferenceCombo1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cbZeichenPrivat,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.cbZeichenPrivat.text")); // NOI18N
        cbZeichenPrivat.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_zeichen.privat}"),
                cbZeichenPrivat,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(cbZeichenPrivat, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            cbSchildPrivat,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.cbSchildPrivat.text")); // NOI18N
        cbSchildPrivat.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.privat}"),
                cbSchildPrivat,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panBeschreibungBodyLeft.add(cbSchildPrivat, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblGueltig.setFont(lblGueltig.getFont().deriveFont(lblGueltig.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGueltig,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblGueltig.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        jPanel5.add(lblGueltig, gridBagConstraints);

        jXDatePicker1.setMaximumSize(new java.awt.Dimension(150, 32));
        jXDatePicker1.setMinimumSize(new java.awt.Dimension(150, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gueltig_bis}"),
                jXDatePicker1,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel5.add(jXDatePicker1, gridBagConstraints);

        jXDatePicker2.setMaximumSize(new java.awt.Dimension(150, 32));
        jXDatePicker2.setMinimumSize(new java.awt.Dimension(150, 32));
        jXDatePicker2.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gueltig_von}"),
                jXDatePicker2,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(new SqlTimestampToUtilDateConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel5.add(jXDatePicker2, gridBagConstraints);

        lblGueltig1.setFont(lblGueltig1.getFont().deriveFont(lblGueltig1.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGueltig1,
            org.openide.util.NbBundle.getMessage(
                VzkatStandortSchildPanel.class,
                "VzkatStandortSchildPanel.lblGueltig1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
        jPanel5.add(lblGueltig1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfuegungsnummer}"),
                txtVerfuegungsnummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel5.add(txtVerfuegungsnummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBodyLeft.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panBeschreibungBody.add(panBeschreibungBodyLeft, gridBagConstraints);

        jPanel4.setBackground(java.awt.Color.gray);
        jPanel4.setMaximumSize(new java.awt.Dimension(5, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(5, 10));
        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(5, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibungBody.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panBeschreibungBody, gridBagConstraints);

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
            cbZeichenPrivat.setVisible(selectedZeichen.getProperty("privat") != null);
            cbSchildPrivat.setVisible(selectedZeichen.getProperty("privat") == null);
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

        RendererTools.makeReadOnly(cbZeichenPrivat);
        if (!editable) {
            RendererTools.makeReadOnly(defaultBindableReferenceCombo1);
            txtVerfuegungsnummer.setEditable(editable);
            RendererTools.makeReadOnly(cbVerkehrszeichen);
            RendererTools.makeReadOnly(cbStvo);
            RendererTools.makeReadOnly(txtBeschriftung);
            RendererTools.makeReadOnly(txtBemerkung);
            RendererTools.makeReadOnly(cbSchildPrivat);
        } else {
            StaticSwingTools.decorateWithFixedAutoCompleteDecorator(cbVerkehrszeichen);

            final JList pop = ((ComboPopup)cbVerkehrszeichen.getUI().getAccessibleChild(cbVerkehrszeichen, 0))
                        .getList();
            final JTextField txt = (JTextField)cbVerkehrszeichen.getEditor().getEditorComponent();
            cbVerkehrszeichen.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final Object selectedValue = pop.getSelectedValue();
                        txt.setText((selectedValue != null) ? String.valueOf(selectedValue) : "");
                    }
                });
        }
        lblGueltig.setVisible(editable);
        jXDatePicker1.setVisible(editable);
        lblGueltig1.setVisible(editable);
        jXDatePicker2.setVisible(editable);

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
                        cbStvo.reload();
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
        if (isEditable() && (this.cidsBean != null)) {
            this.cidsBean.removePropertyChangeListener(changeListener);
        }

        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            jLabel3.setVisible(MetaObject.NEW == cidsBean.getMetaObject().getStatus());
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
            bindingGroup.bind();

            if (isEditable()) {
                cidsBean.addPropertyChangeListener(changeListener);
            }
            refreshIcon(VzkatUtils.createZeichenKey((CidsBean)cidsBean.getProperty("fk_zeichen")));
        }
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
    private ImageIcon loadZeichenIcon(final String key) throws Exception {
//        final String urlString = String.format(ICON_URL_TEMPLATE, key);
//        final InputStream is = WebAccessManager.getInstance().doRequest(new URL(urlString));
        final InputStream is = getClass().getResourceAsStream(String.format(ICON_PATH_TEMPLATE, key));
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

    @Override
    public void dispose() {
        cbStvoActionListenerEnabled = false;
        iconLoadingWorker = null;
        setCidsBean(null);
    }
}
