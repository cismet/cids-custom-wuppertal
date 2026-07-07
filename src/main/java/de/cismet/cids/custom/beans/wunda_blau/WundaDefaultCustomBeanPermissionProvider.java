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
package de.cismet.cids.custom.beans.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.localserver.user.LoginRestrictionHelper;
import Sirius.server.newuser.LoginRestrictionUserException;
import Sirius.server.newuser.User;

import org.openide.util.lookup.ServiceProvider;

import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.dynamics.DefaultCustomBeanPermissionProvider;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = DefaultCustomBeanPermissionProvider.class)
public class WundaDefaultCustomBeanPermissionProvider implements DefaultCustomBeanPermissionProvider {

    //~ Instance fields --------------------------------------------------------

    private CidsBean cidsBean;

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean getCustomWritePermissionDecisionforUser(final User u) {
        return getCustomWritePermissionDecisionforUser(
                u,
                ConnectionContext.create(
                    AbstractConnectionContext.Category.EDITOR,
                    "WundaDefaultCustomBeanPermissionProvider"));
    }

    @Override
    public boolean getCustomWritePermissionDecisionforUser(final User u, final ConnectionContext connectionContext) {
        try {
            final String activeOpeningHours = SessionManager.getProxy()
                        .getConfigAttr(u, "activeOpeningHours", connectionContext);

            if (activeOpeningHours != null) {
                try {
                    LoginRestrictionHelper.getInstance().checkLoginRestriction(activeOpeningHours.trim().split("\n"));
                } catch (LoginRestrictionUserException e) {
                    return false;
                }
            }
        } catch (ConnectionException e) {
            // nopthing to do
        }

        return true;
    }

    @Override
    public boolean getCustomReadPermissionDecisionforUser(final User u) {
        return true;
    }

    @Override
    public boolean getCustomReadPermissionDecisionforUser(final User u, final ConnectionContext connectionContext) {
        return true;
    }

    @Override
    public CidsBean getCidsBean() {
        return cidsBean;
    }

    @Override
    public void setCidsBean(final CidsBean cidsBean) {
        this.cidsBean = cidsBean;
    }
}
