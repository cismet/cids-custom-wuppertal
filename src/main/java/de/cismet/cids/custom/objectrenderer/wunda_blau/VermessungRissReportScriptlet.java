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

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.Collection;

import de.cismet.cids.custom.objecteditors.wunda_blau.VermessungRissEditor;

import de.cismet.tools.gui.MultiPagePictureReader;

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
     * @param   host  laufendeNummer DOCUMENT ME!
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Boolean isImageAvailable(final String host, final String path) {
        final Collection<File> validFiles = VermessungRissEditor.getCorrespondingFiles(host, path);

        return (validFiles != null) && !validFiles.isEmpty();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   host  DOCUMENT ME!
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image[] loadImages(final String host, final String path) {
        final Collection<File> validFiles = VermessungRissEditor.getCorrespondingFiles(host, path);
        Image[] result = new Image[0];
        final File fileToReadFrom;

        if ((validFiles != null) && !validFiles.isEmpty()) {
            fileToReadFrom = validFiles.iterator().next();
        } else {
            return result;
        }

        MultiPagePictureReader reader = null;
        try {
            reader = new MultiPagePictureReader(fileToReadFrom);

            if ((reader != null) && (reader.getNumberOfPages() > 0)) {
                result = new Image[reader.getNumberOfPages()];

                for (int i = 0; i < reader.getNumberOfPages(); i++) {
                    result[i] = reader.loadPage(i);
                }
            }
        } catch (IOException ex) {
            LOG.error("Could not load associated images. Host: '" + host + "', path: '" + path + "'.", ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   host  DOCUMENT ME!
     * @param   path  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Image loadImage(final String host, final String path) {
        final Collection<File> validFiles = VermessungRissEditor.getCorrespondingFiles(host, path);
        final File fileToReadFrom;
        BufferedImage result = null;

        if ((validFiles != null) && !validFiles.isEmpty()) {
            fileToReadFrom = validFiles.iterator().next();
        } else {
            return result;
        }

        MultiPagePictureReader reader = null;
        try {
            reader = new MultiPagePictureReader(fileToReadFrom);

            if ((reader != null) && (reader.getNumberOfPages() > 0)) {
                result = reader.loadPage(0);
            }
        } catch (IOException ex) {
            LOG.error("Could not load associated images. Host: '" + host + "', path: '" + path + "'.", ex);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return result;
    }
}
