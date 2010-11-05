package de.cismet.cids.custom.wunda_blau.search;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.search.CidsServerSearch;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ImageIcon;

/**
 *
 * @author stefan
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class TestToolbarSearch implements CidsToolbarSearch {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TestToolbarSearch.class);
    private String searchString;

    public TestToolbarSearch() {
        mc = ClassCacheMultiple.getMetaClass(CidsBeanSupport.DOMAIN_NAME, "STRASSE");
        icon = new ImageIcon(mc.getIconData());
        classCol = new ArrayList<MetaClass>(1);
        classCol.add(mc);
    }
    private final MetaClass mc;
    private final ImageIcon icon;
    private Collection<MetaClass> classCol;

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public Collection<MetaClass> getPossibleResultClasses() {
        return classCol;
    }

    @Override
    public String getName() {
        return mc.getName();
    }

    @Override
    public void setSearchParameter(String toolbarSearchString) {
        this.searchString = toolbarSearchString;
    }


    @Override
    public CidsServerSearch getServerSearch() {
//        HudWindow hud = new HudWindow("Window");
//        hud.getJDialog().setSize(300, 350);
//        hud.getJDialog().setLocationRelativeTo(null);
//        hud.getJDialog().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        hud.getJDialog().setVisible(true);

        return new CustomStrassenSuche(searchString);

    }

    


}
