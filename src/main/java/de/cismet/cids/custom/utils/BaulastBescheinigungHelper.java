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
package de.cismet.cids.custom.utils;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

import de.aedsicad.aaaweb.service.util.Buchungsblatt;
import de.aedsicad.aaaweb.service.util.Buchungsstelle;
import de.aedsicad.aaaweb.service.util.LandParcel;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungInfo;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastBescheinigungHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaulastBescheinigungHelper.class);
    private static final Map<CidsBean, Buchungsblatt> BUCHUNGSBLATT_CACHE = new HashMap<CidsBean, Buchungsblatt>();

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaulastBescheinigungHelper object.
     */
    protected BaulastBescheinigungHelper() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                           DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   protocolBuffer                        DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void fillFlurstueckeToBaulastenMaps(final Collection<CidsBean> flurstuecke,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final StringBuffer protocolBuffer) throws Exception {
        protocolBuffer.append("\n===\n");

        // belastete Baulasten pro Flurstück
        flurstueckeToBaulastenBelastetMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, true, protocolBuffer));

        // begünstigte Baulasten pro Flurstück
        flurstueckeToBaulastenBeguenstigtMap.putAll(createFlurstueckeToBaulastenMap(
                flurstuecke,
                false,
                protocolBuffer));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public MetaService getMetaService() {
        return DomainServerImpl.getServerInstance();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   serverSearch       DOCUMENT ME!
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected Collection executeSearch(final CidsServerSearch serverSearch,
            final User user,
            final ConnectionContext connectionContext) throws Exception {
        final Map localServers = new HashMap<>();
        localServers.put("WUNDA_BLAU", getMetaService());
        serverSearch.setActiveLocalServers(localServers);
        serverSearch.setUser(user);
        if (serverSearch instanceof ConnectionContextStore) {
            ((ConnectionContextStore)serverSearch).initWithConnectionContext(connectionContext);
        }
        return serverSearch.performServerSearch();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   oid   DOCUMENT ME!
     * @param   cid   DOCUMENT ME!
     * @param   user  DOCUMENT ME!
     * @param   cc    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected MetaObject getMetaObject(final int oid, final int cid, final User user, final ConnectionContext cc)
            throws Exception {
        return DomainServerImpl.getServerInstance().getMetaObject(user, oid, cid, cc);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   query              DOCUMENT ME!
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected MetaObject[] getMetaObjects(final String query,
            final User user,
            final ConnectionContext connectionContext) throws Exception {
        return DomainServerImpl.getServerInstance().getMetaObject(user, query, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke     DOCUMENT ME!
     * @param   belastet        DOCUMENT ME!
     * @param   protocolBuffer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Map<CidsBean, Set<CidsBean>> createFlurstueckeToBaulastenMap(final Collection<CidsBean> flurstuecke,
            final boolean belastet,
            final StringBuffer protocolBuffer) throws Exception {
        final String queryBeguenstigt = "SELECT %d, alb_baulast.%s \n"
                    + "FROM alb_baulast_flurstuecke_beguenstigt, alb_baulast, alb_flurstueck_kicker, flurstueck \n"
                    + "WHERE alb_baulast.id = alb_baulast_flurstuecke_beguenstigt.baulast_reference \n"
                    + "AND alb_baulast_flurstuecke_beguenstigt.flurstueck = alb_flurstueck_kicker.id \n"
                    + "AND alb_flurstueck_kicker.fs_referenz = flurstueck.id \n"
                    + "AND flurstueck.alkis_id ilike '%s' \n"
                    + "AND alb_baulast.geschlossen_am is null AND alb_baulast.loeschungsdatum is null";

        final String queryBelastet = "SELECT %d, alb_baulast.%s \n"
                    + "FROM alb_baulast_flurstuecke_belastet, alb_baulast, alb_flurstueck_kicker, flurstueck \n"
                    + "WHERE alb_baulast.id = alb_baulast_flurstuecke_belastet.baulast_reference \n"
                    + "AND alb_baulast_flurstuecke_belastet.flurstueck = alb_flurstueck_kicker.id \n"
                    + "AND alb_flurstueck_kicker.fs_referenz = flurstueck.id \n"
                    + "AND flurstueck.alkis_id ilike '%s' \n"
                    + "AND alb_baulast.geschlossen_am is null AND alb_baulast.loeschungsdatum is null";

        final MetaClass mcBaulast = ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                "alb_baulast",
                getConnectionContext());

        final String query = belastet ? queryBelastet : queryBeguenstigt;

        protocolBuffer.append("\nSuche der ")
                .append((belastet) ? "belastenden" : "begünstigenden")
                .append(" Baulasten von:\n");
        final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenMap = new HashMap<>();
        for (final CidsBean flurstueck : flurstuecke) {
            protocolBuffer.append(" * Flurstück: ").append(flurstueck).append(" ...\n");
            final Set<CidsBean> baulasten = new HashSet<>();
            try {
                final BaulastSearchInfo searchInfo = new BaulastSearchInfo();
                final Integer gemarkung = Integer.parseInt(((String)flurstueck.getProperty("alkis_id")).substring(
                            2,
                            6));
                final String flur = (String)flurstueck.getProperty("flur");
                final String zaehler = Integer.toString(Integer.parseInt(
                            (String)flurstueck.getProperty("fstck_zaehler")));
                final String nenner = (flurstueck.getProperty("fstck_nenner") == null)
                    ? "0" : Integer.toString(Integer.parseInt((String)flurstueck.getProperty("fstck_nenner")));

                final FlurstueckInfo fsi = new FlurstueckInfo(gemarkung, flur, zaehler, nenner);
                searchInfo.setFlurstuecke(Arrays.asList(fsi));
                searchInfo.setResult(CidsBaulastSearchStatement.Result.BAULAST);
                searchInfo.setBelastet(belastet);
                searchInfo.setBeguenstigt(!belastet);
                searchInfo.setBlattnummer("");
                searchInfo.setArt("");
                final CidsBaulastSearchStatement search = new CidsBaulastSearchStatement(
                        searchInfo,
                        mcBaulast.getId(),
                        -1);

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                final Collection<MetaObjectNode> mons = executeSearch(search, null, getConnectionContext());
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = getMetaObject(mon.getObjectId(),
                            mon.getClassId(),
                            null,
                            getConnectionContext());
                    if ((mo.getBean() != null) && (mo.getBean() != null)
                                && (mo.getBean().getProperty("loeschungsdatum") != null)) {
                        continue;
                    }
                    if (mon.getName().startsWith("indirekt: ")) {
                        throw new BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                }

                final String alkisId = (String)flurstueck.getProperty("alkis_id");

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                final MetaObject[] mos = getMetaObjects(String.format(
                            query,
                            mcBaulast.getID(),
                            mcBaulast.getPrimaryKey(),
                            alkisId),
                        null,
                        getConnectionContext());
                for (final MetaObject mo : mos) {
                    final CidsBean baulast = mo.getBean();
                    final Boolean geprueft = (Boolean)baulast.getProperty("geprueft");
                    if ((geprueft == null) || (geprueft == false)) {
                        throw new BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                    protocolBuffer.append("   => Baulast: ").append(baulast).append("\n");
                    baulasten.add(baulast);
                }
                flurstueckeToBaulastenMap.put(flurstueck, baulasten);
            } catch (final Exception ex) {
                LOG.fatal(ex, ex);
            }
        }
        return flurstueckeToBaulastenMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bescheinigungInfo  grundstueckeToFlurstueckeMap flurstuecke flurstueckeToBaulastengrundstueckMap
     *                             DOCUMENT ME!
     * @param   protocolBuffer     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<ProductGroupAmount> createBilling(final BerechtigungspruefungBescheinigungInfo bescheinigungInfo,
            final StringBuffer protocolBuffer) {
        final List<String> keys = new ArrayList<>();
        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppeInfo
                    : bescheinigungInfo.getBescheinigungsgruppen()) {
            keys.add(gruppeInfo.getName());
        }
        Collections.sort(keys);

        final int anzahlGrundstuecke = bescheinigungInfo.getBescheinigungsgruppen().size();
        if (anzahlGrundstuecke == 1) {
            protocolBuffer.append("\n===\n\nBescheinigungsart des Grundstücks\n:");
        } else {
            protocolBuffer.append("\n===\n\nBescheinigungsarten der ")
                    .append(anzahlGrundstuecke)
                    .append(" ermittelten Grundstücke:\n");
        }

        final Collection<ProductGroupAmount> prodAmounts = new ArrayList<>();

        int anzahlNegativ = 0;
        int anzahlPositiv1 = 0;
        int anzahlPositiv2 = 0;
        int anzahlPositiv3 = 0;

        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppeInfo
                    : bescheinigungInfo.getBescheinigungsgruppen()) {
            final StringBuffer sb = new StringBuffer();
            final String baulastenString = sb.toString();
            final int numOfBaulasten = gruppeInfo.getBaulastenBelastet().size()
                        + gruppeInfo.getBaulastenBeguenstigt().size();
            switch (numOfBaulasten) {
                case 0: {
                    protocolBuffer.append(" * Grundstück ")
                            .append(gruppeInfo.getName())
                            .append(" => Negativ-Bescheinigung\n");
                    anzahlNegativ++;
                }
                break;
                case 1: {
                    protocolBuffer.append(" * Grundstück ")
                            .append(gruppeInfo.getName())
                            .append(" => Positiv-Bescheinigung für eine Baulast (")
                            .append(baulastenString)
                            .append(")\n");
                    anzahlPositiv1++;
                }
                break;
                case 2: {
                    protocolBuffer.append(" * Grundstück ")
                            .append(gruppeInfo.getName())
                            .append(" => Positiv-Bescheinigung für zwei Baulasten (")
                            .append(baulastenString)
                            .append(")\n");
                    anzahlPositiv2++;
                }
                break;
                default: {
                    protocolBuffer.append(" * Grundstück ")
                            .append(gruppeInfo.getName())
                            .append(" => Positiv-Bescheinigung für drei oder mehr Baulasten (")
                            .append(baulastenString)
                            .append(")\n");
                    anzahlPositiv3++;
                }
                break;
            }
        }

        if (anzahlNegativ > 0) {
            if (anzahlNegativ > 10) {
                prodAmounts.add(new ProductGroupAmount("ea_blab_neg_ab_10", 1));
            } else {
                prodAmounts.add(new ProductGroupAmount("ea_blab_neg", anzahlNegativ));
            }
        }
        if (anzahlPositiv1 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_1", anzahlPositiv1));
        }
        if (anzahlPositiv2 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_2", anzahlPositiv2));
        }
        if (anzahlPositiv3 > 0) {
            prodAmounts.add(new ProductGroupAmount("ea_blab_pos_3", anzahlPositiv3));
        }

        return prodAmounts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstueckeToGrundstueckeMap          flurstuecke DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   protocolBuffer                        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Set<BerechtigungspruefungBescheinigungGruppeInfo> createBescheinigungsGruppen(
            final Map<CidsBean, Collection<String>> flurstueckeToGrundstueckeMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final StringBuffer protocolBuffer) {
        final Map<String, BerechtigungspruefungBescheinigungGruppeInfo> gruppeMap = new HashMap<>();

        final List<CidsBean> flurstuecke = new ArrayList<>(flurstueckeToGrundstueckeMap.keySet());
        Collections.sort(flurstuecke, new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    final String s1 = (o1 == null) ? "" : (String)o1.getProperty("alkis_id");
                    final String s2 = (o2 == null) ? "" : (String)o2.getProperty("alkis_id");
                    return s1.compareTo(s2);
                }
            });

        for (final CidsBean flurstueck : flurstuecke) {
            final Collection<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(flurstueck);
            final Collection<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
            final BerechtigungspruefungBescheinigungGruppeInfo newGruppe = AlkisProductDownloadHelper
                        .createBerechtigungspruefungBescheinigungGruppeInfo(
                            flurstueckeToGrundstueckeMap,
                            baulastenBeguenstigt,
                            baulastenBelastet,
                            CachedInfoBaulastRetriever.getInstance().getCache());
            final String gruppeKey = newGruppe.toString();
            if (!gruppeMap.containsKey(gruppeKey)) {
                gruppeMap.put(gruppeKey, newGruppe);
            }
            final BerechtigungspruefungBescheinigungGruppeInfo gruppe = gruppeMap.get(gruppeKey);
            gruppe.getFlurstuecke()
                    .add(AlkisProductDownloadHelper.createBerechtigungspruefungBescheinigungFlurstueckInfo(
                            flurstueck,
                            flurstueckeToGrundstueckeMap.get(flurstueck)));
        }

        final Set<BerechtigungspruefungBescheinigungGruppeInfo> bescheinigungsgruppen = new HashSet<>(
                gruppeMap.values());

        protocolBuffer.append("Anzahl Bescheinigungsgruppen: ").append(bescheinigungsgruppen.size()).append("\n");
        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppe : bescheinigungsgruppen) {
            protocolBuffer.append(" * ").append(gruppe.toString()).append("\n");
        }
        return bescheinigungsgruppen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                   DOCUMENT ME!
     * @param   grundstueckeToFlurstueckeMap  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<CidsBean, Collection<String>> createFlurstueckeToGrundstueckeMap(
            final Collection<CidsBean> flurstuecke,
            final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap) {
        final HashMap<CidsBean, Collection<String>> flurstueckeToGrundstueckeMap = new HashMap<>();

        for (final String grundstueck : grundstueckeToFlurstueckeMap.keySet()) {
            final Collection<CidsBean> gruFlu = grundstueckeToFlurstueckeMap.get(grundstueck);
            for (final CidsBean flurstueck : flurstuecke) {
                if (gruFlu.contains(flurstueck)) {
                    if (!flurstueckeToGrundstueckeMap.containsKey(flurstueck)) {
                        flurstueckeToGrundstueckeMap.put(flurstueck, new HashSet<String>());
                    }
                    final Collection<String> grundstuecke = flurstueckeToGrundstueckeMap.get(flurstueck);
                    grundstuecke.add(grundstueck);
                }
            }
        }
        return flurstueckeToGrundstueckeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke     DOCUMENT ME!
     * @param   protocolBuffer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception             DOCUMENT ME!
     * @throws  InterruptedException  DOCUMENT ME!
     */
    public Map<String, Collection<CidsBean>> createGrundstueckeToFlurstueckeMap(
            final Collection<CidsBean> flurstuecke,
            final StringBuffer protocolBuffer) throws Exception {
        protocolBuffer.append("\n===\n\nZuordnung der Flurstücke zu Grundstücken...\n");

        final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap = new HashMap<>();

        for (final CidsBean flurstueckBean : flurstuecke) {
            final List<CidsBean> buchungsblaetter = new ArrayList<>(flurstueckBean.getBeanCollectionProperty(
                        "buchungsblaetter"));
            if (buchungsblaetter.size() == 1) {
                protocolBuffer.append("\nFlurstück: ").append(flurstueckBean).append(" (1 Buchungsblatt):");
            } else {
                protocolBuffer.append("\nFlurstück: ")
                        .append(flurstueckBean)
                        .append(" (")
                        .append(buchungsblaetter.size())
                        .append(" Buchungsblätter):");
            }
            Collections.sort(buchungsblaetter, new Comparator<CidsBean>() {

                    @Override
                    public int compare(final CidsBean o1, final CidsBean o2) {
                        final String s1 = (o1 == null) ? "" : (String)o1.getProperty("buchungsblattcode");
                        final String s2 = (o2 == null) ? "" : (String)o2.getProperty("buchungsblattcode");
                        return s1.compareTo(s2);
                    }
                });

//            boolean teileigentumAlreadyCounted = false;
            boolean grundstueckFound = false;
            for (final CidsBean buchungsblattBean : buchungsblaetter) {
                if (grundstueckFound) {
                    break; // we are done
                }
                if (buchungsblattBean != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    protocolBuffer.append(" * analysiere Buchungsblatt ").append(buchungsblattBean).append(" ...\n");
                    final Buchungsblatt buchungsblatt = getBuchungsblatt(buchungsblattBean);

                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    }
                    final List<Buchungsstelle> buchungsstellen = Arrays.asList(buchungsblatt.getBuchungsstellen());
                    Collections.sort(buchungsstellen, new Comparator<Buchungsstelle>() {

                            @Override
                            public int compare(final Buchungsstelle o1, final Buchungsstelle o2) {
                                final String s1 = (o1 == null) ? "" : o1.getSequentialNumber();
                                final String s2 = (o2 == null) ? "" : o2.getSequentialNumber();
                                return s1.compareTo(s2);
                            }
                        });

                    for (final Buchungsstelle buchungsstelle : buchungsstellen) {
                        if (grundstueckFound) {
                            break; // we are done
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            throw new InterruptedException();
                        }
                        boolean flurstueckPartOfStelle = false;
                        final LandParcel[] landparcels = AlkisProducts.getLandparcelFromBuchungsstelle(
                                buchungsstelle);
                        if (landparcels != null) {
                            for (final LandParcel lp : landparcels) {
                                if (((String)flurstueckBean.getProperty("alkis_id")).equals(
                                                lp.getLandParcelCode())) {
                                    flurstueckPartOfStelle = true;
                                    break;
                                }
                            }
                        }
                        if (flurstueckPartOfStelle) {
                            final String[] bbc = buchungsblatt.getBuchungsblattCode().split("-");
                            final String gemarkungsnummer = bbc[0].substring(2).trim();
                            final String buchungsblattnummer = bbc[1].trim();
                            final MetaClass mcGemarkung = CidsBean.getMetaClassFromTableName(
                                    "WUNDA_BLAU",
                                    "gemarkung",
                                    getConnectionContext());

                            final String pruefungQuery = "SELECT " + mcGemarkung.getID()
                                        + ", " + mcGemarkung.getTableName() + "." + mcGemarkung.getPrimaryKey() + " "
                                        + "FROM " + mcGemarkung.getTableName() + " "
                                        + "WHERE " + mcGemarkung.getTableName() + ".gemarkungsnummer = "
                                        + Integer.parseInt(gemarkungsnummer) + " "
                                        + "LIMIT 1;";
                            final MetaObject[] mos = getMetaObjects(pruefungQuery, null, getConnectionContext());

                            final String key;
                            if ((mos != null) && (mos.length > 0)) {
                                final CidsBean gemarkung = mos[0].getBean();
                                key = gemarkung.getProperty("name") + " "
                                            + Integer.parseInt(buchungsblattnummer.substring(0, 5))
                                            + buchungsblattnummer.substring(5) + " / "
                                            + Integer.parseInt(buchungsstelle.getSequentialNumber());
                            } else {
                                key = "[" + gemarkungsnummer + "] "
                                            + Integer.parseInt(buchungsblattnummer.substring(0, 5))
                                            + buchungsblattnummer.substring(5) + " / "
                                            + Integer.parseInt(buchungsstelle.getSequentialNumber());
                            }

                            final String buchungsart = buchungsstelle.getBuchungsart();
                            if ("Erbbaurecht".equals(buchungsart)) {
                                protocolBuffer.append("   -> ignoriere ")
                                        .append(key)
                                        .append(" aufgrund der Buchungsart (")
                                        .append(buchungsart)
                                        .append(")");
                                continue;
                            }

                            if (!grundstueckeToFlurstueckeMap.containsKey(key)) {
                                grundstueckeToFlurstueckeMap.put(key, new HashSet<CidsBean>());
                            }

                            final String buchungsartSuffix = "Grundstück".equals(buchungsart)
                                ? "" : (" (" + buchungsart + ")");
                            protocolBuffer.append("   => füge Flurstück ")
                                    .append(flurstueckBean)
                                    .append(" zu Grundstück \"")
                                    .append(key)
                                    .append(" \"hinzu")
                                    .append(buchungsartSuffix);
                            grundstueckeToFlurstueckeMap.get(key).add(flurstueckBean);
                            grundstueckFound = true;
                        }
                    }
                }
            }
        }

        return grundstueckeToFlurstueckeMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Buchungsblatt getBuchungsblatt(final CidsBean buchungsblattBean) throws Exception {
        Buchungsblatt buchungsblatt = null;

        if (buchungsblattBean != null) {
            buchungsblatt = BUCHUNGSBLATT_CACHE.get(buchungsblattBean);
            if (buchungsblatt == null) {
                final String buchungsblattcode = String.valueOf(buchungsblattBean.getProperty("buchungsblattcode"));
                if ((buchungsblattcode != null) && (buchungsblattcode.length() > 5)) {
                    buchungsblatt = AlkisUtils.getInstance()
                                .getBuchungsblattFromAlkisSOAPServerAction(AlkisProducts.fixBuchungslattCode(
                                            buchungsblattcode),
                                        getConnectionContext());
                    BUCHUNGSBLATT_CACHE.put(buchungsblattBean, buchungsblatt);
                }
            }
        }

        return buchungsblatt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BaulastBescheinigungHelper getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class BaBeException extends Exception {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BaBeException object.
         *
         * @param  message  DOCUMENT ME!
         */
        public BaBeException(final String message) {
            super(message);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final BaulastBescheinigungHelper INSTANCE = new BaulastBescheinigungHelper();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
