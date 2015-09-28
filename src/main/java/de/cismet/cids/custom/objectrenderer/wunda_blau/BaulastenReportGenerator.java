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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperDownload;

import de.cismet.tools.gui.MultiPagePictureReader;
import de.cismet.tools.gui.downloadmanager.BackgroundTaskMultipleDownload;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BaulastenReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PARAMETER_JOBNUMBER = "JOBNUMBER";
    private static final String PARAMETER_PROJECTNAME = "PROJECTNAME";
    private static final String PARAMETER_TYPE = "TYPE";
    private static final String PARAMETER_STARTINGPAGES = "STARTINGPAGES";
    private static final String PARAMETER_IMAGEAVAILABLE = "IMAGEAVAILABLE";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaulastenReportGenerator.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum Type {

        //~ Enum constants -----------------------------------------------------

        TEXTBLATT("Bericht mit Textblättern"), TEXTBLATT_PLAN("Bericht mit Textblättern und Plänen"),
        TEXTBLATT_PLAN_RASTER("Bericht mit Textblättern, Plänen und Rasterdateien");

        //~ Instance fields ----------------------------------------------------

        private final String string;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new Type object.
         *
         * @param  string  DOCUMENT ME!
         */
        Type(final String string) {
            this.string = string;
        }

        //~ Methods ------------------------------------------------------------

        // the toString just returns the given name
        @Override
        public String toString() {
            return string;
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   type               DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download generateDownload(final Type type,
            final Collection<CidsBean> selectedBaulasten,
            final String jobnumber,
            final String projectname) {
        final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
            new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                @Override
                public Collection<? extends Download> fetchDownloads() throws Exception {
                    final Collection<BaulastReportBean> reportBeans = new LinkedList<BaulastReportBean>();
                    final Collection<BaulastImageReportBean> imageBeans = new LinkedList<BaulastImageReportBean>();
                    final Collection<URL> additionalFilesToDownload = new LinkedList<URL>();

                    final Map startingPages = new HashMap();
                    int startingPage = 2;
                    if (selectedBaulasten.size() > 27) {
                        startingPage += Math.ceil((selectedBaulasten.size() - 27D) / 37D);
                    }

                    final Map imageAvailable = new HashMap();

                    for (final CidsBean selectedBaulast : selectedBaulasten) {
                        final List<URL> urlListTextblatt = new ArrayList<URL>();
                        final List<URL> urlListLageplan = new ArrayList<URL>();
                        try {
                            urlListTextblatt.addAll(BaulastenPictureFinder.findTextblattPicture(selectedBaulast));
                        } catch (final Exception ex) {
                            // TODO: User feedback?
                            LOG.warn("Could not include raster document for baulast '"
                                        + selectedBaulast.toJSONString(true)
                                        + "'.",
                                ex);
                            continue;
                        }

                        if (Type.TEXTBLATT_PLAN.equals(type) || Type.TEXTBLATT_PLAN_RASTER.equals(type)) {
                            urlListLageplan.addAll(BaulastenPictureFinder.findPlanPicture(selectedBaulast));
                        }
                        if (urlListTextblatt.isEmpty()) {
                            LOG.info("No document URLS found for the Baulasten report");
                        }
                        MultiPagePictureReader reader = null;
                        int pageCount = 0;
                        final List<URL> urlList = new ArrayList<URL>(urlListTextblatt);
                        urlList.addAll(urlListLageplan);
                        for (final URL url : urlList) {
                            try {
                                reader = new MultiPagePictureReader(url, false, false);
                                for (int i = 0; i < reader.getNumberOfPages(); i++) {
                                    imageBeans.add(new BaulastImageReportBean(
                                            i,
                                            reader));
                                }
                                pageCount += reader.getNumberOfPages();
                                if (Type.TEXTBLATT_PLAN_RASTER.equals(type) && urlListLageplan.contains(url)) {
                                    additionalFilesToDownload.add(url);
                                }
                            } catch (final Exception ex) {
                                LOG.warn("Could not read document from URL '" + url.toExternalForm()
                                            + "'. Skipping this url.",
                                    ex);
                            }
                        }

                        imageAvailable.put(selectedBaulast.getProperty("id"), pageCount > 0);

                        if (reader == null) {
                            // Couldn't open any image.
                            continue;
                        }

                        final String startingPageString = Integer.toString(startingPage);

                        startingPages.put(selectedBaulast.getProperty("id"), startingPageString);
                        startingPage += pageCount;
                    }

                    reportBeans.add(new BaulastReportBean(
                            selectedBaulasten,
                            imageBeans));
                    final JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reportBeans);

                    final HashMap parameters = new HashMap();
                    parameters.put(PARAMETER_JOBNUMBER, jobnumber);
                    parameters.put(PARAMETER_PROJECTNAME, projectname);
                    parameters.put(PARAMETER_TYPE, type.toString());
                    parameters.put(PARAMETER_STARTINGPAGES, startingPages);
                    parameters.put(PARAMETER_IMAGEAVAILABLE, imageAvailable);

                    final JasperReport jasperReport = (JasperReport)JRLoader.loadObject(getClass().getResourceAsStream(
                                "/de/cismet/cids/custom/wunda_blau/res/baulasten.jasper"));

                    final String jobname = DownloadManagerDialog.getJobname();

                    final JasperDownload jasperDownload = new JasperDownload(
                            jasperReport,
                            parameters,
                            dataSource,
                            jobname,
                            projectname,
                            "baulasten");

                    final Collection<Download> downloads = new ArrayList<Download>();
                    downloads.add(jasperDownload);

                    if (!additionalFilesToDownload.isEmpty()) {
                        for (final URL additionalFileToDownload : additionalFilesToDownload) {
                            final String file = additionalFileToDownload.getFile()
                                        .substring(additionalFileToDownload.getFile().lastIndexOf('/') + 1);
                            final String filename = file.substring(0, file.lastIndexOf('.'));
                            final String extension = file.substring(file.lastIndexOf('.'));

                            downloads.add(new HttpDownload(
                                    additionalFileToDownload,
                                    null,
                                    jobname,
                                    file,
                                    filename,
                                    extension));
                        }
                    }
                    return downloads;
                }
            };
        return new BackgroundTaskMultipleDownload(null, projectname, fetchDownloadsTask);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class BaulastReportBean {

        //~ Instance fields ----------------------------------------------------

        private final Collection<CidsBean> baulasten;
        private final Collection<BaulastImageReportBean> images;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BaulastenReportBean object.
         *
         * @param  baulasten  DOCUMENT ME!
         * @param  images     DOCUMENT ME!
         */
        public BaulastReportBean(final Collection<CidsBean> baulasten,
                final Collection<BaulastImageReportBean> images) {
            this.baulasten = baulasten;
            this.images = images;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<CidsBean> getBaulasten() {
            return baulasten;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Collection<BaulastImageReportBean> getImages() {
            return images;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class BaulastImageReportBean {

        //~ Instance fields ----------------------------------------------------

        private final Integer page;
        private final MultiPagePictureReader reader;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new BaulastblattReportBean object.
         *
         * @param  page    DOCUMENT ME!
         * @param  reader  DOCUMENT ME!
         */
        public BaulastImageReportBean(final Integer page,
                final MultiPagePictureReader reader) {
            this.page = page;
            this.reader = reader;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Integer getPage() {
            return page;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public MultiPagePictureReader getReader() {
            return reader;
        }
    }
}
