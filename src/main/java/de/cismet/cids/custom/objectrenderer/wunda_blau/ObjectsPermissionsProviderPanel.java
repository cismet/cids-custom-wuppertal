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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.DefaultMetaTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.clientutils.CidsBeansTableModel;
import de.cismet.cids.custom.wunda_blau.search.server.ObjectsPermissionsSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ObjectsPermissionsProviderPanel extends javax.swing.JPanel implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ObjectsPermissionsProviderPanel.class);
    private static final String TABLE_PERMISSIONS = "CS_OBJECTPERMISSIONS";
    private static final String TABLE_COLLECTOR = "CS_OBJECTPERMISSIONS_COLLECTOR";

    private static final String PROPERTY_CLASS_NAME = "_class_name";
    private static final String PROPERTY_OBJECT_NAME = "_object_name";
    private static final String PROPERTY_GROUP_NAME = "group_name";
    private static final String PROPERTY_USER_NAME = "user_name";
    private static final String PROPERTY_CLASS_ID = "class_id";
    private static final String PROPERTY_OBJECT_ID = "object_id";
    private static final String PROPERTY_READ = "read";
    private static final String PROPERTY_WRITE = "write";
    private static final String PROPERTY_TS_START = "ts_start";
    private static final String PROPERTY_TS_END = "ts_end";
    private static final String PROPERTY_ARR_ENTRIES = "arr_entries";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Option {

        //~ Enum constants -----------------------------------------------------

        READ_ONLY, DISABLE_GROUP_PERMISSIONS, DISABLE_USER_PERMISSIONS, DISABLE_ALL_FROM_CLASS, DISABLE_READ,
        DISABLE_WRITE, DISABLE_TIMESTAMPS, DISABLE_CLASS_SELECTION, ONLY_CONFATTR_GROUPS
    }

    //~ Instance fields --------------------------------------------------------

    private final List<String> columnProperties = new ArrayList<>();
    private final List<String> columnNames = new ArrayList<>();
    private final List<Class> columnClasses = new ArrayList<>();
    // private final List<Boolean> columnEditable = new ArrayList<>();

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private final Map<Integer, MetaClass> classesMap = new HashMap<>();
    private final Map<String, CidsBean> objectsMap = new HashMap<>();
    private final Collection<CidsBean> permissionBeansToDelete = new ArrayList<>();
    private final Collection<String> groupNames = new ArrayList<>();

    private final boolean enableClassSelection;
    private final boolean enableEdit;
    private final boolean enableGroupPermission;
    private final boolean enableUserPermission;
    private final boolean enableAllFromClass;
    private final boolean enableReadPermission;
    private final boolean enableWritePermission;
    private final boolean enableTimestamps;
    private final boolean enableAllGroups;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreatePermission;
    private javax.swing.JButton btnDeletePermission;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<MetaClass> cbClasses;
    private javax.swing.JCheckBox chkRead;
    private javax.swing.JCheckBox chkTsEnd;
    private javax.swing.JCheckBox chkTsStart;
    private javax.swing.JCheckBox chkWrite;
    private org.jdesktop.swingx.JXDatePicker dpTsEnd;
    private org.jdesktop.swingx.JXDatePicker dpTsStart;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblGroupName;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JList<CidsBean> lstObjects;
    private javax.swing.JRadioButton optAllClassObjects;
    private javax.swing.JRadioButton optAllListObjects;
    private javax.swing.JRadioButton optByGroupName;
    private javax.swing.JRadioButton optByUserName;
    private javax.swing.JRadioButton optOnlySelectedObjects;
    private org.jdesktop.swingx.JXTable tblPermissions;
    private javax.swing.JTextField txtGroupName;
    private javax.swing.JTextField txtUserName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ObjectsPersmissionsProviderPanel object.
     */
    public ObjectsPermissionsProviderPanel() {
        this(null);
    }

    /**
     * Creates new form ObjectsPersmissionProviderPanel.
     *
     * @param  options  DOCUMENT ME!
     */
    public ObjectsPermissionsProviderPanel(final Option... options) {
        boolean enableAllGroups = true;
        boolean enableEdit = true;
        boolean enableClassSelection = true;
        boolean enableGroupPermission = true;
        boolean enableUserPermission = true;
        boolean enableAllFromClass = true;
        boolean enableReadPermission = true;
        boolean enableWritePermission = true;
        final boolean enableTimestamps = true;
        if (options != null) {
            for (final Option option : options) {
                switch (option) {
                    case READ_ONLY: {
                        enableEdit = false;
                    }
                    break;
                    case DISABLE_GROUP_PERMISSIONS: {
                        enableGroupPermission = false;
                    }
                    break;
                    case DISABLE_USER_PERMISSIONS: {
                        enableUserPermission = false;
                    }
                    break;
                    case DISABLE_ALL_FROM_CLASS: {
                        enableAllFromClass = false;
                    }
                    break;
                    case DISABLE_READ: {
                        enableReadPermission = false;
                    }
                    break;
                    case DISABLE_WRITE: {
                        enableWritePermission = false;
                    }
                    break;
                    case DISABLE_CLASS_SELECTION: {
                        enableClassSelection = false;
                    }
                    break;
                    case ONLY_CONFATTR_GROUPS: {
                        enableAllGroups = false;
                    }
                    break;
                }
            }
        }
        this.enableEdit = enableEdit;
        this.enableGroupPermission = enableGroupPermission;
        this.enableUserPermission = enableUserPermission;
        this.enableAllFromClass = enableAllFromClass;
        this.enableReadPermission = enableReadPermission;
        this.enableWritePermission = enableWritePermission;
        this.enableTimestamps = enableTimestamps;
        this.enableClassSelection = enableClassSelection;

        if (enableClassSelection) {
            columnProperties.add(PROPERTY_CLASS_NAME);
            columnNames.add("Klasse");
            columnClasses.add(String.class);
            // columnEditable.add(false);
        }

        columnProperties.add(PROPERTY_OBJECT_NAME);
        columnNames.add("Objekt-Bezeichnung");
        columnClasses.add(CidsBean.class);
        // columnEditable.add(false);

        if (enableGroupPermission) {
            columnProperties.add(PROPERTY_GROUP_NAME);
            columnNames.add("Gruppe");
            columnClasses.add(String.class);
            // columnEditable.add(false);
        }

        if (enableUserPermission) {
            columnProperties.add(PROPERTY_USER_NAME);
            columnNames.add("Benutzer");
            columnClasses.add(String.class);
            // columnEditable.add(false);
        }

        if (this.enableReadPermission) {
            columnProperties.add(PROPERTY_READ);
            columnNames.add("Lesen");
            columnClasses.add(Boolean.class);
            // columnEditable.add(false);
        }

        if (this.enableWritePermission) {
            columnProperties.add(PROPERTY_WRITE);
            columnNames.add("Schreiben");
            columnClasses.add(Boolean.class);
            // columnEditable.add(false);
        }

        if (this.enableTimestamps) {
            columnProperties.add(PROPERTY_TS_START);
            columnNames.add("Gültig ab");
            columnClasses.add(Timestamp.class);
            // columnEditable.add(false);

            columnProperties.add(PROPERTY_TS_END);
            columnNames.add("Gültig bis");
            columnClasses.add(Timestamp.class);
            // columnEditable.add(false);
        }

        this.enableAllGroups = enableAllGroups;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        optOnlySelectedObjects = new javax.swing.JRadioButton();
        optAllListObjects = new javax.swing.JRadioButton();
        optAllClassObjects = new javax.swing.JRadioButton();
        cbClasses = new javax.swing.JComboBox<>();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstObjects = new DropAwareJList();
        jPanel6 = new javax.swing.JPanel();
        lblGroupName = new javax.swing.JLabel();
        optByGroupName = new javax.swing.JRadioButton();
        lblUserName = new javax.swing.JLabel();
        optByUserName = new javax.swing.JRadioButton();
        txtGroupName = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        txtUserName = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jPanel11 = new javax.swing.JPanel();
        dpTsStart = new org.jdesktop.swingx.JXDatePicker();
        dpTsEnd = new org.jdesktop.swingx.JXDatePicker();
        chkTsStart = new javax.swing.JCheckBox();
        chkTsEnd = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel5 = new javax.swing.JPanel();
        chkRead = new javax.swing.JCheckBox();
        chkWrite = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(32767, 0));
        btnCreatePermission = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPermissions = new org.jdesktop.swingx.JXTable();
        btnDeletePermission = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ObjectsPermissionsProviderPanel.class,
                    "ObjectsPermissionsProviderPanel.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(optOnlySelectedObjects);
        org.openide.awt.Mnemonics.setLocalizedText(
            optOnlySelectedObjects,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.optOnlySelectedObjects.text")); // NOI18N
        optOnlySelectedObjects.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optOnlySelectedObjectsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(optOnlySelectedObjects, gridBagConstraints);

        buttonGroup1.add(optAllListObjects);
        optAllListObjects.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            optAllListObjects,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.optAllListObjects.text")); // NOI18N
        optAllListObjects.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optAllListObjectsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(optAllListObjects, gridBagConstraints);

        buttonGroup1.add(optAllClassObjects);
        org.openide.awt.Mnemonics.setLocalizedText(
            optAllClassObjects,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.optAllClassObjects.text")); // NOI18N
        optAllClassObjects.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optAllClassObjectsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(optAllClassObjects, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                optAllClassObjects,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                cbClasses,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cbClasses.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbClassesActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(cbClasses, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel9, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jPanel4, gridBagConstraints);

        jScrollPane3.setMinimumSize(new java.awt.Dimension(200, 100));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(200, 100));
        jScrollPane3.setRequestFocusEnabled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                optOnlySelectedObjects,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                lstObjects,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lstObjects.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstObjectsValueChanged(evt);
                }
            });
        jScrollPane3.setViewportView(lstObjects);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ObjectsPermissionsProviderPanel.class,
                    "ObjectsPermissionsProviderPanel.jPanel6.border.title"))); // NOI18N
        jPanel6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGroupName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.lblGroupName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblGroupName, gridBagConstraints);

        buttonGroup2.add(optByGroupName);
        optByGroupName.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            optByGroupName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.optByGroupName.text")); // NOI18N
        optByGroupName.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optByGroupNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(optByGroupName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblUserName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.lblUserName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(lblUserName, gridBagConstraints);

        buttonGroup2.add(optByUserName);
        org.openide.awt.Mnemonics.setLocalizedText(
            optByUserName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.optByUserName.text")); // NOI18N
        optByUserName.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optByUserNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(optByUserName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                optByGroupName,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                txtGroupName,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        txtGroupName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    txtGroupNamePropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(txtGroupName, gridBagConstraints);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(groupNames.toArray(new String[0])));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jComboBox1, gridBagConstraints);

        txtUserName.setText(org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.txtUserName.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                optByUserName,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                txtUserName,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        txtUserName.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

                @Override
                public void propertyChange(final java.beans.PropertyChangeEvent evt) {
                    txtUserNamePropertyChange(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(txtUserName, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jSeparator3, gridBagConstraints);

        jPanel11.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkTsStart,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                dpTsStart,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(dpTsStart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                chkTsEnd,
                org.jdesktop.beansbinding.ELProperty.create("${selected}"),
                dpTsEnd,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(dpTsEnd, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkTsStart,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.chkTsStart.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(chkTsStart, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkTsEnd,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.chkTsEnd.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(chkTsEnd, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jPanel11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel6.add(jSeparator2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chkRead,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.chkRead.text")); // NOI18N
        chkRead.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkReadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 12);
        jPanel5.add(chkRead, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkWrite,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.chkWrite.text")); // NOI18N
        chkWrite.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkWriteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 12);
        jPanel5.add(chkWrite, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(filler1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCreatePermission,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.btnCreatePermission.text")); // NOI18N
        btnCreatePermission.setEnabled(false);
        btnCreatePermission.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCreatePermissionActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel5.add(btnCreatePermission, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel1.add(jPanel6, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ObjectsPermissionsProviderPanel.class,
                    "ObjectsPermissionsProviderPanel.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        tblPermissions.setModel(new ObjectsPermissionsTableModel());
        tblPermissions.setEditable(false);
        jScrollPane1.setViewportView(tblPermissions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnDeletePermission,
            org.openide.util.NbBundle.getMessage(
                ObjectsPermissionsProviderPanel.class,
                "ObjectsPermissionsProviderPanel.btnDeletePermission.text")); // NOI18N
        btnDeletePermission.setEnabled(false);
        btnDeletePermission.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnDeletePermissionActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel3.add(btnDeletePermission, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  confAttr  DOCUMENT ME!
     */
    public void setGroupConfAttr(final String confAttr) {
        groupNames.clear();
        try {
            final String groupsString = SessionManager.getProxy()
                        .getConfigAttr(SessionManager.getSession().getUser(), confAttr, getConnectionContext());
            if (groupsString != null) {
                for (final String groupString : groupsString.split("\n")) {
                    groupNames.add(groupString.trim());
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<CidsBean> getPermissionBeansToDelete() {
        return permissionBeansToDelete;
    }

    /**
     * DOCUMENT ME!
     */
    public void persistPermissions() {
        final Collection<CidsBean> allPermissionBeansToPersist = new ArrayList<>();
        allPermissionBeansToPersist.addAll(getPermissionsTableModel().getCidsBeans());
        allPermissionBeansToPersist.addAll(getPermissionBeansToDelete());

        final Map<String, Collection<CidsBean>> permissionBeansPerDomain = new HashMap<>();
        for (final CidsBean permissionBean : allPermissionBeansToPersist) {
            if ((permissionBean != null) && (MetaObject.NO_STATUS != permissionBean.getMetaObject().getStatus())) {
                final MetaObject permissionMo = permissionBean.getMetaObject();
                if (permissionMo != null) {
                    final String domain = permissionMo.getDomain();
                    final Collection<CidsBean> permissionBeans;
                    if (permissionBeansPerDomain.containsKey(domain)) {
                        permissionBeans = permissionBeansPerDomain.get(domain);
                    } else {
                        permissionBeans = new ArrayList<>();
                        permissionBeansPerDomain.put(domain, permissionBeans);
                    }
                    permissionBeans.add(permissionBean);
                }
            }
        }

        final Collection<CidsBean> collectorBeans = new ArrayList<>(permissionBeansPerDomain.size());
        for (final String domain : permissionBeansPerDomain.keySet()) {
            try {
                final CidsBean collectorBean = CidsBean.createNewCidsBeanFromTableName(
                        domain,
                        TABLE_COLLECTOR,
                        getConnectionContext());
                final Collection permissionBeans = permissionBeansPerDomain.get(domain);
                if (permissionBeans != null) {
                    collectorBean.getBeanCollectionProperty(PROPERTY_ARR_ENTRIES).addAll(permissionBeans);
                    collectorBeans.add(collectorBean);
                }
            } catch (final Exception ex) {
                LOG.fatal(ex, ex);
                break;
            }
        }

        final boolean allCollectorsCreatedSuccessfully = collectorBeans.size() == permissionBeansPerDomain.size();
        if (allCollectorsCreatedSuccessfully) {
            for (final CidsBean collectorBean : collectorBeans) {
                try {
                    collectorBean.persist(getConnectionContext());
                } catch (final Exception ex) {
                    error("error while persisting collector", ex);
                }
                try {
                    collectorBean.delete();
                } catch (final Exception ex) {
                    error("error while deleting collector", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDeletePermissionActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDeletePermissionActionPerformed
        final Collection<CidsBean> collectedBeans = new ArrayList<>();
        for (final int row : tblPermissions.getSelectedRows()) {
            final CidsBean selectedPermissionBean = getPermissionsTableModel().getCidsBean(tblPermissions.getRowSorter()
                            .convertRowIndexToModel(row));
            if (selectedPermissionBean != null) {
                collectedBeans.add(selectedPermissionBean);
            }
        }
        permissionBeansToDelete.addAll(collectedBeans);
        for (final CidsBean collectedBean : collectedBeans) {
            final MetaObject permissionMo = collectedBean.getMetaObject();
            if (permissionMo != null) {
                permissionMo.setStatus(MetaObject.TO_DELETE);
                permissionMo.setChanged(true);
                getPermissionsTableModel().remove(collectedBean);
            }
        }
    }                                                                                       //GEN-LAST:event_btnDeletePermissionActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreatePermissionActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCreatePermissionActionPerformed
        if (optOnlySelectedObjects.isSelected() || optAllListObjects.isSelected()) {
            final Collection<CidsBean> allSelectedObjectBeans = new ArrayList<>();
            if (optOnlySelectedObjects.isSelected()) {
                allSelectedObjectBeans.addAll(lstObjects.getSelectedValuesList());
            } else if (optAllListObjects.isSelected()) {
                allSelectedObjectBeans.addAll(objectsMap.values());
            }

            final Map<String, Collection<CidsBean>> objectBeansPerDomain = new HashMap<>();
            for (final CidsBean objectBean : allSelectedObjectBeans) {
                if (objectBean != null) {
                    final MetaObject objectMo = objectBean.getMetaObject();
                    if (objectMo != null) {
                        final String domain = objectMo.getDomain();
                        final Collection<CidsBean> objectBeans;
                        if (objectBeansPerDomain.containsKey(domain)) {
                            objectBeans = objectBeansPerDomain.get(domain);
                        } else {
                            objectBeans = new ArrayList<>();
                            objectBeansPerDomain.put(domain, objectBeans);
                        }
                        objectBeans.add(objectBean);
                    }
                }
            }

            for (final String domain : objectBeansPerDomain.keySet()) {
                if (domain != null) {
                    final Collection<CidsBean> objectBeans = objectBeansPerDomain.get(domain);
                    for (final CidsBean objectBean : objectBeans) {
                        if (objectBean != null) {
                            final MetaObject objectMo = objectBean.getMetaObject();
                            if (objectMo != null) {
                                try {
                                    final CidsBean permissionBean = createPermissionBean(
                                            domain,
                                            objectMo.getClassID(),
                                            objectMo.getID());
                                    getPermissionsTableModel().add(permissionBean);
                                } catch (final Exception ex) {
                                    error("error while creating permission bean", ex);
                                }
                            }
                        }
                    }
                }
            }
        } else if (optAllClassObjects.isSelected()) {
            final MetaClass selectedMetaClass = (MetaClass)cbClasses.getSelectedItem();
            if (selectedMetaClass != null) {
                try {
                    final String domain = selectedMetaClass.getDomain();
                    final CidsBean permissionBean = createPermissionBean(
                            domain,
                            selectedMetaClass.getID(),
                            null /* acts as wildcard */);
                    getPermissionsTableModel().add(permissionBean);
                } catch (final Exception ex) {
                    error("error while creating permission bean", ex);
                }
            }
        }
    }                            //GEN-LAST:event_btnCreatePermissionActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   domain    DOCUMENT ME!
     * @param   classId   DOCUMENT ME!
     * @param   objectId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createPermissionBean(final String domain, final Integer classId, final Integer objectId)
            throws Exception {
        final CidsBean permissionBean = CidsBean.createNewCidsBeanFromTableName(
                domain,
                TABLE_PERMISSIONS,
                getConnectionContext());
        permissionBean.setProperty(PROPERTY_OBJECT_ID, objectId);
        permissionBean.setProperty(PROPERTY_CLASS_ID, classId);
        permissionBean.setProperty(
            PROPERTY_GROUP_NAME,
            optByGroupName.isSelected() ? (enableAllGroups ? txtGroupName.getText() : jComboBox1.getSelectedItem())
                                        : null);
        permissionBean.setProperty(
            PROPERTY_USER_NAME,
            optByUserName.isSelected() ? txtUserName.getText() : null);
        permissionBean.setProperty(PROPERTY_READ, chkRead.isSelected());
        permissionBean.setProperty(PROPERTY_WRITE, chkWrite.isSelected());
        permissionBean.setProperty(
            PROPERTY_TS_START,
            (chkTsStart.isSelected() && (dpTsStart.getDate() != null)) ? new Timestamp(dpTsStart.getDate().getTime())
                                                                       : null);
        permissionBean.setProperty(
            PROPERTY_TS_END,
            (chkTsEnd.isSelected() && (dpTsEnd.getDate() != null)) ? new Timestamp(dpTsEnd.getDate().getTime()) : null);
        return permissionBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optOnlySelectedObjectsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optOnlySelectedObjectsActionPerformed
        cbClasses.setSelectedItem(null);
        refreshAddButton();
    }                                                                                          //GEN-LAST:event_optOnlySelectedObjectsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optAllClassObjectsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optAllClassObjectsActionPerformed
        lstObjects.setSelectedIndices(new int[0]);
        cbClasses.setSelectedIndex(0);
        refreshAddButton();
    }                                                                                      //GEN-LAST:event_optAllClassObjectsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkReadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkReadActionPerformed
        refreshAddButton();
    }                                                                           //GEN-LAST:event_chkReadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkWriteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkWriteActionPerformed
        refreshAddButton();
    }                                                                            //GEN-LAST:event_chkWriteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstObjectsValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstObjectsValueChanged
        refreshAddButton();
    }                                                                                     //GEN-LAST:event_lstObjectsValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optByGroupNameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optByGroupNameActionPerformed
        txtUserName.setText(null);
        refreshAddButton();
    }                                                                                  //GEN-LAST:event_optByGroupNameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optByUserNameActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optByUserNameActionPerformed
        txtGroupName.setText(null);
        refreshAddButton();
    }                                                                                 //GEN-LAST:event_optByUserNameActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtGroupNamePropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_txtGroupNamePropertyChange
        if ("text".equals(evt.getPropertyName())) {
            refreshAddButton();
        }
    }                                                                                   //GEN-LAST:event_txtGroupNamePropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtUserNamePropertyChange(final java.beans.PropertyChangeEvent evt) { //GEN-FIRST:event_txtUserNamePropertyChange
        if ("text".equals(evt.getPropertyName())) {
            refreshAddButton();
        }
    }                                                                                  //GEN-LAST:event_txtUserNamePropertyChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbClassesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbClassesActionPerformed
        refreshAddButton();
    }                                                                             //GEN-LAST:event_cbClassesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void optAllListObjectsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_optAllListObjectsActionPerformed
        cbClasses.setSelectedItem(null);
        refreshAddButton();
    }                                                                                     //GEN-LAST:event_optAllListObjectsActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void refreshAddButton() {
        final boolean objectsSelected = (optOnlySelectedObjects.isSelected()
                        && !lstObjects.getSelectedValuesList().isEmpty())
                    || (enableAllFromClass && optAllClassObjects.isSelected()
                        && (cbClasses.getSelectedItem() != null))
                    || (optAllListObjects.isSelected() && (lstObjects.getModel().getSize() > 0));
        // final boolean groupSelected = enableAllGroups ? ;
        final boolean groupSelected = (enableGroupPermission && optByGroupName.isSelected()
                        && enableAllGroups)
            ? ((txtGroupName.getText() != null)
                        && !txtGroupName.getText().trim().isEmpty()) : (jComboBox1.getSelectedItem() != null);
        final boolean userSelected = enableUserPermission && optByUserName.isSelected()
                    && (txtUserName.getText() != null)
                    && !txtUserName.getText().trim().isEmpty();

        final boolean targetSelected = groupSelected || userSelected;
        final boolean permissionTypeSelected = (enableReadPermission && chkRead.isSelected())
                    || (enableWritePermission && chkWrite.isSelected());
        btnCreatePermission.setEnabled(objectsSelected && targetSelected && permissionTypeSelected);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ObjectsPermissionsTableModel getPermissionsTableModel() {
        return ((ObjectsPermissionsTableModel)tblPermissions.getModel());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  message  DOCUMENT ME!
     * @param  ex       DOCUMENT ME!
     */
    private void error(final String message, final Exception ex) {
        LOG.fatal(message, ex);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEnableEdit() {
        return enableEdit;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        new CidsBeanDropTarget((DropAwareJList)lstObjects);

        optAllClassObjects.setVisible(enableAllFromClass);
        cbClasses.setVisible(enableAllFromClass);

        lblGroupName.setVisible(enableGroupPermission);
        optByGroupName.setVisible(enableGroupPermission);
        txtGroupName.setVisible(enableGroupPermission && enableAllGroups);
        jComboBox1.setVisible(enableGroupPermission && !enableAllGroups);

        lblUserName.setVisible(enableUserPermission);
        optByUserName.setVisible(enableUserPermission);
        txtUserName.setVisible(enableUserPermission);

        chkRead.setVisible(enableReadPermission);
        chkRead.setEnabled(enableWritePermission);
        chkWrite.setVisible(enableWritePermission);
        chkWrite.setEnabled(enableReadPermission);

        jPanel4.setVisible(enableEdit);
        btnDeletePermission.setVisible(enableEdit);

        chkWrite.setSelected(!enableReadPermission);
        chkRead.setSelected(!enableWritePermission);

        jPanel11.setVisible(enableTimestamps);
        jSeparator2.setVisible(enableTimestamps);

        optByGroupName.setVisible(enableUserPermission && enableGroupPermission);
        optByUserName.setVisible(enableUserPermission && enableGroupPermission);
        optOnlySelectedObjects.setVisible(enableAllFromClass);
        optAllClassObjects.setVisible(enableAllFromClass);

        optAllClassObjects.setSelected(false);
        optOnlySelectedObjects.setSelected(true);

        optByUserName.setSelected(!enableGroupPermission);
        optByGroupName.setSelected(!enableUserPermission);

        cbClasses.setVisible(enableClassSelection);

        tblPermissions.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                @Override
                public void valueChanged(final ListSelectionEvent e) {
                    refreshRemoveButton();
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    public void reloadPermissions() {
        final DefaultMetaTreeNode[] nodes = ComponentRegistry.getRegistry()
                    .getActiveCatalogue()
                    .getSelectedNodesArray();
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final List<CidsBean> beans = new ArrayList<>();
                    for (final DefaultMetaTreeNode node : nodes) {
                        if (node instanceof ObjectTreeNode) {
                            final ObjectTreeNode otn = (ObjectTreeNode)node;
                            if (classesMap.containsKey(otn.getClassID())) {
                                beans.add(otn.getMetaObject().getBean());
                            }
                        }
                    }
                    return beans;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> beans = get();
                        setCidsBeans(beans);
//                        final DefaultListModel<CidsBean> lstModel = (DefaultListModel)lstObjects.getModel();
//                        for (final CidsBean bean : beans) {
//                            if (!((DefaultListModel)lstObjects.getModel()).contains(bean)) {
//                                lstModel.addElement(bean);
//                            }
//                        }
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                }
            }.execute();
        new SwingWorker<List<CidsBean>, Void>() {

                @Override
                protected List<CidsBean> doInBackground() throws Exception {
                    final ObjectsPermissionsSearch permSearch = new ObjectsPermissionsSearch(new MetaObjectNode(
                                "WUNDA_BLAU",
                                -1,
                                CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "MAUER", getConnectionContext())
                                            .getId()));
                    final Collection<MetaObjectNode> permissionMons = SessionManager.getProxy()
                                .customServerSearch(permSearch, getConnectionContext());
                    final List<CidsBean> permissionBeans = new ArrayList<>();
                    for (final MetaObjectNode permissionMon : permissionMons) {
                        final MetaObject permissionMo = SessionManager.getConnection()
                                    .getMetaObject(SessionManager.getSession().getUser(),
                                        permissionMon.getObjectId(),
                                        permissionMon.getClassId(),
                                        permissionMon.getDomain(),
                                        getConnectionContext());
                        if (permissionMo != null) {
                            permissionBeans.add(permissionMo.getBean());
                        }
                    }
                    return permissionBeans;
                }

                @Override
                protected void done() {
                    try {
                        final List<CidsBean> permissionBeans = get();
                        getPermissionsTableModel().setCidsBeans(permissionBeans);
                        reloadObjects(permissionBeans);
                    } catch (final Exception ex) {
                        error("error while searching permission beans", ex);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  permissionBeans  DOCUMENT ME!
     */
    private void reloadObjects(final List<CidsBean> permissionBeans) {
        new SwingWorker<Collection<MetaObject>, Void>() {

                @Override
                protected Collection<MetaObject> doInBackground() throws Exception {
                    final Collection<MetaObject> mos = new ArrayList<>();
                    for (final CidsBean permissionBean : permissionBeans) {
                        final Integer classId = (Integer)permissionBean.getProperty("class_id");
                        final Integer objectId = (Integer)permissionBean.getProperty("object_id");
                        if (objectId != null) {
                            final MetaObject mo = SessionManager.getProxy()
                                        .getMetaObject(
                                            objectId,
                                            classId,
                                            SessionManager.getSession().getConnectionInfo().getUserDomain(),
                                            getConnectionContext());
                            mos.add(mo);
                        }
                    }
                    return mos;
                }

                @Override
                protected void done() {
                    try {
                        final Collection<MetaObject> mos = get();
                        for (final MetaObject mo : mos) {
                            objectsMap.put(createObjectKey(mo.getId(), mo.getClassID()), mo.getBean());
                        }
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    getPermissionsTableModel().fireTableDataChanged();
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    public void cleanup() {
        objectsMap.clear();
        permissionBeansToDelete.clear();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshRemoveButton() {
        btnDeletePermission.setEnabled(tblPermissions.getSelectedRowCount() > 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  metaClasses  DOCUMENT ME!
     */
    public void setMetaClasses(final Collection<MetaClass> metaClasses) {
        final DefaultComboBoxModel<MetaClass> classesCbModel = new DefaultComboBoxModel<>();
        for (final MetaClass metaClass : metaClasses) {
            classesMap.put(metaClass.getID(), metaClass);
            if (classesCbModel.getIndexOf(metaClass) < 0) {
                classesCbModel.addElement(metaClass);
            }
        }
        cbClasses.setModel(classesCbModel);
        if (classesCbModel.getSize() > 0) {
            cbClasses.setSelectedIndex(0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        final DefaultListModel<CidsBean> objectsListModel = new DefaultListModel<>();
        if (cidsBeans != null) {
            for (final CidsBean cidsBean : cidsBeans) {
                if (cidsBean != null) {
                    final MetaClass metaClass = cidsBean.getMetaObject().getMetaClass();
                    if (classesMap.containsKey(metaClass.getID())) {
                        final MetaObjectNode objectMon = new MetaObjectNode(cidsBean);
                        objectsMap.put(createObjectKey(objectMon.getObjectId(), objectMon.getClassId()), cidsBean);
                        objectsListModel.addElement(cidsBean);
                    }
                }
            }
        }
        lstObjects.setModel(objectsListModel);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectId  DOCUMENT ME!
     * @param   classId   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String createObjectKey(final Integer objectId, final Integer classId) {
        return String.format("%d@%d", objectId, classId);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class ObjectsPermissionsTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ObjectsPermissionsTableModel object.
         */
        public ObjectsPermissionsTableModel() {
            super(columnProperties.toArray(new String[0]),
                columnNames.toArray(new String[0]),
                columnClasses.toArray(new Class[0]),
                // columnEditable.toArray(new Boolean[0])
                false,
                false);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final CidsBean cidsBean = getCidsBean(rowIndex);
            final String columnProperty = columnProperties.get(columnIndex);
            if (PROPERTY_CLASS_NAME.equals(columnProperty) || PROPERTY_OBJECT_NAME.equals(columnProperty)) {
                final Integer classId = (Integer)cidsBean.getProperty(PROPERTY_CLASS_ID);
                final Integer objectId = (Integer)cidsBean.getProperty(PROPERTY_OBJECT_ID);
                final CidsBean objecBean = (objectId != null) ? objectsMap.get(createObjectKey(objectId, classId))
                                                              : null;
                switch (columnProperty) {
                    case PROPERTY_CLASS_NAME: {
                        final MetaClass metaClass;
                        if (objecBean == null) {
                            metaClass = classesMap.get(classId);
                        } else {
                            metaClass = (objecBean.getMetaObject() != null) ? objecBean.getMetaObject().getMetaClass()
                                                                            : null;
                        }
                        return (metaClass != null) ? metaClass.getName() : null;
                    }
                    case PROPERTY_OBJECT_NAME: {
                        return (objectId != null) ? ((objecBean != null) ? objecBean : "<html><i>wird geladen...")
                                                  : "<html><i>Alle";
                    }
                }
            }
            return super.getValueAt(rowIndex, columnIndex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DropAwareJList extends JList implements CidsBeanDropListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DropAwareJList object.
         */
        public DropAwareJList() {
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  dataModel  DOCUMENT ME!
         */
        public DropAwareJList(final ListModel dataModel) {
            super(dataModel);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Object[] listData) {
            super(listData);
        }

        /**
         * Creates a new DropAwareJList object.
         *
         * @param  listData  DOCUMENT ME!
         */
        public DropAwareJList(final Vector listData) {
            super(listData);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  beans  DOCUMENT ME!
         */
        @Override
        public void beansDropped(final ArrayList<CidsBean> beans) {
            try {
                for (final CidsBean dropped : beans) {
                    if ((dropped != null) && classesMap.containsKey(dropped.getMetaObject().getClassID())
                                && !((DefaultListModel)lstObjects.getModel()).contains(dropped)) {
                        ((DefaultListModel)lstObjects.getModel()).addElement(dropped);
                    }
                }
            } catch (Exception ex) {
                LOG.error("Problem when adding the DroppedBeans", ex);
            }
        }
    }
}
