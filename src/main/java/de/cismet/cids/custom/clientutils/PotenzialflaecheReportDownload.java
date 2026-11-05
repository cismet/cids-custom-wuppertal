/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.clientutils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObjectNode;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.utils.PotenzialflaechenProperties;
import de.cismet.cids.custom.utils.WundaBlauServerResources;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.GetAsyncTaskProgressAction;
import de.cismet.cids.server.actions.GetAsyncTaskResultAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.StartAsyncTaskAction;

import de.cismet.cids.utils.serverresources.PropertiesServerResource;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@Getter
public class PotenzialflaecheReportDownload extends AbstractSecresDownload {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Type {

        //~ Enum constants -----------------------------------------------------

        FLAECHE, KATEGORIE
    }

    //~ Instance fields --------------------------------------------------------

    private final Type type;
    private final boolean zip;
    private final Collection<CidsBean> beans;
    private final CidsBean templateBean;
    private final PotenzialflaechenProperties potenzialflaecheProperties = new PotenzialflaechenProperties();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PotenzialflaecheReportDownload object.
     *
     * @param   type               DOCUMENT ME!
     * @param   zip                DOCUMENT ME!
     * @param   templateBean       DOCUMENT ME!
     * @param   beans              DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public PotenzialflaecheReportDownload(
            final Type type,
            final boolean zip,
            final CidsBean templateBean,
            final Collection<CidsBean> beans,
            final ConnectionContext connectionContext) throws Exception {
        super(
            getTitle(type, beans, templateBean),
            DownloadManagerDialog.getInstance().getJobName(),
            getTargetFileBasename(type, beans, templateBean),
            zip ? ".zip" : ".pdf",
            connectionContext);

        getPotenzialflaecheProperties().setProperties(ServerResourcesLoaderClient.getInstance().loadProperties(
                (PropertiesServerResource)WundaBlauServerResources.POTENZIALFLAECHEN_PROPERTIES.getValue()));

        this.type = type;
        this.zip = zip;
        this.beans = beans;
        this.templateBean = templateBean;

        if (this.templateBean == null) {
            throw new Exception("templateBean has to be given");
        }
        if ((this.beans == null) || this.beans.isEmpty()) {
            throw new Exception("flaecheBeans xor kategorieBeans has to be given");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   type          DOCUMENT ME!
     * @param   beans         DOCUMENT ME!
     * @param   templateBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getTargetFileBasename(final Type type,
            final Collection<CidsBean> beans,
            final CidsBean templateBean) {
        final CidsBean singleFlaecheBean = getSingleFlaeche(type, beans);
        final CidsBean singleKategorieBean = getSingleKategorie(type, beans);
        final String baseName = String.format(
                "%s_%s",
                (String)templateBean.getProperty("bezeichnung"),
                (singleFlaecheBean != null)
                    ? (String)singleFlaecheBean.getProperty("nummer")
                    : ((singleKategorieBean != null) ? (String)singleKategorieBean.getProperty("bezeichnung")
                                                     : "diverse_flaechen"));
        return baseName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type   DOCUMENT ME!
     * @param   beans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getSingleFlaeche(final Type type, final Collection<CidsBean> beans) {
        return (Type.FLAECHE.equals(type)) ? getSingleBean(beans) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type   DOCUMENT ME!
     * @param   beans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getSingleKategorie(final Type type, final Collection<CidsBean> beans) {
        return (Type.KATEGORIE.equals(type)) ? getSingleBean(beans) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   beans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static CidsBean getSingleBean(final Collection<CidsBean> beans) {
        return ((beans != null) && (beans.size() == 1)) ? beans.iterator().next() : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   type          DOCUMENT ME!
     * @param   beans         DOCUMENT ME!
     * @param   templateBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getTitle(final Type type, final Collection<CidsBean> beans, final CidsBean templateBean) {
        final CidsBean singleFlaecheBean = getSingleFlaeche(type, beans);
        final CidsBean singleKategorieBean = getSingleKategorie(type, beans);
        return String.format(
                "%s - %s",
                (String)templateBean.getProperty("bezeichnung"),
                (singleFlaecheBean != null)
                    ? (String)singleFlaecheBean.getProperty("bezeichnung")
                    : ((singleKategorieBean != null) ? (String)singleKategorieBean.getProperty("bezeichnung")
                                                     : "Potenzialflächen"));
    }

    @Override
    public String getSecresPath() throws Exception {
        final Collection<ServerActionParameter> params = new ArrayList<>();
        final Collection<CidsBean> beans = getBeans();
        final Type type = getType();
        if ((beans != null) && (type != null)) {
            for (final CidsBean flaecheBean : beans) {
                params.add(new ServerActionParameter(
                        (Type.FLAECHE.equals(type) ? PotenzialflaecheReportServerAction.Parameter.POTENZIALFLAECHE
                                                   : PotenzialflaecheReportServerAction.Parameter.KATEGORIE).toString(),
                        new MetaObjectNode(flaecheBean)));
            }
        }
        params.add(new ServerActionParameter(
                PotenzialflaecheReportServerAction.Parameter.TEMPLATE.toString(),
                new MetaObjectNode(templateBean)));
        params.add(new ServerActionParameter(
                PotenzialflaecheReportServerAction.Parameter.RESULT_TYPE.toString(),
                isZip() ? PotenzialflaecheReportServerAction.ResultType.ZIP
                        : PotenzialflaecheReportServerAction.ResultType.PDF));

        final Object ret = asyncDownload(params.toArray(new ServerActionParameter[0]),
                PotenzialflaecheReportServerAction.TASK_NAME);

//        final Object ret = SessionManager.getProxy()
//                    .executeTask(
//                        PotenzialflaecheReportServerAction.TASK_NAME,
//                        "WUNDA_BLAU",
//                        null,
//                        getConnectionContext(),
//                        params.toArray(new ServerActionParameter[0]));
        if (ret instanceof Exception) {
            final Exception ex = (Exception)ret;
            throw ex;
        }
        return (String)ret;
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
    public String getSecresApiBasePath() throws Exception {
        return getPotenzialflaecheProperties().getSecresApi();
    }

    @Override
    public String getSecresKey() throws Exception {
        return getPotenzialflaecheProperties().getSecresKey();
    }
}
