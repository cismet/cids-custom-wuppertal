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

import de.cismet.connectioncontext.ConnectionContext;
import de.cismet.connectioncontext.ConnectionContextProvider;

import de.cismet.tools.gui.StaticSwingTools;
import de.cismet.tools.gui.downloadmanager.Download;

/**
 * PrintBillingReportForCustomer gets a Customer-CidsBean and evaluates it to generate a BillingBuchungsbelegReport.
 *
 * @version  $Revision$, $Date$
 */
public class PrintBillingReportForCustomer implements ConnectionContextProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PrintBillingReportForCustomer.class);

    //~ Instance fields --------------------------------------------------------

    private final CidsBean kundeBean;
    private final Date[] fromDate_tillDate;
    private final Collection<CidsBean> billingsBeans;
    private final boolean isRechnungsanlage;
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
    private final ConnectionContext connectionContext;

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
     * @param  connectionContext               DOCUMENT ME!
     */
    public PrintBillingReportForCustomer(final CidsBean kundeBean,
            final Collection<CidsBean> billingsBeans,
            final Date[] fromDate_tillDate,
            final boolean isRechnungsanlage,
            final JPanel panel,
            final boolean showBillingWithoutCostInReport,
            final BillingDoneListener billingDoneListener,
            final ConnectionContext connectionContext) {
        this.kundeBean = kundeBean;
        this.fromDate_tillDate = fromDate_tillDate;
        this.isRechnungsanlage = isRechnungsanlage;
        this.billingsBeans = billingsBeans;
        this.panel = panel;
        this.showBillingWithoutCostInReport = showBillingWithoutCostInReport;
        this.billingDoneListener = billingDoneListener;
        this.connectionContext = connectionContext;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    public void print() {
        BigDecimal bruttoGesamt = new BigDecimal(0.0);
        BigDecimal nettoGesamt = new BigDecimal(0.0);
        BigDecimal katasterNetto = new BigDecimal(0.0);
        BigDecimal katasterBrutto = new BigDecimal(0.0);
        BigDecimal baulastenNetto = new BigDecimal(0.0);
        BigDecimal baulastenBrutto = new BigDecimal(0.0);
        BigDecimal nettoMitMwst = new BigDecimal(0.0);
        BigDecimal bruttoMitMwst = new BigDecimal(0.0);
        BigDecimal nettoOhneMwst = new BigDecimal(0.0);
        BigDecimal bruttoOhneMwst = new BigDecimal(0.0);

        final Collection<CidsBean> unfilteredBillings = new ArrayList<>();
        final Collection<CidsBean> filteredBillingsOhneMwst = new ArrayList<>();
        final Collection<CidsBean> filteredBillingsKatasterOhneMwst = new ArrayList<>();
        final Collection<CidsBean> filteredBillingsBaulastenOhneMwst = new ArrayList<>();
        final Collection<CidsBean> filteredBillingsMitMwst = new ArrayList<>();

        // calculate the netto sum of every mwst_satz
        unfilteredBillings.addAll(billingsBeans);
        for (final CidsBean billing : unfilteredBillings) {
            final Double nettoSumme = (Double)billing.getProperty("netto_summe");
            final Double bruttoSumme = (Double)billing.getProperty("brutto_summe");
            final Double mwstSatz = (Double)billing.getProperty("mwst_satz");

            if (showBillingWithoutCostInReport || (nettoSumme > 0)) {
                nettoGesamt = (nettoSumme != null) ? nettoGesamt.add(new BigDecimal(nettoSumme)) : nettoOhneMwst;
                bruttoGesamt = (bruttoSumme != null) ? bruttoGesamt.add(new BigDecimal(bruttoSumme)) : bruttoOhneMwst;

                // add the billing and sums to the right MwSt-category
                final boolean mitMwst = (mwstSatz != null) && !mwstSatz.equals(new Double(0));
                if (mitMwst) {
                    filteredBillingsMitMwst.add(billing);
                    nettoMitMwst = (nettoSumme != null) ? nettoMitMwst.add(new BigDecimal(nettoSumme)) : nettoMitMwst;
                    bruttoMitMwst = (bruttoSumme != null) ? bruttoMitMwst.add(new BigDecimal(bruttoSumme))
                                                          : bruttoMitMwst;
                } else {
                    filteredBillingsOhneMwst.add(billing);
                    nettoOhneMwst = (nettoSumme != null) ? nettoOhneMwst.add(new BigDecimal(nettoSumme))
                                                         : nettoOhneMwst;
                    bruttoOhneMwst = (bruttoSumme != null) ? bruttoOhneMwst.add(new BigDecimal(bruttoSumme))
                                                           : bruttoOhneMwst;
                }
            }

            setCountersDependingOnVerwendungszweck(billing);
        }

        for (final CidsBean billing : filteredBillingsOhneMwst) {
            final boolean istBaulast = "bla".equals(billing.getProperty("produktkey"))
                        || "blab_be".equals(billing.getProperty("produktkey"));
            if (istBaulast) {
                filteredBillingsBaulastenOhneMwst.add(billing);
                baulastenNetto = baulastenNetto.add(new BigDecimal((Double)billing.getProperty("netto_summe")));
                baulastenBrutto = baulastenBrutto.add(new BigDecimal((Double)billing.getProperty("brutto_summe")));
            } else {
                filteredBillingsKatasterOhneMwst.add(billing);
                katasterNetto = katasterNetto.add(new BigDecimal((Double)billing.getProperty("netto_summe")));
                katasterBrutto = katasterBrutto.add(new BigDecimal((Double)billing.getProperty("brutto_summe")));
            }
        }

        final boolean noData = filteredBillingsOhneMwst.isEmpty()
                    && filteredBillingsKatasterOhneMwst.isEmpty()
                    && filteredBillingsBaulastenOhneMwst.isEmpty()
                    && filteredBillingsMitMwst.isEmpty();

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
                    filteredBillingsOhneMwst,
                    nettoOhneMwst,
                    bruttoOhneMwst,
                    filteredBillingsKatasterOhneMwst,
                    katasterNetto,
                    katasterBrutto,
                    filteredBillingsBaulastenOhneMwst,
                    baulastenNetto,
                    baulastenBrutto,
                    filteredBillingsMitMwst,
                    nettoMitMwst,
                    bruttoMitMwst,
                    nettoGesamt,
                    bruttoGesamt,
                    fromDate_tillDate[0],
                    fromDate_tillDate[1],
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
                                    billing.persist(getConnectionContext());
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

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
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
