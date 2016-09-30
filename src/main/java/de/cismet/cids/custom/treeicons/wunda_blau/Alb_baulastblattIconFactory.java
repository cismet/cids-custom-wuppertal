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
package de.cismet.cids.custom.treeicons.wunda_blau;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.ComponentRegistry;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import Sirius.server.middleware.types.MetaObject;

import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alb_baulastblattIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            Alb_baulastblattIconFactory.class);
    private static ImageIcon FALLBACK = new ImageIcon(Alb_baulastblattIconFactory.class.getResource(
                "/res/16/BaulastGrau.png"));

    //~ Instance fields --------------------------------------------------------

    volatile javax.swing.SwingWorker<Void, Void> objectRetrievingWorker = null;
    final WeakHashMap<ObjectTreeNode, ExecutorService> listOfRetrievingObjectWorkers =
        new WeakHashMap<ObjectTreeNode, ExecutorService>();
    private final ExecutorService objectRetrievalExecutor = Executors.newFixedThreadPool(15);
    private final ImageIcon DELETED_ICON;
    private final ImageIcon WARNING_ICON;

    private final Object objectRetrievingLock = new Object();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastIconFactory object.
     */
    public Alb_baulastblattIconFactory() {
        DELETED_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"));
        WARNING_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"));
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
        return generateIconFromState(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIconFromState(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIconFromState(otn);
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Icon generateIconFromState(final ObjectTreeNode node) {
        Icon result = null;
        if (node != null) {
            final MetaObject baulastblattMO = node.getMetaObject(false);
            if (baulastblattMO != null) {
                final CidsBean baulastBlattBean = baulastblattMO.getBean();
                result = node.getLeafIcon();

                final Alb_baulastIconFactory.Overlay ovl = getOverlayForBaulastBlatt(baulastBlattBean);
                // CROSS OVERLAY

                switch (ovl) {
                    case CROSS: {
                        final Icon overlay = Static2DTools.createOverlayIcon(
                                DELETED_ICON,
                                result.getIconWidth(),
                                result.getIconHeight());
                        result = Static2DTools.mergeIcons(result, overlay);
                        break;
                    }
                    case WARN: {
                        final Icon overlay = Static2DTools.createOverlayIcon(
                                WARNING_ICON,
                                result.getIconWidth(),
                                result.getIconHeight());
                        result = Static2DTools.mergeIcons(result, overlay);
                    }
                }
                return result;
            } else {
                if (!listOfRetrievingObjectWorkers.containsKey(node)) {
                    if (!listOfRetrievingObjectWorkers.containsKey(node)) {
                        listOfRetrievingObjectWorkers.put(node, objectRetrievalExecutor);
                        synchronized (listOfRetrievingObjectWorkers) {
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
                                            log.error("Fehler beim Laden des MetaObjects", e);
                                        } finally {
                                            synchronized (listOfRetrievingObjectWorkers) {
                                                listOfRetrievingObjectWorkers.remove(node);
                                            }
                                        }
                                    }
                                });
                        }
                    }
                } else {
                    // evtl log meldungen
                }
            }
            return FALLBACK;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   baulastBlattBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Alb_baulastIconFactory.Overlay getOverlayForBaulastBlatt(final CidsBean baulastBlattBean) {
        final List<CidsBean> baulasten = baulastBlattBean.getBeanCollectionProperty("baulasten");
        int cross = 0;
        int locked = 0;
        int none = 0;
        for (final CidsBean baulast : baulasten) {
            final Alb_baulastIconFactory.Overlay ovl = Alb_baulastIconFactory.getOverlayForBaulast(baulast);
            switch (ovl) {
                case WARN: {
                    return Alb_baulastIconFactory.Overlay.WARN;
                }
                case NONE: {
                    none++;
                    break;
                }
                case CROSS: {
                    cross++;
                    break;
                }
                case LOCKED: {
                    locked++;
                }
            }
        }
        if (none > 0) {
            return Alb_baulastIconFactory.Overlay.NONE;
        } else if ((cross > 0) || (locked > 0)) {
            return Alb_baulastIconFactory.Overlay.CROSS;
        } else {
            return Alb_baulastIconFactory.Overlay.WARN;
        }
    }
}
