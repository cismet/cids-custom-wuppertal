/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import de.cismet.cids.custom.wunda_blau.res.StaticProperties;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;
import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import java.net.URL;
import javax.swing.JComponent;

/**
 *
 * @author srichter
 */
public class LocationinstanceFeatureRenderer extends CustomCidsFeatureRenderer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LocationinstanceFeatureRenderer.class);
    private static final FeatureAnnotationSymbol DEFAULT_SYMBOL;
    private FeatureAnnotationSymbol symbol;

    static {
        DEFAULT_SYMBOL = safeGetSymbolFromURLString(StaticProperties.POI_SIGNATUR_DEFAULT_ICON);
    }

    private static final FeatureAnnotationSymbol safeGetSymbolFromURLString(final String in) {
        FeatureAnnotationSymbol ret = getSymbolFromURLString(in);
        if (ret != null) {
            return ret;
        } else {
            return new FeatureAnnotationSymbol();
        }
    }

    private static final FeatureAnnotationSymbol getSymbolFromURLString(final String in) {
        if (in != null && in.length() > 0) {
            FeatureAnnotationSymbol ret;
            try {
                final URL defaultSymbolURL = new URL(in);
                ret = new FeatureAnnotationSymbol(defaultSymbolURL);
                return ret;
            } catch (Exception ex) {
                log.warn(ex, ex);
            }
        }
        return null;
    }

    @Override
    public void assign() {
        cidsBean = metaObject.getBean();
        symbol = null;
        Object o = cidsBean.getProperty("signatur");
        if (o instanceof CidsBean) {
            String iconUrl = getUrlStringFromSignature(o);
            symbol = getSymbolFromURLString(iconUrl);
        }
        if (symbol == null) {
            o = cidsBean.getProperty("mainlocationtype");
            if (o instanceof CidsBean) {
                final CidsBean mainLocationType = (CidsBean) o;
                o = mainLocationType.getProperty("signatur");
                String iconUrl = getUrlStringFromSignature(o);
                symbol = getSymbolFromURLString(iconUrl);
            }
        } else {
            symbol = DEFAULT_SYMBOL;
        }

    }

    private final String getUrlStringFromSignature(Object signature) {
        if (signature instanceof CidsBean) {
            final CidsBean signatur = (CidsBean) signature;
            final Object fileName = signatur.getProperty("filename");
            final Object fileExtension = signatur.getProperty("fileextension");
            if (fileName != null && fileExtension != null) {
                return StaticProperties.POI_SIGNATUR_URL_PREFIX + fileName + fileExtension + StaticProperties.POI_SIGNATUR_URL_SUFFIX;
            }
        }
        return null;
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        final FeatureAnnotationSymbol ret = symbol;
        if (ret != null) {
            return ret;
        } else if (DEFAULT_SYMBOL != null) {
            return DEFAULT_SYMBOL;
        } else {
            return new FeatureAnnotationSymbol();
        }
    }

    @Override
    public JComponent getInfoComponent(Refreshable refresh) {
        return null;
    }
}
