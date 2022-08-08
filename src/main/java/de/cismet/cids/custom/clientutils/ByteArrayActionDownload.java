/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.concurrent.Future;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

/**
 * A ByteArrayDownload writes a given byte array to the file system. Using the DownloadManager this class can be used to
 * create the impression of a real download.
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
public class ByteArrayActionDownload extends AbstractCancellableDownload implements ConnectionContextProvider {

    //~ Instance fields --------------------------------------------------------

    protected String taskname;
    protected Object body;
    protected ServerActionParameter[] params;
    protected Future<ServerActionParameter[]> paramsFuture;

    private final String domain;

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ByteArrayActionDownload object.
     *
     * @param  taskname           DOCUMENT ME!
     * @param  body               DOCUMENT ME!
     * @param  params             DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  directory          DOCUMENT ME!
     * @param  filename           DOCUMENT ME!
     * @param  extension          DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ByteArrayActionDownload(final String taskname,
            final Object body,
            final ServerActionParameter[] params,
            final String title,
            final String directory,
            final String filename,
            final String extension,
            final ConnectionContext connectionContext) {
        this("WUNDA_BLAU", taskname, body, params, title, directory, filename, extension, connectionContext);
    }

    /**
     * Creates a new ByteArrayActionDownload object.
     *
     * @param  taskname           DOCUMENT ME!
     * @param  body               DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  directory          DOCUMENT ME!
     * @param  filename           DOCUMENT ME!
     * @param  extension          DOCUMENT ME!
     * @param  params             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ByteArrayActionDownload(final String taskname,
            final Object body,
            final String title,
            final String directory,
            final String filename,
            final String extension,
            final Future<ServerActionParameter[]> params,
            final ConnectionContext connectionContext) {
        this("WUNDA_BLAU", taskname, body, title, directory, filename, extension, params, connectionContext);
    }

    /**
     * Creates a new ByteArrayDownload object.
     *
     * @param  domain             DOCUMENT ME!
     * @param  taskname           DOCUMENT ME!
     * @param  body               DOCUMENT ME!
     * @param  params             DOCUMENT ME!
     * @param  title              The title of the download.
     * @param  directory          The directory of the download.
     * @param  filename           The name of the file to be created.
     * @param  extension          The extension of the file to be created.
     * @param  connectionContext  DOCUMENT ME!
     */
    public ByteArrayActionDownload(final String domain,
            final String taskname,
            final Object body,
            final ServerActionParameter[] params,
            final String title,
            final String directory,
            final String filename,
            final String extension,
            final ConnectionContext connectionContext) {
        this.domain = domain;
        this.taskname = taskname;
        this.body = body;
        this.params = params;
        this.title = title;
        this.directory = directory;
        this.connectionContext = connectionContext;

        status = State.WAITING;

        determineDestinationFile(filename, extension);
    }

    /**
     * Creates a new ByteArrayDownload object.
     *
     * @param  domain             DOCUMENT ME!
     * @param  taskname           DOCUMENT ME!
     * @param  body               DOCUMENT ME!
     * @param  title              The title of the download.
     * @param  directory          The directory of the download.
     * @param  filename           The name of the file to be created.
     * @param  extension          The extension of the file to be created.
     * @param  params             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public ByteArrayActionDownload(final String domain,
            final String taskname,
            final Object body,
            final String title,
            final String directory,
            final String filename,
            final String extension,
            final Future<ServerActionParameter[]> params,
            final ConnectionContext connectionContext) {
        this.domain = domain;
        this.taskname = taskname;
        this.body = body;
        this.paramsFuture = params;
        this.title = title;
        this.directory = directory;
        this.connectionContext = connectionContext;

        status = State.WAITING;

        determineDestinationFile(filename, extension);
    }

    /**
     * Creates a new ByteArrayActionDownload object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    protected ByteArrayActionDownload(final ConnectionContext connectionContext) {
        this("WUNDA_BLAU", connectionContext);
    }

    /**
     * Creates a new ByteArrayActionDownload object.
     *
     * @param  domain             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    protected ByteArrayActionDownload(final String domain, final ConnectionContext connectionContext) {
        this.domain = domain;
        this.connectionContext = connectionContext;
        status = State.WAITING;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }

        status = State.RUNNING;
        stateChanged();

        final byte[] content;
        try {
            content = execAction();
        } catch (final Exception ex) {
            log.warn("Couldn't execute task '" + taskname + "'.", ex);
            error(ex);
            return;
        }

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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected byte[] execAction() throws Exception {
        if ((paramsFuture != null) && (params == null)) {
            params = paramsFuture.get();
        }

        final Object ret = SessionManager.getProxy()
                    .executeTask(
                        taskname,
                        domain,
                        body,
                        getConnectionContext(),
                        params);

        if (ret instanceof Exception) {
            final Exception ex = (Exception)ret;
            throw ex;
        }
        final byte[] content = (byte[])ret;
        return content;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
