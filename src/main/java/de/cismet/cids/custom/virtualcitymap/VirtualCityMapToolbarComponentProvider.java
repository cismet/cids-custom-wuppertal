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

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.utils.WundaBlauServerResources;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.menu.CidsUiComponent;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class VirtualCityMapToolbarComponentProvider implements ToolbarComponentsProvider,
    ConnectionContextStore,
    CidsUiComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static VCMControlFeature currentVCMControlFeature = null;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            VirtualCityMapToolbarComponentProvider.class);
    private static final String HINTSEPARATOR_BEFORE = "<";
    private static final String HINTSEPARATOR_AFTER = ">";

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public String getPluginName() {
        return "VirtualCityMapControl";
    }

    @Override
    public Collection<ToolbarComponentDescription> getToolbarComponents() {
        final Object[] toolbarConfig = getToolbarConfig();
        if (toolbarConfig != null) {
            final String toolbarId = (String)toolbarConfig[0];
            final ToolbarPositionHint toolbarPositionHint = (ToolbarPositionHint)toolbarConfig[1];
            final String toolbarHintTarget = (String)toolbarConfig[2];

            final List<ToolbarComponentDescription> preparationList = new LinkedList<>();
            final ToolbarComponentDescription description = new ToolbarComponentDescription(
                    toolbarId,
                    new VirtualCityMapToolbarComponentProvider.VirtualCityMapButton(getConnectionContext()),
                    toolbarPositionHint,
                    toolbarHintTarget);
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
    public Object[] getToolbarConfig() {
        final VCMProperties properties = VCMProperties.getInstance();
        if (properties.isEmpty()) {
            LOG.warn("openVCM openVCM(). properties are empty. you should check this server_resource: "
                        + WundaBlauServerResources.VCM_PROPERTIES.getValue());
            LOG.info("trying to load the properties from server_resource");
            properties.load(getConnectionContext());
        }

        final String configAttr = properties.getToolbarConfAttr();
        if (configAttr != null) {
            try {
                final String result = SessionManager.getConnection()
                            .getConfigAttr(SessionManager.getSession().getUser(),
                                configAttr,
                                getConnectionContext());
                if (result != null) {
                    if (result.contains(HINTSEPARATOR_BEFORE)) {
                        final String[] split = result.split(HINTSEPARATOR_BEFORE);
                        if (split.length == 2) {
                            return new Object[] { split[0], ToolbarPositionHint.BEFORE, split[1] };
                        }
                    } else if (result.contains(HINTSEPARATOR_AFTER)) {
                        final String[] split = result.split(HINTSEPARATOR_AFTER);
                        if (split.length == 2) {
                            return new Object[] { split[0], ToolbarPositionHint.AFTER, split[1] };
                        }
                    }
                }
            } catch (final ConnectionException ex) {
                LOG.info("Can not check ConfigAttr: " + configAttr, ex);
            }
        }
        return null;
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public String getValue(final String key) {
        if (key.equals(CidsUiComponent.CIDS_ACTION_KEY)) {
            return "VirtualCityMapToolbar";
        } else {
            return null;
        }
    }

    @Override
    public Component getComponent() {
        return new VirtualCityMapToolbarComponentProvider.VirtualCityMapButton(connectionContext);
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
         *
         * @param  connectionContext  DOCUMENT ME!
         */
        public VirtualCityMapButton(final ConnectionContext connectionContext) {
            super(new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    final VCMControlFeature vcmf = new VCMControlFeature(connectionContext);

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
                    VirtualCityMapToolbarComponentProvider.class,
                    "VirtualCityMapToolbarComponent.tooltip"));
            this.setIcon(icon);
            setFocusPainted(false);
            setBorderPainted(false);
        }
    }
}
