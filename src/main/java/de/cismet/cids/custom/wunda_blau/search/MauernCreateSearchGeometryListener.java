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
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchCreateSearchGeometryListener;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernCreateSearchGeometryListener extends AbstractCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    public static final String MAUERN_CREATE_SEARCH_GEOMETRY = "MAUERN_CREATE_SEARCH_GEOMETRY";
    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";

    //~ Instance fields --------------------------------------------------------

    private final PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernCreateSearchGeometryListener object.
     *
     * @param  mc       DOCUMENT ME!
     * @param  toolTip  DOCUMENT ME!
     */
    public MauernCreateSearchGeometryListener(final MappingComponent mc, final PNode toolTip) {
        super(mc);
        registerOnMappingComponent();

        final MetaSearchCreateSearchGeometryListener metaSearchListener = (MetaSearchCreateSearchGeometryListener)
            getMappingComponent().getInputListener(
                MappingComponent.CREATE_SEARCH_POLYGON);

        initListenerRelations(metaSearchListener);
        setAllAttributesFrom(metaSearchListener);
        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void registerOnMappingComponent() {
        getMappingComponent().addCustomInputListener(MAUERN_CREATE_SEARCH_GEOMETRY, this);
        getMappingComponent().putCursor(MAUERN_CREATE_SEARCH_GEOMETRY, new Cursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * DOCUMENT ME!
     *
     * @param  metaSearchListener  DOCUMENT ME!
     */
    private void initListenerRelations(final MetaSearchCreateSearchGeometryListener metaSearchListener) {
        metaSearchListener.addPropertyChangeListener(this);
        addPropertyChangeListener(metaSearchListener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  createSearchGeometryListener  DOCUMENT ME!
     */
    private void setAllAttributesFrom(final CreateSearchGeometryListener createSearchGeometryListener) {
        setMode(createSearchGeometryListener.getMode());
        setLastFeature(createSearchGeometryListener.getLastSearchFeature());
        setNumOfEllipseEdges(createSearchGeometryListener.getNumOfEllipseEdges());
        setHoldGeometries(createSearchGeometryListener.isHoldingGeometries());
        setSearchColor(createSearchGeometryListener.getSearchColor());
        setSearchTransparency(createSearchGeometryListener.getSearchTransparency());
    }

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
