/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.aedsicad.aaaweb.service.util.Address;
import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Owner;
import de.cismet.cids.dynamics.CidsBean;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author srichter
 */
public class AlkisCommons {

    public static final class MAP_CONSTANTS {

        private MAP_CONSTANTS() {
        }
        public static final String SRS = "EPSG:31466";
        public static final String CALL_STRING = "http://s102x082.wuppertal-intra.de:8080/wmsconnector/com.esri.wms.Esrimap/web_navigation_lf?&VERSION=1.1.1&REQUEST=GetMap&SRS=" + SRS + "&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0&STYLES=" +
                "&BBOX=<cismap:boundingBox>" +
                "&WIDTH=<cismap:width>" +
                "&HEIGHT=<cismap:height>";
        public static final double GEO_BUFFER = 5.0;
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
        List<Owner> owners = Arrays.asList(buchungsblatt.getOwners());
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

    public static final String prettyPrintLandparcelCode(String fullLandparcelCode) {
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

    public static final String arrayToSeparatedString(String[] strings, String separator) {
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

    public static final String escapeHtmlSpaces(String toEscape) {
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
    public static final String ownerToString(Owner owner, String spacing) {
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

    private static final String normalizeNameNumber(String nameNumber) {
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

    private static final String generateBuchungsblattLinkInfo(Buchungsblatt buchungsblatt) {
        //TODO: Should return metaclassID::objectID instead of metaclassID::BuchungsblattCode...
        return new StringBuilder("ALKIS_BUCHUNGSBLATT").append(LINK_SEPARATOR_TOKEN).append(buchungsblatt.getBuchungsblattCode()).toString();
    }

    private static final String _prettyPrintLandparcelCode(String flurstueck, String flur, String gemarkung) {
        return _prettyPrintLandparcelCode(flurstueck, flur) + " - Gemarkung " + gemarkung;
    }

    private static final String _prettyPrintLandparcelCode(String flurstueck, String flur) {
        return _prettyPrintLandparcelCode(flurstueck) + " - Flur " + removeLeadingZeros(flur);
    }

    private static final String _prettyPrintLandparcelCode(String flurstueck) {
        return "Flurstück " + prettyPrintFlurstueck(flurstueck);
    }

    private static final String prettyPrintFlurstueck(String fsZahlerNenner) {
        String[] tiles = fsZahlerNenner.split("/");
        if (tiles.length == 2) {
            return removeLeadingZeros(tiles[0]) + "/" + removeLeadingZeros(tiles[1]);
        } else if (tiles.length == 1) {
            return removeLeadingZeros(tiles[0]);
        }
        return fsZahlerNenner;
    }
}
