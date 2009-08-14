/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.treeicons.wunda_demo;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;
import javax.swing.Icon;

/**
 *
 * @author thorsten
 */
public class AapersonIconFactory implements CidsTreeObjectIconFactory {

    private javax.swing.ImageIcon person = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/treeicons/wunda_demo/person.png"));
    private javax.swing.ImageIcon smile = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/treeicons/wunda_demo/smile.png"));
    private javax.swing.ImageIcon star = new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/treeicons/wunda_demo/star.png"));

    public Icon getClassNodeIcon(ClassTreeNode dmtn) {
        return star;
    }

    public Icon getClosedObjectNodeIcon(ObjectTreeNode otn) {
        return null;
    }

    public Icon getClosedPureNodeIcon(PureTreeNode ptn) {
        return smile;
    }

    public Icon getLeafObjectNodeIcon(ObjectTreeNode otn) {
        String name = (String) otn.getMetaObject().getBean().getProperty("name");
        if (name.contains(":-)")) {
            return smile;
        } else {
            return person;
        }
    }

    public Icon getLeafPureNodeIcon(PureTreeNode ptn) {
        return getClosedPureNodeIcon(ptn);
    }

    public Icon getOpenObjectNodeIcon(ObjectTreeNode otn) {
        return person;
    }

    public Icon getOpenPureNodeIcon(PureTreeNode ptn) {
        return star;
    }
}
