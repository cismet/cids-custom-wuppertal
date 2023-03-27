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

import com.vividsolutions.jts.geom.Geometry;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;
import de.cismet.cids.custom.wunda_blau.search.abfrage.AbstractAbfrageWindowSearch;
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
@ServiceProvider(service = CidsWindowSearch.class)
public class AlboFlaecheWindowSearch
        extends AbstractAbfrageWindowSearch<AlboFlaecheSearchPanel, AlboFlaecheSearch.Configuration>
        implements PropertyChangeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String ACTION_TAG = "custom.albo.search@WUNDA_BLAU";

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
        super(new AlboFlaecheSearchPanel(editable, true));
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public boolean checkActionTag() {
        return ObjectRendererUtils.checkActionTag(ACTION_TAG, getConnectionContext());
    }

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        super.initWithConnectionContext(connectionContext);
        getSearchPanel().initWithConnectionContext(connectionContext);
        revalidate();
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
        final AlboFlaecheSearch search = new AlboFlaecheSearch(getSearchPanel().createConfiguration());
        search.setGeometry(geometry);
        return search;
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
}
