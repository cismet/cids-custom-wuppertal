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
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.error.ErrorInfo;

import java.awt.Dimension;
import java.awt.Frame;

import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import de.cismet.cids.client.tools.CidsAuthentification;
import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.layerwidget.ActiveLayerModel;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.log4jquickconfig.Log4JQuickConfig;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TreppenReportTester implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            TreppenReportTester.class);
    private static final String SRS = "EPSG:25832";

    //~ Instance fields --------------------------------------------------------

    private final int id;
    private final ConnectionContext connectionContext;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TreppenReportTester object.
     *
     * @param  id                 DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppenReportTester(final int id, final ConnectionContext connectionContext) {
        this.id = id;
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void initMap() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("initMap");
        }
        final MappingComponent mappingComponent = new MappingComponent();
        final Dimension d = new Dimension(300, 300);
        mappingComponent.setPreferredSize(d);
        mappingComponent.setSize(d);

        final ActiveLayerModel mappingModel = new ActiveLayerModel();
        mappingModel.addHome(new XBoundingBox(
                2583621.251964098d,
                5682507.032498134d,
                2584022.9413952776d,
                5682742.852810634d,
                SRS,
                false));
        mappingModel.setSrs(SRS);

        mappingComponent.setInteractionMode(MappingComponent.SELECT);
        mappingComponent.setMappingModel(mappingModel);
        mappingComponent.gotoInitialBoundingBox();
        mappingComponent.unlock();

        CismapBroker.getInstance().setMappingComponent(mappingComponent);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void go() throws Exception {
        final MetaClass metaClass = CidsBean.getMetaClassFromTableName("WUNDA_BLAU", "treppe", getConnectionContext());
        LOG.fatal("load metaobject");
        final MetaObject metaObject = SessionManager.getProxy()
                    .getMetaObject(SessionManager.getSession().getUser(),
                        id,
                        metaClass.getID(),
                        "WUNDA_BLAU",
                        getConnectionContext());
        final CidsBean cidsBean = metaObject.getBean();
        LOG.fatal("go test with bean");
        go(cidsBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bean  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void go(final CidsBean bean) throws Exception {
        final TreppenReportBean reportBean = new TreppenReportBean(
                bean,
                null,
                getConnectionContext());

        LOG.fatal("report bean created");

        boolean ready = false;
        do {
            ready = true;
            if (!reportBean.isReadyToProceed()) {
                ready = false;
            }
        } while (!ready);

        LOG.fatal("report bean ready, show Report");

        DevelopmentTools.showReportForBeans(
            "/de/cismet/cids/custom/reports/wunda_blau/treppe-katasterblatt.jasper",
            Arrays.asList(new TreppenReportBean[] { reportBean }),
            new HashMap<>());
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
