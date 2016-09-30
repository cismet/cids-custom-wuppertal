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
public class Fs_bestellungIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Fs_bestellungIconFactory.class);
    private static final ImageIcon FALLBACK = new ImageIcon(Fs_bestellungIconFactory.class.getResource(
                "/res/16/FsBestellungGrau.png"));

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Overlay {

        //~ Enum constants -----------------------------------------------------

        OPEN, ERROR
    }

    //~ Instance fields --------------------------------------------------------

    volatile javax.swing.SwingWorker<Void, Void> objectRetrievingWorker = null;
    final WeakHashMap<ObjectTreeNode, ExecutorService> listOfRetrievingObjectWorkers =
        new WeakHashMap<ObjectTreeNode, ExecutorService>();
    private final ExecutorService objectRetrievalExecutor = Executors.newFixedThreadPool(15);

    private final ImageIcon OPEN_ICON;
    private final ImageIcon ERROR_ICON;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastIconFactory object.
     */
    public Fs_bestellungIconFactory() {
        OPEN_ICON = new ImageIcon(getClass().getResource(
                    "/res/16/FsBestellung_overlay_error.png"));
        ERROR_ICON = new ImageIcon(getClass().getResource(
                    "/res/16/FsBestellung_overlay_open.png"));
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
        if (node != null) {
            final MetaObject mo = node.getMetaObject(false);
            if (mo != null) {
                final CidsBean bestellungBean = mo.getBean();
                Icon result = node.getLeafIcon();

                final Overlay ol = getOverlayForBestellung(bestellungBean);
                if (ol != null) {
                    switch (ol) {
                        case OPEN: {
                            final Icon overlay = Static2DTools.createOverlayIcon(
                                    OPEN_ICON,
                                    result.getIconWidth(),
                                    result.getIconHeight());
                            result = Static2DTools.mergeIcons(result, overlay);
                            break;
                        }
                        case ERROR: {
                            final Icon overlay = Static2DTools.createOverlayIcon(
                                    ERROR_ICON,
                                    result.getIconWidth(),
                                    result.getIconHeight());
                            result = Static2DTools.mergeIcons(result, overlay);
                        }
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
                                            LOG.error("Fehler beim Laden des MetaObjects", e);
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
     * @param   bestellung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean isPostweg(final CidsBean bestellung) {
        final Boolean postweg = (Boolean)bestellung.getProperty("postweg");
        if (postweg == null) {
            return false;
        } else {
            return postweg;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bestellung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static boolean isErledigt(final CidsBean bestellung) {
        final Boolean erledigt = (Boolean)bestellung.getProperty("erledigt");
        if (erledigt == null) {
            return false;
        } else {
            return erledigt;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bestellungBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Overlay getOverlayForBestellung(final CidsBean bestellungBean) {
        if (bestellungBean.getProperty("fehler") != null) {
            return Overlay.OPEN;
        } else if (isPostweg(bestellungBean) && !isErledigt(bestellungBean)) {
            return Overlay.ERROR;
        } else {
            return null;
        }
    }
}
