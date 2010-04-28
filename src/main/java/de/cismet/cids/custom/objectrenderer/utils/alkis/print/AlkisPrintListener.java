package de.cismet.cids.custom.objectrenderer.utils.alkis.print;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisCommons.ProduktLayout;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.features.DefaultFeatureCollection;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.tools.PFeatureTools;
import de.cismet.cismap.commons.util.FormatToRealWordCalculator;
import de.cismet.tools.collections.TypeSafeCollections;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author stefan
 */
public class AlkisPrintListener extends PBasicInputEventHandler {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisPrintListener.class);
    //
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final AlkisPrintingToolTip PRINTING_TOOLTIP = new AlkisPrintingToolTip();
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";
    public static final Color BORDER_COLOR = new Color(0, 0, 255, 75);
    //
    private final PropertyChangeListener mapInteractionModeListener;
    private final MappingComponent mappingComponent;
    private final Collection<Feature> printFeatureCollection;
    private final FeatureMoveListener featureMoveListenerDelegate;
    private final List<Feature> backupFeature;
    private final List<Feature> backupHoldFeature;
    private final AlkisPrintingSettingsWidget printWidget;
    private double diagonal;
    private boolean cleared;
    private String oldInteractionMode;
    private StyledFeature printTemplateStyledFeature;

    /** Creates a new instance of PrintingFrameListener */
    public AlkisPrintListener(MappingComponent mappingComponent, AlkisPrintingSettingsWidget printWidget) {
        this.diagonal = 0d;
        this.cleared = true;
        this.printFeatureCollection = TypeSafeCollections.newArrayList(1);
        this.mappingComponent = mappingComponent;
        this.printWidget = printWidget;
        this.featureMoveListenerDelegate = new FeatureMoveListener(mappingComponent);
        this.backupFeature = TypeSafeCollections.newArrayList();
        this.backupHoldFeature = TypeSafeCollections.newArrayList();
        this.oldInteractionMode = "PAN";
        //listener to remove the template feature and reset the old state if interaction mode is changed by user
        this.mapInteractionModeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt != null && MappingComponent.PROPERTY_MAP_INTERACTION_MODE.equals(evt.getPropertyName())) {
                    if (MappingComponent.ALKIS_PRINT.equals(evt.getOldValue())) {
                        cleanUpAndRestoreFeatures();
                    }
                }
            }
        };
    }

    public void init(double massstab,
            ProduktLayout layout,
            Geometry geom,
            boolean findOptimalRotation) {
        String currentInteractionMode = mappingComponent.getInteractionMode();
        double realWorldWidth = FormatToRealWordCalculator.toRealWorldValue(layout.width, massstab);
        double realWorldHeight = FormatToRealWordCalculator.toRealWorldValue(layout.height, massstab);
        if (massstab != 0 && !mappingComponent.isFixedMapScale()) {
            mappingComponent.queryServices();
        }
        initMapTemplate(geom, findOptimalRotation, realWorldWidth, realWorldHeight);
        mappingComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mappingComponent.setPointerAnnotation(PRINTING_TOOLTIP);
        mappingComponent.setPointerAnnotationVisibility(true);
        //do not add listener again if we are already in print mode
        if (!MappingComponent.ALKIS_PRINT.equals(currentInteractionMode)) {
            this.oldInteractionMode = currentInteractionMode;
            mappingComponent.addPropertyChangeListener(mapInteractionModeListener);
        }
        mappingComponent.setInteractionMode(MappingComponent.ALKIS_PRINT);
        cleared = false;
    }

    private void initMapTemplate(Geometry geom, boolean findOptimalRotation, double realWorldWidth, double realWorldHeight) {
        DefaultFeatureCollection mapFeatureCol = (DefaultFeatureCollection) mappingComponent.getFeatureCollection();
        //find center point for template geometry
        Point centroid = geom.getEnvelope().getCentroid();
        double centerX = centroid.getX();
        double centerY = centroid.getY();
        double halfRealWorldWidth = realWorldWidth / 2d;
        double halfRealWorldHeigth = realWorldHeight / 2d;
        //build geometry for sheet with center in origin
        Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(-halfRealWorldWidth, -halfRealWorldHeigth);
        coords[1] = new Coordinate(+halfRealWorldWidth, -halfRealWorldHeigth);
        coords[2] = new Coordinate(+halfRealWorldWidth, +halfRealWorldHeigth);
        coords[3] = new Coordinate(-halfRealWorldWidth, +halfRealWorldHeigth);
        coords[4] = new Coordinate(-halfRealWorldWidth, -halfRealWorldHeigth);
        //create the geometry from coordinates
        LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        Geometry polygon = GEOMETRY_FACTORY.createPolygon(ring, null);
        BoundingBox polygonBB = new BoundingBox(polygon);
        if (findOptimalRotation) {
            //determine the minimum diameter line
            LineString minimumDiameter = new MinimumDiameter(geom).getDiameter();
            Point start = minimumDiameter.getStartPoint();
            Point end = minimumDiameter.getEndPoint();
            //choose right order
            if (start.getX() > end.getX()) {
                Point temp = start;
                start = end;
                end = temp;
            }
            //calculate angle from tangens
            double oppositeLeg = end.getY() - start.getY();
            double adjacentLeg = end.getX() - start.getX();
            double tangens = oppositeLeg / adjacentLeg;
            //modify according to page orientation (portrait <> landscape)
            if (polygonBB.getWidth() > polygonBB.getHeight()) {
                tangens = - 1 / tangens;
            }
            double angle = Math.atan(tangens);
            //rotate to optimal angle
            AffineTransformation rotation = AffineTransformation.rotationInstance(angle);
            polygon.apply(rotation);
        }
        //translate to target landparcel position
        AffineTransformation translateToDestination = AffineTransformation.translationInstance(centerX, centerY);
        polygon.apply(translateToDestination);
        printTemplateStyledFeature = new PrintFeature();
        printTemplateStyledFeature.setFillingPaint(BORDER_COLOR);
        printTemplateStyledFeature.setCanBeSelected(true);
        printTemplateStyledFeature.setEditable(true);
        printTemplateStyledFeature.setGeometry(polygon);
        printFeatureCollection.clear();
        printFeatureCollection.add(printTemplateStyledFeature);
        if (backupFeature.isEmpty() && !MappingComponent.ALKIS_PRINT.equals(mappingComponent.getInteractionMode())) {
            backupFeature.addAll(mapFeatureCol.getAllFeatures());
            backupHoldFeature.addAll(mapFeatureCol.getHoldFeatures());
        }
        diagonal = Math.sqrt(polygonBB.getWidth() * polygonBB.getWidth() + polygonBB.getHeight() * polygonBB.getHeight());
        mapFeatureCol.clear();
        //TODO: bug, buggy bug: selection is no more done if we call hold() :-/
        mapFeatureCol.holdFeature(printTemplateStyledFeature);
        mapFeatureCol.addFeature(printTemplateStyledFeature);
        PFeature printPFeature = mappingComponent.getPFeatureHM().get(printTemplateStyledFeature);
        printPFeature.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("parent".equals(evt.getPropertyName()) && evt.getNewValue() != null) {
                    gotoPrintAreaWithBuffer();
//                        mappingComponent.zoomToAFeatureCollection(printFeatureCollection, false, false);
                } else if ("visible".equals(evt.getPropertyName()) && evt.getNewValue() == null) {
                    cleanUpAndRestoreFeatures();
                }
            }
        });
//        mappingComponent.zoomToFeatureCollection();
        gotoPrintAreaWithBuffer();
        mapFeatureCol.select(printTemplateStyledFeature);
        mappingComponent.setHandleInteractionMode(MappingComponent.ROTATE_POLYGON);
    }

    private void gotoPrintAreaWithBuffer() {
        Point center = printTemplateStyledFeature.getGeometry().getCentroid();
        double halfDiagonal = diagonal / 2d;
        BoundingBox gotoBB = new BoundingBox(
                center.getX() - halfDiagonal,
                center.getY() - halfDiagonal,
                center.getX() + halfDiagonal,
                center.getY() + halfDiagonal);
        mappingComponent.gotoBoundingBoxWithHistory(gotoBB);
    }

    @Override
    public void mouseReleased(PInputEvent e) {
        super.mouseReleased(e);
        featureMoveListenerDelegate.mouseReleased(e);
//        mappingComponent.zoomToAFeatureCollection(printFeatureCollection, false, false);
    }

    private void ensureSelection(PInputEvent e) {
        final Object o = PFeatureTools.getFirstValidObjectUnderPointer(e, new Class[]{PFeature.class});
        if (o instanceof PFeature) {
            PFeature pFeature = (PFeature) o;
            Feature feature = pFeature.getFeature();
            DefaultFeatureCollection mapFeatureCol = (DefaultFeatureCollection) mappingComponent.getFeatureCollection();
            if (!mapFeatureCol.isSelected(feature)) {
                mapFeatureCol.select(feature);
            }
        }
    }

    @Override
    public void mousePressed(PInputEvent e) {
        ensureSelection(e);
        featureMoveListenerDelegate.mousePressed(e);
    }

    @Override
    public void mouseMoved(PInputEvent event) {
        //NOP
    }

    @Override
    public void mouseDragged(PInputEvent e) {
        featureMoveListenerDelegate.mouseDragged(e);
    }

    @Override
    public void mouseWheelRotated(PInputEvent event) {
        //NOP
    }

    @Override
    public void mouseClicked(PInputEvent event) {
        super.mouseClicked(event);
        if (event.getClickCount() > 1 && event.isLeftMouseButton()) {
            double rotationAngle = calculateRotationAngle();
            Point templateCenter = getTemplateCenter();
            printWidget.createProduct(templateCenter, rotationAngle);
            cleanUpAndRestoreFeatures();
        }
    }

    private void cleanUpAndRestoreFeatures() {
        if (!cleared) {
            mappingComponent.removePropertyChangeListener(mapInteractionModeListener);
            FeatureCollection mapFeatureCollection = mappingComponent.getFeatureCollection();
            mapFeatureCollection.unholdFeature(printTemplateStyledFeature);
            mapFeatureCollection.removeFeature(printTemplateStyledFeature);
            if (!backupFeature.isEmpty()) {
                mapFeatureCollection.addFeatures(backupFeature);
                for (Feature toHold : backupHoldFeature) {
                    mapFeatureCollection.holdFeature(toHold);
                }
                mappingComponent.zoomToFeatureCollection();
            }
            backupFeature.clear();
            backupHoldFeature.clear();
            if (MappingComponent.ALKIS_PRINT.equals(mappingComponent.getInteractionMode())) {
                mappingComponent.setInteractionMode(oldInteractionMode);
            }
        }
        cleared = true;
    }

    private final Point getTemplateCenter() {
        return printTemplateStyledFeature.getGeometry().getCentroid();
    }

    private double calculateRotationAngle() {
        Geometry geom = printTemplateStyledFeature.getGeometry();
        Coordinate[] corrds = geom.getCoordinates();
        //take former points (0,0) and (X,0) from template rectangle
        Coordinate point00 = corrds[0];
        Coordinate pointX0 = corrds[1];
        //determine tangens
        double oppositeLeg = pointX0.y - point00.y;
        double adjacentLeg = pointX0.x - point00.x;
        double tangens = oppositeLeg / adjacentLeg;
        //handle quadrant detection, map to range [-180, 180] degree
        double result = adjacentLeg > 0 ? 0d : 180d;
        result = oppositeLeg > 0 ? -result : result;
        //calculate rotation angle in degree
        result -= Math.toDegrees(Math.atan(tangens));
////        round to next full degree
//        return Math.round(result);
        return result;
    }

    private static final class PrintFeature extends DefaultStyledFeature {

        @Override
        public String toString() {
            return "Druckbereich";
        }
    }
}


