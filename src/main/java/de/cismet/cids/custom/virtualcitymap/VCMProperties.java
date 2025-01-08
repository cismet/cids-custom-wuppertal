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

import de.cismet.connectioncontext.ConnectionContext;

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
    private static final String PROP_NEW_URL_TEMPLATE = "NEW_URL_TEMPLATE";
    private static final String PROP_USER = "USER";
    private static final String PROP_PASSWORD = "PASSWORD";
    private static final String PROP_TOOLBAR_CONFATTR = "TOOLBAR_CONFATTR";
    private static final String PROP_ROTATION_INDEX = "ROTATION_INDEX";
    private static final String PROP_HEADINGS = "HEADINGS";
    private static final String PROP_SWEET_SPOTS = "SWEET_SPOTS";
    private static final String USE_NEW_URL = "USE_NEW_URL";
    private static final String USE_AUTHENTIFICATION = "USE_AUTHENTIFICATION";
    private static final String RELOAD_CONFIG_EVERY_TIME = "RELOAD_CONFIG_EVERY_TIME";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VCMProperties object.
     */
    private VCMProperties() {
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
    public String getNewUrlTemplate() {
        return getProperty(PROP_NEW_URL_TEMPLATE);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isNewUrl() {
        return (getProperty(USE_NEW_URL) != null) && getProperty(USE_NEW_URL).equalsIgnoreCase("true");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isWithAuthentication() {
        return (getProperty(USE_AUTHENTIFICATION) != null)
                    && getProperty(USE_AUTHENTIFICATION).equalsIgnoreCase("true");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReloadConfigEveryTime() {
        return (getProperty(RELOAD_CONFIG_EVERY_TIME) != null)
                    && getProperty(RELOAD_CONFIG_EVERY_TIME).equalsIgnoreCase("true");
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
    public int getRotationIndex() {
        try {
            return Integer.parseInt(getProperty(PROP_ROTATION_INDEX));
        } catch (NumberFormatException e) {
            LOG.warn("VCMProperties: Cannot parse rotation index");
            return 0;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int[] getHeadings() {
        try {
            final int[] headings = new int[4];
            final String headingsString = getProperty(PROP_HEADINGS);

            if (headingsString != null) {
                final String[] splitted = headingsString.split(",");

                if (splitted.length == 4) {
                    for (int i = 0; i < 4; ++i) {
                        headings[i] = Integer.parseInt(splitted[i].trim());
                    }

                    return headings;
                }
            }
        } catch (NumberFormatException e) {
            // nothing to do
        }

        LOG.warn("VCMProperties: Cannot parse headings. Use default values");
        return new int[] { 45, 135, 225, 315 };
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double[][] getSweetSpots() {
        try {
            final double[][] sweetSpots = new double[4][];
            final String spotsString = getProperty(PROP_SWEET_SPOTS);

            if (spotsString != null) {
                final String[] splitted = spotsString.split(";");

                if (splitted.length == 4) {
                    for (int i = 0; i < 4; ++i) {
                        final String singleSpot = splitted[i].replace("{", "").replace("}", "").trim();

                        final String[] singleSpotValues = singleSpot.split(",");

                        if (singleSpotValues.length == 2) {
                            final double[] spotValues = new double[2];
                            spotValues[0] = Double.parseDouble(singleSpotValues[0].trim());
                            spotValues[1] = Double.parseDouble(singleSpotValues[1].trim());
                            sweetSpots[i] = spotValues;
                        } else {
                            LOG.warn("VCMProperties: Cannot parse sweet spots. Use default values");
                            return new double[][] {
                                    { 1, 0 },
                                    { 1, 1 },
                                    { 0, 1 },
                                    { 0, 0 }
                                };
                        }
                    }

                    return sweetSpots;
                }
            }
        } catch (NumberFormatException e) {
            // nothing to do
        }

        LOG.warn("Cannot parse sweet spots. Use default values");
        return new double[][] {
                { 1, 0 },
                { 1, 1 },
                { 0, 1 },
                { 0, 0 }
            };
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
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public final void load(final ConnectionContext connectionContext) {
        try {
            final String propertiesString = (String)SessionManager.getSession().getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                                GetServerResourceServerAction.TASK_NAME,
                                "WUNDA_BLAU",
                                WundaBlauServerResources.VCM_PROPERTIES.getValue(),
                                connectionContext);
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
