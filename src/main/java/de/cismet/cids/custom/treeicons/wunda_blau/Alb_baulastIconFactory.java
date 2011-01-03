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
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.gui.Static2DTools;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alb_baulastIconFactory implements CidsTreeObjectIconFactory {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Alb_baulastIconFactory.class);

    //~ Instance fields --------------------------------------------------------

    private final ImageIcon DELETED_ICON;
    private final ImageIcon CLOSED_ICON;
    private final ImageIcon WARNING_ICON;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastIconFactory object.
     */
    public Alb_baulastIconFactory() {
        DELETED_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"));
        CLOSED_ICON = new ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/objecteditors/wunda_blau/encrypted.png"));
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
            final CidsBean baulastBean = node.getMetaObject().getBean();
            result = node.getLeafIcon();
            if (!checkIfBaulastBeansIsComplete(baulastBean)) {
                final Icon overlay = Static2DTools.createOverlayIcon(
                        WARNING_ICON,
                        result.getIconWidth(),
                        result.getIconHeight());
                result = Static2DTools.mergeIcons(result, overlay);
//                result = overlay;
//                result = Static2DTools.mergeIcons(result, Static2DTools.createOverlayIcon(WARNING_ICON, result.getIconWidth(), result.getIconHeight()));
//                result = Static2DTools.mergeIcons(new Icon[]{result, WARNING_ICON});
            } else {
                if (baulastBean.getProperty("loeschungsdatum") != null) {
                    final Icon overlay = Static2DTools.createOverlayIcon(
                            DELETED_ICON,
                            result.getIconWidth(),
                            result.getIconHeight());
                    result = Static2DTools.mergeIcons(result, overlay);
//                    result = overlay;
//                result = Static2DTools.mergeIcons(result, Static2DTools.createOverlayIcon(DELETED_ICON, result.getIconWidth(), result.getIconHeight()));
//                result = Static2DTools.mergeIcons(new Icon[]{result, DELETED_ICON});
                } else if (baulastBean.getProperty("geschlossen_am") != null) {
                    final Icon overlay = Static2DTools.createOverlayIcon(
                            CLOSED_ICON,
                            result.getIconWidth(),
                            result.getIconHeight());
                    result = Static2DTools.mergeIcons(result, overlay);
//                    result = overlay;
//                result = Static2DTools.mergeIcons(result, Static2DTools.createOverlayIcon(CLOSED_ICON, result.getIconWidth(), result.getIconHeight()));
//                result = Static2DTools.mergeIcons(new Icon[]{result, CLOSED_ICON});
                }
            }
        }
        return result;
    }

    /**
     * Checks whether or not all important attributes of a baulast are filled.
     *
     * @param   baulastBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkIfBaulastBeansIsComplete(final CidsBean baulastBean) {
        return (baulastBean.getProperty("laufende_nummer") != null) && (baulastBean.getProperty("lageplan") != null)
                    && (baulastBean.getProperty("textblatt") != null);
    }
}
