/** *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 *************************************************** */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.server.middleware.types.MetaClass;

import lombok.Getter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.MissingResourceException;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.CidsBeanStore;
import de.cismet.cids.dynamics.Disposable;

import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JFormattedTextField;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * DOCUMENT ME!
 *
 * @author sandra
 * @version $Revision$, $Date$
 */
public class BaumOrtsterminPanel extends javax.swing.JPanel implements Disposable,
        CidsBeanStore,
        ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------
    private static final Logger LOG = Logger.getLogger(BaumOrtsterminPanel.class);
    private static final MetaClass MC__VORORT;

    public static final String FIELD__TEILNEHMER = "n_teilnehmer";            // baum_ortstermin
    public static final String FIELD__ZEIT = "zeit";                          // baum_ortstermin
    public static final String FIELD__VORORT = "fk_vorort";                   // baum_ortstermin
    public static final String FIELD__NAME = "name";                          // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_OTSTERMIN = "fk_ortstermin"; // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_NAME = "name";               // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_TELEFON = "telefon";         // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_BEMERKUNG = "bemerkung";     // baum_teilnehmer
    public static final String FIELD__FK_MELDUNG = "fk_meldung";              // baum_ortstermin
    public static final String FIELD__MDATUM = "datum";                       // baum_meldung
    public static final String PARENT_NAME = "BaumOrtstermin";
    public static final String TABLE_NAME__TEILNEHMER = "baum_teilnehmer";

    public static final String BUNDLE_PANE_PREFIX = "BaumOrtsterminPanel.isOkForSaving().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX = "BaumOrtsterminPanel.isOkForSaving().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumOrtsterminPanel.isOkForSaving().JOptionPane.title";
    public static final String BUNDLE_NODATE = "BaumOrtsterminPanel.isOkForSaving().noDatum";
    public static final String BUNDLE_NOTIME = "BaumOrtsterminPanel.isOkForSaving().noZeit";
    public static final String BUNDLE_WRONGTIME = "BaumOrtsterminPanel.isOkForSaving().wrongZeit";
    public static final String BUNDLE_NONAME = "BaumOrtsterminPanel.isOkForSaving().noName";
    public static final String BUNDLE_NOVORORT = "BaumOrtsterminPanel.isOkForSaving().noVorort";
    public static final String BUNDLE_WRONGTEL = "BaumOrtsterminPanel.isOkForSaving().wrongTelefon";
    public static final String BUNDLE_WHICH = "BaumOrtsterminPanel.isOkForSaving().welcherOrt";
    public static final String BUNDLE_MESSAGE = "BaumOrtsterminPanel.isOkForSaving().welcheMeldung";

    public static final String TEL__PATTERN = "\\+[0-9]{1,3}(-[0-9]+){1,}";

    private static final String[] TEILNEHMER_COL_NAMES = new String[]{
        "Name",
        "Telefon",
        "Bemerkung"
    };
    private static final String[] TEILNEHMER_PROP_NAMES = new String[]{
        FIELD__TEILNEHMER_NAME,
        FIELD__TEILNEHMER_TELEFON,
        FIELD__TEILNEHMER_BEMERKUNG
    };
    private static final Class[] TEILNEHMER_PROP_TYPES = new Class[]{
        String.class,
        String.class,
        String.class
    };

    static {
        final ConnectionContext connectionContext = ConnectionContext.create(
                ConnectionContext.Category.STATIC,
                BaumErsatzPanel.class.getSimpleName());
        MC__VORORT = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "BAUM_VORORT",
                connectionContext);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        panOrtstermin = new JPanel();
        lblVorort = new JLabel();
        cbVorort = new DefaultBindableScrollableComboBox(MC__VORORT);
        lblZeit = new JLabel();
        ftZeit = new JFormattedTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkungOrt = new JTextArea();
        panTeil = new JPanel();
        rpTeil = new RoundedPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblTeil1 = new JLabel();
        panTeilnehmerAdd = new JPanel();
        btnAddTeilnehmer = new JButton();
        btnRemTeilnehmer = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        panTeilDaten = new JPanel();
        jScrollPaneTeil = new JScrollPane();
        xtTeil = new JXTable();
        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            lblDatum = new JLabel();
        }
        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            dcDatum = new DefaultBindableDateChooser();
        }

        FormListener formListener = new FormListener();

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panOrtstermin.setName("panOrtstermin"); // NOI18N
        panOrtstermin.setOpaque(false);
        panOrtstermin.setLayout(new GridBagLayout());

        lblVorort.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblVorort, NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblVorort.text")); // NOI18N
        lblVorort.setName("lblVorort"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 2, 5);
        panOrtstermin.add(lblVorort, gridBagConstraints);

        cbVorort.setFont(new Font("Dialog", 0, 12)); // NOI18N
        cbVorort.setMaximumRowCount(15);
        cbVorort.setAutoscrolls(true);
        cbVorort.setEnabled(false);
        cbVorort.setName("cbVorort"); // NOI18N
        cbVorort.setPreferredSize(new Dimension(100, 24));

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.fk_vorort}"), cbVorort, BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(cbVorort, gridBagConstraints);

        lblZeit.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblZeit, NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblZeit.text")); // NOI18N
        lblZeit.setName("lblZeit"); // NOI18N
        lblZeit.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 4, 5);
        panOrtstermin.add(lblZeit, gridBagConstraints);

        ftZeit.setFormatterFactory(new DefaultFormatterFactory(new DateFormatter(DateFormat.getTimeInstance(DateFormat.SHORT))));
        ftZeit.setMinimumSize(new Dimension(80, 28));
        ftZeit.setName("ftZeit"); // NOI18N
        ftZeit.setPreferredSize(new Dimension(80, 28));
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(ftZeit, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        Mnemonics.setLocalizedText(lblBemerkung, "Bemerkung:");
        lblBemerkung.setName("lblBemerkung"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 0, 2, 5);
        panOrtstermin.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setName("scpBemerkung"); // NOI18N
        scpBemerkung.setOpaque(false);

        taBemerkungOrt.setLineWrap(true);
        taBemerkungOrt.setRows(2);
        taBemerkungOrt.setWrapStyleWord(true);
        taBemerkungOrt.setEnabled(false);
        taBemerkungOrt.setName("taBemerkungOrt"); // NOI18N

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkungOrt, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkungOrt);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(scpBemerkung, gridBagConstraints);

        panTeil.setName("panTeil"); // NOI18N
        panTeil.setOpaque(false);
        panTeil.setPreferredSize(new Dimension(100, 100));
        panTeil.setLayout(new GridBagLayout());

        rpTeil.setName("rpTeil"); // NOI18N
        rpTeil.setLayout(new GridBagLayout());

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setName("semiRoundedPanel7"); // NOI18N
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblTeil1.setForeground(new Color(255, 255, 255));
        lblTeil1.setText("Teilnehmer");
        lblTeil1.setName("lblTeil1"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel7.add(lblTeil1, gridBagConstraints);

        panTeilnehmerAdd.setAlignmentX(0.0F);
        panTeilnehmerAdd.setAlignmentY(1.0F);
        panTeilnehmerAdd.setFocusable(false);
        panTeilnehmerAdd.setName("panTeilnehmerAdd"); // NOI18N
        panTeilnehmerAdd.setOpaque(false);
        panTeilnehmerAdd.setLayout(new GridBagLayout());

        btnAddTeilnehmer.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddTeilnehmer.setBorderPainted(false);
        btnAddTeilnehmer.setContentAreaFilled(false);
        btnAddTeilnehmer.setName("btnAddTeilnehmer"); // NOI18N
        btnAddTeilnehmer.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTeilnehmerAdd.add(btnAddTeilnehmer, gridBagConstraints);

        btnRemTeilnehmer.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemTeilnehmer.setBorderPainted(false);
        btnRemTeilnehmer.setContentAreaFilled(false);
        btnRemTeilnehmer.setName("btnRemTeilnehmer"); // NOI18N
        btnRemTeilnehmer.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTeilnehmerAdd.add(btnRemTeilnehmer, gridBagConstraints);

        filler2.setName("filler2"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panTeilnehmerAdd.add(filler2, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 10);
        semiRoundedPanel7.add(panTeilnehmerAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpTeil.add(semiRoundedPanel7, gridBagConstraints);

        panTeilDaten.setMinimumSize(new Dimension(26, 80));
        panTeilDaten.setName("panTeilDaten"); // NOI18N
        panTeilDaten.setLayout(new GridBagLayout());

        jScrollPaneTeil.setName("jScrollPaneTeil"); // NOI18N

        xtTeil.setName("xtTeil"); // NOI18N
        jScrollPaneTeil.setViewportView(xtTeil);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTeilDaten.add(jScrollPaneTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpTeil.add(panTeilDaten, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTeil.add(rpTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 2);
        panOrtstermin.add(panTeil, gridBagConstraints);

        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
            Mnemonics.setLocalizedText(lblDatum, "Datum:");
            lblDatum.setName("lblDatum"); // NOI18N
            lblDatum.setRequestFocusEnabled(false);
        }
        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 0, 4, 5);
            panOrtstermin.add(lblDatum, gridBagConstraints);
        }

        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            dcDatum.setName("dcDatum"); // NOI18N
        }
        if(this.getBaumChildrenLoader() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() != null &&
            this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            panOrtstermin.add(dcDatum, gridBagConstraints);
        }

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panOrtstermin, gridBagConstraints);

        bindingGroup.bind();
    }

    // Code for dispatching events from components to event handlers.

    private class FormListener implements ActionListener {
        FormListener() {}
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() == btnAddTeilnehmer) {
                BaumOrtsterminPanel.this.btnAddTeilnehmerActionPerformed(evt);
            }
            else if (evt.getSource() == btnRemTeilnehmer) {
                BaumOrtsterminPanel.this.btnRemTeilnehmerActionPerformed(evt);
            }
        }
    }// </editor-fold>//GEN-END:initComponents

    //~ Instance fields --------------------------------------------------------
    private final boolean editor;
    private String uhrzeit;
    private java.util.Date datum;
    @Getter
    private final BaumChildrenLoader baumChildrenLoader;
    //private final PropertyChangeListener dateTimeListener = null;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            setChangeFlag();
        }
    };

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnAddTeilnehmer;
    JButton btnRemTeilnehmer;
    JComboBox<String> cbVorort;
    DefaultBindableDateChooser dcDatum;
    Box.Filler filler2;
    JFormattedTextField ftZeit;
    JScrollPane jScrollPaneTeil;
    JLabel lblBemerkung;
    JLabel lblDatum;
    JLabel lblTeil1;
    JLabel lblVorort;
    JLabel lblZeit;
    JPanel panOrtstermin;
    JPanel panTeil;
    JPanel panTeilDaten;
    JPanel panTeilnehmerAdd;
    RoundedPanel rpTeil;
    JScrollPane scpBemerkung;
    SemiRoundedPanel semiRoundedPanel7;
    JTextArea taBemerkungOrt;
    JXTable xtTeil;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new BaumOrtsterminPanelPanel object.
     */
    public BaumOrtsterminPanel() {
        this(null);
    }

    /**
     * Creates new form BaumOrtsterminPanel.
     *
     * @param bclInstance DOCUMENT ME!
     */
    public BaumOrtsterminPanel(final BaumChildrenLoader bclInstance) {
        this.baumChildrenLoader = bclInstance;
        if (bclInstance != null) {
            this.editor = bclInstance.getParentOrganizer().isEditor();
        } else {
            this.editor = false;
        }
        initComponents();
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void btnAddTeilnehmerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddTeilnehmerActionPerformed
        if (getCidsBean() != null) {
            TableUtils.addObjectToTable(xtTeil, TABLE_NAME__TEILNEHMER, getConnectionContext());
            setChangeFlag();
        }
    }//GEN-LAST:event_btnAddTeilnehmerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    private void btnRemTeilnehmerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemTeilnehmerActionPerformed
        if (getCidsBean() != null) {
            TableUtils.removeObjectsFromTable(xtTeil);
            setChangeFlag();
        }
    }//GEN-LAST:event_btnRemTeilnehmerActionPerformed

    @Override
    public final ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
                ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    @Override
    public void dispose() {
        bindingGroup.unbind();
        cidsBean = null;
    }

    @Override
    public CidsBean getCidsBean() {
        return this.cidsBean;
    }

    /**
     * DOCUMENT ME!
     */
    private void setChangeFlag() {
        if ((getBaumChildrenLoader() != null)
                && (getBaumChildrenLoader().getParentOrganizer() != null)
                && (getBaumChildrenLoader().getParentOrganizer().getCidsBean() != null)) {
            getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isEditor() {
        return this.editor;
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor())) {
            RendererTools.makeReadOnly(taBemerkungOrt);
            cbVorort.setEnabled(false);
            xtTeil.setEnabled(false);
            RendererTools.makeReadOnly(dcDatum);
            panTeilnehmerAdd.setVisible(isEditor());
            RendererTools.makeReadOnly(ftZeit);
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (!(Objects.equals(getCidsBean(), cidsBean))) {
            if (isEditor() && (getCidsBean() != null)) {
                getCidsBean().removePropertyChangeListener(changeListener);
            }
            bindingGroup.unbind();
            this.cidsBean = cidsBean;
            bindingGroup.bind();
            if (getCidsBean() != null) {
                if (isEditor()) {
                    getCidsBean().addPropertyChangeListener(changeListener);
                }
                DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                        bindingGroup,
                        getCidsBean(),
                        getConnectionContext());
            } else {
                cbVorort.setSelectedIndex(-1);
                taBemerkungOrt.setText("");
            }
            
            DateTimeFormListener dateTimeFormListener = new DateTimeFormListener();
            if(this.getBaumChildrenLoader() != null &&
                this.getBaumChildrenLoader().getParentOrganizer() != null &&
                this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
                dcDatum.addPropertyChangeListener(dateTimeFormListener);
            }
            ftZeit.addPropertyChangeListener(dateTimeFormListener);
            
            final DivBeanTable teilnehmerModel = new DivBeanTable(
                    isEditor(),
                    getCidsBean(),
                    FIELD__TEILNEHMER,
                    TEILNEHMER_COL_NAMES,
                    TEILNEHMER_PROP_NAMES,
                    TEILNEHMER_PROP_TYPES);
            xtTeil.setModel(teilnehmerModel);
            xtTeil.addMouseMotionListener(new MouseAdapter() {

                @Override
                public void mouseMoved(final MouseEvent e) {
                    final int row = xtTeil.rowAtPoint(e.getPoint());
                    final int col = xtTeil.columnAtPoint(e.getPoint());
                    if ((row > -1) && (col > -1)) {
                        final Object value = xtTeil.getValueAt(row, col);
                        if ((null != value) && !"".equals(value)) {
                            xtTeil.setToolTipText(value.toString());
                        } else {
                            xtTeil.setToolTipText(null); // keinTooltip anzeigen
                        }
                    }
                }
            });
            panOrtstermin.repaint();
            panOrtstermin.updateUI();
            taBemerkungOrt.updateUI();
        }
        setReadOnly();
        loadDateTime();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
    }

    public void loadDateTime(){
        if(getCidsBean()!= null && getCidsBean().getProperty(FIELD__ZEIT) != null){
            final Calendar calDatumZeit = Calendar.getInstance();
            calDatumZeit.setTime((Date) getCidsBean().getProperty(FIELD__ZEIT));
            datum = calDatumZeit.getTime();
            if(this.getBaumChildrenLoader() != null &&
                this.getBaumChildrenLoader().getParentOrganizer() != null &&
                this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor){
                dcDatum.setDate(datum);
            }
            SimpleDateFormat sdfZeit = new SimpleDateFormat("HH:mm");
            uhrzeit = sdfZeit.format(calDatumZeit.getTime());
            ftZeit.setText("" + uhrzeit);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param edit DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        taBemerkungOrt.setEnabled(edit);
        cbVorort.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     *
     * @param saveOrtsterminBean DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isOkayForSaving(final CidsBean saveOrtsterminBean) {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        // dateTime vorhanden
        try {
            if (saveOrtsterminBean.getProperty(FIELD__ZEIT) == null){
                LOG.warn("No datum specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NODATE));
                save = false;
            } else {
                final Calendar calDatumZeit = Calendar.getInstance();
                calDatumZeit.setTime((Date)saveOrtsterminBean.getProperty(FIELD__ZEIT));
                datum = calDatumZeit.getTime();
                if (datum == null){
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NODATE));
                    save = false;
                }
                SimpleDateFormat sdfZeit = new SimpleDateFormat("HH:mm");
                uhrzeit = sdfZeit.format(calDatumZeit.getTime().getTime());
                if (uhrzeit == null){
                    LOG.warn("No time specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NOTIME));
                    save = false;
                } else {
                    if (calDatumZeit.get(Calendar.HOUR_OF_DAY) < 7 || calDatumZeit.get(Calendar.HOUR_OF_DAY) > 19){
                        LOG.warn("Wrong time specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_WRONGTIME));
                        save = false;
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Datum not given.", ex);
            save = false;
        }
        

        // verantwortlicher vorort vorhanden
        try {
            if (saveOrtsterminBean.getProperty(FIELD__VORORT) == null) {
                LOG.warn("No vorort specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NOVORORT));
                save = false;
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Vorort not given.", ex);
            save = false;
        }
        

        // Ansprechpartner muss einen Namen haben
        try {
            final Collection<CidsBean> teilCollection = saveOrtsterminBean.getBeanCollectionProperty(FIELD__TEILNEHMER);
            for (final CidsBean tBean : teilCollection) {
                if (tBean.getProperty(FIELD__NAME) == null) {
                    LOG.warn("No name specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NONAME));
                    save = false;
                }
                if (tBean.getProperty(FIELD__TEILNEHMER_TELEFON) != null) {
                    if (!(tBean.getProperty(FIELD__TEILNEHMER_TELEFON).toString().matches(TEL__PATTERN))) {
                        LOG.warn("No name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_WRONGTEL))
                                .append(tBean.getProperty(FIELD__TEILNEHMER_TELEFON).toString())
                                .append("<br>");
                        save = false;
                    }
                }
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Teilnehmer not correct.", ex);
            save = false;
        }

        if (errorMessage.length() > 0) {
            if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor) {
                SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_WHICH))
                        .append(formatTag.format(saveOrtsterminBean.getProperty(FIELD__ZEIT)));
                final CidsBean meldungBean = (CidsBean) saveOrtsterminBean.getProperty(FIELD__FK_MELDUNG);
                errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_MESSAGE))
                        .append(formatTag.format(meldungBean.getProperty(FIELD__MDATUM)));
            }
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                    NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_PREFIX)
                    + errorMessage.toString()
                    + NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_SUFFIX),
                    NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_TITLE),
                    JOptionPane.WARNING_MESSAGE);
        }
        return save;
    }
    
    public void writeDateTime() {
        java.util.Date givenDate = null;
        if(baumChildrenLoader.getParentOrganizer() instanceof BaumOrtsterminEditor 
                && dcDatum.getDate() != null){
            givenDate = dcDatum.getDate();
        }else{
            if (getCidsBean().getProperty(FIELD__ZEIT)!= null){
                final Calendar calDatumZeit = Calendar.getInstance();
                calDatumZeit.setTime((Date)getCidsBean().getProperty(FIELD__ZEIT));
                givenDate = calDatumZeit.getTime(); 
            }
        }
        if(givenDate != null){
            Calendar dateTime = Calendar.getInstance();
            dateTime.setTime(givenDate);
            if (ftZeit.getValue() != null){
                final Calendar zeit = GregorianCalendar.getInstance();
                final java.util.Date givenTime = (java.util.Date)ftZeit.getValue();
                zeit.setTime(givenTime);

                dateTime.set(Calendar.HOUR_OF_DAY, zeit.get(Calendar.HOUR_OF_DAY));
                dateTime.set(Calendar.MINUTE, zeit.get(Calendar.MINUTE));
                dateTime.set(Calendar.SECOND, 0);
                dateTime.set(Calendar.MILLISECOND, 0);

                try {
                    getCidsBean().setProperty(FIELD__ZEIT, new java.sql.Timestamp(dateTime.getTime().getTime()));
                } catch (Exception ex) {
                    LOG.warn("No date saved. Skip persisting.", ex);
                }
            }
        }
    }
    
    private class DateTimeFormListener implements ActionListener, PropertyChangeListener {
        DateTimeFormListener() {}
    

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == ftZeit) {
                if (uhrzeit != null){
                    if (!uhrzeit.equals(ftZeit.getText())){
                        setChangeFlag();
                        writeDateTime();
                    } 
                }else {
                    if (ftZeit.getValue() != null){
                        setChangeFlag();
                        writeDateTime();
                    } 
                }

            }
            else if (evt.getSource() == dcDatum) {
                SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                if (datum != null){
                    if (!(formatTag.format(datum).equals(formatTag.format(dcDatum.getDate())))){
                        setChangeFlag();
                        ftZeit.setValue(null);
                        writeDateTime();
                    }
                }else {
                    if (dcDatum.getDate() != null){
                        setChangeFlag();
                        ftZeit.setValue(null);
                        writeDateTime();
                    }
                }

            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }
}
