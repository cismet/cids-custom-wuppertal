/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.wunda_blau;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernReportBean extends ReportBeanWithMapAndImages {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  mauer              DOCUMENT ME!
     * @param  beanOnly           DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public MauernReportBean(final CidsBean mauer, final boolean beanOnly, final ConnectionContext connectionContext) {
        super(
            mauer,
            beanOnly ? null : "georeferenz.geo_field",
            beanOnly ? null : "bilder",
            beanOnly ? null : "url",
            beanOnly
                ? null
                : java.util.ResourceBundle.getBundle("de/cismet/cids/custom/reports/wunda_blau/MauernReport").getString(
                    "map_url"),
            connectionContext);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getMauer() {
        return getCidsBean();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   svdg  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer findMax(final String svdg) {
        if (getMauer() == null) {
            return null;
        }
        final Collection<Integer> scores = new ArrayList<>();
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_gelaender.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_kopf.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_ansicht.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_gruendung.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_verformung.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_gelaende_oben.%s", svdg)));
        scores.add((Integer)getMauer().getProperty(String.format("fk_zustand_gelaende.%s", svdg)));
        return maxFrom(scores);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getVerkehrssicherheit() {
        return findMax("verkehrssicherheit");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getStandsicherheit() {
        return findMax("standsicherheit");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getDauerhaftigkeit() {
        return findMax("dauerhaftigkeit");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getGesamt() {
        if (getMauer() == null) {
            return null;
        }

        final Collection<Integer> scores = new ArrayList<>();
        scores.add(getStandsicherheit());
        scores.add(getVerkehrssicherheit());
        scores.add(getDauerhaftigkeit());
        return maxFrom(scores);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   scores  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Integer maxFrom(final Collection<Integer> scores) {
        Integer max = null;
        for (final Integer score : scores) {
            if (score != null) {
                max = (max == null) ? score : Integer.max(max, score);
            }
        }
        return max;
    }
}
