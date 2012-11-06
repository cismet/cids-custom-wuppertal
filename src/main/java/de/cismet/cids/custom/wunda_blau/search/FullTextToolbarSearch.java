/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import javax.swing.ImageIcon;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;

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

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setSearchParameter(final String toolbarSearchString) {
        input = toolbarSearchString;
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
    public MetaObjectNodeServerSearch getServerSearch() {
        return new FullTextSearchStatement(input);
    }
}
