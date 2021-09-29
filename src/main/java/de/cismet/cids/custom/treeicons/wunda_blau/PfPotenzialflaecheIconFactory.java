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
import Sirius.navigator.ui.tree.MetaCatalogueTree;
import Sirius.navigator.ui.tree.SearchResultsTree;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.tools.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheIconFactory.class);
    private static final Map<String, Icon> COLORED_ICONS = new WeakHashMap<>();
    private static ImageIcon PF_ICON;

    //~ Instance fields --------------------------------------------------------

    volatile javax.swing.SwingWorker<Void, Void> objectRetrievingWorker = null;
    final WeakHashMap<ObjectTreeNode, ExecutorService> listOfRetrievingObjectWorkers = new WeakHashMap<>();
    private final ExecutorService objectRetrievalExecutor = Executors.newFixedThreadPool(15);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PfPotenzialflaecheIconFactory object.
     */
    public PfPotenzialflaecheIconFactory() {
        if (PF_ICON == null) {
            try {
                PF_ICON = new ImageIcon(CidsBean.getMetaClassFromTableName(
                            "WUNDA_BLAU",
                            "PF_POTENZIALFLAECHE",
                            ConnectionContext.create(
                                AbstractConnectionContext.Category.STATIC,
                                PfPotenzialflaecheIconFactory.class.getSimpleName())).getIcon().getImageData());
            } catch (final Exception ex) {
                LOG.error("error while loading base icon", ex);
                PF_ICON = new ImageIcon();
            }
        }
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
     * @param  node  DOCUMENT ME!
     */
    private void loadNode(final ObjectTreeNode node) {
        if (!listOfRetrievingObjectWorkers.containsKey(node)) {
            if (!listOfRetrievingObjectWorkers.containsKey(node)) {
                listOfRetrievingObjectWorkers.put(node, objectRetrievalExecutor);
                synchronized (listOfRetrievingObjectWorkers) {
                    final SearchResultsTree searchTree = ComponentRegistry.getRegistry().getSearchResultsTree();
                    final MetaCatalogueTree catalogueTree = ComponentRegistry.getRegistry().getCatalogueTree();
                    final DefaultTreeModel searchTreeModel = (DefaultTreeModel)searchTree.getModel();
                    final DefaultTreeModel catalogueTreeModel = (DefaultTreeModel)catalogueTree.getModel();
                    final Object root = searchTree.getModel().getRoot();
                    if (node != null) {
                        objectRetrievalExecutor.execute(new javax.swing.SwingWorker<Icon, Void>() {

                                @Override
                                protected Icon doInBackground() throws Exception {
                                    if (node.getPath()[0].equals(root)) {
                                        if (searchTree.containsNode(node.getNode())) {
                                            node.getMetaObject(true);
                                        }
                                    } else {
                                        node.getMetaObject(true);
                                    }
                                    return generateIcon(node);
                                }

                                @Override
                                protected void done() {
                                    try {
                                        searchTreeModel.nodeChanged(node);
                                        catalogueTreeModel.nodeChanged(node);
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
     * @param   colorcode  DOCUMENT ME!
     * @param   width      DOCUMENT ME!
     * @param   height     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static Icon getColoredIcon(final String colorcode, final int width, final int height) throws Exception {
        final Color color = Color.decode(colorcode);
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, width, height);
        final Icon colorIcon = new ImageIcon(image);
        return Static2DTools.mergeIcons(colorIcon, PF_ICON);
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
            if (mo == null) {
                loadNode(node);
            } else {
                final CidsBean schildBean = mo.getBean();
                final String colorcode = (String)schildBean.getProperty("kampagne.colorcode");
                if (!COLORED_ICONS.containsKey(colorcode)) {
                    try {
                        final Icon icon = getColoredIcon(colorcode, PF_ICON.getIconHeight(), PF_ICON.getIconWidth());
                        COLORED_ICONS.put(colorcode, icon);
                    } catch (final Exception ex) {
                        LOG.info(ex, ex);
                        COLORED_ICONS.put(colorcode, null);
                    }
                }
                return COLORED_ICONS.get(colorcode);
            }
            return PF_ICON;
        }

        return null;
    }
}
