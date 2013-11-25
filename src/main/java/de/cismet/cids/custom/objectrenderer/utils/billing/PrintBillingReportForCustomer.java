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
package de.cismet.cids.custom.objectrenderer.utils.billing;

import org.apache.log4j.Logger;

import org.openide.util.NbBundle;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.cismet.cids.custom.reports.wunda_blau.BillingBuchungsbelegReport;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class PrintBillingReportForCustomer {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PrintBillingReportForCustomer.class);

    //~ Instance fields --------------------------------------------------------

    private HashMap<Double, HashMap<String, Object>> billingInformation;
    private CidsBean kundeBean;
    private BigDecimal totalSum;
    private BigDecimal mwstValue;
    private Date[] fromDate_tillDate;
    private Collection<CidsBean> billingsBeans;
    private boolean isRechnungsanlage;
    private boolean markBillingsAsBilled;
    private int amountTotalDownloads = 0;
    private int amountWithCosts = 0;
    private int amountWithoutCosts = 0;
    private int amountVUamtlicherLageplan = 0;
    private int amountVUhoheitlicheVermessung = 0;
    private int amountVUsonstige = 0;
    private int amountEigenerGebrauch = 0;
    private int amountWiederverkauf = 0;
    private int amountEigenerGebrauchGebührenbefreit = 0;
    private HashSet amountVUamtlicherLageplanGB = new HashSet();
    private HashSet amountVUhoheitlicheVermessungGB = new HashSet();
    private HashSet amountVUsonstigeGB = new HashSet();
    private HashSet amountEigenerGebrauchGB = new HashSet();
    private HashSet amountWiederverkaufGB = new HashSet();
    private HashSet amountEigenerGebrauchGebührenbefreitGB = new HashSet();
    private JPanel panel;
    private boolean showBillingWithoutCostInReport;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StartReportForCustomer object.
     *
     * @param  kundeBean                       DOCUMENT ME!
     * @param  billingsBeans                   DOCUMENT ME!
     * @param  fromDate_tillDate               DOCUMENT ME!
     * @param  isRechnungsanlage               DOCUMENT ME!
     * @param  markBillingsAsBilled            DOCUMENT ME!
     * @param  panel                           DOCUMENT ME!
     * @param  showBillingWithoutCostInReport  DOCUMENT ME!
     */
    public PrintBillingReportForCustomer(final CidsBean kundeBean,
            final Collection<CidsBean> billingsBeans,
            final Date[] fromDate_tillDate,
            final boolean isRechnungsanlage,
            final boolean markBillingsAsBilled,
            final JPanel panel,
            final boolean showBillingWithoutCostInReport) {
        this.kundeBean = kundeBean;
        this.fromDate_tillDate = fromDate_tillDate;
        this.isRechnungsanlage = isRechnungsanlage;
        this.markBillingsAsBilled = markBillingsAsBilled;
        this.billingsBeans = billingsBeans;
        this.panel = panel;
        this.showBillingWithoutCostInReport = showBillingWithoutCostInReport;
        totalSum = generateStatisticsForTheReport(billingsBeans);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void print() {
        Collection<CidsBean> filteredBuchungen_mwst0 = new ArrayList<CidsBean>();
        BigDecimal nettoSum_0 = null;
        BigDecimal bruttoSum_0 = null;
        boolean noData = true;
        if (billingInformation.containsKey(0d)) {
            final HashMap<String, Object> mwst_information_0 = billingInformation.get(0d);
            filteredBuchungen_mwst0 = (Collection<CidsBean>)mwst_information_0.get("billings");
            nettoSum_0 = (BigDecimal)mwst_information_0.get("netto_summe");
            bruttoSum_0 = (BigDecimal)mwst_information_0.get("brutto_summe");
            noData = false;
        }

        Collection<CidsBean> filteredBuchungen_mwst19 = new ArrayList<CidsBean>();
        BigDecimal nettoSum_19 = null;
        BigDecimal bruttoSum_19 = null;
        if (billingInformation.containsKey(19d)) {
            final HashMap<String, Object> mwst_information_19 = billingInformation.get(19d);
            filteredBuchungen_mwst19 = (Collection<CidsBean>)billingInformation.get(19d).get("billings");
            nettoSum_19 = (BigDecimal)mwst_information_19.get("netto_summe");
            bruttoSum_19 = (BigDecimal)mwst_information_19.get("brutto_summe");
            noData = false;
        }

        if (noData && !isRechnungsanlage) {
            // the report is an empty Buchungsbeleg
            JOptionPane.showMessageDialog(
                panel,
                NbBundle.getMessage(
                    PrintBillingReportForCustomer.class,
                    "PrintBillingReportForCustomer.print().dialog.message"),
                NbBundle.getMessage(
                    PrintBillingReportForCustomer.class,
                    "PrintBillingReportForCustomer.print().dialog.title"),
                JOptionPane.ERROR_MESSAGE);
        } else {
            final BillingBuchungsbelegReport report = new BillingBuchungsbelegReport(
                    kundeBean,
                    filteredBuchungen_mwst0,
                    nettoSum_0,
                    bruttoSum_0,
                    filteredBuchungen_mwst19,
                    nettoSum_19,
                    bruttoSum_19,
                    fromDate_tillDate[0],
                    fromDate_tillDate[1],
                    mwstValue,
                    totalSum,
                    isRechnungsanlage,
                    amountTotalDownloads,
                    amountWithCosts,
                    amountWithoutCosts,
                    amountVUamtlicherLageplan,
                    amountVUhoheitlicheVermessung,
                    amountVUsonstige,
                    amountEigenerGebrauch,
                    amountWiederverkauf,
                    amountEigenerGebrauchGebührenbefreit,
                    amountVUamtlicherLageplanGB.size(),
                    amountVUhoheitlicheVermessungGB.size(),
                    amountVUsonstigeGB.size(),
                    amountEigenerGebrauchGB.size(),
                    amountWiederverkaufGB.size(),
                    amountEigenerGebrauchGebührenbefreitGB.size());
            report.print();

            if (isRechnungsanlage && markBillingsAsBilled) {
                markBillings();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   billingBeans  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private BigDecimal generateStatisticsForTheReport(final Collection<CidsBean> billingBeans) {
        final HashMap<BigDecimal, BigDecimal> mwstSatz_nettoSum = new HashMap<BigDecimal, BigDecimal>();
        billingInformation = new HashMap<Double, HashMap<String, Object>>();

        // calculate the netto sum of every mwst_satz
        for (final CidsBean billing : billingBeans) {
            BigDecimal netto_summe;
            BigDecimal mwst_satz;

            final Double netto_summe_bean = (Double)billing.getProperty("netto_summe");
            if (netto_summe_bean != null) {
                netto_summe = new BigDecimal(netto_summe_bean.toString());
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

            if (showBillingWithoutCostInReport || (netto_summe.compareTo(new BigDecimal("0")) > 0)) {
                // add the billing to the right MwSt-category
                if (!billingInformation.containsKey(mwst_satz.doubleValue())) {
                    final HashMap<String, Object> information = new HashMap<String, Object>();
                    final Collection<CidsBean> billings = new ArrayList<CidsBean>();
                    billings.add(billing);
                    information.put("billings", billings);
                    billingInformation.put(mwst_satz.doubleValue(), information);
                } else {
                    ((Collection)billingInformation.get(mwst_satz.doubleValue()).get("billings")).add(billing);
                }
            }

            setCountersDependingOnVerwendungszweck(billing);
        }

        // calculate the brutto sum from each netto_sum
        totalSum = new BigDecimal("0");
        mwstValue = new BigDecimal("0");
        for (final BigDecimal mwst_satz : mwstSatz_nettoSum.keySet()) {
            final BigDecimal nettoSum = mwstSatz_nettoSum.get(mwst_satz);
            // billing information might not contain any billings for a type of MwSt. if
            // showBillingWithoutCostInReport is false
            if (billingInformation.containsKey(mwst_satz.doubleValue())) {
                billingInformation.get(mwst_satz.doubleValue()).put("netto_summe", nettoSum);

                // calculate: bruttoSum = nettoSum + (nettoSum * (mwst_satz / 100))
                final BigDecimal percent = mwst_satz.divide(new BigDecimal("100"));
                mwstValue = nettoSum.multiply(percent);
                mwstValue = mwstValue.setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal bruttoSum = nettoSum.add(mwstValue);
                bruttoSum = bruttoSum.setScale(2, BigDecimal.ROUND_HALF_UP);
                totalSum = totalSum.add(bruttoSum);
                billingInformation.get(mwst_satz.doubleValue()).put("brutto_summe", bruttoSum);
            }
        }
        return totalSum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  billing  DOCUMENT ME!
     */
    private void setCountersDependingOnVerwendungszweck(final CidsBean billing) {
        final String verwendungsKey = (String)billing.getProperty("verwendungskey");

        amountTotalDownloads++;
        final Double nettoSum = (Double)billing.getProperty("netto_summe");
        if (nettoSum > 0) {
            amountWithCosts++;
        } else {
            amountWithoutCosts++;
        }

        final String geschaeftsbuchnummer = (String)billing.getProperty("geschaeftsbuchnummer");
        boolean geschaeftsbuchnummerIsValid = false;
        if (!geschaeftsbuchnummer.trim().equals("")) {
            geschaeftsbuchnummerIsValid = true;
        }

        if (verwendungsKey.equals("VU aL")) {
            amountVUamtlicherLageplan++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUamtlicherLageplanGB.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("VU hV")) {
            amountVUhoheitlicheVermessung++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUhoheitlicheVermessungGB.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("VU s")) {
            amountVUsonstige++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUsonstigeGB.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("eigG")) {
            amountEigenerGebrauch++;
            if (geschaeftsbuchnummerIsValid) {
                amountEigenerGebrauchGB.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("WV ein")) {
            amountWiederverkauf++;
            if (geschaeftsbuchnummerIsValid) {
                amountWiederverkaufGB.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("eigG frei")) {
            amountEigenerGebrauchGebührenbefreit++;
            if (geschaeftsbuchnummerIsValid) {
                amountEigenerGebrauchGebührenbefreitGB.add(geschaeftsbuchnummer);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void markBillings() {
        for (final CidsBean billing : billingsBeans) {
            try {
                billing.setProperty("abgerechnet", Boolean.TRUE);
                billing.persist();
            } catch (Exception ex) {
                LOG.error("Error while setting value or persisting of billing.", ex);
            }
        }
    }
}
