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

import Sirius.navigator.ui.DescriptionPaneFS;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.awt.Mnemonics;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import de.cismet.cids.custom.objecteditors.utils.BaumChildrenLoader;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.BaumAnsprechpartnerLightweightSearch;

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

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
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
    public static final String FIELD__APARTNER = "arr_ansprechpartner";       // baum_ortstermin
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
    public static final String BUNDLE_PANE_PREFIX_SELECTION =
        "BaumOrtsterminPanel.btnApartnerActionPerformed().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX_SELECTION =
        "BaumOrtsterminPanel.btnApartnerActionPerformed().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE_SELECTION =
        "BaumOrtsterminPanel.btnApartnerActionPerformed().JOptionPane.title";
    public static final String BUNDLE_PANE_SELECTION =
        "BaumOrtsterminPanel.btnApartnerActionPerformed().JOptionPane.message";
    public static final String BUNDLE_AP_QUESTION =
        "BaumOrtsterminPanel.btnRemoveAnsprechpartnerActionPerformed().question";
    public static final String BUNDLE_AP_TITLE = "BaumMeldungPanel.btnRemoveAnsprechpartnerActionPerformed().title";
    public static final String BUNDLE_AP_ERRORTITLE =
        "BaumOrtsterminPanel.btnRemoveAnsprechpartnerActionPerformed().errortitle";
    public static final String BUNDLE_AP_ERRORTEXT =
        "BaumOrtsterminPanel.btnRemoveAnsprechpartnerActionPerformed().errortext";
    public static final String BUNDLE_NOSAVE_MESSAGE = "BaumOrtsterminPanel.noSave().message";
    public static final String BUNDLE_NOSAVE_TITLE = "BaumOrtsterminPanel.noSave().title";

    public static final String TEL__PATTERN = "\\+[0-9]{1,3}(-[0-9]+){1,}";

    private static final String[] TEILNEHMER_COL_NAMES = new String[] {
            "Name",
            "Telefon",
            "Bemerkung"
        };
    private static final String[] TEILNEHMER_PROP_NAMES = new String[] {
            FIELD__TEILNEHMER_NAME,
            FIELD__TEILNEHMER_TELEFON,
            FIELD__TEILNEHMER_BEMERKUNG
        };
    private static final Class[] TEILNEHMER_PROP_TYPES = new Class[] {
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
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        bindingGroup = new BindingGroup();

        comboBoxFilterDialogApartner = new ComboBoxFilterDialog(
                null,
                new BaumAnsprechpartnerLightweightSearch(),
                "Ansprechpartner/Melder auswählen",
                getConnectionContext());
        panOrtstermin = new JPanel();
        lblVorort = new JLabel();
        cbVorort = new DefaultBindableScrollableComboBox(MC__VORORT);
        lblZeit = new JLabel();
        ftZeit = new JFormattedTextField();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkungOrt = new JTextArea();
        panAp = new JPanel();
        scpApartner = new JScrollPane();
        lstApartner = new JList();
        semiRoundedPanel8 = new SemiRoundedPanel();
        lblAp = new JLabel();
        panApAdd = new JPanel();
        btnAddAp = new JButton();
        btnRemAp = new JButton();
        filler3 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        btnApartner = new JButton();
        panTeil = new JPanel();
        rpTeil = new RoundedPanel();
        semiRoundedPanel7 = new SemiRoundedPanel();
        lblTeil1 = new JLabel();
        panTeilnehmerAdd = new JPanel();
        btnAddTeilnehmer = new JButton();
        btnRemTeilnehmer = new JButton();
        filler2 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
        jScrollPaneTeil = new JScrollPane();
        xtTeil = new JXTable();
        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            lblDatum = new JLabel();
        }
        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            dcDatum = new DefaultBindableDateChooser();
        }

        final FormListener formListener = new FormListener();

        comboBoxFilterDialogApartner.setName("comboBoxFilterDialogApartner"); // NOI18N

        setName("Form"); // NOI18N
        setOpaque(false);
        setLayout(new GridBagLayout());

        panOrtstermin.setName("panOrtstermin"); // NOI18N
        panOrtstermin.setOpaque(false);
        panOrtstermin.setLayout(new GridBagLayout());

        lblVorort.setFont(new Font("Tahoma", 1, 11));                                              // NOI18N
        Mnemonics.setLocalizedText(
            lblVorort,
            NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblVorort.text")); // NOI18N
        lblVorort.setName("lblVorort");                                                            // NOI18N
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
        cbVorort.setName("cbVorort");                // NOI18N
        cbVorort.setPreferredSize(new Dimension(100, 24));

        Binding binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.fk_vorort}"),
                cbVorort,
                BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(cbVorort, gridBagConstraints);

        lblZeit.setFont(new Font("Tahoma", 1, 11));                                              // NOI18N
        Mnemonics.setLocalizedText(
            lblZeit,
            NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblZeit.text")); // NOI18N
        lblZeit.setName("lblZeit");                                                              // NOI18N
        lblZeit.setRequestFocusEnabled(false);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 4, 5);
        panOrtstermin.add(lblZeit, gridBagConstraints);

        ftZeit.setFormatterFactory(new DefaultFormatterFactory(
                new DateFormatter(DateFormat.getTimeInstance(DateFormat.SHORT))));
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
        lblBemerkung.setName("lblBemerkung");            // NOI18N
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

        binding = Bindings.createAutoBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                ELProperty.create("${cidsBean.bemerkung}"),
                taBemerkungOrt,
                BeanProperty.create("text"));
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

        panAp.setName("panAp"); // NOI18N
        panAp.setOpaque(false);
        panAp.setPreferredSize(new Dimension(100, 100));
        panAp.setLayout(new GridBagLayout());

        scpApartner.setMinimumSize(new Dimension(258, 66));
        scpApartner.setName("scpApartner"); // NOI18N

        lstApartner.setFont(new Font("Dialog", 0, 12)); // NOI18N
        lstApartner.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstApartner.setName("lstApartner");             // NOI18N

        final ELProperty eLProperty = ELProperty.create("${cidsBean.arr_ansprechpartner}");
        final JListBinding jListBinding = SwingBindings.createJListBinding(
                AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstApartner);
        bindingGroup.addBinding(jListBinding);

        scpApartner.setViewportView(lstApartner);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panAp.add(scpApartner, gridBagConstraints);

        semiRoundedPanel8.setBackground(Color.darkGray);
        semiRoundedPanel8.setName("semiRoundedPanel8"); // NOI18N
        semiRoundedPanel8.setLayout(new GridBagLayout());

        lblAp.setForeground(new Color(255, 255, 255));
        lblAp.setText(NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblAp.text")); // NOI18N
        lblAp.setName("lblAp");                                                                          // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel8.add(lblAp, gridBagConstraints);

        panApAdd.setAlignmentX(0.0F);
        panApAdd.setAlignmentY(1.0F);
        panApAdd.setFocusable(false);
        panApAdd.setName("panApAdd"); // NOI18N
        panApAdd.setOpaque(false);
        panApAdd.setLayout(new GridBagLayout());

        btnAddAp.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddAp.setBorderPainted(false);
        btnAddAp.setContentAreaFilled(false);
        btnAddAp.setName("btnAddAp");                                                                          // NOI18N
        btnAddAp.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panApAdd.add(btnAddAp, gridBagConstraints);

        btnRemAp.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemAp.setBorderPainted(false);
        btnRemAp.setContentAreaFilled(false);
        btnRemAp.setName("btnRemAp");                                                                             // NOI18N
        btnRemAp.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panApAdd.add(btnRemAp, gridBagConstraints);

        filler3.setName("filler3"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panApAdd.add(filler3, gridBagConstraints);

        filler4.setName("filler4"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 10);
        panApAdd.add(filler4, gridBagConstraints);

        btnApartner.setForeground(new Color(255, 255, 255));
        btnApartner.setIcon(new ImageIcon(
                getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/icon-explorerwindow_tuerkis.png"))); // NOI18N
        btnApartner.setBorderPainted(false);
        btnApartner.setContentAreaFilled(false);
        btnApartner.setEnabled(false);
        btnApartner.setFocusPainted(false);
        btnApartner.setName("btnApartner");                                                               // NOI18N
        btnApartner.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        panApAdd.add(btnApartner, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 10);
        semiRoundedPanel8.add(panApAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAp.add(semiRoundedPanel8, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 0, 0, 2);
        panOrtstermin.add(panAp, gridBagConstraints);

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
        lblTeil1.setText(NbBundle.getMessage(BaumOrtsterminPanel.class, "BaumOrtsterminPanel.lblTeil1.text")); // NOI18N
        lblTeil1.setName("lblTeil1");                                                                          // NOI18N
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

        btnAddTeilnehmer.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddTeilnehmer.setBorderPainted(false);
        btnAddTeilnehmer.setContentAreaFilled(false);
        btnAddTeilnehmer.setName("btnAddTeilnehmer");                                                          // NOI18N
        btnAddTeilnehmer.addActionListener(formListener);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTeilnehmerAdd.add(btnAddTeilnehmer, gridBagConstraints);

        btnRemTeilnehmer.setIcon(new ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemTeilnehmer.setBorderPainted(false);
        btnRemTeilnehmer.setContentAreaFilled(false);
        btnRemTeilnehmer.setName("btnRemTeilnehmer");                                                             // NOI18N
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
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        rpTeil.add(semiRoundedPanel7, gridBagConstraints);

        jScrollPaneTeil.setViewportBorder(BorderFactory.createCompoundBorder());
        jScrollPaneTeil.setMinimumSize(new Dimension(285, 100));
        jScrollPaneTeil.setName("jScrollPaneTeil"); // NOI18N
        jScrollPaneTeil.setOpaque(false);

        xtTeil.setName("xtTeil"); // NOI18N
        xtTeil.setVisibleRowCount(8);
        jScrollPaneTeil.setViewportView(xtTeil);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        rpTeil.add(jScrollPaneTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new Insets(5, 0, 0, 2);
        panTeil.add(rpTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 2);
        panOrtstermin.add(panTeil, gridBagConstraints);

        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
            Mnemonics.setLocalizedText(lblDatum, "Datum:");
            lblDatum.setName("lblDatum");                // NOI18N
            lblDatum.setRequestFocusEnabled(false);
        }
        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.anchor = GridBagConstraints.WEST;
            gridBagConstraints.insets = new Insets(2, 0, 4, 5);
            panOrtstermin.add(lblDatum, gridBagConstraints);
        }

        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            dcDatum.setName("dcDatum"); // NOI18N
        }
        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
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

    /**
     * Code for dispatching events from components to event handlers.
     *
     * @version  $Revision$, $Date$
     */
    private class FormListener implements ActionListener {

        /**
         * Creates a new FormListener object.
         */
        FormListener() {
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            if (evt.getSource() == btnAddAp) {
                BaumOrtsterminPanel.this.btnAddApActionPerformed(evt);
            } else if (evt.getSource() == btnRemAp) {
                BaumOrtsterminPanel.this.btnRemApActionPerformed(evt);
            } else if (evt.getSource() == btnApartner) {
                BaumOrtsterminPanel.this.btnApartnerActionPerformed(evt);
            } else if (evt.getSource() == btnAddTeilnehmer) {
                BaumOrtsterminPanel.this.btnAddTeilnehmerActionPerformed(evt);
            } else if (evt.getSource() == btnRemTeilnehmer) {
                BaumOrtsterminPanel.this.btnRemTeilnehmerActionPerformed(evt);
            }
        }
    } // </editor-fold>//GEN-END:initComponents

    @Getter @Setter private static Exception errorNoSave = null;

    //~ Instance fields --------------------------------------------------------

    private final boolean editor;
    private String uhrzeit;
    private java.util.Date datum;
    @Getter private final BaumChildrenLoader baumChildrenLoader;
    // private final PropertyChangeListener dateTimeListener = null;
    private final PropertyChangeListener changeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(final PropertyChangeEvent evt) {
                setChangeFlag();
            }
        };

    private CidsBean cidsBean;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    JButton btnAddAp;
    JButton btnAddTeilnehmer;
    JButton btnApartner;
    JButton btnRemAp;
    JButton btnRemTeilnehmer;
    JComboBox<String> cbVorort;
    ComboBoxFilterDialog comboBoxFilterDialogApartner;
    DefaultBindableDateChooser dcDatum;
    Box.Filler filler2;
    Box.Filler filler3;
    Box.Filler filler4;
    JFormattedTextField ftZeit;
    JScrollPane jScrollPaneTeil;
    JLabel lblAp;
    JLabel lblBemerkung;
    JLabel lblDatum;
    JLabel lblTeil1;
    JLabel lblVorort;
    JLabel lblZeit;
    JList lstApartner;
    JPanel panAp;
    JPanel panApAdd;
    JPanel panOrtstermin;
    JPanel panTeil;
    JPanel panTeilnehmerAdd;
    RoundedPanel rpTeil;
    JScrollPane scpApartner;
    JScrollPane scpBemerkung;
    SemiRoundedPanel semiRoundedPanel7;
    SemiRoundedPanel semiRoundedPanel8;
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
     * @param  bclInstance  DOCUMENT ME!
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
     * @param  evt  DOCUMENT ME!
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
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemTeilnehmerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemTeilnehmerActionPerformed
        if (getCidsBean() != null) {
            TableUtils.removeObjectsFromTable(xtTeil);
            setChangeFlag();
        }
    }//GEN-LAST:event_btnRemTeilnehmerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnApartnerActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnApartnerActionPerformed
        final JDialog dialog = new JDialog((Frame)null,
                "Ansprechpartnerinformationen",
                true);
        final Collection<MetaObjectNode> mons = new ArrayList<>();

        final Object selection = lstApartner.getSelectedValue();
        if (selection != null) {
            if (selection instanceof CidsBean) {
                final CidsBean selectedBean = (CidsBean)selection;
                final MetaObjectNode metaObjectNode = new MetaObjectNode(selectedBean);
                mons.add(metaObjectNode);
            }
            dialog.setContentPane(new DescriptionPaneDialogWrapperPanel(mons));
            dialog.setSize(1200, 800);
            StaticSwingTools.showDialog(this, dialog, true);
        } else {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_PREFIX_SELECTION)
                        + NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_SELECTION)
                        + NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_SUFFIX_SELECTION),
                NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_PANE_TITLE_SELECTION),
                JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnApartnerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddApActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnAddApActionPerformed
        if (getCidsBean() != null) {
            final Object selectedItem = comboBoxFilterDialogApartner.showAndGetSelected();
            try {
                if (selectedItem instanceof CidsBean) {
                    cidsBean = TableUtils.addBeanToCollectionWithMessage(StaticSwingTools.getParentFrame(this),
                            getCidsBean(),
                            FIELD__APARTNER,
                            (CidsBean)selectedItem);
                }
            } catch (Exception ex) {
                LOG.error("Fehler beim Hinzufuegen des Ansprechpartners.", ex);
            } finally {
                getCidsBean().setArtificialChangeFlag(true);
                getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
            }
        }
    }//GEN-LAST:event_btnAddApActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemApActionPerformed(final ActionEvent evt) {//GEN-FIRST:event_btnRemApActionPerformed
        if (getCidsBean() != null) {
            final Object selection = lstApartner.getSelectedValue();
            if (selection != null) {
                final int answer = JOptionPane.showConfirmDialog(StaticSwingTools.getParentFrame(this),
                        NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_QUESTION),
                        NbBundle.getMessage(BaumMeldungPanel.class, BUNDLE_AP_TITLE),
                        JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    try {
                        cidsBean = TableUtils.deleteItemFromList(getCidsBean(), FIELD__APARTNER, selection, false);
                        getCidsBean().setArtificialChangeFlag(true);
                        getBaumChildrenLoader().getParentOrganizer().getCidsBean().setArtificialChangeFlag(true);
                    } catch (Exception ex) {
                        final ErrorInfo ei = new ErrorInfo(
                                BUNDLE_AP_ERRORTITLE,
                                BUNDLE_AP_ERRORTEXT,
                                null,
                                null,
                                ex,
                                Level.SEVERE,
                                null);
                        JXErrorPane.showDialog(this, ei);
                    }
                }
            }
        }
    }//GEN-LAST:event_btnRemApActionPerformed

    @Override
    public final ConnectionContext getConnectionContext() {
        return ((baumChildrenLoader != null) && (baumChildrenLoader.getParentOrganizer() != null))
            ? baumChildrenLoader.getParentOrganizer().getConnectionContext() : null;
    }

    @Override
    public void dispose() {
        if ((this.getBaumChildrenLoader() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                    && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
            dcDatum.removeAll();
        }
        comboBoxFilterDialogApartner.dispose();
        baumChildrenLoader.clearAllMaps();
        bindingGroup.unbind();
        if (isEditor() && (getCidsBean() != null)) {
            getCidsBean().removePropertyChangeListener(changeListener);
        }
        ftZeit.removeAll();
        cidsBean = null;
        xtTeil.removeAll();
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
            RendererTools.makeReadOnly(taBemerkungOrt);
            cbVorort.setEnabled(false);
            xtTeil.setEnabled(false);
            RendererTools.makeReadOnly(dcDatum);
            panTeilnehmerAdd.setVisible(isEditor());
            RendererTools.makeReadOnly(ftZeit);
            RendererTools.makeReadOnly(lstApartner);
            btnAddAp.setVisible(false);
            btnRemAp.setVisible(false);
        }
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        try {
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

                final DateTimeFormListener dateTimeFormListener = new DateTimeFormListener();
                if ((this.getBaumChildrenLoader() != null)
                            && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                            && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
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
        } catch (Exception ex) {
            LOG.error("Bean not set", ex);
            if (isEditor()) {
                setErrorNoSave(ex);
                noSave();
            }
        }
        setReadOnly();
        loadDateTime();
        if (isEditor()) {
            nullNoEdit(getCidsBean() != null);
        }
        btnApartner.setEnabled(getCidsBean() != null);
    }

    /**
     * DOCUMENT ME!
     */
    public void noSave() {
        final ErrorInfo info = new ErrorInfo(
                NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NOSAVE_TITLE),
                NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NOSAVE_MESSAGE),
                null,
                null,
                getErrorNoSave(),
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(BaumOrtsterminPanel.this, info);
    }

    /**
     * DOCUMENT ME!
     */
    public void loadDateTime() {
        if ((getCidsBean() != null) && (getCidsBean().getProperty(FIELD__ZEIT) != null)) {
            final Calendar calDatumZeit = Calendar.getInstance();
            calDatumZeit.setTime((Date)getCidsBean().getProperty(FIELD__ZEIT));
            datum = calDatumZeit.getTime();
            if ((this.getBaumChildrenLoader() != null)
                        && (this.getBaumChildrenLoader().getParentOrganizer() != null)
                        && (this.getBaumChildrenLoader().getParentOrganizer() instanceof BaumOrtsterminEditor)) {
                dcDatum.setDate(datum);
            }
            final SimpleDateFormat sdfZeit = new SimpleDateFormat("HH:mm");
            uhrzeit = sdfZeit.format(calDatumZeit.getTime());
            ftZeit.setText("" + uhrzeit);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  edit  DOCUMENT ME!
     */
    private void nullNoEdit(final boolean edit) {
        taBemerkungOrt.setEnabled(edit);
        cbVorort.setEnabled(edit);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   saveOrtsterminBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isOkayForSaving(final CidsBean saveOrtsterminBean) {
        if (getErrorNoSave() != null) {
            noSave();
            return false;
        } else {
            boolean save = true;
            final StringBuilder errorMessage = new StringBuilder();

            // dateTime vorhanden
            try {
                if (saveOrtsterminBean.getProperty(FIELD__ZEIT) == null) {
                    LOG.warn("No datum specified. Skip persisting.");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NODATE));
                    save = false;
                } else {
                    final Calendar calDatumZeit = Calendar.getInstance();
                    calDatumZeit.setTime((Date)saveOrtsterminBean.getProperty(FIELD__ZEIT));
                    datum = calDatumZeit.getTime();
                    if (datum == null) {
                        LOG.warn("No datum specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NODATE));
                        save = false;
                    }
                    final SimpleDateFormat sdfZeit = new SimpleDateFormat("HH:mm");
                    uhrzeit = sdfZeit.format(calDatumZeit.getTime().getTime());
                    if (uhrzeit == null) {
                        LOG.warn("No time specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NOTIME));
                        save = false;
                    } else {
                        if ((calDatumZeit.get(Calendar.HOUR_OF_DAY) < 7)
                                    || (calDatumZeit.get(Calendar.HOUR_OF_DAY) > 19)) {
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
                final Collection<CidsBean> teilCollection = saveOrtsterminBean.getBeanCollectionProperty(
                        FIELD__TEILNEHMER);
                for (final CidsBean tBean : teilCollection) {
                    if (tBean.getProperty(FIELD__NAME) == null) {
                        LOG.warn("No name specified. Skip persisting.");
                        errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_NONAME));
                        save = false;
                    }
                    if (tBean.getProperty(FIELD__TEILNEHMER_TELEFON) != null) {
                        if (!tBean.getProperty(FIELD__TEILNEHMER_TELEFON).toString().isEmpty()) {
                            if (!(tBean.getProperty(FIELD__TEILNEHMER_TELEFON).toString().matches(TEL__PATTERN))) {
                                LOG.warn("No name specified. Skip persisting.");
                                errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_WRONGTEL))
                                        .append(tBean.getProperty(FIELD__TEILNEHMER_TELEFON).toString())
                                        .append("<br>");
                                save = false;
                            }
                        }
                    }
                }
            } catch (final MissingResourceException ex) {
                LOG.warn("Teilnehmer not correct.", ex);
                save = false;
            }

            if (errorMessage.length() > 0) {
                if (baumChildrenLoader.getParentOrganizer() instanceof BaumGebietEditor) {
                    final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                    errorMessage.append(NbBundle.getMessage(BaumOrtsterminPanel.class, BUNDLE_WHICH))
                            .append(formatTag.format(saveOrtsterminBean.getProperty(FIELD__ZEIT)));
                    final CidsBean meldungBean = (CidsBean)saveOrtsterminBean.getProperty(FIELD__FK_MELDUNG);
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
    }

    /**
     * DOCUMENT ME!
     */
    public void writeDateTime() {
        java.util.Date givenDate = null;
        if ((baumChildrenLoader.getParentOrganizer() instanceof BaumOrtsterminEditor)
                    && (dcDatum.getDate() != null)) {
            givenDate = dcDatum.getDate();
        } else {
            if ((getCidsBean() != null)
                        && (getCidsBean().getProperty(FIELD__ZEIT) != null)) {
                final Calendar calDatumZeit = Calendar.getInstance();
                calDatumZeit.setTime((Date)getCidsBean().getProperty(FIELD__ZEIT));
                givenDate = calDatumZeit.getTime();
            }
        }
        if (givenDate != null) {
            final Calendar dateTime = Calendar.getInstance();
            dateTime.setTime(givenDate);
            if (ftZeit.getValue() != null) {
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public final class DescriptionPaneDialogWrapperPanel extends JPanel {

        //~ Instance fields ----------------------------------------------------

        private final DescriptionPaneFS pane = new DescriptionPaneFS();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TestBlubbPanel object.
         *
         * @param  mons  DOCUMENT ME!
         */
        private DescriptionPaneDialogWrapperPanel(final Collection<MetaObjectNode> mons) {
            setLayout(new BorderLayout());
            add(pane, BorderLayout.CENTER);
            pane.gotoMetaObjectNodes(mons.toArray(new MetaObjectNode[0]));
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class DateTimeFormListener implements ActionListener, PropertyChangeListener {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DateTimeFormListener object.
         */
        DateTimeFormListener() {
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            if (evt.getSource() == ftZeit) {
                if (uhrzeit != null) {
                    if (!uhrzeit.equals(ftZeit.getText())) {
                        setChangeFlag();
                        writeDateTime();
                    }
                } else {
                    if (ftZeit.getValue() != null) {
                        setChangeFlag();
                        writeDateTime();
                    }
                }
            } else if (evt.getSource() == dcDatum) {
                final SimpleDateFormat formatTag = new SimpleDateFormat("dd.MM.yy");
                if (datum != null) {
                    if (!(formatTag.format(datum).equals(formatTag.format(dcDatum.getDate())))) {
                        setChangeFlag();
                        ftZeit.setValue(null);
                        writeDateTime();
                    }
                } else {
                    if (dcDatum.getDate() != null) {
                        setChangeFlag();
                        ftZeit.setValue(null);
                        writeDateTime();
                    }
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
        }
    }
}
