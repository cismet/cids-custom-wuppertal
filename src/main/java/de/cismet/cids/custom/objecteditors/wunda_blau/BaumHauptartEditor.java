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
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;

import org.openide.util.NbBundle;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumHauptartEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    BeforeSavingHook,
    SaveVetoable,
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

    public static final String BUNDLE_NONAME = 
            "BaumHauptartEditor.isOkForSaving().noName";
    public static final String BUNDLE_DUPLICATENAME = 
            "BaumHauptartEditor.isOkForSaving().duplicateName";
    public static final String BUNDLE_DUPLICATEKEY = 
            "BaumHauptartEditor.isOkForSaving().duplicateSchluessel";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumHauptartEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumHauptartEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = 
            "BaumHauptartEditor.isOkForSaving().JOptionPane.title";

    @Override
    public void beforeSaving() {
        final Collection<String> conditions = new ArrayList<>();
        RedundantObjectSearch hauptartSearch = new RedundantObjectSearch(
            REDUNDANT_TOSTRING_TEMPLATE,
            REDUNDANT_TOSTRING_FIELDS,
            null,
            REDUNDANT_TABLE);
        //name nicht redundant
        conditions.add(FIELD__NAME + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        hauptartSearch.setWhere(conditions);
        try{
            redundantName = !(SessionManager.getProxy().customServerSearch(
                            SessionManager.getSession().getUser(),
                            hauptartSearch,
                            getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in check name: load values.", ex);
        } 
        //schluessel nicht redundant
        conditions.clear();
        conditions.add(FIELD__SCHLUESSEL + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        hauptartSearch.setWhere(conditions);
        try{
            redundantKey = !(SessionManager.getProxy().customServerSearch(
                            SessionManager.getSession().getUser(),
                            hauptartSearch,
                            getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in check key: load values.", ex);
        }
        //Schluessel setzen
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            try {
                getCidsBean().setProperty(FIELD__SCHLUESSEL, txtName.getText().trim());
            } catch (Exception ex) {
                LOG.warn("Key not set");
            }
        }
    }

    @Override
    public boolean isOkForSaving() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // name vorhanden
        try {
            if (txtName.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_DUPLICATENAME));
                    save = false;
                } else {
                    if (redundantKey) {
                        LOG.warn("Duplicate key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumHauptartEditor.class, BUNDLE_DUPLICATEKEY));
                        save = false;
                    } 
                }
            }
        } catch (final MissingResourceException ex) {
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
        }
        return save;
    }

    //~ Enums ------------------------------------------------------------------


    //~ Instance fields --------------------------------------------------------
    private Boolean redundantName = false;
    private Boolean redundantKey = false;
    private static String TITLE_NEW_HAUPTART = "eine neue Hauptart anlegen..."; 

    private final boolean editor;
    
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
        this(true);
    }

    /**
     * Creates a new BaumHauptartEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumHauptartEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

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
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
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
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblNameBotanisch, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name_botanisch}"), txtNameBotanisch, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
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
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        add(panContent, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (final Exception ex) {
            LOG.warn("Error setCidsBean", ex);
        }
    }
    
    private boolean isEditor(){
        return this.editor;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtNameBotanisch);
        }
    }

    @Override
    public String getTitle() {
       if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_HAUPTART;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void setTitle(final String string) {
    }
}
