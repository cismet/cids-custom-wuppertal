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
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import Sirius.server.middleware.types.MetaNode;
import Sirius.server.middleware.types.MetaObject;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Alb_baulastblattIconFactory.class);

    //~ Instance fields --------------------------------------------------------

    volatile javax.swing.SwingWorker<Void, Void> objectRetrievingWorker = null;
    final HashSet<ObjectTreeNode> listOfRetrievingObjectWorkers = new HashSet<ObjectTreeNode>();

    private final ImageIcon WARNING_ICON;
    private final ImageIcon BODENNAH_ICON;
    private final ImageIcon PREVIEW_ICON;
    private final ImageIcon SCHRAEG_ICON;
    private final ImageIcon SENKRECHT_ICON;
    private final String PRUEFEN_SUBTREE_NODE_NAME = "Prüfen";
    private final String OK_SUBTREE_NODE_NAME = "Ok";
    private final ExecutorService objectRetrievalExecutor = Executors.newFixedThreadPool(15);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieIconFactory object.
     */
    public Sb_stadtbildserieIconFactory() {
        this.WARNING_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/question.png"));
        this.BODENNAH_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/Stadtbildserie_Bodennah.png"));
        this.PREVIEW_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/Stadtbildserie_Preview.png"));
        this.SCHRAEG_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/Stadtbildserie_Schraeg.png"));
        this.SENKRECHT_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/Stadtbildserie_Senkrecht.png"));
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
        Icon result = PREVIEW_ICON;
        if (node != null) {
            final TreeNode[] nodePath = node.getPath();
            boolean inSubTreePruefen = false;
            // ignore root
            for (int i = 1; i < nodePath.length; i++) {
//                if (node == nodePath[i]) {
//                    // The end of the path has been reached, this happens e.g. if the tree is not the catalog.
//                    // The tree migt be the search result tree
//                    return null;
//                }
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
                final CidsBean bildtypBean = (CidsBean)stadtbildserieBean.getProperty("bildtyp");
                if (bildtypBean != null) {
                    if (bildtypBean.getPrimaryKeyValue() == 0) {
                        result = SCHRAEG_ICON;
                    } else if (bildtypBean.getPrimaryKeyValue() == 1) {
                        result = SENKRECHT_ICON;
                    } else if (bildtypBean.getPrimaryKeyValue() == 2) {
                        result = BODENNAH_ICON;
                    }
                }

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
            } else {
                if (!listOfRetrievingObjectWorkers.contains(node)) {
                    synchronized (listOfRetrievingObjectWorkers) {
                        if (!listOfRetrievingObjectWorkers.contains(node)) {
                            listOfRetrievingObjectWorkers.add(node);
                            objectRetrievalExecutor.execute(new javax.swing.SwingWorker<Void, Void>() {

                                    @Override
                                    protected Void doInBackground() throws Exception {
                                        if (!(node == null)) {
                                            if (node.getPath()[0].equals(
                                                            ComponentRegistry.getRegistry().getSearchResultsTree()
                                                                .getModel().getRoot())) {
                                                // Searchtree
                                                if (ComponentRegistry.getRegistry().getSearchResultsTree().containsNode(
                                                                node.getNode())) {
                                                    node.getMetaObject(true);
                                                }
                                            } else {
                                                // normaler Baum
                                                node.getMetaObject(true);
                                            }
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void done() {
                                        try {
                                            synchronized (listOfRetrievingObjectWorkers) {
                                                listOfRetrievingObjectWorkers.remove(node);
                                            }
                                            final Void result = get();
                                            if (node.getPath()[0].equals(
                                                            ComponentRegistry.getRegistry().getSearchResultsTree()
                                                                .getModel().getRoot())) {
                                                // Searchtree
                                                ((DefaultTreeModel)ComponentRegistry.getRegistry()
                                                            .getSearchResultsTree().getModel()).nodeChanged(node);
                                            } else {
                                                // normaler Baum
                                                ((DefaultTreeModel)ComponentRegistry.getRegistry().getCatalogueTree()
                                                            .getModel()).nodeChanged(node);
                                            }
                                        } catch (Exception e) {
                                            LOG.error("Fehler beim Laden des MetaObjects", e);
                                        }
                                    }
                                });
                        }
                    }
                } else {
                    // evtl log meldungen
                }
            }
        }
        return result;
    }
}
