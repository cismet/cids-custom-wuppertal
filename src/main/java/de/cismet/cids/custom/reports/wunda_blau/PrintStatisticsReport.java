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

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class PrintStatisticsReport {

    //~ Instance fields --------------------------------------------------------

    private HashMap<String, Integer> productInformation;
    private Date[] fromDate_tillDate;
    private Collection<CidsBean> billingsBeans;
    private int amountTotalDownloads = 0;
    private int amountWithCosts = 0;
    private int amountWithoutCosts = 0;
    private int amountVUamtlicherLageplan = 0;
    private int amountVUhoheitlicheVermessung = 0;
    private int amountVUsonstige = 0;
    private int amountVUamtlicherLageplanGB = 0;
    private int amountVUhoheitlicheVermessungGB = 0;
    private int amountVUsonstigeGB = 0;
    private int amountWithCostsVU = 0;
    private int amountWithCostsWiederver = 0;
    private double earningsWithCostsVU = 0;
    private double earningsWithCostsWiederver = 0;
    private int amountWiederverkaeufe = 0;
    private int amountWiederverkaeufeGB = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PrintStatisticsReport object.
     *
     * @param  fromDate_tillDate  DOCUMENT ME!
     * @param  billingsBeans      DOCUMENT ME!
     */
    public PrintStatisticsReport(final Date[] fromDate_tillDate, final Collection<CidsBean> billingsBeans) {
        this.fromDate_tillDate = fromDate_tillDate;
        this.billingsBeans = billingsBeans;
        for (final CidsBean billingBean : billingsBeans) {
            setCountersDependingOnVerwendungszweck(billingBean);
            addProductInformation(billingBean);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void print() {
        if (!billingsBeans.isEmpty()) {
            final BillingStatisticsReport report = new BillingStatisticsReport(
                    billingsBeans,
                    fromDate_tillDate[0],
                    fromDate_tillDate[1],
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
            report.print();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  billing  DOCUMENT ME!
     */
    private void setCountersDependingOnVerwendungszweck(final CidsBean billing) {
        final String verwendungsKey = (String)billing.getProperty("verwendungskey");

        final String geschaeftsbuchnummer = (String)billing.getProperty("geschaeftsbuchnummer");
        boolean geschaeftsbuchnummerIsValid = false;
        if (!geschaeftsbuchnummer.trim().equals("")) {
            geschaeftsbuchnummerIsValid = true;
        }

        amountTotalDownloads++;
        final Double nettoSum = (Double)billing.getProperty("netto_summe");
        boolean withCosts;
        if (nettoSum > 0) {
            amountWithCosts++;
            withCosts = true;
        } else {
            amountWithoutCosts++;
            withCosts = false;
        }

        if (verwendungsKey.startsWith("VU")) {
            if (withCosts) {
                amountWithCostsVU++;
                earningsWithCostsVU += nettoSum;
            }
        }

        if (verwendungsKey.startsWith("WV")) {
            amountWiederverkaeufe++;
            if (geschaeftsbuchnummerIsValid) {
                amountWiederverkaeufeGB++;
            }
            if (withCosts) {
                amountWithCostsWiederver++;
                earningsWithCostsWiederver += nettoSum;
            }
        }

        if (verwendungsKey.equals("VU aL")) {
            amountVUamtlicherLageplan++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUamtlicherLageplanGB++;
            }
        } else if (verwendungsKey.equals("VU hV")) {
            amountVUhoheitlicheVermessung++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUhoheitlicheVermessungGB++;
            }
        } else if (verwendungsKey.equals("VU s")) {
            amountVUsonstige++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUsonstigeGB++;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  billingBean  DOCUMENT ME!
     */
    private void addProductInformation(final CidsBean billingBean) {
        final String verwendungszweck = (String)billingBean.getProperty("verwendungszweck");
        if (productInformation.containsKey(verwendungszweck)) {
            Integer amount = productInformation.get(verwendungszweck);
            amount += 1;
            productInformation.put(verwendungszweck, amount);
        } else {
            productInformation.put(verwendungszweck, 1);
        }
    }
}
