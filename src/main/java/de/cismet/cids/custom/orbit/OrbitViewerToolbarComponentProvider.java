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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.socket.client.IO;
import io.socket.client.Socket;

import org.openide.util.lookup.ServiceProvider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.cismet.cids.custom.wunda_blau.search.actions.GetOrbitStacAction;

import de.cismet.cids.server.actions.ServerActionParameter;

import de.cismet.cismap.commons.gui.ToolbarComponentDescription;
import de.cismet.cismap.commons.gui.ToolbarComponentsProvider;
import de.cismet.cismap.commons.interaction.CismapBroker;

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

    private static OrbitControlFeature currentOrbitControlFeature = null;

    //~ Instance fields --------------------------------------------------------

    private final List<ToolbarComponentDescription> toolbarComponents;
    private final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlkisToobarPluginComponentProvider object.
     */
    public OrbitViewerToolbarComponentProvider() {
        final List<ToolbarComponentDescription> preparationList = TypeSafeCollections.newArrayList();
        final ToolbarComponentDescription description = new ToolbarComponentDescription(
                "tlbMain",
                new OrbitViewerControlJButton(connectionContext),
                ToolbarPositionHint.AFTER,
                "cmdPan");
        preparationList.add(description);
        this.toolbarComponents = Collections.unmodifiableList(preparationList);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    @Override
    public List<ToolbarComponentDescription> getToolbarComponents() {
//        if (AlkisUtils.validateUserHasAlkisPrintAccess(getConnectionContext())) {
        return toolbarComponents;
//        } else {
//            return Collections.emptyList();
//        }
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

        //~ Instance fields ----------------------------------------------------

        private Socket socket;
        private final ObjectMapper mapper = new ObjectMapper();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new AlkisPrintJButton object.
         *
         * @param   connectionContext  DOCUMENT ME!
         *
         * @throws  RuntimeException  DOCUMENT ME!
         */
        public OrbitViewerControlJButton(final ConnectionContext connectionContext) {
            try {
                socket = IO.socket("http://localhost:3001");
                socket.connect();
            } catch (Exception ex) {
                log.fatal(ex, ex);
                throw new RuntimeException(ex);
            }

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
                        SwingUtilities.invokeLater(new Runnable() {

                                @Override
                                public void run() {
                                    StacResult stacResult = null;
                                    try {
                                        final String ip = "notYet";
                                        final Object ret = SessionManager.getProxy()
                                                    .executeTask(
                                                        GetOrbitStacAction.TASK_NAME,
                                                        "WUNDA_BLAU",
                                                        (Object)null,
                                                        connectionContext,
                                                        new ServerActionParameter<String>(
                                                            GetOrbitStacAction.PARAMETER_TYPE.IP.toString(),
                                                            ip));

                                        final String s = ret.toString();
                                        System.out.println(s);

                                        stacResult = mapper.readValue(s, StacResult.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("result:" + stacResult);

                                    final OrbitControlFeature vcmf = new OrbitControlFeature(
                                            connectionContext,
                                            stacResult,
                                            socket);

                                    if (currentOrbitControlFeature != null) {
                                        CismapBroker.getInstance()
                                                .getMappingComponent()
                                                .getFeatureCollection()
                                                .removeFeature(currentOrbitControlFeature);
                                        currentOrbitControlFeature = null;
                                    }
                                    CismapBroker.getInstance()
                                            .getMappingComponent()
                                            .getFeatureCollection()
                                            .addFeature(vcmf);
                                    currentOrbitControlFeature = vcmf;
                                }
                            });
                    }
                });
        }
    }
}