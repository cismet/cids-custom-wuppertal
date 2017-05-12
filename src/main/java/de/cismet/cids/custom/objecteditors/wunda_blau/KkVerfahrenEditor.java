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

import Sirius.server.localserver.attribute.ObjectAttribute;
import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.KompensationskatasterBeanTable;
import de.cismet.cids.custom.wunda_blau.search.server.BPlanByGeometrySearch;
import de.cismet.cids.custom.wunda_blau.search.server.KkKompensationNextSchluesselSearch;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

import de.cismet.cids.editors.DefaultBindableScrollableComboBox;
import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.CidsBeanDropListener;
import de.cismet.cids.navigator.utils.CidsBeanDropTarget;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.gui.attributetable.DateCellEditor;
import de.cismet.cismap.commons.gui.layerwidget.ZoomToFeaturesWorker;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.commons.concurrency.CismetExecutors;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KkVerfahrenEditor extends javax.swing.JPanel implements DisposableCidsBeanStore,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent,
    PropertyChangeListener,
    EditorSaveListener,
    CidsBeanDropListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(KkVerfahrenEditor.class);
    private static final String[] COL_NAMES = new String[] { "Summe", "Erwartet", "Eingang", "Bemerkung" };
    private static final String[] PROP_NAMES = new String[] { "summe", "erwartet", "eingang", "bemerkung" };
    private static final Class[] PROP_TYPES = new Class[] { Double.class, Date.class, Date.class, String.class };
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

    protected final boolean editable;

    private CidsBean cidsBean = null;
    private final CardLayout cardLayout;
    private List<CidsBean> beansToRemoveFromOtherVerfahren = new ArrayList<CidsBean>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddLaufendeNummer;
    private javax.swing.JButton btnAddLaufendeNummer1;
    private javax.swing.JButton btnAddLaufendeNummer2;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCopyBaulast;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnPasteBaulast;
    private javax.swing.JButton btnRemoveLaufendeNummer;
    private javax.swing.JButton btnRemoveLaufendeNummer1;
    private javax.swing.JButton btnRemoveLaufendeNummer2;
    private javax.swing.JComboBox<String> cbGrundlage;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JCheckBox chkAusgleich;
    private javax.swing.JCheckBox chkErsatzzahlung;
    private javax.swing.JCheckBox chkErstattung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcAufnahme;
    private de.cismet.cids.editors.DefaultBindableDateChooser dcRechtskraft;
    private de.cismet.cids.custom.objecteditors.wunda_blau.KkVerfahrenKompensationEditor edFlaeche;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JLabel labBPlan;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblFlaeche;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblLastInMap1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstFlaechen;
    private javax.swing.JList lstLaufendeNummern;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panControlsLaufendeNummern;
    private javax.swing.JPanel panControlsLaufendeNummern1;
    private javax.swing.JPanel panCostsAndDocs;
    private javax.swing.JPanel panFiller;
    private javax.swing.JPanel panFlaechenMain;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panOben;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panUnten;
    private javax.swing.JPanel panVerfahrenInfo;
    private javax.swing.JPanel panVerfahrenskosten;
    private de.cismet.tools.gui.RoundedPanel rpFlaecheninfo;
    private de.cismet.tools.gui.RoundedPanel rpFlaechenliste;
    private de.cismet.tools.gui.RoundedPanel rpLaufendeNummern;
    private javax.swing.JScrollPane scpLaufendeNummern;
    private javax.swing.JScrollPane scpLaufendeNummern1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtTraeger;
    private javax.swing.JTextField txtVerfahrensstand;
    private org.jdesktop.swingx.JXTable xtKosten;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form KkVerfahrenEditor.
     */
    public KkVerfahrenEditor() {
        this(true);
    }

    /**
     * Creates a new KkVerfahrenEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public KkVerfahrenEditor(final boolean editable) {
        this.editable = editable;
        initComponents();
        edFlaeche.addNameChangedListener(new KeyAdapter() {

                @Override
                public void keyReleased(final KeyEvent e) {
                    EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                refreshLabels();
                            }
                        });
                }
            });
        cardLayout = (CardLayout)getLayout();
        RendererTools.makeReadOnly(txtId);
        lstFlaechen.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(final JList list,
                        final Object value,
                        final int index,
                        final boolean isSelected,
                        final boolean cellHasFocus) {
                    Object newValue = value;

                    if (value instanceof CidsBean) {
                        final CidsBean bean = (CidsBean)value;
                        newValue = bean.getProperty("schluessel");

                        if (newValue == null) {
                            newValue = "unbenannt";
                        }
                    }

                    return super.getListCellRendererComponent(list, newValue, index, isSelected, cellHasFocus);
                }
            });

        if (!editable) {
            makeReadOnly(txtBezeichnung);
            makeReadOnly(txtTraeger);
            makeReadOnly(txtVerfahrensstand);
            dcAufnahme.setEditable(false);
            dcRechtskraft.setEditable(false);
            RendererTools.makeReadOnly(cbGrundlage);
            RendererTools.makeReadOnly(cbStatus);
            RendererTools.makeReadOnly(btnAddLaufendeNummer2);
            RendererTools.makeReadOnly(btnRemoveLaufendeNummer2);
            RendererTools.makeReadOnly(btnAddLaufendeNummer1);
            RendererTools.makeReadOnly(btnRemoveLaufendeNummer1);
            makeReadOnly(chkAusgleich);
            makeReadOnly(chkErsatzzahlung);
            makeReadOnly(chkErstattung);
        } else {
            new CidsBeanDropTarget(this);
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
     * Does only work properly with binding adjustments.
     *
     * @param  box  DOCUMENT ME!
     */
    private void makeReadOnly(final JCheckBox box) {
        box.addActionListener(CHECKBOX_ACTION_LISTENER);
        box.setFocusPainted(false);
        box.setFocusable(false);
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
        lblLastInMap1 = new javax.swing.JLabel();
        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 0),
                new java.awt.Dimension(20, 32767));
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        rpLaufendeNummern = new de.cismet.tools.gui.RoundedPanel();
        scpLaufendeNummern = new javax.swing.JScrollPane();
        lstLaufendeNummern = new javax.swing.JList();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel13 = new javax.swing.JLabel();
        panControlsLaufendeNummern = new javax.swing.JPanel();
        btnAddLaufendeNummer = new javax.swing.JButton();
        btnRemoveLaufendeNummer = new javax.swing.JButton();
        btnCopyBaulast = new javax.swing.JButton();
        btnPasteBaulast = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        panMain = new javax.swing.JPanel();
        panOben = new javax.swing.JPanel();
        panVerfahrenInfo = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtBezeichnung = new javax.swing.JTextField();
        txtTraeger = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtVerfahrensstand = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        chkAusgleich = new javax.swing.JCheckBox();
        chkErstattung = new javax.swing.JCheckBox();
        chkErsatzzahlung = new javax.swing.JCheckBox();
        dcAufnahme = new de.cismet.cids.editors.DefaultBindableDateChooser();
        dcRechtskraft = new de.cismet.cids.editors.DefaultBindableDateChooser();
        cbStatus = new DefaultBindableScrollableComboBox();
        cbGrundlage = new DefaultBindableScrollableComboBox();
        panVerfahrenskosten = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xtKosten = new org.jdesktop.swingx.JXTable();
        panFiller = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        btnAddLaufendeNummer2 = new javax.swing.JButton();
        btnRemoveLaufendeNummer2 = new javax.swing.JButton();
        panUnten = new javax.swing.JPanel();
        rpFlaechenliste = new de.cismet.tools.gui.RoundedPanel();
        scpLaufendeNummern1 = new javax.swing.JScrollPane();
        lstFlaechen = new javax.swing.JList();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        panControlsLaufendeNummern1 = new javax.swing.JPanel();
        btnAddLaufendeNummer1 = new javax.swing.JButton();
        btnRemoveLaufendeNummer1 = new javax.swing.JButton();
        rpFlaecheninfo = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblFlaeche = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        labBPlan = new javax.swing.JLabel();
        panFlaechenMain = new javax.swing.JPanel();
        edFlaeche = new de.cismet.cids.custom.objecteditors.wunda_blau.KkVerfahrenKompensationEditor(editable);
        panCostsAndDocs = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14));                                                  // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTitle,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        lblLastInMap1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom-best-fit.png"))); // NOI18N
        lblLastInMap1.setToolTipText(org.openide.util.NbBundle.getMessage(
                KkVerfahrenEditor.class,
                "KkVerfahrenEditor.lblLastInMap1.toolTipText"));                                     // NOI18N
        lblLastInMap1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblLastInMap1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblLastInMap1MouseClicked(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        panTitle.add(lblLastInMap1, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.add(filler1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.01;
        jPanel1.add(jPanel2, gridBagConstraints);

        panFooterLeft.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14));                                                  // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBack,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblBack.text")); // NOI18N
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblBackMouseClicked(evt);
                }
            });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png"))); // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel1.add(panFooterLeft, gridBagConstraints);

        panButtons.add(jPanel1);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        panFooterRight.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterRight.setOpaque(false);
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        btnForward.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png"))); // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnForwardActionPerformed(evt);
                }
            });
        panFooterRight.add(btnForward);

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14));                                                  // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblForw,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblForw.text")); // NOI18N
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(panFooterRight, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel5, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel6, gridBagConstraints);

        jPanel7.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(jPanel7, gridBagConstraints);

        panButtons.add(jPanel3);

        panFooter.add(panButtons, java.awt.BorderLayout.CENTER);

        rpLaufendeNummern.setLayout(new java.awt.GridBagLayout());

        lstLaufendeNummern.setFixedCellWidth(75);
        lstLaufendeNummern.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstLaufendeNummernValueChanged(evt);
                }
            });
        scpLaufendeNummern.setViewportView(lstLaufendeNummern);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpLaufendeNummern.add(scpLaufendeNummern, gridBagConstraints);

        semiRoundedPanel3.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel13,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpLaufendeNummern.add(semiRoundedPanel3, gridBagConstraints);

        panControlsLaufendeNummern.setOpaque(false);
        panControlsLaufendeNummern.setLayout(new java.awt.GridBagLayout());

        btnAddLaufendeNummer.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddLaufendeNummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern.add(btnAddLaufendeNummer, gridBagConstraints);

        btnRemoveLaufendeNummer.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveLaufendeNummerActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern.add(btnRemoveLaufendeNummer, gridBagConstraints);

        btnCopyBaulast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/document-copy.png"))); // NOI18N
        btnCopyBaulast.setMaximumSize(new java.awt.Dimension(43, 25));
        btnCopyBaulast.setMinimumSize(new java.awt.Dimension(43, 25));
        btnCopyBaulast.setPreferredSize(new java.awt.Dimension(43, 25));
        btnCopyBaulast.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnCopyBaulastActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern.add(btnCopyBaulast, gridBagConstraints);

        btnPasteBaulast.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/clipboard-paste.png"))); // NOI18N
        btnPasteBaulast.setMaximumSize(new java.awt.Dimension(43, 25));
        btnPasteBaulast.setMinimumSize(new java.awt.Dimension(43, 25));
        btnPasteBaulast.setPreferredSize(new java.awt.Dimension(43, 25));
        btnPasteBaulast.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnPasteBaulastActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern.add(btnPasteBaulast, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        rpLaufendeNummern.add(panControlsLaufendeNummern, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel11,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel12,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel12.text")); // NOI18N

        jTextField7.setText(org.openide.util.NbBundle.getMessage(
                KkVerfahrenEditor.class,
                "KkVerfahrenEditor.jTextField7.text")); // NOI18N

        setLayout(new java.awt.CardLayout());

        panMain.setLayout(new java.awt.GridBagLayout());

        panOben.setLayout(new java.awt.GridBagLayout());

        panVerfahrenInfo.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel5, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.id}"),
                txtId,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtId, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtBezeichnung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.traeger}"),
                txtTraeger,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtTraeger, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel9, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfahrensstand}"),
                txtVerfahrensstand,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtVerfahrensstand, gridBagConstraints);

        if (editable) {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkAusgleich,
                org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkAusgleich.text")); // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ausgleich}"),
                    chkAusgleich,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        } else {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkAusgleich,
                org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkAusgleich.text")); // NOI18N
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ausgleich}"),
                    chkAusgleich,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        }
        chkAusgleich.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    chkAusgleichActionPerformed(evt);
                }
            });
        chkAusgleich.addVetoableChangeListener(new java.beans.VetoableChangeListener() {

                @Override
                public void vetoableChange(final java.beans.PropertyChangeEvent evt)
                        throws java.beans.PropertyVetoException {
                    chkAusgleichVetoableChange(evt);
                }
            });
        jPanel4.add(chkAusgleich);

        if (editable) {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkErstattung,
                org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkErstattung.text")); // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.erstattung}"),
                    chkErstattung,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        } else {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkErstattung,
                org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkErstattung.text")); // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.erstattung}"),
                    chkErstattung,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        }
        jPanel4.add(chkErstattung);

        if (editable) {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkErsatzzahlung,
                org.openide.util.NbBundle.getMessage(
                    KkVerfahrenEditor.class,
                    "KkVerfahrenEditor.chkErsatzzahlung.text")); // NOI18N

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ersatzzahlung}"),
                    chkErsatzzahlung,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        } else {
            org.openide.awt.Mnemonics.setLocalizedText(
                chkErsatzzahlung,
                org.openide.util.NbBundle.getMessage(
                    KkVerfahrenEditor.class,
                    "KkVerfahrenEditor.chkErsatzzahlung.text")); // NOI18N
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ersatzzahlung}"),
                    chkErsatzzahlung,
                    org.jdesktop.beansbinding.BeanProperty.create("selected"));
            binding.setSourceNullValue(false);
            binding.setSourceUnreadableValue(false);
            bindingGroup.addBinding(binding);
        }
        jPanel4.add(chkErsatzzahlung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jPanel4, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufnahme}"),
                dcAufnahme,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcAufnahme.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(dcAufnahme, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.rechtskraft}"),
                dcRechtskraft,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(dcRechtskraft.getConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(dcRechtskraft, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.status}"),
                cbStatus,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(cbStatus, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.grundlage}"),
                cbGrundlage,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(cbGrundlage, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 6.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 10);
        panOben.add(panVerfahrenInfo, gridBagConstraints);

        panVerfahrenskosten.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel10,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel10.text")); // NOI18N
        panVerfahrenskosten.add(jLabel10, new java.awt.GridBagConstraints());

        jScrollPane1.setViewportView(xtKosten);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panVerfahrenskosten.add(jScrollPane1, gridBagConstraints);

        final javax.swing.GroupLayout panFillerLayout = new javax.swing.GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        panFillerLayout.setVerticalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panVerfahrenskosten.add(panFiller, gridBagConstraints);

        btnAddLaufendeNummer2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer2.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddLaufendeNummer2.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddLaufendeNummer2.setPreferredSize(new java.awt.Dimension(39, 20));
        btnAddLaufendeNummer2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddLaufendeNummer2ActionPerformed(evt);
                }
            });
        jPanel10.add(btnAddLaufendeNummer2);

        btnRemoveLaufendeNummer2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer2.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemoveLaufendeNummer2.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemoveLaufendeNummer2.setPreferredSize(new java.awt.Dimension(39, 20));
        btnRemoveLaufendeNummer2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveLaufendeNummer2ActionPerformed(evt);
                }
            });
        jPanel10.add(btnRemoveLaufendeNummer2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        panVerfahrenskosten.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 20, 3, 3);
        panOben.add(panVerfahrenskosten, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMain.add(panOben, gridBagConstraints);

        panUnten.setLayout(new java.awt.GridBagLayout());

        rpFlaechenliste.setMinimumSize(new java.awt.Dimension(120, 202));
        rpFlaechenliste.setPreferredSize(new java.awt.Dimension(250, 202));
        rpFlaechenliste.setLayout(new java.awt.GridBagLayout());

        lstFlaechen.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstFlaechen.setFixedCellWidth(75);
        lstFlaechen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {

                @Override
                public void valueChanged(final javax.swing.event.ListSelectionEvent evt) {
                    lstFlaechenValueChanged(evt);
                }
            });
        scpLaufendeNummern1.setViewportView(lstFlaechen);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        rpFlaechenliste.add(scpLaufendeNummern1, gridBagConstraints);

        semiRoundedPanel4.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel14,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(jLabel14, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new java.awt.Dimension(1, 1));

        final javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                55,
                Short.MAX_VALUE));
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel4.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpFlaechenliste.add(semiRoundedPanel4, gridBagConstraints);

        panControlsLaufendeNummern1.setOpaque(false);
        panControlsLaufendeNummern1.setLayout(new java.awt.GridBagLayout());

        btnAddLaufendeNummer1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer1.setMaximumSize(new java.awt.Dimension(39, 20));
        btnAddLaufendeNummer1.setMinimumSize(new java.awt.Dimension(39, 20));
        btnAddLaufendeNummer1.setPreferredSize(new java.awt.Dimension(39, 25));
        btnAddLaufendeNummer1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddLaufendeNummer1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern1.add(btnAddLaufendeNummer1, gridBagConstraints);

        btnRemoveLaufendeNummer1.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer1.setMaximumSize(new java.awt.Dimension(39, 20));
        btnRemoveLaufendeNummer1.setMinimumSize(new java.awt.Dimension(39, 20));
        btnRemoveLaufendeNummer1.setPreferredSize(new java.awt.Dimension(39, 25));
        btnRemoveLaufendeNummer1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveLaufendeNummer1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern1.add(btnRemoveLaufendeNummer1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        rpFlaechenliste.add(panControlsLaufendeNummern1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panUnten.add(rpFlaechenliste, gridBagConstraints);

        rpFlaecheninfo.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel5.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel5.setLayout(new java.awt.GridBagLayout());

        lblFlaeche.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaeche,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblFlaeche.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(lblFlaeche, gridBagConstraints);

        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 1));

        final javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                1390,
                Short.MAX_VALUE));
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 1, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel5.add(jPanel9, gridBagConstraints);

        labBPlan.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labBPlan,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.labBPlan.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        semiRoundedPanel5.add(labBPlan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpFlaecheninfo.add(semiRoundedPanel5, gridBagConstraints);

        panFlaechenMain.setOpaque(false);
        panFlaechenMain.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechenMain.add(edFlaeche, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpFlaecheninfo.add(panFlaechenMain, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 8.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panUnten.add(rpFlaecheninfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMain.add(panUnten, gridBagConstraints);

        add(panMain, "main");

        panCostsAndDocs.setLayout(new java.awt.GridBagLayout());

        jLabel1.setForeground(new java.awt.Color(153, 153, 153));
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel1.text")); // NOI18N
        panCostsAndDocs.add(jLabel1, new java.awt.GridBagConstraints());

        add(panCostsAndDocs, "costsAndDocs");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, "main");
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }                                                                           //GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, "costsAndDocs");
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
    }                                                                              //GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }                                                                       //GEN-LAST:event_lblForwMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkAusgleichActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_chkAusgleichActionPerformed
        // TODO add your handling code here:
    } //GEN-LAST:event_chkAusgleichActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLaufendeNummernValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstLaufendeNummernValueChanged
    }                                                                                             //GEN-LAST:event_lstLaufendeNummernValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddLaufendeNummerActionPerformed
    }                                                                                        //GEN-LAST:event_btnAddLaufendeNummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummerActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveLaufendeNummerActionPerformed
    }                                                                                           //GEN-LAST:event_btnRemoveLaufendeNummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCopyBaulastActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnCopyBaulastActionPerformed
    }                                                                                  //GEN-LAST:event_btnCopyBaulastActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPasteBaulastActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnPasteBaulastActionPerformed
    }                                                                                   //GEN-LAST:event_btnPasteBaulastActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlaechenValueChanged(final javax.swing.event.ListSelectionEvent evt) { //GEN-FIRST:event_lstFlaechenValueChanged
        final Object o = lstFlaechen.getSelectedValue();

        if (o instanceof CidsBean) {
            final CidsBean bean = (CidsBean)o;
            bean.removePropertyChangeListener(this);
            bean.addPropertyChangeListener(this);
            edFlaeche.setCidsBean(bean);
            initBPlan(bean);
        } else {
            edFlaeche.setCidsBean(null);
            labBPlan.setText(" ");
        }

        refreshLabels();
    } //GEN-LAST:event_lstFlaechenValueChanged

    /**
     * DOCUMENT ME!
     */
    private void refreshLabels() {
        final CidsBean bean = edFlaeche.getCidsBean();

        if (bean != null) {
            lblFlaeche.setText("Fläche: " + toString(bean.getProperty("schluessel")) + "  "
                        + toString(bean.getProperty("name")));
        } else {
            lblFlaeche.setText("Fläche");
        }
        lstFlaechen.repaint();

        if (edFlaeche.getCidsBean() != null) {
            lstFlaechen.setSelectedValue(edFlaeche.getCidsBean(), true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String toString(final Object o) {
        if (o == null) {
            return "";
        } else {
            return String.valueOf(o);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummer1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddLaufendeNummer1ActionPerformed
        try {
            final CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName("kk_kompensation");
            final String schluessel = getSchluessel();

            if (schluessel == null) {
                LOG.error("Cannot determine new value for property schluessel");
                JOptionPane.showMessageDialog(
                    this,
                    NbBundle.getMessage(
                        KkVerfahrenEditor.class,
                        "KkVerfahrenEditor.btnAddLaufendeNummer1ActionPerformed.message"),
                    NbBundle.getMessage(
                        KkVerfahrenEditor.class,
                        "KkVerfahrenEditor.btnAddLaufendeNummer1ActionPerformed.title"),
                    JOptionPane.ERROR_MESSAGE);

                return;
            }

            bean.setProperty("schluessel", schluessel);

            cidsBean.addCollectionElement("kompensationen", bean);
            ((CustomJListModel)lstFlaechen.getModel()).refresh();
            lstFlaechen.setSelectedValue(bean, true);
            lstFlaechenValueChanged(null);
        } catch (Exception e) {
            LOG.error("Cannot add new kk_kompensation object", e);
        }
    } //GEN-LAST:event_btnAddLaufendeNummer1ActionPerformed

    /**
     * Determines the next schluessel value from the db sequence.
     *
     * @return  the next schluessel value or null, if it cannot be retrieved
     */
    private String getSchluessel() {
        try {
            final AbstractCidsServerSearch search = new KkKompensationNextSchluesselSearch();
            final List res = (List)SessionManager.getProxy()
                        .customServerSearch(SessionManager.getSession().getUser(), search);

            if ((res != null) && (res.size() == 1) && (res.get(0) != null)) {
                return res.get(0).toString();
            } else {
                LOG.error("Cannot retrieve verfahren object");
            }
        } catch (Exception e) {
            LOG.error("Error while retrieving verfahren object", e);
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummer1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveLaufendeNummer1ActionPerformed
        final Object selectedObject = lstFlaechen.getSelectedValue();

        if (selectedObject instanceof CidsBean) {
            final List<CidsBean> kompensationBeans = cidsBean.getBeanCollectionProperty("kompensationen");

            if (kompensationBeans != null) {
                kompensationBeans.remove((CidsBean)selectedObject);
                ((CustomJListModel)lstFlaechen.getModel()).refresh();
                lstFlaechen.getSelectionModel().clearSelection();
                lstFlaechenValueChanged(null);
            }
        }
    } //GEN-LAST:event_btnRemoveLaufendeNummer1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummer2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddLaufendeNummer2ActionPerformed
        try {
            final CidsBean bean = CidsBeanSupport.createNewCidsBeanFromTableName("kk_v_kosten");

            ((KompensationskatasterBeanTable)xtKosten.getModel()).addBean(bean);
        } catch (Exception e) {
            LOG.error("Cannot add new kk_v_kosten object", e);
        }
    } //GEN-LAST:event_btnAddLaufendeNummer2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummer2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveLaufendeNummer2ActionPerformed
        final int[] selectedRows = xtKosten.getSelectedRows();
        final List<Integer> modelRows = new ArrayList<Integer>();

        // The model rows should be in reverse order
        for (final int row : selectedRows) {
            modelRows.add(xtKosten.convertRowIndexToModel(row));
        }

        Collections.sort(modelRows, Collections.reverseOrder());

        for (final Integer row : modelRows) {
            ((KompensationskatasterBeanTable)xtKosten.getModel()).removeRow(row);
        }
    } //GEN-LAST:event_btnRemoveLaufendeNummer2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param   evt  DOCUMENT ME!
     *
     * @throws  java.beans.PropertyVetoException  DOCUMENT ME!
     */
    private void chkAusgleichVetoableChange(final java.beans.PropertyChangeEvent evt)
            throws java.beans.PropertyVetoException { //GEN-FIRST:event_chkAusgleichVetoableChange
    }                                                 //GEN-LAST:event_chkAusgleichVetoableChange

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblLastInMap1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblLastInMap1MouseClicked
        final List<CidsBean> beans = cidsBean.getBeanCollectionProperty("kompensationen");
        final List<CidsFeature> features = new ArrayList<CidsFeature>();

        if ((beans != null) && (beans.size() > 0)) {
            for (final CidsBean tmpBean : beans) {
                features.add(new CidsFeature(tmpBean.getMetaObject()));
            }

            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeatures(features);
            final ZoomToFeaturesWorker worker = new ZoomToFeaturesWorker(features.toArray(
                        new CidsFeature[features.size()]));
            worker.execute();
        }
    } //GEN-LAST:event_lblLastInMap1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  kompensationBean  DOCUMENT ME!
     */
    private void initBPlan(final CidsBean kompensationBean) {
        final Geometry geom = (Geometry)kompensationBean.getProperty("geometrie.geo_field");

        if (geom != null) {
            final Runnable initMapLabels = new Thread("Init bPlan") {

                    @Override
                    public void run() {
                        try {
                            final CidsServerSearch bPlanSearch = new BPlanByGeometrySearch(geom.toText());

                            final List bplan = (List)SessionManager.getProxy()
                                        .customServerSearch(SessionManager.getSession().getUser(), bPlanSearch);

                            if ((bplan != null) && (bplan.size() > 0)) {
                                labBPlan.setText("BPlan: " + String.valueOf(bplan.get(0)));
                            } else {
                                labBPlan.setText(" ");
                            }
                        } catch (Exception e) {
                            LOG.error("Error while retrieving bplan", e);
                        }
                    }
                };

            CismetExecutors.newSingleThreadExecutor().execute(initMapLabels);
        }
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;

        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean);
            bindingGroup.bind();
            lblTitle.setText("Verfahren " + String.valueOf(cidsBean.getProperty("bezeichnung")));

            final KompensationskatasterBeanTable model = new KompensationskatasterBeanTable(
                    editable,
                    cidsBean,
                    "kosten",
                    COL_NAMES,
                    PROP_NAMES,
                    PROP_TYPES);
            xtKosten.setModel(model);
            xtKosten.getColumn(1).setCellEditor(new DateCellEditor());
            xtKosten.getColumn(2).setCellEditor(new DateCellEditor());
            xtKosten.getColumn(0).setCellRenderer(new RightAlignedTableCellRenderer());
            xtKosten.getColumn(1).setCellRenderer(new RightAlignedTableCellRenderer());
            xtKosten.getColumn(2).setCellRenderer(new RightAlignedTableCellRenderer());
            xtKosten.getColumn(3).setCellRenderer(new RightAlignedTableCellRenderer());
            lstFlaechen.setModel(new CustomJListModel("kompensationen"));
            if (lstFlaechen.getModel().getSize() > 0) {
                lstFlaechen.setSelectedIndex(0);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bean  DOCUMENT ME!
     */
    protected void selectKompensation(final CidsBean bean) {
        lstFlaechen.setSelectedValue(bean, true);
    }

    @Override
    public void dispose() {
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
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
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
    }

    @Override
    public boolean prepareForSave() {
        List<CidsBean> kompBeans = null;
        final ObjectAttribute bezAttribute = cidsBean.getMetaObject().getAttribute("bezeichnung");
        final CidsBean grundlage = (CidsBean)cidsBean.getProperty("grundlage");

        if (grundlage == null) {
            JOptionPane.showMessageDialog(
                this,
                NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.prepareForSave.grundlage.message"),
                NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.prepareForSave.grundlage.title"),
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ((bezAttribute != null) && bezAttribute.isChanged()) {
            // the cs_cache table must be updated for the kompensation objects
            if ((cidsBean != null)) {
                final Object colObj = cidsBean.getProperty("kompensationen");
                if (colObj instanceof Collection) {
                    kompBeans = (List<CidsBean>)colObj;
                }
            }

            if ((kompBeans != null) && (kompBeans.size() > 0)) {
                final Executor exec = CismetExecutors.newSingleThreadExecutor();
                for (final CidsBean tmp : kompBeans) {
                    // use invoke later to ensure that the verfahren object will be saved first
                    EventQueue.invokeLater(new Thread() {

                            @Override
                            public void run() {
                                // do not persist the beans in edt. This can happen in the background.
                                exec.execute(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                // this ensures that the kompensation object will be saved (updated).
                                                // Without update, the cs_cache table will not be updated, but this is
                                                // required, if the attribute bezeichnung of the verfahren has changed
                                                tmp.getMetaObject().setStatus(MetaObject.MODIFIED);
                                                tmp.getMetaObject().getAttribute("name").setChanged(true);
                                                tmp.persist();
                                            } catch (Exception e) {
                                                LOG.error("Error while saving kompensation object", e);
                                            }
                                        }
                                    });
                            }
                        });
                }
            }
        }

        return true;
    }

    @Override
    public void beansDropped(final ArrayList<CidsBean> al) {
        boolean question = false;

        for (final CidsBean bean : al) {
            if (bean.getMetaObject().getMetaClass().getTableName().equalsIgnoreCase("kk_kompensation")) {
                if (!question) {
                    question = true;
                    final int answer = JOptionPane.showConfirmDialog(
                            this,
                            NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.beansDropped.message"),
                            NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.beansDropped.title"),
                            JOptionPane.YES_NO_OPTION);

                    if (answer != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                cidsBean.addCollectionElement("kompensationen", bean);
                ((CustomJListModel)lstFlaechen.getModel()).refresh();
                lstFlaechen.setSelectedValue(bean, true);
                lstFlaechenValueChanged(null);
                cidsBean.setArtificialChangeFlag(true);
                // a db trigger will remove the reference between this kompensation and its previous verfahren
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

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
                    final String o1String = String.valueOf(o1.getProperty("schluessel"));
                    final String o2String = String.valueOf(o2.getProperty("schluessel"));

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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class RightAlignedTableCellRenderer extends DefaultTableCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getTableCellRendererComponent(final JTable table,
                final Object value,
                final boolean isSelected,
                final boolean hasFocus,
                final int row,
                final int column) {
            final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); // To change body of generated methods, choose Tools | Templates.

            if (c instanceof JLabel) {
                ((JLabel)c).setHorizontalAlignment(JLabel.RIGHT);
            }

            return c;
        }
    }
}
