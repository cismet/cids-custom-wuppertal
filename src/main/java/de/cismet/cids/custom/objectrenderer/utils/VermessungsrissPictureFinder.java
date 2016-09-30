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

import java.net.URL;

import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Deprecated
public class VermessungsrissPictureFinder {

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
    @Deprecated
    public static List<URL> findVermessungsrissPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .findVermessungsrissPicture(riss, gemarkung, flur, blatt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   riss              DOCUMENT ME!
     * @param   gemarkung         DOCUMENT ME!
     * @param   flur              DOCUMENT ME!
     * @param   blatt             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static List<URL> findVermessungsrissPicture(final boolean checkReducedSize,
            final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .findVermessungsrissPicture(checkReducedSize, riss, gemarkung, flur, blatt);
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
    @Deprecated
    public static List<URL> findGrenzniederschriftPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .findGrenzniederschriftPicture(riss, gemarkung, flur, blatt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   checkReducedSize  DOCUMENT ME!
     * @param   riss              DOCUMENT ME!
     * @param   gemarkung         DOCUMENT ME!
     * @param   flur              DOCUMENT ME!
     * @param   blatt             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static List<URL> findGrenzniederschriftPicture(final boolean checkReducedSize,
            final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .findGrenzniederschriftPicture(checkReducedSize, riss, gemarkung, flur, blatt);
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
    @Deprecated
    public static String getGrenzniederschriftFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .getGrenzniederschriftFilename(riss, gemarkung, flur, blatt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   withPath              DOCUMENT ME!
     * @param   isGrenzniederschrift  blattnummer DOCUMENT ME!
     * @param   riss                  laufendeNummer DOCUMENT ME!
     * @param   gemarkung             DOCUMENT ME!
     * @param   flur                  DOCUMENT ME!
     * @param   blatt                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static String getObjectFilename(final boolean withPath,
            final boolean isGrenzniederschrift,
            final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .getObjectFilename(withPath, isGrenzniederschrift, riss, gemarkung, flur, blatt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   isGrenzNiederschrift  DOCUMENT ME!
     * @param   filename              DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static String getObjectPath(final boolean isGrenzNiederschrift, final String filename) {
        return VermessungsrissWebAccessPictureFinder.getInstance().getObjectPath(isGrenzNiederschrift, filename);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   isGrenzNiederschrift  DOCUMENT ME!
     * @param   documentFileName      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static String getLinkFromLinkDocument(final boolean isGrenzNiederschrift, final String documentFileName) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .getLinkFromLinkDocument(isGrenzNiederschrift, documentFileName);
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
    @Deprecated
    public static String getVermessungsrissPictureFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .getVermessungsrissPictureFilename(riss, gemarkung, flur, blatt);
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
    @Deprecated
    public static List<URL> probeWebserverForRightSuffix(final boolean checkReducedSize,
            final String fileWithoutSuffix,
            final int recursionDepth) {
        return VermessungsrissWebAccessPictureFinder.getInstance()
                    .probeWebserverForRightSuffix(checkReducedSize, fileWithoutSuffix, recursionDepth);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   isGrenzniederschrift  DOCUMENT ME!
     * @param   gemarkung             DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Deprecated
    public static String getFolder(final boolean isGrenzniederschrift, final Integer gemarkung) {
        return VermessungsrissWebAccessPictureFinder.getInstance().getFolder(isGrenzniederschrift, gemarkung);
    }
}
