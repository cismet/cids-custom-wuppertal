/***************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 *
 *              ... and it just works.
 *
 ****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class AlkisSearchInfo {

    //~ Instance fields --------------------------------------------------------
    private final String name;
    private final String vorname;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new BaulastSearchInfo object.
     */
    public AlkisSearchInfo(String name, String vorname) {
        this.name = name;
        this.vorname = vorname;
    }

    public String getName() {
        return name;
    }

    public String getVorname() {
        return vorname;
    }
}
