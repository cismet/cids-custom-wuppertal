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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.GregorianCalendar;

import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservation;
import de.cismet.cids.custom.utils.pointnumberreservation.PointNumberReservationRequest;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class PointNumberDownload extends AbstractDownload {

    //~ Instance fields --------------------------------------------------------

    boolean isFreigabeMode = false;
    boolean downloadProtokoll = false;
    private final StringBuilder contentBuilder = new StringBuilder();
    private PointNumberReservationRequest content;
    private final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PointNumberDownload object.
     *
     * @param  content    DOCUMENT ME!
     * @param  title      DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     */
    public PointNumberDownload(final PointNumberReservationRequest content,
            final String title,
            final String directory,
            final String filename) {
        this.content = content;
        this.title = title;
        this.directory = directory;
        if ((content != null) && content.isSuccessfull() && (content.getPointNumbers() != null)) {
            for (final PointNumberReservation pnr : content.getPointNumbers()) {
                if ((pnr.getAblaufDatum() == null) || pnr.getAblaufDatum().isEmpty()) {
                    isFreigabeMode = true;
                    break;
                }
            }
        }
        status = State.WAITING;
        if ((content == null) || content.isSuccessfull()) {
            determineDestinationFile(filename, ".txt");
        } else {
            downloadProtokoll = true;
            determineDestinationFile(filename, ".xml");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void createFileBody() {
        for (final PointNumberReservation pnr : content.getPointNumbers()) {
            contentBuilder.append(pnr.getPunktnummern());
            if (!isFreigabeMode) {
                contentBuilder.append(" (");
                contentBuilder.append(pnr.getAblaufDatum());
                contentBuilder.append(" )");
            }
            contentBuilder.append(System.getProperty("line.separator"));
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void createFileHeader() {
        String header = "Antragsnummer: " + content.getAntragsnummer() + " erstellt am: ";
        final GregorianCalendar cal = new GregorianCalendar();
        header += df.format(cal.getTime());
        header += " Anzahl ";
        if (isFreigabeMode) {
            header += "freigegebener";
        } else {
            header += "reservierter";
        }

        header += " Punktnummern: " + content.getPointNumbers().size();
        contentBuilder.append(header);
        contentBuilder.append(System.getProperty("line.separator"));
        if (isFreigabeMode) {
            contentBuilder.append("freigegebene Punktnummern");
        } else {
            contentBuilder.append("reservierte Punktnummern (gültig bis)");
        }
        contentBuilder.append(System.getProperty("line.separator"));
        contentBuilder.append(System.getProperty("line.separator"));
    }

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();
        final String bytes;
        if (downloadProtokoll) {
            bytes = content.getProtokoll();
        } else {
            if (!isPointNumberBeanValid()) {
                status = State.COMPLETED_WITH_ERROR;
                stateChanged();
                return;
            }
            createFileHeader();
            createFileBody();
            bytes = contentBuilder.toString();
        }
        if ((bytes == null) || (bytes.isEmpty())) {
            log.info("Downloaded content seems to be empty..");

            if (status == State.RUNNING) {
                status = State.COMPLETED;
                stateChanged();
            }

            return;
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileToSaveTo);
            final Writer w = new OutputStreamWriter(out, "UTF8");
            w.write(bytes);
        } catch (final IOException ex) {
            log.warn("Couldn't write downloaded content to file '" + fileToSaveTo + "'.", ex);
            error(ex);
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
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
