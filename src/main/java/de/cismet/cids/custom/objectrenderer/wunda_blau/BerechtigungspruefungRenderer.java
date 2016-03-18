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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.jdesktop.beansbinding.Converter;

import java.awt.Component;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.converter.SQLTimestampToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.BaulastBescheinigungUtils;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungAhnhangDownloadAction;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungFreigabeServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.tools.BrowserLauncher;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BerechtigungspruefungRenderer extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BerechtigungspruefungRenderer.class);

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT);

    //~ Instance fields --------------------------------------------------------

    private final ObjectMapper MAPPER = new ObjectMapper();

    private String title;
    private CidsBean cidsBean;

    private BerechtigungspruefungDownloadInfo downloadInfo = null;
    private BerechtigungspruefungBescheinigungDownloadInfo bescheinigungDownloadInfo = null;

    private final Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> baulastMap =
        new HashMap<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog diaFreigabe;
    private javax.swing.JDialog diaStorno;
    private org.jdesktop.swingx.JXHyperlink hlDateianhangValue;
    private org.jdesktop.swingx.JXHyperlink hlEMailValue;
    private org.jdesktop.swingx.JXHyperlink hlVorschauValue;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<BerechtigungspruefungBescheinigungBaulastInfo> jList1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JLabel labInfoTitle;
    private javax.swing.JLabel labInfoTitle1;
    private javax.swing.JLabel labInfoTitle2;
    private javax.swing.JLabel lblAbgeholtAm;
    private javax.swing.JLabel lblAbholStatus;
    private javax.swing.JLabel lblAbholStatusValue;
    private javax.swing.JLabel lblAnfrageVon;
    private javax.swing.JLabel lblAnfrageVonValue;
    private javax.swing.JLabel lblBegruendungstext;
    private javax.swing.JLabel lblBegruendungstext2;
    private javax.swing.JLabel lblBerechtigungsgrund;
    private javax.swing.JLabel lblBerechtigungsgrundValue;
    private javax.swing.JLabel lblDateianhang;
    private javax.swing.JLabel lblEingegangenAm;
    private javax.swing.JLabel lblEingegangenAmValue;
    private javax.swing.JLabel lblGeprueftAmValue;
    private javax.swing.JLabel lblProdukt;
    private javax.swing.JLabel lblProduktValue;
    private javax.swing.JLabel lblPruefKommentar;
    private javax.swing.JLabel lblPruefStatus;
    private javax.swing.JLabel lblPruefStatusValue;
    private javax.swing.JLabel lblPruefer;
    private javax.swing.JLabel lblPrueferValue;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JLabel lblSchluesselValue;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleValue;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panInfo;
    private javax.swing.JPanel panInfo1;
    private javax.swing.JPanel panLieferanschrift;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private javax.swing.JScrollPane txaBegruendungstextValue;
    private javax.swing.JScrollPane txaBegruendungstextValue1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Fs_BestellungRenderer.
     */
    public BerechtigungspruefungRenderer() {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        initComponents();
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panTitle = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        lblTitleValue = new javax.swing.JLabel();
        panFooter = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        diaFreigabe = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        diaStorno = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        panMain = new javax.swing.JPanel();
        panInfo = new javax.swing.JPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblSchluessel = new javax.swing.JLabel();
        lblSchluesselValue = new javax.swing.JLabel();
        lblProdukt = new javax.swing.JLabel();
        lblProduktValue = new javax.swing.JLabel();
        lblEingegangenAm = new javax.swing.JLabel();
        lblEingegangenAmValue = new javax.swing.JLabel();
        lblAnfrageVon = new javax.swing.JLabel();
        lblAnfrageVonValue = new javax.swing.JLabel();
        hlEMailValue = new org.jdesktop.swingx.JXHyperlink();
        lblPruefStatus = new javax.swing.JLabel();
        lblPruefStatusValue = new javax.swing.JLabel();
        lblPruefer = new javax.swing.JLabel();
        lblPrueferValue = new javax.swing.JLabel();
        lblGeprueftAmValue = new javax.swing.JLabel();
        lblPruefKommentar = new javax.swing.JLabel();
        txaBegruendungstextValue1 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        panLieferanschrift = new javax.swing.JPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        lblAbholStatus = new javax.swing.JLabel();
        lblAbholStatusValue = new javax.swing.JLabel();
        lblAbgeholtAm = new javax.swing.JLabel();
        lblVorschau = new javax.swing.JLabel();
        hlVorschauValue = new org.jdesktop.swingx.JXHyperlink();
        lblBegruendungstext2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<BerechtigungspruefungBescheinigungBaulastInfo>();
        jPanel7 = new javax.swing.JPanel();
        panInfo1 = new javax.swing.JPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblBerechtigungsgrund = new javax.swing.JLabel();
        lblBerechtigungsgrundValue = new javax.swing.JLabel();
        lblBegruendungstext = new javax.swing.JLabel();
        txaBegruendungstextValue = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lblDateianhang = new javax.swing.JLabel();
        hlDateianhangValue = new org.jdesktop.swingx.JXHyperlink();
        jPanel6 = new javax.swing.JPanel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTitle,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);

        lblTitleValue.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblTitleValue.setForeground(new java.awt.Color(255, 255, 255));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${title}"),
                lblTitleValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(lblTitleValue, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton2.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.postweg}"),
                jButton2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panFooter.add(jButton2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panFooter.add(jButton1, gridBagConstraints);

        diaFreigabe.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        diaFreigabe.setTitle(org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.diaFreigabe.title")); // NOI18N
        diaFreigabe.setMinimumSize(new java.awt.Dimension(300, 200));
        diaFreigabe.setModal(true);
        diaFreigabe.setPreferredSize(new java.awt.Dimension(300, 200));
        diaFreigabe.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jTextArea3.setColumns(20);
        jTextArea3.setRows(5);
        jScrollPane2.setViewportView(jTextArea3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(jLabel1, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel5.add(jButton3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton4,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton4.text")); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jPanel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        diaFreigabe.getContentPane().add(jPanel4, gridBagConstraints);

        diaStorno.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        diaStorno.setTitle(org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.diaStorno.title")); // NOI18N
        diaStorno.setMinimumSize(new java.awt.Dimension(300, 200));
        diaStorno.setModal(true);
        diaStorno.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jTextArea4.setColumns(20);
        jTextArea4.setRows(5);
        jScrollPane3.setViewportView(jTextArea4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        jPanel8.add(jScrollPane3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel8.add(jLabel2, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton5.text")); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel9.add(jButton5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton6,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton6.text")); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(jButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jPanel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        diaStorno.getContentPane().add(jPanel8, gridBagConstraints);

        setLayout(new java.awt.GridBagLayout());

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        panInfo.setOpaque(false);
        panInfo.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        labInfoTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labInfoTitle,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.labInfoTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(labInfoTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panInfo.add(semiRoundedPanel1, gridBagConstraints);

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblSchluessel,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblSchluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblSchluessel, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.schluessel}"),
                lblSchluesselValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblSchluesselValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProdukt,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblProdukt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblProdukt, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProduktValue,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblProduktValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblProduktValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEingegangenAm,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblEingegangenAm.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEingegangenAm, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anfrage_timestamp}"),
                lblEingegangenAmValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEingegangenAmValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnfrageVon,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblAnfrageVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblAnfrageVon, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${kundenName}"),
                lblAnfrageVonValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblAnfrageVonValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${kundenMail}"),
                hlEMailValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        hlEMailValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlEMailValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlEMailValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefStatus,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefStatus, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefStatusValue,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefStatusValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefStatusValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefer,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefer}"),
                lblPrueferValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPrueferValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefung_timestamp}"),
                lblGeprueftAmValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGeprueftAmValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefKommentar,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefKommentar.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefKommentar, gridBagConstraints);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pruefkommentar}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txaBegruendungstextValue1.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txaBegruendungstextValue1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInfo.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        panMain.add(panInfo, gridBagConstraints);

        panLieferanschrift.setOpaque(false);
        panLieferanschrift.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel2.setLayout(new java.awt.GridBagLayout());

        labInfoTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labInfoTitle1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.labInfoTitle1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel2.add(labInfoTitle1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panLieferanschrift.add(semiRoundedPanel2, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.CardLayout());

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAbholStatus,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblAbholStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(lblAbholStatus, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAbholStatusValue,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblAbholStatusValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(lblAbholStatusValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.abgeholt_timestamp}"),
                lblAbgeholtAm,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(lblAbgeholtAm, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorschau,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblVorschau.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(lblVorschau, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            hlVorschauValue,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.hlVorschauValue.text")); // NOI18N
        hlVorschauValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlVorschauValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(hlVorschauValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBegruendungstext2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBegruendungstext2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(lblBegruendungstext2, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(54, 150));

        jList1.setModel(new DefaultListModel<BerechtigungspruefungBescheinigungBaulastInfo>());
        jList1.setCellRenderer(new BaulastInfoListCellRenderer());
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel10.add(jScrollPane1, gridBagConstraints);

        jPanel7.setOpaque(false);

        final javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel10.add(jPanel7, gridBagConstraints);

        jPanel2.add(jPanel10, "card2");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLieferanschrift.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 9, 10, 0);
        panMain.add(panLieferanschrift, gridBagConstraints);

        panInfo1.setOpaque(false);
        panInfo1.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel3.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel3.setLayout(new java.awt.GridBagLayout());

        labInfoTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labInfoTitle2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.labInfoTitle2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(labInfoTitle2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panInfo1.add(semiRoundedPanel3, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBerechtigungsgrund,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBerechtigungsgrund.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblBerechtigungsgrund, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.berechtigungsgrund}"),
                lblBerechtigungsgrundValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblBerechtigungsgrundValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBegruendungstext,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBegruendungstext.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblBegruendungstext, gridBagConstraints);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.begruendung}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txaBegruendungstextValue.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(txaBegruendungstextValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblDateianhang,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblDateianhang.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblDateianhang, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dateiname != null}"),
                hlDateianhangValue,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dateiname}"),
                hlDateianhangValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        hlDateianhangValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlDateianhangValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(hlDateianhangValue, gridBagConstraints);

        jPanel6.setOpaque(false);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInfo1.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        panMain.add(panInfo1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 20, 20, 20);
        add(panMain, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlEMailValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlEMailValueActionPerformed
        try {
            BrowserLauncher.openURL("mailto:" + "");
        } catch (Exception ex) {
            LOG.warn("could not open mailto link", ex);
        }
    }                                                                                //GEN-LAST:event_hlEMailValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        jTextArea3.setText("");
        StaticSwingTools.showDialog(diaFreigabe);
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlDateianhangValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlDateianhangValueActionPerformed
        final String dateiName = (String)cidsBean.getProperty("dateiname");
        final int extPos = dateiName.lastIndexOf(".");
        final String pureName = dateiName.substring(0, extPos);
        final String ext = dateiName.substring(extPos);

        final Download download = new ByteArrayActionDownload(
                BerechtigungspruefungAhnhangDownloadAction.TASK_NAME,
                (String)cidsBean.getProperty("schluessel"),
                null,
                "Berechtigungs-Prüfung - Dateianhang: "
                        + (String)cidsBean.getProperty("schluessel"),
                DownloadManagerDialog.getInstance().getJobName(),
                pureName,
                ext);
        DownloadManager.instance().add(download);
    } //GEN-LAST:event_hlDateianhangValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlVorschauValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlVorschauValueActionPerformed
        BaulastBescheinigungUtils.doDownload(bescheinigungDownloadInfo);
    }                                                                                   //GEN-LAST:event_hlVorschauValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton6ActionPerformed
        diaStorno.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton4ActionPerformed
        diaFreigabe.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton3ActionPerformed
        executeFreigabe();
        diaFreigabe.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton5ActionPerformed
        executeStorno();
        diaStorno.setVisible(false);
    }                                                                            //GEN-LAST:event_jButton5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton1ActionPerformed
        jTextArea4.setText("");
        StaticSwingTools.showDialog(diaStorno);
    }                                                                            //GEN-LAST:event_jButton1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = jList1.getSelectedValue();
            if (selObject instanceof BerechtigungspruefungBescheinigungBaulastInfo) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObjectNode(new MetaObjectNode(
                                baulastMap.get((BerechtigungspruefungBescheinigungBaulastInfo)selObject)),
                            false);
            }
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     */
    private void executeFreigabe() {
        new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        final String schluessel = (String)cidsBean.getProperty("schluessel");
                        final String kommentar = jTextArea3.getText();
                        return (Boolean)SessionManager.getSession().getConnection()
                                    .executeTask(SessionManager.getSession().getUser(),
                                            BerechtigungspruefungFreigabeServerAction.TASK_NAME,
                                            SessionManager.getSession().getUser().getDomain(),
                                            schluessel,
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.KOMMENTAR
                                                    .toString(),
                                                kommentar),
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.MODUS
                                                    .toString(),
                                                BerechtigungspruefungFreigabeServerAction.MODUS_FREIGABE));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    final Boolean ret;

                    try {
                        ret = get();
                        if (ret == null) {
                            LOG.error("Fehler beim Freigeben");
                        } else if (!ret) {
                            LOG.error("Anfrage wurde bereits bearbeitet.");
                        }
                    } catch (final Exception ex) {
                        LOG.error("Fehler beim Freigeben", ex);
                    }
                    jButton1.setEnabled(false);
                    jButton2.setEnabled(false);
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void executeStorno() {
        new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        final String schluessel = (String)cidsBean.getProperty("schluessel");
                        final String kommentar = jTextArea4.getText();
                        return (Boolean)SessionManager.getSession().getConnection()
                                    .executeTask(SessionManager.getSession().getUser(),
                                            BerechtigungspruefungFreigabeServerAction.TASK_NAME,
                                            SessionManager.getSession().getUser().getDomain(),
                                            schluessel,
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.KOMMENTAR
                                                    .toString(),
                                                kommentar),
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.MODUS
                                                    .toString(),
                                                BerechtigungspruefungFreigabeServerAction.MODUS_STORNO));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    final Boolean ret;

                    try {
                        ret = get();
                        if (ret == null) {
                            LOG.error("Fehler beim Freigeben");
                        } else if (!ret) {
                            LOG.error("Anfrage wurde bereits bearbeitet.");
                        }
                    } catch (final Exception ex) {
                        LOG.error("Fehler beim Freigeben", ex);
                    }

                    jButton1.setEnabled(false);
                    jButton2.setEnabled(false);
                }
            }.execute();
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        baulastMap.clear();
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        lblAnfrageVonValue.setText(((cidsBean != null) && (cidsBean.getProperty("benutzer") != null))
                ? (String)cidsBean.getProperty("benutzer") : "-");

        ((DefaultListModel<BerechtigungspruefungBescheinigungBaulastInfo>)jList1.getModel()).clear();
        if (cidsBean != null) {
            if (cidsBean.getProperty("pruefstatus") == null) {
                lblPruefStatusValue.setText("offen");
            } else {
                lblPruefStatusValue.setText((Boolean)cidsBean.getProperty("pruefstatus") ? "freigegeben" : "storniert");
            }

            jButton1.setEnabled(cidsBean.getProperty("pruefstatus") == null);
            jButton2.setEnabled(cidsBean.getProperty("pruefstatus") == null);

            new SwingWorker<CidsBean, Void>() {

                    @Override
                    protected CidsBean doInBackground() throws Exception {
                        return BillingPopup.getInstance().getExternalUser((String)cidsBean.getProperty("benutzer"));
                    }

                    @Override
                    protected void done() {
                        try {
                            final CidsBean externalUser = get();
                            lblAnfrageVonValue.setText((String)externalUser.getProperty("name"));
                            hlEMailValue.setText((String)externalUser.getProperty("kontakt"));
                        } catch (final Exception ex) {
                            LOG.warn(ex, ex);
                        }
                    }
                }.execute();

            if (cidsBean.getProperty("downloadinfo_json") != null) {
                try {
                    downloadInfo =
                        new ObjectMapper().readValue((String)cidsBean.getProperty("downloadinfo_json"),
                            BerechtigungspruefungDownloadInfo.class);

                    if (BerechtigungspruefungBescheinigungDownloadInfo.PRODUKT_TYP.equals(
                                    downloadInfo.getProduktTyp())) {
                        bescheinigungDownloadInfo =
                            new ObjectMapper().readValue((String)cidsBean.getProperty("downloadinfo_json"),
                                BerechtigungspruefungBescheinigungDownloadInfo.class);

                        final Collection<BerechtigungspruefungBescheinigungBaulastInfo> baulastInfos =
                            new ArrayList<BerechtigungspruefungBescheinigungBaulastInfo>();
                        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppeInfo
                                    : bescheinigungDownloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen()) {
                            for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                        : gruppeInfo.getBaulastenBeguenstigt()) {
                                baulastInfos.add(baulastInfo);
                            }
                            for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                        : gruppeInfo.getBaulastenBelastet()) {
                                baulastInfos.add(baulastInfo);
                            }
                        }

                        for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo : baulastInfos) {
                            new SwingWorker<CidsBean, Object>() {

                                    @Override
                                    protected CidsBean doInBackground() throws Exception {
                                        final CidsBean selBean = BaulastBescheinigungUtils.loadBaulast(baulastInfo);
                                        return selBean;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            baulastMap.put(baulastInfo, (CidsBean)get());
                                        } catch (final Exception ex) {
                                            LOG.warn(ex, ex);
                                        }
                                        jList1.revalidate();
                                    }
                                }.execute();
                        }

                        for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo : baulastInfos) {
                            ((DefaultListModel<BerechtigungspruefungBescheinigungBaulastInfo>)jList1.getModel())
                                    .addElement(baulastInfo);
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                    downloadInfo = null;
                }
            } else {
                downloadInfo = null;
            }
        } else {
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            lblPruefStatusValue.setText("-");

            lblAnfrageVonValue.setText("-");
            hlEMailValue.setText("-");

            downloadInfo = null;
        }

        lblAbholStatusValue.setText(((cidsBean != null) && Boolean.TRUE.equals(cidsBean.getProperty("abgeholt")))
                ? (Boolean.TRUE.equals(cidsBean.getProperty("pruefstatus")) ? "Vom Kunden heruntergeladen"
                                                                            : "Stornomeldung erhalten")
                : "Vom Kunden noch nicht heruntergeladen");

        bindingGroup.bind();
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class CurrencyConverter extends Converter<Double, String> {

        //~ Instance fields ----------------------------------------------------

        private final NumberFormat formatter = NumberFormat.getCurrencyInstance();

        //~ Methods ------------------------------------------------------------

        @Override
        public String convertForward(final Double value) {
            return formatter.format(value);
        }

        @Override
        public Double convertReverse(final String string) {
            throw new UnsupportedOperationException("not needed");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class BaulastInfoListCellRenderer extends DefaultListCellRenderer {

        //~ Methods ------------------------------------------------------------

        @Override
        public Component getListCellRendererComponent(final JList<?> list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            final JLabel component = (JLabel)super.getListCellRendererComponent(
                    list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus); // To change body of generated methods, choose Tools | Templates.

            component.setEnabled(baulastMap.containsKey((BerechtigungspruefungBescheinigungBaulastInfo)value));
            return component;
        }
    }
}
