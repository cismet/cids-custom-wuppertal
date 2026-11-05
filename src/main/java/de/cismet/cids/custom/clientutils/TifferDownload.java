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
package de.cismet.cids.custom.clientutils;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

import java.awt.image.RenderedImage;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.wunda_blau.search.actions.ImageAnnotator;
import de.cismet.cids.custom.wunda_blau.search.actions.TifferAction;
import de.cismet.cids.custom.wunda_blau.search.server.MetaObjectNodesStadtbildSerieSearchStatement;

import de.cismet.cids.server.actions.GetAsyncTaskProgressAction;
import de.cismet.cids.server.actions.GetAsyncTaskResultAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.StartAsyncTaskAction;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.downloadmanager.AbstractDownload;

/**
 * A download which uses TifferAction to annotate an image.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 * @see      TifferAction
 * @see      ImageAnnotator
 */
public class TifferDownload extends AbstractDownload implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TifferDownload.class);

    //~ Instance fields --------------------------------------------------------

    private final StadtbilderUtils.StadtbildInfo stadtbildInfo;
    private final String scale;
    private final String format;

    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TifferDownload object.
     *
     * @param  directory          DOCUMENT ME!
     * @param  title              DOCUMENT ME!
     * @param  filename           DOCUMENT ME!
     * @param  stadtbildInfo      DOCUMENT ME!
     * @param  scale              DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TifferDownload(final String directory,
            final String title,
            final String filename,
            final StadtbilderUtils.StadtbildInfo stadtbildInfo,
            final String scale,
            final ConnectionContext connectionContext) {
        this.stadtbildInfo = stadtbildInfo;
        this.directory = directory;
        this.title = title;
        this.scale = scale;
        this.connectionContext = connectionContext;

        status = State.WAITING;

        format =
            (MetaObjectNodesStadtbildSerieSearchStatement.Bildtyp.REIHENSCHRAEG.getId() == stadtbildInfo.getBildtypId())
            ? "jpg" : StadtbilderUtils.getFormatOfHighResPicture(stadtbildInfo);
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

        final String imageNumber = stadtbildInfo.getBildnummer();
        final Integer bildtypId = stadtbildInfo.getBildtypId();
        final String blickrichtung = stadtbildInfo.getBlickrichtung();
        final Integer jahr = stadtbildInfo.getJahr();
        final char firstCharacter = imageNumber.charAt(0);
        final String subdir = "SB/" + firstCharacter + "/" + "SB_";

        final ServerActionParameter paramNummer = new ServerActionParameter(
                TifferAction.ParameterType.BILDNUMMER.toString(),
                imageNumber);
        final ServerActionParameter paramScale = new ServerActionParameter(
                TifferAction.ParameterType.SCALE.toString(),
                scale);
        final ServerActionParameter paramFormat = new ServerActionParameter(
                TifferAction.ParameterType.FORMAT.toString(),
                format);
        final ServerActionParameter paramSubdir = new ServerActionParameter(
                TifferAction.ParameterType.SUBDIR.toString(),
                subdir);
        final ServerActionParameter paramArt = new ServerActionParameter(
                TifferAction.ParameterType.BILDTYP_ID.toString(),
                bildtypId);
        final ServerActionParameter paramJahr = new ServerActionParameter(
                TifferAction.ParameterType.JAHR.toString(),
                jahr);
        final ServerActionParameter paramBlickrichtung = new ServerActionParameter(
                TifferAction.ParameterType.BLICKRICHTUNG.toString(),
                blickrichtung);

        try {
            final ServerActionParameter[] params = new ServerActionParameter[7];

            params[0] = paramNummer;
            params[1] = paramScale;
            params[2] = paramFormat;
            params[3] = paramSubdir;
            params[4] = paramArt;
            params[5] = paramJahr;
            params[6] = paramBlickrichtung;

            final byte[] result = (byte[])asyncDownload(params, TifferAction.ACTION_NAME);

//            final byte[] result = (byte[])SessionManager.getProxy()
//                        .executeTask(
//                                TifferAction.ACTION_NAME,
//                                "WUNDA_BLAU",
//                                (Object)null,
//                                getConnectionContext(),
//                                paramNummer,
//                                paramScale,
//                                paramFormat,
//                                paramSubdir,
//                                paramArt,
//                                paramJahr,
//                                paramBlickrichtung);
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
     * DOCUMENT ME!
     *
     * @param   params    DOCUMENT ME!
     * @param   taskname  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private Object asyncDownload(final ServerActionParameter[] params, final String taskname) throws Exception {
        final ServerActionParameter[] newParams =
            new ServerActionParameter[((params != null) ? (params.length + 1) : 1)];

        if (params != null) {
            for (int i = 0; i < params.length; ++i) {
                newParams[i] = params[i];
            }
        }

        newParams[((params != null) ? params.length : 0)] = new ServerActionParameter(
                StartAsyncTaskAction.Parameter.TASKNAME.name(),
                taskname);

        final String uuid = (String)SessionManager.getProxy()
                    .executeTask(
                            StartAsyncTaskAction.TASK_NAME,
                            "WUNDA_BLAU",
                            (Object)null,
                            getConnectionContext(),
                            false,
                            newParams);

        Integer progress = 0;

        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // nothing to do
            }

            progress = (Integer)SessionManager.getProxy()
                        .executeTask(
                                GetAsyncTaskProgressAction.TASK_NAME,
                                "WUNDA_BLAU",
                                (Object)null,
                                getConnectionContext(),
                                false,
                                new ServerActionParameter(GetAsyncTaskProgressAction.Parameter.UUID.name(), uuid));
        } while (progress != 100);

        return SessionManager.getProxy()
                    .executeTask(
                        GetAsyncTaskResultAction.TASK_NAME,
                        "WUNDA_BLAU",
                        (Object)null,
                        getConnectionContext(),
                        false,
                        new ServerActionParameter(GetAsyncTaskProgressAction.Parameter.UUID.name(), uuid));
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
