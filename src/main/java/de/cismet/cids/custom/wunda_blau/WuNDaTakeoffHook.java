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

    //~ Methods ----------------------------------------------------------------

    @Override
    public void applicationTakeoff() {
        WebAccessManager.getInstance().setTunnel(new CallServerTunnel("WUNDA_BLAU"));
    }
}
