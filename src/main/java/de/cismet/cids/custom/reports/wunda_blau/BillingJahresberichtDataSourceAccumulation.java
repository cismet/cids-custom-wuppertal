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

import net.sf.jasperreports.engine.JRDataSource;

import java.util.Collection;

import de.cismet.cids.custom.wunda_blau.search.server.BillingJahresberichtReportServerSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BillingStatisticsReportServerSearch;

import de.cismet.cids.dynamics.CidsBean;

import static de.cismet.cids.custom.reports.wunda_blau.BillingStatisticsReport.joinCidsBeanIds;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BillingJahresberichtDataSourceAccumulation extends BillingStatisticsDataSourceAccumulation {

    //~ Instance fields --------------------------------------------------------

    private final int year;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingJahresberichtDataSourceAccumulation object.
     *
     * @param  billingBeans  DOCUMENT ME!
     * @param  year          DOCUMENT ME!
     */
    public BillingJahresberichtDataSourceAccumulation(final Collection<CidsBean> billingBeans, final int year) {
        super(billingBeans);

        this.year = year;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getKundenAbrechnungWiederverkaeufer() {
        return getResource(BillingJahresberichtReportServerSearch.KUNDEN_ABRECHNUNG_WIEDERVERKAEUFER);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlKundenInGruppe() {
        return getResource(BillingJahresberichtReportServerSearch.ANZAHL_KUNDEN);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenpflichtig() {
        return getResource(
                BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENPFLICHTIG);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenfrei() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENFREI);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenpflichtig() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENPFLICHTIG);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenfrei() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENFREI);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getSummeEinnahmenProVerwendungszweck() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_SUMME_EINNAHMEN);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlProVerwendungszweck() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_ANZAHL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlVermessungsunterlagenTs3() {
        return getResource(BillingJahresberichtReportServerSearch.ANZAHL_VERMESSUNGSUNTERLAGEN_TS3);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlVermessungsunterlagenTs4() {
        return getResource(BillingJahresberichtReportServerSearch.ANZAHL_VERMESSUNGSUNTERLAGEN_TS4);
    }

    @Override
    protected BillingStatisticsReportServerSearch createServerSearch() {
        return new BillingJahresberichtReportServerSearch(joinCidsBeanIds(billingBeans, ", "), year);
    }
}
