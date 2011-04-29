package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.alkis.AlkisProductDescription;


import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.awt.Point;

import java.io.InputStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.tools.PropertyReader;
import de.cismet.cids.dynamics.CidsBean;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public final class AlkisProducts {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisProducts.class);
    //~ Static fields/initializers -----------------------------------------
    //Flurstueck
    public final String FLURSTUECKSNACHWEIS_PDF;
    public final String FLURSTUECKSNACHWEIS_HTML;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF;
    public final String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML;
    //Buchungsblatt
    public final String BESTANDSNACHWEIS_NRW_PDF;
    public final String BESTANDSNACHWEIS_NRW_HTML;
    public final String BESTANDSNACHWEIS_KOMMUNAL_PDF;
    public final String BESTANDSNACHWEIS_KOMMUNAL_HTML;
    public final String BESTANDSNACHWEIS_KOMMUNAL_INTERN_PDF;
    public final String BESTANDSNACHWEIS_KOMMUNAL_INTERN_HTML;
    public final String GRUNDSTUECKSNACHWEIS_NRW_PDF;
    public final String GRUNDSTUECKSNACHWEIS_NRW_HTML;
    //Punkt
    public final String PUNKTLISTE_PDF;
    public final String PUNKTLISTE_HTML;
    public final String PUNKTLISTE_TXT;
    //
    public final Map<String, Point> ALKIS_FORMATS;
    public final List<AlkisProductDescription> ALKIS_MAP_PRODUCTS;
    private final String IDENTIFICATION;
    //

    public AlkisProducts(String user, String pw, String service) {

        final PropertyReader productProperties = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/alkis/alkis_products.properties");
        final List<AlkisProductDescription> mapProducts = new ArrayList<AlkisProductDescription>();
        final Map<String, Point> formatMap = new HashMap<String, Point>();
        ALKIS_FORMATS = Collections.unmodifiableMap(formatMap);
        ALKIS_MAP_PRODUCTS = Collections.unmodifiableList(mapProducts);
        IDENTIFICATION = "user=" + user + "&password=" + pw + "&service=" + service;
        FLURSTUECKSNACHWEIS_PDF = productProperties.getProperty("FLURSTUECKSNACHWEIS_PDF");
        FLURSTUECKSNACHWEIS_HTML = productProperties.getProperty("FLURSTUECKSNACHWEIS_HTML");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_PDF");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_HTML");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_PDF");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_KOMMUNAL_INTERN_HTML");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_PDF");
        FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_NRW_HTML");
        //
        GRUNDSTUECKSNACHWEIS_NRW_PDF = productProperties.getProperty("GRUNDSTUECKSNACHWEIS_NRW_PDF");
        GRUNDSTUECKSNACHWEIS_NRW_HTML = productProperties.getProperty("GRUNDSTUECKSNACHWEIS_NRW_HTML");
        BESTANDSNACHWEIS_NRW_PDF = productProperties.getProperty("BESTANDSNACHWEIS_NRW_PDF");
        BESTANDSNACHWEIS_NRW_HTML = productProperties.getProperty("BESTANDSNACHWEIS_NRW_HTML");
        BESTANDSNACHWEIS_KOMMUNAL_PDF = productProperties.getProperty("BESTANDSNACHWEIS_KOMMUNAL_PDF");
        BESTANDSNACHWEIS_KOMMUNAL_HTML = productProperties.getProperty("BESTANDSNACHWEIS_KOMMUNAL_HTML");
        BESTANDSNACHWEIS_KOMMUNAL_INTERN_PDF = productProperties.getProperty("BESTANDSNACHWEIS_KOMMUNAL_INTERN_PDF");
        BESTANDSNACHWEIS_KOMMUNAL_INTERN_HTML = productProperties.getProperty("BESTANDSNACHWEIS_KOMMUNAL_INTERN_HTML");
        //
        PUNKTLISTE_PDF = productProperties.getProperty("PUNKTLISTE_PDF");
        PUNKTLISTE_HTML = productProperties.getProperty("PUNKTLISTE_HTML");
        PUNKTLISTE_TXT = productProperties.getProperty("PUNKTLISTE_TXT");
        try {
            final PropertyReader formats = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/alkis/formats.properties");
            final InputStream is = AlkisConstants.class.getClassLoader().getResourceAsStream("de/cismet/cids/custom/wunda_blau/res/alkis/Produktbeschreibung_ALKIS.xml");
            final Document document = new SAXBuilder().build(is);
            // ---------Kartenprodukte----------
            for (final Object o0 : document.getRootElement().getChildren()) {
                final Element category = (Element) o0;
                final String catName = category.getName();
                if ("Karte".equals(catName)) {
                    for (final Object o1 : category.getChildren()) {
                        final Element productClass = (Element) o1;
                        if (productClass.getName().matches(".*[Kk]lasse.*")) {
                            final String clazz = productClass.getAttribute("Name").getValue();
                            for (final Object o2 : productClass.getChildren()) {
                                final Element guiProduct = (Element) o2;
                                final String type = guiProduct.getAttribute("ProduktnameAuswertung").getValue();
                                for (final Object o3 : guiProduct.getChildren()) {
                                    final Element singleProduct = (Element) o3;
                                    final Attribute codeAttr = singleProduct.getAttribute("ID");
                                    if (codeAttr != null) {
                                        final String code = codeAttr.getValue();
                                        final String dinFormatCode = singleProduct.getAttribute("Layout").getValue();
                                        final String layoutDim = formats.getProperty(dinFormatCode);
                                        int width = -1;
                                        int height = -1;
                                        if (layoutDim == null) {
                                            org.apache.log4j.Logger.getLogger(AlkisConstants.class).info("Can not find format dimensions for: " + dinFormatCode);
                                        } else {
                                            final String[] dims = layoutDim.split("(x|X)");
                                            width = Integer.parseInt(dims[0]);
                                            height = Integer.parseInt(dims[1]);
                                            formatMap.put(dinFormatCode, new Point(width, height));
                                        }
                                        final Element preisFaktoren = (Element) singleProduct.getChildren().get(0);
                                        final String dinFormat = preisFaktoren.getAttribute("DINFormat").getValue();
                                        final String fileFormat = preisFaktoren.getAttribute("Dateiformat").getValue();
                                        final Attribute massstabAttr = preisFaktoren.getAttribute("Massstab");
                                        String massstab;
                                        if (massstabAttr != null) {
                                            massstab = preisFaktoren.getAttribute("Massstab").getValue();
                                        } else {
                                            massstab = "-";
                                        }
                                        AlkisProductDescription currentProduct = new AlkisProductDescription(clazz, type, code, dinFormat, massstab, fileFormat, width, height);
                                        mapProducts.add(currentProduct);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error while parsing Alkis Product Description!", ex);
        }
    }

    public String getPointDataForProduct(CidsBean pointBean) {
        StringBuilder sb = new StringBuilder("AX_");
        sb.append(pointBean.getProperty("pointtype"));
        sb.append(":");
        sb.append(pointBean.getProperty("pointcode"));
        return sb.toString().replace(" ", "");
    }

    //~ Methods ------------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @param  objectID  DOCUMENT ME!
     * @param  format      DOCUMENT ME!
     */
    public void productEinzelNachweis(final String objectID, final String productCode) {
        final String url = AlkisConstants.COMMONS.EINZEL_NACHWEIS_SERVICE + "?" + AlkisConstants.MLESSNUMBER + "&product=" + productCode + "&id=" + objectID + "&" + IDENTIFICATION;
        log.info("Open product URL : " + url);
        ObjectRendererUtils.openURL(url);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objectID   DOCUMENT ME!
     * @param  objectArt  DOCUMENT ME!
     * @param  format    DOCUMENT ME!
     */
    public void productListenNachweis(final String objectID, final String objectArt, final String productCode) {
        productListenNachweis(objectArt + ":" + objectID, productCode);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  punktliste  DOCUMENT ME!
     * @param  format      DOCUMENT ME!
     */
    public void productListenNachweis(final String punktliste, final String productCode) {
        final String url = AlkisConstants.COMMONS.LISTEN_NACHWEIS_SERVICE + "?" + AlkisConstants.MLESSNUMBER + "&product=" + productCode + "&ids=" + punktliste + "&" + IDENTIFICATION;
        log.info("Open product URL : " + url);
        ObjectRendererUtils.openURL(url);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objectIDs   DOCUMENT ME!
     * @param  objectArts  DOCUMENT ME!
     * @param  productCode     DOCUMENT ME!
     */
    public void productListenNachweis(final String[] objectIDs, final String[] objectArts, final String productCode) {
        final StringBuilder punktListe = new StringBuilder();
        for (int i = 0; i < objectIDs.length; ++i) {
            if (punktListe.length() > 0) {
                punktListe.append(",");
            }
            punktListe.append(objectArts[i]).append(":").append(objectIDs[i]);
        }
        productListenNachweis(punktListe.toString(), productCode);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parcelCode  DOCUMENT ME!
     */
    public void productKarte(final String parcelCode) {
        final String url = AlkisConstants.COMMONS.LIEGENSCHAFTSKARTE_SERVICE + "?" + AlkisConstants.MLESSNUMBER + "&landparcel=" + parcelCode + "&" + IDENTIFICATION;
        log.info("Open product URL : " + url);
        ObjectRendererUtils.openURL(url);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parcelCode         DOCUMENT ME!
     * @param  produkt            DOCUMENT ME!
     * @param  winkel             DOCUMENT ME!
     * @param  centerX            DOCUMENT ME!
     * @param  centerY            DOCUMENT ME!
     * @param  zusText            DOCUMENT ME!
     * @param  moreThanOneParcel  DOCUMENT ME!
     */
    public void productKarte(final String parcelCode, final AlkisProductDescription produkt, final int winkel, final int centerX, final int centerY, final String zusText, final String auftragsNr, final boolean moreThanOneParcel) {
        String url = AlkisConstants.COMMONS.LIEGENSCHAFTSKARTE_SERVICE + "?" + AlkisConstants.MLESSNUMBER + "&landparcel=" + parcelCode + "&angle=" + winkel + "&product=" + produkt.getCode() + "&centerx=" + centerX + "&centery=" + centerY;
        if ((zusText != null) && (zusText.length() > 0)) {
            url += "&text=" + zusText;
        }
        if ((auftragsNr != null) && (auftragsNr.length() > 0)) {
            url += "&ordernumber=" + auftragsNr;
        }
        if (moreThanOneParcel) {
            url += "&additionalLandparcel=true";
        }
        url += "&" + IDENTIFICATION;
        log.info("Open product URL : " + url);
        ObjectRendererUtils.openURL(url);
    }
}
