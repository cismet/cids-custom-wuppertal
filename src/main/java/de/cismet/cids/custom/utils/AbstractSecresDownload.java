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
package de.cismet.cids.custom.utils;

/**
 *
 * @author jruiz
 */
import Sirius.navigator.connection.SessionManager;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;

import java.net.URL;

import javax.swing.SwingUtilities;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public abstract class AbstractSecresDownload extends AbstractCancellableDownload implements SecresDownload,
    ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = log;
    private static final String SECRES_FORMAT = "%s/secres/%s/%s/%s";

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractSecresDownload object.
     *
     * @param  title                DOCUMENT ME!
     * @param  directory            DOCUMENT ME!
     * @param  targetFileBasename   DOCUMENT ME!
     * @param  targetFileExtension  DOCUMENT ME!
     * @param  connectionContext    DOCUMENT ME!
     */
    public AbstractSecresDownload(final String title,
            final String directory,
            final String targetFileBasename,
            final String targetFileExtension,
            final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        super.title = title;
        setDirectory(directory);
        determineDestinationFile(targetFileBasename, targetFileExtension);

        this.status = State.WAITING;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  directory  DOCUMENT ME!
     */
    protected final void setDirectory(final String directory) {
        this.directory = directory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    protected final void setStatus(final State status) {
        this.status = status;
        stateChanged();
    }
    @Override
    public void run() {
        if (!State.WAITING.equals(getStatus())) {
            return;
        }

        setStatus(State.RUNNING);
        try(final FileOutputStream outputStream = new FileOutputStream(fileToSaveTo)) {
            final URL secresUrl = getSecresURL();
            IOUtils.copyLarge(WebAccessManager.getInstance().doRequest(secresUrl), outputStream);
        } catch (final Exception ex) {
            LOG.warn(String.format("Couldn't write downloaded content to file '%s'.", getFileToSaveTo()), ex);
            super.error(ex);
            return;
        }

        if (State.RUNNING.equals(getStatus())) {
            setStatus(State.COMPLETED);
            stateChanged();
        }
    }

    @Override
    public URL getSecresURL() throws Exception {
        final String jwt = SessionManager.getSession().getUser().getJwsToken();
        final String secresKey = getSecresKey();
        final String secresApiBasePath = getSecresApiBasePath();
        final String secresPath = getSecresPath();

        return new URL(String.format(SECRES_FORMAT, secresApiBasePath, jwt, secresKey, secresPath));
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract String getSecresPath() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract String getSecresApiBasePath() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract String getSecresKey() throws Exception;
}
