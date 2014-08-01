/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public interface Sb_StadtbildChosenListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  source     DOCUMENT ME!
     * @param  stadtbild  DOCUMENT ME!
     */
    void stadtbildChosen(Sb_stadtbildserieGridObject source, CidsBean stadtbild);
    /**
     * DOCUMENT ME!
     *
     * @param  source     DOCUMENT ME!
     * @param  stadtbild  DOCUMENT ME!
     */
    void stadtbildUnchosen(Sb_stadtbildserieGridObject source, CidsBean stadtbild);
}
