/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.Collection;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.objecteditors.wunda_blau.NivellementPunktEditor;

import de.cismet.security.WebAccessManager;

import de.cismet.security.exceptions.AccessMethodIsNotSupportedException;
import de.cismet.security.exceptions.MissingArgumentException;
import de.cismet.security.exceptions.NoHandlerForURLException;
import de.cismet.security.exceptions.RequestFailedException;

import de.cismet.tools.gui.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class NivellementPunktReportScriptlet extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(NivellementPunktReportScriptlet.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   dgkBlattnummer  DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isImageAvailable(final String dgkBlattnummer, final String laufendeNummer) {
        final Collection<URL> validURLs = NivellementPunktEditor.getCorrespondingURLs(dgkBlattnummer, laufendeNummer);

        InputStream streamToReadFrom = null;
        for (final URL url : validURLs) {
            try {
                streamToReadFrom = WebAccessManager.getInstance().doRequest(url);
                break;
            } catch (MissingArgumentException ex) {
                LOG.warn("Could not read document from URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (AccessMethodIsNotSupportedException ex) {
                LOG.warn("Can't access document URL '" + url.toExternalForm()
                            + "' with default access method. Skipping this url.",
                    ex);
            } catch (RequestFailedException ex) {
                LOG.warn("Requesting document from URL '" + url.toExternalForm() + "' failed. Skipping this url.",
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
     * @param   dgkBlattnummer  DOCUMENT ME!
     * @param   laufendeNummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Image loadImage(final String dgkBlattnummer, final String laufendeNummer) {
        final Collection<URL> validURLs = NivellementPunktEditor.getCorrespondingURLs(dgkBlattnummer, laufendeNummer);

        InputStream streamToReadFrom = null;
        for (final URL url : validURLs) {
            try {
                streamToReadFrom = WebAccessManager.getInstance().doRequest(url);
                break;
            } catch (MissingArgumentException ex) {
                LOG.warn("Could not read document from URL '" + url.toExternalForm() + "'. Skipping this url.", ex);
            } catch (AccessMethodIsNotSupportedException ex) {
                LOG.warn("Can't access document URL '" + url.toExternalForm()
                            + "' with default access method. Skipping this url.",
                    ex);
            } catch (RequestFailedException ex) {
                LOG.warn("Requesting document from URL '" + url.toExternalForm() + "' failed. Skipping this url.",
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
            LOG.error("Couldn't get a connection to associated document.");
            return result;
        }

        try {
            result = ImageIO.read(streamToReadFrom);
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

        return Static2DTools.rotate(result, 270D, false, Color.WHITE);
    }
}
