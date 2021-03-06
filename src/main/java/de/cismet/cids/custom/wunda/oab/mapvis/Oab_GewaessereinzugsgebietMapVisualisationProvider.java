/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import Sirius.navigator.ui.ComponentRegistry;

import java.awt.event.ActionEvent;

import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;

import de.cismet.cids.custom.wunda.oab.MapVisualisationActionProvider;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;
import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_GewaessereinzugsgebietMapVisualisationProvider implements MapVisualisationProvider,
    MapVisualisationActionProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Feature getFeature(final CidsBean bean) {
        return new CidsFeature(bean.getMetaObject());
    }

    @Override
    public Action buildAction(final CidsBean bean) {
        return new AbstractAction() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Feature feature = new CidsFeature(bean.getMetaObject());
                    CismapBroker.getInstance().getMappingComponent().getFeatureCollection().addFeature(feature);
                    ComponentRegistry.getRegistry().showComponent("map");
                    CismapBroker.getInstance()
                            .getMappingComponent()
                            .zoomToAFeatureCollection(Arrays.asList(feature), true, false);
                }
            };
    }
}
