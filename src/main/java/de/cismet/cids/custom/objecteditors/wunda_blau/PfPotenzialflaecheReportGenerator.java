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

import org.apache.commons.collections.map.MultiValueMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.utils.PotenzialflaecheReportDownload;

import de.cismet.cids.dynamics.CidsBean;

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
     * @param  kampagneBean          DOCUMENT ME!
     * @param  selectedTemplateBean  DOCUMENT ME!
     * @param  connectionContext     DOCUMENT ME!
     */
    private static void startZipDownload(final CidsBean kampagneBean,
            final CidsBean selectedTemplateBean,
            final ConnectionContext connectionContext) {
        if (selectedTemplateBean != null) {
            try {
                final Download download = new PotenzialflaecheReportDownload(
                        PotenzialflaecheReportDownload.Type.KAMPAGNE,
                        false,
                        selectedTemplateBean,
                        Arrays.asList(kampagneBean),
                        connectionContext);
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
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaecheBeans          DOCUMENT ME!
     * @param  selectedTemplateBean  DOCUMENT ME!
     * @param  connectionContext     DOCUMENT ME!
     */
    private static void startZipDownload(final Collection<CidsBean> flaecheBeans,
            final CidsBean selectedTemplateBean,
            final ConnectionContext connectionContext) {
        if (selectedTemplateBean != null) {
            try {
                final Download download = new PotenzialflaecheReportDownload(
                        PotenzialflaecheReportDownload.Type.FLAECHE,
                        false,
                        selectedTemplateBean,
                        flaecheBeans,
                        connectionContext);
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
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flaecheBean           DOCUMENT ME!
     * @param  selectedTemplateBean  DOCUMENT ME!
     * @param  connectionContext     DOCUMENT ME!
     */
    private static void startPdfDownload(final CidsBean flaecheBean,
            final CidsBean selectedTemplateBean,
            final ConnectionContext connectionContext) {
        if (selectedTemplateBean != null) {
            try {
                final Download download = new PotenzialflaecheReportDownload(
                        PotenzialflaecheReportDownload.Type.FLAECHE,
                        false,
                        selectedTemplateBean,
                        Arrays.asList(flaecheBean),
                        connectionContext);
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
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kampagneBean       DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Collection<CidsBean> identifyActiveTemplateBeans(final CidsBean kampagneBean,
            final ConnectionContext connectionContext) throws Exception {
        return (kampagneBean != null)
            ? (Collection<CidsBean>)identifyActiveTemplateBeansMap(Arrays.asList(kampagneBean), connectionContext)
                    .get(kampagneBean) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kampagneBeans      DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static MultiValueMap identifyActiveTemplateBeansMap(
            final Collection<CidsBean> kampagneBeans,
            final ConnectionContext connectionContext) throws Exception {
        final MultiValueMap activeTemplateBeansMap = new MultiValueMap();

        if (kampagneBeans != null) {
            for (final CidsBean kampagneBean : kampagneBeans) {
                if (kampagneBean != null) {
                    for (final CidsBean templateBean : kampagneBean.getBeanCollectionProperty("n_steckbrieftemplates")) {
                        if (templateBean != null) {
                            final String confAttr = (String)templateBean.getProperty("conf_attr");
                            if ((confAttr != null) && !confAttr.trim().isEmpty()) {
                                if (SessionManager.getConnection().hasConfigAttr(
                                                SessionManager.getSession().getUser(),
                                                confAttr,
                                                connectionContext)) {
                                    activeTemplateBeansMap.put(kampagneBean, templateBean);
                                }
                            } else {
                                activeTemplateBeansMap.put(kampagneBean, templateBean);
                            }
                        }
                    }
                }
            }
        }
        return activeTemplateBeansMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   mainTemplateId       DOCUMENT ME!
     * @param   activeTemplateBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean askForSelection(final Integer mainTemplateId,
            final Collection<CidsBean> activeTemplateBeans) {
        CidsBean mainTemplateBean = (!activeTemplateBeans.isEmpty()) ? activeTemplateBeans.iterator().next() : null;
        for (final CidsBean templateBean : activeTemplateBeans) {
            if (templateBean.getMetaObject().getId() == mainTemplateId) {
                mainTemplateBean = templateBean;
            }
        }

        final CidsBean selectedTemplateBean;
        if (activeTemplateBeans.size() > 1) {
            final Object selection = JOptionPane.showInputDialog(ComponentRegistry.getRegistry().getDescriptionPane(),
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
        return selectedTemplateBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kampagneBean       flaecheBean DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startDownloadForKampagne(final CidsBean kampagneBean,
            final ConnectionContext connectionContext) {
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<Collection<CidsBean>, Void>() {

                    @Override
                    protected Collection<CidsBean> doInBackground() throws Exception {
                        return identifyActiveTemplateBeans(kampagneBean, connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<CidsBean> activeTemplateBeans = get();
                            final Integer mainTemplateId = (Integer)kampagneBean.getProperty(
                                    "haupt_steckbrieftemplate_id");
                            startZipDownload(
                                kampagneBean,
                                askForSelection(mainTemplateId, activeTemplateBeans),
                                connectionContext);
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
     * @param  flaecheBean        DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startDownloadForFlaeche(final CidsBean flaecheBean, final ConnectionContext connectionContext) {
        final CidsBean kampagneBean = (flaecheBean != null) ? (CidsBean)flaecheBean.getProperty("kampagne") : null;
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<Collection<CidsBean>, Void>() {

                    @Override
                    protected Collection<CidsBean> doInBackground() throws Exception {
                        return identifyActiveTemplateBeans(kampagneBean, connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<CidsBean> activeTemplateBeans = get();
                            final Integer mainTemplateId = (Integer)kampagneBean.getProperty(
                                    "haupt_steckbrieftemplate_id");
                            startPdfDownload(
                                flaecheBean,
                                askForSelection(mainTemplateId, activeTemplateBeans),
                                connectionContext);
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
     * @param  flaecheBeans       DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startDownloadForFlaechen(final Collection<CidsBean> flaecheBeans,
            final ConnectionContext connectionContext) {
        final MultiValueMap flaecheToKampagneMaps = new MultiValueMap();
        for (final CidsBean flaecheBean : flaecheBeans) {
            flaecheToKampagneMaps.put((CidsBean)flaecheBean.getProperty("kampagne"), flaecheBean);
        }

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<MultiValueMap, Void>() {

                    @Override
                    protected MultiValueMap doInBackground() throws Exception {
                        return identifyActiveTemplateBeansMap(flaecheToKampagneMaps.keySet(), connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final MultiValueMap activeTemplateBeansMap = get();
                            for (final CidsBean kampagneBean : (Set<CidsBean>)activeTemplateBeansMap.keySet()) {
                                final Collection<CidsBean> activeTemplateBeans = activeTemplateBeansMap.getCollection(
                                        kampagneBean);
                                final Integer mainTemplateId = (Integer)kampagneBean.getProperty(
                                        "haupt_steckbrieftemplate_id");
                                final CidsBean templateBean = askForSelection(mainTemplateId, activeTemplateBeans);
                                startZipDownload(
                                    flaecheToKampagneMaps.getCollection(kampagneBean),
                                    templateBean,
                                    connectionContext);
                            }
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
}
