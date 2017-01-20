/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import org.apache.log4j.Logger;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import java.lang.reflect.InvocationTargetException;

import javax.swing.Action;

import de.cismet.cids.custom.wunda.oab.MapVisualisationActionProvider;
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
public abstract class AbstractOabVisualisationProvider implements MapVisualisationProvider,
    MapVisualisationActionProvider {

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
                    final Action action = buildAction(bean);

                    if (action instanceof OabMapVisualisationAction) {
                        ((OabMapVisualisationAction)action).setAutoAddFeatureToMap(false);
                    }

                    action.actionPerformed(new ActionEvent(
                            AbstractOabVisualisationProvider.this,
                            ActionEvent.ACTION_PERFORMED,
                            OabUtilities.GOTO_USEROBJECT_COMMAND));

                    if (action instanceof OabMapVisualisationAction) {
                        h.action = ((OabMapVisualisationAction)action);
                    }
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
            return new CidsFeature(h.action.getFeatureBean().getMetaObject()); // NOI18N
        } else {
            return null;
        }
    }
}
