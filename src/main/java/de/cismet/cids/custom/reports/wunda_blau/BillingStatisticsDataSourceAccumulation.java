/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.reports.wunda_blau;

import Sirius.navigator.connection.SessionManager;
import Sirius.navigator.exception.ConnectionException;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.Collection;
import java.util.HashMap;

import de.cismet.cids.custom.wunda_blau.search.server.BillingStatisticsReportServerSearch;

import de.cismet.cids.dynamics.CidsBean;

import static de.cismet.cids.custom.reports.wunda_blau.BillingStatisticsReport.joinCidsBeanIds;

/**
 * A accumulation of JRDataSource which is used by the statistics report to provide each of its subreports with the
 * correct JRDataSource. The needed data for the creation of the JRDataSources is fetched via a ServerSearch once,
 * although this ServerSearch uses multiple queries.
 *
 * @version  $Revision$, $Date$
 * @see      BillingStatisticsReportServerSearch
 */
public class BillingStatisticsDataSourceAccumulation {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            BillingStatisticsDataSourceAccumulation.class);

    //~ Instance fields --------------------------------------------------------

    protected final Collection<CidsBean> billingBeans;

    private HashMap<String, Collection> searchResults;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DataSourceCollection object.
     *
     * @param  billingBeans  DOCUMENT ME!
     */
    public BillingStatisticsDataSourceAccumulation(final Collection<CidsBean> billingBeans) {
        this.billingBeans = billingBeans;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getKundeBranche() {
        return getResource(BillingStatisticsReportServerSearch.BRANCHEN_AMOUNTS);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getKundenAntraege() {
        return getResource(BillingStatisticsReportServerSearch.ANTRAEGE_AMOUNTS);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloads() {
        return getResource(BillingStatisticsReportServerSearch.DOWNLOADS_AMOUNTS);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getKundenUmsatz() {
        return getResource(BillingStatisticsReportServerSearch.KUNDEN_UMSATZ);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getProdukteCommonDownloads() {
        return getResource(BillingStatisticsReportServerSearch.PRODUKTE_COMMON_DOWNLOADS);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getProdukteDownloads() {
        return getResource(BillingStatisticsReportServerSearch.PRODUKTE_DOWNLOADS);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getProdukteEinnahmen() {
        return getResource(BillingStatisticsReportServerSearch.PRODUKTE_EINNAHMEN);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getEinnahmen() {
        return getResource(BillingStatisticsReportServerSearch.EINNAHMEN);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected JRDataSource getResource(final String key) {
        return new JRBeanCollectionDataSource(searchResults.get(key),
                false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected BillingStatisticsReportServerSearch createServerSearch() {
        final String ids = joinCidsBeanIds(billingBeans, ", ");
        return new BillingStatisticsReportServerSearch(ids);
    }

    /**
     * Gets the data for the charts, if something goes wrong an empty HashMap is returned.
     */
    public void fetchSearchResults() {
        try {
            final BillingStatisticsReportServerSearch search = createServerSearch();
            final Collection searchResultsCol = SessionManager.getConnection()
                        .customServerSearch(SessionManager.getSession().getUser(), search);
            // get the HashMap from the search results, it is supposed that it is the only result.
            searchResults = (HashMap<String, Collection>)searchResultsCol.iterator().next();
        } catch (ConnectionException ex) {
            LOG.error("Could not fetch the data for the report.", ex);
            searchResults = null;
        }
    }
}
