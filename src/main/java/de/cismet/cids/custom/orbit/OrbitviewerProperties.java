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
package de.cismet.cids.custom.orbit;

import java.util.Properties;

import de.cismet.cids.custom.clientutils.ServerResourceProperties;
import de.cismet.cids.custom.utils.WundaBlauServerResources;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class OrbitviewerProperties extends Properties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            OrbitviewerProperties.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BerechtigungspruefungProperties object.
     *
     * @param   properties  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private OrbitviewerProperties(final Properties properties) throws Exception {
        super(properties);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getOpenChannelsSecret() {
        return getProperty("openChannelsSecret");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getOpenChannelTimeout() {
        return getProperty("openChannelTimeout");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSocketBroadcaster() {
        return getProperty("socketBroadcaster");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLauncherUrl() {
        return getProperty("launcherUrl");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getArcColorRGBA() {
        return getProperty("arcColorRGBA");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static OrbitviewerProperties getInstance() {
        return OrbitviewerProperties.LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final OrbitviewerProperties INSTANCE;

        static {
            OrbitviewerProperties instance = null;
            try {
                instance = new OrbitviewerProperties(ServerResourceProperties.loadPropertiesFromServerResource(
                            WundaBlauServerResources.ORBIT_SETTINGS_PROPERTIES));
            } catch (final Exception ex) {
                LOG.error("Error while initializing OrbitviewerProperties", ex);
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
