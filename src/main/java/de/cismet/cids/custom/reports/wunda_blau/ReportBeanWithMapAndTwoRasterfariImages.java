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
public class ReportBeanWithMapAndTwoRasterfariImages extends AbstractReportBeanWithMapAndImages
        implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ReportBeanWithMapAndTwoRasterfariImages.class);
    private static final String DOWNLOAD_TEMPLATE =
        "<rasterfari:url>?REQUEST=GetMap&SERVICE=WMS&customDocumentInfo=download&LAYERS=<rasterfari:path>/<rasterfari:document>";

    //~ Instance fields --------------------------------------------------------

    private final String rasterfariUrl;
    private final String rasterfariPath;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportBeanWithMapAndTwoRasterfariImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoRasterfariImages(final CidsBean cidsBean, final ConnectionContext connectionContext) {
        this(cidsBean, null, null, null, null, null, null, null, connectionContext);
    }

    /**
     * Creates a new ReportBeanWithMapAndTwoRasterfariImages object.
     *
     * @param  cidsBean           DOCUMENT ME!
     * @param  geomProp           DOCUMENT ME!
     * @param  docsProp           DOCUMENT ME!
     * @param  positionProp       DOCUMENT ME!
     * @param  filenameProp       DOCUMENT ME!
     * @param  mapUrl             DOCUMENT ME!
     * @param  rasterfariUrl      DOCUMENT ME!
     * @param  rasterfariPath     DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ReportBeanWithMapAndTwoRasterfariImages(final CidsBean cidsBean,
            final String geomProp,
            final String docsProp,
            final String positionProp,
            final String filenameProp,
            final String mapUrl,
            final String rasterfariUrl,
            final String rasterfariPath,
            final ConnectionContext connectionContext) {
        super(cidsBean, geomProp, docsProp, positionProp, filenameProp, mapUrl, connectionContext);
        this.rasterfariUrl = rasterfariUrl;
        this.rasterfariPath = rasterfariPath;
        initImgStates();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void initImgStates() {
        final List<CidsBean> imageBeans = getImageBeans();
        if (imageBeans != null) {
            String filename0 = null;
            String filename1 = null;
            for (final CidsBean b : imageBeans) {
                final Integer nr = (Integer)b.getProperty(getPositionProp());
                if (new Integer(1).equals(nr)) {
                    filename0 = b.getProperty(getFilenameProp()).toString();
                } else if (new Integer(2).equals(nr)) {
                    filename1 = b.getProperty(getFilenameProp()).toString();
                }
            }

            try {
                setImgState0(loadImage(filename0, true));
            } catch (final Exception ex) {
                LOG.error(String.format("error while loading '%s' from rasterfari", filename0), ex);
            }
            try {
                setImgState1(loadImage(filename1, false));
            } catch (final Exception ex) {
                LOG.error(String.format("error while loading '%s' from rasterfari", filename0), ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   filename  url DOCUMENT ME!
     * @param   oneOrTwo  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private ImageState loadImage(final String filename, final boolean oneOrTwo) throws Exception {
        final URL url = new URL(DOWNLOAD_TEMPLATE.replace("<rasterfari:path>", rasterfariPath).replace(
                    "<rasterfari:url>",
                    rasterfariUrl).replace(
                    "<rasterfari:document>",
                    filename));

        final ImageState imgState = new ImageState();
        CismetThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (url.equals("")) {
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
