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
package de.cismet.cids.custom.orbit;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.awt.Component;
import java.awt.event.ActionEvent;

import java.util.Collections;
import java.util.List;

import javax.swing.JButton;

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.collections.TypeSafeCollections;

import de.cismet.tools.gui.menu.CidsUiComponent;

/**
 * DOCUMENT ME!
 *
 * @author   helllth
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = ToolbarComponentsProvider.class)
public class OrbitViewerToolbarComponentProvider implements ToolbarComponentsProvider,
    ConnectionContextStore,
    CidsUiComponent {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(OrbitViewerToolbarComponentProvider.class);
    private static final String CONFIG_ATTR = "custom.orbit.toolbarButton@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private List<ToolbarComponentDescription> toolbarComponents;
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisToobarPluginComponentProvider object.
     */
    public OrbitViewerToolbarComponentProvider() {
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;

        if (validateUserConfigAttr(connectionContext)) {
            try {
                final List<ToolbarComponentDescription> preparationList = TypeSafeCollections.newArrayList();
                final ToolbarComponentDescription description = new ToolbarComponentDescription(
                        "tlbMain",
                        new OrbitViewerControlJButton(connectionContext),
                        ToolbarPositionHint.AFTER,
                        "cmdPan");
                preparationList.add(description);
                toolbarComponents = Collections.unmodifiableList(preparationList);
            } catch (final Exception ex) {
                LOG.error("Error during loading of the Orbit ServerRessources", ex);

                toolbarComponents = Collections.emptyList();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean validateUserConfigAttr(final ConnectionContext connectionContext) {
        try {
            return SessionManager.getConnection()
                        .hasConfigAttr(
                            SessionManager.getSession().getUser(),
                            CONFIG_ATTR,
                            connectionContext);
        } catch (ConnectionException ex) {
            LOG.info("Could not validate action tag:" + CONFIG_ATTR, ex);
        }
        return false;
    }

    @Override
    public List<ToolbarComponentDescription> getToolbarComponents() {
        return toolbarComponents;
    }

    @Override
    public String getPluginName() {
        return "OrbitViewerControl";
    }

    @Override
    public final ConnectionContext getConnectionContext() {
        return connectionContext;
    }

    @Override
    public String getValue(final String key) {
        if (key.equals(CidsUiComponent.CIDS_ACTION_KEY)) {
            return "OrbitViewerControlPlugin";
        } else {
            return null;
        }
    }

    @Override
    public Component getComponent() {
        return new OrbitViewerControlJButton(connectionContext);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    final class OrbitViewerControlJButton extends JButton {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AlkisPrintJButton object.
         *
         * @param  connectionContext  DOCUMENT ME!
         */
        public OrbitViewerControlJButton(final ConnectionContext connectionContext) {
            setText(null);
            setToolTipText("OrbitViewer");
            setName("OrbitViewerControl");
            setBorderPainted(false);
            setFocusable(false);
            setIcon(new javax.swing.ImageIcon(
                    getClass().getResource("/de/cismet/cids/custom/orbitviewer/orbit22.png")));
            setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

            addActionListener(new java.awt.event.ActionListener() {

                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        OrbitControlFeature.controlOrAddOnMap(getConnectionContext());
                    }
                });
        }
    }
}
