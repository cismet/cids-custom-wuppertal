/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.lang.reflect.InvocationTargetException;

import de.cismet.cids.custom.wunda.oab.OabUtilities;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;

import de.cismet.cismap.navigatorplugin.CidsFeature;
import de.cismet.cismap.navigatorplugin.MapVisualisationProvider;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class Oab_BerechnungMapVisualisationProvider implements MapVisualisationProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger log = Logger.getLogger(Oab_BerechnungMapVisualisationProvider.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public Feature getFeature(final CidsBean bean) {
        /**
         * DOCUMENT ME!
         *
         * @version  $Revision$, $Date$
         */
        class Holder {

            OabMapVisualisationAction action;
        }

        final Holder h = new Holder();

        final Runnable r = new Runnable() {

                @Override
                public void run() {
                    final OabMapVisualisationDialog dialog = new OabMapVisualisationDialog();
                    dialog.setBeVisible(true);
                    dialog.setBeGetMapUrl((String)bean.getProperty("zustand_massnahme.bruchkanten_simple_getmap")); // NOI18N
                    dialog.setTinVisible(true);
                    dialog.setTinGetMapUrl((String)bean.getProperty("zustand_massnahme.tin_simple_getmap"));        // NOI18N
                    dialog.setMaxWaterVisible(true);
                    dialog.setMaxWaterGetMapUrl((String)bean.getProperty("max_wasser_simple_getmap"));              // NOI18N
                    dialog.setTSWaterVisible(true);
                    dialog.setTsWaterGetMapUrl((String)bean.getProperty("zr_wasser_simple_getmap"));                // NOI18N

                    final OabMapVisualisationAction action = new OabMapVisualisationAction(ComponentRegistry
                                    .getRegistry().getMainWindow(),
                            dialog);
                    action.setAutoAddFeatureToMap(false);
                    action.actionPerformed(new ActionEvent(
                            Oab_BerechnungMapVisualisationProvider.this,
                            ActionEvent.ACTION_PERFORMED,
                            OabUtilities.GOTO_USEROBJECT_COMMAND));
                    h.action = action;
                }
            };

        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            try {
                EventQueue.invokeAndWait(r);
            } catch (final InterruptedException ex) {
                log.error("cannot wait for dialog to be finished", ex); // NOI18N
            } catch (final InvocationTargetException ex) {
                log.error("cannot wait for dialog to be finished", ex); // NOI18N
            }
        }

        if ((h.action != null) && h.action.isFeatureAdditionSelected()) {
            return new CidsFeature(((CidsBean)bean.getProperty("zustand_massnahme")).getMetaObject()); // NOI18N
        } else {
            return null;
        }
    }
}
