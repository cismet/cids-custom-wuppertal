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
package de.cismet.cids.custom.actions.wunda_blau;

import Sirius.navigator.connection.SessionManager;

import org.openide.util.lookup.ServiceProvider;

import java.awt.event.ActionEvent;

import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.nas.NasDialog;

import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class NASDataRetrievalAction extends AbstractAction implements CommonFeatureAction, ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DEFAULT_CRS = "EPSG:25832";

    //~ Instance fields --------------------------------------------------------

    Feature f = null;
    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private boolean hasNasAccess = false;

    private ConnectionContext connectionContext = ConnectionContext.createDummy();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDataRetrievalAction object.
     */
    public NASDataRetrievalAction() {
        super("NAS Daten abfragen");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
        try {
            hasNasAccess = SessionManager.getConnection()
                        .hasConfigAttr(SessionManager.getSession().getUser(),
                                "csa://nasDataQuery",
                                getConnectionContext());
        } catch (Exception ex) {
            log.error("Could not validate nas action tag (csa://nasDataQuery)!", ex);
            hasNasAccess = false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int getSorter() {
        return 10;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Feature getSourceFeature() {
        return f;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public boolean isActive() {
        return hasNasAccess;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  source  DOCUMENT ME!
     */
    @Override
    public void setSourceFeature(final Feature source) {
        f = source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final LinkedList<Feature> featureToSelect = new LinkedList<Feature>();
                    featureToSelect.add(f);
                    final NasDialog dialog = new NasDialog(
                            StaticSwingTools.getParentFrame(
                                CismapBroker.getInstance().getMappingComponent()),
                            false,
                            featureToSelect,
                            getConnectionContext());
                    StaticSwingTools.showDialog(dialog);
                }
            });
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
    }
}
