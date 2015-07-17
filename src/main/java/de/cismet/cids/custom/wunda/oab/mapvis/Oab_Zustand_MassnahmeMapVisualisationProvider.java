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
public class Oab_Zustand_MassnahmeMapVisualisationProvider extends AbstractOabVisualisationProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public OabMapVisualisationAction buildAction(final CidsBean bean) {
        final OabMapVisualisationDialog dialog = new OabMapVisualisationDialog();
        dialog.setFeatureBean(bean);
        dialog.setTinVisible(true);
        dialog.setTinCapabilitiesUrl((String)bean.getProperty("tin_cap"));         // NOI18N
        dialog.setTinLayername((String)bean.getProperty("tin_layer_name"));        // NOI18N
        dialog.setBeVisible(true);
        dialog.setBeCapabilitiesUrl((String)bean.getProperty("bruchkanten_cap"));  // NOI18N
        dialog.setBeLayername((String)bean.getProperty("bruchkanten_layer_name")); // NOI18N
        dialog.setMaxWaterVisible(false);
        dialog.setTSWaterVisible(false);

        return new OabMapVisualisationAction(ComponentRegistry.getRegistry().getMainWindow(), dialog);
    }
}
