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

import Sirius.server.localserver.attribute.Attribute;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KaufvertragFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            KaufvertragFeatureRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public float getTransparency() {
        return 0.5f;
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        Color c = Color.ORANGE;
        final int size = 20;
        try {
            final Object attrib = metaObject.getAttributeByName("TEILMARKT", 1).toArray()[0];
            final String tm = ((Attribute)attrib).toString().substring(0, 1);
            if (tm.startsWith("b")) {
                c = Color.RED;
            } else if (tm.startsWith("E")) {
                c = Color.BLACK;
            } else if (tm.startsWith("u")) {
                c = Color.GREEN;
            }
            if (tm.startsWith("W")) {
                c = Color.BLUE;
            }
        } catch (Throwable t) {
            LOG.warn("Fehler in getPointSymbol()", t);
        }

        final BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics = (Graphics2D)bi.getGraphics();
        graphics.setColor(c);
        graphics.fillOval(0, 0, size, size);
        final FeatureAnnotationSymbol symb = new FeatureAnnotationSymbol(bi);
        symb.setSweetSpotX(0.5);
        symb.setSweetSpotY(0.5);
        return symb;
    }

    @Override
    public Stroke getLineStyle() {
        return null;
    }

    @Override
    public Paint getLinePaint() {
        return null;
    }

    @Override
    public Paint getFillingStyle() {
        return null;
    }

    @Override
    public void assign() {
        return;
    }
}
