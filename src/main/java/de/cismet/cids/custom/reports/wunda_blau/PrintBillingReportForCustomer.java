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

import Sirius.navigator.ui.ComponentRegistry;

import org.apache.log4j.Logger;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import org.openide.util.NbBundle;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.interaction.CismapBroker;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;

/**
 * PrintBillingReportForCustomer gets a Customer-CidsBean and evaluates it to generate a BillingBuchungsbelegReport.
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
    private int amountTotalDownloads = 0;
    private int amountWithCosts = 0;
    private int amountWithoutCosts = 0;
    private int amountVUamtlicherLageplan = 0;
    private int amountVUhoheitlicheVermessung = 0;
    private int amountVUsonstige = 0;
    private int amountEigenerGebrauch = 0;
    private int amountWiederverkauf = 0;
    private int amountEigenerGebrauchGebührenbefreit = 0;
    private final HashSet amountVUamtlicherLageplanGB = new HashSet();
    private final HashSet amountVUhoheitlicheVermessungGB = new HashSet();
    private final HashSet amountVUsonstigeGB = new HashSet();
    private final HashSet amountEigenerGebrauchGB = new HashSet();
    private final HashSet amountWiederverkaufGB = new HashSet();
    private final HashSet amountEigenerGebrauchGebührenbefreitGB = new HashSet();
    private final JPanel panel;
    private final boolean showBillingWithoutCostInReport;
    private DownloadFinishedObserver downloadFinishedObserver = new DownloadFinishedObserver();
    private final BillingDoneListener billingDoneListener;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StartReportForCustomer object.
     *
     * @param  kundeBean                       DOCUMENT ME!
     * @param  billingsBeans                   DOCUMENT ME!
     * @param  fromDate_tillDate               DOCUMENT ME!
     * @param  isRechnungsanlage               DOCUMENT ME!
     * @param  panel                           DOCUMENT ME!
     * @param  showBillingWithoutCostInReport  DOCUMENT ME!
     * @param  billingDoneListener             DOCUMENT ME!
     */
    public PrintBillingReportForCustomer(final CidsBean kundeBean,
            final Collection<CidsBean> billingsBeans,
            final Date[] fromDate_tillDate,
            final boolean isRechnungsanlage,
            final JPanel panel,
            final boolean showBillingWithoutCostInReport,
            final BillingDoneListener billingDoneListener) {
        this.kundeBean = kundeBean;
        this.fromDate_tillDate = fromDate_tillDate;
        this.isRechnungsanlage = isRechnungsanlage;
        this.billingsBeans = billingsBeans;
        this.panel = panel;
        this.showBillingWithoutCostInReport = showBillingWithoutCostInReport;
        totalSum = generateStatisticsForTheReport(billingsBeans);
        this.billingDoneListener = billingDoneListener;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void print() {
        final Object[] mwst0 = new Object[3];
        final Object[] kataster_mwst0 = new Object[3];
        final Object[] baulasten_mwst0 = new Object[3];
        final Object[] mwst19 = new Object[3];

        final Collection<CidsBean> filteredBuchungen_mwst0 = new ArrayList<CidsBean>();
        final Collection<CidsBean> filteredBuchungen_kataster_mwst0 = new ArrayList<CidsBean>();
        final Collection<CidsBean> filteredBuchungen_baulasten_mwst0 = new ArrayList<CidsBean>();
        boolean noData = true;
        if (billingInformation.containsKey(0d)) {
            final HashMap<String, Object> mwst_information_0 = billingInformation.get(0d);
            final Collection<CidsBean> billings = (Collection<CidsBean>)mwst_information_0.get("billings");
            filteredBuchungen_mwst0.addAll(billings);
            BigDecimal katasterNetto = new BigDecimal(0.0);
            BigDecimal katasterBrutto = new BigDecimal(0.0);
            BigDecimal baulastenNetto = new BigDecimal(0.0);
            BigDecimal baulastenBrutto = new BigDecimal(0.0);
            for (final CidsBean billing : billings) {
                if ("bla".equals(billing.getProperty("produktkey"))
                            || "blab_be".equals(billing.getProperty("produktkey"))) {
                    filteredBuchungen_baulasten_mwst0.add(billing);
                    baulastenNetto = baulastenNetto.add(new BigDecimal((Double)billing.getProperty("netto_summe")));
                    baulastenBrutto = baulastenBrutto.add(new BigDecimal((Double)billing.getProperty("brutto_summe")));
                } else {
                    filteredBuchungen_kataster_mwst0.add(billing);
                    katasterNetto = katasterNetto.add(new BigDecimal((Double)billing.getProperty("netto_summe")));
                    katasterBrutto = katasterBrutto.add(new BigDecimal((Double)billing.getProperty("brutto_summe")));
                }
            }
            mwst0[1] = (BigDecimal)mwst_information_0.get("netto_summe");
            mwst0[2] = (BigDecimal)mwst_information_0.get("brutto_summe");
            kataster_mwst0[1] = katasterNetto;
            kataster_mwst0[2] = katasterBrutto;
            baulasten_mwst0[1] = baulastenNetto;
            baulasten_mwst0[2] = baulastenBrutto;
            noData = false;
        }
        mwst0[0] = filteredBuchungen_mwst0;
        kataster_mwst0[0] = filteredBuchungen_kataster_mwst0;
        baulasten_mwst0[0] = filteredBuchungen_baulasten_mwst0;
        final Collection<CidsBean> filteredBuchungen_mwst19 = new ArrayList<CidsBean>();
        if (billingInformation.containsKey(19d)) {
            final HashMap<String, Object> mwst_information_19 = billingInformation.get(19d);
            filteredBuchungen_mwst19.addAll((Collection<CidsBean>)billingInformation.get(19d).get("billings"));
            mwst19[1] = (BigDecimal)mwst_information_19.get("netto_summe");
            mwst19[2] = (BigDecimal)mwst_information_19.get("brutto_summe");
            noData = false;
        }
        mwst19[0] = filteredBuchungen_mwst19;

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
                    mwst0,
                    kataster_mwst0,
                    baulasten_mwst0,
                    mwst19,
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
            report.setDownloadObserver(downloadFinishedObserver);
            report.generateReport();
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
            if ((mwst_satz_bean != null) && !mwst_satz_bean.equals(new Double(0))) {
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
        if (isRechnungsanlage) {
            final BillingBillDialog diag = new BillingBillDialog(ComponentRegistry.getRegistry().getNavigator());
            StaticSwingTools.showDialog(diag);
            if (Boolean.TRUE.equals(diag.isBilling())) {
                new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() throws Exception {
                            for (final CidsBean billing : billingsBeans) {
                                try {
                                    billing.setProperty("abrechnungsdatum", new Timestamp(new Date().getTime()));
                                    billing.setProperty("abgerechnet", Boolean.TRUE);
                                    billing.persist();
                                } catch (final Exception ex) {
                                    LOG.error("Error while setting value or persisting of billing.", ex);
                                    final org.jdesktop.swingx.error.ErrorInfo ei = new ErrorInfo(
                                            "Fehler beim Abrechnen",
                                            "Buchung konnte nicht auf abgerechnet gesetzt werden.",
                                            ex.getMessage(),
                                            null,
                                            ex,
                                            Level.ALL,
                                            null);
                                    SwingUtilities.invokeLater(new Runnable() {

                                            @Override
                                            public void run() {
                                                JXErrorPane.showDialog(
                                                    StaticSwingTools.getParentFrameIfNotNull(
                                                        CismapBroker.getInstance().getMappingComponent()),
                                                    ei);
                                            }
                                        });
                                }
                            }
                            billingDoneListener.billingDone(true);
                            return null;
                        }
                    }.execute();
            } else {
                billingDoneListener.billingDone(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadFinishedObserver  DOCUMENT ME!
     */
    public void setDownloadFinishedObserver(final DownloadFinishedObserver downloadFinishedObserver) {
        this.downloadFinishedObserver = downloadFinishedObserver;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public interface BillingDoneListener {

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param  isDone  DOCUMENT ME!
         */
        void billingDone(final boolean isDone);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * After the report has been created successfully then the billings are marked, if necessary, and some additional
     * functionality is done, if required.
     *
     * @version  $Revision$, $Date$
     */
    public class DownloadFinishedObserver implements Observer {

        //~ Methods ------------------------------------------------------------

        @Override
        public void update(final Observable o, final Object arg) {
            if (o instanceof JasperReportDownload) {
                final JasperReportDownload download = (JasperReportDownload)o;
                if (download.getStatus().equals(Download.State.COMPLETED)) {
                    markBillings();
                    additionalFunctionalityIfDownloadCompleted();
                }
            }
        }

        /**
         * This method can be overwritten so that some additional functionality can be executed after the successful
         * creation of the report. For example reload the billings in the table.
         */
        public void additionalFunctionalityIfDownloadCompleted() {
        }
    }
}
