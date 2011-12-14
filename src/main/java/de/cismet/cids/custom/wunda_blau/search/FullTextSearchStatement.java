/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
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
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.search.CidsServerSearch;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class FullTextSearchStatement extends CidsServerSearch {

    //~ Instance fields --------------------------------------------------------

    private String searchString;
    private String geometry;
    private boolean caseSensitive;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FullTextSearchStatement object.
     *
     * @param  searchString   DOCUMENT ME!
     * @param  geometry       DOCUMENT ME!
     * @param  caseSensitive  DOCUMENT ME!
     */
    public FullTextSearchStatement(final String searchString, final String geometry, final boolean caseSensitive) {
        this.searchString = searchString;
        this.geometry = geometry;
        this.caseSensitive = caseSensitive;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        try {
            final String classes = "6";
            String condition = "AND lower(string_val) LIKE lower('%" + searchString + "%') ";
            if (caseSensitive) {
                condition = "AND string_val LIKE '%" + searchString + "%' ";
            }

            final String sql = ""
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
                        + "                     WHERE   father IN (" + classes + ") "
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
                        + condition
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
                        + "WHERE  ocid IN (" + classes + ") "
                        + "LIMIT  1000;";
            if (getLog().isDebugEnabled()) {
                getLog().debug("search started");
            }

            final MetaService ms = (MetaService)getActiveLocalServers().get("WUNDA_BLAU");

            final ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            final ArrayList<Node> aln = new ArrayList<Node>();
            for (final ArrayList al : result) {
                final int cid = (Integer)al.get(0);
                final int oid = (Integer)al.get(1);
                final MetaObjectNode mon = new MetaObjectNode("WUNDA_BLAU", oid, cid);

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
