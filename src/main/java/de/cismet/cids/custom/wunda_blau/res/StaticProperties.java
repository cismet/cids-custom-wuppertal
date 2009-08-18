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
    public static final String URL_PREFIX;
    public static final String URL_SUFFIX;

    static {
//        propReader = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/urlconfig.properties");
        propReader = new PropertyReader("/de/cismet/cids/custom/wunda_blau/res/urlconfig.properties");
        URL_PREFIX = propReader.getProperty("url_prefix");
        URL_SUFFIX = propReader.getProperty("url_suffix");
    }

    public static final String getProperty(String in) {
        return propReader.getProperty(in);
    }

    private StaticProperties() {
        throw new AssertionError();
    }
}
