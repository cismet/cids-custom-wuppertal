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

import java.awt.Cursor;

import java.beans.PropertyChangeSupport;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AbstractCreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchCreateSearchGeometryListener;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
public class AlkisCreateSearchGeometryListener extends AbstractCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String ALKIS_CREATE_SEARCH_GEOMETRY = "ALKIS_CREATE_SEARCH_GEOMETRY";
    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";

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
        super(mc);
        getMappingComponent().addCustomInputListener(ALKIS_CREATE_SEARCH_GEOMETRY, this);
        getMappingComponent().putCursor(ALKIS_CREATE_SEARCH_GEOMETRY, new Cursor(Cursor.CROSSHAIR_CURSOR));

        final MetaSearchCreateSearchGeometryListener metaSearchListener = (MetaSearchCreateSearchGeometryListener)
            getMappingComponent().getInputListener(MappingComponent.CREATE_SEARCH_POLYGON);

        metaSearchListener.addPropertyChangeListener(this);
        addPropertyChangeListener(metaSearchListener);

        setMode(metaSearchListener.getMode());
        setLastFeature(metaSearchListener.getLastSearchFeature());
        setNumOfEllipseEdges(metaSearchListener.getNumOfEllipseEdges());
        setHoldGeometries(metaSearchListener.isHoldingGeometries());
        setSearchColor(metaSearchListener.getSearchColor());
        setSearchTransparency(metaSearchListener.getSearchTransparency());
        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected boolean performSearch(final PureNewFeature searchFeature) {
        final PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        final PureNewFeature oldFeature = getLastSearchFeature();

        propertyChangeSupport.firePropertyChange(PROPERTY_LAST_FEATURE, oldFeature, searchFeature);
        propertyChangeSupport.firePropertyChange(PROPERTY_FORGUI_LAST_FEATURE, oldFeature, searchFeature);
        propertyChangeSupport.firePropertyChange(ACTION_SEARCH_STARTED, null, searchFeature.getGeometry());

        return true;
    }

    @Override
    protected PNode getPointerAnnotation() {
        return toolTip;
    }
}
