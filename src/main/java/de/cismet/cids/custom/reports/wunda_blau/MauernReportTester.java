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

import org.apache.commons.beanutils.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauernReportTester {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            MauernReportTester.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        final CidsBean[] beans = DevelopmentTools.createCidsBeansFromRMIConnectionOnLocalhost(
                "WUNDA_BLAU",
                "Administratoren",
                "admin",
                "kif",
                "mauer",
                1);

        System.out.println("Anzahl:" + beans.length);
        final MauernReportBean[] mauern = new MauernReportBean[beans.length];
        int i = 0;
        for (final CidsBean b : beans) {
            final MauernReportBean x = new MauernReportBean(b, false, ConnectionContext.createDeprecated());
            mauern[i++] = x;
            LOG.fatal(b.getMOString());
        }
//        final MauernReportBean[] mauern = new MauernReportBean[beans.length];
//        int i = 0;
//        for (final CidsBean b : beans) {
//            final MauernReportBean x = new MauernReportBean(b);
//            mauern[i++] = x;
//            LOG.fatal(b.getMOString());
//        }

        final ArrayList<String> fields = new ArrayList<String>(Arrays.asList(
                    "lagebezeichnung",
                    "mauer_nummer",
                    "lagebeschreibung",
                    "umgebung",
                    "neigung",
                    "stuetzmauertyp.name",
                    "materialtyp.name",
                    "hoehe_min",
                    "hoehe_max",
                    "laenge",
                    "eigentuemer.name",
                    "datum_erste_pruefung",
                    "datum_letzte_pruefung",
                    "datum_naechste_pruefung",
                    "art_erste_pruefung.name",
                    "art_letzte_pruefung.name",
                    "art_naechste_pruefung.name",
                    "beschreibung_gelaender",
                    "beschreibung_kopf",
                    "beschreibung_ansicht",
                    "beschreibung_gruendung",
                    "beschreibung_verformung",
                    "beschreibung_gelaende",
                    "zustand_gelaender",
                    "zustand_kopf",
                    "zustand_ansicht",
                    "zustand_gruendung",
                    "zustand_verformung",
                    "zustand_gelaende",
                    "san_massnahme_gelaender",
                    "san_massnahme_kopf",
                    "san_massnahme_ansicht",
                    "san_massnahme_gruendung",
                    "san_massnahme_verformung",
                    "san_massnahme_gelaende",
                    "san_kosten_gelaender",
                    "san_kosten_kopf",
                    "san_kosten_ansicht",
                    "san_kosten_gruendung",
                    "san_kosten_verformung",
                    "san_kosten_gelaende",
                    "san_eingriff_gelaender",
                    "san_eingriff_kopf",
                    "san_eingriff_ansicht",
                    "san_eingriff_gruendung",
                    "san_eingriff_verformung",
                    "san_eingriff_gelaende",
                    "standsicherheit.name",
                    "verkehrssicherheit.name",
                    "dauerhaftigkeit.name",
                    "besonderheiten"));

        for (final String pv : fields) {
            try {
                final Object out = BeanUtils.getProperty(beans[0], pv);
                System.out.println("<field name=\"mauer." + pv + "\" class=\"java.lang.String\"/>");
            } catch (Exception skip) {
                LOG.fatal(skip, skip);

                System.out.println("!" + pv + "-->Problem");
            }
        }

        boolean ready = false;
        do {
            ready = true;
            for (final MauernReportBean m : mauern) {
                if (!m.isReadyToProceed()) {
                    ready = false;
                    break;
                }
            }
        } while (!ready);

        DevelopmentTools.showReportForBeans(
            "/de/cismet/cids/custom/reports/wunda_blau/mauer-katasterblatt.jasper",
            Arrays.asList(mauern));
        System.out.println("alles fertich.ok");
    }
}
