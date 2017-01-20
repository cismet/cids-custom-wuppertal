/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.MetaClass;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.explodingpixels.macwidgets.HudWindow;

import java.awt.FlowLayout;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.wunda_blau.search.server.CustomStrassenSearchStatement;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class CustomStrassenToolbarSearch implements CidsToolbarSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            CustomStrassenToolbarSearch.class);

    //~ Instance fields --------------------------------------------------------

    private String searchString;
    private final MetaClass mc;
    private final ImageIcon icon;
    private final Collection<MetaClass> classCol;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CustomStrassenToolbarSearch object.
     */
    public CustomStrassenToolbarSearch() {
        mc = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "STRASSE");
        classCol = new ArrayList<MetaClass>(1);
        if (mc != null) {
            icon = new ImageIcon(mc.getIconData());
            classCol.add(mc);
        } else {
            icon = null;
            LOG.info("MetaClass Strasse is null, the permissions are probably missing.");
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getName() {
        return mc.getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  toolbarSearchString  DOCUMENT ME!
     */
    @Override
    public void setSearchParameter(final String toolbarSearchString) {
        this.searchString = toolbarSearchString;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public MetaObjectNodeServerSearch getServerSearch() {
        if (searchString.startsWith("str ")) {
            searchString = searchString.substring(4);
            final HudWindow hud = new HudWindow("Straßensuche");
            hud.getJDialog().setSize(300, 60);
            hud.getJDialog().setResizable(true);
            hud.getJDialog().setAlwaysOnTop(true);
            hud.getContentPane().setLayout(new FlowLayout());
            hud.getContentPane().add(HudWidgetFactory.createHudLabel("Strasse  "));
            final JTextField t = HudWidgetFactory.createHudTextField(searchString);
            t.setColumns(15);
            hud.getContentPane().add(t);
            final JButton s = HudWidgetFactory.createHudButton("Suche");
            hud.getContentPane().add(s);
            hud.getJDialog().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            StaticSwingTools.showDialog(StaticSwingTools.getParentFrame(
                    ComponentRegistry.getRegistry().getMainWindow()),
                hud.getJDialog(),
                true);
        }

        return new CustomStrassenSearchStatement(searchString);
    }
}
