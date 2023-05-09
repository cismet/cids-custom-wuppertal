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
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.navigator.search.CidsSearchExecutor;
import Sirius.navigator.search.dynamic.SearchControlListener;

import Sirius.server.middleware.types.MetaClass;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import org.openide.util.lookup.ServiceProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.net.URL;

import javax.swing.ImageIcon;

import de.cismet.cids.custom.objectrenderer.utils.CidsBeanSupport;
import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.abfrage.AbstractAbfrageWindowSearch;
import de.cismet.cids.custom.wunda_blau.search.server.CidsMauernSearchStatement;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@ServiceProvider(service = CidsWindowSearch.class)
public class MauernWindowSearch
        extends AbstractAbfrageWindowSearch<MauernWindowSearchPanel, CidsMauernSearchStatement.Configuration>
        implements PropertyChangeListener,
            SearchControlListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ACTION_TAG = "custom.mauern.search@WUNDA_BLAU";

    private static final Logger LOG = Logger.getLogger(MauernWindowSearch.class);

    //~ Instance fields --------------------------------------------------------

    private ImageIcon icon;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernWindowSearch object.
     */
    public MauernWindowSearch() {
        super(new MauernWindowSearchPanel());
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }

    @Override
    public MetaObjectNodeServerSearch assembleSearch() {
        return getServerSearch();
    }

    @Override
    public void searchStarted() {
    }

    @Override
    public void searchDone(final int numberOfResults) {
    }

    @Override
    public void searchCanceled() {
    }

    @Override
    public boolean suppressEmptyResultMessage() {
        return false;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (MauernCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }

    @Override
    public GeoSearchButton createGeoSearchButton() {
        final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
        final MauernCreateSearchGeometryListener geometryListener = new MauernCreateSearchGeometryListener(
                mappingComponent,
                new MauernSearchTooltip(getIcon()));
        geometryListener.addPropertyChangeListener(this);
        return new GeoSearchButton(
                MauernCreateSearchGeometryListener.MAUERN_CREATE_SEARCH_GEOMETRY,
                mappingComponent,
                null,
                "Geo-Suche nach Stützmauern");
    }

    @Override
    public String getArtificialId() {
        return "mauer.abfragen";
    }

    @Override
    public ImageIcon getIcon() {
        return icon;
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);

        ((MauernWindowSearchPanel)getSearchPanel()).initWithConnectionContext(connectionContext);

        final MetaClass metaClass = ClassCacheMultiple.getMetaClass(
                CidsBeanSupport.DOMAIN_NAME,
                "mauer",
                getConnectionContext());

        byte[] iconDataFromMetaclass = new byte[] {};

        if (metaClass != null) {
            iconDataFromMetaclass = metaClass.getIconData();
        }

        if (iconDataFromMetaclass.length > 0) {
            LOG.info("Using icon from metaclass.");
            icon = new ImageIcon(metaClass.getIconData());
        } else {
            LOG.warn("Metaclass icon is not set. Trying to load default icon.");
            final URL urlToIcon = getClass().getResource("/de/cismet/cids/custom/wunda_blau/search/search.png");

            if (urlToIcon != null) {
                icon = new ImageIcon(urlToIcon);
            } else {
                icon = new ImageIcon(new byte[] {});
            }
        }
    }

    @Override
    public MetaObjectNodeServerSearch createServerSearch(final Geometry geometry) {
        return new CidsMauernSearchStatement(getSearchPanel().createConfiguration(), geometry);
    }

    @Override
    public String getName() {
        return "Mauern-Suche";
    }
}
