/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.butler;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.nas.NasDialog;

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.JPopupMenuButton;
import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.menu.CidsUiComponent;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ToolbarComponentsProvider.class)
public class DigitalDataExportToolbarComponentProvider implements ToolbarComponentsProvider,
    ConnectionContextStore,
    CidsUiComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(DigitalDataExportToolbarComponentProvider.class);

    //~ Instance fields --------------------------------------------------------

    private List<ToolbarComponentDescription> toolbarComponents;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DigitalDataExportToolbarComponentProvider object.
     */
    public DigitalDataExportToolbarComponentProvider() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        final List<ToolbarComponentDescription> preparationList = new LinkedList<>();
        final ToolbarComponentDescription description = new ToolbarComponentDescription(
                "tlbMain",
                new DataExportButton(),
                ToolbarPositionHint.AFTER,
                "cmdPrint");
        preparationList.add(description);
        this.toolbarComponents = Collections.unmodifiableList(preparationList);
    }

    @Override
    public String getPluginName() {
        return "BUTLER";
    }

    @Override
    public Collection<ToolbarComponentDescription> getToolbarComponents() {
        if (validateUserHasButler1Access(getConnectionContext()) || validateUserHasNasAccess(getConnectionContext())) {
            return toolbarComponents;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionCon1text  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasButler1Access(final ConnectionContext connectionCon1text) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "csa://butler1Query",
                            connectionCon1text);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Butler!", ex);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserHasNasAccess(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                            "csa://nasDataQuery",
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.error("Could not validate action tag for Butler!", ex);
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
            return "DigitalDataExportToolbar";
        } else {
            return null;
        }
    }

    @Override
    public Component getComponent() {
        if (validateUserHasButler1Access(getConnectionContext()) || validateUserHasNasAccess(getConnectionContext())) {
            return new DataExportButton();
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
    final class DataExportButton extends JPopupMenuButton {

        //~ Instance fields ----------------------------------------------------

        Icon exportIcon = new javax.swing.ImageIcon(getClass().getResource(
                    "/de/cismet/cids/custom/icons/alkis_export.png")); // NOI18N
        private JPopupMenu popUpMenu = new DataExportPopupMenu();
        private boolean setsPopUpVisible = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DataExportButton object.
         */
        public DataExportButton() {
            super();
            super.setIcon(exportIcon);
            super.setToolTipText(NbBundle.getMessage(
                    DigitalDataExportToolbarComponentProvider.class,
                    "DigitalDataExportToolbarComponentProvider.DataExportButton.tooltip"));
            super.setPopupMenu(popUpMenu);
            setFocusPainted(false);
            setBorderPainted(false);
            addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    if (!popUpMenu.isVisible() && !setsPopUpVisible) {
                                        setsPopUpVisible = true;
                                        popUpMenu.show(DataExportButton.this, 0, DataExportButton.this.getHeight());
                                        popUpMenu.setVisible(true);
                                    } else {
                                        if (!popUpMenu.isVisible()) {
                                            setsPopUpVisible = false;
                                        }
                                    }
                                }
                            });
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class DataExportPopupMenu extends JPopupMenu {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new DataExportPopupMenu object.
         */
        public DataExportPopupMenu() {
            if (validateUserHasNasAccess(getConnectionContext())) {
                this.add(createNASMenuItem());
            }
            if (validateUserHasButler1Access(getConnectionContext())) {
                this.add(createButler2MenuItem());
                this.add(createButler1MenuItem());
            }
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private JMenuItem createNASMenuItem() {
            final String title = org.openide.util.NbBundle.getMessage(NasDialog.class, "NasDialog.title");
            final AbstractAction action = new AbstractAction(title) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        StaticSwingTools.showDialog(
                            new NasDialog(
                                StaticSwingTools.getParentFrame(
                                    CismapBroker.getInstance().getMappingComponent()),
                                true,
                                getConnectionContext()));
                    }
                };
            return new JMenuItem(action);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private JMenuItem createButler1MenuItem() {
            final String title = org.openide.util.NbBundle.getMessage(Butler1Dialog.class, "Butler1Dialog.title_1");
            final AbstractAction action = new AbstractAction(title) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final Butler1Dialog dialog = new Butler1Dialog();
                        dialog.initWithConnectionContext(getConnectionContext());
                        StaticSwingTools.showDialog(dialog);
                    }
                };
            return new JMenuItem(action);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private JMenuItem createButler2MenuItem() {
            final String title = org.openide.util.NbBundle.getMessage(Butler2Dialog.class, "Butler2Dialog.title");
            final AbstractAction action = new AbstractAction(title) {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final Butler2Dialog dialog = new Butler2Dialog();
                        dialog.initWithConnectionContext(getConnectionContext());
                        StaticSwingTools.showDialog(dialog);
                    }
                };
            return new JMenuItem(action);
        }
    }
}
