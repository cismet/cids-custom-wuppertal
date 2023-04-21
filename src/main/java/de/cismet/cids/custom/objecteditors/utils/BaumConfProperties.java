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
public class BaumConfProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaumConfProperties.class);

    //~ Instance fields --------------------------------------------------------

    private final Double bufferMeter;
    private final String beschrPattern;
    private final String azPattern;
    private final String urlErsatzbaum;
    private final String urlFestsetzung;
    private final String urlSchaden;
    private final String urlDefault;
    private final String urlRasterfari;
    private final String ordnerDokumente;
    private final String ordnerFotos;
    private final String ordnerThema;
    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private BaumConfProperties(final Properties properties) {
        bufferMeter = Double.valueOf(readProperty(properties, "BUFFER_METER", null));
        beschrPattern = String.valueOf(readProperty(properties, "BESCHR__PATTERN", null));
        azPattern = String.valueOf(readProperty(properties, "AZ__PATTERN", null));
        urlErsatzbaum = String.valueOf(readProperty(properties, "MAP_CALL_STRING_ERSATZBAUM", null));
        urlFestsetzung = String.valueOf(readProperty(properties, "MAP_CALL_STRING_FESTSETZUNG", null));
        urlSchaden = String.valueOf(readProperty(properties, "MAP_CALL_STRING_SCHADEN", null));
        urlDefault = String.valueOf(readProperty(properties, "MAP_CALL_STRING_DEFAULT", null));
        urlRasterfari = String.valueOf(readProperty(properties, "RASTERFARI_URL", null));
        ordnerDokumente = String.valueOf(readProperty(properties, "ORDNER_DOKUMENTE", null));
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
                        + WundaBlauServerResources.BAUM_CONF_PROPERTIES.getValue()
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
    public static BaumConfProperties getInstance() {
        return BaumConfProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final BaumConfProperties INSTANCE;

        static {
            BaumConfProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(
                                user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.BAUM_CONF_PROPERTIES.getValue(),
                                ConnectionContext.create(Category.STATIC, BaumConfProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new BaumConfProperties(properties);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
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
