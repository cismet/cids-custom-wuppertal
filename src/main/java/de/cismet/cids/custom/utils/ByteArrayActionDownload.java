/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.openide.util.Exceptions;

import java.io.FileOutputStream;
import java.io.IOException;

import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionDownloadBestellungAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;
import de.cismet.tools.gui.downloadmanager.ByteArrayDownload;

/**
 * A ByteArrayDownload writes a given byte array to the file system. Using the DownloadManager this class can be used to
 * create the impression of a real download.
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class ByteArrayActionDownload extends AbstractDownload {

    //~ Instance fields --------------------------------------------------------

    private final String taskname;
    private final Object body;
    private final ServerActionParameter[] params;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ByteArrayDownload object.
     *
     * @param  taskname   DOCUMENT ME!
     * @param  body       DOCUMENT ME!
     * @param  params     DOCUMENT ME!
     * @param  title      The title of the download.
     * @param  directory  The directory of the download.
     * @param  filename   The name of the file to be created.
     * @param  extension  The extension of the file to be created.
     */
    public ByteArrayActionDownload(final String taskname,
            final Object body,
            final ServerActionParameter[] params,
            final String title,
            final String directory,
            final String filename,
            final String extension) {
        this.taskname = taskname;
        this.body = body;
        this.params = params;
        this.title = title;
        this.directory = directory;

        status = State.WAITING;

        determineDestinationFile(filename, extension);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();

        final Object ret;
        try {
            ret = SessionManager.getProxy().executeTask(taskname, "WUNDA_BLAU", body, params);

            if (ret instanceof Exception) {
                final Exception ex = (Exception)ret;
                throw ex;
            }
        } catch (final Exception ex) {
            log.warn("Couldn't execute task '" + taskname + "'.", ex);
            error(ex);
            return;
        }

        final byte[] content = (byte[])ret;

        if ((content == null) || (content.length <= 0)) {
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
            out.write(content);
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
}
