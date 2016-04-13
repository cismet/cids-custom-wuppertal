/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.toolbaritem;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.openide.util.Exceptions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.wunda_blau.startuphooks.MotdWundaStartupHook;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;

import de.cismet.cids.server.actions.PublishCidsServerMessageAction;
import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.tools.StaticDebuggingTools;

/**
 * DOCUMENT ME!
 *
 * @author   jweintraut
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsClientToolbarItem.class)
public class TestSetMotdAction extends AbstractAction implements CidsClientToolbarItem {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NavigatorDownloadManagerAction object.
     */
    public TestSetMotdAction() {
        putValue(Action.NAME, "MOTD");
        putValue(Action.SHORT_DESCRIPTION, "set MOTD (test)");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getSorterString() {
        return "100";
    }

    @Override
    public boolean isVisible() {
        try {
            return StaticDebuggingTools.checkHomeForFile("MotdTestToolbarEnabled")
                        && SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                                "csa://"
                                + PublishCidsServerMessageAction.TASK_NAME);
        } catch (ConnectionException ex) {
            return false;
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String message = JOptionPane.showInputDialog("MOTD ?");

        if (message != null) {
            try {
                SessionManager.getSession()
                        .getConnection()
                        .executeTask(
                            SessionManager.getSession().getUser(),
                            PublishCidsServerMessageAction.TASK_NAME,
                            SessionManager.getSession().getUser().getDomain(),
                            message,
                            new ServerActionParameter<String>(
                                PublishCidsServerMessageAction.ParameterType.CATEGORY.toString(),
                                MotdWundaStartupHook.MOTD_MESSAGE_MOTD));
                SessionManager.getSession()
                        .getConnection()
                        .executeTask(
                            SessionManager.getSession().getUser(),
                            PublishCidsServerMessageAction.TASK_NAME,
                            SessionManager.getSession().getUser().getDomain(),
                            message.substring(0, Math.min(40, message.length())),
                            new ServerActionParameter<String>(
                                PublishCidsServerMessageAction.ParameterType.CATEGORY.toString(),
                                MotdWundaStartupHook.MOTD_MESSAGE_TOTD));
            } catch (final ConnectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
