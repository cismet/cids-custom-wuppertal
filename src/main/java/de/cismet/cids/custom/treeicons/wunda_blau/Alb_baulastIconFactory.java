/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.treeicons.wunda_blau;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.tools.gui.Static2DTools;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author srichter
 */
public class Alb_baulastIconFactory implements CidsTreeObjectIconFactory {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Alb_baulastIconFactory.class);

    public Alb_baulastIconFactory() {
        DELETED_ICON = new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/edit-delete.png"));
        CLOSED_ICON = new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/encrypted.png"));
        WARNING_ICON = new ImageIcon(getClass().getResource("/de/cismet/cids/custom/objecteditors/wunda_blau/dialog-warning.png"));
    }
    private final ImageIcon DELETED_ICON;
    private final ImageIcon CLOSED_ICON;
    private final ImageIcon WARNING_ICON;

    @Override
    public Icon getClosedPureNodeIcon(PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenPureNodeIcon(PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getLeafPureNodeIcon(PureTreeNode ptn) {
        return null;
    }

    @Override
    public Icon getOpenObjectNodeIcon(ObjectTreeNode otn) {
        return generateIconFromState(otn);
    }

    @Override
    public Icon getClosedObjectNodeIcon(ObjectTreeNode otn) {
        return generateIconFromState(otn);
    }

    @Override
    public Icon getLeafObjectNodeIcon(ObjectTreeNode otn) {
        return generateIconFromState(otn);
    }

    @Override
    public Icon getClassNodeIcon(ClassTreeNode dmtn) {
        return null;
    }

    private final Icon generateIconFromState(ObjectTreeNode node) {
        Icon result = null;
        if (node != null) {
            final CidsBean baulastBean = node.getMetaObject().getBean();
            result = node.getLeafIcon();
            if (!checkIfBaulastBeansIsComplete(baulastBean)) {
                Icon overlay = Static2DTools.createOverlayIcon(WARNING_ICON, result.getIconWidth(), result.getIconHeight());
                result = Static2DTools.mergeIcons(result, overlay);
//                result = overlay;
//                result = Static2DTools.mergeIcons(result, Static2DTools.createOverlayIcon(WARNING_ICON, result.getIconWidth(), result.getIconHeight()));
//                result = Static2DTools.mergeIcons(new Icon[]{result, WARNING_ICON});
            } else {
                if (baulastBean.getProperty("loeschungsdatum") != null) {
                    Icon overlay = Static2DTools.createOverlayIcon(DELETED_ICON, result.getIconWidth(), result.getIconHeight());
                    result = Static2DTools.mergeIcons(result, overlay);
//                    result = overlay;
//                result = Static2DTools.mergeIcons(result, Static2DTools.createOverlayIcon(DELETED_ICON, result.getIconWidth(), result.getIconHeight()));
//                result = Static2DTools.mergeIcons(new Icon[]{result, DELETED_ICON});
                } else if (baulastBean.getProperty("geschlossen_am") != null) {
                    Icon overlay = Static2DTools.createOverlayIcon(CLOSED_ICON, result.getIconWidth(), result.getIconHeight());
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
     * @param baulastBean
     * @return
     */
    private final boolean checkIfBaulastBeansIsComplete(CidsBean baulastBean) {
        return baulastBean.getProperty("laufende_nummer") != null && baulastBean.getProperty("lageplan") != null && baulastBean.getProperty("textblatt") != null;
    }
}
