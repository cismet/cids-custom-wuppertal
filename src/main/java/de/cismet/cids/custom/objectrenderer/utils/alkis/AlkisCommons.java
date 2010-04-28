/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.aedsicad.aaaweb.service.util.Address;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.Owner;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.objectrenderer.utils.PropertyReader;
import de.cismet.cids.dynamics.CidsBean;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author srichter
 */
public final class AlkisCommons {

    public static enum LiegenschaftskarteProdukt {

        FARBIG("LK, farbig", "F"), SCHWARZWEISS("LK, sw", "SW"), FARBIG_MIT_BODENSCH("LK mit Bodensch., farbig", "BF"), SCHWARZWEISS_MIT_BODENSCH("LK mit Bodensch., sw", "BSW");

        private LiegenschaftskarteProdukt(String description, String code) {
            this.descriptionString = description;
            this.codeString = code;
        }
        private final String descriptionString;
        private final String codeString;

        @Override
        public String toString() {
            return descriptionString;
        }

        public String getCode() {
            return codeString;
        }
    }

    public static enum ProduktFormat {

        PDF("PDF"), HTML("HTML"), TEXT("TEXT");

        private ProduktFormat(String string) {
            this.formatString = string;
        }
        private final String formatString;

        @Override
        public String toString() {
            return formatString;
        }

        ;
    }
    private static final String FORMAT_FILE = "/de/cismet/cids/custom/wunda_blau/res/alkis/formats.properties";
    private static final PropertyReader FORMATS = new PropertyReader(FORMAT_FILE);

    public static enum ProduktLayout {

        A4Hoch("DINA4 Hochformat", "A4H"), A4Quer("DINA4 Querformat", "A4Q"), A3Hoch("DINA3 Hochformat", "A3H"), A3Quer("DINA3 Querformat", "A3Q");

        private ProduktLayout(String description, String code) {
            this.description = description;
            this.code = code;
            final String compoundFormatString = FORMATS.getProperty(code);
            final String[] dimensions = compoundFormatString.split("x|X");
            this.width = Integer.parseInt(dimensions[0]);
            this.height = Integer.parseInt(dimensions[1]);
        }
        public final int width, height;
        private final String description;
        private final String code;

        @Override
        public String toString() {
            return description;
        }

        public String getCode() {
            return code;
        }

        ;
    }

    public static final class MapKonstanten {

        private MapKonstanten() {
            throw new AssertionError();
        }
        public static final String SRS = "EPSG:31466";
        public static final String CALL_STRING = "http://s102x082.wuppertal-intra.de:8080/wmsconnector/com.esri.wms.Esrimap/web_navigation_lf?&VERSION=1.1.1&REQUEST=GetMap&SRS=" + SRS + "&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0&STYLES="
                + "&BBOX=<cismap:boundingBox>"
                + "&WIDTH=<cismap:width>"
                + "&HEIGHT=<cismap:height>";
        public static final double GEO_BUFFER = 5.0;
    }

    public static final class Produkte {

        private static final String PRODUCT_BESTANDSNACHWEIS = "BESTANDSNACHWEIS";
        private static final String PRODUCT_FLURSTUECKSNACHWEIS = "FLURSTUECKSNACHWEIS";
        private static final String PRODUCT_EIGENTUMSNACHWEIS = "EIGENTUMSNACHWEIS";
        private static final String PRODUCT_PUNKTLISTE = "PUNKTLISTE";
        private static final String PRODUCT_LIEGENSCHAFTSKARTE = "LIEGENSCHAFTSKARTE";
        //
        private static final String PRODUCT_CODES_FILE = "/de/cismet/cids/custom/wunda_blau/res/alkis/produkte.properties";
        private static final PropertyReader PRODUCT_CODES = new PropertyReader(PRODUCT_CODES_FILE);
        //
        private static final String IDENTIFICATION = "user=" + SOAPAccessProvider.USER + "&password=" + SOAPAccessProvider.PASSWORD + "&service=" + SOAPAccessProvider.SERVICE;
        //

        private Produkte() {
            throw new AssertionError();
        }

        public static void productBestandsnachweisProduct(String buchungsblattCode, ProduktFormat format) {
            String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?" + IDENTIFICATION + "&product=" + PRODUCT_CODES.getProperty(PRODUCT_BESTANDSNACHWEIS) + "&id=" + buchungsblattCode + "&contentType=" + format + "&certificationType=9701";
            ObjectRendererUtils.openURL(url);
        }

        public static void productFlurstuecksnachweis(String parcelCode, ProduktFormat format) {
            String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?" + IDENTIFICATION + "&product=" + PRODUCT_CODES.getProperty(PRODUCT_FLURSTUECKSNACHWEIS) + "&id=" + parcelCode + "&contentType=" + format + "&certificationType=9511";
            ObjectRendererUtils.openURL(url);
        }

        public static void productFlurstuecksEigentumsnachweis(String parcelCode, ProduktFormat format) {
            String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISBuchNachweis?" + IDENTIFICATION + "&product=" + PRODUCT_CODES.getProperty(PRODUCT_EIGENTUMSNACHWEIS) + "&id=" + parcelCode + "&contentType=" + format + "&certificationType=9551";
            ObjectRendererUtils.openURL(url);
        }

        public static void productPunktliste(String pointID, String pointArt, ProduktFormat format) {
            productPunktliste(pointArt + ":" + pointID, format);
        }

        public static void productPunktliste(String punktliste, ProduktFormat format) {
            final String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISListenNachweis?" + IDENTIFICATION + "&product=" + PRODUCT_CODES.getProperty(PRODUCT_PUNKTLISTE + "_" + format) + "&ids=" + punktliste;
            ObjectRendererUtils.openURL(url);
        }

        public static void productPunktliste(String[] pointIDs, String[] pointArts, ProduktFormat format) {
            StringBuffer punktListe = new StringBuffer();
            for (int i = 0; i < pointIDs.length; ++i) {
                if (punktListe.length() > 0) {
                    punktListe.append(",");
                }
                punktListe.append(pointArts[i]).append(":").append(pointIDs[i]);
            }
            productPunktliste(punktListe.toString(), format);
        }

        public static void productKarte(String parcelCode) {
            String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISLiegenschaftskarte?" + IDENTIFICATION + "&landparcel=" + parcelCode;
            ObjectRendererUtils.openURL(url);
        }

        public static void productKarte(String parcelCode, LiegenschaftskarteProdukt produkt, ProduktLayout layout, int massstab, int winkel, int centerX, int centerY, String zusText, boolean moreThanOneParcel) {
            String url = "http://s102x083:8080/ASWeb34/ASA_AAAWeb/ALKISLiegenschaftskarte?" + IDENTIFICATION + "&landparcel=" + parcelCode
                    + "&angle=" + winkel
                    + "&product=" + PRODUCT_CODES.getProperty(PRODUCT_LIEGENSCHAFTSKARTE + "_" + produkt.getCode() + "_PDF_" + layout.getCode() + "_" + massstab)
                    + "&centerx=" + centerX + "&centery=" + centerY;
            if (zusText != null && zusText.length() > 0) {
                url += "&text=" + zusText;
            }
            if (moreThanOneParcel) {
                url += "&additionalLandparcel=true";
            }
            url += "&";
            ObjectRendererUtils.openURL(url);
        }
    }

    public static final String generateLinkFromCidsBean(CidsBean bean, String description) {
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
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisCommons.class);
    private static final String NEWLINE = "<br>";
    public static final String LINK_SEPARATOR_TOKEN = "::";

    public static final String buchungsblattToString(Buchungsblatt buchungsblatt, CidsBean buchungsblattBean) {
        final List<Owner> owners = Arrays.asList(buchungsblatt.getOwners());
        if (owners != null && owners.size() > 0) {
            final StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"left\" valign=\"top\">");
//            infoBuilder.append("<tr><td width=\"200\"><b><a href=\"").append(generateBuchungsblattLinkInfo(buchungsblatt)).append("\">").append(buchungsblatt.getBuchungsblattCode()).append("</a></b></td><td>");
            infoBuilder.append("<tr><td width=\"200\"><b>" + generateLinkFromCidsBean(buchungsblattBean, buchungsblatt.getBuchungsblattCode()) + "</b></td><td>");
            final Iterator<Owner> ownerIterator = owners.iterator();
//            if (ownerIterator.hasNext()) {
//                infoBuilder.append(ownerToString(ownerIterator.next(), ""));
//            }
            infoBuilder.append("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"left\" valign=\"top\">");
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

    public static final String addressToString(Address address) {
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

    public static String getLandparcelCodeFromParcelBeanObject(Object landParcel) {
        if (landParcel instanceof CidsBean) {
            CidsBean cidsBean = (CidsBean) landParcel;
            final Object parcelCodeObj = cidsBean.getProperty("alkis_id");
            if (parcelCodeObj != null) {
                return parcelCodeObj.toString();
            }
        }
        return "";
    }

    public static String prettyPrintLandparcelCode(String fullLandparcelCode) {
        final String[] tiles = fullLandparcelCode.split("-");
        if (tiles.length == 1) {
            String flurstueck = tiles[0];
            return _prettyPrintLandparcelCode(flurstueck);
        } else if (tiles.length == 2) {
            String flurstueck = tiles[1];
            String flur = tiles[0];
            String result = _prettyPrintLandparcelCode(flurstueck, flur);
            return result;
        } else if (tiles.length == 3) {
            String flurstueck = tiles[2];
            String flur = tiles[1];
            String gemarkung = tiles[0];
            return _prettyPrintLandparcelCode(flurstueck, flur, gemarkung);
        } else {
            return fullLandparcelCode;
        }
    }

    public static String arrayToSeparatedString(String[] strings, String separator) {
        if (strings != null) {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < strings.length;/**incremented in loop**/
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

    public static String escapeHtmlSpaces(String toEscape) {
        if (toEscape != null) {
            toEscape = toEscape.replace(" ", "%20");
        }
        return toEscape;
    }

    /**
     *
     * @param owner
     * @param spacing Einrückung
     * @return
     */
    public static String ownerToString(Owner owner, String spacing) {
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

    //----------------private
    private static final String removeLeadingZeros(String in) {
        return in.replaceAll("^0*", "");
    }

    private static String normalizeNameNumber(String nameNumber) {
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

    private static String generateBuchungsblattLinkInfo(Buchungsblatt buchungsblatt) {
        //TODO: Should return metaclassID::objectID instead of metaclassID::BuchungsblattCode...
        return new StringBuilder("ALKIS_BUCHUNGSBLATT").append(LINK_SEPARATOR_TOKEN).append(buchungsblatt.getBuchungsblattCode()).toString();
    }

    private static String _prettyPrintLandparcelCode(String flurstueck, String flur, String gemarkung) {
        return _prettyPrintLandparcelCode(flurstueck, flur) + " - Gemarkung " + gemarkung;
    }

    private static String _prettyPrintLandparcelCode(String flurstueck, String flur) {
        return _prettyPrintLandparcelCode(flurstueck) + " - Flur " + removeLeadingZeros(flur);
    }

    private static String _prettyPrintLandparcelCode(String flurstueck) {
        return "Flurstück " + prettyPrintFlurstueck(flurstueck);
    }

    private static String prettyPrintFlurstueck(String fsZahlerNenner) {
        String[] tiles = fsZahlerNenner.split("/");
        if (tiles.length == 2) {
            return removeLeadingZeros(tiles[0]) + "/" + removeLeadingZeros(tiles[1]);
        } else if (tiles.length == 1) {
            return removeLeadingZeros(tiles[0]);
        }
        return fsZahlerNenner;
    }

    public static String getBuchungsartFromBuchungsblatt(Buchungsblatt blatt) {
        final Buchungsstelle[] buchungsstellen = blatt.getBuchungsstellen();
        if (buchungsstellen != null && buchungsstellen.length > 0) {
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

    public static String prettyPrintFration(String fraction) {
        if (fraction != null) {
            final String[] elements = fraction.split("/");
            if (elements != null && elements.length == 2) {
                String zaehler = elements[0];
                String nenner = elements[1];
                zaehler = zaehler.substring(0, zaehler.lastIndexOf("."));
                nenner = nenner.substring(0, nenner.lastIndexOf("."));
                return zaehler + "/" + nenner;
            }
        }
        return "";
    }
}
