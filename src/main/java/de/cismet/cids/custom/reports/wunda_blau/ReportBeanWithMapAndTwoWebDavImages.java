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
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import de.cismet.cids.client.tools.WebDavTunnelHelper;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.netutil.ProxyHandler;

import de.cismet.tools.CismetThreadPool;
import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportBeanWithMapAndTwoWebDavImages extends AbstractReportBeanWithMapAndImages
        implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndTwoWebDavImages.class);

    //~ Instance fields --------------------------------------------------------

    private final String davUrlProp;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMapAndTwoWebDavImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoWebDavImages(final CidsBean cidsBean, final ConnectionContext connectionContext) {
        this(cidsBean, null, null, null, null, connectionContext);
    }

    /**
     * Creates a new ReportBeanWithMapAndTwoWebDavImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  geomProp           DOCUMENT ME!
     * @param  imgsProp           DOCUMENT ME!
     * @param  davUrlProp         DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoWebDavImages(final CidsBean cidsBean,
            final String geomProp,
            final String imgsProp,
            final String davUrlProp,
            final String mapUrl,
            final ConnectionContext connectionContext) {
        super(cidsBean, geomProp, imgsProp, "laufende_nummer", "url.object_name", mapUrl, connectionContext);
        this.davUrlProp = davUrlProp;
        initImgStates();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void initImgStates() {
        final List<CidsBean> imageBeans = getImageBeans();
        if (imageBeans != null) {
            final StringBuilder url0Builder = new StringBuilder();
            final StringBuilder url1Builder = new StringBuilder();
            for (final CidsBean b : imageBeans) {
                final Integer nr = (Integer)b.getProperty(getPositionProp());
                if (new Integer(1).equals(nr)) {
                    url0Builder.append(b.getProperty(getFilenameProp()).toString());
                } else if (new Integer(2).equals(nr)) {
                    url1Builder.append(b.getProperty(getFilenameProp()).toString());
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
            final WebDavTunnelHelper webDavHelper = new WebDavTunnelHelper(
                    "WUNDA_BLAU",
                    ProxyHandler.getInstance().getProxy(),
                    webDavUser,
                    webDavPassword,
                    false);

            final ImageState[] imgStates = new ImageState[2];
            imgStates[0] = loadImage(url0Builder.toString(), webDavHelper, webDavDirectory, true);
            imgStates[1] = loadImage(url1Builder.toString(), webDavHelper, webDavDirectory, false);
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
