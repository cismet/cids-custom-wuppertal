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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.utils.PotenzialflaechenProperties;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheReportGenerator.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  flaecheBean           DOCUMENT ME!
     * @param  selectedTemplateBean  DOCUMENT ME!
     * @param  connectionContext     DOCUMENT ME!
     */
    private static void startDownload(final CidsBean flaecheBean,
            final CidsBean selectedTemplateBean,
            final ConnectionContext connectionContext) {
        if (selectedTemplateBean != null) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = String.format("Potenzialflaeche_%s", (String)flaecheBean.getProperty("nummer"));
            final String downloadTitle = String.format(
                    "%s - %s",
                    (String)selectedTemplateBean.getProperty("bezeichnung"),
                    (String)flaecheBean.getProperty("bezeichnung"));

            new SwingWorker<Download, Void>() {

                    @Override
                    protected Download doInBackground() throws Exception {
                        final BufferedImage[] maps = new BufferedImage[2];
                        maps[0] = generateOverviewMap(flaecheBean, false);
                        maps[1] = generateOverviewMap(flaecheBean, true);
                        try(final ByteArrayOutputStream out0 = new ByteArrayOutputStream();
                                    final ByteArrayOutputStream out1 = new ByteArrayOutputStream()) {
                            final Collection<ServerActionParameter> params = new ArrayList<>();
                            if (maps[0] != null) {
                                ImageIO.write(maps[0], "png", out0);
                                params.add(new ServerActionParameter<>(
                                        PotenzialflaecheReportServerAction.Parameter.IMAGE_ORTHO.toString(),
                                        out0.toByteArray()));
                            }
                            if (maps[1] != null) {
                                ImageIO.write(maps[1], "png", out1);
                                params.add(new ServerActionParameter<>(
                                        PotenzialflaecheReportServerAction.Parameter.IMAGE_DGK.toString(),
                                        out1.toByteArray()));
                            }
                            params.add(new ServerActionParameter<>(
                                    PotenzialflaecheReportServerAction.Parameter.TEMPLATE.toString(),
                                    new MetaObjectNode(selectedTemplateBean)));

                            final Download download = new ByteArrayActionDownload(
                                    PotenzialflaecheReportServerAction.TASK_NAME,
                                    new MetaObjectNode(flaecheBean),
                                    params.toArray(new ServerActionParameter[0]),
                                    downloadTitle,
                                    jobname,
                                    filename,
                                    ".pdf",
                                    connectionContext);
                            return download;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            final Download download = (Download)get();
                            DownloadManager.instance().add(download);
                        } catch (final Exception ex) {
                            LOG.error("Cannot create report", ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Fehler Erstellen des Reports",
                                ex,
                                StaticSwingTools.getFirstParentFrame(
                                    ComponentRegistry.getRegistry().getDescriptionPane()));
                        }
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaecheBean        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startDownload(final CidsBean flaecheBean, final ConnectionContext connectionContext) {
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<Collection<CidsBean>, Void>() {

                    @Override
                    protected Collection<CidsBean> doInBackground() throws Exception {
                        final List<CidsBean> activeTemplateBeans = new ArrayList<>();
                        final Collection<CidsBean> templateBeans = flaecheBean.getBeanCollectionProperty(
                                "kampagne.n_steckbrieftemplates");
                        for (final CidsBean templateBean : templateBeans) {
                            if (templateBean != null) {
                                final String confAttr = (String)templateBean.getProperty("conf_attr");
                                if ((confAttr != null) && !confAttr.trim().isEmpty()) {
                                    if (
                                        SessionManager.getConnection().getConfigAttr(
                                                    SessionManager.getSession().getUser(),
                                                    confAttr,
                                                    connectionContext)
                                                != null) {
                                        activeTemplateBeans.add(templateBean);
                                    }
                                } else {
                                    activeTemplateBeans.add(templateBean);
                                }
                            }
                        }
                        return activeTemplateBeans;
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<CidsBean> activeTemplateBeans = get();

                            final Integer mainTemplateId = (Integer)flaecheBean.getProperty(
                                    "kampagne.haupt_steckbrieftemplate_id");
                            CidsBean mainTemplateBean = (!activeTemplateBeans.isEmpty())
                                ? activeTemplateBeans.iterator().next() : null;
                            for (final CidsBean templateBean : activeTemplateBeans) {
                                if (templateBean.getMetaObject().getId() == mainTemplateId) {
                                    mainTemplateBean = templateBean;
                                }
                            }

                            final CidsBean selectedTemplateBean;
                            if (activeTemplateBeans.size() > 1) {
                                final Object selection = JOptionPane.showInputDialog(ComponentRegistry.getRegistry()
                                                .getDescriptionPane(),
                                        "Wählen Sie die Art des Steckbriefs, der erzeugt werden soll:",
                                        "Steckbriefart auswählen",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        activeTemplateBeans.toArray(new CidsBean[0]),
                                        mainTemplateBean);
                                selectedTemplateBean = (selection instanceof CidsBean) ? (CidsBean)selection : null;
                            } else {
                                selectedTemplateBean = mainTemplateBean;
                            }

                            startDownload(flaecheBean, selectedTemplateBean, connectionContext);
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Fehler Erstellen des Reports",
                                ex,
                                StaticSwingTools.getFirstParentFrame(
                                    ComponentRegistry.getRegistry().getDescriptionPane()));
                        }
                    }
                }.execute();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   flaecheBean  isDgk DOCUMENT ME!
     * @param   isDgk        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static BufferedImage generateOverviewMap(final CidsBean flaecheBean, final boolean isDgk) {
        try {
            final String urlBackground = (isDgk ? PotenzialflaechenProperties.getInstance().getDgkUrl()
                                                : PotenzialflaechenProperties.getInstance().getOrthoUrl());
            final Geometry geom = (Geometry)flaecheBean.getProperty("geometrie.geo_field");

            if (geom != null) {
                final XBoundingBox boundingBox = new XBoundingBox(geom);
                boundingBox.increase(10);
                boundingBox.setX1(boundingBox.getX1() - 50);
                boundingBox.setY1(boundingBox.getY1() - 50);
                boundingBox.setX2(boundingBox.getX2() + 50);
                boundingBox.setY2(boundingBox.getY2() + 50);

                final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
                mapProvider.setCenterMapOnResize(true);
                mapProvider.setBoundingBox(boundingBox);
                final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(urlBackground);
                final SimpleWMS simpleWms = new SimpleWMS(getMapUrl);
                mapProvider.addLayer(simpleWms);
                final DefaultStyledFeature f = new DefaultStyledFeature();
                f.setGeometry(geom);
                f.setHighlightingEnabled(true);
                f.setLinePaint(Color.RED);
                f.setLineWidth(3);
                mapProvider.addFeature(f);

                return (BufferedImage)mapProvider.getImageAndWait(72, 300, 250, 150);
            } else {
                return null;
            }
        } catch (final Exception e) {
            LOG.error("Error while retrieving map", e);
            return null;
        }
    }
}
