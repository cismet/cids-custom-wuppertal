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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.custom.objecteditors.wunda_blau.TreppeEditor;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
@Getter
public class TreppenReportBean extends ReportBeanWithMapAndTwoWebDavImages {

    //~ Instance fields --------------------------------------------------------

    private final Collection<StuetzMauerBean> stuetzmauern = new ArrayList<>();
    private final Double zustandStuetzmauern;
    private final Double kostenStuetzmauern;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauernReportBean object.
     *
     * @param  treppe             DOCUMENT ME!
     * @param  editor             DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public TreppenReportBean(final CidsBean treppe,
            final TreppeEditor editor,
            final ConnectionContext connectionContext) {
        super(
            treppe,
            "geometrie.geo_field",
            "bilder",
            "url_treppen",
            java.util.ResourceBundle.getBundle(
                "de/cismet/cids/custom/reports/wunda_blau/MauernReport").getString("map_url"),
            connectionContext);

        zustandStuetzmauern = editor.getZustandStuetzmauern();
        kostenStuetzmauern = editor.getKostenStuetzmauern();

        for (final CidsBean treppeMauerBean : editor.getMauerBeans().keySet()) {
            final CidsBean mauerBean = editor.getMauerBeans().get(treppeMauerBean);

            final Boolean bauwerk = true;
            final String name = (String)mauerBean.getProperty("lagebezeichnung");
            final String art = (String)mauerBean.getProperty("materialtyp.name");
            final String wo = (String)treppeMauerBean.getProperty("wo");
            final StuetzMauerBean stuetzmauer = new StuetzMauerBean(bauwerk, name, art, wo);
            stuetzmauern.add(stuetzmauer);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean getTreppe() {
        return getCidsBean();
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    @AllArgsConstructor
    public class StuetzMauerBean {

        //~ Instance fields ----------------------------------------------------

        private final Boolean treppenbauwerk;
        private final String name;
        private final String art;
        private final String wo;
    }
}
