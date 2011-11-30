/*
 * Copyright (C) 2011 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.search.HereModifier;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.search.CidsServerSearch;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.CustomAlkisPointSearchStatement;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import de.cismet.cids.tools.search.clientstuff.Modifier;
import java.util.Collection;
import javax.swing.ImageIcon;

/**
 *
 * @author jweintraut
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class CustomAlkisPointToolbarSearch implements CidsToolbarSearch {
    private static final String CIDSCLASS = "ALKIS_POINT";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CustomAlkisPointToolbarSearch.class);

    private String searchString;
    private Collection<? extends Modifier> modifiers;
    private final MetaClass metaClass;
    private final ImageIcon icon;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomStrassenToolbarSearch object.
     */
    public CustomAlkisPointToolbarSearch() {
        metaClass = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, CIDSCLASS);
        icon = new ImageIcon(metaClass.getIconData());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return "ALKIS-Punkt";
    }
    
    @Override
    public String getHint() {
        return "Nummer des ALKIS-Punktes";
    }

    @Override
    public void setSearchParameter(final String searchString) {
        this.searchString = searchString;
    }
    
    @Override
    public void applyModifiers(final Collection<? extends Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public CidsServerSearch getServerSearch() {
        String geometry = "";
        
        for(final Modifier modifier : modifiers) {
            if(modifier instanceof HereModifier) {
                geometry = modifier.getValue();
                break;
            }
        }
        
        return new CustomAlkisPointSearchStatement(searchString, geometry);
    }
}
