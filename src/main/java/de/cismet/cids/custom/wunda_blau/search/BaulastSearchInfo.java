/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import java.util.ArrayList;
import java.util.List;

import de.cismet.cids.custom.wunda_blau.search.CidsBaulastSearchStatement.Result;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class BaulastSearchInfo {

    //~ Instance fields --------------------------------------------------------

    private List<FlurstueckInfo> flurstuecke;

    private String blattnummer;
    //
    private Result result;
    //
    private boolean gueltig;
    private boolean ungueltig;
    //
    private boolean belastet;
    private boolean beguenstigt;
    //
    private String bounds;
    //
    private String art;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BaulastSearchInfo object.
     */
    public BaulastSearchInfo() {
        gueltig = true;
        ungueltig = true;
        belastet = true;
        beguenstigt = true;
        flurstuecke = new ArrayList<FlurstueckInfo>();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the blattnummer
     */
    public String getBlattnummer() {
        return blattnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<FlurstueckInfo> getFlurstuecke() {
        return flurstuecke;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  blattnummer  the blattnummer to set
     */
    public void setBlattnummer(final String blattnummer) {
        this.blattnummer = blattnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  result  the result to set
     */
    public void setResult(final Result result) {
        this.result = result;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the gueltig
     */
    public boolean isGueltig() {
        return gueltig;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  gueltig  the gueltig to set
     */
    public void setGueltig(final boolean gueltig) {
        this.gueltig = gueltig;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  flurstuecke  DOCUMENT ME!
     */
    public void setFlurstuecke(final List<FlurstueckInfo> flurstuecke) {
        this.flurstuecke = flurstuecke;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the ungueltig
     */
    public boolean isUngueltig() {
        return ungueltig;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  ungueltig  the ungueltig to set
     */
    public void setUngueltig(final boolean ungueltig) {
        this.ungueltig = ungueltig;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the belastet
     */
    public boolean isBelastet() {
        return belastet;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  belastet  the belastet to set
     */
    public void setBelastet(final boolean belastet) {
        this.belastet = belastet;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the beguenstigt
     */
    public boolean isBeguenstigt() {
        return beguenstigt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beguenstigt  the beguenstigt to set
     */
    public void setBeguenstigt(final boolean beguenstigt) {
        this.beguenstigt = beguenstigt;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the bounds
     */
    public String getBounds() {
        return bounds;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bounds  the bounds to set
     */
    public void setBounds(final String bounds) {
        this.bounds = bounds;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the art
     */
    public String getArt() {
        return art;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  art  the art to set
     */
    public void setArt(final String art) {
        this.art = art;
    }
}
