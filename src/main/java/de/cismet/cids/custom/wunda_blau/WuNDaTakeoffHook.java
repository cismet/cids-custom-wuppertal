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
package de.cismet.cids.custom.wunda_blau;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.client.tools.CallServerTunnel;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.configuration.TakeoffHook;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = TakeoffHook.class)
public class WuNDaTakeoffHook implements TakeoffHook {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WuNDaTakeoffHook.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationTakeoff() {
        final String intranetUse = System.getProperty("jnlp.intranetUse", "false");
        if (!intranetUse.equals("false") && !intranetUse.equals("true")) {
            LOG.warn("SystemProperty intranetUse should be set to either true or false. You set it to: " + intranetUse
                        + " (Will handle that like false.)");
        }
        if (!intranetUse.equals("true")) {
            try {
                WebAccessManager.getInstance().setTunnel(new CallServerTunnel("WUNDA_BLAU"));
            } catch (Throwable e) {
                LOG.error("problem initializing WebaccessManager", e);
            }
        }
    }
}
