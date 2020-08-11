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

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * Gets the needed information about the billing Buchungsbeleg (or Rechnungsanlage) report and generates a download for
 * that report.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingBuchungsbelegReport {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_BUCHUNGSBELEG_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/buchungsbeleg.jasper";
    private static final String REPORT_RECHNUNGSANLAGE_URL =
        "/de/cismet/cids/custom/reports/wunda_blau/rechnungsanlage.jasper";

    //~ Instance fields --------------------------------------------------------

    SwingWorker<JasperPrint, Void> downloadWorker;

    private CidsBean kundeBean;
    private Collection<CidsBean> billingBeansOhneMwst;
    private Collection<CidsBean> billingBeansBaulastenOhneMwst;
    private Collection<CidsBean> billingBeansKatasterOhneMwst;
    private BigDecimal nettoSummeOhneMwst;
    private BigDecimal bruttoSummeOhneMwst;
    private BigDecimal katasterNettoSummeOhneMwst;
    private BigDecimal katasterBruttoSummeOhneMwst;
    private BigDecimal baulastenNettoSummeOhneMwst;
    private BigDecimal baulastenBruttoSummeOhneMwst;
    private Collection<CidsBean> billingBeansMitMwst;
    private BigDecimal nettoSummeMitMwst;
    private BigDecimal bruttoSummeMitMwst;
    private BigDecimal nettoSummeGesamt;
    private BigDecimal bruttoSummeGesamt;
    private Date from;
    private Date till;
    private boolean isRechnungsanlage;
    private int amountTotalDownloads;
    private int amountWithCosts;
    private int amountWithoutCosts;
    private int amountVUamtlicherLageplan;
    private int amountVUhoheitlicheVermessung;
    private int amountVUsonstige;
    private int amountEigenerGebrauch = 0;
    private int amountWiederverkauf = 0;
    private int amountEigenerGebrauchGebührenbefreit = 0;
    private int amountVUamtlicherLageplanGB = 0;
    private int amountVUhoheitlicheVermessungGB = 0;
    private int amountVUsonstigeGB = 0;
    private int amountEigenerGebrauchGB = 0;
    private int amountWiederverkaufGB = 0;
    private int amountEigenerGebrauchGebührenbefreitGB = 0;
    private Observer downloadObserver;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingBuchungsbelegReport object.
     *
     * @param  kundeBean                  DOCUMENT ME!
     * @param  filteredBillingsOhneMwst   DOCUMENT ME!
     * @param  nettoOhneMwst              DOCUMENT ME!
     * @param  bruttoOhneMwst             DOCUMENT ME!
     * @param  katasterBillingsOhneMwst   DOCUMENT ME!
     * @param  katasterNettoOhneMwst      DOCUMENT ME!
     * @param  katasterBruttoOhneMwst     DOCUMENT ME!
     * @param  baulastenBillingsOhneMwst  DOCUMENT ME!
     * @param  baulastenNettoOhneMwst     DOCUMENT ME!
     * @param  baulastenBruttoOhneMwst    DOCUMENT ME!
     * @param  filteredBuchungenMitMwst   DOCUMENT ME!
     * @param  nettoMitMwst               DOCUMENT ME!
     * @param  bruttoMitMwst              DOCUMENT ME!
     * @param  nettoGesamt                totalSum DOCUMENT ME!
     * @param  bruttoGesamt               DOCUMENT ME!
     * @param  from                       DOCUMENT ME!
     * @param  till                       DOCUMENT ME!
     */
    public BillingBuchungsbelegReport(
            final CidsBean kundeBean,
            final Collection<CidsBean> filteredBillingsOhneMwst,
            final BigDecimal nettoOhneMwst,
            final BigDecimal bruttoOhneMwst,
            final Collection<CidsBean> katasterBillingsOhneMwst,
            final BigDecimal katasterNettoOhneMwst,
            final BigDecimal katasterBruttoOhneMwst,
            final Collection<CidsBean> baulastenBillingsOhneMwst,
            final BigDecimal baulastenNettoOhneMwst,
            final BigDecimal baulastenBruttoOhneMwst,
            final Collection<CidsBean> filteredBuchungenMitMwst,
            final BigDecimal nettoMitMwst,
            final BigDecimal bruttoMitMwst,
            final BigDecimal nettoGesamt,
            final BigDecimal bruttoGesamt,
            final Date from,
            final Date till) {
        this(
            kundeBean,
            filteredBillingsOhneMwst,
            nettoOhneMwst,
            bruttoOhneMwst,
            katasterBillingsOhneMwst,
            katasterNettoOhneMwst,
            katasterBruttoOhneMwst,
            baulastenBillingsOhneMwst,
            baulastenNettoOhneMwst,
            baulastenBruttoOhneMwst,
            filteredBuchungenMitMwst,
            nettoMitMwst,
            bruttoMitMwst,
            nettoGesamt,
            bruttoGesamt,
            from,
            till,
            false,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0);
    }
    /**
     * Creates a new BillingBuchungsbelegReport object.
     *
     * @param  kundeBean                               DOCUMENT ME!
     * @param  filteredBillingsOhneMwst                DOCUMENT ME!
     * @param  nettoOhneMwst                           DOCUMENT ME!
     * @param  bruttoOhneMwst                          DOCUMENT ME!
     * @param  katasterBillingsOhneMwst                DOCUMENT ME!
     * @param  katasterNettoOhneMwst                   DOCUMENT ME!
     * @param  katasterBruttoOhneMwst                  DOCUMENT ME!
     * @param  baulastenBillingsOhneMwst               DOCUMENT ME!
     * @param  baulastenNettoOhneMwst                  DOCUMENT ME!
     * @param  baulastenBruttoOhneMwst                 DOCUMENT ME!
     * @param  filteredBuchungenMitMwst                DOCUMENT ME!
     * @param  nettoMitMwst                            DOCUMENT ME!
     * @param  bruttoMitMwst                           DOCUMENT ME!
     * @param  nettoGesamt                             totalSum DOCUMENT ME!
     * @param  bruttoGesamt                            DOCUMENT ME!
     * @param  from                                    DOCUMENT ME!
     * @param  till                                    DOCUMENT ME!
     * @param  isRechnungsanlage                       DOCUMENT ME!
     * @param  amountTotalDownloads                    DOCUMENT ME!
     * @param  amountWithCosts                         DOCUMENT ME!
     * @param  amountWithoutCosts                      DOCUMENT ME!
     * @param  amountVUamtlicherLageplan               DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessung           DOCUMENT ME!
     * @param  amountVUsonstige                        DOCUMENT ME!
     * @param  amountEigenerGebrauch                   DOCUMENT ME!
     * @param  amountWiederverkauf                     DOCUMENT ME!
     * @param  amountEigenerGebrauchGebührenbefreit    DOCUMENT ME!
     * @param  amountVUamtlicherLageplanGB             DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessungGB         DOCUMENT ME!
     * @param  amountVUsonstigeGB                      DOCUMENT ME!
     * @param  amountEigenerGebrauchGB                 DOCUMENT ME!
     * @param  amountWiederverkaufGB                   DOCUMENT ME!
     * @param  amountEigenerGebrauchGebührenbefreitGB  DOCUMENT ME!
     */
    public BillingBuchungsbelegReport(
            final CidsBean kundeBean,
            final Collection<CidsBean> filteredBillingsOhneMwst,
            final BigDecimal nettoOhneMwst,
            final BigDecimal bruttoOhneMwst,
            final Collection<CidsBean> katasterBillingsOhneMwst,
            final BigDecimal katasterNettoOhneMwst,
            final BigDecimal katasterBruttoOhneMwst,
            final Collection<CidsBean> baulastenBillingsOhneMwst,
            final BigDecimal baulastenNettoOhneMwst,
            final BigDecimal baulastenBruttoOhneMwst,
            final Collection<CidsBean> filteredBuchungenMitMwst,
            final BigDecimal nettoMitMwst,
            final BigDecimal bruttoMitMwst,
            final BigDecimal nettoGesamt,
            final BigDecimal bruttoGesamt,
            final Date from,
            final Date till,
            final boolean isRechnungsanlage,
            final int amountTotalDownloads,
            final int amountWithCosts,
            final int amountWithoutCosts,
            final int amountVUamtlicherLageplan,
            final int amountVUhoheitlicheVermessung,
            final int amountVUsonstige,
            final int amountEigenerGebrauch,
            final int amountWiederverkauf,
            final int amountEigenerGebrauchGebührenbefreit,
            final int amountVUamtlicherLageplanGB,
            final int amountVUhoheitlicheVermessungGB,
            final int amountVUsonstigeGB,
            final int amountEigenerGebrauchGB,
            final int amountWiederverkaufGB,
            final int amountEigenerGebrauchGebührenbefreitGB) {
        // super(REPORT_URL, kundeBean);

        this.kundeBean = kundeBean;

        this.billingBeansOhneMwst = filteredBillingsOhneMwst;
        this.nettoSummeOhneMwst = nettoOhneMwst;
        this.bruttoSummeOhneMwst = bruttoOhneMwst;

        this.billingBeansKatasterOhneMwst = katasterBillingsOhneMwst;
        this.katasterNettoSummeOhneMwst = katasterNettoOhneMwst;
        this.katasterBruttoSummeOhneMwst = katasterBruttoOhneMwst;

        this.billingBeansBaulastenOhneMwst = baulastenBillingsOhneMwst;
        this.baulastenNettoSummeOhneMwst = baulastenNettoOhneMwst;
        this.baulastenBruttoSummeOhneMwst = baulastenBruttoOhneMwst;

        this.billingBeansMitMwst = filteredBuchungenMitMwst;
        this.nettoSummeMitMwst = nettoMitMwst;
        this.bruttoSummeMitMwst = bruttoMitMwst;

        this.nettoSummeGesamt = nettoGesamt;
        this.bruttoSummeGesamt = bruttoGesamt;

        this.from = from;
        this.till = till;

        this.isRechnungsanlage = isRechnungsanlage;

        this.amountTotalDownloads = amountTotalDownloads;
        this.amountWithCosts = amountWithCosts;
        this.amountWithoutCosts = amountWithoutCosts;
        this.amountVUamtlicherLageplan = amountVUamtlicherLageplan;
        this.amountVUhoheitlicheVermessung = amountVUhoheitlicheVermessung;
        this.amountVUsonstige = amountVUsonstige;
        this.amountEigenerGebrauch = amountEigenerGebrauch;
        this.amountWiederverkauf = amountWiederverkauf;
        this.amountEigenerGebrauchGebührenbefreit = amountEigenerGebrauchGebührenbefreit;
        this.amountVUamtlicherLageplanGB = amountVUamtlicherLageplanGB;
        this.amountVUhoheitlicheVermessungGB = amountVUhoheitlicheVermessungGB;
        this.amountVUsonstigeGB = amountVUsonstigeGB;
        this.amountEigenerGebrauchGB = amountEigenerGebrauchGB;
        this.amountWiederverkaufGB = amountWiederverkaufGB;
        this.amountEigenerGebrauchGebührenbefreitGB = amountEigenerGebrauchGebührenbefreitGB;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   current  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map generateReportParam(final CidsBean current) {
        final HashMap params = new HashMap();
        params.put("kundeBean", kundeBean);
        params.put("billingBeansOhneMwst", billingBeansOhneMwst);
        params.put("billingBeansKatasterOhneMwst", billingBeansKatasterOhneMwst);
        params.put("billingBeansBaulastenOhneMwst", billingBeansBaulastenOhneMwst);

        params.put("nettoSummeOhneMwst", nettoSummeOhneMwst);
        params.put("bruttoSummeOhneMwst", bruttoSummeOhneMwst);

        params.put("katasterNettoSummeOhneMwst", katasterNettoSummeOhneMwst);
        params.put("katasterBruttoSummeOhneMwst", katasterBruttoSummeOhneMwst);

        params.put("baulastenNettoSummeOhneMwst", baulastenNettoSummeOhneMwst);
        params.put("baulastenBruttoSummeOhneMwst", baulastenBruttoSummeOhneMwst);

        params.put("billingBeansMitMwst", billingBeansMitMwst);
        params.put("nettoSummeMitMwst", nettoSummeMitMwst);
        params.put("bruttoSummeMitMwst", bruttoSummeMitMwst);

        params.put("nettoSummeGesamt", nettoSummeGesamt);
        params.put("bruttoSummeGesamt", bruttoSummeGesamt);

        params.put("from", from);
        if (till == null) {
            params.put("till", from);
        } else {
            params.put("till", till);
        }

        params.put("isRechnungsanlage", isRechnungsanlage);

        params.put("amountTotalDownloads", amountTotalDownloads);
        params.put("amountWithCosts", amountWithCosts);
        params.put("amountWithoutCosts", amountWithoutCosts);
        params.put("amountVUamtlicherLageplan", amountVUamtlicherLageplan);
        params.put("amountVUhoheitlicheVermessung", amountVUhoheitlicheVermessung);
        params.put("amountVUsonstige", amountVUsonstige);
        params.put("amountEigenerGebrauch", amountEigenerGebrauch);
        params.put("amountWiederverkauf", amountWiederverkauf);
        params.put("amountEigenerGebrauchGebührenbefreit", amountEigenerGebrauchGebührenbefreit);

        params.put("amountVUamtlicherLageplanGB", amountVUamtlicherLageplanGB);
        params.put("amountVUhoheitlicheVermessungGB", amountVUhoheitlicheVermessungGB);
        params.put("amountVUsonstigeGB", amountVUsonstigeGB);
        params.put("amountEigenerGebrauchGB", amountEigenerGebrauchGB);
        params.put("amountWiederverkaufGB", amountWiederverkaufGB);
        params.put("amountEigenerGebrauchGebührenbefreitGB", amountEigenerGebrauchGebührenbefreitGB);

        return params;
    }

    /**
     * DOCUMENT ME!
     */
    public void generateReport() {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    final ArrayList beans = new ArrayList<CidsBean>();
                    beans.add(kundeBean);
                    final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(beans);
                    return beanArray;
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    return generateReportParam(kundeBean);
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            String filename;
            String title;
            String resourceName;
            if (isRechnungsanlage) {
                filename = "buchungen_rechnungsanlage";
                title = "Buchungen: Rechnungsanlage";
                resourceName = REPORT_RECHNUNGSANLAGE_URL;
            } else {
                filename = "buchungen_buchungsbeleg";
                title = "Buchungen: Buchungsbeleg";
                resourceName = REPORT_BUCHUNGSBELEG_URL;
            }

            // worst case: "_null" will be appended to the filename, but at least nothing will break
            filename += "_" + String.valueOf(kundeBean.getProperty("name_intern"));

            final JasperReportDownload download = new JasperReportDownload(
                    resourceName,
                    parametersGenerator,
                    dataSourceGenerator,
                    jobname,
                    title,
                    filename);
            download.addObserver(downloadObserver);
            DownloadManager.instance().add(download);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  downloadObserver  DOCUMENT ME!
     */
    public void setDownloadObserver(final Observer downloadObserver) {
        this.downloadObserver = downloadObserver;
    }
}
