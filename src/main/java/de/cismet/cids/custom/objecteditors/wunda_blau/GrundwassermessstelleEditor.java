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

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.jfree.chart.JFreeChart;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.reports.wunda_blau.GrundwassermessstellenReportBean;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.wunda_blau.search.actions.GrundwassermessstellenAusbauplanDownloadAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class GrundwassermessstelleEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            GrundwassermessstelleEditor.class);
    private static final int GEO_BUFFER = 25;

    //~ Instance fields --------------------------------------------------------

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    javax.swing.JButton btnReport;
    private javax.swing.ButtonGroup buttonGroup1;
    private de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor cbGeometrie;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboEigentuemer;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboProjekt;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboProjekt1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboProjekt2;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboProjekt3;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboProjekt4;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboStrasse;
    private javax.swing.JCheckBox chbUeberwachung;
    private javax.swing.JCheckBox chbUnterdruecken;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler10;
    private javax.swing.Box.Filler filler11;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler13;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler6;
    private javax.swing.Box.Filler filler7;
    private javax.swing.Box.Filler filler8;
    private javax.swing.Box.Filler filler9;
    private de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleMessungenTablePanel
        grundwassermessstelleTablePanel1;
    private javax.swing.JCheckBox jCheckBox1;
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
    private javax.swing.JTextArea jTextArea1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblBemerkung;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBrunnennummerAlt;
    private javax.swing.JLabel lblEigentuemer;
    private javax.swing.JLabel lblEingemessen;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblGeometrie;
    private javax.swing.JLabel lblHausnummer;
    private javax.swing.JLabel lblLageTitle;
    private javax.swing.JLabel lblMessstellenausbauTitle;
    private javax.swing.JLabel lblMessungenTitle;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblNummer1;
    private javax.swing.JLabel lblNummer2;
    private javax.swing.JLabel lblNummer3;
    private javax.swing.JLabel lblNummer4;
    private javax.swing.JLabel lblNummer5;
    private javax.swing.JLabel lblNummer6;
    private javax.swing.JLabel lblNummer7;
    private javax.swing.JLabel lblNummer8;
    private javax.swing.JLabel lblNummer9;
    private javax.swing.JLabel lblProjekt;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblUeberwachung;
    private javax.swing.JLabel lblUnterduecken;
    private de.cismet.tools.gui.RoundedPanel panBeschreibung;
    private javax.swing.JPanel panBeschreibungBody;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private de.cismet.tools.gui.RoundedPanel panLage;
    private javax.swing.JPanel panLageBody;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel panMap;
    private de.cismet.tools.gui.RoundedPanel panMessstellenausbau;
    private javax.swing.JPanel panMessstellenausbauBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessstellenausbauTitle;
    private de.cismet.tools.gui.RoundedPanel panMessungen;
    private javax.swing.JPanel panMessungenBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessungenTitle;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTop;
    private javax.swing.JTextArea txtBemerkung;
    private javax.swing.JTextField txtBrunnennummerAlt;
    private javax.swing.JTextField txtHausnummer;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNummer;
    private javax.swing.JTextField txtNummer1;
    private javax.swing.JTextField txtNummer2;
    private javax.swing.JTextField txtNummer3;
    private javax.swing.JTextField txtNummer4;
    private javax.swing.JTextField txtNummer5;
    private javax.swing.JLabel txtTitle;
    private javax.swing.JLabel txtTitle1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GrundwassermessstelleEditor.
     */
    public GrundwassermessstelleEditor() {
        this(true);
    }

    /**
     * Creates a new GrundwassermessstelleEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public GrundwassermessstelleEditor(final boolean editable) {
        this.editable = editable;
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
        final MappingComponent mc = new MappingComponent();
        CismapBroker.getInstance().setMappingComponent(mc);
        final long before = System.currentTimeMillis();
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "grundwassermessstelle",
            133,
            1920,
            1024);
        final long after = System.currentTimeMillis();

        System.out.println(after - before);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
        this.grundwassermessstelleTablePanel1.initWithConnectionContext(connectionContext);

        if (!editable) {
            lblGeometrie.setVisible(false);
            cbGeometrie.setVisible(false);
            RendererTools.makeReadOnly(bindingGroup, "cidsBean");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public void generateMesswerteReport(final CidsBean cidsBean,
            final Component parent,
            final ConnectionContext connectionContext) {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    return new JRBeanCollectionDataSource(Arrays.asList(createReportBean()));
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();

            DownloadManager.instance()
                    .add(new JasperReportDownload(
                            "/de/cismet/cids/custom/reports/wunda_blau/grundwassermessstelle_messwerte.jasper",
                            dataSourceGenerator,
                            jobname,
                            "Grundwassermessstelle - Messwerte",
                            "gwm_messwerte"));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private GrundwassermessstellenReportBean createReportBean() {
        final CidsBean kategorieBean = grundwassermessstelleTablePanel1.getKategorieBean();
        final JFreeChart chart = grundwassermessstelleTablePanel1.getChart();
        final List<CidsBean> messungBeans = grundwassermessstelleTablePanel1.getMessungBeans();
        final List<GrundwassermessstellenReportBean.LegendeBean> legendeLeft =
            grundwassermessstelleTablePanel1.getDiagrammPanel().getLegendLeftBeans();
        final List<GrundwassermessstellenReportBean.LegendeBean> legendeRight =
            grundwassermessstelleTablePanel1.getDiagrammPanel().getLegendRightBeans();
        final GrundwassermessstellenReportBean reportBean = new GrundwassermessstellenReportBean(
                getCidsBean(),
                kategorieBean,
                messungBeans,
                legendeLeft,
                legendeRight,
                chart,
                getConnectionContext());
        return reportBean;
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
        txtTitle = new javax.swing.JLabel();
        txtTitle1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panFooter = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        panTop = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        panBeschreibung = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        panBeschreibungBody = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblProjekt = new javax.swing.JLabel();
        cboProjekt = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblBemerkung = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtBemerkung = new javax.swing.JTextArea();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel7 = new javax.swing.JPanel();
        lblEigentuemer = new javax.swing.JLabel();
        cboEigentuemer = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblBrunnennummerAlt = new javax.swing.JLabel();
        txtBrunnennummerAlt = new javax.swing.JTextField();
        lblUeberwachung = new javax.swing.JLabel();
        chbUeberwachung = new javax.swing.JCheckBox();
        lblUnterduecken = new javax.swing.JLabel();
        chbUnterdruecken = new javax.swing.JCheckBox();
        lblName1 = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        panLage = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle = new javax.swing.JLabel();
        panLageBody = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        lblStrasse = new javax.swing.JLabel();
        cboStrasse = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblGeometrie = new javax.swing.JLabel();
        cbGeometrie = new de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel5 = new javax.swing.JPanel();
        lblHausnummer = new javax.swing.JLabel();
        txtHausnummer = new javax.swing.JTextField();
        lblEingemessen = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        panMap = new de.cismet.cids.custom.objectrenderer.utils.DefaultPreviewMapPanel();
        panMessstellenausbau = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        lblNummer1 = new javax.swing.JLabel();
        cboProjekt1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblNummer2 = new javax.swing.JLabel();
        txtNummer1 = new javax.swing.JTextField();
        lblNummer3 = new javax.swing.JLabel();
        txtNummer2 = new javax.swing.JTextField();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel8 = new javax.swing.JPanel();
        lblNummer4 = new javax.swing.JLabel();
        cboProjekt2 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblNummer5 = new javax.swing.JLabel();
        cboProjekt3 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblNummer6 = new javax.swing.JLabel();
        cboProjekt4 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        jPanel9 = new javax.swing.JPanel();
        lblNummer7 = new javax.swing.JLabel();
        txtNummer3 = new javax.swing.JTextField();
        lblNummer8 = new javax.swing.JLabel();
        txtNummer4 = new javax.swing.JTextField();
        lblNummer9 = new javax.swing.JLabel();
        txtNummer5 = new javax.swing.JTextField();
        filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        panMessungen = new de.cismet.tools.gui.RoundedPanel();
        panMessungenTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessungenTitle = new javax.swing.JLabel();
        btnReport = new javax.swing.JButton();
        panMessungenBody = new javax.swing.JPanel();
        grundwassermessstelleTablePanel1 =
            new de.cismet.cids.custom.objecteditors.wunda_blau.GrundwassermessstelleMessungenTablePanel(editable);
        panButtons = new javax.swing.JPanel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${title}"),
                txtTitle,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panTitle.add(txtTitle, gridBagConstraints);

        txtTitle1.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            txtTitle1,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.txtTitle1.text"));     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panTitle.add(txtTitle1, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panTitle.add(jPanel4, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panTop.setOpaque(false);
        panTop.setLayout(new java.awt.CardLayout());

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        panBeschreibung.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblBeschreibungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(lblBeschreibungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBeschreibung.add(panBeschreibungTitle, gridBagConstraints);

        panBeschreibungBody.setOpaque(false);
        panBeschreibungBody.setLayout(new java.awt.GridBagLayout());

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        lblProjekt.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblProjekt,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblProjekt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel2.add(lblProjekt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.projekt}"),
                cboProjekt,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel2.add(cboProjekt, gridBagConstraints);

        lblNummer.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel2.add(lblNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel2.add(txtNummer, gridBagConstraints);

        lblName.setFont(new java.awt.Font("Tahoma", 1, 11));         // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblName,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblName.text"));        // NOI18N
        lblName.setToolTipText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblName.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel2.add(lblName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.name}"),
                txtName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel2.add(txtName, gridBagConstraints);

        lblBemerkung.setFont(new java.awt.Font("Tahoma", 1, 11));         // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBemerkung,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblBemerkung.text"));        // NOI18N
        lblBemerkung.setToolTipText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblBemerkung.toolTipText")); // NOI18N
        lblBemerkung.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel2.add(lblBemerkung, gridBagConstraints);

        txtBemerkung.setColumns(20);
        txtBemerkung.setLineWrap(true);
        txtBemerkung.setRows(3);
        txtBemerkung.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bemerkung}"),
                txtBemerkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(txtBemerkung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel2.add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungBody.add(jPanel2, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        lblEigentuemer.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblEigentuemer,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblEigentuemer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel7.add(lblEigentuemer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eigentuemer}"),
                cboEigentuemer,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel7.add(cboEigentuemer, gridBagConstraints);

        lblBrunnennummerAlt.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBrunnennummerAlt,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblBrunnennummerAlt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel7.add(lblBrunnennummerAlt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.brunnennummer_alt}"),
                txtBrunnennummerAlt,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel7.add(txtBrunnennummerAlt, gridBagConstraints);

        lblUeberwachung.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUeberwachung,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblUeberwachung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel7.add(lblUeberwachung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chbUeberwachung,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.chbUeberwachung.text")); // NOI18N
        chbUeberwachung.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ueberwachung}"),
                chbUeberwachung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel7.add(chbUeberwachung, gridBagConstraints);

        lblUnterduecken.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUnterduecken,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblUnterduecken.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel7.add(lblUnterduecken, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            chbUnterdruecken,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.chbUnterdruecken.text")); // NOI18N
        chbUnterdruecken.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.unterdruecken}"),
                chbUnterdruecken,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel7.add(chbUnterdruecken, gridBagConstraints);

        lblName1.setFont(new java.awt.Font("Tahoma", 1, 11));         // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblName1,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblName1.text"));        // NOI18N
        lblName1.setToolTipText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblName1.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel7.add(lblName1, gridBagConstraints);

        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink1,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.jXHyperlink1.text"));                                                       // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ausbauplan != null}"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ausbauplan.name}"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("name"));
        binding.setSourceNullValue("nicht vorhanden");
        binding.setSourceUnreadableValue("nicht vorhanden");
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(jXHyperlink1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel7.add(filler5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungBody.add(jPanel7, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBeschreibungBody.add(filler8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panBeschreibungBody.add(filler9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBeschreibung.add(panBeschreibungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel10.add(panBeschreibung, gridBagConstraints);

        panLage.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        lblLageTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblLageTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle.add(lblLageTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panLage.add(panLageTitle, gridBagConstraints);

        panLageBody.setOpaque(false);
        panLageBody.setLayout(new java.awt.GridBagLayout());

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblStrasse.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrasse,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel3.add(lblStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.strasse}"),
                cboStrasse,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel3.add(cboStrasse, gridBagConstraints);

        lblGeometrie.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblGeometrie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel3.add(lblGeometrie, gridBagConstraints);

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeometrie,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeometrie).getConverter());
            bindingGroup.addBinding(binding);
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel3.add(cbGeometrie, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(filler2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageBody.add(jPanel3, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        lblHausnummer.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHausnummer,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblHausnummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel5.add(lblHausnummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hausnummer}"),
                txtHausnummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel5.add(txtHausnummer, gridBagConstraints);

        lblEingemessen.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblEingemessen,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblEingemessen.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel5.add(lblEingemessen, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jCheckBox1,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.jCheckBox1.text")); // NOI18N
        jCheckBox1.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eingemessen}"),
                jCheckBox1,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel5.add(jCheckBox1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(filler1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageBody.add(jPanel5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody.add(filler10, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLageBody.add(filler11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLage.add(panLageBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel10.add(panLage, gridBagConstraints);

        panMap.setName(""); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel10.add(panMap, gridBagConstraints);

        panMessstellenausbau.setLayout(new java.awt.GridBagLayout());

        panMessstellenausbauTitle.setBackground(java.awt.Color.darkGray);
        panMessstellenausbauTitle.setLayout(new java.awt.GridBagLayout());

        lblMessstellenausbauTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMessstellenausbauTitle,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblMessstellenausbauTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauTitle.add(lblMessstellenausbauTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessstellenausbau.add(panMessstellenausbauTitle, gridBagConstraints);

        panMessstellenausbauBody.setOpaque(false);
        panMessstellenausbauBody.setLayout(new java.awt.GridBagLayout());

        jPanel6.setOpaque(false);
        jPanel6.setLayout(new java.awt.GridBagLayout());

        lblNummer1.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer1,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel6.add(lblNummer1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezugshoehe}"),
                cboProjekt1,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel6.add(cboProjekt1, gridBagConstraints);

        lblNummer2.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer2,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel6.add(lblNummer2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe_messpunkt}"),
                txtNummer1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel6.add(txtNummer1, gridBagConstraints);

        lblNummer3.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer3,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel6.add(lblNummer3, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.hoehe_gelaende}"),
                txtNummer2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel6.add(txtNummer2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(filler4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauBody.add(jPanel6, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        lblNummer4.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer4,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel8.add(lblNummer4, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aufschlussart}"),
                cboProjekt2,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel8.add(cboProjekt2, gridBagConstraints);

        lblNummer5.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer5,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel8.add(lblNummer5, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.filter}"),
                cboProjekt3,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel8.add(cboProjekt3, gridBagConstraints);

        lblNummer6.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer6,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel8.add(lblNummer6, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gok_ausbau}"),
                cboProjekt4,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel8.add(cboProjekt4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel8.add(filler6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauBody.add(jPanel8, gridBagConstraints);

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        lblNummer7.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer7,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel9.add(lblNummer7, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.durchmesser}"),
                txtNummer3,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel9.add(txtNummer3, gridBagConstraints);

        lblNummer8.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer8,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel9.add(lblNummer8, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.endteufe}"),
                txtNummer4,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel9.add(txtNummer4, gridBagConstraints);

        lblNummer9.setFont(new java.awt.Font("Tahoma", 1, 11));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer9,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblNummer9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 5);
        jPanel9.add(lblNummer9, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.kf_wert}"),
                txtNummer5,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 0, 1, 0);
        jPanel9.add(txtNummer5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(filler7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauBody.add(jPanel9, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMessstellenausbauBody.add(filler12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMessstellenausbauBody.add(filler13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMessstellenausbauBody.add(filler14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMessstellenausbau.add(panMessstellenausbauBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel10.add(panMessstellenausbau, gridBagConstraints);

        panTop.add(jPanel10, "info");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(panTop, gridBagConstraints);

        panMessungen.setLayout(new java.awt.GridBagLayout());

        panMessungenTitle.setBackground(java.awt.Color.darkGray);
        panMessungenTitle.setLayout(new java.awt.GridBagLayout());

        lblMessungenTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMessungenTitle,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblMessungenTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessungenTitle.add(lblMessungenTitle, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/printer.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.btnReport.text"));                       // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.btnReport.toolTipText"));                // NOI18N
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
        panMessungenTitle.add(btnReport, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMessungen.add(panMessungenTitle, gridBagConstraints);

        panMessungenBody.setOpaque(false);
        panMessungenBody.setLayout(new java.awt.GridBagLayout());

        grundwassermessstelleTablePanel1.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean}"),
                grundwassermessstelleTablePanel1,
                org.jdesktop.beansbinding.BeanProperty.create("cidsBean"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMessungenBody.add(grundwassermessstelleTablePanel1, gridBagConstraints);

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBack,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblBack.text")); // NOI18N
        lblBack.setEnabled(false);
        lblBack.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblBackMouseClicked(evt);
                }
            });
        panFooterLeft.add(lblBack);

        btnBack.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left.png")));         // NOI18N
        btnBack.setBorder(null);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setEnabled(false);
        btnBack.setFocusPainted(false);
        btnBack.setMaximumSize(new java.awt.Dimension(30, 30));
        btnBack.setMinimumSize(new java.awt.Dimension(30, 30));
        btnBack.setPreferredSize(new java.awt.Dimension(30, 30));
        btnBack.setPressedIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-pressed.png"))); // NOI18N
        btnBack.setRolloverIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/arrow-left-sel.png")));     // NOI18N
        btnBack.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnBackActionPerformed(evt);
                }
            });
        panFooterLeft.add(btnBack);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtons.add(panFooterLeft, gridBagConstraints);

        panFooterRight.setMaximumSize(new java.awt.Dimension(164, 30));
        panFooterRight.setMinimumSize(new java.awt.Dimension(164, 30));
        panFooterRight.setOpaque(false);
        panFooterRight.setPreferredSize(new java.awt.Dimension(164, 30));
        panFooterRight.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

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

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14));  // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblForw,
            org.openide.util.NbBundle.getMessage(
                GrundwassermessstelleEditor.class,
                "GrundwassermessstelleEditor.lblForw.text")); // NOI18N
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panButtons.add(panFooterRight, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panMessungenBody.add(panButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMessungen.add(panMessungenBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(panMessungen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel1, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void lblBackMouseClicked(final java.awt.event.MouseEvent evt) { //GEN-FIRST:event_lblBackMouseClicked
        if (lblBack.isEnabled()) {
            btnBackActionPerformed(null);
        }
    }                                                                       //GEN-LAST:event_lblBackMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        grundwassermessstelleTablePanel1.setShowDiagramm(false);
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
        grundwassermessstelleTablePanel1.setShowDiagramm(true);
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
        if (lblForw.isEnabled()) {
            btnForwardActionPerformed(null);
        }
    }                                                                       //GEN-LAST:event_lblForwMouseClicked

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        final Integer messstelleId = cidsBean.getPrimaryKeyValue();
        final String title = "Ausbauplan - Grundwassermessstelle " + (String)cidsBean.getProperty("name");
        final String directory = DownloadManagerDialog.getInstance().getJobName();
        final String filename = (String)cidsBean.getProperty("ausbauplan.name");
        final String extension = ".pdf";
        final Download download = new ByteArrayActionDownload(
                GrundwassermessstellenAusbauplanDownloadAction.TASK_NAME,
                messstelleId,
                null,
                title,
                directory,
                filename.substring(0, filename.length() - 4),
                extension,
                getConnectionContext());
        DownloadManager.instance().add(download);
    }                                                                                //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        generateMesswerteReport(cidsBean, this, getConnectionContext());
    }                                                                             //GEN-LAST:event_btnReportActionPerformed

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
                cidsBean,
                getConnectionContext());
            panMap.initMap(cidsBean, "geometrie.geo_field", GEO_BUFFER);
            bindingGroup.bind();
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeometrie).dispose();
        }
    }

    @Override
    public String getTitle() {
        final List<String> strings = new ArrayList<>();
        if (cidsBean != null) {
            if (cidsBean.getProperty("projekt.name") != null) {
                strings.add((String)cidsBean.getProperty("projekt.name"));
            }
            if (cidsBean.getProperty("nummer") != null) {
                strings.add((String)cidsBean.getProperty("nummer"));
            }
            return String.join(" - ", strings);
        } else {
            return "";
        }
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
    }

    @Override
    public boolean prepareForSave() {
        return true;
    }

    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
