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
import Sirius.navigator.exception.ConnectionException;

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.nas.NasDialog;
import de.cismet.cids.custom.wunda_blau.search.actions.NasZaehlObjekteSearch;

import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.StaticSwingTools;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CommonFeatureAction.class)
public class NASDataRetrievalAction extends AbstractAction implements CommonFeatureAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DEFAULT_CRS = "EPSG:25832";

    //~ Instance fields --------------------------------------------------------

    Feature f = null;
    private final transient org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NASDataRetrievalAction object.
     */
    public NASDataRetrievalAction() {
        super("NAS Daten abfragen");
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public int getSorter() {
        return 10;
    }

    @Override
    public Feature getSourceFeature() {
        return f;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void setSourceFeature(final Feature source) {
        f = source;
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    final NasDialog dialog = new NasDialog(
                            StaticSwingTools.getParentFrame(
                                CismapBroker.getInstance().getMappingComponent()),
                            false);
                    StaticSwingTools.showDialog(dialog);
                }
            });
    }
}
