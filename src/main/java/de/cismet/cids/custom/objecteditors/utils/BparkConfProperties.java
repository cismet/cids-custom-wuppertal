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
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.newuser.User;

import lombok.Getter;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
@Getter
public class BparkConfProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BparkConfProperties.class);

    //~ Instance fields --------------------------------------------------------

    private final Double bufferMeter;
    private final String zonePattern;
    private final String nummerPattern;
    private final String urlMap;
    private final String urlRasterfari;
    private final String ordnerFotos;
    private final String ordnerThema;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BparkProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private BparkConfProperties(final Properties properties) {
        bufferMeter = Double.valueOf(readProperty(properties, "BUFFER_METER", null));
        zonePattern = String.valueOf(readProperty(properties, "ZONE_PATTERN", null));
        nummerPattern = String.valueOf(readProperty(properties, "NUMMER_PATTERN", null));
        urlMap = String.valueOf(readProperty(properties, "MAP_CALL_STRING", null));
        urlRasterfari = String.valueOf(readProperty(properties, "RASTERFARI_URL", null));
        ordnerFotos = String.valueOf(readProperty(properties, "ORDNER_FOTOS", null));
        ordnerThema = String.valueOf(readProperty(properties, "ORDNER_THEMA", null));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   properties    DOCUMENT ME!
     * @param   property      DOCUMENT ME!
     * @param   defaultValue  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String readProperty(final Properties properties, final String property, final String defaultValue) {
        String value = defaultValue;
        try {
            value = properties.getProperty(property, defaultValue);
        } catch (final Exception ex) {
            final String message = "could not read " + property + " from "
                        + WundaBlauServerResources.BPARK_CONF_PROPERTIES.getValue()
                        + ". setting to default value: " + defaultValue;
            LOG.warn(message, ex);
        }
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BparkConfProperties getInstance() {
        return BparkConfProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final BparkConfProperties INSTANCE;

        static {
            BparkConfProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.BPARK_CONF_PROPERTIES.getValue(),
                                ConnectionContext.create(Category.STATIC, BparkConfProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new BparkConfProperties(properties);
            } catch (final Exception ex) {
                LOG.error("Fehler beim Laden der Properties für Bpark-Client", ex);
            }

            INSTANCE = instance;
        }

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
