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

import de.cismet.cids.custom.wunda_blau.search.actions.VermessungsrissPictureServerAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungsrissPictureFinderClientUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DOMAIN = "WUNDA_BLAU";

    private static final Logger LOG = Logger.getLogger(VermessungsrissPictureFinderClientUtils.class);
    private static final ConnectionContext CONNECTION_CONTEXT = ConnectionContext.create(
            AbstractConnectionContext.Category.STATIC,
            VermessungsrissPictureFinderClientUtils.class.getSimpleName());

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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.FIND_VERMESSUNGSRISS,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BLATT.toString(),
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.FIND_GRENZNIEDERSCHRIFT,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BLATT.toString(),
                                    blatt));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schluessel     DOCUMENT ME!
     * @param   gemarkung      DOCUMENT ME!
     * @param   gemarkungName  DOCUMENT ME!
     * @param   steuerbezirk   DOCUMENT ME!
     * @param   bezeichner     DOCUMENT ME!
     * @param   historisch     DOCUMENT ME!
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.FIND_BUCHWERK,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.STEUERBEZIRK.toString(),
                                    steuerbezirk),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BEZEICHNER.toString(),
                                    bezeichner),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.HISTORISCH.toString(),
                                    historisch));
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.GET_VERMESSUNGSRISS_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BLATT.toString(),
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.GET_GRENZNIEDERSCHRIFT_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    riss),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.FLUR.toString(),
                                    flur),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BLATT.toString(),
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.GET_BUCHWERK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.SCHLUESSEL.toString(),
                                    schluessel),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.GEMARKUNG.toString(),
                                    gemarkung),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.STEUERBEZIRK.toString(),
                                    steuerbezirk),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.BEZEICHNER.toString(),
                                    bezeichner),
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.HISTORISCH.toString(),
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.GET_VERMESSUNGSRISS_LINK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.LINK.toString(),
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
                                VermessungsrissPictureServerAction.TASK_NAME,
                                DOMAIN,
                                VermessungsrissPictureServerAction.Body.GET_GRENZNIEDERSCHRIFT_LINK_FILENAME,
                                CONNECTION_CONTEXT,
                                new ServerActionParameter<>(
                                    VermessungsrissPictureServerAction.Param.LINK.toString(),
                                    link));
        } catch (final Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VermessungsrissPictureFinderClientUtils getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final VermessungsrissPictureFinderClientUtils INSTANCE =
            new VermessungsrissPictureFinderClientUtils();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
