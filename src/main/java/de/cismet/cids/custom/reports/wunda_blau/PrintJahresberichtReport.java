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

import java.util.Collection;
import java.util.Date;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PrintJahresberichtReport extends PrintStatisticsReport {

    //~ Instance fields --------------------------------------------------------

    private final int year;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PrintJahresberichtReport object.
     *
     * @param  year               DOCUMENT ME!
     * @param  fromDate_tillDate  DOCUMENT ME!
     * @param  billingsBeans      DOCUMENT ME!
     */
    public PrintJahresberichtReport(final int year,
            final Date[] fromDate_tillDate,
            final Collection<CidsBean> billingsBeans) {
        super(fromDate_tillDate, billingsBeans);
        this.year = year;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected BillingJahresberichtReport createReport() {
        return new BillingJahresberichtReport(
                year,
                billingsBeans,
                fromDate_tillDate[0],
                fromDate_tillDate[1],
                amountTotalDownloads,
                amountWithCosts,
                amountWithoutCosts,
                amountVUamtlicherLageplan,
                amountVUhoheitlicheVermessung,
                amountVUsonstige,
                amountVUamtlicherLageplanGBs.size(),
                amountVUhoheitlicheVermessungGBs.size(),
                amountVUsonstigeGBs.size(),
                amountWithCostsVU,
                amountWithCostsWiederver,
                earningsWithCostsVU,
                earningsWithCostsWiederver,
                amountWiederverkaeufe,
                amountWiederverkaeufeGBs.size());
    }
}
