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
package de.cismet.cids.custom.virtualcitymap;

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VCMProperties extends Properties {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VCMProperties.class);

    private static final String PROP_URL_TEMPLATE = "URL_TEMPLATE";
    private static final String PROP_USER = "USER";
    private static final String PROP_PASSWORD = "PASSWORD";
    private static final String PROP_TOOLBAR_CONFATTR = "TOOLBAR_CONFATTR";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VCMProperties object.
     */
    private VCMProperties() {
        load();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUrlTemplate() {
        return getProperty(PROP_URL_TEMPLATE);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUser() {
        return getProperty(PROP_USER);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPassword() {
        return getProperty(PROP_PASSWORD);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getToolbarConfAttr() {
        return getProperty(PROP_TOOLBAR_CONFATTR);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VCMProperties getInstance() {
        return VCMProperties.LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     */
    public final void load() {
        try {
            final String propertiesString = (String)SessionManager.getSession().getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.VCM_PROPERTIES.getValue());
            super.load(new StringReader(propertiesString));
        } catch (final Exception ex) {
            LOG.warn("could not load properties.", ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final VCMProperties INSTANCE = new VCMProperties();
    }
}
