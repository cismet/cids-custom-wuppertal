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
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import java.awt.Color;
import java.awt.Paint;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.wunda_blau.PoiTools;
import de.cismet.cids.custom.wunda_blau.res.StaticProperties;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Poi_locationinstanceFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            Poi_locationinstanceFeatureRenderer.class);
    private static final FeatureAnnotationSymbol DEFAULT_SYMBOL;

    static {
        DEFAULT_SYMBOL = getSymbolFromResourceString(StaticProperties.POI_SIGNATUR_DEFAULT_ICON);
        if (DEFAULT_SYMBOL != null) {
            DEFAULT_SYMBOL.setSweetSpotX(0.5d);
            DEFAULT_SYMBOL.setSweetSpotY(0.5d);
        }
    }

    //~ Instance fields --------------------------------------------------------

    private FeatureAnnotationSymbol symbol;
    private boolean assigned = false;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   in  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static FeatureAnnotationSymbol getSymbolFromResourceString(final String in) {
        final ImageIcon icon = PoiTools.getIconFromResourceString(in);
        if (icon != null) {
            final FeatureAnnotationSymbol ret = new FeatureAnnotationSymbol(icon.getImage());
            if (ret == null) {
                log.error("konnte kein FAS aus:" + in + "erzeugen");
            }
            return ret;
        }
        return null;
    }

    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        return new Color(0.5f, 0.5f, 0.5f, 0.1f);
    }

    @Override
    public Paint getLinePaint() {
        return new Color(0f, 0f, 0f, 0.5f);
    }

    @Override
    public void assign() {
        if (metaObject != null) {
            cidsBean = metaObject.getBean();
            if (cidsBean != null) {
                final ImageIcon ii = PoiTools.getPoiSignatureIcon(cidsBean);

                if (ii != null) {
                    symbol = new FeatureAnnotationSymbol(ii.getImage());
                }
                if (symbol == null) {
                    symbol = DEFAULT_SYMBOL;
                } else {
                    symbol.setSweetSpotX(0.5d);
                    symbol.setSweetSpotY(0.5d);
                }
            }
        }
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        // BUGFIX - ugly but necessary
        if (!assigned) {
            assign();
        }
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
    public float getTransparency() {
        return 0.95f;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }
}
