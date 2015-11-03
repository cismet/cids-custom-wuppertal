/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.navigator.ui.ComponentRegistry;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.tools.gui.downloadmanager.DownloadManager;
import de.cismet.tools.gui.downloadmanager.DownloadManagerDialog;

/**
 * Gets the needed information about the billing statistics report and generates a download for that report.
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingStatisticsReport {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_URL = "/de/cismet/cids/custom/reports/wunda_blau/geschaeftsstatisktik.jasper";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BillingStatisticsReport.class);

    //~ Instance fields --------------------------------------------------------

    protected Date from;
    protected Date till;
    protected int amountTotalDownloads;
    protected int amountWithCosts;
    protected int amountWithoutCosts;
    protected int amountVUamtlicherLageplan;
    protected int amountVUhoheitlicheVermessung;
    protected int amountVUsonstige;
    protected int amountVUamtlicherLageplanGB = 0;
    protected int amountVUhoheitlicheVermessungGB = 0;
    protected int amountVUsonstigeGB = 0;
    protected int amountWithCostsVU = 0;
    protected int amountWithCostsWiederver = 0;
    protected double earningsWithCostsVU = 0;
    protected double earningsWithCostsWiederver = 0;
    protected int amountWiederverkaeufe = 0;
    protected int amountWiederverkaeufeGB = 0;

    SwingWorker<JasperPrint, Void> downloadWorker;
    Collection<CidsBean> billingBeans;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingStatisticsReport object.
     *
     * @param  billingBeans                     DOCUMENT ME!
     * @param  from                             DOCUMENT ME!
     * @param  till                             DOCUMENT ME!
     * @param  amountTotalDownloads             DOCUMENT ME!
     * @param  amountWithCosts                  DOCUMENT ME!
     * @param  amountWithoutCosts               DOCUMENT ME!
     * @param  amountVUamtlicherLageplan        DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessung    DOCUMENT ME!
     * @param  amountVUsonstige                 DOCUMENT ME!
     * @param  amountVUamtlicherLageplanGB      DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessungGB  DOCUMENT ME!
     * @param  amountVUsonstigeGB               DOCUMENT ME!
     * @param  amountWithCostsVU                DOCUMENT ME!
     * @param  amountWithCostsWiederver         DOCUMENT ME!
     * @param  earningsWithCostsVU              DOCUMENT ME!
     * @param  earningsWithCostsWiederver       DOCUMENT ME!
     * @param  amountWiederverkaeufe            DOCUMENT ME!
     * @param  amountWiederverkaeufeGB          DOCUMENT ME!
     */
    public BillingStatisticsReport(final Collection<CidsBean> billingBeans,
            final Date from,
            final Date till,
            final int amountTotalDownloads,
            final int amountWithCosts,
            final int amountWithoutCosts,
            final int amountVUamtlicherLageplan,
            final int amountVUhoheitlicheVermessung,
            final int amountVUsonstige,
            final int amountVUamtlicherLageplanGB,
            final int amountVUhoheitlicheVermessungGB,
            final int amountVUsonstigeGB,
            final int amountWithCostsVU,
            final int amountWithCostsWiederver,
            final double earningsWithCostsVU,
            final double earningsWithCostsWiederver,
            final int amountWiederverkaeufe,
            final int amountWiederverkaeufeGB) {
        this.billingBeans = billingBeans;
        this.from = from;
        this.till = till;
        this.amountTotalDownloads = amountTotalDownloads;
        this.amountWithCosts = amountWithCosts;
        this.amountWithoutCosts = amountWithoutCosts;
        this.amountVUamtlicherLageplan = amountVUamtlicherLageplan;
        this.amountVUhoheitlicheVermessung = amountVUhoheitlicheVermessung;
        this.amountVUsonstige = amountVUsonstige;
        this.amountVUamtlicherLageplanGB = amountVUamtlicherLageplanGB;
        this.amountVUhoheitlicheVermessungGB = amountVUhoheitlicheVermessungGB;
        this.amountVUsonstigeGB = amountVUsonstigeGB;
        this.amountWithCostsVU = amountWithCostsVU;
        this.amountWithCostsWiederver = amountWithCostsWiederver;
        this.earningsWithCostsVU = earningsWithCostsVU;
        this.earningsWithCostsWiederver = earningsWithCostsWiederver;
        this.amountWiederverkaeufe = amountWiederverkaeufe;
        this.amountWiederverkaeufeGB = amountWiederverkaeufeGB;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getReportUrl() {
        return REPORT_URL;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getFilename() {
        return "buchungen_geschaeftsstatistik";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getTitle() {
        return "Buchungen: Geschäftsstatistik";
    }

    /**
     * DOCUMENT ME!
     */
    public void generateReport() {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    // this is only a dummy value, because returning null results in a blank report
                    final JRBeanCollectionDataSource beanArray = new JRBeanCollectionDataSource(billingBeans);
                    return beanArray;
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    return BillingStatisticsReport.this.generateParamters();
                }
            };

        if (DownloadManagerDialog.getInstance().showAskingForUserTitleDialog(
                        ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getInstance().getJobName();
            final String filename = getFilename();
            final String title = getTitle();

            DownloadManager.instance()
                    .add(new JasperReportDownload(
                            getReportUrl(),
                            parametersGenerator,
                            dataSourceGenerator,
                            jobname,
                            title,
                            filename));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected BillingStatisticsDataSourceAccumulation createDataSourceAccumulation() {
        final BillingStatisticsDataSourceAccumulation dataSourceAccumulation =
            new BillingStatisticsDataSourceAccumulation(billingBeans);
        dataSourceAccumulation.fetchSearchResults();
        return dataSourceAccumulation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    Map generateParamters() {
        final HashMap params = new HashMap();

        params.put("from", from);
        if (till == null) {
            params.put("till", from);
        } else {
            params.put("till", till);
        }

        params.put("dataSourceCollection", createDataSourceAccumulation());

        params.put("amountTotalDownloads", amountTotalDownloads);
        params.put("amountWithCosts", amountWithCosts);
        params.put("amountWithoutCosts", amountWithoutCosts);
        params.put("amountWithCostsVU", amountWithCostsVU);
        params.put("amountWithCostsWiederver", amountWithCostsWiederver);

        params.put("earningsWithCostsVU", earningsWithCostsVU);
        params.put("earningsWithCostsWiederver", earningsWithCostsWiederver);

        params.put("amountVUamtlicherLageplan", amountVUamtlicherLageplan);
        params.put("amountVUamtlicherLageplanGB", amountVUamtlicherLageplanGB);
        params.put("amountVUhoheitlicheVermessung", amountVUhoheitlicheVermessung);
        params.put("amountVUhoheitlicheVermessungGB", amountVUhoheitlicheVermessungGB);
        params.put("amountVUsonstige", amountVUsonstige);
        params.put("amountVUsonstigeGB", amountVUsonstigeGB);
        params.put("amountWiederverkaeufe", amountWiederverkaeufe);
        params.put("amountWiederverkaeufeGB", amountWiederverkaeufeGB);

        return params;
    }
    /**
     * DOCUMENT ME!
     *
     * @param   list         DOCUMENT ME!
     * @param   conjunction  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String joinCidsBeanIds(final Collection<CidsBean> list, final String conjunction) {
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final CidsBean item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item.getPrimaryKeyValue());
        }
        return sb.toString();
    }
}
