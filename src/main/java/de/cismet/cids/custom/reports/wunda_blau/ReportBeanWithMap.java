/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.wunda_blau;

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Image;

import java.text.NumberFormat;

import java.util.concurrent.Future;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class ReportBeanWithMap implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMap.class);
    private static final int MAP_DPI = 300;

    //~ Instance fields --------------------------------------------------------

    Image mapImage = null;
    private boolean mapError = false;
    private String masstab = "";
    private final CidsBean cidsBean;
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMap object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMap(final CidsBean cidsBean,
            final String mapUrl,
            final ConnectionContext connectionContext) {
        this.cidsBean = cidsBean;
        this.connectionContext = connectionContext;

        final Geometry geometry = getGeometry();
        if (geometry == null) {
            mapError = true;
            LOG.info("Geometry is null. Can not create a map for the mauer katasterblatt report");
        } else {
            final DefaultStyledFeature dsf = new DefaultStyledFeature();
            dsf.setGeometry(geometry);
            dsf.setLineWidth(5);
            dsf.setLinePaint(Color.RED);
            dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));

            final SimpleWMS s = new SimpleWMS(new SimpleWmsGetMapUrl(mapUrl));
            final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
            mapProvider.addLayer(s);
            mapProvider.addFeature(dsf);
            mapProvider.setMinimumScaleDenomimator(750);
            mapProvider.setRoundScaleTo(HeadlessMapProvider.RoundingPrecision.HUNDRETH);
            mapProvider.setCenterMapOnResize(true);

            final int height = Integer.parseInt(NbBundle.getMessage(
                        ReportBeanWithMap.class,
                        "MauernReportBeanWithMapAndImages.mapHeight"));
            final int width = Integer.parseInt(NbBundle.getMessage(
                        ReportBeanWithMap.class,
                        "MauernReportBeanWithMapAndImages.mapWidth"));
            final XBoundingBox boundingBox = new XBoundingBox(geometry);
            mapProvider.setBoundingBox(boundingBox);
            final Future<Image> f = mapProvider.getImage(72, MAP_DPI, width, height);
            try {
                final Image img = f.get();
                masstab = "1:" + NumberFormat.getIntegerInstance().format(mapProvider.getImageScaleDenominator());
                mapImage = img;
            } catch (final Exception ex) {
                mapError = true;
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract Geometry getGeometry();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getMapImage() {
        return mapImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mapImage  DOCUMENT ME!
     */
    public void setMapImage(final Image mapImage) {
        this.mapImage = mapImage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMasstab() {
        return masstab;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  masstab  DOCUMENT ME!
     */
    public void setMasstab(final String masstab) {
        this.masstab = masstab;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isReadyToProceed() {
        return (cidsBean != null) && ((mapImage != null) || mapError);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
