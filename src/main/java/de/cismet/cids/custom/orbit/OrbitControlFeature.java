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
package de.cismet.cids.custom.orbit;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.virtualcitymap.*;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.ChildNodesProvider;
import de.cismet.cismap.commons.features.DefaultFeatureCollection;
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

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class OrbitControlFeature extends DefaultStyledFeature implements XStyledFeature,
    ChildNodesProvider,
    RequestForUnaddableHandles,
    RequestForUnmoveableHandles,
    RequestForUnremovableHandles,
    RequestForHidingHandles,
    RequestForRotatingPivotLock,
    RequestForNonreflectingFeature,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrbitControlFeature.class);

    static final Image OPENORBIT = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/orbitviewer/orbit32.png")).getImage();
    static final Image ROTATE = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/turn.png")).getImage();
    static final Image REMOVE = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/remove.png")).getImage();

    private static final int ARCSIZE = 200;

    //~ Instance fields --------------------------------------------------------

    ArrayList<PNode> children = new ArrayList<>();
    private final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
    private final VCMProperties properties = VCMProperties.getInstance();
    private final ConnectionContext connectionContext;

    private final int[] headings = new int[] { 0, 45, 90, 135, 180, 225, 270, 315 };
    private FixedPImage arrow;
    private CamState camState = new CamState();

    private String socketChannelId;
    private String launcherUrl;
    private StacResult stacInfo;
    private Socket socket;

    private final ObjectMapper mapper = new ObjectMapper();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VCMControlFeature object.
     *
     * @param  connectionContext  DOCUMENT ME!
     * @param  stac               stacResult
     * @param  socket             DOCUMENT ME!
     * @param  launcherUrl        DOCUMENT ME!
     */
    public OrbitControlFeature(final ConnectionContext connectionContext,
            final StacResult stac,
            final Socket socket,
            final String launcherUrl) {
        this.connectionContext = connectionContext;
        setEditable(true);
        setCanBeSelected(true);

        setLinePaint(new Color(0, 0, 0, 0));
        setHighlightingEnabled(false);

        this.stacInfo = stac;
        this.socket = socket;
        this.launcherUrl = launcherUrl;
        this.camState.setFov(100);
        this.camState.setTilt(0);
        this.camState.setPan(0);
        this.setGeometry(getControlFeatureGeometry());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Geometry getControlFeatureGeometry() {
        return getControlFeatureGeometry(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   centroid  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Geometry getControlFeatureGeometry(Point centroid) {
        final Geometry bb = mappingComponent.getCurrentBoundingBoxFromCamera()
                    .getGeometry(
                        CrsTransformer.extractSridFromCrs(
                            CismapBroker.getInstance().getSrs().getCode()));

        if (centroid == null) {
            centroid = bb.getCentroid();
        }
        final double h = bb.getEnvelopeInternal().getHeight();
        final double w = bb.getEnvelopeInternal().getWidth();

        if (h > w) {
            return centroid.buffer(w / 2 * 0.625).getEnvelope();
        } else {
            return centroid.buffer(h / 2 * 0.625).getEnvelope();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("started");

        final JFrame j = new JFrame("test");
        j.getContentPane().setLayout(new BorderLayout());
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final ImageIcon FOVVIS = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                    "/de/cismet/cids/custom/virtualcitymap/vcm.control.png"));
        final JLabel l = new JLabel("");
        final JLabel lblPan = new JLabel("");
        final JLabel lblFOV = new JLabel("");

        j.getContentPane().add(l, BorderLayout.CENTER);
        j.setVisible(true);

        final JSlider pan = new JSlider(0, 360);
        pan.setMajorTickSpacing(45);
        pan.setMinorTickSpacing(10);
        pan.setPaintTicks(true);
        pan.setPaintLabels(true);
        final JSlider fov = new JSlider(5, 130);
        fov.setMajorTickSpacing(10);
        fov.setMinorTickSpacing(5);
        fov.setPaintTicks(true);
        fov.setPaintLabels(true);
        pan.setValue(0);
        fov.setValue(80);
        l.setIcon(createArcImage(200, 200, pan.getValue(), 100, fov.getValue()));

        pan.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent e) {
                    l.setIcon(createArcImage(200, 200, pan.getValue(), 100, fov.getValue()));
                }
            });

        fov.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(final ChangeEvent e) {
                    l.setIcon(createArcImage(200, 200, pan.getValue(), 100, fov.getValue()));
                }
            });

        j.getContentPane().add(pan, BorderLayout.NORTH);
        j.getContentPane().add(fov, BorderLayout.SOUTH);
        j.getContentPane().add(l, BorderLayout.CENTER);

        j.setSize(new Dimension(500, 500));

        System.out.println("fertich");
//        System.exit(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   width   DOCUMENT ME!
     * @param   height  DOCUMENT ME!
     * @param   pan     DOCUMENT ME!
     * @param   tilt    DOCUMENT ME!
     * @param   fov     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon createArcImage(final int width, final int height, int pan, final int tilt, int fov) {
        final BufferedImage bi = new BufferedImage(200, 200, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g2 = (Graphics2D)bi.getGraphics();
        // tilt is between -90 and 90

        //
        g2.setPaint(new Color(11, 72, 107, 255));

        fov = fov + 10;
        pan = ((pan + (fov / 2) - 90) * -1) % 360;

        if (pan < 0) {
            pan = 360 + pan;
        }

        g2.fill(new Arc2D.Double(0, 0, width, height, pan, fov, Arc2D.PIE));
        return new ImageIcon(bi);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSocketChannelId() {
        return socketChannelId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  socketChannelId  DOCUMENT ME!
     */
    public void setSocketChannelId(final String socketChannelId) {
        this.socketChannelId = socketChannelId;
        if (this.socketChannelId != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("listening for fromOrbit:" + socketChannelId);
            }
            socket.on("fromOrbit:" + socketChannelId, new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        if (LOG.isDebugEnabled()) {
                            // JSONObject obj = (JSONObject)args[0];
                            LOG.debug("args on socket.io" + args[0]);
                        }
                        CamState cs = null;
                        try {
                            cs = mapper.readValue(args[0].toString(), CamState.class);
                            final Point oldCentroid = getGeometry().getCentroid();
                            if ((Math.abs(cs.getX() - oldCentroid.getX()) > 0.1)
                                        || (Math.abs(cs.getY() - oldCentroid.getY()) > 0.1)) {
                                final GeometryFactory factory = new GeometryFactory(
                                        new PrecisionModel(PrecisionModel.FLOATING),
                                        new Integer(camState.getCrs()));
                                final Geometry newGeometry = getControlFeatureGeometry(
                                        factory.createPoint(new Coordinate(cs.getX(), cs.getY())));
                                setGeometry(newGeometry);
                                ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).removeFeature(
                                    OrbitControlFeature.this);
                                ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).addFeature(
                                    OrbitControlFeature.this);
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("move it: " + cs.getX() + "," + cs.getY());
                                }
                                setCamState(cs);
                                visualizeRotation();
                                mappingComponent.ensureVisibilityOfSpecialFeatures(OrbitControlFeature.class, true);
                            } else {
                                setCamState(cs);
                                visualizeRotation();
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(
                                        "pan,tilt, fov it: "
                                                + cs.getPan()
                                                + ", "
                                                + cs.getTilt()
                                                + ", "
                                                + cs.getFov());
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void publishCamState() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  camState  DOCUMENT ME!
     */
    public void setCamState(final CamState camState) {
        this.camState = camState;
    }

    /**
     * DOCUMENT ME!
     */
    public void updateCamStatePosition() {
        this.camState.setX(this.getGeometry().getCentroid().getX());
        this.camState.setY(this.getGeometry().getCentroid().getY());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLaunchUrl() {
        updateCamStatePosition();
        return launcherUrl + "/?cidsOrbitSTAC="
                    + stacInfo.getStac()
                    + "&initialx="
                    + camState.getX()
                    + "&initialy="
                    + camState.getY()
                    + "&initialPan=" + camState.getPan() + "&initialTilt=" + camState.getTilt() + "&initialFOV="
                    + camState.getFov();
    }

    @Override
    public ImageIcon getIconImage() {
        return null;
    }

    @Override
    public String getType() {
        return "OrbitViewer Steuerung";
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
        return "OrbitViewer Steuerung";
    }

    @Override
    public Collection<PNode> provideChildren(final PFeature parent) {
//        if (children.isEmpty()) {
        children.clear();
        initPNodeChildren(parent);
//        }
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
        final ImageIcon rotated = createArcImage(
                ARCSIZE,
                ARCSIZE,
                (int)(camState.getPan() + 0.5),
                (int)(camState.getTilt() + 0.5),
                (int)(camState.getFov() + 0.5));

        arrow = new DerivedFixedPImage(rotated.getImage(), parent, new DeriveRule() {

                    @Override
                    public Geometry derive(final Geometry in) {
                        return in.getCentroid();
                    }
                });
        arrow.setSweetSpotX(0.5d);
        arrow.setSweetSpotY(0.5d);

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
                    OrbitControlFeature.this.rotate();
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
                OPENORBIT,
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
                    openOrbitLauncher();
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
        mappingComponent.getFeatureCollection().removeFeature(this);
    }

    /**
     * DOCUMENT ME!
     */
    public void openOrbitLauncher() {
        final int currentSrid = CrsTransformer.extractSridFromCrs(mappingComponent.getMappingModel().getSrs()
                        .getCode());
        final int epsg = currentSrid;

        final String url = getLaunchUrl();

        final String channel = getSocketChannelId();
        if (channel == null) {
            try {
                de.cismet.tools.BrowserLauncher.openURL(url);
                setSocketChannelId(stacInfo.getSocketChannelId());
            } catch (Exception ex) {
                LOG.error("error while browserlaunching url: " + url, ex);
            }
        } else {
            try {
                socket.emit(
                    "toOrbit:"
                            + getSocketChannelId(),
                    mapper.writeValueAsString(camState));
            } catch (Exception e) {
                e.printStackTrace();
                setSocketChannelId("");
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void rotate() {
//        rotationIndex = (rotationIndex + 1) % headings.length;
        double np = this.camState.getPan();
        np = (np - (np % 45) + 45) % 360;
        this.camState.setPan(np);
        updateCamStatePosition();
        visualizeRotation();
        updateOrbitIfPossible();
    }

    /**
     * DOCUMENT ME!
     */
    public void visualizeRotation() {
        final ImageIcon rotated = createArcImage(
                ARCSIZE,
                ARCSIZE,
                (int)(camState.getPan() + 0.5),
                (int)(camState.getTilt() + 0.5),
                (int)(camState.getFov() + 0.5));
        arrow.setImage(rotated.getImage());
        arrow.setSweetSpotX(0.5d);
        arrow.setSweetSpotY(0.5d);
        CismapBroker.getInstance().getMappingComponent().repaint();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateOrbitIfPossible() {
        final String channel = getSocketChannelId();
        if (channel != null) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("toOrbit:" + getSocketChannelId() + "  " + mapper.writeValueAsString(camState));
                }
                socket.emit(
                    "toOrbit:"
                            + getSocketChannelId(),
                    mapper.writeValueAsString(camState));
            } catch (Exception e) {
                e.printStackTrace();
                setSocketChannelId("");
            }
        }
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
            mappingComponent.ensureVisibilityOfSpecialFeatures(OrbitControlFeature.class, true);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Geometry changed" + parentFeature.getFeature().getGeometry());
            }
            updateCamStatePosition();
            updateOrbitIfPossible();
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
