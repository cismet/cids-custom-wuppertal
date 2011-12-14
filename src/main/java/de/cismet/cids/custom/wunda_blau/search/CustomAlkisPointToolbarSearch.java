/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.search.CaseSensitiveModifier;
import Sirius.navigator.search.HereModifier;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.search.CidsServerSearch;

import java.util.Collection;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.CustomAlkisPointSearchStatement;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import de.cismet.cids.tools.search.clientstuff.Modifier;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class CustomAlkisPointToolbarSearch implements CidsToolbarSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final String CIDSCLASS = "ALKIS_POINT";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CustomAlkisPointToolbarSearch.class);

    //~ Instance fields --------------------------------------------------------

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
        final boolean caseSensitive = false;

        for (final Modifier modifier : modifiers) {
            if (modifier instanceof HereModifier) {
                geometry = modifier.getValue();
                break;
            }
        }

        return new CustomAlkisPointSearchStatement(searchString, geometry);
    }
}
