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
import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;

import de.cismet.cids.servermessage.CidsServerMessageNotifier;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

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
public class TotdStartUpHook implements StartupHook, ClientConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(TotdStartUpHook.class);

    //~ Instance fields --------------------------------------------------------

    private ClientConnectionContext connectionContext = ClientConnectionContext.create(getClass().getSimpleName());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initAfterConnectionContext() {
    }

    @Override
    public void applicationStarted() {
        if (ComponentRegistry.getRegistry().getNavigator() != null) {
            try {
                if (SessionManager.getConnection().hasConfigAttr(
                                SessionManager.getSession().getUser(),
                                "csm://"
                                + MotdWundaStartupHook.MOTD_MESSAGE_TOTD,
                                getConnectionContext())) {
                    CidsServerMessageNotifier.getInstance()
                            .subscribe(new CidsServerMessageNotifierListener() {

                                    @Override
                                    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
                                        try {
                                            final String totd = (String)event.getMessage().getContent();
                                            ComponentRegistry.getRegistry().getNavigator().setTotd(totd);
                                        } catch (final Exception ex) {
                                            LOG.warn(ex, ex);
                                        }
                                    }
                                },
                                MotdWundaStartupHook.MOTD_MESSAGE_TOTD);
                }
            } catch (ConnectionException ex) {
                LOG.warn("Konnte Rechte an csm://" + MotdWundaStartupHook.MOTD_MESSAGE_TOTD
                            + " nicht abfragen. Keine Titleleiste des Tages !",
                    ex);
            }
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
