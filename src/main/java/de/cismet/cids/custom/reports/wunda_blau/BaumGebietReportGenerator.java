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

import de.cismet.cids.custom.clientutils.ByteArrayActionDownload;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class BaumGebietReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BaumGebietReportGenerator.class);
    public static final String FIELD__AZ = "aktenzeichen";

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  gebietBean         DOCUMENT ME!
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
                final String az = (String)gebietBean.getProperty(FIELD__AZ);

                DownloadManager.instance()
                        .add(new ByteArrayActionDownload(
                                taskName,
                                new MetaObjectNode(gebietBean),
                                String.format("Baumschutzsatzung - Aktenzeichen %s", az),
                                jobname,
                                String.format("baum_gebiet_%s", az),
                                ".pdf",
                                null,
                                connectionContext));
            }
        } catch (final Exception ex) {
            LOG.error("Fehler beim Download Gebiet-Report.", ex);
        }
    }
}
