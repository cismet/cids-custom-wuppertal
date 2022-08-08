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
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.utils.berechtigungspruefung.baulastbescheinigung.BerechtigungspruefungBescheinigungBaulastInfo;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CachedInfoBaulastRetriever {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CachedInfoBaulastRetriever.class);

    //~ Instance fields --------------------------------------------------------

    private final Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> cache = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CachedInfoBaulastRetriever object.
     */
    private CachedInfoBaulastRetriever() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   info               DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean loadBaulast(final BerechtigungspruefungBescheinigungBaulastInfo info,
            final ConnectionContext connectionContext) {
        if (cache.containsKey(info)) {
            return cache.get(info);
        } else {
            final MetaClass mcBaulast = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "alb_baulast", connectionContext);

            final String query = "SELECT %d, id "
                        + "FROM alb_baulast "
                        + "WHERE blattnummer ILIKE '%s' "
                        + "AND laufende_nummer ILIKE '%s'";
            try {
                final MetaObject[] mos = SessionManager.getProxy()
                            .getMetaObjectByQuery(String.format(
                                    query,
                                    mcBaulast.getID(),
                                    info.getBlattnummer(),
                                    info.getLaufende_nummer()),
                                0,
                                connectionContext);
                cache.put(info, mos[0].getBean());
                return mos[0].getBean();
            } catch (final ConnectionException ex) {
                LOG.error(ex, ex);
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map<BerechtigungspruefungBescheinigungBaulastInfo, CidsBean> getCache() {
        return cache;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CachedInfoBaulastRetriever getInstance() {
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

        private static final CachedInfoBaulastRetriever INSTANCE = new CachedInfoBaulastRetriever();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
