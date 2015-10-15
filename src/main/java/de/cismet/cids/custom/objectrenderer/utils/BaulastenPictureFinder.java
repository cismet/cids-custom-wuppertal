/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils;

import org.apache.commons.io.IOUtils;


import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;

import de.cismet.cids.dynamics.CidsBean;

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
//    "TIF", "JPG", "TIFF", "JPEG"};
    public static final String PATH = StaticProperties.ALB_BAULAST_URL_PREFIX; // "http://s102x003/Baulasten/";
    public static final String SUFFIX_REDUCED_SIZE = "_rs";
    public static final String EXTENSION_REDUCED_SIZE = ".jpg";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   cidsBean          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<URL> findPlanPicture(final boolean checkReducedSize, final CidsBean cidsBean) {
        final String picturePath = getPlanPictureFilename(cidsBean);
        if (log.isDebugEnabled()) {
            log.debug("findPlanPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(checkReducedSize, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findPlanPicture(final CidsBean cidsBean) {
        return findPlanPicture(false, cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findReducedPlanPicture(final CidsBean cidsBean) {
        return findPlanPicture(true, cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   blattnummer       DOCUMENT ME!
     * @param   laufendeNummer    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findPlanPicture(final boolean checkReducedSize,
            final String blattnummer,
            final String laufendeNummer) {
        final String picturePath = getPlanPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findPlanPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(checkReducedSize, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findPlanPicture(final String blattnummer, final String laufendeNummer) {
        return findPlanPicture(false, blattnummer, laufendeNummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   cidsBean          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<URL> findTextblattPicture(final boolean checkReducedSize, final CidsBean cidsBean) {
        final String picturePath = getTextblattPictureFilename(cidsBean);
        if (log.isDebugEnabled()) {
            log.debug("findTextblattPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(checkReducedSize, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findTextblattPicture(final CidsBean cidsBean) {
        return findTextblattPicture(false, cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findReducedTextblattPicture(final CidsBean cidsBean) {
        return findTextblattPicture(false, cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   blattnummer       picture DOCUMENT ME!
     * @param   laufendeNummer    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findTextblattPicture(final boolean checkReducedSize,
            final String blattnummer,
            final String laufendeNummer) {
        final String picturePath = getTextblattPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findTextblattPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(checkReducedSize, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findTextblattPicture(final String blattnummer, final String laufendeNummer) {
        return findTextblattPicture(false, blattnummer, laufendeNummer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getTextblattPictureFilename(final CidsBean cidsBean) {
        final String picturePath = (String)cidsBean.getProperty("textblatt");
        final String blattnummer = (String)cidsBean.getProperty("blattnummer");
        final String laufendeNummer = (String)cidsBean.getProperty("laufende_nummer");
        if (picturePath != null) {
            return new StringBuffer(PATH).append(picturePath).append(".").toString();
        } else {
            final String ret = getObjectFilename(blattnummer, laufendeNummer);
            return (ret != null) ? new StringBuffer(ret).append("b.").toString() : null;
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
    public static String getTextblattPictureFilename(final String blattnummer, final String laufendeNummer) {
        final String ret = getObjectFilename(blattnummer, laufendeNummer);

        return (ret != null) ? new StringBuffer(ret).append("b.").toString() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getObjectFilename(final String blattnummer, final String laufendeNummer) {
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

            return new StringBuffer(getFolder(number)).append(SEP)
                        .append(String.format("%06d", number))
                        .append(trenner)
                        .append(String.format("%02d", lfdNr))
                        .toString();
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
    private static String getObjectPath(final String filename) {
        // 001625-01b

        final String numberS = filename.substring(0, 6);
        final int number = new Integer(numberS);
        return new StringBuffer(getFolder(number)).append(SEP).append(filename).append('.').toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getPlanPictureFilename(final CidsBean cidsBean) {
        final String picturePath = (String)cidsBean.getProperty("lageplan");
        final String blattnummer = (String)cidsBean.getProperty("blattnummer");
        final String laufendeNummer = (String)cidsBean.getProperty("laufende_nummer");
        if (picturePath != null) {
            return new StringBuffer(PATH).append(picturePath).append(".").toString();
        } else {
            final String ret = getObjectFilename(blattnummer, laufendeNummer);
            return (ret != null) ? new StringBuffer(ret).append("p.").toString() : null;
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
    public static String getPlanPictureFilename(final String blattnummer, final String laufendeNummer) {
        final String ret = getObjectFilename(blattnummer, laufendeNummer);

        return (ret != null) ? new StringBuffer(ret).append("p.").toString() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   number  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getFolder(final int number) {
        int modulo = (number % 1000);
        if (modulo == 0) {
            modulo = 1000;
        }
        int lowerBorder = (number == 0) ? 0 : (number - modulo);
        final int higherBorder = lowerBorder + 1000;
        if (lowerBorder != 0) {
            lowerBorder += 1;
        }

        final String lb = String.format("%06d", lowerBorder);
        final String hb = String.format("%06d", higherBorder);
        return new StringBuffer(PATH).append(lb).append("-").append(hb).toString();
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
     * @param   checkReducedSize   DOCUMENT ME!
     * @param   fileWithoutSuffix  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<URL> probeWebserverForRightSuffix(final boolean checkReducedSize,
            final String fileWithoutSuffix) {
        return probeWebserverForRightSuffix(checkReducedSize, fileWithoutSuffix, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize   DOCUMENT ME!
     * @param   fileWithoutSuffix  DOCUMENT ME!
     * @param   recursionDepth     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> probeWebserverForRightSuffix(final boolean checkReducedSize,
            final String fileWithoutSuffix,
            final int recursionDepth) {
        if (log.isDebugEnabled()) {
            log.debug("Searching for picture: " + fileWithoutSuffix + "xxx");
        }
        final List<URL> results = new ArrayList<URL>();

        if (checkReducedSize) {
            int counter = 1;
            boolean picfound = true;
            while (picfound) {
                final String tmp = fileWithoutSuffix.substring(0, fileWithoutSuffix.lastIndexOf("."));
                final String urlString = tmp + SUFFIX_REDUCED_SIZE + String.format("%02d", counter)
                            + EXTENSION_REDUCED_SIZE;
                try {
                    final URL objectURL = new URL(urlString);
                    final HttpURLConnection huc = (HttpURLConnection)objectURL.openConnection();
                    huc.setRequestMethod("GET");
                    huc.connect();
                    final int reponse = huc.getResponseCode();
                    if (reponse == 200) {
                        results.add(objectURL);
                        picfound = true;
                    } else {
                        picfound = false;
                    }
                } catch (Exception ex) {
                    log.error("Problem occured, during checking for " + urlString, ex);
                    picfound = false;
                }
                counter++;
            }
        } else {
            for (final String suffix : SUFFIXE) {
                try {
                    final URL objectURL = new URL(fileWithoutSuffix + suffix);

                    final HttpURLConnection huc = (HttpURLConnection)objectURL.openConnection();
                    huc.setRequestMethod("GET");
                    huc.connect();
                    final int reponse = huc.getResponseCode();
                    if (reponse == 200) {
                        results.add(objectURL);
                    }
                } catch (Exception ex) {
                    log.error("Problem occured, during checking for " + fileWithoutSuffix + suffix, ex);
                }
            }
        }

        if (results.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("No picture file found. Check for Links");
            }
            if (recursionDepth < 3) {
                try {
                    final URL objectURL = new URL(fileWithoutSuffix + LINKEXTENSION);
                    final HttpURLConnection huc = (HttpURLConnection)objectURL.openConnection();
                    huc.setRequestMethod("GET");
                    huc.connect();
                    final int reponse = huc.getResponseCode();
                    if (reponse == 200) {
                        final String link = IOUtils.toString(huc.getInputStream());
                        return probeWebserverForRightSuffix(
                                checkReducedSize,
                                getObjectPath(link.trim()),
                                recursionDepth
                                        + 1);
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
