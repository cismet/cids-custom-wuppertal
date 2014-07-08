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
import java.awt.image.BufferedImage;

import java.net.URL;

import java.util.List;

import de.cismet.cids.custom.objectrenderer.utils.VermessungsrissPictureFinder;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class VermessungRissReportScriptlet extends JRDefaultScriptlet {

    //~ Static fields/initializers ---------------------------------------------

    protected static final Logger LOG = Logger.getLogger(VermessungRissReportScriptlet.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   host        DOCUMENT ME!
     * @param   schluessel  DOCUMENT ME!
     * @param   gemarkung   DOCUMENT ME!
     * @param   flur        DOCUMENT ME!
     * @param   blatt       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Boolean isImageAvailable(final String host,
            final String schluessel,
            final Integer gemarkung,
            final String flur,
            final String blatt) {
        final List<URL> validURLs;
        if (host.equals(AlkisConstants.COMMONS.VERMESSUNG_HOST_GRENZNIEDERSCHRIFTEN)) {
            validURLs = VermessungsrissPictureFinder.findGrenzniederschriftPicture(schluessel, gemarkung, flur, blatt);
        } else {
            validURLs = VermessungsrissPictureFinder.findVermessungsrissPicture(schluessel, gemarkung, flur, blatt);
        }

        boolean imageAvailable = false;
        for (final URL urls : validURLs) {
            final URL url = urls;
            if (WebAccessManager.getInstance().checkIfURLaccessible(url)) {
                imageAvailable = true;
                break;
            }
        }
        return imageAvailable;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageToRotate  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BufferedImage rotate(final BufferedImage imageToRotate) {
        BufferedImage result = imageToRotate;

        if (imageToRotate == null) {
            return result;
        }

        if ((imageToRotate instanceof BufferedImage) && (imageToRotate.getWidth() > imageToRotate.getHeight())) {
            result = Static2DTools.rotate(imageToRotate, 90D, false, Color.white);
        }

        return result;
    }
}
