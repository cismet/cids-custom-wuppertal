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

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.cismet.cids.custom.utils.CidsBeansTableModel;
import de.cismet.cids.custom.wunda_blau.search.server.ObjectsPermissionsSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.CidsBeanList;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ObjectsPersmissionsProviderPanel extends javax.swing.JPanel implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(ObjectsPersmissionsProviderPanel.class);
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
        DISABLE_WRITE, DISABLE_TIMESTAMPS
    }

    //~ Instance fields --------------------------------------------------------

    private final List<String> columnProperties;
    private final List<String> columnNames;
    private final List<Class> columnClasses;
    private final List<Boolean> columnEditable;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    private final Map<Integer, MetaClass> classesMap = new HashMap<>();
    private final Map<String, CidsBean> objectsMap = new HashMap<>();
    private final Collection<CidsBean> permissionBeansToDelete = new ArrayList<>();

    private final boolean enableEdit;
    private final boolean enableGroupPermission;
    private final boolean enableUserPermission;
    private final boolean enableAllFromClass;
    private final boolean enableReadPermission;
    private final boolean enableWritePermission;
    private final boolean enableTimestamps;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCreatePermission;
    private javax.swing.JButton btnDeletePermission;
    private javax.swing.JButton btnPersist;
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
    private javax.swing.JDialog jDialog1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblGroupName;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JList<CidsBean> lstObjects;
    private javax.swing.JRadioButton optAllClassObjects;
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
     * Creates new form ObjectsPersmissionProviderPanel.
     *
     * @param  options  DOCUMENT ME!
     */
    public ObjectsPersmissionsProviderPanel(final Option... options) {
        boolean enableEdit = true;
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

        columnProperties = new ArrayList<>();
        columnNames = new ArrayList<>();
        columnClasses = new ArrayList<>();
        columnEditable = new ArrayList<>();

        columnProperties.add(PROPERTY_CLASS_NAME);
        columnNames.add("Klasse");
        columnClasses.add(String.class);
        columnEditable.add(false);

        columnProperties.add(PROPERTY_OBJECT_NAME);
        columnNames.add("Objekt");
        columnClasses.add(CidsBean.class);
        columnEditable.add(false);

        columnProperties.add(PROPERTY_GROUP_NAME);
        columnNames.add("Gruppe");
        columnClasses.add(String.class);
        columnEditable.add(false);

        columnProperties.add(PROPERTY_USER_NAME);
        columnNames.add("Benutzer");
        columnClasses.add(String.class);
        columnEditable.add(false);

        if (this.enableReadPermission) {
            columnProperties.add(PROPERTY_READ);
            columnNames.add("Lesen");
            columnClasses.add(Boolean.class);
            columnEditable.add(false);
        }

        if (this.enableWritePermission) {
            columnProperties.add(PROPERTY_WRITE);
            columnNames.add("Schreiben");
            columnClasses.add(Boolean.class);
            columnEditable.add(false);
        }

        if (this.enableTimestamps) {
            columnProperties.add(PROPERTY_TS_START);
            columnNames.add("Gültig ab");
            columnClasses.add(Timestamp.class);
            columnEditable.add(false);

            columnProperties.add(PROPERTY_TS_END);
            columnNames.add("Gültig bis");
            columnClasses.add(Timestamp.class);
            columnEditable.add(false);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jDialog1 = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jPanel7 = this;
        jPanel6 = new javax.swing.JPanel();
        btnPersist = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        optAllClassObjects = new javax.swing.JRadioButton();
        cbClasses = new javax.swing.JComboBox<>();
        optOnlySelectedObjects = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel10 = new javax.swing.JPanel();
        lblGroupName = new javax.swing.JLabel();
        optByGroupName = new javax.swing.JRadioButton();
        lblUserName = new javax.swing.JLabel();
        optByUserName = new javax.swing.JRadioButton();
        txtGroupName = new javax.swing.JTextField();
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
        jScrollPane3 = new javax.swing.JScrollPane();
        lstObjects = new CidsBeanList();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPermissions = new org.jdesktop.swingx.JXTable();
        btnDeletePermission = new javax.swing.JButton();

        jDialog1.setTitle(org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.jDialog1.title")); // NOI18N
        jDialog1.setMinimumSize(new java.awt.Dimension(1000, 500));
        jDialog1.setModal(true);
        jDialog1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(jPanel7, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        org.openide.awt.Mnemonics.setLocalizedText(
            btnPersist,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.btnPersist.text")); // NOI18N
        btnPersist.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPersistActionPerformed(evt);
                }
            });
        jPanel6.add(btnPersist);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnCancel,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCancelActionPerformed(evt);
                }
            });
        jPanel6.add(btnCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jDialog1.getContentPane().add(jPanel8, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ObjectsPersmissionsProviderPanel.class,
                    "ObjectsPersmissionsProviderPanel.jPanel2.border.title"))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel9.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(optAllClassObjects);
        org.openide.awt.Mnemonics.setLocalizedText(
            optAllClassObjects,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.optAllClassObjects.text")); // NOI18N
        optAllClassObjects.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optAllClassObjectsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(cbClasses, gridBagConstraints);

        buttonGroup1.add(optOnlySelectedObjects);
        optOnlySelectedObjects.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            optOnlySelectedObjects,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.optOnlySelectedObjects.text")); // NOI18N
        optOnlySelectedObjects.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optOnlySelectedObjectsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel9.add(optOnlySelectedObjects, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jSeparator1, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGroupName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.lblGroupName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(lblGroupName, gridBagConstraints);

        buttonGroup2.add(optByGroupName);
        optByGroupName.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(
            optByGroupName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.optByGroupName.text")); // NOI18N
        optByGroupName.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optByGroupNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(optByGroupName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblUserName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.lblUserName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(lblUserName, gridBagConstraints);

        buttonGroup2.add(optByUserName);
        org.openide.awt.Mnemonics.setLocalizedText(
            optByUserName,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.optByUserName.text")); // NOI18N
        optByUserName.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    optByUserNameActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(optByUserName, gridBagConstraints);

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(txtGroupName, gridBagConstraints);

        txtUserName.setText(org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.txtUserName.text")); // NOI18N

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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel10.add(txtUserName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jSeparator3, gridBagConstraints);

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
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.chkTsStart.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(chkTsStart, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chkTsEnd,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.chkTsEnd.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel11.add(chkTsEnd, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel11, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel4.add(jSeparator2, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            chkRead,
            org.openide.util.NbBundle.getMessage(
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.chkRead.text")); // NOI18N
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
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.chkWrite.text")); // NOI18N
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
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.btnCreatePermission.text")); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    ObjectsPersmissionsProviderPanel.class,
                    "ObjectsPersmissionsProviderPanel.jPanel3.border.title"))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        tblPermissions.setModel(new ObjectsPermissionsTableModel());
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
                ObjectsPersmissionsProviderPanel.class,
                "ObjectsPersmissionsProviderPanel.btnDeletePermission.text")); // NOI18N
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
     * @param  evt  DOCUMENT ME!
     */
    private void btnCancelActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCancelActionPerformed
        jDialog1.dispose();
    }                                                                             //GEN-LAST:event_btnCancelActionPerformed

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
    private void persistPermissions() {
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
    private void btnPersistActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPersistActionPerformed
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    persistPermissions();
                    return null;
                }

                @Override
                protected void done() {
                    jDialog1.dispose();
                }
            }.execute();
    } //GEN-LAST:event_btnPersistActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnDeletePermissionActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnDeletePermissionActionPerformed
        for (final int row : tblPermissions.getSelectedRows()) {
            final CidsBean selectedPermissionBean = getPermissionsTableModel().getCidsBean(row);
            if (selectedPermissionBean != null) {
                permissionBeansToDelete.add(selectedPermissionBean);
                final MetaObject permissionMo = selectedPermissionBean.getMetaObject();
                if (permissionMo != null) {
                    permissionMo.setStatus(MetaObject.TO_DELETE);
                    permissionMo.setChanged(true);
                    getPermissionsTableModel().remove(selectedPermissionBean);
                }
            }
        }
    }                                                                                       //GEN-LAST:event_btnDeletePermissionActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCreatePermissionActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCreatePermissionActionPerformed
        if (optOnlySelectedObjects.isSelected()) {
            final Collection<CidsBean> allSelectedObjectBeans = new ArrayList<>(lstObjects.getSelectedValuesList());

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
     */
    private void refreshAddButton() {
        final boolean objectsSelected = (optOnlySelectedObjects.isSelected()
                        && !lstObjects.getSelectedValuesList().isEmpty())
                    || (enableAllFromClass && optAllClassObjects.isSelected()
                        && (cbClasses.getSelectedItem() != null));
        final boolean targetSelected = (enableGroupPermission && optByGroupName.isSelected()
                        && (txtGroupName.getText() != null)
                        && !txtGroupName.getText().trim().isEmpty())
                    || (enableUserPermission && optByUserName.isSelected() && (txtUserName.getText() != null)
                        && !txtUserName.getText().trim().isEmpty());
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

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        optAllClassObjects.setVisible(enableAllFromClass);
        cbClasses.setVisible(enableAllFromClass);

        lblGroupName.setVisible(enableGroupPermission);
        optByGroupName.setVisible(enableGroupPermission);
        txtGroupName.setVisible(enableGroupPermission);

        lblUserName.setVisible(enableUserPermission);
        optByUserName.setVisible(enableUserPermission);
        txtUserName.setVisible(enableUserPermission);

        chkRead.setVisible(enableReadPermission);
        chkWrite.setVisible(enableWritePermission);

        jPanel4.setVisible(enableEdit);
        btnDeletePermission.setVisible(enableEdit);
        btnPersist.setVisible(enableEdit);

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
    private void refreshRemoveButton() {
        btnDeletePermission.setEnabled(tblPermissions.getSelectedRowCount() > 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBeans          DOCUMENT ME!
     * @param   options            DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static JDialog createNewDialog(final Collection<CidsBean> cidsBeans,
            final Option[] options,
            final ConnectionContext connectionContext) {
        final ObjectsPersmissionsProviderPanel panel = new ObjectsPersmissionsProviderPanel((options != null) ? options
                                                                                                              : null);
        panel.initWithConnectionContext(connectionContext);
        panel.setCidsBeans(cidsBeans);
        panel.jDialog1.pack();
        return panel.jDialog1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setCidsBeans(final Collection<CidsBean> cidsBeans) {
        final DefaultListModel<CidsBean> objectsListModel = new DefaultListModel<>();
        final DefaultComboBoxModel<MetaClass> classesCbModel = new DefaultComboBoxModel<>();
        final Collection<MetaObjectNode> objectMons = new ArrayList<>();
        classesMap.clear();
        objectsMap.clear();
        permissionBeansToDelete.clear();
        if (cidsBeans != null) {
            for (final CidsBean cidsBean : cidsBeans) {
                if (cidsBean != null) {
                    final MetaObjectNode objectMon = new MetaObjectNode(cidsBean);
                    objectMons.add(objectMon);
                    objectsMap.put(createObjectKey(objectMon.getObjectId(), objectMon.getClassId()), cidsBean);
                    objectsListModel.addElement(cidsBean);
                    final MetaClass metaClass = cidsBean.getMetaObject().getMetaClass();
                    classesMap.put(metaClass.getID(), metaClass);
                    if (classesCbModel.getIndexOf(metaClass) < 0) {
                        classesCbModel.addElement(metaClass);
                    }
                }
            }
        }
        lstObjects.setModel(objectsListModel);
        cbClasses.setModel(classesCbModel);
        cbClasses.setSelectedIndex(0);

        if (!objectMons.isEmpty()) {
            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        final ObjectsPermissionsSearch permSearch = new ObjectsPermissionsSearch(objectMons);
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
                        } catch (final Exception ex) {
                            error("error while searching permission beans", ex);
                        }
                    }
                }.execute();
        } else {
            getPermissionsTableModel().setCidsBeans(null);
        }
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
                columnEditable.toArray(new Boolean[0]));
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
                        return objecBean;
                    }
                }
            }
            return super.getValueAt(rowIndex, columnIndex);
        }
    }
}
