package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.search.CidsServerSearch;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author stefan
 */
public class Alb_BaulastChecker extends CidsServerSearch {

    private String searchQuery;

    public Alb_BaulastChecker(String blattnummer, String lastnummer, int id) {
        blattnummer = blattnummer.replaceAll("'", "");
        lastnummer = lastnummer.replaceAll("'", "");
        this.searchQuery = "select count(*) from alb_baulast where blattnummer = '" + blattnummer + "' and laufende_nummer = '" + lastnummer + "' and id <> " + id;
    }

    @Override
    public Collection performServerSearch() {
        MetaService ms = (MetaService) getActiveLoaclServers().get("WUNDA_BLAU");
        if (ms != null) {
            try {
                ArrayList<ArrayList> lists = ms.performCustomSearch(searchQuery);
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
