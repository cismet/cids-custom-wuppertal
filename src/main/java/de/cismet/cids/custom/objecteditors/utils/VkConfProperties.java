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
public class VkConfProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VkConfProperties.class);

    //~ Instance fields --------------------------------------------------------

    private final Double bufferMeter;
    private final String url;
    private final String pathDokumente;
    private final String pathFotos;
    private final String neuVorhaben;
    private final String mailBB;
    private final String mailNeu;
    private final String hilfeDokumente;
    private final String hilfeDokumenteUrl;
    private final String hilfeDokumenteEndung;
    private final String hilfeFotos;
    private final String hilfeFotosUrl;
    private final String hilfeFotosEndung;
    private final String hilfeStek;
    private final String hilfeLink;
    private final String hilfeBeschluss;
    private final String hilfeAnhang;
    private final String hilfeOrt;
    private final String hilfeKontakt;
    private final String hinweisMailversand;
    private final String textAbgeschlossenJa;
    private final String textAbgeschlossenNein;
    private final String textAbgeschlossenNicht;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VkProperties object.
     *
     * @param  properties  DOCUMENT ME!
     */
    private VkConfProperties(final Properties properties) {
        bufferMeter = Double.valueOf(readProperty(properties, "BUFFER_METER", null));
        url = String.valueOf(readProperty(properties, "MAP_CALL_STRING", null));
        pathDokumente = String.valueOf(readProperty(properties, "DOK_PATH", null));
        pathFotos = String.valueOf(readProperty(properties, "FOTOS_PATH", null));
        neuVorhaben = String.valueOf(readProperty(properties, "NEU_VORHABEN", null));
        mailBB = String.valueOf(readProperty(properties, "MAIL_BB", null));
        mailNeu = String.valueOf(readProperty(properties, "MAIL_NEU", null));
        hilfeDokumente = String.valueOf(readProperty(properties, "HILFE_DOKUMENTE", null));
        hilfeDokumenteUrl = String.valueOf(readProperty(properties, "HILFE_DOKUMENTE_URL", null));
        hilfeDokumenteEndung = String.valueOf(readProperty(properties, "HILFE_DOKUMENTE_ENDUNG", null));
        hilfeFotos = String.valueOf(readProperty(properties, "HILFE_FOTOS", null));
        hilfeFotosUrl = String.valueOf(readProperty(properties, "HILFE_FOTOS_URL", null));
        hilfeFotosEndung = String.valueOf(readProperty(properties, "HILFE_FOTOS_ENDUNG", null));
        hilfeStek = String.valueOf(readProperty(properties, "HILFE_STEK", null));
        hilfeLink = String.valueOf(readProperty(properties, "HILFE_LINK", null));
        hilfeBeschluss = String.valueOf(readProperty(properties, "HILFE_BESCHLUSS", null));
        hilfeAnhang = String.valueOf(readProperty(properties, "HILFE_ANHANG", null));
        hilfeOrt = String.valueOf(readProperty(properties, "HILFE_ORT", null));
        hilfeKontakt = String.valueOf(readProperty(properties, "HILFE_KONTAKT", null));
        hinweisMailversand = String.valueOf(readProperty(properties, "HINWEIS_MAILVERSAND", null));
        textAbgeschlossenJa = String.valueOf(readProperty(properties, "TEXT_ABGESCHLOSSEN_JA", null));
        textAbgeschlossenNein = String.valueOf(readProperty(properties, "TEXT_ABGESCHLOSSEN_NEIN", null));
        textAbgeschlossenNicht = String.valueOf(readProperty(properties, "TEXT_ABGESCHLOSSEN_NICHT", null));
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
                        + WundaBlauServerResources.VK_CONF_PROPERTIES.getValue()
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
    public static VkConfProperties getInstance() {
        return VkConfProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final VkConfProperties INSTANCE;

        static {
            VkConfProperties instance = null;

            try {
                final User user = SessionManager.getSession().getUser();
                final Object ret = SessionManager.getSession()
                            .getConnection()
                            .executeTask(user,
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.VK_CONF_PROPERTIES.getValue(),
                                ConnectionContext.create(Category.STATIC, VkConfProperties.class.getSimpleName()));
                if (ret instanceof Exception) {
                    throw (Exception)ret;
                }
                final Properties properties = new Properties();
                properties.load(new StringReader((String)ret));
                instance = new VkConfProperties(properties);
            } catch (final Exception ex) {
                LOG.error("Fehler beim Laden der Properties für Vk-Client", ex);
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
