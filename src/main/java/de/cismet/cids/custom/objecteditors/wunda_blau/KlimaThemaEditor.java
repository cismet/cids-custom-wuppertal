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
import java.util.MissingResourceException;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class KlimaThemaEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------
    private static final String TITLE_NEW_THEMA = "eine neues Thema anlegen...";
    private static final Logger LOG = Logger.getLogger(KlimaThemaEditor.class);

    public static final String FIELD__NAME = "name";             // KlimaThema
    public static final String FIELD__ID = "id";                 // KlimaThema
    public static final String TABLE_NAME = "klima_thema";

    public static final String BUNDLE_NONAME = "KlimaThemaEditor.prepareForSave().noName";
    public static final String BUNDLE_NOICON = "KlimaThemaEditor.prepareForSave().noIcon";
    public static final String BUNDLE_NOCOLOR = "KlimaThemaEditor.prepareForSave().noFarbe";
    public static final String BUNDLE_WRONGCOLOR = "KlimaThemaEditor.prepareForSave().wrongFarbe";
    public static final String BUNDLE_DUPLICATENAME = "KlimaThemaEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_PANE_PREFIX =
        "KlimaThemaEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "KlimaThemaEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "KlimaThemaEditor.prepareForSave().JOptionPane.title";

    //~ Enums ------------------------------------------------------------------


    //~ Instance fields --------------------------------------------------------

    private SwingWorker worker_name;

    private Boolean redundantName = false;

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblFarbe;
    private JLabel lblIcon;
    private JLabel lblName;
    private JPanel panContent;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panName;
    private JTextField txtFarbe;
    private JTextField txtIcon;
    private JTextField txtName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public KlimaThemaEditor() {
    }

    /**
     * Creates a new KlimaThemaEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public KlimaThemaEditor(final boolean boolEditor) {
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

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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

        GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
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
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblName, gridBagConstraints);

        txtName.setToolTipText("");

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
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
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblIcon, gridBagConstraints);

        txtIcon.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.icon}"), txtIcon, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblFarbe, gridBagConstraints);

        txtFarbe.setToolTipText("");

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.farbe}"), txtFarbe, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtFarbe, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
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
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimaThemaEditor.class, BUNDLE_NONAME));
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimaThemaEditor.class, BUNDLE_DUPLICATENAME));
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
                errorMessage.append(NbBundle.getMessage(KlimaThemaEditor.class, BUNDLE_NOICON));
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        // farbe vorhanden
        try {
            if (txtFarbe.getText().trim().isEmpty()) {
                LOG.warn("No color specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(KlimaThemaEditor.class, BUNDLE_NOCOLOR));
            } else { 
                if (!(txtFarbe.getText().trim().length() == 7 && txtFarbe.getText().trim().matches("#[a-fA-F0-9]{0,6}$"))){
                    LOG.warn("Wrong color specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(KlimaThemaEditor.class, BUNDLE_WRONGCOLOR));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(KlimaThemaEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(KlimaThemaEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(KlimaThemaEditor.class,
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
            RendererTools.makeReadOnly(txtIcon);
            RendererTools.makeReadOnly(txtFarbe);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     * @param  fall   DOCUMENT ME!
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
        if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
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
