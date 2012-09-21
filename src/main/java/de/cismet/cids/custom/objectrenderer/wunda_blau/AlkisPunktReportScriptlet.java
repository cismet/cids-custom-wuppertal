/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import org.apache.log4j.Logger;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Collection;

import javax.imageio.ImageIO;

import javax.media.jai.RenderedImageAdapter;

import de.cismet.security.WebAccessManager;

import de.cismet.security.exceptions.AccessMethodIsNotSupportedException;
import de.cismet.security.exceptions.MissingArgumentException;
import de.cismet.security.exceptions.NoHandlerForURLException;
import de.cismet.security.exceptions.RequestFailedException;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class AlkisPunktReportScriptlet extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(AlkisPunktReportScriptlet.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   pointcode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isImageAvailable(final String pointcode) {
        final Collection<URL> validURLs = AlkisPointRenderer.getCorrespondingURLs(pointcode);

        InputStream streamToReadFrom = null;
        for (final URL url : validURLs) {
            try {
                streamToReadFrom = WebAccessManager.getInstance().doRequest(url);
                break;
            } catch (MissingArgumentException ex) {
                LOG.warn("Could not read ap map from URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (AccessMethodIsNotSupportedException ex) {
                LOG.warn("Can't access ap map URL '" + url.toExternalForm()
                            + "' with default access method. Skipping this url.",
                    ex);
            } catch (RequestFailedException ex) {
                LOG.warn("Requesting ap map from URL '" + url.toExternalForm() + "' failed. Skipping this url.",
                    ex);
            } catch (NoHandlerForURLException ex) {
                LOG.warn("Can't handle URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (Exception ex) {
                LOG.warn("An exception occurred while opening URL '" + url.toExternalForm()
                            + "'. Skipping this url.",
                    ex);
            }
        }

        return streamToReadFrom != null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   pointcode  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image loadImage(final String pointcode) {
        final Collection<URL> validURLs = AlkisPointRenderer.getCorrespondingURLs(pointcode);
        String suffix = "";

        InputStream streamToReadFrom = null;
        for (final URL url : validURLs) {
            try {
                streamToReadFrom = WebAccessManager.getInstance().doRequest(url);
                suffix = url.toExternalForm().substring(url.toExternalForm().lastIndexOf('.'));
                break;
            } catch (MissingArgumentException ex) {
                LOG.warn("Could not read ap map from URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (AccessMethodIsNotSupportedException ex) {
                LOG.warn("Can't access ap map URL '" + url.toExternalForm()
                            + "' with default access method. Skipping this url.",
                    ex);
            } catch (RequestFailedException ex) {
                LOG.warn("Requesting ap map from URL '" + url.toExternalForm() + "' failed. Skipping this url.",
                    ex);
            } catch (NoHandlerForURLException ex) {
                LOG.warn("Can't handle URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (Exception ex) {
                LOG.warn("An exception occurred while opening URL '" + url.toExternalForm()
                            + "'. Skipping this url.",
                    ex);
            }
        }

        BufferedImage result = null;
        if (streamToReadFrom == null) {
            LOG.error("Couldn't get a connection to associated ap map.");
            return result;
        }

        try {
            if (suffix.endsWith("tif") || suffix.endsWith("tiff") || suffix.endsWith("TIF")
                        || suffix.endsWith("TIFF")) {
                final TIFFDecodeParam param = null;
                final ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", streamToReadFrom, param);
                final RenderedImage image = decoder.decodeAsRenderedImage();
                final RenderedImageAdapter imageAdapter = new RenderedImageAdapter(image);
                result = imageAdapter.getAsBufferedImage();
            } else {
                result = ImageIO.read(streamToReadFrom);
            }
        } catch (IOException ex) {
            LOG.warn("Could not read image.", ex);
            return result;
        } finally {
            try {
                if (streamToReadFrom != null) {
                    streamToReadFrom.close();
                }
            } catch (IOException ex) {
                LOG.warn("Couldn't close the stream.", ex);
            }
        }

        return result;
    }
}
