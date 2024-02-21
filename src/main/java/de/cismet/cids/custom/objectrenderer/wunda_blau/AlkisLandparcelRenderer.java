/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Alkis_pointRenderer.java
 *
 * Created on 10.09.2009, 15:52:16
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.vividsolutions.jts.geom.Geometry;

import de.aedsicad.aaaweb.rest.model.LandParcel;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.jdesktop.swingx.graphics.ReflectionRenderer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import de.cismet.cids.custom.clientutils.BaulastBescheinigungDialog;
import de.cismet.cids.custom.objectrenderer.utils.AlphanumComparator;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisProducts;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.DownloadInfoFactory;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisEinzelnachweisDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.katasterauszug.BerechtigungspruefungAlkisKarteDownloadInfo;
import de.cismet.cids.custom.utils.billing.BillingProductGroupAmount;

import de.cismet.cids.dynamics.CidsBean;

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
import de.cismet.tools.StaticDebuggingTools;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.TitleComponentProvider;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class AlkisLandparcelRenderer extends javax.swing.JPanel implements BorderProvider,
    CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider,
    RequestsFullSizeComponent,
    ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ICON_RES_PACKAGE = "/de/cismet/cids/custom/wunda_blau/res/";
    private static final String ALKIS_RES_PACKAGE = ICON_RES_PACKAGE + "alkis/";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisLandparcelRenderer.class);
    private static final String CARD_1 = "CARD_1";
    private static final String CARD_2 = "CARD_2";
    //
    private static final String PRODUCT_ACTION_TAG_KARTE = "custom.alkis.product.karte@WUNDA_BLAU";
    private static final String PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_ENABLED =
        "baulast.report.bescheinigung_enabled@WUNDA_BLAU";
    private static final String PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_DISABLED =
        "baulast.report.bescheinigung_disabled@WUNDA_BLAU";
    //

    /**
     * <editor-fold defaultstate="collapsed" desc="Border- and Titleprovider method implementations">.
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getTitleComponent() {
        return panTitle;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public JComponent getFooterComponent() {
        return panFooter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the landparcel
     */
    public Object getLandparcel() {
        return landparcel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getTitleBorder() {
        return new EmptyBorder(10, 10, 10, 10);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getFooterBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Border getCenterrBorder() {
        return new EmptyBorder(5, 5, 5, 5);
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
        if (!eigentuemerPanel.isContinueInBackground()) {
            eigentuemerPanel.cancelWorker();
            setWaiting(false);
        }
        map.dispose();
    }
// </editor-fold>

    //~ Instance fields --------------------------------------------------------

    private boolean eigentuemerPermission;
    private final boolean demoMode = StaticDebuggingTools.checkHomeForFile("demoMode");
    private ImageIcon BUCH_PDF;
    private ImageIcon BUCH_HTML;
    private ImageIcon BUCH_EIG_NRW_PDF;
    private ImageIcon BUCH_EIG_NRW_HTML;
    private ImageIcon BUCH_EIG_KOM_PDF;
    private ImageIcon BLA_BESCH_PDF;
    private ImageIcon BUCH_EIG_KOM_HTML;
    private ImageIcon KARTE_PDF;
    private final Map<Object, ImageIcon> productPreviewImages;
    private CardLayout cardLayout;
    private MappingComponent map;
    private LandParcel landparcel;
    private CidsBean cidsBean;
    private String title;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXBusyLabel blWait;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisEigentuemerPanel eigentuemerPanel;
    private javax.swing.JEditorPane epLage;
    private org.jdesktop.swingx.JXHyperlink hlBaulastbescheinigung;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisKomHtml;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisKomInternHtml;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisKomInternPdf;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisKomPdf;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisNrwHtml;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksEigentumsnachweisNrwPdf;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksnachweisHtml;
    private org.jdesktop.swingx.JXHyperlink hlFlurstuecksnachweisPdf;
    private org.jdesktop.swingx.JXHyperlink hlKarte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblDescGemarkung;
    private javax.swing.JLabel lblDescGemeinde;
    private javax.swing.JLabel lblDescGroesse;
    private javax.swing.JLabel lblDescLage;
    private javax.swing.JLabel lblDescLandparcelCode;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblGemarkung;
    private javax.swing.JLabel lblGemeinde;
    private javax.swing.JLabel lblGroesse;
    private javax.swing.JLabel lblLandparcelCode;
    private javax.swing.JLabel lblPreviewHead;
    private javax.swing.JLabel lblProductPreview;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JPanel panBuchungEigentum;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panDescription;
    private javax.swing.JPanel panFlurstueckMap;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private javax.swing.JPanel panHtmlProducts;
    private javax.swing.JPanel panMainInfo;
    private javax.swing.JPanel panPdfProducts;
    private javax.swing.JPanel panProductPreview;
    private javax.swing.JPanel panProducts;
    private javax.swing.JPanel panSpacing;
    private javax.swing.JPanel panTitle;
    private javax.swing.JScrollPane scpLage;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel1;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel2;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisLandparcelRenderer object.
     */
    public AlkisLandparcelRenderer() {
        productPreviewImages = TypeSafeCollections.newHashMap();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        eigentuemerPermission = AlkisProductDownloadHelper.validateUserHasEigentuemerAccess(getConnectionContext());

        initIcons();
        initComponents();
        eigentuemerPanel.initWithConnectionContext(connectionContext);
        initFooterElements();
        initProductPreview();

        scpLage.getViewport().setOpaque(false);
        blWait.setVisible(false);
        final LayoutManager layoutManager = getLayout();
        if (layoutManager instanceof CardLayout) {
            cardLayout = (CardLayout)layoutManager;
            cardLayout.show(this, CARD_1);
        } else {
            cardLayout = new CardLayout();
            LOG.error("Alkis_landparcelRenderer exspects CardLayout as major layout manager, but has " + getLayout()
                        + "!");
        }

        epLage.addHyperlinkListener(new HyperlinkListener() {

                @Override
                public void hyperlinkUpdate(final HyperlinkEvent e) {
                    if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        final CidsBean adresse = null; // gotoBeanMap.get(e.getDescription());
                        if (adresse != null) {
                            eigentuemerPanel.setContinueInBackground(true);
                            ComponentRegistry.getRegistry()
                                    .getDescriptionPane()
                                    .gotoMetaObject(adresse.getMetaObject(), "");
                        } else {
                            LOG.warn("Could not find adress bean in gotoMap");
                        }
                    }
                }
            });
        map = new MappingComponent();
        panFlurstueckMap.add(map, BorderLayout.CENTER);
        initEditorPanes();
        if (!AlkisProductDownloadHelper.validateUserHasAlkisProductAccess(getConnectionContext())) {
            // disable Product page if user does not have the right to see it.
            btnForward.setEnabled(false);
            lblForw.setEnabled(false);
        }
        if (!eigentuemerPermission) {
            panBuchungEigentum.setVisible(false);
        }
        panHtmlProducts.setVisible(AlkisProductDownloadHelper.validateUserHasAlkisHTMLProductAccess(
                getConnectionContext()));

        final boolean billingAllowedFsueKom = BillingPopup.isBillingAllowed("fsuekom", getConnectionContext());
        final boolean billingAllowedFsueNw = BillingPopup.isBillingAllowed("fsuenw", getConnectionContext());
        final boolean billingAllowedFsNw = BillingPopup.isBillingAllowed("fsnw", getConnectionContext());
        final boolean billingAllowedBlabBe = BillingPopup.isBillingAllowed("blab_be", getConnectionContext());

        hlKarte.setEnabled(ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_KARTE, getConnectionContext()));
        hlFlurstuecksnachweisPdf.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_PDF)),
                getConnectionContext()) && billingAllowedFsNw);
        hlFlurstuecksnachweisHtml.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_HTML)),
                getConnectionContext()));
        hlFlurstuecksEigentumsnachweisKomPdf.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF)),
                getConnectionContext()) && billingAllowedFsueKom);
        hlFlurstuecksEigentumsnachweisKomHtml.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML)),
                getConnectionContext()));
        hlFlurstuecksEigentumsnachweisKomInternPdf.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF)),
                getConnectionContext()));
        hlFlurstuecksEigentumsnachweisKomInternHtml.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML)),
                getConnectionContext()));
        hlFlurstuecksEigentumsnachweisNrwPdf.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF)),
                getConnectionContext()) && billingAllowedFsueNw);
        hlFlurstuecksEigentumsnachweisNrwHtml.setEnabled(ObjectRendererUtils.checkActionTag(
                ClientAlkisProducts.getInstance().getActionTag(
                    ClientAlkisProducts.getInstance().get(
                        ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML)),
                getConnectionContext()));
        hlBaulastbescheinigung.setEnabled(
            ObjectRendererUtils.checkActionTag(
                PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_ENABLED,
                getConnectionContext())
                    && !ObjectRendererUtils.checkActionTag(
                        PRODUCT_ACTION_TAG_BAULASTBESCHEINIGUNG_DISABLED,
                        getConnectionContext())
                    && billingAllowedBlabBe);
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     */
    private void initIcons() {
        final ReflectionRenderer reflectionRenderer = new ReflectionRenderer(0.5f, 0.15f, false);
//        BACKWARD_SELECTED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-left-sel.png"));
//        BACKWARD_PRESSED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-left-pressed.png"));
//
//        FORWARD_SELECTED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-right-sel.png"));
//        FORWARD_PRESSED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-right-pressed.png"));
        BufferedImage i1 = null;
        BufferedImage i2 = null;
        BufferedImage i3 = null;
        BufferedImage i4 = null;
        BufferedImage i5 = null;
        BufferedImage i6 = null;
        BufferedImage i7 = null;
        BufferedImage i8 = null;
        try {
            // TODO: Richtige Screenshots machen und zuordnen!
            i1 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "buchnachweispdf.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i2 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "buchnachweishtml.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i3 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "bucheignachweispdf.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i4 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "bucheignachweishtml.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i5 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "karte.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i6 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "buchnachweispdf.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i7 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "buchnachweishtml.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        try {
            i8 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "baulastbescheinigungpdf.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        BUCH_PDF = new ImageIcon(i1);
        BUCH_HTML = new ImageIcon(i2);
        BUCH_EIG_NRW_PDF = new ImageIcon(i3);
        BUCH_EIG_NRW_HTML = new ImageIcon(i4);
        KARTE_PDF = new ImageIcon(i5);
        BUCH_EIG_KOM_PDF = new ImageIcon(i6);
        BUCH_EIG_KOM_HTML = new ImageIcon(i7);
        BLA_BESCH_PDF = new ImageIcon(i8);
    }

    /**
     * DOCUMENT ME!
     */
    private void initProductPreview() {
        initProductPreviewImages();
        int maxX = 0;
        int maxY = 0;
        for (final ImageIcon ii : productPreviewImages.values()) {
            if (ii.getIconWidth() > maxX) {
                maxX = ii.getIconWidth();
            }
            if (ii.getIconHeight() > maxY) {
                maxY = ii.getIconHeight();
            }
        }
        final Dimension previewDim = new Dimension(maxX + 20, maxY + 40);
        ObjectRendererUtils.setAllDimensions(panProductPreview, previewDim);
    }

    /**
     * DOCUMENT ME!
     */
    private void initEditorPanes() {
        // Font and Layout
        final Font font = UIManager.getFont("Label.font");
        final String bodyRule = "body { font-family: " + font.getFamily() + "; "
                    + "font-size: " + font.getSize() + "pt; }";
        final StyleSheet css2 = ((HTMLEditorKit)epLage.getEditorKit()).getStyleSheet();
        css2.addRule(bodyRule);
    }

    /**
     * DOCUMENT ME!
     */
    private void initProductPreviewImages() {
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisNrwPdf, BUCH_EIG_NRW_PDF);
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisKomPdf, BUCH_EIG_KOM_PDF);
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisKomInternPdf, BUCH_EIG_KOM_PDF);
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisNrwHtml, BUCH_EIG_NRW_HTML);
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisKomHtml, BUCH_EIG_KOM_HTML);
        productPreviewImages.put(hlFlurstuecksEigentumsnachweisKomInternHtml, BUCH_EIG_KOM_HTML);
        productPreviewImages.put(hlFlurstuecksnachweisHtml, BUCH_HTML);
        productPreviewImages.put(hlFlurstuecksnachweisPdf, BUCH_PDF);
        productPreviewImages.put(hlKarte, KARTE_PDF);
        productPreviewImages.put(hlBaulastbescheinigung, BLA_BESCH_PDF);
        final ProductLabelMouseAdaper productListener = new ProductLabelMouseAdaper();
        hlFlurstuecksEigentumsnachweisNrwPdf.addMouseListener(productListener);
        hlFlurstuecksEigentumsnachweisKomPdf.addMouseListener(productListener);
        hlFlurstuecksEigentumsnachweisNrwHtml.addMouseListener(productListener);
        hlFlurstuecksEigentumsnachweisKomHtml.addMouseListener(productListener);
        hlFlurstuecksEigentumsnachweisKomInternPdf.addMouseListener(productListener);
        hlFlurstuecksEigentumsnachweisKomInternHtml.addMouseListener(productListener);
        hlFlurstuecksnachweisHtml.addMouseListener(productListener);
        hlFlurstuecksnachweisPdf.addMouseListener(productListener);
        hlKarte.addMouseListener(productListener);
        hlBaulastbescheinigung.addMouseListener(productListener);
    }
    /**
     * DOCUMENT ME!
     *
     * @param  waiting  DOCUMENT ME!
     */
    private void setWaiting(final boolean waiting) {
        blWait.setVisible(waiting);
        blWait.setBusy(waiting);
    }

    /**
     * DOCUMENT ME!
     */
    private void initFooterElements() {
        ObjectRendererUtils.decorateJLabelAndButtonSynced(
            lblForw,
            btnForward,
            ObjectRendererUtils.FORWARD_SELECTED,
            ObjectRendererUtils.FORWARD_PRESSED);
        ObjectRendererUtils.decorateJLabelAndButtonSynced(
            lblBack,
            btnBack,
            ObjectRendererUtils.BACKWARD_SELECTED,
            ObjectRendererUtils.BACKWARD_PRESSED);
//        ObjectRendererUtils.decorateJLabelAndButtonSynced(lblForw, btnForward, FORWARD_SELECTED, FORWARD_PRESSED);
//        ObjectRendererUtils.decorateJLabelAndButtonSynced(lblBack, btnBack, BACKWARD_SELECTED, BACKWARD_PRESSED);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  product                DOCUMENT ME!
     * @param  berechtigungspruefung  DOCUMENT ME!
     */
    private void downloadEinzelnachweisProduct(final String product, final boolean berechtigungspruefung) {
        if (
            !ObjectRendererUtils.checkActionTag(
                        ClientAlkisProducts.getInstance().getActionTag(product),
                        getConnectionContext())) {
            AlkisProductDownloadHelper.showNoProductPermissionWarning(this);
            return;
        }

        try {
            final List<String> parcelCodes = Arrays.asList(AlkisProducts.getLandparcelCodeFromParcelBeanObject(
                        cidsBean));

            final BerechtigungspruefungAlkisEinzelnachweisDownloadInfo downloadInfo = DownloadInfoFactory
                        .createBerechtigungspruefungAlkisEinzelnachweisDownloadInfo(
                            product,
                            parcelCodes);
            final String billingKey = ClientAlkisProducts.getInstance().getBillingKey(product);
            if ((billingKey == null)
                        || BillingPopup.doBilling(
                            billingKey,
                            "no.yet",
                            (Geometry)null,
                            (berechtigungspruefung
                                && AlkisProductDownloadHelper.checkBerechtigungspruefung(
                                    downloadInfo.getProduktTyp(),
                                    getConnectionContext())) ? downloadInfo : null,
                            getConnectionContext(),
                            new BillingProductGroupAmount("ea", 1))) {
                AlkisProductDownloadHelper.downloadEinzelnachweisProduct(downloadInfo, getConnectionContext());
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void downloadKarteProduct() {
        if (!ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_KARTE, getConnectionContext())) {
            AlkisProductDownloadHelper.showNoProductPermissionWarning(this);
            return;
        }

        final List<String> parcelCodes = Arrays.asList(AlkisProducts.getLandparcelCodeFromParcelBeanObject(
                    cidsBean));

        final BerechtigungspruefungAlkisKarteDownloadInfo downloadInfo = DownloadInfoFactory
                    .createBerechtigungspruefungAlkisKarteDownloadInfo(parcelCodes);
        AlkisProductDownloadHelper.downloadKarteProduct(downloadInfo, getConnectionContext());
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
        blWait = new org.jdesktop.swingx.JXBusyLabel();
        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        panDescription = new javax.swing.JPanel();
        panBuchungEigentum = new RoundedPanel();
        semiRoundedPanel1 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        eigentuemerPanel = new de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisEigentuemerPanel();
        panMainInfo = new RoundedPanel();
        lblLandparcelCode = new javax.swing.JLabel();
        lblDescLandparcelCode = new javax.swing.JLabel();
        lblDescGemeinde = new javax.swing.JLabel();
        lblGemeinde = new javax.swing.JLabel();
        lblDescGemarkung = new javax.swing.JLabel();
        lblGemarkung = new javax.swing.JLabel();
        lblDescLage = new javax.swing.JLabel();
        lblGroesse = new javax.swing.JLabel();
        lblDescGroesse = new javax.swing.JLabel();
        scpLage = new javax.swing.JScrollPane();
        epLage = new javax.swing.JEditorPane();
        semiRoundedPanel2 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        panFlurstueckMap = new javax.swing.JPanel();
        panProducts = new javax.swing.JPanel();
        panPdfProducts = new RoundedPanel();
        hlKarte = new org.jdesktop.swingx.JXHyperlink();
        hlFlurstuecksEigentumsnachweisNrwPdf = new org.jdesktop.swingx.JXHyperlink();
        hlFlurstuecksnachweisPdf = new org.jdesktop.swingx.JXHyperlink();
        jPanel1 = new javax.swing.JPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        hlFlurstuecksEigentumsnachweisKomPdf = new org.jdesktop.swingx.JXHyperlink();
        hlFlurstuecksEigentumsnachweisKomInternPdf = new org.jdesktop.swingx.JXHyperlink();
        hlBaulastbescheinigung = new org.jdesktop.swingx.JXHyperlink();
        panHtmlProducts = new RoundedPanel();
        hlFlurstuecksEigentumsnachweisKomHtml = new org.jdesktop.swingx.JXHyperlink();
        hlFlurstuecksnachweisHtml = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        hlFlurstuecksEigentumsnachweisNrwHtml = new org.jdesktop.swingx.JXHyperlink();
        hlFlurstuecksEigentumsnachweisKomInternHtml = new org.jdesktop.swingx.JXHyperlink();
        panSpacing = new javax.swing.JPanel();
        panProductPreview = new RoundedPanel();
        lblProductPreview = new javax.swing.JLabel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblPreviewHead = new javax.swing.JLabel();

        panTitle.setOpaque(false);
        panTitle.setLayout(new java.awt.GridBagLayout());

        lblTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TITLE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panTitle.add(lblTitle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 5);
        panTitle.add(blWait, gridBagConstraints);

        panFooter.setOpaque(false);
        panFooter.setLayout(new java.awt.BorderLayout());

        panButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 6, 0));
        panButtons.setOpaque(false);
        panButtons.setLayout(new java.awt.GridBagLayout());

        panFooterLeft.setMaximumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setMinimumSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setOpaque(false);
        panFooterLeft.setPreferredSize(new java.awt.Dimension(124, 40));
        panFooterLeft.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));

        lblBack.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblBack.setForeground(new java.awt.Color(255, 255, 255));
        lblBack.setText("Info");
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
        panButtons.add(panFooterLeft, gridBagConstraints);

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

        lblForw.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblForw.setForeground(new java.awt.Color(255, 255, 255));
        lblForw.setText("Produkte");
        lblForw.addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mouseClicked(final java.awt.event.MouseEvent evt) {
                    lblForwMouseClicked(evt);
                }
            });
        panFooterRight.add(lblForw);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panButtons.add(panFooterRight, gridBagConstraints);

        panFooter.add(panButtons, java.awt.BorderLayout.CENTER);

        setLayout(new java.awt.CardLayout());

        panDescription.setOpaque(false);
        panDescription.setLayout(new java.awt.GridBagLayout());

        panBuchungEigentum.setLayout(new java.awt.GridBagLayout());

        semiRoundedPanel1.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(org.openide.util.NbBundle.getMessage(
                AlkisLandparcelRenderer.class,
                "AlkisEitentuemerPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panBuchungEigentum.add(semiRoundedPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panBuchungEigentum.add(eigentuemerPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        panDescription.add(panBuchungEigentum, gridBagConstraints);

        panMainInfo.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.alkis_id}"),
                lblLandparcelCode,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        panMainInfo.add(lblLandparcelCode, gridBagConstraints);

        lblDescLandparcelCode.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescLandparcelCode.setText("Flurstückskennzeichen:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panMainInfo.add(lblDescLandparcelCode, gridBagConstraints);

        lblDescGemeinde.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescGemeinde.setText("Gemeinde:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panMainInfo.add(lblDescGemeinde, gridBagConstraints);

        lblGemeinde.setText("Wuppertal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        panMainInfo.add(lblGemeinde, gridBagConstraints);

        lblDescGemarkung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescGemarkung.setText("Gemarkung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panMainInfo.add(lblDescGemarkung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.gemarkung}"),
                lblGemarkung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        panMainInfo.add(lblGemarkung, gridBagConstraints);

        lblDescLage.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescLage.setText("Lage:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panMainInfo.add(lblDescLage, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.groesse} m²"),
                lblGroesse,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        panMainInfo.add(lblGroesse, gridBagConstraints);

        lblDescGroesse.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescGroesse.setText("Größe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        panMainInfo.add(lblDescGroesse, gridBagConstraints);

        scpLage.setBorder(null);
        scpLage.setMaximumSize(new java.awt.Dimension(250, 20));
        scpLage.setMinimumSize(new java.awt.Dimension(250, 20));
        scpLage.setOpaque(false);
        scpLage.setPreferredSize(new java.awt.Dimension(250, 20));

        epLage.setEditable(false);
        epLage.setBorder(null);
        epLage.setContentType("text/html"); // NOI18N
        epLage.setOpaque(false);
        scpLage.setViewportView(epLage);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panMainInfo.add(scpLage, gridBagConstraints);

        semiRoundedPanel2.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setText("Flurstücksinformation");
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel2.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panMainInfo.add(semiRoundedPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panDescription.add(panMainInfo, gridBagConstraints);

        panFlurstueckMap.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panFlurstueckMap.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        panDescription.add(panFlurstueckMap, gridBagConstraints);

        add(panDescription, "CARD_1");

        panProducts.setOpaque(false);
        panProducts.setLayout(new java.awt.GridBagLayout());

        panPdfProducts.setOpaque(false);
        panPdfProducts.setLayout(new java.awt.GridBagLayout());

        hlKarte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlKarte.setText("Karte");
        hlKarte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlKarteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 10, 7);
        panPdfProducts.add(hlKarte, gridBagConstraints);

        hlFlurstuecksEigentumsnachweisNrwPdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisNrwPdf.setText("Flurstücks- und Eigentumsnachweis (NRW)");
        hlFlurstuecksEigentumsnachweisNrwPdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisNrwPdfActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPdfProducts.add(hlFlurstuecksEigentumsnachweisNrwPdf, gridBagConstraints);

        hlFlurstuecksnachweisPdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlFlurstuecksnachweisPdf.setText("Flurstücksnachweis");
        hlFlurstuecksnachweisPdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksnachweisPdfActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 7, 7, 7);
        panPdfProducts.add(hlFlurstuecksnachweisPdf, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panPdfProducts.add(jPanel1, gridBagConstraints);

        semiRoundedPanel4.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("PDF-Produkte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel4.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panPdfProducts.add(semiRoundedPanel4, gridBagConstraints);

        hlFlurstuecksEigentumsnachweisKomPdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisKomPdf.setText("Flurstücks- und Eigentumsnachweis (kommunal)");
        hlFlurstuecksEigentumsnachweisKomPdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisKomPdfActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPdfProducts.add(hlFlurstuecksEigentumsnachweisKomPdf, gridBagConstraints);

        hlFlurstuecksEigentumsnachweisKomInternPdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisKomInternPdf.setText("Flurstücks- und Eigentumsnachweis (kommunal, intern)");
        hlFlurstuecksEigentumsnachweisKomInternPdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisKomInternPdfActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPdfProducts.add(hlFlurstuecksEigentumsnachweisKomInternPdf, gridBagConstraints);

        hlBaulastbescheinigung.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlBaulastbescheinigung.setText("Baulastbescheinigung");
        hlBaulastbescheinigung.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlBaulastbescheinigungActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPdfProducts.add(hlBaulastbescheinigung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 9, 5);
        panProducts.add(panPdfProducts, gridBagConstraints);

        panHtmlProducts.setOpaque(false);
        panHtmlProducts.setLayout(new java.awt.GridBagLayout());

        hlFlurstuecksEigentumsnachweisKomHtml.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisKomHtml.setText("Flurstücks- und Eigentumsnachweis (kommunal)");
        hlFlurstuecksEigentumsnachweisKomHtml.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisKomHtmlActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 10, 7);
        panHtmlProducts.add(hlFlurstuecksEigentumsnachweisKomHtml, gridBagConstraints);

        hlFlurstuecksnachweisHtml.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlFlurstuecksnachweisHtml.setText("Flurstücksnachweis");
        hlFlurstuecksnachweisHtml.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksnachweisHtmlActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 7, 7, 7);
        panHtmlProducts.add(hlFlurstuecksnachweisHtml, gridBagConstraints);

        jPanel2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panHtmlProducts.add(jPanel2, gridBagConstraints);

        semiRoundedPanel5.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("HTML-Produkte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel5.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panHtmlProducts.add(semiRoundedPanel5, gridBagConstraints);

        hlFlurstuecksEigentumsnachweisNrwHtml.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisNrwHtml.setText("Flurstücks- und Eigentumsnachweis (NRW)");
        hlFlurstuecksEigentumsnachweisNrwHtml.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisNrwHtmlActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 10, 7);
        panHtmlProducts.add(hlFlurstuecksEigentumsnachweisNrwHtml, gridBagConstraints);

        hlFlurstuecksEigentumsnachweisKomInternHtml.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlFlurstuecksEigentumsnachweisKomInternHtml.setText("Flurstücks- und Eigentumsnachweis (kommunal,intern)");
        hlFlurstuecksEigentumsnachweisKomInternHtml.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlFlurstuecksEigentumsnachweisKomInternHtmlActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 10, 7);
        panHtmlProducts.add(hlFlurstuecksEigentumsnachweisKomInternHtml, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 5, 5, 5);
        panProducts.add(panHtmlProducts, gridBagConstraints);

        panSpacing.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weighty = 1.0;
        panProducts.add(panSpacing, gridBagConstraints);

        panProductPreview.setOpaque(false);
        panProductPreview.setLayout(new java.awt.BorderLayout());

        lblProductPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblProductPreview.setBorder(javax.swing.BorderFactory.createEmptyBorder(7, 7, 7, 7));
        panProductPreview.add(lblProductPreview, java.awt.BorderLayout.CENTER);

        semiRoundedPanel3.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel3.setLayout(new java.awt.GridBagLayout());

        lblPreviewHead.setText("Vorschau");
        lblPreviewHead.setForeground(new java.awt.Color(255, 255, 255));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel3.add(lblPreviewHead, gridBagConstraints);

        panProductPreview.add(semiRoundedPanel3, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panProducts.add(panProductPreview, gridBagConstraints);

        add(panProducts, "CARD_2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlKarteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlKarteActionPerformed
        if (!demoMode) {
            downloadKarteProduct();
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl() + "flurstueckskarte.pdf");
        }
    }                                                                           //GEN-LAST:event_hlKarteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksnachweisPdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksnachweisPdfActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_PDF),
                true);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksnachweis.pdf");
        }
    }                                                                                            //GEN-LAST:event_hlFlurstuecksnachweisPdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksEigentumsnachweisKomHtmlActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisKomHtmlActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML),
                false);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksnachweis.pdf");
        }
    }                                                                                                         //GEN-LAST:event_hlFlurstuecksEigentumsnachweisKomHtmlActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksnachweisHtmlActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksnachweisHtmlActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKSNACHWEIS_HTML),
                false);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksnachweis.html");
        }
    }                                                                                             //GEN-LAST:event_hlFlurstuecksnachweisHtmlActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnBackActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnBackActionPerformed
        cardLayout.show(this, CARD_1);
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
        cardLayout.show(this, CARD_2);
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
    private void hlFlurstuecksEigentumsnachweisNrwPdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisNrwPdfActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF),
                true);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksundeigentumsnachweis_nrw.pdf");
        }
    }                                                                                                        //GEN-LAST:event_hlFlurstuecksEigentumsnachweisNrwPdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksEigentumsnachweisKomPdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisKomPdfActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF),
                true);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksundeigentumsnachweis_kommunal.pdf");
        }
    }                                                                                                        //GEN-LAST:event_hlFlurstuecksEigentumsnachweisKomPdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksEigentumsnachweisNrwHtmlActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisNrwHtmlActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML),
                false);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksundeigentumsnachweis_kommunal.pdf");
        }
    }                                                                                                         //GEN-LAST:event_hlFlurstuecksEigentumsnachweisNrwHtmlActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksEigentumsnachweisKomInternPdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisKomInternPdfActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(
                ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF),
                true);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksundeigentumsnachweis_kommunal_intern.pdf");
        }
    }                                                                                                              //GEN-LAST:event_hlFlurstuecksEigentumsnachweisKomInternPdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlFlurstuecksEigentumsnachweisKomInternHtmlActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlFlurstuecksEigentumsnachweisKomInternHtmlActionPerformed
        if (!demoMode) {
            downloadEinzelnachweisProduct(
                ClientAlkisProducts.getInstance().get(
                    ClientAlkisProducts.Type.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML),
                false);
        } else {
            BrowserLauncher.openURLorFile(ClientAlkisConf.getInstance().getDemoServiceUrl()
                        + "flurstuecksundeigentumsnachweis_kommunal_intern.html");
        }
    }                                                                                                               //GEN-LAST:event_hlFlurstuecksEigentumsnachweisKomInternHtmlActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlBaulastbescheinigungActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlBaulastbescheinigungActionPerformed
        final Collection<CidsBean> flurstuecke = new ArrayList<>();
        flurstuecke.add(cidsBean);
        BaulastBescheinigungDialog.getInstance().show(flurstuecke, this, getConnectionContext());
    }                                                                                          //GEN-LAST:event_hlBaulastbescheinigungActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  cb  DOCUMENT ME!
     */
    @Override
    public void setCidsBean(final CidsBean cb) {
        bindingGroup.unbind();
        if (cb != null) {
            this.cidsBean = cb;
            initMap();
            initLage();
            bindingGroup.bind();
            if (eigentuemerPermission) {
                eigentuemerPanel.setFlurstuecke(Arrays.asList(cidsBean), new AlkisEigentuemerPanel.Listener() {

                        @Override
                        public void loadingStarted() {
                            blWait.setVisible(true);
                            setWaiting(true);
                        }

                        @Override
                        public void loadingDone() {
                            blWait.setVisible(false);
                        }
                    });
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initLage() {
        final Map<String, List<CidsBean>> streetToBeans = TypeSafeCollections.newHashMap();
        final Object adressenObj = cidsBean.getProperty("adressen");
        if (adressenObj instanceof List) {
            final List<CidsBean> adressenBeans = (List<CidsBean>)adressenObj;
            for (final CidsBean adresse : adressenBeans) {
                final Object strasseObj = adresse.getProperty("strasse");
                List<CidsBean> beansWithThisStreet;
                if (strasseObj != null) {
                    final String strasse = strasseObj.toString();
                    beansWithThisStreet = streetToBeans.get(strasse);
                    if (beansWithThisStreet == null) {
                        beansWithThisStreet = TypeSafeCollections.newArrayList();
                        streetToBeans.put(strasse, beansWithThisStreet);
                    }
                    beansWithThisStreet.add(adresse);
                }
            }
        }
        final StringBuilder adressenContent = new StringBuilder(
                "<html><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\" valign=\"top\">");
        // sort by street
        final List<String> sortStrassen = TypeSafeCollections.newArrayList(streetToBeans.keySet());
        Collections.sort(sortStrassen);
        int entryCount = sortStrassen.size();
        for (final String strasse : sortStrassen) {
            final List<CidsBean> beansWithThisStreet = streetToBeans.get(strasse);
            Collections.sort(beansWithThisStreet, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        if ((o1 != null) && (o2 != null)) {
                            final Object n1 = o1.getProperty("nummer");
                            final Object n2 = o2.getProperty("nummer");
                            return AlphanumComparator.getInstance().compare(String.valueOf(n1), String.valueOf(n2));
                        }
                        return 0;
                    }
                });

            final Map<String, CidsBean> hausnummernToBeans = TypeSafeCollections.newLinkedHashMap();
            for (final CidsBean adresse : beansWithThisStreet) {
                final Object hausnummerObj = adresse.getProperty("nummer");
                if (hausnummerObj != null) {
                    hausnummernToBeans.put(hausnummerObj.toString(), adresse);
                }
            }
            if (hausnummernToBeans.isEmpty()) {
                for (final CidsBean bean : beansWithThisStreet) {
                    adressenContent.append("<tr><td>");
                    if ((strasse != null) && !strasse.trim().matches(".*\\(\\d+\\)$")
                                && !strasse.trim().matches("^\\(\\d+\\).*")) {
                        adressenContent.append(strasse);
                    } else {
                        adressenContent.append(AlkisProducts.generateLinkFromCidsBean(bean, strasse));
                    }
                    adressenContent.append("</td></tr>");
                }
            } else {
                // allocate an extra line if number of housenumbers is big
                entryCount += (hausnummernToBeans.size() / 7);
                adressenContent.append("<tr><td>");
                adressenContent.append(strasse).append("&nbsp;");
                adressenContent.append("</td>");
                adressenContent.append("<td>");
                for (final Entry<String, CidsBean> entry : hausnummernToBeans.entrySet()) {
                    final String nummer = entry.getKey();
                    final CidsBean numberBean = entry.getValue();
                    adressenContent.append(AlkisProducts.generateLinkFromCidsBean(numberBean, nummer));
                    adressenContent.append(", ");
                }
                adressenContent.delete(adressenContent.length() - 2, adressenContent.length());
                adressenContent.append("</td>");
                adressenContent.append("</tr>");
            }
        }
        adressenContent.append("</table></html>");
        epLage.setText(adressenContent.toString());
        final int linecount = entryCount;
        if (linecount > 1) {
            if (linecount < 5) {
                ObjectRendererUtils.setAllDimensions(
                    scpLage,
                    new Dimension(scpLage.getPreferredSize().width, 20 * linecount));
            } else {
                ObjectRendererUtils.setAllDimensions(scpLage, new Dimension(scpLage.getPreferredSize().width, 100));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initMap() {
        final Object geoObj = cidsBean.getProperty("geometrie.geo_field");
        if (geoObj instanceof Geometry) {
            final Geometry pureGeom = CrsTransformer.transformToGivenCrs((Geometry)geoObj,
                    ClientAlkisConf.getInstance().getSrsService());
            final XBoundingBox box = new XBoundingBox(pureGeom.getEnvelope().buffer(
                        ClientAlkisConf.getInstance().getGeoBuffer()));

            final Runnable mapRunnable = new Runnable() {

                    @Override
                    public void run() {
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
                        map.setMappingModel(mappingModel);
                        // initial positioning of the map
                        final int duration = map.getAnimationDuration();
                        map.setAnimationDuration(0);
                        map.gotoInitialBoundingBox();
                        // interaction mode
                        map.setInteractionMode(MappingComponent.ZOOM);
                        // finally when all configurations are done ...
                        map.unlock();
                        map.addCustomInputListener("MUTE", new PBasicInputEventHandler() {

                                @Override
                                public void mouseClicked(final PInputEvent evt) {
                                    if (evt.getClickCount() > 1) {
                                        final CidsBean bean = cidsBean;
                                        ObjectRendererUtils.switchToCismapMap();
                                        ObjectRendererUtils.addBeanGeomAsFeatureToCismapMap(bean, false);
                                    }
                                }
                            });
                        map.setInteractionMode("MUTE");
                        map.getFeatureCollection().addFeature(dsf);
                        map.setAnimationDuration(duration);
                    }
                };
            if (EventQueue.isDispatchThread()) {
                mapRunnable.run();
            } else {
                EventQueue.invokeLater(mapRunnable);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  title  DOCUMENT ME!
     */
    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "<Error>";
        } else {
            title = AlkisProducts.prettyPrintLandparcelCode(title);
        }
        this.title = title;
        lblTitle.setText(this.title);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * <editor-fold defaultstate="collapsed" desc="Listeners">.
     *
     * @version  $Revision$, $Date$
     */
    class ProductLabelMouseAdaper extends MouseAdapter {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void mouseEntered(final MouseEvent e) {
            final Object srcObj = e.getSource();
            final ImageIcon imageIcon = productPreviewImages.get(srcObj);
            if (imageIcon != null) {
                lblProductPreview.setIcon(imageIcon);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  e  DOCUMENT ME!
         */
        @Override
        public void mouseExited(final MouseEvent e) {
            lblProductPreview.setIcon(null);
        }
    }
// </editor-fold>
}
