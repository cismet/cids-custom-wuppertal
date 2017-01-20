/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils.alkis.print;

import com.vividsolutions.jts.algorithm.MinimumDiameter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.util.AffineTransformation;

import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.Color;
import java.awt.Cursor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.utils.alkis.AlkisProductDescription;

import de.cismet.cismap.commons.BoundingBox;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultFeatureCollection;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.FeatureCollection;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.FeatureMoveListener;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.tools.PFeatureTools;

import de.cismet.tools.collections.TypeSafeCollections;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class AlkisPrintListener extends FeatureMoveListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AlkisPrintListener.class);
    //
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(
                PrecisionModel.FLOATING),
            CrsTransformer.extractSridFromCrs(AlkisConstants.COMMONS.SRS_SERVICE));
    private static final AlkisPrintingToolTip PRINTING_TOOLTIP = new AlkisPrintingToolTip();
    public static final String WIDTH = "WIDTH";
    public static final String HEIGHT = "HEIGHT";
    public static final Color BORDER_COLOR = new Color(0, 0, 255, 75);

    //~ Instance fields --------------------------------------------------------

    //
    private final PropertyChangeListener mapInteractionModeListener;
    private final MappingComponent mappingComponent;
    private final Collection<Feature> printFeatureCollection;
    // private final FeatureMoveListener featureMoveListenerDelegate;
    private final List<Feature> backupFeature;
    private final List<Feature> backupHoldFeature;
    private final AlkisPrintingSettingsWidget printWidget;
    private double diagonal;
    private boolean cleared;
    private String oldInteractionMode;
    private StyledFeature printTemplateStyledFeature;
    private boolean oldOverlappingCheck = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new instance of PrintingFrameListener.
     *
     * @param  mappingComponent  DOCUMENT ME!
     * @param  printWidget       DOCUMENT ME!
     */
    public AlkisPrintListener(final MappingComponent mappingComponent, final AlkisPrintingSettingsWidget printWidget) {
        super(mappingComponent);
        this.diagonal = 0d;
        this.cleared = true;
        this.printFeatureCollection = TypeSafeCollections.newArrayList(1);
        this.mappingComponent = mappingComponent;
        this.printWidget = printWidget;
//        this.featureMoveListenerDelegate = new FeatureMoveListener(mappingComponent);
        this.backupFeature = TypeSafeCollections.newArrayList();
        this.backupHoldFeature = TypeSafeCollections.newArrayList();
        this.oldInteractionMode = "PAN";
        // listener to remove the template feature and reset the old state if interaction mode is changed by user
        this.mapInteractionModeListener = new PropertyChangeListener() {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    if ((evt != null) && MappingComponent.PROPERTY_MAP_INTERACTION_MODE.equals(evt.getPropertyName())) {
                        if (MappingComponent.ALKIS_PRINT.equals(evt.getOldValue())) {
                            cleanUpAndRestoreFeatures();
                        }
                    }
                }
            };
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void init() {
        mappingComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mappingComponent.setPointerAnnotation(PRINTING_TOOLTIP);
        mappingComponent.setPointerAnnotationVisibility(true);
        final String currentInteractionMode = mappingComponent.getInteractionMode();

        // do not add listener again if we are already in print mode
        if (!MappingComponent.ALKIS_PRINT.equals(currentInteractionMode)) {
            this.oldInteractionMode = currentInteractionMode;
            mappingComponent.addPropertyChangeListener(mapInteractionModeListener);
        }
        mappingComponent.setInteractionMode(MappingComponent.ALKIS_PRINT);

        cleared = false;
        mappingComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mappingComponent.setPointerAnnotation(PRINTING_TOOLTIP);
        mappingComponent.setPointerAnnotationVisibility(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  product              DOCUMENT ME!
     * @param  geom                 DOCUMENT ME!
     * @param  findOptimalRotation  DOCUMENT ME!
     */
    public void refreshPreviewGeometry(final AlkisProductDescription product,
            final Geometry geom,
            final boolean findOptimalRotation) {
        if (geom != null) {
            // translate from alkis db geom srid to alkis service srid
            final Geometry serviceConformGeometry = CrsTransformer.transformToGivenCrs(
                    geom,
                    AlkisConstants.COMMONS.SRS_SERVICE);
            final double massstab = Double.parseDouble(product.getMassstab());
            final double realWorldWidth = product.getWidth() / 1000.0d * massstab;
            final double realWorldHeight = product.getHeight() / 1000.0d * massstab;
            if ((massstab != 0) && !mappingComponent.isFixedMapScale()) {
                mappingComponent.queryServices();
            }
            initMapTemplate(product, serviceConformGeometry, findOptimalRotation, realWorldWidth, realWorldHeight);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  product              DOCUMENT ME!
     * @param  geom                 DOCUMENT ME!
     * @param  findOptimalRotation  DOCUMENT ME!
     * @param  realWorldWidth       DOCUMENT ME!
     * @param  realWorldHeight      DOCUMENT ME!
     */
    private void initMapTemplate(final AlkisProductDescription product,
            final Geometry geom,
            final boolean findOptimalRotation,
            final double realWorldWidth,
            final double realWorldHeight) {
        final DefaultFeatureCollection mapFeatureCol = (DefaultFeatureCollection)
            mappingComponent.getFeatureCollection();
        // find center point for template geometry
        final Point centroid = geom.getEnvelope().getCentroid();
        final double centerX = centroid.getX();
        final double centerY = centroid.getY();
        final double halfRealWorldWidth = realWorldWidth / 2d;
        final double halfRealWorldHeigth = realWorldHeight / 2d;
        // build geometry for sheet with center in origin
        final Coordinate[] outerCoords = new Coordinate[5];
        outerCoords[0] = new Coordinate(-halfRealWorldWidth, -halfRealWorldHeigth);
        outerCoords[1] = new Coordinate(+halfRealWorldWidth, -halfRealWorldHeigth);
        outerCoords[2] = new Coordinate(+halfRealWorldWidth, +halfRealWorldHeigth);
        outerCoords[3] = new Coordinate(-halfRealWorldWidth, +halfRealWorldHeigth);
        outerCoords[4] = new Coordinate(-halfRealWorldWidth, -halfRealWorldHeigth);

        // create the geometry from coordinates
        LinearRing outerRing = GEOMETRY_FACTORY.createLinearRing(outerCoords);
        LinearRing[] innerRings = null;

        // translate to target landparcel position
        final AffineTransformation translateToDestination = AffineTransformation.translationInstance(centerX, centerY);
        outerRing = (LinearRing)translateToDestination.transform(outerRing);

        // Check for Stempelfeld
        if (product.getStempelfeldInfo() != null) {
            // coords[0] lower left
            final double fromX = product.getStempelfeldInfo().getFromX();
            final double fromY = product.getStempelfeldInfo().getFromY();
            final double toX = product.getStempelfeldInfo().getToX();
            final double toY = product.getStempelfeldInfo().getToY();

            final double stempelfeldWidth = realWorldWidth * (toX - fromX);
            final double stempelfeldHeight = realWorldHeight * (toY - fromY);

            final Coordinate[] innerCoords = new Coordinate[5];
            final Coordinate base = new Coordinate(outerCoords[0].x + (fromX * realWorldWidth),
                    outerCoords[0].y
                            + (fromY * realWorldHeight));

            innerCoords[0] = base;
            innerCoords[1] = new Coordinate(base.x, base.y + stempelfeldHeight);
            innerCoords[2] = new Coordinate(base.x + stempelfeldWidth, base.y + stempelfeldHeight);
            innerCoords[3] = new Coordinate(base.x + stempelfeldWidth, base.y);
            innerCoords[4] = base;
            LinearRing innerRing = GEOMETRY_FACTORY.createLinearRing(innerCoords);
            innerRings = new LinearRing[1];
//            innerRing.apply(translateToDestination);
            innerRing = (LinearRing)translateToDestination.transform(innerRing);

            innerRings[0] = innerRing;
        }

        final BoundingBox polygonBB = new BoundingBox(outerRing);

        AffineTransformation rotation = null;
        if (findOptimalRotation) {
            // determine the minimum diameter line
            final LineString minimumDiameter = new MinimumDiameter(geom).getDiameter();
            Point start = minimumDiameter.getStartPoint();
            Point end = minimumDiameter.getEndPoint();
            // choose right order
            if (start.getX() > end.getX()) {
                final Point temp = start;
                start = end;
                end = temp;
            }
            // calculate angle from tangens
            final double oppositeLeg = end.getY() - start.getY();
            final double adjacentLeg = end.getX() - start.getX();
            double tangens = oppositeLeg / adjacentLeg;
            // modify according to page orientation (portrait <> landscape)
            if (polygonBB.getWidth() > polygonBB.getHeight()) {
                tangens = -1 / tangens;
            }
            final double angle = Math.atan(tangens);
            // rotate to optimal angle
            rotation = AffineTransformation.rotationInstance(angle, centerX, centerY);
            outerRing = (LinearRing)rotation.transform(outerRing);
            if (innerRings != null) {
                innerRings[0] = (LinearRing)rotation.transform(innerRings[0]);
            }
        }
        final Geometry polygon = GEOMETRY_FACTORY.createPolygon(outerRing, innerRings);
        final Feature oldPrintFeature = printTemplateStyledFeature;
        printTemplateStyledFeature = new PrintFeature();
        printTemplateStyledFeature.setFillingPaint(BORDER_COLOR);
        printTemplateStyledFeature.setCanBeSelected(true);
        printTemplateStyledFeature.setEditable(true);
        printTemplateStyledFeature.setGeometry(polygon);
        printFeatureCollection.clear();
        printFeatureCollection.add(printTemplateStyledFeature);
        diagonal = Math.sqrt((polygonBB.getWidth() * polygonBB.getWidth())
                        + (polygonBB.getHeight() * polygonBB.getHeight()));
        // TODO: bug, buggy bug: selection is no more done if we call hold() :-/
        if (oldPrintFeature != null) {
            mapFeatureCol.unholdFeature(oldPrintFeature);
            mapFeatureCol.removeFeature(oldPrintFeature);
        } else {
            oldOverlappingCheck = CismapBroker.getInstance().isCheckForOverlappingGeometriesAfterFeatureRotation();
            CismapBroker.getInstance().setCheckForOverlappingGeometriesAfterFeatureRotation(false);
        }
        mapFeatureCol.holdFeature(printTemplateStyledFeature);
        mapFeatureCol.addFeature(printTemplateStyledFeature);
        final PFeature printPFeature = mappingComponent.getPFeatureHM().get(printTemplateStyledFeature);

        gotoPrintAreaWithBuffer();
        mapFeatureCol.select(printTemplateStyledFeature);
        mappingComponent.setHandleInteractionMode(MappingComponent.ROTATE_POLYGON);
    }

    /**
     * DOCUMENT ME!
     */
    private void gotoPrintAreaWithBuffer() {
        final Point center = printTemplateStyledFeature.getGeometry().getCentroid();
        final double halfDiagonal = diagonal / 2d;
        final XBoundingBox gotoBB = new XBoundingBox(
                center.getX()
                        - halfDiagonal,
                center.getY()
                        - halfDiagonal,
                center.getX()
                        + halfDiagonal,
                center.getY()
                        + halfDiagonal,
                AlkisConstants.COMMONS.SRS_SERVICE,
                true);
        mappingComponent.gotoBoundingBoxWithHistory(gotoBB);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void ensureSelection(final PInputEvent e) {
        final Object o = PFeatureTools.getFirstValidObjectUnderPointer(e, new Class[] { PFeature.class });
        if (o instanceof PFeature) {
            final PFeature pFeature = (PFeature)o;
            final Feature feature = pFeature.getFeature();
            final DefaultFeatureCollection mapFeatureCol = (DefaultFeatureCollection)
                mappingComponent.getFeatureCollection();
            if (!mapFeatureCol.isSelected(feature)) {
                mapFeatureCol.select(feature);
            }
        }
    }

    @Override
    public void mouseClicked(final PInputEvent event) {
        super.mouseClicked(event);
        if ((event.getClickCount() > 1) && event.isLeftMouseButton()) {
            final double rotationAngle = calculateRotationAngle();
            final Point templateCenter = getTemplateCenter();
            printWidget.downloadProduct(templateCenter, rotationAngle);
            cleanUpAndRestoreFeatures();
        }
    }

    @Override
    public void mouseReleased(final PInputEvent e) {
        super.mouseReleased(e);
        gotoPrintAreaWithBuffer();
    }

    /**
     * DOCUMENT ME!
     */
    public void cleanUpAndRestoreFeatures() {
        if (!cleared) {
            mappingComponent.removePropertyChangeListener(mapInteractionModeListener);
            if (printTemplateStyledFeature != null) {
                final FeatureCollection mapFeatureCollection = mappingComponent.getFeatureCollection();
                mapFeatureCollection.unholdFeature(printTemplateStyledFeature);
                mapFeatureCollection.removeFeature(printTemplateStyledFeature);
                printTemplateStyledFeature = null;
            }
            if (MappingComponent.ALKIS_PRINT.equals(mappingComponent.getInteractionMode())) {
                mappingComponent.setInteractionMode(oldInteractionMode);
            }
        }
        cleared = true;
        CismapBroker.getInstance().setCheckForOverlappingGeometriesAfterFeatureRotation(oldOverlappingCheck);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Point getTemplateCenter() {
        return printTemplateStyledFeature.getGeometry().getEnvelope().getCentroid();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private double calculateRotationAngle() {
        final Geometry geom = printTemplateStyledFeature.getGeometry();
        final Coordinate[] corrds = geom.getCoordinates();
        // take former points (0,0) and (X,0) from template rectangle
        final Coordinate point00 = corrds[0];
        final Coordinate pointX0 = corrds[1];
        // determine tangens
        final double oppositeLeg = pointX0.y - point00.y;
        final double adjacentLeg = pointX0.x - point00.x;
        final double tangens = oppositeLeg / adjacentLeg;
        // handle quadrant detection, map to range [-180, 180] degree
        double result = (adjacentLeg > 0) ? 0d : 180d;
        result = (oppositeLeg > 0) ? -result : result;
        // calculate rotation angle in degree
        result -= Math.toDegrees(Math.atan(tangens));
////        round to next full degree
//        return Math.round(result);
        return result;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class PrintFeature extends DefaultStyledFeature {

        //~ Methods ------------------------------------------------------------

        @Override
        public String toString() {
            return "Druckbereich";
        }
    }
}
