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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.wunda_blau.search.server.BaumChildLightweightSearch;
import de.cismet.cids.dynamics.CidsBean;
import de.cismet.connectioncontext.ConnectionContext;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import de.cismet.cids.custom.objecteditors.wunda_blau.BaumParentPanel;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra Simmert
 * @version  $Revision$, $Date$
 */
public class BaumChildrenLoader {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BaumChildrenLoader.class);
    @Getter public Boolean loadingCompletedWithoutError = false;
    public static final String CHILD_TOSTRING_TEMPLATE = "%s";
    public static final String TABLE_ERSATZ = "baum_ersatz";
    public static final String TABLE_FEST = "baum_festsetzung";
    public static final String TABLE_SCHADEN = "baum_schaden";
    public static final String TABLE_ORT = "baum_ortstermin";
    public static final String TABLE_MELDUNG = "baum_meldung";
    public static final String[] CHILD_TOSTRING_FIELDS = {"id"};
    public static final String FK_SCHADEN = "fk_schaden";
    public static final String FK_MELDUNG = "fk_meldung";
    public static final String FK_GEBIET = "fk_gebiet";
    private final BaumChildLightweightSearch searchChild;
    @Getter private final BaumParentPanel parentOrganizer;
    @Getter public Map <Integer, List<CidsBean>> mapErsatz = new HashMap <>();
    @Getter public Map <Integer, List<CidsBean>> mapFest = new HashMap <>();
    @Getter public Map <Integer, List<CidsBean>> mapSchaden = new HashMap <>();
    @Getter public Map <Integer, List<CidsBean>> mapOrt = new HashMap <>();
    public Collection<Listener> listeners =  new ArrayList<>();
    @Getter public final Map <Integer, List<CidsBean>> mapMeldung = new HashMap <>();
    //~ Methods ----------------------------------------------------------------

    public boolean loadChildrenMeldung(Integer gebietId, ConnectionContext connectionContext) throws ConnectionException{
        Collection<MetaObjectNode> mons;
        try{
            searchChild.setParentId(gebietId);
            searchChild.setFkField(FK_GEBIET);
            searchChild.setTable(TABLE_MELDUNG);
            searchChild.setRepresentationFields(CHILD_TOSTRING_FIELDS);
            mons = SessionManager.getProxy().customServerSearch(
                    //SessionManager.getSession().getUser(),
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
            
            if(!mapMeldung.containsKey(gebietId)){
                mapMeldung.put(gebietId, beansMeldung);
            }
            fireLoadingCompleteMeldung();
            for(CidsBean beanMeldung:beansMeldung){
                if(loadChildrenSchaden(beanMeldung.getPrimaryKeyValue(), connectionContext)){
                    //fireLoadingCompleteSchaden(beanMeldung.getPrimaryKeyValue());
                } else {
                    fireLoadingErrorSchaden(beanMeldung.getPrimaryKeyValue());
                    return false;
                }
                if (loadChildrenOrtstermin(beanMeldung.getPrimaryKeyValue(), connectionContext)){
                    fireLoadingCompleteOrt(beanMeldung.getPrimaryKeyValue());
                } else {
                    fireLoadingErrorOrt(beanMeldung.getPrimaryKeyValue());
                    return false;
                }
                
            }
        }catch (ConnectionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    public boolean loadChildrenForSchaden(Integer id, ConnectionContext connectionContext) throws ConnectionException{
        if(loadChildrenFest(id, connectionContext)){
            fireLoadingCompleteFest(id);
        } else {
            fireLoadingErrorFest(id);
            return false;
        }
        if (loadChildrenErsatz(id, connectionContext)){
            fireLoadingCompleteErsatz(id);
        } else {
            fireLoadingErrorErsatz(id);
            return false;
        }
        return true;
    }   
    public boolean loadChildrenOrtstermin(final Integer id, final ConnectionContext connectionContext) throws ConnectionException{
        Collection<MetaObjectNode> mons;
        try{
            //Ortstermin hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_ORT);
            searchChild.setFkField(FK_MELDUNG);
            mons = SessionManager.getProxy().customServerSearch(
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
                if(!mapOrt.containsKey(id)){
                    mapOrt.put(id, beansOrt);
                }
            }
        }catch (ConnectionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        } finally{
            
        }
        
        return true;
    }
    public boolean loadChildrenSchaden(final Integer id, final ConnectionContext connectionContext) throws ConnectionException{
        Collection<MetaObjectNode> mons;
        try{
            //Schaden hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_SCHADEN);
            searchChild.setFkField(FK_MELDUNG);
            mons = SessionManager.getProxy().customServerSearch(
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
                if (!mapSchaden.containsKey(id)){
                   mapSchaden.put(id, beansSchaden);
                }
                //final CidsBean[] beanArraySchaden = beansSchaden.toArray();
                    fireLoadingCompleteSchaden(id);
                for(CidsBean beanSchaden:beansSchaden){
               // for(CidsBean beanSchaden:beanArraySchaden){;
                    //return loadChildrenForSchaden(beanSchaden.getPrimaryKeyValue(), connectionContext);
                    if(!(loadChildrenForSchaden(beanSchaden.getPrimaryKeyValue(), connectionContext))){
                        return false;
                    }
                }
            }
        }catch (ConnectionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    public boolean loadChildrenErsatz(final Integer id, final ConnectionContext connectionContext) throws ConnectionException{
        Collection<MetaObjectNode> mons;
        try{
            //Ersatz hinzufuegen
            searchChild.setTable(TABLE_ERSATZ);
            searchChild.setParentId(id);
            searchChild.setFkField(FK_SCHADEN);
            mons = SessionManager.getProxy().customServerSearch(
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
                if(!mapErsatz.containsKey(id)){
                    mapErsatz.put(id, beansErsatz);
                }
            }
        }catch (ConnectionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    public boolean loadChildrenFest(final Integer id, final ConnectionContext connectionContext) throws ConnectionException{
        Collection<MetaObjectNode> mons;
        try{
            //Fest hinzufuegen
            searchChild.setParentId(id);
            searchChild.setTable(TABLE_FEST);
            searchChild.setFkField(FK_SCHADEN);
            mons = SessionManager.getProxy().customServerSearch(
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
                if(!mapFest.containsKey(id)){
                    mapFest.put(id, beansFest);
                }
            }
        }catch (ConnectionException ex) {
            //Exceptions.printStackTrace(ex);
            LOG.error("Error during loading", ex);
            return false;
        }
        return true;
    }
    public BaumChildrenLoader(BaumParentPanel baumOrganizer) {
       searchChild = new BaumChildLightweightSearch(
                CHILD_TOSTRING_TEMPLATE,
                CHILD_TOSTRING_FIELDS,
                TABLE_MELDUNG,
                FK_GEBIET);
       this.parentOrganizer = baumOrganizer;
    }

    public List<CidsBean> getMapValueMeldung(Integer key){
        return mapMeldung.get(key);
    }
    public List<CidsBean> getMapValueSchaden(Integer key){
        return mapSchaden.get(key);
    }
    public List<CidsBean> getMapValueOrt(Integer key){
        return mapOrt.get(key);
    }
    public List<CidsBean> getMapValueFest(Integer key){
        return mapFest.get(key);
    }
    public List<CidsBean> getMapValueErsatz(Integer key){
        return mapErsatz.get(key);
    }
    
    public void clearAllMaps(){
        mapOrt.clear();
        mapSchaden.clear();
        mapErsatz.clear();
        mapFest.clear();
        mapMeldung.clear();
    }
    
    public void setLoadingCompletedWithoutError(final Boolean status){
        this.loadingCompletedWithoutError = status;
        if (status){
            fireLoadingComplete();
        }
    }
    
    public boolean removeOrt (Integer idMeldung, CidsBean beanOrt){
        //return (mapOrt.get(idMeldung)).remove(beanOrt);
        return removeFromMap(idMeldung, beanOrt, mapOrt);
    }
    
    public boolean removeSchaden (Integer idMeldung, CidsBean beanSchaden){
        return removeFromMap(idMeldung, beanSchaden, mapSchaden);
    }
    
    public boolean removeFest (Integer idSchaden, CidsBean beanFest){
        return removeFromMap(idSchaden, beanFest, mapFest);
    }
    
    public boolean removeErsatz (Integer idSchaden, CidsBean beanErsatz){
        return removeFromMap(idSchaden, beanErsatz, mapErsatz);
    }
    
    public boolean removeMeldung (Integer idGebiet, CidsBean beanMeldung){
        return removeFromMap(idGebiet, beanMeldung, mapMeldung);
    }
    
    public void addOrt (Integer idMeldung, CidsBean beanOrt){
       /* List<CidsBean> tempList = new ArrayList<>();
        if (mapOrt.get(idMeldung) != null){
            tempList = mapOrt.get(idMeldung); 
            tempList.add(beanOrt);
            mapOrt.replace(idMeldung, tempList);
        } else{
            tempList.add(beanOrt);
            mapOrt.put(idMeldung, tempList);
        }*/
        addToMap(idMeldung, beanOrt, mapOrt);
    }
    
    public void addSchaden (Integer idMeldung, CidsBean beanSchaden){
        addToMap(idMeldung, beanSchaden, mapSchaden);
    }
    
    public void addMeldung (Integer idMeldung, CidsBean beanMeldung){
        addToMap(idMeldung, beanMeldung, mapMeldung);
    }
    
    public void addErsatz (Integer idSchaden, CidsBean beanErsatz){
        addToMap(idSchaden, beanErsatz, mapErsatz);
    }
    
    public void addFest (Integer idSchaden, CidsBean beanFest){
        addToMap(idSchaden, beanFest, mapFest);
    }
    
    
    public boolean removeFromMap (Integer id, CidsBean bean, Map <Integer, List<CidsBean>> map){
        return (map.get(id)).remove(bean);
    }
    
    public void addToMap (Integer id, CidsBean bean, Map <Integer, List<CidsBean>> map){
        List<CidsBean> tempList = new ArrayList<>();
        if (map.get(id) != null){
            tempList = map.get(id); 
            tempList.add(bean);
            map.replace(id, tempList);
        } else{
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
    
    private void fireLoadingComplete() {
        for (Listener listener:listeners){
            listener.loadingComplete();
        }
    }
    
    private void fireLoadingCompleteMeldung() {
        for (Listener listener:listeners){
            listener.loadingCompleteMeldung();
        }
    }
    
    private void fireLoadingCompleteOrt(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingCompleteOrt(primaryKeyValue);
        }
    }

    private void fireLoadingErrorOrt(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingErrorOrt(primaryKeyValue);
        }
    }

    private void fireLoadingCompleteSchaden(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingCompleteSchaden(primaryKeyValue);
        }
    }

    private void fireLoadingErrorSchaden(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingErrorSchaden(primaryKeyValue);
        }
    }
    
    private void fireLoadingCompleteErsatz(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingCompleteErsatz(primaryKeyValue);
        }
    }

    private void fireLoadingErrorErsatz(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingErrorErsatz(primaryKeyValue);
        }
    }

    private void fireLoadingCompleteFest(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingCompleteFest(primaryKeyValue);
        }
    }

    private void fireLoadingErrorFest(Integer primaryKeyValue) {
        for (Listener listener:listeners){
            listener.loadingErrorFest(primaryKeyValue);
        }
    }

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
         * @param idMeldung
         */
        void loadingCompleteSchaden(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingCompleteOrt(Integer idMeldung);/**
         * DOCUMENT ME!
         */

        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingCompleteFest(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingCompleteErsatz(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingErrorSchaden(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingErrorFest(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingErrorErsatz(Integer idMeldung);
        /**
         * DOCUMENT ME!
         * @param idMeldung
         */
        void loadingErrorOrt(Integer idMeldung);/**
         * DOCUMENT ME!
         */
    }
    
}
