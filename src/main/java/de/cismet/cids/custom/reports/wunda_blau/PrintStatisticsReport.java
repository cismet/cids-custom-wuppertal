/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.reports.wunda_blau;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

/**
 * PrintStatisticsReport gets Billing-CidsBean and evaluates them to generate a BillingStatisticsReport.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class PrintStatisticsReport {

    //~ Instance fields --------------------------------------------------------

    protected final HashMap<String, Integer> productInformation = new HashMap<String, Integer>();
    protected final Date[] fromDate_tillDate;
    protected final Collection<CidsBean> billingsBeans;
    protected int amountTotalDownloads = 0;
    protected int amountWithCosts = 0;
    protected int amountWithoutCosts = 0;
    protected int amountVUamtlicherLageplan = 0;
    protected int amountVUhoheitlicheVermessung = 0;
    protected int amountVUsonstige = 0;
    protected int amountWithCostsVU = 0;
    protected int amountWithCostsWiederver = 0;
    protected double earningsWithCostsVU = 0;
    protected double earningsWithCostsWiederver = 0;
    protected int amountWiederverkaeufe = 0;
    protected final Set<String> amountWiederverkaeufeGBs = new HashSet<String>();
    protected final Set<String> amountVUamtlicherLageplanGBs = new HashSet<String>();
    protected final Set<String> amountVUhoheitlicheVermessungGBs = new HashSet<String>();
    protected final Set<String> amountVUsonstigeGBs = new HashSet<String>();

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
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final CidsBean[] billings = new CidsBean[] {
                DevelopmentTools.createCidsBeanFromRMIConnectionOnLocalhost(
                    "WUNDA_BLAU",
                    "Administratoren",
                    "admin",
                    "kif",
                    "billing_billing",
                    6838)
            };
        final ArrayList<CidsBean> list = new ArrayList<CidsBean>(1);
        list.add(billings[0]);

        System.out.println(PrintStatisticsReport.class.getResourceAsStream(
                "/de/cismet/cids/custom/reports/wunda_blau/geschaeftsstatisktik.jasper"));

        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        final Date start = formatter.parse("01.05.2013");
        final Date end = formatter.parse("31.05.2013");

        final PrintStatisticsReport printStatisticsReport = new PrintStatisticsReport(
                new Date[] { start, end },
                list);
        final BillingStatisticsReport report = printStatisticsReport.createReport();
        final Map params = report.generateParamters();
        DevelopmentTools.showReportForBeans(
            "/de/cismet/cids/custom/reports/wunda_blau/geschaeftsstatisktik.jasper",
            list,
            params);
    }

    /**
     * DOCUMENT ME!
     */
    public void print() {
        if (!billingsBeans.isEmpty()) {
            final BillingStatisticsReport report = createReport();
            report.generateReport();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected BillingStatisticsReport createReport() {
        return new BillingStatisticsReport(
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

    /**
     * DOCUMENT ME!
     *
     * @param  billing  DOCUMENT ME!
     */
    private void setCountersDependingOnVerwendungszweck(final CidsBean billing) {
        final String verwendungsKey = (String)billing.getProperty("verwendungskey");

        final String geschaeftsbuchnummer = (String)billing.getProperty("geschaeftsbuchnummer");
        boolean geschaeftsbuchnummerIsValid = false;
        if ((geschaeftsbuchnummer != null) && !geschaeftsbuchnummer.trim().equals("")) {
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
                amountWiederverkaeufeGBs.add(geschaeftsbuchnummer);
            }
            if (withCosts) {
                amountWithCostsWiederver++;
                earningsWithCostsWiederver += nettoSum;
            }
        }

        if (verwendungsKey.equals("VU aL")) {
            amountVUamtlicherLageplan++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUamtlicherLageplanGBs.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("VU hV")) {
            amountVUhoheitlicheVermessung++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUhoheitlicheVermessungGBs.add(geschaeftsbuchnummer);
            }
        } else if (verwendungsKey.equals("VU s")) {
            amountVUsonstige++;
            if (geschaeftsbuchnummerIsValid) {
                amountVUsonstigeGBs.add(geschaeftsbuchnummer);
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
