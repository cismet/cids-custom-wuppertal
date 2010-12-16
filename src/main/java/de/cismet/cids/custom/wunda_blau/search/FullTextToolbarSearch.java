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

import Sirius.server.search.CidsServerSearch;
import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import javax.swing.ImageIcon;

/**
 *
 * @author thorsten
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class FullTextToolbarSearch implements CidsToolbarSearch {
    String input;
    @Override
    public void setSearchParameter(String toolbarSearchString) {
        input=toolbarSearchString;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getName() {
        return "Volltextsuche";
    }


    @Override
    public CidsServerSearch getServerSearch() {
        return new FullTextSearchStatement(input);
    }

}
