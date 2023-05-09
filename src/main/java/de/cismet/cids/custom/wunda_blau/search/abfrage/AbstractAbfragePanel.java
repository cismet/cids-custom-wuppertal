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

import lombok.Getter;

import javax.swing.JPanel;

import de.cismet.cids.custom.wunda_blau.search.server.StorableSearch;

/**
 * DOCUMENT ME!
 *
 * @param    <C>
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractAbfragePanel<C extends StorableSearch.Configuration> extends JPanel
        implements AbfragePanel<C> {

    //~ Instance fields --------------------------------------------------------

    @Getter private final boolean editable;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new AbstractAbfragePanel object.
     *
     * @param  editable  DOCUMENT ME!
     */
    protected AbstractAbfragePanel(final boolean editable) {
        this.editable = editable;
    }
}
