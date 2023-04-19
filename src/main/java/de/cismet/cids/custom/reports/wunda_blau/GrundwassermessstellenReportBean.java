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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.jfree.chart.JFreeChart;

import java.awt.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Getter
public class GrundwassermessstellenReportBean extends ReportBeanWithMap {

    //~ Static fields/initializers ---------------------------------------------

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(
            "de/cismet/cids/custom/reports/wunda_blau/GwmsReport");

    //~ Instance fields --------------------------------------------------------

    private final JFreeChart chart;
    private final CidsBean kategorieBean;
    private final List<CidsBean> messungBeans = new ArrayList<>();
    private final List<LegendeBean> legendeLeft = new ArrayList<>();
    private final List<LegendeBean> legendeRight = new ArrayList<>();
    private final Image chartImage;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  messstelleBean     DOCUMENT ME!
     * @param  kategorieBean      editor DOCUMENT ME!
     * @param  messungBeans       DOCUMENT ME!
     * @param  legendeLeft        DOCUMENT ME!
     * @param  legendeRight       DOCUMENT ME!
     * @param  chart              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public GrundwassermessstellenReportBean(final CidsBean messstelleBean,
            final CidsBean kategorieBean,
            final List<CidsBean> messungBeans,
            final List<LegendeBean> legendeLeft,
            final List<LegendeBean> legendeRight,
            final JFreeChart chart,
            final ConnectionContext connectionContext) {
        super(
            messstelleBean,
            RESOURCE_BUNDLE.getString("map_url"),
            connectionContext);
        this.kategorieBean = kategorieBean;
        this.messungBeans.addAll(messungBeans);
        this.legendeLeft.addAll(legendeLeft);
        this.legendeRight.addAll(legendeRight);
        this.chart = chart;

        final int dpi = Integer.parseInt(RESOURCE_BUNDLE.getString("chart_dpi"));
        final int width = (int)(Integer.parseInt(RESOURCE_BUNDLE.getString("chart_width")) * (dpi / 72d));
        final int height = (int)(Integer.parseInt(RESOURCE_BUNDLE.getString("chart_height")) * (dpi / 72d));
        this.chartImage = chart.createBufferedImage(width, height);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Geometry getGeometry() {
        return (getCidsBean() != null) ? (Geometry)getCidsBean().getProperty("geometrie.geo_field") : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getGwms() {
        return getCidsBean();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public static class LegendeBean {

        //~ Instance fields ----------------------------------------------------

        private final CidsBean stoffBean;
        private final Image shapeImage;
    }
}
