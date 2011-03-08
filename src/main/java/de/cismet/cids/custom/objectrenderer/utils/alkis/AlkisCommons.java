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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import de.aedsicad.aaaweb.service.util.Address;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.Owner;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.awt.Point;

import java.io.InputStream;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.PropertyReader;

import de.cismet.cids.dynamics.CidsBean;
import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class AlkisCommons {

    //~ Static fields/initializers ---------------------------------------------
    static {
        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisCommons.class);
        try {
            final List<AlkisProduct> mapProducts = new ArrayList<AlkisProduct>();
            final Map<String, Point> formatMap = new HashMap<String, Point>();
            Products.ALKIS_FORMATS = Collections.unmodifiableMap(formatMap);
            Products.ALKIS_MAP_PRODUCTS = Collections.unmodifiableList(mapProducts);
            final PropertyReader serviceProperties = new PropertyReader(
                    "/de/cismet/cids/custom/wunda_blau/res/alkis/alkis_conf.properties");
            final PropertyReader productProperties = new PropertyReader(
                    "/de/cismet/cids/custom/wunda_blau/res/alkis/alkis_products.properties");
            log.debug("SERVICE_PROPERTIES: " + serviceProperties.getInternalProperties());
            log.debug("PRODUCT_PROPERTIES: " + productProperties.getInternalProperties());
            final String server = serviceProperties.getProperty("SERVER");
            final String service = serviceProperties.getProperty("SERVICE");
            final String user = serviceProperties.getProperty("USER");
            final String pass = serviceProperties.getProperty("PASSWORD");
            SERVER = server;
            SERVICE = service;
            USER = user;
            PASSWORD = pass;
            Products.IDENTIFICATION = "user=" + user + "&password=" + pass + "&service=" + service;
            //
            CATALOG_SERVICE = serviceProperties.getProperty("CATALOG_SERVICE");
            INFO_SERVICE = serviceProperties.getProperty("INFO_SERVICE");
            SEARCH_SERVICE = serviceProperties.getProperty("SEARCH_SERVICE");
            //
            EINZEL_NACHWEIS_SERVICE = server + serviceProperties.getProperty("BUCH_NACHWEIS_SERVICE");
            LISTEN_NACHWEIS_SERVICE = server + serviceProperties.getProperty("LISTEN_NACHWEIS_SERVICE");
            LIEGENSCHAFTSKARTE_SERVICE = server + serviceProperties.getProperty("LIEGENSCHAFTSKARTE_SERVICE");
            //
            Products.FLURSTUECKSNACHWEIS_PDF = productProperties.getProperty("FLURSTUECKSNACHWEIS_PDF");
            Products.FLURSTUECKSNACHWEIS_HTML = productProperties.getProperty("FLURSTUECKSNACHWEIS_HTML");
            Products.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_PDF = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_PDF");
            Products.FLURSTUECKS_UND_EIGENTUMSNACHWEIS_HTML = productProperties.getProperty("FLURSTUECKS_UND_EIGENTUMSNACHWEIS_HTML");
            //
            Products.BESTANDSNACHWEIS_PDF = productProperties.getProperty("BESTANDSNACHWEIS_PDF");
            Products.BESTANDSNACHWEIS_HTML = productProperties.getProperty("BESTANDSNACHWEIS_HTML");
            //
            Products.PUNKTLISTE_PDF = productProperties.getProperty("PUNKTLISTE_PDF");
            Products.PUNKTLISTE_HTML = productProperties.getProperty("PUNKTLISTE_HTML");
            Products.PUNKTLISTE_TXT = productProperties.getProperty("PUNKTLISTE_TXT");
            //
            final String srs = serviceProperties.getProperty("SRS");
            SRS = srs;
            MAP_CALL_STRING = serviceProperties.getProperty("MAP_CALL_STRING") + srs;
            GEO_BUFFER = Double.parseDouble(serviceProperties.getProperty("GEO_BUFFER"));
            //
            try {
                final PropertyReader formats = new PropertyReader(
                        "/de/cismet/cids/custom/wunda_blau/res/alkis/formats.properties");
                final InputStream is = AlkisCommons.class.getClassLoader().getResourceAsStream(
                        "de/cismet/cids/custom/wunda_blau/res/alkis/Produktbeschreibung_ALKIS.xml");
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
                                                org.apache.log4j.Logger.getLogger(AlkisCommons.class).info("Can not find format dimensions for: " + dinFormatCode);
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
                                            AlkisProduct currentProduct = new AlkisProduct(
                                                    clazz,
                                                    type,
                                                    code,
                                                    dinFormat,
                                                    massstab,
                                                    fileFormat,
                                                    width,
                                                    height);

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
        } catch (Exception ex) {
            log.error("AlkisCommons Error!", ex);
        }
    }
    public static String USER;
    public static String PASSWORD;
    public static String SERVICE;
    public static String SERVER;
    public static String CATALOG_SERVICE;
    public static String INFO_SERVICE;
    public static String SEARCH_SERVICE;
    public static String SRS;
    public static String MAP_CALL_STRING;
    public static double GEO_BUFFER;
    public static String EINZEL_NACHWEIS_SERVICE;
    public static String LISTEN_NACHWEIS_SERVICE;
    public static String LIEGENSCHAFTSKARTE_SERVICE;
    //
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisCommons.class);
    private static final String NEWLINE = "<br>";
    public static final String LINK_SEPARATOR_TOKEN = "::";

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @param   bean         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateLinkFromCidsBean(final CidsBean bean, final String description) {
        if (bean != null) {
            final int objectID = bean.getMetaObject().getId();
            final StringBuilder result = new StringBuilder("<a href=\"");
//            result.append(bean.getMetaObject().getMetaClass().getID()).append(LINK_SEPARATOR_TOKEN).append(objectID);
            result.append(bean.getMetaObject().getMetaClass().getID()).append(LINK_SEPARATOR_TOKEN).append(objectID);
            result.append("\">");
            result.append(description);
            result.append("</a>");
            return result.toString();
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblatt      DOCUMENT ME!
     * @param   buchungsblattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String buchungsblattToString(final Buchungsblatt buchungsblatt, final CidsBean buchungsblattBean) {
        final List<Owner> owners = Arrays.asList(buchungsblatt.getOwners());
        if ((owners != null) && (owners.size() > 0)) {
            final StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append(
                    "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"left\" valign=\"top\">");
//            infoBuilder.append("<tr><td width=\"200\"><b><a href=\"").append(generateBuchungsblattLinkInfo(buchungsblatt)).append("\">").append(buchungsblatt.getBuchungsblattCode()).append("</a></b></td><td>");
            infoBuilder.append("<tr><td width=\"200\"><b>").append(generateLinkFromCidsBean(buchungsblattBean, buchungsblatt.getBuchungsblattCode())).append("</b></td><td>");
            final Iterator<Owner> ownerIterator = owners.iterator();
//            if (ownerIterator.hasNext()) {
//                infoBuilder.append(ownerToString(ownerIterator.next(), ""));
//            }
            infoBuilder.append(
                    "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"left\" valign=\"top\">");
            while (ownerIterator.hasNext()) {
                infoBuilder.append(ownerToString(ownerIterator.next(), ""));
//                infoBuilder.append(ownerToString(ownerIterator.next(), "</td><td>"));
            }
            infoBuilder.append("</table>");
            infoBuilder.append("</td></tr>");
            infoBuilder.append("</table>");
//            infoBuilder.append("</html>");
            return infoBuilder.toString();
//            lblBuchungsblattEigentuemer.setText(infoBuilder.toString());
        } else {
            return "";
//            lblBuchungsblattEigentuemer.setText("-");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   address  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String addressToString(final Address address) {
        if (address != null) {
            final StringBuilder addressStringBuilder = new StringBuilder();
            if (address.getStreet() != null) {
                addressStringBuilder.append(address.getStreet()).append(" ");
            }
            if (address.getHouseNumber() != null) {
                addressStringBuilder.append(address.getHouseNumber());
            }
            if (addressStringBuilder.length() > 0) {
                addressStringBuilder.append(NEWLINE);
            }
            if (address.getPostalCode() != null) {
                addressStringBuilder.append(address.getPostalCode()).append(" ");
            }
            if (address.getCity() != null) {
                addressStringBuilder.append(address.getCity());
            }
            if (addressStringBuilder.length() > 0) {
                addressStringBuilder.append(NEWLINE);
            }
            return addressStringBuilder.toString();
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   landParcel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getLandparcelCodeFromParcelBeanObject(final Object landParcel) {
        if (landParcel instanceof CidsBean) {
            final CidsBean cidsBean = (CidsBean) landParcel;
            final Object parcelCodeObj = cidsBean.getProperty("alkis_id");
            if (parcelCodeObj != null) {
                return parcelCodeObj.toString();
            }
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fullLandparcelCode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String prettyPrintLandparcelCode(final String fullLandparcelCode) {
        final String[] tiles = fullLandparcelCode.split("-");
        if (tiles.length == 1) {
            final String flurstueck = tiles[0];
            return _prettyPrintLandparcelCode(flurstueck);
        } else if (tiles.length == 2) {
            final String flurstueck = tiles[1];
            final String flur = tiles[0];
            final String result = _prettyPrintLandparcelCode(flurstueck, flur);
            return result;
        } else if (tiles.length == 3) {
            final String flurstueck = tiles[2];
            final String flur = tiles[1];
            final String gemarkung = tiles[0];
            return _prettyPrintLandparcelCode(flurstueck, flur, gemarkung);
        } else {
            return fullLandparcelCode;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   strings    DOCUMENT ME!
     * @param   separator  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String arrayToSeparatedString(final String[] strings, final String separator) {
        if (strings != null) {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < strings.length; /**incremented in loop**/
                    ) {
                result.append(strings[i]);
                if (++i < strings.length) {
                    result.append(separator);
                }
            }
            return result.toString();
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   toEscape  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String escapeHtmlSpaces(String toEscape) {
        if (toEscape != null) {
            toEscape = toEscape.replace(" ", "%20");
        }
        return toEscape;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   owner    DOCUMENT ME!
     * @param   spacing  Einrückung
     *
     * @return  DOCUMENT ME!
     */
    public static String ownerToString(final Owner owner, final String spacing) {
        if (owner != null) {
            final StringBuilder ownerStringBuilder = new StringBuilder();
            ownerStringBuilder.append("<tr><td width=\"75\">").append(spacing);
            if (owner.getNameNumber() != null) {
                ownerStringBuilder.append(normalizeNameNumber(owner.getNameNumber()));
            }
            ownerStringBuilder.append("</td><td>");
            if (owner.getForeName() != null) {
                ownerStringBuilder.append(owner.getForeName()).append(" ");
            }
            if (owner.getSurName() != null) {
                ownerStringBuilder.append(owner.getSurName());
            }
            if (owner.getSalutation() != null) {
                ownerStringBuilder.append(", ").append(owner.getSalutation());
            }
            if (owner.getDateOfBirth() != null) {
                ownerStringBuilder.append(", *").append(owner.getDateOfBirth());
            }
            if (owner.getNameOfBirth() != null) {
                ownerStringBuilder.append(NEWLINE + "geb. ").append(owner.getNameOfBirth());
            }
            ownerStringBuilder.append(NEWLINE).append("</td></tr>");
            final Address[] addresses = owner.getAddresses();
            if (addresses != null) {
                for (final Address address : addresses) {
                    if (address != null) {
                        ownerStringBuilder.append("<tr><td></td>").append(spacing).append("<td>");
                        ownerStringBuilder.append(addressToString(address)).append(NEWLINE);
                        ownerStringBuilder.append("</td></tr>");
                    }
                }
            }
            return ownerStringBuilder.toString();
        }
        return "";
    }

    /**
     * ----------------private.
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String removeLeadingZeros(final String in) {
        return in.replaceAll("^0*", "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   nameNumber  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String normalizeNameNumber(final String nameNumber) {
        final String[] tokens = nameNumber.split("\\.");
        final StringBuilder result = new StringBuilder();
        for (String token : tokens) {
            token = removeLeadingZeros(token);
            if (token.length() > 0) {
                result.append(token).append(".");
            }
        }
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
            return result.toString();
        } else {
            return "0";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblatt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String generateBuchungsblattLinkInfo(final Buchungsblatt buchungsblatt) {
        // TODO: Should return metaclassID::objectID instead of metaclassID::BuchungsblattCode...
        return new StringBuilder("ALKIS_BUCHUNGSBLATT").append(LINK_SEPARATOR_TOKEN).append(buchungsblatt.getBuchungsblattCode()).toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     * @param   flur        DOCUMENT ME!
     * @param   gemarkung   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String _prettyPrintLandparcelCode(final String flurstueck,
            final String flur,
            final String gemarkung) {
        return _prettyPrintLandparcelCode(flurstueck, flur) + " - Gemarkung " + gemarkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     * @param   flur        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String _prettyPrintLandparcelCode(final String flurstueck, final String flur) {
        return _prettyPrintLandparcelCode(flurstueck) + " - Flur " + removeLeadingZeros(flur);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueck  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String _prettyPrintLandparcelCode(final String flurstueck) {
        return "Flurstück " + prettyPrintFlurstueck(flurstueck);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fsZahlerNenner  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String prettyPrintFlurstueck(final String fsZahlerNenner) {
        final String[] tiles = fsZahlerNenner.split("/");
        if (tiles.length == 2) {
            return removeLeadingZeros(tiles[0]) + "/" + removeLeadingZeros(tiles[1]);
        } else if (tiles.length == 1) {
            return removeLeadingZeros(tiles[0]);
        }
        return fsZahlerNenner;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blatt  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getBuchungsartFromBuchungsblatt(final Buchungsblatt blatt) {
        final Buchungsstelle[] buchungsstellen = blatt.getBuchungsstellen();
        if ((buchungsstellen != null) && (buchungsstellen.length > 0)) {
            final Buchungsstelle letzteBuchungsstelle = buchungsstellen[buchungsstellen.length - 1];
            if (letzteBuchungsstelle != null) {
                final StringBuilder result = new StringBuilder();
                final String prettyFration = prettyPrintFration(letzteBuchungsstelle.getFraction());
                result.append(prettyFration);
                if (prettyFration.length() > 0) {
                    result.append(" ");
                }
                result.append(letzteBuchungsstelle.getBuchungsart());
                return result.toString();
            }
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fraction  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String prettyPrintFration(final String fraction) {
        if (fraction != null) {
            final String[] elements = fraction.split("/");
            if ((elements != null) && (elements.length == 2)) {
                String zaehler = elements[0];
                String nenner = elements[1];
                zaehler = zaehler.substring(0, zaehler.lastIndexOf("."));
                nenner = nenner.substring(0, nenner.lastIndexOf("."));
                return zaehler + "/" + nenner;
            }
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisPrintAccess() {
        try {
            return SessionManager.getConnection().getConfigAttr(SessionManager.getSession().getUser(), "navigator.alkis.print") != null;
        } catch (ConnectionException ex) {
            log.error("Could not validate action tag for Alkis Print Dialoge!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasAlkisProductAccess() {
        try {
            return SessionManager.getConnection().getConfigAttr(SessionManager.getSession().getUser(), "navigator.alkis.products") != null;
        } catch (ConnectionException ex) {
            log.error("Could not validate action tag for Alkis Products!", ex);
        }
        return false;
    }

    //~ Inner Classes ----------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class Products {

        //~ Static fields/initializers -----------------------------------------
        //Flurstueck
        public static String FLURSTUECKSNACHWEIS_PDF;
        public static String FLURSTUECKSNACHWEIS_HTML;
        public static String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_PDF;
        public static String FLURSTUECKS_UND_EIGENTUMSNACHWEIS_HTML;
        //Buchungsblatt
        public static String BESTANDSNACHWEIS_PDF;
        public static String BESTANDSNACHWEIS_HTML;
        //Punkt
        public static String PUNKTLISTE_PDF;
        public static String PUNKTLISTE_HTML;
        public static String PUNKTLISTE_TXT;
        //
        public static Map<String, Point> ALKIS_FORMATS;
        public static List<AlkisProduct> ALKIS_MAP_PRODUCTS;
        private static String IDENTIFICATION;
        //

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new Products object.
         *
         * @throws  AssertionError  DOCUMENT ME!
         */
        private Products() {
            throw new AssertionError();
        }

        //~ Methods ------------------------------------------------------------
        /**
         * DOCUMENT ME!
         *
         * @param  objectID  DOCUMENT ME!
         * @param  format      DOCUMENT ME!
         */
        public static void productEinzelNachweis(final String objectID, final String productCode) {
            final String url = EINZEL_NACHWEIS_SERVICE + "?" + IDENTIFICATION + "&product=" + productCode
                    + "&id=" + objectID;
            log.info("Open product URL : " + url);
            ObjectRendererUtils.openURL(url);

        }

        /**
         * DOCUMENT ME!
         *
         * @param  pointID   DOCUMENT ME!
         * @param  pointArt  DOCUMENT ME!
         * @param  format    DOCUMENT ME!
         */
        public static void productListenNachweis(final String pointID, final String pointArt, final String productCode) {
            productListenNachweis(pointArt + ":" + pointID, productCode);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  punktliste  DOCUMENT ME!
         * @param  format      DOCUMENT ME!
         */
        public static void productListenNachweis(final String punktliste, final String productCode) {
            final String url = LISTEN_NACHWEIS_SERVICE + "?" + IDENTIFICATION + "&product=" + productCode
                    + "&ids=" + punktliste;
            log.info("Open product URL : " + url);
            ObjectRendererUtils.openURL(url);


        }

        /**
         * DOCUMENT ME!
         *
         * @param  objectIDs   DOCUMENT ME!
         * @param  pointArts  DOCUMENT ME!
         * @param  productCode     DOCUMENT ME!
         */
        public static void productListenNachweis(final String[] objectIDs,
                final String[] pointArts,
                final String productCode) {
            final StringBuilder punktListe = new StringBuilder();
            for (int i = 0; i < objectIDs.length; ++i) {
                if (punktListe.length() > 0) {
                    punktListe.append(",");
                }
                punktListe.append(pointArts[i]).append(":").append(objectIDs[i]);
            }
            productListenNachweis(punktListe.toString(), productCode);
        }

        /**
         * DOCUMENT ME!
         *
         * @param  parcelCode  DOCUMENT ME!
         */
        public static void productKarte(final String parcelCode) {
            final String url = LIEGENSCHAFTSKARTE_SERVICE + "?" + IDENTIFICATION + "&landparcel=" + parcelCode;
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
        public static void productKarte(final String parcelCode,
                final AlkisProduct produkt,
                final int winkel,
                final int centerX,
                final int centerY,
                final String zusText,
                final boolean moreThanOneParcel) {
            String url = LIEGENSCHAFTSKARTE_SERVICE + "?" + IDENTIFICATION + "&landparcel=" + parcelCode
                    + "&angle=" + winkel
                    + "&product=" + produkt.getCode()
                    + "&centerx=" + centerX + "&centery=" + centerY;
            if ((zusText != null) && (zusText.length() > 0)) {
                url += "&text=" + zusText;
            }
            if (moreThanOneParcel) {
                url += "&additionalLandparcel=true";
            }
            url += "&";
            log.info("Open product URL : " + url);
            ObjectRendererUtils.openURL(url);
        }
    }
}
