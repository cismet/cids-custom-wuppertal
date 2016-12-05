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

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FlurstueckNodesSearchCreateSearchGeometryListener extends NodesSearchCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String NAME = "ALKIS_LANDPARCEL_SEARCH_GEOMETRY_LISTENER";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RissSearchGeometryListener object.
     *
     * @param  mc                  DOCUMENT ME!
     * @param  propChangeListener  DOCUMENT ME!
     */
    public FlurstueckNodesSearchCreateSearchGeometryListener(final MappingComponent mc,
            final PropertyChangeListener propChangeListener) {
        super(mc, propChangeListener);
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
            final MetaClass mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "alkis_landparcel");
            search.setValidClasses(Arrays.asList(mc));
            search.setGeometry(geometry);
            return search;
        } catch (Exception ex) {
            return null;
        }
    }
}
