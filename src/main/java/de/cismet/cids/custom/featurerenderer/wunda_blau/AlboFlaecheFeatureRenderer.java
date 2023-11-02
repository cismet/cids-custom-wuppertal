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
                    return Color.getColor(color, new Color(163, 77, 179));
                }
                case "altstandort": {
                    return Color.getColor(color, new Color(96, 159, 255));
                }
                case "betriebsstandort": {
                    return Color.getColor(color, new Color(23, 254, 00));
                }
                case "rcl": {
                    return Color.getColor(color, new Color(196, 128, 28));
                }
                case "stoffliche": {
                    return Color.getColor(color, Color.RED);
                }
                case "bplan_gutachten": {
                    return Color.getColor(color, new Color(208, 201, 67));
                }
                case "bodenaehnlich":
                case "abraumhalde":
                case "baugrund":
                case "in_betrieb":
                case "sonstiges":
                case "industrieanlagen":
                case "ohne_verdacht": {
                    return Color.getColor(color, new Color(218, 111, 0));
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
