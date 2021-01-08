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

import java.awt.event.ActionEvent;

import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.cismet.cids.custom.objectrenderer.wunda_blau.MauerObjectsPermissionsProviderDialog;

import de.cismet.cids.navigator.utils.CidsClientToolbarItem;

import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.menu.CidsUiAction;

import static javax.swing.Action.LARGE_ICON_KEY;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsClientToolbarItem.class)
public class MauerObjectPermissionsAction extends AbstractAction implements CidsClientToolbarItem,
    ConnectionContextStore,
    CidsUiAction {

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerObjectPermissionsAction object.
     */
    public MauerObjectPermissionsAction() {
        putValue(Action.NAME, "MauerObjectPermissions");
        putValue(Action.SHORT_DESCRIPTION, "Mauer - Objektrechte");
        putValue(CidsUiAction.CIDS_ACTION_KEY, "mauerObjektRechteManagement");

        final URL icon = getClass().getResource("/de/cismet/cismap/commons/gui/metasearch/mauer_permissions.png");
        putValue(LARGE_ICON_KEY, new javax.swing.ImageIcon(icon));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public String getSorterString() {
        return "500";
    }

    @Override
    public boolean isVisible() {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "mauerObjektRechteManagement",
                            getConnectionContext());
        } catch (ConnectionException ex) {
            return false;
        }
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()),
            MauerObjectsPermissionsProviderDialog.getInstance(),
            true);
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
