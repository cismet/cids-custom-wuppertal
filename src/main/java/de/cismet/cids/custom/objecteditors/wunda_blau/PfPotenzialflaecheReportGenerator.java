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

import org.apache.commons.collections.map.MultiValueMap;

import org.openide.util.NbBundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.custom.actions.wunda_blau.SetTIMNoteAction;
import de.cismet.cids.custom.clientutils.PotenzialflaecheReportDownload;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.actions.DeletePotenzialflaecheReportCacheServerAction;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.interaction.CismapBroker;

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
     * @param  kategorieBean         DOCUMENT ME!
     * @param  selectedTemplateBean  DOCUMENT ME!
     * @param  connectionContext     DOCUMENT ME!
     */
    private static void startZipDownload(final CidsBean kategorieBean,
            final CidsBean selectedTemplateBean,
            final ConnectionContext connectionContext) {
        if (selectedTemplateBean != null) {
            try {
                final Download download = new PotenzialflaecheReportDownload(
                        PotenzialflaecheReportDownload.Type.KATEGORIE,
                        false,
                        selectedTemplateBean,
                        Arrays.asList(kategorieBean),
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
     * @param   kategorieBean      DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Collection<CidsBean> identifyActiveTemplateBeans(final CidsBean kategorieBean,
            final ConnectionContext connectionContext) throws Exception {
        return (kategorieBean != null)
            ? (Collection<CidsBean>)identifyActiveTemplateBeansMap(Arrays.asList(kategorieBean), connectionContext)
                    .get(kategorieBean) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   kategorieBeans     DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static MultiValueMap identifyActiveTemplateBeansMap(
            final Collection<CidsBean> kategorieBeans,
            final ConnectionContext connectionContext) throws Exception {
        final MultiValueMap activeTemplateBeansMap = new MultiValueMap();

        if (kategorieBeans != null) {
            for (final CidsBean kategorieBean : kategorieBeans) {
                if (kategorieBean != null) {
                    for (final CidsBean templateBean : kategorieBean.getBeanCollectionProperty("n_steckbrieftemplates")) {
                        if (templateBean != null) {
                            final String confAttr = (String)templateBean.getProperty("conf_attr");
                            if ((confAttr != null) && !confAttr.trim().isEmpty()) {
                                if (SessionManager.getConnection().hasConfigAttr(
                                                SessionManager.getSession().getUser(),
                                                confAttr,
                                                connectionContext)) {
                                    activeTemplateBeansMap.put(kategorieBean, templateBean);
                                }
                            } else {
                                activeTemplateBeansMap.put(kategorieBean, templateBean);
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
     * @param  kategorieBean      flaecheBean DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public static void startDownloadForKategorie(final CidsBean kategorieBean,
            final ConnectionContext connectionContext) {
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<Collection<CidsBean>, Void>() {

                    @Override
                    protected Collection<CidsBean> doInBackground() throws Exception {
                        return identifyActiveTemplateBeans(kategorieBean, connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<CidsBean> activeTemplateBeans = get();
                            final Integer mainTemplateId = (Integer)kategorieBean.getProperty(
                                    "haupt_steckbrieftemplate_id");
                            startZipDownload(
                                kategorieBean,
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
        final CidsBean kategorieBean = (flaecheBean != null) ? (CidsBean)flaecheBean.getProperty("kampagne") : null;
        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<Collection<CidsBean>, Void>() {

                    @Override
                    protected Collection<CidsBean> doInBackground() throws Exception {
                        return identifyActiveTemplateBeans(kategorieBean, connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<CidsBean> activeTemplateBeans = get();
                            final Integer mainTemplateId = (Integer)kategorieBean.getProperty(
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
    public static void deleteDownloadCacheForFlaechen(final Collection<CidsBean> flaecheBeans,
            final ConnectionContext connectionContext) {
        if (flaecheBeans != null) {
            new SwingWorker<Boolean, Void>() {

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        final List<Integer> idList = new ArrayList<>();

                        for (final CidsBean tmpFlaeche : flaecheBeans) {
                            final Integer pk = tmpFlaeche.getPrimaryKeyValue();
                            idList.add(pk);
                        }

                        final Collection<ServerActionParameter> params = new ArrayList<>();

                        params.add(new ServerActionParameter(
                                DeletePotenzialflaecheReportCacheServerAction.Parameter.POTENZIALFLAECHE.toString(),
                                idList.toArray(new Integer[idList.size()])));

                        final Object ret = SessionManager.getProxy()
                                    .executeTask(
                                        DeletePotenzialflaecheReportCacheServerAction.TASK_NAME,
                                        "WUNDA_BLAU",
                                        null,
                                        connectionContext,
                                        params.toArray(new ServerActionParameter[0]));

                        return (Boolean)ret;
                    }

                    @Override
                    protected void done() {
                        try {
                            final Boolean success = get();

                            if (!success) {
                                JOptionPane.showMessageDialog(
                                    StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()),
                                    NbBundle.getMessage(
                                        SetTIMNoteAction.class,
                                        "PfPotenzialflaecheReportGenerator.deleteDownloadCacheForFlaechen().success.message"),
                                    NbBundle.getMessage(
                                        SetTIMNoteAction.class,
                                        "PfPotenzialflaecheReportGenerator.deleteDownloadCacheForFlaechen().success.title"),
                                    JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(
                                    StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()),
                                    NbBundle.getMessage(
                                        SetTIMNoteAction.class,
                                        "PfPotenzialflaecheReportGenerator.deleteDownloadCacheForFlaechen().error.message"),
                                    NbBundle.getMessage(
                                        SetTIMNoteAction.class,
                                        "PfPotenzialflaecheReportGenerator.deleteDownloadCacheForFlaechen().error.title"),
                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            ObjectRendererUtils.showExceptionWindowToUser(
                                "Fehler Löschen des Caches",
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
        final MultiValueMap flaecheToKategorieMaps = new MultiValueMap();
        for (final CidsBean flaecheBean : flaecheBeans) {
            flaecheToKategorieMaps.put((CidsBean)flaecheBean.getProperty("kampagne"), flaecheBean);
        }

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            new SwingWorker<MultiValueMap, Void>() {

                    @Override
                    protected MultiValueMap doInBackground() throws Exception {
                        return identifyActiveTemplateBeansMap(flaecheToKategorieMaps.keySet(), connectionContext);
                    }

                    @Override
                    protected void done() {
                        try {
                            final MultiValueMap activeTemplateBeansMap = get();
                            for (final CidsBean kategorieBean : (Set<CidsBean>)activeTemplateBeansMap.keySet()) {
                                final Collection<CidsBean> activeTemplateBeans = activeTemplateBeansMap.getCollection(
                                        kategorieBean);
                                final Integer mainTemplateId = (Integer)kategorieBean.getProperty(
                                        "haupt_steckbrieftemplate_id");
                                final CidsBean templateBean = askForSelection(mainTemplateId, activeTemplateBeans);
                                startZipDownload(
                                    flaecheToKategorieMaps.getCollection(kategorieBean),
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
