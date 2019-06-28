/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils;

import org.apache.commons.io.IOUtils;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.utils.StaticProperties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class BaulastenPictureFinder {

    //~ Static fields/initializers ---------------------------------------------

    public static final String SEP = "/";
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(BaulastenPictureFinder.class);
    private static final String[] SUFFIXE = new String[] { "tif", "jpg", "tiff", "jpeg" };
    private static final String LINKEXTENSION = "txt";
    public static final String SUFFIX_REDUCED_SIZE = "_rs";
    public static final String EXTENSION_REDUCED_SIZE = "jpg";
    private static final String DOWNLOAD_TEMPLATE =
        "<rasterfari:url>?REQUEST=GetMap&SERVICE=WMS&customDocumentInfo=download&LAYERS=<rasterfari:document>";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean          DOCUMENT ME!
     * @param   checkReducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findPlanPicture(final CidsBean cidsBean, final boolean checkReducedSize) {
        final FileWithoutSuffix picturePath = getPlanPictureFilename(cidsBean);
        if (log.isDebugEnabled()) {
            log.debug("findPlanPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(picturePath, checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findPlanPicture(final CidsBean cidsBean) {
        return findPlanPicture(cidsBean, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer       DOCUMENT ME!
     * @param   laufendeNummer    DOCUMENT ME!
     * @param   checkReducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findPlanPicture(final String blattnummer,
            final String laufendeNummer,
            final boolean checkReducedSize) {
        final FileWithoutSuffix picturePath = getPlanPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findPlanPicture: " + picturePath.toString());
        }
        return probeWebserverForRightSuffix(picturePath, checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findPlanPicture(final String blattnummer, final String laufendeNummer) {
        return findPlanPicture(blattnummer, laufendeNummer, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean          DOCUMENT ME!
     * @param   checkReducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findTextblattPicture(final CidsBean cidsBean, final boolean checkReducedSize) {
        final FileWithoutSuffix picturePath = getTextblattPictureFilename(cidsBean);
        if (log.isDebugEnabled()) {
            log.debug("findTextblattPicture: " + picturePath.toString());
        }
        return probeWebserverForRightSuffix(picturePath, checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findTextblattPicture(final CidsBean cidsBean) {
        return findTextblattPicture(cidsBean, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   document  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static URL getUrlForDocument(final String document) throws Exception {
        return new URL(DOWNLOAD_TEMPLATE.replace(
                    "<rasterfari:url>",
                    ClientStaticProperties.getInstance().getAlbBaulastUrlPrefix()).replace(
                    "<rasterfari:document>",
                    document));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer       picture DOCUMENT ME!
     * @param   laufendeNummer    DOCUMENT ME!
     * @param   checkReducedSize  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findTextblattPicture(final String blattnummer,
            final String laufendeNummer,
            final boolean checkReducedSize) {
        final FileWithoutSuffix picturePath = getTextblattPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findTextblattPicture: " + picturePath.toString());
        }
        return probeWebserverForRightSuffix(picturePath, checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> findTextblattPicture(final String blattnummer, final String laufendeNummer) {
        return findTextblattPicture(blattnummer, laufendeNummer, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FileWithoutSuffix getTextblattPictureFilename(final CidsBean cidsBean) {
        final String picturePath = (String)cidsBean.getProperty("textblatt");
        final String blattnummer = (String)cidsBean.getProperty("blattnummer");
        final String laufendeNummer = (String)cidsBean.getProperty("laufende_nummer");
        if (picturePath != null) {
//            return new StringBuffer(checkReducedSize ? PATH_RS : PATH).append(picturePath).append(".").toString();

            return new FileWithoutSuffix(null, picturePath + ".");
        } else {
            final FileWithoutSuffix ret = getObjectFilename(blattnummer, laufendeNummer);
            if (ret != null) {
                ret.file = ret.file + "b.";
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FileWithoutSuffix getTextblattPictureFilename(final String blattnummer, final String laufendeNummer) {
        final FileWithoutSuffix ret = getObjectFilename(blattnummer, laufendeNummer);
        if (ret != null) {
            ret.file = ret.file + "b.";
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FileWithoutSuffix getObjectFilename(final String blattnummer, final String laufendeNummer) {
        if (laufendeNummer == null) {
            return null;
        } else {
            final int lfdNr = new Integer(laufendeNummer);
            String trenner = "-";
            int number = 0;
            if (blattnummer.length() == 6) {
                number = new Integer(blattnummer);
            } else {
                // length==7
                number = new Integer(blattnummer.substring(0, 6));
                trenner = blattnummer.substring(6, 7);
            }
            final String file = new StringBuffer(String.format("%06d", number)).append(trenner)
                        .append(String.format("%02d", lfdNr))
                        .toString();

            return new FileWithoutSuffix(number, file);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static int getBlattnummer(final String blattnummer) {
        int number = 0;
        if (blattnummer.length() == 6) {
            number = new Integer(blattnummer);
        } else {
            // length==7
            number = new Integer(blattnummer.substring(0, 6));
        }
        return number;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getObjectFilenameWithoutFolder(final String blattnummer, final String laufendeNummer) {
        if (laufendeNummer == null) {
            return null;
        } else {
            final int lfdNr = new Integer(laufendeNummer);
            String trenner = "-";
            int number = 0;
            if (blattnummer.length() == 6) {
                number = new Integer(blattnummer);
            } else {
                // length==7
                number = new Integer(blattnummer.substring(0, 6));
                trenner = blattnummer.substring(6, 7);
            }

            return new StringBuffer().append(String.format("%06d", number))
                        .append(trenner)
                        .append(String.format("%02d", lfdNr))
                        .toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   filename  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static FileWithoutSuffix getObjectPath(final String filename) {
        // 001625-01b

        final String numberS = filename.substring(0, 6);
        final int number = new Integer(numberS);
        return new FileWithoutSuffix(number, filename + ".");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FileWithoutSuffix getPlanPictureFilename(final CidsBean cidsBean) {
        final String picturePath = (String)cidsBean.getProperty("lageplan");
        final String blattnummer = (String)cidsBean.getProperty("blattnummer");
        final String laufendeNummer = (String)cidsBean.getProperty("laufende_nummer");
        if (picturePath != null) {
            return new FileWithoutSuffix(null, picturePath + ".");
        } else {
            final FileWithoutSuffix ret = getObjectFilename(blattnummer, laufendeNummer);
            if (ret != null) {
                ret.file = ret.file + "p.";
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FileWithoutSuffix getPlanPictureFilename(final String blattnummer, final String laufendeNummer) {
        final FileWithoutSuffix ret = getObjectFilename(blattnummer, laufendeNummer);
        if (ret != null) {
            ret.file = ret.file + "p.";
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getFolderWihoutPath(final int number) {
        int modulo = (number % 1000);
        if (modulo == 0) {
            modulo = 1000;
        }
        int lowerBorder = number - modulo;
        final int higherBorder = lowerBorder + 1000;
        if (lowerBorder != 0) {
            lowerBorder += 1;
        }

        final String lb = String.format("%06d", lowerBorder);
        final String hb = String.format("%06d", higherBorder);
        return new StringBuffer().append(lb).append("-").append(hb).toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileWithoutSuffix  DOCUMENT ME!
     * @param   checkReducedSize   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<String> probeWebserverForRightSuffix(final FileWithoutSuffix fileWithoutSuffix,
            final boolean checkReducedSize) {
        return probeWebserverForRightSuffix(fileWithoutSuffix, 0, checkReducedSize);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileWithoutSuffix  DOCUMENT ME!
     * @param   recursionDepth     DOCUMENT ME!
     * @param   checkReducedSize   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<String> probeWebserverForRightSuffix(final FileWithoutSuffix fileWithoutSuffix,
            final int recursionDepth,
            final boolean checkReducedSize) {
        final String fileWithoutSuffixString = fileWithoutSuffix.toString();
        if (log.isDebugEnabled()) {
            log.debug("Searching for picture: " + fileWithoutSuffixString + "xxx");
        }
        final List<String> results = new ArrayList<>();

        if (checkReducedSize) {
            int counter = 1;
            boolean picfound = true;
            while (picfound) {
                final String fileWithSuffix = fileWithoutSuffixString.substring(
                        0,
                        fileWithoutSuffixString.lastIndexOf("."))
                            + SUFFIX_REDUCED_SIZE + String.format("%02d", counter) + "." + EXTENSION_REDUCED_SIZE;
                try {
                    final URL objectURL = getUrlForDocument(fileWithSuffix);
                    picfound = WebAccessManager.getInstance().checkIfURLaccessible(objectURL);
                    if (picfound) {
                        results.add(fileWithSuffix);
                    }
                } catch (Exception ex) {
                    log.error("Problem occured, during checking for " + fileWithSuffix, ex);
                    picfound = false;
                }
                counter++;
            }
        } else {
            for (final String suffix : SUFFIXE) {
                try {
                    final String fileWithSuffix = fileWithoutSuffixString + suffix;
                    final URL objectURL = getUrlForDocument(fileWithSuffix);
                    if (WebAccessManager.getInstance().checkIfURLaccessible(objectURL)) {
                        results.add(fileWithSuffix);
                    }
                } catch (Exception ex) {
                    log.error("Problem occured, during checking for " + fileWithoutSuffixString + suffix, ex);
                }
            }
        }

        if (results.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No picture file found. Check for Links");
            }
            if (recursionDepth < 3) {
                try {
                    final URL objectURL = getUrlForDocument(fileWithoutSuffix + LINKEXTENSION);
                    if (WebAccessManager.getInstance().checkIfURLaccessible(objectURL)) {
                        final String link = IOUtils.toString(WebAccessManager.getInstance().doRequest(objectURL),
                                "UTF-8");
                        return probeWebserverForRightSuffix(getObjectPath(link.trim()),
                                recursionDepth
                                        + 1,
                                checkReducedSize);
                    }
                } catch (Exception ex) {
                    log.error(ex, ex);
                }
            } else {
                log.error(
                    "No hop,hop,hop possible within this logic. Seems to be an endless loop, sorry.",
                    new Exception("JustTheStackTrace"));
            }
        }
        return results;
    }
}
