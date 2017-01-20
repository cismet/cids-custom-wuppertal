/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import Sirius.navigator.ui.ComponentRegistry;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_BerechnungMapVisualisationProvider extends AbstractOabVisualisationProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public OabMapVisualisationAction buildAction(final CidsBean bean) {
        final OabMapVisualisationDialog dialog = new OabMapVisualisationDialog();
        dialog.setFeatureBean((CidsBean)bean.getProperty("zustand_massnahme"));
        dialog.setBeVisible(true);
        dialog.setBeCapabilitiesUrl((String)bean.getProperty("zustand_massnahme.bruchkanten_cap"));  // NOI18N
        dialog.setBeLayername((String)bean.getProperty("zustand_massnahme.bruchkanten_layer_name")); // NOI18N
        dialog.setTinVisible(true);
        dialog.setTinCapabilitiesUrl((String)bean.getProperty("zustand_massnahme.tin_cap"));         // NOI18N
        dialog.setTinLayername((String)bean.getProperty("zustand_massnahme.tin_layer_name"));        // NOI18N
        dialog.setMaxWaterVisible(true);
        dialog.setMaxWaterCapabilitiesUrl((String)bean.getProperty("max_wasser_cap"));               // NOI18N
        dialog.setMaxWaterLayername((String)bean.getProperty("max_wasser_layer_name"));              // NOI18N
        dialog.setTSWaterVisible(true);
        dialog.setTsWaterCapabilitiesUrl((String)bean.getProperty("zr_wasser_cap"));                 // NOI18N
        dialog.setTsWaterLayername((String)bean.getProperty("zr_wasser_layer_name"));                // NOI18N

        return new OabMapVisualisationAction(ComponentRegistry.getRegistry().getMainWindow(), dialog);
    }
}
