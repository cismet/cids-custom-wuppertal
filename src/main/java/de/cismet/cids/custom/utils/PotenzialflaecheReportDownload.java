/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.utils;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.utils.serverresources.PropertiesServerResource;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.gui.downloadmanager.AbstractCancellableDownload;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class PotenzialflaecheReportDownload extends AbstractCancellableDownload implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final String SECRES_FORMAT = "%s/secres/%s/%s/%s";

    //~ Instance fields --------------------------------------------------------

    private final ConnectionContext connectionContext;
    private final Collection<CidsBean> flaecheBeans;
    private final Collection<CidsBean> kampagneBeans;
    private final CidsBean templateBean;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PotenzialflaecheReportDownload object.
     *
     * @param   templateBean       DOCUMENT ME!
     * @param   flaecheBeans       DOCUMENT ME!
     * @param   kampagneBeans      DOCUMENT ME!
     * @param   directory          DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public PotenzialflaecheReportDownload(
            final CidsBean templateBean,
            final Collection<CidsBean> flaecheBeans,
            final Collection<CidsBean> kampagneBeans,
            final String directory,
            final ConnectionContext connectionContext) throws Exception {
        super.directory = directory;
        this.flaecheBeans = flaecheBeans;
        this.kampagneBeans = kampagneBeans;
        this.templateBean = templateBean;
        this.connectionContext = connectionContext;

        if (templateBean == null) {
            throw new Exception("templateBean has to be given");
        }
        if (((flaecheBeans != null) && (kampagneBeans != null))
                    || ((flaecheBeans == null) && (kampagneBeans == null))) {
            throw new Exception("flaecheBeans xor kampagneBeans has to be given");
        }

        status = State.WAITING;

        final boolean singleFlaeche = (flaecheBeans != null) && (flaecheBeans.size() == 1);
        final boolean singleKampagne = (kampagneBeans != null) && (kampagneBeans.size() == 1);

        final CidsBean flaecheBean = singleFlaeche ? flaecheBeans.iterator().next() : null;
        final CidsBean kampagneBean = singleKampagne ? kampagneBeans.iterator().next() : null;

        super.title = String.format(
                "%s - %s",
                singleFlaeche ? (String)flaecheBean.getProperty("bezeichnung")
                              : (singleKampagne ? (String)kampagneBean.getProperty("bezeichnung") : "Potenzialflächen"),
                (String)templateBean.getProperty("bezeichnung"));

        final String fileName = String.format(
                "%s_%s",
                (String)kampagneBean.getProperty("bezeichnung"),
                singleFlaeche
                    ? (String)flaecheBean.getProperty("nummer")
                    : (singleKampagne ? (String)kampagneBean.getProperty("bezeichnung") : "diverse_flaechen"));
        final String extension = singleFlaeche ? ".pdf" : ".zip";
        determineDestinationFile(fileName, extension);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void run() {
        if (status != State.WAITING) {
            return;
        }
        status = State.RUNNING;
        stateChanged();

        try(final FileOutputStream outputStream = new FileOutputStream(fileToSaveTo)) {
            final PotenzialflaechenProperties pfProperties = new PotenzialflaechenProperties();
            pfProperties.setProperties(ServerResourcesLoaderClient.getInstance().loadProperties(
                    (PropertiesServerResource)WundaBlauServerResources.POTENZIALFLAECHEN_PROPERTIES.getValue()));

            final String api = pfProperties.getSecresApi();
            final String jwt = SessionManager.getSession().getUser().getJwsToken();
            final String secresKey = pfProperties.getSecresKey();
            final String path = execAction();
            final URL url = new URL(String.format(SECRES_FORMAT, api, jwt, secresKey, path));
            IOUtils.copyLarge(WebAccessManager.getInstance().doRequest(url), outputStream);
        } catch (final Exception ex) {
            log.warn(String.format("Couldn't write downloaded content to file '%s'.", fileToSaveTo), ex);
            error(ex);
            return;
        }

        if (status == State.RUNNING) {
            status = State.COMPLETED;
            stateChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private String execAction() throws Exception {
        final Collection<ServerActionParameter> params = new ArrayList<>();
        if (flaecheBeans != null) {
            for (final CidsBean flaecheBean : flaecheBeans) {
                params.add(new ServerActionParameter(
                        PotenzialflaecheReportServerAction.Parameter.POTENZIALFLAECHE.toString(),
                        new MetaObjectNode(flaecheBean)));
            }
        }
        if (kampagneBeans != null) {
            for (final CidsBean kampagneBean : kampagneBeans) {
                params.add(new ServerActionParameter(
                        PotenzialflaecheReportServerAction.Parameter.KAMPAGNE.toString(),
                        new MetaObjectNode(kampagneBean)));
            }
        }
        params.add(new ServerActionParameter(
                PotenzialflaecheReportServerAction.Parameter.TEMPLATE.toString(),
                new MetaObjectNode(templateBean)));

        final Object ret = SessionManager.getProxy()
                    .executeTask(
                        PotenzialflaecheReportServerAction.TASK_NAME,
                        "WUNDA_BLAU",
                        null,
                        getConnectionContext(),
                        params.toArray(new ServerActionParameter[0]));
        if (ret instanceof Exception) {
            final Exception ex = (Exception)ret;
            throw ex;
        }
        return (String)ret;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
