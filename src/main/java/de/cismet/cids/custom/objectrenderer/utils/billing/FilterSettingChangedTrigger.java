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
package de.cismet.cids.custom.objectrenderer.utils.billing;

import javax.swing.Action;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public interface FilterSettingChangedTrigger {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Action getFilterSettingChangedAction();

    /**
     * DOCUMENT ME!
     *
     * @param  filterSettingChangedAction  DOCUMENT ME!
     */
    void setFilterSettingChangedAction(Action filterSettingChangedAction);
}
