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

import de.cismet.cids.custom.utils.alkis.AlkisConstants;

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
            final String rgbValueStr = AlkisConstants.COMMONS.LANDPARCEL_FEATURE_RENDERER_COLOR;
            int rgbValue;
            if (rgbValueStr != null) {
                rgbValue = Integer.parseInt(rgbValueStr, 16);
            } else {
                rgbValue = AQUA_SKY;
            }
            FILLING_STYLE_COLOR = new Color(rgbValue);
        } catch (final Exception ex) {
            LOG.warn("AlkisConf could not be loaded", ex);
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
