package de.cismet.cids.custom.wunda_blau.search;

import java.io.Serializable;

/**
 *
 * @author stefan
 */
public class FlurstueckInfo implements Serializable {

    public FlurstueckInfo(int gemarkung, String flur, String zaehler, String nenner) {
        this.gemarkung = gemarkung;
        this.flur = flur;
        this.zaehler = zaehler;
        this.nenner = nenner;
    }
    public final int gemarkung;
    public final String flur;
    public final String zaehler;
    public final String nenner;
}
