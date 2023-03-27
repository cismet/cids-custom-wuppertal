/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Binding;

import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.GridBagConstraints;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.abfrage.AbstractAbfragePanel;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultBindableReferenceCombo;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = AbstractAbfragePanel.class)
public class AlboFlaecheSearchPanel extends AbstractAbfragePanel<AlboFlaecheSearch.Configuration> {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlboFlaecheSearchPanel.class);
    // End of variables declaration

    //~ Instance fields --------------------------------------------------------

    private final boolean showVorgaenge;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();
    private final Collection<AlboFlaecheArtSearchPanel> artInfoPanels = new ArrayList<>();

    private Bean bean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cbFlaechenstatus;
    private javax.swing.JComboBox<String> cbFlaechentyp;
    private javax.swing.JComboBox<String> cbFlaechenzuordnung;
    private javax.swing.JCheckBox cbOnlyVorgang;
    private javax.swing.JCheckBox cbVorgang;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JPanel pnlPruefung;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboFlaecheSearchPanel object.
     */
    public AlboFlaecheSearchPanel() {
        this(true, true);
    }

    /**
     * Creates a new AlboFlaecheSearchPanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AlboFlaecheSearchPanel(final boolean editable) {
        this(editable, false);
    }

    /**
     * Creates a new AlboFlaecheSearchPanel object.
     *
     * @param  editable       DOCUMENT ME!
     * @param  showVorgaenge  DOCUMENT ME!
     */
    public AlboFlaecheSearchPanel(final boolean editable, final boolean showVorgaenge) {
        super(editable);
        this.showVorgaenge = showVorgaenge;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        this.bean = new Bean();

        initComponents();

        final boolean editable = isEditable();

        jPanel7.setVisible(showVorgaenge);
        RendererTools.makeReadOnly(jRadioButton1, !editable);
        RendererTools.makeReadOnly(jRadioButton2, !editable);
        RendererTools.makeReadOnly(jRadioButton5, !editable);
        RendererTools.makeReadOnly(jRadioButton6, !editable);
        RendererTools.makeReadOnly(jCheckBox1, !editable);
        RendererTools.makeReadOnly(jTextField1, !editable);
        RendererTools.makeReadOnly(jTextField2, !editable);
        RendererTools.makeReadOnly(cbFlaechenstatus, !editable);
        RendererTools.makeReadOnly(cbFlaechentyp, !editable);
        RendererTools.makeReadOnly(cbFlaechenzuordnung, !editable);
        RendererTools.makeReadOnly(cbVorgang, !editable);
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Bean getBean() {
        return bean;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(600, 0),
                new java.awt.Dimension(600, 0),
                new java.awt.Dimension(600, 32767));
        pnlPruefung = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        cbFlaechentyp = new DefaultBindableReferenceCombo(ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "albo_flaechentyp",
                    getConnectionContext()),
                true,
                false);
        jLabel13 = new javax.swing.JLabel();
        cbFlaechenstatus = new DefaultBindableReferenceCombo(ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "albo_flaechenstatus",
                    getConnectionContext()),
                true,
                false);
        jLabel14 = new javax.swing.JLabel();
        cbFlaechenzuordnung = new DefaultBindableReferenceCombo(ClassCacheMultiple.getMetaClass(
                    CidsBeanSupport.DOMAIN_NAME,
                    "albo_flaechenzuordnung",
                    getConnectionContext()),
                true,
                false);
        jLabel3 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel4 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel7 = new javax.swing.JPanel();
        cbVorgang = new javax.swing.JCheckBox();
        cbOnlyVorgang = new javax.swing.JCheckBox();
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(300, 1),
                new java.awt.Dimension(400, 1),
                new java.awt.Dimension(500, 1));
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));

        filler2.setName("filler2"); // NOI18N

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        pnlPruefung.setBorder(javax.swing.BorderFactory.createTitledBorder("Suche nach Flächen mit:"));
        pnlPruefung.setName("pnlPruefung"); // NOI18N
        pnlPruefung.setOpaque(false);
        pnlPruefung.setLayout(new java.awt.GridBagLayout());

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("<html>Erhebungsnummer: <i>*");
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel1, gridBagConstraints);

        jTextField1.setName("jTextField1"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.erhebungsnummer}"),
                jTextField1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jTextField1, gridBagConstraints);

        jLabel2.setText("<html>Vorgangsnummer: <i>*");
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel2, gridBagConstraints);

        jTextField2.setName("jTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.vorgangsnummer}"),
                jTextField2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jTextField2, gridBagConstraints);

        jLabel15.setText("Flächentyp:");
        jLabel15.setName("jLabel15"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel15, gridBagConstraints);

        cbFlaechentyp.setName("cbFlaechentyp"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkTyp}"),
                cbFlaechentyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cbFlaechentyp, gridBagConstraints);

        jLabel13.setText("Flächenstatus:");
        jLabel13.setName("jLabel13"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel13, gridBagConstraints);

        cbFlaechenstatus.setName("cbFlaechenstatus"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkStatus}"),
                cbFlaechenstatus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cbFlaechenstatus, gridBagConstraints);

        jLabel14.setText("Flächenzuordnung:");
        jLabel14.setName("jLabel14"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel14, gridBagConstraints);

        cbFlaechenzuordnung.setName("cbFlaechenzuordnung"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${bean.fkZuordnung}"),
                cbFlaechenzuordnung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(cbFlaechenzuordnung, gridBagConstraints);

        jLabel3.setText("<html><i>(*) Das Zeichen '%' kann als Platzhalter verwendet werden.");
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlPruefung.add(jPanel3, gridBagConstraints);

        filler1.setName("filler1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlPruefung.add(filler1, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        pnlPruefung.add(filler3, gridBagConstraints);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jLabel4, gridBagConstraints);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("und");
        jRadioButton1.setContentAreaFilled(false);
        jRadioButton1.setName("jRadioButton1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jRadioButton1, gridBagConstraints);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("oder");
        jRadioButton2.setContentAreaFilled(false);
        jRadioButton2.setName("jRadioButton2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel1.add(jRadioButton2, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        pnlPruefung.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(pnlPruefung, gridBagConstraints);
        pnlPruefung.getAccessibleContext().setAccessibleName("Verkehrszeichen");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Unterdrückte Flächen:"));
        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jCheckBox1.setText("unterdrückte Flächen mit einbeziehen");
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jCheckBox1, gridBagConstraints);

        jCheckBox2.setText("ausschließlich diese");
        jCheckBox2.setName("jCheckBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jCheckBox1,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                jCheckBox2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 2, 2);
        jPanel4.add(jCheckBox2, gridBagConstraints);

        filler9.setName("filler9"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(filler9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jPanel4, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Vorgänge"));
        jPanel7.setName("jPanel7"); // NOI18N
        jPanel7.setLayout(new java.awt.GridBagLayout());

        cbVorgang.setText("Vorgänge mit einbeziehen");
        cbVorgang.setMinimumSize(new java.awt.Dimension(291, 23));
        cbVorgang.setName("cbVorgang"); // NOI18N
        cbVorgang.setPreferredSize(new java.awt.Dimension(291, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel7.add(cbVorgang, gridBagConstraints);

        cbOnlyVorgang.setText("ausschließlich diese");
        cbOnlyVorgang.setName("cbOnlyVorgang"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                cbVorgang,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                cbOnlyVorgang,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 12, 2, 2);
        jPanel7.add(cbOnlyVorgang, gridBagConstraints);

        filler10.setName("filler10"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(filler10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jPanel7, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Flächenartspezifische Suche:"));
        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 5, 20);
        jPanel5.add(jPanel2, gridBagConstraints);

        jButton3.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/optionspanels/wunda_blau/add.png"))); // NOI18N
        jButton3.setName("jButton3");                                                                // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());

        jLabel5.setText("Bedingung hinzufügen");
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(jLabel5, gridBagConstraints);

        jPanel6.setName("jPanel6"); // NOI18N
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        filler8.setName("filler8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(filler8, gridBagConstraints);

        buttonGroup2.add(jRadioButton5);
        jRadioButton5.setSelected(true);
        jRadioButton5.setText("und");
        jRadioButton5.setContentAreaFilled(false);
        jRadioButton5.setName("jRadioButton5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jRadioButton5, gridBagConstraints);

        buttonGroup2.add(jRadioButton6);
        jRadioButton6.setText("oder");
        jRadioButton6.setContentAreaFilled(false);
        jRadioButton6.setName("jRadioButton6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jRadioButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(jPanel5, gridBagConstraints);

        filler5.setName("filler5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(filler5, gridBagConstraints);

        filler7.setName("filler7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler7, gridBagConstraints);

        filler6.setName("filler6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(filler6, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        artInfoPanels.add(new AlboFlaecheArtSearchPanel(this, isEditable()));
        refreshArtInfoPanels();
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public AlboFlaecheSearch.Configuration createConfiguration() {
        final AlboFlaecheSearch.Configuration configuration = new AlboFlaecheSearch.Configuration();
        configuration.setSearchModeMain(jRadioButton1.isSelected() ? AlboFlaecheSearch.SearchMode.AND
                                                                   : AlboFlaecheSearch.SearchMode.OR);
        configuration.setSearchModeArt(jRadioButton5.isSelected() ? AlboFlaecheSearch.SearchMode.AND
                                                                  : AlboFlaecheSearch.SearchMode.OR);
        configuration.setErhebungsNummer(jTextField1.getText());
        configuration.setVorgangSchluessel(jTextField2.getText());
        configuration.setStatusSchluessel((cbFlaechenstatus.getSelectedItem() instanceof CidsBean)
                ? (String)((CidsBean)cbFlaechenstatus.getSelectedItem()).getProperty("schluessel") : null);
        configuration.setTypSchluessel((cbFlaechentyp.getSelectedItem() instanceof CidsBean)
                ? (String)((CidsBean)cbFlaechentyp.getSelectedItem()).getProperty("schluessel") : null);
        configuration.setZuordnungSchluessel((cbFlaechenzuordnung.getSelectedItem() instanceof CidsBean)
                ? (String)((CidsBean)cbFlaechenzuordnung.getSelectedItem()).getProperty("schluessel") : null);
        configuration.setArtInfos(createArtInfos());
        configuration.setUnterdrueckt(jCheckBox2.isSelected() ? Boolean.TRUE
                                                              : ((!jCheckBox1.isSelected()) ? Boolean.FALSE : null));
        configuration.setVorgaenge(cbOnlyVorgang.isSelected() ? Boolean.TRUE
                                                              : ((cbVorgang.isSelected()) ? Boolean.FALSE : null));
        return configuration;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AlboFlaecheSearch getServerSearch(final Geometry geometry) {
        final Geometry transformedBoundingBox;
        if (geometry != null) {
            transformedBoundingBox = CrsTransformer.transformToDefaultCrs(geometry);
            transformedBoundingBox.setSRID(CismapBroker.getInstance().getDefaultCrsAlias());
        } else {
            transformedBoundingBox = null;
        }

        final AlboFlaecheSearch search = new AlboFlaecheSearch(createConfiguration());
        search.setGeometry(geometry);
        return search;
    }

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
     * DOCUMENT ME!
     *
     * @param  configuration  DOCUMENT ME!
     */
    @Override
    public void initFromConfiguration(final AlboFlaecheSearch.Configuration configuration) {
        this.bean = new Bean();
        artInfoPanels.clear();
        if (configuration != null) {
            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        return getSchluesselBean(
                                "albo_flaechenstatus",
                                configuration.getStatusSchluessel(),
                                getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            bean.setFkStatus(get());
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
                                "albo_flaechenzuordnung",
                                configuration.getZuordnungSchluessel(),
                                getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            bean.setFkZuordnung(get());
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
                                "albo_flaechentyp",
                                configuration.getTypSchluessel(),
                                getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            bean.setFkTyp(get());
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                        rebind();
                    }
                }.execute();

            bean.setErhebungsnummer(configuration.getErhebungsNummer());
            bean.setVorgangsnummer(configuration.getVorgangSchluessel());
            buttonGroup1.setSelected(jRadioButton1.getModel(),
                AlboFlaecheSearch.SearchMode.AND.equals(configuration.getSearchModeMain()));
            buttonGroup1.setSelected(jRadioButton2.getModel(),
                AlboFlaecheSearch.SearchMode.OR.equals(configuration.getSearchModeMain()));
            buttonGroup2.setSelected(jRadioButton5.getModel(),
                AlboFlaecheSearch.SearchMode.AND.equals(configuration.getSearchModeArt()));
            buttonGroup2.setSelected(jRadioButton6.getModel(),
                AlboFlaecheSearch.SearchMode.OR.equals(configuration.getSearchModeArt()));

            if (configuration.getArtInfos() != null) {
                for (final AlboFlaecheSearch.ArtInfo artInfo : configuration.getArtInfos()) {
                    final AlboFlaecheArtSearchPanel artInfoPanel = new AlboFlaecheArtSearchPanel(this, isEditable());
                    artInfoPanel.initFromArtInfo(artInfo);
                    artInfoPanels.add(artInfoPanel);
                }
            }

            jCheckBox1.setSelected(!Boolean.FALSE.equals(configuration.getUnterdrueckt()));
            jCheckBox2.setSelected(Boolean.TRUE.equals(configuration.getUnterdrueckt()));
        }

        rebind();
        refreshArtInfoPanels();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tablename          DOCUMENT ME!
     * @param   schluessel         DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean getSchluesselBean(final String tablename,
            final String schluessel,
            final ConnectionContext connectionContext) {
        if ((schluessel == null) || (tablename == null)) {
            return null;
        }
        try {
            final String query = String.format(
                    "SELECT (SELECT id FROM cs_class WHERE table_name ilike '%1$s') AS class_id, %1$s.id AS id FROM %1$s WHERE %1$s.schluessel = '%2$s';",
                    tablename,
                    schluessel);

            final MetaObject[] mos = SessionManager.getProxy().getMetaObjectByQuery(query, 0, connectionContext);
            final CidsBean schluesselBean = ((mos != null) && (mos.length > 0) && (mos[0] != null)) ? mos[0].getBean()
                                                                                                    : null;
            return schluesselBean;
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshArtInfoPanels() {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jPanel2.removeAll();
                    int count = 0;
                    for (final AlboFlaecheArtSearchPanel artInfoPanel : artInfoPanels) {
                        final int artNumber = ++count;
                        artInfoPanel.setName(String.format("artPanel%d", artNumber)); // NOI18N

                        final JPanel panel = new JPanel();
                        panel.setOpaque(false);
                        panel.setBorder(
                            javax.swing.BorderFactory.createTitledBorder(
                                String.format("Flächenart-Bedingung %d", artNumber)));
                        panel.setLayout(new java.awt.GridBagLayout());

                        {
                            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
                            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                            gridBagConstraints.weightx = 1.0;
                            gridBagConstraints.weighty = 1.0;
                            panel.add(artInfoPanel, gridBagConstraints);
                        }

                        {
                            final GridBagConstraints gridBagConstraints = new GridBagConstraints();
                            gridBagConstraints.gridx = 0;
                            gridBagConstraints.gridy = -1;
                            gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
                            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                            gridBagConstraints.weightx = 1.0;
                            gridBagConstraints.weighty = 1.0;
                            jPanel2.add(panel, gridBagConstraints);
                        }
                    }
                    revalidate();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<AlboFlaecheSearch.ArtInfo> createArtInfos() {
        final Collection<AlboFlaecheSearch.ArtInfo> artInfos = new ArrayList<>();
        for (final Component comp : jPanel2.getComponents()) {
            if (comp instanceof JPanel) {
                for (final Component subComp : ((JPanel)comp).getComponents()) {
                    if (subComp instanceof AlboFlaecheArtSearchPanel) {
                        artInfos.add(((AlboFlaecheArtSearchPanel)subComp).createArtInfo());
                    }
                }
            }
        }
        return artInfos;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  artPanel  DOCUMENT ME!
     */
    public void removeArtPanel(final AlboFlaecheArtSearchPanel artPanel) {
        artInfoPanels.remove(artPanel);

        refreshArtInfoPanels();
    }

    @Override
    public String getTableName() {
        return "albo_flaeche";
    }

    @Override
    public void initFromConfiguration(final Object configuration) {
        initFromConfiguration((AlboFlaecheSearch.Configuration)configuration);
    }

    @Override
    public ObjectMapper getConfigurationMapper() {
        return AlboFlaecheSearch.OBJECT_MAPPER;
    }

    @Override
    public AlboFlaecheSearch.Configuration readConfiguration(final String confJson) throws Exception {
        return getConfigurationMapper().readValue(
                confJson,
                AlboFlaecheSearch.Configuration.class);
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

        private String erhebungsnummer;
        private String vorgangsnummer;
        private CidsBean fkStatus;
        private CidsBean fkTyp;
        private CidsBean fkZuordnung;
        private Boolean andMain;
        private Boolean andArt;
    }
}
