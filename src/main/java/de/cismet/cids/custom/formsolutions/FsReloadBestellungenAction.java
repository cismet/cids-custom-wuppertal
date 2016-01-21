/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.formsolutions;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.types.treenode.RootTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.MetaCatalogueTree;

import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionBestellungChangeStatusServerAction;
import de.cismet.cids.custom.wunda_blau.search.actions.FormSolutionServerNewStuffAvailableAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cids.utils.abstracts.AbstractCidsBeanAction;

import de.cismet.tools.gui.StaticSwingTools;

import static javax.swing.Action.NAME;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FsReloadBestellungenAction extends AbstractCidsBeanAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FsReloadBestellungenAction.class);

    //~ Instance fields --------------------------------------------------------

    private final PureTreeNode ptn;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FsStatusBearbeitetAction object.
     *
     * @param  ptn  DOCUMENT ME!
     */
    public FsReloadBestellungenAction(final PureTreeNode ptn) {
        putValue(NAME, "Offene Bestellungen vom Formularserver abholen");
        final ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/res/16/FsBestellungReload.png"));
        putValue(SMALL_ICON, icon);

        this.ptn = ptn;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        try {
            StaticSwingTools.showDialog(new FSReloadBestellungenDialog());
        } catch (final Exception ex) {
            LOG.error(ex, ex);
        }
    }
}
