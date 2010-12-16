
package de.cismet.cids.custom.wunda_blau.search;

import de.cismet.cids.custom.wunda_blau.search.CidsBaulastSearchStatement.Result;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author stefan
 */
public class BaulastSearchInfo {

    public BaulastSearchInfo() {
        gueltig = true;
        ungueltig = true;
        belastet = true;
        beguenstigt = true;
        flurstuecke = new ArrayList<FlurstueckInfo>();
    }

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

    /**
     * @return the blattnummer
     */
    public String getBlattnummer() {
        return blattnummer;
    }

    public List<FlurstueckInfo> getFlurstuecke() {
        return flurstuecke;
    }

    /**
     * @param blattnummer the blattnummer to set
     */
    public void setBlattnummer(String blattnummer) {
        this.blattnummer = blattnummer;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return the gueltig
     */
    public boolean isGueltig() {
        return gueltig;
    }

    /**
     * @param gueltig the gueltig to set
     */
    public void setGueltig(boolean gueltig) {
        this.gueltig = gueltig;
    }

    public void setFlurstuecke(List<FlurstueckInfo> flurstuecke) {
        this.flurstuecke = flurstuecke;
    }

    /**
     * @return the ungueltig
     */
    public boolean isUngueltig() {
        return ungueltig;
    }

    /**
     * @param ungueltig the ungueltig to set
     */
    public void setUngueltig(boolean ungueltig) {
        this.ungueltig = ungueltig;
    }

    /**
     * @return the belastet
     */
    public boolean isBelastet() {
        return belastet;
    }

    /**
     * @param belastet the belastet to set
     */
    public void setBelastet(boolean belastet) {
        this.belastet = belastet;
    }

    /**
     * @return the beguenstigt
     */
    public boolean isBeguenstigt() {
        return beguenstigt;
    }

    /**
     * @param beguenstigt the beguenstigt to set
     */
    public void setBeguenstigt(boolean beguenstigt) {
        this.beguenstigt = beguenstigt;
    }

    /**
     * @return the bounds
     */
    public String getBounds() {
        return bounds;
    }

    /**
     * @param bounds the bounds to set
     */
    public void setBounds(String bounds) {
        this.bounds = bounds;
    }

    /**
     * @return the art
     */
    public String getArt() {
        return art;
    }

    /**
     * @param art the art to set
     */
    public void setArt(String art) {
        this.art = art;
    }
}
