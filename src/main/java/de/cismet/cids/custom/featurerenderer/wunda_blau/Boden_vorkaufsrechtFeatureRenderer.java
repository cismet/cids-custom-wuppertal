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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;

import java.util.HashSet;
import java.util.Set;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.gui.piccolo.FixedWidthStroke;

import de.cismet.cismap.navigatorplugin.CidsFeature;

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
    private static final Color ORANGE_FILL = new Color(243, 134, 48);
    private static final Color ORANGE_LINE = new Color(250, 105, 0);

    //~ Instance fields --------------------------------------------------------

    private final Set<Integer> flurstueckeBeans = new HashSet<Integer>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ABoden_VorkaufsrechtFeatureRenderer object.
     */
    public Boden_vorkaufsrechtFeatureRenderer() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        return ORANGE_FILL;
//        log.info("test getFillingStyle");
//        if (subFeature != null) {
//            final Integer id = subFeature.getMetaObject().getID();
//            if (flurstueckeBeans.contains(id)) {
//                return ORANGE_FILL;
//            }
//        }
//        return Color.RED;
    }

    @Override
    public Paint getLinePaint(final CidsFeature subFeature) {
        return ORANGE_LINE;
    }

    @Override
    public Stroke getLineStyle(final CidsFeature subFeature) {
        return new FixedWidthStroke();
    }

    @Override
    public float getTransparency(final CidsFeature subFeature) {
        return 0.6f;
    }

//    @Override
//    public void setMetaObject(final MetaObject metaObject) throws ConnectionException {
//        super.setMetaObject(metaObject);
//        flurstueckeBeans.clear();
//
//        if (cidsBean != null) {
//            final Collection<CidsBean> beans;
//            beans = CidsBeanSupport.getBeanCollectionFromProperty(cidsBean, "ref_flurstuecke");
//            for (final CidsBean bean : beans) {
//                flurstueckeBeans.add(bean.getMetaObject().getID());
//            }
//        }
//    }

    @Override
    public void assign() {
    }
}
