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
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXTable;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.KompensationskatasterBeanTable;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.wunda_blau.search.server.GemeindeByGeometrySearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableComboboxCellEditor;
import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.attributetable.DateCellEditor;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.SemiRoundedPanel;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KkVerfahrenKompensationEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    BorderProvider,
    RequestsFullSizeComponent,
    PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KkVerfahrenKompensationEditor.class);
    private static final MetaClass MASSNAHMEN_MC = ClassCacheMultiple.getMetaClass(
            CidsBeanSupport.DOMAIN_NAME,
            "kk_massnahme");
    private static final MetaClass BIOTOP_MC = ClassCacheMultiple.getMetaClass(
            CidsBeanSupport.DOMAIN_NAME,
            "kk_biotop");
    private static final MetaClass AUSGANGS_BIOTOP_MC = ClassCacheMultiple.getMetaClass(
            CidsBeanSupport.DOMAIN_NAME,
            "kk_ausgangsbiotop");
    private static final String[] MASSNAHMEN_COL_NAMES = new String[] {
            "Kompensationsmaßnahme",
            "Möglich",
            "Festgesetzt"
        };
    private static final String[] MASSNAHMEN_PROP_NAMES = new String[] { "massnahme", "moeglich", "festgesetzt" };
    private static final Class[] MASSNAHMEN_PROP_TYPES = new Class[] { CidsBean.class, Boolean.class, Boolean.class };
    private static final String[] KONTROLLE_COL_NAMES = new String[] {
            "Durch",
            "Geplant",
            "Durchgeführt",
            "Bemerkung"
        };
    private static final String[] KONTROLLE_PROP_NAMES = new String[] {
            "kontrolle_durch",
            "geplant_am",
            "durchgefuehrt_am",
            "bemerkungen"
        };
    private static final Class[] KONTROLLE_PROP_TYPES = new Class[] {
            String.class,
            Date.class,
            Date.class,
            String.class
        };

    private static final String[] BIO_ZIEL_COL_NAMES = new String[] { "Zielbiotop", "Größe" };
    private static final String[] BIO_ZIEL_PROP_NAMES = new String[] { "biotop", "groesse" };
    private static final Class[] BIO_ZIEL_PROP_TYPES = new Class[] { CidsBean.class, Double.class };

    private static final String[] BIO_AUS_COL_NAMES = new String[] { "Ausgangsbiotop" };
    private static final String[] BIO_AUS_PROP_NAMES = new String[] {};
    private static final Class[] BIO_AUS_PROP_TYPES = new Class[] { CidsBean.class };
    public static final ActionListener CHECKBOX_ACTION_LISTENER = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final Object source = e.getSource();

                if (source instanceof JCheckBox) {
                    final JCheckBox box = (JCheckBox)source;

                    box.setSelected(!box.isSelected());
                }
            }
        };

    //~ Instance fields --------------------------------------------------------

    final StyledFeature previewGeometry = new DefaultStyledFeature();

    private CidsBean cidsBean = null;
    private final boolean editable;
    private MappingComponent previewMap;
    private CardLayout tabPaneCardLayout;
    private List<KeyListener> keyListener = new ArrayList<KeyListener>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBioAus;
    private javax.swing.JButton btnAddBioEin;
    private javax.swing.JButton btnAddKontr;
    private javax.swing.JButton btnAddMass;
    private javax.swing.JButton btnRemBioAus;
    private javax.swing.JButton btnRemBioEin;
    private javax.swing.JButton btnRemKontr;
    private javax.swing.JButton btnRemMass;
    private javax.swing.JComboBox cbGeom;
    private javax.swing.JComboBox<String> cboFlaecheKategorie;
    private javax.swing.JComboBox<String> cboFlaecheLandschaftsplan;
    private javax.swing.JComboBox<String> cboFlaecheSchutzstatus;
    private javax.swing.JCheckBox chkFlaecheMassnahmeUmgesetzt;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcAufnahme;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel labGem;
    private javax.swing.JLabel labQm;
    private javax.swing.JLabel lblAus;
    private javax.swing.JLabel lblEin;
    private javax.swing.JLabel lblGeometrie;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panBio;
    private javax.swing.JPanel panBiotope;
    private javax.swing.JPanel panControl;
    private javax.swing.JPanel panControlsLaufendeNummern2;
    private javax.swing.JPanel panFlaechenMainSub1;
    private javax.swing.JPanel panFlaechenMainSub2;
    private javax.swing.JPanel panFlaechenMainSub3;
    private javax.swing.JPanel panFlaechenMainSubTabbedPane;
    private javax.swing.JPanel panKontr;
    private javax.swing.JPanel panKontrollen;
    private javax.swing.JPanel panMass;
    private javax.swing.JPanel panMassnahmen;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel pnlMap;
    private de.cismet.tools.gui.RoundedPanel rpGIS;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel6;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanelSouth;
    private javax.swing.JTextArea taBemerkung;
    private javax.swing.JTextArea taNebenbest;
    private javax.swing.JTextField txtFlaecheAusfuehrender;
    private javax.swing.JTextField txtFlaecheId;
    private javax.swing.JTextField txtFlaecheJahrDerUmsetzung;
    private javax.swing.JTextField txtFlaecheName;
    private org.jdesktop.swingx.JXTable xtBiotopeAus;
    private org.jdesktop.swingx.JXTable xtBiotopeEin;
    private org.jdesktop.swingx.JXTable xtKontrollen;
    private org.jdesktop.swingx.JXTable xtMassnahmen;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KkKompensation.
     */
    public KkVerfahrenKompensationEditor() {
        this(true);
    }

    /**
     * Creates new form KkKompensation.
     *
     * @param  editable  DOCUMENT ME!
     */
    public KkVerfahrenKompensationEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        final KeyAdapter ka = new KeyAdapter() {

                @Override
                public void keyTyped(final KeyEvent e) {
                    for (final KeyListener tmp : keyListener) {
                        tmp.keyTyped(e);
                    }
                }

                @Override
                public void keyPressed(final KeyEvent e) {
                    for (final KeyListener tmp : keyListener) {
                        tmp.keyPressed(e);
                    }
                }

                @Override
                public void keyReleased(final KeyEvent e) {
                    for (final KeyListener tmp : keyListener) {
                        tmp.keyReleased(e);
                    }
                }
            };
        txtFlaecheName.addKeyListener(ka);
        txtFlaecheId.addKeyListener(ka);

        tabPaneCardLayout = (CardLayout)panControl.getLayout();
        jTabbedPane1StateChanged(null);
        previewMap = new MappingComponent();
        pnlMap.setLayout(new BorderLayout());
        pnlMap.add(previewMap, BorderLayout.CENTER);

        if (!editable) {
            lblGeometrie.setVisible(false);
            makeReadOnly(txtFlaecheAusfuehrender);
            makeReadOnly(txtFlaecheId);
            makeReadOnly(txtFlaecheJahrDerUmsetzung);
            makeReadOnly(txtFlaecheName);
            RendererTools.makeReadOnly(cboFlaecheKategorie);
            RendererTools.makeReadOnly(cboFlaecheLandschaftsplan);
            RendererTools.makeReadOnly(cboFlaecheSchutzstatus);
            RendererTools.makeReadOnly(taBemerkung);
            RendererTools.makeReadOnly(taNebenbest);
            chkFlaecheMassnahmeUmgesetzt.addActionListener(CHECKBOX_ACTION_LISTENER);
            chkFlaecheMassnahmeUmgesetzt.setFocusPainted(false);
            chkFlaecheMassnahmeUmgesetzt.setFocusable(false);
            dcAufnahme.setEditable(false);
            RendererTools.makeReadOnly(btnAddBioAus);
            RendererTools.makeReadOnly(btnAddBioEin);
            RendererTools.makeReadOnly(btnAddKontr);
            RendererTools.makeReadOnly(btnAddMass);
            RendererTools.makeReadOnly(btnRemBioAus);
            RendererTools.makeReadOnly(btnRemBioEin);
            RendererTools.makeReadOnly(btnRemKontr);
            RendererTools.makeReadOnly(btnRemMass);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Does not remove the border in difference to the RendererTools.
     *
     * @param  tComp  DOCUMENT ME!
     */
    private void makeReadOnly(final JTextComponent tComp) {
        tComp.setEditable(false);
        tComp.setOpaque(false);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panFlaechenMainSub1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtFlaecheId = new javax.swing.JTextField();
        txtFlaecheName = new javax.swing.JTextField();
        cboFlaecheKategorie = new DefaultBindableScrollableComboBox();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        chkFlaecheMassnahmeUmgesetzt = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        cboFlaecheLandschaftsplan = new DefaultBindableScrollableComboBox();
        cboFlaecheSchutzstatus = new DefaultBindableScrollableComboBox();
        txtFlaecheJahrDerUmsetzung = new javax.swing.JTextField();
        txtFlaecheAusfuehrender = new javax.swing.JTextField();
        dcAufnahme = new de.cismet.cids.editors.DefaultBindableDateChooser();
        if (editable) {
            cbGeom = new DefaultCismapGeometryComboBoxEditor();
        }
        lblGeometrie = new javax.swing.JLabel();
        panFlaechenMainSub2 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        taBemerkung = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        panFlaechenMainSub3 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        taNebenbest = new javax.swing.JTextArea();
        panFlaechenMainSubTabbedPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panControl = new GlassPane();
        panMass = new GlassPane();
        btnAddMass = new javax.swing.JButton();
        btnRemMass = new javax.swing.JButton();
        panKontr = new GlassPane();
        btnAddKontr = new javax.swing.JButton();
        btnRemKontr = new javax.swing.JButton();
        panBio = new GlassPane();
        lblAus = new javax.swing.JLabel();
        btnAddBioAus = new javax.swing.JButton();
        btnRemBioAus = new javax.swing.JButton();
        lblEin = new javax.swing.JLabel();
        btnAddBioEin = new javax.swing.JButton();
        btnRemBioEin = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        panBiotope = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        xtBiotopeAus = new org.jdesktop.swingx.JXTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        xtBiotopeEin = new org.jdesktop.swingx.JXTable();
        panMassnahmen = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        xtMassnahmen = new org.jdesktop.swingx.JXTable();
        panKontrollen = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        xtKontrollen = new org.jdesktop.swingx.JXTable();
        rpGIS = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel6 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel16 = new javax.swing.JLabel();
        labQm = new javax.swing.JLabel();
        panControlsLaufendeNummern2 = new javax.swing.JPanel();
        pnlMap = new javax.swing.JPanel();
        semiRoundedPanelSouth = new de.cismet.tools.gui.SemiRoundedPanel();
        labGem = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14));    // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTitle,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        setMinimumSize(new java.awt.Dimension(862, 348));
        setLayout(new java.awt.GridBagLayout());

        panFlaechenMainSub1.setMinimumSize(new java.awt.Dimension(580, 164));
        panFlaechenMainSub1.setOpaque(false);
        panFlaechenMainSub1.setPreferredSize(new java.awt.Dimension(580, 164));
        panFlaechenMainSub1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel17,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        panFlaechenMainSub1.add(jLabel17, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel18,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        panFlaechenMainSub1.add(jLabel18, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel19,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel19.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        panFlaechenMainSub1.add(jLabel19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel20,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        panFlaechenMainSub1.add(jLabel20, gridBagConstraints);

        txtFlaecheId.setEnabled(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"),
                txtFlaecheId,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("null");
        binding.setSourceUnreadableValue("null");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheId, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtFlaecheName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kategorie}"),
                cboFlaecheKategorie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheKategorie, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel21,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel21, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel22,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel22.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel22, gridBagConstraints);

        if (editable) {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkFlaecheMassnahmeUmgesetzt,
                org.openide.util.NbBundle.getMessage(
                    KkVerfahrenKompensationEditor.class,
                    "KkVerfahrenKompensationEditor.chkFlaecheMassnahmeUmgesetzt.text")); // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.massnahme_umgesetzt}"),
                    chkFlaecheMassnahmeUmgesetzt,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        } else {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkFlaecheMassnahmeUmgesetzt,
                org.openide.util.NbBundle.getMessage(
                    KkVerfahrenEditor.class,
                    "KkVerfahrenKompensationEditor.chkFlaecheMassnahmeUmgesetzt.text")); // NOI18N
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.massnahme_umgesetzt}"),
                    chkFlaecheMassnahmeUmgesetzt,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(chkFlaecheMassnahmeUmgesetzt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel23,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel23.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel23, gridBagConstraints);

        cboFlaecheLandschaftsplan.setMaximumSize(new java.awt.Dimension(200, 25));
        cboFlaecheLandschaftsplan.setMinimumSize(new java.awt.Dimension(200, 25));
        cboFlaecheLandschaftsplan.setPreferredSize(new java.awt.Dimension(200, 25));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.landschaftsplan}"),
                cboFlaecheLandschaftsplan,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheLandschaftsplan, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schutzstatus}"),
                cboFlaecheSchutzstatus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cboFlaecheSchutzstatus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cboFlaecheSchutzstatusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheSchutzstatus, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.jahr_der_umsetzung}"),
                txtFlaecheJahrDerUmsetzung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtFlaecheJahrDerUmsetzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    txtFlaecheJahrDerUmsetzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheJahrDerUmsetzung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ausfuehrender}"),
                txtFlaecheAusfuehrender,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheAusfuehrender, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.datum_der_aufnahme}"),
                dcAufnahme,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcAufnahme.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(dcAufnahme, gridBagConstraints);

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);
        }
        if (editable) {
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 3;
            gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
            panFlaechenMainSub1.add(cbGeom, gridBagConstraints);
        }

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.lblGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        panFlaechenMainSub1.add(lblGeometrie, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 0, 4);
        add(panFlaechenMainSub1, gridBagConstraints);

        panFlaechenMainSub2.setOpaque(false);
        panFlaechenMainSub2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel24,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel24.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panFlaechenMainSub2.add(jLabel24, gridBagConstraints);

        taBemerkung.setColumns(20);
        taBemerkung.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anmerkungen}"),
                taBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(taBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechenMainSub2.add(jScrollPane2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 7);
        add(panFlaechenMainSub2, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        panFlaechenMainSub3.setMinimumSize(new java.awt.Dimension(225, 40));
        panFlaechenMainSub3.setOpaque(false);
        panFlaechenMainSub3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel25,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel25.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panFlaechenMainSub3.add(jLabel25, gridBagConstraints);

        jScrollPane3.setHorizontalScrollBar(null);

        taNebenbest.setColumns(20);
        taNebenbest.setLineWrap(true);
        taNebenbest.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nebenbestimmungen}"),
                taNebenbest,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane3.setViewportView(taNebenbest);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechenMainSub3.add(jScrollPane3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 10, 4);
        jPanel2.add(panFlaechenMainSub3, gridBagConstraints);

        panFlaechenMainSubTabbedPane.setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new javax.swing.OverlayLayout(jPanel1));

        panControl.setAlignmentX(0.0F);
        panControl.setAlignmentY(1.0F);
        panControl.setFocusable(false);
        panControl.setOpaque(false);
        panControl.setLayout(new java.awt.CardLayout());

        panMass.setAlignmentX(0.0F);
        panMass.setAlignmentY(1.0F);
        panMass.setFocusable(false);
        panMass.setOpaque(false);
        panMass.setLayout(new java.awt.GridBagLayout());

        btnAddMass.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddMass.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddMass.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddMass.setPreferredSize(new java.awt.Dimension(39, 20));
        btnAddMass.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddMassActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMass.add(btnAddMass, gridBagConstraints);

        btnRemMass.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemMass.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemMass.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemMass.setPreferredSize(new java.awt.Dimension(39, 20));
        btnRemMass.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemMassActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panMass.add(btnRemMass, gridBagConstraints);

        panControl.add(panMass, "massnahme");

        panKontr.setAlignmentX(0.0F);
        panKontr.setAlignmentY(1.0F);
        panKontr.setFocusable(false);
        panKontr.setOpaque(false);
        panKontr.setLayout(new java.awt.GridBagLayout());

        btnAddKontr.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddKontr.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddKontr.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddKontr.setPreferredSize(new java.awt.Dimension(39, 20));
        btnAddKontr.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddKontrActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panKontr.add(btnAddKontr, gridBagConstraints);

        btnRemKontr.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemKontr.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemKontr.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemKontr.setPreferredSize(new java.awt.Dimension(39, 20));
        btnRemKontr.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemKontrActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panKontr.add(btnRemKontr, gridBagConstraints);

        panControl.add(panKontr, "kontrolle");

        panBio.setAlignmentX(0.0F);
        panBio.setAlignmentY(1.0F);
        panBio.setFocusable(false);
        panBio.setOpaque(false);
        panBio.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAus,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.lblAus.text",
                new Object[] {})); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 4);
        panBio.add(lblAus, gridBagConstraints);

        btnAddBioAus.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBioAus.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddBioAus.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddBioAus.setPreferredSize(new java.awt.Dimension(39, 20));
        btnAddBioAus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddBioAusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        panBio.add(btnAddBioAus, gridBagConstraints);

        btnRemBioAus.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemBioAus.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemBioAus.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemBioAus.setPreferredSize(new java.awt.Dimension(39, 20));
        btnRemBioAus.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemBioAusActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panBio.add(btnRemBioAus, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEin,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.lblEin.text",
                new Object[] {})); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 15, 0, 4);
        panBio.add(lblEin, gridBagConstraints);

        btnAddBioEin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBioEin.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddBioEin.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddBioEin.setPreferredSize(new java.awt.Dimension(39, 20));
        btnAddBioEin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddBioEinActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weighty = 1.0;
        panBio.add(btnAddBioEin, gridBagConstraints);

        btnRemBioEin.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemBioEin.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemBioEin.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemBioEin.setPreferredSize(new java.awt.Dimension(39, 20));
        btnRemBioEin.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemBioEinActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        panBio.add(btnRemBioEin, gridBagConstraints);

        panControl.add(panBio, "biotope");

        jPanel1.add(panControl);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {

                @Override
                public void stateChanged(final javax.swing.event.ChangeEvent evt) {
                    jTabbedPane1StateChanged(evt);
                }
            });

        panBiotope.setLayout(new java.awt.GridBagLayout());

        jScrollPane6.setViewportView(xtBiotopeAus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.7;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panBiotope.add(jScrollPane6, gridBagConstraints);

        jScrollPane1.setViewportView(xtBiotopeEin);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        panBiotope.add(jScrollPane1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.panBiotope.TabConstraints.tabTitle",
                new Object[] {}),
            panBiotope); // NOI18N

        panMassnahmen.setLayout(new java.awt.GridBagLayout());

        jScrollPane5.setViewportView(xtMassnahmen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panMassnahmen.add(jScrollPane5, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.panMassnahmen.TabConstraints.tabTitle",
                new Object[] {}),
            panMassnahmen); // NOI18N

        panKontrollen.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setViewportView(xtKontrollen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        panKontrollen.add(jScrollPane4, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.panKontrollen.TabConstraints.tabTitle",
                new Object[] {}),
            panKontrollen); // NOI18N

        jPanel1.add(jTabbedPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechenMainSubTabbedPane.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel2.add(panFlaechenMainSubTabbedPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel2, gridBagConstraints);

        rpGIS.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel6.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel16,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.jLabel16.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanel6.add(jLabel16, gridBagConstraints);

        labQm.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        semiRoundedPanel6.add(labQm, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpGIS.add(semiRoundedPanel6, gridBagConstraints);

        panControlsLaufendeNummern2.setOpaque(false);
        panControlsLaufendeNummern2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 5);
        panControlsLaufendeNummern2.add(pnlMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpGIS.add(panControlsLaufendeNummern2, gridBagConstraints);

        semiRoundedPanelSouth.setCurveLocation(SemiRoundedPanel.Orientation.SOUTH);
        semiRoundedPanelSouth.setBackground(java.awt.Color.darkGray);
        semiRoundedPanelSouth.setLayout(new java.awt.GridBagLayout());

        labGem.setForeground(new java.awt.Color(255, 255, 255));
        labGem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            labGem,
            org.openide.util.NbBundle.getMessage(
                KkVerfahrenKompensationEditor.class,
                "KkVerfahrenKompensationEditor.labGem.text",
                new Object[] {})); // NOI18N
        labGem.setMinimumSize(new java.awt.Dimension(10, 17));
        labGem.setPreferredSize(new java.awt.Dimension(10, 17));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        semiRoundedPanelSouth.add(labGem, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpGIS.add(semiRoundedPanelSouth, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 15, 10);
        add(rpGIS, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cboFlaecheSchutzstatusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cboFlaecheSchutzstatusActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_cboFlaecheSchutzstatusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtFlaecheJahrDerUmsetzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_txtFlaecheJahrDerUmsetzungActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_txtFlaecheJahrDerUmsetzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddBioAusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddBioAusActionPerformed
        addObjectToTable(xtBiotopeAus, "KK_AUSGANGSBIOTOP");
    }                                                                                //GEN-LAST:event_btnAddBioAusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemBioAusActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemBioAusActionPerformed
        removeObjectsFromTable(xtBiotopeAus);
    }                                                                                //GEN-LAST:event_btnRemBioAusActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddKontrActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddKontrActionPerformed
        addObjectToTable(xtKontrollen, "kk_massnahmenkontrolle");
    }                                                                               //GEN-LAST:event_btnAddKontrActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemKontrActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemKontrActionPerformed
        removeObjectsFromTable(xtKontrollen);
    }                                                                               //GEN-LAST:event_btnRemKontrActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddMassActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddMassActionPerformed
        addObjectToTable(xtMassnahmen, "kk_kompensationsmassnahmen");
    }                                                                              //GEN-LAST:event_btnAddMassActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemMassActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemMassActionPerformed
        removeObjectsFromTable(xtMassnahmen);
    }                                                                              //GEN-LAST:event_btnRemMassActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemBioEinActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemBioEinActionPerformed
        removeObjectsFromTable(xtBiotopeEin);
    }                                                                                //GEN-LAST:event_btnRemBioEinActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddBioEinActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddBioEinActionPerformed
        addObjectToTable(xtBiotopeEin, "kk_zielbiotope");
    }                                                                                //GEN-LAST:event_btnAddBioEinActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jTabbedPane1StateChanged(final javax.swing.event.ChangeEvent evt) { //GEN-FIRST:event_jTabbedPane1StateChanged
        if (tabPaneCardLayout != null) {
            switch (jTabbedPane1.getSelectedIndex()) {
                case 0: {
                    tabPaneCardLayout.show(panControl, "biotope");
                    break;
                }
                case 1: {
                    tabPaneCardLayout.show(panControl, "massnahme");
                    break;
                }
                case 2: {
                    tabPaneCardLayout.show(panControl, "kontrolle");
                    break;
                }
            }
        }
    }                                                                                //GEN-LAST:event_jTabbedPane1StateChanged

    /**
     * DOCUMENT ME!
     *
     * @param  table  DOCUMENT ME!
     */
    private void removeObjectsFromTable(final JXTable table) {
        final int[] selectedRows = table.getSelectedRows();
        final List<Integer> modelRows = new ArrayList<Integer>();

        // The model rows should be in reverse order
        for (final int row : selectedRows) {
            modelRows.add(table.convertRowIndexToModel(row));
        }

        Collections.sort(modelRows, Collections.reverseOrder());

        for (final Integer row : modelRows) {
            ((KompensationskatasterBeanTable)table.getModel()).removeRow(row);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  table       DOCUMENT ME!
     * @param  tableClass  DOCUMENT ME!
     */
    private void addObjectToTable(final JXTable table, final String tableClass) {
        try {
            final CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName(tableClass);

            ((KompensationskatasterBeanTable)table.getModel()).addBean(bean);
        } catch (Exception e) {
            LOG.error("Cannot add new " + tableClass + " object", e);
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean == getCidsBean()) {
            return;
        }
        dispose();
        bindingGroup.unbind();
        if (cidsBean != null) {
            cidsBean.addPropertyChangeListener(this);
        }
        this.cidsBean = cidsBean;

        if (cidsBean != null) {
            if (editable) {
                ((DefaultCismapGeometryComboBoxEditor)cbGeom).setCidsMetaObject(cidsBean.getMetaObject());
                ((DefaultCismapGeometryComboBoxEditor)cbGeom).initForNewBinding();
            }
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);
            bindingGroup.bind();
        } else {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).initForNewBinding();
            txtFlaecheAusfuehrender.setText("");
            txtFlaecheId.setText("");
            txtFlaecheJahrDerUmsetzung.setText("");
            txtFlaecheName.setText("");
            taNebenbest.setText("");
            taBemerkung.setText("");
            dcAufnahme.setDate(null);
            chkFlaecheMassnahmeUmgesetzt.setSelected(false);
            cboFlaecheKategorie.setSelectedIndex(-1);
            cboFlaecheLandschaftsplan.setSelectedIndex(-1);
            cboFlaecheSchutzstatus.setSelectedIndex(-1);
            cbGeom.setSelectedIndex(-1);
        }

        final KompensationskatasterBeanTable massnahmenModel = new KompensationskatasterBeanTable(
                editable,
                cidsBean,
                "massnahmen",
                MASSNAHMEN_COL_NAMES,
                MASSNAHMEN_PROP_NAMES,
                MASSNAHMEN_PROP_TYPES);
        xtMassnahmen.setModel(massnahmenModel);
        xtMassnahmen.getColumn(0).setCellEditor(new DefaultBindableComboboxCellEditor(MASSNAHMEN_MC));
        final KompensationskatasterBeanTable kontrollenModel = new KompensationskatasterBeanTable(
                editable,
                cidsBean,
                "massnahmenkontrolle",
                KONTROLLE_COL_NAMES,
                KONTROLLE_PROP_NAMES,
                KONTROLLE_PROP_TYPES);
        xtKontrollen.setModel(kontrollenModel);
        xtKontrollen.getColumn(1).setCellEditor(new DateCellEditor());
        xtKontrollen.getColumn(2).setCellEditor(new DateCellEditor());
        final KompensationskatasterBeanTable biotopZielModel = new KompensationskatasterBeanTable(
                editable,
                cidsBean,
                "zielbiotope",
                BIO_ZIEL_COL_NAMES,
                BIO_ZIEL_PROP_NAMES,
                BIO_ZIEL_PROP_TYPES);
        xtBiotopeEin.setModel(biotopZielModel);
        xtBiotopeEin.getColumn(0).setCellEditor(new DefaultBindableComboboxCellEditor(BIOTOP_MC));
        final KompensationskatasterBeanTable biotopAusModel = new KompensationskatasterBeanTable(
                editable,
                cidsBean,
                "ausgangsbiotope",
                BIO_AUS_COL_NAMES,
                BIO_AUS_PROP_NAMES,
                BIO_AUS_PROP_TYPES);
        xtBiotopeAus.setModel(biotopAusModel);
        xtBiotopeAus.getColumn(0).setCellEditor(new DefaultBindableComboboxCellEditor(AUSGANGS_BIOTOP_MC));
        initMap();

        if (cidsBean != null) {
            double qm = -1.0;
            final Geometry g = (Geometry)cidsBean.getProperty("geometrie.geo_field");

            if (g != null) {
                qm = Math.round(g.getArea() * 100) / 100.0;
            }

            if (qm != -1.0) {
                labQm.setText(qm + " m²");
            } else {
                labQm.setText("");
            }
        } else {
            labQm.setText("");
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        XBoundingBox box = null;
        double diagonalLength = 0;
        Geometry pureGeom = null;

        if (cidsBean != null) {
            final Object geoObj = cidsBean.getProperty("geometrie.geo_field");

            if (geoObj instanceof Geometry) {
                pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                        AlkisConstants.COMMONS.SRS_SERVICE);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("ALKISConstatns.Commons.GeoBUffer: " + AlkisConstants.COMMONS.GEO_BUFFER);
                }
                box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                            AlkisConstants.COMMONS.GEO_BUFFER));
                diagonalLength = Math.sqrt((box.getWidth() * box.getWidth())
                                + (box.getHeight() * box.getHeight()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Buffer for map: " + diagonalLength);
                }
            }
        }

        final XBoundingBox bufferedBox = ((box != null) ? new XBoundingBox(box.getGeometry().buffer(diagonalLength))
                                                        : null);
        final Geometry pureFeatureGeom = pureGeom;

        final Runnable mapRunnable = new Runnable() {

                @Override
                public void run() {
                    final ActiveLayerModel mappingModel = new ActiveLayerModel();
                    mappingModel.setSrs(AlkisConstants.COMMONS.SRS_SERVICE);

                    if (bufferedBox != null) {
                        mappingModel.addHome(new XBoundingBox(
                                bufferedBox.getX1(),
                                bufferedBox.getY1(),
                                bufferedBox.getX2(),
                                bufferedBox.getY2(),
                                AlkisConstants.COMMONS.SRS_SERVICE,
                                true));
                        final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                                    AlkisConstants.COMMONS.MAP_CALL_STRING));
                        swms.setName("Stadtbildserie");

                        // add the raster layer to the model
                        mappingModel.addLayer(swms);
                    } else {
                        mappingModel.addHome(new XBoundingBox(
                                1,
                                1,
                                2,
                                2,
                                AlkisConstants.COMMONS.SRS_SERVICE,
                                true));
                    }
                    // set the model
                    previewMap.setMappingModel(mappingModel);
                    // initial positioning of the map
                    final int duration = previewMap.getAnimationDuration();
                    previewMap.setAnimationDuration(0);
                    previewMap.gotoInitialBoundingBox();
                    // interaction mode
                    previewMap.setInteractionMode(MappingComponent.ZOOM);
                    // finally when all configurations are done ...
                    previewMap.unlock();
                    previewMap.addCustomInputListener("ADD_TO_MAP_KOMP", new PBasicInputEventHandler() {

                            @Override
                            public void mouseClicked(final PInputEvent evt) {
                                if (evt.getClickCount() > 1) {
                                    final CidsBean bean = cidsBean;
                                    ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                }
                            }
                        });
                    previewMap.setInteractionMode("ADD_TO_MAP_KOMP");
                    previewMap.getFeatureCollection().removeAllFeatures();
                    if (bufferedBox != null) {
                        previewMap.getFeatureCollection().addFeature(new CidsFeature(cidsBean.getMetaObject()));
                    }
//                    if (previewGeometry != null) {
//                        previewGeometry.setGeometry(pureFeatureGeom);
//                        previewGeometry.setFillingPaint(new Color(1, 0, 0, 0.5f));
//                        previewGeometry.setLineWidth(3);
//                        previewGeometry.setLinePaint(new Color(1, 0, 0, 1f));
//                        previewMap.getFeatureCollection().addFeature(previewGeometry);
//                    }
                    previewMap.setAnimationDuration(duration);
                }
            };
        if (EventQueue.isDispatchThread()) {
            mapRunnable.run();
        } else {
            EventQueue.invokeLater(mapRunnable);
        }

        if (cidsBean != null) {
            final Geometry geom = (Geometry)cidsBean.getProperty("geometrie.geo_field");

            if (geom != null) {
                final Runnable initMapLabels = new Runnable() {

                        @Override
                        public void run() {
                            try {
                                final CidsServerSearch gemeindeSearch = new GemeindeByGeometrySearch(geom.toText());

                                final List gemeinde = (List)SessionManager.getProxy()
                                            .customServerSearch(SessionManager.getSession().getUser(), gemeindeSearch);

                                if ((gemeinde != null) && (gemeinde.size() > 0)) {
                                    labGem.setText(String.valueOf(gemeinde.get(0)));
                                } else {
                                    labGem.setText("");
                                }
                            } catch (Exception e) {
                                LOG.error("Error while retrieving Gemeinde", e);
                            }
                        }
                    };

                CismetExecutors.newSingleThreadExecutor().execute(initMapLabels);
            }
        } else {
            labGem.setText("");
        }
    }

    /**
     * Adds a key listener that will be fired, if the name field was changed.
     *
     * @param  l  DOCUMENT ME!
     */
    public void addNameChangedListener(final KeyListener l) {
        keyListener.add(l);
    }

    /**
     * Removes a key listener that will be fired, if the name field was changed.
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeNameChangedListener(final KeyListener l) {
        keyListener.remove(l);
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        if (cidsBean != null) {
            cidsBean.removePropertyChangeListener(this);
        }
    }

    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(0, 5, 0, 5);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("geometrie")) {
            initMap();
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * A Panel with a contains method that only considers its components, but not the panel itself.
     *
     * <p>Why: The panel with the buttons overlaps a part of the tab pane and this affects the mouse look. In order to
     * manipulate the cursor by the tab pane, the contains method should return false, if no component of the panel is
     * contains the given position</p>
     *
     * @version  $Revision$, $Date$
     */
    private class GlassPane extends JPanel {

        //~ Methods ------------------------------------------------------------

        @Override
        public boolean contains(final int x, final int y) {
            // The contains method should return false, if no component of the panel is contains the given position
            synchronized (getTreeLock()) {
                for (final Component comp : getComponents()) {
                    if (comp.contains(x - comp.getLocation().x, y - comp.getLocation().y)) {
                        return super.contains(x, y);
                    }
                }
            }

            return false;
        }
    }
}
