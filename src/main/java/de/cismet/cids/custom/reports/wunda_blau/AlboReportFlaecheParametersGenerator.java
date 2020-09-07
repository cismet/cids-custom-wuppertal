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

import java.awt.Color;
import java.awt.Image;

import java.text.NumberFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import de.cismet.cids.custom.objecteditors.utils.AlboProperties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
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
public class AlboReportFlaecheParametersGenerator implements JasperReportDownload.JasperReportParametersGenerator,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AlboReportFlaecheParametersGenerator.class);
    private static final String GEO_FIELD__PROPERTY = "fk_geom.geo_field";
    private static final AlboProperties PROPERTIES = AlboProperties.getInstance();

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParametersKeys {

        //~ Enum constants -----------------------------------------------------

        MAP_MASSSTAB, MAP_IMAGE, MAP_ERROR
    }

    //~ Instance fields --------------------------------------------------------

    private final CidsBean flaecheBean;
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboReportFlaecheParametersGenerator object.
     *
     * @param  flaecheBean        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public AlboReportFlaecheParametersGenerator(final CidsBean flaecheBean, final ConnectionContext connectionContext) {
        this.flaecheBean = flaecheBean;
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getCidsBean() {
        return flaecheBean;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public Map generateParamters() {
        final Map<String, Object> parameters = new HashMap<>();

        final SimpleWMS s = new SimpleWMS(new SimpleWmsGetMapUrl(AlboProperties.getInstance().getFlaecheMapUrl()));

        final Geometry geometry = (Geometry)flaecheBean.getProperty(GEO_FIELD__PROPERTY);
        if (geometry == null) {
            parameters.put(ParametersKeys.MAP_ERROR.toString(), true);
            LOG.info("Geometry is null. Can not create a map for the albo flaeche report");
        } else {
            final DefaultStyledFeature dsf = new DefaultStyledFeature();
            dsf.setGeometry(geometry);
            dsf.setLineWidth(5);
            dsf.setLinePaint(Color.RED);
            dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));

            final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
            mapProvider.addLayer(s);
            mapProvider.addFeature(dsf);
            mapProvider.setMinimumScaleDenomimator(750);
            mapProvider.setRoundScaleTo(HeadlessMapProvider.RoundingPrecision.HUNDRETH);
            mapProvider.setCenterMapOnResize(true);

            final XBoundingBox boundingBox = new XBoundingBox(geometry);
            mapProvider.setBoundingBox(boundingBox);
            try {
                final Future<Image> f = mapProvider.getImage(
                        72,
                        PROPERTIES.getFlaecheMapDpi(),
                        PROPERTIES.getFlaecheMapWidth(),
                        PROPERTIES.getFlaecheMapHeight());
                final Image img = f.get();
                parameters.put(ParametersKeys.MAP_MASSSTAB.toString(),
                    "1:"
                            + NumberFormat.getIntegerInstance().format(mapProvider.getImageScaleDenominator()));
                parameters.put(ParametersKeys.MAP_IMAGE.toString(), img);
            } catch (final Exception ex) {
                parameters.put(ParametersKeys.MAP_ERROR.toString(), true);
            }
        }
        return parameters;
    }
}
