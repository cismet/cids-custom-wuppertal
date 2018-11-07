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

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class PrintJahresberichtReport extends PrintStatisticsReport {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PrintJahresberichtReport object.
     *
     * @param  fromDate_tillDate  DOCUMENT ME!
     * @param  billingsBeans      DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public PrintJahresberichtReport(
            final Date[] fromDate_tillDate,
            final Collection<CidsBean> billingsBeans,
            final ConnectionContext connectionContext) {
        super(fromDate_tillDate, billingsBeans, connectionContext);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected BillingJahresberichtReport createReport() {
        return new BillingJahresberichtReport(
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
                amountWiederverkaeufe,
                amountWiederverkaeufeGBs.size(),
                earningsWithCostsVU,
                earningsWithCostsWiederver,
                getConnectionContext());
    }
}
