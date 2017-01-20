/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObject;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import org.apache.log4j.Logger;

import org.jdesktop.beansbinding.Converter;

import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreePath;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.RetrievalServiceLayer;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultXStyledFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.raster.wms.SlidableWMSServiceLayerGroup;
import de.cismet.cismap.commons.raster.wms.WMSServiceLayer;
import de.cismet.cismap.commons.wms.capabilities.Layer;
import de.cismet.cismap.commons.wms.capabilities.WMSCapabilities;
import de.cismet.cismap.commons.wms.capabilities.WMSCapabilitiesFactory;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class OabUtilities {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = Logger.getLogger(OabUtilities.class);

    public static final DateFormat COMMON_DATE_FORMAT = DateFormat.getDateInstance(DateFormat.MEDIUM);
    public static final int CRS = 25832;
    public static final String EPSG = "EPSG:" + CRS; // NOI18N

    public static final String BEAN_CLIENT_PROPERTY = "__prop_bean__";      // NOI18N
    public static final String GOTO_USEROBJECT_COMMAND = "GOTO_USEROBJECT"; // NOI18N

    public static final String OAB_GEWAESSEREINZUGSGEBIET_TABLE_NAME = "OAB_GEWAESSEREINZUGSGEBIET"; // NOI18N
    public static final String OAB_PROJEKT_TABLE_NAME = "OAB_PROJEKT";                               // NOI18N
    public static final String OAB_ZUSTAND_MASSNAHME_TABLE_NAME = "OAB_ZUSTAND_MASSNAHME";           // NOI18N
    public static final String OAB_BERECHNUNG_TABLE_NAME = "OAB_BERECHNUNG";                         // NOI18N

    public static final String OAB_PREVIEW_BACKGROUND_LAYER_CAP_PREFIX =
        "OabUtilities.previewWMS.backgroundLayer.capabilities."; // NOI18N
    public static final String OAB_PREVIEW_BACKGROUND_LAYER_NAME_PREFIX =
        "OabUtilities.previewWMS.backgroundLayer.layername.";    // NOI18N

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OabUtilities object.
     */
    private OabUtilities() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   c     DOCUMENT ME!
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ImageIcon loadImageIcon(final Class c, final String name) {
        return ImageUtilities.loadImageIcon(c.getPackage().getName().replaceAll("\\.", "/") + "/" + name, false); // NOI18N
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean          DOCUMENT ME!
     * @param   collectionProperty  DOCUMENT ME!
     * @param   listPanel           DOCUMENT ME!
     * @param   refHolderList       DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static void initGotoBeanHyperlinkList(final CidsBean sourceBean,
            final String collectionProperty,
            final JPanel listPanel,
            final EventListenerList refHolderList) {
        initGotoBeanHyperlinkList(
            sourceBean,
            collectionProperty,
            "name", // NOI18N
            null,
            null,
            new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) || !(o1.getProperty("name") instanceof String) || (o2 == null) // NOI18N
                                || !(o2.getProperty("name") instanceof String)) {                   // NOI18N
                        throw new IllegalStateException(
                            "bean without valid name [obj1="                                        // NOI18N
                                    + o1
                                    + "|obj2="                                                      // NOI18N
                                    + o2
                                    + "]");                                                         // NOI18N
                    }

                    return ((String)o1.getProperty("name")).compareTo( // NOI18N
                            (String)o2.getProperty("name"));           // NOI18N
                }
            },
            listPanel,
            refHolderList);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean          DOCUMENT ME!
     * @param   collectionProperty  DOCUMENT ME!
     * @param   displayProperty     DOCUMENT ME!
     * @param   listPanel           DOCUMENT ME!
     * @param   refHolderList       DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static void initGotoBeanHyperlinkList(final CidsBean sourceBean,
            final String collectionProperty,
            final String displayProperty,
            final JPanel listPanel,
            final EventListenerList refHolderList) {
        initGotoBeanHyperlinkList(
            sourceBean,
            collectionProperty,
            displayProperty,
            null,
            null,
            new Comparator<CidsBean>() {

                @Override
                public int compare(final CidsBean o1, final CidsBean o2) {
                    if ((o1 == null) || !(o1.getProperty(displayProperty) instanceof String)
                                || (o2 == null)
                                || !(o2.getProperty(displayProperty) instanceof String)) {
                        throw new IllegalStateException(
                            "bean without valid property: [property=" // NOI18N
                                    + displayProperty
                                    + "|obj1="                        // NOI18N
                                    + o1
                                    + "|obj2="                        // NOI18N
                                    + o2
                                    + "]");                           // NOI18N
                    }

                    return ((String)o1.getProperty(displayProperty)).compareTo((String)o2.getProperty(displayProperty));
                }
            },
            listPanel,
            refHolderList);
    }

    /**
     * Re-fetches a cidsbean if the backlink is not set (e.g. if it is part of the collection of the parent).
     *
     * @param   source            DOCUMENT ME!
     * @param   backlinkProperty  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public static CidsBean getBean(final CidsBean source, final String backlinkProperty) {
        // backlink not set due to 1:n
        if (source.getProperty(backlinkProperty) == null) {
            try {
                final MetaObject mo = source.getMetaObject();
                final MetaObject copy = SessionManager.getConnection()
                            .getMetaObject(SessionManager.getSession().getUser(),
                                mo.getID(),
                                mo.getClassID(),
                                mo.getDomain());
                return copy.getBean();
            } catch (final ConnectionException ex) {
                throw new IllegalStateException("cannot re-fetch cidsbean", ex); // NOI18N
            }
        } else {
            return source;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   btn            DOCUMENT ME!
     * @param   gotoBean       DOCUMENT ME!
     * @param   refHolderList  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static void toGotoBeanHyperlinkButton(final JButton btn,
            final CidsBean gotoBean,
            final EventListenerList refHolderList) {
        if ((btn == null) || (gotoBean == null) || (refHolderList == null)) {
            throw new IllegalArgumentException("null arguments not allowed"); // NOI18N
        }

        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("must only be called from EDT"); // NOI18N
        }

        btn.setText("<html><font color=#000099><u>" + btn.getText() + "</u></font></html>"); // NOI18N
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setHorizontalTextPosition(SwingConstants.LEFT);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(BEAN_CLIENT_PROPERTY, gotoBean);

        final ActionListener gotoL = new OabUtilities.GotoBeanListener();
        refHolderList.add(ActionListener.class, gotoL);

        btn.addActionListener(WeakListeners.create(ActionListener.class, gotoL, btn));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   name           DOCUMENT ME!
     * @param   gotoBean       DOCUMENT ME!
     * @param   refHolderList  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static JButton createGotoBeanHyperlinkButton(final String name,
            final CidsBean gotoBean,
            final EventListenerList refHolderList) {
        if ((name == null) || (gotoBean == null) || (refHolderList == null)) {
            throw new IllegalArgumentException("null arguments not allowed"); // NOI18N
        }

        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("must only be called from EDT"); // NOI18N
        }
        final JButton btn = new JButton(name);
        toGotoBeanHyperlinkButton(btn, gotoBean, refHolderList);

        return btn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean             DOCUMENT ME!
     * @param   collectionProperty     DOCUMENT ME!
     * @param   displayProperty        DOCUMENT ME!
     * @param   displayPropertyPrefix  DOCUMENT ME!
     * @param   displayPropertySuffix  DOCUMENT ME!
     * @param   sorter                 DOCUMENT ME!
     * @param   pnlList                DOCUMENT ME!
     * @param   refHolderList          DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static void initGotoBeanHyperlinkList(final CidsBean sourceBean,
            final String collectionProperty,
            final String displayProperty,
            final String displayPropertyPrefix,
            final String displayPropertySuffix,
            final Comparator<CidsBean> sorter,
            final JPanel pnlList,
            final EventListenerList refHolderList) {
        if ((sourceBean == null) || (collectionProperty == null) || (displayProperty == null) || (sorter == null)
                    || (refHolderList == null)
                    || (pnlList == null)) {
            throw new IllegalArgumentException("null arguments not allowed"); // NOI18N
        }

        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("must only be called from EDT"); // NOI18N
        }

        final List<CidsBean> c = sourceBean.getBeanCollectionProperty(collectionProperty); // NOI18N

        if ((c == null) || c.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("no collection found: [sourceBean=" + sourceBean + "|prop=" + collectionProperty + "]"); // NOI18N
            }

            return;
        }

        Collections.sort(c, sorter);

        pnlList.removeAll();
        pnlList.setLayout(new GridBagLayout());
        final String prefix = (displayPropertyPrefix == null) ? "" : displayPropertyPrefix; // NOI18N
        final String suffix = (displayPropertySuffix == null) ? "" : displayPropertySuffix; // NOI18N

        for (int i = 0; i < c.size(); ++i) {
            final CidsBean bean = c.get(i);
            final JButton btn = createGotoBeanHyperlinkButton(prefix + bean.getProperty(displayProperty) + suffix,
                    bean,
                    refHolderList);

            final GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.insets = new Insets(5, 5, 5, 5);

            pnlList.add(btn, constraints);
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.gridx = 0;
        constraints.gridy = c.size();
        constraints.weighty = 1;
        pnlList.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, 32767)), constraints);

        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        pnlList.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0)), constraints);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  sourceBean      DOCUMENT ME!
     * @param  geomProperty    DOCUMENT ME!
     * @param  map             DOCUMENT ME!
     * @param  titleComponent  DOCUMENT ME!
     */
    public static void initPreviewMap(final CidsBean sourceBean,
            final String geomProperty,
            final MappingComponent map,
            final JLabel titleComponent) {
        initPreviewMap(sourceBean, geomProperty, map, titleComponent, null, (RetrievalServiceLayer[])null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   sourceBean        DOCUMENT ME!
     * @param   geomProperty      DOCUMENT ME!
     * @param   map               DOCUMENT ME!
     * @param   titleComponent    DOCUMENT ME!
     * @param   mapClickAction    DOCUMENT ME!
     * @param   additionalLayers  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     * @throws  IllegalStateException     DOCUMENT ME!
     */
    public static void initPreviewMap(final CidsBean sourceBean,
            final String geomProperty,
            final MappingComponent map,
            final JLabel titleComponent,
            final Action mapClickAction,
            final RetrievalServiceLayer... additionalLayers) {
        if ((sourceBean == null) || (geomProperty == null) || (map == null)) {
            throw new IllegalArgumentException("null arguments not allowed"); // NOI18N
        }

        final Geometry geom = (Geometry)sourceBean.getProperty(geomProperty);

        if (geom == null) {
            throw new IllegalStateException("null geometry: [bean=" // NOI18N
                        + sourceBean
                        + "|geomProperty=" // NOI18N
                        + geomProperty
                        + "]");            // NOI18N
        }

        final XBoundingBox bbox = new XBoundingBox(geom.getEnvelope().buffer(50d), OabUtilities.EPSG, true);
        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        mappingModel.setSrs(new Crs(OabUtilities.EPSG, OabUtilities.EPSG, OabUtilities.EPSG, true, true));
        mappingModel.addHome(bbox);

        final List<RetrievalServiceLayer> backgroundLayers = createBackgroupLayers();

        if (backgroundLayers != null) {
            for (final RetrievalServiceLayer backgroundLayer : backgroundLayers) {
                if (backgroundLayer != null) {
                    mappingModel.addLayer(backgroundLayer);
                }
            }
        }

        if (additionalLayers != null) {
            for (final RetrievalServiceLayer additionalLayer : additionalLayers) {
                if (additionalLayer != null) {
                    mappingModel.addLayer(additionalLayer);
                }
            }
        }

        map.setMappingModel(mappingModel);
        map.gotoInitialBoundingBox();
        final DefaultXStyledFeature feature = new DefaultXStyledFeature(
                null,
                "Geometrie", // NOI18N
                "Polygon", // NOI18N
                null,
                new BasicStroke());
        feature.setFillingPaint(Color.RED);
        feature.setTransparency(0.2f);
        feature.setGeometry(geom);
        map.getFeatureCollection().addFeature(feature);

        map.unlock();
        map.addCustomInputListener(GOTO_USEROBJECT_COMMAND, new PBasicInputEventHandler() { // NOI18N

                @Override
                public void mouseClicked(final PInputEvent evt) {
                    if (evt.getClickCount() > 1) {
                        if ((mapClickAction != null) && mapClickAction.isEnabled()) {
                            mapClickAction.actionPerformed(
                                new ActionEvent(sourceBean, ActionEvent.ACTION_PERFORMED, GOTO_USEROBJECT_COMMAND));
                        }
                    }
                }
            });
        map.setInteractionMode(GOTO_USEROBJECT_COMMAND); // NOI18N
        map.gotoInitialBoundingBox();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<RetrievalServiceLayer> createBackgroupLayers() {
        boolean layerfound = true;
        int i = 0;
        final List<RetrievalServiceLayer> mapServices = new ArrayList<RetrievalServiceLayer>();
        while (layerfound) {
            // starting with one
            i++;

            String capLink = null;
            String name = null;
            try {
                capLink = NbBundle.getMessage(OabUtilities.class, OAB_PREVIEW_BACKGROUND_LAYER_CAP_PREFIX + i);
                name = NbBundle.getMessage(OabUtilities.class, OAB_PREVIEW_BACKGROUND_LAYER_NAME_PREFIX + i);
            } catch (final MissingResourceException ex) {
                // noop, the layer is simply not there
            }

            if ((capLink == null) || (name == null)) {
                if (log.isDebugEnabled()) {
                    log.debug("no link or name found: [capLink=" + capLink + "|name=" + name + "|capProp=" // NOI18N
                                + OAB_PREVIEW_BACKGROUND_LAYER_CAP_PREFIX + i + "|nameProp=" // NOI18N
                                + OAB_PREVIEW_BACKGROUND_LAYER_NAME_PREFIX + i + "]"); // NOI18N
                }
                layerfound = false;
            } else {
                final RetrievalServiceLayer layer = createWMSLayer(capLink, name);
                if (layer != null) {
                    mapServices.add(layer);
                }
            }
        }

        return mapServices;
    }

    /**
     * Creates a <code>RetrievalServiceLayer</code> from a wms capabilities link and a layername. Returns <code>
     * null</code> if the wms capabilities do not list a layer with the corresponding name. Throws <code>
     * IllegalArgumentException</code> if a {@link WMSCapabilities} object cannot be created from the provided link for
     * any reason.
     *
     * @param   capabilities  DOCUMENT ME!
     * @param   layername     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static RetrievalServiceLayer createWMSLayer(final String capabilities, final String layername) {
        if ((capabilities == null) || (layername == null)) {
            throw new IllegalArgumentException("no argument must be null"); // NOI18N
        }

        final WMSCapabilitiesFactory capFactory = new WMSCapabilitiesFactory();
        final WMSCapabilities caps;
        try {
            caps = capFactory.createCapabilities(capabilities);
        } catch (final Exception ex) {
            log.warn("improper capabilities link: " + capabilities, ex); // NOI18N
            return null;
        }

        final Layer rootLayer = caps.getLayer();
        TreePath layerPath = new TreePath(rootLayer);
        if (!layername.equals(rootLayer.getName())) {
            layerPath = findLayer(layerPath, layername);
        }

        final RetrievalServiceLayer service;
        if (layerPath == null) {
            service = null;
        } else {
            final List path = new ArrayList(1);
            path.add(layerPath);

            if (isSlidableWMSLayer((Layer)layerPath.getLastPathComponent())) {
                // a slidable layergroup
                service = new SlidableWMSServiceLayerGroup(path);
                ((SlidableWMSServiceLayerGroup)service).setWmsCapabilities(caps);
            } else {
                service = new WMSServiceLayer(path);
                ((WMSServiceLayer)service).setWmsCapabilities(caps);
            }
        }

        return service;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   layer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public static boolean isSlidableWMSLayer(final Layer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("layer must not be null"); // NOI18N
        }

        boolean slidable = layer.getName().endsWith("[]"); // NOI18N

        if (!slidable) {
            final String[] keywords = layer.getKeywords();
            if (keywords != null) {
                for (int i = 0; (i < keywords.length) && !slidable; ++i) {
                    slidable = "cismapSlidingLayerGroup".equals(keywords[i]); // NOI18N
                }
            }
        }

        return slidable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   path       DOCUMENT ME!
     * @param   layername  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static TreePath findLayer(final TreePath path, final String layername) {
        final Layer[] layers = ((Layer)path.getLastPathComponent()).getChildren();

        TreePath result = null;

        for (int i = 0; (i < layers.length) && (result == null); ++i) {
            final TreePath newPath = path.pathByAddingChild(layers[i]);
            if (layername.equals(layers[i].getName())) {
                result = newPath;
            } else {
                result = findLayer(newPath, layername);
            }
        }

        return result;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class DateToStringConverter extends Converter<Date, String> {

        //~ Methods ------------------------------------------------------------

        @Override
        public String convertForward(final Date s) {
            return OabUtilities.COMMON_DATE_FORMAT.format(s);
        }

        @Override
        public Date convertReverse(final String t) {
            try {
                return OabUtilities.COMMON_DATE_FORMAT.parse(t);
            } catch (final ParseException ex) {
                throw new IllegalStateException("cannot parse previously converted date", ex); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static final class GotoBeanListener implements ActionListener {

        //~ Static fields/initializers -----------------------------------------

        private static final Logger log = Logger.getLogger(GotoBeanListener.class);

        //~ Methods ------------------------------------------------------------

        @Override
        public void actionPerformed(final ActionEvent e) {
            if (e.getSource() instanceof JComponent) {
                final JComponent source = (JComponent)e.getSource();
                final Object cp = source.getClientProperty(BEAN_CLIENT_PROPERTY);
                if (cp instanceof CidsBean) {
                    ComponentRegistry.getRegistry()
                            .getDescriptionPane()
                            .gotoMetaObject(((CidsBean)cp).getMetaObject(), ((CidsBean)cp).toString());
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("source does not contain bean client property: " + source); // NOI18N
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("source is no jcomponent: " + e.getSource());                   // NOI18N
                }
            }
        }
    }
}
