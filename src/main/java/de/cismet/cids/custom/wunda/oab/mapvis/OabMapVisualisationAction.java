/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda.oab.mapvis;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.SimpleGetFeatureInfoUrl;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;

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
                        map.getFeatureCollection().addFeature(new CidsFeature(dialog.getFeatureBean().getMetaObject()));
                    } else {
                        featureAdditionSelected = true;
                    }
                }

                if (dialog.isAddTin()) {
                    final SimpleWMS layer = new SimpleWMS(new SimpleGetFeatureInfoUrl(dialog.getTinGetMapUrl()));
                    map.getMappingModel().addLayer(layer);
                }

                if (dialog.isAddBE()) {
                    final SimpleWMS layer = new SimpleWMS(new SimpleGetFeatureInfoUrl(dialog.getBeGetMapUrl()));
                    map.getMappingModel().addLayer(layer);
                }

                if (dialog.isAddMaxWater()) {
                    final SimpleWMS layer = new SimpleWMS(new SimpleGetFeatureInfoUrl(dialog.getMaxWaterGetMapUrl()));
                    map.getMappingModel().addLayer(layer);
                }

                if (dialog.isAddTSWater()) {
                    final SimpleWMS layer = new SimpleWMS(new SimpleGetFeatureInfoUrl(dialog.getTsWaterGetMapUrl()));
                    map.getMappingModel().addLayer(layer);
                }
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
