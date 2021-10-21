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

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Color;
import java.awt.EventQueue;

import java.io.IOException;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.NumberFormat;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.converter.SQLTimestampToStringConverter;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionDownloadBestellungAction;
import de.cismet.cids.custom.wunda_blau.search.server.CidsAlkisSearchStatement;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

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
public class Fs_bestellungRenderer extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Fs_bestellungRenderer.class);

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT);

    private static final Converter<Boolean, String> CONVERTER_BEZUGSWEG = new Converter<Boolean, String>() {

            @Override
            public String convertForward(final Boolean s) {
                if (Boolean.TRUE.equals(s)) {
                    return "Postweg";
                } else if (Boolean.FALSE.equals(s)) {
                    return "Download";
                } else {
                    return null;
                }
            }

            @Override
            public Boolean convertReverse(final String t) {
                return null;
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final ObjectMapper MAPPER = new ObjectMapper();

    private String title;
    private CidsBean cidsBean;
    private MetaObjectNode flurstueckMon;
    private MetaObjectNode buchungsblattMon;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdAttachBilling;
    private javax.swing.JButton cmdReload;
    private org.jdesktop.swingx.JXHyperlink hlBerechtigungspruefung;
    private org.jdesktop.swingx.JXHyperlink hlBuchungsblattValue;
    private org.jdesktop.swingx.JXHyperlink hlEMailValue;
    private org.jdesktop.swingx.JXHyperlink hlFlurstueckeValue;
    private org.jdesktop.swingx.JXHyperlink hlProduktValue;
    private org.jdesktop.swingx.JXHyperlink hlStatusErrorDetails;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private javax.swing.JLabel labInfoTitle;
    private javax.swing.JLabel labInfoTitle1;
    private javax.swing.JLabel labInfoTitle2;
    private javax.swing.JLabel lblAdresse;
    private javax.swing.JLabel lblBezugsweg;
    private javax.swing.JLabel lblBezugswegValue;
    private javax.swing.JLabel lblBuchungsblatt;
    private javax.swing.JLabel lblEMail;
    private javax.swing.JLabel lblEingegangenAm;
    private javax.swing.JLabel lblEingegangenAmValue;
    private javax.swing.JLabel lblFlurstuecke;
    private javax.swing.JLabel lblGebuehr;
    private javax.swing.JLabel lblGebuehrDownload;
    private javax.swing.JLabel lblGebuehrDownloadValue;
    private javax.swing.JLabel lblGebuehrPostweg;
    private javax.swing.JLabel lblGebuehrPostwegValue;
    private javax.swing.JLabel lblGebuehrValue;
    private javax.swing.JLabel lblLaAdresse;
    private javax.swing.JLabel lblLaFirma;
    private javax.swing.JLabel lblLaFirmaValue;
    private javax.swing.JLabel lblLaLand;
    private javax.swing.JLabel lblLaLandValue;
    private javax.swing.JLabel lblLaName;
    private javax.swing.JLabel lblLaNameValue;
    private javax.swing.JLabel lblLaOrt;
    private javax.swing.JLabel lblLaOrtValue;
    private javax.swing.JLabel lblLaStrasse;
    private javax.swing.JLabel lblLaStrasseValue;
    private javax.swing.JLabel lblLand;
    private javax.swing.JLabel lblLandValue;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameValue;
    private javax.swing.JLabel lblOrt;
    private javax.swing.JLabel lblOrtValue;
    private javax.swing.JLabel lblProdukt;
    private javax.swing.JLabel lblRaFirma;
    private javax.swing.JLabel lblRaFirmaValue;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatusValue;
    private javax.swing.JLabel lblStrasse;
    private javax.swing.JLabel lblStrasseValue;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTitleValue;
    private javax.swing.JLabel lblTransId;
    private de.cismet.cismap.commons.gui.MappingComponent mappingComponent1;
    private javax.swing.JPanel panAdressen;
    private javax.swing.JPanel panFiller;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panInfo;
    private javax.swing.JPanel panLieferanschrift;
    private javax.swing.JPanel panMain;
    private javax.swing.JPanel panMap;
    private javax.swing.JPanel panProduktValue;
    private javax.swing.JPanel panRechnungsanschrift;
    private javax.swing.JPanel panStatusValue;
    private javax.swing.JPanel panTitle;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Fs_bestellungRenderer object.
     */
    public Fs_bestellungRenderer() {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        initComponents();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geom  DOCUMENT ME!
     */
    private void initMap(final Geometry geom) {
        final Runnable mapRunnable = new Runnable() {

                @Override
                public void run() {
                    final Geometry pureGeom = CrsTransformer.transformToGivenCrs(
                            geom,
                            ClientAlkisConf.getInstance().getSrsService());
                    final de.cismet.cismap.commons.XBoundingBox box = new de.cismet.cismap.commons.XBoundingBox(
                            pureGeom.getEnvelope().buffer(ClientAlkisConf.getInstance().getGeoBuffer()));
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
                    swms.setName("Flurstueck");
                    final StyledFeature dsf = new DefaultStyledFeature();
                    dsf.setGeometry(pureGeom);
                    dsf.setFillingPaint(new Color(1, 0, 0, 0.75F));
                    // add the raster layer to the model
                    mappingModel.addLayer(swms);
                    // set the model
                    mappingComponent1.setMappingModel(mappingModel);
                    // initial positioning of the map
                    final int duration = mappingComponent1.getAnimationDuration();
                    mappingComponent1.setAnimationDuration(0);
                    mappingComponent1.gotoInitialBoundingBox();
                    // interaction mode
                    mappingComponent1.setInteractionMode(MappingComponent.ZOOM);
                    // finally when all configurations are done ...
                    mappingComponent1.unlock();
                    mappingComponent1.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                            @Override
                            public void mouseClicked(final PInputEvent evt) {
                                if (evt.getClickCount() > 1) {
                                    final CidsBean bean = cidsBean;
                                    ObjectRendererUtils.switchToCismapMap();
                                    ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                }
                            }
                        });
                    mappingComponent1.setInteractionMode("MUTE");
                    mappingComponent1.getFeatureCollection().addFeature(dsf);
                    mappingComponent1.setAnimationDuration(duration);
                }
            };
        if (EventQueue.isDispatchThread()) {
            mapRunnable.run();
        } else {
            EventQueue.invokeLater(mapRunnable);
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
        lblTitle = new javax.swing.JLabel();
        lblTitleValue = new javax.swing.JLabel();
        panFooter = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        panMain = new javax.swing.JPanel();
        panInfo = new javax.swing.JPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblTransId = new javax.swing.JLabel();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        lblEingegangenAm = new javax.swing.JLabel();
        lblEingegangenAmValue = new javax.swing.JLabel();
        lblBezugsweg = new javax.swing.JLabel();
        lblBezugswegValue = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        panStatusValue = new javax.swing.JPanel();
        lblStatusValue = new javax.swing.JLabel();
        hlStatusErrorDetails = new org.jdesktop.swingx.JXHyperlink();
        jPanel6 = new javax.swing.JPanel();
        lblFlurstuecke = new javax.swing.JLabel();
        hlFlurstueckeValue = new org.jdesktop.swingx.JXHyperlink();
        lblBuchungsblatt = new javax.swing.JLabel();
        hlBuchungsblattValue = new org.jdesktop.swingx.JXHyperlink();
        lblProdukt = new javax.swing.JLabel();
        panProduktValue = new javax.swing.JPanel();
        hlProduktValue = new org.jdesktop.swingx.JXHyperlink();
        cmdReload = new javax.swing.JButton();
        cmdAttachBilling = new javax.swing.JButton();
        hlBerechtigungspruefung = new org.jdesktop.swingx.JXHyperlink();
        lblGebuehr = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        lblGebuehrValue = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jPanel5 = new javax.swing.JPanel();
        lblGebuehrDownload = new javax.swing.JLabel();
        lblGebuehrPostweg = new javax.swing.JLabel();
        lblGebuehrDownloadValue = new javax.swing.JLabel();
        lblGebuehrPostwegValue = new javax.swing.JLabel();
        panMap = new javax.swing.JPanel();
        mappingComponent1 = new de.cismet.cismap.commons.gui.MappingComponent();
        panAdressen = new javax.swing.JPanel();
        panLieferanschrift = new javax.swing.JPanel();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblLaFirma = new javax.swing.JLabel();
        lblLaFirmaValue = new javax.swing.JLabel();
        lblLaName = new javax.swing.JLabel();
        lblLaNameValue = new javax.swing.JLabel();
        lblLaStrasse = new javax.swing.JLabel();
        lblLaStrasseValue = new javax.swing.JLabel();
        lblLaOrt = new javax.swing.JLabel();
        lblLaOrtValue = new javax.swing.JLabel();
        lblLaAdresse = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        lblLaLand = new javax.swing.JLabel();
        lblLaLandValue = new javax.swing.JLabel();
        lblEMail = new javax.swing.JLabel();
        hlEMailValue = new org.jdesktop.swingx.JXHyperlink();
        panRechnungsanschrift = new javax.swing.JPanel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        labInfoTitle2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblRaFirma = new javax.swing.JLabel();
        lblRaFirmaValue = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblNameValue = new javax.swing.JLabel();
        lblStrasse = new javax.swing.JLabel();
        lblStrasseValue = new javax.swing.JLabel();
        lblOrt = new javax.swing.JLabel();
        lblOrtValue = new javax.swing.JLabel();
        lblAdresse = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        lblLand = new javax.swing.JLabel();
        lblLandValue = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        panFiller = new javax.swing.JPanel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            lblTitle,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblTitle.text")); // NOI18N
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
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.jButton2.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.postweg}"),
                jButton2,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        jButton2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jButton2ActionPerformed(evt);
                }
            });
        panFooter.add(jButton2, new java.awt.GridBagConstraints());

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
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.labInfoTitle.text")); // NOI18N
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
            lblTransId,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblTransId.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblTransId, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.transid}"),
                jXHyperlink2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink2ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jXHyperlink2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEingegangenAm,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblEingegangenAm.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEingegangenAm, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.eingang_ts}"),
                lblEingegangenAmValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new SQLTimestampToStringConverter(DATE_FORMAT));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblEingegangenAmValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBezugsweg,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblBezugsweg.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBezugsweg, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.postweg}"),
                lblBezugswegValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(CONVERTER_BEZUGSWEG);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBezugswegValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStatus,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblStatus.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblStatus, gridBagConstraints);

        panStatusValue.setMinimumSize(new java.awt.Dimension(79, 1));
        panStatusValue.setOpaque(false);
        panStatusValue.setPreferredSize(new java.awt.Dimension(100, 1));
        panStatusValue.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStatusValue,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblStatusValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panStatusValue.add(lblStatusValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            hlStatusErrorDetails,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.hlStatusErrorDetails.text")); // NOI18N
        hlStatusErrorDetails.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlStatusErrorDetailsActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panStatusValue.add(hlStatusErrorDetails, gridBagConstraints);

        jPanel6.setOpaque(false);

        final javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panStatusValue.add(jPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(panStatusValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblFlurstuecke,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblFlurstuecke.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblFlurstuecke, gridBagConstraints);

        hlFlurstueckeValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstueckeValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlFlurstueckeValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblBuchungsblatt,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblBuchungsblatt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblBuchungsblatt, gridBagConstraints);

        hlBuchungsblattValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBuchungsblattValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(hlBuchungsblattValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblProdukt,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblProdukt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblProdukt, gridBagConstraints);

        panProduktValue.setOpaque(false);
        panProduktValue.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${ cidsBean.produkt_dateipfad != null && !produktTooOld }"),
                hlProduktValue,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        hlProduktValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlProduktValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panProduktValue.add(hlProduktValue, gridBagConstraints);

        cmdReload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/stock_refresh.png")));              // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cmdReload,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.cmdReload.text")); // NOI18N
        cmdReload.setToolTipText(org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.cmdReload.toolTipText"));                                                        // NOI18N
        cmdReload.setFocusPainted(false);
        cmdReload.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdReloadActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        panProduktValue.add(cmdReload, gridBagConstraints);

        cmdAttachBilling.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/16/money--pencil.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(
            cmdAttachBilling,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.cmdAttachBilling.text"));                                                  // NOI18N
        cmdAttachBilling.setToolTipText(org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.cmdAttachBilling.toolTipText"));                                           // NOI18N
        cmdAttachBilling.setFocusPainted(false);
        cmdAttachBilling.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cmdAttachBillingActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_END;
        panProduktValue.add(cmdAttachBilling, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("(${cidsBean.berechtigungspruefung.schluessel})"),
                hlBerechtigungspruefung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        hlBerechtigungspruefung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBerechtigungspruefungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panProduktValue.add(hlBerechtigungspruefung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(panProduktValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGebuehr,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblGebuehr.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGebuehr, gridBagConstraints);

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebuehr}"),
                lblGebuehrValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new CurrencyConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel7.add(lblGebuehrValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            jXHyperlink1,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.jXHyperlink1.text")); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${ cidsBean.rechnung_dateipfad != null && !produktTooOld }"),
                jXHyperlink1,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    jXHyperlink1ActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        jPanel7.add(jXHyperlink1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        jPanel5.setOpaque(false);

        final javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jPanel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGebuehrDownload,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblGebuehrDownload.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGebuehrDownload, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblGebuehrPostweg,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblGebuehrPostweg.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGebuehrPostweg, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebuehr}"),
                lblGebuehrDownloadValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new CurrencyConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGebuehrDownloadValue, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gebuehr_postweg}"),
                lblGebuehrPostwegValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        binding.setConverter(new CurrencyConverter());
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblGebuehrPostwegValue, gridBagConstraints);

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

        panMap.setMinimumSize(new java.awt.Dimension(200, 100));
        panMap.setPreferredSize(new java.awt.Dimension(400, 200));
        panMap.setLayout(new java.awt.BorderLayout());
        panMap.add(mappingComponent1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        panMain.add(panMap, gridBagConstraints);

        panAdressen.setOpaque(false);
        panAdressen.setLayout(new java.awt.GridLayout(1, 0, 20, 20));

        panLieferanschrift.setOpaque(false);
        panLieferanschrift.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel2.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel2.setLayout(new java.awt.GridBagLayout());

        labInfoTitle1.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labInfoTitle1,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.labInfoTitle1.text")); // NOI18N
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
            lblLaFirma,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblLaFirma.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaFirma, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_versand.firma}"),
                lblLaFirmaValue,
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
        jPanel2.add(lblLaFirmaValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLaName,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblLaName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_versand.vorname} ${cidsBean.fk_adresse_versand.name}"),
                lblLaNameValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaNameValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLaStrasse,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblLaStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_versand.strasse} ${cidsBean.fk_adresse_versand.hausnummer}"),
                lblLaStrasseValue,
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
        jPanel2.add(lblLaStrasseValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLaOrt,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblLaOrt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaOrt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_versand.plz} ${cidsBean.fk_adresse_versand.ort}"),
                lblLaOrtValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaOrtValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLaAdresse,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.lblLaAdresse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaAdresse, gridBagConstraints);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_versand.alternativ}"),
                jTextArea1,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(jTextArea1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jScrollPane1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLaLand,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblLaLand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblLaLand, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_versand.staat}"),
                lblLaLandValue,
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
        jPanel2.add(lblLaLandValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblEMail,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblEMail.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(lblEMail, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.email != null}"),
                hlEMailValue,
                org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.email}"),
                hlEMailValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("-");
        binding.setSourceUnreadableValue("-");
        bindingGroup.addBinding(binding);

        hlEMailValue.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlEMailValueActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(hlEMailValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panLieferanschrift.add(jPanel2, gridBagConstraints);

        panAdressen.add(panLieferanschrift);

        panRechnungsanschrift.setOpaque(false);
        panRechnungsanschrift.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel3.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel3.setLayout(new java.awt.GridBagLayout());

        labInfoTitle2.setForeground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(
            labInfoTitle2,
            org.openide.util.NbBundle.getMessage(
                Fs_bestellungRenderer.class,
                "Fs_bestellungRenderer.labInfoTitle2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(labInfoTitle2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panRechnungsanschrift.add(semiRoundedPanel3, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(
            lblRaFirma,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblRaFirma.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblRaFirma, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_rechnung.firma}"),
                lblRaFirmaValue,
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
        jPanel3.add(lblRaFirmaValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblName,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblName, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_rechnung.vorname} ${cidsBean.fk_adresse_rechnung.name}"),
                lblNameValue,
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
        jPanel3.add(lblNameValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblStrasse,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblStrasse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblStrasse, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_rechnung.strasse} ${cidsBean.fk_adresse_rechnung.hausnummer}"),
                lblStrasseValue,
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
        jPanel3.add(lblStrasseValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblOrt,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblOrt.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblOrt, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create(
                    "${cidsBean.fk_adresse_rechnung.plz} ${cidsBean.fk_adresse_rechnung.ort}"),
                lblOrtValue,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblOrtValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblAdresse,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblAdresse.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblAdresse, gridBagConstraints);

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_rechnung.alternativ}"),
                jTextArea2,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        jScrollPane2.setViewportView(jTextArea2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jScrollPane2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(
            lblLand,
            org.openide.util.NbBundle.getMessage(Fs_bestellungRenderer.class, "Fs_bestellungRenderer.lblLand.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(lblLand, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.fk_adresse_rechnung.staat}"),
                lblLandValue,
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
        jPanel3.add(lblLandValue, gridBagConstraints);

        jPanel4.setOpaque(false);

        final javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel3.add(jPanel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panRechnungsanschrift.add(jPanel3, gridBagConstraints);

        panAdressen.add(panRechnungsanschrift);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panMain.add(panAdressen, gridBagConstraints);

        panFiller.setOpaque(false);

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
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panMain.add(panFiller, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panMain, gridBagConstraints);

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstueckeValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstueckeValueActionPerformed
        if (flurstueckMon != null) {
            ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObjectNode(flurstueckMon, false);
        }
    }                                                                                      //GEN-LAST:event_hlFlurstueckeValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlEMailValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlEMailValueActionPerformed
        try {
            BrowserLauncher.openURL("mailto:" + (String)cidsBean.getProperty("email"));
        } catch (Exception ex) {
            LOG.warn("could not open mailto link", ex);
        }
    }                                                                                //GEN-LAST:event_hlEMailValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlProduktValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlProduktValueActionPerformed
        final String filePath = (String)cidsBean.getProperty("produkt_dateipfad");
        final String fileName = (String)cidsBean.getProperty("produkt_dateiname_orig");
        if (filePath != null) {
            try {
                final MetaObject mo = getCidsBean().getMetaObject();
                final MetaObjectNode mon = new MetaObjectNode(mo.getDomain(), mo.getId(), mo.getClassID());

                final int extPos = fileName.lastIndexOf(".");
                final String pureName = fileName.substring(0, extPos);
                final String ext = fileName.substring(extPos);

                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(Fs_bestellungRenderer.this)) {
                    final String path = DownloadManagerDialog.getInstance().getJobName();
                    final Download download = new ByteArrayActionDownload(
                            FormSolutionDownloadBestellungAction.TASK_NAME,
                            mon,
                            null,
                            "Bestellung: "
                                    + title,
                            path,
                            pureName,
                            ext,
                            getConnectionContext());
                    DownloadManager.instance().add(download);
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
    } //GEN-LAST:event_hlProduktValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlStatusErrorDetailsActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlStatusErrorDetailsActionPerformed
        final String exceptionJson = (String)cidsBean.getProperty("exception");
        final String fehler = (String)cidsBean.getProperty("fehler");
        final Timestamp fehler_ts = (Timestamp)cidsBean.getProperty("fehler_ts");
        final String fehler_time = (fehler_ts != null) ? ("(" + DATE_FORMAT.format(fehler_ts) + ") ") : "";

        Exception ex = null;
        if (exceptionJson != null) {
            try {
                ex = MAPPER.readValue(exceptionJson, Exception.class);
            } catch (final IOException ex1) {
                LOG.error(ex1, ex1);
            }
        }

        final ErrorInfo info = new ErrorInfo(
                fehler_time
                        + "Fehler beim Bearbeiten",
                fehler,
                null,
                null,
                ex,
                Level.SEVERE,
                null);
        JXErrorPane.showDialog(this, info);
    } //GEN-LAST:event_hlStatusErrorDetailsActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdReloadActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdReloadActionPerformed
        StaticSwingTools.showDialog(new FSReloadProduktDialog(cidsBean, getConnectionContext()));
    }                                                                             //GEN-LAST:event_cmdReloadActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jButton2ActionPerformed
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(this)) {
            final Download download = FsBestellungReportGenerator.createJasperDownload(
                    cidsBean,
                    DownloadManagerDialog.getInstance().getJobName());
            DownloadManager.instance().add(download);
        }
    }                                                                            //GEN-LAST:event_jButton2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cmdAttachBillingActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cmdAttachBillingActionPerformed
        StaticSwingTools.showDialog(new FSAttachBillingForProduktDialog(cidsBean, getConnectionContext()));
    }                                                                                    //GEN-LAST:event_cmdAttachBillingActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink1ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink1ActionPerformed
        final String filePath = (String)cidsBean.getProperty("rechnung_dateipfad");
        final String fileName = (String)cidsBean.getProperty("rechnung_dateiname_orig");
        if (filePath != null) {
            try {
                final MetaObject mo = getCidsBean().getMetaObject();
                final MetaObjectNode mon = new MetaObjectNode(mo.getDomain(), mo.getId(), mo.getClassID());

                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(Fs_bestellungRenderer.this)) {
                    final String path = DownloadManagerDialog.getInstance().getJobName();
                    final Download download = new ByteArrayActionDownload(
                            FormSolutionDownloadBestellungAction.TASK_NAME,
                            mon,
                            new ServerActionParameter[] {
                                new ServerActionParameter<>(
                                    FormSolutionDownloadBestellungAction.Parameter.TYPE.toString(),
                                    FormSolutionDownloadBestellungAction.Type.RECHNUNG)
                            },
                            "Rechnung: "
                                    + title,
                            path,
                            fileName,
                            ".pdf",
                            getConnectionContext());
                    DownloadManager.instance().add(download);
                }
            } catch (final Exception ex) {
                LOG.error(ex, ex);
            }
        }
    } //GEN-LAST:event_jXHyperlink1ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void jXHyperlink2ActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_jXHyperlink2ActionPerformed

        final String url = "https://www.wuppertal.de/kartendownload/index.php?tid="
                    + (String)cidsBean.getProperty("transid");
        try {
            BrowserLauncher.openURL(url);
        } catch (final Exception ex) {
            final ErrorInfo info = new ErrorInfo(
                    "Fehler beim Öffnen der URL",
                    ex.getMessage(),
                    null,
                    null,
                    ex,
                    Level.SEVERE,
                    null);
            JXErrorPane.showDialog(this, info);
        }
    } //GEN-LAST:event_jXHyperlink2ActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBerechtigungspruefungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBerechtigungspruefungActionPerformed
        ComponentRegistry.getRegistry()
                .getDescriptionPane()
                .gotoMetaObjectNode(new MetaObjectNode((CidsBean)cidsBean.getProperty("berechtigungspruefung")));
    }                                                                                           //GEN-LAST:event_hlBerechtigungspruefungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBuchungsblattValueActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBuchungsblattValueActionPerformed
        if (buchungsblattMon != null) {
            ComponentRegistry.getRegistry().getDescriptionPane().gotoMetaObjectNode(buchungsblattMon, false);
        }
    }                                                                                        //GEN-LAST:event_hlBuchungsblattValueActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isProduktTooOld() {
        if ((cidsBean != null) && (cidsBean.getProperty("produkt_ts") != null)) {
            final Timestamp produktTs = (Timestamp)cidsBean.getProperty("produkt_ts");
            final Date now = new Date();
            final long daysOld = (now.getTime() - produktTs.getTime()) / (1000 * 60 * 60 * 24);
            return daysOld > 30;
        }
        return false;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        bindingGroup.unbind();
        setFlurstueckMon(null);
        this.cidsBean = cidsBean;

        lblStatusValue.setText("-");
        hlFlurstueckeValue.setText("-");
        hlBuchungsblattValue.setText("-");
        hlProduktValue.setText("-");
        hlStatusErrorDetails.setVisible(false);
        lblBuchungsblatt.setVisible(false);
        hlBuchungsblattValue.setVisible(false);

        if (cidsBean != null) {
            final String statusText;
            final Boolean erledigt = (Boolean)cidsBean.getProperty("erledigt");
            final Boolean postweg = (Boolean)cidsBean.getProperty("postweg");
            final String fehler = (String)cidsBean.getProperty("fehler");
            final String type = (String)cidsBean.getProperty("fk_produkt.fk_typ.key");

            if (fehler != null) {
                statusText = "Fehler: " + fehler;
            } else if (Boolean.TRUE.equals(erledigt)) {
                statusText = "erledigt";
            } else if ("LK.".startsWith(type) || ("BAB".equals(type) && Boolean.TRUE.equals(postweg))) {
                statusText = "in Bearbeitung";
            } else if ("BAB".equals(type) && Boolean.FALSE.equals(postweg)) {
                statusText = "warten auf Berechtigungsprüfung";
            } else {
                statusText = "-";
            }

            lblGebuehr.setVisible(!"BAB_WEITERLEITUNG".equals(type));
            jPanel7.setVisible(!"BAB_WEITERLEITUNG".equals(type));
            lblGebuehrDownload.setVisible("BAB_WEITERLEITUNG".equals(type));
            lblGebuehrDownloadValue.setVisible("BAB_WEITERLEITUNG".equals(type));
            lblGebuehrPostweg.setVisible("BAB_WEITERLEITUNG".equals(type));
            lblGebuehrPostwegValue.setVisible("BAB_WEITERLEITUNG".equals(type));

            lblStatusValue.setText(statusText);

            hlStatusErrorDetails.setVisible(fehler != null);

            final String landparcelcodesString = (String)cidsBean.getProperty("landparcelcode");
            final String landparcelcode;
            if ((landparcelcodesString != null) && !landparcelcodesString.isEmpty()) {
                final String[] landparcelcodes = landparcelcodesString.split(",");
                landparcelcode = landparcelcodes[0];
                hlFlurstueckeValue.setText(landparcelcode + ((landparcelcodes.length > 1) ? " u.a." : ""));
            } else {
                landparcelcode = null;
            }

            final String buchngsblattString = (String)cidsBean.getProperty("buchungsblattcode");
            final String buchungsblatt;

            if ((buchngsblattString != null) && !buchngsblattString.isEmpty()) {
                final String[] buchungsblaetter = buchngsblattString.split(",");
                buchungsblatt = buchungsblaetter[0];
                hlBuchungsblattValue.setText(buchungsblatt + ((buchungsblaetter.length > 1) ? " u.a." : ""));
            } else {
                buchungsblatt = null;
            }

            if ((type != null) && type.startsWith("LK.")) {
                hlProduktValue.setText(cidsBean.getProperty("fk_produkt.fk_typ.name") + ", "
                            + cidsBean.getProperty("fk_produkt.fk_format.format") + ", 1:"
                            + cidsBean.getProperty("massstab"));
            } else if ("BAB".equals(type)) {
                hlProduktValue.setText("Baulastbescheinigung");
            } else if ("bekom".equals(type) || "fsuekom".equals(type)) {
                hlProduktValue.setText("Liegenschaftsbuchauszug");
            } else {
                hlProduktValue.setText("-");
            }

            final Geometry geom = (Geometry)cidsBean.getProperty("geometrie.geo_field");
            if (geom != null) {
                initMap(geom);
            }

            new SwingWorker<MetaObjectNode, Void>() {

                    @Override
                    protected MetaObjectNode doInBackground() throws Exception {
                        final CidsAlkisSearchStatement search = new CidsAlkisSearchStatement(
                                CidsAlkisSearchStatement.Resulttyp.FLURSTUECK,
                                CidsAlkisSearchStatement.SucheUeber.FLURSTUECKSNUMMER,
                                landparcelcode,
                                null);
                        final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                    .customServerSearch(SessionManager.getSession().getUser(),
                                        search,
                                        getConnectionContext());
                        if (!mons.isEmpty()) {
                            return mons.iterator().next();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            setFlurstueckMon(get());
                        } catch (final Exception ex) {
                            LOG.warn(ex, ex);
                        }
                    }
                }.execute();

            if ((type != null) && (type.startsWith("bekom") || type.startsWith("fsuekom"))) {
                lblBuchungsblatt.setVisible(true);
                hlBuchungsblattValue.setVisible(true);

                new SwingWorker<MetaObjectNode, Void>() {

                        @Override
                        protected MetaObjectNode doInBackground() throws Exception {
                            final CidsAlkisSearchStatement search = new CidsAlkisSearchStatement(
                                    CidsAlkisSearchStatement.Resulttyp.BUCHUNGSBLATT,
                                    CidsAlkisSearchStatement.SucheUeber.BUCHUNGSBLATTNUMMER,
                                    buchungsblatt,
                                    null);
                            final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                                        .customServerSearch(SessionManager.getSession().getUser(),
                                            search,
                                            getConnectionContext());
                            if (!mons.isEmpty()) {
                                return mons.iterator().next();
                            } else {
                                return null;
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                setBuchungsblattMon(get());
                            } catch (final Exception ex) {
                                LOG.warn(ex, ex);
                            }
                        }
                    }.execute();
            }

            final boolean lieferAlternativ = cidsBean.getProperty("fk_adresse_versand.alternativ") != null;
            lblLaAdresse.setVisible(lieferAlternativ);
            jScrollPane1.setVisible(lieferAlternativ);
            lblLaStrasse.setVisible(!lieferAlternativ);
            lblLaStrasseValue.setVisible(!lieferAlternativ);
            lblLaOrt.setVisible(!lieferAlternativ);
            lblLaOrtValue.setVisible(!lieferAlternativ);

            final boolean rechnungAlternativ = cidsBean.getProperty("fk_adresse_rechnung.alternativ") != null;
            lblAdresse.setVisible(rechnungAlternativ);
            jScrollPane2.setVisible(rechnungAlternativ);
            lblStrasse.setVisible(!rechnungAlternativ);
            lblStrasseValue.setVisible(!rechnungAlternativ);
            lblOrt.setVisible(!rechnungAlternativ);
            lblOrtValue.setVisible(!rechnungAlternativ);
        }

        bindingGroup.bind();
        if (((cidsBean != null) && (cidsBean.getProperty("fk_billing") != null))
                    || ((cidsBean != null) && (cidsBean.getProperty("fk_produkt.fk_typ.billing") != null)
                        && !(Boolean)cidsBean.getProperty("fk_produkt.fk_typ.billing"))) {
            cmdAttachBilling.setVisible(false);
        } else {
            cmdAttachBilling.setVisible(true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstueckMon  DOCUMENT ME!
     */
    private void setFlurstueckMon(final MetaObjectNode flurstueckMon) {
        this.flurstueckMon = flurstueckMon;
        cmdReload.setEnabled(flurstueckMon != null);
        hlFlurstueckeValue.setEnabled(flurstueckMon != null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  buchungsblattMon  flurstueckMon DOCUMENT ME!
     */
    private void setBuchungsblattMon(final MetaObjectNode buchungsblattMon) {
        this.buchungsblattMon = buchungsblattMon;
        hlBuchungsblattValue.setEnabled(buchungsblattMon != null);
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
}
