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
package de.cismet.cids.custom.objectrenderer.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungsrissPictureFinder {

    //~ Static fields/initializers ---------------------------------------------

    public static final String SEP = "/";
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(VermessungsrissPictureFinder.class);
    private static final String[] SUFFIXE = new String[] {
            ".tif",
            ".jpg",
            ".jpe",
            ".tiff",
            ".jpeg",
            ".TIF",
            ".JPG",
            ".JPE",
            ".TIFF",
            ".JPEG"
        };
    private static final String SUFFIX_REDUCED_SIZE = "_rs";
    private static final String LINKEXTENSION = ".txt";
    public static String PATH_VERMESSUNG;         // = AlkisConstants.COMMONS.VERMESSUNG_HOST_BILDER; //
                                                  // "http://s102x003/Vermessungsrisse/";
    public static String PATH_GRENZNIEDERSCHRIFT; // = AlkisConstants.COMMONS.VERMESSUNG_HOST_GRENZNIEDERSCHRIFTEN;
                                                  // // "http://s102x003/Vermessungsrisse/";
    private static final String GRENZNIEDERSCHRIFT_PREFIX = "GN";
    private static final String VERMESSUNGSRISS_PREFIX = "VR";

    static {
        PATH_VERMESSUNG = "http://localhost:8000/Vermessungsrisse/";
        PATH_GRENZNIEDERSCHRIFT = "http://localhost:8000/Grenzniederschriften/";
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   riss       blattnummer picture DOCUMENT ME!
     * @param   gemarkung  laufendeNummer DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findVermessungsrissPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final String picturePath = getVermessungsrissPictureFilename(riss, gemarkung, flur, blatt);
        if (log.isDebugEnabled()) {
            log.debug("findVermessungrissPicture: " + picturePath);
        }

        return probeWebserverForRightSuffix(true, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       blattnummer picture DOCUMENT ME!
     * @param   gemarkung  laufendeNummer DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static List<URL> findGrenzniederschriftPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final String picturePath = getGrenzniederschriftFilename(riss, gemarkung, flur, blatt);
        if (log.isDebugEnabled()) {
            log.debug("findGrenzniederschriftPicture: " + picturePath);
        }
        return probeWebserverForRightSuffix(false, picturePath);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       blattnummer DOCUMENT ME!
     * @param   gemarkung  laufendeNummer DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getGrenzniederschriftFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final String ret = getObjectFilename(true, riss, gemarkung, flur, blatt);

        return (ret != null) ? ret : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   isGrenzniederschrift  blattnummer DOCUMENT ME!
     * @param   riss                  laufendeNummer DOCUMENT ME!
     * @param   gemarkung             DOCUMENT ME!
     * @param   flur                  DOCUMENT ME!
     * @param   blatt                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getObjectFilename(final boolean isGrenzniederschrift,
            final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final StringBuffer buf = new StringBuffer();
        if (isGrenzniederschrift) {
            buf.append(GRENZNIEDERSCHRIFT_PREFIX);
        } else {
            buf.append(VERMESSUNGSRISS_PREFIX);
        }
        buf.append("_");
        buf.append(StringUtils.leftPad(riss, 3, '0'));
        buf.append("-");
        buf.append(String.format("%04d", gemarkung));
        buf.append("-");
        buf.append(StringUtils.leftPad(flur, 3, '0'));
        buf.append("-");
        buf.append(StringUtils.leftPad(blatt, 8, '0'));
        return new StringBuffer(getFolder(isGrenzniederschrift, gemarkung)).append(SEP)
                    .append(buf.toString())
                    .toString();
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
     * @param   isGrenzNiederschrift  DOCUMENT ME!
     * @param   filename              DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getObjectPath(final boolean isGrenzNiederschrift, final String filename) {
        final Integer gemarkung;
        final String[] splittedFilename = filename.split("-");
        gemarkung = Integer.parseInt(splittedFilename[1]);
        String filenameWithPrefix = isGrenzNiederschrift ? GRENZNIEDERSCHRIFT_PREFIX : VERMESSUNGSRISS_PREFIX;
        filenameWithPrefix += "_" + filename;
        return new StringBuffer(getFolder(isGrenzNiederschrift, gemarkung)).append(SEP)
                    .append(filenameWithPrefix)
                    .toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       blattnummer DOCUMENT ME!
     * @param   gemarkung  laufendeNummer DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getVermessungsrissPictureFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final String ret = getObjectFilename(false, riss, gemarkung, flur, blatt);

        return (ret != null) ? ret : null;
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
        // check if there is a reduced size image direcly...
        final String searchName = checkReducedSize ? (fileWithoutSuffix + SUFFIX_REDUCED_SIZE) : fileWithoutSuffix;
        for (final String suffix : SUFFIXE) {
            try {
                final URL objectURL = new URL(searchName + suffix);

                final HttpURLConnection huc = (HttpURLConnection)objectURL.openConnection();
                huc.setRequestMethod("GET");
                huc.connect();
                final int reponse = huc.getResponseCode();
                if (reponse == 200) {
                    results.add(objectURL);
                }
            } catch (Exception ex) {
                log.error("Problem occured, during checking for " + searchName + suffix, ex);
            }
        }
        // we need to do an extra round if we checked with _rs suffix...
        if (results.isEmpty() && checkReducedSize) {
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
                    log.error("Problem occured, during checking for " + searchName + suffix, ex);
                }
            }
        }
        // if the results is still empty check if there is a link...
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
                        final boolean isGrenzNiederschrift = fileWithoutSuffix.contains(GRENZNIEDERSCHRIFT_PREFIX);
                        return probeWebserverForRightSuffix(
                                checkReducedSize,
                                getObjectPath(isGrenzNiederschrift, link.trim()),
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

    /**
     * DOCUMENT ME!
     *
     * @param   isGrenzniederschrift  DOCUMENT ME!
     * @param   gemarkung             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getFolder(final boolean isGrenzniederschrift, final Integer gemarkung) {
        final StringBuffer buf;
        if (isGrenzniederschrift) {
            buf = new StringBuffer(PATH_GRENZNIEDERSCHRIFT);
        } else {
            buf = new StringBuffer(PATH_VERMESSUNG);
        }
        return buf.append(String.format("%04d", gemarkung)).toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  riss       DOCUMENT ME!
     * @param  gemarkung  DOCUMENT ME!
     * @param  flur       DOCUMENT ME!
     * @param  blatt      DOCUMENT ME!
     */
    public static void testVermessungsrissPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final List<URL> url;
        System.out.println("Search Picture for " + riss + "-" + gemarkung + "-" + flur + "-" + blatt);
        url = VermessungsrissPictureFinder.findVermessungsrissPicture(riss, gemarkung, flur, blatt);
        if (url.isEmpty()) {
            System.out.println("Could not find a URL");
            return;
        }
        System.out.println("Found the following valid URL" + url.get(0).toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  riss       DOCUMENT ME!
     * @param  gemarkung  DOCUMENT ME!
     * @param  flur       DOCUMENT ME!
     * @param  blatt      DOCUMENT ME!
     */
    public static void testGrenzniderschriftPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final List<URL> url;
        System.out.println("Search Picture for " + riss + "-" + gemarkung + "-" + flur + "-" + blatt);
        url = VermessungsrissPictureFinder.findGrenzniederschriftPicture(riss, gemarkung, flur, blatt);
        if (url.isEmpty()) {
            System.out.println("Could not find a URL");
            return;
        }
        System.out.println("Found the following valid URL" + url.get(0).toString());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println("Tests Vermessungsriss Picture");
        System.out.println("#########################################\n");
        // normal file..
        VermessungsrissPictureFinder.testVermessungsrissPicture("505", 3001, "001", "1");
        // reduced size
        VermessungsrissPictureFinder.testVermessungsrissPicture("505", 0, "0", "878681");
        // link in same folder
        VermessungsrissPictureFinder.testVermessungsrissPicture("504", 3301, "000", "1");
        // link in other folder
        VermessungsrissPictureFinder.testVermessungsrissPicture("504", 3301, "000", "3");

        System.out.println("#########################################\n\n");
        System.out.println("Tests Grenzniederschrift Picture");
        System.out.println("#########################################\n");

        //normal file..
        VermessungsrissPictureFinder.testGrenzniderschriftPicture("505", 3001, "004", "17");
        //link in same folder
        VermessungsrissPictureFinder.testGrenzniderschriftPicture("505", 3001, "111", "1");
        //link in other folder
        VermessungsrissPictureFinder.testGrenzniderschriftPicture("505", 3135, "002", "25");
    }
}
