/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.commons.searchgeometrylistener;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import java.beans.PropertyChangeListener;

import java.util.Arrays;

import de.cismet.cids.custom.wunda_blau.search.server.BufferingGeosearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.gui.MappingComponent;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class RissNodesSearchCreateSearchGeometryListener extends NodesSearchCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String NAME = "VERMESSUNG_RISS_SEARCH_GEOMETRY_LISTENER";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RissSearchGeometryListener object.
     *
     * @param  mc                  DOCUMENT ME!
     * @param  propChangeListener  DOCUMENT ME!
     * @param  connectionContext   DOCUMENT ME!
     */
    public RissNodesSearchCreateSearchGeometryListener(final MappingComponent mc,
            final PropertyChangeListener propChangeListener,
            final ConnectionContext connectionContext) {
        super(mc, propChangeListener, connectionContext);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getCrs() {
        return "EPSG:25832";
    }

    @Override
    public CidsServerSearch getCidsServerSearch(final Geometry geometry) {
        final BufferingGeosearch search = new BufferingGeosearch();
        try {
            final MetaClass mc = CidsBean.getMetaClassFromTableName(
                    "WUNDA_BLAU",
                    "vermessung_riss",
                    getConnectionContext());
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(geometry);
            return search;
        } catch (Exception ex) {
            return null;
        }
    }
}
