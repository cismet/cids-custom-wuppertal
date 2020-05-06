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

import javax.swing.*;

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
import java.util.concurrent.ExecutionException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import java.text.DecimalFormat;
import java.util.MissingResourceException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobradSteckerEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EmobradSteckerEditor.class);
    
    public static final String FIELD__SCHLUESSEL = "schluessel";                    // emobrad_stecker
    public static final String FIELD__KWATT = "kilowatt";                           // emobrad_stecker
    public static final String FIELD__AMPERE = "ampere";                            // emobrad_stecker
    public static final String FIELD__VOLT= "volt";                                 // emobrad_stecker
    public static final String FIELD__TYP= "typ";                                   // emobrad_stecker
    public static final String FIELD__ID = "id";                                    // emobrad_stecker
    public static final String TABLE_NAME = "emobrad_stecker";
    
    public static final String BUNDLE_NONAME = "EmobradSteckerEditor.prepareForSave().noName";
    public static final String BUNDLE_DUPLICATENAME = "EmobradSteckerEditor.prepareForSave().duplicateName";
    public static final String BUNDLE_DUPLICATEKEY = "EmobradSteckerEditor.prepareForSave().duplicateSchluessel";
    public static final String BUNDLE_NOKWATT = "EmobradSteckerEditor.prepareForSave().noKiloWatt";
    public static final String BUNDLE_WRONGKWATT = "EmobradSteckerEditor.prepareForSave().wrongKiloWatt";
    public static final String BUNDLE_NOAMPERE = "EmobradSteckerEditor.prepareForSave().noAmpere";
    public static final String BUNDLE_WRONGAMPERE = "EmobradSteckerEditor.prepareForSave().wrongAmpere";
    public static final String BUNDLE_NOVOLT = "EmobradSteckerEditor.prepareForSave().noVolt";
    public static final String BUNDLE_WRONGVOLT = "EmobradSteckerEditor.prepareForSave().wrongVolt";
    public static final String BUNDLE_PANE_PREFIX = "EmobradSteckerEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "EmobradSteckerEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EmobradSteckerEditor.prepareForSave().JOptionPane.title";

    //~ Instance fields --------------------------------------------------------
    private SwingWorker worker_key;
    private SwingWorker worker_name;

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum otherTableCases {

        //~ Enum constants -----------------------------------------------------

        REDUNDANTATTKEY, REDUNDANTNAME
    }

    //~ Instance fields --------------------------------------------------------

    private Boolean redundantName = false;
    private Boolean redundantKey = false;
    
    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JFormattedTextField ftxtAmpere;
    private JFormattedTextField ftxtVolt;
    private JLabel lblAmpere;
    private JLabel lblKWatt;
    private JLabel lblTyp;
    private JLabel lblVolt;
    private JPanel panContent;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panName;
    JSpinner spKWatt;
    private JTextField txtTyp;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EmobradSteckerEditor() {
    }

    /**
     * Creates a new EmobradSteckerEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EmobradSteckerEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        
        DocumentListener keyListener = new DocumentListener() {

                // Immer, wenn der "Name" geändert wird, wird dieser überprüft.
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
            };
      
        txtTyp.getDocument().addDocumentListener(keyListener);
        ((JSpinner.DefaultEditor)spKWatt.getEditor()).getTextField().getDocument().addDocumentListener(keyListener);
        ftxtAmpere.getDocument().addDocumentListener(keyListener);
        ftxtVolt.getDocument().addDocumentListener(keyListener);
        
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
        lblTyp = new JLabel();
        txtTyp = new JTextField();
        lblKWatt = new JLabel();
        spKWatt = new JSpinner();
        lblAmpere = new JLabel();
        ftxtAmpere = new JFormattedTextField();
        lblVolt = new JLabel();
        ftxtVolt = new JFormattedTextField();

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

        lblTyp.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTyp.setText("Typ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblTyp, gridBagConstraints);

        txtTyp.setHorizontalAlignment(JTextField.RIGHT);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.typ}"), txtTyp, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtTyp, gridBagConstraints);

        lblKWatt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblKWatt.setText("Kilowatt:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblKWatt, gridBagConstraints);

        spKWatt.setFont(new Font("Dialog", 0, 12)); // NOI18N
        spKWatt.setModel(new SpinnerNumberModel(0.0d, 0.0d, 100.0d, 0.1d));
        spKWatt.setName("spKWatt"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.kilowatt}"), spKWatt, BeanProperty.create("value"));
        binding.setSourceNullValue(0d);
        binding.setSourceUnreadableValue(0d);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(spKWatt, gridBagConstraints);

        lblAmpere.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAmpere.setText("Ampere:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblAmpere, gridBagConstraints);

        ftxtAmpere.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));
        ftxtAmpere.setHorizontalAlignment(JTextField.RIGHT);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ampere}"), ftxtAmpere, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(ftxtAmpere, gridBagConstraints);

        lblVolt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblVolt.setText("Volt:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 0, 5);
        panName.add(lblVolt, gridBagConstraints);

        ftxtVolt.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));
        ftxtVolt.setHorizontalAlignment(JTextField.RIGHT);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.volt}"), ftxtVolt, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(ftxtVolt, gridBagConstraints);

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

        // stecker vorhanden
        try {
            if (txtTyp.getText().trim().isEmpty()) {
                LOG.warn("No name specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradSteckerEditor.class, BUNDLE_NONAME));
            } else {
                if (this.cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                    cidsBean.setProperty(FIELD__SCHLUESSEL, createKey());
                }
                if (redundantName) {
                    LOG.warn("Duplicate attributes specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EmobradSteckerEditor.class, BUNDLE_DUPLICATENAME));
                } else {
                    if (redundantKey) {
                        LOG.warn("Duplicate key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(EmobradSteckerEditor.class, BUNDLE_DUPLICATEKEY));
                    } else {
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        
        // KiloWatt muss angegeben werden
        try {
            if (spKWatt.getValue().equals(0.0)) {
                LOG.warn("No kilowatt specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOKWATT));
            } 
        } catch (final MissingResourceException ex) {
            LOG.warn("KiloWatt not given.", ex);
            save = false;
        }
        
        // Ampere muss angegeben werden
        try {
            if (ftxtAmpere.getText().trim().isEmpty()) {
                LOG.warn("No ampere specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOAMPERE));
            } else {
                try {
                    if (Integer.parseInt(ftxtAmpere.getText()) <= 0){
                        errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGAMPERE));
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong ampere specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGAMPERE));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Ampere not given.", ex);
            save = false;
        }
        
        // Volt muss angegeben werden
        try {
            if (ftxtVolt.getText().trim().isEmpty()) {
                LOG.warn("No volt specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_NOVOLT));
            } else {
                try {
                    if (Integer.parseInt(ftxtVolt.getText()) <= 0){
                        errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGVOLT));
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("Wrong volt specified. Skip persisting.", e);
                    errorMessage.append(NbBundle.getMessage(EmobradLadestationEditor.class, BUNDLE_WRONGVOLT));
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Volt not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EmobradSteckerEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EmobradSteckerEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EmobradSteckerEditor.class,
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
            RendererTools.makeReadOnly(txtTyp);
            RendererTools.makeDoubleSpinnerWithoutButtons(spKWatt, 1);
            RendererTools.makeReadOnly(spKWatt);
            RendererTools.makeReadOnly(ftxtAmpere);
            RendererTools.makeReadOnly(ftxtVolt);
        }
    }

    public String createKey() {
        return txtTyp.getText().trim() + " (" + spKWatt.getValue() + "kW, " + ftxtAmpere.getText().trim() + "A, " + ftxtVolt.getText().trim() + "V)";
    }
            
   
    /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     * @param  fall   DOCUMENT ME!
     */
    private void checkKey(final String field, final String insertValues, final otherTableCases fall) {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(TABLE_NAME,
            " where "
                    + field
                    + " ilike '"
                    + insertValues
                    + "' and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            fall);
    }

     /**
     * DOCUMENT ME!
     *
     * @param  field  DOCUMENT ME!
     * @param  fall   DOCUMENT ME!
     */
    private void checkName(final String where, final otherTableCases fall) {
        // Worker Aufruf, ob das Objekt schon existiert
        valueFromOtherTable(TABLE_NAME,
            where 
                    + " and "
                    + FIELD__ID
                    + " <> "
                    + cidsBean.getProperty(FIELD__ID),
            fall);
    }
    /**
     * DOCUMENT ME!
     */
    private void checkAttributes() {
        checkName (" where " + FIELD__TYP + " ilike '" +  cidsBean.getProperty(FIELD__TYP) + "' and " 
                + FIELD__AMPERE + " = " + cidsBean.getProperty(FIELD__AMPERE) + " and "
                + FIELD__VOLT + " = " + cidsBean.getProperty(FIELD__VOLT) + " and "
                + FIELD__KWATT + " = " + cidsBean.getProperty(FIELD__KWATT), otherTableCases.REDUNDANTNAME);
        checkKey(FIELD__SCHLUESSEL, createKey(), otherTableCases.REDUNDANTATTKEY);
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
                        if (!isCancelled()) {
                            check = get();
                            if (check != null) {
                                switch (fall) {
                                    case REDUNDANTATTKEY: {  // check redundant key
                                        redundantKey = true;
                                        break;
                                    }
                                    case REDUNDANTNAME: { // check redundant name
                                        redundantName = true;
                                        break;
                                    }
                                }
                            } else {
                                switch (fall) {
                                    case REDUNDANTATTKEY: {  // check redundant key
                                        redundantKey = false;
                                        break;
                                    }
                                    case REDUNDANTNAME: { // check redundant name
                                        redundantName = false;
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        LOG.warn("problem in Worker: load values.", e);
                    }
                }
            };
       
        if (fall.equals(otherTableCases.REDUNDANTNAME)){
            if (worker_name != null) {
                worker_name.cancel(true);
            }
            worker_name = worker;
            worker_name.execute();
        } else{
           if (worker_key != null) {
                worker_key.cancel(true);
            }
            worker_key = worker;
            worker_key.execute(); 
        }
    }
}
