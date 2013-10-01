/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import edu.umd.cs.piccolo.PNode;

import org.apache.log4j.Logger;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchFollowingCreateSearchGeometryListener;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class MeasurementPointCreateSearchGeometryListener extends MetaSearchFollowingCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MeasurementPointCreateSearchGeometryListener.class);

    public static final String MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY = "MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY";

    //~ Instance fields --------------------------------------------------------

    private final PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateAlkisPointSearchGeometryListener object.
     *
     * @param  mappingComponent  DOCUMENT ME!
     * @param  toolTip           DOCUMENT ME!
     */
    public MeasurementPointCreateSearchGeometryListener(final MappingComponent mappingComponent, final PNode toolTip) {
        super(mappingComponent, MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY);
        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected PNode getPointerAnnotation() {
        return toolTip;
    }
}
