/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.nas;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservationRequest;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class PointNumberXmlDownload extends AbstractDownload {

    //~ Instance fields --------------------------------------------------------

    boolean downloadProtokoll = false;
    private final PointNumberReservationRequest content;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PointNumberDownload object.
     *
     * @param  content    DOCUMENT ME!
     * @param  title      DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     */
    public PointNumberXmlDownload(final PointNumberReservationRequest content,
            final String title,
            final String directory,
            final String filename) {
        this.content = content;
        this.title = title;
        this.directory = directory;
        status = State.WAITING;
        determineDestinationFile(filename, ".xml");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();
        final String protokoll;
        if (downloadProtokoll) {
            protokoll = content.getProtokoll();
        } else {
            if (!isPointNumberBeanValid()) {
                status = State.COMPLETED_WITH_ERROR;
                stateChanged();
                return;
            }
            protokoll = content.getRawResult();
        }
        if ((protokoll == null) || (protokoll.isEmpty())) {
            log.info("Downloaded content seems to be empty..");

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }

            return;
        }

        try(final FileOutputStream out = new FileOutputStream(fileToSaveTo);
                    final Writer w = new OutputStreamWriter(out, "UTF-8")) {
            w.write(protokoll);
            w.flush();
        } catch (final IOException ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
            return;
        }

        if (status == State.RUNNING) {
            status = State.COMPLETED;
            stateChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isPointNumberBeanValid() {
        if (content == null) {
            return false;
        }
        if ((content.getAntragsnummer() == null) || content.getAntragsnummer().isEmpty()) {
            return false;
        }
        if ((content.getPointNumbers() == null) || content.getPointNumbers().isEmpty()) {
            return false;
        }
        return true;
    }
}
