/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
import Sirius.navigator.types.treenode.ObjectTreeNode;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.editors.AutoBindableCidsEditor;
import de.cismet.cids.editors.CidsObjectEditorFactory;
import de.cismet.cids.editors.DefaultCidsEditor;
import de.cismet.cids.editors.NavigatorAttributeEditorGui;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

/*
 * Copyright (C) 2012 cismet GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class NewClass {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NewClass.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "leo");
////        DevelopmentTools.initSessionManagerFromRMIConnectionOnLocalhost(
////                "WUNDA_BLAU",
////                "Baulasten",
////                "SchulteJ102",
////                "broenson10!");
//
//        MetaClass mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 175);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 176);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 177);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 178);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 179);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 180);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 181);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 182);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 183);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 184);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 185);
//        System.out.println("mc " + mc);
//        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", 202);
//        System.out.println("mc " + mc);
//
//        System.out.println("fertich");
//        System.exit(0);
//
//

        final int w = 800;
        final int h = 600;
        final Class c = Class.forName("de.cismet.cids.editors.CidsObjectEditorFactory");
        final java.lang.reflect.Method getDefEditor = c.getDeclaredMethod("getDefaultEditor", MetaClass.class);
        final java.lang.reflect.Method autobind = c.getDeclaredMethod("bindCidsEditor", AutoBindableCidsEditor.class);
        de.cismet.cids.navigator.utils.ClassCacheMultiple.setInstance("WUNDA_BLAU");
        getDefEditor.setAccessible(true);
        autobind.setAccessible(true);
        final ObjectTreeNode otn = (ObjectTreeNode)
            (((NavigatorAttributeEditorGui)ComponentRegistry.getRegistry().getAttributeEditor()).getTreeNode());
        final CidsBean edited = otn.getMetaObject().getBean();
        getDefEditor.invoke(CidsObjectEditorFactory.getInstance(), edited.getMetaObject().getMetaClass());
        final DefaultCidsEditor e = (DefaultCidsEditor)(getDefEditor.invoke(
                    CidsObjectEditorFactory.getInstance(),
                    edited.getMetaObject().getMetaClass()));
        e.setCidsBean(edited);
        autobind.invoke(CidsObjectEditorFactory.getInstance(), e);
        final JFrame jf = new JFrame("Test");
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jf.getContentPane().setLayout(new BorderLayout());
        jf.getContentPane().add(new JScrollPane(e), BorderLayout.CENTER);
        jf.setSize(
            w,
            h);
        jf.setVisible(
            true);
        final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        jf.setBounds((int)(screenSize.width - w) / 2, (int)(screenSize.height - h) / 2, w, h);
    }
}
