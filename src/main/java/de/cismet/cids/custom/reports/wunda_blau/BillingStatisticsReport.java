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

    //~ Instance fields --------------------------------------------------------

    SwingWorker<JasperPrint, Void> downloadWorker;

    private Date from;
    private Date till;
    private int amountTotalDownloads;
    private int amountWithCosts;
    private int amountWithoutCosts;
    private int amountVUamtlicherLageplan;
    private int amountVUhoheitlicheVermessung;
    private int amountVUsonstige;
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
     * Creates a new BillingStatisticsReport object.
     *
     * @param  beans                            DOCUMENT ME!
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
    public BillingStatisticsReport(final Collection<CidsBean> beans,
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
     */
    public void generateReport() {
        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    return null;
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    final HashMap params = new HashMap();

                    params.put("from", from);
                    if (till == null) {
                        params.put("till", from);
                    } else {
                        params.put("till", till);
                    }

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
            };

        if (DownloadManagerDialog.showAskingForUserTitle(ComponentRegistry.getRegistry().getMainWindow())) {
            final String jobname = DownloadManagerDialog.getJobname();
            final String filename = "buchungen_geschaeftsstatistik";
            final String title = "Buchungen: Geschäftsstatistik";

            DownloadManager.instance()
                    .add(new JasperReportDownload(
                            REPORT_URL,
                            parametersGenerator,
                            dataSourceGenerator,
                            jobname,
                            title,
                            filename));
        }
    }
}
