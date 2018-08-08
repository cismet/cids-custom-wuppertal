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
import java.util.Date;

import de.cismet.cids.custom.wunda_blau.search.server.BillingJahresberichtReportServerSearch;
import de.cismet.cids.custom.wunda_blau.search.server.BillingStatisticsReportServerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

import static de.cismet.cids.custom.reports.wunda_blau.BillingStatisticsReport.joinCidsBeanIds;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BillingJahresberichtDataSourceAccumulation extends BillingStatisticsDataSourceAccumulation {

    //~ Instance fields --------------------------------------------------------

    private final Date from;
    private final Date till;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BillingJahresberichtDataSourceAccumulation object.
     *
     * @param  billingBeans       DOCUMENT ME!
     * @param  from               year DOCUMENT ME!
     * @param  till               DOCUMENT ME!
     * @param  connectionContext  DOCUMENT ME!
     */
    public BillingJahresberichtDataSourceAccumulation(final Collection<CidsBean> billingBeans,
            final Date from,
            final Date till,
            final ConnectionContext connectionContext) {
        super(billingBeans, connectionContext);

        this.from = from;
        this.till = till;
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
                BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENPFLICHTIG_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenpflichtigBaulasten() {
        return getResource(
                BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENPFLICHTIG_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenpflichtigKummunal() {
        return getResource(
                BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENPFLICHTIG_KO);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenfrei() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENFREI_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenfreiBaulasten() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENFREI_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlGeschaeftsbuchnummernKostenfreiKommunal() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_GESCHAEFTSBUCHNUMMERN_KOSTENFREI_KO);
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenpflichtig() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENPFLICHTIG_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenpflichtigBaulasten() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENPFLICHTIG_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenpflichtigKommunal() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENPFLICHTIG_KO);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenfrei() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENFREI_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenfreiBaulasten() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENFREI_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlDownloadsKostenfreiKommunal() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_DOWNLOADS_KOSTENFREI_KO);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getSummeEinnahmenProVerwendungszweck() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_SUMME_EINNAHMEN_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getSummeEinnahmenProVerwendungszweckBaulasten() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_SUMME_EINNAHMEN_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getSummeEinnahmenProVerwendungszweckKommunal() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_SUMME_EINNAHMEN_KO);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlProVerwendungszweck() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_ANZAHL_LK);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlProVerwendungszweckBaulasten() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_ANZAHL_BL);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public JRDataSource getAnzahlProVerwendungszweckKommunal() {
        return getResource(BillingJahresberichtReportServerSearch.VERWENDUNGSZWECK_ANZAHL_KO);
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
        return new BillingJahresberichtReportServerSearch(joinCidsBeanIds(billingBeans, ", "), from, till);
    }
}
