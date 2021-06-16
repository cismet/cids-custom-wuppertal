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
import java.awt.Stroke;

import javax.swing.JComponent;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class PfPotenzialflaecheFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            PfPotenzialflaecheFeatureRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public Paint getFillingStyle() {
        return getColor();
    }

    @Override
    public Stroke getLineStyle() {
        return new CustomFixedWidthStroke(1.5f);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Color getColor() {
        Color color = Color.RED;
        final String colorString = (String)metaObject.getBean().getProperty("kampagne.colorcode");
        if (colorString != null) {
            try {
                color = Color.decode(colorString);
            } catch (final Exception ex) {
                LOG.warn("color code couldn't be decoded, falling back to default color (red)", ex);
            }
        }
        return color;
    }

    @Override
    public Paint getLinePaint() {
        return getColor();
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public void assign() {
    }
}
