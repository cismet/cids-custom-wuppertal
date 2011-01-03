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
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.permission.Policy;
import Sirius.server.search.CidsServerSearch;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class CustomStrassenSearchStatement extends CidsServerSearch {

    //~ Instance fields --------------------------------------------------------

    private String searchString;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomStrassenSearchStatement object.
     *
     * @param  searchString  DOCUMENT ME!
     */
    public CustomStrassenSearchStatement(final String searchString) {
        this.searchString = searchString;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        try {
            getLog().fatal("search started");

            final MetaService ms = (MetaService)getActiveLoaclServers().get("WUNDA_BLAU");

            final MetaClass c = ms.getClassByTableName(getUser(), "strasse");

            final String sql = "select strassenschluessel,name from strasse where name like '%" + searchString
                        + "%' order by name desc";

            final ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            final ArrayList<Node> aln = new ArrayList<Node>();
            for (final ArrayList al : result) {
                final int id = (Integer)al.get(0);
                final MetaObjectNode mon = new MetaObjectNode(c.getDomain(), id, c.getId());

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
