/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.commons.searchgeometrylistener;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.ui.ComponentRegistry;

import Sirius.server.middleware.types.Node;

import com.vividsolutions.jts.geom.Geometry;

import edu.umd.cs.piccolo.PNode;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;
import de.cismet.cismap.commons.features.SearchFeature;
import de.cismet.cismap.commons.gui.MappingComponent;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.AbstractCreateSearchGeometryListener;
import de.cismet.cismap.commons.gui.piccolo.eventlistener.CreateGeometryListenerInterface;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class NodesSearchCreateSearchGeometryListener extends AbstractCreateSearchGeometryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(NodesSearchCreateSearchGeometryListener.class);

    public static final String INPUT_LISTENER_NAME = "CREATE_CUSTOMSEARCH_GEOMETRY";

    public static final String ACTION_SEARCH_STARTED = "ACTION_SEARCH_STARTED";
    public static final String ACTION_SEARCH_DONE = "ACTION_SEARCH_DONE";
    public static final String ACTION_SEARCH_FAILED = "ACTION_SEARCH_FAILED";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreateSearchGeometryListener object.
     *
     * @param  mc                  DOCUMENT ME!
     * @param  propChangeListener  DOCUMENT ME!
     */
    public NodesSearchCreateSearchGeometryListener(final MappingComponent mc,
            final PropertyChangeListener propChangeListener) {
        super(mc, INPUT_LISTENER_NAME);

        setMode(CreateGeometryListenerInterface.POLYGON);

        addPropertyChangeListener(propChangeListener);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   geometry  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract CidsServerSearch getCidsServerSearch(final Geometry geometry);
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public abstract String getCrs();

    @Override
    protected boolean performSearch(final SearchFeature searchFeature) {
        final String crs = getCrs();
        final Geometry geometry;
        if (crs != null) {
            final int srid = CrsTransformer.extractSridFromCrs(crs);
            geometry = CrsTransformer.transformToGivenCrs(searchFeature.getGeometry(), crs);
            geometry.setSRID(srid);
        } else {
            geometry = searchFeature.getGeometry();
        }
        final CidsServerSearch search = getCidsServerSearch(geometry);
        final PropertyChangeSupport propChangeSupport = getPropertyChangeSupport();
        propChangeSupport.firePropertyChange(ACTION_SEARCH_STARTED, null, geometry);
        new SwingWorker<Node[], Void>() {

                @Override
                protected Node[] doInBackground() throws Exception {
                    Node[] result = null;
                    final Collection searchResult = SessionManager.getProxy()
                                .customServerSearch(SessionManager.getSession().getUser(), search);

                    if (isCancelled()) {
                        return result;
                    }

                    final ArrayList<Node> nodes = new ArrayList<Node>(searchResult.size());

                    for (final Object singleSearchResult : searchResult) {
                        nodes.add((Node)singleSearchResult);

                        if (isCancelled()) {
                            return result;
                        }
                    }

                    result = nodes.toArray(new Node[0]);
                    return result;
                }

                @Override
                protected void done() {
                    try {
                        final Node[] nodes = get();
                        if (nodes == null) {
                            propChangeSupport.firePropertyChange(
                                ACTION_SEARCH_FAILED,
                                null,
                                new Exception("Fehler während der Suche."));
                        } else {
                            propChangeSupport.firePropertyChange(ACTION_SEARCH_DONE, null, nodes);
                        }
                    } catch (final Exception ex) {
                        propChangeSupport.firePropertyChange(ACTION_SEARCH_FAILED, null, ex);
                    }
                }
            }.execute();

        return true;
    }

    @Override
    protected PNode getPointerAnnotation() {
        return null;
    }
}
