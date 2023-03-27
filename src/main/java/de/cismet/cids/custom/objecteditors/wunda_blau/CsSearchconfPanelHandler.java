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
package de.cismet.cids.custom.objecteditors.wunda_blau;

import org.apache.log4j.Logger;

import org.openide.util.Lookup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.custom.wunda_blau.search.abfrage.AbstractAbfragePanel;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class CsSearchconfPanelHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(CsSearchconfPanelHandler.class);

    //~ Instance fields --------------------------------------------------------

    private final Map<String, AbstractAbfragePanel> storableSearchPanels = new HashMap<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CsSearchconfHandler object.
     */
    private CsSearchconfPanelHandler() {
        final Collection<? extends AbstractAbfragePanel> lookupStorableSearchPanels = Lookup.getDefault()
                    .lookupAll(AbstractAbfragePanel.class);
        if (lookupStorableSearchPanels != null) {
            for (final AbstractAbfragePanel storableSearchPanel : lookupStorableSearchPanels) {
                if (storableSearchPanel != null) {
                    final String tableName = storableSearchPanel.getTableName();
                    if (tableName != null) {
                        storableSearchPanels.put(tableName, storableSearchPanel);
                    }
                }
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   name  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AbstractAbfragePanel getStorableSearchPanel(final String name) {
        final AbstractAbfragePanel wrapper = storableSearchPanels.get(name);
        if (wrapper != null) {
            try {
                return wrapper.getClass().getConstructor(boolean.class).newInstance(false);
            } catch (final Exception ex) {
                LOG.error(ex, ex);
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static CsSearchconfPanelHandler getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final CsSearchconfPanelHandler INSTANCE = new CsSearchconfPanelHandler();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
