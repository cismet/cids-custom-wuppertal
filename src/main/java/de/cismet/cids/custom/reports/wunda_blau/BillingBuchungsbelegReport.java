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
import java.util.Map;

import de.cismet.cids.custom.objectrenderer.utils.AbstractJasperReportPrint;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingBuchungsbelegReport extends AbstractJasperReportPrint {

    //~ Static fields/initializers ---------------------------------------------

    private static final String REPORT_URL = "/de/cismet/cids/custom/reports/wunda_blau/buchungsbeleg.jasper";

    //~ Instance fields --------------------------------------------------------

    private CidsBean kundeBean;
    private Collection<CidsBean> billingBeans_mwst0;
    private Double mwst_0 = 0d;
    private BigDecimal netto_summe_0;
    private BigDecimal brutto_summe_0;
    private Collection<CidsBean> billingBeans_mwst19;
    private Double mwst_19 = 19d;
    private BigDecimal netto_summe_19;
    private BigDecimal brutto_summe_19;
    private Date from;
    private Date till;
    private BigDecimal totalSum;
    private boolean isRechnungsanlage;
    private int amountTotalDownloads;
    private int amountWithCosts;
    private int amountWithoutCosts;
    private int amountVUamtlicherLageplan;
    private int amountVUhoheitlicheVermessung;
    private int amountVUsonstige;
    private int amountVUamtlicherLageplanGB = 0;
    private int amountVUhoheitlicheVermessungGB = 0;
    private int amountVUsonstigeGB = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingBuchungsbelegReport object.
     *
     * @param  kundeBean            DOCUMENT ME!
     * @param  billingBeans_mwst0   DOCUMENT ME!
     * @param  netto_summe_0        DOCUMENT ME!
     * @param  brutto_summe_0       DOCUMENT ME!
     * @param  billingBeans_mwst19  DOCUMENT ME!
     * @param  netto_summe_19       DOCUMENT ME!
     * @param  brutto_summe_19      DOCUMENT ME!
     * @param  from                 DOCUMENT ME!
     * @param  till                 DOCUMENT ME!
     * @param  totalSum             DOCUMENT ME!
     */
    public BillingBuchungsbelegReport(
            final CidsBean kundeBean,
            final Collection<CidsBean> billingBeans_mwst0,
            final BigDecimal netto_summe_0,
            final BigDecimal brutto_summe_0,
            final Collection<CidsBean> billingBeans_mwst19,
            final BigDecimal netto_summe_19,
            final BigDecimal brutto_summe_19,
            final Date from,
            final Date till,
            final BigDecimal totalSum) {
        this(
            kundeBean,
            billingBeans_mwst0,
            netto_summe_0,
            brutto_summe_0,
            billingBeans_mwst19,
            netto_summe_19,
            brutto_summe_19,
            from,
            till,
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
            0);
    }
    /**
     * Creates a new BillingBuchungsbelegReport object.
     *
     * @param  kundeBean                        DOCUMENT ME!
     * @param  billingBeans_mwst0               DOCUMENT ME!
     * @param  netto_summe_0                    DOCUMENT ME!
     * @param  brutto_summe_0                   DOCUMENT ME!
     * @param  billingBeans_mwst19              DOCUMENT ME!
     * @param  netto_summe_19                   DOCUMENT ME!
     * @param  brutto_summe_19                  DOCUMENT ME!
     * @param  from                             DOCUMENT ME!
     * @param  till                             DOCUMENT ME!
     * @param  totalSum                         DOCUMENT ME!
     * @param  isRechnungsanlage                DOCUMENT ME!
     * @param  amountTotalDownloads             DOCUMENT ME!
     * @param  amountWithCosts                  DOCUMENT ME!
     * @param  amountWithoutCosts               DOCUMENT ME!
     * @param  amountVUamtlicherLageplan        DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessung    DOCUMENT ME!
     * @param  amountVUsonstige                 DOCUMENT ME!
     * @param  amountVUamtlicherLageplanGB      DOCUMENT ME!
     * @param  amountVUhoheitlicheVermessungGB  DOCUMENT ME!
     * @param  amountVUsonstigeGB               DOCUMENT ME!
     */
    public BillingBuchungsbelegReport(
            final CidsBean kundeBean,
            final Collection<CidsBean> billingBeans_mwst0,
            final BigDecimal netto_summe_0,
            final BigDecimal brutto_summe_0,
            final Collection<CidsBean> billingBeans_mwst19,
            final BigDecimal netto_summe_19,
            final BigDecimal brutto_summe_19,
            final Date from,
            final Date till,
            final BigDecimal totalSum,
            final boolean isRechnungsanlage,
            final int amountTotalDownloads,
            final int amountWithCosts,
            final int amountWithoutCosts,
            final int amountVUamtlicherLageplan,
            final int amountVUhoheitlicheVermessung,
            final int amountVUsonstige,
            final int amountVUamtlicherLageplanGB,
            final int amountVUhoheitlicheVermessungGB,
            final int amountVUsonstigeGB) {
        super(REPORT_URL, kundeBean);

        this.kundeBean = kundeBean;
        if (billingBeans_mwst0 == null) {
            this.billingBeans_mwst0 = new ArrayList<CidsBean>();
            this.netto_summe_0 = new BigDecimal("0.0");
            this.brutto_summe_0 = new BigDecimal("0.0");
        } else {
            this.billingBeans_mwst0 = billingBeans_mwst0;
            this.netto_summe_0 = netto_summe_0;
            this.brutto_summe_0 = brutto_summe_0;
        }

        if (billingBeans_mwst19 == null) {
            this.billingBeans_mwst19 = new ArrayList<CidsBean>();
            this.netto_summe_19 = new BigDecimal("0.0");
            this.brutto_summe_19 = new BigDecimal("0.0");
        } else {
            this.billingBeans_mwst19 = billingBeans_mwst19;
            this.netto_summe_19 = netto_summe_19;
            this.brutto_summe_19 = brutto_summe_19;
        }
        this.from = from;
        this.till = till;

        this.totalSum = totalSum;

        this.isRechnungsanlage = isRechnungsanlage;

        this.amountTotalDownloads = amountTotalDownloads;
        this.amountWithCosts = amountWithCosts;
        this.amountWithoutCosts = amountWithoutCosts;
        this.amountVUamtlicherLageplan = amountVUamtlicherLageplan;
        this.amountVUhoheitlicheVermessung = amountVUhoheitlicheVermessung;
        this.amountVUsonstige = amountVUsonstige;
        this.amountVUamtlicherLageplanGB = amountVUamtlicherLageplanGB;
        this.amountVUhoheitlicheVermessungGB = amountVUhoheitlicheVermessungGB;
        this.amountVUsonstigeGB = amountVUsonstigeGB;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Map generateReportParam(final CidsBean current) {
        final HashMap params = new HashMap();
        params.put("kundeBean", kundeBean);
        params.put("billingBeans_mwst0", billingBeans_mwst0);
        params.put("mwst_0", mwst_0);
        params.put("netto_summe_0", netto_summe_0);
        params.put("brutto_summe_0", brutto_summe_0);

        params.put("billingBeans_mwst19", billingBeans_mwst19);
        params.put("mwst_19", mwst_19);
        params.put("netto_summe_19", netto_summe_19);
        params.put("brutto_summe_19", brutto_summe_19);

        params.put("from", from);
        if (till == null) {
            params.put("till", from);
        } else {
            params.put("till", till);
        }

        params.put("end_summe", totalSum);

        params.put("isRechnungsanlage", isRechnungsanlage);

        params.put("amountTotalDownloads", amountTotalDownloads);
        params.put("amountWithCosts", amountWithCosts);
        params.put("amountWithoutCosts", amountWithoutCosts);
        params.put("amountVUamtlicherLageplan", amountVUamtlicherLageplan);
        params.put("amountVUhoheitlicheVermessung", amountVUhoheitlicheVermessung);
        params.put("amountVUsonstige", amountVUsonstige);

        params.put("amountVUamtlicherLageplanGB", amountVUamtlicherLageplanGB);
        params.put("amountVUhoheitlicheVermessungGB", amountVUhoheitlicheVermessungGB);
        params.put("amountVUsonstigeGB", amountVUsonstigeGB);

        return params;
    }

    @Override
    public Map generateReportParam(final Collection<CidsBean> beans) {
        return generateReportParam(kundeBean);
    }
}
