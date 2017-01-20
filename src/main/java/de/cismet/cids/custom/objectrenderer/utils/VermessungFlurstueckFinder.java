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
package de.cismet.cids.custom.objectrenderer.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.AbstractAttributeRepresentationFormater;
import Sirius.server.middleware.types.LightweightMetaObject;
import Sirius.server.middleware.types.MetaObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import de.cismet.cids.custom.wunda_blau.search.server.VeraenderungsartLightweightSearch;
import de.cismet.cids.custom.wunda_blau.search.server.VermessungFlurstueckKickerLightweightSearch;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class VermessungFlurstueckFinder {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VermessungFlurstueckFinder.class);

    public static final String FLURSTUECK_KICKER_TABLE_NAME = "vermessung_flurstueck_kicker";

    public static final String FLURSTUECK_GEMARKUNG = "gemarkung";
    public static final String FLURSTUECK_FLUR = "flur";
    public static final String FLURSTUECK_ZAEHLER = "zaehler";
    public static final String FLURSTUECK_NENNER = "nenner";

    public static final String GEMARKUNG_TABLE_NAME = "gemarkung";
    public static final String GEMARKUNG_ID = "gemarkungsnummer";
    public static final String GEMARKUNG_NAME = "name";

    public static final String VERMESSUNG_FLURSTUECKSVERMESSUNG_TABLE_NAME = "vermessung_flurstuecksvermessung";
    public static final String VERMESSUNG_FLURSTUECKSVERMESSUNG_FLURSTUECK = "flurstueck";
    public static final String VERMESSUNG_FLURSTUECKSVERMESSUNG_VERMESSUNGSART = "veraenderungsart";

    public static final String VERMESSUNG_GEMARKUNG_TABLE_NAME = "vermessung_gemarkung";
    public static final String VERMESSUNG_GEMARKUNG_ID = "id";
    public static final String VERMESSUNG_GEMARKUNG_NAME = "name";

    public static final String VERMESSUNG_VERAENDERUNGSART_TABLE_NAME = "vermessung_art";
    public static final String VERMESSUNG_VERAENDERUNGSART_ID = "id";
    public static final String VERMESSUNG_VERAENDERUNGSART_CODE = "code";
    public static final String VERMESSUNG_VERAENDERUNGSART_NAME = "name";

    public static final String SEP = " - ";

    private static final Comparator<MetaObject> ZAEHLER_NENNER_COMPARATOR = new Comparator<MetaObject>() {

            @Override
            public final int compare(final MetaObject o1, final MetaObject o2) {
                // DANGER!
                // Only Stefan Richter knows why. Maybe it's the cast?
                final LightweightMetaObject lwmo1 = (LightweightMetaObject)o1;
                final LightweightMetaObject lwmo2 = (LightweightMetaObject)o2;

                int result = AlphanumComparator.getInstance()
                            .compare(lwmo1.getLWAttribute(FLURSTUECK_NENNER), lwmo2.getLWAttribute(FLURSTUECK_NENNER));
                if (result == 0) {
                    result = AlphanumComparator.getInstance()
                                .compare(lwmo1.getLWAttribute(FLURSTUECK_ZAEHLER),
                                        lwmo2.getLWAttribute(FLURSTUECK_ZAEHLER));
                }

                return result;
            }
        };

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject[] getLWLandparcels() {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.ALLE_FLUSTUECKE);
            search.setRepresentationFields(
                new String[] { FLURSTUECK_GEMARKUNG, FLURSTUECK_FLUR, FLURSTUECK_ZAEHLER, FLURSTUECK_NENNER });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            // TODO: Check the capacity of the StringBuilder.
                            final StringBuilder result = new StringBuilder(30);
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
            return lwmos.toArray(new MetaObject[0]);
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject[] getLWGemarkungen() {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.ALLE_GEMARKUNGEN);
            search.setRepresentationFields(new String[] { "id", FLURSTUECK_GEMARKUNG, VERMESSUNG_GEMARKUNG_NAME });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            return String.valueOf(getAttribute(FLURSTUECK_GEMARKUNG));
                        }
                    });
            }
            return lwmos.toArray(new MetaObject[0]);
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject getLWGemarkung(final int gemarkung) {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.GEMARKUNG);
            search.setGemarkungsnummer(Integer.toString(gemarkung));
            search.setRepresentationFields(new String[] { "id", VERMESSUNG_GEMARKUNG_NAME });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            return String.valueOf(getAttribute(VERMESSUNG_GEMARKUNG_NAME));
                        }
                    });
            }
            return lwmos.toArray(new MetaObject[0])[0];
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkungsnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject[] getLWFlure(final String gemarkungsnummer) {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.FLURE);
            search.setGemarkungsnummer(gemarkungsnummer);
            search.setRepresentationFields(new String[] { "id", FLURSTUECK_FLUR });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            return String.valueOf(getAttribute(FLURSTUECK_FLUR));
                        }
                    });
            }
            return lwmos.toArray(new MetaObject[0]);
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MetaObject[] getVeraenderungsarten() {
        try {
            final VeraenderungsartLightweightSearch search = new VeraenderungsartLightweightSearch();
            search.setRepresentationFields(
                new String[] { "id", VERMESSUNG_VERAENDERUNGSART_CODE, VERMESSUNG_VERAENDERUNGSART_NAME });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            final StringBuilder result = new StringBuilder();

                            result.append(getAttribute(VERMESSUNG_VERAENDERUNGSART_CODE));
                            result.append(" (");
                            result.append(getAttribute(VERMESSUNG_VERAENDERUNGSART_NAME));
                            result.append(')');

                            return result.toString();
                        }
                    });
            }
            return lwmos.toArray(new MetaObject[0]);
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkungsnummer  DOCUMENT ME!
     * @param   flur              DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject[] getLWFurstuecksZaehlerNenner(final String gemarkungsnummer, final String flur) {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.ZAEHLER_NENNER);
            search.setGemarkungsnummer(gemarkungsnummer);
            search.setFlur(flur);
            search.setRepresentationFields(new String[] { "id", FLURSTUECK_ZAEHLER, FLURSTUECK_NENNER });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            final StringBuilder result = new StringBuilder();

                            result.append(getAttribute(FLURSTUECK_ZAEHLER));

                            final Object nenner = getAttribute(FLURSTUECK_NENNER);
                            if (nenner != null) {
                                result.append("/").append(nenner);
                            }

                            return result.toString();
                        }
                    });
            }
            final MetaObject[] result = lwmos.toArray(new MetaObject[0]);
            Arrays.sort(result, ZAEHLER_NENNER_COMPARATOR);
            return result;
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkungsnummer  DOCUMENT ME!
     * @param   flur              DOCUMENT ME!
     * @param   zaehler           DOCUMENT ME!
     * @param   nenner            DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static final MetaObject[] getLWLandparcel(final String gemarkungsnummer,
            final String flur,
            final String zaehler,
            final String nenner) {
        try {
            final VermessungFlurstueckKickerLightweightSearch search =
                new VermessungFlurstueckKickerLightweightSearch();
            search.setSearchFor(VermessungFlurstueckKickerLightweightSearch.SearchFor.FLURSTUECKE);
            search.setGemarkungsnummer(gemarkungsnummer);
            search.setFlur(flur);
            search.setZaehler(zaehler);
            search.setNenner(nenner);
            search.setRepresentationFields(
                new String[] { "id", FLURSTUECK_GEMARKUNG, FLURSTUECK_FLUR, FLURSTUECK_ZAEHLER, FLURSTUECK_NENNER });
            final Collection<LightweightMetaObject> lwmos = SessionManager.getProxy().customServerSearch(search);
            for (final LightweightMetaObject lwmo : lwmos) {
                lwmo.setFormater(new AbstractAttributeRepresentationFormater() {

                        @Override
                        public String getRepresentation() {
                            // TODO: Check capacity in StringBuilder
                            final StringBuilder result = new StringBuilder(30);

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
            final MetaObject[] result = lwmos.toArray(new MetaObject[0]);
            Arrays.sort(result, ZAEHLER_NENNER_COMPARATOR);
            return result;
        } catch (final ConnectionException ex) {
            LOG.error(ex, ex);
            return null;
        }
    }
}
