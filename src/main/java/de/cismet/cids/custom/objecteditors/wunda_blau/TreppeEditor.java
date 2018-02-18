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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;

import java.awt.CardLayout;
import java.awt.Color;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.deprecated.TabbedPaneUITransparent;
import de.cismet.cids.custom.reports.wunda_blau.TreppenReportGenerator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class TreppeEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TreppeEditor.class);
    private static final Color ROT = new Color(255, 0, 60);
    private static final Color GELB = new Color(250, 190, 40);
    private static final Color GRUEN = new Color(0, 193, 118);

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private final ZustandOverview overview = new ZustandOverview();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnImages;
    private javax.swing.JButton btnInfo;
    private javax.swing.JButton btnReport;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink6;
    private javax.swing.JLabel lblHeaderAllgemein;
    private javax.swing.JLabel lblImages;
    private javax.swing.JLabel lblInfo;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panLeft;
    private javax.swing.JPanel panRight;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.RoundedPanel panZusammenfassung;
    private javax.swing.JPanel panZusammenfassungContent;
    private de.cismet.tools.gui.SemiRoundedPanel panZusammenfassungTitle;
    private javax.swing.JPanel pnlCard1;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel1;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel2;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel3;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel4;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel5;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel6;
    private de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel roundedPanel7;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBeschreibungPanel treppeBeschreibungPanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEntwaesserungPanel treppeEntwaesserung1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaeufePanel treppeHandlaeufePanel2;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaeufePanel treppeLaeufePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementePanel treppeLeitelementePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppePicturePanel treppePicturePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestePanel treppePodestePanel1;
    private de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauernPanel treppeStuetzmauernPanel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppeEditor object.
     */
    public TreppeEditor() {
        this(true);
    }

    /**
     * Creates new form TreppenEditor.
     *
     * @param  editable  DOCUMENT ME!
     */
    public TreppeEditor(final boolean editable) {
        this.editable = editable;
        initComponents();

        jTabbedPane1.setUI(new TabbedPaneUITransparent());
        jTabbedPane1.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent e) {
                    if (e.getSource() instanceof JTabbedPane) {
                        final JTabbedPane pane = (JTabbedPane)e.getSource();
                        if (pane.getSelectedIndex() == 0) {
                            overview.recalculateAll();
                        }
                    }
                }
            });
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        DevelopmentTools.createEditorInFrameFromRestfulConnection(
            "WUNDA_BLAU",
            null,
            "admin",
            "xxx",
            "treppe",
            4,
            1000,
            1000);
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
        btnReport = new javax.swing.JButton();
        panFooter = new javax.swing.JPanel();
        panLeft = new javax.swing.JPanel();
        lblInfo = new javax.swing.JLabel();
        btnInfo = new javax.swing.JButton();
        panRight = new javax.swing.JPanel();
        btnImages = new javax.swing.JButton();
        lblImages = new javax.swing.JLabel();
        pnlCard1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panZusammenfassung = new de.cismet.tools.gui.RoundedPanel();
        panZusammenfassungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAllgemein = new javax.swing.JLabel();
        panZusammenfassungContent = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink4 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();
        jXHyperlink6 = new org.jdesktop.swingx.JXHyperlink();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        roundedPanel1 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel2 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel3 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel4 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel5 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel6 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        roundedPanel7 = new de.cismet.cids.custom.objecteditors.utils.FullyRoundedPanel();
        treppeBeschreibungPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeBeschreibungPanel(editable);
        jPanel2 = new javax.swing.JPanel();
        treppeLaeufePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLaeufePanel(editable);
        jPanel3 = new javax.swing.JPanel();
        treppePodestePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppePodestePanel(editable);
        jPanel4 = new javax.swing.JPanel();
        treppeLeitelementePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeLeitelementePanel(editable);
        jPanel5 = new javax.swing.JPanel();
        treppeHandlaeufePanel2 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeHandlaeufePanel(editable);
        jPanel6 = new javax.swing.JPanel();
        treppeEntwaesserung1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEntwaesserungPanel(editable);
        jPanel7 = new javax.swing.JPanel();
        treppeStuetzmauernPanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppeStuetzmauernPanel(editable);
        treppePicturePanel1 = new de.cismet.cids.custom.objecteditors.wunda_blau.TreppePicturePanel(editable);

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitle, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png")));                 // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnReport.text")); // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.btnReport.toolTipText"));                                               // NOI18N
        btnReport.setBorderPainted(false);
        btnReport.setContentAreaFilled(false);
        btnReport.setFocusPainted(false);
        btnReport.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnReportActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        panLeft.setOpaque(false);

        lblInfo.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                   // NOI18N
        lblInfo.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInfo,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblInfo.text")); // NOI18N
        lblInfo.setEnabled(false);
        panLeft.add(lblInfo);

        btnInfo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-left.png")));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnInfo,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnInfo.text")); // NOI18N
        btnInfo.setBorderPainted(false);
        btnInfo.setContentAreaFilled(false);
        btnInfo.setEnabled(false);
        btnInfo.setFocusPainted(false);
        btnInfo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnInfoActionPerformed(evt);
                }
            });
        panLeft.add(btnInfo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panFooter.add(panLeft, gridBagConstraints);

        panRight.setOpaque(false);

        btnImages.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/arrow-right.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnImages,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.btnImages.text")); // NOI18N
        btnImages.setBorderPainted(false);
        btnImages.setContentAreaFilled(false);
        btnImages.setFocusPainted(false);
        btnImages.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnImagesActionPerformed(evt);
                }
            });
        panRight.add(btnImages);

        lblImages.setFont(new java.awt.Font("DejaVu Sans", 1, 14));                                   // NOI18N
        lblImages.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblImages,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblImages.text")); // NOI18N
        panRight.add(lblImages);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        panFooter.add(panRight, gridBagConstraints);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        pnlCard1.setOpaque(false);
        pnlCard1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panZusammenfassung.setLayout(new java.awt.GridBagLayout());

        panZusammenfassungTitle.setBackground(new java.awt.Color(51, 51, 51));
        panZusammenfassungTitle.setLayout(new java.awt.FlowLayout());

        lblHeaderAllgemein.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHeaderAllgemein,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.lblHeaderAllgemein.text")); // NOI18N
        panZusammenfassungTitle.add(lblHeaderAllgemein);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        panZusammenfassung.add(panZusammenfassungTitle, gridBagConstraints);

        panZusammenfassungContent.setOpaque(false);
        panZusammenfassungContent.setLayout(new java.awt.GridBagLayout());

        jPanel11.setOpaque(false);
        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel19,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel19.text")); // NOI18N
        jLabel19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jLabel19, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel20,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel20, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink1,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink1.text")); // NOI18N
        jXHyperlink1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink2,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink2.text")); // NOI18N
        jXHyperlink2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink3,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink3.text")); // NOI18N
        jXHyperlink3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink4,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink4.text")); // NOI18N
        jXHyperlink4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink5,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink5.text")); // NOI18N
        jXHyperlink5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink6,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jXHyperlink6.text")); // NOI18N
        jXHyperlink6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 12, 5);
        jPanel11.add(jXHyperlink6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel21,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel21, gridBagConstraints);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel22, gridBagConstraints);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel23, gridBagConstraints);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel24, gridBagConstraints);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel25, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel26,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel26.text")); // NOI18N
        jLabel26.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel26, gridBagConstraints);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel27,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel27.text")); // NOI18N
        jLabel27.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel27, gridBagConstraints);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel28,
            org.openide.util.NbBundle.getMessage(TreppeEditor.class, "TreppeEditor.jLabel28.text")); // NOI18N
        jLabel28.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        jPanel11.add(jLabel28, gridBagConstraints);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel29, gridBagConstraints);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel30, gridBagConstraints);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel31, gridBagConstraints);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel32, gridBagConstraints);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel33.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel33, gridBagConstraints);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel34.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel34, gridBagConstraints);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(12, 5, 2, 5);
        jPanel11.add(jLabel35, gridBagConstraints);

        roundedPanel1.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel1, gridBagConstraints);

        roundedPanel2.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel2, gridBagConstraints);

        roundedPanel3.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel3.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel3.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel3Layout = new javax.swing.GroupLayout(roundedPanel3);
        roundedPanel3.setLayout(roundedPanel3Layout);
        roundedPanel3Layout.setHorizontalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel3Layout.setVerticalGroup(
            roundedPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel3, gridBagConstraints);

        roundedPanel4.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel4.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel4.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel4Layout = new javax.swing.GroupLayout(roundedPanel4);
        roundedPanel4.setLayout(roundedPanel4Layout);
        roundedPanel4Layout.setHorizontalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel4Layout.setVerticalGroup(
            roundedPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel4, gridBagConstraints);

        roundedPanel5.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel5.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel5.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel5Layout = new javax.swing.GroupLayout(roundedPanel5);
        roundedPanel5.setLayout(roundedPanel5Layout);
        roundedPanel5Layout.setHorizontalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel5Layout.setVerticalGroup(
            roundedPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel5, gridBagConstraints);

        roundedPanel6.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel6.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel6.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel6Layout = new javax.swing.GroupLayout(roundedPanel6);
        roundedPanel6.setLayout(roundedPanel6Layout);
        roundedPanel6Layout.setHorizontalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                0,
                Short.MAX_VALUE));
        roundedPanel6Layout.setVerticalGroup(
            roundedPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel6, gridBagConstraints);

        roundedPanel7.setMaximumSize(new java.awt.Dimension(32, 32));
        roundedPanel7.setMinimumSize(new java.awt.Dimension(32, 32));
        roundedPanel7.setPreferredSize(new java.awt.Dimension(32, 32));

        final javax.swing.GroupLayout roundedPanel7Layout = new javax.swing.GroupLayout(roundedPanel7);
        roundedPanel7.setLayout(roundedPanel7Layout);
        roundedPanel7Layout.setHorizontalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));
        roundedPanel7Layout.setVerticalGroup(
            roundedPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(
                0,
                32,
                Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel11.add(roundedPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panZusammenfassungContent.add(jPanel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panZusammenfassung.add(panZusammenfassungContent, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(panZusammenfassung, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean}"),
                treppeBeschreibungPanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel1.add(treppeBeschreibungPanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel1.TabConstraints.tabTitle"),
            jPanel1); // NOI18N

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.treppenlaeufe}"),
                treppeLaeufePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel2.add(treppeLaeufePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel2.TabConstraints.tabTitle"),
            jPanel2); // NOI18N

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.podeste}"),
                treppePodestePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel3.add(treppePodestePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel3.TabConstraints.tabTitle"),
            jPanel3); // NOI18N

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.absturzsicherungen}"),
                treppeLeitelementePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel4.add(treppeLeitelementePanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel4.TabConstraints.tabTitle"),
            jPanel4); // NOI18N

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlaeufe}"),
                treppeHandlaeufePanel2,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel5.add(treppeHandlaeufePanel2, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel5.TabConstraints.tabTitle"),
            jPanel5); // NOI18N

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwaesserung}"),
                treppeEntwaesserung1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel6.add(treppeEntwaesserung1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel6.TabConstraints.tabTitle"),
            jPanel6); // NOI18N

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stuetzmauern}"),
                treppeStuetzmauernPanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBeans"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel7.add(treppeStuetzmauernPanel1, gridBagConstraints);

        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(
                TreppeEditor.class,
                "TreppeEditor.jPanel7.TabConstraints.tabTitle"),
            jPanel7); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCard1.add(jTabbedPane1, gridBagConstraints);

        add(pnlCard1, "card1");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean}"),
                treppePicturePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        add(treppePicturePanel1, "card2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnInfoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnInfoActionPerformed
        ((CardLayout)getLayout()).show(this, "card1");
        btnImages.setEnabled(true);
        btnInfo.setEnabled(false);
        lblImages.setEnabled(true);
        lblInfo.setEnabled(false);
    }                                                                           //GEN-LAST:event_btnInfoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnImagesActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnImagesActionPerformed
        ((CardLayout)getLayout()).show(this, "card2");
        btnImages.setEnabled(false);
        btnInfo.setEnabled(true);
        lblImages.setEnabled(false);
        lblInfo.setEnabled(true);
    }                                                                             //GEN-LAST:event_btnImagesActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        TreppenReportGenerator.generateKatasterBlatt(Arrays.asList(new CidsBean[] { cidsBean }), TreppeEditor.this);
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink6ActionPerformed
        jTabbedPane1.setSelectedIndex(6);
    }                                                                                //GEN-LAST:event_jXHyperlink6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink5ActionPerformed
        jTabbedPane1.setSelectedIndex(5);
    }                                                                                //GEN-LAST:event_jXHyperlink5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink4ActionPerformed
        jTabbedPane1.setSelectedIndex(4);
    }                                                                                //GEN-LAST:event_jXHyperlink4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink3ActionPerformed
        jTabbedPane1.setSelectedIndex(3);
    }                                                                                //GEN-LAST:event_jXHyperlink3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink2ActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }                                                                                //GEN-LAST:event_jXHyperlink2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        lblTitle.setText("Treppe: " + cidsBean);
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            if (editable) {
                CidsBean entwaesserungBean = (CidsBean)cidsBean.getProperty("entwaesserung");
                if (entwaesserungBean == null) {
                    try {
                        entwaesserungBean = CidsBean.createNewCidsBeanFromTableName(
                                "WUNDA_BLAU",
                                "treppe_entwaesserung");
                        entwaesserungBean.setProperty(
                            "zustand",
                            CidsBean.createNewCidsBeanFromTableName("WUNDA_BLAU", "treppe_zustand"));
                        cidsBean.setProperty("entwaesserung", entwaesserungBean);
                    } catch (final Exception ex) {
                        LOG.error("could not create entwaesserung bean", ex);
                    }
                }
            }

            bindingGroup.unbind();
            bindingGroup.bind();
            overview.recalculateAll();
        }

        ((CardLayout)getLayout()).show(this, "card1");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ZustandOverview getOverview() {
        return overview;
    }

    @Override
    public void dispose() {
        treppeHandlaeufePanel2.dispose();
        treppePodestePanel1.dispose();
        treppeLeitelementePanel1.dispose();
        treppeHandlaeufePanel2.dispose();
        treppeEntwaesserung1.dispose();
        treppeStuetzmauernPanel1.dispose();
        treppePicturePanel1.dispose();
        bindingGroup.unbind();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public void setTitle(final String title) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent event) {
        treppePicturePanel1.editorClosed(event);
    }

    @Override
    public boolean prepareForSave() {
        return treppePicturePanel1.prepareForSave();
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getZustandStuetzmauern() {
        return overview.getZustandStuetzmauern();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Double getKostenStuetzmauern() {
        return overview.getKostenStuetzmauern();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, CidsBean> getMauerBeans() {
        return treppeStuetzmauernPanel1.getMauerBeans();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    public class ZustandOverview {

        //~ Instance fields ----------------------------------------------------

        private double zustandTreppen = 0;
        private double zustandPodeste = 0;
        private double zustandHandlauf = 0;
        private double zustandEntwaesserung = 0;
        private double zustandAbsturzsicherung = 0;
        private double zustandStuetzmauern = 0;
        private double zustandGesamt = 0;

        private double kostenTreppen = 0;
        private double kostenPodeste = 0;
        private double kostenHandlauf = 0;
        private double kostenEntwaesserung = 0;
        private double kostenAbsturzsicherung = 0;
        private double kostenStuetzmauern = 0;
        private double kostenGesamt = 0;

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        public void recalculateAll() {
            if (cidsBean != null) {
                recalculateTreppenlaeufe();
                recalculatePodeste();
                recalculateHandlauf();
                recalculateAbsturzsicherung();
                recalculateEntwaesserung();
                recalculateStuetzmauern();
                recalculateGesamt();
            }
            refreshView();
        }

        /**
         * DOCUMENT ME!
         */
        public void refreshView() {
            final Currency eur = Currency.getInstance("EUR");
            final NumberFormat formatKosten = NumberFormat.getCurrencyInstance(Locale.GERMANY);
            formatKosten.setCurrency(eur);
            jLabel29.setText(formatKosten.format(getKostenTreppen()));
            jLabel30.setText(formatKosten.format(getKostenPodeste()));
            jLabel31.setText(formatKosten.format(getKostenAbsturzsicherung()));
            jLabel32.setText(formatKosten.format(getKostenHandlauf()));
            jLabel33.setText(formatKosten.format(getKostenEntwaesserung()));

            jLabel34.setText(formatKosten.format(getKostenStuetzmauern()));
            jLabel35.setText(formatKosten.format(getKostenGesamt()));

            final NumberFormat formatZustand = new DecimalFormat("#.#");

            if (getZustandTreppen() < 2) {
                roundedPanel1.setBackground(GRUEN);
                roundedPanel1.setForeground(GRUEN);
            } else if (getZustandTreppen() < 3) {
                roundedPanel1.setBackground(GELB);
                roundedPanel1.setForeground(GELB);
            } else {
                roundedPanel1.setBackground(ROT);
                roundedPanel1.setForeground(ROT);
            }
            jLabel23.setText(formatZustand.format(getZustandTreppen()));

            if (getZustandPodeste() < 2) {
                roundedPanel2.setBackground(GRUEN);
                roundedPanel2.setForeground(GRUEN);
            } else if (getZustandPodeste() < 3) {
                roundedPanel2.setBackground(GELB);
                roundedPanel2.setForeground(GELB);
            } else {
                roundedPanel2.setBackground(ROT);
                roundedPanel2.setForeground(ROT);
            }
            jLabel24.setText(formatZustand.format(getZustandPodeste()));

            if (getZustandAbsturzsicherung() < 2) {
                roundedPanel3.setBackground(GRUEN);
                roundedPanel3.setForeground(GRUEN);
            } else if (getZustandAbsturzsicherung() < 3) {
                roundedPanel3.setBackground(GELB);
                roundedPanel3.setForeground(GELB);
            } else {
                roundedPanel3.setBackground(ROT);
                roundedPanel3.setForeground(ROT);
            }
            jLabel25.setText(formatZustand.format(getZustandAbsturzsicherung()));

            if (getZustandHandlauf() < 2) {
                roundedPanel4.setBackground(GRUEN);
                roundedPanel4.setForeground(GRUEN);
            } else if (getZustandHandlauf() < 3) {
                roundedPanel4.setBackground(GELB);
                roundedPanel4.setForeground(GELB);
            } else {
                roundedPanel4.setBackground(ROT);
                roundedPanel4.setForeground(ROT);
            }
            jLabel26.setText(formatZustand.format(getZustandHandlauf()));

            if (getZustandEntwaesserung() < 2) {
                roundedPanel5.setBackground(GRUEN);
                roundedPanel5.setForeground(GRUEN);
            } else if (getZustandEntwaesserung() < 3) {
                roundedPanel5.setBackground(GELB);
                roundedPanel5.setForeground(GELB);
            } else {
                roundedPanel5.setBackground(ROT);
                roundedPanel5.setForeground(ROT);
            }
            jLabel27.setText(formatZustand.format(getZustandEntwaesserung()));

            if (getZustandStuetzmauern() < 2) {
                roundedPanel6.setBackground(GRUEN);
                roundedPanel6.setForeground(GRUEN);
            } else if (getZustandStuetzmauern() < 3) {
                roundedPanel6.setBackground(GELB);
                roundedPanel6.setForeground(GELB);
            } else {
                roundedPanel6.setBackground(ROT);
                roundedPanel6.setForeground(ROT);
            }
            jLabel22.setText(formatZustand.format(getZustandStuetzmauern()));

            if (getZustandGesamt() < 2) {
                roundedPanel7.setBackground(GRUEN);
                roundedPanel7.setForeground(GRUEN);
            } else if (getZustandGesamt() < 3) {
                roundedPanel7.setBackground(GELB);
                roundedPanel7.setForeground(GELB);
            } else {
                roundedPanel7.setBackground(ROT);
                roundedPanel7.setForeground(ROT);
            }
            jLabel28.setText(formatZustand.format(getZustandGesamt()));
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateTreppenlaeufe() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("treppenlaeufe")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }
            setZustandTreppen(zustandGesamt);
            setKostenTreppen(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculatePodeste() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("podeste")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }
            setZustandPodeste(zustandGesamt);
            setKostenPodeste(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateHandlauf() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("handlaeufe")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandHandlauf(zustandGesamt);
            setKostenHandlauf(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateAbsturzsicherung() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean laufBean : cidsBean.getBeanCollectionProperty("absturzsicherungen")) {
                    final Double zustand = (laufBean != null) ? (Double)laufBean.getProperty("zustand.gesamt") : null;
                    final Double kosten = (laufBean != null) ? (Double)laufBean.getProperty("zustand.kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandAbsturzsicherung(zustandGesamt);
            setKostenAbsturzsicherung(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateEntwaesserung() {
            final Double zustand = (cidsBean != null) ? (Double)cidsBean.getProperty("entwaesserung.zustand.gesamt")
                                                      : null;
            final Double kosten = (cidsBean != null) ? (Double)cidsBean.getProperty("entwaesserung.zustand.kosten")
                                                     : null;

            setZustandEntwaesserung((zustand != null) ? zustand : 0);
            setKostenEntwaesserung((kosten != null) ? kosten : 0);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateStuetzmauern() {
            double zustandGesamt = 0;
            double kostenGesamt = 0;
            if (cidsBean != null) {
                for (final CidsBean zustandBean : treppeStuetzmauernPanel1.getZustandBeans()) {
                    final Double zustand = (zustandBean != null) ? (Double)zustandBean.getProperty("gesamt") : null;
                    final Double kosten = (zustandBean != null) ? (Double)zustandBean.getProperty("kosten") : null;

                    kostenGesamt += ((kosten != null) ? kosten : 0);
                    if ((zustand != null) && (zustand > zustandGesamt)) {
                        zustandGesamt = zustand;
                    }
                }
            }

            setZustandStuetzmauern(zustandGesamt);
            setKostenStuetzmauern(kostenGesamt);
        }

        /**
         * DOCUMENT ME!
         */
        public void recalculateGesamt() {
            final double[] kostenAll = new double[] {
                    kostenTreppen,
                    kostenPodeste,
                    kostenHandlauf,
                    kostenEntwaesserung,
                    kostenAbsturzsicherung,
                    kostenStuetzmauern
                };
            final double[] zustandAll = new double[] {
                    zustandTreppen,
                    zustandPodeste,
                    zustandHandlauf,
                    zustandEntwaesserung,
                    zustandAbsturzsicherung,
                    zustandStuetzmauern
                };

            double kosten = 0;
            double zustand = 0;
            for (int i = 0; i < kostenAll.length; i++) {
                kosten += kostenAll[i];
                if (zustandAll[i] > zustand) {
                    zustand = zustandAll[i];
                }
            }

            setKostenGesamt(kosten);
            setZustandGesamt(zustand);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class IntegerToLongConverter extends Converter<Integer, Long> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Long convertForward(final Integer i) {
            if (i == null) {
                return null;
            }
            return i.longValue();
        }

        @Override
        public Integer convertReverse(final Long l) {
            if (l == null) {
                return null;
            }
            return l.intValue();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class DoubleToLongConverter extends Converter<Double, Long> {

        //~ Methods ------------------------------------------------------------

        @Override
        public Long convertForward(final Double d) {
            if (d == null) {
                return null;
            }
            return d.longValue();
        }

        @Override
        public Double convertReverse(final Long l) {
            if (l == null) {
                return null;
            }
            return l.doubleValue();
        }
    }
}
