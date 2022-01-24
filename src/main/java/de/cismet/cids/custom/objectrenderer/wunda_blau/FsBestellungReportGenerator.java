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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.gui.printing.JasperReportDownload;

import de.cismet.tools.gui.downloadmanager.Download;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class FsBestellungReportGenerator {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PARAMETER_DATUM_HEUTE = "DATUM_HEUTE";
    private static final String PARAMETER_DATUM_EINGANG = "DATUM_EINGANG";
    private static final String PARAMETER_PRODUKTBEZEICHNUNG = "PRODUKTBEZEICHNUNG";
    private static final String PARAMETER_FLURSTUECKSKENNZEICHEN = "FLURSTUECKSKENNZEICHEN";
    private static final String PARAMETER_TRANSAKTIONSID = "TRANSAKTIONSID";

    private static final String PARAMETER_LIEFER_FIRMA = "LIEFER_FIRMA";
    private static final String PARAMETER_LIEFER_VORNAME = "LIEFER_VORNAME";
    private static final String PARAMETER_LIEFER_NAME = "LIEFER_NAME";
    private static final String PARAMETER_LIEFER_STRASSE = "LIEFER_STRASSE";
    private static final String PARAMETER_LIEFER_HAUSNUMMER = "LIEFER_HAUSNUMMER";
    private static final String PARAMETER_LIEFER_PLZ = "LIEFER_PLZ";
    private static final String PARAMETER_LIEFER_ORT = "LIEFER_ORT";
    private static final String PARAMETER_LIEFER_ALTERNATIV = "LIEFER_ALTERNATIV";
    private static final String PARAMETER_LIEFER_ADRESSE = "LIEFER_ADRESSE";

    private static final String PARAMETER_RECHNUNG_FIRMA = "RECHNUNG_FIRMA";
    private static final String PARAMETER_RECHNUNG_VORNAME = "RECHNUNG_VORNAME";
    private static final String PARAMETER_RECHNUNG_NAME = "RECHNUNG_NAME";
    private static final String PARAMETER_RECHNUNG_STRASSE = "RECHNUNG_STRASSE";
    private static final String PARAMETER_RECHNUNG_HAUSNUMMER = "RECHNUNG_HAUSNUMMER";
    private static final String PARAMETER_RECHNUNG_PLZ = "RECHNUNG_PLZ";
    private static final String PARAMETER_RECHNUNG_ORT = "RECHNUNG_ORT";
    private static final String PARAMETER_RECHNUNG_ALTERNATIV = "RECHNUNG_ALTERNATIV";
    private static final String PARAMETER_RECHNUNG_ADRESSE = "RECHNUNG_ADRESSE";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            FsBestellungReportGenerator.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   string  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String dashIfNull(final String string) {
        return (string != null) ? string : "-";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   string  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String emptyIfNull(final String string) {
        return (string != null) ? string : "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bestellungbean  DOCUMENT ME!
     * @param   jobname         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download createJasperDownload(final CidsBean bestellungbean, final String jobname) {
        final DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");

        final String vorgangsnummer = (bestellungbean.getProperty("transid") != null)
            ? ((String)bestellungbean.getProperty("transid")).substring("KFAS_KF600200-".length()) : null;
        final String landparcelcode = (String)bestellungbean.getProperty("landparcelcode");

        final JasperReportDownload.JasperReportDataSourceGenerator dataSourceGenerator =
            new JasperReportDownload.JasperReportDataSourceGenerator() {

                @Override
                public JRDataSource generateDataSource() {
                    try {
                        return new JRBeanCollectionDataSource(Arrays.asList(bestellungbean));
                    } catch (final Exception ex) {
                        LOG.warn(ex, ex);
                        return null;
                    }
                }
            };

        final JasperReportDownload.JasperReportParametersGenerator parametersGenerator =
            new JasperReportDownload.JasperReportParametersGenerator() {

                @Override
                public Map generateParamters() {
                    final String datumHeute = dashIfNull(dateformat.format(new Date()));
                    final String datumEingang = dashIfNull(dateformat.format(
                                (Timestamp)bestellungbean.getProperty("eingang_ts")));
                    final String transaktionsid = dashIfNull(vorgangsnummer);
                    final String flurstueckskennzeichen = dashIfNull(landparcelcode).replace(",", ", ");
                    final String produktbezeichnung = dashIfNull((String)bestellungbean.getProperty(
                                "fk_produkt.fk_typ.name"));
                    final String lieferFirma = emptyIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.firma"));
                    final String lieferVorname = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.vorname"));
                    final String lieferName = dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.name"));
                    final String lieferStrasse = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.strasse"));
                    final String lieferHausnummer = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.hausnummer"));
                    final String lieferPlz = dashIfNull((bestellungbean.getProperty("fk_adresse_versand.plz") != null)
                                ? Integer.toString((Integer)bestellungbean.getProperty("fk_adresse_versand.plz"))
                                : null);
                    final String lieferOrt = dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.ort"));
                    final String lieferStaat = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.staat"));
                    final String lieferAlternativ = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_versand.alternativ"));
                    final String lieferAdresse = (lieferAlternativ.equals("-"))
                        ? (lieferStrasse + " " + lieferHausnummer + "\n" + lieferPlz + " " + lieferOrt)
                        : (lieferAlternativ + "\n" + lieferStaat);
                    final String rechnungFirma = emptyIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.firma"));
                    final String rechnungVorname = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.vorname"));
                    final String rechnungName = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.name"));
                    final String rechnungStrasse = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.strasse"));
                    final String rechnungHausnummer = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.hausnummer"));
                    final String rechnungPlz = dashIfNull(
                            (bestellungbean.getProperty("fk_adresse_rechnung.plz") != null)
                                ? Integer.toString((Integer)bestellungbean.getProperty("fk_adresse_rechnung.plz"))
                                : null);
                    final String rechnungOrt = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.ort"));
                    final String rechnungStaat = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.staat"));
                    final String rechnungAlternativ = dashIfNull((String)bestellungbean.getProperty(
                                "fk_adresse_rechnung.alternativ"));
                    final String rechnungAdresse = (rechnungAlternativ.equals("-"))
                        ? (rechnungStrasse + " " + rechnungHausnummer + "\n" + rechnungPlz + " " + rechnungOrt)
                        : (rechnungAlternativ + "\n" + rechnungStaat);

                    final HashMap parameters = new HashMap();
                    parameters.put(PARAMETER_DATUM_HEUTE, datumHeute);
                    parameters.put(PARAMETER_DATUM_EINGANG, datumEingang);
                    parameters.put(PARAMETER_TRANSAKTIONSID, transaktionsid);
                    parameters.put(PARAMETER_FLURSTUECKSKENNZEICHEN, flurstueckskennzeichen);
                    parameters.put(PARAMETER_PRODUKTBEZEICHNUNG, produktbezeichnung);
                    parameters.put(PARAMETER_LIEFER_FIRMA, lieferFirma);
                    parameters.put(PARAMETER_LIEFER_VORNAME, lieferVorname);
                    parameters.put(PARAMETER_LIEFER_NAME, lieferName);
                    parameters.put(PARAMETER_LIEFER_STRASSE, lieferStrasse);
                    parameters.put(PARAMETER_LIEFER_HAUSNUMMER, lieferHausnummer);
                    parameters.put(PARAMETER_LIEFER_PLZ, lieferPlz);
                    parameters.put(PARAMETER_LIEFER_ORT, lieferOrt);
                    parameters.put(PARAMETER_LIEFER_ALTERNATIV, lieferAlternativ);
                    parameters.put(PARAMETER_LIEFER_ADRESSE, lieferAdresse);
                    parameters.put(PARAMETER_RECHNUNG_FIRMA, rechnungFirma);
                    parameters.put(PARAMETER_RECHNUNG_VORNAME, rechnungVorname);
                    parameters.put(PARAMETER_RECHNUNG_NAME, rechnungName);
                    parameters.put(PARAMETER_RECHNUNG_STRASSE, rechnungStrasse);
                    parameters.put(PARAMETER_RECHNUNG_HAUSNUMMER, rechnungHausnummer);
                    parameters.put(PARAMETER_RECHNUNG_PLZ, rechnungPlz);
                    parameters.put(PARAMETER_RECHNUNG_ORT, rechnungOrt);
                    parameters.put(PARAMETER_RECHNUNG_ALTERNATIV, rechnungAlternativ);
                    parameters.put(PARAMETER_RECHNUNG_ADRESSE, rechnungAdresse);
                    return parameters;
                }
            };

        final boolean isBaulasten = ((String)bestellungbean.getProperty("transid")).startsWith("KFAS_KF600202-")
                    || ((String)bestellungbean.getProperty("transid")).startsWith("KFAS_KF600203-");
        
        final boolean isLiegenschaftsbuch = ((String)bestellungbean.getProperty("transid")).startsWith("KFAS_KF600204-")
                    || ((String)bestellungbean.getProperty("transid")).startsWith("KFAS_KF600205-");        

        final JasperReportDownload download = new JasperReportDownload(
                isBaulasten ? "/de/cismet/cids/custom/wunda_blau/res/bestellung_baulasten.jasper"
                        : isLiegenschaftsbuch ? "/de/cismet/cids/custom/wunda_blau/res/bestellung_liegenschaftsbuch.jasper"
                            : "/de/cismet/cids/custom/wunda_blau/res/bestellung_produkt.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                "Produkt-Bestellung",
                "Bestellung_"
                        + vorgangsnummer);

        return download;
    }
}
