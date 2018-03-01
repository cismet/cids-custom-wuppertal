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

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;
import de.cismet.cids.server.connectioncontext.ClientConnectionContextStore;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.configuration.StartupHook;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = StartupHook.class)
public class FeatureStylingKeyStartUpHook implements StartupHook, ClientConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger log = Logger.getLogger(FeatureStylingKeyStartUpHook.class);

    //~ Instance fields --------------------------------------------------------

    private ClientConnectionContext connectionContext = ClientConnectionContext.create(getClass().getSimpleName());

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initAfterConnectionContext() {
    }

    @Override
    public void applicationStarted() {
        try {
            final String stylingKey = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                            "feature.styling",
                            getConnectionContext());
            if (stylingKey != null) {
                CismapBroker.getInstance().setFeatureStylingComponentKey(stylingKey);
            }
        } catch (Exception e) {
            log.warn("Exception during retrievel of configuration attribute feature.styling", e);
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
