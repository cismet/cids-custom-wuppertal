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

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;
import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.features.FeatureRenderer;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import javax.swing.JComponent;

/**
 *
 * @author srichter
 */
public class Alb_baulastFeatureRenderer implements FeatureRenderer {

    private static final Color BELASTET = new Color(0, 255, 0);
    private static final Color BEGUENSTIGT = new Color(255, 255, 0);

    @Override
    public Paint getFillingStyle() {
        return BEGUENSTIGT;
    }

    @Override
    public Stroke getLineStyle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Paint getLinePaint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public float getTransparency() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureAnnotationSymbol getPointSymbol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JComponent getInfoComponent(Refreshable refresh) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
