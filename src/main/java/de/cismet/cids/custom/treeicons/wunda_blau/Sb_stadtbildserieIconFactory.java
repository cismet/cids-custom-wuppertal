/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.treeicons.wunda_blau;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import Sirius.server.middleware.types.MetaNode;
import Sirius.server.middleware.types.MetaObject;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieIconFactory implements CidsTreeObjectIconFactory {

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon WARNING_ICON;
    private final String PRUEFEN_SUBTREE_NODE_NAME = "Prüfen";
    private final String OK_SUBTREE_NODE_NAME = "Ok";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieIconFactory object.
     */
    public Sb_stadtbildserieIconFactory() {
        this.WARNING_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/question.png"));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getClosedPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getLeafPureNodeIcon(final PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIconAccordingToPosition(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIconAccordingToPosition(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIconAccordingToPosition(otn);
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        return null;
    }

    /**
     * Iterates through the tree path and tries to find out, if the node is a child of the pruefen-subtree or the
     * ok-subtree in the catalog. According to its position and its pruefen-flag it is checked if the node is in the
     * correct subtree. If it is not in the correct subtree, a question mark is added to its icon.
     *
     * @param   node  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Icon generateIconAccordingToPosition(final ObjectTreeNode node) {
        Icon result = null;
        if (node != null) {
            final TreeNode[] nodePath = node.getPath();
            boolean inSubTreePruefen = false;
            // ignore root
            for (int i = 1; i < nodePath.length; i++) {
                if (node == nodePath[i]) {
                    // The end of the path has been reached, this happens e.g. if the tree is not the catalog.
                    // The tree migt be the search result tree
                    return null;
                }
                if (nodePath[i] instanceof DefaultMutableTreeNode) {
                    final Object userObject = ((DefaultMutableTreeNode)nodePath[i]).getUserObject();
                    if (userObject instanceof MetaNode) {
                        final String name = ((MetaNode)userObject).getName();
                        if (name.equals(PRUEFEN_SUBTREE_NODE_NAME)) {
                            inSubTreePruefen = true;
                            break;
                        } else if (name.equals(OK_SUBTREE_NODE_NAME)) {
                            break;
                        }
                    }
                }
            }

            final MetaObject stadtbildserieMO = node.getMetaObject(false);
            if (stadtbildserieMO != null) {
                final CidsBean stadtbildserieBean = stadtbildserieMO.getBean();
                result = node.getLeafIcon();
                // True if true, False if false or null
                final Boolean pruefen = Boolean.TRUE.equals(stadtbildserieBean.getProperty(
                            "pruefen"));
                if (!pruefen.equals(inSubTreePruefen)) {
                    final Icon overlay = Static2DTools.createOverlayIcon(
                            WARNING_ICON,
                            result.getIconWidth(),
                            result.getIconHeight());
                    result = Static2DTools.mergeIcons(result, overlay);
                }
            }
        }
        return result;
    }
}
