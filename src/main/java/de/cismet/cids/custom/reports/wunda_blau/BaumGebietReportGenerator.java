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
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.Component;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;
import de.cismet.cids.custom.objecteditors.utils.BaumConfProperties;
import de.cismet.cids.custom.wunda_blau.search.actions.BaumGebietReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.features.StyledFeature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import java.awt.Color;
import java.io.IOException;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumGebietReportGenerator {

    //~ Static fields/initializers ---------------------------------------------
    private static final Color FEATURE_COLOR_GEBIET = new Color(0f, 0f, 1f);
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BaumGebietReportGenerator.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   gebietBean  DOCUMENT ME!
     * @param   isDgk        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static BufferedImage generateOverviewMap(final CidsBean gebietBean, final boolean isDgk) {
        try {
            final String mapUrl = BaumConfProperties.getInstance().getUrlDefault();
            Geometry geom = null;
            final Collection<Feature> features = new ArrayList<>();
                final Geometry flaecheGeom = (Geometry)gebietBean.getProperty("fk_geom.geo_field");
                if (flaecheGeom != null) {
                    final StyledFeature dsf = new DefaultStyledFeature();
                    dsf.setGeometry(flaecheGeom);
                    dsf.setFillingPaint(FEATURE_COLOR_GEBIET);
                    dsf.setTransparency(0.5f);
                    features.add(dsf);
                    if (geom == null) {
                        geom = (Geometry)flaecheGeom.buffer(0).clone();
                    } else {
                        geom = geom.union((Geometry)flaecheGeom.buffer(0).clone());
                    }
                }

            final int margin = 50;
            if (geom != null) {
                final XBoundingBox boundingBox = new XBoundingBox(geom);
                boundingBox.increase(10);
                boundingBox.setX1(boundingBox.getX1() - margin);
                boundingBox.setY1(boundingBox.getY1() - margin);
                boundingBox.setX2(boundingBox.getX2() + margin);
                boundingBox.setY2(boundingBox.getY2() + margin);

                final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
                mapProvider.setCenterMapOnResize(true);
                mapProvider.setBoundingBox(boundingBox);
                final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(mapUrl);
                final SimpleWMS simpleWms = new SimpleWMS(getMapUrl);
                mapProvider.addLayer(simpleWms);

                for (final Feature feature : features) {
                    mapProvider.addFeature(feature);
                }

                return (BufferedImage)mapProvider.getImageAndWait(
                        72,
                        BaumConfProperties.getInstance().getGebietMapDpi(),
                        BaumConfProperties.getInstance().getGebietMapWidth(),
                        BaumConfProperties.getInstance().getGebietMapHeight());
            } else {
                return null;
            }
        } catch (IllegalArgumentException | InterruptedException | ExecutionException e) {
            LOG.error("Error while retrieving map", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gebietBean        DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  taskName           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startGebietReportDownload(final CidsBean gebietBean,
            final Component parent,
            final String taskName,
            final ConnectionContext connectionContext) {
        try {
            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                final String az = (String)gebietBean.getProperty("aktenzeichen");

                final Future<ServerActionParameter[]> paramsFuture = new FutureParams(gebietBean);

                DownloadManager.instance()
                        .add(new ByteArrayActionDownload(
                                taskName,
                                new MetaObjectNode(gebietBean),
                                String.format("Baumschutzsatzung - Aktenzeichen %s", az),
                                jobname,
                                String.format("baum_gebiet_%s", az),
                                ".pdf",
                                paramsFuture,
                                connectionContext));
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static class FutureParams implements Future<ServerActionParameter[]> {

        //~ Instance fields ----------------------------------------------------

        private ServerActionParameter[] parameters = null;
        private volatile boolean done = false;
        private volatile boolean cancel = false;
        private final ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new FutureParams object.
         *
         * @param  gebietBean  DOCUMENT ME!
         */
        public FutureParams(final CidsBean gebietBean) {
            final Thread backgroundTask = new Thread() {

                    @Override
                    public void run() {
                        try {
                            final byte[] byteArray;

                            try(final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                                final BufferedImage image = generateOverviewMap(gebietBean, true);
                                ImageIO.write(image, "png", bos);
                                byteArray = bos.toByteArray();
                            }

                            parameters = new ServerActionParameter[] {
                                    new ServerActionParameter(
                                        BaumGebietReportServerAction.Parameter.MAP_IMAGE_BYTES.toString(),
                                        byteArray)
                                };
                        } catch (IOException ex) {
                            LOG.error(ex, ex);
                        }

                        unlock();
                    }
                };

            backgroundTask.start();
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        private void unlock() {
            lock.lock();

            try {
                done = true;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public boolean cancel(final boolean mayInterruptIfRunning) {
            if (done || cancel) {
                return false;
            } else {
                cancel = true;
                return true;
            }
        }

        @Override
        public boolean isCancelled() {
            return cancel;
        }

        @Override
        public boolean isDone() {
            return done || cancel;
        }

        @Override
        public ServerActionParameter[] get() throws InterruptedException, ExecutionException {
            lock.lock();
            try {
                if (!isDone()) {
                    condition.await();
                }

                return parameters;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public ServerActionParameter[] get(final long timeout, final TimeUnit unit) throws InterruptedException,
            ExecutionException,
            TimeoutException {
            lock.lock();
            try {
                if (!isDone()) {
                    if (!condition.await(timeout, unit)) {
                        throw new TimeoutException();
                    }
                }

                return parameters;
            } finally {
                lock.unlock();
            }
        }
    }
}
