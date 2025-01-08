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
package de.cismet.cids.custom.clientutils;

import org.apache.log4j.Logger;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.wunda_blau.search.actions.AlkisRestAction;

import de.cismet.cids.utils.serverresources.PropertiesServerResource;
import de.cismet.cids.utils.serverresources.TextServerResource;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class AlkisClientUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisClientUtils.class);

    private static boolean useNewVersion = false;

    static {
        try {
            final String alkisResString = ServerResourcesLoaderClient.getInstance()
                        .loadText((TextServerResource)WundaBlauServerResources.ALKIS_REST_CONF.getValue());
//            final String alkisResString = ServerResourcesLoaderClient.getInstance()
//                        .get((TextServerResource)WundaBlauServerResources.ALKIS_REST_CONF.getValue(), true);

            final Properties props = new Properties();
            props.load(new StringReader(alkisResString));
            useNewVersion = (props.getProperty("NEW_REST_SERVICE_USED") != null)
                        && props.getProperty("NEW_REST_SERVICE_USED").equalsIgnoreCase("true");
        } catch (Exception e) {
            LOG.error("Cannot read alkis_rest_conf properties", e);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   buchungsblattCode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String fixBuchungslattCode(final String buchungsblattCode) {
        return AlkisProducts.fixBuchungslattCode(buchungsblattCode, useNewVersion);
    }
}
