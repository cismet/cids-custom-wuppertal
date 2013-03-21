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

import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

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
public class Alb_baulastblattFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Color BELASTET_COLOR = new Color(0, 255, 0);
    private static final Color BEGUENSTIGT_COLOR = new Color(255, 255, 0);
    private static final Color BEIDES_COLOR = Color.RED;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            Alb_baulastblattFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final Set<Integer> belastetBeans = new HashSet<Integer>();
    private final Set<Integer> beguenstigtBeans = new HashSet<Integer>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastblattFeatureRenderer object.
     */
    public Alb_baulastblattFeatureRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        if ((subFeature != null) && subFeature.getMyAttributeStringInParentFeature().contains("flurstueck")) {
            final Integer id = subFeature.getMetaObject().getID();
            if (belastetBeans.contains(id)) {
                if (beguenstigtBeans.contains(id)) {
                    return BEIDES_COLOR;
                } else {
                    return BELASTET_COLOR;
                }
            } else if (beguenstigtBeans.contains(id)) {
                return BEGUENSTIGT_COLOR;
            } else {
                return Color.RED;
            }
        }
        return null;
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
    public void setMetaObject(final MetaObject metaObject) throws ConnectionException {
        super.setMetaObject(metaObject);
        belastetBeans.clear();
        beguenstigtBeans.clear();
        if (cidsBean != null) {
            final Collection<CidsBean> beans = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "baulasten");
            for (final CidsBean baulast : beans) {
                Collection<CidsBean> flurstueckBeans = CidsBeanSupport.getBeanCollectionFromProperty(
                        baulast,
                        "flurstuecke_belastet");
                for (final CidsBean bean : flurstueckBeans) {
                    belastetBeans.add(bean.getMetaObject().getID());
                }

                flurstueckBeans = CidsBeanSupport.getBeanCollectionFromProperty(baulast, "flurstuecke_beguenstigt");
                for (final CidsBean bean : flurstueckBeans) {
                    beguenstigtBeans.add(bean.getMetaObject().getID());
                }
            }
        }
    }

    @Override
    public void assign() {
    }
}
