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

public class Fnp_aenderungenFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 1.0f;
    }

    @Override
    public Stroke getLineStyle() {
        return new CustomFixedWidthStroke(5.0f);
    }

    @Override
    public Paint getLinePaint() {
        final String rechtswirksam = (String)cidsBean.getProperty("rechtswirk");
        if (rechtswirksam == null) {
            return new Color(255, 0, 0, 50);
        }

        return new Color(0, 255, 0, 50);
    }

    @Override
    public Paint getFillingStyle() {
        final String rechtswirksam = (String)cidsBean.getProperty("rechtswirk");

        if (rechtswirksam == null) {
            return new Color(255, 0, 0, 255);
        }

        return new Color(0, 255, 0, 50);
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public void assign() {
        cidsBean = metaObject.getBean();
    }
}
