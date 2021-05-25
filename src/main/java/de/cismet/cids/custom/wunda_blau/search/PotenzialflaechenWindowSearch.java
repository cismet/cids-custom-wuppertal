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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.vividsolutions.jts.geom.Geometry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Arrays;

import javax.swing.JPanel;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.actions.PotenzialflaecheReportServerAction;
import de.cismet.cids.custom.wunda_blau.search.server.PotenzialflaecheSearch;

import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cids.tools.search.clientstuff.CidsWindowSearch;

import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.cismap.navigatorplugin.GeoSearchButton;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsWindowSearch.class)
public class PotenzialflaechenWindowSearch extends AbstractAbfrageWindowSearch<PotenzialflaecheSearch.Configuration>
        implements PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ACTION_TAG = "custom.potenzialflaeche.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private final PotenzialflaechenWindowSearchPanel searchPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PotenzialflaechenWindowSearch object.
     */
    public PotenzialflaechenWindowSearch() {
        this.searchPanel = new PotenzialflaechenWindowSearchPanel();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public MetaObjectNodeServerSearch createServerSearch(final Geometry geometry) {
        return new PotenzialflaecheSearch(searchPanel.createConfiguration(), geometry);
    }

    @Override
    public String getName() {
        return "Potenzialflächen";
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (PotenzialflaechenCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return PotenzialflaecheSearch.OBJECT_MAPPER;
    }

    @Override
    public JPanel getSearchPanel() {
        return searchPanel;
    }

    @Override
    public GeoSearchButton createGeoSearchButton() {
        final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
        final PotenzialflaechenCreateSearchGeometryListener geometryListener =
            new PotenzialflaechenCreateSearchGeometryListener(
                mappingComponent,
                new TreppenSearchTooltip(getIcon()));
        geometryListener.addPropertyChangeListener(this);
        return new GeoSearchButton(
                PotenzialflaechenCreateSearchGeometryListener.POTENZIALFLAECHEN_CREATE_SEARCH_GEOMETRY,
                mappingComponent,
                null,
                "Geo-Suche nach Potenzialflächen");
    }

    @Override
    public String getTableName() {
        return "pf_potenzialflaeche";
    }

    @Override
    public String getArtificialId() {
        return "pf.abfragen";
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        searchPanel.initWithConnectionContext(connectionContext);
        searchPanel.setFilters(Arrays.asList(
                new PotenzialflaecheSearch.FilterInfo(
                    PotenzialflaecheReportServerAction.Property.BEZEICHNUNG,
                    "")));
    }

    @Override
    public PotenzialflaecheSearch.Configuration createConfiguration() {
        return searchPanel.createConfiguration();
    }

    @Override
    public void initFromConfiguration(final PotenzialflaecheSearch.Configuration configuration) {
        searchPanel.initFromConfiguration(configuration);
    }

    @Override
    public void initFromConfiguration(final Object configuration) {
        initFromConfiguration((PotenzialflaecheSearch.Configuration)configuration);
    }

    @Override
    public PotenzialflaecheSearch.Configuration readConfiguration(final String configuration) throws Exception {
        return getObjectMapper().readValue(configuration, PotenzialflaecheSearch.Configuration.class);
    }

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }
}
