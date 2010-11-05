/*
 *  Copyright (C) 2010 stefan
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
public class Alb_BaulastblattChecker extends CidsServerSearch {

    private String searchQuery;

    public Alb_BaulastblattChecker(String blattnummer, int id) {
        blattnummer = blattnummer.replaceAll("'", "");
        this.searchQuery = "select count(*) from alb_baulastblatt where blattnummer = '" + blattnummer + "' and id <> " + id;
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
