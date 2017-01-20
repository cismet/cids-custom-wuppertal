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

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.newuser.User;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.jfif.JfifDescriptor;
import com.drew.metadata.jfif.JfifDirectory;
import com.drew.metadata.jpeg.JpegDescriptor;
import com.drew.metadata.jpeg.JpegDirectory;

import com.twelvemonkeys.imageio.metadata.CompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Entry;
import com.twelvemonkeys.imageio.metadata.exif.EXIFReader;
import com.twelvemonkeys.imageio.metadata.exif.Rational;
import com.twelvemonkeys.imageio.metadata.exif.TIFF;
import com.twelvemonkeys.util.LinkedSet;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.objectrenderer.utils.BaulastenPictureFinder;
import de.cismet.cids.custom.objectrenderer.utils.billing.BillingPopup;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.tools.gui.WebAccessMultiPagePictureReader;
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
    private static final String PARAMETER_FABRICATIONNOTICE = "FABRICATIONNOTICE";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaulastenReportGenerator.class);

    static final double INCH_IN_MM = 25.4;

    static final int MAX_WIDTH_DINA4 = 170;
    static final int MAX_HEIGHT_DINA4 = 257;

    static final int MAX_WIDTH_DINA3 = 257;
    static final int MAX_HEIGHT_DINA3 = 380;

    static final int MAX_WIDTH_DINA2 = 380;
    static final int MAX_HEIGHT_DINA2 = 554;

    static final int MAX_WIDTH_DINA1 = 554;
    static final int MAX_HEIGHT_DINA1 = 801;

    static final int MAX_WIDTH_DINA0 = 801;
    static final int MAX_HEIGHT_DINA0 = 1149;

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
     * @param   user  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public static String createFertigungsVermerk(final User user) throws ConnectionException {
        final String fertigungsVermerk = SessionManager.getConnection()
                    .getConfigAttr(user, "custom.baulasten.fertigungsVermerk@WUNDA_BLAU");
        if (fertigungsVermerk != null) {
            return fertigungsVermerk;
        } else {
            final CidsBean billingLogin = (CidsBean)BillingPopup.getInstance().getExternalUser(user);
            if (billingLogin != null) {
                final CidsBean billingKunde = (CidsBean)billingLogin.getProperty("kunde");
                if (billingKunde != null) {
                    return (String)billingKunde.getProperty("name");
                }
            }
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dir  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MetadataInfo createMetadataInfoFromTiff(final com.twelvemonkeys.imageio.metadata.Directory dir) {
        if (dir == null) {
            return null;
        }
        final Entry pageNumberEntry = dir.getEntryById(TIFF.TAG_PAGE_NUMBER);
        final Entry imageWidthEntry = dir.getEntryById(TIFF.TAG_IMAGE_WIDTH);
        final Entry imageHeightEntry = dir.getEntryById(TIFF.TAG_IMAGE_HEIGHT);
        final Entry xResolutionEntry = dir.getEntryById(TIFF.TAG_X_RESOLUTION);
        final Entry yResolutionEntry = dir.getEntryById(TIFF.TAG_Y_RESOLUTION);

        final int pageNumber = (pageNumberEntry != null) ? (((int[])pageNumberEntry.getValue())[0] + 1) : -1;
        try {
            final int imageWidth = Integer.parseInt(imageWidthEntry.getValue().toString());
            final int imageHeight = Integer.parseInt(imageHeightEntry.getValue().toString());
            final int xResolution = ((Rational)xResolutionEntry.getValue()).intValue();
            final int yResolution = ((Rational)yResolutionEntry.getValue()).intValue();

            return new MetadataInfo(pageNumber, imageWidth, imageHeight, xResolution, yResolution);
        } catch (final Exception ex) {
            LOG.info("could not extract metadata for this page", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   metadata  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static MetadataInfo createMetadataInfoFromJpeg(final Metadata metadata) {
        try {
            final JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            final JfifDirectory jfifDirectory = metadata.getFirstDirectoryOfType(JfifDirectory.class);

            final JpegDescriptor descJPeg = new JpegDescriptor(jpegDirectory);

            final String widthDesc = descJPeg.getImageWidthDescription();
            final String heightDesc = descJPeg.getImageHeightDescription();

            final JfifDescriptor descJFif = new JfifDescriptor(jfifDirectory);

//                                            final String unitDesc = descJFif.getImageResUnitsDescription();
            final String xResDesc = descJFif.getImageResXDescription();
            final String yResDesc = descJFif.getImageResYDescription();

            if ((widthDesc != null) && (heightDesc != null) && (xResDesc != null) && (yResDesc != null)) {
                final long width = Long.parseLong(widthDesc.replaceAll(" pixels", ""));
                final long height = Long.parseLong(heightDesc.replaceAll(" pixels", ""));
                final int dpiX = Integer.parseInt(xResDesc.replaceAll(" dots", ""));
                final int dpiY = Integer.parseInt(yResDesc.replaceAll(" dots", ""));

                return new MetadataInfo(1, width, height, dpiX, dpiY);
            } else {
                return null;
            }
        } catch (final Exception ex) {
            LOG.warn("couldn't read the metadata", ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   jobname            DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Collection<? extends Download> generateRasterDownloads(
            final String jobname,
            final Collection<CidsBean> selectedBaulasten,
            final String jobnumber,
            final String projectname) throws Exception {
        final Collection<Download> downloads = new ArrayList<Download>();
        downloads.add(createJasperDownload(

                Type.TEXTBLATT_PLAN_RASTER,
                selectedBaulasten,
                jobname,
                jobnumber,
                projectname,
                "Bericht aus dem Baulastenverzeichnis"));

        final Collection<URL> additionalFilesToDownload = new LinkedSet<URL>();
        for (final CidsBean selectedBaulast : selectedBaulasten) {
            final List<URL> urlListRasterdaten = BaulastenPictureFinder.findPlanPicture(
                    selectedBaulast);
            additionalFilesToDownload.addAll(urlListRasterdaten);
        }

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
        return downloads;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type               DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Download generateDownload(final Type type,
            final Collection<CidsBean> selectedBaulasten,
            final String jobnumber,
            final String projectname) throws Exception {
        final String jobname = DownloadManagerDialog.getJobname();
        if (Type.TEXTBLATT_PLAN_RASTER.equals(type)) {
            final BackgroundTaskMultipleDownload.FetchDownloadsTask fetchDownloadsTask =
                new BackgroundTaskMultipleDownload.FetchDownloadsTask() {

                    @Override
                    public Collection<? extends Download> fetchDownloads() throws Exception {
                        return generateRasterDownloads(jobname, selectedBaulasten, jobnumber, projectname);
                    }
                };
            return new BackgroundTaskMultipleDownload(null, jobname, fetchDownloadsTask);
        } else {
            return createJasperDownload(type, selectedBaulasten, jobname, jobnumber, projectname, projectname);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type               DOCUMENT ME!
     * @param   selectedBaulasten  DOCUMENT ME!
     * @param   jobname            DOCUMENT ME!
     * @param   jobnumber          DOCUMENT ME!
     * @param   projectname        DOCUMENT ME!
     * @param   title              DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Download createJasperDownload(final Type type,
            final Collection<CidsBean> selectedBaulasten,
            final String jobname,
            final String jobnumber,
            final String projectname,
            final String title) throws Exception {
        final HashMap parameters = new HashMap();

        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    try {
                        final List<CidsBean> sortedBaulasten = new ArrayList<CidsBean>(selectedBaulasten);
                        Collections.sort(sortedBaulasten, new Comparator<CidsBean>() {

                                @Override
                                public int compare(final CidsBean o1, final CidsBean o2) {
                                    final String bnr1 = (o1 == null) ? "" : (String)o1.getProperty("blattnummer");
                                    final String bnr2 = (o2 == null) ? "" : (String)o2.getProperty("blattnummer");

                                    final Integer lfdN1 = (o1 == null)
                                        ? -1 : Integer.parseInt((String)o1.getProperty("laufende_nummer"));
                                    final int lfdN2 = (o2 == null)
                                        ? -1 : Integer.parseInt((String)o2.getProperty("laufende_nummer"));

                                    if (!bnr1.equalsIgnoreCase(bnr2)) {
                                        return bnr1.compareToIgnoreCase(bnr2);
                                    } else {
                                        return lfdN1.compareTo(lfdN2);
                                    }
                                }
                            });

                        final Collection<BaulastReportBean> reportBeans = new LinkedList<BaulastReportBean>();
                        final Collection<BaulastImageReportBean> imageBeans = new LinkedList<BaulastImageReportBean>();
                        final Collection<BaulastPlotempfehlungReportBean> plotBeans =
                            new LinkedList<BaulastPlotempfehlungReportBean>();

                        final Map startingPages = new HashMap();
                        int tileTableRows = 10 + sortedBaulasten.size();

                        final Map imageAvailable = new HashMap();

                        if (Type.TEXTBLATT_PLAN_RASTER.equals(type)) {
                            int rasterPages = 0;
                            for (final CidsBean selectedBaulast : sortedBaulasten) {
                                final List<URL> urlListRasterdaten = BaulastenPictureFinder.findPlanPicture(
                                        selectedBaulast);

                                for (final URL url : urlListRasterdaten) {
//
//                                final WebAccessMultiPagePictureReader reader = new WebAccessMultiPagePictureReader(new File(
//                                            "/home/jruiz/Downloads/000509-02p.tif"));
                                    final WebAccessMultiPagePictureReader reader = new WebAccessMultiPagePictureReader(
                                            url,
                                            false,
                                            false);

                                    final EXIFReader exif = new EXIFReader();

                                    final Map<Integer, MetadataInfo> metadatainfoPerPage = new HashMap();

                                    final InputStream is = reader.getInputStream();
                                    if (WebAccessMultiPagePictureReader.CODEC_JPEG.equals(reader.getCodec())) {
                                        final Metadata metadata = ImageMetadataReader.readMetadata(is);
                                        final MetadataInfo metadataInfo = createMetadataInfoFromJpeg(metadata);
                                        if (metadataInfo != null) {
                                            metadatainfoPerPage.put(metadataInfo.getPageNumber(), metadataInfo);
                                        }
                                    } else if (WebAccessMultiPagePictureReader.CODEC_TIFF.equals(reader.getCodec())) {
                                        final com.twelvemonkeys.imageio.metadata.Directory dir = exif.read(ImageIO
                                                        .createImageInputStream(is));
                                        final Map<Integer, MetadataInfo> metadataInfoPerPageFallBack = new HashMap();
                                        final CompoundDirectory dirs = (CompoundDirectory)dir;
                                        boolean exifPageError = false;
                                        for (int i = 0; i < dirs.directoryCount(); i++) {
                                            final com.twelvemonkeys.imageio.metadata.Directory subDir =
                                                dirs.getDirectory(
                                                    i);
                                            final MetadataInfo metadataInfo = createMetadataInfoFromTiff(subDir);
                                            if (metadataInfo != null) {
                                                if ((metadataInfo.getPageNumber() <= 0)
                                                            || (metadataInfo.getPageNumber() > dirs.directoryCount())
                                                            || metadataInfoPerPageFallBack.keySet().contains(
                                                                metadataInfo.getPageNumber())) {
                                                    // between 1 and max, and not a duplicate
                                                    exifPageError = true;
                                                }
                                                metadatainfoPerPage.put(metadataInfo.getPageNumber(), metadataInfo);
                                                metadataInfoPerPageFallBack.put(i + 1, metadataInfo);
                                            } else {
                                                exifPageError = true;
                                            }
                                        }
                                        if (metadataInfoPerPageFallBack.size() != dirs.directoryCount()) {
                                            // missing exif data ?
                                            exifPageError = true;
                                        }
                                        if (exifPageError) {
                                            // correct the pagenumber
                                            for (final int pageNumber : metadataInfoPerPageFallBack.keySet()) {
                                                final MetadataInfo metadataInfo = metadataInfoPerPageFallBack.get(
                                                        pageNumber);
                                                metadataInfo.setPageNumber(pageNumber);
                                            }
                                            // using the fallback hashmap
                                            metadatainfoPerPage.clear();
                                            metadatainfoPerPage.putAll(metadataInfoPerPageFallBack);
                                        }
                                    }

                                    for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                                        final MetadataInfo metadataInfo = metadatainfoPerPage.get(page);

                                        final String plotempfehlung = (metadataInfo != null)
                                            ? calculateDinFormat(metadataInfo.getWidth(),
                                                metadataInfo.getHeight(),
                                                metadataInfo.getDpiWidth(),
                                                metadataInfo.getDpiHeight()) : null;
                                        plotBeans.add(new BaulastPlotempfehlungReportBean(
                                                page,
                                                reader.getNumberOfPages(),
                                                url.getFile().substring(url.getFile().lastIndexOf("/") + 1),
                                                (String)selectedBaulast.getProperty("blattnummer"),
                                                (String)selectedBaulast.getProperty("laufende_nummer"),
                                                (plotempfehlung != null) ? plotempfehlung : "-"));
                                    }
                                    rasterPages += reader.getNumberOfPages();
                                }
                            }
                            tileTableRows += 5 + rasterPages;
                        }
                        int startingPage = 2 + (int)(tileTableRows / 37);

                        for (final CidsBean selectedBaulast : sortedBaulasten) {
                            final List<URL> urlListTextblatt = new ArrayList<URL>();
                            final List<URL> urlListLageplan = new ArrayList<URL>();
                            try {
                                urlListTextblatt.addAll(BaulastenPictureFinder.findReducedTextblattPicture(
                                        selectedBaulast));
                            } catch (final Exception ex) {
                                // TODO: User feedback?
                                LOG.warn("Could not include raster document for baulast '"
                                            + selectedBaulast.toJSONString(true)
                                            + "'.",
                                    ex);
                                continue;
                            }

                            if (Type.TEXTBLATT_PLAN.equals(type) || Type.TEXTBLATT_PLAN_RASTER.equals(type)) {
                                urlListLageplan.addAll(BaulastenPictureFinder.findReducedPlanPicture(selectedBaulast));
                            }
                            if (urlListTextblatt.isEmpty()) {
                                LOG.info("No document URLS found for the Baulasten report");
                            }
                            int pageCount = 0;
                            final List<URL> urlList = new ArrayList<URL>(urlListTextblatt);
                            urlList.addAll(urlListLageplan);
                            for (final URL url : urlList) {
                                try {
//                                final WebAccessMultiPagePictureReader reader = new WebAccessMultiPagePictureReader(new File(
//                                            "/home/jruiz/Downloads/000509-02p.tif"));
                                    final WebAccessMultiPagePictureReader reader = new WebAccessMultiPagePictureReader(
                                            url,
                                            false,
                                            false);
                                    for (int i = 0; i < reader.getNumberOfPages(); i++) {
                                        imageBeans.add(new BaulastImageReportBean(
                                                i,
                                                reader));
                                    }
                                    pageCount += reader.getNumberOfPages();
                                } catch (final Exception ex) {
                                    LOG.warn("Could not read document from URL '" + url.toExternalForm()
                                                + "'. Skipping this url.",
                                        ex);
                                }
                            }

                            imageAvailable.put(selectedBaulast.getProperty("id"), pageCount > 0);

//                        if (reader == null) {
//                            // Couldn't open any image.
//                            continue;
//                        }
                            final String startingPageString = Integer.toString(startingPage);

                            startingPages.put(selectedBaulast.getProperty("id"), startingPageString);
                            startingPage += pageCount;
                        }

                        reportBeans.add(new BaulastReportBean(
                                sortedBaulasten,
                                imageBeans,
                                plotBeans));

                        parameters.put(PARAMETER_JOBNUMBER, jobnumber);
                        parameters.put(PARAMETER_PROJECTNAME, projectname);
                        parameters.put(PARAMETER_TYPE, type.toString());
                        parameters.put(PARAMETER_STARTINGPAGES, startingPages);
                        parameters.put(PARAMETER_IMAGEAVAILABLE, imageAvailable);
                        parameters.put(
                            PARAMETER_FABRICATIONNOTICE,
                            createFertigungsVermerk(SessionManager.getSession().getUser()));
                        return new JRBeanCollectionDataSource(reportBeans);
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
                    return parameters;
                }
            };

        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/baulasten.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                title,
                "baulasten");

        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pixX  DOCUMENT ME!
     * @param   pixY  DOCUMENT ME!
     * @param   dpiX  DOCUMENT ME!
     * @param   dpiY  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String calculateDinFormat(final long pixX, final long pixY, final int dpiX, final int dpiY) {
        if ((dpiX > 0) && (dpiY > 0)) {
            final double mmX = (pixX / dpiX) * INCH_IN_MM;
            final double mmY = (pixY / dpiY) * INCH_IN_MM;

            final double cmWidth = (mmX <= mmY) ? mmX : mmY;
            final double cmHeight = (mmX <= mmY) ? mmY : mmX;

            final String format = (mmX <= mmY) ? "Hochformat" : "Querformat";

            final String din;
            if ((cmWidth < MAX_WIDTH_DINA4) && (cmHeight < MAX_HEIGHT_DINA4)) {
                din = "DIN A4";
            } else if ((cmWidth < MAX_WIDTH_DINA3) && (cmHeight < MAX_HEIGHT_DINA3)) {
                din = "DIN A3";
            } else if ((cmWidth < MAX_WIDTH_DINA2) && (cmHeight < MAX_HEIGHT_DINA2)) {
                din = "DIN A2";
            } else if ((cmWidth < MAX_WIDTH_DINA1) && (cmHeight < MAX_HEIGHT_DINA1)) {
                din = "DIN A1";
            } else if ((cmWidth < MAX_WIDTH_DINA0) && (cmHeight < MAX_HEIGHT_DINA0)) {
                din = "DIN A0";
            } else {
                return null;
            }
            return din + " " + format;
        } else {
            return null;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class BaulastReportBean {

        //~ Instance fields ----------------------------------------------------

        private final Collection<CidsBean> baulasten;
        private final Collection<BaulastImageReportBean> images;
        private final Collection<BaulastPlotempfehlungReportBean> plotempfehlungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class BaulastImageReportBean {

        //~ Instance fields ----------------------------------------------------

        private final Integer page;
        private final WebAccessMultiPagePictureReader reader;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class BaulastPlotempfehlungReportBean {

        //~ Instance fields ----------------------------------------------------

        private final Integer page;
        private final Integer totalPages;
        private final String fileName;
        private final String blattnummer;
        private final String laufende_nummer;
        private final String plotempfehlung;
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    public static class MetadataInfo {

        //~ Instance fields ----------------------------------------------------

        private int pageNumber;
        private long width;
        private long height;
        private int dpiWidth;
        private int dpiHeight;
    }
}
