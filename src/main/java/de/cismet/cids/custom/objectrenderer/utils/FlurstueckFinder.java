/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaObject;
import de.cismet.cids.objectrenderer.AlphanumComparator;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author srichter
 */
public class FlurstueckFinder {

    public static final String FLURSTUECK_TABLE_NAME = "alb_flurstueck_kicker";
    public static final String FLURSTUECK_GEMARKUNG = "gemarkung";
    public static final String FLURSTUECK_FLUR = "flur";
    public static final String FLURSTUECK_ZAEHLER = "zaehler";
    public static final String FLURSTUECK_NENNER = "nenner";
    //
    private static final String STMNT_LANDPARCELS = "select id," + FLURSTUECK_GEMARKUNG + "," + FLURSTUECK_FLUR + "," + FLURSTUECK_ZAEHLER + "," + FLURSTUECK_NENNER + " from " + FLURSTUECK_TABLE_NAME + " order by " + FLURSTUECK_GEMARKUNG + ", " + FLURSTUECK_FLUR + ", " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER;

    public static final MetaObject[] getLWLandparcels() {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                FLURSTUECK_TABLE_NAME, STMNT_LANDPARCELS, new String[]{FLURSTUECK_GEMARKUNG, FLURSTUECK_FLUR, FLURSTUECK_ZAEHLER, FLURSTUECK_NENNER}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                StringBuilder result = new StringBuilder(30);
                result.append(getAttribute(FLURSTUECK_GEMARKUNG)).append("-");
                result.append(getAttribute(FLURSTUECK_FLUR)).append("-");
                result.append(getAttribute(FLURSTUECK_ZAEHLER));
                final Object nenner = getAttribute(FLURSTUECK_NENNER);
                if (nenner != null) {
                    result.append("/").append(nenner);
                }
                return result.toString();
            }
        });
    }

    public static final MetaObject[] getLWGemarkungen() {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_GEMARKUNG + " from " + FLURSTUECK_TABLE_NAME + " group by " + FLURSTUECK_GEMARKUNG + " order by " + FLURSTUECK_GEMARKUNG, new String[]{"id", FLURSTUECK_GEMARKUNG}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute(FLURSTUECK_GEMARKUNG));
            }
        });
    }

    public static final MetaObject[] getLWFlure(String gemarkungsnummer) {
        MetaObject[] result = ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_FLUR + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " group by " + FLURSTUECK_FLUR + " order by " + FLURSTUECK_FLUR, new String[]{"id", FLURSTUECK_FLUR}, new AbstractAttributeRepresentationFormater() {
//                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_FLUR + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " group by " + FLURSTUECK_FLUR, new String[]{"id", FLURSTUECK_FLUR}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute(FLURSTUECK_FLUR));
            }
        });
//        Arrays.sort(result, AlphanumComparator.getInstance());
        return result;
    }

    public static final MetaObject[] getLWFurstuecksZaehlerNenner(String gemarkungsnummer, String flur) {
        MetaObject[] result = ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                //                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " and " + FLURSTUECK_FLUR + " = '" + flur + "' group by " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER + " order by " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER
                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " and " + FLURSTUECK_FLUR + " = '" + flur + "' group by " + FLURSTUECK_ZAEHLER + ", " + FLURSTUECK_NENNER, new String[]{"id", FLURSTUECK_ZAEHLER, FLURSTUECK_NENNER}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                final Object nenner = getAttribute(FLURSTUECK_NENNER);
                final StringBuilder result = new StringBuilder();
                result.append(getAttribute(FLURSTUECK_ZAEHLER));
                if (nenner != null) {
                    result.append("/").append(nenner);
                }
                return result.toString();
            }
        });
        Arrays.sort(result, ZAEHLER_NENNER_COMPARATOR);
        return result;
    }
    private static final Comparator<MetaObject> ZAEHLER_NENNER_COMPARATOR = new Comparator<MetaObject>() {

        @Override
        public final int compare(MetaObject o1, MetaObject o2) {
            //DANGER!
            LightweightMetaObject lwmo1 = (LightweightMetaObject) o1;
            LightweightMetaObject lwmo2 = (LightweightMetaObject) o2;
            int res = AlphanumComparator.getInstance().compare(lwmo1.getLWAttribute(FLURSTUECK_NENNER), lwmo2.getLWAttribute(FLURSTUECK_NENNER));
            if (res == 0) {
                res = AlphanumComparator.getInstance().compare(lwmo1.getLWAttribute(FLURSTUECK_ZAEHLER), lwmo2.getLWAttribute(FLURSTUECK_ZAEHLER));
            }
            return res;
        }
    };

    public static final MetaObject[] getLWFurstuecksZaehler(String gemarkungsnummer, String flur) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                //                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_ZAEHLER + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " and " + FLURSTUECK_FLUR + " = '" + flur + "' group by " + FLURSTUECK_ZAEHLER + " order by " + FLURSTUECK_ZAEHLER
                FLURSTUECK_TABLE_NAME, "select min(id) as id, " + FLURSTUECK_ZAEHLER + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " and " + FLURSTUECK_FLUR + " = '" + flur + "' group by " + FLURSTUECK_ZAEHLER, new String[]{"id", FLURSTUECK_ZAEHLER}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute(FLURSTUECK_ZAEHLER));
            }
        });
    }

    public static final MetaObject[] getLWFurstuecksNenner(String gemarkungsnummer, String flur, String zaehler) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery(
                FLURSTUECK_TABLE_NAME, "select id, " + FLURSTUECK_NENNER + " from " + FLURSTUECK_TABLE_NAME + " where " + FLURSTUECK_GEMARKUNG + " = " + gemarkungsnummer + " and " + FLURSTUECK_FLUR + " = '" + flur + "' and " + FLURSTUECK_ZAEHLER + " = " + zaehler + " order by " + FLURSTUECK_NENNER, new String[]{"id", FLURSTUECK_NENNER}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute(FLURSTUECK_NENNER));
            }
        });
    }
}
