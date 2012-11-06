/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class Alb_BaulastChecker extends AbstractCidsServerSearch {

    //~ Instance fields --------------------------------------------------------

    private String searchQuery;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_BaulastChecker object.
     *
     * @param  blattnummer  DOCUMENT ME!
     * @param  lastnummer   DOCUMENT ME!
     * @param  id           DOCUMENT ME!
     */
    public Alb_BaulastChecker(String blattnummer, String lastnummer, final int id) {
        blattnummer = blattnummer.replaceAll("'", "");
        lastnummer = lastnummer.replaceAll("'", "");
        this.searchQuery = "select count(*) from alb_baulast where blattnummer = '" + blattnummer
                    + "' and laufende_nummer = '" + lastnummer + "' and id <> " + id;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final MetaService ms = (MetaService)getActiveLocalServers().get("WUNDA_BLAU");
        if (ms != null) {
            try {
                final ArrayList<ArrayList> lists = ms.performCustomSearch(searchQuery);
                return lists;
            } catch (RemoteException ex) {
            }
        }
        //
        return null;
    }

    @Override
    public String toString() {
        return searchQuery;
    }
}
