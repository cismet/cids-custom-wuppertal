/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.tools.MetaObjectCache;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.commons.lang.ObjectUtils;

import java.awt.Color;
import java.awt.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfSchluesseltabelleEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfSchluesseltabelleEditor.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;

    private final SortedSet<CidsBean> cidsBeans = new TreeSet(new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) && (o2 == null)) {
                        return 0;
                    } else if (o1 == null) {
                        return -1;
                    } else if (o2 == null) {
                        return 1;
                    }

                    int compare = 0;
                    final Integer i1 = (Integer)o1.getProperty("order_by");
                    final Integer i2 = (Integer)o2.getProperty("order_by");
                    final String n1 = (String)o1.getProperty("name");
                    final String n2 = (String)o2.getProperty("name");
                    if ((i1 != null) && (i2 != null)) {
                        compare = ObjectUtils.compare(i1, i2);
                    } else if (i1 != null) {
                        compare = ObjectUtils.compare(i1, -1);
                    } else if (i2 != null) {
                        compare = ObjectUtils.compare(-1, i2);
                    } else if ((n1 != null) && (n2 != null)) {
                        compare = ObjectUtils.compare(n1, n2);
                    } else if (n1 != null) {
                        compare = ObjectUtils.compare(n1, "");
                    } else if (n2 != null) {
                        compare = ObjectUtils.compare("", n2);
                    }
                    return (compare != 0) ? compare : ObjectUtils.compare(o1.hashCode(), o2.hashCode());
                }
            });

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JList<Object> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JLabel lblDefinition;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JLabel lblName2;
    private javax.swing.JLabel lblName3;
    private javax.swing.JLabel lblName4;
    private de.cismet.tools.gui.RoundedPanel panDetail;
    private de.cismet.tools.gui.RoundedPanel panDetail1;
    private de.cismet.tools.gui.RoundedPanel panDetail2;
    private javax.swing.JScrollPane scpDefinition;
    private javax.swing.JTextArea txtDefinition;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtName1;
    private javax.swing.JTextField txtName2;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PfSchluesseltabelleEditor object.
     */
    public PfSchluesseltabelleEditor() {
        this(true);
    }

    /**
     * Creates a new PfPotenzialflaecheEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public PfSchluesseltabelleEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

        RendererTools.makeReadOnly(txtName, !isEditable());
        RendererTools.makeReadOnly(txtName1, !isEditable());
        RendererTools.makeReadOnly(txtName2, !isEditable());
        RendererTools.makeReadOnly(txtDefinition, !isEditable());
        jCheckBox1.setEnabled(isEditable());
        jCheckBox2.setEnabled(isEditable());
        if (!isEditable()) {
            RendererTools.makeReadOnly(jList1);
        }
        jToggleButton2.setVisible(isEditable());
        jList1.setCellRenderer(new STCellRenderer());
        jButton3.setVisible(isEditable());
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

        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(300, 0),
                new java.awt.Dimension(300, 0),
                new java.awt.Dimension(600, 32767));
        jPanel6 = new javax.swing.JPanel();
        panDetail2 = new de.cismet.tools.gui.RoundedPanel();
        jPanel7 = new javax.swing.JPanel();
        lblName1 = new javax.swing.JLabel();
        txtName1 = new javax.swing.JTextField();
        lblName2 = new javax.swing.JLabel();
        txtName2 = new javax.swing.JTextField();
        jToggleButton2 = new javax.swing.JToggleButton();
        lblName3 = new javax.swing.JLabel();
        lblName4 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        panDetail1 = new de.cismet.tools.gui.RoundedPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new FixedSelectionList();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel4 = new javax.swing.JPanel();
        panDetail = new de.cismet.tools.gui.RoundedPanel();
        jPanel1 = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblDefinition = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        scpDefinition = new javax.swing.JScrollPane();
        txtDefinition = new javax.swing.JTextArea();
        jToggleButton1 = new javax.swing.JToggleButton();
        jButton4 = new javax.swing.JButton();
        filler = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(filler3, gridBagConstraints);

        jPanel6.setBorder(null);
        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        panDetail2.setLayout(new java.awt.GridBagLayout());

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lblName1, "Bezeichnung der Schlüsseltabelle:");
        lblName1.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblName1, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtName1.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtName1FocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtName1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblName2, "verbundene System-Tabelle:");
        lblName2.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblName2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.table_name}"),
                txtName2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtName2.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtName2FocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(txtName2, gridBagConstraints);

        jToggleButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock.png")));      // NOI18N
        jToggleButton2.setBorderPainted(false);
        jToggleButton2.setContentAreaFilled(false);
        jToggleButton2.setFocusPainted(false);
        jToggleButton2.setMaximumSize(new java.awt.Dimension(28, 28));
        jToggleButton2.setMinimumSize(new java.awt.Dimension(28, 28));
        jToggleButton2.setPreferredSize(new java.awt.Dimension(28, 28));
        jToggleButton2.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_edit.png"))); // NOI18N
        jToggleButton2.setRolloverSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_go.png")));   // NOI18N
        jToggleButton2.setSelectedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/lock_open.png"))); // NOI18N
        jToggleButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jToggleButton2, gridBagConstraints);
        jToggleButton1.setVisible(isEditable());

        org.openide.awt.Mnemonics.setLocalizedText(lblName3, "Definitionen anzeigen:");
        lblName3.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblName3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblName4, "Einträge im Editor erweiterbar:");
        lblName4.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblName4, gridBagConstraints);

        jCheckBox1.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.definition}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jCheckBox1, gridBagConstraints);

        jCheckBox2.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.erweiterbar}"),
                jCheckBox2,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jCheckBox2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panDetail2.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(panDetail2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel6, gridBagConstraints);
        jPanel4.setVisible(false);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        panDetail1.setLayout(new java.awt.GridBagLayout());

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jList1.setVisibleRowCount(20);
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    jList1ValueChanged(evt);
                }
            });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        panDetail1.add(jPanel5, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/up.png"))); // NOI18N
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setMaximumSize(new java.awt.Dimension(36, 36));
        jButton1.setMinimumSize(new java.awt.Dimension(36, 36));
        jButton1.setPreferredSize(new java.awt.Dimension(36, 36));
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        jPanel3.add(jButton1, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/clientutils/down.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.setFocusPainted(false);
        jButton2.setMaximumSize(new java.awt.Dimension(36, 36));
        jButton2.setMinimumSize(new java.awt.Dimension(36, 36));
        jButton2.setPreferredSize(new java.awt.Dimension(36, 36));
        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        jPanel3.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panDetail1.add(jPanel3, gridBagConstraints);
        jPanel3.setVisible(isEditable());

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, "neuen Eintrag erzeugen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        panDetail1.add(jButton3, gridBagConstraints);
        jButton3.setVisible(isEditable());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        panDetail1.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(panDetail1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel2, gridBagConstraints);

        jPanel4.setBorder(null);
        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(lblName, "Bezeichnung:");
        lblName.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel1.add(lblName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jList1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtName.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtNameFocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(txtName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(lblDefinition, "Definition:");
        lblDefinition.setName(PotenzialflaecheReportServerAction.Property.BEZEICHNUNG.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel1.add(lblDefinition, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(filler1, gridBagConstraints);

        txtDefinition.setColumns(20);
        txtDefinition.setLineWrap(true);
        txtDefinition.setRows(10);
        txtDefinition.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                jList1,
                org.jdesktop.beansbinding.ELProperty.create("${selectedElement.definition}"),
                txtDefinition,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpDefinition.setViewportView(txtDefinition);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(scpDefinition, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panDetail.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(panDetail, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, "zum Löschen markieren");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jToggleButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jToggleButton1, gridBagConstraints);
        jToggleButton1.setVisible(isEditable());

        org.openide.awt.Mnemonics.setLocalizedText(jButton4, "entfernen");
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jButton4, gridBagConstraints);
        jButton4.setVisible(isEditable());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jPanel4, gridBagConstraints);
        jPanel4.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(filler, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAeussereErschluessungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbAeussereErschluessungActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cbAeussereErschluessungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        final Object selectedObject = jList1.getSelectedValue();
        if (selectedObject instanceof CidsBean) {
            final CidsBean[] allBeans = getCidsBeans().toArray(new CidsBean[0]);
            CidsBean beforeBean = null;
            for (final CidsBean indexBean : allBeans) {
                if (selectedObject.equals(indexBean)) {
                    if (beforeBean != null) {
                        try {
                            final Integer beforeOrderBy = (Integer)beforeBean.getProperty("order_by");
                            final Integer indexOrderBy = (Integer)indexBean.getProperty("order_by");
                            indexBean.setProperty("order_by", beforeOrderBy);
                            beforeBean.setProperty("order_by", indexOrderBy);
                        } catch (final Exception ex) {
                            LOG.warn(ex, ex);
                        }
                        cidsBeans.remove(beforeBean);
                        cidsBeans.remove(indexBean);
                        cidsBeans.add(indexBean);
                        cidsBeans.add(beforeBean);
                        ((SortedListModel)jList1.getModel()).refresh();
                        jList1.setSelectedValue(selectedObject, true);
                        break;
                    }
                }
                beforeBean = indexBean;
            }
        }
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        final Object selectedObject = jList1.getSelectedValue();
        if (selectedObject instanceof CidsBean) {
            final CidsBean[] allBeans = getCidsBeans().toArray(new CidsBean[0]);
            CidsBean beforeBean = null;
            for (final CidsBean indexBean : allBeans) {
                if (beforeBean != null) {
                    try {
                        final Integer beforeOrderBy = (Integer)beforeBean.getProperty("order_by");
                        final Integer indexOrderBy = (Integer)indexBean.getProperty("order_by");
                        indexBean.setProperty("order_by", beforeOrderBy);
                        beforeBean.setProperty("order_by", indexOrderBy);
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                    }
                    cidsBeans.remove(beforeBean);
                    cidsBeans.remove(indexBean);
                    cidsBeans.add(indexBean);
                    cidsBeans.add(beforeBean);
                    ((SortedListModel)jList1.getModel()).refresh();
                    jList1.setSelectedValue(selectedObject, true);
                    break;
                }
                if (selectedObject.equals(indexBean)) {
                    beforeBean = indexBean;
                }
            }
        }
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton1ActionPerformed
        if (jList1.getSelectedValue() instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean)jList1.getSelectedValue();
            if (cidsBean.getMetaObject().getId() >= 0) {
                final boolean toDelete = jToggleButton1.isSelected();
                cidsBean.getMetaObject().setStatus(toDelete ? MetaObject.TO_DELETE : MetaObject.MODIFIED);
            } else {
                getCidsBeans().remove(cidsBean);
            }
            ((SortedListModel)jList1.getModel()).refresh();
        }
    }                                                                                  //GEN-LAST:event_jToggleButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    return getEffectiveMetaClass().getEmptyInstance(getConnectionContext()).getBean();
                }

                @Override
                protected void done() {
                    CidsBean cidsBean = null;
                    try {
                        cidsBean = (get());
                        cidsBean.setProperty("order_by", jList1.getModel().getSize() + 1);
                        getCidsBeans().add(cidsBean);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    ((SortedListModel)jList1.getModel()).refresh();
                    jList1.setSelectedValue(cidsBean, true);
                }
            }.execute();
    } //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1ValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_jList1ValueChanged
        if (jList1.getSelectedValue() instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean)jList1.getSelectedValue();
            jPanel4.setVisible(true);
            jToggleButton1.setVisible(isParentBean() && isEditable()
                        && (cidsBean.getMetaObject().getStatus() != MetaObject.NEW));
            jToggleButton1.setSelected(cidsBean.getMetaObject().getStatus() == 3);
            jButton4.setVisible(isEditable() && (cidsBean.getMetaObject().getStatus() == MetaObject.NEW));
        } else {
            final CidsBean cidsBean = (CidsBean)jList1.getSelectedValue();
        }
    }                                                                                 //GEN-LAST:event_jList1ValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        if (jList1.getSelectedValue() instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean)jList1.getSelectedValue();
            getCidsBeans().remove(cidsBean);
            ((SortedListModel)jList1.getModel()).refresh();
        }
        jList1.setSelectedIndex(-1);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtNameFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtNameFocusLost
        jList1.repaint();
    }                                                                    //GEN-LAST:event_txtNameFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtName1FocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtName1FocusLost
        // TODO add your handling code here:
    } //GEN-LAST:event_txtName1FocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtName2FocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtName2FocusLost
        // TODO add your handling code here:
    } //GEN-LAST:event_txtName2FocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jToggleButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jToggleButton2ActionPerformed
        txtName2.setEnabled(jToggleButton2.isSelected());
    }                                                                                  //GEN-LAST:event_jToggleButton2ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metaClass  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String createQuery(final MetaClass metaClass) {
        return createQuery(metaClass, "order_by", null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metaClass  DOCUMENT ME!
     * @param   orderBy    DOCUMENT ME!
     * @param   where      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String createQuery(final MetaClass metaClass, final String orderBy, final String where) {
        final String template = "SELECT %d, %s FROM %s WHERE %s ORDER BY %s";
        final String query = String.format(
                template,
                metaClass.getID(),
                metaClass.getPrimaryKey(),
                metaClass.getTableName(),
                (where != null) ? where : "TRUE",
                (orderBy != null) ? orderBy : metaClass.getPrimaryKey());
        return query;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private SortedSet<CidsBean> getCidsBeans() {
        return cidsBeans;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MetaClass getEffectiveMetaClass() {
        if (cidsBean != null) {
            try {
                final MetaClass metaClass = isParentBean()
                    ? CidsBean.getMetaClassFromTableName(
                        "WUNDA_BLAU",
                        (String)cidsBean.getProperty("table_name"),
                        getConnectionContext()) : getCidsBean().getMetaObject().getMetaClass();
                return metaClass;
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
        return null;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        jList1.setModel(new DefaultListModel<>());

        if (isParentBean()) {
            cidsBean.setArtificialChangeFlag(true);
        }
        jToggleButton1.setVisible(isParentBean());
        jToggleButton2.setSelected(false);
        txtName2.setEnabled(false);
        jButton3.setVisible(isEditable() && isParentBean());
        jPanel6.setVisible(isParentBean());

        bindingGroup.unbind();
        if (cidsBean != null) {
            jList1.setModel(new JList<>(new Object[] { "wird geladen..." }).getModel());
            getCidsBeans().clear();

            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        final MetaClass metaClass = getEffectiveMetaClass();
                        final String query = createQuery(metaClass);
                        final List<CidsBean> beans = new ArrayList<>();
                        final MetaObject[] mos = MetaObjectCache.getInstance()
                                    .getMetaObjectsByQuery(
                                        query,
                                        metaClass,
                                        true,
                                        getConnectionContext());
                        if (mos != null) {
                            for (final MetaObject mo : mos) {
                                if (mo != null) {
                                    beans.add(mo.getBean());
                                }
                            }
                        }
                        return beans;
                    }

                    @Override
                    protected void done() {
                        try {
                            final List<CidsBean> beans = get();
                            getCidsBeans().addAll(beans);
                            if ((cidsBean != null)) {
                                if (isParentBean()) {
                                    for (int index = 0; index < beans.size(); index++) {
                                        final CidsBean subBean = beans.get(index);
                                        if ((subBean != null) && (subBean.getProperty("order_by") == null)) {
                                            subBean.setProperty("order_by", index + 1);
                                        }
                                    }
                                } else {
                                    // replace bean from db-selection with actual cidsbean
                                    // otherwise persisting does not work as expected
                                    getCidsBeans().remove(cidsBean);
                                    getCidsBeans().add(cidsBean);
                                }
                            }
                            jList1.setModel(new SortedListModel(getCidsBeans()));

                            jList1.setSelectedValue(cidsBean, true);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            jList1.setModel(new JList<>(new Object[] { "Fehler !" }).getModel());
                        }
                    }
                }.execute();

            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return (cidsBean != null)
            ? String.format("%s: %s", cidsBean.getMetaObject().getMetaClass().getName(), cidsBean.toString()) : null;
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        final Set<String> errorMessages = new HashSet<>();
        final Map<String, CidsBean> uniqueName = new HashMap<>();
        for (int index = 0; index < jList1.getModel().getSize(); index++) {
            final Object object = jList1.getModel().getElementAt(index);
            if ((object instanceof CidsBean)) {
                if (((CidsBean)object).getMetaObject().getStatus() != MetaObject.TO_DELETE) {
                    final CidsBean cidsBean = (CidsBean)object;
                    final String name = (String)cidsBean.getProperty("name");
                    if ((name == null) || name.isEmpty()) {
                        errorMessages.add("<li>Der Name der nicht leer sein.</li>");
                    } else if (uniqueName.containsKey(name)) {
                        errorMessages.add(String.format("<li>Der Name '%s' ist nicht eindeutig.</li>", name));
                    }
                    uniqueName.put(name, cidsBean);
                }
            }
        }
        if (isParentBean() && errorMessages.isEmpty()) {
            for (final String name : uniqueName.keySet()) {
                final CidsBean cidsBean = uniqueName.get(name);
                if (!cidsBean.equals(getCidsBean())) {
                    try {
                        cidsBean.persist(getConnectionContext());
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        errorMessages.add(String.format("<li>Fehler beim Speichern von %s.</li>", name));
                    }
                }
            }
        }
        final MetaClass metaClass = getEffectiveMetaClass();
        if (metaClass != null) {
            MetaObjectCache.getInstance().clearCache(metaClass);
        }

        if (errorMessages.isEmpty()) {
            return true;
        } else {
            JOptionPane.showMessageDialog(
                StaticSwingTools.getParentFrame(this),
                String.format(
                    "<html><body>Folgende Probleme verhindern das Speichern:<ul>%s</ul></body></html>",
                    String.join("<br/>", errorMessages)),
                "Die Änderungen können nicht gespeichert werden.",
                JOptionPane.WARNING_MESSAGE);

            return false;
        }
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isParentBean() {
        return "PF_SCHLUESSELTABELLE".equalsIgnoreCase(cidsBean.getMetaObject().getMetaClass().getTableName());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class STCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final Component component = super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus
                            && isParentBean());
            if ((component instanceof JLabel) && (value instanceof CidsBean)) {
                final CidsBean cidsBean = (CidsBean)value;
                final JLabel label = (JLabel)component;
                label.setEnabled(isParentBean() || cidsBean.equals(getCidsBean()));
                label.setOpaque(isSelected);
                if (cidsBean.getProperty("name") == null) {
                    label.setText("<html><i>[neuer Eintrag]");
                }
                if (isEditable() && isParentBean()) {
                    switch (cidsBean.getMetaObject().getStatus()) {
                        case MetaObject.MODIFIED: {
                            label.setForeground(Color.BLUE);
                        }
                        break;
                        case MetaObject.TO_DELETE: {
                            label.setForeground(Color.RED);
                        }
                        break;
                        case MetaObject.NEW: {
                            label.setForeground(Color.GREEN.darker());
                        }
                        break;
                        default: {
                            label.setForeground(Color.BLACK);
                        }
                    }
                }
            }
            return component;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class SortedListModel extends AbstractListModel {

        //~ Instance fields ----------------------------------------------------

        private final SortedSet<CidsBean> cidsBeans;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new SortedListModel object.
         *
         * @param  cidsBeans  DOCUMENT ME!
         */
        public SortedListModel(final SortedSet<CidsBean> cidsBeans) {
            this.cidsBeans = cidsBeans;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public int getSize() {
            return cidsBeans.size();
        }

        @Override
        public Object getElementAt(final int index) {
            return cidsBeans.toArray()[index];
        }

        /**
         * DOCUMENT ME!
         *
         * @param  element  DOCUMENT ME!
         */
        public void add(final Object element) {
            if ((element instanceof CidsBean) && cidsBeans.add((CidsBean)element)) {
                refresh();
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  elements  DOCUMENT ME!
         */
        public void addAll(final Object[] elements) {
            final Collection c = Arrays.asList(elements);
            cidsBeans.addAll(c);
            refresh();
        }

        /**
         * DOCUMENT ME!
         */
        public void clear() {
            cidsBeans.clear();
            refresh();
        }

        /**
         * DOCUMENT ME!
         *
         * @param   element  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean contains(final Object element) {
            if (element instanceof CidsBean) {
                return cidsBeans.contains((CidsBean)element);
            } else {
                return false;
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object firstElement() {
            return cidsBeans.first();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Iterator iterator() {
            return cidsBeans.iterator();
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object lastElement() {
            return cidsBeans.last();
        }

        /**
         * DOCUMENT ME!
         *
         * @param   element  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean removeElement(final Object element) {
            if (element instanceof CidsBean) {
                final boolean removed = cidsBeans.remove((CidsBean)element);
                if (removed) {
                    refresh();
                }
                return removed;
            } else {
                return false;
            }
        }

        /**
         * DOCUMENT ME!
         */
        public void refresh() {
            fireContentsChanged(this, 0, getSize());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class FixedSelectionList extends javax.swing.JList<Object> {

        //~ Methods ------------------------------------------------------------

        @Override
        public void setSelectionInterval(final int anchor, final int lead) {
            if (isParentBean()) {
                super.setSelectionInterval(anchor, lead);
            }
        }
    }
}
