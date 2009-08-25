/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.wunda_blau.res;

import de.cismet.cids.custom.objectrenderer.utils.PropertyReader;

/**
 *
 * @author srichter
 */
public final class StaticProperties {

    private static final PropertyReader propReader;
    public static final String ARCHIVAR_URL_PREFIX;
    public static final String ARCHIVAR_URL_SUFFIX;
    public static final String POI_URL_PREFIX;
    public static final String POI_URL_SUFFIX;
    public static final String POI_DEFAULT_ICON;

    static {
//        propReader = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/urlconfig.properties");
        propReader = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/urlconfig.properties");
        ARCHIVAR_URL_PREFIX = propReader.getProperty("archivar_url_prefix");
        ARCHIVAR_URL_SUFFIX = propReader.getProperty("archivar_url_suffix");
        POI_URL_PREFIX = propReader.getProperty("poi_url_prefix");
        POI_URL_SUFFIX = propReader.getProperty("poi_url_suffix");
        POI_DEFAULT_ICON = propReader.getProperty("poi_default_icon");
    }

    public static final String getProperty(String in) {
        return propReader.getProperty(in);
    }

    private StaticProperties() {
        throw new AssertionError();
    }
}
