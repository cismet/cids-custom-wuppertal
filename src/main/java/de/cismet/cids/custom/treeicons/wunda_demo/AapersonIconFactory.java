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
package de.cismet.cids.custom.treeicons.wunda_demo;

import Sirius.navigator.types.treenode.ClassTreeNode;
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.types.treenode.PureTreeNode;
import Sirius.navigator.ui.tree.CidsTreeObjectIconFactory;

import javax.swing.Icon;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class AapersonIconFactory implements CidsTreeObjectIconFactory {

    //~ Instance fields --------------------------------------------------------

    private javax.swing.ImageIcon person = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/custom/treeicons/wunda_demo/person.png"));
    private javax.swing.ImageIcon smile = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/custom/treeicons/wunda_demo/smile.png"));
    private javax.swing.ImageIcon star = new javax.swing.ImageIcon(getClass().getResource(
                "/de/cismet/cids/custom/treeicons/wunda_demo/star.png"));

    //~ Methods ----------------------------------------------------------------

    @Override
    public Icon getClassNodeIcon(final ClassTreeNode dmtn) {
        return star;
    }

    @Override
    public Icon getClosedObjectNodeIcon(final ObjectTreeNode otn) {
        return null;
    }

    @Override
    public Icon getClosedPureNodeIcon(final PureTreeNode ptn) {
        return smile;
    }

    @Override
    public Icon getLeafObjectNodeIcon(final ObjectTreeNode otn) {
        final String name = (String)otn.getMetaObject().getBean().getProperty("name");
        if (name.contains(":-)")) {
            return smile;
        } else {
            return person;
        }
    }

    @Override
    public Icon getLeafPureNodeIcon(final PureTreeNode ptn) {
        return getClosedPureNodeIcon(ptn);
    }

    @Override
    public Icon getOpenObjectNodeIcon(final ObjectTreeNode otn) {
        return person;
    }

    @Override
    public Icon getOpenPureNodeIcon(final PureTreeNode ptn) {
        return star;
    }
}
