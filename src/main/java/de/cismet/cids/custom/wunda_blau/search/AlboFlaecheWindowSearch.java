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

import org.openide.util.NbBundle;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.AlboFlaecheSearchPanel;
import de.cismet.cids.custom.wunda_blau.search.server.AlboFlaecheSearch;

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
public class AlboFlaecheWindowSearch extends AbstractAbfrageWindowSearch<AlboFlaecheSearch.Configuration>
        implements PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ACTION_TAG = "custom.albo.search@WUNDA_BLAU";

    //~ Instance fields --------------------------------------------------------

    private final AlboFlaecheSearchPanel searchPanel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AlboFlaecheWindowSearch object.
     */
    public AlboFlaecheWindowSearch() {
        this(true);
    }

    /**
     * Creates a new AlboFlaecheWindowSearch object.
     *
     * @param  editable  DOCUMENT ME!
     */
    public AlboFlaecheWindowSearch(final boolean editable) {
        this.searchPanel = new AlboFlaecheSearchPanel(editable, true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public ObjectMapper getObjectMapper() {
        return AlboFlaecheSearch.OBJECT_MAPPER;
    }

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }

    @Override
    public String getTableName() {
        return "albo_flaeche";
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        searchPanel.initWithConnectionContext(connectionContext);
        revalidate();
    }

    @Override
    public AlboFlaecheSearchPanel getSearchPanel() {
        return searchPanel;
    }

    @Override
    public GeoSearchButton createGeoSearchButton() {
        final MappingComponent mappingComponent = CismapBroker.getInstance().getMappingComponent();
        final AlboFlaecheCreateSearchGeometryListener geometryListener = new AlboFlaecheCreateSearchGeometryListener(
                mappingComponent,
                new AlboFlaecheSearchTooltip(getIcon()));
        geometryListener.addPropertyChangeListener(this);
        return new GeoSearchButton(
                AlboFlaecheCreateSearchGeometryListener.CREATE_SEARCH_GEOMETRY,
                mappingComponent,
                null,
                "Geo-Suche nach Altlasten");
    }

    @Override
    public String getArtificialId() {
        return "albo.abfragen";
    }

    @Override
    public MetaObjectNodeServerSearch createServerSearch(final Geometry geometry) {
        final AlboFlaecheSearch search = new AlboFlaecheSearch(createConfiguration());
        search.setGeometry(geometry);
        return search;
    }

    @Override
    public AlboFlaecheSearch.Configuration createConfiguration() {
        return searchPanel.createConfiguration();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  configuration  DOCUMENT ME!
     */
    @Override
    public void initFromConfiguration(final AlboFlaecheSearch.Configuration configuration) {
        searchPanel.initFromConfiguration(configuration);
    }

    @Override
    public void initFromConfiguration(final Object configuration) {
        initFromConfiguration((AlboFlaecheSearch.Configuration)configuration);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        if (AlboFlaecheCreateSearchGeometryListener.ACTION_SEARCH_STARTED.equals(evt.getPropertyName())) {
            if ((evt.getNewValue() != null) && (evt.getNewValue() instanceof Geometry)) {
                final MetaObjectNodeServerSearch search = getServerSearch((Geometry)evt.getNewValue());
                CidsSearchExecutor.searchAndDisplayResultsWithDialog(search, getConnectionContext());
            }
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AlboFlaecheWindowSearch.class, "AlboFlaecheWindowSearch.name");
    }

    @Override
    public AlboFlaecheSearch.Configuration readConfiguration(final String conf_json) throws Exception {
        return getObjectMapper().readValue(
                conf_json,
                AlboFlaecheSearch.Configuration.class);
    }
}
