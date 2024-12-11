/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.clientutils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.actions.PreparedAsyncDownloadHelper;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.concurrent.Future;

import de.cismet.cids.server.actions.PreparedAsyncByteAction;
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

    //~ Static fields/initializers ---------------------------------------------

    private static final int MIN_LENGTH_TO_SHOW_PROGRESS = 10000;

    //~ Instance fields --------------------------------------------------------

    protected String taskname;
    protected Object body;
    protected ServerActionParameter[] params;
    protected Future<ServerActionParameter[]> paramsFuture;

    private final String domain;

    private final ConnectionContext connectionContext;
    private volatile int progress = 0;

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

        final Object contentObject;

        try {
            contentObject = execAction();
        } catch (final Exception ex) {
            log.warn("Couldn't execute task '" + taskname + "'.", ex);
            error(ex);
            return;
        }

        if (contentObject instanceof PreparedAsyncByteAction) {
            FileOutputStream out = null;

            try {
                out = new FileOutputStream(fileToSaveTo);
                PreparedAsyncDownloadHelper.PreparedAsyncDownloadMonitor monitor = null;
                final long fileLength = ((PreparedAsyncByteAction)contentObject).getLength();

                if (fileLength > MIN_LENGTH_TO_SHOW_PROGRESS) {
                    monitor = new PreparedAsyncDownloadHelper.PreparedAsyncDownloadMonitor() {

                            @Override
                            public void progress(final long bytesRead) {
                                final int prog = (int)(bytesRead * 100 / fileLength);

                                if (prog > (progress + 5)) {
                                    progress = prog;
                                    status = State.RUNNING_WITH_PROGRESS;
                                    stateChanged();
                                }
                            }
                        };
                }

                PreparedAsyncDownloadHelper.download((PreparedAsyncByteAction)contentObject, out, monitor);
            } catch (final Exception ex) {
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
        } else {
            final byte[] content = (byte[])contentObject;

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
        }

        if ((status == State.RUNNING) || (status == State.RUNNING_WITH_PROGRESS)) {
            status = State.COMPLETED;
            stateChanged();
        }
    }

    @Override
    public int getProgress() {
        return progress;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected Object execAction() throws Exception {
        if ((paramsFuture != null) && (params == null)) {
            params = paramsFuture.get();
        }

        final Object ret = SessionManager.getProxy()
                    .executeTask(
                        taskname,
                        domain,
                        body,
                        getConnectionContext(),
                        false,
                        params);

        if (ret instanceof Exception) {
            final Exception ex = (Exception)ret;
            throw ex;
        }

        return ret;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
