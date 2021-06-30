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
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.navigator.connection.SessionManager;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingWorker;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @param    <O>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CidsSearchResultsList<O> extends JList<O> implements ConnectionContextStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CidsSearchResultsList.class);
    private static final ListModel MODEL_LOAD = new DefaultListModel() {

            {
                add(0, "Ergebnisse werden geladen...");
            }
        };

    private static final ListModel MODEL_ERROR = new DefaultListModel() {

            {
                add(0, "[Fehler]");
            }
        };

    private static final ListModel MODEL_EMPTY = new DefaultListModel() {

            {
                add(0, "keine");
            }
        };

    //~ Instance fields --------------------------------------------------------

    @Getter ConnectionContext connectionContext = ConnectionContext.createDummy();

    @Getter @Setter private CidsServerSearch search;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MonSearchResultsList object.
     */
    public CidsSearchResultsList() {
        this(null);
    }

    /**
     * Creates a new MonSearchResultsList object.
     *
     * @param  search  DOCUMENT ME!
     */
    public CidsSearchResultsList(final CidsServerSearch search) {
        this.search = search;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void initWithConnectionContext(final ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (search != null) {
            setModel(MODEL_LOAD);
            new SwingWorker<Collection<O>, Void>() {

                    @Override
                    protected Collection<O> doInBackground() throws Exception {
                        return SessionManager.getProxy().customServerSearch(search, getConnectionContext());
                    }

                    @Override
                    protected void done() {
                        try {
                            final Collection<O> results = get();
                            if (results != null) {
                                if (results.isEmpty()) {
                                    setModel(MODEL_EMPTY);
                                } else {
                                    final DefaultListModel<O> model = new DefaultListModel<>();
                                    for (final O result : results) {
                                        model.addElement(result);
                                    }
                                    setModel(model);
                                }
                            }
                        } catch (final Exception ex) {
                            LOG.error(ex, ex);
                            setModel(MODEL_ERROR);
                        }
                    }
                }.execute();
        }
    }
}
