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
package de.cismet.cids.custom.virtualcitymap;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.NbBundle;

import java.awt.event.ActionEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.nas.PointNumberReservationToolbarComponentProvider;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class VirtualCityMapToolbarComponentProvider implements ToolbarComponentsProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static VCMControlFeature currentVCMControlFeature = null;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VirtualCityMapToolbarComponentProvider.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getPluginName() {
        return "VirtualCityMapControl";
    }

    @Override
    public Collection<ToolbarComponentDescription> getToolbarComponents() {
        if (checkActionTag()) {
            final List<ToolbarComponentDescription> preparationList = new LinkedList<ToolbarComponentDescription>();
            final ToolbarComponentDescription description = new ToolbarComponentDescription(
                    "tlbMain",
                    new VirtualCityMapToolbarComponentProvider.VirtualCityMapButton(),
                    ToolbarPositionHint.AFTER,
                    "cmdPan");
            preparationList.add(description);

            return Collections.unmodifiableList(preparationList);
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean checkActionTag() {
        final String actionTag = VCMProperties.getInstance().getActionAttr();
        boolean result;
        try {
            result = SessionManager.getConnection().getConfigAttr(SessionManager.getSession().getUser(), actionTag)
                        != null;
        } catch (final ConnectionException ex) {
            LOG.info("Can not check ActionTag: " + actionTag, ex);
            result = false;
        }
        return result;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class VirtualCityMapButton extends JButton {

        //~ Instance fields ----------------------------------------------------

        Icon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/virtualcitymap/vcm22.png")); // NOI18N

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PunktNummernButton object.
         */
        public VirtualCityMapButton() {
            super(new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    final VCMControlFeature vcmf = new VCMControlFeature();

                                    final Geometry bb = CismapBroker.getInstance()
                                                .getMappingComponent()
                                                .getCurrentBoundingBoxFromCamera()
                                                .getGeometry(
                                                    CrsTransformer.extractSridFromCrs(
                                                        CismapBroker.getInstance().getSrs().getCode()));

                                    final double h = bb.getEnvelopeInternal().getHeight();
                                    final double w = bb.getEnvelopeInternal().getWidth();

                                    if (h > w) {
                                        vcmf.setGeometry(bb.getCentroid().buffer(w / 2 * 0.625).getEnvelope());
                                    } else {
                                        vcmf.setGeometry(bb.getCentroid().buffer(h / 2 * 0.625).getEnvelope());
                                    }
                                    if (currentVCMControlFeature != null) {
                                        CismapBroker.getInstance()
                                                .getMappingComponent()
                                                .getFeatureCollection()
                                                .removeFeature(currentVCMControlFeature);
                                        currentVCMControlFeature = null;
                                    }
                                    CismapBroker.getInstance()
                                            .getMappingComponent()
                                            .getFeatureCollection()
                                            .addFeature(vcmf);
                                    currentVCMControlFeature = vcmf;
                                }
                            });
                    }
                });
            super.setToolTipText(NbBundle.getMessage(
                    PointNumberReservationToolbarComponentProvider.class,
                    "PointNumberReservationToolbarComponent.tooltip"));
            this.setIcon(icon);
            setFocusPainted(false);
            setBorderPainted(false);
        }
    }
}
