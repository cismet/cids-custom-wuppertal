/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 srichter
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.wunda_blau.AlkisBuchungsblattRenderer;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.FixedWidthStroke;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public class Alkis_buchungsblattFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static int COLOR_INDEX = 0;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            AlkisBuchungsblattRenderer.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public synchronized Paint getFillingStyle(final CidsFeature subFeature) {
        ++COLOR_INDEX;
        COLOR_INDEX %= AlkisBuchungsblattRenderer.LANDPARCEL_COLORS.size();
        return AlkisBuchungsblattRenderer.LANDPARCEL_COLORS.get(COLOR_INDEX);
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh, final CidsFeature subFeature) {
        return null;
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        return Color.BLACK;
    }

    @Override
    public Stroke getLineStyle(final CidsFeature subFeature) {
        return new FixedWidthStroke();
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol(final CidsFeature subFeature) {
        return null;
    }

    @Override
    public float getTransparency(final CidsFeature subFeature) {
        return 0.6f;
    }

    @Override
    public Paint getFillingStyle() {
        return getFillingStyle(null);
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh) {
        return getInfoComponent(refresh, null);
    }

    @Override
    public Paint getLinePaint() {
        return getLinePaint(null);
    }

    @Override
    public Stroke getLineStyle() {
        return getLineStyle(null);
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        return getPointSymbol(null);
    }

    @Override
    public float getTransparency() {
        return getTransparency(null);
    }

    @Override
    public void assign() {
    }
}
