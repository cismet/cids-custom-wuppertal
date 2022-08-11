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

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ClientBaulastBescheinigungHelper object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public ClientBaulastBescheinigungHelper(final ConnectionContext connectionContext) {
        super(SessionManager.getSession().getUser(), null, connectionContext);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Collection executeSearch(final CidsServerSearch serverSearch) throws Exception {
        return SessionManager.getProxy().customServerSearch(serverSearch, getConnectionContext());
    }

    @Override
    protected MetaObject getMetaObject(final int oid, final int cid) throws Exception {
        return SessionManager.getProxy().getMetaObject(oid, cid, "WUNDA_BLAU", getConnectionContext());
    }

    @Override
    protected MetaObject[] getMetaObjects(final String query) throws Exception {
        return SessionManager.getProxy().getMetaObjectByQuery(query, 0, getConnectionContext());
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
}
