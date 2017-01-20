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

import Sirius.navigator.ui.RequestsFullSizeComponent;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

import com.vividsolutions.jts.geom.Geometry;

import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.util.Point;
import de.aedsicad.aaaweb.service.util.PointLocation;

import org.jdesktop.beansbinding.Converter;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.graphics.ReflectionRenderer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import org.openide.util.Exceptions;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import javax.media.jai.RenderedImageAdapter;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.alkis.AlkisPunktReportScriptlet;
import de.cismet.cids.custom.utils.alkis.AlkisSOAPWorkerService;
import de.cismet.cids.custom.utils.alkis.SOAPAccessProvider;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.measuring.MeasuringComponent;

import de.cismet.security.WebAccessManager;

import de.cismet.security.exceptions.AccessMethodIsNotSupportedException;
import de.cismet.security.exceptions.MissingArgumentException;
import de.cismet.security.exceptions.NoHandlerForURLException;
import de.cismet.security.exceptions.RequestFailedException;

import de.cismet.tools.CismetThreadPool;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.BorderProvider;
import de.cismet.tools.gui.FooterComponentProvider;
import de.cismet.tools.gui.RoundedPanel;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.TitleComponentProvider;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class AlkisPointRenderer extends javax.swing.JPanel implements CidsBeanRenderer,
    TitleComponentProvider,
    FooterComponentProvider,
    BorderProvider,
    RequestsFullSizeComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ICON_RES_PACKAGE = "/de/cismet/cids/custom/wunda_blau/res/";
    private static final String ALKIS_RES_PACKAGE = ICON_RES_PACKAGE + "alkis/";
    private static final String CARD_1 = "CARD_1";
    private static final String CARD_2 = "CARD_2";
    private static final String CARD_PREVIEW = "preview";
    private static final String CARD_APMAP = "apmap";
    private static final String ERHEBUNGS_PROPERTIES = "datenerhebung.properties";
    static final String PRODUCT_ACTION_TAG_PUNKTLISTE = "custom.alkis.product.punktliste@WUNDA_BLAU";
//    private ImageIcon FORWARD_PRESSED;
//    private ImageIcon FORWARD_SELECTED;
//    private ImageIcon BACKWARD_PRESSED;
//    private ImageIcon BACKWARD_SELECTED;
    private static final Color PUNKTORT_MIT_KARTENDARSTELLUNG = new Color(120, 255, 190);
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlkisPointRenderer.class);
    protected static final String POINTTYPE_AUFNAHMEPUNKT = "Aufnahmepunkt";
    protected static final String POINTTYPE_SONSTIGERVERMESSUNGSPUNKT = "Sonstiger Vermessungspunkt";
    protected static XBoundingBox INITIAL_BOUNDINGBOX = new XBoundingBox(
            2583621.251964098d,
            5682507.032498134d,
            2584022.9413952776d,
            5682742.852810634d,
            AlkisConstants.COMMONS.SRS_SERVICE,
            true);
    protected static Crs CRS = new Crs(
            AlkisConstants.COMMONS.SRS_SERVICE,
            AlkisConstants.COMMONS.SRS_SERVICE,
            AlkisConstants.COMMONS.SRS_SERVICE,
            true,
            true);
    private static final Converter<String, String> ALKIS_BOOLEAN_CONVERTER = new Converter<String, String>() {

            private static final String TRUE_REP = "Ja";
            private static final String FALSE_REP = "Nein";

            @Override
            public String convertForward(final String s) {
                if ((s != null) && s.equals("1")) {
                    return TRUE_REP;
                } else {
                    return FALSE_REP;
                }
            }

            @Override
            public String convertReverse(final String t) {
                if (TRUE_REP.equals(t)) {
                    return "1";
                } else {
                    return "0";
                }
            }
        };

    private static final Converter<String, String> ALKIS_ERHEBUNG_CONVERTER = new Converter<String, String>() {

            // anonymous constructor
            {
                this.datenerhebungWerte = new Properties();
                try {
                    this.datenerhebungWerte.load(getClass().getResource(ALKIS_RES_PACKAGE + ERHEBUNGS_PROPERTIES)
                                .openStream());
                } catch (Exception ex) {
                    LOG.error(ex, ex);
                }
            }

            private final Properties datenerhebungWerte;

            @Override
            public String convertForward(final String s) {
                String descr = "keine Angabe (-)";
                String searchKey = "0000";
                if (s != null) {
                    try {
                        final SAXBuilder builder = new SAXBuilder();

                        final Reader in = new StringReader(s);
                        final Document doc = builder.build(in);
                        final Element root = doc.getRootElement();
                        final ElementFilter filter = new ElementFilter("AX_Datenerhebung_Punktort");
                        final Iterator it = root.getDescendants(filter);
                        while (it.hasNext()) {
                            final Element c = (Element)it.next();
                            searchKey = c.getTextNormalize();
                        }
                        // lookup description for code
                        descr = datenerhebungWerte.getProperty(searchKey);
                        if (descr != null) {
                            // return result + format with html (max. column length)
                            return "<html><table width=\"300\" border=\"0\"><tr><td>(" + searchKey + ") " + descr
                                        + "</tr></table></html>";
                        } else {
                            LOG.warn("No description found for Erhebung with key: " + searchKey);
                        }
                    } catch (Exception e) {
                        LOG.error("Error in converter", e);
                    }

                    LOG.warn("Could not translate response: " + s);
                }
                return "keine Angabe";
            }

            @Override
            public String convertReverse(final String t) {
                throw new UnsupportedOperationException("Will not be supported!");
            }
        };

    private static final Converter<CidsBean, String> ALKIS_VERMARKUNG_CONVERTER = new Converter<CidsBean, String>() {

            @Override
            public String convertForward(final CidsBean bean) {
                Object markenObj = bean.getProperty("abmarkung");
                if (markenObj == null) {
                    markenObj = bean.getProperty("vermarkung");
                }
                return (markenObj == null) ? null : markenObj.toString();
            }

            @Override
            public CidsBean convertReverse(final String vermarkungText) {
                throw new UnsupportedOperationException("Will/Can not be supported!");
            }
        };

    private static final Comparator<PointLocation> POINTLOCATION_COMPARATOR = new Comparator<PointLocation>() {

            @Override
            public int compare(final PointLocation p1, final PointLocation p2) {
                final int result = compareKartendarstellung(p1, p2);
                if (result != 0) {
                    // descending order
                    return -result;
                } else {
                    // descending order
                    return -compareDate(p1, p2);
                }
            }

            private int compareKartendarstellung(final PointLocation p1, final PointLocation p2) {
                final String kd1 = p1.getKartendarstellung();
                final String kd2 = p2.getKartendarstellung();
                if (kd1 != kd2) {
                    if (kd1 != null) {
                        if (kd2 != null) {
                            return kd1.compareTo(kd2);
                        } else {
                            return 1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            }

            private int compareDate(final PointLocation p1, final PointLocation p2) {
                final String lz1 = p1.getLebenszeitIntervallBeginnt();
                final String lz2 = p2.getLebenszeitIntervallBeginnt();
                if (lz1 != lz2) {
                    if (lz1 != null) {
                        if (lz2 != null) {
                            if ((lz1.length() > 9) && (lz2.length() > 9)) {
                                // 10 = length of YYYY-MM-DD
                                return compareDateStrings(lz1.substring(0, 11), lz2.substring(0, 11));
                            } else {
                                throw new IllegalStateException("Could not parse Dates: " + lz1 + " or " + lz2);
                            }
                        } else {
                            return 1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            }

            private int compareDateStrings(final String ds1, final String ds2) {
                final String[] ymd1 = ds1.split("-");
                final String[] ymd2 = ds2.split("-");
                if ((ymd1.length == 3) && (ymd2.length == 3)) {
                    int result = 0;
                    for (int i = 0; (i < 3) && (result == 0); ++i) {
                        result = ymd1[i].compareTo(ymd2[i]);
                    }
                    return result;
                } else {
                    throw new IllegalStateException("Could not parse Dates: " + ds1 + " or " + ds2);
                }
            }
        };

    //~ Instance fields --------------------------------------------------------

    private final Map<Object, ImageIcon> productPreviewImages;
    private final List<JLabel> retrieveableLabels;
    private SOAPAccessProvider soapProvider;
    private ALKISInfoServices infoService;
    private Point point;
    private CidsBean cidsBean;
    private String title;
    private final CardLayout cardLayout;
    private final CardLayout cardLayoutForContent;
//    private BindingGroup punktOrtBindingGroup;
    private List<PointLocation> pointLocations;
    // should be static!
    private ImageIcon PUNKT_PDF;
    private ImageIcon PUNKT_HTML;
    private ImageIcon PUNKT_TXT;
    private String urlOfAPMap;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgrAPMapControls;
    private org.jdesktop.swingx.JXBusyLabel blWaiting;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRetrieve;
    private javax.swing.JComboBox cbPunktorte;
    private javax.swing.Box.Filler gluFiller;
    private org.jdesktop.swingx.JXHyperlink hlPunktlisteHtml;
    private org.jdesktop.swingx.JXHyperlink hlPunktlistePdf;
    private org.jdesktop.swingx.JXHyperlink hlPunktlisteTxt;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JLabel lblBack;
    private javax.swing.JLabel lblDescAnlass;
    private javax.swing.JLabel lblDescBeginn;
    private javax.swing.JLabel lblDescDatenerhebung;
    private javax.swing.JLabel lblDescDienststelle;
    private javax.swing.JLabel lblDescEnde;
    private javax.swing.JLabel lblDescGenauigkeitsstufe;
    private javax.swing.JLabel lblDescHinweise;
    private javax.swing.JLabel lblDescHochwert;
    private javax.swing.JLabel lblDescIdentifikator;
    private javax.swing.JLabel lblDescKartendarstellung;
    private javax.swing.JLabel lblDescKoordStatus;
    private javax.swing.JLabel lblDescLand;
    private javax.swing.JLabel lblDescMarke;
    private javax.swing.JLabel lblDescModellart;
    private javax.swing.JLabel lblDescPLIdentifikator;
    private javax.swing.JLabel lblDescPLObjektart;
    private javax.swing.JLabel lblDescPOAnlass;
    private javax.swing.JLabel lblDescPOBeginn;
    private javax.swing.JLabel lblDescPOEnde;
    private javax.swing.JLabel lblDescPOModellart;
    private javax.swing.JLabel lblDescPunktart;
    private javax.swing.JLabel lblDescPunktkennung;
    private javax.swing.JLabel lblDescPunktorte;
    private javax.swing.JLabel lblDescRechtswert;
    private javax.swing.JLabel lblForw;
    private javax.swing.JLabel lblHeaderAPMap;
    private javax.swing.JLabel lblLocHead;
    private javax.swing.JLabel lblMissingAPMap;
    private javax.swing.JLabel lblPointHead;
    private javax.swing.JLabel lblPreviewHead;
    private javax.swing.JLabel lblProductPreview;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTxtAbmarkungMarke;
    private javax.swing.JLabel lblTxtAnlass;
    private javax.swing.JLabel lblTxtBeginn;
    private javax.swing.JLabel lblTxtDatenerhebung;
    private javax.swing.JLabel lblTxtDienststelle;
    private javax.swing.JLabel lblTxtEnde;
    private javax.swing.JLabel lblTxtGenauigkeitsstufe;
    private javax.swing.JLabel lblTxtHinweise;
    private javax.swing.JLabel lblTxtHochwert;
    private javax.swing.JLabel lblTxtIdentifikator;
    private javax.swing.JLabel lblTxtKartendarstellung;
    private javax.swing.JLabel lblTxtKoordStatus;
    private javax.swing.JLabel lblTxtLand;
    private javax.swing.JLabel lblTxtModellart;
    private javax.swing.JLabel lblTxtPLIdentifikator;
    private javax.swing.JLabel lblTxtPLObjektart;
    private javax.swing.JLabel lblTxtPOAnlass;
    private javax.swing.JLabel lblTxtPOBeginn;
    private javax.swing.JLabel lblTxtPOEnde;
    private javax.swing.JLabel lblTxtPOModellart;
    private javax.swing.JLabel lblTxtPunktart;
    private javax.swing.JLabel lblTxtPunktkennung;
    private javax.swing.JLabel lblTxtRechtswert;
    private de.cismet.cismap.commons.gui.measuring.MeasuringComponent measuringComponent;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panFooter;
    private javax.swing.JPanel panFooterLeft;
    private javax.swing.JPanel panFooterRight;
    private javax.swing.JPanel panHtmlProducts;
    private javax.swing.JPanel panInfo;
    private javax.swing.JPanel panLocationInfos;
    private javax.swing.JPanel panPdfProducts;
    private javax.swing.JPanel panPointInfo;
    private javax.swing.JPanel panProductPreview;
    private javax.swing.JPanel panProducts;
    private javax.swing.JPanel panTitle;
    private javax.swing.JPanel panTxtProducts;
    private de.cismet.tools.gui.RoundedPanel pnlAPMap;
    private javax.swing.JPanel pnlContent;
    private de.cismet.tools.gui.RoundedPanel pnlControls;
    private de.cismet.tools.gui.SemiRoundedPanel pnlHeaderAPMap;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel3;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel4;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel5;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel6;
    private de.cismet.tools.gui.SemiRoundedPanel semiRoundedPanel7;
    private de.cismet.tools.gui.SemiRoundedPanel srpHeadLocInfo;
    private de.cismet.tools.gui.SemiRoundedPanel srpPointInfo;
    private javax.swing.JToggleButton togPan;
    private javax.swing.JToggleButton togZoom;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates new form Alkis_pointRenderer.
     */
    public AlkisPointRenderer() {
        retrieveableLabels = TypeSafeCollections.newArrayList();
        productPreviewImages = TypeSafeCollections.newHashMap();

        if (!AlkisUtils.validateUserShouldUseAlkisSOAPServerActions()) {
            try {
                soapProvider = new SOAPAccessProvider(AlkisConstants.COMMONS);
                infoService = soapProvider.getAlkisInfoService();
            } catch (Exception ex) {
                LOG.fatal(ex, ex);
            }
        }
        initIcons();
        initComponents();
        initFooterElements();
        initProductPreview();
        setWait(false);
        panLocationInfos.setVisible(false);
        cbPunktorte.setRenderer(new LocationComboBoxRenderer());
        LayoutManager layoutManager = getLayout();
        if (layoutManager instanceof CardLayout) {
            cardLayout = (CardLayout)layoutManager;
            cardLayout.show(this, CARD_1);
        } else {
            cardLayout = new CardLayout();
            LOG.error("AlkisPointRenderer exspects CardLayout as major layout manager, but has " + getLayout()
                        + "!");
        }
        layoutManager = pnlContent.getLayout();
        if (layoutManager instanceof CardLayout) {
            cardLayoutForContent = (CardLayout)layoutManager;
            cardLayoutForContent.show(pnlContent, CARD_APMAP);
        } else {
            cardLayoutForContent = new CardLayout();
            LOG.error(
                "AlkisPointRenderer exspects CardLayout as layout manager to display preview and AP maps, but has "
                        + getLayout()
                        + "!");
        }
        retrieveableLabels.add(lblTxtBeginn);
        retrieveableLabels.add(lblTxtEnde);
        retrieveableLabels.add(lblTxtModellart);
        retrieveableLabels.add(lblTxtDienststelle);
        retrieveableLabels.add(lblTxtLand);
        retrieveableLabels.add(lblTxtDienststelle);
        retrieveableLabels.add(lblTxtAnlass);
        if (!AlkisUtils.validateUserHasAlkisProductAccess()) {
            // disable Product page if user does not have the right to see it.
            btnForward.setEnabled(false);
            lblForw.setEnabled(false);
        }
        panHtmlProducts.setVisible(AlkisUtils.validateUserHasAlkisHTMLProductAccess());

        final boolean billingAllowedPdf = BillingPopup.isBillingAllowed("pktlstpdf");
        final boolean billingAllowedTxt = BillingPopup.isBillingAllowed("pktlsttxt");
        final boolean billingAllowedHtml = billingAllowedPdf || billingAllowedTxt;

        hlPunktlisteHtml.setEnabled(billingAllowedHtml
                    && ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_PUNKTLISTE));
        hlPunktlistePdf.setEnabled(billingAllowedPdf
                    && ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_PUNKTLISTE));
        hlPunktlisteTxt.setEnabled(billingAllowedTxt
                    && ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_PUNKTLISTE));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void initProductPreviewImages() {
        productPreviewImages.put(hlPunktlistePdf, PUNKT_PDF);
        productPreviewImages.put(hlPunktlisteHtml, PUNKT_HTML);
        productPreviewImages.put(hlPunktlisteTxt, PUNKT_TXT);
        final ProductLabelMouseAdaper productListener = new ProductLabelMouseAdaper();
        hlPunktlistePdf.addMouseListener(productListener);
        hlPunktlisteHtml.addMouseListener(productListener);
        hlPunktlisteTxt.addMouseListener(productListener);
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
     */
    private void initIcons() {
//        BACKWARD_SELECTED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-left-sel.png"));
//        BACKWARD_PRESSED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-left-pressed.png"));
//
//        FORWARD_SELECTED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-right-sel.png"));
//        FORWARD_PRESSED = new ImageIcon(getClass().getResource(ICON_RES_PACKAGE + "arrow-right-pressed.png"));
        final ReflectionRenderer reflectionRenderer = new ReflectionRenderer(0.5f, 0.15f, false);
        BufferedImage i1 = null;
        BufferedImage i2 = null;
        BufferedImage i3 = null;
        try {
            // TODO: own picture!
            i1 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "punktlistepdf.png")));
            i2 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "punktlistehtml.png")));
            i3 = reflectionRenderer.appendReflection(ImageIO.read(
                        getClass().getResource(ALKIS_RES_PACKAGE + "punktlistetxt.png")));
        } catch (Exception ex) {
            LOG.error(ex, ex);
        }
        PUNKT_PDF = new ImageIcon(i1);
        PUNKT_HTML = new ImageIcon(i2);
        PUNKT_TXT = new ImageIcon(i3);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  waiting  DOCUMENT ME!
     */
    private void setWait(final boolean waiting) {
        blWaiting.setBusy(waiting);
        blWaiting.setVisible(waiting);
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
        blWaiting = new org.jdesktop.swingx.JXBusyLabel();
        panFooter = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        panFooterLeft = new javax.swing.JPanel();
        lblBack = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        panFooterRight = new javax.swing.JPanel();
        btnForward = new javax.swing.JButton();
        lblForw = new javax.swing.JLabel();
        bgrAPMapControls = new javax.swing.ButtonGroup();
        panInfo = new javax.swing.JPanel();
        panPointInfo = new RoundedPanel();
        lblTxtPunktkennung = new javax.swing.JLabel();
        lblTxtIdentifikator = new javax.swing.JLabel();
        btnRetrieve = new javax.swing.JButton();
        lblDescIdentifikator = new javax.swing.JLabel();
        lblDescMarke = new javax.swing.JLabel();
        lblDescPunktart = new javax.swing.JLabel();
        lblTxtPunktart = new javax.swing.JLabel();
        lblDescPunktkennung = new javax.swing.JLabel();
        lblTxtAbmarkungMarke = new javax.swing.JLabel();
        lblTxtBeginn = new javax.swing.JLabel();
        lblDescModellart = new javax.swing.JLabel();
        lblDescLand = new javax.swing.JLabel();
        lblDescDienststelle = new javax.swing.JLabel();
        lblDescBeginn = new javax.swing.JLabel();
        lblTxtModellart = new javax.swing.JLabel();
        lblTxtLand = new javax.swing.JLabel();
        lblTxtDienststelle = new javax.swing.JLabel();
        lblDescEnde = new javax.swing.JLabel();
        lblTxtEnde = new javax.swing.JLabel();
        lblDescAnlass = new javax.swing.JLabel();
        lblTxtAnlass = new javax.swing.JLabel();
        srpPointInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblPointHead = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        panLocationInfos = new RoundedPanel();
        cbPunktorte = new javax.swing.JComboBox();
        lblDescKartendarstellung = new javax.swing.JLabel();
        lblDescRechtswert = new javax.swing.JLabel();
        lblDescHochwert = new javax.swing.JLabel();
        lblDescDatenerhebung = new javax.swing.JLabel();
        lblTxtRechtswert = new javax.swing.JLabel();
        lblTxtHochwert = new javax.swing.JLabel();
        lblTxtHinweise = new javax.swing.JLabel();
        lblDescKoordStatus = new javax.swing.JLabel();
        lblTxtKoordStatus = new javax.swing.JLabel();
        lblDescGenauigkeitsstufe = new javax.swing.JLabel();
        lblDescHinweise = new javax.swing.JLabel();
        lblTxtGenauigkeitsstufe = new javax.swing.JLabel();
        lblTxtDatenerhebung = new javax.swing.JLabel();
        srpHeadLocInfo = new de.cismet.tools.gui.SemiRoundedPanel();
        lblLocHead = new javax.swing.JLabel();
        lblDescPunktorte = new javax.swing.JLabel();
        lblDescPOModellart = new javax.swing.JLabel();
        lblTxtPOModellart = new javax.swing.JLabel();
        lblDescPOAnlass = new javax.swing.JLabel();
        lblTxtPOAnlass = new javax.swing.JLabel();
        lblDescPOBeginn = new javax.swing.JLabel();
        lblTxtPOBeginn = new javax.swing.JLabel();
        lblDescPOEnde = new javax.swing.JLabel();
        lblTxtPOEnde = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblDescPLIdentifikator = new javax.swing.JLabel();
        lblTxtPLIdentifikator = new javax.swing.JLabel();
        lblDescPLObjektart = new javax.swing.JLabel();
        lblTxtPLObjektart = new javax.swing.JLabel();
        lblTxtKartendarstellung = new javax.swing.JLabel();
        panProducts = new javax.swing.JPanel();
        panPdfProducts = new RoundedPanel();
        hlPunktlistePdf = new org.jdesktop.swingx.JXHyperlink();
        jPanel1 = new javax.swing.JPanel();
        semiRoundedPanel4 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel4 = new javax.swing.JLabel();
        panHtmlProducts = new RoundedPanel();
        hlPunktlisteHtml = new org.jdesktop.swingx.JXHyperlink();
        jPanel2 = new javax.swing.JPanel();
        semiRoundedPanel5 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel5 = new javax.swing.JLabel();
        panTxtProducts = new RoundedPanel();
        hlPunktlisteTxt = new org.jdesktop.swingx.JXHyperlink();
        jPanel7 = new javax.swing.JPanel();
        semiRoundedPanel6 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel6 = new javax.swing.JLabel();
        pnlControls = new de.cismet.tools.gui.RoundedPanel();
        togPan = new javax.swing.JToggleButton();
        togZoom = new javax.swing.JToggleButton();
        btnHome = new javax.swing.JButton();
        semiRoundedPanel7 = new de.cismet.tools.gui.SemiRoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        btnOpen = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        panProductPreview = new RoundedPanel();
        lblProductPreview = new javax.swing.JLabel();
        semiRoundedPanel3 = new de.cismet.tools.gui.SemiRoundedPanel();
        lblPreviewHead = new javax.swing.JLabel();
        pnlAPMap = new de.cismet.tools.gui.RoundedPanel();
        pnlHeaderAPMap = new de.cismet.tools.gui.SemiRoundedPanel();
        lblHeaderAPMap = new javax.swing.JLabel();
        measuringComponent = new MeasuringComponent(INITIAL_BOUNDINGBOX, CRS);
        lblMissingAPMap = new javax.swing.JLabel();
        gluFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 0),
                new java.awt.Dimension(0, 32767));

        panTitle.setMinimumSize(new java.awt.Dimension(101, 32));
        panTitle.setOpaque(false);
        panTitle.setPreferredSize(new java.awt.Dimension(101, 32));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panTitle.add(blWaiting, gridBagConstraints);

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

        panInfo.setOpaque(false);
        panInfo.setLayout(new java.awt.GridBagLayout());

        panPointInfo.setMaximumSize(new java.awt.Dimension(350, 500));
        panPointInfo.setMinimumSize(new java.awt.Dimension(350, 500));
        panPointInfo.setOpaque(false);
        panPointInfo.setPreferredSize(new java.awt.Dimension(350, 500));
        panPointInfo.setLayout(new java.awt.GridBagLayout());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pointcode}"),
                lblTxtPunktkennung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 7, 7, 7);
        panPointInfo.add(lblTxtPunktkennung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.uuid}"),
                lblTxtIdentifikator,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtIdentifikator, gridBagConstraints);

        btnRetrieve.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/network-wired.png"))); // NOI18N
        btnRetrieve.setText("Punktorte laden");
        btnRetrieve.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnRetrieveActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panPointInfo.add(btnRetrieve, gridBagConstraints);

        lblDescIdentifikator.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescIdentifikator.setText("Identifikator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescIdentifikator, gridBagConstraints);

        lblDescMarke.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescMarke.setText("Marke:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescMarke, gridBagConstraints);

        lblDescPunktart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPunktart.setText("Punktart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescPunktart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.pointtype.bezeichnung}"),
                lblTxtPunktart,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtPunktart, gridBagConstraints);

        lblDescPunktkennung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPunktkennung.setText("Punktkennung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 7, 7, 7);
        panPointInfo.add(lblDescPunktkennung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.marker}"),
                lblTxtAbmarkungMarke,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtAbmarkungMarke, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.beginn}"),
                lblTxtBeginn,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtBeginn, gridBagConstraints);

        lblDescModellart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescModellart.setText("Modellart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescModellart, gridBagConstraints);

        lblDescLand.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescLand.setText("Land:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescLand, gridBagConstraints);

        lblDescDienststelle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescDienststelle.setText("Dienststelle:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescDienststelle, gridBagConstraints);

        lblDescBeginn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescBeginn.setText("Lebenszeit-Beginn:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescBeginn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.modellart}"),
                lblTxtModellart,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtModellart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.land}"),
                lblTxtLand,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtLand, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.dienststelle}"),
                lblTxtDienststelle,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtDienststelle, gridBagConstraints);

        lblDescEnde.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescEnde.setText("Lebenszeit-Ende:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescEnde, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.ende}"),
                lblTxtEnde,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtEnde, gridBagConstraints);

        lblDescAnlass.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescAnlass.setText("Anlass:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblDescAnlass, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                this,
                org.jdesktop.beansbinding.ELProperty.create("${cidsBean.anlass}"),
                lblTxtAnlass,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panPointInfo.add(lblTxtAnlass, gridBagConstraints);

        srpPointInfo.setBackground(java.awt.Color.darkGray);
        srpPointInfo.setLayout(new java.awt.GridBagLayout());

        lblPointHead.setForeground(new java.awt.Color(255, 255, 255));
        lblPointHead.setText("Info");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpPointInfo.add(lblPointHead, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panPointInfo.add(srpPointInfo, gridBagConstraints);

        jPanel3.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        panPointInfo.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        panInfo.add(panPointInfo, gridBagConstraints);

        panLocationInfos.setMaximumSize(new java.awt.Dimension(500, 500));
        panLocationInfos.setMinimumSize(new java.awt.Dimension(500, 500));
        panLocationInfos.setOpaque(false);
        panLocationInfos.setPreferredSize(new java.awt.Dimension(500, 500));
        panLocationInfos.setLayout(new java.awt.GridBagLayout());

        cbPunktorte.setMaximumSize(new java.awt.Dimension(200, 20));
        cbPunktorte.setMinimumSize(new java.awt.Dimension(200, 20));
        cbPunktorte.setPreferredSize(new java.awt.Dimension(200, 20));

        final org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create(
                "${pointLocations}");
        final org.jdesktop.swingbinding.JComboBoxBinding jComboBoxBinding = org.jdesktop.swingbinding.SwingBindings
                    .createJComboBoxBinding(
                        org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                        this,
                        eLProperty,
                        cbPunktorte);
        jComboBoxBinding.setSourceNullValue(null);
        jComboBoxBinding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(jComboBoxBinding);

        cbPunktorte.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    cbPunktorteActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 7, 7, 7);
        panLocationInfos.add(cbPunktorte, gridBagConstraints);

        lblDescKartendarstellung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescKartendarstellung.setText("Kartendarstellung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescKartendarstellung, gridBagConstraints);

        lblDescRechtswert.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescRechtswert.setText("Rechtswert:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescRechtswert, gridBagConstraints);

        lblDescHochwert.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescHochwert.setText("Hochwert:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescHochwert, gridBagConstraints);

        lblDescDatenerhebung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescDatenerhebung.setText("Datenerhebung:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescDatenerhebung, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.rechtswert}"),
                lblTxtRechtswert,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtRechtswert, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.hochwert}"),
                lblTxtHochwert,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtHochwert, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.hinweis}"),
                lblTxtHinweise,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtHinweise, gridBagConstraints);

        lblDescKoordStatus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescKoordStatus.setText("Koordinatenstatus:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescKoordStatus, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.koordinatenStatusName}"),
                lblTxtKoordStatus,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtKoordStatus, gridBagConstraints);

        lblDescGenauigkeitsstufe.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescGenauigkeitsstufe.setText("Genauigkeitsstufe:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescGenauigkeitsstufe, gridBagConstraints);

        lblDescHinweise.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescHinweise.setText("Hinweise:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescHinweise, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.qualitaetsangabenGenauigkeitsstufeName}"),
                lblTxtGenauigkeitsstufe,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtGenauigkeitsstufe, gridBagConstraints);

        lblTxtDatenerhebung.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        lblTxtDatenerhebung.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.qualitaetsangabenHerkunft}"),
                lblTxtDatenerhebung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(ALKIS_ERHEBUNG_CONVERTER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 7, 7);
        panLocationInfos.add(lblTxtDatenerhebung, gridBagConstraints);

        srpHeadLocInfo.setBackground(java.awt.Color.darkGray);
        srpHeadLocInfo.setLayout(new java.awt.GridBagLayout());

        lblLocHead.setForeground(new java.awt.Color(255, 255, 255));
        lblLocHead.setText("Punktorte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        srpHeadLocInfo.add(lblLocHead, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        panLocationInfos.add(srpHeadLocInfo, gridBagConstraints);

        lblDescPunktorte.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPunktorte.setText("Koordinatensystem:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(14, 7, 7, 7);
        panLocationInfos.add(lblDescPunktorte, gridBagConstraints);

        lblDescPOModellart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPOModellart.setText("Modellart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPOModellart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.modellArt}"),
                lblTxtPOModellart,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPOModellart, gridBagConstraints);

        lblDescPOAnlass.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPOAnlass.setText("Anlass:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPOAnlass, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.anlass}"),
                lblTxtPOAnlass,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPOAnlass, gridBagConstraints);

        lblDescPOBeginn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPOBeginn.setText("Lebenszeit-Beginn:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPOBeginn, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.lebenszeitIntervallBeginnt}"),
                lblTxtPOBeginn,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPOBeginn, gridBagConstraints);

        lblDescPOEnde.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPOEnde.setText("Lebenszeit-Ende:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPOEnde, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.lebenszeitIntervallEndet}"),
                lblTxtPOEnde,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPOEnde, gridBagConstraints);

        jPanel4.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        panLocationInfos.add(jPanel4, gridBagConstraints);

        lblDescPLIdentifikator.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPLIdentifikator.setText("Identifikator:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPLIdentifikator, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.UUId}"),
                lblTxtPLIdentifikator,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPLIdentifikator, gridBagConstraints);

        lblDescPLObjektart.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDescPLObjektart.setText("Objektart:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblDescPLObjektart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.pointLocationType}"),
                lblTxtPLObjektart,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtPLObjektart, gridBagConstraints);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(
                org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ,
                cbPunktorte,
                org.jdesktop.beansbinding.ELProperty.create("${selectedItem.kartendarstellung}"),
                lblTxtKartendarstellung,
                org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("keine Angabe");
        binding.setSourceUnreadableValue("<Error>");
        binding.setConverter(ALKIS_BOOLEAN_CONVERTER);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        panLocationInfos.add(lblTxtKartendarstellung, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(15, 15, 15, 15);
        panInfo.add(panLocationInfos, gridBagConstraints);

        add(panInfo, "CARD_1");

        panProducts.setOpaque(false);
        panProducts.setLayout(new java.awt.GridBagLayout());

        panPdfProducts.setMaximumSize(new java.awt.Dimension(175, 80));
        panPdfProducts.setMinimumSize(new java.awt.Dimension(175, 80));
        panPdfProducts.setOpaque(false);
        panPdfProducts.setPreferredSize(new java.awt.Dimension(175, 80));
        panPdfProducts.setLayout(new java.awt.GridBagLayout());

        hlPunktlistePdf.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/pdf.png"))); // NOI18N
        hlPunktlistePdf.setText("Punktliste");
        hlPunktlistePdf.setToolTipText("Punktliste für Einzelpunkt (PDF)");
        hlPunktlistePdf.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlPunktlistePdfActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 7, 7, 7);
        panPdfProducts.add(hlPunktlistePdf, gridBagConstraints);

        jPanel1.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 9, 5);
        panProducts.add(panPdfProducts, gridBagConstraints);

        panHtmlProducts.setMaximumSize(new java.awt.Dimension(175, 80));
        panHtmlProducts.setMinimumSize(new java.awt.Dimension(175, 80));
        panHtmlProducts.setOpaque(false);
        panHtmlProducts.setPreferredSize(new java.awt.Dimension(175, 80));
        panHtmlProducts.setLayout(new java.awt.GridBagLayout());

        hlPunktlisteHtml.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-html.png"))); // NOI18N
        hlPunktlisteHtml.setText("Punktliste");
        hlPunktlisteHtml.setToolTipText("Punktliste für Einzelpunkt (HTML)");
        hlPunktlisteHtml.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlPunktlisteHtmlActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(17, 7, 7, 7);
        panHtmlProducts.add(hlPunktlisteHtml, gridBagConstraints);

        jPanel2.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 5, 5, 5);
        panProducts.add(panHtmlProducts, gridBagConstraints);

        panTxtProducts.setMaximumSize(new java.awt.Dimension(175, 80));
        panTxtProducts.setMinimumSize(new java.awt.Dimension(175, 80));
        panTxtProducts.setOpaque(false);
        panTxtProducts.setPreferredSize(new java.awt.Dimension(175, 80));
        panTxtProducts.setLayout(new java.awt.GridBagLayout());

        hlPunktlisteTxt.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/icons/text-plain.png"))); // NOI18N
        hlPunktlisteTxt.setText("Punktliste");
        hlPunktlisteTxt.setToolTipText("Punktliste für Einzelpunkt (TEXT)");
        hlPunktlisteTxt.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    hlPunktlisteTxtActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(17, 7, 7, 7);
        panTxtProducts.add(hlPunktlisteTxt, gridBagConstraints);

        jPanel7.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panTxtProducts.add(jPanel7, gridBagConstraints);

        semiRoundedPanel6.setBackground(java.awt.Color.darkGray);
        semiRoundedPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Textformat-Produkte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        semiRoundedPanel6.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panTxtProducts.add(semiRoundedPanel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 5, 5, 5);
        panProducts.add(panTxtProducts, gridBagConstraints);

        pnlControls.setLayout(new java.awt.GridBagLayout());

        bgrAPMapControls.add(togPan);
        togPan.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/pan.gif"))); // NOI18N
        togPan.setSelected(true);
        togPan.setText("Verschieben");
        togPan.setToolTipText("Verschieben");
        togPan.setEnabled(false);
        togPan.setFocusPainted(false);
        togPan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togPan.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togPanActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 3, 5);
        pnlControls.add(togPan, gridBagConstraints);

        bgrAPMapControls.add(togZoom);
        togZoom.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/zoom.gif"))); // NOI18N
        togZoom.setText("Zoomen");
        togZoom.setToolTipText("Zoomen");
        togZoom.setEnabled(false);
        togZoom.setFocusPainted(false);
        togZoom.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        togZoom.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    togZoomActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 3, 5);
        pnlControls.add(togZoom, gridBagConstraints);

        btnHome.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/home.gif"))); // NOI18N
        btnHome.setText("Übersicht");
        btnHome.setToolTipText("Übersicht");
        btnHome.setEnabled(false);
        btnHome.setFocusPainted(false);
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnHomeActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 3, 5);
        pnlControls.add(btnHome, gridBagConstraints);

        semiRoundedPanel7.setBackground(new java.awt.Color(51, 51, 51));
        semiRoundedPanel7.setLayout(new java.awt.FlowLayout());

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Steuerung");
        semiRoundedPanel7.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        pnlControls.add(semiRoundedPanel7, gridBagConstraints);

        btnOpen.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/wunda_blau/res/folder-image.png"))); // NOI18N
        btnOpen.setText("Öffnen");
        btnOpen.setToolTipText("Download zum Öffnen in externer Anwendung");
        btnOpen.setEnabled(false);
        btnOpen.setFocusPainted(false);
        btnOpen.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnOpen.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(final java.awt.event.ActionEvent evt) {
                    btnOpenActionPerformed(evt);
                }
            });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        pnlControls.add(btnOpen, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        panProducts.add(pnlControls, gridBagConstraints);

        pnlContent.setOpaque(false);
        pnlContent.setLayout(new java.awt.CardLayout());

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

        pnlContent.add(panProductPreview, "preview");

        pnlAPMap.setLayout(new java.awt.GridBagLayout());

        pnlHeaderAPMap.setBackground(java.awt.Color.darkGray);
        pnlHeaderAPMap.setLayout(new java.awt.GridBagLayout());

        lblHeaderAPMap.setForeground(java.awt.Color.white);
        lblHeaderAPMap.setText("AP-Karte");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        pnlHeaderAPMap.add(lblHeaderAPMap, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 0.1;
        pnlAPMap.add(pnlHeaderAPMap, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlAPMap.add(measuringComponent, gridBagConstraints);

        lblMissingAPMap.setBackground(java.awt.Color.white);
        lblMissingAPMap.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMissingAPMap.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/missingRasterdocument.png"))); // NOI18N
        lblMissingAPMap.setText(
            "<html><body><strong>Zu diesem ALKIS-Punkt wurde keine<br />AP-Karte gefunden.</strong></body></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        pnlAPMap.add(lblMissingAPMap, gridBagConstraints);

        pnlContent.add(pnlAPMap, "apmap");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panProducts.add(pnlContent, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        panProducts.add(gluFiller, gridBagConstraints);

        add(panProducts, "CARD_2");

        bindingGroup.bind();
    } // </editor-fold>//GEN-END:initComponents

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnRetrieveActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnRetrieveActionPerformed
        final CidsBean bean = cidsBean;
        if (bean != null) {
            final String pointCode = String.valueOf(bean.getProperty("pointcode"));
            if (pointCode != null) {
                AlkisSOAPWorkerService.execute(new RetrieveWorker(pointCode));
            }
        }
    }                                                                               //GEN-LAST:event_btnRetrieveActionPerformed

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
        cardLayout.show(this, CARD_1);
        btnBack.setEnabled(false);
        btnForward.setEnabled(true);
        lblBack.setEnabled(false);
        lblForw.setEnabled(true);
        lblTitle.setText(title);
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
        lblTitle.setText("Produkte");
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
     */
    private void showNoProductPermissionWarning() {
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(this),
            "Sie besitzen keine Berechtigung zur Erzeugung dieses Produkts!");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productType  DOCUMENT ME!
     */
    private void downloadProduct(final String productType) {
        if (!ObjectRendererUtils.checkActionTag(PRODUCT_ACTION_TAG_PUNKTLISTE)) {
            showNoProductPermissionWarning();
            return;
        }

        String extension = ".pdf";
        if (AlkisUtils.PRODUCTS.PUNKTLISTE_HTML.equals(productType)) {
            extension = ".html";
        } else if (AlkisUtils.PRODUCTS.PUNKTLISTE_TXT.equals(productType)) {
            extension = ".plst";
        }

        final String pointData = AlkisUtils.PRODUCTS.getPointDataForProduct(cidsBean);
//        URL url = null;
        if ((pointData != null) && (pointData.length() > 0)) {
            try {
//                url = AlkisUtils.PRODUCTS.productListenNachweisUrl(pointData, productType);
//
//                if (url != null) {
//                    if (!DownloadManagerDialog.showAskingForUserTitle(StaticSwingTools.getParentFrame(this))) {
//                        return;
//                    }
//
//                    final HttpDownload download = new HttpDownload(
//                            url,
//                            "",
//                            DownloadManagerDialog.getJobname(),
//                            "Punktliste",
//                            productType,
//                            extension);
//                    DownloadManager.instance().add(download);
//                }

                final String url = AlkisUtils.PRODUCTS.productListenNachweisUrl(pointData, productType);
                if ((url != null) && (url.trim().length() > 0)) {
                    if (!DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(AlkisPointRenderer.this)) {
                        return;
                    }

                    HttpDownload download = null;
                    final int parameterPosition = url.indexOf('?');

                    if (parameterPosition < 0) {
                        download = new HttpDownload(
                                new URL(url),
                                "",
                                DownloadManagerDialog.getInstance().getJobName(),
                                "Punktnachweis",
                                productType,
                                extension);
                    } else {
                        final String parameters = url.substring(parameterPosition + 1);
                        download = new HttpDownload(
                                new URL(url.substring(0, parameterPosition)),
                                parameters,
                                AlkisProducts.POST_HEADER,
                                DownloadManagerDialog.getInstance().getJobName(),
                                "Punktnachweis",
                                productType,
                                extension);
                    }

                    DownloadManager.instance().add(download);
                }
            } catch (Exception ex) {
                ObjectRendererUtils.showExceptionWindowToUser(
                    "Fehler beim Aufruf des Produkts: "
                            + productType,
                    ex,
                    AlkisPointRenderer.this);
                LOG.error("The URL to download product '" + productType + "' (actionTag: "
                            + PRODUCT_ACTION_TAG_PUNKTLISTE + ") could not be constructed.",
                    ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlPunktlistePdfActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlPunktlistePdfActionPerformed
        try {
            if (BillingPopup.doBilling("pktlstpdf", "no.yet", (Geometry)null, new ProductGroupAmount("eafifty", 1))) {
                downloadProduct(AlkisUtils.PRODUCTS.PUNKTLISTE_PDF);
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }                                                                                   //GEN-LAST:event_hlPunktlistePdfActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlPunktlisteHtmlActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlPunktlisteHtmlActionPerformed
        downloadProduct(AlkisUtils.PRODUCTS.PUNKTLISTE_HTML);
    }                                                                                    //GEN-LAST:event_hlPunktlisteHtmlActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void cbPunktorteActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_cbPunktorteActionPerformed
        final Object selection = cbPunktorte.getSelectedItem();
        if (selection instanceof PointLocation) {
            final PointLocation pointLoc = (PointLocation)selection;
            if ((pointLoc.getKartendarstellung() != null) && pointLoc.getKartendarstellung().equals("1")) {
                cbPunktorte.setBackground(PUNKTORT_MIT_KARTENDARSTELLUNG);
            } else {
                cbPunktorte.setBackground(Color.WHITE);
            }
        }
    }                                                                               //GEN-LAST:event_cbPunktorteActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void hlPunktlisteTxtActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_hlPunktlisteTxtActionPerformed
        try {
            if (BillingPopup.doBilling(
                            "pktlsttxt",
                            "no.yet",
                            (Geometry)null,
                            new ProductGroupAmount("eapkt_1000", 1))) {
                downloadProduct(AlkisUtils.PRODUCTS.PUNKTLISTE_TXT);
            }
        } catch (Exception e) {
            LOG.error("Error when trying to produce a alkis product", e);
            // Hier noch ein Fehlerdialog
        }
    }                                                                                   //GEN-LAST:event_hlPunktlisteTxtActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togPanActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togPanActionPerformed
        measuringComponent.actionPan();
    }                                                                          //GEN-LAST:event_togPanActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void togZoomActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_togZoomActionPerformed
        measuringComponent.actionZoom();
    }                                                                           //GEN-LAST:event_togZoomActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnHomeActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnHomeActionPerformed
        measuringComponent.actionOverview();
    }                                                                           //GEN-LAST:event_btnHomeActionPerformed

    /**
     * DOCUMENT ME!
     *
     * @param  evt  DOCUMENT ME!
     */
    private void btnOpenActionPerformed(final java.awt.event.ActionEvent evt) { //GEN-FIRST:event_btnOpenActionPerformed
        if (urlOfAPMap != null) {
            final URL url;
            try {
                url = new URL(urlOfAPMap);
            } catch (MalformedURLException ex) {
                LOG.info("Couldn't download AP map from '" + urlOfAPMap + "'.", ex);
                return;
            }

            try {
                if (BillingPopup.doBilling(
                                "appdf",
                                url.toString(),
                                (Geometry)null,
                                new ProductGroupAmount("ea", 1))) {
                    CismetThreadPool.execute(new Runnable() {

                            @Override
                            public void run() {
                                if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                                                AlkisPointRenderer.this)) {
                                    final String filename = urlOfAPMap.substring(urlOfAPMap.lastIndexOf("/") + 1);
                                    DownloadManager.instance()
                                            .add(
                                                new HttpDownload(
                                                    url,
                                                    "",
                                                    DownloadManagerDialog.getInstance().getJobName(),
                                                    "AP-Karte",
                                                    filename.substring(0, filename.lastIndexOf(".")),
                                                    filename.substring(filename.lastIndexOf("."))));
                                }
                            }
                        });
                }
            } catch (Exception e) {
                LOG.error("Error when trying to produce a alkis product", e);
                // Hier noch ein Fehlerdialog
            }
        }
    } //GEN-LAST:event_btnOpenActionPerformed

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
     * @param   alkisPoint  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean hasAPMap(final CidsBean alkisPoint) {
        boolean result = false;

        final Object pointtype = alkisPoint.getProperty("pointtype.bezeichnung");
        if (pointtype instanceof String) {
            if (POINTTYPE_AUFNAHMEPUNKT.equalsIgnoreCase(pointtype.toString())
                        || POINTTYPE_SONSTIGERVERMESSUNGSPUNKT.equalsIgnoreCase(pointtype.toString())) {
                result = true;
            }
        }

        return result;
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
            bindingGroup.bind();

            if (!hasAPMap(cidsBean)) {
                pnlControls.setVisible(false);
                cardLayoutForContent.show(pnlContent, CARD_PREVIEW);
            } else {
                final Object pointcode = cidsBean.getProperty("pointcode");
                if (pointcode != null) {
                    CismetThreadPool.execute(new RefreshDocumentWorker(pointcode.toString()));
                } else {
                    LOG.error("The given CidsBean (alkis_point) has no pointcode.");
                }
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
            title = "Punkt " + title;
        }
        this.title = title;
        lblTitle.setText(this.title);
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
     * @return  the point
     */
    public Point getPoint() {
        return point;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  point  the point to set
     */
    public void setPoint(final Point point) {
        this.point = point;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the pointLocations
     */
    public List<PointLocation> getPointLocations() {
        return pointLocations;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  pointLocations  the pointLocations to set
     */
    public void setPointLocations(final List<PointLocation> pointLocations) {
        this.pointLocations = pointLocations;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    public void dispose() {
        bindingGroup.unbind();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "sb",
                "alkis_point",
                // 548516,
                574043,
                // 1,
                "ALKIS-Punkt-Renderer",
                1024,
                768);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class RetrieveWorker extends SwingWorker<Point, Void> {

        //~ Instance fields ----------------------------------------------------

        private final String pointCode;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RetrieveWorker object.
         *
         * @param  pointCode  DOCUMENT ME!
         */
        public RetrieveWorker(final String pointCode) {
            setWait(true);
            this.pointCode = pointCode;
            btnRetrieve.setEnabled(false);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected Point doInBackground() throws Exception {
            if (infoService != null) {
                return infoService.getPoint(soapProvider.getIdentityCard(), soapProvider.getService(), pointCode);
            } else {
                return AlkisUtils.getPointFromAlkisSOAPServerAction(pointCode);
            }
        }

        /**
         * DOCUMENT ME!
         */
        private void restoreOnException() {
            btnRetrieve.setEnabled(true);
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            setWait(false);
            if (!isCancelled()) {
                try {
                    final Point point = get();
                    if (point != null) {
                        AlkisPointRenderer.this.setPoint(point);
                        final PointLocation[] pointlocArr = point.getPointLocations();
                        if (pointlocArr != null) {
                            Arrays.sort(pointlocArr, POINTLOCATION_COMPARATOR);
                            AlkisPointRenderer.this.setPointLocations(Arrays.asList(pointlocArr));
                        }
                        AlkisPointRenderer.this.bindingGroup.unbind();
                        AlkisPointRenderer.this.bindingGroup.bind();
                        panLocationInfos.setVisible(true);
                    }
                } catch (InterruptedException ex) {
                    restoreOnException();
                    LOG.warn(ex, ex);
                } catch (Exception ex) {
                    // TODO show error message to user?
                    restoreOnException();
                    final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                            "Fehler beim Retrieve",
                            ex.getMessage(),
                            null,
                            null,
                            ex,
                            Level.ALL,
                            null);
                    org.jdesktop.swingx.JXErrorPane.showDialog(StaticSwingTools.getParentFrame(
                            AlkisPointRenderer.this),
                        ei);
                    LOG.error(ex, ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static final class LocationComboBoxRenderer extends JLabel implements ListCellRenderer {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LocationComboBoxRenderer object.
         */
        public LocationComboBoxRenderer() {
            setOpaque(true);
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   list          DOCUMENT ME!
         * @param   value         DOCUMENT ME!
         * @param   index         DOCUMENT ME!
         * @param   isSelected    DOCUMENT ME!
         * @param   cellHasFocus  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public Component getListCellRendererComponent(final JList list,
                final Object value,
                final int index,
                final boolean isSelected,
                final boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
//                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            if (value instanceof PointLocation) {
                final PointLocation loc = (PointLocation)value;
                setText(loc.getKoordinatenReferenzSystem());
                if ((loc.getKartendarstellung() != null) && loc.getKartendarstellung().equals("1")) {
                    if (!isSelected) {
                        setBackground(PUNKTORT_MIT_KARTENDARSTELLUNG);
                    }
                }
            } else {
                setText((value == null) ? "" : value.toString());
            }
            return this;
        }
    }

    /**
     * DOCUMENT ME!
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    class RefreshDocumentWorker extends SwingWorker<BufferedImage, Object> {

        //~ Instance fields ----------------------------------------------------

        private String pointcode;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new RefreshDocumentWorker object.
         *
         * @param  pointcode  DOCUMENT ME!
         */
        public RefreshDocumentWorker(final String pointcode) {
            this.pointcode = pointcode;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  Exception  DOCUMENT ME!
         */
        @Override
        protected BufferedImage doInBackground() throws Exception {
            BufferedImage result = null;

            if (pointcode == null) {
                LOG.error("The current alkis point has no pointcode.");
                return result;
            }

            final Collection<URL> validURLs = AlkisUtils.PRODUCTS.getCorrespondingPointURLs(pointcode);
            InputStream streamToReadFrom = null;
            for (final URL url : validURLs) {
                try {
                    if (WebAccessManager.getInstance().checkIfURLaccessible(url)) {
                        streamToReadFrom = WebAccessManager.getInstance().doRequest(url);
                        urlOfAPMap = url.toExternalForm();
                        if (streamToReadFrom != null) {
                            break;
                        }
                    }
                } catch (MissingArgumentException ex) {
                    LOG.warn("Could not read AP map from URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
                } catch (AccessMethodIsNotSupportedException ex) {
                    LOG.warn("Can't access AP map URL '" + url.toExternalForm()
                                + "' with default access method. Skipping this url.",
                        ex);
                } catch (RequestFailedException ex) {
                    LOG.warn("Requesting AP map from URL '" + url.toExternalForm() + "' failed. Skipping this url.",
                        ex);
                } catch (NoHandlerForURLException ex) {
                    LOG.warn("Can't handle URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
                } catch (Exception ex) {
                    LOG.warn("An exception occurred while opening URL '" + url.toExternalForm()
                                + "'. Skipping this url.",
                        ex);
                }
            }

            if (streamToReadFrom == null) {
                LOG.error("Couldn't get a connection to associated AP map.");
                urlOfAPMap = null;
                return result;
            } else if (LOG.isDebugEnabled()) {
                LOG.debug("Loading '" + urlOfAPMap + "'.");
            }

            try {
                if (urlOfAPMap.endsWith("tif") || urlOfAPMap.endsWith("tiff") || urlOfAPMap.endsWith("TIF")
                            || urlOfAPMap.endsWith("TIFF")) {
                    final TIFFDecodeParam param = null;
                    final ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", streamToReadFrom, param);
                    final RenderedImage image = decoder.decodeAsRenderedImage();
                    final RenderedImageAdapter imageAdapter = new RenderedImageAdapter(image);
                    result = imageAdapter.getAsBufferedImage();
                } else {
                    result = ImageIO.read(streamToReadFrom);
                }
            } catch (IOException ex) {
                LOG.warn("Could not read image.", ex);
                urlOfAPMap = null;
            } finally {
                try {
                    if (streamToReadFrom != null) {
                        streamToReadFrom.close();
                    }
                } catch (IOException ex) {
                    LOG.warn("Couldn't close the stream.", ex);
                }
            }

            return result;
        }

        /**
         * DOCUMENT ME!
         */
        @Override
        protected void done() {
            BufferedImage document = null;
            try {
                if (!isCancelled()) {
                    document = get();
                }
            } catch (InterruptedException ex) {
                LOG.warn("Was interrupted while refreshing AP map.", ex);
            } catch (ExecutionException ex) {
                LOG.warn("There was an exception while refreshing AP map.", ex);
            }

            measuringComponent.reset();
            if ((document != null) && !isCancelled()) {
                measuringComponent.setVisible(true);
                lblMissingAPMap.setVisible(false);
                measuringComponent.addImage(document);
                measuringComponent.zoomToFeatureCollection();
                btnHome.setEnabled(true);
                btnOpen.setEnabled(true);
                togPan.setEnabled(true);
                togZoom.setEnabled(true);
            } else {
                measuringComponent.setVisible(false);
                lblMissingAPMap.setVisible(true);
                btnHome.setEnabled(false);
                btnOpen.setEnabled(false);
                togPan.setEnabled(false);
                togZoom.setEnabled(false);
            }
        }
    }
}
