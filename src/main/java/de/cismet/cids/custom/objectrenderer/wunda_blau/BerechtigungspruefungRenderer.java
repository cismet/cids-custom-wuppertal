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
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.CardLayout;
import java.awt.Component;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.berechtigungspruefung.BerechtigungspruefungMessageNotifier;
import de.cismet.cids.custom.clientutils.BerechtigungspruefungKonfiguration;
import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.clientutils.CachedInfoBaulastRetriever;
import de.cismet.cids.custom.objectrenderer.converter.SQLTimestampToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisProducts;
import de.cismet.cids.custom.objectrenderer.utils.billing.ClientBillingUtils;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.BerechtigungspruefungHandler;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungFlurstueckInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungAnhangDownloadAction;
import de.cismet.cids.custom.wunda_blau.search.actions.BerechtigungspruefungFreigabeServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.CidsAlkisSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FormSolutionsBestellungSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

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
    FooterComponentProvider,
    ConnectionContextStore {

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
    private CidsBean[] bestellungBeans;

    private BerechtigungspruefungDownloadInfo downloadInfo = null;

    private final Map<String, CidsBean> baulastMap = new HashMap<>();
    private final Map<String, CidsBean> alkisMap = new HashMap<>();

    private final Map<String, String> freigabegruendeMap = new HashMap<>();
    private final Map<String, String> ablehnungsgruendeMap = new HashMap<>();

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFreigeben;
    private javax.swing.JButton btnFreigeben1;
    private javax.swing.JButton btnStorno;
    private javax.swing.JDialog diaFreigabe;
    private javax.swing.JDialog diaStorno;
    private org.jdesktop.swingx.JXHyperlink hlBestellung;
    private org.jdesktop.swingx.JXHyperlink hlBestellung1;
    private org.jdesktop.swingx.JXHyperlink hlDateianhangValue;
    private org.jdesktop.swingx.JXHyperlink hlEMailValue;
    private org.jdesktop.swingx.JXHyperlink hlVorschauValue;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JTextArea jTextArea6;
    private javax.swing.JLabel labInfoTitle;
    private javax.swing.JLabel labInfoTitle1;
    private javax.swing.JLabel labInfoTitle2;
    private javax.swing.JLabel lblAbholStatus;
    private javax.swing.JLabel lblAbholStatusValue;
    private javax.swing.JLabel lblAnfrageVon;
    private javax.swing.JLabel lblAnfrageVonValue;
    private javax.swing.JLabel lblBegruendungstext;
    private javax.swing.JLabel lblBegruendungstext2;
    private javax.swing.JLabel lblBegruendungstext3;
    private javax.swing.JLabel lblBegruendungstext4;
    private javax.swing.JLabel lblBerechtigungsgrund;
    private javax.swing.JLabel lblBerechtigungsgrundValue;
    private javax.swing.JLabel lblBestellung;
    private javax.swing.JLabel lblBestellung1;
    private javax.swing.JLabel lblDateianhang;
    private javax.swing.JLabel lblEMail;
    private javax.swing.JLabel lblEingegangenAmValue;
    private javax.swing.JLabel lblGeprueftAmValue;
    private javax.swing.JLabel lblPruefKommentar;
    private javax.swing.JLabel lblPruefStatus;
    private javax.swing.JLabel lblPruefStatusValue;
    private javax.swing.JLabel lblPruefTsValue;
    private javax.swing.JLabel lblPruefer;
    private javax.swing.JLabel lblPrueferValue;
    private javax.swing.JLabel lblSchluessel;
    private javax.swing.JLabel lblSchluesselValue;
    private javax.swing.JLabel lblTelNummer;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleValue;
    private javax.swing.JLabel lblVorschau;
    private javax.swing.JPanel panDownloadTyp;
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
    private javax.swing.JTextField txtTelNummerValue;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BerechtigungspruefungRenderer object.
     */
    public BerechtigungspruefungRenderer() {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();

        final DefaultComboBoxModel<String> freigabegruendeModel = new DefaultComboBoxModel<String>();
        freigabegruendeModel.addElement("<html><i>Vorlage auswählen</i>");
        for (final BerechtigungspruefungKonfiguration.Freigabegrund freigabegrund
                    : BerechtigungspruefungKonfiguration.INSTANCE.getFreigabegruende()) {
            freigabegruendeMap.put(freigabegrund.getVorlage(), freigabegrund.getLangtext());
            freigabegruendeModel.addElement(freigabegrund.getVorlage());
        }
        jComboBox1.setModel(freigabegruendeModel);

        final DefaultComboBoxModel<String> ablehnungsgruendeModel = new DefaultComboBoxModel<String>();
        ablehnungsgruendeModel.addElement("<html><i>Vorlage auswählen.</i>");
        for (final BerechtigungspruefungKonfiguration.Ablehnungsgrund ablehnungsgrund
                    : BerechtigungspruefungKonfiguration.INSTANCE.getAblehnungsgruende()) {
            ablehnungsgruendeMap.put(ablehnungsgrund.getVorlage(), ablehnungsgrund.getLangtext());
            ablehnungsgruendeModel.addElement(ablehnungsgrund.getVorlage());
        }
        jComboBox2.setModel(ablehnungsgruendeModel);
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
        lblTitleValue = new javax.swing.JLabel();
        panFooter = new javax.swing.JPanel();
        btnFreigeben = new javax.swing.JButton();
        btnStorno = new javax.swing.JButton();
        btnFreigeben1 = new javax.swing.JButton();
        diaFreigabe = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jComboBox1 = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        diaStorno = new javax.swing.JDialog();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<String>();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        panMain = new javax.swing.JPanel();
        panInfo = new javax.swing.JPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblSchluessel = new javax.swing.JLabel();
        lblSchluesselValue = new javax.swing.JLabel();
        lblEingegangenAmValue = new javax.swing.JLabel();
        lblAnfrageVon = new javax.swing.JLabel();
        lblAnfrageVonValue = new javax.swing.JLabel();
        lblEMail = new javax.swing.JLabel();
        hlEMailValue = new org.jdesktop.swingx.JXHyperlink();
        lblTelNummer = new javax.swing.JLabel();
        txtTelNummerValue = new javax.swing.JTextField();
        lblPruefStatus = new javax.swing.JLabel();
        lblPruefStatusValue = new javax.swing.JLabel();
        lblPruefTsValue = new javax.swing.JLabel();
        lblPruefer = new javax.swing.JLabel();
        lblPrueferValue = new javax.swing.JLabel();
        lblGeprueftAmValue = new javax.swing.JLabel();
        lblPruefKommentar = new javax.swing.JLabel();
        txaBegruendungstextValue1 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel7 = new javax.swing.JPanel();
        lblBestellung = new javax.swing.JLabel();
        hlBestellung = new org.jdesktop.swingx.JXHyperlink();
        lblBestellung1 = new javax.swing.JLabel();
        hlBestellung1 = new org.jdesktop.swingx.JXHyperlink();
        panLieferanschrift = new javax.swing.JPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblAbholStatus = new javax.swing.JLabel();
        lblAbholStatusValue = new javax.swing.JLabel();
        lblVorschau = new javax.swing.JLabel();
        hlVorschauValue = new org.jdesktop.swingx.JXHyperlink();
        panDownloadTyp = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<String>();
        lblBegruendungstext4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<String>();
        lblBegruendungstext2 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<String>();
        lblBegruendungstext3 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
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
            btnFreigeben,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.btnFreigeben.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.postweg}"),
                btnFreigeben,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnFreigeben.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFreigebenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panFooter.add(btnFreigeben, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnStorno,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.btnStorno.text")); // NOI18N
        btnStorno.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnStornoActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panFooter.add(btnStorno, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnFreigeben1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.btnFreigeben1.text")); // NOI18N
        btnFreigeben1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnFreigeben1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        panFooter.add(btnFreigeben1, gridBagConstraints);

        diaFreigabe.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        diaFreigabe.setTitle(org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.diaFreigabe.title")); // NOI18N
        diaFreigabe.setMinimumSize(new java.awt.Dimension(350, 250));
        diaFreigabe.setModal(true);
        diaFreigabe.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jTextArea3.setColumns(20);
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setWrapStyleWord(true);
        jScrollPane2.setViewportView(jTextArea3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel4.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel4.add(jLabel1, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton3,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton3.text")); // NOI18N
        jButton3.setEnabled(false);
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(jPanel5, gridBagConstraints);

        jTextArea5.setEditable(false);
        jTextArea5.setColumns(20);
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(5);
        jTextArea5.setWrapStyleWord(true);
        jScrollPane4.setViewportView(jTextArea5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jScrollPane4, gridBagConstraints);

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel4.add(jComboBox1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel3,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

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
        diaStorno.setMinimumSize(new java.awt.Dimension(350, 250));
        diaStorno.setModal(true);
        diaStorno.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel8.setLayout(new java.awt.GridBagLayout());

        jTextArea4.setColumns(20);
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setWrapStyleWord(true);
        jScrollPane3.setViewportView(jTextArea4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel8.add(jScrollPane3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel8.add(jLabel2, gridBagConstraints);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jButton5,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jButton5.text")); // NOI18N
        jButton5.setEnabled(false);
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel8.add(jPanel9, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel4,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel8.add(jLabel4, gridBagConstraints);

        jComboBox2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jComboBox2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel8.add(jComboBox2, gridBagConstraints);

        jTextArea6.setEditable(false);
        jTextArea6.setColumns(20);
        jTextArea6.setLineWrap(true);
        jTextArea6.setRows(5);
        jTextArea6.setWrapStyleWord(true);
        jScrollPane5.setViewportView(jTextArea6);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel8.add(jScrollPane5, gridBagConstraints);

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

        jPanel1.setMinimumSize(new java.awt.Dimension(343, 252));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblSchluessel,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblSchluessel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblSchluesselValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anfrage_timestamp}"),
                lblEingegangenAmValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEingegangenAmValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAnfrageVon,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblAnfrageVon.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblAnfrageVon, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblAnfrageVonValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEMail,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblEMail.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEMail, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            hlEMailValue,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.hlEMailValue.text")); // NOI18N
        hlEMailValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlEMailValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlEMailValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblTelNummer,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblTelNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblTelNummer, gridBagConstraints);

        txtTelNummerValue.setEditable(false);
        txtTelNummerValue.setBackground(null);
        txtTelNummerValue.setText(org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.txtTelNummerValue.text")); // NOI18N
        txtTelNummerValue.setBorder(null);
        txtTelNummerValue.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txtTelNummerValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefStatus,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefStatusValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.freigabe_timestamp}"),
                lblPruefTsValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefTsValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblPruefer,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblPruefer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
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
        gridBagConstraints.gridy = 7;
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
        gridBagConstraints.gridy = 7;
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblPruefKommentar, gridBagConstraints);

        txaBegruendungstextValue1.setMinimumSize(new java.awt.Dimension(222, 92));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(6);
        jTextArea2.setWrapStyleWord(true);

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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(txaBegruendungstextValue1, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBestellung,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBestellung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBestellung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            hlBestellung,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.hlBestellung.text")); // NOI18N
        hlBestellung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBestellungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlBestellung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBestellung1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBestellung1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBestellung1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            hlBestellung1,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.hlBestellung1.text")); // NOI18N
        hlBestellung1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBestellung1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlBestellung1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panInfo.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
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
        jPanel2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAbholStatus,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblAbholStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblAbholStatus, gridBagConstraints);

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
        jPanel2.add(lblAbholStatusValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorschau,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblVorschau.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblVorschau, gridBagConstraints);

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(hlVorschauValue, gridBagConstraints);

        panDownloadTyp.setBorder(javax.swing.BorderFactory.createTitledBorder(
                org.openide.util.NbBundle.getMessage(
                    BerechtigungspruefungRenderer.class,
                    "BerechtigungspruefungRenderer.panDownloadTyp.border.title"))); // NOI18N
        panDownloadTyp.setMinimumSize(new java.awt.Dimension(355, 242));
        panDownloadTyp.setOpaque(false);
        panDownloadTyp.setLayout(new java.awt.CardLayout());

        jPanel15.setOpaque(false);
        jPanel15.setLayout(new java.awt.GridBagLayout());

        jScrollPane7.setMinimumSize(new java.awt.Dimension(100, 100));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(100, 100));

        jList3.setModel(new DefaultListModel<String>());
        jList3.setCellRenderer(new AlkisFlurstueckInfoListCellRenderer());
        jList3.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList3MouseClicked(evt);
                }
            });
        jScrollPane7.setViewportView(jList3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jScrollPane7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBegruendungstext4,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBegruendungstext4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(lblBegruendungstext4, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(100, 100));

        jList1.setModel(new DefaultListModel<String>());
        jList1.setCellRenderer(new BescheinigungBaulastInfoListCellRenderer());
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList1MouseClicked(evt);
                }
            });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBegruendungstext2,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBegruendungstext2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel15.add(lblBegruendungstext2, gridBagConstraints);

        panDownloadTyp.add(jPanel15, "card2");

        jPanel12.setMinimumSize(new java.awt.Dimension(107, 50));
        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel5,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel5, gridBagConstraints);

        jScrollPane6.setMinimumSize(new java.awt.Dimension(100, 100));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(100, 100));

        jList2.setModel(new DefaultListModel<String>());
        jList2.setCellRenderer(new AlkisFlurstueckInfoListCellRenderer());
        jList2.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    jList2MouseClicked(evt);
                }
            });
        jScrollPane6.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jScrollPane6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBegruendungstext3,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.lblBegruendungstext3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(lblBegruendungstext3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel9,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel12.add(jLabel9, gridBagConstraints);

        panDownloadTyp.add(jPanel12, "katasterauszug");

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel6,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel6.text")); // NOI18N
        jPanel13.add(jLabel6, new java.awt.GridBagConstraints());

        panDownloadTyp.add(jPanel13, "card3");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(panDownloadTyp, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel7,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jLabel8,
            org.openide.util.NbBundle.getMessage(
                BerechtigungspruefungRenderer.class,
                "BerechtigungspruefungRenderer.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel8, gridBagConstraints);

        jPanel10.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel10, gridBagConstraints);

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
        gridBagConstraints.weighty = 1.0;
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

        jPanel3.setMinimumSize(new java.awt.Dimension(254, 152));
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
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(6);
        jTextArea1.setWrapStyleWord(true);

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
    private void btnFreigebenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFreigebenActionPerformed
        jTextArea3.setText("");
        diaFreigabe.pack();
        StaticSwingTools.showDialog(diaFreigabe);
    }                                                                                //GEN-LAST:event_btnFreigebenActionPerformed

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
                BerechtigungspruefungAnhangDownloadAction.TASK_NAME,
                (String)cidsBean.getProperty("schluessel"),
                null,
                "Berechtigungs-Prüfung - Dateianhang: "
                        + (String)cidsBean.getProperty("schluessel"),
                DownloadManagerDialog.getInstance().getJobName(),
                pureName,
                ext,
                getConnectionContext());
        DownloadManager.instance().add(download);
    } //GEN-LAST:event_hlDateianhangValueActionPerformed

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
    private void btnStornoActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnStornoActionPerformed
        jTextArea4.setText("");
        diaStorno.pack();
        StaticSwingTools.showDialog(diaStorno);
    }                                                                             //GEN-LAST:event_btnStornoActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox1ActionPerformed
        final String vorlage = (String)jComboBox1.getSelectedItem();
        final String langtext = freigabegruendeMap.get(vorlage);
        jTextArea5.setText(langtext);
        jButton3.setEnabled(langtext != null);
        diaFreigabe.pack();
    }                                                                              //GEN-LAST:event_jComboBox1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jComboBox2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jComboBox2ActionPerformed
        final String vorlage = (String)jComboBox2.getSelectedItem();
        final String langtext = ablehnungsgruendeMap.get(vorlage);
        jTextArea6.setText(langtext);
        jButton5.setEnabled(langtext != null);
        diaStorno.pack();
    }                                                                              //GEN-LAST:event_jComboBox2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList1MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList1MouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = jList1.getSelectedValue();
            if (selObject instanceof String) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObjectNode(new MetaObjectNode(baulastMap.get((String)selObject)), false);
            }
        }
    }                                                                      //GEN-LAST:event_jList1MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlVorschauValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlVorschauValueActionPerformed
        try {
            AlkisProductDownloadHelper.download((String)cidsBean.getProperty("schluessel"),
                downloadInfo.getProduktTyp(),
                (String)cidsBean.getProperty("downloadinfo_json"),
                getConnectionContext());
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }                                                                                   //GEN-LAST:event_hlVorschauValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList3MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList3MouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = jList3.getSelectedValue();
            if (selObject instanceof String) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObjectNode(new MetaObjectNode(alkisMap.get((String)selObject)), false);
            }
        }
    }                                                                      //GEN-LAST:event_jList3MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlEMailValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlEMailValueActionPerformed
        final String email = hlEMailValue.getText();
        if ((email != null) && !email.isEmpty()) {
            try {
                BrowserLauncher.openURL("mailto:" + email);
            } catch (final Exception ex) {
                LOG.warn(ex, ex);
            }
        }
    }                                                                                //GEN-LAST:event_hlEMailValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jList2MouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_jList2MouseClicked
        if (evt.getClickCount() > 1) {
            final Object selObject = jList2.getSelectedValue();
            if (selObject instanceof String) {
                ComponentRegistry.getRegistry()
                        .getDescriptionPane()
                        .gotoMetaObjectNode(new MetaObjectNode(alkisMap.get((String)selObject)), false);
            }
        }
    }                                                                      //GEN-LAST:event_jList2MouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFreigeben1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFreigeben1ActionPerformed
        final String kommentar = (String)cidsBean.getProperty("pruefkommentar");
        executeFreigabeOrStorno(true, kommentar);
    }                                                                                 //GEN-LAST:event_btnFreigeben1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBestellungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBestellungActionPerformed
        ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObjectNode(new MetaObjectNode(bestellungBeans[0]));
    }                                                                                //GEN-LAST:event_hlBestellungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBestellung1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBestellung1ActionPerformed
        ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObjectNode(new MetaObjectNode(bestellungBeans[1]));
    }                                                                                 //GEN-LAST:event_hlBestellung1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  freigabe   DOCUMENT ME!
     * @param  kommentar  DOCUMENT ME!
     */
    private void executeFreigabeOrStorno(final boolean freigabe, final String kommentar) {
        final String schluessel = (String)cidsBean.getProperty("schluessel");
        new SwingWorker<BerechtigungspruefungFreigabeServerAction.ReturnType, Void>() {

                @Override
                protected BerechtigungspruefungFreigabeServerAction.ReturnType doInBackground() throws Exception {
                    try {
                        return (BerechtigungspruefungFreigabeServerAction.ReturnType)SessionManager
                                    .getSession().getConnection()
                                    .executeTask(SessionManager.getSession().getUser(),
                                            BerechtigungspruefungFreigabeServerAction.TASK_NAME,
                                            SessionManager.getSession().getUser().getDomain(),
                                            schluessel,
                                            getConnectionContext(),
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.KOMMENTAR
                                                    .toString(),
                                                kommentar),
                                            new ServerActionParameter<String>(
                                                BerechtigungspruefungFreigabeServerAction.ParameterType.MODUS
                                                    .toString(),
                                                freigabe ? BerechtigungspruefungFreigabeServerAction.MODUS_FREIGABE
                                                         : BerechtigungspruefungFreigabeServerAction.MODUS_STORNO));
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        return null;
                    }
                }

                @Override
                protected void done() {
                    final BerechtigungspruefungFreigabeServerAction.ReturnType ret;
                    try {
                        ret = get();
                        BerechtigungspruefungMessageNotifier.getInstance().fireAnfrageRemoved(schluessel);
                        if (ret.equals(BerechtigungspruefungFreigabeServerAction.ReturnType.OK)) {
                            final String title = freigabe ? "Berechtigungs-Anfrage freigegeben."
                                                          : "Berechtigungs-Anfrage abgelehnt.";
                            final String message = freigabe ? "<html>Die Berechtigungs-Anfrage wurde freigegeben."
                                                            : "<html>Die Berechtigungs-Anfrage wurde abgelehnt.";
                            JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(
                                    BerechtigungspruefungRenderer.this),
                                message,
                                title,
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            final String title = freigabe ? "Fehler beim Freigeben." : "Fehler beim Ablehnen.";
                            switch (ret) {
                                case ALREADY: {
                                    final String message = freigabe
                                        ? "<html>Die Berechtigungs-Anfrage wurde bereits freigegeben."
                                        : "<html>Die Berechtigungs-Anfrage wurde bereits abgelehnt.";
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(
                                            BerechtigungspruefungRenderer.this),
                                        message,
                                        title,
                                        JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                                case PENDING: {
                                    final String message =
                                        "<html>Die Berechtigungs-Anfrage wird bereits von einem anderen Prüfer bearbeitet.";
                                    JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(
                                            BerechtigungspruefungRenderer.this),
                                        message,
                                        title,
                                        JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                                default: {
                                    break;
                                }
                            }
                        }
                    } catch (final Exception ex) {
                        final String title = freigabe ? "Fehler beim Freigeben." : "Fehler beim Ablehnen.";
                        final String message = freigabe ? "Beim Freigegen ist es zu unerwartetem einem Fehler gekommen."
                                                        : "Beim Ablehnen ist es zu unerwartetem einem Fehler gekommen.";
                        final ErrorInfo info = new ErrorInfo(
                                title,
                                message,
                                null,
                                null,
                                ex,
                                Level.SEVERE,
                                null);
                        JXErrorPane.showDialog(BerechtigungspruefungRenderer.this, info);

                        LOG.error("Fehler beim Freigeben", ex);
                    } finally {
                        btnStorno.setEnabled(false);
                        btnFreigeben.setEnabled(false);
                    }
                }
            }.execute();
    }

    /**
     * DOCUMENT ME!
     */
    private void executeFreigabe() {
        final String kommentar = jTextArea5.getText()
                    + (((jTextArea3.getText() != null) && !jTextArea3.getText().trim().isEmpty())
                        ? ("\n\n" + jTextArea3.getText()) : "");
        executeFreigabeOrStorno(true, kommentar);
    }

    /**
     * DOCUMENT ME!
     */
    private void executeStorno() {
        final String kommentar = jTextArea6.getText()
                    + (((jTextArea4.getText() != null) && !jTextArea4.getText().trim().isEmpty())
                        ? ("\n\n" + jTextArea4.getText()) : "");
        executeFreigabeOrStorno(false, kommentar);
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        baulastMap.clear();
        alkisMap.clear();
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        lblAnfrageVonValue.setText(((cidsBean != null) && (cidsBean.getProperty("benutzer") != null))
                ? (String)cidsBean.getProperty("benutzer") : "-");

        ((DefaultListModel<String>)jList1.getModel()).clear();
        if (cidsBean != null) {
            final boolean isBestellung = "formsolutions".equals(cidsBean.getProperty("benutzer"));

            hlDateianhangValue.setEnabled((cidsBean.getProperty("dateiname") != null)
                        && ((Timestamp)cidsBean.getProperty("anfrage_timestamp") != null)
                        && !((Timestamp)cidsBean.getProperty("anfrage_timestamp")).before(
                            BerechtigungspruefungHandler.getThresholdAnhangDate()));

            if (cidsBean.getProperty("pruefer") == null) {
                lblPruefStatusValue.setText("offen");
            } else if (cidsBean.getProperty("pruefstatus") == null) {
                lblPruefStatusValue.setText("in Bearbeitung");
            } else {
                lblPruefStatusValue.setText((Boolean)cidsBean.getProperty("pruefstatus") ? "freigegeben" : "abgelehnt");
            }

            final String pruefer = SessionManager.getSession().getUser().getName();
            btnFreigeben.setEnabled((cidsBean.getProperty("pruefer") == null)
                        || (pruefer.equals(cidsBean.getProperty("pruefer"))
                            && (cidsBean.getProperty("pruefstatus") == null)));
            btnFreigeben1.setVisible(Boolean.TRUE.equals(cidsBean.getProperty("pruefstatus"))
                        && Boolean.TRUE.equals(cidsBean.getProperty("abgeholt")));
            btnStorno.setEnabled((cidsBean.getProperty("pruefer") == null)
                        || (pruefer.equals(cidsBean.getProperty("pruefer"))
                            && (cidsBean.getProperty("pruefstatus") == null)));

            lblAnfrageVonValue.setText((String)cidsBean.getProperty("benutzer"));
            hlEMailValue.setText("");
            hlEMailValue.setText("");

            lblBestellung.setVisible(isBestellung);
            hlBestellung.setVisible(isBestellung);
            lblBestellung1.setVisible(isBestellung);
            hlBestellung1.setVisible(isBestellung);
            lblTelNummer.setVisible(!isBestellung);
            txtTelNummerValue.setVisible(!isBestellung);
            if (isBestellung) {
                new SwingWorker<CidsBean[], Void>() {

                        @Override
                        protected CidsBean[] doInBackground() throws Exception {
                            final FormSolutionsBestellungSearch search = new FormSolutionsBestellungSearch();
                            search.setBerechtigungspruefungSchluessel((String)cidsBean.getProperty("schluessel"));
                            final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                        .customServerSearch(SessionManager.getSession().getUser(),
                                            search,
                                            getConnectionContext());
                            final List<CidsBean> beans = new ArrayList<>();
                            if (!mons.isEmpty()) {
                                for (final MetaObjectNode mon : mons) {
                                    beans.add(SessionManager.getProxy().getMetaObject(
                                            mon.getObjectId(),
                                            mon.getClassId(),
                                            "WUNDA_BLAU",
                                            getConnectionContext()).getBean());
                                }
                                return beans.toArray(new CidsBean[0]);
                            } else {
                                return null;
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                bestellungBeans = get();
                                if ((bestellungBeans != null) && (bestellungBeans.length > 0)) {
                                    lblAnfrageVonValue.setText(
                                        (String)bestellungBeans[0].getProperty("fk_adresse_rechnung.vorname")
                                                + " "
                                                + (String)bestellungBeans[0].getProperty("fk_adresse_rechnung.name"));
                                    hlEMailValue.setText((String)bestellungBeans[0].getProperty("email"));
                                    hlBestellung.setText((String)bestellungBeans[0].getProperty("transid"));
                                    if (bestellungBeans.length > 1) {
                                        hlBestellung1.setText((String)bestellungBeans[1].getProperty("transid"));
                                    }
                                }
                            } catch (final Exception ex) {
                                LOG.warn(ex, ex);
                            }
                        }
                    }.execute();
            } else {
                new SwingWorker<CidsBean, Void>() {

                        @Override
                        protected CidsBean doInBackground() throws Exception {
                            return ClientBillingUtils.getInstance()
                                        .getExternalUser((String)cidsBean.getProperty("benutzer"), connectionContext);
                        }

                        @Override
                        protected void done() {
                            try {
                                final CidsBean externalUser = get();
                                if (externalUser != null) {
                                    lblAnfrageVonValue.setText((String)externalUser.getProperty("kunde.name") + " ("
                                                + (String)cidsBean.getProperty("benutzer") + ")");
                                    hlEMailValue.setText((String)externalUser.getProperty("kontakt"));
                                    txtTelNummerValue.setText((String)externalUser.getProperty("tel_nummer"));
                                }
                            } catch (final Exception ex) {
                                LOG.warn(ex, ex);
                            }
                        }
                    }.execute();
            }
            if (cidsBean.getProperty("downloadinfo_json") != null) {
                try {
                    downloadInfo = BerechtigungspruefungHandler.extractDownloadInfo((String)cidsBean.getProperty(
                                "downloadinfo_json"));

                    final String downloadTyp = downloadInfo.getProduktTyp();
                    jLabel8.setText(downloadTyp);
                    ((CardLayout)panDownloadTyp.getLayout()).show(panDownloadTyp, downloadTyp);
                    revalidate();
                    repaint();

                    if (downloadInfo instanceof BerechtigungspruefungBescheinigungDownloadInfo) {
                        final BerechtigungspruefungBescheinigungDownloadInfo bescheinigungDownloadInfo =
                            (BerechtigungspruefungBescheinigungDownloadInfo)downloadInfo;

                        final Collection<BerechtigungspruefungBescheinigungFlurstueckInfo> flurstueckInfos =
                            new ArrayList<>();
                        final Collection<BerechtigungspruefungBescheinigungBaulastInfo> baulastInfos =
                            new ArrayList<>();
                        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppeInfo
                                    : bescheinigungDownloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen()) {
                            flurstueckInfos.addAll(gruppeInfo.getFlurstuecke());
                            baulastInfos.addAll(gruppeInfo.getBaulastenBeguenstigt());
                            baulastInfos.addAll(gruppeInfo.getBaulastenBelastet());
                        }

                        for (final BerechtigungspruefungBescheinigungFlurstueckInfo flurstueckInfo : flurstueckInfos) {
                            new SwingWorker<CidsBean, Object>() {

                                    @Override
                                    protected CidsBean doInBackground() throws Exception {
                                        final CidsBean selBean = loadAlkisObject(flurstueckInfo.getAlkisId(),
                                                CidsAlkisSearchStatement.Resulttyp.FLURSTUECK);
                                        return selBean;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            alkisMap.put(flurstueckInfo.getAlkisId(), (CidsBean)get());
                                        } catch (final Exception ex) {
                                            LOG.warn(ex, ex);
                                        }
                                        jList3.revalidate();
                                        jList3.repaint();
                                    }
                                }.execute();
                            ((DefaultListModel<String>)jList3.getModel()).addElement(flurstueckInfo.getAlkisId());
                        }

                        for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo : baulastInfos) {
                            new SwingWorker<CidsBean, Object>() {

                                    @Override
                                    protected CidsBean doInBackground() throws Exception {
                                        final CidsBean selBean = CachedInfoBaulastRetriever.getInstance()
                                                    .loadBaulast(
                                                        baulastInfo,
                                                        getConnectionContext());
                                        return selBean;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            baulastMap.put(baulastInfo.toString(), (CidsBean)get());
                                        } catch (final Exception ex) {
                                            LOG.warn(ex, ex);
                                        }
                                        jList1.revalidate();
                                        jList1.repaint();
                                    }
                                }.execute();
                            ((DefaultListModel<String>)jList1.getModel()).addElement(baulastInfo.toString());
                        }
                    } else if (downloadInfo instanceof BerechtigungspruefungAlkisDownloadInfo) {
                        final BerechtigungspruefungAlkisDownloadInfo alkisDownloadInfo =
                            (BerechtigungspruefungAlkisDownloadInfo)downloadInfo;

                        if (null == alkisDownloadInfo.getAlkisObjectTyp()) {
                            lblBegruendungstext3.setText("Alkis-Codes:");
                        } else {
                            switch (alkisDownloadInfo.getAlkisObjectTyp()) {
                                case FLURSTUECKE: {
                                    lblBegruendungstext3.setText("Flurstücke:");
                                    break;
                                }
                                case BUCHUNGSBLAETTER: {
                                    lblBegruendungstext3.setText("Buchungsblätter:");
                                    break;
                                }
                            }
                        }

                        if (downloadInfo instanceof BerechtigungspruefungAlkisEinzelnachweisDownloadInfo) {
                            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo einzelnachweisInfo =
                                (BerechtigungspruefungAlkisEinzelnachweisDownloadInfo)downloadInfo;
                            final String alkisProdukt = einzelnachweisInfo.getAlkisProdukt();
                            jLabel9.setText(ClientAlkisProducts.getInstance().getProductName(alkisProdukt));
                        } else if (downloadInfo instanceof BerechtigungspruefungAlkisKarteDownloadInfo) {
                            jLabel9.setText("Karte");
                        }

                        final Collection<String> alkisCodes = alkisDownloadInfo.getAlkisCodes();
                        jList2.removeAll();
                        for (final String alkisCode : alkisCodes) {
                            new SwingWorker<CidsBean, Object>() {

                                    @Override
                                    protected CidsBean doInBackground() throws Exception {
                                        final CidsBean selBean = loadAlkisObject(
                                                alkisCode,
                                                BerechtigungspruefungAlkisDownloadInfo.AlkisObjektTyp.FLURSTUECKE
                                                        .equals(alkisDownloadInfo.getAlkisObjectTyp())
                                                    ? CidsAlkisSearchStatement.Resulttyp.FLURSTUECK
                                                    : CidsAlkisSearchStatement.Resulttyp.BUCHUNGSBLATT);
                                        return selBean;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            alkisMap.put(alkisCode, (CidsBean)get());
                                        } catch (final Exception ex) {
                                            LOG.warn(ex, ex);
                                        }
                                        jList2.revalidate();
                                        jList2.repaint();
                                    }
                                }.execute();
                            ((DefaultListModel)jList2.getModel()).addElement(alkisCode);
                        }
                    }
                } catch (final Exception ex) {
                    LOG.warn(ex, ex);
                    downloadInfo = null;
                }
            } else {
                downloadInfo = null;
            }

            final String notYetDownloadedKuText = "Vom Kunden noch nicht heruntergeladen";
            final String notYetDownloadedFSText = "Noch nicht zum Download ausgeliefert";
            final String pruefungTrueKuText = "Vom Kunden heruntergeladen";
            final String pruefungTrueFSText = "Zum Download ausgeliefert";

            final String notYetDownloadedText = isBestellung ? notYetDownloadedFSText : notYetDownloadedKuText;

            final String pruefungTrueText = isBestellung ? pruefungTrueFSText : pruefungTrueKuText;
            final String pruefungFalseText = "Ablehnungsmeldung erhalten";

            final String downloadedText = Boolean.TRUE.equals(cidsBean.getProperty("pruefstatus")) ? pruefungTrueText
                                                                                                   : pruefungFalseText;

            final String abholstatusText = Boolean.TRUE.equals(cidsBean.getProperty("abgeholt")) ? downloadedText
                                                                                                 : notYetDownloadedText;

            lblAbholStatusValue.setText(abholstatusText);
        } else {
            hlDateianhangValue.setEnabled(false);

            btnStorno.setEnabled(false);
            btnFreigeben.setEnabled(false);
            lblPruefStatusValue.setText("-");

            lblAnfrageVonValue.setText("-");
            hlEMailValue.setText("");
            txtTelNummerValue.setText("");

            downloadInfo = null;
            lblAbholStatusValue.setText(null);
        }

        bindingGroup.bind();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   alkisId  DOCUMENT ME!
     * @param   typ      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean loadAlkisObject(final String alkisId, final CidsAlkisSearchStatement.Resulttyp typ)
            throws Exception {
        final CidsAlkisSearchStatement search = new CidsAlkisSearchStatement(
                typ,
                CidsAlkisSearchStatement.Resulttyp.FLURSTUECK.equals(typ)
                    ? CidsAlkisSearchStatement.SucheUeber.FLURSTUECKSNUMMER
                    : CidsAlkisSearchStatement.SucheUeber.BUCHUNGSBLATTNUMMER,
                alkisId,
                null);

        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                    .customServerSearch(SessionManager.getSession().getUser(), search, getConnectionContext());
        if (!mons.isEmpty()) {
            final MetaObjectNode mon = mons.iterator().next();
            return SessionManager.getProxy()
                        .getMetaObject(mon.getObjectId(), mon.getClassId(), "WUNDA_BLAU", getConnectionContext())
                        .getBean();
        } else {
            return null;
        }
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

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
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
    class BescheinigungBaulastInfoListCellRenderer extends DefaultListCellRenderer {

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

            component.setEnabled(baulastMap.containsKey((String)value));
            return component;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class AlkisFlurstueckInfoListCellRenderer extends DefaultListCellRenderer {

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

            component.setEnabled(alkisMap.containsKey((String)value));
            return component;
        }
    }
}
