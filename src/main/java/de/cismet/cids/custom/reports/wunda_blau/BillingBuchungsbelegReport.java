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
    private Collection<CidsBean> billingBeans_mwst0;
    private Collection<CidsBean> billingBeans_baulasten_mwst0;
    private Collection<CidsBean> billingBeans_kataster_mwst0;
    private Double mwst_0 = 0d;
    private BigDecimal netto_summe_0;
    private BigDecimal brutto_summe_0;
    private BigDecimal kataster_netto_summe_0;
    private BigDecimal kataster_brutto_summe_0;
    private BigDecimal baulasten_netto_summe_0;
    private BigDecimal baulasten_brutto_summe_0;
    private Collection<CidsBean> billingBeans_mwst19;
    private Double mwst_19 = 19d;
    private BigDecimal netto_summe_19;
    private BigDecimal brutto_summe_19;
    private Date from;
    private Date till;
    private BigDecimal mwstValue;
    private BigDecimal totalSum;
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
     * @param  kundeBean        DOCUMENT ME!
     * @param  mwst0            netto_summe_0 DOCUMENT ME!
     * @param  kataster_mwst0   DOCUMENT ME!
     * @param  baulasten_mwst0  DOCUMENT ME!
     * @param  mwst19           brutto_summe_0 DOCUMENT ME!
     * @param  from             DOCUMENT ME!
     * @param  till             DOCUMENT ME!
     * @param  mwstValue        DOCUMENT ME!
     * @param  totalSum         DOCUMENT ME!
     */
    public BillingBuchungsbelegReport(
            final CidsBean kundeBean,
            final Object[] mwst0,
            final Object[] kataster_mwst0,
            final Object[] baulasten_mwst0,
            final Object[] mwst19,
            final Date from,
            final Date till,
            final BigDecimal mwstValue,
            final BigDecimal totalSum) {
        this(
            kundeBean,
            mwst0,
            kataster_mwst0,
            baulasten_mwst0,
            mwst19,
            from,
            till,
            mwstValue,
            totalSum,
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
     * @param  mwst0                                   billingBeans_mwst0 DOCUMENT ME!
     * @param  kataster_mwst0                          DOCUMENT ME!
     * @param  baulasten_mwst0                         DOCUMENT ME!
     * @param  mwst19                                  netto_summe_0 DOCUMENT ME!
     * @param  from                                    DOCUMENT ME!
     * @param  till                                    DOCUMENT ME!
     * @param  mwstValue                               DOCUMENT ME!
     * @param  totalSum                                DOCUMENT ME!
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
            final Object[] mwst0,
            final Object[] kataster_mwst0,
            final Object[] baulasten_mwst0,
            final Object[] mwst19,
            final Date from,
            final Date till,
            final BigDecimal mwstValue,
            final BigDecimal totalSum,
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

        this.billingBeans_mwst0 = (mwst0[0] != null) ? (Collection)mwst0[0] : new ArrayList<CidsBean>();
        this.netto_summe_0 = (mwst0[1] != null) ? (BigDecimal)mwst0[1] : new BigDecimal("0.0");
        this.brutto_summe_0 = (mwst0[2] != null) ? (BigDecimal)mwst0[2] : new BigDecimal("0.0");

        this.billingBeans_kataster_mwst0 = (kataster_mwst0[0] != null) ? (Collection)kataster_mwst0[0]
                                                                       : new ArrayList<CidsBean>();
        this.kataster_netto_summe_0 = (kataster_mwst0[1] != null) ? (BigDecimal)kataster_mwst0[1]
                                                                  : new BigDecimal("0.0");
        this.kataster_brutto_summe_0 = (kataster_mwst0[2] != null) ? (BigDecimal)kataster_mwst0[2]
                                                                   : new BigDecimal("0.0");

        this.billingBeans_baulasten_mwst0 = (baulasten_mwst0[0] != null) ? (Collection)baulasten_mwst0[0]
                                                                         : new ArrayList<CidsBean>();
        this.baulasten_netto_summe_0 = (baulasten_mwst0[1] != null) ? (BigDecimal)baulasten_mwst0[1]
                                                                    : new BigDecimal("0.0");
        this.baulasten_brutto_summe_0 = (baulasten_mwst0[2] != null) ? (BigDecimal)baulasten_mwst0[2]
                                                                     : new BigDecimal("0.0");

        this.billingBeans_mwst19 = (mwst19[0] != null) ? (Collection)mwst19[0] : new ArrayList<CidsBean>();
        this.netto_summe_19 = (mwst19[1] != null) ? (BigDecimal)mwst19[1] : new BigDecimal("0.0");
        this.brutto_summe_19 = (mwst19[2] != null) ? (BigDecimal)mwst19[2] : new BigDecimal("0.0");

        this.from = from;
        this.till = till;

        this.mwstValue = mwstValue;
        this.totalSum = totalSum;

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
        params.put("billingBeans_mwst0", billingBeans_mwst0);
        params.put("billingBeans_kataster_mwst0", billingBeans_kataster_mwst0);
        params.put("billingBeans_baulasten_mwst0", billingBeans_baulasten_mwst0);

        params.put("mwst_0", mwst_0);
        params.put("mwst_19", mwst_19);

        params.put("netto_summe_0", netto_summe_0);
        params.put("brutto_summe_0", brutto_summe_0);

        params.put("kataster_netto_summe_0", kataster_netto_summe_0);
        params.put("kataster_brutto_summe_0", kataster_brutto_summe_0);

        params.put("baulasten_netto_summe_0", baulasten_netto_summe_0);
        params.put("baulasten_brutto_summe_0", baulasten_brutto_summe_0);

        params.put("billingBeans_mwst19", billingBeans_mwst19);
        params.put("netto_summe_19", netto_summe_19);
        params.put("brutto_summe_19", brutto_summe_19);

        params.put("from", from);
        if (till == null) {
            params.put("till", from);
        } else {
            params.put("till", till);
        }

        params.put("mwstValue", mwstValue);
        params.put("end_summe", totalSum);

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
