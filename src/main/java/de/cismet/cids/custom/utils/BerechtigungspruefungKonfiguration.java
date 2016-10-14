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
package de.cismet.cids.custom.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import java.util.Collection;

import static de.cismet.cids.custom.utils.BaulastBescheinigungDialog.LOG;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@Getter
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class BerechtigungspruefungKonfiguration {

    //~ Static fields/initializers ---------------------------------------------

    public static BerechtigungspruefungKonfiguration INSTANCE;

    static {
        BerechtigungspruefungKonfiguration conf = null;
        try {
            conf = (BerechtigungspruefungKonfiguration)
                new ObjectMapper().readValue(BerechtigungspruefungAnfragePanel.class.getResourceAsStream(
                        "/de/cismet/cids/custom/berechtigungspruefung/berechtigungspruefung_conf.json"),
                    BerechtigungspruefungKonfiguration.class);
        } catch (final Exception ex) {
            LOG.warn("error while creating BerechtigungspruefungKonfiguration instance", ex);
        }
        INSTANCE = conf;
    }

    //~ Instance fields --------------------------------------------------------

    private final Collection<ProduktTyp> produkte;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Konfiguration object.
     *
     * @param  produkte  DOCUMENT ME!
     */
    public BerechtigungspruefungKonfiguration(@JsonProperty("produkte") final Collection<ProduktTyp> produkte) {
        this.produkte = produkte;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
    )
    public static class ProduktTyp {

        //~ Instance fields ----------------------------------------------------

        @JsonProperty private final String produktbezeichnung;
        @JsonProperty private final boolean begruendungstextErlaubt;
        @JsonProperty private final boolean berechtigungsgrundErlaubt;
        @JsonProperty private final boolean dateianhangErlaubt;
        @JsonProperty private final Collection<String> berechtigungsgruende;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ProduktTyp object.
         *
         * @param  produktbezeichnung         DOCUMENT ME!
         * @param  begruendungstextErlaubt    DOCUMENT ME!
         * @param  berechtigungsgrundErlaubt  DOCUMENT ME!
         * @param  dateianhangErlaubt         DOCUMENT ME!
         * @param  berechtigungsgruende       DOCUMENT ME!
         */
        public ProduktTyp(@JsonProperty("produktbezeichnung") final String produktbezeichnung,
                @JsonProperty("begruendungstext_erlaubt") final boolean begruendungstextErlaubt,
                @JsonProperty("berechtigungsgrund_erlaubt") final boolean berechtigungsgrundErlaubt,
                @JsonProperty("dateianhang_erlaubt") final boolean dateianhangErlaubt,
                @JsonProperty("berechtigungsgruende") final Collection<String> berechtigungsgruende) {
            this.produktbezeichnung = produktbezeichnung;
            this.begruendungstextErlaubt = begruendungstextErlaubt;
            this.berechtigungsgrundErlaubt = berechtigungsgrundErlaubt;
            this.dateianhangErlaubt = dateianhangErlaubt;
            this.berechtigungsgruende = berechtigungsgruende;
        }
    }
}
