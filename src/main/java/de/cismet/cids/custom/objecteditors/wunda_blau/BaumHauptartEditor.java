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
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

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

import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumHauptartEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumHauptartEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = {"name", "id"};
    public static final String REDUNDANT_TABLE = "baum_hauptart";

    public static final String FIELD__SCHLUESSEL = "schluessel";            // baum_hauptart
    public static final String FIELD__NAME = "name";                        // baum_hauptart
    public static final String FIELD__NAME_BOTANISCH = "name_botanisch";    // baum_hauptart
    public static final String FIELD__ID = "id";                            // baum_hauptart
    public static final String TABLE_NAME = "baum_hauptart";

    public static final String BUNDLE_NONAME = "BaumHauptartEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "BaumHauptartEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_DUPLICATEKEY = "BaumHauptartEditor.prepareForSave().duplicateSchluessel";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumHauptartEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumHauptartEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumHauptartEditor.prepareForSave().JOptionPane.title";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum OtherTableCases {

        //~ Enum constants -----------------------------------------------------

        REDUNDANT_ATT_KEY, REDUNDANT_ATT_NAME
    }

    //~ Instance fields --------------------------------------------------------

    private SwingWorker worker_key;
    private SwingWorker worker_name;

    private Boolean redundantName = false;
    private Boolean redundantKey = false;
    private static String TITLE_NEW_HAUPTART = "eine neue Hauptart anlegen..."; 

    private boolean isEditor = true;
    private final RedundantObjectSearch hauptartSearch = new RedundantObjectSearch(
            REDUNDANT_TOSTRING_TEMPLATE,
            REDUNDANT_TOSTRING_FIELDS,
            null,
            REDUNDANT_TABLE);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel lblName;
    private JLabel lblNameBotanisch;
    private JPanel panContent;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panName;
    private JTextField txtName;
    private JTextField txtNameBotanisch;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumHauptartEditor() {
    }

    /**
     * Creates a new BaumHauptartEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumHauptartEditor(final boolean boolEditor) {
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
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panFillerUnten = new JPanel();
        panContent = new RoundedPanel();
        panName = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblNameBotanisch = new JLabel();
        txtNameBotanisch = new JTextField();
        panFillerUnten1 = new JPanel();

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

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        lblNameBotanisch.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblNameBotanisch.setText("Name botanisch:");
        lblNameBotanisch.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblNameBotanisch, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name_botanisch}"), txtNameBotanisch, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtNameBotanisch, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        panContent.add(panFillerUnten1, gridBagConstraints);

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
                errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_NONAME));
            } else {
                if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                    cidsBean.setProperty(FIELD__SCHLUESSEL, txtName.getText().trim());
                }
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_DUPLICATENAME));
                } else {
                    if (redundantKey) {
                        LOG.warn("Duplicate key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_DUPLICATEKEY));
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
                NbBundle.getMessage(BaumHauptartEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumHauptartEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumHauptartEditor.class,
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
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     * @param  fall   DOCUMENT ME!
     */
    private void checkName(final String field, final OtherTableCases fall) {
        // Worker Aufruf, ob das Objekt schon existiert
        final Collection<String> conditions = new ArrayList<>();
        conditions.add(field + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + cidsBean.getProperty(FIELD__ID));
        valueFromOtherTable(conditions, fall);
    }

    /**
     * DOCUMENT ME!
     */
    private void checkAttributes() {        
        checkName(FIELD__NAME, OtherTableCases.REDUNDANT_ATT_NAME);
        checkName(FIELD__SCHLUESSEL, OtherTableCases.REDUNDANT_ATT_KEY);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public String getTitle() {
       // return cidsBean.toString();
       if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_HAUPTART;
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
     * @param  where        DOCUMENT ME!
     * @param  fall         DOCUMENT ME!
     */
    private void valueFromOtherTable(final Collection<String> where, final OtherTableCases fall) {
        final SwingWorker<Collection<MetaObjectNode>, Void> worker = new SwingWorker<Collection<MetaObjectNode>, Void>() {
            
                @Override
                protected Collection<MetaObjectNode> doInBackground() throws Exception {
                    hauptartSearch.setWhere(where);
                    hauptartSearch.setTable(REDUNDANT_TABLE);
                    return SessionManager.getProxy().customServerSearch(
                            SessionManager.getSession().getUser(),
                            hauptartSearch,
                            getConnectionContext());
                } 

                @Override
                protected void done() {
                    final Collection<MetaObjectNode> check;
                    try {
                        if (!isCancelled()) {
                            check = get();
                            switch (fall) {
                                case REDUNDANT_ATT_KEY: {  // check redundant key
                                    redundantKey = !check.isEmpty();
                                    break;
                                }
                                case REDUNDANT_ATT_NAME: { // check redundant name
                                    redundantName = !check.isEmpty();
                                    break;
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                }
            };
        if (fall.equals(OtherTableCases.REDUNDANT_ATT_NAME)) {
            if (worker_name != null) {
                worker_name.cancel(true);
            }
            worker_name = worker;
            worker_name.execute();
        } else {
            if (worker_key != null) {
                worker_key.cancel(true);
            }
            worker_key = worker;
            worker_key.execute();
        }
    }
}
