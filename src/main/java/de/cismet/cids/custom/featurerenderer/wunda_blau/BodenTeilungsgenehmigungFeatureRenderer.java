/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.featurerenderer.wunda_blau;

import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObject;

import java.awt.Color;
import java.awt.Paint;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.featurerenderer.CustomCidsFeatureRenderer;

import de.cismet.cismap.commons.Refreshable;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class BodenTeilungsgenehmigungFeatureRenderer extends CustomCidsFeatureRenderer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Color ORANGE_FILL = new Color(243, 134, 48);
    private static final Color ORANGE_LINE = new Color(250, 105, 0);
    private static final Color YELLOW = new Color(249, 212, 35);

    //~ Instance fields --------------------------------------------------------

    private final Set<Integer> flurstueckeBeans = new HashSet<Integer>();

    //~ Methods ----------------------------------------------------------------

    @Override
    public void assign() {
    }
    @Override
    public Paint getFillingStyle(final CidsFeature subFeature) {
        if (subFeature != null) {
            final Integer id = subFeature.getMetaObject().getID();
            if (flurstueckeBeans.contains(id)) {
                return ORANGE_FILL;
            }
        }
        if (subFeature.getMetaClass().getTableName().equalsIgnoreCase("boden_teilungsgenehmigung")) {
            return YELLOW;
        }
        return Color.RED;
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
    public Paint getLinePaint(final CidsFeature subFeature) {
        return ORANGE_LINE;
    }
}
