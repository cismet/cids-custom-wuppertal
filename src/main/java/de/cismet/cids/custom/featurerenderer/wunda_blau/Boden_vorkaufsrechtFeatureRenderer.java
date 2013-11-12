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

import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.Collection;
import java.util.Set;

import javax.swing.JComponent;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;
import de.cismet.cismap.commons.gui.piccolo.FeatureAnnotationSymbol;
import de.cismet.cismap.commons.gui.piccolo.FixedWidthStroke;

import de.cismet.cismap.navigatorplugin.CidsFeature;

import de.cismet.tools.collections.TypeSafeCollections;

/**
 * DOCUMENT ME!
 *
 * @author   verkenis
 * @version  $Revision$, $Date$
 */
public class Boden_vorkaufsrechtFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
            Boden_vorkaufsrechtFeatureRenderer.class);

    //~ Instance fields --------------------------------------------------------

    private final Set<Integer> flurstueckeBeans = TypeSafeCollections.newHashSet();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ABoden_VorkaufsrechtFeatureRenderer object.
     */
    public Boden_vorkaufsrechtFeatureRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        log.info("test getFillingStyle");
        if (subFeature != null) {
            final Integer id = subFeature.getMetaObject().getID();
            if (flurstueckeBeans.contains(id)) {
                return Color.ORANGE;
            }
        }
        return Color.RED;
    }

    @Override
    public JComponent getInfoComponent(final Refreshable refresh, final CidsFeature subFeature) {
        return null;
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        return Color.RED;
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
        flurstueckeBeans.clear();

        if (cidsBean != null) {
            final Collection<CidsBean> beans;
            beans = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");
            for (final CidsBean bean : beans) {
                flurstueckeBeans.add(bean.getMetaObject().getID());
            }
        }
    }

    @Override
    public void assign() {
    }
}
