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

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import de.cismet.cids.custom.wunda_blau.search.actions.VermessungPictureServerAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungPictureFinderClientUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DOMAIN = "WUNDA_BLAU";

    private static final Logger LOG = Logger.getLogger(VermessungPictureFinderClientUtils.class);
    private static final ConnectionContext CONNECTION_CONTEXT = ConnectionContext.create(
            AbstractConnectionContext.Category.STATIC,
            VermessungPictureFinderClientUtils.class.getSimpleName());

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   riss       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findVermessungsrissPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_VERMESSUNGSRISS,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    blatt));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findGrenzniederschriftPicture(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_GRENZNIEDERSCHRIFT,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    blatt));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel    DOCUMENT ME!
     * @param   gemarkung     DOCUMENT ME!
     * @param   steuerbezirk  DOCUMENT ME!
     * @param   bezeichner    DOCUMENT ME!
     * @param   historisch    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findBuchwerkPicture(final String schluessel,
            final Integer gemarkung,
            final Integer steuerbezirk,
            final String bezeichner,
            final boolean historisch) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_BUCHWERK,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.STEUERBEZIRK.toString(),
                                    steuerbezirk),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BEZEICHNER.toString(),
                                    bezeichner),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.HISTORISCH.toString(),
                                    historisch));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     * @param   gemarkung   DOCUMENT ME!
     * @param   flur        steuerbezirk DOCUMENT ME!
     * @param   blatt       bezeichner DOCUMENT ME!
     * @param   version     historisch DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findInselkartenPicture(final String schluessel,
            final Integer gemarkung,
            final Integer flur,
            final Integer blatt,
            final String version) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_INSELKARTE,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    Integer.toString(flur)),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    Integer.toString(blatt)),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.VERSION.toString(),
                                    version));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkungOrKmquadrat  DOCUMENT ME!
     * @param   liste                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findGewannePicture(final Integer gemarkungOrKmquadrat,
            final boolean liste) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_GEWANNE,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    liste ? VermessungPictureServerAction.Param.GEMARKUNG.toString()
                                          : VermessungPictureServerAction.Param.KMQUADRAT.toString(),
                                    gemarkungOrKmquadrat));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ordner  DOCUMENT ME!
     * @param   nummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String findGebaeudebeschreibungPicture(final String ordner, final Integer nummer) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.FIND_GEBAEUDEBESCHREIBUNG,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.ORDNER.toString(),
                                    ordner),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.NUMMER.toString(),
                                    nummer));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel  DOCUMENT ME!
     * @param   gemarkung   DOCUMENT ME!
     * @param   flur        steuerbezirk DOCUMENT ME!
     * @param   blatt       bezeichner DOCUMENT ME!
     * @param   version     historisch DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getInselkartenPictureFilename(final String schluessel,
            final Integer gemarkung,
            final Integer flur,
            final Integer blatt,
            final String version) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_INSELKARTE_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    Integer.toString(flur)),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    Integer.toString(blatt)),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.VERSION.toString(),
                                    version));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   gemarkungOrKmquadrat  DOCUMENT ME!
     * @param   liste                 DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getGewannePictureFilename(final Integer gemarkungOrKmquadrat,
            final boolean liste) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_GEWANNE_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    liste ? VermessungPictureServerAction.Param.GEMARKUNG.toString()
                                          : VermessungPictureServerAction.Param.KMQUADRAT.toString(),
                                    gemarkungOrKmquadrat));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ordner  DOCUMENT ME!
     * @param   nummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getGebauedebschreibungPictureFilename(final String ordner, final Integer nummer) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_GEBAEUDEBESCHREIBUNG_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.ORDNER.toString(),
                                    ordner),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.NUMMER.toString(),
                                    nummer));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getVermessungsrissPictureFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_VERMESSUNGSRISS_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    blatt));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   riss       DOCUMENT ME!
     * @param   gemarkung  DOCUMENT ME!
     * @param   flur       DOCUMENT ME!
     * @param   blatt      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getGrenzniederschriftPictureFilename(final String riss,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_GRENZNIEDERSCHRIFT_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BLATT.toString(),
                                    blatt));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel    DOCUMENT ME!
     * @param   gemarkung     DOCUMENT ME!
     * @param   steuerbezirk  DOCUMENT ME!
     * @param   bezeichner    DOCUMENT ME!
     * @param   historisch    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getBuchwerkPictureFilename(final String schluessel,
            final Integer gemarkung,
            final Integer steuerbezirk,
            final String bezeichner,
            final boolean historisch) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_BUCHWERK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.STEUERBEZIRK.toString(),
                                    steuerbezirk),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.BEZEICHNER.toString(),
                                    bezeichner),
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.HISTORISCH.toString(),
                                    historisch));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getVermessungsrissLinkFilename(final String link) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_VERMESSUNGSRISS_LINK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.LINK.toString(),
                                    link));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   link  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getGrenzniederschriftLinkFilename(final String link) {
        try {
            return (String)SessionManager.getProxy()
                        .executeTask(
                                VermessungPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungPictureServerAction.Body.GET_GRENZNIEDERSCHRIFT_LINK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungPictureServerAction.Param.LINK.toString(),
                                    link));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }
}
