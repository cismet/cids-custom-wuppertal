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
package de.cismet.cids.custom.objecteditors.utils;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import Sirius.server.middleware.types.MetaObjectNode;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.objecteditors.wunda_blau.BaumParentPanel;
import de.cismet.cids.custom.wunda_blau.search.server.BaumChildLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class BaumChildrenLoader {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumChildrenLoader.class);
    private static final String CHILD_TOSTRING_TEMPLATE = "%s";
    private static final String TABLE_ERSATZ = "baum_ersatz";
    private static final String TABLE_FEST = "baum_festsetzung";
    private static final String TABLE_SCHADEN = "baum_schaden";
    private static final String TABLE_ORT = "baum_ortstermin";
    private static final String TABLE_MELDUNG = "baum_meldung";
    private static final String[] CHILD_TOSTRING_FIELDS = { "id" };
    private static final String FK_SCHADEN = "fk_schaden";
    private static final String FK_MELDUNG = "fk_meldung";
    private static final String FK_GEBIET = "fk_gebiet";

    //~ Instance fields --------------------------------------------------------

    @Getter public Boolean loadingCompletedWithoutError = false;
    @Getter public Map<Integer, List<CidsBean>> mapErsatz = new HashMap<>();
    @Getter public Map<Integer, List<CidsBean>> mapFest = new HashMap<>();
    @Getter public Map<Integer, List<CidsBean>> mapSchaden = new HashMap<>();
    @Getter public Map<Integer, List<CidsBean>> mapOrt = new HashMap<>();
    public Collection<Listener> listeners = new ArrayList<>();
    @Getter public final Map<Integer, List<CidsBean>> mapMeldung = new HashMap<>();
    private final BaumChildLightweightSearch searchChild;
    @Getter private final BaumParentPanel parentOrganizer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaumChildrenLoader object.
     *
     * @param  baumOrganizer  DOCUMENT ME!
     */
    public BaumChildrenLoader(final BaumParentPanel baumOrganizer) {
        searchChild = new BaumChildLightweightSearch(
                CHILD_TOSTRING_TEMPLATE,
                CHILD_TOSTRING_FIELDS,
                TABLE_MELDUNG,
                FK_GEBIET);
        this.parentOrganizer = baumOrganizer;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   gebietId           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenMeldung(final Integer gebietId, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            searchChild.setParentId(gebietId);
            searchChild.setFkField(FK_GEBIET);
            searchChild.setTable(TABLE_MELDUNG);
            searchChild.setRepresentationFields(CHILD_TOSTRING_FIELDS);
            mons = SessionManager.getProxy().customServerSearch(

                    // SessionManager.getSession().getUser(),
                    searchChild,
                    connectionContext);
            final List<CidsBean> beansMeldung = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansMeldung.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
            }

            if (!mapMeldung.containsKey(gebietId)) {
                mapMeldung.put(gebietId, beansMeldung);
            }
            fireLoadingCompleteMeldung();
            for (final CidsBean beanMeldung : beansMeldung) {
                if (loadChildrenSchaden(beanMeldung.getPrimaryKeyValue(), connectionContext)) {
                    // fireLoadingCompleteSchaden(beanMeldung.getPrimaryKeyValue());
                } else {
                    fireLoadingErrorSchaden(beanMeldung.getPrimaryKeyValue());
                    return false;
                }
                if (loadChildrenOrtstermin(beanMeldung.getPrimaryKeyValue(), connectionContext)) {
                    fireLoadingCompleteOrt(beanMeldung.getPrimaryKeyValue());
                } else {
                    fireLoadingErrorOrt(beanMeldung.getPrimaryKeyValue());
                    return false;
                }
            }
        } catch (ConnectionException ex) {
            // Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   id                 DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenForSchaden(final Integer id, final ConnectionContext connectionContext)
            throws ConnectionException {
        if (loadChildrenFest(id, connectionContext)) {
            fireLoadingCompleteFest(id);
        } else {
            fireLoadingErrorFest(id);
            return false;
        }
        if (loadChildrenErsatz(id, connectionContext)) {
            fireLoadingCompleteErsatz(id);
        } else {
            fireLoadingErrorErsatz(id);
            return false;
        }
        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   id                 DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenOrtstermin(final Integer id, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            // Ortstermin hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_ORT);
            searchChild.setFkField(FK_MELDUNG);
            mons = SessionManager.getProxy()
                        .customServerSearch(
                                SessionManager.getSession().getUser(),
                                searchChild,
                                connectionContext);
            final List<CidsBean> beansOrt = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansOrt.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
                if (!mapOrt.containsKey(id)) {
                    mapOrt.put(id, beansOrt);
                }
            }
        } catch (ConnectionException ex) {
            // Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        } finally {
        }

        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   id                 DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenSchaden(final Integer id, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            // Schaden hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_SCHADEN);
            searchChild.setFkField(FK_MELDUNG);
            mons = SessionManager.getProxy()
                        .customServerSearch(
                                SessionManager.getSession().getUser(),
                                searchChild,
                                connectionContext);
            final List<CidsBean> beansSchaden = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansSchaden.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
                if (!mapSchaden.containsKey(id)) {
                    mapSchaden.put(id, beansSchaden);
                }
                // final CidsBean[] beanArraySchaden = beansSchaden.toArray();
                fireLoadingCompleteSchaden(id);
                for (final CidsBean beanSchaden : beansSchaden) {
                    // for(CidsBean beanSchaden:beanArraySchaden){;
                    // return loadChildrenForSchaden(beanSchaden.getPrimaryKeyValue(), connectionContext);
                    if (!(loadChildrenForSchaden(beanSchaden.getPrimaryKeyValue(), connectionContext))) {
                        return false;
                    }
                }
            }
        } catch (ConnectionException ex) {
            // Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   id                 DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenErsatz(final Integer id, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            // Ersatz hinzufuegen
            searchChild.setTable(TABLE_ERSATZ);
            searchChild.setParentId(id);
            searchChild.setFkField(FK_SCHADEN);
            mons = SessionManager.getProxy()
                        .customServerSearch(
                                SessionManager.getSession().getUser(),
                                searchChild,
                                connectionContext);
            final List<CidsBean> beansErsatz = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansErsatz.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
                if (!mapErsatz.containsKey(id)) {
                    mapErsatz.put(id, beansErsatz);
                }
            }
        } catch (ConnectionException ex) {
            // Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   id                 DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenFest(final Integer id, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            // Fest hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_FEST);
            searchChild.setFkField(FK_SCHADEN);
            mons = SessionManager.getProxy()
                        .customServerSearch(
                                SessionManager.getSession().getUser(),
                                searchChild,
                                connectionContext);
            final List<CidsBean> beansFest = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansFest.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
                if (!mapFest.containsKey(id)) {
                    mapFest.put(id, beansFest);
                }
            }
        } catch (ConnectionException ex) {
            // Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueMeldung(final Integer key) {
        return mapMeldung.get(key);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueSchaden(final Integer key) {
        return mapSchaden.get(key);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueOrt(final Integer key) {
        return mapOrt.get(key);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueFest(final Integer key) {
        return mapFest.get(key);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueErsatz(final Integer key) {
        return mapErsatz.get(key);
    }

    /**
     * DOCUMENT ME!
     */
    public void clearAllMaps() {
        mapOrt.clear();
        mapSchaden.clear();
        mapErsatz.clear();
        mapFest.clear();
        mapMeldung.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    public void setLoadingCompletedWithoutError(final Boolean status) {
        this.loadingCompletedWithoutError = status;
        if (status) {
            fireLoadingComplete();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idMeldung  DOCUMENT ME!
     * @param   beanOrt    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeOrt(final Integer idMeldung, final CidsBean beanOrt) {
        // return (mapOrt.get(idMeldung)).remove(beanOrt);
        return removeFromMap(idMeldung, beanOrt, mapOrt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idMeldung    DOCUMENT ME!
     * @param   beanSchaden  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeSchaden(final Integer idMeldung, final CidsBean beanSchaden) {
        return removeFromMap(idMeldung, beanSchaden, mapSchaden);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idSchaden  DOCUMENT ME!
     * @param   beanFest   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeFest(final Integer idSchaden, final CidsBean beanFest) {
        return removeFromMap(idSchaden, beanFest, mapFest);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idSchaden   DOCUMENT ME!
     * @param   beanErsatz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeErsatz(final Integer idSchaden, final CidsBean beanErsatz) {
        return removeFromMap(idSchaden, beanErsatz, mapErsatz);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idGebiet     DOCUMENT ME!
     * @param   beanMeldung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeMeldung(final Integer idGebiet, final CidsBean beanMeldung) {
        return removeFromMap(idGebiet, beanMeldung, mapMeldung);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idMeldung  DOCUMENT ME!
     * @param  beanOrt    DOCUMENT ME!
     */
    public void addOrt(final Integer idMeldung, final CidsBean beanOrt) {
        /* List<CidsBean> tempList = new ArrayList<>();
         * if (mapOrt.get(idMeldung) != null){  tempList = mapOrt.get(idMeldung);   tempList.add(beanOrt);
         * mapOrt.replace(idMeldung, tempList); } else{  tempList.add(beanOrt);  mapOrt.put(idMeldung, tempList); }*/
        addToMap(idMeldung, beanOrt, mapOrt);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idMeldung    DOCUMENT ME!
     * @param  beanSchaden  DOCUMENT ME!
     */
    public void addSchaden(final Integer idMeldung, final CidsBean beanSchaden) {
        addToMap(idMeldung, beanSchaden, mapSchaden);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idMeldung    DOCUMENT ME!
     * @param  beanMeldung  DOCUMENT ME!
     */
    public void addMeldung(final Integer idMeldung, final CidsBean beanMeldung) {
        addToMap(idMeldung, beanMeldung, mapMeldung);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idSchaden   DOCUMENT ME!
     * @param  beanErsatz  DOCUMENT ME!
     */
    public void addErsatz(final Integer idSchaden, final CidsBean beanErsatz) {
        addToMap(idSchaden, beanErsatz, mapErsatz);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idSchaden  DOCUMENT ME!
     * @param  beanFest   DOCUMENT ME!
     */
    public void addFest(final Integer idSchaden, final CidsBean beanFest) {
        addToMap(idSchaden, beanFest, mapFest);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id    DOCUMENT ME!
     * @param   bean  DOCUMENT ME!
     * @param   map   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeFromMap(final Integer id, final CidsBean bean, final Map<Integer, List<CidsBean>> map) {
        return (map.get(id)).remove(bean);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id    DOCUMENT ME!
     * @param  bean  DOCUMENT ME!
     * @param  map   DOCUMENT ME!
     */
    public void addToMap(final Integer id, final CidsBean bean, final Map<Integer, List<CidsBean>> map) {
        List<CidsBean> tempList = new ArrayList<>();
        if (map.get(id) != null) {
            tempList = map.get(id);
            tempList.add(bean);
            map.replace(id, tempList);
        } else {
            tempList.add(bean);
            map.put(id, tempList);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean addListener(final Listener listener) {
        return listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeListener(final Listener listener) {
        return listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireLoadingComplete() {
        for (final Listener listener : listeners) {
            listener.loadingComplete();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void fireLoadingCompleteMeldung() {
        for (final Listener listener : listeners) {
            listener.loadingCompleteMeldung();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteOrt(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingCompleteOrt(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorOrt(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorOrt(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteSchaden(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingCompleteSchaden(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorSchaden(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorSchaden(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteErsatz(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingCompleteErsatz(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorErsatz(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorErsatz(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteFest(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingCompleteFest(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorFest(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorFest(primaryKeyValue);
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface Listener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         */
        void loadingComplete();

        /**
         * DOCUMENT ME!
         */
        void loadingCompleteMeldung();

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingCompleteSchaden(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingCompleteOrt(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingCompleteFest(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingCompleteErsatz(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingErrorSchaden(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingErrorFest(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingErrorErsatz(Integer idMeldung);

        /**
         * DOCUMENT ME!
         *
         * @param  idMeldung  DOCUMENT ME!
         */
        void loadingErrorOrt(Integer idMeldung);
    }
}
