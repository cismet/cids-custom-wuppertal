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
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
public class AlkisCreateSearchGeometryListener extends MetaSearchFollowingCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String ALKIS_CREATE_SEARCH_GEOMETRY = "ALKIS_CREATE_SEARCH_GEOMETRY";

    //~ Instance fields --------------------------------------------------------

    private final PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisCreateSearchGeometryListener object.
     *
     * @param  mc       DOCUMENT ME!
     * @param  toolTip  DOCUMENT ME!
     */
    public AlkisCreateSearchGeometryListener(final MappingComponent mc, final PNode toolTip) {
        super(mc, ALKIS_CREATE_SEARCH_GEOMETRY);
        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected PNode getPointerAnnotation() {
        return toolTip;
    }
}
