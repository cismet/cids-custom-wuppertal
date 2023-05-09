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

import java.util.List;

import javax.imageio.ImageIO;

import de.cismet.cids.client.tools.WebDavTunnelHelper;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.CismetThreadPool;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public abstract class ReportBeanWithMapAndTwoWebDavImages extends AbstractReportBeanWithMapAndImages
        implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndTwoWebDavImages.class);

    //~ Instance fields --------------------------------------------------------

    private final WebDavTunnelHelper webDavHelper;
    private final String webDavDirectory;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMapAndTwoWebDavImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  webDavHelper       DOCUMENT ME!
     * @param  webDavDirectory    DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoWebDavImages(final CidsBean cidsBean,
            final String mapUrl,
            final WebDavTunnelHelper webDavHelper,
            final String webDavDirectory,
            final ConnectionContext connectionContext) {
        super(cidsBean, mapUrl, connectionContext);
        this.webDavHelper = webDavHelper;
        this.webDavDirectory = webDavDirectory;
        initImgStates();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected abstract String getDavFile(final CidsBean cidsBean);

    @Override
    protected void initImgStates() {
        final List<CidsBean> imageBeans = getImageBeans();
        if (imageBeans != null) {
            final String url0 = (imageBeans.size() > 0) ? getDavFile(imageBeans.get(0)) : null;
            final String url1 = (imageBeans.size() > 1) ? getDavFile(imageBeans.get(1)) : null;
            setImgState0((url0 != null) ? loadImage(url0, webDavHelper, webDavDirectory, true) : null);
            setImgState1((url1 != null) ? loadImage(url1, webDavHelper, webDavDirectory, false) : null);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   url              DOCUMENT ME!
     * @param   webDavHelper     DOCUMENT ME!
     * @param   webDavDirectory  DOCUMENT ME!
     * @param   oneOrTwo         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ImageState loadImage(final String url,
            final WebDavTunnelHelper webDavHelper,
            final String webDavDirectory,
            final boolean oneOrTwo) {
        final ImageState imgState = new ImageState();
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
                                webDavDirectory,
                                getConnectionContext());
                        final Image img = ImageIO.read(iStream);
                        if (img == null) {
                            imgState.setError(true);
                            LOG.warn("error during image retrieval from Webdav");
                        }
                        imgState.setImg(img);
                    } catch (final Exception e) {
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
        return imgState;
    }
}
