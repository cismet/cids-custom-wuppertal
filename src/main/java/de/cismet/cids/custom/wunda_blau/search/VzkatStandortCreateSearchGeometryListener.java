/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wunda_blau.search;

import edu.umd.cs.piccolo.PNode;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchFollowingCreateSearchGeometryListener;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VzkatStandortCreateSearchGeometryListener extends MetaSearchFollowingCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String CREATE_SEARCH_GEOMETRY = "VZKAT_STANDORT_CREATE_SEARCH_GEOMETRY";

    //~ Instance fields --------------------------------------------------------

    private final PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppenCreateSearchGeometryListener object.
     *
     * @param  mc       DOCUMENT ME!
     * @param  toolTip  DOCUMENT ME!
     */
    public VzkatStandortCreateSearchGeometryListener(final MappingComponent mc, final PNode toolTip) {
        super(mc, CREATE_SEARCH_GEOMETRY);
        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected PNode getPointerAnnotation() {
        return toolTip;
    }
}
