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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import de.aedsicad.aaaweb.service.util.Buchungsblatt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils;
import de.cismet.cids.custom.utils.alkis.AlkisProducts;
import de.cismet.cids.custom.utils.alkis.BaulastBescheinigungHelper;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ClientBaulastBescheinigungHelper extends BaulastBescheinigungHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Map<CidsBean, Buchungsblatt> BUCHUNGSBLATT_CACHE = new HashMap<CidsBean, Buchungsblatt>();

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

    @Override
    protected MetaClass getMetaClass(final String name) {
        return ClassCacheMultiple.getMetaClass(
                "WUNDA_BLAU",
                name,
                getConnectionContext());
    }

    @Override
    protected Buchungsblatt getBuchungsblatt(final CidsBean buchungsblattBean) throws Exception {
        Buchungsblatt buchungsblatt = null;

        if (buchungsblattBean != null) {
            buchungsblatt = BUCHUNGSBLATT_CACHE.get(buchungsblattBean);
            if (buchungsblatt == null) {
                final String buchungsblattcode = String.valueOf(buchungsblattBean.getProperty("buchungsblattcode"));
                if ((buchungsblattcode != null) && (buchungsblattcode.length() > 5)) {
                    buchungsblatt = AlkisUtils.getInstance()
                                .getBuchungsblattFromAlkisSOAPServerAction(AlkisProducts.fixBuchungslattCode(
                                            buchungsblattcode),
                                        getConnectionContext());
                    BUCHUNGSBLATT_CACHE.put(buchungsblattBean, buchungsblatt);
                }
            }
        }

        return buchungsblatt;
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
