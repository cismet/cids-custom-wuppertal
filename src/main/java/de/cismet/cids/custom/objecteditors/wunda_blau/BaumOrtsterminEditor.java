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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import com.vividsolutions.jts.geom.Point;
import de.cismet.cids.client.tools.DevelopmentTools;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.BindingGroup;

import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

import java.awt.BorderLayout;
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
import javax.swing.text.DefaultFormatter;

import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.BindingGroupStore;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;


import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.SemiRoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;

import static de.cismet.cids.custom.objecteditors.utils.TableUtils.getOtherTableValue;
import static de.cismet.cids.custom.objecteditors.wunda_blau.EmobLadestationEditor.TABLE_NAME_STECKDOSENTYP;
import de.cismet.cids.custom.objectrenderer.utils.DivBeanTable;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
import de.cismet.cids.editors.DefaultBindableDateChooser;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.converters.SqlDateToUtilDateConverter;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import javax.swing.AbstractListModel;
import javax.swing.JList;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
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
    private static final String[] MELDUNG_COL_NAMES = new String[] { "Meldungsdatum", "Gebiet-Aktenzeichen" };
    private static final String[] MELDUNG_PROP_NAMES = new String[] {
            "datum",
            "fk_gebiet"
        };
    private static final Class[] MELDUNG_PROP_TYPES = new Class[] {
            Date.class,
            CidsBean.class
        };
    
    
    public static final String FIELD__TEILNEHMER = "n_teilnehmer";              // baum_ortstermin
    public static final String FIELD__DATUM = "datum";                          // baum_ortstermin
    public static final String FIELD__MELDUNG = "fk_meldung";                   // baum_ortstermin
    public static final String FIELD__NAME = "name";                            // baum_teilnehmer
    public static final String TABLE_NAME__MELDUNG = "baum_meldung"; 
    
    public static final String BUNDLE_PANE_PREFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.prefix";
    public static final String BUNDLE_PANE_SUFFIX =
        "BaumGebietEditor.prepareForSave().JOptionPane.message.suffix";
    public static final String BUNDLE_PANE_TITLE = "BaumGebietEditor.prepareForSave().JOptionPane.title";
    public static final String BUNDLE_TEIL_QUESTION = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().question";
    public static final String BUNDLE_TEIL_TITLE = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().title";
    public static final String BUNDLE_TEIL_ERRORTITLE = "BaumOrtsterminEditor.btnRemoveTeilrActionPerformed().errortitle";
    public static final String BUNDLE_TEIL_ERRORTEXT = "BaumOrtsterminEditor.btnRemoveTeilActionPerformed().errortext";

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
    
    private SwingWorker worker_name;
    private SwingWorker worker_versatz;


    private boolean isEditor = true;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private BaumTeilnehmerPanel baumTeilnehmerPanel1;
    JButton btnAddNewTeil;
    JButton btnRemoveTeil;
    JButton btnTeilMenAbort;
    JButton btnTeilMenOk;
    private JComboBox cbMeldung;
    private DefaultBindableDateChooser dcDatum;
    JDialog dlgAddTeil;
    private Box.Filler filler4;
    private JPanel jPanel1;
    private JScrollPane jScrollPaneMeldung;
    private JLabel lblBemerkung;
    private JLabel lblDatum;
    private JLabel lblGebiet_Meldung;
    JLabel lblTeil;
    JLabel lblTeilAuswaehlen;
    JLabel lblTeiln;
    JList lstTeil;
    JPanel panAddTeil;
    private JPanel panControlsNewTeil;
    private JPanel panFillerUnten;
    JPanel panMenTeilButtons;
    JPanel panOrtstermin;
    JPanel panTeil;
    JPanel panTeilMain;
    private RoundedPanel rpTeilinfo;
    RoundedPanel rpTeilliste;
    private JScrollPane scpBemerkung;
    JScrollPane scpLaufendeTeil;
    SemiRoundedPanel semiRoundedPanel6;
    private SemiRoundedPanel semiRoundedPanelTeil;
    private SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private JTextArea taBemerkung;
    JTextField txtFirma;
    JTextField txtTeil;
    private JXTable xtMeldung;
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
        dlgAddTeil = new JDialog();
        panAddTeil = new JPanel();
        lblTeilAuswaehlen = new JLabel();
        panMenTeilButtons = new JPanel();
        btnTeilMenAbort = new JButton();
        btnTeilMenOk = new JButton();
        txtTeil = new JTextField();
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
        panTeil = new JPanel();
        rpTeilliste = new RoundedPanel();
        scpLaufendeTeil = new JScrollPane();
        lstTeil = new JList();
        semiRoundedPanelTeil = new SemiRoundedPanel();
        lblTeiln = new JLabel();
        panControlsNewTeil = new JPanel();
        btnAddNewTeil = new JButton();
        btnRemoveTeil = new JButton();
        rpTeilinfo = new RoundedPanel();
        semiRoundedPanel6 = new SemiRoundedPanel();
        lblTeil = new JLabel();
        panTeilMain = new JPanel();
        baumTeilnehmerPanel1 = new BaumTeilnehmerPanel();
        filler4 = new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
        final MetaObject[] meldung = ObjectRendererUtils.getLightweightMetaObjectsForTable("baum_meldung", new String[]{"datum"}, getConnectionContext());
        if(meldung != null) {
            Arrays.sort(meldung);
            cbMeldung = new JComboBox(meldung);
        }
        jPanel1 = new JPanel();
        txtFirma = new JTextField();

        dlgAddTeil.setTitle(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.dlgAddTeil.title")); // NOI18N
        dlgAddTeil.setModal(true);

        panAddTeil.setMaximumSize(new Dimension(180, 120));
        panAddTeil.setMinimumSize(new Dimension(180, 120));
        panAddTeil.setPreferredSize(new Dimension(335, 120));
        panAddTeil.setRequestFocusEnabled(false);
        panAddTeil.setLayout(new GridBagLayout());

        lblTeilAuswaehlen.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblTeilAuswaehlen.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panAddTeil.add(lblTeilAuswaehlen, gridBagConstraints);

        panMenTeilButtons.setLayout(new GridBagLayout());

        btnTeilMenAbort.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.btnTeilMenAbort.text")); // NOI18N
        btnTeilMenAbort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnTeilMenAbortActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenTeilButtons.add(btnTeilMenAbort, gridBagConstraints);

        btnTeilMenOk.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.btnTeilMenOk.text")); // NOI18N
        btnTeilMenOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnTeilMenOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panMenTeilButtons.add(btnTeilMenOk, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddTeil.add(panMenTeilButtons, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panAddTeil.add(txtTeil, gridBagConstraints);

        dlgAddTeil.getContentPane().add(panAddTeil, BorderLayout.CENTER);

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

        xtMeldung.setVisibleRowCount(2);
        jScrollPaneMeldung.setViewportView(xtMeldung);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        panOrtstermin.add(jScrollPaneMeldung, gridBagConstraints);

        panTeil.setMinimumSize(new Dimension(197, 150));
        panTeil.setOpaque(false);
        panTeil.setPreferredSize(new Dimension(217, 150));
        panTeil.setLayout(new GridBagLayout());

        rpTeilliste.setMinimumSize(new Dimension(90, 202));
        rpTeilliste.setPreferredSize(new Dimension(90, 202));
        rpTeilliste.setLayout(new GridBagLayout());

        lstTeil.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstTeil.setFixedCellWidth(75);

        ELProperty eLProperty = ELProperty.create("${cidsBean." + FIELD__TEILNEHMER + "}");
        JListBinding jListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, lstTeil);
        bindingGroup.addBinding(jListBinding);

        scpLaufendeTeil.setViewportView(lstTeil);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        rpTeilliste.add(scpLaufendeTeil, gridBagConstraints);

        semiRoundedPanelTeil.setBackground(Color.darkGray);
        semiRoundedPanelTeil.setMinimumSize(new Dimension(90, 25));
        semiRoundedPanelTeil.setPreferredSize(new Dimension(90, 25));
        semiRoundedPanelTeil.setLayout(new GridBagLayout());

        lblTeiln.setForeground(new Color(255, 255, 255));
        lblTeiln.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblTeiln.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanelTeil.add(lblTeiln, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpTeilliste.add(semiRoundedPanelTeil, gridBagConstraints);

        panControlsNewTeil.setOpaque(false);
        panControlsNewTeil.setLayout(new GridBagLayout());

        btnAddNewTeil.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddNewTeil.setMaximumSize(new Dimension(39, 20));
        btnAddNewTeil.setMinimumSize(new Dimension(39, 20));
        btnAddNewTeil.setPreferredSize(new Dimension(39, 25));
        btnAddNewTeil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnAddNewTeilActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewTeil.add(btnAddNewTeil, gridBagConstraints);

        btnRemoveTeil.setIcon(new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveTeil.setMaximumSize(new Dimension(39, 20));
        btnRemoveTeil.setMinimumSize(new Dimension(39, 20));
        btnRemoveTeil.setPreferredSize(new Dimension(39, 25));
        btnRemoveTeil.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnRemoveTeilActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panControlsNewTeil.add(btnRemoveTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(0, 0, 5, 0);
        rpTeilliste.add(panControlsNewTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(0, 0, 0, 5);
        panTeil.add(rpTeilliste, gridBagConstraints);

        rpTeilinfo.setLayout(new GridBagLayout());

        semiRoundedPanel6.setBackground(Color.darkGray);
        semiRoundedPanel6.setLayout(new GridBagLayout());

        lblTeil.setForeground(new Color(255, 255, 255));
        lblTeil.setText(NbBundle.getMessage(BaumOrtsterminEditor.class, "BaumOrtsterminEditor.lblTeil.text")); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(lblTeil, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpTeilinfo.add(semiRoundedPanel6, gridBagConstraints);

        panTeilMain.setOpaque(false);
        panTeilMain.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, lstTeil, ELProperty.create("${selectedElement}"), baumTeilnehmerPanel1, BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panTeilMain.add(baumTeilnehmerPanel1, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        panTeilMain.add(filler4, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpTeilinfo.add(panTeilMain, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new Insets(0, 5, 0, 0);
        panTeil.add(rpTeilinfo, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 0, 0, 0);
        panOrtstermin.add(panTeil, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        panOrtstermin.add(cbMeldung, gridBagConstraints);

        jPanel1.setLayout(new GridBagLayout());

        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, this, ELProperty.create("${cidsBean.name}"), txtFirma, BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 1808;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        jPanel1.add(txtFirma, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panOrtstermin.add(jPanel1, gridBagConstraints);

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
   
    
    private void btnAddNewTeilActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnAddNewTeilActionPerformed
        try {
            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(BaumOrtsterminEditor.this), dlgAddTeil, true);
        } catch (Exception e) {
            LOG.error("Cannot add new BaumOrtstermin object", e);
        }
    }//GEN-LAST:event_btnAddNewTeilActionPerformed

    private void btnRemoveTeilActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnRemoveTeilActionPerformed
        final Object selectedObject = lstTeil.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            //final List<CidsBean> meldungBeans = cidsBean.getBeanCollectionProperty(FIELD__MELDUNGEN);

            if (teilBeans != null) {
                teilBeans.remove((CidsBean)selectedObject);
                //((CustomJListModel)lstMeldungen.getModel()).refresh();
                //lstMeldungen.getSelectionModel().clearSelection();
                if (teilBeans != null && teilBeans.size() > 0) {
                    lstTeil.setSelectedIndex(0);
                }else{
                    lstTeil.clearSelection();
                }
            }
        }
    }//GEN-LAST:event_btnRemoveTeilActionPerformed

    private void btnTeilMenAbortActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnTeilMenAbortActionPerformed
        dlgAddTeil.setVisible(false);
        txtTeil.setText("");
    }//GEN-LAST:event_btnTeilMenAbortActionPerformed

    private void btnTeilMenOkActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnTeilMenOkActionPerformed
        try{
            //meldungsBean erzeugen und vorbelegen:
            final CidsBean beanTeil = CidsBean.createNewCidsBeanFromTableName(
                "WUNDA_BLAU",
                "BAUM_Teilnehmer",
                getConnectionContext());
            beanTeil.setProperty(FIELD__NAME, txtTeil.getText());

            //Meldungen erweitern:
            teilBeans.add(beanTeil);

           
            lstTeil.setSelectedValue(beanTeil, true);

        } catch (Exception ex) {
            LOG.error(ex, ex);
        } finally {
            dlgAddTeil.setVisible(false);
        }
    }//GEN-LAST:event_btnTeilMenOkActionPerformed


    /**
     * DOCUMENT ME!
     */
 /*   private void doWithFotoUrl() {
        final String foto = EmobConfProperties.getInstance().getFotoUrlRaeder().concat(txtFoto.getText());
        // Worker Aufruf, grün/rot
        checkUrl(foto, lblUrlCheck);
        // Worker Aufruf, Foto laden
        loadPictureWithUrl(foto, lblFotoAnzeigen);
    }*/


    @Override
    public boolean prepareForSave() {
        boolean save = true;
        final StringBuilder errorMessage = new StringBuilder();

        
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
            if (this.cidsBean != null){
                setTeilnehmerBeans(cidsBean.getBeanCollectionProperty(FIELD__TEILNEHMER));   
            } else {
                setTeilnehmerBeans(null);
            }
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
            final DivBeanTable meldungModel = new DivBeanTable(
                    isEditor,
                    cidsBean,
                    FIELD__MELDUNG,
                    MELDUNG_COL_NAMES,
                    MELDUNG_PROP_NAMES,
                    MELDUNG_PROP_TYPES);
            xtMeldung.setModel(meldungModel);
            xtMeldung.getColumn(2).setCellEditor(new DefaultBindableComboboxCellEditor(meldungMetaClass));
            //xtMeldung.getColumn(4).setPreferredWidth(COLUMN_WIDTH);
        
            if (teilBeans != null && teilBeans.size() > 0) {
                lstTeil.setSelectedIndex(0);
            }
            lstTeil.setCellRenderer(new DefaultListCellRenderer() {

                    @Override
                    public Component getListCellRendererComponent(final JList list,
                            final Object value,
                            final int index,
                            final boolean isSelected,
                            final boolean cellHasFocus) {
                        Object newValue = value;

                        if (value instanceof CidsBean) {
                            final CidsBean bean = (CidsBean)value;
                            newValue = bean.getProperty(FIELD__NAME);

                            if (newValue == null) {
                                newValue = "unbenannt";
                            }
                        }
                        final Component compoTeil = super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                        compoTeil.setForeground(Color.red);
                        return compoTeil;
                    }
                });
            dlgAddTeil.pack();
            dlgAddTeil.getRootPane().setDefaultButton(btnTeilMenOk);

            } catch (final Exception ex) {
                Exceptions.printStackTrace(ex);
                LOG.error("Bean not set.", ex);
            }
    }

    /**
     * DOCUMENT ME!
     */
    private void setReadOnly() {
        if (!(isEditor)) {
            RendererTools.makeReadOnly(taBemerkung);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  cidsBeans  DOCUMENT ME!
     */
    public void setTeilnehmerBeans(final List<CidsBean> cidsBeans) {
        this.teilBeans = cidsBeans;
        baumTeilnehmerPanel1.setCidsBean(null);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getTeilnehmerBeans() {
        return teilBeans;
    }
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CustomJListModel extends AbstractListModel<CidsBean> {

        //~ Instance fields ----------------------------------------------------

        private String listPropertyName;
        private Comparator beanComparator = new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final String o1String = String.valueOf(o1.getProperty("datum"));
                    final String o2String = String.valueOf(o2.getProperty("datum"));

                    try {
                        final Integer o1Int = Integer.parseInt(o1String);
                        final Integer o2Int = Integer.parseInt(o2String);

                        return o1Int.compareTo(o2Int);
                    } catch (NumberFormatException e) {
                        // do nothing
                    }

                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
            };

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new CustomJListModel object.
         *
         * @param  listPropertyName  DOCUMENT ME!
         */
        public CustomJListModel(final String listPropertyName) {
            this.listPropertyName = listPropertyName;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private List<CidsBean> getBeanList() {
            if ((cidsBean != null) && (listPropertyName != null)) {
                final Object colObj = cidsBean.getProperty(listPropertyName);
                if (colObj instanceof Collection) {
                    return (List<CidsBean>)colObj;
                }
            }
            return null;
        }

        /**
         * DOCUMENT ME!
         */
        public void refresh() {
            fireContentsChanged(this, 0, getBeanList().size() - 1);
        }

        @Override
        public int getSize() {
            return getBeanList().size();
        }

        @Override
        public CidsBean getElementAt(final int index) {
            final List<CidsBean> l = new ArrayList<CidsBean>();
            l.addAll(getBeanList());

            Collections.sort(l, beanComparator);

            return l.get(index);
        }
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
        dlgAddTeil.dispose();
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

          
}
    

