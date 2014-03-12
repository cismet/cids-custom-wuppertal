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
import Sirius.util.image.ImageAnnotator;

import java.awt.image.RenderedImage;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.wunda_blau.search.actions.TifferAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * A download which uses TifferAction to annotate an image.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 * @see TifferAction
 * @see ImageAnnotator
 */
public class TifferDownload extends AbstractDownload {

    //~ Instance fields --------------------------------------------------------

    private String imageNumber;
    private String location;
    private String dateRecorded;
    private String scale;
    private String format;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TifferDownload object.
     *
     * @param  directory     DOCUMENT ME!
     * @param  title         DOCUMENT ME!
     * @param  filename      DOCUMENT ME!
     * @param  imageNumber   DOCUMENT ME!
     * @param  location      DOCUMENT ME!
     * @param  dateRecorded  DOCUMENT ME!
     * @param  scale         DOCUMENT ME!
     * @param  format        DOCUMENT ME!
     */
    public TifferDownload(final String directory,
            final String title,
            final String filename,
            final String imageNumber,
            final String location,
            final String dateRecorded,
            final String scale,
            final String format) {
        this.imageNumber = imageNumber;
        this.directory = directory;
        this.title = title;
        this.location = location;
        this.dateRecorded = dateRecorded;
        this.scale = scale;
        this.format = format;

        status = State.WAITING;

        determineDestinationFile(filename, "." + format.toLowerCase());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;

        stateChanged();

        final ServerActionParameter paramNummer = new ServerActionParameter(
                TifferAction.ParameterType.BILDNUMMER.toString(),
                imageNumber);
        final ServerActionParameter paramLocation = new ServerActionParameter(
                TifferAction.ParameterType.ORT.toString(),
                location);
        final ServerActionParameter paramDateRecorded = new ServerActionParameter(
                TifferAction.ParameterType.AUFNAHME_DATUM.toString(),
                dateRecorded);
        final ServerActionParameter paramScale = new ServerActionParameter(
                TifferAction.ParameterType.SCALE.toString(),
                scale);
        final ServerActionParameter paramFormat = new ServerActionParameter(
                TifferAction.ParameterType.FORMAT.toString(),
                format);

        try {
            final byte[] result = (byte[])SessionManager.getProxy()
                        .executeTask(
                                TifferAction.ACTION_NAME,
                                "WUNDA_BLAU",
                                null,
                                paramNummer,
                                paramLocation,
                                paramDateRecorded,
                                paramScale,
                                paramFormat);
            if (result != null) {
                final RenderedImage image = ImageIO.read(
                        new ByteArrayInputStream(result));
                ImageIO.write(image, format, fileToSaveTo);
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
}
