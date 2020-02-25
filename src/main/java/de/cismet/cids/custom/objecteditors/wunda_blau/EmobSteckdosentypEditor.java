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

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

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
public class EmobSteckdosentypEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EmobSteckdosentypEditor.class);

    public static final String TABLE_NAME = "emob_steckdosentyp";
    public static final String FIELD__SCHLUESSEL = "schluessel";
    public static final String FIELD__NAME = "name";
    public static final String FIELD__ID = "id";

    public static final String BUNDLE_NONAME = "EmobSteckdosentypEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "EmobSteckdosentypEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_DUPLICATEKEY = "EmobSteckdosentypEditor.prepareForSave().duplicateSchluessel";
    public static final String BUNDLE_PANE_PREFIX =
        "EmobSteckdosentypEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "EmobSteckdosentypEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EmobSteckdosentypEditor.prepareForSave().JOptionPane.title";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum otherTableCases {

        //~ Enum constants -----------------------------------------------------

        redundantAttKey, redundantAttName
    }

    //~ Instance fields --------------------------------------------------------

    private Boolean redundantName = false;
    private Boolean redundantKey = false;

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel panContent;
    private javax.swing.JPanel panFillerUnten;
    private javax.swing.JPanel panFillerUnten1;
    private javax.swing.JPanel panName;
    private javax.swing.JTextField txtName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EmobSteckdosentypEditor() {
    }

    /**
     * Creates a new EmobSteckdosentypEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EmobSteckdosentypEditor(final boolean boolEditor) {
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
                    checkAttributes();
                }

                @Override
                public void removeUpdate(final DocumentEvent e) {
                    checkAttributes();
                }

                @Override
                public void changedUpdate(final DocumentEvent e) {
                    checkAttributes();
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
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panFillerUnten = new javax.swing.JPanel();
        panContent = new RoundedPanel();
        panFillerUnten1 = new javax.swing.JPanel();
        panName = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();

        setAutoscrolls(true);
        setMinimumSize(new java.awt.Dimension(600, 646));
        setPreferredSize(new java.awt.Dimension(600, 737));
        setLayout(new java.awt.GridBagLayout());

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
        add(panFillerUnten, gridBagConstraints);

        panContent.setAutoscrolls(true);
        panContent.setMaximumSize(new java.awt.Dimension(450, 2147483647));
        panContent.setMinimumSize(new java.awt.Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new java.awt.Dimension(450, 961));
        panContent.setLayout(new java.awt.GridBagLayout());

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final javax.swing.GroupLayout panFillerUnten1Layout = new javax.swing.GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(
            panFillerUnten1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panContent.add(panFillerUnten1, gridBagConstraints);

        panName.setOpaque(false);
        panName.setLayout(new java.awt.GridBagLayout());

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panName.add(lblName, gridBagConstraints);

        txtName.setToolTipText("");

        final org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_NONAME));
            } else {
                if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                    cidsBean.setProperty(FIELD__SCHLUESSEL, txtName.getText().trim());
                }
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_DUPLICATENAME));
                } else {
                    if (redundantKey) {
                        LOG.warn("Duplicate key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_DUPLICATEKEY));
                    } else {
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EmobSteckdosentypEditor.class, BUNDLE_PANE_TITLE),
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
        // dispose();  Wenn Aufruf hier, dann cbGeom.getSelectedItem()wird ein neu gezeichnetes Polygon nicht erkannt.
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            bindingGroup.bind();
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
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     * @param  fall   DOCUMENT ME!
     */
    private void checkName(final String field, final otherTableCases fall) {
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
                    + cidsBean.getProperty(FIELD__ID),
            fall);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkAttributes() {
        checkName(FIELD__NAME, otherTableCases.redundantAttName);
        checkName(FIELD__SCHLUESSEL, otherTableCases.redundantAttKey);
    }

    @Override
    public void dispose() {
        super.dispose();
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
    /**
     * DOCUMENT ME!
     *
     * @param  tableName    DOCUMENT ME!
     * @param  whereClause  DOCUMENT ME!
     * @param  fall         DOCUMENT ME!
     */
    private void valueFromOtherTable(final String tableName, final String whereClause, final otherTableCases fall) {
        final SwingWorker<CidsBean, Void> worker = new SwingWorker<CidsBean, Void>() {

                @Override
                protected CidsBean doInBackground() throws Exception {
                    return getOtherTableValue(tableName, whereClause, getConnectionContext());
                }

                @Override
                protected void done() {
                    final CidsBean check;
                    try {
                        check = get();
                        if (check != null) {
                            switch (fall) {
                                case redundantAttKey: {  // check redundant key
                                    redundantKey = true;
                                    break;
                                }
                                case redundantAttName: { // check redundant name
                                    redundantName = true;
                                    break;
                                }
                            }
                        } else {
                            switch (fall) {
                                case redundantAttKey: {  // check redundant key
                                    redundantKey = false;
                                    break;
                                }
                                case redundantAttName: { // check redundant name
                                    redundantName = false;
                                    break;
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                }
            };
        worker.execute();
    }
}
