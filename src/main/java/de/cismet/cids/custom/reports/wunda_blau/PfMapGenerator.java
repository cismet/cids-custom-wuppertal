/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.reports.wunda_blau;

/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.tools.MapImageFactory;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import Sirius.util.MapImageFactoryConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.StringReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction.PfMapConfiguration;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfMapGenerator extends MapImageFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfMapGenerator.class);
    private static final double PPI = 72.156d;
    private static final double METERS_TO_INCH_FACTOR = 0.0254d;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected MapImageFactoryConfiguration extractConfiguration(final String configuration) throws Exception {
        return new ObjectMapper().readValue(configuration, PfMapConfiguration.class);
    }

    @Override
    protected BufferedImage generateMap(final MapImageFactoryConfiguration configuration) throws Exception {
        if (configuration instanceof PfMapConfiguration) {
            return generateMap((PfMapConfiguration)configuration);
        } else {
            throw new Exception("wrong configuration format");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   configuration  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected BufferedImage generateMap(final PfMapConfiguration configuration) throws Exception {
        final Properties properties = getProperties(
                WundaBlauServerResources.POTENZIALFLAECHEN_MAPFACTORY_PROPERTIES,
                getConnectionContext());

        final MetaClass mc;
        if ((configuration.getType() == PfMapConfiguration.Type.PF_ORTHO)
                    || (configuration.getType() == PfMapConfiguration.Type.PF_DGK)) {
            mc = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "PF_POTENZIALFLAECHE", getConnectionContext());
        } else {
            return null;
        }

        final Collection<Feature> features = new ArrayList();
        for (final Integer id : configuration.getIds()) {
            final MetaObject mo = SessionManager.getProxy()
                        .getMetaObject(
                            SessionManager.getSession().getUser(),
                            id,
                            mc.getId(),
                            "WUNDA_BLAU",
                            getConnectionContext());

            final Feature feature = createPfFeature((mo != null) ? mo.getBean() : null);
            if (feature != null) {
                features.add(feature);
            }
        }

        final int mapHeight = configuration.getHeight();
        final int mapWidth = configuration.getWidth();

        final XBoundingBox boundingBox = genBoundingBox(features, configuration.getBuffer(), configuration.getSrs());
        final double scaleDenominator = getScaleDenom(
                mapWidth,
                mapHeight,
                boundingBox.getWidth(),
                boundingBox.getHeight());

        final String mapUrl = properties.getProperty("mapUrl_" + configuration.getType().name());
        final SimpleWMS simpleWms = new SimpleWMS(new SimpleWmsGetMapUrl(mapUrl));
        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.setCenterMapOnResize(true);
        mapProvider.setCrs(new Crs(configuration.getSrs(), "", "", true, true));
        mapProvider.addLayer(simpleWms);

        for (final Feature feature : features) {
            mapProvider.addFeature(feature);
            if (mapProvider.getMappingComponent().getPFeatureHM().get(feature) != null) {
                final PNode annotationNode = mapProvider.getMappingComponent()
                            .getPFeatureHM()
                            .get(feature)
                            .getPrimaryAnnotationNode();
                if (annotationNode != null) {
                    final PBounds bounds = annotationNode.getBounds();
                    bounds.x = -bounds.width / 2;
                    bounds.y = -bounds.height / 2;
                    annotationNode.setBounds(bounds);
                }
            }
        }

        final double bbWidth = boundingBox.getWidth();
        final double bbHeight = boundingBox.getHeight();
        final double bbCenterX = boundingBox.getX1() + (bbWidth / 2);
        final double bbCenterY = boundingBox.getY1() + (bbHeight / 2);

        final double mapWidthInMeter = (mapWidth / PPI) * METERS_TO_INCH_FACTOR;
        final double mapHeightInMeter = (mapHeight / PPI) * METERS_TO_INCH_FACTOR;
        final double worldWidthInPx = mapWidthInMeter * scaleDenominator;
        final double worldHeightInPx = mapHeightInMeter * scaleDenominator;

        boundingBox.setX1(bbCenterX - (worldWidthInPx / 2d));
        boundingBox.setX2(bbCenterX + (worldWidthInPx / 2d));
        boundingBox.setY1(bbCenterY - (worldHeightInPx / 2d));
        boundingBox.setY2(bbCenterY + (worldHeightInPx / 2d));

        mapProvider.setBoundingBox(boundingBox);

        final int mapDPI = Integer.parseInt(properties.getProperty("mapDPI_" + configuration.getType().name()));

        mapProvider.setFeatureResolutionFactor(mapDPI);
        return (BufferedImage)mapProvider.getImageAndWait((int)PPI, mapDPI, mapWidth, mapHeight);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   serverResource     DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Properties getProperties(final WundaBlauServerResources serverResource,
            final ConnectionContext connectionContext) throws Exception {
        final User user = SessionManager.getSession().getUser();
        final Object result = SessionManager.getProxy()
                    .executeTask(
                        user,
                        GetServerResourceServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        serverResource.getValue(),
                        connectionContext);
        if (result instanceof Exception) {
            throw (Exception)result;
        } else if (result instanceof String) {
            final Properties properties = new Properties();
            properties.load(new StringReader((String)result));
            return properties;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pfBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Feature createPfFeature(final CidsBean pfBean) {
        final Geometry geom = (pfBean != null) ? (Geometry)pfBean.getProperty("geometrie.geo_field") : null;

        if (geom != null) {
            final DefaultStyledFeature feature = new DefaultStyledFeature();
            feature.setGeometry(geom);
            feature.setHighlightingEnabled(true);
            feature.setLinePaint(Color.RED);
            feature.setFillingPaint(new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 127));
            feature.setLineWidth(3);
            return feature;
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mapWidthInPx     DOCUMENT ME!
     * @param   mapHeightInPx    DOCUMENT ME!
     * @param   bbWidthInMeter   DOCUMENT ME!
     * @param   bbHeightInMeter  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static double getScaleDenom(final int mapWidthInPx,
            final int mapHeightInPx,
            final double bbWidthInMeter,
            final double bbHeightInMeter) {
        final double mapRatio = mapWidthInPx / (double)mapHeightInPx;
        final double bbRatio = bbWidthInMeter / bbHeightInMeter;

        final double mapWidthInMeter = (mapWidthInPx / PPI) * METERS_TO_INCH_FACTOR;
        final double mapHeightInMeter = (mapHeightInPx / PPI) * METERS_TO_INCH_FACTOR;

        return (bbRatio > mapRatio) ? (bbWidthInMeter / mapWidthInMeter) : (bbHeightInMeter / mapHeightInMeter);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   features  DOCUMENT ME!
     * @param   buffer    properties DOCUMENT ME!
     * @param   srs       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static XBoundingBox genBoundingBox(final Collection<Feature> features,
            final Integer buffer,
            final String srs) {
        int srid = CrsTransformer.extractSridFromCrs(srs);
        boolean first = true;
        final Collection<Geometry> geoms = new ArrayList<>(features.size());
        for (final Feature feature : features) {
            Geometry geometry = feature.getGeometry();

            if (geometry != null) {
                geometry = geometry.getEnvelope();

                if (first) {
                    srid = geometry.getSRID();
                    first = false;
                } else {
                    if (geometry.getSRID() != srid) {
                        geometry = CrsTransformer.transformToGivenCrs(geometry, srs);
                    }
                }

                geoms.add(geometry);
            }
        }
        final GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
        Geometry union = factory.buildGeometry(geoms);
        if (union instanceof GeometryCollection) {
            union = ((GeometryCollection)union).union();
        }
        final Geometry boxGeom;
        if (buffer != null) {
            boxGeom = union.buffer(buffer);
        } else {
            boxGeom = union;
        }
        boxGeom.setSRID(srid);
        return new XBoundingBox(boxGeom);
    }
}