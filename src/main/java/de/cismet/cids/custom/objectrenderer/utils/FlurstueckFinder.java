/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.MetaObject;

/**
 *
 * @author srichter
 */
public class FlurstueckFinder {

    public static final MetaObject[] getLWLandparcels() {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("flurstueck", "select id,gemarkungs_nr, flur, fstnr_z, fstnr_n from flurstueck order by gemarkungs_nr, flur, fstnr_z, fstnr_n", new String[]{"gemarkungs_nr", "flur", "fstnr_z", "fstnr_n"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                StringBuilder result = new StringBuilder(20);
                result.append(getAttribute("gemarkungs_nr")).append("-");
                result.append(getAttribute("flur")).append("-");
                result.append(getAttribute("fstnr_z"));
                final Object nenner = getAttribute("fstnr_n");
                if (nenner != null) {
                    result.append("/").append(nenner);
                }
                return result.toString();
            }
        });
    }

    public static final MetaObject[] getLWGemarkungen() {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("gemarkung", "select gemarkungsnummer, name from gemarkung order by name", new String[]{"gemarkungsnummer", "name"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute("gemarkungsnummer"));
            }
        });
    }

    public static final MetaObject[] getLWFlure(String gemarkungsnummer) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("flurstueck", "select min(id) as id, flur from flurstueck where gemarkungs_nr = " + gemarkungsnummer + " group by flur order by flur", new String[]{"id", "flur"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute("flur"));
            }
        });
    }

    public static final MetaObject[] getLWFurstuecksZaehlerNenner(String gemarkungsnummer, String flur) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("flurstueck", "select min(id) as id, fstnr_z, fstnr_n from flurstueck where gemarkungs_nr = " + gemarkungsnummer + " and flur = '" + flur + "' group by fstnr_z, fstnr_n order by fstnr_z, fstnr_n", new String[]{"id", "fstnr_z", "fstnr_n"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                final Object nenner = getAttribute("fstnr_n");
                final StringBuilder result = new StringBuilder();
                result.append(getAttribute("fstnr_z"));
                if (nenner != null) {
                    result.append("/").append(nenner);
                }
                return result.toString();
            }
        });
    }

    public static final MetaObject[] getLWFurstuecksZaehler(String gemarkungsnummer, String flur) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("flurstueck", "select min(id) as id, fstnr_z from flurstueck where gemarkungs_nr = " + gemarkungsnummer + " and flur = '" + flur + "' group by fstnr_z order by fstnr_z", new String[]{"id", "fstnr_z"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute("fstnr_z"));
            }
        });
    }

    public static final MetaObject[] getLWFurstuecksNenner(String gemarkungsnummer, String flur, String zaehler) {
        return ObjectRendererUtils.getLightweightMetaObjectsForQuery("flurstueck", "select id, fstnr_n from flurstueck where gemarkungs_nr = " + gemarkungsnummer + " and flur = '" + flur + "' and fstnr_z = " + zaehler + " order by fstnr_n", new String[]{"id", "fstnr_n"}, new AbstractAttributeRepresentationFormater() {

            @Override
            public String getRepresentation() {
                return String.valueOf(getAttribute("fstnr_n"));
            }
        });
    }
}
