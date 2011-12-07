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

import Sirius.navigator.search.CaseSensitiveModifier;
import Sirius.navigator.search.HereModifier;
import Sirius.server.search.CidsServerSearch;

import javax.swing.ImageIcon;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import de.cismet.cids.tools.search.clientstuff.Modifier;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class FullTextToolbarSearch implements CidsToolbarSearch {

    //~ Instance fields --------------------------------------------------------

    String input;
    private Collection<? extends Modifier> modifiers;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSearchParameter(final String toolbarSearchString) {
        input = toolbarSearchString;
    }
    
    @Override
    public void applyModifiers(final Collection<? extends Modifier> modifiers) {
        this.modifiers = modifiers;
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
    public String getHint() {
        return "Suchtext";
    }
    
    @Override
    public CidsServerSearch getServerSearch() {
        boolean caseSensitive = false;
        String geometry = "";
        
        for(final Modifier modifier : modifiers) {
            if(modifier instanceof CaseSensitiveModifier) {
                caseSensitive = true;
            } else if(modifier instanceof HereModifier) {
                geometry = modifier.getValue();
            }
        }
        
        return new FullTextSearchStatement(input, geometry, caseSensitive);
    }
}
