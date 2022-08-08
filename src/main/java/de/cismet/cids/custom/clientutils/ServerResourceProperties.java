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

import Sirius.navigator.connection.SessionManager;

import java.io.StringReader;

import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ServerResourceProperties extends Properties {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ServerResourceProperties.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ServerResourceProperties object.
     *
     * @param   serverResource  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected ServerResourceProperties(final WundaBlauServerResources serverResource) throws Exception {
        super(loadPropertiesFromServerResource(serverResource));
    }

    /**
     * Creates a new ServerResourceProperties object.
     */
    private ServerResourceProperties() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   serverResource  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Properties loadPropertiesFromServerResource(final WundaBlauServerResources serverResource)
            throws Exception {
        final Object ret = SessionManager.getSession()
                    .getConnection()
                    .executeTask(
                        SessionManager.getSession().getUser(),
                        GetServerResourceServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        serverResource.getValue(),
                        ConnectionContext.create(Category.STATIC, ServerResourceProperties.class.getSimpleName()));
        if (ret instanceof Exception) {
            throw (Exception)ret;
        }
        final Properties properties = new Properties();
        properties.load(new StringReader((String)ret));
        return properties;
    }
}
