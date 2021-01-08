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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import de.cismet.cids.client.tools.DevelopmentTools;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import java.util.Collections;
import java.util.concurrent.ExecutionException;


import javax.swing.*;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objecteditors.utils.TableUtils;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.albo.ComboBoxFilterDialog;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.utils.CidsBeansTableModel;
import de.cismet.cids.custom.wunda_blau.search.server.BaumMeldungLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BaumMeldungSearch;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.converters.SqlDateToUtilDateConverter;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.MissingResourceException;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingx.JXTable;
/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumOrtsterminEditor extends DefaultCustomObjectEditor implements CidsBeanRenderer,
    EditorSaveListener,
    BindingGroupStore,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------
    private MetaClass meldungMetaClass;
    private static final Comparator<Object> COMPARATOR = new Comparator<Object>() {

            @Override
           /* public int compare(final Object o1, final Object o2) {
                return AlphanumComparator.getInstance().compare(String.valueOf(o1), String.valueOf(o2));
            }*/
            public int compare(final Object o1, final Object o2) {
                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
        };   
    public static final String GEOMTYPE = "Point";
    public static final int FOTO_WIDTH = 150;

    private List<CidsBean> teilBeans;
    private static final Logger LOG = Logger.getLogger(BaumOrtsterminEditor.class);
    private static final String[] MELDUNG_COL_NAMES = new String[] {  "Gebiet-Aktenzeichen", "Gebiet-Bemerkung", "Meldungsdatum", "Meldung-Bemerkung" };
    private static final String[] MELDUNG_PROP_NAMES = new String[] {
            "fk_gebiet.aktenzeichen",
            "fk_gebiet.bemerkung",
            "datum",
            "bemerkung"
        };
    private static final Class[] MELDUNG_PROP_TYPES = new Class[] {
            CidsBean.class,
            CidsBean.class, 
            Date.class,
            String.class
        };
    private static final String[] LOADING_COL_NAMES = new String[] { "Die Daten werden geladen......"};
    private static final String[] MUSTSET_COL_NAMES = new String[] { "Die Daten bitte zuweisen......"};
    
    public static final String FIELD__TEILNEHMER = "n_teilnehmer";              // baum_ortstermin
    public static final String FIELD__DATUM = "datum";                          // baum_ortstermin
    public static final String FIELD__ID = "id";                                // baum_ortstermin
    public static final String FIELD__MELDUNG = "fk_meldung";                   // baum_ortstermin
    public static final String FIELD__MELDUNG_ID = "fk_meldung.id";             // baum_meldung
    public static final String FIELD__NAME = "name";                            // baum_teilnehmer
    public static final String FIELD__MELDUNGEN = "n_ortstermine";              // baum_meldung
    public static final String FIELD__TEILNEHMER_OTSTERMIN = "fk_ortstermin";   // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_NAME = "name";                 // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_TELEFON = "telefon";           // baum_teilnehmer
    public static final String FIELD__TEILNEHMER_BEMERKUNG = "bemerkung";       // baum_teilnehmer
    public static final String TABLE_NAME__MELDUNG = "baum_meldung";
    public static final String TABLE_NAME__TEILNEHMER = "baum_teilnehmer"; 
    
    public static final String BUNDLE_PANE_PREFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumGebietEditor.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_TEIL_QUESTION = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().question";
    public static final String BUNDLE_TEIL_TITLE = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().title";
    public static final String BUNDLE_TEIL_ERRORTITLE = "BaumOrtsterminEditor.btnRemoveTeilrActionPerformed().errortitle";
    public static final String BUNDLE_TEIL_ERRORTEXT = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().errortext";
    public static final String BUNDLE_NOMELDUNG = "BaumOrtsterminEditor.prepareForSave().noMeldung";
    
    private static final String[] TEILNEHMER_COL_NAMES = new String[] { "Name", "Telefon", "Bemerkung"};
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static enum OtherTableCases {

        //~ Enum constants -----------------------------------------------------

        SET_VALUE, REDUNDANT_ATT_NAME
    }

    //~ Instance fields --------------------------------------------------------
    
    private MetaClass teilnehmerMetaClass;

    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnAddTeilnehmer;
    private JButton btnChangeGebiet;
    private JButton btnRemTeilnehmer;
    private ComboBoxFilterDialog comboBoxFilterDialogGebiet;
    private DefaultBindableDateChooser dcDatum;
    private Box.Filler filler2;
    private Box.Filler filler7;
    private JPanel jPanelButtons;
    private JScrollPane jScrollPaneMeldung;
    private JScrollPane jScrollPaneTeil;
    private JLabel lblBemerkung;
    private JLabel lblDatum;
    private JLabel lblGebiet_Meldung;
    JLabel lblTeil1;
    private JPanel panFillerUnten;
    JPanel panOrtstermin;
    JPanel panTeil;
    private JPanel panTeilDaten;
    private JPanel panTeilnehmerAdd;
    private RoundedPanel rpTeil;
    private JScrollPane scpBemerkung;
    SemiRoundedPanel semiRoundedPanel7;
    private SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private JTextArea taBemerkung;
    private JXTable xtMeldung;
    private JXTable xtTeil;
    private BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form.
     */
    public BaumOrtsterminEditor() {
    }

    /**
     * Creates a new BaumGebietEditor object.
     *
     * @param  boolEditor  DOCUMENT ME!
     */
    public BaumOrtsterminEditor(final boolean boolEditor) {
        this.isEditor = boolEditor;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        initComponents();
        meldungMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME__MELDUNG,
                connectionContext);
        setReadOnly();
        teilnehmerMetaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                TABLE_NAME__TEILNEHMER,
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

        sqlDateToUtilDateConverter = new SqlDateToUtilDateConverter();
        comboBoxFilterDialogGebiet = new ComboBoxFilterDialog(null, new BaumMeldungLightweightSearch(), "Gebiet-Meldung auswählen", getConnectionContext());
        panFillerUnten = new JPanel();
        panOrtstermin = new JPanel();
        lblDatum = new JLabel();
        dcDatum = new DefaultBindableDateChooser();
        lblBemerkung = new JLabel();
        scpBemerkung = new JScrollPane();
        taBemerkung = new JTextArea();
        lblGebiet_Meldung = new JLabel();
        jScrollPaneMeldung = new JScrollPane();
        xtMeldung = new JXTable();
        jPanelButtons = new JPanel();
        btnChangeGebiet = new JButton();
        filler7 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767));
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

        panOrtstermin.setOpaque(false);
        panOrtstermin.setLayout(new GridBagLayout());

        lblDatum.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblDatum.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblDatum.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panOrtstermin.add(lblDatum, gridBagConstraints);

        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.datum}"), dcDatum, BeanProperty.create("date"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        binding.setConverter(dcDatum.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panOrtstermin.add(dcDatum, gridBagConstraints);

        lblBemerkung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblBemerkung.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblBemerkung.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panOrtstermin.add(lblBemerkung, gridBagConstraints);

        scpBemerkung.setOpaque(false);

        taBemerkung.setLineWrap(true);
        taBemerkung.setRows(2);
        taBemerkung.setWrapStyleWord(true);

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.bemerkung}"), taBemerkung, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        scpBemerkung.setViewportView(taBemerkung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 4, 2, 2);
        panOrtstermin.add(scpBemerkung, gridBagConstraints);

        lblGebiet_Meldung.setFont(new Font("Tahoma", 1, 11)); // NOI18N
        lblGebiet_Meldung.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblGM.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(2, 0, 2, 5);
        panOrtstermin.add(lblGebiet_Meldung, gridBagConstraints);

        xtMeldung.setModel(new OrtsterminMeldungTableModel());
        xtMeldung.setVisibleRowCount(1);
        jScrollPaneMeldung.setViewportView(xtMeldung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(jScrollPaneMeldung, gridBagConstraints);

        jPanelButtons.setOpaque(false);
        jPanelButtons.setLayout(new GridBagLayout());

        btnChangeGebiet.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/tick_32.png"))); // NOI18N
        btnChangeGebiet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnChangeGebietActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        jPanelButtons.add(btnChangeGebiet, gridBagConstraints);
        btnChangeGebiet.setVisible(isEditor);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanelButtons.add(filler7, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        panOrtstermin.add(jPanelButtons, gridBagConstraints);

        panTeil.setOpaque(false);
        panTeil.setPreferredSize(new Dimension(100, 100));
        panTeil.setLayout(new GridBagLayout());

        rpTeil.setLayout(new GridBagLayout());

        semiRoundedPanel7.setBackground(Color.darkGray);
        semiRoundedPanel7.setLayout(new GridBagLayout());

        lblTeil1.setForeground(new Color(255, 255, 255));
        lblTeil1.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblTeil.text")); // NOI18N
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
        panTeilnehmerAdd.setOpaque(false);
        panTeilnehmerAdd.setLayout(new GridBagLayout());

        btnAddTeilnehmer.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddTeilnehmer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddTeilnehmerActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTeilnehmerAdd.add(btnAddTeilnehmer, gridBagConstraints);

        btnRemTeilnehmer.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemTeilnehmer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemTeilnehmerActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 2, 0);
        panTeilnehmerAdd.add(btnRemTeilnehmer, gridBagConstraints);
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
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel7.add(panTeilnehmerAdd, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpTeil.add(semiRoundedPanel7, gridBagConstraints);

        panTeilDaten.setMinimumSize(new Dimension(26, 80));
        panTeilDaten.setLayout(new GridBagLayout());

        xtTeil.setVisibleRowCount(7);
        jScrollPaneTeil.setViewportView(xtTeil);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panTeil.add(rpTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panOrtstermin.add(panTeil, gridBagConstraints);
        panTeil.getAccessibleContext().setAccessibleName("");

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panOrtstermin, gridBagConstraints);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     */
    private void refreshLabels() {
    /*    final CidsBean bean = edMeldung.getCidsBean();

        if (bean != null) {
            lblMeldung.setText("Meldung: " + toString(bean.getProperty("schluessel")) + "  "
                        + toString(bean.getProperty("name")));
        } else {
            lblMeldung.setText("Fläche");
        }
        lstMeldungen.repaint();

        if (edMeldung.getCidsBean() != null) {
            lstMeldungen.setSelectedValue(edMeldung.getCidsBean(), true);
        }*/
    }

    private String toString(final Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }
   
    
    private void btnChangeGebietActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnChangeGebietActionPerformed
        final Object selectedItem = comboBoxFilterDialogGebiet.showAndGetSelected();
        if (selectedItem instanceof CidsBean) {
            final CidsBean meldungBean = (CidsBean)selectedItem;
            setMeldungTable(meldungBean);
            
            xtMeldung.getTableHeader().setForeground(Color.BLACK);
            try {
                cidsBean.setProperty(FIELD__MELDUNG, meldungBean);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }//GEN-LAST:event_btnChangeGebietActionPerformed

    private void btnAddTeilnehmerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddTeilnehmerActionPerformed
        TableUtils.addObjectToTable(xtTeil, TABLE_NAME__TEILNEHMER, getConnectionContext());
    }//GEN-LAST:event_btnAddTeilnehmerActionPerformed

    private void btnRemTeilnehmerActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemTeilnehmerActionPerformed
        TableUtils.removeObjectsFromTable(xtTeil);
    }//GEN-LAST:event_btnRemTeilnehmerActionPerformed


   


    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

         try {
            if (cidsBean.getProperty(FIELD__MELDUNG_ID) == null) {
                LOG.warn("No geom specified. Skip persisting.");
                errorMessage.append(NbBundle.getMessage(BaumGebietEditor.class, BUNDLE_NOMELDUNG));
            }
        } catch (final MissingResourceException ex) {
            LOG.warn("Geom not given.", ex);
            save = false;
        }

        
        if (errorMessage.length() > 0) {
            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
                NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_PREFIX)
                        + errorMessage.toString()
                        + NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_SUFFIX),
                NbBundle.getMessage(BaumOrtsterminEditor.class, BUNDLE_PANE_TITLE),
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
            if (teilBeans != null) {
                Collections.sort((List)teilBeans, COMPARATOR);
            }
        // 8.5.17 s.Simmert: Methodenaufruf, weil sonst die Comboboxen nicht gefüllt werden
            // evtl. kann dies verbessert werden.
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cb,
                getConnectionContext());
            bindingGroup.bind();
            final DivBeanTable teilnehmerModel = new DivBeanTable(
                    isEditor,
                    cidsBean,
                    FIELD__TEILNEHMER,
                    TEILNEHMER_COL_NAMES,
                    TEILNEHMER_PROP_NAMES,                   
                    TEILNEHMER_PROP_TYPES);
            xtTeil.setModel(teilnehmerModel);
            xtTeil.addMouseMotionListener(new MouseAdapter(){
                @Override
		public void mouseMoved(MouseEvent e) {
                    int row=xtTeil.rowAtPoint(e.getPoint());
                    int col=xtTeil.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1){
                        Object value=xtTeil.getValueAt(row, col);
                        if(null!=value && !"".equals(value)){
                            xtTeil.setToolTipText(value.toString());
                        }else{
                            xtTeil.setToolTipText(null);//keinTooltip anzeigen
                        }
                    }
                }
            });
            //searchMeldungen();
            
            xtMeldung.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 12));
            if(cidsBean.getProperty(FIELD__MELDUNG) == null){
                xtMeldung.getTableHeader().setForeground(Color.red);
            }else{
                xtMeldung.getTableHeader().setForeground(Color.BLACK);
                setMeldungTable((CidsBean)cidsBean.getProperty(FIELD__MELDUNG));
            }
            xtMeldung.addMouseMotionListener(new MouseAdapter(){
                @Override
		public void mouseMoved(MouseEvent e) {
                    int row=xtMeldung.rowAtPoint(e.getPoint());
                    int col=xtMeldung.columnAtPoint(e.getPoint());
                    if(row>-1 && col>-1){
                        Object value=xtMeldung.getValueAt(row, col);
                        if(null!=value && !"".equals(value)){
                            xtMeldung.setToolTipText(value.toString());
                        }else{
                            xtMeldung.setToolTipText(null);//keinTooltip anzeigen
                        }
                    }
                }
            });
            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.error("Bean not set.", ex);
            }
    }
    
    
    private void setMeldungTable(CidsBean meldungBean){
        List<CidsBean> meldungBeans = new ArrayList<>();
        meldungBeans.add(meldungBean);
        //xtMeldung.setModel(new OrtsterminMeldungTableModel());
        ((OrtsterminMeldungTableModel)xtMeldung.getModel()).setCidsBeans(meldungBeans);
    }
    
    private void searchMeldungen() {
        xtMeldung.setModel(new LoadingTableModel());

        if (getCidsBean() != null) {
            new SwingWorker<List<CidsBean>, Void>() {

                    @Override
                    protected List<CidsBean> doInBackground() throws Exception {
                        final BaumMeldungSearch search = new BaumMeldungSearch();
                        search.setOrtsterminId((Integer)getCidsBean().getProperty(FIELD__ID));
                        search.setOrtsterminFKMeldung((Integer)getCidsBean().getProperty(FIELD__MELDUNG_ID));

                        final Collection<MetaObjectNode> mons = (Collection)SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(),
                                            search,
                                            getConnectionContext());

                        if (mons == null) {
                            xtMeldung.setModel(new MustSetTableModel());
                            return null;
                        }

                        final List<CidsBean> beans = new ArrayList<>();
                        for (final MetaObjectNode mon : mons) {
                            
                            beans.add(SessionManager.getProxy().getMetaObject(
                                    mon.getObjectId(),
                                    mon.getClassId(),
                                    "WUNDA_BLAU",
                                    getConnectionContext()).getBean());
                        }
                        return beans;
                    }

                    @Override
                    protected void done() {
                        try {
                            final List<CidsBean> beans = get();
                            List<CidsBean> meldungBeans = new ArrayList<>();
                            if (cidsBean.getProperty(FIELD__MELDUNG_ID) != null){
                                for (final CidsBean cb : beans){
                                    if(cb.getProperty(FIELD__ID).toString().equals(cidsBean.getProperty(FIELD__MELDUNG_ID).toString())){
                                       meldungBeans.add(cb);
                                       break;
                                    }
                                }
                            }
                            if(meldungBeans.isEmpty()){
                                xtMeldung.setModel(new MustSetTableModel());
                            }else{
                                xtMeldung.setModel(new OrtsterminMeldungTableModel());
                                ((OrtsterminMeldungTableModel)xtMeldung.getModel()).setCidsBeans(meldungBeans);
                            }
                        } catch (final InterruptedException | ExecutionException ex) {
                            LOG.fatal(ex, ex);
                        }
                    }
                }.execute();
        }
    }
    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(taBemerkung);
        }
        RendererTools.makeReadOnly(xtMeldung);
    }
    
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "baum_ortstermin",
            1,
            800,
            600);
    }

    @Override
    public String getTitle() {
        return String.format("Baumschutzsatzung - Ortstermin: %s", cidsBean.getProperty(FIELD__DATUM));
    }

    @Override
    public void dispose() {
        super.dispose();
     //   ((OrtsterminMeldungTableModel)xtMeldung.getModel()).clear();
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

    class OrtsterminMeldungTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new OrtsterminMeldungTableModel object.
         */
        public OrtsterminMeldungTableModel() {
            super(MELDUNG_PROP_NAMES, MELDUNG_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }  
    class LoadingTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public LoadingTableModel() {
            super( MELDUNG_PROP_NAMES,LOADING_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    } 
    class MustSetTableModel extends CidsBeansTableModel {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LoadingTableModel object.
         */
        public MustSetTableModel() {
            super( MELDUNG_PROP_NAMES,MUSTSET_COL_NAMES, MELDUNG_PROP_TYPES);
        }
    }
}
    

