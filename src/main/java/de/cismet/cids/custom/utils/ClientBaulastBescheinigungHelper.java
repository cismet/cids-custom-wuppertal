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
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import java.util.Collection;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ClientBaulastBescheinigungHelper extends BaulastBescheinigungHelper {

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Collection executeSearch(final CidsServerSearch serverSearch,
            final User user,
            final ConnectionContext connectionContext) throws Exception {
        return SessionManager.getProxy().customServerSearch(serverSearch, connectionContext);
    }

    @Override
    protected MetaObject getMetaObject(final int oid, final int cid, final User user, final ConnectionContext cc)
            throws Exception {
        return SessionManager.getProxy().getMetaObject(oid, cid, "WUNDA_BLAU", cc);
    }

    @Override
    protected MetaObject[] getMetaObjects(final String query,
            final User user,
            final ConnectionContext connectionContext) throws Exception {
        return SessionManager.getProxy().getMetaObjectByQuery(query, 0, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ClientBaulastBescheinigungHelper getInstance() {
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

        private static final ClientBaulastBescheinigungHelper INSTANCE = new ClientBaulastBescheinigungHelper();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
