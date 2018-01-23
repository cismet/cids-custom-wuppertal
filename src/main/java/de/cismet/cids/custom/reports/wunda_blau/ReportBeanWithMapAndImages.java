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

import lombok.Getter;
import lombok.Setter;

import org.openide.util.NbBundle;

import java.awt.Color;
import java.awt.Image;

import java.io.InputStream;

import java.text.NumberFormat;

import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportBeanWithMapAndImages {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndImages.class);
    private static String WEB_DAV_DIRECTORY;
    private static String WEB_DAV_USER;
    private static String WEB_DAV_PASSWORD;
    private static String MAP_URL;
    private static final int MAP_DPI = 300;

    //~ Instance fields --------------------------------------------------------

    Image mapImage = null;
    private boolean mapError = false;
    private String masstab = "";
    private final CidsBean cidsBean;
    private final ImageState imgState0 = new ImageState();
    private final ImageState imgState1 = new ImageState();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernBeanWithMapAndImages object.
     *
     * @param  cidsBean  DOCUMENT ME!
     * @param  geomProp  DOCUMENT ME!
     * @param  imgsProp  DOCUMENT ME!
     */
    public ReportBeanWithMapAndImages(final CidsBean cidsBean, final String geomProp, final String imgsProp) {
        this.cidsBean = cidsBean;
        LOG.fatal("ReportBeanWithMapAndImages");
//        final ResourceBundle webDavBundle = ResourceBundle.getBundle("WebDav");
//        String pass = webDavBundle.getString("password");
//
//        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
//            pass = PasswordEncrypter.decryptString(pass);
//        }
//
//        WEB_DAV_PASSWORD = pass;
//        WEB_DAV_USER = webDavBundle.getString("user");
//        WEB_DAV_DIRECTORY = webDavBundle.getString("url");
//        final WebDavHelper webDavHelper = new WebDavHelper(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD, false);

        MAP_URL = java.util.ResourceBundle.getBundle(
                "de/cismet/cids/custom/reports/wunda_blau/MauernReport").getString("map_url");

        final SimpleWMS s = new SimpleWMS(new SimpleWmsGetMapUrl(
                    MAP_URL));

        final Geometry g = (Geometry)cidsBean.getProperty(geomProp);
        if (g == null) {
            mapError = true;
            LOG.info("Geometry is null. Can not create a map for the mauer katasterblatt report");
        } else {
            final DefaultStyledFeature dsf = new DefaultStyledFeature();
            dsf.setGeometry(g);
            dsf.setLineWidth(5);
            dsf.setLinePaint(Color.RED);
            dsf.setFillingPaint(new Color(1, 0, 0, 0.5f));

            final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
            mapProvider.addLayer(s);
            mapProvider.addFeature(dsf);
            mapProvider.setMinimumScaleDenomimator(750);
            mapProvider.setRoundScaleTo(HeadlessMapProvider.RoundingPrecision.HUNDRETH);
            mapProvider.setCenterMapOnResize(true);

            final int height = Integer.parseInt(NbBundle.getMessage(
                        ReportBeanWithMapAndImages.class,
                        "MauernReportBeanWithMapAndImages.mapHeight"));
            final int width = Integer.parseInt(NbBundle.getMessage(
                        ReportBeanWithMapAndImages.class,
                        "MauernReportBeanWithMapAndImages.mapWidth"));
            final XBoundingBox boundingBox = new XBoundingBox(g);
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

//        final List<CidsBean> images = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, imgsProp);
//        final StringBuilder url0Builder = new StringBuilder();
//
//        final StringBuilder url1Builder = new StringBuilder();
//        for (final CidsBean b : images) {
//            final Integer nr = (Integer)b.getProperty("laufende_nummer");
//            if (nr == 1) {
//                url0Builder.append(b.getProperty("url.object_name").toString());
//            } else if (nr == 2) {
//                url1Builder.append(b.getProperty("url.object_name").toString());
//            }
//        }
//
//        loadImage(url0Builder.toString(), imgState0, webDavHelper);
//        loadImage(url1Builder.toString(), imgState1, webDavHelper);

        imgState0.setError(true);
        imgState1.setError(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  url           DOCUMENT ME!
     * @param  imgState      DOCUMENT ME!
     * @param  webDavHelper  DOCUMENT ME!
     */
    private void loadImage(final String url, final ImageState imgState, final WebDavHelper webDavHelper) {
        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (url.equals("")) {
                            imgState.setError(true);
                            return;
                        }
                        final InputStream iStream = webDavHelper.getFileFromWebDAV(
                                url,
                                WEB_DAV_DIRECTORY);
                        final Image img = ImageIO.read(iStream);
                        if (img == null) {
                            imgState.setError(true);
                            LOG.warn("error during image retrieval from Webdav");
                        }
                        imgState.setImg(img);
                    } catch (final Exception e) {
                        imgState.setError(true);
                    }
                }
            });
    }

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
    public Image getImg0() {
        return imgState0.getImg();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img0  DOCUMENT ME!
     */
    public void setImg0(final Image img0) {
        this.imgState0.setImg(img0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImg1() {
        return imgState1.getImg();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img1  DOCUMENT ME!
     */
    public void setImg1(final Image img1) {
        this.imgState1.setImg(img1);
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
        return (cidsBean != null) && ((mapImage != null) || mapError)
                    && ((imgState0.getImg() != null) || imgState0.isError())
                    && ((imgState1.getImg() != null) || imgState1.isError());
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    class ImageState {

        //~ Instance fields ----------------------------------------------------

        private Image img;
        private boolean error;
    }
}
