package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.search.CidsToolbarSearch;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.search.SearchOption;
import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public Collection<SearchOption> generateSearchStatement() {
        try {
            Map soMap = SessionManager.getProxy().getSearchOptions();
            SearchOption so = (SearchOption) soMap.get("TEXTSEARCH@WUNDA_BLAU");
            so.setDefaultSearchParameter("text", searchString);
            List<SearchOption> res = new ArrayList<SearchOption>(0);
            res.add(so);
            return res;
        } catch (Exception ex) {
            log.error(ex, ex);
            return Collections.EMPTY_LIST;
        }
    }
}
