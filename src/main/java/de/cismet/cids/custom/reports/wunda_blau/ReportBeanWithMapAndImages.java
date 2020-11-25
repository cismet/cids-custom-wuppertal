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

import lombok.Getter;
import lombok.Setter;

import java.awt.Image;

import java.io.InputStream;

import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.objecteditors.utils.WebDavHelper;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.netutil.Proxy;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportBeanWithMapAndImages extends ReportBeanWithMap implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndImages.class);

    //~ Instance fields --------------------------------------------------------

    private final ImageState imgState0;
    private final ImageState imgState1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMapAndImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndImages(final CidsBean cidsBean, final ConnectionContext connectionContext) {
        this(cidsBean, null, null, null, null, connectionContext);
    }

    /**
     * Creates a new MauernBeanWithMapAndImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  geomProp           DOCUMENT ME!
     * @param  imgsProp           DOCUMENT ME!
     * @param  davUrlProp         DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndImages(final CidsBean cidsBean,
            final String geomProp,
            final String imgsProp,
            final String davUrlProp,
            final String mapUrl,
            final ConnectionContext connectionContext) {
        super(cidsBean, geomProp, mapUrl, connectionContext);

        final List<CidsBean> images = (imgsProp != null)
            ? CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, imgsProp) : null;
        if (images != null) {
            final StringBuilder url0Builder = new StringBuilder();
            final StringBuilder url1Builder = new StringBuilder();
            for (final CidsBean b : images) {
                final Integer nr = (Integer)b.getProperty("laufende_nummer");
                if (nr == 1) {
                    url0Builder.append(b.getProperty("url.object_name").toString());
                } else if (nr == 2) {
                    url1Builder.append(b.getProperty("url.object_name").toString());
                }
            }

            final ResourceBundle webDavBundle = ResourceBundle.getBundle("WebDav");
            String pass = webDavBundle.getString("password");

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }

            final String webDavPassword = pass;
            final String webDavUser = webDavBundle.getString("user");
            final String webDavDirectory = webDavBundle.getString(davUrlProp);
            final WebDavHelper webDavHelper = new WebDavHelper(Proxy.fromPreferences(),
                    webDavUser,
                    webDavPassword,
                    false);

            imgState0 = new ImageState();
            imgState1 = new ImageState();
            loadImage(url0Builder.toString(), imgState0, webDavHelper, webDavDirectory);
            loadImage(url1Builder.toString(), imgState1, webDavHelper, webDavDirectory);
        } else {
            imgState0 = null;
            imgState1 = null;
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  url              DOCUMENT ME!
     * @param  imgState         DOCUMENT ME!
     * @param  webDavHelper     DOCUMENT ME!
     * @param  webDavDirectory  DOCUMENT ME!
     */
    private void loadImage(final String url,
            final ImageState imgState,
            final WebDavHelper webDavHelper,
            final String webDavDirectory) {
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
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImg0() {
        return (imgState0 != null) ? imgState0.getImg() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img0  DOCUMENT ME!
     */
    public void setImg0(final Image img0) {
        if (imgState0 != null) {
            imgState0.setImg(img0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image getImg1() {
        return (imgState1 != null) ? imgState1.getImg() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  img1  DOCUMENT ME!
     */
    public void setImg1(final Image img1) {
        if (imgState1 != null) {
            imgState1.setImg(img1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isReadyToProceed() {
        return super.isReadyToProceed() && ((imgState0 == null) || (imgState0.getImg() != null) || imgState0.isError())
                    && ((imgState1 == null) || (imgState1.getImg() != null) || imgState1.isError());
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
