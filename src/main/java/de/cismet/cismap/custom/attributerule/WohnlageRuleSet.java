/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cismap.custom.attributerule;

import Sirius.server.middleware.types.MetaClass;

import org.deegree.style.se.unevaluated.Style;

import java.awt.Color;
import java.awt.Paint;

import java.util.List;
import java.util.Map;

import de.cismet.cids.server.cidslayer.CidsLayerInfo;

import de.cismet.cismap.cidslayer.CidsLayerFeature;

import de.cismet.cismap.commons.features.FeatureServiceFeature;
import de.cismet.cismap.commons.featureservice.LayerProperties;
import de.cismet.cismap.commons.gui.attributetable.DefaultAttributeTableRuleSet;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.SelectableServiceFeature;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class WohnlageRuleSet extends DefaultAttributeTableRuleSet {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Class<? extends FeatureServiceFeature> getFeatureClass() {
        return WohnlageLayerFeature.class;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class WohnlageLayerFeature extends CidsLayerFeature implements SelectableServiceFeature {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new WohnlageLayerFeature object.
         *
         * @param  feature  DOCUMENT ME!
         */
        public WohnlageLayerFeature(final CidsLayerFeature feature) {
            super(feature);
        }

        /**
         * Creates a new WohnlageLayerFeature object.
         *
         * @param  properties       DOCUMENT ME!
         * @param  metaClass        DOCUMENT ME!
         * @param  layerInfo        DOCUMENT ME!
         * @param  layerProperties  DOCUMENT ME!
         * @param  styles           DOCUMENT ME!
         */
        public WohnlageLayerFeature(
                final Map<String, Object> properties,
                final MetaClass metaClass,
                final CidsLayerInfo layerInfo,
                final LayerProperties layerProperties,
                final List<Style> styles) {
            super(properties, metaClass, layerInfo, layerProperties, styles);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Paint getFillingPaint() {
            return new Color((Integer)getProperty("farbcode"));
        }
    }
}
