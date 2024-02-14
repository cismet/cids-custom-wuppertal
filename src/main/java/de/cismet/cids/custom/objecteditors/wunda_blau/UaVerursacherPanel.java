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

import Sirius.server.middleware.types.MetaObject;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;


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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import javax.swing.*;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultCustomObjectEditor;


import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.util.MissingResourceException;
import lombok.Getter;
import org.jdom.Text;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class UaVerursacherPanel extends javax.swing.JPanel implements Disposable,
    CidsBeanStore,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    

    private static final Logger LOG = Logger.getLogger(UaVerursacherPanel.class);

    public static final String FIELD__ID = "id";
    public static final String FIELD__NAME = "name";
    public static final String FIELD__ADRESSE = "adresse";
    public static final String FIELD__BEMERKUNG = "bemerkung";                            
    
    public static final String TABLE_NAME = "ua_verursacher";

    public static final String BUNDLE_NONAME = "UaVerursacherPanel.isOkForSaving().noName";
    public static final String BUNDLE_PANE_PREFIX = "UaVerursacherPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "UaVerursacherPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "UaVerursacherPanel.isOkForSaving().JOptionPane.title";
   

    //~ Enums ------------------------------------------------------------------

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    private CidsBean cidsBean;
    private Text saveName;
    private Text saveAdresse;
    private Text saveBem;
    @Getter private final UaEinsatzEditor ueeInstance;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case FIELD__NAME: {
                        if (evt.getNewValue() != saveName) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__ADRESSE: {
                        if (evt.getNewValue() != saveAdresse) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__BEMERKUNG: {
                        if (evt.getNewValue() != saveBem) {
                            setChangeFlag();
                        }
                        break;
                    }
                    
                    default: {
                        setChangeFlag();
                    }
                }
            }
        };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Box.Filler filler1;
    private JLabel lblVAdresse;
    private JLabel lblVBemerkung;
    private JLabel lblVName;
    private JPanel panContent;
    private JPanel panVBemerkung;
    private JPanel panVerursacher;
    private JScrollPane scpVBemerkung;
    private JTextArea taVBemerkung;
    private JTextField txtVAdresse;
    private JTextField txtVName;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public UaVerursacherPanel() {
        this(null);
    }

    /**
     * Creates a new UaEinsatzPanel object.
     *
     * @param ueeInstance
     */
    public UaVerursacherPanel(final UaEinsatzEditor ueeInstance) {
        this.ueeInstance = ueeInstance;
        if (ueeInstance != null) {
            this.editor = ueeInstance.isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
        
        setReadOnly();
    }

    //~ Methods ----------------------------------------------------------------

  

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panContent = new RoundedPanel();
        panVerursacher = new JPanel();
        lblVName = new JLabel();
        txtVName = new JTextField();
        lblVAdresse = new JLabel();
        txtVAdresse = new JTextField();
        panVBemerkung = new JPanel();
        scpVBemerkung = new JScrollPane();
        taVBemerkung = new JTextArea();
        lblVBemerkung = new JLabel();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));

        setOpaque(false);
        setLayout(new GridBagLayout());

        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setLayout(new GridBagLayout());

        panVerursacher.setOpaque(false);
        panVerursacher.setLayout(new GridBagLayout());

        lblVName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVName.setText("Name:");
        panVerursacher.add(lblVName, new GridBagConstraints());

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtVName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(txtVName, gridBagConstraints);

        lblVAdresse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVAdresse.setText("Adresse:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVAdresse, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.adresse}"), txtVAdresse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(txtVAdresse, gridBagConstraints);

        panVBemerkung.setOpaque(false);
        panVBemerkung.setLayout(new GridBagLayout());

        taVBemerkung.setColumns(20);
        taVBemerkung.setLineWrap(true);
        taVBemerkung.setRows(3);
        taVBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taVBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpVBemerkung.setViewportView(taVBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panVBemerkung.add(scpVBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(panVBemerkung, gridBagConstraints);

        lblVBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 2, 0, 0);
        panContent.add(panVerursacher, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panContent.add(filler1, gridBagConstraints);

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditor() {
        return this.editor;
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }
    
    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        ueeInstance.getCidsBean().setArtificialChangeFlag(true);
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("remove propchange ua_einsatz: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (isEditor() && (getCidsBean() != null)) {
                LOG.info("add propchange ua_einsatz: " + getCidsBean());
                getCidsBean().addPropertyChangeListener(changeListener);
            }
            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            //DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
             //   bindingGroup,
             //   cb,
            //    getConnectionContext());
            bindingGroup.bind(); 
            if ((getCidsBean() != null) && isEditor()) {
                setSaveValues();
            }
            if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
                
            }
            if (isEditor()){
                                
        
            }
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtVName);
            RendererTools.makeReadOnly(txtVAdresse);
            RendererTools.makeReadOnly(taVBemerkung);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveName = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? ((Text)getCidsBean().getProperty(FIELD__NAME)) : null;
        saveAdresse= (getCidsBean().getProperty(FIELD__ADRESSE) != null)
            ? ((Text)getCidsBean().getProperty(FIELD__ADRESSE)) : null;
        saveBem = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? (Text)getCidsBean().getProperty(FIELD__BEMERKUNG) : null;
    }
    
    
    
    @Override
    public void dispose() {
        if (isEditor()) {
            if (getCidsBean() != null) {
                LOG.info("remove propchange ua_verursacher: " + getCidsBean());
                getCidsBean().removePropertyChangeListener(changeListener);
            }
        }
        bindingGroup.unbind();
        this.cidsBean = null;
    }


    public boolean isOkForSaving(CidsBean saveVerursacherBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // Melder
        try {
            if (saveVerursacherBean.getProperty(FIELD__NAME) == null) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(UaEinsatzEditor.class, BUNDLE_NONAME));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("name not given.", ex);
            save = false;
        }
        
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(UaVerursacherPanel.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(UaVerursacherPanel.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(UaVerursacherPanel.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        } 
        return save;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return ueeInstance.getConnectionContext();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class LoadModelCb extends DefaultComboBoxModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadModelCb object.
         */
        public LoadModelCb() {
            super(new String[] { "Die Daten werden geladen......" });
        }
    }

    
}
