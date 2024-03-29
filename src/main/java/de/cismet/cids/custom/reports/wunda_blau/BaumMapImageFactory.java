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

import Sirius.server.middleware.types.MetaObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;

import lombok.Getter;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.utils.BaumMapImageFactoryConfiguration;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumMapImageFactory extends MapImageFactory<BaumMapImageFactoryConfiguration>
        implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static Map<String, String> colorMap = new HashMap<>();
    private static final Color DEFAULT_COLOR = new Color(0.5f, 0.5f, 0.5f);
    private static final String FIELD__GEOM = "fk_geom.geo_field";

    //~ Instance fields --------------------------------------------------------

    @Getter private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    protected BaumMapImageFactoryConfiguration extractConfiguration(final String confJson) throws Exception {
        return new ObjectMapper().readValue(confJson, BaumMapImageFactoryConfiguration.class);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geom   DOCUMENT ME!
     * @param   color  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private DefaultStyledFeature createPointFeature(final Geometry geom, final Color color) {
        final DefaultStyledFeature dsf = new DefaultStyledFeature();
        final Geometry polygon = BufferOp.bufferOp(geom, 0.1);
        dsf.setGeometry(polygon);
        dsf.setFillingPaint(color);
        dsf.setLineWidth(3);
        dsf.setLinePaint(color);
        return dsf;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   geom   DOCUMENT ME!
     * @param   color  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private DefaultStyledFeature createRectangleFeature(final Geometry geom, final Color color) {
        final DefaultStyledFeature dsf = new DefaultStyledFeature();
        dsf.setGeometry(geom);
        dsf.setTransparency(0.5F);
        dsf.setFillingPaint(color);
        return dsf;
    }

    @Override
    protected BufferedImage generateMap(final BaumMapImageFactoryConfiguration config) throws Exception {
        Geometry geom = null;
        final Collection<Feature> features = new ArrayList<>();

        for (final BaumMapImageFactoryConfiguration.ObjectIdentifier oi : config.getMons()) {
            final MetaObject mo = SessionManager.getProxy()
                        .getMetaObject(
                            oi.getObjectId(),
                            oi.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext);
            final CidsBean bean = mo.getBean();

            final Geometry baumGeom = (Geometry)bean.getProperty(FIELD__GEOM);
            if (baumGeom != null) {
                colorMap = config.getColorMap();
                final Color classColor = ((colorMap.containsKey(mo.getMetaClass().getName()))
                        ? Color.decode(colorMap.get(mo.getMetaClass().getName())) : DEFAULT_COLOR);
                StyledFeature dsf;
                if (baumGeom.getGeometryType().equals("Polygon")) {
                    dsf = createRectangleFeature(baumGeom, classColor);
                } else {
                    dsf = createPointFeature(baumGeom, classColor);
                }
                features.add(dsf);
                if (geom == null) {
                    geom = (Geometry)dsf.getGeometry().buffer(0).clone();
                } else {
                    geom = geom.union((Geometry)dsf.getGeometry().buffer(0).clone());
                }
            }
        }

        final int margin = 50;
        if (geom != null) {
            final XBoundingBox boundingBox = new XBoundingBox(geom);
            boundingBox.increase(10);
            boundingBox.setX1(boundingBox.getX1() - margin);
            boundingBox.setY1(boundingBox.getY1() - margin);
            boundingBox.setX2(boundingBox.getX2() + margin);
            boundingBox.setY2(boundingBox.getY2() + margin);

            final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
            mapProvider.setCenterMapOnResize(true);
            mapProvider.setBoundingBox(boundingBox);
            final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(config.getMapUrl());
            final SimpleWMS simpleWms = new SimpleWMS(getMapUrl);
            mapProvider.addLayer(simpleWms);

            features.forEach((feature) -> { mapProvider.addFeature(feature); });

            return (BufferedImage)mapProvider.getImageAndWait(
                    72,
                    config.getMapDpi(),
                    config.getWidth(),
                    config.getHeight());
        } else {
            return null;
        }
    }
}
