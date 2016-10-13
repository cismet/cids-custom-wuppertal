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

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public interface BerechtigungspruefungMessageNotifierListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    void anfrageAdded(final String key);

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    void anfrageRemoved(final String key);
}
