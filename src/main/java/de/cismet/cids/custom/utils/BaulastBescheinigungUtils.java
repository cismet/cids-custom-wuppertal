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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisProductDownloadHelper;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroupAmount;
import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungFlurstueckInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;
import de.cismet.cids.custom.wunda_blau.search.server.BaulastSearchInfo;
import de.cismet.cids.custom.wunda_blau.search.server.CidsBaulastSearchStatement;
import de.cismet.cids.custom.wunda_blau.search.server.FlurstueckInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

import static de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator.createFertigungsVermerk;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastBescheinigungUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaulastBescheinigungUtils.class);

    private static final String PARAMETER_JOBNUMBER = "JOBNUMBER";
    private static final String PARAMETER_PROJECTNAME = "PROJECTNAME";
    private static final String PARAMETER_PRUEFKEY = "PRUEFKEY";
    private static final String PARAMETER_HAS_BELASTET = "HAS_BELASTET";
    private static final String PARAMETER_HAS_BEGUENSTIGT = "HAS_BEGUENSTIGT";
    private static final String PARAMETER_FABRICATIONNOTICE = "FABRICATIONNOTICE";
    private static final String PARAMETER_FABRICATIONDATE = "FABRICATIONDATE";

    private static final ConnectionContext CONNECTION_CONTEXT = ConnectionContext.createDummy();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  downloadInfo       DOCUMENT ME!
     * @param  anfrageSchluessel  DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void doDownload(final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel,
            final ConnectionContext connectionContext) {
        try {
            final Download download = generateBaulastbescheinigungDownload(
                    downloadInfo,
                    anfrageSchluessel,
                    connectionContext);
            if (download != null) {
                DownloadManager.instance().add(download);
            }
        } catch (final Exception ex) {
            LOG.error("error while generating download", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   downloadInfo       DOCUMENT ME!
     * @param   anfrageSchluessel  DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download generateBaulastbescheinigungDownload(
            final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel,
            final ConnectionContext connectionContext) throws Exception {
        if (
            !DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            return null;
        }
        final String jobname = DownloadManagerDialog.getInstance().getJobName();

        final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
            new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                @Override
                public Collection<? extends Download> fetchDownloads() throws Exception {
                    final Collection<Download> downloads = new ArrayList<>();
                    try {
                        downloads.add(new TxtDownload(
                                downloadInfo.getProtokoll(),
                                jobname,
                                "Baulastbescheinigung-Protokoll",
                                "baulastbescheinigung_protokoll",
                                ".txt"));

                        if (downloadInfo.getBescheinigungsInfo() != null) {
                            final Set<CidsBean> allBaulasten = new HashSet<>();

                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen().size();

                            final List<BerechtigungspruefungBescheinigungGruppeInfo> sortedBescheinigungsGruppen =
                                new ArrayList<>(
                                    downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen());
                            Collections.sort(
                                sortedBescheinigungsGruppen,
                                new Comparator<BerechtigungspruefungBescheinigungGruppeInfo>() {

                                    @Override
                                    public int compare(final BerechtigungspruefungBescheinigungGruppeInfo o1,
                                            final BerechtigungspruefungBescheinigungGruppeInfo o2) {
                                        final String alkisId1 = o1.getFlurstuecke().iterator().next().getAlkisId();
                                        final String alkisId2 = o2.getFlurstuecke().iterator().next().getAlkisId();
                                        return alkisId1.compareTo(alkisId2);
                                    }
                                });
                            for (final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppe
                                        : sortedBescheinigungsGruppen) {
                                downloads.add(createBescheinigungPdf(
                                        bescheinigungsGruppe,
                                        (jobname != null) ? jobname : downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung(),
                                        anfrageSchluessel,
                                        downloadInfo.getBescheinigungsInfo().getDatum(),
                                        ++number,
                                        max,
                                        connectionContext));
                                // alle Baulasten ermitteln
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBelastet()) {
                                    allBaulasten.add(CachedInfoBaulastRetriever.getInstance().loadBaulast(
                                            baulastInfo,
                                            connectionContext));
                                }
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBeguenstigt()) {
                                    allBaulasten.add(CachedInfoBaulastRetriever.getInstance().loadBaulast(
                                            baulastInfo,
                                            connectionContext));
                                }
                            }

                            if (!allBaulasten.isEmpty()) {
                                // Download: Bericht für alle Baulasten
                                downloads.addAll(BaulastenReportGenerator.generateRasterDownloads(
                                        jobname,
                                        allBaulasten,
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung(),
                                        connectionContext));
                            }
                        }
                    } catch (final Exception ex) {
                        LOG.fatal(ex, ex);
                    }

                    return downloads;
                }
            };
        return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bescheinigungsGruppe  DOCUMENT ME!
     * @param   jobname               DOCUMENT ME!
     * @param   jobnumber             DOCUMENT ME!
     * @param   projectName           DOCUMENT ME!
     * @param   anfrageSchluessel     DOCUMENT ME!
     * @param   fabricationdate       DOCUMENT ME!
     * @param   number                projectname DOCUMENT ME!
     * @param   max                   DOCUMENT ME!
     * @param   connectionContext     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Download createBescheinigungPdf(
            final BerechtigungspruefungBescheinigungGruppeInfo bescheinigungsGruppe,
            final String jobname,
            final String jobnumber,
            final String projectName,
            final String anfrageSchluessel,
            final Date fabricationdate,
            final int number,
            final int max,
            final ConnectionContext connectionContext) throws Exception {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    try {
                        final Collection<BerechtigungspruefungBescheinigungGruppeInfo> reportBeans = Arrays.asList(
                                new BerechtigungspruefungBescheinigungGruppeInfo[] { bescheinigungsGruppe });
                        final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);
                        return dataSource;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    try {
                        final HashMap parameters = new HashMap();
                        parameters.put(PARAMETER_JOBNUMBER, jobnumber);
                        parameters.put(PARAMETER_PROJECTNAME, projectName);
                        parameters.put(PARAMETER_PRUEFKEY, anfrageSchluessel);

                        parameters.put(PARAMETER_HAS_BELASTET, !bescheinigungsGruppe.getBaulastenBelastet().isEmpty());
                        parameters.put(
                            PARAMETER_FABRICATIONDATE,
                            new SimpleDateFormat("dd.MM.yyyy").format(fabricationdate));
                        parameters.put(
                            PARAMETER_HAS_BEGUENSTIGT,
                            !bescheinigungsGruppe.getBaulastenBeguenstigt().isEmpty());
                        parameters.put(
                            PARAMETER_FABRICATIONNOTICE,
                            createFertigungsVermerk(SessionManager.getSession().getUser(), connectionContext));
                        return parameters;
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final Collection<BerechtigungspruefungBescheinigungFlurstueckInfo> fls = bescheinigungsGruppe.getFlurstuecke();
        final boolean ua = (fls.size() > 1);
        final String title = "Bescheinigung " + fls.iterator().next().getAlkisId() + (ua ? " (ua)" : "")
                    + " " + number + "/" + max;
        final String fileName = "bescheinigung_" + fls.iterator().next().getAlkisId().replace("/", "--")
                    + (ua ? ".ua" : "")
                    + "_" + number;

        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/baulastbescheinigung.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                title,
                fileName);

        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke                           DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   protocolWriter                        DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void fillFlurstueckeToBaulastenMaps(final Collection<CidsBean> flurstuecke,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final BaulastBescheinigungDialog.ProtocolWriter protocolWriter) throws Exception {
        protocolWriter.addMessage("\n===");

        // belastete Baulasten pro Flurstück
        flurstueckeToBaulastenBelastetMap.putAll(createFlurstueckeToBaulastenMap(flurstuecke, true, protocolWriter));

        // begünstigte Baulasten pro Flurstück
        flurstueckeToBaulastenBeguenstigtMap.putAll(createFlurstueckeToBaulastenMap(
                flurstuecke,
                false,
                protocolWriter));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecke     DOCUMENT ME!
     * @param   belastet        DOCUMENT ME!
     * @param   protocolWriter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Map<CidsBean, Set<CidsBean>> createFlurstueckeToBaulastenMap(final Collection<CidsBean> flurstuecke,
            final boolean belastet,
            final BaulastBescheinigungDialog.ProtocolWriter protocolWriter) throws Exception {
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

        protocolWriter.addMessage("\nSuche der " + ((belastet) ? "belastenden" : "begünstigenden") + " Baulasten von:");
        final Map<CidsBean, Set<CidsBean>> flurstueckeToBaulastenMap = new HashMap<>();
        for (final CidsBean flurstueck : flurstuecke) {
            protocolWriter.addMessage(" * Flurstück: " + flurstueck + " ...");
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
                final Collection<MetaObjectNode> mons = SessionManager.getProxy()
                            .customServerSearch(search, getConnectionContext());
                for (final MetaObjectNode mon : mons) {
                    final MetaObject mo = SessionManager.getProxy()
                                .getMetaObject(mon.getObjectId(),
                                    mon.getClassId(),
                                    "WUNDA_BLAU",
                                    getConnectionContext());
                    if ((mo.getBean() != null) && (mo.getBean() != null)
                                && (mo.getBean().getProperty("loeschungsdatum") != null)) {
                        continue;
                    }
                    if (mon.getName().startsWith("indirekt: ")) {
                        throw new BaulastBescheinigungDialog.BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                }

                final String alkisId = (String)flurstueck.getProperty("alkis_id");

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }
                final MetaObject[] mos = SessionManager.getProxy()
                            .getMetaObjectByQuery(String.format(
                                    query,
                                    mcBaulast.getID(),
                                    mcBaulast.getPrimaryKey(),
                                    alkisId),
                                0,
                                getConnectionContext());
                for (final MetaObject mo : mos) {
                    final CidsBean baulast = mo.getBean();
                    final Boolean geprueft = (Boolean)baulast.getProperty("geprueft");
                    if ((geprueft == null) || (geprueft == false)) {
                        throw new BaulastBescheinigungDialog.BaBeException(
                            "Zu den angegebenen Flurstücken kann aktuell keine Baulastauskunft erteilt werden, da sich einige der enthaltenen Baulasten im Bearbeitungszugriff befinden.");
                    }
                    protocolWriter.addMessage("   => Baulast: " + baulast);
                    baulasten.add(baulast);
                }
                flurstueckeToBaulastenMap.put(flurstueck, baulasten);
            } catch (final ConnectionException ex) {
                LOG.fatal(ex, ex);
            }
        }
        return flurstueckeToBaulastenMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   grundstueckeToFlurstueckeMap          flurstuecke flurstueckeToBaulastengrundstueckMap DOCUMENT ME!
     * @param   flurstueckeToBaulastenBelastetMap     DOCUMENT ME!
     * @param   flurstueckeToBaulastenBeguenstigtMap  DOCUMENT ME!
     * @param   protocolWriter                        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<ProductGroupAmount> createBilling(
            final Map<String, Collection<CidsBean>> grundstueckeToFlurstueckeMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final BaulastBescheinigungDialog.ProtocolWriter protocolWriter) {
        final List<String> keys = new ArrayList<>(grundstueckeToFlurstueckeMap.keySet());
        Collections.sort(keys);

        final int anzahlGrundstuecke = grundstueckeToFlurstueckeMap.size();
        if (anzahlGrundstuecke == 1) {
            protocolWriter.addMessage("\n===\n\nBescheinigungsart des Grundstücks:");
        } else {
            protocolWriter.addMessage("\n===\n\nBescheinigungsarten der " + anzahlGrundstuecke
                        + " ermittelten Grundstücke:");
        }

        final Collection<ProductGroupAmount> prodAmounts = new ArrayList<>();

        int anzahlNegativ = 0;
        int anzahlPositiv1 = 0;
        int anzahlPositiv2 = 0;
        int anzahlPositiv3 = 0;

        for (final String key : keys) {
            if (grundstueckeToFlurstueckeMap.containsKey(key)) {
                boolean first = true;

                final Collection<CidsBean> flurstuecke = grundstueckeToFlurstueckeMap.get(key);

                final Set<CidsBean> baulasten = new HashSet<>();
                for (final CidsBean flurstueck : flurstuecke) {
                    final Collection<CidsBean> baulastenBelastet = flurstueckeToBaulastenBelastetMap.get(flurstueck);
                    final Collection<CidsBean> baulastenBeguenstigt = flurstueckeToBaulastenBeguenstigtMap.get(
                            flurstueck);
                    baulasten.addAll(baulastenBelastet);
                    baulasten.addAll(baulastenBeguenstigt);
                }

                final StringBuffer sb = new StringBuffer();
                for (final CidsBean baulast : baulasten) {
                    if (first) {
                        first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(baulast);
                }
                final String baulastenString = sb.toString();

                final int numOfBaulasten = baulasten.size();
                switch (numOfBaulasten) {
                    case 0: {
                        protocolWriter.addMessage(" * Grundstück " + key + " => Negativ-Bescheinigung");
                        anzahlNegativ++;
                        break;
                    }
                    case 1: {
                        protocolWriter.addMessage(" * Grundstück " + key
                                    + " => Positiv-Bescheinigung für eine Baulast ("
                                    + baulastenString
                                    + ")");
                        anzahlPositiv1++;
                        break;
                    }
                    case 2: {
                        protocolWriter.addMessage(" * Grundstück " + key
                                    + " => Positiv-Bescheinigung für zwei Baulasten ("
                                    + baulastenString
                                    + ")");
                        anzahlPositiv2++;
                        break;
                    }
                    default: {
                        protocolWriter.addMessage(" * Grundstück " + key
                                    + " => Positiv-Bescheinigung für drei oder mehr Baulasten ("
                                    + baulastenString + ")");
                        anzahlPositiv3++;
                        break;
                    }
                }
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
     * @param   protocolWriter                        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Set<BerechtigungspruefungBescheinigungGruppeInfo> createBescheinigungsGruppen(
            final Map<CidsBean, Collection<String>> flurstueckeToGrundstueckeMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBeguenstigtMap,
            final Map<CidsBean, Collection<CidsBean>> flurstueckeToBaulastenBelastetMap,
            final BaulastBescheinigungDialog.ProtocolWriter protocolWriter) {
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

        protocolWriter.addMessage("Anzahl Bescheinigungsgruppen: " + bescheinigungsgruppen.size());
        for (final BerechtigungspruefungBescheinigungGruppeInfo gruppe : bescheinigungsgruppen) {
            protocolWriter.addMessage(" * " + gruppe.toString());
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
    public static Map<CidsBean, Collection<String>> createFlurstueckeToGrundstueckeMap(
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
    private static ConnectionContext getConnectionContext() {
        return CONNECTION_CONTEXT;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    static class TxtDownload extends AbstractDownload {

        //~ Instance fields ----------------------------------------------------

        private final String content;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new TxtDownload object.
         *
         * @param  content    DOCUMENT ME!
         * @param  directory  DOCUMENT ME!
         * @param  title      DOCUMENT ME!
         * @param  filename   DOCUMENT ME!
         * @param  extension  DOCUMENT ME!
         */
        public TxtDownload(
                final String content,
                final String directory,
                final String title,
                final String filename,
                final String extension) {
            this.content = content;
            this.directory = directory;
            this.title = title;

            status = Download.State.WAITING;

            determineDestinationFile(filename, extension);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            if (status != Download.State.WAITING) {
                return;
            }

            status = Download.State.RUNNING;

            stateChanged();

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(fileToSaveTo, false));
                writer.write(content);
            } catch (Exception ex) {
                error(ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (Exception e) {
                        log.warn("Exception occured while closing file.", e);
                    }
                }
            }

            if (status == Download.State.RUNNING) {
                status = Download.State.COMPLETED;
                stateChanged();
            }
        }
    }
}
