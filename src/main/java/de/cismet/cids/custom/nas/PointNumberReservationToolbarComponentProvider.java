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
package de.cismet.cids.custom.nas;

import Sirius.navigator.connection.SessionManager;

import org.apache.log4j.Logger;

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

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.menu.CidsUiComponent;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class PointNumberReservationToolbarComponentProvider implements ToolbarComponentsProvider,
    ConnectionContextStore,
    CidsUiComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PointNumberReservationToolbarComponentProvider.class);

    //~ Instance fields --------------------------------------------------------

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public String getPluginName() {
        return "Punktnummernreservierung";
    }

    @Override
    public Collection<ToolbarComponentDescription> getToolbarComponents() {
        if (validateUserHasAccess()) {
            final JButton b = new JButton("Punktnummern");

            final List<ToolbarComponentDescription> preparationList = new LinkedList<ToolbarComponentDescription>();
            final ToolbarComponentDescription description = new ToolbarComponentDescription(
                    "tlbMain",
                    new PunktNummernButton(getConnectionContext()),
                    ToolbarPositionHint.AFTER,
                    "cmdPrint");
            preparationList.add(description);

            return Collections.unmodifiableList(preparationList);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean validateUserHasAccess() {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "csa://pointNumberReservation",
                            getConnectionContext());
        } catch (final Exception ex) {
            LOG.error("Could not validate action tag for PunktnummernReservierung!", ex);
        }
        return false;
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public String getValue(final String key) {
        if (key.equals(CidsUiComponent.CIDS_ACTION_KEY)) {
            return "PointNumberReservationToolbar";
        } else {
            return null;
        }
    }

    @Override
    public Component getComponent() {
        if (validateUserHasAccess()) {
            return new PunktNummernButton(connectionContext);
        } else {
            return null;
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PunktNummernButton extends JButton {

        //~ Instance fields ----------------------------------------------------

        Icon icon = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/nas/punktnummernreservierung.png")); // NOI18N

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PunktNummernButton object.
         *
         * @param  connectionContext  DOCUMENT ME!
         */
        public PunktNummernButton(final ConnectionContext connectionContext) {
            super(new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    final PointNumberDialog dialog = new PointNumberDialog(
                                            StaticSwingTools.getParentFrame(
                                                CismapBroker.getInstance().getMappingComponent()),
                                            true,
                                            connectionContext);
                                    StaticSwingTools.showDialog(dialog);
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
