/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;

import java.beans.PropertyChangeEvent;

import de.cismet.cismap.commons.features.PureNewFeature;
import de.cismet.cismap.commons.features.SearchFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateSearchGeometryListener;
import de.cismet.cismap.commons.tools.PFeatureTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class CreateAlkisPointSearchGeometryListener extends CreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CreateAlkisPointSearchGeometryListener.class);

    public static final String CREATE_ALKISPOINTSEARCH_GEOMETRY = "CREATE_ALKISPOINTSEARCH_GEOMETRY";
    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";

    //~ Instance fields --------------------------------------------------------

    private PNode toolTip;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateAlkisPointSearchGeometryListener object.
     *
     * @param  mappingComponent  DOCUMENT ME!
     * @param  toolTip           DOCUMENT ME!
     */
    public CreateAlkisPointSearchGeometryListener(final MappingComponent mappingComponent, final PNode toolTip) {
        super(mappingComponent);

        mc.addCustomInputListener(CREATE_ALKISPOINTSEARCH_GEOMETRY, this);
        mc.putCursor(CREATE_ALKISPOINTSEARCH_GEOMETRY, new Cursor(Cursor.CROSSHAIR_CURSOR));

        final CreateSearchGeometryListener createSearchGeometryListener = (CreateSearchGeometryListener)
            mc.getInputListener(MappingComponent.CREATE_SEARCH_POLYGON);
        createSearchGeometryListener.addPropertyChangeListener(this);
        addPropertyChangeListener(createSearchGeometryListener);

        super.setMode(createSearchGeometryListener.getMode());
        lastFeature = createSearchGeometryListener.getLastSearchFeature();
        this.numOfEllipseEdges = createSearchGeometryListener.getNumOfEllipseEdges();
        this.holdGeometries = createSearchGeometryListener.isHoldingGeometries();
        this.searchColor = createSearchGeometryListener.getSearchColor();
        this.searchTransparency = createSearchGeometryListener.getSearchTransparency();

        this.toolTip = toolTip;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void doSearch(final PureNewFeature searchFeature) {
        // letzte Suchgeometrie merken
        final PureNewFeature oldFeature = lastFeature;
        lastFeature = searchFeature;

        propertyChangeSupport.firePropertyChange(PROPERTY_LAST_FEATURE, oldFeature, searchFeature);
        propertyChangeSupport.firePropertyChange(PROPERTY_FORGUI_LAST_FEATURE, oldFeature, searchFeature);
        propertyChangeSupport.firePropertyChange(ACTION_SEARCH_STARTED, null, searchFeature.getGeometry());
    }

    @Override
    protected void handleUserFinishedSearchGeometry(final PureNewFeature feature) {
        mc.getFeatureCollection().addFeature(feature);

        doSearch(feature);

        cleanup(feature);
    }

    @Override
    protected void handleDoubleClickInMap(final PInputEvent pInputEvent) {
        final Object o = PFeatureTools.getFirstValidObjectUnderPointer(pInputEvent, new Class[] { PFeature.class });

        if (!(o instanceof PFeature)) {
            return;
        }
        final PFeature sel = (PFeature)o;

        if (!(sel.getFeature() instanceof SearchFeature)) {
            return;
        }

        if (pInputEvent.isLeftMouseButton()) {
            mc.getHandleLayer().removeAllChildren();
            // neue Suche mit Geometry auslösen
            ((CreateSearchGeometryListener)mc.getInputListener(CREATE_ALKISPOINTSEARCH_GEOMETRY)).search((SearchFeature)
                sel.getFeature());
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        final Object newValue = evt.getNewValue();

        if (MappingComponent.PROPERTY_MAP_INTERACTION_MODE.equals(propertyName)) {
            if (CREATE_ALKISPOINTSEARCH_GEOMETRY.equals(newValue)) {
                generateAndShowPointerAnnotation();
            }
        } else if (PROPERTY_LAST_FEATURE.equals(propertyName)) {
            lastFeature = (PureNewFeature)newValue;
            propertyChangeSupport.firePropertyChange(
                PROPERTY_FORGUI_LAST_FEATURE,
                evt.getOldValue(),
                newValue);
        } else if (PROPERTY_MODE.equals(propertyName)) {
            super.setMode(newValue.toString());
            propertyChangeSupport.firePropertyChange(PROPERTY_FORGUI_MODE, evt.getOldValue(), evt.getNewValue());
        } else if (PROPERTY_HOLD_GEOMETRIES.equals(propertyName) && (newValue instanceof Boolean)) {
            this.holdGeometries = (Boolean)newValue;
        } else if (PROPERTY_NUM_OF_ELLIPSE_EDGES.equals(propertyName) && (newValue instanceof Integer)) {
            this.numOfEllipseEdges = (Integer)newValue;
        } else if (PROPERTY_SEARCH_COLOR.equals(propertyName) && (newValue instanceof Color)) {
            this.searchColor = (Color)newValue;
        } else if (PROPERTY_SEARCH_TRANSPARENCY.equals(propertyName) && (newValue instanceof Float)) {
            this.searchTransparency = (Float)newValue;
        }
    }

    @Override
    protected void generateAndShowPointerAnnotation() {
        if (!CREATE_ALKISPOINTSEARCH_GEOMETRY.equals(mc.getInteractionMode()) || (toolTip == null)) {
            return;
        }

        final Runnable showPointerAnnotation = new Runnable() {

                @Override
                public void run() {
                    mc.setPointerAnnotation(toolTip);
                    mc.setPointerAnnotationVisibility(true);
                }
            };

        if (EventQueue.isDispatchThread()) {
            showPointerAnnotation.run();
        } else {
            EventQueue.invokeLater(showPointerAnnotation);
        }
    }
}
