package de.cismet.cids.custom.objectrenderer.utils.alkis.print;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.AffineTransformation;
import de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisCommons.ProduktLayout;
import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.features.DefaultFeatureCollection;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.util.FormatToRealWordCalculator;
import de.cismet.tools.collections.TypeSafeCollections;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author stefan
 */
public class AlkisPrintListener extends PBasicInputEventHandler {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    private static final AlkisPrintingToolTip PRINTING_TOOLTIP = new AlkisPrintingToolTip();
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisPrintListener.class);
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";
    public static final Color BORDER_COLOR = new Color(0, 0, 255, 75);
    private final PropertyChangeListener mapInteractionModeListener;
    private final MappingComponent mappingComponent;
    private StyledFeature printTemplateStyledFeature;
    private final FeatureMoveListener featureMoveListenerDelegate;
    private final List<Feature> backupFeature;
    private final List<Feature> backupHoldFeature;
    private final AlkisPrintingSettingsWidget printWidget;
    private double realWorldWidth = 0;
    private double realWorldHeight = 0;
    private String oldInteractionMode;

    /** Creates a new instance of PrintingFrameListener */
    public AlkisPrintListener(MappingComponent mappingComponent, AlkisPrintingSettingsWidget printWidget) {
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
                if (evt != null && evt.getPropertyName().equals(MappingComponent.PROPERTY_MAP_INTERACTION_MODE)) {
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
        initScaling(massstab, layout.width, layout.height);
        initMapTemplate(geom, findOptimalRotation);
        mappingComponent.setInteractionMode(MappingComponent.ALKIS_PRINT);
        mappingComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mappingComponent.setPointerAnnotation(PRINTING_TOOLTIP);
        mappingComponent.setPointerAnnotationVisibility(true);
        if (!MappingComponent.ALKIS_PRINT.equals(currentInteractionMode)) {
            this.oldInteractionMode = currentInteractionMode;
            mappingComponent.addPropertyChangeListener(mapInteractionModeListener);
        }
    }

    private void initMapTemplate(Geometry geom, boolean findOptimalRotation) {

        DefaultFeatureCollection mapFeatureCol = (DefaultFeatureCollection) mappingComponent.getFeatureCollection();
//        Point centroid = geom.getCentroid();
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
        Polygon polygon = GEOMETRY_FACTORY.createPolygon(ring, null);
        if (findOptimalRotation) {
            BoundingBox polygonBB = new BoundingBox(polygon);
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
//            StyledFeature debug = new DefaultStyledFeature();
//            debug.setGeometry(minimumDiameter);
//            debug.setFillingPaint(Color.RED);
//            mapFeatureCol.addFeature(debug);
        }
        //translate to target landparcel position
        AffineTransformation translateToDestination = AffineTransformation.translationInstance(centerX, centerY);
        polygon.apply(translateToDestination);
        printTemplateStyledFeature = new DefaultStyledFeature();
        printTemplateStyledFeature.setFillingPaint(BORDER_COLOR);
        printTemplateStyledFeature.setCanBeSelected(true);
        printTemplateStyledFeature.setEditable(true);
        printTemplateStyledFeature.setGeometry(polygon);
        if (backupFeature.isEmpty()) {
            backupFeature.addAll(mapFeatureCol.getAllFeatures());
            backupHoldFeature.addAll(mapFeatureCol.getHoldFeatures());
        }
        mapFeatureCol.clear();
        //TODO: bug, buggy bug: selection is no more done if we call hold() :-/
        mapFeatureCol.holdFeature(printTemplateStyledFeature);
        mapFeatureCol.addFeature(printTemplateStyledFeature);
        mappingComponent.zoomToFeatureCollection();
        mapFeatureCol.select(printTemplateStyledFeature);
        mappingComponent.setHandleInteractionMode(MappingComponent.ROTATE_POLYGON);
//        mappingComponent.showHandles(false);
    }

    private void initScaling(
            double massstab,
            int formatWidth,
            int formatHeigth) {
        realWorldWidth = FormatToRealWordCalculator.toRealWorldValue(formatWidth, massstab);
        realWorldHeight = FormatToRealWordCalculator.toRealWorldValue(formatHeigth, massstab);
        if (massstab != 0 && !mappingComponent.isFixedMapScale()) {
            mappingComponent.queryServices();
        }
    }

//    public double getScaleDenominator() {
//        double real;
//        double paper;
//        if (bestimmerDimension.equals(PrintingFrameListener.HEIGHT)) {
////            real = getPrintingBoundingBox().getHeight();
//            paper = placeholderHeight / DEFAULT_JAVA_RESOLUTION_IN_DPI * MILLIMETER_OF_AN_INCH / MILLIMETER_OF_A_METER;
//        } else {
////            real = getPrintingBoundingBox().getWidth();
//            paper = placeholderWidth / DEFAULT_JAVA_RESOLUTION_IN_DPI * MILLIMETER_OF_AN_INCH / MILLIMETER_OF_A_METER;
//        }
////        return real / paper;
//        return 0d;
//    }
    @Override
    public void mouseReleased(PInputEvent e) {
        super.mouseReleased(e);
        featureMoveListenerDelegate.mouseReleased(e);
    }

    @Override
    public void mousePressed(PInputEvent e) {
        featureMoveListenerDelegate.mousePressed(e);
    }

    @Override
    public void mouseMoved(PInputEvent event) {
    }

    @Override
    public void mouseDragged(PInputEvent e) {
        featureMoveListenerDelegate.mouseDragged(e);
    }

    @Override
    public void mouseWheelRotated(PInputEvent event) {
    }

//    private void zoom(double scale) {
//        PBounds b = getPrintingRectangle().getBounds();
//        double oldW = b.getWidth();
//        double oldH = b.getHeight();
//        double w = b.getWidth();
//        double h = b.getHeight();
//        if (bestimmerDimension.equals(WIDTH)) {
//            w = w * scale;
//            h = w / widthToHeightRatio;
//        } else {
//            h = h * scale;
//            w = h * widthToHeightRatio;
//        }
//        double diffW = oldW - w;
//        double diffH = oldH - h;
//
//        b.setOrigin(b.getOrigin().getX() + diffW / 2, b.getOrigin().getY() + diffH / 2);
//        b.setSize(w, h);
//        getPrintingRectangle().setBounds(b);
////        getPrintingRectangle().setR
//    }
    @Override
    public void mouseClicked(PInputEvent event) {
        super.mouseClicked(event);
        if (event.getClickCount() > 1 && event.isLeftMouseButton()) {
            double rotationAngle = calculateRotationAngle();
            Point templateCenter = getTemplateCenter();
            printWidget.createProduct(templateCenter, rotationAngle);
            cleanUpAndRestoreFeatures();
            mappingComponent.setInteractionMode(oldInteractionMode);
        }
    }

    private void cleanUpAndRestoreFeatures() {
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
//    public BoundingBox getPrintingBoundingBox() {
//        WorldToScreenTransform wtst = mappingComponent.getWtst();
//        double x1 = wtst.getWorldX(getPrintingRectangle().getBounds().getMinX());
//        double y1 = wtst.getWorldY(getPrintingRectangle().getBounds().getMinY());
//        double x2 = wtst.getWorldX(getPrintingRectangle().getBounds().getMaxX());
//        double y2 = wtst.getWorldY(getPrintingRectangle().getBounds().getMaxY());
//        return new BoundingBox(x1, y1, x2, y2);
//    }
//    public void adjustMap() {
//        int delayTime = 800;
//        zoomTime = System.currentTimeMillis() + delayTime;
//        if (zoomThread == null || !zoomThread.isAlive()) {
//            zoomThread = new Thread() {
//
//                @Override
//                public void run() {
//                    while (System.currentTimeMillis() < zoomTime) {
//                        try {
//                            sleep(100);
//                            //log.debug("WAIT");
//                        } catch (InterruptedException iex) {
//                        }
//                    }
//                    //log.debug("ZOOOOOOOOOOOOOOOOOOOOOOOOOOOM");
//                    PBounds b = getPrintingRectangle().getBounds();
//                    PBounds mover = new PBounds(b.getX() - b.getWidth() * (0.25 / 2.0), b.getY() - b.getHeight() * (0.25 / 2.0), b.getWidth() * 1.25, b.getHeight() * 1.25);
//                    mappingComponent.getCamera().animateViewToCenterBounds(mover, true, mappingComponent.getAnimationDuration());
//                    //TODO Hier muss noch die momentane BoundingBox in die History gesetzt werden
//                    mappingComponent.queryServices();
//                }
//            };
//            zoomThread.setPriority(Thread.NORM_PRIORITY);
//            CismetThreadPool.execute(zoomThread);
//        }
//    }
//    Thread zoomThread;
//    long zoomTime;
}


