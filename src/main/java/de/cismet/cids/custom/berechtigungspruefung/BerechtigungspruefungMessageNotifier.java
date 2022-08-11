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
package de.cismet.cids.custom.berechtigungspruefung;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cismet.cids.custom.clientutils.BerechtigungspruefungKonfiguration;
import de.cismet.cids.custom.wunda_blau.search.server.BerechtigungspruefungOffeneAnfragenStatement;

import de.cismet.cids.servermessage.CidsServerMessageNotifierListener;
import de.cismet.cids.servermessage.CidsServerMessageNotifierListenerEvent;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BerechtigungspruefungMessageNotifier implements CidsServerMessageNotifierListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(BerechtigungspruefungMessageNotifier.class);
    public static final String CONFATTR_PRODUKTTYPES = "custom.berechtigungspruefung.benachrichtigung_produkttypen";

    //~ Instance fields --------------------------------------------------------

    private final Collection<BerechtigungspruefungMessageNotifierListener> listeners = new ArrayList<>();
    private final Set<String> produkttypeList = new HashSet<>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BerechtigungspruefungMessageNotifier object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    private BerechtigungspruefungMessageNotifier(final ConnectionContext connectionContext) {
        try {
            final String confAttr = SessionManager.getConnection()
                        .getConfigAttr(SessionManager.getSession().getUser(),
                            CONFATTR_PRODUKTTYPES,
                            connectionContext);
            if (confAttr != null) {
                final BerechtigungspruefungKonfiguration conf = BerechtigungspruefungKonfiguration.INSTANCE;
                final Collection<String> existingTypes = new ArrayList<String>(conf.getProdukte().size());
                for (final BerechtigungspruefungKonfiguration.ProduktTyp type : conf.getProdukte()) {
                    existingTypes.add(type.getProduktbezeichnung());
                }
                for (final String line : Arrays.asList(confAttr.trim().split("\n"))) {
                    if (line != null) {
                        final String type = line.trim();
                        if (existingTypes.contains(type)) {
                            produkttypeList.add(type);
                        }
                    }
                }
            }
        } catch (final ConnectionException ex) {
            LOG.warn("error while getConfAttr: " + CONFATTR_PRODUKTTYPES, ex);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getProdukttypeList() {
        return produkttypeList;
    }

    @Override
    public void messageRetrieved(final CidsServerMessageNotifierListenerEvent event) {
        final String category = event.getMessage().getCategory();
        if (BerechtigungspruefungProperties.getInstance().getCsmAnfrage().equals(category)) {
            final List<String> schluesselList = (List)event.getMessage().getContent();
            final String schluessel = schluesselList.iterator().next();
            fireAnfrageAdded(schluessel);
        } else if (BerechtigungspruefungProperties.getInstance().getCsmBearbeitung().equals(category)) {
            final List<String> schluesselList = (List)event.getMessage().getContent();
            final String schluessel = schluesselList.isEmpty() ? null : schluesselList.iterator().next();
            fireAnfrageRemoved(schluessel);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public String getAeltesteOffeneAnfrage(final ConnectionContext connectionContext) throws ConnectionException {
        if (getOffeneAnfragen(connectionContext).isEmpty()) {
            return null;
        } else {
            return getOffeneAnfragen(connectionContext).iterator().next();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   connectionContext  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ConnectionException  DOCUMENT ME!
     */
    public List<String> getOffeneAnfragen(final ConnectionContext connectionContext) throws ConnectionException {
        final List<String> offeneAnfragen = (List<String>)SessionManager.getProxy()
                    .customServerSearch(SessionManager.getSession().getUser(),
                            new BerechtigungspruefungOffeneAnfragenStatement(produkttypeList),
                            connectionContext);
        return offeneAnfragen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BerechtigungspruefungMessageNotifier getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean addListener(final BerechtigungspruefungMessageNotifierListener listener) {
        return listeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   listener  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeListener(final BerechtigungspruefungMessageNotifierListener listener) {
        return listeners.remove(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void fireAnfrageAdded(final String key) {
        for (final BerechtigungspruefungMessageNotifierListener listener : listeners) {
            listener.anfrageAdded(key);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void fireAnfrageRemoved(final String key) {
        for (final BerechtigungspruefungMessageNotifierListener listener : listeners) {
            listener.anfrageRemoved(key);
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final BerechtigungspruefungMessageNotifier INSTANCE = new BerechtigungspruefungMessageNotifier(
                ConnectionContext.create(
                    ConnectionContext.Category.INSTANCE,
                    BerechtigungspruefungMessageNotifier.class.getSimpleName()));

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
