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

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.CustomStrassenSearchStatement;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import de.cismet.cids.tools.search.clientstuff.Modifier;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class CustomStrassenToolbarSearch implements CidsToolbarSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            CustomStrassenToolbarSearch.class);

    //~ Instance fields --------------------------------------------------------

    private String searchString;
    private Collection<? extends Modifier> modifiers;
    private final MetaClass mc;
    private final ImageIcon icon;
    private Collection<MetaClass> classCol;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomStrassenToolbarSearch object.
     */
    public CustomStrassenToolbarSearch() {
        mc = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "STRASSE");
        icon = new ImageIcon(mc.getIconData());
        classCol = new ArrayList<MetaClass>(1);
        classCol.add(mc);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getName() {
        return mc.getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  toolbarSearchString  DOCUMENT ME!
     */
    @Override
    public String getHint() {
        return "Strassenname";
    }

    @Override
    public void setSearchParameter(final String toolbarSearchString) {
        this.searchString = toolbarSearchString;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public void applyModifiers(final Collection<? extends Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public CidsServerSearch getServerSearch() {
        boolean caseSensitive = false;
        String geometry = "";

        for (final Modifier modifier : modifiers) {
            if (modifier instanceof CaseSensitiveModifier) {
                caseSensitive = true;
            } else if (modifier instanceof HereModifier) {
                geometry = modifier.getValue();
            }
        }

        return new CustomStrassenSearchStatement(searchString, geometry, caseSensitive);
    }
}
