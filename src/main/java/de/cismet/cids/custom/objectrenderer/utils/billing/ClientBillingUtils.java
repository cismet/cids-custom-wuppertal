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
package de.cismet.cids.custom.objectrenderer.utils.billing;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import de.cismet.cids.custom.utils.billing.BillingUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ClientBillingUtils extends BillingUtils {

    //~ Methods ----------------------------------------------------------------

    @Override
    public MetaClass getMetaClass(final String tableName, final ConnectionContext connectionContext) {
        return ClassCacheMultiple.getMetaClass("WUNDA_BLAU", tableName, connectionContext);
    }

    @Override
    public MetaObject[] getMetaObjects(final String query,
            final MetaService metaService,
            final User user,
            final ConnectionContext connectionContext) throws Exception {
        return SessionManager.getProxy().getMetaObjectByQuery(query, 0, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean getExternalUser(final ConnectionContext connectionContext) throws Exception {
        return getExternalUser(SessionManager.getSession().getUser(), connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   user               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean getExternalUser(final User user, final ConnectionContext connectionContext) throws Exception {
        return getExternalUser(user.getName(), connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   loginName          DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public CidsBean getExternalUser(final String loginName, final ConnectionContext connectionContext)
            throws Exception {
        return getExternalUser(loginName, null, SessionManager.getSession().getUser(), connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ClientBillingUtils getInstance() {
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

        private static final ClientBillingUtils INSTANCE = new ClientBillingUtils();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
