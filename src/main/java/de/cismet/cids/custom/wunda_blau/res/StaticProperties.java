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
    private static final String PROPERTY_FILE_URL = "/de/cismet/cids/custom/wunda_blau/res/urlconfig.properties";
    //--------------
    public static final String ARCHIVAR_URL_PREFIX;
    public static final String ARCHIVAR_URL_SUFFIX;
    public static final String POI_SIGNATUR_URL_PREFIX;
    public static final String POI_SIGNATUR_URL_SUFFIX;
    public static final String POI_SIGNATUR_DEFAULT_ICON;
    public static final String POI_LOCATIONTYPE_URL_PREFIX;
    public static final String POI_LOCATIONTYPE_URL_SUFFIX;
    //--------------
    static {
        propReader = new PropertyReader(PROPERTY_FILE_URL);
        ARCHIVAR_URL_PREFIX = propReader.getProperty("archivar_url_prefix");
        ARCHIVAR_URL_SUFFIX = propReader.getProperty("archivar_url_suffix");
        POI_SIGNATUR_URL_PREFIX = propReader.getProperty("poi_signatur_url_prefix");
        POI_SIGNATUR_URL_SUFFIX = propReader.getProperty("poi_signatur_url_suffix");
        POI_SIGNATUR_DEFAULT_ICON = propReader.getProperty("poi_signatur_default_icon");
        POI_LOCATIONTYPE_URL_PREFIX = propReader.getProperty("poi_locationtype_url_prefix");
        POI_LOCATIONTYPE_URL_SUFFIX = propReader.getProperty("poi_locationtype_url_suffix");
    }

    public static final String getProperty(String in) {
        return propReader.getProperty(in);
    }

    private StaticProperties() {
        throw new AssertionError();
    }
}
