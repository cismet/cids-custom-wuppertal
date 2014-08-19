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
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import java.awt.image.RenderedImage;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.wunda_blau.search.actions.ImageAnnotator;
import de.cismet.cids.custom.wunda_blau.search.actions.TifferAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

import static de.cismet.cids.custom.wunda_blau.search.actions.TifferAction.ParameterType.*;

/**
 * A download which uses TifferAction to annotate an image.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 * @see      TifferAction
 * @see      ImageAnnotator
 */
public class TifferDownload extends AbstractDownload {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TifferDownload.class);

    //~ Instance fields --------------------------------------------------------

    private final String imageNumber;
    private final String scale;
    private final String format;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TifferDownload object.
     *
     * @param  directory    DOCUMENT ME!
     * @param  title        DOCUMENT ME!
     * @param  filename     DOCUMENT ME!
     * @param  imageNumber  DOCUMENT ME!
     * @param  scale        DOCUMENT ME!
     */
    public TifferDownload(final String directory,
            final String title,
            final String filename,
            final String imageNumber,
            final String scale) {
        this.imageNumber = imageNumber;
        this.directory = directory;
        this.title = title;
        this.scale = scale;

        status = State.WAITING;

        format = Sb_stadtbildUtils.getFormatOfHighResPicture(imageNumber);
        if (format != null) {
            determineDestinationFile(filename, "." + format.toLowerCase());
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        if (format == null) {
            log.error(
                "No format found for the image. The image might not exist");
            error(new Exception("No format found for the image. The image might not exist"));
            return;
        }

        status = State.RUNNING;

        stateChanged();

        final ServerActionParameter paramNummer = new ServerActionParameter(
                BILDNUMMER.toString(),
                imageNumber);
        final ServerActionParameter paramScale = new ServerActionParameter(
                SCALE.toString(),
                scale);
        final ServerActionParameter paramFormat = new ServerActionParameter(
                FORMAT.toString(),
                format);

        final ServerActionParameter paramSubdir = createSubDirForURL(imageNumber);

        try {
            final byte[] result = (byte[])SessionManager.getProxy()
                        .executeTask(
                                TifferAction.ACTION_NAME,
                                "WUNDA_BLAU",
                                null,
                                paramNummer,
                                paramScale,
                                paramFormat,
                                paramSubdir);
            if (result != null) {
                final RenderedImage image = ImageIO.read(
                        new ByteArrayInputStream(result));
                TifferAction.writeImage(image, format, fileToSaveTo);
            } else {
                log.error(
                    "Nothing returned by TifferAction. Check its log to see what went wrong.");
                error(new Exception("Nothing returned by TifferAction. Check its log to see what went wrong."));
            }
        } catch (Exception ex) {
            log.error(
                "error during loading the high resolution picture",
                ex);
            error(ex);
        }

        if (status == State.RUNNING) {
            status = State.COMPLETED;
            stateChanged();
        }
    }

    /**
     * The URL later on used by Tiffer consists of the parts base, subdir, filename (prefix and image number) and file
     * extension. The subdir will be the kind of file (VB (Vorschaubild, low res. image) or SB (Stadtbild, high res.
     * image)). In this case SB is always used.
     *
     * @param   imageNumber  DOCUMENT ME!
     *
     * @return  the subdir for an imageNumber as ServerActionParameter
     */
    private ServerActionParameter createSubDirForURL(final String imageNumber) {
        final char firstCharacter = imageNumber.charAt(0);
        final String subdir = "SB/" + firstCharacter + "/" + "SB_";

        return new ServerActionParameter(
                SUBDIR.toString(),
                subdir);
    }
}
