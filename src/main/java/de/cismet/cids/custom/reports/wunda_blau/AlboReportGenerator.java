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

import java.awt.Component;

import de.cismet.cids.custom.utils.ByteArrayActionDownload;
import de.cismet.cids.custom.wunda_blau.search.actions.AlboVorgangReportServerAction;

import de.cismet.cids.dynamics.CidsBean;

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

                DownloadManager.instance()
                        .add(new ByteArrayActionDownload(
                                AlboVorgangReportServerAction.TASK_NAME,
                                new MetaObjectNode(vorgangBean),
                                null,
                                String.format("Altlastenkataster - Vorgang %s", vorgang),
                                jobname,
                                String.format("albo_vorgang_%s", vorgang),
                                ".pdf",
                                connectionContext));
            }
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
        }
    }
}
