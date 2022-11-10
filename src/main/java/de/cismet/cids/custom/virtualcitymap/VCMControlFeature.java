/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.virtualcitymap;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.Color;
import java.awt.Image;
import java.awt.Stroke;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.ChildNodesProvider;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.RequestForHidingHandles;
import de.cismet.cismap.commons.features.RequestForNonreflectingFeature;
import de.cismet.cismap.commons.features.RequestForRotatingPivotLock;
import de.cismet.cismap.commons.features.RequestForUnaddableHandles;
import de.cismet.cismap.commons.features.RequestForUnmoveableHandles;
import de.cismet.cismap.commons.features.RequestForUnremovableHandles;
import de.cismet.cismap.commons.features.XStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.FixedPImage;
import de.cismet.cismap.commons.gui.piccolo.PFeature;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DeriveRule;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DerivedCommandArea;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DerivedFixedPImage;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.DerivedFixedPImageCommandArea;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class VCMControlFeature extends DefaultStyledFeature implements XStyledFeature,
    ChildNodesProvider,
    RequestForUnaddableHandles,
    RequestForUnmoveableHandles,
    RequestForUnremovableHandles,
    RequestForRotatingPivotLock,
    RequestForNonreflectingFeature,
    RequestForHidingHandles,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VCMControlFeature.class);

    static final ImageIcon ARROWII = new javax.swing.ImageIcon(VCMControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/vcm.control.png"));
    static final Image ARROW = ARROWII.getImage();
    static final Image OPENVCM = new javax.swing.ImageIcon(VCMControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/vcm22.png")).getImage();
    static final Image ROTATE = new javax.swing.ImageIcon(VCMControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/turn.png")).getImage();
    static final Image REMOVE = new javax.swing.ImageIcon(VCMControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/remove.png")).getImage();

    //~ Instance fields --------------------------------------------------------

    ArrayList<PNode> children = new ArrayList<>();
    private final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
    private final VCMProperties properties = VCMProperties.getInstance();
    private final ConnectionContext connectionContext;

    private int rotationIndex = 3;

    private int[] headings = new int[] { 45, 135, 225, 315 };
    private double[][] sweetSpots = new double[][] {
            { 1, 0 },
            { 1, 1 },
            { 0, 1 },
            { 0, 0 }
        };
    private FixedPImage arrow;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VCMControlFeature object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public VCMControlFeature(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        setEditable(true);
        setCanBeSelected(true);
        setLinePaint(new Color(0, 0, 0, 0));
        setHighlightingEnabled(false);

        if (properties != null) {
            rotationIndex = properties.getRotationIndex();
            headings = properties.getHeadings();
            sweetSpots = properties.getSweetSpots();
        } else {
            LOG.warn("VCMProperties == null. Use default values.");
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ImageIcon getIconImage() {
        return null;
    }

    @Override
    public String getType() {
        return "VirtualCityMap Steuerung";
    }

    @Override
    public JComponent getInfoComponent(final Refreshable r) {
        return null;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    @Override
    public String getName() {
        return "VirtualCityMap Steuerung";
    }

    @Override
    public Collection<PNode> provideChildren(final PFeature parent) {
        if (children.isEmpty()) {
            initPNodeChildren(parent);
        }
        return children;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    private void initPNodeChildren(final PFeature parent) {
        children.add(createArrow(parent));
        children.add(createMover(parent));
        children.add(createRotateArea(parent));
        children.add(createLinkArea(parent));
        children.add(createCloseArea(parent));
        rotate();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private DerivedMoveArea createMover(final PFeature parent) {
        final DerivedMoveArea mover = new DerivedMoveArea(parent);
        return mover;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private FixedPImage createArrow(final PFeature parent) {
        arrow = new DerivedFixedPImage(ARROW, parent, new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        return in.getCentroid();
                    }
                });
        arrow.setSweetSpotX(0.5d);
        arrow.setSweetSpotY(0d);

        return arrow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private FixedPImage createRotateArea(final PFeature parent) {
        final DerivedFixedPImageCommandArea rotateArea = new DerivedFixedPImageCommandArea(
                ROTATE,
                parent,
                new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        final Coordinate[] cs = in.buffer(in.getEnvelopeInternal().getHeight() * (-0.10))
                                        .getEnvelope()
                                        .getCoordinates();
                        final GeometryFactory factory = new GeometryFactory(
                                new PrecisionModel(),
                                CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));
                        final Point point = factory.createPoint(cs[3]);
                        return point;
                    }
                }) {

                @Override
                public void mousePressed(final PInputEvent event) {
                    VCMControlFeature.this.rotate();
                }
            };
        rotateArea.setSweetSpotX(0.5d);
        rotateArea.setSweetSpotY(0.5d);

        return rotateArea;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private FixedPImage createLinkArea(final PFeature parent) {
        final DerivedFixedPImageCommandArea linkArea = new DerivedFixedPImageCommandArea(
                OPENVCM,
                parent,
                new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        final Coordinate[] cs = in.buffer(in.getEnvelopeInternal().getHeight() * (-0.10))
                                        .getEnvelope()
                                        .getCoordinates();
                        final GeometryFactory factory = new GeometryFactory(
                                new PrecisionModel(),
                                CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));
                        final Point point = factory.createPoint(cs[0]);
                        return point;
                    }
                }) {

                @Override
                public void mousePressed(final PInputEvent event) {
                    openVCM();
                }
            };
        linkArea.setSweetSpotX(0.5d);
        linkArea.setSweetSpotY(0.5d);

        return linkArea;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   parent  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private FixedPImage createCloseArea(final PFeature parent) {
        final DerivedFixedPImageCommandArea closeArea = new DerivedFixedPImageCommandArea(
                REMOVE,
                parent,
                new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        final Coordinate[] cs = in.buffer(in.getEnvelopeInternal().getHeight() * (-0.10))
                                        .getEnvelope()
                                        .getCoordinates();
                        final GeometryFactory factory = new GeometryFactory(
                                new PrecisionModel(),
                                CrsTransformer.extractSridFromCrs(CismapBroker.getInstance().getSrs().getCode()));
                        final Point point = factory.createPoint(cs[2]);
                        return point;
                    }
                }) {

                @Override
                public void mousePressed(final PInputEvent event) {
                    removeFeature();
                }
            };
        closeArea.setSweetSpotX(0.5d);
        closeArea.setSweetSpotY(0.5d);

        return closeArea;
    }

    /**
     * DOCUMENT ME!
     */
    public void removeFeature() {
        CismapBroker.getInstance().getMappingComponent().getFeatureCollection().removeFeature(this);
    }

    /**
     * DOCUMENT ME!
     */
    public void openVCM() {
        if (properties.isEmpty()) {
            LOG.warn("openVCM openVCM(). properties are empty. you should check this server_resource: "
                        + WundaBlauServerResources.VCM_PROPERTIES.getValue());
            LOG.info("trying to load the properties from server_resource");
            properties.load(getConnectionContext());
        }

        final Point point = getGeometry().getCentroid();
        final double distance = CrsTransformer.transformToMetricCrs(getGeometry()).getEnvelopeInternal().getHeight()
                    * 1.10;
        final String user = properties.getUser();
        final String password = properties.getPassword();
        final double groundPosX = point.getX();
        final double groundPosY = point.getY();
        final double groundPosZ = 192.2062;
        final int heading = headings[rotationIndex];
        final double camPosX = groundPosX;
        final double camPosY = groundPosY;
        final double camPosZ = groundPosZ + distance;
        final int currentSrid = CrsTransformer.extractSridFromCrs(mappingComponent.getMappingModel().getSrs()
                        .getCode());
        final int epsg = currentSrid;

        final String url = String.format(
                properties.getUrlTemplate(),
                user,
                password,
                groundPosX
                        + "",
                groundPosY
                        + "",
                groundPosZ
                        + "",
                distance
                        + "",
                heading
                        + "",
                camPosX
                        + "",
                camPosY
                        + "",
                camPosZ
                        + "",
                epsg
                        + "");
        try {
            de.cismet.tools.BrowserLauncher.openURL(url);
        } catch (Exception ex) {
            LOG.error("error while browserlaunching url: " + url, ex);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void rotate() {
        if (rotationIndex >= 3) {
            rotationIndex = 0;
        } else {
            rotationIndex += 1;
        }
        final ImageIcon rotated = Static2DTools.rotate(ARROWII, headings[rotationIndex], false);
        arrow.setImage(rotated.getImage());
        arrow.setSweetSpotX(sweetSpots[rotationIndex][0]);
        arrow.setSweetSpotY(sweetSpots[rotationIndex][1]);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public class DerivedMoveArea extends DerivedCommandArea {

        //~ Instance fields ----------------------------------------------------

        private PFeature parentFeature;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DerivedCommandArea object.
         *
         * @param  parent  DOCUMENT ME!
         */
        public DerivedMoveArea(final PFeature parent) {
            super(parent, new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        return getGeometry().buffer(in.getEnvelopeInternal().getHeight() * (-0.05));
                    }
                });
            setPaint(Color.white);
            setStroke(null);
            setTransparency(0.3f);
            parentFeature = parent;
        }

        //~ Methods ------------------------------------------------------------

        // Moving
        @Override
        public void mousePressed(final PInputEvent event) {
            super.mousePressed(event);
            ((PBasicInputEventHandler)mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON)).mousePressed(
                event);
        }

        @Override
        public void mouseDragged(final PInputEvent event) {
            super.mouseDragged(event);
            ((PBasicInputEventHandler)mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON)).mouseDragged(
                event);
        }

        @Override
        public void mouseReleased(final PInputEvent event) {
            super.mouseReleased(event);

            ((PBasicInputEventHandler)mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON)).mouseReleased(
                event);
            mappingComponent.ensureVisibilityOfSpecialFeatures(VCMControlFeature.class, true);
        }

        @Override
        public void mouseMoved(final PInputEvent event) {
            super.mouseMoved(event);
//                mappingComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            ((PBasicInputEventHandler)mappingComponent.getInputListener(MappingComponent.MOVE_POLYGON)).mouseMoved(
                event);
        }
    }
}
