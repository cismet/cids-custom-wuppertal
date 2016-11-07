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

import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DisposableCidsBeanStore;

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
    RequestsFullSizeComponent {

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean = null;
    private final boolean editable;
    private final CardLayout cardLayout;

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
    private javax.swing.JComboBox<String> cboFlaecheKategorie;
    private javax.swing.JComboBox<String> cboFlaecheLandschaftsplan;
    private javax.swing.JComboBox<String> cboFlaecheSchutzstatus;
    private javax.swing.JComboBox<String> cboGrundlage;
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JCheckBox chkAusgleich;
    private javax.swing.JCheckBox chkErsatzzahlung;
    private javax.swing.JCheckBox chkErstattung;
    private javax.swing.JCheckBox chkFlaecheMassnahmeUmgesetzt;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField7;
    private org.jdesktop.swingx.JXTable jXTable1;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblLastInMap;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JList lstFlaechen;
    private javax.swing.JList lstLaufendeNummern;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panControlsLaufendeNummern;
    private javax.swing.JPanel panControlsLaufendeNummern1;
    private javax.swing.JPanel panControlsLaufendeNummern2;
    private javax.swing.JPanel panCostsAndDocs;
    private javax.swing.JPanel panFiller;
    private javax.swing.JPanel panFlaechenMain;
    private javax.swing.JPanel panFlaechenMainSub1;
    private javax.swing.JPanel panFlaechenMainSub2;
    private javax.swing.JPanel panFlaechenMainSub3;
    private javax.swing.JPanel panFlaechenMainSubTabbedPane;
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
    private de.cismet.tools.gui.RoundedPanel rpGIS;
    private de.cismet.tools.gui.RoundedPanel rpLaufendeNummern;
    private javax.swing.JScrollPane scpLaufendeNummern;
    private javax.swing.JScrollPane scpLaufendeNummern1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel6;
    private javax.swing.JTextField txtAufnahme;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtFlaecheAufnahme;
    private javax.swing.JTextField txtFlaecheAusfuehrender;
    private javax.swing.JTextField txtFlaecheId;
    private javax.swing.JTextField txtFlaecheJahrDerUmsetzung;
    private javax.swing.JTextField txtFlaecheName;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtRechtskraft;
    private javax.swing.JTextField txtTraeger;
    private javax.swing.JTextField txtVerfahrensstand;
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
        cardLayout = (CardLayout)getLayout();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
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
        txtAufnahme = new javax.swing.JTextField();
        txtTraeger = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtVerfahrensstand = new javax.swing.JTextField();
        cboStatus = new javax.swing.JComboBox<>();
        txtRechtskraft = new javax.swing.JTextField();
        cboGrundlage = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        chkAusgleich = new javax.swing.JCheckBox();
        chkErstattung = new javax.swing.JCheckBox();
        chkErsatzzahlung = new javax.swing.JCheckBox();
        panVerfahrenskosten = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jXTable1 = new org.jdesktop.swingx.JXTable();
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
        jLabel15 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblLastInMap = new javax.swing.JLabel();
        panFlaechenMain = new javax.swing.JPanel();
        panFlaechenMainSub1 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtFlaecheId = new javax.swing.JTextField();
        txtFlaecheName = new javax.swing.JTextField();
        cboFlaecheKategorie = new javax.swing.JComboBox<>();
        txtFlaecheAufnahme = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        chkFlaecheMassnahmeUmgesetzt = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        cboFlaecheLandschaftsplan = new javax.swing.JComboBox<>();
        cboFlaecheSchutzstatus = new javax.swing.JComboBox<>();
        txtFlaecheJahrDerUmsetzung = new javax.swing.JTextField();
        txtFlaecheAusfuehrender = new javax.swing.JTextField();
        panFlaechenMainSub2 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        panFlaechenMainSub3 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        panFlaechenMainSubTabbedPane = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        rpGIS = new de.cismet.tools.gui.RoundedPanel();
        semiRoundedPanel6 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel16 = new javax.swing.JLabel();
        panControlsLaufendeNummern2 = new javax.swing.JPanel();
        panCostsAndDocs = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblTitle, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

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

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBack, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblBack.text")); // NOI18N
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBackMouseClicked(evt);
            }
        });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png"))); // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        btnForward.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-right.png"))); // NOI18N
        btnForward.setBorder(null);
        btnForward.setBorderPainted(false);
        btnForward.setContentAreaFilled(false);
        btnForward.setFocusPainted(false);
        btnForward.setMaximumSize(new java.awt.Dimension(30, 30));
        btnForward.setMinimumSize(new java.awt.Dimension(30, 30));
        btnForward.setPreferredSize(new java.awt.Dimension(30, 30));
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });
        panFooterRight.add(btnForward);

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblForw, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblForw.text")); // NOI18N
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
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
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
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
        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpLaufendeNummern.add(semiRoundedPanel3, gridBagConstraints);

        panControlsLaufendeNummern.setOpaque(false);
        panControlsLaufendeNummern.setLayout(new java.awt.GridBagLayout());

        btnAddLaufendeNummer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLaufendeNummerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern.add(btnAddLaufendeNummer, gridBagConstraints);

        btnRemoveLaufendeNummer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel11.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel12.text")); // NOI18N

        jTextField7.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jTextField7.text")); // NOI18N

        setLayout(new java.awt.CardLayout());

        panMain.setLayout(new java.awt.GridBagLayout());

        panOben.setLayout(new java.awt.GridBagLayout());

        panVerfahrenInfo.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jLabel5, gridBagConstraints);

        txtId.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtId, gridBagConstraints);

        txtBezeichnung.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtBezeichnung, gridBagConstraints);

        txtAufnahme.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtAufnahme.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtAufnahme, gridBagConstraints);

        txtTraeger.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtTraeger.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtTraeger, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 13, 3, 3);
        panVerfahrenInfo.add(jLabel9, gridBagConstraints);

        txtVerfahrensstand.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtVerfahrensstand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtVerfahrensstand, gridBagConstraints);

        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(cboStatus, gridBagConstraints);

        txtRechtskraft.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtRechtskraft.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(txtRechtskraft, gridBagConstraints);

        cboGrundlage.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(cboGrundlage, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(chkAusgleich, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkAusgleich.text")); // NOI18N
        chkAusgleich.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAusgleichActionPerformed(evt);
            }
        });
        jPanel4.add(chkAusgleich);

        org.openide.awt.Mnemonics.setLocalizedText(chkErstattung, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkErstattung.text")); // NOI18N
        jPanel4.add(chkErstattung);

        org.openide.awt.Mnemonics.setLocalizedText(chkErsatzzahlung, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkErsatzzahlung.text")); // NOI18N
        jPanel4.add(chkErsatzzahlung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panVerfahrenInfo.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 6.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 10);
        panOben.add(panVerfahrenInfo, gridBagConstraints);

        panVerfahrenskosten.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel10.text")); // NOI18N
        panVerfahrenskosten.add(jLabel10, new java.awt.GridBagConstraints());

        jScrollPane1.setViewportView(jXTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panVerfahrenskosten.add(jScrollPane1, gridBagConstraints);

        javax.swing.GroupLayout panFillerLayout = new javax.swing.GroupLayout(panFiller);
        panFiller.setLayout(panFillerLayout);
        panFillerLayout.setHorizontalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panFillerLayout.setVerticalGroup(
            panFillerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panVerfahrenskosten.add(panFiller, gridBagConstraints);

        btnAddLaufendeNummer2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLaufendeNummer2ActionPerformed(evt);
            }
        });
        jPanel10.add(btnAddLaufendeNummer2);

        btnRemoveLaufendeNummer2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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

        rpFlaechenliste.setLayout(new java.awt.GridBagLayout());

        lstFlaechen.setFixedCellWidth(75);
        lstFlaechen.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
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
        org.openide.awt.Mnemonics.setLocalizedText(jLabel14, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel14.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(jLabel14, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new java.awt.Dimension(1, 1));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 67, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

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

        btnAddLaufendeNummer1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddLaufendeNummer1.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer1.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer1.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddLaufendeNummer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLaufendeNummer1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panControlsLaufendeNummern1.add(btnAddLaufendeNummer1, gridBagConstraints);

        btnRemoveLaufendeNummer1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveLaufendeNummer1.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer1.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer1.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveLaufendeNummer1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panUnten.add(rpFlaechenliste, gridBagConstraints);

        rpFlaecheninfo.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel5.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel15, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel15.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(jLabel15, gridBagConstraints);

        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(1, 1));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 995, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        semiRoundedPanel5.add(jPanel9, gridBagConstraints);

        lblLastInMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom-best-fit.png"))); // NOI18N
        lblLastInMap.setToolTipText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.lblLastInMap.toolTipText")); // NOI18N
        lblLastInMap.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        lblLastInMap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLastInMapMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        semiRoundedPanel5.add(lblLastInMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpFlaecheninfo.add(semiRoundedPanel5, gridBagConstraints);

        panFlaechenMain.setOpaque(false);
        panFlaechenMain.setLayout(new java.awt.GridBagLayout());

        panFlaechenMainSub1.setOpaque(false);
        panFlaechenMainSub1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel17, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel18, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel19, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel19.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel20, gridBagConstraints);

        txtFlaecheId.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtFlaecheId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheId, gridBagConstraints);

        txtFlaecheName.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtFlaecheName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheName, gridBagConstraints);

        cboFlaecheKategorie.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheKategorie, gridBagConstraints);

        txtFlaecheAufnahme.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtFlaecheAufnahme.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheAufnahme, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel21, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel22.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel22, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(chkFlaecheMassnahmeUmgesetzt, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.chkFlaecheMassnahmeUmgesetzt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(chkFlaecheMassnahmeUmgesetzt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel23.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(jLabel23, gridBagConstraints);

        cboFlaecheLandschaftsplan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheLandschaftsplan, gridBagConstraints);

        cboFlaecheSchutzstatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboFlaecheSchutzstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFlaecheSchutzstatusActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(cboFlaecheSchutzstatus, gridBagConstraints);

        txtFlaecheJahrDerUmsetzung.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtFlaecheJahrDerUmsetzung.text")); // NOI18N
        txtFlaecheJahrDerUmsetzung.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFlaecheJahrDerUmsetzungActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheJahrDerUmsetzung, gridBagConstraints);

        txtFlaecheAusfuehrender.setText(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.txtFlaecheAusfuehrender.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        panFlaechenMainSub1.add(txtFlaecheAusfuehrender, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panFlaechenMain.add(panFlaechenMainSub1, gridBagConstraints);

        panFlaechenMainSub2.setOpaque(false);
        panFlaechenMainSub2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel24, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel24.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panFlaechenMainSub2.add(jLabel24, gridBagConstraints);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

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
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panFlaechenMain.add(panFlaechenMainSub2, gridBagConstraints);

        panFlaechenMainSub3.setOpaque(false);
        panFlaechenMainSub3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel25, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel25.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        panFlaechenMainSub3.add(jLabel25, gridBagConstraints);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

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
        gridBagConstraints.weighty = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panFlaechenMain.add(panFlaechenMainSub3, gridBagConstraints);

        panFlaechenMainSubTabbedPane.setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jPanel11.TabConstraints.tabTitle"), jPanel11); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jPanel12.TabConstraints.tabTitle"), jPanel12); // NOI18N

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 740, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 208, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jPanel13.TabConstraints.tabTitle"), jPanel13); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panFlaechenMainSubTabbedPane.add(jTabbedPane1, gridBagConstraints);
        jTabbedPane1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panFlaechenMain.add(panFlaechenMainSubTabbedPane, gridBagConstraints);

        rpGIS.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel6.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel16.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(jLabel16, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        rpGIS.add(semiRoundedPanel6, gridBagConstraints);

        panControlsLaufendeNummern2.setOpaque(false);
        panControlsLaufendeNummern2.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rpGIS.add(panControlsLaufendeNummern2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 6.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        panFlaechenMain.add(rpGIS, gridBagConstraints);

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
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(KkVerfahrenEditor.class, "KkVerfahrenEditor.jLabel1.text")); // NOI18N
        panCostsAndDocs.add(jLabel1, new java.awt.GridBagConstraints());

        add(panCostsAndDocs, "costsAndDocs");
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBackMouseClicked
        btnBackActionPerformed(null);
    }//GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, "main");
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
    }//GEN-LAST:event_btnBackActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnForwardActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        cardLayout.show(this, "costsAndDocs");
        btnBack.setEnabled(true);
        btnForward.setEnabled(false);
        lblBack.setEnabled(true);
        lblForw.setEnabled(false);
    }//GEN-LAST:event_btnForwardActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblForwMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblForwMouseClicked
        btnForwardActionPerformed(null);
    }//GEN-LAST:event_lblForwMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void chkAusgleichActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAusgleichActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkAusgleichActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstLaufendeNummernValueChanged(final javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstLaufendeNummernValueChanged
    }//GEN-LAST:event_lstLaufendeNummernValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummerActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLaufendeNummerActionPerformed
    }//GEN-LAST:event_btnAddLaufendeNummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummerActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLaufendeNummerActionPerformed
    }//GEN-LAST:event_btnRemoveLaufendeNummerActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnCopyBaulastActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyBaulastActionPerformed
    }//GEN-LAST:event_btnCopyBaulastActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnPasteBaulastActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteBaulastActionPerformed
    }//GEN-LAST:event_btnPasteBaulastActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lstFlaechenValueChanged(final javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFlaechenValueChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_lstFlaechenValueChanged

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummer1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLaufendeNummer1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddLaufendeNummer1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummer1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLaufendeNummer1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveLaufendeNummer1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblLastInMapMouseClicked(final java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLastInMapMouseClicked
    }//GEN-LAST:event_lblLastInMapMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddLaufendeNummer2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLaufendeNummer2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddLaufendeNummer2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveLaufendeNummer2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveLaufendeNummer2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveLaufendeNummer2ActionPerformed

    private void cboFlaecheSchutzstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFlaecheSchutzstatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboFlaecheSchutzstatusActionPerformed

    private void txtFlaecheJahrDerUmsetzungActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFlaecheJahrDerUmsetzungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFlaecheJahrDerUmsetzungActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        if (cidsBean != null) {
            this.cidsBean = cidsBean;

            lblTitle.setText("Verfahren " + String.valueOf(cidsBean.getProperty("bezeichnung")));
            // bindingGroup.bind();
        }
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
}
