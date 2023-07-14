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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.types.MetaClass;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Binding;

import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.utils.LongNumberConverter;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.AlboFlaecheMainStandortPanel;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialogEnabledFilter;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import static de.cismet.cids.custom.wunda_blau.search.AlboFlaecheSearchPanel.getSchluesselBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboFlaecheArtSearchPanel extends javax.swing.JPanel implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlboFlaecheArtSearchPanel.class);

    private static final DotDotDotCellRenderer DOTDOTDOT_CELL_RENDERER = new DotDotDotCellRenderer(60);

    //~ Instance fields --------------------------------------------------------

    private final AlboFlaecheSearchPanel parent;
    private MetaClass mcWirtschaftszweig;

    private final boolean editable;

    private Bean bean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbErhebungsklasse;
    private javax.swing.JComboBox<String> cbFlaechenart;
    private javax.swing.JComboBox<String> cbRclart;
    private javax.swing.JComboBox<String> cbStilllegung;
    private javax.swing.JComboBox<String> cbStofflicheart;
    private javax.swing.JComboBox<String> cbVerfuellkategorie;
    private javax.swing.JComboBox<CidsBean> cbWirtschaftszweig;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JFormattedTextField jFormattedTextField17;
    private javax.swing.JFormattedTextField jFormattedTextField18;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPanel pnlAltablagerung;
    private javax.swing.JPanel pnlArt;
    private javax.swing.JPanel pnlEmpty;
    private javax.swing.JPanel pnlRcl;
    private javax.swing.JPanel pnlStandort;
    private javax.swing.JPanel pnlStoffliche;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboFlaecheArtWindowSearch object.
     */
    public AlboFlaecheArtSearchPanel() {
        this(null, true);
    }
    /**
     * Creates new form AlboFlaecheArtWindowSearch.
     *
     * @param  parent    DOCUMENT ME!
     * @param  editable  DOCUMENT ME!
     */
    public AlboFlaecheArtSearchPanel(final AlboFlaecheSearchPanel parent, final boolean editable) {
        this.parent = parent;
        this.editable = editable;
        this.bean = new Bean();

        try {
            mcWirtschaftszweig = CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "ALBO_WIRTSCHAFTSZWEIG",
                    getConnectionContext());
        } catch (Exception ex) {
            LOG.warn(ex, ex);
        }
        initComponents();

        RendererTools.makeReadOnly(jFormattedTextField17, !editable);
        RendererTools.makeReadOnly(jFormattedTextField18, !editable);
        RendererTools.makeReadOnly(jComboBox1, !editable);
        RendererTools.makeReadOnly(jComboBox2, !editable);
        RendererTools.makeReadOnly(cbErhebungsklasse, !editable);
        RendererTools.makeReadOnly(cbFlaechenart, !editable);
        RendererTools.makeReadOnly(cbStofflicheart, !editable);
        RendererTools.makeReadOnly(cbRclart, !editable);
        RendererTools.makeReadOnly(cbStilllegung, !editable);
        RendererTools.makeReadOnly(cbVerfuellkategorie, !editable);
        RendererTools.makeReadOnly(cbWirtschaftszweig, !editable);

        if (editable) {
            cbWirtschaftszweig.setRenderer(new DotDotDotCellRenderer(60) {

                    @Override
                    public Component getListCellRendererComponent(final JList<?> list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        final Component result = super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus);

                        if (value instanceof DefaultBindableReferenceCombo.NullableItem) {
                            ((JLabel)result).setText(" ");
                        }

                        if ((result instanceof JLabel) && (value instanceof CidsBean)) {
                            final CidsBean bean = (CidsBean)value;

                            final Object schluessel = ((CidsBean)bean).getProperty("fk_erhebungsklasse.schluessel");
                            String tooltip = "";

                            if ((schluessel != null) && (schluessel.equals("0"))) {
                                tooltip = NbBundle.getMessage(
                                        AlboFlaecheMainStandortPanel.class,
                                        "AlboFlaecheMainStandortPanel.getTooltip().wz0");
                            } else if ((schluessel != null) && (schluessel.equals("4"))) {
                                tooltip = NbBundle.getMessage(
                                        AlboFlaecheMainStandortPanel.class,
                                        "AlboFlaecheMainStandortPanel.getTooltip().wz4");
                            }

                            if ((schluessel != null) && (schluessel.equals("0") || schluessel.equals("4"))) {
                                ((JLabel)result).setForeground(Color.GRAY);
                            } else {
                                ((JLabel)result).setForeground(Color.BLACK);
                            }
                            ((JLabel)result).setToolTipText(tooltip);
                        }

                        return result;
                    }
                });
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void rebind() {
        synchronized (bindingGroup) {
            try {
                for (final Binding binding : bindingGroup.getBindings()) {
                    if (binding.isBound()) {
                        binding.unbind();
                        binding.bind();
                    }
                }
            } catch (final Exception ex) {
                LOG.warn(ex, ex);
            }
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

        pnlStandort = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        cbWirtschaftszweig = new DefaultBindableScrollableComboBox(mcWirtschaftszweig, true, false);
        jButton1 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jFormattedTextField17 = new javax.swing.JFormattedTextField();
        jButton3 = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jFormattedTextField18 = new javax.swing.JFormattedTextField();
        jButton4 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlAltablagerung = new javax.swing.JPanel();
        cbStilllegung = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_stilllegung",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.NullableOption());
        jLabel19 = new javax.swing.JLabel();
        cbVerfuellkategorie = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_verfuellkategorie",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.NullableOption());
        jLabel20 = new javax.swing.JLabel();
        cbErhebungsklasse = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_erhebungsklasse",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.NullableOption());
        jLabel21 = new javax.swing.JLabel();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlStoffliche = new javax.swing.JPanel();
        cbStofflicheart = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_stofflicheart",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.NullableOption(null, "-"));
        jLabel24 = new javax.swing.JLabel();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlRcl = new javax.swing.JPanel();
        cbRclart = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_rclart",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.NullableOption(null, "-"));
        jLabel25 = new javax.swing.JLabel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        pnlEmpty = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        cbFlaechenart = new DefaultBindableReferenceCombo(
                new DefaultBindableReferenceCombo.MetaClassOption(
                    ClassCacheMultiple.getMetaClass(
                        CidsBeanSupport.DOMAIN_NAME,
                        "albo_flaechenart",
                        getConnectionContext())),
                new DefaultBindableReferenceCombo.CategorisedOption(" : ", true),
                new DefaultBindableReferenceCombo.SortingColumnOption("order_by"));
        pnlArt = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();

        pnlStandort.setOpaque(false);
        pnlStandort.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, "Wirtschaftszweig:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jLabel14, gridBagConstraints);

        cbWirtschaftszweig.setRenderer(DOTDOTDOT_CELL_RENDERER);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkWirtschaftszweig}"),
                cbWirtschaftszweig,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbWirtschaftszweig.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbWirtschaftszweigActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(cbWirtschaftszweig, gridBagConstraints);
        ((DefaultBindableReferenceCombo)cbWirtschaftszweig).setNullable(true);

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlStandort.add(jButton1, gridBagConstraints);
        jButton1.setVisible(editable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, "Jahr:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jLabel22, gridBagConstraints);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "exakt", "vor", "nach" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jComboBox1, gridBagConstraints);

        jFormattedTextField17.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.standortJahr}"),
                jFormattedTextField17,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(new LongNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jFormattedTextField17, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/emptytrash.png"))); // NOI18N
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setFocusPainted(false);
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlStandort.add(jButton3, gridBagConstraints);
        jButton3.setVisible(editable);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, "Dauer:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jLabel23, gridBagConstraints);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "exakt", "kürzer als", "länger als" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jComboBox2, gridBagConstraints);

        jFormattedTextField18.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.standortDauer}"),
                jFormattedTextField18,
                org.jdesktop.beansbinding.BeanProperty.create("value"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(new LongNumberConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStandort.add(jFormattedTextField18, gridBagConstraints);

        jButton4.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/emptytrash.png"))); // NOI18N
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setFocusPainted(false);
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlStandort.add(jButton4, gridBagConstraints);
        jButton4.setVisible(editable);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlStandort.add(filler1, gridBagConstraints);

        pnlAltablagerung.setOpaque(false);
        pnlAltablagerung.setLayout(new java.awt.GridBagLayout());

        cbStilllegung.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkStilllegung}"),
                cbStilllegung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(cbStilllegung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, "Ablagerungstyp:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(jLabel19, gridBagConstraints);

        cbVerfuellkategorie.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkVerfuellkategorie}"),
                cbVerfuellkategorie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(cbVerfuellkategorie, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, "Verfüllkategorie:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(jLabel20, gridBagConstraints);

        cbErhebungsklasse.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkErhebungsklasse}"),
                cbErhebungsklasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(cbErhebungsklasse, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, "Erhebungsklasse:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlAltablagerung.add(jLabel21, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlAltablagerung.add(filler6, gridBagConstraints);

        pnlStoffliche.setOpaque(false);
        pnlStoffliche.setLayout(new java.awt.GridBagLayout());

        cbStofflicheart.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkStofflicheArt}"),
                cbStofflicheart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStoffliche.add(cbStofflicheart, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, "Art der Bodenveränderung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlStoffliche.add(jLabel24, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlStoffliche.add(filler7, gridBagConstraints);

        pnlRcl.setOpaque(false);
        pnlRcl.setLayout(new java.awt.GridBagLayout());

        cbRclart.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkRclArt}"),
                cbRclart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlRcl.add(cbRclart, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, "RCL-Art:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        pnlRcl.add(jLabel25, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlRcl.add(filler8, gridBagConstraints);

        pnlEmpty.setOpaque(false);
        pnlEmpty.setLayout(new java.awt.GridBagLayout());

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, "Flächenart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jLabel13, gridBagConstraints);

        cbFlaechenart.setRenderer(DOTDOTDOT_CELL_RENDERER);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkFlaechenArt}"),
                cbFlaechenart,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbFlaechenart.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbFlaechenartActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(cbFlaechenart, gridBagConstraints);

        pnlArt.setOpaque(false);
        pnlArt.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(pnlArt, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/remove.png"))); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jButton2, gridBagConstraints);
        jButton2.setVisible(editable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final ComboBoxFilterDialogEnabledFilter filter = new AlboFlaecheMainStandortPanel.CustomWirtschaftszweigFilter(
                cbWirtschaftszweig,
                true);

        ComboBoxFilterDialog.showForCombobox(
            cbWirtschaftszweig,
            "Wirtschaftszweig auswählen",
            filter,
            getConnectionContext());
    } //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        parent.removeArtPanel(this);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */

    private void cbFlaechenartActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbFlaechenartActionPerformed
        refreshArtPanel();
        parent.revalidate();
    }                                                                                 //GEN-LAST:event_cbFlaechenartActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        jFormattedTextField17.setValue(null);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        jFormattedTextField18.setValue(null);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbWirtschaftszweigActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbWirtschaftszweigActionPerformed
        final Object selectedItem = cbWirtschaftszweig.getSelectedItem();

        if (selectedItem instanceof CidsBean) {
            final Object schluessel = ((CidsBean)selectedItem).getProperty("fk_erhebungsklasse.schluessel");

            if ((schluessel != null) && (schluessel.equals("0") || schluessel.equals("4"))) {
                cbWirtschaftszweig.setForeground(Color.GRAY);
            } else {
                cbWirtschaftszweig.setForeground(Color.BLACK);
            }
        }
    } //GEN-LAST:event_cbWirtschaftszweigActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void refreshArtPanel() {
        final Object selectedObject = cbFlaechenart.getSelectedItem();
        JPanel pnl = null;
        if (selectedObject instanceof CidsBean) {
            final String artSchluessel = (String)((CidsBean)selectedObject).getProperty("schluessel");
            if (artSchluessel != null) {
                switch (artSchluessel) {
                    case "altstandort":
                    case "betriebsstandort": {
                        pnl = pnlStandort;
                    }
                    break;
                    case "altablagerung": {
                        pnl = pnlAltablagerung;
                    }
                    break;
                    case "rcl": {
                        pnl = pnlRcl;
                    }
                    break;
                    case "stoffliche": {
                        pnl = pnlStoffliche;
                    }
                    break;
                    default: {
                        pnl = pnlEmpty;
                    }
                }
            }
        }
        pnlArt.removeAll();
        if (pnl != null) {
            pnlArt.add(jSeparator1, BorderLayout.NORTH);
            pnlArt.add(pnl, BorderLayout.CENTER);
        }
        pnlArt.repaint();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  artInfo  DOCUMENT ME!
     */
    public void initFromArtInfo(final AlboFlaecheSearch.ArtInfo artInfo) {
        this.bean = new Bean();

        if (artInfo != null) {
            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        return getSchluesselBean(
                                "albo_flaechenart",
                                artInfo.getFlaechenartSchluessel(),
                                getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            bean.setFkFlaechenArt(get());
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                        rebind();
                    }
                }.execute();
            if (artInfo instanceof AlboFlaecheSearch.AltablagerungInfo) {
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_stilllegung",
                                    ((AlboFlaecheSearch.AltablagerungInfo)artInfo).getStilllegungSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkStilllegung(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_verfuellkategorie",
                                    ((AlboFlaecheSearch.AltablagerungInfo)artInfo).getVerfuellkategorieSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkVerfuellkategorie(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_erhebungsklasse",
                                    ((AlboFlaecheSearch.AltablagerungInfo)artInfo).getErhebungsklasseSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkErhebungsklasse(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
            } else if (artInfo instanceof AlboFlaecheSearch.StandortInfo) {
                final Boolean jahrModus = ((AlboFlaecheSearch.StandortInfo)artInfo).getJahrModus();
                final Boolean dauerModus = ((AlboFlaecheSearch.StandortInfo)artInfo).getDauerModus();
                jComboBox1.setSelectedItem(Boolean.TRUE.equals(jahrModus)
                        ? "nach" : (Boolean.FALSE.equals(jahrModus) ? "vor" : "exakt"));
                jComboBox2.setSelectedItem(Boolean.TRUE.equals(dauerModus)
                        ? "länger als" : (Boolean.FALSE.equals(dauerModus) ? "kürzer als" : "exakt"));
                bean.setStandortDauer(((AlboFlaecheSearch.StandortInfo)artInfo).getDauer());
                bean.setStandortJahr(((AlboFlaecheSearch.StandortInfo)artInfo).getJahr());

                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_wirtschaftszweig",
                                    ((AlboFlaecheSearch.StandortInfo)artInfo).getWzSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkWirtschaftszweig(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
            } else if (artInfo instanceof AlboFlaecheSearch.RclInfo) {
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_rclart",
                                    ((AlboFlaecheSearch.RclInfo)artInfo).getArtSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkRclArt(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
            } else if (artInfo instanceof AlboFlaecheSearch.StofflicheInfo) {
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return getSchluesselBean(
                                    "albo_stofflicheart",
                                    ((AlboFlaecheSearch.StofflicheInfo)artInfo).getArtSchluessel(),
                                    getConnectionContext());
                        }

                        @Override
                        protected void done() {
                            try {
                                bean.setFkStofflicheArt(get());
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                            rebind();
                        }
                    }.execute();
            }
        }
        rebind();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AlboFlaecheSearch.ArtInfo createArtInfo() {
        final CidsBean selectedArt = (CidsBean)((DefaultBindableReferenceCombo)cbFlaechenart).getSelectedItem();
        if (selectedArt == null) {
            return null;
        } else {
            final String artSchluessel = (String)selectedArt.getProperty("schluessel");
            if (artSchluessel != null) {
                final AlboFlaecheSearch.ArtInfo artInfo;
                switch (artSchluessel) {
                    case "altstandort":
                    case "betriebsstandort": {
                        artInfo = new AlboFlaecheSearch.StandortInfo(artSchluessel);
                        final CidsBean wirtschaftszweig = (cbWirtschaftszweig.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbWirtschaftszweig.getSelectedItem() : null;
                        ((AlboFlaecheSearch.StandortInfo)artInfo).setWzSchluessel((wirtschaftszweig != null)
                                ? (String)wirtschaftszweig.getProperty("schluessel") : null);

                        final Integer jahr = (bean != null) ? bean.getStandortJahr() : null;
                        final Integer dauer = (bean != null) ? bean.getStandortDauer() : null;
                        final Boolean jahrModus = "nach".equals(jComboBox1.getSelectedItem())
                            ? Boolean.TRUE : ("vor".equals(jComboBox1.getSelectedItem()) ? Boolean.FALSE : null);
                        final Boolean dauerModus = "länger als".equals(jComboBox2.getSelectedItem())
                            ? Boolean.TRUE : ("kürzer als".equals(jComboBox2.getSelectedItem()) ? Boolean.FALSE : null);
                        ((AlboFlaecheSearch.StandortInfo)artInfo).setJahr(jahr);
                        ((AlboFlaecheSearch.StandortInfo)artInfo).setJahrModus(jahrModus);
                        ((AlboFlaecheSearch.StandortInfo)artInfo).setDauer(dauer);
                        ((AlboFlaecheSearch.StandortInfo)artInfo).setDauerModus(dauerModus);
                    }
                    break;
                    case "altablagerung": {
                        final CidsBean stilllegung = (cbStilllegung.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbStilllegung.getSelectedItem() : null;
                        final CidsBean verfuellkategorie = (cbVerfuellkategorie.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbVerfuellkategorie.getSelectedItem() : null;
                        final CidsBean erhebungsklasse = (cbErhebungsklasse.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbErhebungsklasse.getSelectedItem() : null;

                        artInfo = new AlboFlaecheSearch.AltablagerungInfo();
                        ((AlboFlaecheSearch.AltablagerungInfo)artInfo).setStilllegungSchluessel((stilllegung != null)
                                ? (String)stilllegung.getProperty("schluessel") : null);
                        ((AlboFlaecheSearch.AltablagerungInfo)artInfo).setVerfuellkategorieSchluessel(
                            (verfuellkategorie != null) ? (String)verfuellkategorie.getProperty("schluessel") : null);
                        ((AlboFlaecheSearch.AltablagerungInfo)artInfo).setErhebungsklasseSchluessel(
                            (erhebungsklasse != null) ? (String)erhebungsklasse.getProperty("schluessel") : null);
                    }
                    break;
                    case "rcl": {
                        final CidsBean rclart = (cbRclart.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbRclart.getSelectedItem() : null;

                        artInfo = new AlboFlaecheSearch.RclInfo();
                        ((AlboFlaecheSearch.RclInfo)artInfo).setArtSchluessel((rclart != null)
                                ? (String)rclart.getProperty("schluessel") : null);
                    }
                    break;
                    case "stoffliche": {
                        final CidsBean stofflicheart = (cbStofflicheart.getSelectedItem() instanceof CidsBean)
                            ? (CidsBean)cbStofflicheart.getSelectedItem() : null;

                        artInfo = new AlboFlaecheSearch.StofflicheInfo();
                        ((AlboFlaecheSearch.StofflicheInfo)artInfo).setArtSchluessel((stofflicheart != null)
                                ? (String)stofflicheart.getProperty(
                                    "schluessel") : null);
                    }
                    break;
                    default: {
                        artInfo = new AlboFlaecheSearch.ArtInfo(artSchluessel);
                    }
                }
                return artInfo;
            } else {
                return null;
            }
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return (parent != null) ? parent.getConnectionContext() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Bean getBean() {
        return bean;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    public class Bean {

        //~ Instance fields ----------------------------------------------------

        private CidsBean fkFlaechenArt;
        private CidsBean fkWirtschaftszweig;
        private CidsBean fkStilllegung;
        private CidsBean fkVerfuellkategorie;
        private CidsBean fkErhebungsklasse;
        private CidsBean fkRclArt;
        private CidsBean fkStofflicheArt;
        private Integer standortJahr;
        private Integer standortDauer;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class DotDotDotCellRenderer extends DefaultListCellRenderer {

        //~ Instance fields ----------------------------------------------------

        private final int maxLength;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DotDotDotCellRenderer object.
         *
         * @param  maxLength  DOCUMENT ME!
         */
        public DotDotDotCellRenderer(final int maxLength) {
            this.maxLength = maxLength;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if ((value instanceof DefaultBindableReferenceCombo.NullableItem) && (comp instanceof JLabel)) {
                ((JLabel)comp).setText(" ");

                return comp;
            } else if (value == null) {
                return new JLabel(" ");
            }

            if (comp instanceof JLabel) {
                final String text = ((JLabel)comp).getText();
                if (text != null) {
                    final String trimedText;
                    if (text.length() <= maxLength) {
                        trimedText = text;
                    } else {
                        trimedText = text.substring(0, maxLength - 3) + "...";
                    }

                    ((JLabel)comp).setText(trimedText);
                }
            }
            return comp;
        }
    }
}
