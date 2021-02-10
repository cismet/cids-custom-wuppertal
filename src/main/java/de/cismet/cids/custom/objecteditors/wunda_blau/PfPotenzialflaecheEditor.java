/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.KstSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheEditor extends javax.swing.JPanel implements CidsBeanRenderer,
    EditorSaveListener,
    FooterComponentProvider,
    TitleComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheEditor.class);
    private static CidsBean lastKampagne = null;

    //~ Instance fields --------------------------------------------------------

    private final PfPotenzialflaecheTitlePanel panTitle = new PfPotenzialflaecheTitlePanel(this);
    private final PfPotenzialflaecheFooterPanel panFooter = new PfPotenzialflaecheFooterPanel(this);
    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private Object currentTreeNode = null;

    private MetaClass mcBrachflaeche;
    private MetaClass mcEmpfohleneNutzung;
    private MetaClass mcEigentuemer;
    private MetaClass mcRestriktion;
    private MetaClass mcNutzung;
    private MetaClass mcOepnv;
    private MetaClass mcRegionalplan;

    private boolean initialized = false;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFlaeche;
    private javax.swing.JButton btnMassnahmen;
    private javax.swing.JButton btnMenAbort4;
    private javax.swing.JButton btnMenAbort5;
    private javax.swing.JButton btnMenOk4;
    private javax.swing.JButton btnMenOk5;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAeussereErschluessung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit;
    private javax.swing.JComboBox<String> cbGeom;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbHandlungsdruck;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbLagetyp;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbOepnv;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbTopografie;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbTopografie1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVerfuegbarkeit;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboBebauungsplan;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboFlaechennutzung;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField1;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField2;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField3;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField4;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField5;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField6;
    private de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField defaultBindableCheckboxField7;
    private javax.swing.JDialog dlgFlaeche;
    private javax.swing.JDialog dlgMassnahme;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler14;
    private javax.swing.Box.Filler filler15;
    private javax.swing.Box.Filler filler16;
    private javax.swing.Box.Filler filler17;
    private javax.swing.Box.Filler filler18;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblAessereErschl;
    private javax.swing.JLabel lblAktivierbarkeit;
    private javax.swing.JLabel lblArtDerNutzung;
    private javax.swing.JLabel lblBebauungplan;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBeschreibungTitle1;
    private javax.swing.JLabel lblBeschreibungTitle2;
    private javax.swing.JLabel lblBeschreibungTitle4;
    private javax.swing.JLabel lblBeschreibungTitle5;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblBisherigeNutzungTitle;
    private javax.swing.JLabel lblEntwicklungsausssichten;
    private javax.swing.JLabel lblFlaeche;
    private javax.swing.JLabel lblFlaechengroesse;
    private javax.swing.JLabel lblFlaechengroesseWert;
    private javax.swing.JLabel lblFlaechennutzung;
    private javax.swing.JLabel lblGeometrie5;
    private javax.swing.JLabel lblHandlungsdruck;
    private javax.swing.JLabel lblLageTitle;
    private javax.swing.JLabel lblLageTitle1;
    private javax.swing.JLabel lblLageTitle2;
    private javax.swing.JLabel lblLageTitle4;
    private javax.swing.JLabel lblLageTitle5;
    private javax.swing.JLabel lblLagetyp;
    private javax.swing.JLabel lblMessstellenausbauTitle;
    private javax.swing.JLabel lblNaechsteSchritte;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblNutzungsaufgabe;
    private javax.swing.JLabel lblOepnv;
    private javax.swing.JLabel lblQuartiere;
    private javax.swing.JLabel lblQuartiereWert;
    private javax.swing.JLabel lblQuelle;
    private javax.swing.JLabel lblRegionalplaeneTitle;
    private javax.swing.JLabel lblRestriktionenTitle;
    private javax.swing.JLabel lblRevitalisierung;
    private javax.swing.JLabel lblStadtbezirk;
    private javax.swing.JLabel lblStadtbezirkWert;
    private javax.swing.JLabel lblStand;
    private javax.swing.JLabel lblTopografie;
    private javax.swing.JLabel lblTopografie1;
    private javax.swing.JLabel lblUmgebungsnutzungTitle;
    private javax.swing.JLabel lblVerfuegbarkeit;
    private javax.swing.JLabel lblVorhandeneBebauung;
    private javax.swing.JLabel lblWbpfNummer;
    private javax.swing.JLabel lblWbpfNummer1;
    private javax.swing.JLabel lblWbpfNummer2;
    private javax.swing.JLabel lblZentrennaehe;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private de.cismet.tools.gui.RoundedPanel panAllgemein1;
    private de.cismet.tools.gui.RoundedPanel panAllgemein2;
    private de.cismet.tools.gui.RoundedPanel panAllgemein4;
    private de.cismet.tools.gui.RoundedPanel panAllgemein5;
    private javax.swing.JPanel panArtControls3;
    private javax.swing.JPanel panArtControls4;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle1;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle4;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle5;
    private de.cismet.tools.gui.RoundedPanel panBewertung;
    private de.cismet.tools.gui.RoundedPanel panBisherigeNutzung;
    private javax.swing.JPanel panBisherigeNutzungBody;
    private de.cismet.tools.gui.SemiRoundedPanel panBisherigeNutzungTitle;
    private de.cismet.tools.gui.RoundedPanel panBrachflaechen;
    private javax.swing.JPanel panDetail;
    private de.cismet.tools.gui.RoundedPanel panEigentuemer;
    private javax.swing.JPanel panEigentuemerBody;
    private de.cismet.tools.gui.SemiRoundedPanel panEigentuemerTitle;
    private javax.swing.JPanel panFlaeche;
    private javax.swing.JPanel panFlaeche1;
    private javax.swing.JPanel panLageBody;
    private javax.swing.JPanel panLageBody1;
    private javax.swing.JPanel panLageBody2;
    private javax.swing.JPanel panLageBody4;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle1;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle4;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMenButtons4;
    private javax.swing.JPanel panMenButtons5;
    private javax.swing.JPanel panMessstellenausbauBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessstellenausbauTitle;
    private de.cismet.tools.gui.RoundedPanel panNachfolgenutzung;
    private de.cismet.tools.gui.RoundedPanel panPlanungsrecht;
    private de.cismet.tools.gui.RoundedPanel panRegionalplaene;
    private javax.swing.JPanel panRegionalplaeneBody;
    private de.cismet.tools.gui.SemiRoundedPanel panRegionalplaeneTitle;
    private de.cismet.tools.gui.RoundedPanel panRestriktionen;
    private javax.swing.JPanel panRestriktionenBody;
    private de.cismet.tools.gui.SemiRoundedPanel panRestriktionenTitle;
    private de.cismet.tools.gui.RoundedPanel panStandortdaten;
    private de.cismet.tools.gui.RoundedPanel panUmgebungsnutzung;
    private javax.swing.JPanel panUmgebungsnutzungBody;
    private de.cismet.tools.gui.SemiRoundedPanel panUmgebungsnutzungTitle;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextArea taFlaeche;
    private javax.swing.JTextArea taFlaecheDialog;
    private javax.swing.JTextArea taMassnahmeDialog;
    private javax.swing.JTextArea taNotwendigeMassnahme;
    private javax.swing.JTextField txtArtDerNutzung;
    private javax.swing.JTextField txtBPlanName;
    private javax.swing.JTextField txtBPlanNummer;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtEntwicklungsausssichten;
    private javax.swing.JTextField txtJahrNutzungsaufgabe;
    private javax.swing.JTextField txtJahrNutzungsaufgabe1;
    private javax.swing.JTextField txtNummer;
    private javax.swing.JTextField txtQuelle;
    private javax.swing.JTextField txtRevitalisierung;
    private javax.swing.JTextField txtZentrennaehe;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form GrundwassermessstelleEditor.
     */
    public PfPotenzialflaecheEditor() {
        this(true);
    }

    /**
     * Creates a new GrundwassermessstelleEditor object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public PfPotenzialflaecheEditor(final boolean editable) {
        this.editable = editable;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the lastKampagne
     */
    public static CidsBean getLastKampagne() {
        return lastKampagne;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isEditable() {
        return editable;
    }

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
        DevelopmentTools.createEditorFromRestfulConnection(
            DevelopmentTools.RESTFUL_CALLSERVER_CALLSERVER,
            "WUNDA_BLAU",
            null,
            true,
            "pf_potenzialflaeche",
            10,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        panTitle.init();
        panFooter.init();

        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    mcBrachflaeche = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_brachflaeche",
                            getConnectionContext());
                    mcEmpfohleneNutzung = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_empfohle_nutzung",
                            getConnectionContext());
                    mcEigentuemer = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_eigentuemer",
                            getConnectionContext());
                    mcRestriktion = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_restriktionen",
                            getConnectionContext());
                    mcNutzung = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_nutzung",
                            getConnectionContext());
                    mcOepnv = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_oepnv",
                            getConnectionContext());
                    mcRegionalplan = ClassCacheMultiple.getMetaClass(
                            CidsBeanSupport.DOMAIN_NAME,
                            "pf_regionalplan",
                            getConnectionContext());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        initComponents();

                        panArtControls3.setVisible(editable);
                        panArtControls4.setVisible(editable);

                        cbGeom.setVisible(editable);
                        lblGeometrie5.setVisible(editable);

                        dlgFlaeche.addComponentListener(new ComponentAdapter() {

                                @Override
                                public void componentResized(final ComponentEvent e) {
                                    dlgFlaeche.doLayout();
                                    taFlaecheDialog.setSize(dlgFlaeche.getWidth() - 5, taFlaecheDialog.getHeight());
                                }
                            });

                        dlgMassnahme.addComponentListener(new ComponentAdapter() {

                                @Override
                                public void componentResized(final ComponentEvent e) {
                                    dlgMassnahme.doLayout();
                                    taMassnahmeDialog.setSize(
                                        dlgMassnahme.getWidth()
                                                - 5,
                                        taMassnahmeDialog.getHeight());
                                }
                            });

                        if (editable) {
                            // new MCSwingWorker(lstAlleRegionalplan, mcRegionalplan).execute();
                            // new MCSwingWorker(lstAlleBisherigeNutzung, mcNutzung).execute();
                            // new MCSwingWorker(lstAlleUmgebungsnutzungenNutzung, mcNutzung).execute();
                            // new MCSwingWorker(lstAlleOepnv, mcOepnv).execute();
                        } else {
                            RendererTools.makeReadOnly(bindingGroup, "cidsBean");
                        }
                    } finally {
                        initialized = true;
                    }
                }
            }.execute();
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

        dlgFlaeche = new javax.swing.JDialog();
        panFlaeche = new javax.swing.JPanel();
        panMenButtons4 = new javax.swing.JPanel();
        btnMenAbort4 = new javax.swing.JButton();
        btnMenOk4 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        taFlaecheDialog = new javax.swing.JTextArea();
        dlgMassnahme = new javax.swing.JDialog();
        panFlaeche1 = new javax.swing.JPanel();
        panMenButtons5 = new javax.swing.JPanel();
        btnMenAbort5 = new javax.swing.JButton();
        btnMenOk5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        taMassnahmeDialog = new javax.swing.JTextArea();
        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panMain = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        txtBezeichnung = new javax.swing.JTextField();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        lblFlaeche = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        taFlaeche = new javax.swing.JTextArea();
        panArtControls3 = new javax.swing.JPanel();
        btnFlaeche = new javax.swing.JButton();
        lblNaechsteSchritte = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        taNotwendigeMassnahme = new javax.swing.JTextArea();
        panArtControls4 = new javax.swing.JPanel();
        btnMassnahmen = new javax.swing.JButton();
        lblAessereErschl = new javax.swing.JLabel();
        cbAeussereErschluessung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel20 = new javax.swing.JLabel();
        lblOepnv = new javax.swing.JLabel();
        cbOepnv = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel23 = new javax.swing.JLabel();
        lblQuelle = new javax.swing.JLabel();
        txtQuelle = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        lblStand = new javax.swing.JLabel();
        dateStand = new de.cismet.cids.editors.DefaultBindableDateChooser();
        jLabel17 = new javax.swing.JLabel();
        panAllgemein1 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle1 = new javax.swing.JLabel();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        jPanel8 = new javax.swing.JPanel();
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = !editable ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        lblFlaechengroesse = new javax.swing.JLabel();
        lblFlaechengroesseWert = new javax.swing.JLabel();
        lblStadtbezirk = new javax.swing.JLabel();
        lblStadtbezirkWert = new javax.swing.JLabel();
        lblQuartiere = new javax.swing.JLabel();
        lblQuartiereWert = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        panStandortdaten = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle2 = new javax.swing.JLabel();
        panLageBody2 = new javax.swing.JPanel();
        cbLagetyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel3 = new javax.swing.JLabel();
        lblLagetyp = new javax.swing.JLabel();
        lblVorhandeneBebauung = new javax.swing.JLabel();
        cbVorhandeneBebauung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel4 = new javax.swing.JLabel();
        lblTopografie = new javax.swing.JLabel();
        cbTopografie = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel6 = new javax.swing.JLabel();
        lblTopografie1 = new javax.swing.JLabel();
        cbTopografie1 = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel7 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblZentrennaehe = new javax.swing.JLabel();
        txtZentrennaehe = new javax.swing.JTextField();
        filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        panPlanungsrecht = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        lblFlaechennutzung = new javax.swing.JLabel();
        cboFlaechennutzung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel9 = new javax.swing.JLabel();
        lblBebauungplan = new javax.swing.JLabel();
        cboBebauungsplan = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jLabel10 = new javax.swing.JLabel();
        lblWbpfNummer1 = new javax.swing.JLabel();
        txtBPlanNummer = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        lblWbpfNummer2 = new javax.swing.JLabel();
        txtBPlanName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblWbpfNummer = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        panBewertung = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle4 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle4 = new javax.swing.JLabel();
        panLageBody4 = new javax.swing.JPanel();
        lblVerfuegbarkeit = new javax.swing.JLabel();
        cbVerfuegbarkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblArtDerNutzung = new javax.swing.JLabel();
        txtArtDerNutzung = new javax.swing.JTextField();
        lblRevitalisierung = new javax.swing.JLabel();
        txtRevitalisierung = new javax.swing.JTextField();
        lblEntwicklungsausssichten = new javax.swing.JLabel();
        txtEntwicklungsausssichten = new javax.swing.JTextField();
        lblHandlungsdruck = new javax.swing.JLabel();
        cbHandlungsdruck = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblAktivierbarkeit = new javax.swing.JLabel();
        cbAktivierbarkeit = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jPanel5 = new javax.swing.JPanel();
        panAllgemein2 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle2 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        panAllgemein4 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle4 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle4 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        panAllgemein5 = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle5 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle5 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        panDetail = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        panBisherigeNutzung = new de.cismet.tools.gui.RoundedPanel();
        panBisherigeNutzungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBisherigeNutzungTitle = new javax.swing.JLabel();
        panBisherigeNutzungBody = new javax.swing.JPanel();
        defaultBindableCheckboxField5 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        panUmgebungsnutzung = new de.cismet.tools.gui.RoundedPanel();
        panUmgebungsnutzungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblUmgebungsnutzungTitle = new javax.swing.JLabel();
        panUmgebungsnutzungBody = new javax.swing.JPanel();
        defaultBindableCheckboxField6 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        panNachfolgenutzung = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle1 = new javax.swing.JLabel();
        panLageBody1 = new javax.swing.JPanel();
        defaultBindableCheckboxField2 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        jPanel4 = new javax.swing.JPanel();
        panEigentuemer = new de.cismet.tools.gui.RoundedPanel();
        panEigentuemerTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle5 = new javax.swing.JLabel();
        panEigentuemerBody = new javax.swing.JPanel();
        defaultBindableCheckboxField3 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        panRestriktionen = new de.cismet.tools.gui.RoundedPanel();
        panRestriktionenTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblRestriktionenTitle = new javax.swing.JLabel();
        panRestriktionenBody = new javax.swing.JPanel();
        defaultBindableCheckboxField4 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        panRegionalplaene = new de.cismet.tools.gui.RoundedPanel();
        panRegionalplaeneTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblRegionalplaeneTitle = new javax.swing.JLabel();
        panRegionalplaeneBody = new javax.swing.JPanel();
        defaultBindableCheckboxField7 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        panBrachflaechen = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle = new javax.swing.JLabel();
        panLageBody = new javax.swing.JPanel();
        defaultBindableCheckboxField1 = new de.cismet.cids.custom.commons.gui.DefaultBindableLabelsField(isEditable());
        lblNutzungsaufgabe = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe = new javax.swing.JTextField();
        filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0));

        dlgFlaeche.setTitle(org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.dlgFlaeche.title", new Object[] {})); // NOI18N
        dlgFlaeche.setModal(true);
        dlgFlaeche.setSize(new java.awt.Dimension(600, 400));

        panFlaeche.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche.setLayout(new java.awt.GridBagLayout());

        panMenButtons4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMenAbort4, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnMenAbort4.text", new Object[] {})); // NOI18N
        btnMenAbort4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenAbort4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons4.add(btnMenAbort4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnMenOk4, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnMenOk4.text", new Object[] {})); // NOI18N
        btnMenOk4.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenOk4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons4.add(btnMenOk4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche.add(panMenButtons4, gridBagConstraints);

        taFlaecheDialog.setColumns(20);
        taFlaecheDialog.setLineWrap(true);
        taFlaecheDialog.setRows(5);
        taFlaecheDialog.setWrapStyleWord(true);
        jScrollPane8.setViewportView(taFlaecheDialog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche.add(jScrollPane8, gridBagConstraints);

        dlgFlaeche.getContentPane().add(panFlaeche, java.awt.BorderLayout.CENTER);

        dlgMassnahme.setTitle(org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.dlgMassnahme.title", new Object[] {})); // NOI18N
        dlgMassnahme.setModal(true);
        dlgMassnahme.setSize(new java.awt.Dimension(600, 400));

        panFlaeche1.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setLayout(new java.awt.GridBagLayout());

        panMenButtons5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMenAbort5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnMenAbort5.text", new Object[] {})); // NOI18N
        btnMenAbort5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenAbort5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons5.add(btnMenAbort5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(btnMenOk5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnMenOk5.text", new Object[] {})); // NOI18N
        btnMenOk5.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenOk5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons5.add(btnMenOk5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche1.add(panMenButtons5, gridBagConstraints);

        taMassnahmeDialog.setColumns(20);
        taMassnahmeDialog.setLineWrap(true);
        taMassnahmeDialog.setRows(5);
        taMassnahmeDialog.setWrapStyleWord(true);
        jScrollPane2.setViewportView(taMassnahmeDialog);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panFlaeche1.add(jScrollPane2, gridBagConstraints);

        dlgMassnahme.getContentPane().add(panFlaeche1, java.awt.BorderLayout.CENTER);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        panAllgemein.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setFont(lblBeschreibungTitle.getFont());
        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBeschreibungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle.add(lblBeschreibungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein.add(panBeschreibungTitle, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        lblBezeichnung.setFont(lblBezeichnung.getFont().deriveFont(lblBezeichnung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblBezeichnung, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBezeichnung, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"), txtBezeichnung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBezeichnung.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBezeichnungFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 3.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtBezeichnung, gridBagConstraints);

        lblNummer.setFont(lblNummer.getFont().deriveFont(lblNummer.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblNummer, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 5, 5);
        jPanel3.add(lblNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"), txtNummer, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel3.add(txtNummer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(jPanel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel18, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel18.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel18, gridBagConstraints);

        lblFlaeche.setFont(lblFlaeche.getFont().deriveFont(lblFlaeche.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblFlaeche, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblFlaeche.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblFlaeche, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel21, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel21.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel21, gridBagConstraints);

        jScrollPane15.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane15.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(200, 80));

        taFlaeche.setColumns(20);
        taFlaeche.setLineWrap(true);
        taFlaeche.setRows(1);
        taFlaeche.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_flaeche}"), taFlaeche, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane15.setViewportView(taFlaeche);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane15, gridBagConstraints);

        panArtControls3.setOpaque(false);
        panArtControls3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnFlaeche, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnFlaeche.text", new Object[] {})); // NOI18N
        btnFlaeche.setMaximumSize(new java.awt.Dimension(43, 25));
        btnFlaeche.setMinimumSize(new java.awt.Dimension(43, 25));
        btnFlaeche.setPreferredSize(new java.awt.Dimension(43, 25));
        btnFlaeche.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFlaecheActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls3.add(btnFlaeche, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls3, gridBagConstraints);

        lblNaechsteSchritte.setFont(lblNaechsteSchritte.getFont().deriveFont(lblNaechsteSchritte.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblNaechsteSchritte, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblNaechsteSchritte.text")); // NOI18N
        lblNaechsteSchritte.setToolTipText(org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblNaechsteSchritte.toolTipText", new Object[] {})); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblNaechsteSchritte, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel22, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel22.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel22, gridBagConstraints);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(200, 80));

        taNotwendigeMassnahme.setColumns(20);
        taNotwendigeMassnahme.setLineWrap(true);
        taNotwendigeMassnahme.setRows(1);
        taNotwendigeMassnahme.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.notwendige_massnahmen}"), taNotwendigeMassnahme, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(taNotwendigeMassnahme);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane7, gridBagConstraints);

        panArtControls4.setOpaque(false);
        panArtControls4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(btnMassnahmen, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.btnMassnahmen.text", new Object[] {})); // NOI18N
        btnMassnahmen.setMaximumSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.setMinimumSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.setPreferredSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMassnahmenActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls4.add(btnMassnahmen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls4, gridBagConstraints);

        lblAessereErschl.setFont(lblAessereErschl.getFont().deriveFont(lblAessereErschl.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblAessereErschl, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblAessereErschl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        jPanel7.add(lblAessereErschl, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_aeussere_erschliessung}"), cbAeussereErschluessung, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbAeussereErschluessung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel20, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel20.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel20, gridBagConstraints);

        lblOepnv.setFont(lblOepnv.getFont().deriveFont(lblOepnv.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblOepnv, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblOepnv.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        jPanel7.add(lblOepnv, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_oepnv}"), cbOepnv, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbOepnv, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel23, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel23.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel23, gridBagConstraints);

        lblQuelle.setFont(lblQuelle.getFont().deriveFont(lblQuelle.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblQuelle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblQuelle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuelle, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.quelle}"), txtQuelle, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtQuelle, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel16, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel16.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel16, gridBagConstraints);

        lblStand.setFont(lblStand.getFont().deriveFont(lblStand.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblStand, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblStand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStand, gridBagConstraints);

        dateStand.setToolTipText(org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.dateStand.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand}"), dateStand, org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(dateStand, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel17, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel17.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel7.add(jLabel17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panAllgemein, gridBagConstraints);

        panAllgemein1.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle1.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle1.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle1.setFont(lblBeschreibungTitle1.getFont());
        lblBeschreibungTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle1, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBeschreibungTitle1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle1.add(lblBeschreibungTitle1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein1.add(panBeschreibungTitle1, gridBagConstraints);

        mappingComponent1.setMinimumSize(new java.awt.Dimension(400, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panAllgemein1.add(mappingComponent1, gridBagConstraints);

        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        lblGeometrie5.setFont(lblGeometrie5.getFont().deriveFont(lblGeometrie5.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblGeometrie5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblGeometrie5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel8.add(lblGeometrie5, gridBagConstraints);

        if (editable) {

            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"), cbGeom, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

            cbGeom.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    cbGeomFocusLost(evt);
                }
            });
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel8.add(cbGeom, gridBagConstraints);

        lblFlaechengroesse.setFont(lblFlaechengroesse.getFont().deriveFont(lblFlaechengroesse.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechengroesse, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblFlaechengroesse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel8.add(lblFlaechengroesse, gridBagConstraints);

        lblFlaechengroesseWert.setFont(lblFlaechengroesseWert.getFont().deriveFont(lblFlaechengroesseWert.getFont().getStyle() & ~java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechengroesseWert, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblFlaechengroesseWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel8.add(lblFlaechengroesseWert, gridBagConstraints);

        lblStadtbezirk.setFont(lblStadtbezirk.getFont().deriveFont(lblStadtbezirk.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirk, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblStadtbezirk.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel8.add(lblStadtbezirk, gridBagConstraints);

        lblStadtbezirkWert.setFont(lblStadtbezirkWert.getFont().deriveFont(lblStadtbezirkWert.getFont().getStyle() & ~java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblStadtbezirkWert, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblStadtbezirkWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel8.add(lblStadtbezirkWert, gridBagConstraints);

        lblQuartiere.setFont(lblQuartiere.getFont().deriveFont(lblQuartiere.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblQuartiere, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblQuartiere.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel8.add(lblQuartiere, gridBagConstraints);

        lblQuartiereWert.setFont(lblQuartiereWert.getFont().deriveFont(lblQuartiereWert.getFont().getStyle() & ~java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblQuartiereWert, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblQuartiereWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel8.add(lblQuartiereWert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panAllgemein1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(panAllgemein1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panMain.add(jPanel1, gridBagConstraints);

        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        panStandortdaten.setLayout(new java.awt.GridBagLayout());

        panLageTitle2.setBackground(java.awt.Color.darkGray);
        panLageTitle2.setLayout(new java.awt.GridBagLayout());

        lblLageTitle2.setFont(lblLageTitle2.getFont());
        lblLageTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle2, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLageTitle2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle2.add(lblLageTitle2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panStandortdaten.add(panLageTitle2, gridBagConstraints);

        panLageBody2.setOpaque(false);
        panLageBody2.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagetyp}"), cbLagetyp, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbLagetyp, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(jLabel3, gridBagConstraints);

        lblLagetyp.setFont(lblLagetyp.getFont().deriveFont(lblLagetyp.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblLagetyp, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLagetyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp, gridBagConstraints);

        lblVorhandeneBebauung.setFont(lblVorhandeneBebauung.getFont().deriveFont(lblVorhandeneBebauung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblVorhandeneBebauung, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblVorhandeneBebauung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblVorhandeneBebauung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vorhandene_bebauung}"), cbVorhandeneBebauung, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbVorhandeneBebauung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(jLabel4, gridBagConstraints);

        lblTopografie.setFont(lblTopografie.getFont().deriveFont(lblTopografie.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblTopografie, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblTopografie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblTopografie, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.topografie}"), cbTopografie, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbTopografie, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel6.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(jLabel6, gridBagConstraints);

        lblTopografie1.setFont(lblTopografie1.getFont().deriveFont(lblTopografie1.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblTopografie1, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblTopografie1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblTopografie1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_ausrichtung}"), cbTopografie1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbTopografie1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(jLabel5, gridBagConstraints);

        lblZentrennaehe.setFont(lblZentrennaehe.getFont().deriveFont(lblZentrennaehe.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblZentrennaehe, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblZentrennaehe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody2.add(lblZentrennaehe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zentrennaehe}"), txtZentrennaehe, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(txtZentrennaehe, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody2.add(filler17, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panStandortdaten.add(panLageBody2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(panStandortdaten, gridBagConstraints);

        panPlanungsrecht.setLayout(new java.awt.GridBagLayout());

        panMessstellenausbauTitle.setBackground(java.awt.Color.darkGray);
        panMessstellenausbauTitle.setLayout(new java.awt.GridBagLayout());

        lblMessstellenausbauTitle.setFont(lblMessstellenausbauTitle.getFont());
        lblMessstellenausbauTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblMessstellenausbauTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblMessstellenausbauTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMessstellenausbauTitle.add(lblMessstellenausbauTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panPlanungsrecht.add(panMessstellenausbauTitle, gridBagConstraints);

        panMessstellenausbauBody.setOpaque(false);
        panMessstellenausbauBody.setLayout(new java.awt.GridBagLayout());

        lblFlaechennutzung.setFont(lblFlaechennutzung.getFont().deriveFont(lblFlaechennutzung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblFlaechennutzung, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblFlaechennutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaechennutzungsplan}"), cboFlaechennutzung, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(cboFlaechennutzung, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel9.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(jLabel9, gridBagConstraints);

        lblBebauungplan.setFont(lblBebauungplan.getFont().deriveFont(lblBebauungplan.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblBebauungplan, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBebauungplan.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblBebauungplan, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bebauungsplan}"), cboBebauungsplan, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(cboBebauungsplan, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel10.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(jLabel10, gridBagConstraints);

        lblWbpfNummer1.setFont(lblWbpfNummer1.getFont().deriveFont(lblWbpfNummer1.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblWbpfNummer1, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblWbpfNummer1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bplan_nummer}"), txtBPlanNummer, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtBPlanNummer, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel11.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(jLabel11, gridBagConstraints);

        lblWbpfNummer2.setFont(lblWbpfNummer2.getFont().deriveFont(lblWbpfNummer2.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblWbpfNummer2, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblWbpfNummer2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bplan_name}"), txtBPlanName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtBPlanName, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel12.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(jLabel12, gridBagConstraints);

        lblWbpfNummer.setFont(lblWbpfNummer.getFont().deriveFont(lblWbpfNummer.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblWbpfNummer, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblWbpfNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wbpf_nummer}"), txtJahrNutzungsaufgabe1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtJahrNutzungsaufgabe1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.jLabel13.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(jLabel13, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMessstellenausbauBody.add(filler16, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panPlanungsrecht.add(panMessstellenausbauBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(panPlanungsrecht, gridBagConstraints);

        panBewertung.setPreferredSize(new java.awt.Dimension(400, 235));
        panBewertung.setLayout(new java.awt.GridBagLayout());

        panLageTitle4.setBackground(java.awt.Color.darkGray);
        panLageTitle4.setLayout(new java.awt.GridBagLayout());

        lblLageTitle4.setFont(lblLageTitle4.getFont());
        lblLageTitle4.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle4, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLageTitle4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle4.add(lblLageTitle4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBewertung.add(panLageTitle4, gridBagConstraints);

        panLageBody4.setOpaque(false);
        panLageBody4.setLayout(new java.awt.GridBagLayout());

        lblVerfuegbarkeit.setFont(lblVerfuegbarkeit.getFont().deriveFont(lblVerfuegbarkeit.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblVerfuegbarkeit, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblVerfuegbarkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblVerfuegbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfuegbarkeit}"), cbVerfuegbarkeit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbVerfuegbarkeit, gridBagConstraints);

        lblArtDerNutzung.setFont(lblArtDerNutzung.getFont().deriveFont(lblArtDerNutzung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblArtDerNutzung, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblArtDerNutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblArtDerNutzung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_der_nutzung}"), txtArtDerNutzung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtArtDerNutzung, gridBagConstraints);

        lblRevitalisierung.setFont(lblRevitalisierung.getFont().deriveFont(lblRevitalisierung.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblRevitalisierung, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblRevitalisierung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblRevitalisierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.revitalisierung}"), txtRevitalisierung, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtRevitalisierung, gridBagConstraints);

        lblEntwicklungsausssichten.setFont(lblEntwicklungsausssichten.getFont().deriveFont(lblEntwicklungsausssichten.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblEntwicklungsausssichten, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblEntwicklungsausssichten.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblEntwicklungsausssichten, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwicklungsaussischten}"), txtEntwicklungsausssichten, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtEntwicklungsausssichten, gridBagConstraints);

        lblHandlungsdruck.setFont(lblHandlungsdruck.getFont().deriveFont(lblHandlungsdruck.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblHandlungsdruck, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblHandlungsdruck.text")); // NOI18N
        lblHandlungsdruck.setToolTipText(org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblHandlungsdruck.toolTipText", new Object[] {})); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblHandlungsdruck, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlungsdruck}"), cbHandlungsdruck, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbHandlungsdruck, gridBagConstraints);

        lblAktivierbarkeit.setFont(lblAktivierbarkeit.getFont().deriveFont(lblAktivierbarkeit.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblAktivierbarkeit, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblAktivierbarkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblAktivierbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aktivierbarkeit}"), cbAktivierbarkeit, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbAktivierbarkeit, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panLageBody4.add(filler18, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBewertung.add(panLageBody4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(panBewertung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMain.add(jPanel2, gridBagConstraints);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridBagLayout());

        panAllgemein2.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle2.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle2.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle2.setFont(lblBeschreibungTitle2.getFont());
        lblBeschreibungTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle2, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBeschreibungTitle2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle2.add(lblBeschreibungTitle2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein2.add(panBeschreibungTitle2, gridBagConstraints);

        jPanel10.setOpaque(false);
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel10.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein2.add(jPanel10, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(panAllgemein2, gridBagConstraints);

        panAllgemein4.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle4.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle4.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle4.setFont(lblBeschreibungTitle4.getFont());
        lblBeschreibungTitle4.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle4, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBeschreibungTitle4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle4.add(lblBeschreibungTitle4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein4.add(panBeschreibungTitle4, gridBagConstraints);

        jPanel12.setOpaque(false);
        jPanel12.setLayout(new java.awt.GridBagLayout());

        jList3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel12.add(jScrollPane4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein4.add(jPanel12, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(panAllgemein4, gridBagConstraints);

        panAllgemein5.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle5.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle5.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle5.setFont(lblBeschreibungTitle5.getFont());
        lblBeschreibungTitle5.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBeschreibungTitle5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBeschreibungTitle5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBeschreibungTitle5.add(lblBeschreibungTitle5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panAllgemein5.add(panBeschreibungTitle5, gridBagConstraints);

        jPanel13.setOpaque(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        jList4.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jScrollPane5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panAllgemein5.add(jPanel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(panAllgemein5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMain.add(jPanel5, gridBagConstraints);

        add(panMain, "grunddaten");

        panDetail.setOpaque(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        jPanel9.setOpaque(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        panBisherigeNutzung.setLayout(new java.awt.GridBagLayout());

        panBisherigeNutzungTitle.setBackground(java.awt.Color.darkGray);
        panBisherigeNutzungTitle.setLayout(new java.awt.GridBagLayout());

        lblBisherigeNutzungTitle.setFont(lblBisherigeNutzungTitle.getFont());
        lblBisherigeNutzungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblBisherigeNutzungTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblBisherigeNutzungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panBisherigeNutzungTitle.add(lblBisherigeNutzungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBisherigeNutzung.add(panBisherigeNutzungTitle, gridBagConstraints);

        panBisherigeNutzungBody.setOpaque(false);
        panBisherigeNutzungBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcNutzung);
        defaultBindableCheckboxField5.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bisherige_nutzung}"), defaultBindableCheckboxField5, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBisherigeNutzungBody.add(defaultBindableCheckboxField5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBisherigeNutzung.add(panBisherigeNutzungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(panBisherigeNutzung, gridBagConstraints);

        panUmgebungsnutzung.setLayout(new java.awt.GridBagLayout());

        panUmgebungsnutzungTitle.setBackground(java.awt.Color.darkGray);
        panUmgebungsnutzungTitle.setLayout(new java.awt.GridBagLayout());

        lblUmgebungsnutzungTitle.setFont(lblUmgebungsnutzungTitle.getFont());
        lblUmgebungsnutzungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblUmgebungsnutzungTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblUmgebungsnutzungTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panUmgebungsnutzungTitle.add(lblUmgebungsnutzungTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panUmgebungsnutzung.add(panUmgebungsnutzungTitle, gridBagConstraints);

        panUmgebungsnutzungBody.setOpaque(false);
        panUmgebungsnutzungBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcNutzung);
        defaultBindableCheckboxField6.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebungsnutzung}"), defaultBindableCheckboxField6, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panUmgebungsnutzungBody.add(defaultBindableCheckboxField6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panUmgebungsnutzung.add(panUmgebungsnutzungBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(panUmgebungsnutzung, gridBagConstraints);

        panNachfolgenutzung.setLayout(new java.awt.GridBagLayout());

        panLageTitle1.setBackground(java.awt.Color.darkGray);
        panLageTitle1.setLayout(new java.awt.GridBagLayout());

        lblLageTitle1.setFont(lblLageTitle1.getFont());
        lblLageTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle1, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLageTitle1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle1.add(lblLageTitle1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panNachfolgenutzung.add(panLageTitle1, gridBagConstraints);

        panLageBody1.setOpaque(false);
        panLageBody1.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField2.setMetaClass(mcEmpfohleneNutzung);
        defaultBindableCheckboxField2.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_empfohlene_nutzungen}"), defaultBindableCheckboxField2, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody1.add(defaultBindableCheckboxField2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panNachfolgenutzung.add(panLageBody1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel9.add(panNachfolgenutzung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel9.add(filler14, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDetail.add(jPanel9, gridBagConstraints);

        jPanel4.setOpaque(false);
        jPanel4.setLayout(new java.awt.GridBagLayout());

        panEigentuemer.setLayout(new java.awt.GridBagLayout());

        panEigentuemerTitle.setBackground(java.awt.Color.darkGray);
        panEigentuemerTitle.setLayout(new java.awt.GridBagLayout());

        lblLageTitle5.setFont(lblLageTitle5.getFont());
        lblLageTitle5.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle5, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLageTitle5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panEigentuemerTitle.add(lblLageTitle5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panEigentuemer.add(panEigentuemerTitle, gridBagConstraints);

        panEigentuemerBody.setOpaque(false);
        panEigentuemerBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcEigentuemer);
        defaultBindableCheckboxField3.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_eigentuemer}"), defaultBindableCheckboxField3, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panEigentuemerBody.add(defaultBindableCheckboxField3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panEigentuemer.add(panEigentuemerBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(panEigentuemer, gridBagConstraints);

        panRestriktionen.setLayout(new java.awt.GridBagLayout());

        panRestriktionenTitle.setBackground(java.awt.Color.darkGray);
        panRestriktionenTitle.setLayout(new java.awt.GridBagLayout());

        lblRestriktionenTitle.setFont(lblRestriktionenTitle.getFont());
        lblRestriktionenTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblRestriktionenTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblRestriktionenTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panRestriktionenTitle.add(lblRestriktionenTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panRestriktionen.add(panRestriktionenTitle, gridBagConstraints);

        panRestriktionenBody.setOpaque(false);
        panRestriktionenBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcRestriktion);
        defaultBindableCheckboxField4.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_restriktionen}"), defaultBindableCheckboxField4, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panRestriktionenBody.add(defaultBindableCheckboxField4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panRestriktionen.add(panRestriktionenBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(panRestriktionen, gridBagConstraints);

        panRegionalplaene.setLayout(new java.awt.GridBagLayout());

        panRegionalplaeneTitle.setBackground(java.awt.Color.darkGray);
        panRegionalplaeneTitle.setLayout(new java.awt.GridBagLayout());

        lblRegionalplaeneTitle.setFont(lblRegionalplaeneTitle.getFont());
        lblRegionalplaeneTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblRegionalplaeneTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblRegionalplaeneTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panRegionalplaeneTitle.add(lblRegionalplaeneTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panRegionalplaene.add(panRegionalplaeneTitle, gridBagConstraints);

        panRegionalplaeneBody.setOpaque(false);
        panRegionalplaeneBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcRegionalplan);
        defaultBindableCheckboxField7.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.regionalplan}"), defaultBindableCheckboxField7, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panRegionalplaeneBody.add(defaultBindableCheckboxField7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panRegionalplaene.add(panRegionalplaeneBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(panRegionalplaene, gridBagConstraints);

        panBrachflaechen.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        lblLageTitle.setFont(lblLageTitle.getFont());
        lblLageTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(lblLageTitle, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblLageTitle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle.add(lblLageTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBrachflaechen.add(panLageTitle, gridBagConstraints);

        panLageBody.setOpaque(false);
        panLageBody.setLayout(new java.awt.GridBagLayout());

        defaultBindableCheckboxField1.setMetaClass(mcBrachflaeche);
        defaultBindableCheckboxField1.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.arr_brachflaechen}"), defaultBindableCheckboxField1, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLageBody.add(defaultBindableCheckboxField1, gridBagConstraints);

        lblNutzungsaufgabe.setFont(lblNutzungsaufgabe.getFont().deriveFont(lblNutzungsaufgabe.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(lblNutzungsaufgabe, org.openide.util.NbBundle.getMessage(PfPotenzialflaecheEditor.class, "PfPotenzialflaecheEditor.lblNutzungsaufgabe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        panLageBody.add(lblNutzungsaufgabe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_jahr_nutzungsaufgabe}"), txtJahrNutzungsaufgabe, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(txtJahrNutzungsaufgabe, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBrachflaechen.add(panLageBody, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(panBrachflaechen, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(filler15, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panDetail.add(jPanel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panDetail.add(filler12, gridBagConstraints);

        add(panDetail, "details");

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbort4ActionPerformed
        dlgFlaeche.setVisible(false);
    }//GEN-LAST:event_btnMenAbort4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOk4ActionPerformed
        try {
            cidsBean.setProperty("beschreibung_flaeche", taFlaecheDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for beschreibung_flaeche", e);
        }
        dlgFlaeche.setVisible(false);
    }//GEN-LAST:event_btnMenOk4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort5ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenAbort5ActionPerformed
        dlgMassnahme.setVisible(false);
    }//GEN-LAST:event_btnMenAbort5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk5ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenOk5ActionPerformed
        try {
            cidsBean.setProperty("notwendige_massnahmen", taMassnahmeDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for notwendige_massnahmen", e);
        }
        dlgMassnahme.setVisible(false);
    }//GEN-LAST:event_btnMenOk5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMassnahmenActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMassnahmenActionPerformed
        taMassnahmeDialog.setText((String)cidsBean.getProperty("notwendige_massnahmen"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgMassnahme, true);
    }//GEN-LAST:event_btnMassnahmenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaecheActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFlaecheActionPerformed
        taFlaecheDialog.setText((String)cidsBean.getProperty("beschreibung_flaeche"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgFlaeche, true);
    }//GEN-LAST:event_btnFlaecheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbGeomFocusLost
        setGeometryArea();
    }//GEN-LAST:event_cbGeomFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnungFocusLost(final java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBezeichnungFocusLost
        panTitle.refreshTitle();
    }//GEN-LAST:event_txtBezeichnungFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbAeussereErschluessungActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAeussereErschluessungActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAeussereErschluessungActionPerformed

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kampagne  gup DOCUMENT ME!
     */
    public static void setLastKampagne(final CidsBean kampagne) {
        lastKampagne = kampagne;
    }

    /**
     * DOCUMENT ME!
     */
    private void cidsBeanChanged() {
        new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() throws Exception {
                    while (!initialized) {
                        Thread.sleep(100);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    bindingGroup.unbind();
                    if (cidsBean != null) {
                        initMap();
                        refreshGeomFeatures();

                        DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                            bindingGroup,
                            cidsBean,
                            getConnectionContext());
                        bindingGroup.bind();

                        setGeometryArea();

                        if ((getLastKampagne() != null) && editable) {
                            try {
                                if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                                    cidsBean.setProperty("kampagne", getLastKampagne());
                                    markUsedFields(
                                        PfPotenzialflaecheEditor.this,
                                        (CidsBean)getLastKampagne().getProperty("steckbrieftemplate"));
                                }
                            } catch (Exception ex) {
                                LOG.error("Cannot add kampagne", ex);
                                ObjectRendererUtils.showExceptionWindowToUser(
                                    "Kampagne konnte nicht hinzugefügt werden",
                                    ex,
                                    PfPotenzialflaecheEditor.this);
                            }
                        } else {
                            final CidsBean kampagne = (CidsBean)cidsBean.getProperty("kampagne");
                            if (kampagne != null) {
                                markUsedFields(
                                    PfPotenzialflaecheEditor.this,
                                    (CidsBean)kampagne.getProperty("steckbrieftemplate"));
                            }
                        }
                    }
                    if ((cidsBean == null) || (cidsBean.getProperty("geometrie") == null)) {
                        lblStadtbezirkWert.setText("");
                    } else {
                        new SwingWorker<Collection<CidsBean>, Void>() {

                                @Override
                                protected Collection<CidsBean> doInBackground() throws Exception {
                                    final Collection<CidsBean> kstBezirke = new ArrayList<>();
                                    final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                                .customServerSearch(new KstSearch(
                                                        KstSearch.SearchFor.BEZIRK,
                                                        (Geometry)cidsBean.getProperty("geometrie.geo_field")),
                                                    getConnectionContext());
                                    for (final MetaObjectNode mon : mons) {
                                        final MetaObject mo = SessionManager.getProxy()
                                                    .getMetaObject(mon.getObjectId(),
                                                        mon.getClassId(),
                                                        mon.getDomain(),
                                                        getConnectionContext());
                                        kstBezirke.add(mo.getBean());
                                    }
                                    return kstBezirke;
                                }

                                @Override
                                protected void done() {
                                    Collection<CidsBean> kstBezirke = null;
                                    try {
                                        kstBezirke = get();
                                    } catch (final Exception ex) {
                                        LOG.error(ex, ex);
                                    }
                                    final Collection<String> kstBezirkStrings = new ArrayList<>();
                                    if (kstBezirke != null) {
                                        for (final CidsBean kstBezirk : kstBezirke) {
                                            kstBezirkStrings.add((String)kstBezirk.getProperty("name"));
                                        }
                                    }
                                    lblStadtbezirkWert.setText(String.join(", ", kstBezirkStrings));
                                }
                            }.execute();
                    }
                }
            }.execute();
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
        panTitle.refreshTitle();
        cidsBeanChanged();
    }

    /**
     * DOCUMENT ME!
     */
    public void startDownload() {
        PfPotenzialflaecheReportGenerator.startDownload(getCidsBean(), getConnectionContext());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editor              DOCUMENT ME!
     * @param  steckbrieftemplate  DOCUMENT ME!
     */
    private void markUsedFields(final PfPotenzialflaecheEditor editor, final CidsBean steckbrieftemplate) {
        if (steckbrieftemplate != null) {
            final String fields = (String)steckbrieftemplate.getProperty("verwendete_flaechenattribute");
            final List<String> usedFields = new ArrayList<>();

            final StringTokenizer st = new StringTokenizer(fields, ",");

            while (st.hasMoreTokens()) {
                usedFields.add(st.nextToken());
            }

            Collections.sort(usedFields);

            for (final String key : PotenzialflaecheReportServerAction.REPORT_PROPERTY_MAP.keySet()) {
                final PotenzialflaecheReportServerAction.ReportProperty rp =
                    PotenzialflaecheReportServerAction.REPORT_PROPERTY_MAP.get(key);

                if (rp.getEditorLabelName() == null) {
                    continue;
                }

                try {
                    final Field labelField = PfPotenzialflaecheEditor.class.getDeclaredField(rp.getEditorLabelName());
                    final Object o = labelField.get(editor);

                    if (o instanceof JComponent) {
                        final JComponent label = (JComponent)o;
                        if (Collections.binarySearch(usedFields, rp.getDbName()) >= 0) {
                            label.setFont(label.getFont().deriveFont(Font.BOLD));
                        } else {
                            label.setFont(label.getFont().deriveFont(Font.PLAIN));
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Cannot find field " + rp.getEditorLabelName(), ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setGeometryArea() {
        final Object geo = cidsBean.getProperty("geometrie.geo_field");

        if (geo instanceof Geometry) {
            final double area = ((Geometry)geo).getArea();
            lblFlaechengroesseWert.setText(String.format("%d m², (%d ha)", Math.round(area), Math.round(area / 10000)));
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
        mappingComponent1.dispose();
    }

    @Override
    public String getTitle() {
        return panTitle.getTitle();
    }

    @Override
    public void setTitle(final String string) {
    }

    @Override
    public void editorClosed(final EditorClosedEvent ece) {
        try {
            if (ece.getStatus().equals(EditorSaveListener.EditorSaveStatus.SAVE_SUCCESS)) {
                if ((getLastKampagne() != null) && (cidsBean.getMetaObject().getStatus() == MetaObject.NEW)
                            && (currentTreeNode instanceof ObjectTreeNode)) {
                    final List<CidsBean> flaechen = CidsBeanSupport.getBeanCollectionFromProperty(
                            getLastKampagne(),
                            "zugeordnete_flaechen");
                    final MetaObject mo = ((ObjectTreeNode)currentTreeNode).getMetaObject();
                    if (!flaechen.contains(mo.getBean())) {
                        flaechen.add(mo.getBean());
                        try {
                            getLastKampagne().persist(getConnectionContext());
                        } catch (Exception ex) {
                            LOG.error("Cannot save kampagne object", ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Kampagne konnte nicht gespeichert werden",
                                ex,
                                this);
                        }
                    }
                }
            }
        } finally {
            setLastKampagne(null);
        }
    }

    @Override
    public boolean prepareForSave() {
        currentTreeNode = ComponentRegistry.getRegistry().getAttributeEditor().getTreeNode();
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

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        if (getCidsBean() != null) {
            final Geometry geom = (Geometry)getCidsBean().getProperty("geometrie.geo_field");
            try {
                final XBoundingBox box = new XBoundingBox(geom.getEnvelope().buffer(
                            ClientAlkisConf.getInstance().getGeoBuffer()
                                    * 2));

                final ActiveLayerModel mappingModel = new ActiveLayerModel();
                mappingModel.setSrs(ClientAlkisConf.getInstance().getSrsService());
                mappingModel.addHome(new XBoundingBox(
                        box.getX1(),
                        box.getY1(),
                        box.getX2(),
                        box.getY2(),
                        ClientAlkisConf.getInstance().getSrsService(),
                        true));
                final SimpleWMS swms = new SimpleWMS(new SimpleWmsGetMapUrl(
                            ClientAlkisConf.getInstance().getMapCallString()));
                swms.setName("Potenzialflächen");

                // add the raster layer to the model
                mappingModel.addLayer(swms);
                // set the model
                mappingComponent1.setMappingModel(mappingModel);
                // interaction mode
                mappingComponent1.gotoInitialBoundingBox();
                mappingComponent1.setInteractionMode(MappingComponent.ZOOM);
                // finally when all configurations are done ...
                mappingComponent1.unlock();
            } catch (final Exception ex) {
                LOG.warn("could not init Map !", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshGeomFeatures() {
        mappingComponent1.getFeatureCollection().removeAllFeatures();
        if (getCidsBean() != null) {
            final Geometry geom = (Geometry)getCidsBean().getProperty("geometrie.geo_field");
            if (geom != null) {
                final StyledFeature dsf = new DefaultStyledFeature();
                dsf.setGeometry(geom);
                mappingComponent1.getFeatureCollection().addFeature(dsf);
            }
        }
    }
//
//    //~ Inner Classes ----------------------------------------------------------
//
//    /**
//     * DOCUMENT ME!
//     *
//     * @version  $Revision$, $Date$
//     */
//    private class MCSwingWorker extends SwingWorker<DefaultComboBoxModel, Void> {
//
//        //~ Instance fields ----------------------------------------------------
//
//        private final JList list;
//        private final MetaClass mc;
//
//        //~ Constructors -------------------------------------------------------
//
//        /**
//         * Creates a new MCSwingWorker object.
//         *
//         * @param  list  DOCUMENT ME!
//         * @param  mc    DOCUMENT ME!
//         */
//        public MCSwingWorker(final JList list, final MetaClass mc) {
//            this.list = list;
//            this.mc = mc;
//        }
//
//        //~ Methods ------------------------------------------------------------
//
//        @Override
//        protected DefaultComboBoxModel doInBackground() throws Exception {
//            Thread.currentThread().setName("MCSwingWorker init()");
//
//            return DefaultBindableReferenceCombo.getModelByMetaClass(
//                    mc,
//                    false,
//                    false,
//                    getConnectionContext());
//        }
//
//        @Override
//        protected void done() {
//            try {
//                list.setModel(get());
//            } catch (final Exception ex) {
//                LOG.error("Error while initializing the model of a referenceCombo", ex); // NOI18N
//            }
//        }
//    }

    /**
     * DOCUMENT ME!
     */
    public void showDetails() {
        ((CardLayout)getLayout()).show(this, "details");
    }

    /**
     * DOCUMENT ME!
     */
    public void showGrunddaten() {
        ((CardLayout)getLayout()).show(this, "grunddaten");
    }
}
