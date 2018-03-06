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

import de.cismet.cids.custom.motd.MotdDialog;
import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;

import de.cismet.connectioncontext.ClientConnectionContext;
import de.cismet.connectioncontext.ClientConnectionContextStore;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class MotdStartUpHook implements StartupHook, ClientConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(MotdStartUpHook.class);

    //~ Instance fields --------------------------------------------------------

    private ClientConnectionContext connectionContext = ClientConnectionContext.create(getClass().getSimpleName());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initAfterConnectionContext() {
    }

    @Override
    public void applicationStarted() {
        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + MotdWundaStartupHook.MOTD_MESSAGE_MOTD,
                            getConnectionContext())) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(MotdDialog.getInstance(), MotdWundaStartupHook.MOTD_MESSAGE_MOTD);
            }
        } catch (ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + MotdWundaStartupHook.MOTD_MESSAGE_MOTD
                        + " nicht abfragen. Keine Meldung des Tages !",
                ex);
        }
        try {
            if (SessionManager.getConnection().hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            "csm://"
                            + MotdWundaStartupHook.MOTD_MESSAGE_MOTD_EXTERN,
                            getConnectionContext())) {
                CidsServerMessageNotifier.getInstance()
                        .subscribe(MotdDialog.getInstance(), MotdWundaStartupHook.MOTD_MESSAGE_MOTD_EXTERN);
            }
        } catch (ConnectionException ex) {
            LOG.warn("Konnte Rechte an csm://" + MotdWundaStartupHook.MOTD_MESSAGE_MOTD_EXTERN
                        + " nicht abfragen. Keine Meldung des Tages !",
                ex);
        }
    }

    @Override
    public ClientConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public void setConnectionContext(final ClientConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }
}
