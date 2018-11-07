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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BillingJahresberichtReport extends BillingStatisticsReport {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_URL = "/de/cismet/cids/custom/reports/wunda_blau/BezReg_JB.jasper";

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingJahresberichtReport object.
     *
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
     * @param  amountWiederverkaeufe            DOCUMENT ME!
     * @param  amountWiederverkaeufeGB          DOCUMENT ME!
     * @param  earningsWithCostsVU              DOCUMENT ME!
     * @param  earningsWithCostsWiederver       DOCUMENT ME!
     * @param  connectionContext                DOCUMENT ME!
     */
    public BillingJahresberichtReport(
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
            final int amountWiederverkaeufe,
            final int amountWiederverkaeufeGB,
            final double earningsWithCostsVU,
            final double earningsWithCostsWiederver,
            final ConnectionContext connectionContext) {
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
            amountWiederverkaeufe,
            amountWiederverkaeufeGB,
            earningsWithCostsVU,
            earningsWithCostsWiederver,
            connectionContext);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected BillingStatisticsDataSourceAccumulation createDataSourceAccumulation() {
        final BillingStatisticsDataSourceAccumulation dataSourceAccumulation =
            new BillingJahresberichtDataSourceAccumulation(billingBeans, from, till, getConnectionContext());
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
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        final int year = calendar.get(Calendar.YEAR);
        return ((fullYear) ? ("BezReg_JB_" + year) : ("BezReg_JB_" + year + "_bis_" + format.format(till)));
    }

    @Override
    protected String getTitle() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(from);
        final int year = calendar.get(Calendar.YEAR);
        return "Jahresbericht für Bezirksregierung " + year;
    }
}
