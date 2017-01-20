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
package de.cismet.cids.custom.reports.wunda_blau;

import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BillingJahresberichtReport extends BillingStatisticsReport {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_URL = "/de/cismet/cids/custom/reports/wunda_blau/BezReg_JB.jasper";

    //~ Instance fields --------------------------------------------------------

    protected final int year;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingJahresberichtReport object.
     *
     * @param  year                             DOCUMENT ME!
     * @param  billingBeans                     DOCUMENT ME!
     * @param  from                             DOCUMENT ME!
     * @param  till                             DOCUMENT ME!
     * @param  amountTotalDownloads             DOCUMENT ME!
     * @param  amountWithCosts                  DOCUMENT ME!
     * @param  amountWithoutCosts               DOCUMENT ME!
     * @param  amountVUamtlicherLageplan        DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessung    DOCUMENT ME!
     * @param  amountVUsonstige                 DOCUMENT ME!
     * @param  amountVUamtlicherLageplanGB      DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessungGB  DOCUMENT ME!
     * @param  amountVUsonstigeGB               DOCUMENT ME!
     * @param  amountWithCostsVU                DOCUMENT ME!
     * @param  amountWithCostsWiederver         DOCUMENT ME!
     * @param  earningsWithCostsVU              DOCUMENT ME!
     * @param  earningsWithCostsWiederver       DOCUMENT ME!
     * @param  amountWiederverkaeufe            DOCUMENT ME!
     * @param  amountWiederverkaeufeGB          DOCUMENT ME!
     */
    public BillingJahresberichtReport(final int year,
            final Collection<CidsBean> billingBeans,
            final Date from,
            final Date till,
            final int amountTotalDownloads,
            final int amountWithCosts,
            final int amountWithoutCosts,
            final int amountVUamtlicherLageplan,
            final int amountVUhoheitlicheVermessung,
            final int amountVUsonstige,
            final int amountVUamtlicherLageplanGB,
            final int amountVUhoheitlicheVermessungGB,
            final int amountVUsonstigeGB,
            final int amountWithCostsVU,
            final int amountWithCostsWiederver,
            final double earningsWithCostsVU,
            final double earningsWithCostsWiederver,
            final int amountWiederverkaeufe,
            final int amountWiederverkaeufeGB) {
        super(
            billingBeans,
            from,
            till,
            amountTotalDownloads,
            amountWithCosts,
            amountWithoutCosts,
            amountVUamtlicherLageplan,
            amountVUhoheitlicheVermessung,
            amountVUsonstige,
            amountVUamtlicherLageplanGB,
            amountVUhoheitlicheVermessungGB,
            amountVUsonstigeGB,
            amountWithCostsVU,
            amountWithCostsWiederver,
            earningsWithCostsVU,
            earningsWithCostsWiederver,
            amountWiederverkaeufe,
            amountWiederverkaeufeGB);
        this.year = year;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected BillingStatisticsDataSourceAccumulation createDataSourceAccumulation() {
        final BillingStatisticsDataSourceAccumulation dataSourceAccumulation =
            new BillingJahresberichtDataSourceAccumulation(billingBeans, year);
        dataSourceAccumulation.fetchSearchResults();
        return dataSourceAccumulation;
    }

    @Override
    protected String getReportUrl() {
        return REPORT_URL;
    }
    @Override
    protected String getFilename() {
        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        final boolean fullYear = false;
        return ((fullYear) ? ("BezReg_JB_" + year) : ("BezReg_JB_" + year + "_bis_" + format.format(till)));
    }

    @Override
    protected String getTitle() {
        return "Jahresbericht für Bezirksregierung " + year;
    }
}
