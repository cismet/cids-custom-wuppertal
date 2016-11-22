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
import Sirius.server.newuser.User;

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

import de.cismet.cids.custom.objectrenderer.wunda_blau.BaulastenReportGenerator;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungDownloadInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungFlurstueckInfo;
import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungGruppeInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.tools.gui.StaticSwingTools;
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

    static final Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> BAULAST_CACHE =
        new HashMap<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean>();

    private static final String PARAMETER_JOBNUMBER = "JOBNUMBER";
    private static final String PARAMETER_PROJECTNAME = "PROJECTNAME";
    private static final String PARAMETER_PRUEFKEY = "PRUEFKEY";
    private static final String PARAMETER_HAS_BELASTET = "HAS_BELASTET";
    private static final String PARAMETER_HAS_BEGUENSTIGT = "HAS_BEGUENSTIGT";
    private static final String PARAMETER_FABRICATIONNOTICE = "FABRICATIONNOTICE";
    private static final String PARAMETER_FABRICATIONDATE = "FABRICATIONDATE";

    //~ Methods ----------------------------------------------------------------

    /**
     * Creates a new BescheinigungsGruppe object.
     *
     * @param   baulastenBeguenstigt  DOCUMENT ME!
     * @param   baulastenBelastet     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungGruppeInfo createGruppeInfo(
            final Collection<CidsBean> baulastenBeguenstigt,
            final Collection<CidsBean> baulastenBelastet) {
        return createGruppeInfo(new HashMap<CidsBean, Collection<String>>(), baulastenBeguenstigt, baulastenBelastet);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flurstuecketoGrundstueckeMap  DOCUMENT ME!
     * @param   baulastenBeguenstigtBeans     DOCUMENT ME!
     * @param   baulastenBelastetBeans        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungGruppeInfo createGruppeInfo(
            final Map<CidsBean, Collection<String>> flurstuecketoGrundstueckeMap,
            final Collection<CidsBean> baulastenBeguenstigtBeans,
            final Collection<CidsBean> baulastenBelastetBeans) {
        final List<BerechtigungspruefungBescheinigungFlurstueckInfo> flurstueckeInfo =
            new ArrayList<BerechtigungspruefungBescheinigungFlurstueckInfo>();
        for (final CidsBean flurstueck : flurstuecketoGrundstueckeMap.keySet()) {
            flurstueckeInfo.add(createFlurstueckInfo(flurstueck, flurstuecketoGrundstueckeMap.get(flurstueck)));
        }

        final List<BerechtigungspruefungBescheinigungBaulastInfo> baulastBeguenstigtInfos =
            new ArrayList<BerechtigungspruefungBescheinigungBaulastInfo>();
        for (final CidsBean baulastBeguenstigt : baulastenBeguenstigtBeans) {
            final BerechtigungspruefungBescheinigungBaulastInfo baulastBeguenstigtInfo = createBaulastInfo(
                    baulastBeguenstigt);
            BAULAST_CACHE.put(baulastBeguenstigtInfo, baulastBeguenstigt);
            baulastBeguenstigtInfos.add(baulastBeguenstigtInfo);
        }

        final List<BerechtigungspruefungBescheinigungBaulastInfo> baulastBelastetInfos =
            new ArrayList<BerechtigungspruefungBescheinigungBaulastInfo>();
        for (final CidsBean baulastBelastet : baulastenBelastetBeans) {
            final BerechtigungspruefungBescheinigungBaulastInfo baulastBelastetInfo = createBaulastInfo(
                    baulastBelastet);
            BAULAST_CACHE.put(baulastBelastetInfo, baulastBelastet);
            baulastBelastetInfos.add(baulastBelastetInfo);
        }

        Collections.sort(flurstueckeInfo, new Comparator<BerechtigungspruefungBescheinigungFlurstueckInfo>() {

                @Override
                public int compare(final BerechtigungspruefungBescheinigungFlurstueckInfo o1,
                        final BerechtigungspruefungBescheinigungFlurstueckInfo o2) {
                    final int compareGemarkung = compareString(o1.getGemarkung(), o2.getGemarkung());
                    if (compareGemarkung != 0) {
                        return compareGemarkung;
                    } else {
                        final int compareFlur = compareString(o1.getFlur(), o2.getFlur());
                        if (compareFlur != 0) {
                            return compareFlur;
                        } else {
                            final int compareNummer = compareString(o1.getNummer(), o2.getNummer());
                            if (compareNummer != 0) {
                                return compareNummer;
                            } else {
                                return 0;
                            }
                        }
                    }
                }
            });

        final Comparator<BerechtigungspruefungBescheinigungBaulastInfo> baulastBeanComparator =
            new Comparator<BerechtigungspruefungBescheinigungBaulastInfo>() {

                @Override
                public int compare(final BerechtigungspruefungBescheinigungBaulastInfo o1,
                        final BerechtigungspruefungBescheinigungBaulastInfo o2) {
                    final int compareBlattnummer = compareString(o1.getBlattnummer(), o2.getBlattnummer());
                    if (compareBlattnummer != 0) {
                        return compareBlattnummer;
                    } else {
                        final Integer lfdN1 = (o1 == null) ? -1 : Integer.parseInt((String)o1.getLaufende_nummer());
                        final int lfdN2 = (o2 == null) ? -1 : Integer.parseInt((String)o2.getLaufende_nummer());
                        final int compareLaufendenummer = lfdN1.compareTo(lfdN2);

                        if (compareLaufendenummer != 0) {
                            return compareLaufendenummer;
                        } else {
                            return 0;
                        }
                    }
                }
            };

        Collections.sort(baulastBeguenstigtInfos, baulastBeanComparator);
        Collections.sort(baulastBelastetInfos, baulastBeanComparator);

        return new BerechtigungspruefungBescheinigungGruppeInfo(
                flurstueckeInfo,
                baulastBeguenstigtInfos,
                baulastBelastetInfos);
    }

    /**
     * Creates a new FlurstueckBean object.
     *
     * @param   flurstueck    DOCUMENT ME!
     * @param   grundstuecke  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungFlurstueckInfo createFlurstueckInfo(final CidsBean flurstueck,
            final Collection<String> grundstuecke) {
        final String alkisId = (String)flurstueck.getProperty("alkis_id");
        final String gemarkung = (String)flurstueck.getProperty("gemarkung");
        final String flur = (String)flurstueck.getProperty("flur");
        final String nenner = (String)flurstueck.getProperty("fstck_nenner");
        final String zaehler = (String)flurstueck.getProperty("fstck_zaehler");

        final String lage;
        final Collection<CidsBean> adressen = flurstueck.getBeanCollectionProperty("adressen");
        if (adressen.isEmpty()) {
            lage = "";
        } else {
            final Set<String> strassen = new HashSet<String>();
            final Map<String, Collection<String>> hausnummernMap = new HashMap<String, Collection<String>>();
            for (final CidsBean adresse : adressen) {
                final String strasse = (String)adresse.getProperty("strasse");
                final String hausnummer = (String)adresse.getProperty("nummer");
                strassen.add(strasse);
                if (hausnummer != null) {
                    if (!hausnummernMap.containsKey(strasse)) {
                        hausnummernMap.put(strasse, new ArrayList<String>());
                    }
                    final List<String> hausnummern = (List)hausnummernMap.get(strasse);
                    hausnummern.add(hausnummer);
                }
            }
            final String strasse = strassen.iterator().next();
            final StringBuffer sb = new StringBuffer(strasse);
            boolean first = true;
            final List<String> hausnummern = (List)hausnummernMap.get(strasse);
            if (hausnummern != null) {
                Collections.sort(hausnummern);
                sb.append(" ");
                for (final String hausnummer : hausnummern) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(hausnummer);
                    first = false;
                }
            }
            if (strassen.size() > 1) {
                sb.append(" u.a.");
            }
            lage = sb.toString();
        }

        return new BerechtigungspruefungBescheinigungFlurstueckInfo(
                alkisId,
                gemarkung,
                flur,
                zaehler,
                nenner,
                lage,
                grundstuecke);
    }

    /**
     * Creates a new BaulastBean object.
     *
     * @param   baulast  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungBescheinigungBaulastInfo createBaulastInfo(final CidsBean baulast) {
        final String blattnummer = (String)baulast.getProperty("blattnummer");
        final String laufende_nummer = (String)baulast.getProperty("laufende_nummer");

        final StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (final CidsBean art : baulast.getBeanCollectionProperty("art")) {
            if (!first) {
                sb.append(", ");
                first = false;
            }
            sb.append(art.getProperty("baulast_art"));
        }
        final String arten = sb.toString();

        return new BerechtigungspruefungBescheinigungBaulastInfo(blattnummer, laufende_nummer, arten);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   info  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean loadBaulast(final BerechtigungspruefungBescheinigungBaulastInfo info) {
        if (!BAULAST_CACHE.containsKey(info)) {
            final MetaClass mcBaulast = ClassCacheMultiple.getMetaClass(
                    "WUNDA_BLAU",
                    "alb_baulast");

            final String query = "SELECT %d, id "
                        + "FROM alb_baulast "
                        + "WHERE blattnummer ILIKE '%s' "
                        + "AND laufende_nummer ILIKE '%s'";
            try {
                final MetaObject[] mos = SessionManager.getProxy()
                            .getMetaObjectByQuery(String.format(
                                    query,
                                    mcBaulast.getID(),
                                    info.getBlattnummer(),
                                    info.getLaufende_nummer()),
                                0);
                BAULAST_CACHE.put(info, mos[0].getBean());
            } catch (ConnectionException ex) {
                LOG.error(ex, ex);
                return null;
            }
        }
        return BAULAST_CACHE.get(info);
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
            final int max) throws Exception {
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
                            createFertigungsVermerk(SessionManager.getSession().getUser()));
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
     * @param  downloadInfo       DOCUMENT ME!
     * @param  anfrageSchluessel  DOCUMENT ME!
     */
    public static void doDownload(final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel) {
        try {
            final Download download = generateDownload(downloadInfo, anfrageSchluessel);
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
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download generateDownload(final BerechtigungspruefungBescheinigungDownloadInfo downloadInfo,
            final String anfrageSchluessel) throws Exception {
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
                    final Collection<Download> downloads = new ArrayList<Download>();
                    try {
                        downloads.add(new TxtDownload(
                                downloadInfo.getProtokoll(),
                                jobname,
                                "Baulastbescheinigung-Protokoll",
                                "baulastbescheinigung_protokoll",
                                ".txt"));

                        if (downloadInfo.getBescheinigungsInfo() != null) {
                            final Set<CidsBean> allBaulasten = new HashSet<CidsBean>();

                            // Download: Berichte für alle Bescheinigungsgruppen
                            int number = 0;
                            final int max = downloadInfo.getBescheinigungsInfo().getBescheinigungsgruppen().size();

                            final List<BerechtigungspruefungBescheinigungGruppeInfo> sortedBescheinigungsGruppen =
                                new ArrayList<BerechtigungspruefungBescheinigungGruppeInfo>(
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
                                        max));
                                // alle Baulasten ermitteln
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBelastet()) {
                                    allBaulasten.add(loadBaulast(baulastInfo));
                                }
                                for (final BerechtigungspruefungBescheinigungBaulastInfo baulastInfo
                                            : bescheinigungsGruppe.getBaulastenBeguenstigt()) {
                                    allBaulasten.add(loadBaulast(baulastInfo));
                                }
                            }

                            if (!allBaulasten.isEmpty()) {
                                // Download: Bericht für alle Baulasten
                                downloads.addAll(BaulastenReportGenerator.generateRasterDownloads(
                                        jobname,
                                        allBaulasten,
                                        downloadInfo.getAuftragsnummer(),
                                        downloadInfo.getProduktbezeichnung()));
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
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Collection<CidsBean> loadOpenDownloads(final User user) {
        try {
            final MetaClass mcBerechtigungspruefung = CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "berechtigungspruefung");

            final String pruefungQuery = "SELECT DISTINCT %d, id "
                        + "FROM berechtigungspruefung "
                        + "WHERE benutzer ILIKE '%s' "
                        + "AND (geprueft IS NOT NULL AND geprueft IS TRUE) "
                        + "AND (abgeholt IS NULL OR abgeholt IS FALSE);";

            final MetaObject[] mos = SessionManager.getProxy()
                        .getMetaObjectByQuery(String.format(
                                pruefungQuery,
                                mcBerechtigungspruefung.getID(),
                                user.getKey()),
                            0);
            if (mos != null) {
                final Collection<CidsBean> beans = new ArrayList<CidsBean>(mos.length);
                for (final MetaObject mo : mos) {
                    beans.add(mo.getBean());
                }
                return beans;
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CidsBean loadPruefung(final String schluessel) {
        try {
            final MetaClass mcBerechtigungspruefung = CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "berechtigungspruefung");

            final String pruefungQuery = "SELECT DISTINCT %d, id "
                        + "FROM berechtigungspruefung "
                        + "WHERE schluessel LIKE '%s' "
                        + "LIMIT 1;";

            final MetaObject[] mos = SessionManager.getProxy()
                        .getMetaObjectByQuery(String.format(pruefungQuery, mcBerechtigungspruefung.getID(), schluessel),
                            0);
            if ((mos != null) && (mos.length > 0)) {
                return mos[0].getBean();
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s1  DOCUMENT ME!
     * @param   s2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int compareString(final String s1, final String s2) {
        if (s1 == null) {
            if (s2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (s1.equals(s2)) {
            return 0;
        } else {
            return s1.compareTo(s2);
        }
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
