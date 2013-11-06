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
package de.cismet.cids.custom.objectrenderer.utils;

import java.math.BigDecimal;

import java.util.Collection;
import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingCalculations {

    //~ Methods ----------------------------------------------------------------

    /**
     * Calculates the total brutto sum (netto sum + MwSt.) for a collection of billings.
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static BigDecimal calculateBruttoSumFromBillings(final Collection<CidsBean> billingBeans) {
        final HashMap<BigDecimal, BigDecimal> mwstSatz_nettoSum = new HashMap<BigDecimal, BigDecimal>();

        // calculate the netto sum of every mwst_satz
        for (final CidsBean billing : billingBeans) {
            BigDecimal netto_summe;
            BigDecimal mwst_satz;

            final Double netto_summe_bean = (Double)billing.getProperty("netto_summe");
            if (netto_summe_bean != null) {
                netto_summe = BigDecimal.valueOf(netto_summe_bean);
            } else {
                netto_summe = new BigDecimal("0");
            }

            final Double mwst_satz_bean = (Double)billing.getProperty("mwst_satz");
            if (mwst_satz_bean != null) {
                mwst_satz = new BigDecimal(mwst_satz_bean.toString());
            } else {
                mwst_satz = new BigDecimal("0");
            }

            if (mwstSatz_nettoSum.containsKey(mwst_satz)) {
                final BigDecimal subtotal = mwstSatz_nettoSum.get(mwst_satz);
                final BigDecimal newSubtotal = subtotal.add(netto_summe);
                mwstSatz_nettoSum.put(mwst_satz, newSubtotal);
            } else {
                mwstSatz_nettoSum.put(mwst_satz, netto_summe);
            }
        }

        // calculate the brutto sum from each netto_sum
        BigDecimal totalSum = new BigDecimal("0");
        for (final BigDecimal mwst_satz : mwstSatz_nettoSum.keySet()) {
            final BigDecimal nettoSum = mwstSatz_nettoSum.get(mwst_satz);
            final BigDecimal percent = mwst_satz.divide(new BigDecimal("100"));
            final BigDecimal mwstValue = nettoSum.multiply(percent);
            BigDecimal bruttoSum = nettoSum.add(mwstValue);
            bruttoSum = bruttoSum.setScale(2, BigDecimal.ROUND_HALF_UP);
            totalSum = totalSum.add(bruttoSum);
        }
        return totalSum;
    }
}
