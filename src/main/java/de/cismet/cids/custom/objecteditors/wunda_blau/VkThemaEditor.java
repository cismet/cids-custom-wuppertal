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

import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class VkThemaEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final String TITLE_NEW_THEMA = "eine neues Thema anlegen...";
    private static final Logger LOG = Logger.getLogger(VkThemaEditor.class);

    public static final String FIELD__NAME = "name";   // Thema
    public static final String FIELD__ID = "id";       // Thema
    public static final String FIELD__FARBE = "farbe"; // Thema
    public static final String TABLE_NAME = "vk_thema";

    public static final String BUNDLE_NONAME = "VkThemaEditor.prepareForSave().noName";
    public static final String BUNDLE_NOICON = "VkThemaEditor.prepareForSave().noIcon";
    public static final String BUNDLE_NOCOLOR = "VkThemaEditor.prepareForSave().noFarbe";
    public static final String BUNDLE_WRONGCOLOR = "VkThemaEditor.prepareForSave().wrongFarbe";
    public static final String BUNDLE_DUPLICATENAME = "VkThemaEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_PANE_PREFIX = "VkThemaEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "VkThemaEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "VkThemaEditor.prepareForSave().JOptionPane.title";

    //~ Instance fields --------------------------------------------------------

    private SwingWorker worker_name;

    private Boolean redundantName = false;

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnChooseColor;
    private JButton btnMenAbortColor;
    private JButton btnMenOkColor;
    private JDialog dlgColor;
    private JColorChooser jColorChooser;
    private JLabel lblBeschreibung;
    private JLabel lblFarbe;
    private JLabel lblFuellung;
    private JLabel lblIcon;
    private JLabel lblName;
    private JLabel lblProzent;
    private JPanel panBeschreibung;
    private JPanel panContent;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panMenButtonsColor;
    private JPanel panName;
    private JScrollPane scpBeschreibung;
    JSpinner spFuellung;
    private JTextArea taBeschreibung;
    private JTextField txtFarbe;
    private JTextField txtFarbeZeigen;
    private JTextField txtIcon;
    private JTextField txtName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public VkThemaEditor() {
    }

    /**
     * Creates a new VkThemaEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public VkThemaEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        txtName.getDocument().addDocumentListener(new DocumentListener() {

                // Immer, wenn der Name geändert wird, wird dieser überprüft.
                @Override
                public void insertUpdate(final DocumentEvent e) {
                    checkName(FIELD__NAME);
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkName(FIELD__NAME);
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkName(FIELD__NAME);
                }
            });

        setReadOnly();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        dlgColor = new JDialog(StaticSwingTools.getParentFrame(this));
        jColorChooser = new JColorChooser();
        panMenButtonsColor = new JPanel();
        btnMenAbortColor = new JButton();
        btnMenOkColor = new JButton();
        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panFillerUnten1 = new JPanel();
        panName = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblIcon = new JLabel();
        txtIcon = new JTextField();
        lblFarbe = new JLabel();
        txtFarbe = new JTextField();
        btnChooseColor = new JButton();
        txtFarbeZeigen = new JTextField();
        lblFuellung = new JLabel();
        spFuellung = new JSpinner();
        lblProzent = new JLabel();
        lblBeschreibung = new JLabel();
        panBeschreibung = new JPanel();
        scpBeschreibung = new JScrollPane();
        taBeschreibung = new JTextArea();

        dlgColor.setTitle("Farbe auswählen");
        dlgColor.setModal(true);
        dlgColor.setSize(new Dimension(700, 500));
        dlgColor.getContentPane().setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        dlgColor.getContentPane().add(jColorChooser, gridBagConstraints);

        panMenButtonsColor.setLayout(new GridBagLayout());

        btnMenAbortColor.setText("Abbrechen");
        btnMenAbortColor.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenAbortColorActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsColor.add(btnMenAbortColor, gridBagConstraints);

        btnMenOkColor.setText("Ok");
        btnMenOkColor.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnMenOkColorActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenButtonsColor.add(btnMenOkColor, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        dlgColor.getContentPane().add(panMenButtonsColor, gridBagConstraints);

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(panFillerUnten, gridBagConstraints);

        panContent.setAutoscrolls(true);
        panContent.setMaximumSize(new Dimension(450, 2147483647));
        panContent.setMinimumSize(new Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(450, 961));
        panContent.setLayout(new GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panContent.add(panFillerUnten1, gridBagConstraints);

        panName.setOpaque(false);
        panName.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                txtName,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        lblIcon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblIcon.setText("Icon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblIcon, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.signatur}"),
                txtIcon,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtIcon, gridBagConstraints);

        lblFarbe.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFarbe.setText("Farbe:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblFarbe, gridBagConstraints);

        txtFarbe.setEnabled(false);
        txtFarbe.setMaximumSize(new Dimension(75, 2147483647));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.farbe}"),
                txtFarbe,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtFarbe, gridBagConstraints);

        btnChooseColor.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnChooseColor.setToolTipText(NbBundle.getMessage(VkThemaEditor.class, "TOOL_FARBE"));                 // NOI18N
        btnChooseColor.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    btnChooseColorActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(btnChooseColor, gridBagConstraints);

        txtFarbeZeigen.setEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtFarbeZeigen, gridBagConstraints);

        lblFuellung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblFuellung.setText("Flächenfüllung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblFuellung, gridBagConstraints);

        spFuellung.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spFuellung.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        spFuellung.setPreferredSize(new Dimension(75, 25));

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fuellung}"),
                spFuellung,
                BeanProperty.create("value"));
        binding.setSourceNullValue(0);
        binding.setSourceUnreadableValue(0);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(spFuellung, gridBagConstraints);

        lblProzent.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblProzent.setText("% Transparenz");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 7, 2, 5);
        panName.add(lblProzent, gridBagConstraints);

        lblBeschreibung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBeschreibung.setText("Beschreibung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblBeschreibung, gridBagConstraints);

        panBeschreibung.setOpaque(false);
        panBeschreibung.setLayout(new GridBagLayout());

        taBeschreibung.setColumns(20);
        taBeschreibung.setLineWrap(true);
        taBeschreibung.setRows(8);
        taBeschreibung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.beschreibung}"),
                taBeschreibung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBeschreibung.setViewportView(taBeschreibung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(scpBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(panBeschreibung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(25, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnChooseColorActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnChooseColorActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(VkThemaEditor.this), dlgColor, true);
    }                                                                   //GEN-LAST:event_btnChooseColorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbortColorActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenAbortColorActionPerformed
        dlgColor.setVisible(false);
    }                                                                     //GEN-LAST:event_btnMenAbortColorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOkColorActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_btnMenOkColorActionPerformed
        try {
            final java.awt.Color selColor = jColorChooser.getColor();
            final String hex_red = twoHexString(Integer.toHexString(selColor.getRed()));
            final String hex_green = twoHexString(Integer.toHexString(selColor.getGreen()));
            final String hex_blue = twoHexString(Integer.toHexString(selColor.getBlue()));
            this.getCidsBean().setProperty(FIELD__FARBE, '#' + hex_red + hex_green + hex_blue);
            txtFarbeZeigen.setBackground(selColor);
        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgColor.setVisible(false);
        }
    }                                                                  //GEN-LAST:event_btnMenOkColorActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   hex  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String twoHexString(final String hex) {
        if (hex.length() == 1) {
            return '0' + hex;
        }
        return hex;
    }

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkThemaEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkThemaEditor.class, BUNDLE_DUPLICATENAME));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // icon vorhanden
        try {
            if (txtIcon.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkThemaEditor.class, BUNDLE_NOICON));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // farbe vorhanden
        try {
            if (txtFarbe.getText().trim().isEmpty()) {
                LOG.warn("No color specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(VkThemaEditor.class, BUNDLE_NOCOLOR));
            } else {
                if (!((txtFarbe.getText().trim().length() == 7)
                                && txtFarbe.getText().trim().matches("#[a-fA-F0-9]{0,6}$"))) {
                    LOG.warn("Wrong color specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(VkThemaEditor.class, BUNDLE_WRONGCOLOR));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(VkThemaEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(VkThemaEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(VkThemaEditor.class,
                    BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return save;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            bindingGroup.bind();
            setFarbe();
        } catch (final Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtIcon);
            RendererTools.makeReadOnly(txtFarbe);
            RendererTools.makeReadOnly(taBeschreibung);
            RendererTools.jSpinnerShouldLookLikeLabel(spFuellung);
            RendererTools.makeDoubleSpinnerWithoutButtons(spFuellung, 0);
            RendererTools.makeReadOnly(spFuellung);
            btnChooseColor.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setFarbe() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__FARBE) != null)) {
            final String hexColor = getCidsBean().getProperty(FIELD__FARBE).toString();
            final int rot = Integer.valueOf(hexColor.substring(1, 3), 16);
            final int gruen = Integer.valueOf(hexColor.substring(3, 5), 16);
            final int blau = Integer.valueOf(hexColor.substring(5, 7), 16);
            final java.awt.Color showColor = new java.awt.Color(rot, gruen, blau);
            txtFarbeZeigen.setBackground(showColor);
        } else {
            txtFarbeZeigen.setBackground(new java.awt.Color(214, 217, 223));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     */
    private void checkName(final String field) {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(
            TABLE_NAME,
            " where "
                    + field
                    + " ilike '"
                    + txtName.getText().trim()
                    + "' and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public String getTitle() {
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_THEMA;
        } else {
            return cidsBean.toString();
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public BindingGroup getBindingGroup() {
        return bindingGroup;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tableName    DOCUMENT ME!
     * @param  whereClause  DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName, final String whereClause) {
        final SwingWorker<CidsBean, Void> worker = new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    return getOtherTableValue(tableName, whereClause, getConnectionContext());
                }

                @Override
                protected void done() {
                    final CidsBean check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            redundantName = check != null;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                }
            };
        if (worker_name != null) {
            worker_name.cancel(true);
        }
        worker_name = worker;
        worker_name.execute();
    }
}
