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

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.utils.PotenzialflaecheReportCreator;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.Crs;
import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

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
public class PfMapFactory extends MapImageFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PfMapFactory.class);
    private static final double PPI = 72.156d;
    private static final double METERS_TO_INCH_FACTOR = 0.0254d;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected PotenzialflaecheReportCreator.MapConfiguration extractConfiguration(final String configuration)
            throws Exception {
        return new ObjectMapper().readValue(configuration, PotenzialflaecheReportCreator.MapConfiguration.class);
    }

    @Override
    protected BufferedImage generateMap(final MapImageFactoryConfiguration configuration) throws Exception {
        if (configuration instanceof PotenzialflaecheReportCreator.MapConfiguration) {
            return generateMap((PotenzialflaecheReportCreator.MapConfiguration)configuration);
        } else {
            throw new Exception("wrong configuration format");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   config  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected BufferedImage generateMap(final PotenzialflaecheReportCreator.MapConfiguration config) throws Exception {
        final File file = config.getFileFromCache();
        if (Boolean.TRUE.equals(config.getUseCache())) {
            if ((file != null) && file.exists() && file.isFile()) {
                return ImageIO.read(file);
            }
        }

        final MetaClass mc = CidsBean.getMetaClassFromTableName(
                "WUNDA_BLAU",
                "PF_POTENZIALFLAECHE",
                getConnectionContext());

        final Collection<Feature> features = new ArrayList();
        for (final Integer id : config.getIds()) {
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

        final int mapHeight = config.getHeight();
        final int mapWidth = config.getWidth();

        final XBoundingBox boundingBox = genBoundingBox(features, config.getBuffer(), config.getSrs());
        final double scaleDenominator = getScaleDenom(
                mapWidth,
                mapHeight,
                boundingBox.getWidth(),
                boundingBox.getHeight());

        final String mapUrl = config.getMapUrl();
        final SimpleWMS simpleWms = new SimpleWMS(new SimpleWmsGetMapUrl(mapUrl));
        final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
        mapProvider.setCenterMapOnResize(true);
        mapProvider.setCrs(new Crs(config.getSrs(), "", "", true, true));
        mapProvider.addLayer(simpleWms);

        if (Boolean.TRUE.equals(config.getShowGeom())) {
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

        final int mapDPI = config.getMapDpi();

        mapProvider.setFeatureResolutionFactor(mapDPI);
        final BufferedImage image = (BufferedImage)mapProvider.getImageAndWait((int)PPI, mapDPI, mapWidth, mapHeight);
        try {
            if ((file != null) && (file.getParentFile() != null)) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdir();
                }
                ImageIO.write(image, "png", file);
            }
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
        return image;
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
            Color color = Color.RED;
            final String colorString = (String)pfBean.getProperty("kampagne.colorcode");
            if (colorString != null) {
                try {
                    color = Color.decode(colorString);
                } catch (final Exception ex) {
                    LOG.warn("color code couldn't be decoded, falling back to default color (red)", ex);
                }
            }

            final DefaultStyledFeature feature = new DefaultStyledFeature();
            feature.setGeometry(geom);
            feature.setHighlightingEnabled(true);
            feature.setLinePaint(color);
            feature.setFillingPaint(new Color(color.getRed(), color.getGreen(), color.getBlue(), 127));
            feature.setLineWidth(3);
            return new CidsFeature(pfBean.getMetaObject());
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
     * @param   buffer    buffer DOCUMENT ME!
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
