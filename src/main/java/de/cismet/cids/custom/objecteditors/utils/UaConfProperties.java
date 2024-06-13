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
public class UaConfProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UaConfProperties.class);

    //~ Instance fields --------------------------------------------------------

    private final Double  bufferMeter;
    private final String  url;
    private final String  urlRasterfari;
    private final String  filesDokumente;
    private final String  filesFotos;
    private final String  kompFotos;
    private final String  showFotos;
    private final Integer fileLimit;
    private final String  ordnerThema;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new UaProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private UaConfProperties(final Properties properties) {
        bufferMeter = Double.valueOf(readProperty(properties, "BUFFER_METER", null));
        url = String.valueOf(readProperty(properties, "MAP_CALL_STRING", null));
        urlRasterfari = String.valueOf(readProperty(properties, "RASTERFARI_URL", null));
        filesDokumente = String.valueOf(readProperty(properties, "FILES_DOKUMENTE", null));
        filesFotos = String.valueOf(readProperty(properties, "FILES_FOTOS", null));
        ordnerThema = String.valueOf(readProperty(properties, "ORDNER_THEMA", null));
        kompFotos = String.valueOf(readProperty(properties, "KOMP_FOTOS", null));
        showFotos = String.valueOf(readProperty(properties, "SHOW_FOTOS", null));
        fileLimit = Integer.valueOf(readProperty(properties, "FILE_LIMIT", null));
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
                        + WundaBlauServerResources.UA_CONF_PROPERTIES.getValue()
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
    public static UaConfProperties getInstance() {
        return UaConfProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final UaConfProperties INSTANCE;

        static {
            UaConfProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.UA_CONF_PROPERTIES.getValue(),
                                ConnectionContext.create(Category.STATIC, UaConfProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new UaConfProperties(properties);
            } catch (final Exception ex) {
                LOG.error("Fehler beim Laden der Properties für Ua-Client", ex);
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
