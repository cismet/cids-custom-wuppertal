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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;
import org.jdesktop.swingx.JXTable;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumAnsprechpartnerEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    BeforeSavingHook,
    SaveVetoable,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumAnsprechpartnerEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = {"name", "id"};
    public static final String REDUNDANT_TABLE = "baum_ansprechpartner";

    public static final String TABLE_NAME = "baum_ansprechpartner";
    public static final String FIELD__NAME = "name";
    public static final String FIELD__SCHLUESSEL = "schluessel";
    public static final String FIELD__BEMERKUNG = "bemerkung";
    public static final String FIELD__TELEFON = "nummer";
    public static final String FIELD__TELEFONE = "n_telefone";
    public static final String FIELD__ID = "id";
    public static final String TABLE_NAME_TELEFON = "baum_telefon";
    
    private static String TITLE_NEW_AP = "einen neuen Ansprechpartner anlegen..."; 

    public static final String BUNDLE_NOBEM = 
            "BaumAnsprechpartnerEditor.isOkForSaving().noBemerkung";
    public static final String BUNDLE_NOTEL = 
            "BaumAnsprechpartnerEditor.isOkForSaving().noTelefon";
    public static final String BUNDLE_WRONGTEL = 
            "BaumAnsprechpartnerEditor.isOkForSaving().wrongTelefon";
    public static final String BUNDLE_NONAME = 
            "BaumAnsprechpartnerEditor.isOkForSaving().noName";
    public static final String BUNDLE_DUPLICATENAME = 
            "BaumAnsprechpartnerEditor.isOkForSaving().duplicateName";
    public static final String BUNDLE_DUPLICATEKEY = 
            "BaumAnsprechpartnerEditor.isOkForSaving().duplicateSchluessel";
    public static final String BUNDLE_PANE_PREFIX =
        "BaumAnsprechpartnerEditor.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumAnsprechpartnerEditor.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = 
            "BaumAnsprechpartnerEditor.isOkForSaving().JOptionPane.title";

    public static final String BUNDLE_HP_TITLE = 
            "BaumAnsprechpartnerEditor.xhHomepageActionPerformed.title";
    public static final String BUNDLE_HP_TEXT = 
            "BaumAnsprechpartnerEditor.xhHomepageActionPerformed.text";

    public static final String TEL__PATTERN = "\\+[0-9]{1,3}(-[0-9]+){1,}";
    
    private static final String[] TELEFON_COL_NAMES = new String[] { "Telefon", "Bemerkung" };
    private static final String[] TELEFON_PROP_NAMES = new String[] {"nummer", "bemerkung"};
    private static final Class[] TELEFON_PROP_TYPES = new Class[] {
            String.class,
            String.class
        };

    @Override
    public void beforeSaving() {
        RedundantObjectSearch apSearch = new RedundantObjectSearch(
            REDUNDANT_TOSTRING_TEMPLATE,
            REDUNDANT_TOSTRING_FIELDS,
            null,
            REDUNDANT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        //redundanter name
        conditions.add(FIELD__NAME + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        apSearch.setWhere(conditions);
        try {
            redundantName = !(SessionManager.getProxy().customServerSearch(
                    SessionManager.getSession().getUser(),
                    apSearch,
                    getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in check name: load values.", ex);
        }
        //redundanter Schluessel
        conditions.clear();
        conditions.add(FIELD__SCHLUESSEL + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));
        apSearch.setWhere(conditions);
        try {
            redundantKey = !(SessionManager.getProxy().customServerSearch(
                    SessionManager.getSession().getUser(),
                    apSearch,
                    getConnectionContext())).isEmpty();
        } catch (ConnectionException ex) {
            LOG.warn("problem in check key: load values.", ex);
        }     
        //Schluessel setzen 
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            try {
                getCidsBean().setProperty(FIELD__SCHLUESSEL, txtName.getText().trim());
            } catch (Exception ex) {
                LOG.warn("Error: Key not set.", ex);
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
                errorMessage.append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_DUPLICATENAME));
                    save = false;
                } else {
                    if (redundantKey) {
                        LOG.warn("Duplicate key specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_DUPLICATEKEY));
                        save = false;
                    } 
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }
        
        //Telefon muss eine Nummer & eine Bemerkung haben
        try{
            Collection<CidsBean> telCollection =  getCidsBean().getBeanCollectionProperty(FIELD__TELEFONE);
            for (final CidsBean tBean:telCollection){
                if (tBean.getProperty(FIELD__TELEFON)== null) {
                    LOG.warn("No tel specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_NOTEL));
                    save = false;
                } else {
                    if (!(tBean.getProperty(FIELD__TELEFON).toString().matches(TEL__PATTERN))) {
                        LOG.warn("Wrong tel specified. Skip persisting.");
                        errorMessage
                                .append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_WRONGTEL))
                                .append(tBean.getProperty(FIELD__TELEFON).toString());
                        save = false;
                    }
                }
                if (tBean.getProperty(FIELD__BEMERKUNG)== null) {
                    LOG.warn("No bem specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_NOBEM));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Teilnehmer not correct.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumAnsprechpartnerEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumAnsprechpartnerEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumAnsprechpartnerEditor.class, BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    //~ Enums ------------------------------------------------------------------

    //~ Instance fields --------------------------------------------------------
    private Boolean redundantName = false;
    private Boolean redundantKey = false;
    
    private DivBeanTable telefonModel;

    private final boolean editor;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddTelefon;
    private JButton btnRemTelefon;
    private Box.Filler filler1;
    private Box.Filler filler3;
    private JFormattedTextField ftxtPlz;
    private JScrollPane jScrollPaneTelefon;
    private JLabel lblBemerkung;
    private JLabel lblHnr;
    private JLabel lblMail;
    private JLabel lblName;
    private JLabel lblOrt;
    private JLabel lblPlz;
    private JLabel lblStrasse;
    private JPanel panContent;
    private JPanel panDaten;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panTel;
    private JPanel panTelefon;
    private JPanel panTelefonAdd;
    private JScrollPane scpBemerkung;
    private JTextArea taBemerkung;
    private JTextField txtHnr;
    private JTextField txtMail;
    private JTextField txtName;
    private JTextField txtOrt;
    private JTextField txtStrasse;
    private JXTable xtTelefon;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumAnsprechpartnerEditor() {
        this(true);
    }

    /**
     * Creates a new BaumAnsprechpartnerEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumAnsprechpartnerEditor(final boolean boolEditor) {
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

        panContent = new RoundedPanel();
        panDaten = new JPanel();
        lblName = new JLabel();
        txtName = new JTextField();
        lblStrasse = new JLabel();
        txtStrasse = new JTextField();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        lblHnr = new JLabel();
        txtHnr = new JTextField();
        lblPlz = new JLabel();
        ftxtPlz = new JFormattedTextField();
        lblOrt = new JLabel();
        txtOrt = new JTextField();
        panTelefon = new JPanel();
        panTel = new JPanel();
        jScrollPaneTelefon = new JScrollPane();
        xtTelefon = new JXTable();
        panTelefonAdd = new JPanel();
        btnAddTelefon = new JButton();
        btnRemTelefon = new JButton();
        filler1 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        lblMail = new JLabel();
        txtMail = new JTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        panFillerUnten1 = new JPanel();
        panFillerUnten = new JPanel();

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panContent.setAutoscrolls(true);
        panContent.setMaximumSize(new Dimension(450, 2147483647));
        panContent.setMinimumSize(new Dimension(450, 488));
        panContent.setName(""); // NOI18N
        panContent.setOpaque(false);
        panContent.setPreferredSize(new Dimension(450, 961));
        panContent.setLayout(new GridBagLayout());

        panDaten.setOpaque(false);
        panDaten.setLayout(new GridBagLayout());

        lblName.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblName, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtName, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtName, gridBagConstraints);

        lblStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblStrasse, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.strasse}"), txtStrasse, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtStrasse, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panDaten.add(filler3, gridBagConstraints);

        lblHnr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHnr.setText("Hnr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblHnr, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.hausnummer}"), txtHnr, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtHnr, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblPlz, gridBagConstraints);

        ftxtPlz.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#####"))));

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.plz}"), ftxtPlz, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(ftxtPlz, gridBagConstraints);

        lblOrt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOrt.setText("Ort:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblOrt, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.ort}"), txtOrt, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtOrt, gridBagConstraints);

        panTelefon.setOpaque(false);
        panTelefon.setLayout(new GridBagLayout());

        panTel.setMinimumSize(new Dimension(26, 80));
        panTel.setOpaque(false);
        panTel.setLayout(new GridBagLayout());

        xtTelefon.setVisibleRowCount(6);
        jScrollPaneTelefon.setViewportView(xtTelefon);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panTel.add(jScrollPaneTelefon, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTelefon.add(panTel, gridBagConstraints);

        panTelefonAdd.setAlignmentX(0.0F);
        panTelefonAdd.setAlignmentY(1.0F);
        panTelefonAdd.setFocusable(false);
        panTelefonAdd.setOpaque(false);
        panTelefonAdd.setLayout(new GridBagLayout());

        btnAddTelefon.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddTelefon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddTelefonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTelefonAdd.add(btnAddTelefon, gridBagConstraints);

        btnRemTelefon.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemTelefon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemTelefonActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTelefonAdd.add(btnRemTelefon, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        panTelefonAdd.add(filler1, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(2, 5, 2, 2);
        panTelefon.add(panTelefonAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panDaten.add(panTelefon, gridBagConstraints);

        lblMail.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMail.setText("Email:");
        lblMail.setToolTipText("");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblMail, gridBagConstraints);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.mail}"), txtMail, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.7;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(txtMail, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panDaten.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panDaten.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 0, 10);
        panContent.add(panDaten, gridBagConstraints);

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
        gridBagConstraints.weighty = 1.0;
        add(panFillerUnten, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddTelefonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddTelefonActionPerformed
        TableUtils.addObjectToTable(xtTelefon, TABLE_NAME_TELEFON, getConnectionContext());
    }//GEN-LAST:event_btnAddTelefonActionPerformed

    private void btnRemTelefonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemTelefonActionPerformed
        TableUtils.removeObjectsFromTable(xtTelefon);
    }//GEN-LAST:event_btnRemTelefonActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;

            // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                getCidsBean(),
                getConnectionContext());
            bindingGroup.bind();
            telefonModel = new DivBeanTable(
                    isEditor(),
                    getCidsBean(),
                    FIELD__TELEFONE,
                    TELEFON_COL_NAMES,
                    TELEFON_PROP_NAMES,
                    TELEFON_PROP_TYPES);
            xtTelefon.setModel(telefonModel);
        } catch (final Exception ex) {
            LOG.error("Bean not set.", ex);
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
            RendererTools.makeReadOnly(ftxtPlz);
            RendererTools.makeReadOnly(txtMail);
            RendererTools.makeReadOnly(txtOrt);
            RendererTools.makeReadOnly(txtHnr);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(xtTelefon);
            panTelefonAdd.setVisible(isEditor());
        }
    }

    @Override
    public String getTitle() {
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW){
            return TITLE_NEW_AP;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void setTitle(final String string) {
    }        
}
