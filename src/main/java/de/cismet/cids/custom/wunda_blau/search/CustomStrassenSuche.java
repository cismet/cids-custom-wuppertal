/*
 *  Copyright (C) 2010 thorsten
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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.permission.Policy;
import Sirius.server.search.CidsServerSearch;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author thorsten
 */
public class CustomStrassenSuche extends CidsServerSearch {
   
    private String searchString;

    public CustomStrassenSuche(String searchString) {
        this.searchString = searchString;
    }
    
    @Override
    public Collection performServerSearch() {
        try {
            getLog().fatal("search started");


            MetaService ms = (MetaService) getActiveLoaclServers().get("WUNDA_BLAU");

            MetaClass c = ms.getClassByTableName(getUser(), "strasse");


            String sql = "select strassenschluessel,name from strasse where name like '%" + searchString + "%'";

            ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            ArrayList<Node> aln = new ArrayList<Node>();
            for (ArrayList al : result) {

                int id = (Integer) al.get(0);
                MetaObjectNode mon = new MetaObjectNode(c.getDomain(), id, c.getId());

                aln.add(mon);
            }
            // Thread.sleep(5000);
            return aln;
        } catch (Exception e) {
            getLog().fatal("Problem", e);
            return null;
        }
    }
}


