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

import java.awt.Color;
import java.awt.Cursor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AbstractCreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.MetaSearchCreateSearchGeometryListener;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class MeasurementPointCreateSearchGeometryListener extends AbstractCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MeasurementPointCreateSearchGeometryListener.class);

    public static final String MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY = "MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY";
    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";

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
        super(mappingComponent);

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
        getMappingComponent().addCustomInputListener(MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY, this);
        getMappingComponent().putCursor(MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY, new Cursor(Cursor.CROSSHAIR_CURSOR));
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
    public void propertyChange(final PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        final Object newValue = evt.getNewValue();

        if (MappingComponent.PROPERTY_MAP_INTERACTION_MODE.equals(propertyName)) {
            if (MEASUREMENTPOINT_CREATE_SEARCH_GEOMETRY.equals(newValue)) {
                generateAndShowPointerAnnotation();
            }
        } else if (PROPERTY_LAST_FEATURE.equals(propertyName)) {
            setLastFeature((PureNewFeature)newValue);
        } else if (PROPERTY_MODE.equals(propertyName)) {
            super.setMode(newValue.toString());
            // Yes, invoking super.setMode(String) fires a new PropertyChangeEvent for PROPERTY_MODE. And we don't need
            // this new PropertyChangeEvent, we just want to set the new mode. But this new PropertyChangeEvent doesn't
            // any damage, so it's okay. But we should consider a "bypassing" method
            // AbstractCreateSearchGeometryListener.setReallyOnlyTheMode(String m) {super.setMode(m);}
            getPropertyChangeSupport().firePropertyChange(PROPERTY_FORGUI_MODE, evt.getOldValue(), evt.getNewValue());
        } else if (PROPERTY_HOLD_GEOMETRIES.equals(propertyName) && (newValue instanceof Boolean)) {
            setHoldGeometries((Boolean)newValue);
        } else if (PROPERTY_NUM_OF_ELLIPSE_EDGES.equals(propertyName) && (newValue instanceof Integer)) {
            setNumOfEllipseEdges((Integer)newValue);
        } else if (PROPERTY_SEARCH_COLOR.equals(propertyName) && (newValue instanceof Color)) {
            setSearchColor((Color)newValue);
        } else if (PROPERTY_SEARCH_TRANSPARENCY.equals(propertyName) && (newValue instanceof Float)) {
            setSearchTransparency((Float)newValue);
        }
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
