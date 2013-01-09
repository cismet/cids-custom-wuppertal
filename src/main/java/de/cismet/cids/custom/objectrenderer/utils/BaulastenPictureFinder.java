/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils;

import java.io.File;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

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
//    "TIF", "JPG", "TIFF", "JPEG"};
    public static final String PATH = "http://s102is/Baulasten/";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     picture DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findPlanPicture(final String blattnummer, final String laufendeNummer) {
        final String picturePath = getPlanPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findPlanPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   blattnummer     picture DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findTextblattPicture(final String blattnummer, final String laufendeNummer) {
        final String picturePath = getTextblattPictureFilename(blattnummer, laufendeNummer);
        if (log.isDebugEnabled()) {
            log.debug("findTextblattPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(picturePath);
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
    private static String getObjectFilename(final String blattnummer, final String laufendeNummer) {
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
    private static String getFolder(final int number) {
        int lowerBorder = number - (number % 1000);
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
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        Log4JQuickConfig.configure4LumbermillOnLocalhost();
        System.out.println(getFolder(50));
        System.out.println(getFolder(1050));
        System.out.println(getFolder(1000));
        System.out.println(getFolder(1001));
        System.out.println(getFolder(12001));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileWithoutSuffix  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<File> probeFilesystemForRightSuffix(final String fileWithoutSuffix) {
        if (log.isDebugEnabled()) {
            log.debug("Searching for picture: " + fileWithoutSuffix + "xxx");
        }
        final List<File> results = TypeSafeCollections.newArrayList();
        for (final String suffix : SUFFIXE) {
            try {
                final URL fileURL = new URL(fileWithoutSuffix + suffix);

                final File testFile = new File(fileURL.toURI());
                if (testFile.isFile()) {
                    if (log.isDebugEnabled()) {
                        log.debug("Found picture in file: " + testFile.getAbsolutePath());
                    }
                    results.add(testFile);
                }
            } catch (Exception ex) {
                log.error(ex, ex);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("No picture file found.");
        }
        return results;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fileWithoutSuffix  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<URL> probeWebserverForRightSuffix(final String fileWithoutSuffix) {
        if (log.isDebugEnabled()) {
            log.debug("Searching for picture: " + fileWithoutSuffix + "xxx");
        }
        final List<URL> results = new ArrayList<URL>();

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
                log.error(ex, ex);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("No picture file found.");
        }
        return results;
    }
}
