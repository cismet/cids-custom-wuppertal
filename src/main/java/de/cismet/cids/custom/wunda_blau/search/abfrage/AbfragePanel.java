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
package de.cismet.cids.custom.wunda_blau.search.abfrage;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cismet.cids.custom.wunda_blau.search.server.StorableSearch;

import de.cismet.connectioncontext.ConnectionContextStore;

/**
 * DOCUMENT ME!
 *
 * @param    <C>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface AbfragePanel<C extends StorableSearch.Configuration> extends ConnectionContextStore {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    ObjectMapper getConfigurationMapper();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getTableName();

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    C createConfiguration();

    /**
     * DOCUMENT ME!
     *
     * @param   configurationJson  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    C readConfiguration(final String configurationJson) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param  configuration  DOCUMENT ME!
     */
    void initFromConfiguration(final C configuration);

    /**
     * DOCUMENT ME!
     *
     * @param  configuration  DOCUMENT ME!
     */
    void initFromConfiguration(final Object configuration); // jalopy workaround

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isEditable();
}
