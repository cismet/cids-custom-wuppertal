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

import com.vividsolutions.jts.geom.Geometry;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.cismet.cids.custom.utils.MauernProperties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerDokumenteEditor.DOCUMENTS_PROPERTY;
import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerDokumenteEditor.FILENAME_PROPERTY;
import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerDokumenteEditor.GEOFIELD_PROPERTY;
import static de.cismet.cids.custom.objecteditors.wunda_blau.MauerDokumenteEditor.POSITION_PROPERTY;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernReportBean extends ReportBeanWithMapAndTwoUrlImages {

    //~ Static fields/initializers ---------------------------------------------

    private static final String DOWNLOAD_TEMPLATE =
        "<rasterfari:url>?REQUEST=GetMap&SERVICE=WMS&customDocumentInfo=download&LAYERS=<rasterfari:path>/<rasterfari:document>";

    //~ Instance fields --------------------------------------------------------

    private final MauernProperties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  mauer              DOCUMENT ME!
     * @param  beanOnly           DOCUMENT ME!
     * @param  properties         DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public MauernReportBean(final CidsBean mauer,
            final boolean beanOnly,
            final MauernProperties properties,
            final ConnectionContext connectionContext) {
        super(
            mauer,
            beanOnly ? null : properties.getMapUrl(),
            connectionContext);
        this.properties = properties;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected List<CidsBean> getImageBeans() {
        if (getCidsBean() == null) {
            return null;
        }
        return getCidsBean().getBeanCollectionProperty(DOCUMENTS_PROPERTY).stream().filter((b) -> {
                    return b.getProperty(POSITION_PROPERTY) != null;
                }).filter((b) -> { return "foto".equals(b.getProperty("fk_art.schluessel")); })
                    .sorted(Comparator.comparing((b) -> {
                                    return (b != null) ? (Integer)b.getProperty(POSITION_PROPERTY) : null;
                                },
                                Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
    }

    @Override
    protected URL getUrl(final CidsBean cidsBean) throws Exception {
        return new URL(DOWNLOAD_TEMPLATE.replace("<rasterfari:path>", properties.getRasterfariPath()).replace(
                    "<rasterfari:url>",
                    properties.getRasterfariUrl()).replace(
                    "<rasterfari:document>",
                    (String)cidsBean.getProperty(FILENAME_PROPERTY)));
    }

    @Override
    protected Geometry getGeometry() {
        return (getCidsBean() != null) ? (Geometry)getCidsBean().getProperty(GEOFIELD_PROPERTY) : null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private MauernProperties getProperties() {
        return properties;
    }

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
