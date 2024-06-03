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


import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.MissingResourceException;
import java.util.regex.Pattern;
import javax.swing.text.DefaultFormatter;
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
    public static final String FIELD__MAIL = "mail";
    public static final String FIELD__TEL = "telefon";                            
    
    public static final String TABLE_NAME = "ua_verursacher";

    public static final String BUNDLE_NONAME = "UaVerursacherPanel.isOkForSaving().noName";
    public static final String BUNDLE_PANE_PREFIX = "UaVerursacherPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "UaVerursacherPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "UaVerursacherPanel.isOkForSaving().JOptionPane.title";
   
    public static final Pattern TEL_FILLING_PATTERN = Pattern.compile("(|\\+(-|[0-9])*)");
    public static final Pattern TEL_MATCHING_PATTERN = Pattern.compile("\\+[0-9]{1,3}(-[0-9]+){1,}");
    //~ Enums ------------------------------------------------------------------

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    private CidsBean cidsBean;
    private Text saveName;
    private Text saveAdresse;
    private Text saveBem;
    private Text saveTel;
    private Text saveMail;
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
                    case FIELD__MAIL: {
                        if (evt.getNewValue() != saveMail) {
                            setChangeFlag();
                        }
                        break;
                    }
                    case FIELD__TEL: {
                        if (evt.getNewValue() != saveTel) {
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
    
    private final RegexPatternFormatter telPatternFormatter = new RegexPatternFormatter(
            TEL_FILLING_PATTERN,
            TEL_MATCHING_PATTERN);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Box.Filler filler1;
    private Box.Filler filler2;
    private JFormattedTextField ftxtVTelefon;
    private JLabel lblVAdresse;
    private JLabel lblVBemerkung;
    private JLabel lblVMail;
    private JLabel lblVName;
    private JLabel lblVTelefon;
    private JPanel panContent;
    private JPanel panVBemerkung;
    private JPanel panVerursacher;
    private JScrollPane scpVBemerkung;
    private JTextArea taVBemerkung;
    private JTextField txtVAdresse;
    private JTextField txtVMail;
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
        lblVTelefon = new JLabel();
        ftxtVTelefon = new JFormattedTextField(telPatternFormatter);
        lblVMail = new JLabel();
        txtVMail = new JTextField();
        panVBemerkung = new JPanel();
        scpVBemerkung = new JScrollPane();
        taVBemerkung = new JTextArea();
        lblVBemerkung = new JLabel();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
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
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtVName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(txtVName, gridBagConstraints);

        lblVAdresse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVAdresse.setText("Adresse:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVAdresse, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.adresse}"), txtVAdresse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(txtVAdresse, gridBagConstraints);

        lblVTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVTelefon, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.telefon}"), ftxtVTelefon, BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        ftxtVTelefon.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent evt) {
                ftxtVTelefonFocusLost(evt);
            }
        });
        ftxtVTelefon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ftxtVTelefonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(ftxtVTelefon, gridBagConstraints);

        lblVMail.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVMail.setText("Mail:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panVerursacher.add(lblVMail, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.mail}"), txtVMail, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panVerursacher.add(txtVMail, gridBagConstraints);

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
        gridBagConstraints.gridwidth = 4;
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        panVerursacher.add(filler2, gridBagConstraints);

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

    private void ftxtVTelefonFocusLost(FocusEvent evt) {//GEN-FIRST:event_ftxtVTelefonFocusLost
        refreshValidTel();
    }//GEN-LAST:event_ftxtVTelefonFocusLost

    private void ftxtVTelefonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ftxtVTelefonActionPerformed
        refreshValidTel();
    }//GEN-LAST:event_ftxtVTelefonActionPerformed

    
    /**
     * DOCUMENT ME!
     */
    private void refreshValidTel() {
        ftxtVTelefon.setValue(telPatternFormatter.getLastValid());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  okValue  DOCUMENT ME!
     */
    private void saveValidTel(final Object okValue) {
        telPatternFormatter.setLastValid(okValue);
    }
    
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
            setReadOnly();
            saveValidTel(String.valueOf(cidsBean.getProperty(FIELD__TEL)));
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
            RendererTools.makeReadOnly(txtVMail);
            RendererTools.makeReadOnly(ftxtVTelefon);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setSaveValues() {
        saveName = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? ((Text)getCidsBean().getProperty(FIELD__NAME)) : null;
        saveAdresse= (getCidsBean().getProperty(FIELD__NAME) != null)
            ? ((Text)getCidsBean().getProperty(FIELD__ADRESSE)) : null;
        saveBem = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? (Text)getCidsBean().getProperty(FIELD__BEMERKUNG) : null;
        saveTel = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? (Text)getCidsBean().getProperty(FIELD__TEL) : null;
        saveBem = (getCidsBean().getProperty(FIELD__NAME) != null)
            ? (Text)getCidsBean().getProperty(FIELD__MAIL) : null;
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
    
    class RegexPatternFormatter extends DefaultFormatter {

        //~ Instance fields ----------------------------------------------------

        protected java.util.regex.Matcher fillingMatcher;
        protected java.util.regex.Matcher matchingMatcher;
        private Object lastValid = null;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RegexPatternFormatter object.
         *
         * @param  fillingRegex   DOCUMENT ME!
         * @param  matchingRegex  DOCUMENT ME!
         */
        public RegexPatternFormatter(final Pattern fillingRegex, final Pattern matchingRegex) {
            setOverwriteMode(false);
            fillingMatcher = fillingRegex.matcher("");
            matchingMatcher = matchingRegex.matcher("");
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Object stringToValue(final String string) throws java.text.ParseException {
            if ((string == null) || string.isEmpty()) {
                lastValid = null;
                return null;
            }
            fillingMatcher.reset(string);

            if (!fillingMatcher.matches()) {
                throw new java.text.ParseException("does not match regex", 0);
            }

            final Object value = (String)super.stringToValue(string);

            matchingMatcher.reset(string);
            if (matchingMatcher.matches()) {
                lastValid = value;
            }
            return value;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Object getLastValid() {
            return lastValid;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  okValue  DOCUMENT ME!
         */
        public void setLastValid(final Object okValue) {
            if (lastValid == null && !(okValue.equals("null"))) {
                lastValid = okValue;
            }
        }
    }
    
}
