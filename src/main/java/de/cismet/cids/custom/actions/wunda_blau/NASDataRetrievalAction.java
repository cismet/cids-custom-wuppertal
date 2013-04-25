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

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.lookup.ServiceProvider;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import de.cismet.cids.custom.nas.NASDownload;
import de.cismet.cids.custom.wunda_blau.search.actions.NasDataQueryAction;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.CommonFeatureAction;
import de.cismet.cismap.commons.features.Feature;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

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
//        super.putValue(
//            Action.SMALL_ICON,
//            new javax.swing.ImageIcon(getClass().getResource("/de/cismet/cids/custom/icons/wrrl-db-mv/raisePoly.png")));
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
        // show mask to ask for the Product Template
        final Object[] possibilities = { "Komplett", "Ohne Eigentuemer", "Nur Punkte" };
        final String s = (String)JOptionPane.showInputDialog(
                StaticSwingTools.getParentFrame(CismapBroker.getInstance().getMappingComponent()),
                "Please choose a template",
                "Product Template",
                JOptionPane.PLAIN_MESSAGE,
                null,
                possibilities,
                "Komplett");
        final Geometry g = f.getGeometry();
        CrsTransformer.transformToGivenCrs(g, DEFAULT_CRS);
        NasDataQueryAction.PRODUCT_TEMPLATE template;
        if (s.equals("Komplett")) {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.KOMPLETT;
        } else if (s.equals("Ohne Eigentuemer")) {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.OHNE_EIGENTUEMER;
        } else {
            template = NasDataQueryAction.PRODUCT_TEMPLATE.POINTS;
        }
        if (DownloadManagerDialog.showAskingForUserTitle(
                        CismapBroker.getInstance().getMappingComponent())) {
            final String jobname = (!DownloadManagerDialog.getJobname().equals("")) ? DownloadManagerDialog
                            .getJobname() : null;
            DownloadManager.instance().add(
                new NASDownload("NAS-Download", jobname, "", template, f.getGeometry()));
        } else {
            DownloadManager.instance().add(
                new NASDownload("NAS-Download", "", template, f.getGeometry()));
        }
    }
}
