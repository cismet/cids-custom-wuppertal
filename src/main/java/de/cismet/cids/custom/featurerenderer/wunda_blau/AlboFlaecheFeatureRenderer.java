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

import de.cismet.cids.custom.objecteditors.utils.ClientAlboProperties;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.CustomFixedWidthStroke;

/**
 * DOCUMENT ME!
 *
 * @author   reinhard.verkennis
 * @version  $Revision$, $Date$
 */

public class AlboFlaecheFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public Paint getFillingStyle() {
        final String art = (String)metaObject.getBean().getProperty("fk_art.schluessel");
        if (art != null) {
            final String color = ClientAlboProperties.getInstance().getColorOfArt(art);
            switch (art) {
                case "altablagerung": {
                    return Color.getColor(color, Color.BLUE);
                }
                case "altstandort": {
                    return Color.getColor(color, Color.CYAN);
                }
                case "betriebsstandort": {
                    return Color.getColor(color, Color.PINK);
                }
                case "sonstige": {
                    return Color.getColor(color, Color.MAGENTA);
                }
                case "ohne_verdacht": {
                    return Color.getColor(color, Color.GREEN);
                }
            }
        }
        return Color.GRAY;
    }

    @Override
    public Stroke getLineStyle() {
        return new CustomFixedWidthStroke(1.5f);
    }

    @Override
    public Paint getLinePaint() {
        return new Color(0, 0, 0, 255);
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return null;
    }

    @Override
    public void assign() {
    }
}
