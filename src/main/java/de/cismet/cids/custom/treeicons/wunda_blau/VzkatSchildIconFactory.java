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
import Sirius.navigator.ui.tree.SearchResultsTree;

import Sirius.server.middleware.types.MetaObject;

import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultTreeModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.security.WebAccessManager;

import de.cismet.tools.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class VzkatSchildIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VzkatSchildIconFactory.class);
    private static final Map<String, ImageIcon> ICONS = new HashMap<>();

    // TODO change
    private static final ImageIcon LOADING_ICON = new ImageIcon(VzkatSchildIconFactory.class.getResource(
                "/res/16/vzkat_loading.png"));
    private static final ImageIcon ERROR_ICON = new ImageIcon(VzkatSchildIconFactory.class.getResource(
                "/res/16/vzkat_error.png"));
    private static final String ICON_URL_TEMPLATE =
        "http://dokumente.s10222.wuppertal-intra.de/vzkat-bilder/16x16/%s.png";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Overlay {

        //~ Enum constants -----------------------------------------------------

        ERROR
    }

    //~ Instance fields --------------------------------------------------------

    volatile javax.swing.SwingWorker<Void, Void> objectRetrievingWorker = null;
    final WeakHashMap<ObjectTreeNode, ExecutorService> listOfRetrievingObjectWorkers = new WeakHashMap<>();
    private final ExecutorService objectRetrievalExecutor = Executors.newFixedThreadPool(15);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VzkatSchildIconFactory object.
     */
    public VzkatSchildIconFactory() {
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
        return generateIcon(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIcon(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        return generateIcon(otn);
    }

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static ImageIcon loadZeichenIcon(final String key) throws Exception {
        final String urlString = String.format(ICON_URL_TEMPLATE, key);
        final InputStream is = WebAccessManager.getInstance().doRequest(new URL(urlString));
        return new ImageIcon(ImageIO.read(is));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   icon     DOCUMENT ME!
     * @param   overlay  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Icon overlayIcon(final ImageIcon icon, final Overlay overlay) {
        if (overlay != null) {
            switch (overlay) {
                case ERROR: {
                    final Icon overlayedIcon = Static2DTools.createOverlayIcon(
                            icon,
                            icon.getIconWidth(),
                            icon.getIconHeight());
                    return Static2DTools.mergeIcons(icon, overlayedIcon);
                }
                default: {
                    return icon;
                }
            }
        } else {
            return icon;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  node  DOCUMENT ME!
     */
    private void loadNode(final ObjectTreeNode node) {
        if (!listOfRetrievingObjectWorkers.containsKey(node)) {
            if (!listOfRetrievingObjectWorkers.containsKey(node)) {
                listOfRetrievingObjectWorkers.put(node, objectRetrievalExecutor);
                synchronized (listOfRetrievingObjectWorkers) {
                    final SearchResultsTree tree = ComponentRegistry.getRegistry().getSearchResultsTree();
                    final DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
                    final Object root = tree.getModel().getRoot();
                    if (node != null) {
                        objectRetrievalExecutor.execute(new javax.swing.SwingWorker<Void, Void>() {

                                @Override
                                protected Void doInBackground() throws Exception {
                                    if (node.getPath()[0].equals(root)) {
                                        // Searchtree
                                        if (tree.containsNode(node.getNode())) {
                                            node.getMetaObject(true);
                                        }
                                    } else {
                                        // normaler Baum
                                        node.getMetaObject(true);
                                    }
                                    generateIcon(node);
                                    return null;
                                }

                                @Override
                                protected void done() {
                                    try {
                                        get();
                                        treeModel.reload(node);
                                    } catch (final Exception ex) {
                                        LOG.error("Fehler beim Laden des MetaObjects", ex);
                                    } finally {
                                        synchronized (listOfRetrievingObjectWorkers) {
                                            listOfRetrievingObjectWorkers.remove(node);
                                        }
                                    }
                                }
                            });
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   node  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Icon generateIcon(final ObjectTreeNode node) {
        if (node != null) {
            final MetaObject mo = node.getMetaObject(false);
            if (mo != null) {
                final CidsBean schildBean = mo.getBean();

                final String key = String.format(
                        "%s_%s",
                        (String)schildBean.getProperty("fk_zeichen.fk_stvo.schluessel"),
                        (String)schildBean.getProperty("fk_zeichen.schluessel"));
                if (ICONS.containsKey(key)) {
                    final ImageIcon icon = (ICONS.get(key) != null) ? ICONS.get(key) : ERROR_ICON;
                    final Overlay overlay = getOverlay(schildBean);
                    return overlayIcon(icon, overlay);
                } else {
                    new SwingWorker<ImageIcon, Void>() {

                            @Override
                            protected ImageIcon doInBackground() throws Exception {
                                return loadZeichenIcon(key);
                            }

                            @Override
                            protected void done() {
                                try {
                                    final ImageIcon icon = get();
                                    ICONS.put(key, icon);
                                } catch (final Exception ex) {
                                    LOG.error(ex, ex);
                                    ICONS.put(key, ERROR_ICON);
                                }
                                final SearchResultsTree tree = ComponentRegistry.getRegistry().getSearchResultsTree();
                                final DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
                                treeModel.reload(node);
                            }
                        }.execute();
                }
            } else {
                loadNode(node);
            }
            return LOADING_ICON;
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   schild  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Overlay getOverlay(final CidsBean schild) {
//        if (Boolean.FALSE.equals(schild.getProperty("status"))) {
//            return Overlay.ERROR;
//        } else {
        return null;
//        }
    }
}