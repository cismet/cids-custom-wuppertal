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
public class VermessungRissCreateSearchGeometryListener extends MetaSearchFollowingCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VermessungRissCreateSearchGeometryListener.class);

    public static final String VERMESSUNGRISS_CREATE_SEARCH_GEOMETRY = "VERMESSUNGRISS_CREATE_SEARCH_GEOMETRY";

    //~ Instance fields --------------------------------------------------------

    private final PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateAlkisPointSearchGeometryListener object.
     *
     * @param  mappingComponent  DOCUMENT ME!
     * @param  toolTip           DOCUMENT ME!
     */
    public VermessungRissCreateSearchGeometryListener(final MappingComponent mappingComponent, final PNode toolTip) {
        super(mappingComponent, VERMESSUNGRISS_CREATE_SEARCH_GEOMETRY);

        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected PNode getPointerAnnotation() {
        return toolTip;
    }
}
