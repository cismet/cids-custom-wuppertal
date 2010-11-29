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
import Sirius.server.search.CidsServerSearch;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author thorsten
 */
public class FullTextSearchStatement extends CidsServerSearch {

    private String searchString;

    public FullTextSearchStatement(String searchString) {
        this.searchString = searchString;
    }

    @Override
    public Collection performServerSearch() {
        try {
            String classes="6";



            String sql = ""
                    + "WITH recursive derived_index(ocid,oid,acid,aid,depth) AS "
                    + "( SELECT class_id         , "
                    + "        object_id         , "
                    + "        CAST (NULL AS INT), "
                    + "        CAST (NULL AS INT), "
                    + "        0 "
                    + "FROM    textsearch "
                    + "WHERE   class_id IN ( WITH recursive derived_child(father,child,depth) AS "
                    + "                     ( SELECT father, "
                    + "                             father , "
                    + "                             0 "
                    + "                     FROM    cs_class_hierarchy "
                    + "                     WHERE   father IN ("+classes+") "
                    + "                      "
                    + "                     UNION ALL "
                    + "                      "
                    + "                     SELECT ch.father, "
                    + "                            ch.child , "
                    + "                            dc.depth+1 "
                    + "                     FROM   derived_child dc, "
                    + "                            cs_class_hierarchy ch "
                    + "                     WHERE  ch.father=dc.child "
                    + "                     ) "
                    + "              SELECT DISTINCT father "
                    + "              FROM            derived_child "
                    + "              LIMIT           100 ) "
                    + "AND             lower(string_val) LIKE '%" + searchString + "%' "
                    + " "
                    + "UNION ALL "
                    + " "
                    + "SELECT aam.class_id      , "
                    + "       aam.object_id     , "
                    + "       aam.attr_class_id , "
                    + "       aam.attr_object_id, "
                    + "       di.depth+1 "
                    + "FROM   cs_all_attr_mapping aam, "
                    + "       derived_index di "
                    + "WHERE  aam.attr_class_id =di.ocid "
                    + "AND    aam.attr_object_id=di.oid "
                    + ") "
                    + "SELECT * "
                    + "FROM   derived_index "
                    + "WHERE  ocid IN ("+classes+") "
                    + "LIMIT  1000;" ;

            getLog().debug("search started");


            MetaService ms = (MetaService) getActiveLoaclServers().get("WUNDA_BLAU");



            

            ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            ArrayList<Node> aln = new ArrayList<Node>();
            for (ArrayList al : result) {

                int cid = (Integer) al.get(0);
                 int oid = (Integer) al.get(1);
                MetaObjectNode mon = new MetaObjectNode("WUNDA_BLAU", oid,cid );

                aln.add(mon);
            }
            // Thread.sleep(5000);
            return aln;
        } catch (Exception e) {
            getLog().error("Problem", e);
            return null;
        }
    }
}
