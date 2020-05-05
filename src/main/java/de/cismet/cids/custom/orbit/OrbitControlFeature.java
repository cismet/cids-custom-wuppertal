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

import Sirius.navigator.connection.SessionManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import io.socket.client.IO;
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
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.wunda_blau.search.actions.GetOrbitStacAction;

import de.cismet.cids.server.actions.ServerActionParameter;

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
import de.cismet.cismap.commons.features.RequestNoAutoSelectionWhenMoving;
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
    ConnectionContextProvider,
    RequestNoAutoSelectionWhenMoving {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrbitControlFeature.class);

    private static final Image OPENORBIT = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/orbitviewer/orbit32.png")).getImage();
    private static final Image ROTATE = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/turn.png")).getImage();
    private static final Image REMOVE = new javax.swing.ImageIcon(OrbitControlFeature.class.getResource(
                "/de/cismet/cids/custom/virtualcitymap/remove.png")).getImage();

    private static final int ARCSIZE = 200;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static OrbitControlFeature CURRENT_CONTROL_FEATURE = null;

    //~ Instance fields --------------------------------------------------------

    DerivedFixedPImageCommandArea linkAreaRef = null;

    private final ArrayList<PNode> children = new ArrayList<>();
    private final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
    private final ConnectionContext connectionContext;

//    private final int[] headings = new int[] { 0, 45, 90, 135, 180, 225, 270, 315 };
    private FixedPImage arrow;
    private CamState camState = new CamState();

    private String socketChannelId;
    private final String launcherUrl;
    private final StacResult stacInfo;
    private final Socket socket;
    private boolean browserOpened = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OrbitControlFeature object.
     *
     * @param  connectionContext  DOCUMENT ME!
     * @param  stac               DOCUMENT ME!
     * @param  socket             DOCUMENT ME!
     * @param  launcherUrl        DOCUMENT ME!
     */
    public OrbitControlFeature(final ConnectionContext connectionContext,
            final StacResult stac,
            final Socket socket,
            final String launcherUrl) {
        this(connectionContext, stac, socket, launcherUrl, null);
    }

    /**
     * Creates a new OrbitControlFeature object.
     *
     * @param  connectionContext  DOCUMENT ME!
     * @param  stac               DOCUMENT ME!
     * @param  socket             DOCUMENT ME!
     * @param  launcherUrl        DOCUMENT ME!
     * @param  centroid           DOCUMENT ME!
     */
    public OrbitControlFeature(final ConnectionContext connectionContext,
            final StacResult stac,
            final Socket socket,
            final String launcherUrl,
            final Point centroid) {
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
        this.setSocketChannelId(stacInfo.getSocketChannelId());
        this.setGeometry(getControlFeatureGeometry(centroid));
    }

    /**
     * Creates a new VCMControlFeature object.
     *
     * @param  connectionContext  DOCUMENT ME!
     * @param  stac               stacResult
     * @param  socket             DOCUMENT ME!
     * @param  launcherUrl        DOCUMENT ME!
     * @param  centroid           DOCUMENT ME!
     * @param  fov                DOCUMENT ME!
     * @param  tilt               DOCUMENT ME!
     * @param  pan                DOCUMENT ME!
     */
    public OrbitControlFeature(final ConnectionContext connectionContext,
            final StacResult stac,
            final Socket socket,
            final String launcherUrl,
            final Point centroid,
            final float fov,
            final float tilt,
            final float pan) {
        this.connectionContext = connectionContext;
        setEditable(true);
        setCanBeSelected(true);

        setLinePaint(new Color(0, 0, 0, 0));
        setHighlightingEnabled(false);

        this.stacInfo = stac;
        this.socket = socket;
        this.launcherUrl = launcherUrl;
        this.camState.setFov(fov);
        this.camState.setTilt(tilt);
        this.camState.setPan(pan);
        this.setSocketChannelId(stacInfo.getSocketChannelId());
        this.setGeometry(getControlFeatureGeometry(centroid));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isBrowserOpened() {
        return browserOpened;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  browserOpened  DOCUMENT ME!
     */
    public void setBrowserOpened(final boolean browserOpened) {
        this.browserOpened = browserOpened;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static OrbitControlFeature getCurrentControlFeature() {
        return CURRENT_CONTROL_FEATURE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void addToMap(final ConnectionContext connectionContext) {
        addToMap(null, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  position           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void controlOrAddOnMap(final Point position, final ConnectionContext connectionContext) {
        controlOrAddOnMap(position, connectionContext, null, null, null, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  position           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     * @param  fov                DOCUMENT ME!
     * @param  pan                DOCUMENT ME!
     * @param  tilt               DOCUMENT ME!
     * @param  reason             DOCUMENT ME!
     * @param  additionalInfo     DOCUMENT ME!
     */
    public static void controlOrAddOnMap(final Point position,
            final ConnectionContext connectionContext,
            final Double fov,
            final Double pan,
            final Double tilt,
            final String reason,
            final String additionalInfo) {
        if ((CURRENT_CONTROL_FEATURE != null) && (CURRENT_CONTROL_FEATURE.socketChannelId != null)
                    && (CismapBroker.getInstance().getMappingComponent().getPFeatureHM().get(CURRENT_CONTROL_FEATURE)
                        != null)) {
            CURRENT_CONTROL_FEATURE.setGeometry(CURRENT_CONTROL_FEATURE.getControlFeatureGeometry(position));
//            CismapBroker.getInstance()
//                    .getMappingComponent()
//                    .getFeatureCollection()
//                    .removeFeature(CURRENT_CONTROL_FEATURE);
//            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(CURRENT_CONTROL_FEATURE);

            CURRENT_CONTROL_FEATURE.updateCamStatePosition();

            if (fov != null) {
                CURRENT_CONTROL_FEATURE.camState.setFov(fov);
            }
            if (pan != null) {
                CURRENT_CONTROL_FEATURE.camState.setPan(pan);
            }
            if (tilt != null) {
                CURRENT_CONTROL_FEATURE.camState.setTilt(tilt);
            }
            if (reason != null) {
                CURRENT_CONTROL_FEATURE.camState.setReason(reason);
            }
            if (additionalInfo != null) {
                CURRENT_CONTROL_FEATURE.camState.setAdditionalInfo(additionalInfo);
            }
            CURRENT_CONTROL_FEATURE.updateOrbitIfPossible();
            CismapBroker.getInstance().getMappingComponent().getPFeatureHM().get(CURRENT_CONTROL_FEATURE).visualize();
            CURRENT_CONTROL_FEATURE.visualizeRotation();
        } else {
            addToMap(position, connectionContext, fov, pan, tilt, reason, additionalInfo);
        }
    }
    /**
     * DOCUMENT ME!
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void controlOrAddOnMap(final ConnectionContext connectionContext) {
        controlOrAddOnMap(null, connectionContext);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  position           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void addToMap(final Point position, final ConnectionContext connectionContext) {
        addToMap(position, connectionContext, null, null, null, null, null);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   centroid           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     * @param   fov                DOCUMENT ME!
     * @param   pan                DOCUMENT ME!
     * @param   tilt               DOCUMENT ME!
     * @param   reason             DOCUMENT ME!
     * @param   additionalInfo     DOCUMENT ME!
     *
     * @throws  RuntimeException  DOCUMENT ME!
     */
    public static void addToMap(final Point centroid,
            final ConnectionContext connectionContext,
            final Double fov,
            final Double pan,
            final Double tilt,
            final String reason,
            final String additionalInfo) {
        try {
            new SwingWorker<OrbitControlFeature, Void>() {

                    @Override
                    protected OrbitControlFeature doInBackground() throws Exception {
                        final Socket socket = IO.socket(OrbitviewerProperties.getInstance().getSocketBroadcaster());
                        socket.connect();
                        final String ip = "notYet";
                        final Object ret = SessionManager.getProxy()
                                    .executeTask(
                                        GetOrbitStacAction.TASK_NAME,
                                        "WUNDA_BLAU",
                                        (Object)null,
                                        connectionContext,
                                        new ServerActionParameter<>(
                                            GetOrbitStacAction.PARAMETER_TYPE.IP.toString(),
                                            ip));

                        final String s = ret.toString();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("raw action result:" + s);
                        }

                        final StacResult stacResult = MAPPER.readValue(s, StacResult.class);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("stacResult:" + stacResult);
                        }
                        return new OrbitControlFeature(
                                connectionContext,
                                stacResult,
                                socket,
                                OrbitviewerProperties.getInstance().getLauncherUrl(),
                                centroid);
                    }

                    @Override
                    protected void done() {
                        try {
                            final OrbitControlFeature vcmf = get();
                            if (CURRENT_CONTROL_FEATURE != null) {
                                CismapBroker.getInstance()
                                        .getMappingComponent()
                                        .getFeatureCollection()
                                        .removeFeature(CURRENT_CONTROL_FEATURE);
                                CURRENT_CONTROL_FEATURE = null;
                            }
                            CURRENT_CONTROL_FEATURE = vcmf;
                            if (fov != null) {
                                CURRENT_CONTROL_FEATURE.camState.setFov(fov);
                            }
                            if (pan != null) {
                                CURRENT_CONTROL_FEATURE.camState.setPan(pan);
                            }
                            if (tilt != null) {
                                CURRENT_CONTROL_FEATURE.camState.setTilt(tilt);
                            }
                            if (reason != null) {
                                CURRENT_CONTROL_FEATURE.camState.setReason(reason);
                            }
                            if (additionalInfo != null) {
                                CURRENT_CONTROL_FEATURE.camState.setAdditionalInfo(additionalInfo);
                            }
                            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(vcmf);
                            CismapBroker.getInstance().getMappingComponent().getFeatureCollection().holdFeature(vcmf);
                            CURRENT_CONTROL_FEATURE.visualizeRotation();
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                        }
                    }
                }.execute();
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
            throw new RuntimeException(ex);
        }
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
        final BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        final Graphics2D g2 = (Graphics2D)bi.getGraphics();
        // tilt is between -90 and 90

        //
        final int[] rgba = { 11, 72, 107, 207 };
        if (OrbitviewerProperties.getInstance().getArcColorRGBA() != null) {
            final String[] rgbaFromSettings = OrbitviewerProperties.getInstance().getArcColorRGBA().split(",");
            try {
                final int r = Integer.parseInt(rgbaFromSettings[0].trim());
                final int g = Integer.parseInt(rgbaFromSettings[1].trim());
                final int b = Integer.parseInt(rgbaFromSettings[2].trim());
                final int a = Integer.parseInt(rgbaFromSettings[3].trim());

                rgba[0] = r;
                rgba[1] = g;
                rgba[2] = b;
                rgba[3] = a;
            } catch (final Exception ex) {
                LOG.warn("could not parse rgba from settings", ex);
            }
        }
        g2.setPaint(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));

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
                            cs = MAPPER.readValue(args[0].toString(), CamState.class);
                            final Point oldCentroid = getGeometry().getCentroid();
                            if ((Math.abs(cs.getX() - oldCentroid.getX()) > 0.1)
                                        || (Math.abs(cs.getY() - oldCentroid.getY()) > 0.1)) {
                                final GeometryFactory factory = new GeometryFactory(
                                        new PrecisionModel(PrecisionModel.FLOATING),
                                        new Integer(camState.getCrs()));
                                final Geometry newGeometry = getControlFeatureGeometry(
                                        factory.createPoint(new Coordinate(cs.getX(), cs.getY())));
                                setGeometry(newGeometry);
//
//                                ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).removeFeature(
//                                    OrbitControlFeature.this);
//                                ((DefaultFeatureCollection)mappingComponent.getFeatureCollection()).addFeature(
//                                    OrbitControlFeature.this);
                                CismapBroker.getInstance()
                                        .getMappingComponent()
                                        .getPFeatureHM()
                                        .get(CURRENT_CONTROL_FEATURE)
                                        .visualize();

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
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
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
        linkAreaRef = linkArea;
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
        socketChannelId = null;
        CURRENT_CONTROL_FEATURE = null;
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
        if (isBrowserOpened() == false) {
            try {
                de.cismet.tools.BrowserLauncher.openURL(url);
                setBrowserOpened(true);
                linkAreaRef.setTransparency((float)0.1);
            } catch (Exception ex) {
                LOG.error("error while browserlaunching url: " + url, ex);
            }
        } else {
            try {
                socket.emit(
                    "toOrbit:"
                            + getSocketChannelId(),
                    MAPPER.writeValueAsString(camState));
            } catch (final Exception ex) {
                LOG.error(ex, ex);
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
                    LOG.debug("toOrbit:" + getSocketChannelId() + "  " + MAPPER.writeValueAsString(camState));
                }
                socket.emit(
                    "toOrbit:"
                            + getSocketChannelId(),
                    MAPPER.writeValueAsString(camState));
            } catch (final Exception ex) {
                LOG.error(ex, ex);
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
