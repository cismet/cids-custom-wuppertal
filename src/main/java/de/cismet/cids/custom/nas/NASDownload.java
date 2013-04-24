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
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import de.aedsicad.aaaweb.service.util.Point;

import org.openide.util.Exceptions;

import java.io.FileOutputStream;
import java.io.IOException;

import de.cismet.cids.custom.actions.wunda_blau.NASDataRetrievalAction;
import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

import static de.cismet.cids.custom.objectrenderer.utils.alkis.AlkisUtils.ALKIS_SOAP_OVER_CSA;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class NASDownload extends AbstractDownload {

    //~ Static fields/initializers ---------------------------------------------

    private static String SEVER_ACTION = "nasDataQuery";

    //~ Instance fields --------------------------------------------------------

    final String givenTemplate;
    Geometry geometry;
    private transient byte[] content;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDownload object.
     *
     * @param  title      DOCUMENT ME!
     * @param  directory  DOCUMENT ME!
     * @param  filename   DOCUMENT ME!
     * @param  extension  DOCUMENT ME!
     * @param  template   DOCUMENT ME!
     * @param  g          DOCUMENT ME!
     */
    public NASDownload(final String title,
            final String directory,
            final String filename,
            final String extension,
            final String template,
            final Geometry g) {
        givenTemplate = template;
        geometry = g;
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
        NasDataQueryAction.PRODUCT_TEMPLATE template;
        if (givenTemplate.equals("Komplett")) {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.KOMPLETT;
        } else if (givenTemplate.equals("Ohne Eigentuemer")) {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.OHNE_EIGENTUEMER;
        } else {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.POINTS;
        }
        final ServerActionParameter paramTemplate = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.TEMPLATE
                        .toString(),
                template);
        final ServerActionParameter paramGeom = new ServerActionParameter(NasDataQueryAction.PARAMETER_TYPE.GEOMETRY
                        .toString(),
                geometry);
        try {
            content = (byte[])SessionManager.getProxy()
                        .executeTask(
                                SEVER_ACTION,
                                "WUNDA_BLAU",
                                null,
                                paramTemplate,
                                paramGeom);
        } catch (ConnectionException ex) {
            Exceptions.printStackTrace(ex);
        }
//        final NASProductGenerator pg = new NASProductGenerator();
//        content = pg.executeAsynchQuery(template, geometry);
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
