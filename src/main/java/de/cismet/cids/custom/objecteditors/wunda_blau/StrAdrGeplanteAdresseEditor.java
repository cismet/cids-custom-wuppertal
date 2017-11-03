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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.text.Collator;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.*;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultBindableReferenceCombo;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;
import de.cismet.cids.editors.FastBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class StrAdrGeplanteAdresseEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(StrAdrGeplanteAdresseEditor.class);

    //~ Instance fields --------------------------------------------------------

    protected Object hausnr;
    private CidsBean cidsBean = null;
    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbAntragsteller;
    private javax.swing.JComboBox cbGeom;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbGrund;
    private javax.swing.JComboBox cbStrassenname;
    private de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhaben;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcAlkis;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcBauantrag;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcGeplant;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcHistorisch;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcVorhanden;
    private javax.swing.JFormattedTextField ftxHausnr;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JPanel panAdresse;
    private javax.swing.JPanel panFillerMitteWasDatum;
    private javax.swing.JPanel panFillerRechts;
    private javax.swing.JPanel panFillerRechtsAdresse;
    private javax.swing.JPanel panFillerRechtsHaupt;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUntenHaupt;
    private javax.swing.JPanel panHaupt;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panPreviewMap;
    private javax.swing.JPanel panWasDatum;
    private de.cismet.tools.gui.RoundedPanel rpKarte;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private javax.swing.JTextField txtAdr_zusatz;
    private javax.swing.JTextField txtBemerkung;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public StrAdrGeplanteAdresseEditor() {
        initComponents();
        ((DefaultCismapGeometryComboBoxEditor)cbGeom).setLocalRenderFeatureString("georeferenz");
    }

    /**
     * Creates a new StrAdrGeplanteAdresseEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public StrAdrGeplanteAdresseEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
        initComponents();
        noEdit();
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

        panFillerRechts = new javax.swing.JPanel();
        panFillerUnten = new javax.swing.JPanel();
        panHaupt = new javax.swing.JPanel();
        panAdresse = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtAdr_zusatz = new javax.swing.JTextField();
        cbStrassenname = new FastBindableReferenceCombo(
                "select s.id, s.name, s.strasse, s.name || ' (' || s.strasse || ')' as anzeige from str_adr_strasse s where s.strasse::int < 4000 order by s.name",
                "%1$2s",
                new String[] { "anzeige", "strasse" });
        ftxHausnr = new javax.swing.JFormattedTextField();
        lblSchluessel = new javax.swing.JLabel();
        if (isEditor) {
            jLabel3 = new javax.swing.JLabel();
        }
        if (isEditor) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        rpKarte = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel17 = new javax.swing.JLabel();
        panPreviewMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
        panFillerRechtsAdresse = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtBemerkung = new javax.swing.JTextField();
        panFillerRechtsHaupt = new javax.swing.JPanel();
        panFillerUntenHaupt = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        panWasDatum = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        dcGeplant = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcBauantrag = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcVorhanden = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcHistorisch = new de.cismet.cids.editors.DefaultBindableDateChooser();
        cbGrund = new DefaultBindableReferenceCombo(true);
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        dcAlkis = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cbVorhaben = new DefaultBindableReferenceCombo(false);
        cbAntragsteller = new DefaultBindableReferenceCombo(false);
        panFillerMitteWasDatum = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        panFillerRechts.setName(""); // NOI18N
        panFillerRechts.setOpaque(false);

        final javax.swing.GroupLayout panFillerRechtsLayout = new javax.swing.GroupLayout(panFillerRechts);
        panFillerRechts.setLayout(panFillerRechtsLayout);
        panFillerRechtsLayout.setHorizontalGroup(
            panFillerRechtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerRechtsLayout.setVerticalGroup(
            panFillerRechtsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 5.0E-4;
        add(panFillerRechts, gridBagConstraints);

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final javax.swing.GroupLayout panFillerUntenLayout = new javax.swing.GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(
            panFillerUntenLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 5.0E-4;
        add(panFillerUnten, gridBagConstraints);

        panHaupt.setOpaque(false);
        panHaupt.setLayout(new java.awt.GridBagLayout());

        panAdresse.setOpaque(false);
        panAdresse.setLayout(new java.awt.GridBagLayout());

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel12.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 15, 2, 2);
        panAdresse.add(jLabel12, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel13.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 2);
        panAdresse.add(jLabel13, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        panAdresse.add(jLabel1, gridBagConstraints);

        txtAdr_zusatz.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.adr_zusatz}"),
                txtAdr_zusatz,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtAdr_zusatz.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtAdr_zusatzFocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        panAdresse.add(txtAdr_zusatz, gridBagConstraints);

        // ((FastBindableReferenceCombo)cbStrassenname).setLocale(Locale.GERMAN);
        ((FastBindableReferenceCombo)cbStrassenname).setSorted(false);
        cbStrassenname.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_strasse_id}"),
                cbStrassenname,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbStrassenname.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    cbStrassennameMouseClicked(evt);
                }
            });
        cbStrassenname.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbStrassennameActionPerformed(evt);
                }
            });
        cbStrassenname.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    cbStrassennamePropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panAdresse.add(cbStrassenname, gridBagConstraints);

        ftxHausnr.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnr}"),
                ftxHausnr,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panAdresse.add(ftxHausnr, gridBagConstraints);

        lblSchluessel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSchluessel.setText("     ");
        lblSchluessel.setToolTipText("");
        lblSchluessel.setName(""); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.insets = new java.awt.Insets(7, 4, 6, 5);
        panAdresse.add(lblSchluessel, gridBagConstraints);

        if (isEditor) {
            jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        }
        if (isEditor) {
            jLabel3.setText(org.openide.util.NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.jLabel3.text")); // NOI18N
        }
        if (isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
            panAdresse.add(jLabel3, gridBagConstraints);
        }

        if (isEditor) {
            if (isEditor) {
                cbGeom.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
            }

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.georeferenz}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (isEditor) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
            panAdresse.add(cbGeom, gridBagConstraints);
        }

        rpKarte.setName(""); // NOI18N
        rpKarte.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel7.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel7.setLayout(new java.awt.GridBagLayout());

        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("Lage");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel7.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpKarte.add(semiRoundedPanel7, gridBagConstraints);

        panPreviewMap.setMinimumSize(new java.awt.Dimension(600, 600));
        panPreviewMap.setName(""); // NOI18N
        panPreviewMap.setPreferredSize(new java.awt.Dimension(500, 300));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        rpKarte.add(panPreviewMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 5, 0);
        panAdresse.add(rpKarte, gridBagConstraints);

        panFillerRechtsAdresse.setName(""); // NOI18N
        panFillerRechtsAdresse.setOpaque(false);

        final javax.swing.GroupLayout panFillerRechtsAdresseLayout = new javax.swing.GroupLayout(
                panFillerRechtsAdresse);
        panFillerRechtsAdresse.setLayout(panFillerRechtsAdresseLayout);
        panFillerRechtsAdresseLayout.setHorizontalGroup(
            panFillerRechtsAdresseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerRechtsAdresseLayout.setVerticalGroup(
            panFillerRechtsAdresseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        panAdresse.add(panFillerRechtsAdresse, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.9;
        panHaupt.add(panAdresse, gridBagConstraints);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel11.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panHaupt.add(jLabel11, gridBagConstraints);

        txtBemerkung.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBemerkung.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    txtBemerkungPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 0);
        panHaupt.add(txtBemerkung, gridBagConstraints);

        panFillerRechtsHaupt.setName(""); // NOI18N
        panFillerRechtsHaupt.setOpaque(false);

        final javax.swing.GroupLayout panFillerRechtsHauptLayout = new javax.swing.GroupLayout(panFillerRechtsHaupt);
        panFillerRechtsHaupt.setLayout(panFillerRechtsHauptLayout);
        panFillerRechtsHauptLayout.setHorizontalGroup(
            panFillerRechtsHauptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerRechtsHauptLayout.setVerticalGroup(
            panFillerRechtsHauptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        panHaupt.add(panFillerRechtsHaupt, gridBagConstraints);

        panFillerUntenHaupt.setName(""); // NOI18N
        panFillerUntenHaupt.setOpaque(false);

        final javax.swing.GroupLayout panFillerUntenHauptLayout = new javax.swing.GroupLayout(panFillerUntenHaupt);
        panFillerUntenHaupt.setLayout(panFillerUntenHauptLayout);
        panFillerUntenHauptLayout.setHorizontalGroup(
            panFillerUntenHauptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUntenHauptLayout.setVerticalGroup(
            panFillerUntenHauptLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panHaupt.add(panFillerUntenHaupt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        panHaupt.add(jSeparator1, gridBagConstraints);

        panWasDatum.setOpaque(false);
        panWasDatum.setLayout(new java.awt.GridBagLayout());

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel4.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel4.text")); // NOI18N
        jLabel4.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel4, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel5.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel5, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel6.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel6, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel7.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel7, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel8.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dat_geplant}"),
                dcGeplant,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcGeplant.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(dcGeplant, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dat_bauantrag}"),
                dcBauantrag,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcBauantrag.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(dcBauantrag, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dat_vorhanden}"),
                dcVorhanden,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcVorhanden.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(dcVorhanden, gridBagConstraints);

        dcHistorisch.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dat_historisch}"),
                dcHistorisch,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcHistorisch.getConverter());
        bindingGroup.addBinding(binding);

        dcHistorisch.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dcHistorischPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(dcHistorisch, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.grund}"),
                cbGrund,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        cbGrund.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(final java.awt.event.ItemEvent evt) {
                    cbGrundItemStateChanged(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(cbGrund, gridBagConstraints);
        cbGrund.getAccessibleContext().setAccessibleName("");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel14.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel14, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12));                                                    // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"))); // NOI18N
        jLabel2.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel2.text"));                                                   // NOI18N
        jLabel2.setToolTipText("Gebäude noch nicht eingemessen.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        panWasDatum.add(jLabel2, gridBagConstraints);

        dcAlkis.setEnabled(false);
        dcAlkis.setName(""); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dat_alkis}"),
                dcAlkis,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        dcAlkis.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    dcAlkisPropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(dcAlkis, gridBagConstraints);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel9.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        jLabel10.setText(org.openide.util.NbBundle.getMessage(
                StrAdrGeplanteAdresseEditor.class,
                "StrAdrGeplanteAdresseEditor.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panWasDatum.add(jLabel10, gridBagConstraints);

        cbVorhaben.setSelectedIndex(0);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vorhaben}"),
                cbVorhaben,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(cbVorhaben, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.antrag}"),
                cbAntragsteller,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 4, 2, 2);
        panWasDatum.add(cbAntragsteller, gridBagConstraints);

        panFillerMitteWasDatum.setName(""); // NOI18N
        panFillerMitteWasDatum.setOpaque(false);

        final javax.swing.GroupLayout panFillerMitteWasDatumLayout = new javax.swing.GroupLayout(
                panFillerMitteWasDatum);
        panFillerMitteWasDatum.setLayout(panFillerMitteWasDatumLayout);
        panFillerMitteWasDatumLayout.setHorizontalGroup(
            panFillerMitteWasDatumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerMitteWasDatumLayout.setVerticalGroup(
            panFillerMitteWasDatumLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 20);
        panWasDatum.add(panFillerMitteWasDatum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panHaupt.add(panWasDatum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        add(panHaupt, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dcAlkisPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dcAlkisPropertyChange
        alkisDatumIsSet();
    }                                                                              //GEN-LAST:event_dcAlkisPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtAdr_zusatzFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtAdr_zusatzFocusLost
        String zusatz = txtAdr_zusatz.getText().trim();
        zusatz = "   " + zusatz;
        txtAdr_zusatz.setText(zusatz);                                         // Drei Leerzeichen + Buchstabe
    }                                                                          //GEN-LAST:event_txtAdr_zusatzFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrassennameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbStrassennameActionPerformed
        setAddressNoEdit();
        if (cbStrassenname.getSelectedItem() != null) {
            lblSchluessel.setText(String.valueOf(
                    getOtherTableValue("str_adr_strasse", getMyWhere(cbStrassenname.getSelectedItem().toString()))
                                .getProperty("strasse")));
        }
    }                                                                                  //GEN-LAST:event_cbStrassennameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void dcHistorischPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_dcHistorischPropertyChange
        histDatumIsSet();
    }                                                                                   //GEN-LAST:event_dcHistorischPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrassennamePropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_cbStrassennamePropertyChange
        if (cbStrassenname.getSelectedItem() != null) {
            lblSchluessel.setText(String.valueOf(
                    getOtherTableValue("str_adr_strasse", getMyWhere(cbStrassenname.getSelectedItem().toString()))
                                .getProperty("strasse")));
        }
    }                                                                                     //GEN-LAST:event_cbStrassennamePropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBemerkungPropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_txtBemerkungPropertyChange
        checkEdit();
    }                                                                                   //GEN-LAST:event_txtBemerkungPropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbStrassennameMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_cbStrassennameMouseClicked
        final List<CidsBean> cblStrassen = this.getCidsBean().getBeanCollectionProperty("fk_strasse_id");
        final Collator umlautCollator = Collator.getInstance(Locale.GERMAN);
        umlautCollator.setStrength(Collator.SECONDARY);
        Collections.sort(cblStrassen, umlautCollator);
        cbStrassenname.setModel(new DefaultComboBoxModel(cblStrassen.toArray()));
    }                                                                              //GEN-LAST:event_cbStrassennameMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGrundItemStateChanged(final java.awt.event.ItemEvent evt) { //GEN-FIRST:event_cbGrundItemStateChanged
        grundIsSet();
    }                                                                          //GEN-LAST:event_cbGrundItemStateChanged
    /**
     * DOCUMENT ME!
     */
    private void checkEdit() {
        try {
            final CidsBean myCB = this.getCidsBean();
            if (myCB.getProperty("kein_edit") != null) {
                final String sEdit = myCB.getProperty("kein_edit").toString();
                // if (sEdit == "true"){
                if ("true".equalsIgnoreCase(sEdit)) {
                    noEdit();
                }
            }
        } catch (Exception e) {
            LOG.warn("Could not determine cidsBeans id. ", e);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void alkisDatumIsSet() {
        final Date myDate = dcAlkis.getDate();
        if (myDate != null) {
            noEdit();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void noEdit() {
        cbStrassenname.setEnabled(false);
        ftxHausnr.setEnabled(false);
        txtAdr_zusatz.setEnabled(false);
        dcGeplant.setEnabled(false);
        dcBauantrag.setEnabled(false);
        dcVorhanden.setEnabled(false);
        dcHistorisch.setEnabled(false);
        cbGrund.setEnabled(false);
        cbVorhaben.setEnabled(false);
        cbAntragsteller.setEnabled(false);
        // Geom nur im Editor
        if (this.isEditor) {
            cbGeom.setEnabled(false);
        } else {
            // Bemerkung darf im Editor immer geändert werden im Renderer nicht.
            txtBemerkung.setEnabled(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void grundIsSet() {
        final Date myDate = dcHistorisch.getDate();
        final Integer iGrund = cbGrund.getSelectedIndex();
        if (myDate != null) {
            if (iGrund != -1) {
                noEdit();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void histDatumIsSet() {
        final Date myDate = dcHistorisch.getDate();
        if (myDate != null) {
            final Integer iGrund = cbGrund.getSelectedIndex();
            if (iGrund != -1) {
                noEdit();
            } else {
                dcGeplant.setEnabled(false);
                dcBauantrag.setEnabled(false);
                dcVorhanden.setEnabled(false);
                // Geom existiert nur bei Editor
                if (this.isEditor) {
                    cbGeom.setEnabled(false);
                }
            }
        } else {
            final Date alkisDate = dcAlkis.getDate();
            try {
                boolean booledit = true;
                final CidsBean myCB = this.getCidsBean();
                if (myCB.getProperty("kein_edit") != null) {
                    final String sEdit = myCB.getProperty("kein_edit").toString();
                    // if (sEdit == "true"){
                    if ("true".equalsIgnoreCase(sEdit)) {
                        booledit = false;
                    }
                }
                // &es muss ein Editor sein
                if (booledit && this.isEditor) {
                    if (alkisDate == null) {
                        dcGeplant.setEnabled(true);
                        dcBauantrag.setEnabled(true);
                        dcVorhanden.setEnabled(true);
                        cbGeom.setEnabled(true);
                    }
                }
            } catch (Exception e) {
                // LOG.warn("Could not determine cidsBeans id. ", e);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setAddressNoEdit() {
        final CidsBean myCidsBean = this.getCidsBean();
        try {
            if (myCidsBean != null) {
                if (myCidsBean.getMetaObject().getStatus() != MetaObject.NEW) {
                    cbStrassenname.setEnabled(false);
                    ftxHausnr.setEnabled(false);
                    txtAdr_zusatz.setEnabled(false);
                    // cbGeom.setEnabled(false);
                }
            }
        } catch (Exception e) {
            LOG.warn("Could not determine cidsBeans in setAddressNoEdit. ", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   myTable  DOCUMENT ME!
     * @param   myWhere  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private CidsBean getOtherTableValue(final String myTable, final String myWhere) {
        try {
            final MetaClass myClass = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    myTable);
            if (myClass != null) {
                final StringBuffer myQuery = new StringBuffer("select ").append(myClass.getId())
                            .append(", ")
                            .append(myClass.getPrimaryKey())
                            .append(" from ")
                            .append(myClass.getTableName())
                            .append(myWhere);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("SQL: myQuery:" + myQuery.toString());
                }
                final MetaObject[] myMetaObject;
                try {
                    myMetaObject = SessionManager.getProxy().getMetaObjectByQuery(myQuery.toString(), 0);
                    if (myMetaObject.length > 0) {
                        return myMetaObject[0].getBean();
                    }
                } catch (ConnectionException ex) {
                    LOG.error(ex, ex);
                }
            }
        } catch (Exception ex) {
            LOG.error(myWhere + " kann nicht geladen werden in getOtherTableValue.", ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   myWhere  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getMyWhere(final String myWhere) {
        return " where name ilike '" + myWhere + "'";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkDate     DOCUMENT ME!
     * @param   welchesDatum  DOCUMENT ME!
     * @param   fehlerFrueh   DOCUMENT ME!
     * @param   fehlerSpaet   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StringBuilder checkDateRange(final Date checkDate,
            final String welchesDatum,
            final String fehlerFrueh,
            final String fehlerSpaet) {
        final StringBuilder errorMessage = new StringBuilder();
        final LocalDate ld;
        // date range
        final LocalDate fruehDatum = LocalDate.of(1999, 1, 1);
        final LocalDate jetztDatum = LocalDate.now();
        final LocalDate spaetDatum = jetztDatum.plusDays(100);

        if (checkDate != null) {
            ld = checkDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (ld.isBefore(fruehDatum)) {
                LOG.warn("Wrong '" + welchesDatum + "' specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        StrAdrGeplanteAdresseEditor.class,
                        "StrAdrGeplanteAdresseEditor.prepareForSave()."
                                + fehlerFrueh));
            }
            if (ld.isAfter(spaetDatum)) {
                LOG.warn("Wrong '" + welchesDatum + "' specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(
                        StrAdrGeplanteAdresseEditor.class,
                        "StrAdrGeplanteAdresseEditor.prepareForSave()."
                                + fehlerSpaet));
            }
        }
        return errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   controlValue   DOCUMENT ME!
     * @param   welchesObject  DOCUMENT ME!
     * @param   fehler         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StringBuilder noSelectedItem(final Object controlValue, final String welchesObject, final String fehler) {
        final StringBuilder errorMessage = new StringBuilder();

        if (controlValue == null) {
            LOG.warn("No '" + welchesObject + "' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave()."
                            + fehler));
        }
        return errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dateBefore  DOCUMENT ME!
     * @param   dateAfter   DOCUMENT ME!
     * @param   welcheDati  DOCUMENT ME!
     * @param   fehler      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StringBuilder getDateBefore(final Date dateBefore,
            final Date dateAfter,
            final String welcheDati,
            final String fehler) {
        final StringBuilder errorMessage = new StringBuilder();

        if (dateBefore.before(dateAfter)) {
            LOG.warn("Wrong '" + welcheDati + "' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave()."
                            + fehler));
        }
        return errorMessage;
    }

    @Override
    public boolean prepareForSave() {
        // return checkHausnummer();
        final StringBuilder errorMessage = new StringBuilder();

        // Hausnummer
        if ((ftxHausnr.getText() == null) || ftxHausnr.getText().trim().isEmpty()) {
            LOG.warn("No 'hausnr' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().noHausnr"));
        } else if (ftxHausnr.getText().length() > 3) {
            LOG.warn("Property 'hausnr' is too long. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().tooLongHausnr"));
        }

        // Zusatz
        if ((txtAdr_zusatz.getText() == null) || txtAdr_zusatz.getText().trim().isEmpty()) {
            txtAdr_zusatz.setText("    "); // Vier Leerzeichen
        } else if (txtAdr_zusatz.getText().trim().matches("[a-z]") && (txtAdr_zusatz.getText().trim().length() == 1)) {
            String zusatz = txtAdr_zusatz.getText().trim();
            zusatz = "   " + zusatz;
            txtAdr_zusatz.setText(zusatz); // Drei Leerzeichen + Buchstabe
        } else {
            LOG.warn("No 'zusatz' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().wrongZusatz"));
        }

        // Vorhaben
        errorMessage.append(noSelectedItem(cbVorhaben.getSelectedItem(), "vorhaben", "noVorhaben"));

        // Antrag
        errorMessage.append(noSelectedItem(cbAntragsteller.getSelectedItem(), "antrag", "noAntrag"));

        // Strasse
        errorMessage.append(noSelectedItem(cbStrassenname.getSelectedItem(), "strasse", "noStrasse"));

        // geom
        if ((cbGeom.getSelectedItem() == null) || cbGeom.getSelectedItem().toString().trim().isEmpty()) {
            LOG.warn("No 'geom' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().noGeom"));
        }

        // geplant
        errorMessage.append(checkDateRange(
                dcGeplant.getDate(),
                "geplant date",
                "datFalschGeplantFrueh",
                "datFalschGeplantSpaet"));

        // bauantrag
        errorMessage.append(checkDateRange(
                dcBauantrag.getDate(),
                "bauantrag date",
                "datFalschBauantragFrueh",
                "datFalschBauantragSpaet"));

        // vorhanden
        errorMessage.append(checkDateRange(
                dcVorhanden.getDate(),
                "vorhanden date",
                "datFalschVorhandenFrueh",
                "datFalschVorhandenSpaet"));

        // historisch
        errorMessage.append(checkDateRange(
                dcHistorisch.getDate(),
                "historisch date",
                "datFalschHistorischFrueh",
                "datFalschHistorischSpaet"));

        // date geplant, bauantrag, vorhanden
        if ((dcGeplant.getDate() == null) && (dcBauantrag.getDate() == null) && (dcVorhanden.getDate() == null)) {
            LOG.warn("No 'date' specified. Skip persisting.");
            errorMessage.append(NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().noDate"));
        } else { // Datumsangaben in richtiger Reihenfolge
            if ((dcGeplant.getDate() != null) && (dcBauantrag.getDate() != null)) {
                errorMessage.append(getDateBefore(
                        dcBauantrag.getDate(),
                        dcGeplant.getDate(),
                        "date (geplant-bauantrag)",
                        "datFalschGeplantBauantrag"));
            }
            if ((dcGeplant.getDate() != null) && (dcVorhanden.getDate() != null)) {
                errorMessage.append(getDateBefore(
                        dcVorhanden.getDate(),
                        dcGeplant.getDate(),
                        "date (geplant-vorhanden)",
                        "datFalschGeplantVorhanden"));
            }
            if ((dcBauantrag.getDate() != null) && (dcVorhanden.getDate() != null)) {
                errorMessage.append(getDateBefore(
                        dcVorhanden.getDate(),
                        dcBauantrag.getDate(),
                        "date (bauantrag-vorhanden)",
                        "datFalschBauantragVorhanden"));
            }
        }

        // date historisch
        if (dcHistorisch.getDate() != null) {
            if (dcGeplant.getDate() != null) {
                errorMessage.append(getDateBefore(
                        dcHistorisch.getDate(),
                        dcGeplant.getDate(),
                        "date (geplant-historisch)",
                        "datFalschGeplantHistorisch"));
            }
            if (dcVorhanden.getDate() != null) {
                errorMessage.append(getDateBefore(
                        dcHistorisch.getDate(),
                        dcVorhanden.getDate(),
                        "date (vorhanden-historisch)",
                        "datFalschVorhandenHistorisch"));
            }
            if (dcBauantrag.getDate() != null) {
                errorMessage.append(getDateBefore(
                        dcHistorisch.getDate(),
                        dcBauantrag.getDate(),
                        "date (bauantrag-historisch)",
                        "datFalschBauantragHistorisch"));
            }
            // grund muss ausgewählt werden
            errorMessage.append(noSelectedItem(cbGrund.getSelectedItem(), "grund", "datHistorischGrund"));
        }

        // grund ausgewaehlt
        if (((cbGrund.getSelectedItem() != null))) {
            errorMessage.append(noSelectedItem(dcHistorisch.getDate(), "historisch", "datGrundHistorisch"));
        }

        // Beim Speichern einer nicht historischen neuen Adresse
        try {
            final CidsBean myCB = this.getCidsBean();
            // Adresse bereits vorhanden
            if (myCB.getMetaObject().getStatus() == MetaObject.NEW) {
                if (dcHistorisch.getDate() == null) {
                    if (cbStrassenname.getSelectedItem() != null) {
                        final String myStrasse = getOtherTableValue(
                                    "str_adr_strasse",
                                    getMyWhere(cbStrassenname.getSelectedItem().toString())).getProperty("id")
                                    .toString();
                        final String myHausnummer = ftxHausnr.getText();
                        final String myZusatz = txtAdr_zusatz.getText().trim();
                        String myQuery = null;

                        myQuery = " where fk_strasse_id = " + myStrasse + " and hausnr = " + myHausnummer
                                    + " and trim(adr_zusatz) ilike '" + myZusatz + "' and dat_historisch is null";

                        if (getOtherTableValue("str_adr_geplante_adresse", myQuery) != null) {
                            LOG.warn("Not unique 'adress' specified. Skip persisting.");
                            errorMessage.append(NbBundle.getMessage(
                                    StrAdrGeplanteAdresseEditor.class,
                                    "StrAdrGeplanteAdresseEditor.prepareForSave().adresseVorhanden"));
                        }
                    }
                }
            } else {
                // Beim Speichern eines ehemals historischen als nicht historisches
                final String myId = String.valueOf(myCB.getProperty("id"));
                final String myQuery = " where id = " + myId + " and dat_historisch is not null";
                final CidsBean dbBean = getOtherTableValue("str_adr_geplante_adresse", myQuery);

                if (dbBean != null) {
                    errorMessage.append(noSelectedItem(
                            dcHistorisch.getDate(),
                            "freies historisch",
                            "historischVorhanden"));
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().JOptionPane.message.prefix")
                        + errorMessage.toString()
                        + NbBundle.getMessage(
                            StrAdrGeplanteAdresseEditor.class,
                            "StrAdrGeplanteAdresseEditor.prepareForSave().JOptionPane.message.suffix"),
                NbBundle.getMessage(
                    StrAdrGeplanteAdresseEditor.class,
                    "StrAdrGeplanteAdresseEditor.prepareForSave().JOptionPane.title"),
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
        return true;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        // dispose();  Wenn Aufruf hier, dann wird ein neu gezeichnetes Polygon nicht erkannt.

        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb);
            panPreviewMap.initMap(cb, "georeferenz.geo_field");

            bindingGroup.bind();
            if (cb.getMetaObject().getStatus() == MetaObject.NEW) {
                // Defaultwerte setzen
                this.cidsBean.setProperty(
                    "antrag",
                    getOtherTableValue("str_adr_geplante_adresse_antrag", getMyWhere("Behörde")));
                this.cidsBean.setProperty(
                    "vorhaben",
                    getOtherTableValue("str_adr_geplante_adresse_vorhaben", getMyWhere("Neubau")));
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.isEditor) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public String getTitle() {
        return cidsBean.toString();
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
}
