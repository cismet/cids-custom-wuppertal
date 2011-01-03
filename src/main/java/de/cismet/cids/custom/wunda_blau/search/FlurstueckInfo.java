/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class FlurstueckInfo implements Serializable {

    //~ Instance fields --------------------------------------------------------

    public final int gemarkung;
    public final String flur;
    public final String zaehler;
    public final String nenner;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FlurstueckInfo object.
     *
     * @param  gemarkung  DOCUMENT ME!
     * @param  flur       DOCUMENT ME!
     * @param  zaehler    DOCUMENT ME!
     * @param  nenner     DOCUMENT ME!
     */
    public FlurstueckInfo(final int gemarkung, final String flur, final String zaehler, final String nenner) {
        this.gemarkung = gemarkung;
        this.flur = flur;
        this.zaehler = zaehler;
        this.nenner = nenner;
    }
}
