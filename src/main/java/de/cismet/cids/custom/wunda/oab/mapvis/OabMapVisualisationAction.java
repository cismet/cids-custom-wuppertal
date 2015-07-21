/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import Sirius.navigator.plugin.PluginRegistry;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import de.cismet.cids.custom.wunda.oab.OabUtilities;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.CidsFeature;

/**
 * DOCUMENT ME!
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
public class OabMapVisualisationAction extends AbstractAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger log = Logger.getLogger(OabMapVisualisationAction.class);

    //~ Instance fields --------------------------------------------------------

    private final Component parent;
    private final OabMapVisualisationDialog dialog;

    private boolean autoAddFeatureToMap;
    private boolean featureAdditionSelected;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OabMapVisualisationAction object.
     *
     * @param   parent  DOCUMENT ME!
     * @param   dialog  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  DOCUMENT ME!
     */
    public OabMapVisualisationAction(final Component parent, final OabMapVisualisationDialog dialog) {
        super(NbBundle.getMessage(
                OabMapVisualisationAction.class,
                "OabMapVisualisationAction.<init>(Component,OabMapVisualisationDialog).super.name")); // NOI18N

        if (parent == null) {
            throw new IllegalArgumentException("parent must not be null"); // NOI18N
        }
        if (dialog == null) {
            throw new IllegalArgumentException("dialog must not be null"); // NOI18N
        }

        this.parent = parent;
        this.dialog = dialog;
        autoAddFeatureToMap = true;
        featureAdditionSelected = false;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void actionPerformed(final ActionEvent e) {
        final int answer = JOptionPane.showConfirmDialog(
                parent,
                dialog,
                NbBundle.getMessage(
                    OabMapVisualisationAction.class,
                    "OabMapVisualisationAction.actionPerformed(ActionEvent).dialog.title"), // NOI18N
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (answer == JOptionPane.OK_OPTION) {
            try {
                final MappingComponent map = CismapBroker.getInstance().getMappingComponent();

                if (dialog.isAddFeature()) {
                    if (isAutoAddFeatureToMap()) {
                        final Feature feature = new CidsFeature(dialog.getFeatureBean().getMetaObject());
                        map.getFeatureCollection().addFeature(feature);
                        CismapBroker.getInstance()
                                .getMappingComponent()
                                .zoomToAFeatureCollection(Arrays.asList(feature), true, false);
                    } else {
                        featureAdditionSelected = true;
                    }
                }

                if (dialog.isAddTin()) {
                    final String cap = dialog.getTinCapabilitiesUrl();
                    final String lname = dialog.getTinLayername();
                    map.getMappingModel().addLayer(OabUtilities.createWMSLayer(cap, lname));
                }

                if (dialog.isAddBE()) {
                    final String cap = dialog.getBeCapabilitiesUrl();
                    final String lname = dialog.getBeLayername();
                    map.getMappingModel().addLayer(OabUtilities.createWMSLayer(cap, lname));
                }

                if (dialog.isAddMaxWater()) {
                    final String cap = dialog.getMaxWaterCapabilitiesUrl();
                    final String lname = dialog.getMaxWaterLayername();
                    map.getMappingModel().addLayer(OabUtilities.createWMSLayer(cap, lname));
                }

                if (dialog.isAddTSWater()) {
                    final String cap = dialog.getTsWaterCapabilitiesUrl();
                    final String lname = dialog.getTsWaterLayername();
                    map.getMappingModel().addLayer(OabUtilities.createWMSLayer(cap, lname));
                }

                PluginRegistry.getRegistry()
                        .getPluginDescriptor("cismap")
                        .getUIDescriptor("cismap")
                        .getView()
                        .makeVisible();
            } catch (final Exception ex) {
                log.warn("illegal action setup, oab map visualisation state undefined", ex); // NOI18N
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  autoAddFeatureToMap  DOCUMENT ME!
     */
    public void setAutoAddFeatureToMap(final boolean autoAddFeatureToMap) {
        this.autoAddFeatureToMap = autoAddFeatureToMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAutoAddFeatureToMap() {
        return autoAddFeatureToMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isFeatureAdditionSelected() {
        return featureAdditionSelected;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getFeatureBean() {
        return dialog.getFeatureBean();
    }
}
