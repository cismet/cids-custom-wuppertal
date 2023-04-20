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
package de.cismet.cids.custom.objecteditors.wunda_blau.albo;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface ComboBoxFilterDialogEnabledFilter {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   o    DOCUMENT ME!
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean isEnabled(final Object o, final int row);

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    boolean selectionOfDisabledElementsAllowed();

    /**
     * DOCUMENT ME!
     *
     * @param   o    DOCUMENT ME!
     * @param   row  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getTooltip(final Object o, final int row);
}
