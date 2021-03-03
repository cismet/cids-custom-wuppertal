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

import javax.imageio.ImageIO;

import javax.swing.SwingWorker;

import de.cismet.cids.custom.objecteditors.utils.ClientAlboProperties;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.wunda_blau.search.actions.AlboVorgangReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class AlboReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AlboReportGenerator.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   vorgangBean  DOCUMENT ME!
     * @param   isDgk        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static BufferedImage generateOverviewMap(final CidsBean vorgangBean, final boolean isDgk) {
        try {
            final String mapUrl = ClientAlboProperties.getInstance().getVorgangMapUrl();
            Geometry geom = null;
            final Collection<Feature> features = new ArrayList<>();
            for (final CidsBean flaecheBean : vorgangBean.getBeanCollectionProperty("arr_flaechen")) {
                if (flaecheBean != null) {
                    final Geometry flaecheGeom = (Geometry)flaecheBean.getProperty("fk_geom.geo_field");
                    if (flaecheGeom != null) {
                        features.add(new CidsFeature(flaecheBean.getMetaObject()));
                        if (geom == null) {
                            geom = (Geometry)flaecheGeom.buffer(0).clone();
                        } else {
                            geom = geom.union((Geometry)flaecheGeom.buffer(0).clone());
                        }
                    }
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
                        ClientAlboProperties.getInstance().getVorgangMapDpi(),
                        ClientAlboProperties.getInstance().getVorgangMapWidth(),
                        ClientAlboProperties.getInstance().getVorgangMapHeight());
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Error while retrieving map", e);
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vorgangBean        DOCUMENT ME!
     * @param  parent             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startVorgangReportDownload(final CidsBean vorgangBean,
            final Component parent,
            final ConnectionContext connectionContext) {
        try {
            if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(parent)) {
                final String jobname = DownloadManagerDialog.getInstance().getJobName();
                final String vorgang = (String)vorgangBean.getProperty("schluessel");

                new SwingWorker<byte[], Void>() {

                        @Override
                        protected byte[] doInBackground() throws Exception {
                            try(final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                                final BufferedImage image = generateOverviewMap(vorgangBean, true);
                                ImageIO.write(image, "png", bos);
                                return bos.toByteArray();
                            }
                        }

                        @Override
                        protected void done() {
                            final byte[] byteArray;
                            try {
                                byteArray = get();

                                DownloadManager.instance()
                                        .add(new ByteArrayActionDownload(
                                                AlboVorgangReportServerAction.TASK_NAME,
                                                new MetaObjectNode(vorgangBean),
                                                new ServerActionParameter[] {
                                                    new ServerActionParameter(
                                                        AlboVorgangReportServerAction.Parameter.MAP_IMAGE_BYTES
                                                            .toString(),
                                                        byteArray)
                                                },
                                                String.format("Altlastenkataster - Vorgang %s", vorgang),
                                                jobname,
                                                String.format("albo_vorgang_%s", vorgang),
                                                ".pdf",
                                                connectionContext));
                            } catch (final Exception ex) {
                                LOG.error(ex, ex);
                            }
                        }
                    }.execute();
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }
}
