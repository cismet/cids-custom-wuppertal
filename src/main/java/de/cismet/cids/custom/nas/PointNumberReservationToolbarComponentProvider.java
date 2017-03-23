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
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

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

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class PointNumberReservationToolbarComponentProvider implements ToolbarComponentsProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PointNumberReservationToolbarComponentProvider.class);

    //~ Methods ----------------------------------------------------------------

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
                    new PunktNummernButton(),
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
    private static boolean validateUserHasAccess() {
        try {
            return SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(), "csa://pointNumberReservation")
                        != null;
        } catch (final Exception ex) {
            LOG.error("Could not validate action tag for PunktnummernReservierung!", ex);
        }
        return false;
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
         */
        public PunktNummernButton() {
            super(new AbstractAction() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    final PointNumberDialog dialog = new PointNumberDialog(
                                            StaticSwingTools.getParentFrame(
                                                CismapBroker.getInstance().getMappingComponent()),
                                            true);
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
