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
}
