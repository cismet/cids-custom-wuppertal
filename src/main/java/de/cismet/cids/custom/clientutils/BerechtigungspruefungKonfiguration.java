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

import Sirius.navigator.connection.SessionManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

import org.apache.log4j.Logger;

import java.io.StringReader;

import java.util.Collection;
import java.util.List;

import de.cismet.cids.server.actions.GetServerResourceServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext.Category;

import de.cismet.connectioncontext.ConnectionContext;

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

    public static final Logger LOG = Logger.getLogger(BerechtigungspruefungKonfiguration.class);

    public static BerechtigungspruefungKonfiguration INSTANCE;

    static {
        BerechtigungspruefungKonfiguration conf = null;
        try {
            final Object ret = SessionManager.getSession()
                        .getConnection()
                        .executeTask(SessionManager.getSession().getUser(),
                            GetServerResourceServerAction.TASK_NAME,
                            "WUNDA_BLAU",
                            WundaBlauServerResources.BERECHTIGUNGSPRUEFUNG_CONF_JSON.getValue(),
                            ConnectionContext.create(
                                Category.STATIC,
                                BerechtigungspruefungKonfiguration.class.getSimpleName()));
            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
            conf = (BerechtigungspruefungKonfiguration)
                new ObjectMapper().readValue(new StringReader((String)ret),
                    BerechtigungspruefungKonfiguration.class);
        } catch (final Exception ex) {
            LOG.warn("error while creating BerechtigungspruefungKonfiguration instance", ex);
        }
        INSTANCE = conf;
    }

    //~ Instance fields --------------------------------------------------------

    @JsonProperty private final List<ProduktTyp> produkte;
    @JsonProperty private final List<Freigabegrund> freigabegruende;
    @JsonProperty private final List<Ablehnungsgrund> ablehnungsgruende;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Konfiguration object.
     *
     * @param  produkte           DOCUMENT ME!
     * @param  freigabegruende    DOCUMENT ME!
     * @param  ablehnungsgruende  DOCUMENT ME!
     */
    public BerechtigungspruefungKonfiguration(@JsonProperty("produkte") final List<ProduktTyp> produkte,
            @JsonProperty("freigabegruende") final List<Freigabegrund> freigabegruende,
            @JsonProperty("ablehnungsgruende") final List<Ablehnungsgrund> ablehnungsgruende) {
        this.produkte = produkte;
        this.freigabegruende = freigabegruende;
        this.ablehnungsgruende = ablehnungsgruende;
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
    public static class Ablehnungsgrund {

        //~ Instance fields ----------------------------------------------------

        @JsonProperty private final String vorlage;
        @JsonProperty private final String langtext;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ProduktTyp object.
         *
         * @param  vorlage   DOCUMENT ME!
         * @param  langtext  DOCUMENT ME!
         */
        public Ablehnungsgrund(@JsonProperty("vorlage") final String vorlage,
                @JsonProperty("langtext") final String langtext) {
            this.vorlage = vorlage;
            this.langtext = langtext;
        }
    }

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
    public static class Freigabegrund {

        //~ Instance fields ----------------------------------------------------

        @JsonProperty private final String vorlage;
        @JsonProperty private final String langtext;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ProduktTyp object.
         *
         * @param  vorlage   DOCUMENT ME!
         * @param  langtext  DOCUMENT ME!
         */
        public Freigabegrund(@JsonProperty("vorlage") final String vorlage,
                @JsonProperty("langtext") final String langtext) {
            this.vorlage = vorlage;
            this.langtext = langtext;
        }
    }
}
