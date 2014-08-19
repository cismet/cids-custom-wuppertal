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
 * A listener which gets notified if a Sb_stadtbildserieGridObject was moved to another grid or if a Stadtbild of that
 * GridObject was selected or deselected.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public interface Sb_stadtbildserieGridObjectListener {

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

    /**
     * DOCUMENT ME!
     *
     * @param  source  DOCUMENT ME!
     */
    void sb_stadtbildserieGridObjectMoveToBin(Sb_stadtbildserieGridObject source);
    /**
     * DOCUMENT ME!
     *
     * @param  source  DOCUMENT ME!
     */
    void sb_stadtbildserieGridObjectRemovedFromBin(Sb_stadtbildserieGridObject source);
}
