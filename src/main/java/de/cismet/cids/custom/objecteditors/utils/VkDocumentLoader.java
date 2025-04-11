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
import de.cismet.cids.custom.objecteditors.wunda_blau.VkParentPanel;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.cids.custom.wunda_blau.search.server.VkDocumentLightweightSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class VkDocumentLoader {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(VkDocumentLoader.class);
    private static final String CHILD_TOSTRING_TEMPLATE = "%s";
    private static final String TABLE_VORHABEN = "vk_vorhaben";
    private static final String TABLE_BESCHLUSS = "vk_vorhaben_beschluesse";
    private static final String TABLE_LINKS = "vk_vorhaben_links";
    private static final String[] CHILD_TOSTRING_FIELDS = { "id" };
    private static final String FK_VORHABEN = "fk_vorhaben";

    //~ Instance fields --------------------------------------------------------

    @Getter public Boolean loadingCompletedWithoutError = false;
    @Getter public Map<Integer, List<CidsBean>> mapBeschluesse = new HashMap<>();
    @Getter public Map<Integer, List<CidsBean>> mapLinks = new HashMap<>();
    public Collection<Listener> listeners = new ArrayList<>();
    private final VkDocumentLightweightSearch searchDocument;
    @Getter private final VkParentPanel parentOrganizer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VkDocumentLoader object.
     *
     * @param vkOrganizer
     */
    public VkDocumentLoader(final VkParentPanel vkOrganizer) {
        searchDocument = new VkDocumentLightweightSearch(
                CHILD_TOSTRING_TEMPLATE,
                CHILD_TOSTRING_FIELDS,
                TABLE_VORHABEN,
                FK_VORHABEN);
        this.parentOrganizer = vkOrganizer;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   idVorhaben           DOCUMENT ME!
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public boolean loadChildrenBeschluesse(final Integer idVorhaben, final ConnectionContext connectionContext)
            throws ConnectionException {
        final Collection<MetaObjectNode> mons;
        try {
            searchDocument.setParentId(idVorhaben);
            searchDocument.setFkField(FK_VORHABEN);
            searchDocument.setTable(TABLE_BESCHLUSS);
            searchDocument.setRepresentationFields(CHILD_TOSTRING_FIELDS);
            mons = SessionManager.getProxy().customServerSearch(

                    // SessionManager.getSession().getUser(),
                    searchDocument,
                    connectionContext);
            final List<CidsBean> beansBeschluesse = new ArrayList<>();
            if (!mons.isEmpty()) {
                for (final MetaObjectNode mon : mons) {
                    beansBeschluesse.add(SessionManager.getProxy().getMetaObject(
                            mon.getObjectId(),
                            mon.getClassId(),
                            "WUNDA_BLAU",
                            connectionContext).getBean());
                }
            }

            if (!mapBeschluesse.containsKey(idVorhaben)) {
                mapBeschluesse.put(idVorhaben, beansBeschluesse);
            }
            fireLoadingCompleteBeschluesse();
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
    public List<CidsBean> getMapValueBeschluesse(final Integer key) {
        return mapBeschluesse.get(key);
    }
    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<CidsBean> getMapValueLinks(final Integer key) {
        return mapLinks.get(key);
    }
    

    /**
     * DOCUMENT ME!
     */
    public void clearAllMaps() {
        mapBeschluesse.clear();
        mapLinks.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  status  DOCUMENT ME!
     */
    public void setLoadingCompletedWithoutError(final Boolean status) {
        this.loadingCompletedWithoutError = status;
        if (status) {
            fireLoadingCompleteBeschluesse();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idVorhaben  DOCUMENT ME!
     * @param   beanBeschluesse    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeBeschluesse(final Integer idVorhaben, final CidsBean beanBeschluesse) {
        // return (mapOrt.get(idMeldung)).remove(beanOrt);
        return removeFromMap(idVorhaben, beanBeschluesse, mapBeschluesse);
    }
    
    
    /**
     * DOCUMENT ME!
     *
     * @param   idVorhaben  DOCUMENT ME!
     * @param   beanLinks   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeLinks (final Integer idVorhaben, final CidsBean beanLinks ) {
        return removeFromMap(idVorhaben, beanLinks , mapLinks );
    }


    /**
     * DOCUMENT ME!
     *
     * @param  idVorhaben    DOCUMENT ME!
     * @param  beanBeschluesse  DOCUMENT ME!
     */
    public void addBeschluesse(final Integer idVorhaben, final CidsBean beanBeschluesse) {
        addToMap(idVorhaben, beanBeschluesse, mapBeschluesse);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  idVorhaben    DOCUMENT ME!
     * @param  beanLinks  DOCUMENT ME!
     */
    public void addLinks(final Integer idVorhaben, final CidsBean beanLinks) {
        addToMap(idVorhaben, beanLinks, mapLinks);
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
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteLinks(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingCompleteLinks();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorLinks(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorLinks(primaryKeyValue);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingCompleteBeschluesse() {
        for (final Listener listener : listeners) {
            listener.loadingCompleteBeschluesse();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  primaryKeyValue  DOCUMENT ME!
     */
    private void fireLoadingErrorBeschluesse(final Integer primaryKeyValue) {
        for (final Listener listener : listeners) {
            listener.loadingErrorBeschluesse(primaryKeyValue);
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
         *
         */
        void loadingCompleteBeschluesse();

        /**
         * DOCUMENT ME!
         *
         */
        void loadingCompleteLinks();

        /**
         * DOCUMENT ME!
         *
         * @param  idVorhaben  DOCUMENT ME!
         */
        void loadingErrorBeschluesse(Integer idVorhaben);

        /**
         * DOCUMENT ME!
         *
         * @param  idVorhaben DOCUMENT ME!
         */
        void loadingErrorLinks(Integer idVorhaben);

    }
}
