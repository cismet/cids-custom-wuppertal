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

    private static final String PARAMETER_RECHNUNG_FIRMA = "RECHNUNG_FIRMA";
    private static final String PARAMETER_RECHNUNG_VORNAME = "RECHNUNG_VORNAME";
    private static final String PARAMETER_RECHNUNG_NAME = "RECHNUNG_NAME";
    private static final String PARAMETER_RECHNUNG_STRASSE = "RECHNUNG_STRASSE";
    private static final String PARAMETER_RECHNUNG_HAUSNUMMER = "RECHNUNG_HAUSNUMMER";
    private static final String PARAMETER_RECHNUNG_PLZ = "RECHNUNG_PLZ";
    private static final String PARAMETER_RECHNUNG_ORT = "RECHNUNG_ORT";

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
     * @param   bestellungbean  DOCUMENT ME!
     * @param   jobname         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static Download createJasperDownload(final CidsBean bestellungbean, final String jobname) {
        final DateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy");

        final String vorgangsnummer = (bestellungbean.getProperty("transid") != null)
            ? ((String)bestellungbean.getProperty("transid")).substring("AS_KF600200-".length()) : null;
        final String flurstuecksKennzeichen = (String)bestellungbean.getProperty("landparcelcode");

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
                    final HashMap parameters = new HashMap();
                    parameters.put(PARAMETER_DATUM_HEUTE,
                        dashIfNull(dateformat.format(new Date())));
                    parameters.put(
                        PARAMETER_DATUM_EINGANG,
                        dashIfNull(dateformat.format((Timestamp)bestellungbean.getProperty("eingang_ts"))));
                    parameters.put(PARAMETER_TRANSAKTIONSID, dashIfNull(vorgangsnummer));
                    parameters.put(
                        PARAMETER_FLURSTUECKSKENNZEICHEN,
                        dashIfNull(flurstuecksKennzeichen));
                    parameters.put(
                        PARAMETER_PRODUKTBEZEICHNUNG,
                        dashIfNull((String)bestellungbean.getProperty("fk_produkt.fk_typ.name")));

                    parameters.put(
                        PARAMETER_LIEFER_FIRMA,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.firma")));
                    parameters.put(
                        PARAMETER_LIEFER_VORNAME,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.vorname")));
                    parameters.put(
                        PARAMETER_LIEFER_NAME,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.name")));
                    parameters.put(
                        PARAMETER_LIEFER_STRASSE,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.strasse")));
                    parameters.put(
                        PARAMETER_LIEFER_HAUSNUMMER,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.hausnummer")));
                    parameters.put(
                        PARAMETER_LIEFER_PLZ,
                        dashIfNull(
                            (bestellungbean.getProperty("fk_adresse_versand.plz") != null)
                                ? Integer.toString((Integer)bestellungbean.getProperty("fk_adresse_versand.plz"))
                                : null));
                    parameters.put(
                        PARAMETER_LIEFER_ORT,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_versand.ort")));

                    parameters.put(
                        PARAMETER_RECHNUNG_FIRMA,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.firma")));
                    parameters.put(
                        PARAMETER_RECHNUNG_VORNAME,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.vorname")));
                    parameters.put(
                        PARAMETER_RECHNUNG_NAME,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.name")));
                    parameters.put(
                        PARAMETER_RECHNUNG_STRASSE,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.strasse")));
                    parameters.put(
                        PARAMETER_RECHNUNG_HAUSNUMMER,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.hausnummer")));
                    parameters.put(
                        PARAMETER_RECHNUNG_PLZ,
                        dashIfNull(
                            (bestellungbean.getProperty("fk_adresse_rechnung.plz") != null)
                                ? Integer.toString((Integer)bestellungbean.getProperty("fk_adresse_rechnung.plz"))
                                : null));
                    parameters.put(
                        PARAMETER_RECHNUNG_ORT,
                        dashIfNull((String)bestellungbean.getProperty("fk_adresse_rechnung.ort")));
                    return parameters;
                }
            };

        final JasperReportDownload download = new JasperReportDownload(
                "/de/cismet/cids/custom/wunda_blau/res/bestellung_produkt.jasper",
                parametersGenerator,
                dataSourceGenerator,
                jobname,
                "Produkt-Bestelllung",
                "Bestellung_"
                        + vorgangsnummer);

        return download;
    }
}
