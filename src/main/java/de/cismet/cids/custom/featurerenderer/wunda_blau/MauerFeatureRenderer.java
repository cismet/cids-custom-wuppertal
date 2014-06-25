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
 * @author   reinhard.verkennis
 * @version  $Revision$, $Date$
 */

public class MauerFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 0.9f;
    }

    @Override
    public Paint getFillingStyle() {
        return new Color(255, 0, 0, 50);
    }

    @Override
    public Stroke getLineStyle() {
        return new CustomFixedWidthStroke(3.0f);
    }

    @Override
    public Paint getLinePaint() {
        return new Color(255, 0, 0, 255);
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public void assign() {
    }
}
