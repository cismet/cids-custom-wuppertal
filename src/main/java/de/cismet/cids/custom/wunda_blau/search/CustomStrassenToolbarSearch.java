package de.cismet.cids.custom.wunda_blau.search;

import de.cismet.cids.tools.search.clientstuff.CidsToolbarSearch;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.newuser.permission.Policy;
import Sirius.server.search.CidsServerSearch;
import com.explodingpixels.macwidgets.HudWindow;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author stefan
 */
@org.openide.util.lookup.ServiceProvider(service = CidsToolbarSearch.class)
public class CustomStrassenToolbarSearch implements CidsToolbarSearch {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CustomStrassenToolbarSearch.class);
    private String searchString;

    public CustomStrassenToolbarSearch() {
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

        return new CustomStrassenSearchStatement(searchString);

    }

    


}
