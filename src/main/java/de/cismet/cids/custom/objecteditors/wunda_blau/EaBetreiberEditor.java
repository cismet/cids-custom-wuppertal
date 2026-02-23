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
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.wunda_blau.search.server.RedundantObjectSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.SaveVetoable;
import de.cismet.cids.editors.hooks.BeforeSavingHook;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EaBetreiberEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    BeforeSavingHook,
    SaveVetoable,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(EaBetreiberEditor.class);
    public static final String REDUNDANT_TOSTRING_TEMPLATE = "%s";
    public static final String[] REDUNDANT_TOSTRING_FIELDS = { "name", "id" };
    public static final String REDUNDANT_TABLE = "ea_betreiber";

    public static final String FIELD__NAME = "name";
    public static final String FIELD__ID = "id";
    public static final String TABLE_NAME = "ea_betreiber";
    public static final String FIELD__TEL = "telefon";
    public static final String FIELD__HP = "homepage";

    public static final String BUNDLE_NONAME = "EaBetreiberEditor.isOkForSaving().noName";
    public static final String BUNDLE_DUPLICATENAME = "EaBetreiber.isOkForSaving().duplicateName";
    public static final String BUNDLE_PANE_PREFIX = "EaBetreiber.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "EaBetreiber.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "EaBetreiberEditor.isOkForSaving().JOptionPane.title";
    private static String TITLE_NEW_BETREIBER = "einen neuen Betreiber anlegen...";

    public static final String BUNDLE_HP_TITLE = "EaBetreiberEditor.xhHomepageActionPerformed.title";
    public static final String BUNDLE_HP_TEXT = "EaBetreiberEditor.xhHomepageActionPerformed.text";

    public static final Pattern TEL_FILLING_PATTERN = Pattern.compile("(|\\+(-|[0-9])*)");
    public static final Pattern TEL_MATCHING_PATTERN = Pattern.compile("\\+[0-9]{1,3}(-[0-9]+){1,}");

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon statusFalsch = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"));
    private final ImageIcon statusOk = new ImageIcon(
            getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status.png"));

    private Boolean redundantName = false;

    private final boolean editor;

    private final RegexPatternFormatter telPatternFormatter = new RegexPatternFormatter(
            TEL_FILLING_PATTERN,
            TEL_MATCHING_PATTERN);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Box.Filler filler3;
    private JFormattedTextField ftxtTelefon;
    private JLabel lblAStrasse;
    private JLabel lblBemerkung;
    private JLabel lblHNr;
    private JLabel lblHomepage;
    private JLabel lblMail;
    private JLabel lblName;
    private JLabel lblName1;
    private JLabel lblOrt;
    private JLabel lblPlz;
    private JLabel lblTelefon;
    private JLabel lblUrlCheck;
    private JPanel panBemerkung;
    private JPanel panContent;
    private JPanel panFillerUnten;
    private JPanel panFillerUnten1;
    private JPanel panName;
    private JPanel panUrl;
    private JScrollPane scpBemerkung;
    private JTextArea taBemerkung;
    private JTextField txtHNr;
    private JTextField txtHomepage;
    private JTextField txtMail;
    private JTextField txtName;
    private JTextField txtOrt;
    private JTextField txtPlz;
    private JTextField txtStrasse;
    private JXHyperlink xhHomepage;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public EaBetreiberEditor() {
        this(true);
    }

    /**
     * Creates a new EaBetreiberEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public EaBetreiberEditor(final boolean boolEditor) {
        this.editor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void beforeSaving() {
        final RedundantObjectSearch betreiberSearch = new RedundantObjectSearch(
                REDUNDANT_TOSTRING_TEMPLATE,
                REDUNDANT_TOSTRING_FIELDS,
                null,
                REDUNDANT_TABLE);
        final Collection<String> conditions = new ArrayList<>();
        conditions.add(FIELD__NAME + " ilike '" + txtName.getText().trim() + "'");
        conditions.add(FIELD__ID + " <> " + getCidsBean().getProperty(FIELD__ID));

        betreiberSearch.setWhere(conditions);
        try {
            redundantName =
                !(SessionManager.getProxy().customServerSearch(
                        SessionManager.getSession().getUser(),
                        betreiberSearch,
                        getConnectionContext())).isEmpty();
        } catch (ConnectionException e) {
            LOG.warn("problem in check name Betreiber: load values.", e);
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
                errorMessage.append(NbBundle.getMessage(EaBetreiberEditor.class, BUNDLE_NONAME));
                save = false;
            } else {
                if (redundantName) {
                    LOG.warn("Duplicate name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(EaBetreiberEditor.class, BUNDLE_DUPLICATENAME));
                    save = false;
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Name not given.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(EaBetreiberEditor.class,
                    BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(EaBetreiberEditor.class,
                            BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(EaBetreiberEditor.class,
                    BUNDLE_PANE_TITLE),
                JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();

        if (isEditor()) {
            txtHomepage.getDocument().addDocumentListener(new DocumentListener() {

                    // Immer, wenn das Foto geändert wird, wird dieses überprüft und neu geladen.
                    @Override
                    public void insertUpdate(final DocumentEvent e) {
                        doWithUrl();
                    }

                    @Override
                    public void removeUpdate(final DocumentEvent e) {
                        doWithUrl();
                    }

                    @Override
                    public void changedUpdate(final DocumentEvent e) {
                        doWithUrl();
                    }
                });
        }
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
        lblAStrasse = new JLabel();
        txtStrasse = new JTextField();
        lblHNr = new JLabel();
        txtHNr = new JTextField();
        lblOrt = new JLabel();
        txtOrt = new JTextField();
        lblPlz = new JLabel();
        txtPlz = new JTextField();
        lblTelefon = new JLabel();
        ftxtTelefon = new JFormattedTextField(telPatternFormatter);
        lblHomepage = new JLabel();
        txtHomepage = new JTextField();
        xhHomepage = new JXHyperlink();
        panUrl = new JPanel();
        lblUrlCheck = new JLabel();
        lblMail = new JLabel();
        txtMail = new JTextField();
        lblBemerkung = new JLabel();
        panBemerkung = new JPanel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblName1 = new JLabel();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        panFillerUnten1 = new JPanel();

        setAutoscrolls(true);
        setMinimumSize(new Dimension(600, 646));
        setPreferredSize(new Dimension(600, 737));
        setLayout(new GridBagLayout());

        panFillerUnten.setName(""); // NOI18N
        panFillerUnten.setOpaque(false);

        final GroupLayout panFillerUntenLayout = new GroupLayout(panFillerUnten);
        panFillerUnten.setLayout(panFillerUntenLayout);
        panFillerUntenLayout.setHorizontalGroup(panFillerUntenLayout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUntenLayout.setVerticalGroup(panFillerUntenLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGap(0, 0, Short.MAX_VALUE));

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

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.name}"),
                txtName,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtName, gridBagConstraints);

        lblAStrasse.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblAStrasse.setText("Straße:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblAStrasse, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.strasse}"),
                txtStrasse,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtStrasse, gridBagConstraints);

        lblHNr.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHNr.setText("HNr:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblHNr, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.hnr}"),
                txtHNr,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtHNr, gridBagConstraints);

        lblOrt.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblOrt.setText("Ort:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblOrt, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.ort}"),
                txtOrt,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtOrt, gridBagConstraints);

        lblPlz.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblPlz.setText("PLZ:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblPlz, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.plz}"),
                txtPlz,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtPlz, gridBagConstraints);

        lblTelefon.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblTelefon.setText("Telefon:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblTelefon, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.telefon}"),
                ftxtTelefon,
                BeanProperty.create("value"));
        bindingGroup.addBinding(binding);

        ftxtTelefon.addFocusListener(new FocusAdapter() {

                @Override
                public void focusLost(final FocusEvent evt) {
                    ftxtTelefonFocusLost(evt);
                }
            });
        ftxtTelefon.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    ftxtTelefonActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.3;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(ftxtTelefon, gridBagConstraints);

        lblHomepage.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblHomepage.setText("Homepage:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblHomepage, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.homepage}"),
                txtHomepage,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtHomepage, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.homepage}"),
                xhHomepage,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        xhHomepage.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    xhHomepageActionPerformed(evt);
                }
            });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panName.add(xhHomepage, gridBagConstraints);

        panUrl.setOpaque(false);
        panUrl.setLayout(new GridBagLayout());

        lblUrlCheck.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/status-busy.png"))); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        panUrl.add(lblUrlCheck, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(panUrl, gridBagConstraints);

        lblMail.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblMail.setText("Mail:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblMail, gridBagConstraints);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.mail}"),
                txtMail,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(txtMail, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText("Bemerkung:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblBemerkung, gridBagConstraints);

        panBemerkung.setOpaque(false);
        panBemerkung.setLayout(new GridBagLayout());

        taBemerkung.setColumns(20);
        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkung,
                BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBemerkung.add(scpBemerkung, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panName.add(panBemerkung, gridBagConstraints);

        lblName1.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblName1.setText("Name:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panName.add(lblName1, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panName.add(filler3, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panContent.add(panName, gridBagConstraints);

        panFillerUnten1.setName(""); // NOI18N
        panFillerUnten1.setOpaque(false);

        final GroupLayout panFillerUnten1Layout = new GroupLayout(panFillerUnten1);
        panFillerUnten1.setLayout(panFillerUnten1Layout);
        panFillerUnten1Layout.setHorizontalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        panFillerUnten1Layout.setVerticalGroup(panFillerUnten1Layout.createParallelGroup(
                GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

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
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtTelefonFocusLost(final FocusEvent evt) { //GEN-FIRST:event_ftxtTelefonFocusLost
        refreshValidTel();
    }                                                         //GEN-LAST:event_ftxtTelefonFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void ftxtTelefonActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_ftxtTelefonActionPerformed
        refreshValidTel();
    }                                                                //GEN-LAST:event_ftxtTelefonActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void xhHomepageActionPerformed(final ActionEvent evt) { //GEN-FIRST:event_xhHomepageActionPerformed

        try {
            BrowserLauncher.openURL(xhHomepage.getText());
        } catch (final Exception e) {
            LOG.fatal("Problem during opening url", e);
            final ErrorInfo ei = new ErrorInfo(
                    BUNDLE_HP_TITLE,
                    BUNDLE_HP_TEXT,
                    null,
                    null,
                    e,
                    Level.SEVERE,
                    null);
            JXErrorPane.showDialog(this, ei);
        }
    } //GEN-LAST:event_xhHomepageActionPerformed

    /**
     * DOCUMENT ME!
     */
    private void refreshValidTel() {
        ftxtTelefon.setValue(telPatternFormatter.getLastValid());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  okValue  DOCUMENT ME!
     */
    private void saveValidTel(final Object okValue) {
        telPatternFormatter.setLastValid(okValue);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cb) {
        try {
            bindingGroup.unbind();
            this.cidsBean = cb;
            if (cb != null) {
                DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                    bindingGroup,
                    cb,
                    getConnectionContext());
            }
            bindingGroup.bind();
            if (getCidsBean().getProperty(FIELD__HP) != null) {
                checkUrl(getCidsBean().getProperty(FIELD__HP).toString(), lblUrlCheck);
            }
        } catch (final Exception ex) {
            LOG.warn("Error setCidsBean.", ex);
        }
        saveValidTel(String.valueOf(cidsBean.getProperty(FIELD__TEL)));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isEditor() {
        return this.editor;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(txtName);
            RendererTools.makeReadOnly(txtPlz);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtOrt);
            RendererTools.makeReadOnly(ftxtTelefon);
            RendererTools.makeReadOnly(txtMail);
            RendererTools.makeReadOnly(txtOrt);
            RendererTools.makeReadOnly(txtHNr);
            RendererTools.makeReadOnly(txtStrasse);
            RendererTools.makeReadOnly(txtHomepage);
            RendererTools.makeReadOnly(taBemerkung);
            // RendererTools.makeReadOnly(xhHomepage);
            txtHomepage.setVisible(false);
        } else {
            xhHomepage.setVisible(false);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void doWithUrl() {
        // Worker Aufruf, grün/rot
        checkUrl(txtHomepage.getText(), lblUrlCheck);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  url        DOCUMENT ME!
     * @param  showLabel  DOCUMENT ME!
     */
    private void checkUrl(final String url, final JLabel showLabel) {
        showLabel.setIcon(statusFalsch);
        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        final SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    return WebAccessManager.getInstance().checkIfURLaccessible(new URL(url));
                }

                @Override
                protected void done() {
                    final Boolean check;
                    try {
                        check = get();
                        if (check) {
                            showLabel.setIcon(statusOk);
                            showLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            showLabel.setIcon(statusFalsch);
                            showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        showLabel.setIcon(statusFalsch);
                        showLabel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        LOG.warn("URL Check Problem in Worker.", e);
                    }
                }
            };
        worker.execute();
    }

    @Override
    public String getTitle() {
        if (getCidsBean().getMetaObject().getStatus() == MetaObject.NEW) {
            return TITLE_NEW_BETREIBER;
        } else {
            return getCidsBean().toString();
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
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
            if ((lastValid == null) && !(okValue.equals("null"))) {
                lastValid = okValue;
            }
        }
    }
}
