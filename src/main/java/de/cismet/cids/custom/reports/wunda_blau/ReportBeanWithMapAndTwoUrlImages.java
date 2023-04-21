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

import java.awt.Image;

import java.io.InputStream;

import java.net.URL;

import java.util.List;

import javax.imageio.ImageIO;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.commons.security.handler.SimpleHttpAccessHandler;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class ReportBeanWithMapAndTwoUrlImages extends AbstractReportBeanWithMapAndImages
        implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndTwoUrlImages.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMapAndTwoUrlImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoUrlImages(final CidsBean cidsBean, final ConnectionContext connectionContext) {
        this(cidsBean, null, connectionContext);
    }

    /**
     * Creates a new ReportBeanWithMapAndTwoUrlImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoUrlImages(final CidsBean cidsBean,
            final String mapUrl,
            final ConnectionContext connectionContext) {
        super(cidsBean, mapUrl, connectionContext);
        initImgStates();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract URL getUrl(final CidsBean cidsBean) throws Exception;

    @Override
    protected void initImgStates() {
        final List<CidsBean> imageBeans = getImageBeans();
        if (imageBeans != null) {
            try {
                setImgState0(loadImage((imageBeans.size() > 0) ? imageBeans.get(0) : null, true));
            } catch (final Exception ex) {
                LOG.error("error while loading url0", ex);
            }
            try {
                setImgState1(loadImage((imageBeans.size() > 1) ? imageBeans.get(1) : null, false));
            } catch (final Exception ex) {
                LOG.error("error while loading url1", ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageBean  url filename url DOCUMENT ME!
     * @param   oneOrTwo   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private ImageState loadImage(final CidsBean imageBean, final boolean oneOrTwo) throws Exception {
        final ImageState imgState = new ImageState();
        if (imageBean != null) {
            CismetThreadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            final URL url = getUrl(imageBean);
                            if (url == null) {
                                imgState.setError(true);
                                return;
                            }
                            final InputStream iStream = new SimpleHttpAccessHandler().doRequest(url);
                            final Image img = ImageIO.read(iStream);
                            if (img == null) {
                                imgState.setError(true);
                                LOG.warn("error during image retrieval from Rasterfari");
                            }
                            imgState.setImg(img);
                        } catch (final Exception e) {
                            LOG.error(e, e);
                            imgState.setError(true);
                        } finally {
                            if (oneOrTwo) {
                                setImg0Ready(true);
                            } else {
                                setImg1Ready(true);
                            }
                        }
                    }
                });
        } else {
            if (oneOrTwo) {
                setImg0Ready(true);
            } else {
                setImg1Ready(true);
            }
        }
        return imgState;
    }
}
