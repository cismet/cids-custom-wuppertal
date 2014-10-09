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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import jxl.common.Logger;

import java.awt.Component;

import java.net.URL;

import java.util.Date;

import javax.swing.JOptionPane;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;
import de.cismet.tools.gui.downloadmanager.HttpDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class AlkisProductDownloadHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(AlkisProductDownloadHelper.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   stichtag                   DOCUMENT ME!
     * @param   downloadTitle              DOCUMENT ME!
     * @param   product                    DOCUMENT ME!
     * @param   actionTag                  DOCUMENT ME!
     * @param   completeBuchungsblattCode  DOCUMENT ME!
     * @param   parent                     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download createStichtagProductDownload(final Date stichtag,
            final String downloadTitle,
            final String product,
            final String actionTag,
            final String completeBuchungsblattCode,
            final Component parent) {
        HttpDownload download = null;
        if (!ObjectRendererUtils.checkActionTag(actionTag)) {
            showNoProductPermissionWarning(parent);
            return null;
        }

        try {
            if (completeBuchungsblattCode.length() > 0) {
                final String queryID = AlkisUtils.escapeHtmlSpaces(completeBuchungsblattCode);
                final URL url = AlkisUtils.PRODUCTS.productEinzelnachweisStichtagsbezogenUrl(
                        queryID,
                        product,
                        stichtag);

                if (url != null) {
                    String filename = product + "." + completeBuchungsblattCode.replace("/", "--").trim();
                    filename = filename.replaceAll(" +", "_"); // replace all whitespaces
                    download = new HttpDownload(
                            url,
                            "",
                            DownloadManagerDialog.getJobname(),
                            downloadTitle,
                            filename,
                            ".pdf");
                }
            }
        } catch (Exception ex) {
            ObjectRendererUtils.showExceptionWindowToUser(
                "Fehler beim Aufruf des Produkts: "
                        + product,
                ex,
                parent);
            LOG.error(ex);
            return null;
        }
        return download;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parent  DOCUMENT ME!
     */
    private static void showNoProductPermissionWarning(final Component parent) {
        JOptionPane.showMessageDialog(StaticSwingTools.getParentFrame(parent),
            "Sie besitzen keine Berechtigung zur Erzeugung dieses Produkts!");
    }
}
