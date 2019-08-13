/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.utils.RendererTools;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.PotenzialflaechenProperties;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.DefaultCustomObjectEditor;
import de.cismet.cids.editors.EditorClosedEvent;
import de.cismet.cids.editors.EditorSaveListener;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.cids.geometryeditor.DefaultCismapGeometryComboBoxEditor;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

import static de.cismet.cids.editors.DefaultBindableReferenceCombo.getModelByMetaClass;

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

    private final MetaClass NUTZUNG_MC;
    private final MetaClass OEPNV_MC;
    private final MetaClass REGIONALPLAN_MC;

    private final boolean editable;
    private CidsBean cidsBean;
    private ConnectionContext connectionContext;
    private Object currentTreeNode = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArt2;
    private javax.swing.JButton btnAddBisherigeNutzung;
    private javax.swing.JButton btnAddRegionalplan;
    private javax.swing.JButton btnAddUmgebungsnutzung;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnFlaeche;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnMassnahmen;
    private javax.swing.JButton btnMenAbort1;
    private javax.swing.JButton btnMenAbort2;
    private javax.swing.JButton btnMenAbort3;
    private javax.swing.JButton btnMenAbort4;
    private javax.swing.JButton btnMenAbort5;
    private javax.swing.JButton btnMenAbort6;
    private javax.swing.JButton btnMenOk1;
    private javax.swing.JButton btnMenOk2;
    private javax.swing.JButton btnMenOk3;
    private javax.swing.JButton btnMenOk4;
    private javax.swing.JButton btnMenOk5;
    private javax.swing.JButton btnMenOk6;
    private javax.swing.JButton btnRemoveArt2;
    private javax.swing.JButton btnRemoveBisherigeNutzung;
    private javax.swing.JButton btnRemoveRegionalplan;
    private javax.swing.JButton btnRemoveUmgebungsnutzung;
    private javax.swing.JButton btnReport;
    private javax.swing.ButtonGroup buttonGroup1;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbAktivierbarkeit;
    private javax.swing.JCheckBox cbBfGewerbe;
    private javax.swing.JCheckBox cbBfMilitaer;
    private javax.swing.JCheckBox cbBfVerkehr;
    private javax.swing.JCheckBox cbEinzelhandel;
    private javax.swing.JCheckBox cbFbEinzelhandel;
    private javax.swing.JCheckBox cbFreiraum;
    private javax.swing.JCheckBox cbFreizeit;
    private javax.swing.JComboBox<String> cbGeom;
    private javax.swing.JCheckBox cbGewerbeDienstleistung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbHandlungsdruck;
    private javax.swing.JCheckBox cbInfrastrukturSozial;
    private javax.swing.JCheckBox cbInfrastrukturTechnisch;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbLagetyp;
    private javax.swing.JCheckBox cbNnGewerbeProdukt;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbRestriktionen;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbTopografie;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVerfuegbarkeit;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cbVorhandeneBebauung;
    private javax.swing.JCheckBox cbWohnen;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboBebauungsplan;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboFlaechennutzung;
    de.cismet.cids.editors.DefaultBindableReferenceCombo cboWbpfNn;
    private de.cismet.cids.editors.DefaultBindableDateChooser dateStand;
    private javax.swing.JDialog dlgAddBisherigeNutzung;
    private javax.swing.JDialog dlgAddOepnv;
    private javax.swing.JDialog dlgAddRegionalplan;
    private javax.swing.JDialog dlgAddUmgebungsnutzung;
    private javax.swing.JDialog dlgFlaeche;
    private javax.swing.JDialog dlgMassnahme;
    private javax.swing.Box.Filler filler12;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.Box.Filler filler5;
    private javax.swing.Box.Filler filler9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JLabel lblAessereErschl;
    private javax.swing.JLabel lblAktivierbarkeit;
    private javax.swing.JLabel lblArtDerNutzung;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblBebauungplan;
    private javax.swing.JLabel lblBeschreibungTitle;
    private javax.swing.JLabel lblBezeichnung;
    private javax.swing.JLabel lblBisherigeNutzung;
    private javax.swing.JLabel lblEntwicklungsausssichten;
    private javax.swing.JLabel lblFlaeche;
    private javax.swing.JLabel lblFlaechengroesse;
    private javax.swing.JLabel lblFlaechengroesseWert;
    private javax.swing.JLabel lblFlaechennutzung;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblGeometrie5;
    private javax.swing.JLabel lblGnnSonstiges;
    private javax.swing.JLabel lblHandlungsdruck;
    private javax.swing.JLabel lblInnereErschl;
    private javax.swing.JLabel lblLageTitle;
    private javax.swing.JLabel lblLageTitle1;
    private javax.swing.JLabel lblLageTitle2;
    private javax.swing.JLabel lblLageTitle3;
    private javax.swing.JLabel lblLageTitle4;
    private javax.swing.JLabel lblLagetyp;
    private javax.swing.JLabel lblMessstellenausbauTitle;
    private javax.swing.JLabel lblNaechsteSchritte;
    private javax.swing.JLabel lblNummer;
    private javax.swing.JLabel lblNutzungsaufgabe;
    private javax.swing.JLabel lblOepnv;
    private javax.swing.JLabel lblQuelle;
    private javax.swing.JLabel lblRegionalplan;
    private javax.swing.JLabel lblRestriktionen;
    private javax.swing.JLabel lblRevitalisierung;
    private javax.swing.JLabel lblStadtbezirk;
    private javax.swing.JLabel lblStadtbezirkWert;
    private javax.swing.JLabel lblStand;
    private javax.swing.JLabel lblSuchwortEingeben1;
    private javax.swing.JLabel lblSuchwortEingeben2;
    private javax.swing.JLabel lblSuchwortEingeben3;
    private javax.swing.JLabel lblSuchwortEingeben4;
    private javax.swing.JLabel lblTopografie;
    private javax.swing.JLabel lblUmgebungsnutzung;
    private javax.swing.JLabel lblVerfuegbarkeit;
    private javax.swing.JLabel lblVorhandeneBebauung;
    private javax.swing.JLabel lblWbpfNn;
    private javax.swing.JLabel lblWbpfNummer;
    private javax.swing.JLabel lblWbpfNummer1;
    private javax.swing.JLabel lblWbpfNummer2;
    private javax.swing.JLabel lblZentrennaehe;
    private javax.swing.JList<Object> lstAlleBisherigeNutzung;
    private javax.swing.JList<Object> lstAlleOepnv;
    private javax.swing.JList<Object> lstAlleRegionalplan;
    private javax.swing.JList<Object> lstAlleUmgebungsnutzungenNutzung;
    private javax.swing.JList<String> lstBisherigeNutzung;
    private javax.swing.JList<String> lstOepnv;
    private javax.swing.JList<String> lstRegionalplan;
    private javax.swing.JList<String> lstUmgebungsnutzung;
    private javax.swing.JPanel panAddBaulastArt;
    private javax.swing.JPanel panAddBaulastArt1;
    private javax.swing.JPanel panAddBaulastArt2;
    private javax.swing.JPanel panAddBaulastArt3;
    private de.cismet.tools.gui.RoundedPanel panAllgemein;
    private javax.swing.JPanel panArtControls;
    private javax.swing.JPanel panArtControls1;
    private javax.swing.JPanel panArtControls2;
    private javax.swing.JPanel panArtControls3;
    private javax.swing.JPanel panArtControls4;
    private javax.swing.JPanel panArtControls5;
    private de.cismet.tools.gui.SemiRoundedPanel panBeschreibungTitle;
    private de.cismet.tools.gui.RoundedPanel panBewertung;
    private de.cismet.tools.gui.RoundedPanel panBrachflaechen;
    private javax.swing.JPanel panDetail;
    private javax.swing.JPanel panFlaeche;
    private javax.swing.JPanel panFlaeche1;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private de.cismet.tools.gui.RoundedPanel panInfrastruktur;
    private javax.swing.JPanel panLageBody;
    private javax.swing.JPanel panLageBody1;
    private javax.swing.JPanel panLageBody2;
    private javax.swing.JPanel panLageBody3;
    private javax.swing.JPanel panLageBody4;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle1;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle2;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle3;
    private de.cismet.tools.gui.SemiRoundedPanel panLageTitle4;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMenButtons1;
    private javax.swing.JPanel panMenButtons2;
    private javax.swing.JPanel panMenButtons3;
    private javax.swing.JPanel panMenButtons4;
    private javax.swing.JPanel panMenButtons5;
    private javax.swing.JPanel panMenButtons6;
    private javax.swing.JPanel panMessstellenausbauBody;
    private de.cismet.tools.gui.SemiRoundedPanel panMessstellenausbauTitle;
    private de.cismet.tools.gui.RoundedPanel panNachfolgenutzung;
    private de.cismet.tools.gui.RoundedPanel panPlanungsrecht;
    private de.cismet.tools.gui.RoundedPanel panStandortdaten;
    private javax.swing.JPanel panTitle;
    private de.cismet.cids.editors.converters.SqlDateToUtilDateConverter sqlDateToUtilDateConverter;
    private javax.swing.JTextArea taFlaeche;
    private javax.swing.JTextArea taFlaecheDialog;
    private javax.swing.JTextArea taMassnahmeDialog;
    private javax.swing.JTextArea taNotwendigeMassnahme;
    private javax.swing.JTextField txtAeussereErschl;
    private javax.swing.JTextField txtArtDerNutzung;
    private javax.swing.JTextField txtBPlanName;
    private javax.swing.JTextField txtBPlanNummer;
    private javax.swing.JTextField txtBezeichnung;
    private javax.swing.JTextField txtEntwicklungsausssichten;
    private javax.swing.JTextField txtInnereErschl;
    private javax.swing.JTextField txtJahrNutzungsaufgabe;
    private javax.swing.JTextField txtJahrNutzungsaufgabe1;
    private javax.swing.JTextField txtNummer;
    private javax.swing.JTextField txtQuelle;
    private javax.swing.JTextField txtRevitalisierung;
    private javax.swing.JTextField txtSonstiges;
    private javax.swing.JLabel txtTitle;
    private javax.swing.JLabel txtTitle1;
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
        NUTZUNG_MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_nutzung",
                getConnectionContext()); // NOI18N
        OEPNV_MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_oepnv",
                getConnectionContext()); // NOI18N
        REGIONALPLAN_MC = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "pf_regionalplan",
                getConnectionContext()); // NOI18N

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
            1,
            800,
            600);
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        initComponents();

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
                    taMassnahmeDialog.setSize(dlgMassnahme.getWidth() - 5, taMassnahmeDialog.getHeight());
                }
            });

        if (!editable) {
            RendererTools.makeReadOnly(txtAeussereErschl);
            RendererTools.makeReadOnly(txtArtDerNutzung);
            RendererTools.makeReadOnly(txtBPlanName);
            RendererTools.makeReadOnly(txtBPlanNummer);
            RendererTools.makeReadOnly(txtBezeichnung);
            RendererTools.makeReadOnly(txtEntwicklungsausssichten);
            RendererTools.makeReadOnly(txtInnereErschl);
            RendererTools.makeReadOnly(txtJahrNutzungsaufgabe);
            RendererTools.makeReadOnly(txtJahrNutzungsaufgabe1);
            RendererTools.makeReadOnly(txtNummer);
            RendererTools.makeReadOnly(txtQuelle);
            RendererTools.makeReadOnly(txtRevitalisierung);
            RendererTools.makeReadOnly(txtSonstiges);
            RendererTools.makeReadOnly(dateStand);
            RendererTools.makeReadOnly(txtZentrennaehe);
            RendererTools.makeReadOnly(cbAktivierbarkeit);
            RendererTools.makeReadOnly(cbBfGewerbe);
            RendererTools.makeReadOnly(cbBfMilitaer);
            RendererTools.makeReadOnly(cbBfVerkehr);
            RendererTools.makeReadOnly(cbEinzelhandel);
            RendererTools.makeReadOnly(cbFbEinzelhandel);
            RendererTools.makeReadOnly(cbFreiraum);
            RendererTools.makeReadOnly(cbFreizeit);
            RendererTools.makeReadOnly(cbGewerbeDienstleistung);
            RendererTools.makeReadOnly(cbHandlungsdruck);
            RendererTools.makeReadOnly(cbInfrastrukturSozial);
            RendererTools.makeReadOnly(cbInfrastrukturTechnisch);
            RendererTools.makeReadOnly(cbLagetyp);
            RendererTools.makeReadOnly(cbNnGewerbeProdukt);
            RendererTools.makeReadOnly(cbRestriktionen);
            RendererTools.makeReadOnly(cbTopografie);
            RendererTools.makeReadOnly(cbVerfuegbarkeit);
            RendererTools.makeReadOnly(cbVorhandeneBebauung);
            RendererTools.makeReadOnly(cbWohnen);
            RendererTools.makeReadOnly(cboBebauungsplan);
            RendererTools.makeReadOnly(cboFlaechennutzung);
            RendererTools.makeReadOnly(cboWbpfNn);
            RendererTools.makeReadOnly(taFlaeche);
            RendererTools.makeReadOnly(taNotwendigeMassnahme);
            panArtControls.setVisible(false);
            panArtControls1.setVisible(false);
            panArtControls2.setVisible(false);
            panArtControls3.setVisible(false);
            panArtControls4.setVisible(false);
            panArtControls5.setVisible(false);
            cbGeom.setVisible(false);
            lblGeometrie5.setVisible(false);
        } else {
            final MCSwingWorker workerRegional = new MCSwingWorker(lstAlleRegionalplan, REGIONALPLAN_MC);
            workerRegional.execute();
            final MCSwingWorker workerBisherigeNutzung = new MCSwingWorker(lstAlleBisherigeNutzung, NUTZUNG_MC);
            workerBisherigeNutzung.execute();
            final MCSwingWorker workerUmgebungsnutzung = new MCSwingWorker(
                    lstAlleUmgebungsnutzungenNutzung,
                    NUTZUNG_MC);
            workerUmgebungsnutzung.execute();
            final MCSwingWorker workerUOepnv = new MCSwingWorker(
                    lstAlleOepnv,
                    OEPNV_MC);
            workerUOepnv.execute();
        }
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
        btnReport = new javax.swing.JButton();
        panFooter = new javax.swing.JPanel();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        buttonGroup1 = new javax.swing.ButtonGroup();
        dlgAddBisherigeNutzung = new javax.swing.JDialog();
        panAddBaulastArt = new javax.swing.JPanel();
        lblSuchwortEingeben1 = new javax.swing.JLabel();
        panMenButtons1 = new javax.swing.JPanel();
        btnMenAbort1 = new javax.swing.JButton();
        btnMenOk1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstAlleBisherigeNutzung = new javax.swing.JList<>();
        dlgAddUmgebungsnutzung = new javax.swing.JDialog();
        panAddBaulastArt1 = new javax.swing.JPanel();
        lblSuchwortEingeben2 = new javax.swing.JLabel();
        panMenButtons2 = new javax.swing.JPanel();
        btnMenAbort2 = new javax.swing.JButton();
        btnMenOk2 = new javax.swing.JButton();
        jScrollPane12 = new javax.swing.JScrollPane();
        lstAlleUmgebungsnutzungenNutzung = new javax.swing.JList<>();
        dlgAddOepnv = new javax.swing.JDialog();
        panAddBaulastArt2 = new javax.swing.JPanel();
        lblSuchwortEingeben3 = new javax.swing.JLabel();
        panMenButtons3 = new javax.swing.JPanel();
        btnMenAbort3 = new javax.swing.JButton();
        btnMenOk3 = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        lstAlleOepnv = new javax.swing.JList<>();
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
        dlgAddRegionalplan = new javax.swing.JDialog();
        panAddBaulastArt3 = new javax.swing.JPanel();
        lblSuchwortEingeben4 = new javax.swing.JLabel();
        panMenButtons6 = new javax.swing.JPanel();
        btnMenAbort6 = new javax.swing.JButton();
        btnMenOk6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstAlleRegionalplan = new javax.swing.JList<>();
        sqlDateToUtilDateConverter = new de.cismet.cids.editors.converters.SqlDateToUtilDateConverter();
        panMain = new javax.swing.JPanel();
        panAllgemein = new de.cismet.tools.gui.RoundedPanel();
        panBeschreibungTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblBeschreibungTitle = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblBezeichnung = new javax.swing.JLabel();
        lblFlaeche = new javax.swing.JLabel();
        txtBezeichnung = new javax.swing.JTextField();
        lblNummer = new javax.swing.JLabel();
        txtNummer = new javax.swing.JTextField();
        lblQuelle = new javax.swing.JLabel();
        lblStand = new javax.swing.JLabel();
        txtQuelle = new javax.swing.JTextField();
        lblGeometrie5 = new javax.swing.JLabel();
        cbGeom = (!editable) ? new JComboBox() : new DefaultCismapGeometryComboBoxEditor();
        lblNaechsteSchritte = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        taFlaeche = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        taNotwendigeMassnahme = new javax.swing.JTextArea();
        dateStand = new de.cismet.cids.editors.DefaultBindableDateChooser();
        lblFlaechengroesse = new javax.swing.JLabel();
        lblFlaechengroesseWert = new javax.swing.JLabel();
        lblStadtbezirk = new javax.swing.JLabel();
        lblStadtbezirkWert = new javax.swing.JLabel();
        filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panArtControls3 = new javax.swing.JPanel();
        btnFlaeche = new javax.swing.JButton();
        panArtControls4 = new javax.swing.JPanel();
        btnMassnahmen = new javax.swing.JButton();
        panStandortdaten = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle2 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle2 = new javax.swing.JLabel();
        panLageBody2 = new javax.swing.JPanel();
        cbLagetyp = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblLagetyp = new javax.swing.JLabel();
        lblBisherigeNutzung = new javax.swing.JLabel();
        lblVorhandeneBebauung = new javax.swing.JLabel();
        cbVorhandeneBebauung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblUmgebungsnutzung = new javax.swing.JLabel();
        lblTopografie = new javax.swing.JLabel();
        cbTopografie = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblRestriktionen = new javax.swing.JLabel();
        cbRestriktionen = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jScrollPane4 = new javax.swing.JScrollPane();
        lstBisherigeNutzung = new javax.swing.JList<>();
        jScrollPane5 = new javax.swing.JScrollPane();
        lstUmgebungsnutzung = new javax.swing.JList<>();
        panArtControls = new javax.swing.JPanel();
        btnAddBisherigeNutzung = new javax.swing.JButton();
        btnRemoveBisherigeNutzung = new javax.swing.JButton();
        panArtControls1 = new javax.swing.JPanel();
        btnAddUmgebungsnutzung = new javax.swing.JButton();
        btnRemoveUmgebungsnutzung = new javax.swing.JButton();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0),
                new java.awt.Dimension(200, 0));
        panPlanungsrecht = new de.cismet.tools.gui.RoundedPanel();
        panMessstellenausbauTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblMessstellenausbauTitle = new javax.swing.JLabel();
        panMessstellenausbauBody = new javax.swing.JPanel();
        lblRegionalplan = new javax.swing.JLabel();
        lblFlaechennutzung = new javax.swing.JLabel();
        cboFlaechennutzung = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblBebauungplan = new javax.swing.JLabel();
        cboBebauungsplan = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        lblWbpfNummer = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe1 = new javax.swing.JTextField();
        lblWbpfNummer1 = new javax.swing.JLabel();
        txtBPlanNummer = new javax.swing.JTextField();
        lblWbpfNummer2 = new javax.swing.JLabel();
        txtBPlanName = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        lstRegionalplan = new javax.swing.JList<>();
        panArtControls5 = new javax.swing.JPanel();
        btnAddRegionalplan = new javax.swing.JButton();
        btnRemoveRegionalplan = new javax.swing.JButton();
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(250, 0),
                new java.awt.Dimension(250, 0),
                new java.awt.Dimension(250, 0));
        filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));
        panDetail = new javax.swing.JPanel();
        panBrachflaechen = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle = new javax.swing.JLabel();
        panLageBody = new javax.swing.JPanel();
        cbBfGewerbe = new javax.swing.JCheckBox();
        cbBfMilitaer = new javax.swing.JCheckBox();
        cbInfrastrukturSozial = new javax.swing.JCheckBox();
        cbInfrastrukturTechnisch = new javax.swing.JCheckBox();
        cbBfVerkehr = new javax.swing.JCheckBox();
        cbFbEinzelhandel = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lblNutzungsaufgabe = new javax.swing.JLabel();
        txtJahrNutzungsaufgabe = new javax.swing.JTextField();
        panNachfolgenutzung = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle1 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle1 = new javax.swing.JLabel();
        panLageBody1 = new javax.swing.JPanel();
        cbNnGewerbeProdukt = new javax.swing.JCheckBox();
        cbGewerbeDienstleistung = new javax.swing.JCheckBox();
        cbWohnen = new javax.swing.JCheckBox();
        cbFreiraum = new javax.swing.JCheckBox();
        cbFreizeit = new javax.swing.JCheckBox();
        cbEinzelhandel = new javax.swing.JCheckBox();
        lblWbpfNn = new javax.swing.JLabel();
        cboWbpfNn = new de.cismet.cids.editors.DefaultBindableReferenceCombo();
        jPanel6 = new javax.swing.JPanel();
        lblGnnSonstiges = new javax.swing.JLabel();
        txtSonstiges = new javax.swing.JTextField();
        panInfrastruktur = new de.cismet.tools.gui.RoundedPanel();
        panLageTitle3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLageTitle3 = new javax.swing.JLabel();
        panLageBody3 = new javax.swing.JPanel();
        lblAessereErschl = new javax.swing.JLabel();
        lblInnereErschl = new javax.swing.JLabel();
        lblOepnv = new javax.swing.JLabel();
        lblZentrennaehe = new javax.swing.JLabel();
        txtAeussereErschl = new javax.swing.JTextField();
        txtInnereErschl = new javax.swing.JTextField();
        txtZentrennaehe = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        lstOepnv = new javax.swing.JList<>();
        panArtControls2 = new javax.swing.JPanel();
        btnAddArt2 = new javax.swing.JButton();
        btnRemoveArt2 = new javax.swing.JButton();
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
        filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0));

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        txtTitle.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panTitle.add(txtTitle, gridBagConstraints);

        txtTitle1.setFont(new java.awt.Font("DejaVu Sans", 1, 18)); // NOI18N
        txtTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            txtTitle1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.txtTitle1.text"));        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panTitle.add(txtTitle1, gridBagConstraints);

        btnReport.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/einzelReport.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            btnReport,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnReport.text"));                               // NOI18N
        btnReport.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnReport.toolTipText"));                        // NOI18N
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        panTitle.add(btnReport, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.GridBagLayout());

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

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblForw,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblForw.text"));   // NOI18N
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFooter.add(panFooterRight, gridBagConstraints);

        panFooterLeft.setMaximumSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(164, 30));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBack,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBack.text"));   // NOI18N
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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panFooter.add(panFooterLeft, gridBagConstraints);

        dlgAddBisherigeNutzung.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgAddBisherigeNutzung.title",
                new Object[] {})); // NOI18N
        dlgAddBisherigeNutzung.setModal(true);
        dlgAddBisherigeNutzung.setSize(new java.awt.Dimension(350, 210));

        panAddBaulastArt.setMaximumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt.setMinimumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt.setPreferredSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt.setLayout(new java.awt.GridBagLayout());

        lblSuchwortEingeben1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSuchwortEingeben1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblSuchwortEingeben1.text",
                new Object[] {}));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt.add(lblSuchwortEingeben1, gridBagConstraints);

        panMenButtons1.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort1.text",
                new Object[] {})); // NOI18N
        btnMenAbort1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons1.add(btnMenAbort1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk1.text",
                new Object[] {})); // NOI18N
        btnMenOk1.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk1.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk1.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons1.add(btnMenOk1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt.add(panMenButtons1, gridBagConstraints);

        jScrollPane3.setViewportView(lstAlleBisherigeNutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt.add(jScrollPane3, gridBagConstraints);

        dlgAddBisherigeNutzung.getContentPane().add(panAddBaulastArt, java.awt.BorderLayout.CENTER);

        dlgAddUmgebungsnutzung.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgAddUmgebungsnutzung.title",
                new Object[] {})); // NOI18N
        dlgAddUmgebungsnutzung.setModal(true);
        dlgAddUmgebungsnutzung.setSize(new java.awt.Dimension(350, 210));

        panAddBaulastArt1.setMaximumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt1.setMinimumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt1.setPreferredSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt1.setLayout(new java.awt.GridBagLayout());

        lblSuchwortEingeben2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSuchwortEingeben2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblSuchwortEingeben2.text",
                new Object[] {}));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt1.add(lblSuchwortEingeben2, gridBagConstraints);

        panMenButtons2.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort2.text",
                new Object[] {})); // NOI18N
        btnMenAbort2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons2.add(btnMenAbort2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk2.text",
                new Object[] {})); // NOI18N
        btnMenOk2.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk2.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk2.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons2.add(btnMenOk2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt1.add(panMenButtons2, gridBagConstraints);

        jScrollPane12.setViewportView(lstAlleUmgebungsnutzungenNutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt1.add(jScrollPane12, gridBagConstraints);

        dlgAddUmgebungsnutzung.getContentPane().add(panAddBaulastArt1, java.awt.BorderLayout.CENTER);

        dlgAddOepnv.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgAddOepnv.title",
                new Object[] {})); // NOI18N
        dlgAddOepnv.setModal(true);
        dlgAddOepnv.setSize(new java.awt.Dimension(350, 210));

        panAddBaulastArt2.setMaximumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt2.setMinimumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt2.setPreferredSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt2.setLayout(new java.awt.GridBagLayout());

        lblSuchwortEingeben3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSuchwortEingeben3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblSuchwortEingeben3.text",
                new Object[] {}));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt2.add(lblSuchwortEingeben3, gridBagConstraints);

        panMenButtons3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort3.text",
                new Object[] {})); // NOI18N
        btnMenAbort3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons3.add(btnMenAbort3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk3.text",
                new Object[] {})); // NOI18N
        btnMenOk3.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk3.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk3.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk3.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk3ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons3.add(btnMenOk3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt2.add(panMenButtons3, gridBagConstraints);

        jScrollPane13.setViewportView(lstAlleOepnv);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt2.add(jScrollPane13, gridBagConstraints);

        dlgAddOepnv.getContentPane().add(panAddBaulastArt2, java.awt.BorderLayout.CENTER);

        dlgFlaeche.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgFlaeche.title",
                new Object[] {})); // NOI18N
        dlgFlaeche.setModal(true);
        dlgFlaeche.setSize(new java.awt.Dimension(600, 400));

        panFlaeche.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche.setLayout(new java.awt.GridBagLayout());

        panMenButtons4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort4.text",
                new Object[] {})); // NOI18N
        btnMenAbort4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort4ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons4.add(btnMenAbort4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk4.text",
                new Object[] {})); // NOI18N
        btnMenOk4.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk4.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk4.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
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

        dlgMassnahme.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgMassnahme.title",
                new Object[] {})); // NOI18N
        dlgMassnahme.setModal(true);
        dlgMassnahme.setSize(new java.awt.Dimension(600, 400));

        panFlaeche1.setMaximumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setMinimumSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setPreferredSize(new java.awt.Dimension(300, 120));
        panFlaeche1.setLayout(new java.awt.GridBagLayout());

        panMenButtons5.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort5.text",
                new Object[] {})); // NOI18N
        btnMenAbort5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort5ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons5.add(btnMenAbort5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk5.text",
                new Object[] {})); // NOI18N
        btnMenOk5.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk5.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk5.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
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

        dlgAddRegionalplan.setTitle(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dlgAddRegionalplan.title",
                new Object[] {})); // NOI18N
        dlgAddRegionalplan.setModal(true);
        dlgAddRegionalplan.setSize(new java.awt.Dimension(350, 210));

        panAddBaulastArt3.setMaximumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt3.setMinimumSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt3.setPreferredSize(new java.awt.Dimension(300, 120));
        panAddBaulastArt3.setLayout(new java.awt.GridBagLayout());

        lblSuchwortEingeben4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            lblSuchwortEingeben4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblSuchwortEingeben4.text",
                new Object[] {}));                                        // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt3.add(lblSuchwortEingeben4, gridBagConstraints);

        panMenButtons6.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenAbort6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenAbort6.text",
                new Object[] {})); // NOI18N
        btnMenAbort6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenAbort6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons6.add(btnMenAbort6, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMenOk6,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMenOk6.text",
                new Object[] {})); // NOI18N
        btnMenOk6.setMaximumSize(new java.awt.Dimension(85, 23));
        btnMenOk6.setMinimumSize(new java.awt.Dimension(85, 23));
        btnMenOk6.setPreferredSize(new java.awt.Dimension(85, 23));
        btnMenOk6.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnMenOk6ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMenButtons6.add(btnMenOk6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt3.add(panMenButtons6, gridBagConstraints);

        jScrollPane1.setViewportView(lstAlleRegionalplan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panAddBaulastArt3.add(jScrollPane1, gridBagConstraints);

        dlgAddRegionalplan.getContentPane().add(panAddBaulastArt3, java.awt.BorderLayout.CENTER);

        setOpaque(false);
        setLayout(new java.awt.CardLayout());

        panMain.setOpaque(false);
        panMain.setLayout(new java.awt.GridBagLayout());

        panAllgemein.setLayout(new java.awt.GridBagLayout());

        panBeschreibungTitle.setBackground(java.awt.Color.darkGray);
        panBeschreibungTitle.setLayout(new java.awt.GridBagLayout());

        lblBeschreibungTitle.setFont(lblBeschreibungTitle.getFont());
        lblBeschreibungTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBeschreibungTitle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBeschreibungTitle.text")); // NOI18N
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

        lblBezeichnung.setFont(lblBezeichnung.getFont().deriveFont(
                lblBezeichnung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBezeichnung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBezeichnung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblBezeichnung, gridBagConstraints);

        lblFlaeche.setFont(lblFlaeche.getFont().deriveFont(lblFlaeche.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaeche,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaeche.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblFlaeche, gridBagConstraints);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bezeichnung}"),
                txtBezeichnung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        txtBezeichnung.addFocusListener(new java.awt.event.FocusAdapter() {

                @Override
                public void focusLost(final java.awt.event.FocusEvent evt) {
                    txtBezeichnungFocusLost(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtBezeichnung, gridBagConstraints);

        lblNummer.setFont(lblNummer.getFont().deriveFont(lblNummer.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNummer,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.nummer}"),
                txtNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtNummer, gridBagConstraints);

        lblQuelle.setFont(lblQuelle.getFont().deriveFont(lblQuelle.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblQuelle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblQuelle.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblQuelle, gridBagConstraints);

        lblStand.setFont(lblStand.getFont().deriveFont(lblStand.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblStand,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStand, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.quelle}"),
                txtQuelle,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(txtQuelle, gridBagConstraints);

        lblGeometrie5.setFont(lblGeometrie5.getFont().deriveFont(
                lblGeometrie5.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGeometrie5,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblGeometrie5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblGeometrie5, gridBagConstraints);

        if (editable) {
            binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                    org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                    this,
                    org.jdesktop.beansbinding.ELProperty.create("${cidsBean.geometrie}"),
                    cbGeom,
                    org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
            binding.setConverter(((DefaultCismapGeometryComboBoxEditor)cbGeom).getConverter());
            bindingGroup.addBinding(binding);

            cbGeom.addFocusListener(new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusLost(final java.awt.event.FocusEvent evt) {
                        cbGeomFocusLost(evt);
                    }
                });
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(cbGeom, gridBagConstraints);

        lblNaechsteSchritte.setFont(lblNaechsteSchritte.getFont().deriveFont(
                lblNaechsteSchritte.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNaechsteSchritte,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNaechsteSchritte.text")); // NOI18N
        lblNaechsteSchritte.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNaechsteSchritte.toolTipText",
                new Object[] {}));                                     // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblNaechsteSchritte, gridBagConstraints);

        jScrollPane15.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane15.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane15.setPreferredSize(new java.awt.Dimension(200, 80));

        taFlaeche.setColumns(20);
        taFlaeche.setLineWrap(true);
        taFlaeche.setRows(1);
        taFlaeche.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beschreibung_flaeche}"),
                taFlaeche,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane15.setViewportView(taFlaeche);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane15, gridBagConstraints);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setMinimumSize(new java.awt.Dimension(200, 80));
        jScrollPane7.setPreferredSize(new java.awt.Dimension(200, 80));

        taNotwendigeMassnahme.setColumns(20);
        taNotwendigeMassnahme.setLineWrap(true);
        taNotwendigeMassnahme.setRows(1);
        taNotwendigeMassnahme.setWrapStyleWord(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.notwendige_massnahmen}"),
                taNotwendigeMassnahme,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane7.setViewportView(taNotwendigeMassnahme);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jScrollPane7, gridBagConstraints);

        dateStand.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.dateStand.toolTipText")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.stand}"),
                dateStand,
                org.jdesktop.beansbinding.BeanProperty.create("date"));
        binding.setConverter(sqlDateToUtilDateConverter);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(dateStand, gridBagConstraints);

        lblFlaechengroesse.setFont(lblFlaechengroesse.getFont().deriveFont(
                lblFlaechengroesse.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechengroesse,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechengroesse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblFlaechengroesse, gridBagConstraints);

        lblFlaechengroesseWert.setFont(lblFlaechengroesseWert.getFont().deriveFont(
                lblFlaechengroesseWert.getFont().getStyle()
                        & ~java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechengroesseWert,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechengroesseWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(lblFlaechengroesseWert, gridBagConstraints);

        lblStadtbezirk.setFont(lblStadtbezirk.getFont().deriveFont(
                lblStadtbezirk.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirk,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirk.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        jPanel7.add(lblStadtbezirk, gridBagConstraints);
        lblStadtbezirk.setVisible(false);

        lblStadtbezirkWert.setFont(lblStadtbezirkWert.getFont().deriveFont(
                lblStadtbezirkWert.getFont().getStyle()
                        & ~java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblStadtbezirkWert,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblStadtbezirkWert.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(lblStadtbezirkWert, gridBagConstraints);
        lblStadtbezirkWert.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel7.add(filler5, gridBagConstraints);

        panArtControls3.setOpaque(false);
        panArtControls3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnFlaeche,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnFlaeche.text",
                new Object[] {})); // NOI18N
        btnFlaeche.setMaximumSize(new java.awt.Dimension(43, 25));
        btnFlaeche.setMinimumSize(new java.awt.Dimension(43, 25));
        btnFlaeche.setPreferredSize(new java.awt.Dimension(43, 25));
        btnFlaeche.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls3, gridBagConstraints);

        panArtControls4.setOpaque(false);
        panArtControls4.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            btnMassnahmen,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.btnMassnahmen.text",
                new Object[] {})); // NOI18N
        btnMassnahmen.setMaximumSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.setMinimumSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.setPreferredSize(new java.awt.Dimension(43, 25));
        btnMassnahmen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel7.add(panArtControls4, gridBagConstraints);

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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(panAllgemein, gridBagConstraints);

        panStandortdaten.setMaximumSize(new java.awt.Dimension(440, 350));
        panStandortdaten.setMinimumSize(new java.awt.Dimension(440, 350));
        panStandortdaten.setPreferredSize(new java.awt.Dimension(440, 350));
        panStandortdaten.setLayout(new java.awt.GridBagLayout());

        panLageTitle2.setBackground(java.awt.Color.darkGray);
        panLageTitle2.setLayout(new java.awt.GridBagLayout());

        lblLageTitle2.setFont(lblLageTitle2.getFont());
        lblLageTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle2.text")); // NOI18N
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

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.lagetyp}"),
                cbLagetyp,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbLagetyp, gridBagConstraints);

        lblLagetyp.setFont(lblLagetyp.getFont().deriveFont(lblLagetyp.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLagetyp,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLagetyp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblLagetyp, gridBagConstraints);

        lblBisherigeNutzung.setFont(lblBisherigeNutzung.getFont().deriveFont(
                lblBisherigeNutzung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBisherigeNutzung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBisherigeNutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblBisherigeNutzung, gridBagConstraints);

        lblVorhandeneBebauung.setFont(lblVorhandeneBebauung.getFont().deriveFont(
                lblVorhandeneBebauung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVorhandeneBebauung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVorhandeneBebauung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblVorhandeneBebauung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.vorhandene_bebauung}"),
                cbVorhandeneBebauung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbVorhandeneBebauung, gridBagConstraints);

        lblUmgebungsnutzung.setFont(lblUmgebungsnutzung.getFont().deriveFont(
                lblUmgebungsnutzung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblUmgebungsnutzung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblUmgebungsnutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblUmgebungsnutzung, gridBagConstraints);

        lblTopografie.setFont(lblTopografie.getFont().deriveFont(
                lblTopografie.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTopografie,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblTopografie.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblTopografie, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.topografie}"),
                cbTopografie,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbTopografie, gridBagConstraints);

        lblRestriktionen.setFont(lblRestriktionen.getFont().deriveFont(
                lblRestriktionen.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblRestriktionen,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblRestriktionen.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody2.add(lblRestriktionen, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.restriktionen}"),
                cbRestriktionen,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(cbRestriktionen, gridBagConstraints);

        jScrollPane4.setMaximumSize(new java.awt.Dimension(100, 85));
        jScrollPane4.setMinimumSize(new java.awt.Dimension(100, 85));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(100, 85));

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${cidsBean.bisherige_nutzung}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJListBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                        this,
                        eLProperty,
                        lstBisherigeNutzung);
        bindingGroup.addBinding(jListBinding);

        jScrollPane4.setViewportView(lstBisherigeNutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(jScrollPane4, gridBagConstraints);

        jScrollPane5.setMaximumSize(new java.awt.Dimension(100, 85));
        jScrollPane5.setMinimumSize(new java.awt.Dimension(100, 85));
        jScrollPane5.setPreferredSize(new java.awt.Dimension(100, 85));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.umgebungsnutzung}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstUmgebungsnutzung);
        bindingGroup.addBinding(jListBinding);

        jScrollPane5.setViewportView(lstUmgebungsnutzung);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody2.add(jScrollPane5, gridBagConstraints);

        panArtControls.setOpaque(false);
        panArtControls.setLayout(new java.awt.GridBagLayout());

        btnAddBisherigeNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddBisherigeNutzung.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddBisherigeNutzung.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddBisherigeNutzung.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddBisherigeNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddBisherigeNutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls.add(btnAddBisherigeNutzung, gridBagConstraints);

        btnRemoveBisherigeNutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveBisherigeNutzung.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveBisherigeNutzung.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveBisherigeNutzung.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveBisherigeNutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveBisherigeNutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls.add(btnRemoveBisherigeNutzung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(panArtControls, gridBagConstraints);

        panArtControls1.setOpaque(false);
        panArtControls1.setLayout(new java.awt.GridBagLayout());

        btnAddUmgebungsnutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddUmgebungsnutzung.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddUmgebungsnutzung.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddUmgebungsnutzung.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddUmgebungsnutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddUmgebungsnutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls1.add(btnAddUmgebungsnutzung, gridBagConstraints);

        btnRemoveUmgebungsnutzung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveUmgebungsnutzung.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveUmgebungsnutzung.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveUmgebungsnutzung.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveUmgebungsnutzung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveUmgebungsnutzungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls1.add(btnRemoveUmgebungsnutzung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody2.add(panArtControls1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panLageBody2.add(filler3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 10);
        panStandortdaten.add(panLageBody2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(panStandortdaten, gridBagConstraints);

        panPlanungsrecht.setMaximumSize(new java.awt.Dimension(440, 350));
        panPlanungsrecht.setMinimumSize(new java.awt.Dimension(440, 350));
        panPlanungsrecht.setPreferredSize(new java.awt.Dimension(440, 350));
        panPlanungsrecht.setLayout(new java.awt.GridBagLayout());

        panMessstellenausbauTitle.setBackground(java.awt.Color.darkGray);
        panMessstellenausbauTitle.setLayout(new java.awt.GridBagLayout());

        lblMessstellenausbauTitle.setFont(lblMessstellenausbauTitle.getFont());
        lblMessstellenausbauTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblMessstellenausbauTitle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblMessstellenausbauTitle.text")); // NOI18N
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

        lblRegionalplan.setFont(lblRegionalplan.getFont().deriveFont(
                lblRegionalplan.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblRegionalplan,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblRegionalplan.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblRegionalplan, gridBagConstraints);

        lblFlaechennutzung.setFont(lblFlaechennutzung.getFont().deriveFont(
                lblFlaechennutzung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlaechennutzung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblFlaechennutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblFlaechennutzung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.flaechennutzungsplan}"),
                cboFlaechennutzung,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(cboFlaechennutzung, gridBagConstraints);

        lblBebauungplan.setFont(lblBebauungplan.getFont().deriveFont(
                lblBebauungplan.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblBebauungplan,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblBebauungplan.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblBebauungplan, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bebauungsplan}"),
                cboBebauungsplan,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(cboBebauungsplan, gridBagConstraints);

        lblWbpfNummer.setFont(lblWbpfNummer.getFont().deriveFont(
                lblWbpfNummer.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblWbpfNummer,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblWbpfNummer.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wbpf_nummer}"),
                txtJahrNutzungsaufgabe1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtJahrNutzungsaufgabe1, gridBagConstraints);

        lblWbpfNummer1.setFont(lblWbpfNummer1.getFont().deriveFont(
                lblWbpfNummer1.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblWbpfNummer1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblWbpfNummer1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer1, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bplan_nummer}"),
                txtBPlanNummer,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtBPlanNummer, gridBagConstraints);

        lblWbpfNummer2.setFont(lblWbpfNummer2.getFont().deriveFont(
                lblWbpfNummer2.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblWbpfNummer2,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblWbpfNummer2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panMessstellenausbauBody.add(lblWbpfNummer2, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bplan_name}"),
                txtBPlanName,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(txtBPlanName, gridBagConstraints);

        jScrollPane11.setMaximumSize(new java.awt.Dimension(100, 85));
        jScrollPane11.setMinimumSize(new java.awt.Dimension(100, 85));
        jScrollPane11.setPreferredSize(new java.awt.Dimension(100, 85));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.regionalplan}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstRegionalplan);
        bindingGroup.addBinding(jListBinding);

        jScrollPane11.setViewportView(lstRegionalplan);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panMessstellenausbauBody.add(jScrollPane11, gridBagConstraints);

        panArtControls5.setOpaque(false);
        panArtControls5.setLayout(new java.awt.GridBagLayout());

        btnAddRegionalplan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddRegionalplan.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddRegionalplan.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddRegionalplan.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddRegionalplan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddRegionalplanActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls5.add(btnAddRegionalplan, gridBagConstraints);

        btnRemoveRegionalplan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveRegionalplan.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveRegionalplan.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveRegionalplan.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveRegionalplan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveRegionalplanActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls5.add(btnRemoveRegionalplan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panMessstellenausbauBody.add(panArtControls5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        panMessstellenausbauBody.add(filler4, gridBagConstraints);

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMain.add(panPlanungsrecht, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panMain.add(filler9, gridBagConstraints);

        add(panMain, "grunddaten");

        panDetail.setOpaque(false);
        panDetail.setLayout(new java.awt.GridBagLayout());

        panBrachflaechen.setLayout(new java.awt.GridBagLayout());

        panLageTitle.setBackground(java.awt.Color.darkGray);
        panLageTitle.setLayout(new java.awt.GridBagLayout());

        lblLageTitle.setFont(lblLageTitle.getFont());
        lblLageTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle.text")); // NOI18N
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

        cbBfGewerbe.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbBfGewerbe,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbBfGewerbe.text"));      // NOI18N
        cbBfGewerbe.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_gewerbe_industrie}"),
                cbBfGewerbe,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbBfGewerbe, gridBagConstraints);

        cbBfMilitaer.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbBfMilitaer,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbBfMilitaer.text"));      // NOI18N
        cbBfMilitaer.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_militaer}"),
                cbBfMilitaer,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbBfMilitaer, gridBagConstraints);

        cbInfrastrukturSozial.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbInfrastrukturSozial,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbInfrastrukturSozial.text"));      // NOI18N
        cbInfrastrukturSozial.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_infrastruktur_sozial}"),
                cbInfrastrukturSozial,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbInfrastrukturSozial, gridBagConstraints);

        cbInfrastrukturTechnisch.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbInfrastrukturTechnisch,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbInfrastrukturTechnisch.text"));      // NOI18N
        cbInfrastrukturTechnisch.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_infrastruktur_technisch}"),
                cbInfrastrukturTechnisch,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbInfrastrukturTechnisch, gridBagConstraints);

        cbBfVerkehr.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbBfVerkehr,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbBfVerkehr.text"));      // NOI18N
        cbBfVerkehr.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_verkehr}"),
                cbBfVerkehr,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbBfVerkehr, gridBagConstraints);

        cbFbEinzelhandel.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbFbEinzelhandel,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbFbEinzelhandel.text"));      // NOI18N
        cbFbEinzelhandel.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_einzelhandel}"),
                cbFbEinzelhandel,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody.add(cbFbEinzelhandel, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        panLageBody.add(jPanel1, gridBagConstraints);

        lblNutzungsaufgabe.setFont(lblNutzungsaufgabe.getFont().deriveFont(
                lblNutzungsaufgabe.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblNutzungsaufgabe,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblNutzungsaufgabe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        panLageBody.add(lblNutzungsaufgabe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.bk_jahr_nutzungsaufgabe}"),
                txtJahrNutzungsaufgabe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDetail.add(panBrachflaechen, gridBagConstraints);

        panNachfolgenutzung.setLayout(new java.awt.GridBagLayout());

        panLageTitle1.setBackground(java.awt.Color.darkGray);
        panLageTitle1.setLayout(new java.awt.GridBagLayout());

        lblLageTitle1.setFont(lblLageTitle1.getFont());
        lblLageTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle1,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle1.text")); // NOI18N
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

        cbNnGewerbeProdukt.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbNnGewerbeProdukt,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbNnGewerbeProdukt.text"));      // NOI18N
        cbNnGewerbeProdukt.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_gewerbe_produktorientiert}"),
                cbNnGewerbeProdukt,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbNnGewerbeProdukt, gridBagConstraints);

        cbGewerbeDienstleistung.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbGewerbeDienstleistung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbGewerbeDienstleistung.text"));      // NOI18N
        cbGewerbeDienstleistung.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_gewerbe_dienstleistung}"),
                cbGewerbeDienstleistung,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbGewerbeDienstleistung, gridBagConstraints);

        cbWohnen.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbWohnen,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbWohnen.text"));      // NOI18N
        cbWohnen.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_wohnen}"),
                cbWohnen,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbWohnen, gridBagConstraints);

        cbFreiraum.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbFreiraum,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbFreiraum.text"));      // NOI18N
        cbFreiraum.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_freiraum}"),
                cbFreiraum,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbFreiraum, gridBagConstraints);

        cbFreizeit.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbFreizeit,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbFreizeit.text"));      // NOI18N
        cbFreizeit.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_freizeit}"),
                cbFreizeit,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbFreizeit, gridBagConstraints);

        cbEinzelhandel.setFont(new java.awt.Font("Noto Sans", 1, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cbEinzelhandel,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.cbEinzelhandel.text"));      // NOI18N
        cbEinzelhandel.setContentAreaFilled(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_einzelhandel}"),
                cbEinzelhandel,
                org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cbEinzelhandel, gridBagConstraints);

        lblWbpfNn.setFont(lblWbpfNn.getFont().deriveFont(lblWbpfNn.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblWbpfNn,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblWbpfNn.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody1.add(lblWbpfNn, gridBagConstraints);

        cboWbpfNn.setPreferredSize(new java.awt.Dimension(112, 27));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.wbpf_nachfolgenutzung}"),
                cboWbpfNn,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(cboWbpfNn, gridBagConstraints);

        jPanel6.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        panLageBody1.add(jPanel6, gridBagConstraints);

        lblGnnSonstiges.setFont(lblGnnSonstiges.getFont().deriveFont(
                lblGnnSonstiges.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblGnnSonstiges,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblGnnSonstiges.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody1.add(lblGnnSonstiges, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gnn_sonstiges}"),
                txtSonstiges,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody1.add(txtSonstiges, gridBagConstraints);

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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDetail.add(panNachfolgenutzung, gridBagConstraints);

        panInfrastruktur.setLayout(new java.awt.GridBagLayout());

        panLageTitle3.setBackground(java.awt.Color.darkGray);
        panLageTitle3.setLayout(new java.awt.GridBagLayout());

        lblLageTitle3.setFont(lblLageTitle3.getFont());
        lblLageTitle3.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle3,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panLageTitle3.add(lblLageTitle3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panInfrastruktur.add(panLageTitle3, gridBagConstraints);

        panLageBody3.setOpaque(false);
        panLageBody3.setLayout(new java.awt.GridBagLayout());

        lblAessereErschl.setFont(lblAessereErschl.getFont().deriveFont(
                lblAessereErschl.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAessereErschl,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAessereErschl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblAessereErschl, gridBagConstraints);

        lblInnereErschl.setFont(lblInnereErschl.getFont().deriveFont(
                lblInnereErschl.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblInnereErschl,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblInnereErschl.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblInnereErschl, gridBagConstraints);

        lblOepnv.setFont(lblOepnv.getFont().deriveFont(lblOepnv.getFont().getStyle() | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblOepnv,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblOepnv.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblOepnv, gridBagConstraints);

        lblZentrennaehe.setFont(lblZentrennaehe.getFont().deriveFont(
                lblZentrennaehe.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblZentrennaehe,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblZentrennaehe.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 6);
        panLageBody3.add(lblZentrennaehe, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aeussere_erschliessung}"),
                txtAeussereErschl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(txtAeussereErschl, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.innere_erschliessung}"),
                txtInnereErschl,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(txtInnereErschl, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.zentrennaehe}"),
                txtZentrennaehe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(txtZentrennaehe, gridBagConstraints);

        jScrollPane6.setMaximumSize(new java.awt.Dimension(254, 100));
        jScrollPane6.setMinimumSize(new java.awt.Dimension(254, 100));
        jScrollPane6.setPreferredSize(new java.awt.Dimension(254, 100));

        lstOepnv.setPreferredSize(new java.awt.Dimension(52, 150));

        eLProperty = org.jdesktop.beansbinding.ELProperty.create("${cidsBean.oepnv_anbindung}");
        jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                eLProperty,
                lstOepnv);
        bindingGroup.addBinding(jListBinding);

        jScrollPane6.setViewportView(lstOepnv);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody3.add(jScrollPane6, gridBagConstraints);

        panArtControls2.setOpaque(false);
        panArtControls2.setLayout(new java.awt.GridBagLayout());

        btnAddArt2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_add_mini.png"))); // NOI18N
        btnAddArt2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnAddArt2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnAddArt2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnAddArt2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnAddArt2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls2.add(btnAddArt2, gridBagConstraints);

        btnRemoveArt2.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit_remove_mini.png"))); // NOI18N
        btnRemoveArt2.setMaximumSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.setMinimumSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.setPreferredSize(new java.awt.Dimension(43, 25));
        btnRemoveArt2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRemoveArt2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panArtControls2.add(btnRemoveArt2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panLageBody3.add(panArtControls2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panInfrastruktur.add(panLageBody3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDetail.add(panInfrastruktur, gridBagConstraints);

        panBewertung.setPreferredSize(new java.awt.Dimension(400, 235));
        panBewertung.setLayout(new java.awt.GridBagLayout());

        panLageTitle4.setBackground(java.awt.Color.darkGray);
        panLageTitle4.setLayout(new java.awt.GridBagLayout());

        lblLageTitle4.setFont(lblLageTitle4.getFont());
        lblLageTitle4.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblLageTitle4,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblLageTitle4.text")); // NOI18N
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

        lblVerfuegbarkeit.setFont(lblVerfuegbarkeit.getFont().deriveFont(
                lblVerfuegbarkeit.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblVerfuegbarkeit,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblVerfuegbarkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblVerfuegbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.verfuegbarkeit}"),
                cbVerfuegbarkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbVerfuegbarkeit, gridBagConstraints);

        lblArtDerNutzung.setFont(lblArtDerNutzung.getFont().deriveFont(
                lblArtDerNutzung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblArtDerNutzung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblArtDerNutzung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblArtDerNutzung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.art_der_nutzung}"),
                txtArtDerNutzung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtArtDerNutzung, gridBagConstraints);

        lblRevitalisierung.setFont(lblRevitalisierung.getFont().deriveFont(
                lblRevitalisierung.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblRevitalisierung,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblRevitalisierung.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblRevitalisierung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.revitalisierung}"),
                txtRevitalisierung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtRevitalisierung, gridBagConstraints);

        lblEntwicklungsausssichten.setFont(lblEntwicklungsausssichten.getFont().deriveFont(
                lblEntwicklungsausssichten.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblEntwicklungsausssichten,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblEntwicklungsausssichten.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblEntwicklungsausssichten, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.entwicklungsaussischten}"),
                txtEntwicklungsausssichten,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(txtEntwicklungsausssichten, gridBagConstraints);

        lblHandlungsdruck.setFont(lblHandlungsdruck.getFont().deriveFont(
                lblHandlungsdruck.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblHandlungsdruck,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck.text")); // NOI18N
        lblHandlungsdruck.setToolTipText(org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblHandlungsdruck.toolTipText",
                new Object[] {}));                                   // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblHandlungsdruck, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.handlungsdruck}"),
                cbHandlungsdruck,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbHandlungsdruck, gridBagConstraints);

        lblAktivierbarkeit.setFont(lblAktivierbarkeit.getFont().deriveFont(
                lblAktivierbarkeit.getFont().getStyle()
                        | java.awt.Font.BOLD));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblAktivierbarkeit,
            org.openide.util.NbBundle.getMessage(
                PfPotenzialflaecheEditor.class,
                "PfPotenzialflaecheEditor.lblAktivierbarkeit.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        panLageBody4.add(lblAktivierbarkeit, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.aktivierbarkeit}"),
                cbAktivierbarkeit,
                org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        panLageBody4.add(cbAktivierbarkeit, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        panBewertung.add(panLageBody4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panDetail.add(panBewertung, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        panDetail.add(filler12, gridBagConstraints);

        add(panDetail, "details");

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
        ((CardLayout)getLayout()).show(this, "grunddaten");
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
        ((CardLayout)getLayout()).show(this, "details");
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
    private void btnAddBisherigeNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddBisherigeNutzungActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgAddBisherigeNutzung, true);
    }                                                                                          //GEN-LAST:event_btnAddBisherigeNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveBisherigeNutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveBisherigeNutzungActionPerformed
        final List<String> selection = lstBisherigeNutzung.getSelectedValuesList();
        if ((selection != null) && (selection.size() > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die bisherige Nutzung wirklich gelöscht werden?",
                    "Bisherige Nutzung entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection artCol = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "bisherige_nutzung");
                if (artCol != null) {
                    for (final Object cur : selection) {
                        try {
                            artCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                             //GEN-LAST:event_btnRemoveBisherigeNutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddUmgebungsnutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddUmgebungsnutzungActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgAddUmgebungsnutzung, true);
    }                                                                                          //GEN-LAST:event_btnAddUmgebungsnutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveUmgebungsnutzungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveUmgebungsnutzungActionPerformed
        final List<String> selection = lstUmgebungsnutzung.getSelectedValuesList();
        if ((selection != null) && (selection.size() > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die Umgebungsnutzung wirklich gelöscht werden?",
                    "Umgebungsnutzung entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection artCol = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "umgebungsnutzung");
                if (artCol != null) {
                    for (final Object cur : selection) {
                        try {
                            artCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                             //GEN-LAST:event_btnRemoveUmgebungsnutzungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddArt2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddArt2ActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgAddOepnv, true);
    }                                                                              //GEN-LAST:event_btnAddArt2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveArt2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveArt2ActionPerformed
        final List<String> selection = lstOepnv.getSelectedValuesList();
        if ((selection != null) && (selection.size() > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll die ÖPNV-Anbindung wirklich gelöscht werden?",
                    "ÖPNV-Anbindung entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection artCol = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "oepnv_anbindung");
                if (artCol != null) {
                    for (final Object cur : selection) {
                        try {
                            artCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                 //GEN-LAST:event_btnRemoveArt2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort1ActionPerformed
        dlgAddBisherigeNutzung.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk1ActionPerformed
        final List<Object> selectedObjects = lstAlleBisherigeNutzung.getSelectedValuesList();

        if ((selectedObjects != null) && (selectedObjects.size() > 0)) {
            final Collection<CidsBean> colToAdd = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "bisherige_nutzung");

            for (final Object selection : selectedObjects) {
                if ((colToAdd != null) && (selection instanceof CidsBean)) {
                    if (!colToAdd.contains(selection)) {
                        colToAdd.add((CidsBean)selection);
                    }
                }
            }
        }

        dlgAddBisherigeNutzung.setVisible(false);
    } //GEN-LAST:event_btnMenOk1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort2ActionPerformed
        dlgAddUmgebungsnutzung.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk2ActionPerformed
        final List<Object> selectedObjects = lstAlleUmgebungsnutzungenNutzung.getSelectedValuesList();

        if ((selectedObjects != null) && (selectedObjects.size() > 0)) {
            final Collection<CidsBean> colToAdd = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "umgebungsnutzung");

            for (final Object selection : selectedObjects) {
                if ((colToAdd != null) && (selection instanceof CidsBean)) {
                    if (!colToAdd.contains(selection)) {
                        colToAdd.add((CidsBean)selection);
                    }
                }
            }
        }

        dlgAddUmgebungsnutzung.setVisible(false);
    } //GEN-LAST:event_btnMenOk2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort3ActionPerformed
        dlgAddOepnv.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk3ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk3ActionPerformed
        final List<Object> selectedObjects = lstAlleOepnv.getSelectedValuesList();

        if ((selectedObjects != null) && (selectedObjects.size() > 0)) {
            final Collection<CidsBean> colToAdd = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "oepnv_anbindung");

            for (final Object selection : selectedObjects) {
                if ((colToAdd != null) && (selection instanceof CidsBean)) {
                    if (!colToAdd.contains(selection)) {
                        colToAdd.add((CidsBean)selection);
                    }
                }
            }
        }

        dlgAddOepnv.setVisible(false);
    } //GEN-LAST:event_btnMenOk3ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort4ActionPerformed
        dlgFlaeche.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk4ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk4ActionPerformed
        try {
            cidsBean.setProperty("beschreibung_flaeche", taFlaecheDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for beschreibung_flaeche", e);
        }
        dlgFlaeche.setVisible(false);
    }                                                                             //GEN-LAST:event_btnMenOk4ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort5ActionPerformed
        dlgMassnahme.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk5ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk5ActionPerformed
        try {
            cidsBean.setProperty("notwendige_massnahmen", taMassnahmeDialog.getText());
        } catch (Exception e) {
            LOG.error("Cannot save text for notwendige_massnahmen", e);
        }
        dlgMassnahme.setVisible(false);
    }                                                                             //GEN-LAST:event_btnMenOk5ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnAddRegionalplanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnAddRegionalplanActionPerformed
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgAddRegionalplan, true);
    }                                                                                      //GEN-LAST:event_btnAddRegionalplanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRemoveRegionalplanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRemoveRegionalplanActionPerformed
        final List<String> selection = lstRegionalplan.getSelectedValuesList();
        if ((selection != null) && (selection.size() > 0)) {
            final int answer = JOptionPane.showConfirmDialog(
                    StaticSwingTools.getParentFrame(this),
                    "Soll der Regionalplan wirklich gelöscht werden?",
                    "Regionalplan entfernen",
                    JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                final Collection artCol = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "regionalplan");
                if (artCol != null) {
                    for (final Object cur : selection) {
                        try {
                            artCol.remove(cur);
                        } catch (Exception e) {
                            ObjectRendererUtils.showExceptionWindowToUser("Fehler beim Löschen", e, this);
                        }
                    }
                }
            }
        }
    }                                                                                         //GEN-LAST:event_btnRemoveRegionalplanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenAbort6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenAbort6ActionPerformed
        dlgAddRegionalplan.setVisible(false);
    }                                                                                //GEN-LAST:event_btnMenAbort6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMenOk6ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMenOk6ActionPerformed
        final List<Object> selectedObjects = lstAlleRegionalplan.getSelectedValuesList();

        if ((selectedObjects != null) && (selectedObjects.size() > 0)) {
            final Collection<CidsBean> colToAdd = CidsBeanSupport.getBeanCollectionFromProperty(
                    cidsBean,
                    "regionalplan");

            for (final Object selection : selectedObjects) {
                if ((colToAdd != null) && (selection instanceof CidsBean)) {
                    if (!colToAdd.contains(selection)) {
                        colToAdd.add((CidsBean)selection);
                    }
                }
            }
        }

        dlgAddRegionalplan.setVisible(false);
    } //GEN-LAST:event_btnMenOk6ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnMassnahmenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnMassnahmenActionPerformed
        taMassnahmeDialog.setText((String)cidsBean.getProperty("notwendige_massnahmen"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgMassnahme, true);
    }                                                                                 //GEN-LAST:event_btnMassnahmenActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnFlaecheActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnFlaecheActionPerformed
        taFlaecheDialog.setText((String)cidsBean.getProperty("beschreibung_flaeche"));
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(this), dlgFlaeche, true);
    }                                                                              //GEN-LAST:event_btnFlaecheActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbGeomFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_cbGeomFocusLost
        setGeometryArea();
    }                                                                   //GEN-LAST:event_cbGeomFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void txtBezeichnungFocusLost(final java.awt.event.FocusEvent evt) { //GEN-FIRST:event_txtBezeichnungFocusLost
        txtTitle.setText(getTitle());
    }                                                                           //GEN-LAST:event_txtBezeichnungFocusLost

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  isDgk DOCUMENT ME!
     * @param   isDgk        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public BufferedImage generateOverviewMap(final CidsBean flaecheBean, final boolean isDgk) {
        try {
            final String urlBackground = (isDgk ? PotenzialflaechenProperties.getInstance().getDgkUrl()
                                                : PotenzialflaechenProperties.getInstance().getOrthoUrl());
            final Geometry geom = (Geometry)flaecheBean.getProperty("geometrie.geo_field");

            if (geom != null) {
                final XBoundingBox boundingBox = new XBoundingBox(geom);
                boundingBox.increase(10);
                boundingBox.setX1(boundingBox.getX1() - 50);
                boundingBox.setY1(boundingBox.getY1() - 50);
                boundingBox.setX2(boundingBox.getX2() + 50);
                boundingBox.setY2(boundingBox.getY2() + 50);

                final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
                mapProvider.setCenterMapOnResize(true);
                mapProvider.setBoundingBox(boundingBox);
                final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(urlBackground);
                final SimpleWMS simpleWms = new SimpleWMS(getMapUrl);
                mapProvider.addLayer(simpleWms);
                final DefaultStyledFeature f = new DefaultStyledFeature();
                f.setGeometry(geom);
                f.setHighlightingEnabled(true);
                f.setLinePaint(Color.RED);
                f.setLineWidth(3);
                mapProvider.addFeature(f);

                return (BufferedImage)mapProvider.getImageAndWait(72, 300, 250, 150);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while retrieving map", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnReportActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnReportActionPerformed
        final CidsBean flaecheBean = cidsBean;

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = "Potenzialflaeche"
                        + flaecheBean.toString();
            final String downloadTitle = "Potenzialflaeche "
                        + flaecheBean.toString();
            new SwingWorker<Download, Void>() {

                    @Override
                    protected Download doInBackground() throws Exception {
                        final BufferedImage[] maps = new BufferedImage[2];
                        maps[0] = generateOverviewMap(flaecheBean, false);
                        maps[1] = generateOverviewMap(flaecheBean, true);
                        try(final ByteArrayOutputStream out0 = new ByteArrayOutputStream();
                                    final ByteArrayOutputStream out1 = new ByteArrayOutputStream()) {
                            final Collection<ServerActionParameter> params = new ArrayList<>();
                            if (maps[0] != null) {
                                ImageIO.write(maps[0], "png", out0);
                                params.add(new ServerActionParameter<>(
                                        PotenzialflaecheReportServerAction.Parameter.IMAGE_ORTHO.toString(),
                                        out0.toByteArray()));
                            }
                            if (maps[1] != null) {
                                ImageIO.write(maps[1], "png", out1);
                                params.add(new ServerActionParameter<>(
                                        PotenzialflaecheReportServerAction.Parameter.IMAGE_DGK.toString(),
                                        out1.toByteArray()));
                            }

                            final Download download = new ByteArrayActionDownload(
                                    PotenzialflaecheReportServerAction.TASK_NAME,
                                    new MetaObjectNode(flaecheBean),
                                    params.toArray(new ServerActionParameter[0]),
                                    downloadTitle,
                                    jobname,
                                    filename,
                                    ".pdf",
                                    PfPotenzialflaecheEditor.this.getConnectionContext());
                            return download;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final Download download = (Download)get();
                            DownloadManager.instance().add(download);
                        } catch (final Exception ex) {
                            LOG.error("Cannot create report", ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Fehler Erstellen des Reports",
                                ex,
                                PfPotenzialflaecheEditor.this);
                        }
                    }
                }.execute();
        }
    } //GEN-LAST:event_btnReportActionPerformed

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

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        this.cidsBean = cidsBean;
        if (cidsBean != null) {
            DefaultCustomObjectEditor.setMetaClassInformationToMetaClassStoreComponentsInBindingGroup(
                bindingGroup,
                cidsBean,
                getConnectionContext());
            bindingGroup.bind();
            txtTitle.setText(getTitle());

            setGeometryArea();

            if ((getLastKampagne() != null) && editable) {
                try {
                    if (cidsBean.getMetaObject().getStatus() == MetaObject.NEW) {
                        cidsBean.setProperty("kampagne", getLastKampagne());
                        markUsedFields(
                            this,
                            (CidsBean)getLastKampagne().getProperty("steckbrieftemplate"));
                    }
                } catch (Exception ex) {
                    LOG.error("Cannot add kampagne", ex);
                    ObjectRendererUtils.showExceptionWindowToUser(
                        "Kampagne konnte nicht hinzugefügt werden",
                        ex,
                        this);
                }
            } else {
                final CidsBean kampagne = (CidsBean)cidsBean.getProperty("kampagne");
                if (kampagne != null) {
                    markUsedFields(
                        this,
                        (CidsBean)kampagne.getProperty("steckbrieftemplate"));
                }
            }
        }
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
            lblFlaechengroesseWert.setText(Math.round(area) + " m²");
        }
    }

    @Override
    public void dispose() {
        if (editable) {
            ((DefaultCismapGeometryComboBoxEditor)cbGeom).dispose();
        }
    }

    @Override
    public String getTitle() {
        if (cidsBean.getProperty("bezeichnung") == null) {
            return "";
        } else {
            return (String)cidsBean.getProperty("bezeichnung");
        }
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

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class MCSwingWorker extends SwingWorker<DefaultComboBoxModel, Void> {

        //~ Instance fields ----------------------------------------------------

        private JList list;
        private MetaClass mc;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new MCSwingWorker object.
         *
         * @param  list  DOCUMENT ME!
         * @param  mc    DOCUMENT ME!
         */
        public MCSwingWorker(final JList list, final MetaClass mc) {
            this.list = list;
            this.mc = mc;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        protected DefaultComboBoxModel doInBackground() throws Exception {
            Thread.currentThread().setName("MCSwingWorker init()");

            return getModelByMetaClass(
                    mc,
                    false,
                    false,
                    getConnectionContext());
        }

        @Override
        protected void done() {
            try {
                final DefaultComboBoxModel tmp = get();
                list.setModel(tmp);
            } catch (InterruptedException interruptedException) {
            } catch (ExecutionException executionException) {
                LOG.error("Error while initializing the model of a referenceCombo", executionException); // NOI18N
            }
        }
    }
}
