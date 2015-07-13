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
        dialog.setBeGetMapUrl((String)bean.getProperty("zustand_massnahme.bruchkanten_simple_getmap")); // NOI18N
        dialog.setTinVisible(true);
        dialog.setTinGetMapUrl((String)bean.getProperty("zustand_massnahme.tin_simple_getmap"));        // NOI18N
        dialog.setMaxWaterVisible(true);
        dialog.setMaxWaterGetMapUrl((String)bean.getProperty("max_wasser_simple_getmap"));              // NOI18N
        dialog.setTSWaterVisible(true);
        dialog.setTsWaterGetMapUrl((String)bean.getProperty("zr_wasser_simple_getmap"));                // NOI18N

        return new OabMapVisualisationAction(ComponentRegistry.getRegistry().getMainWindow(), dialog);
    }
}
