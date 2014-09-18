/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import java.awt.Color;
import java.awt.Paint;

import java.util.Properties;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Alkis_landparcelFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final int AQUA_SKY = 0x3ACBC7;
    private static Color FILLING_STYLE_COLOR;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            Alkis_landparcelFeatureRenderer.class);

    static {
        try {
            final Properties prop = new Properties();
            prop.load(Alkis_landparcelFeatureRenderer.class.getResourceAsStream(
                    "/de/cismet/cids/custom/wunda_blau/res/alkis/alkis_conf.properties"));
            final String rgbValueStr = prop.getProperty("LANDPARCEL_FEATURE_RENDERER_COLOR");
            int rgbValue;
            if (rgbValueStr != null) {
                rgbValue = Integer.parseInt(rgbValueStr, 16);
            } else {
                rgbValue = AQUA_SKY;
            }
            FILLING_STYLE_COLOR = new Color(rgbValue);
        } catch (Exception ex) {
            LOG.warn("Bundle could not be loaded", ex);
            FILLING_STYLE_COLOR = new Color(AQUA_SKY);
        }
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
        // do nothing
    }

    @Override
    public Paint getFillingStyle() {
        return FILLING_STYLE_COLOR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        final Alkis_landparcelFeatureRenderer ren = new Alkis_landparcelFeatureRenderer();
        System.out.println(ren.getFillingStyle());
    }
}
