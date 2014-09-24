/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.navigatorstartuphooks;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;

import de.cismet.cids.custom.butler.*;
import de.cismet.cids.custom.utils.butler.ButlerRequestInfo;
import de.cismet.cids.custom.wunda_blau.search.actions.ButlerQueryAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.configuration.StartupHook;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.MultipleDownload;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class FeatureStylingKeyStartUpHook implements StartupHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = Logger.getLogger(FeatureStylingKeyStartUpHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationStarted() {
        try {
            final String stylingKey = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(), "feature.styling");
            if (stylingKey != null) {
                CismapBroker.getInstance().setFeatureStylingComponentKey(stylingKey);
            }
        } catch (Exception e) {
            log.warn("Exception during retrievel of configuration attribute feature.styling", e);
        }
    }
}
