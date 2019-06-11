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
package de.cismet.cids.custom.utils;

import com.lowagie.text.Font;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Image;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JLabel;

import de.cismet.cids.custom.objecteditors.wunda_blau.PfPotenzialflaecheEditor;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cismap.commons.HeadlessMapProvider;
import de.cismet.cismap.commons.XBoundingBox;
import de.cismet.cismap.commons.features.DefaultStyledFeature;
import de.cismet.cismap.commons.gui.printing.JasperReportDownload;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWMS;
import de.cismet.cismap.commons.raster.wms.simple.SimpleWmsGetMapUrl;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class PotenzialFlaechenPrintHelper {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger LOG = Logger.getLogger(PotenzialFlaechenPrintHelper.class);
    private static final String PROP_BEZEICHNUNG = "bezeichnung";
    private static final String PROP_NUMMER = "nummer";
    private static final String PROP_BESCHREIBUNG_FLAECHE = "beschreibung_flaeche";
    private static final String PROP_NOTWENDIGE_MASSNAHMEN = "notwendige_massnahmen";
    private static final String PROP_QUELLE = "quelle";
    private static final String PROP_STAND = "stand";
    private static final String PROP_STADTBEZIRK = "stadtbezirk";
    private static final String PROP_GROESSE = "groesse";
    private static final String PROP_EIGENTUEMER = "eigentuemer";
    private static final String PROP_REGIONALPLAN = "regionalplan";
    private static final String PROP_FLAECHENNUTZUNGSPLAN = "flaechennutzungsplan";
    private static final String PROP_BEBAUUNGSPLAN = "bebauungsplan";
    private static final String PROP_WBPF_NUMMER = "wbpf_nummer";
    private static final String PROP_BK_GEWERBE_INDUSTRIE = "bk_gewerbe_industrie";
    private static final String PROP_BK_MILITAER = "bk_militaer";
    private static final String PROP_BK_VERKEHR = "bk_verkehr";
    private static final String PROP_BK_INFRASTRUKTUR_SOZIAL = "bk_infrastruktur_sozial";
    private static final String PROP_BK_INFRASTRUKTUR_TECHNISCH = "bk_infrastruktur_technisch";
    private static final String PROP_BK_EINZELHANDEL = "bk_einzelhandel";
    private static final String PROP_BK_NUTZUNGSAUFGABE = "bk_jahr_nutzungsaufgabe";
    private static final String PROP_GEWERBE_PRODUKTIONSORIENTIERT = "gnn_gewerbe_produktorientiert";
    private static final String PROP_GNN_GEWERBE_DIENSTLEISTUNG = "gnn_gewerbe_dienstleistung";
    private static final String PROP_GNN_WOHNEN = "gnn_wohnen";
    private static final String PROP_GNN_FREIRAUM = "gnn_freiraum";
    private static final String PROP_GNN_FREIZEIT = "gnn_freizeit";
    private static final String PROP_GNN_EINZELHANDEL = "gnn_einzelhandel";
    private static final String PROP_GNN_SONSTIGES = "gnn_sonstiges";
    private static final String PROP_LAGETYP = "lagetyp";
    private static final String PROP_BISHERIGE_NUTZUNG = "bisherige_nutzung";
    private static final String PROP_VORHANDENE_BEBAUUNG = "vorhandene_bebauung";
    private static final String PROP_UMGEBUNGSNUTZUNG = "umgebungsnutzung";
    private static final String PROP_TOPOGRAFIE = "topografie";
    private static final String PROP_RESTRIKTIONEN = "restriktionen";
    private static final String PROP_AEUSSERE_ERSCHLIESSUNG = "aeussere_erschliessung";
    private static final String PROP_INNERE_ERSCHLIESSUNG = "innere_erschliessung";
    private static final String PROP_OEPNV_ANBINDUNG = "oepnv_anbindung";
    private static final String PROP_ZENTRENNAEHE = "zentrennaehe";
    private static final String PROP_VERFUEGBARKEIT = "verfuegbarkeit";
    private static final String PROP_ART_DER_NUTZUNG = "art_der_nutzung";
    private static final String PROP_REVITALISIERUNG = "revitalisierung";
    private static final String PROP_ENTWICKLUNGSAUSSSICHTEN = "entwicklungsaussichten";
    private static final String PROP_HANDLUNGSDRUCK = "handlungsdruck";
    private static final String PROP_AKTIVIERBARKEIT = "aktivierbarkeit";
    private static final String PROP_ZENTRALER_VERSORGUNGSBEREICH = "zentraler_versorgungsbereich";
    private static final String PROP_WBPF_NACHFOLGENUTZUNG = "wbpf_nachfolgenutzung";
    public static final Map<String, ReportProperty> REPORT_PROPERTY_MAP = new HashMap<String, ReportProperty>();

    static {
        REPORT_PROPERTY_MAP.put(PROP_BEZEICHNUNG, new StringReportProperty(PROP_BEZEICHNUNG, "lblBezeichnung"));
        REPORT_PROPERTY_MAP.put(PROP_NUMMER, new StringReportProperty(PROP_NUMMER, "lblNummer"));
        REPORT_PROPERTY_MAP.put(
            PROP_BESCHREIBUNG_FLAECHE,
            new StringReportProperty(PROP_BESCHREIBUNG_FLAECHE, "lblFlaeche"));
        REPORT_PROPERTY_MAP.put(
            PROP_NOTWENDIGE_MASSNAHMEN,
            new StringReportProperty(PROP_NOTWENDIGE_MASSNAHMEN, "lblNaechsteSchritte"));
        REPORT_PROPERTY_MAP.put(PROP_QUELLE, new StringReportProperty(PROP_QUELLE, "lblQuelle"));
        REPORT_PROPERTY_MAP.put(PROP_STAND, new StringReportProperty(PROP_STAND, "lblStand"));
        REPORT_PROPERTY_MAP.put(
            PROP_FLAECHENNUTZUNGSPLAN,
            new StringReportProperty(PROP_FLAECHENNUTZUNGSPLAN, "lblFlaechennutzung"));
        REPORT_PROPERTY_MAP.put(PROP_BEBAUUNGSPLAN, new StringReportProperty(PROP_BEBAUUNGSPLAN, "lblBebauungplan"));
        REPORT_PROPERTY_MAP.put(PROP_WBPF_NUMMER, new StringReportProperty(PROP_WBPF_NUMMER, "lblWbpfNummer"));
        REPORT_PROPERTY_MAP.put(PROP_LAGETYP, new StringReportProperty(PROP_LAGETYP, "lblLagetyp"));
        REPORT_PROPERTY_MAP.put(
            PROP_VORHANDENE_BEBAUUNG,
            new StringReportProperty(PROP_VORHANDENE_BEBAUUNG, "lblVorhandeneBebauung"));
        REPORT_PROPERTY_MAP.put(PROP_TOPOGRAFIE, new StringReportProperty(PROP_TOPOGRAFIE, "lblTopografie"));
        REPORT_PROPERTY_MAP.put(PROP_RESTRIKTIONEN, new StringReportProperty(PROP_RESTRIKTIONEN, "lblRestriktionen"));
        REPORT_PROPERTY_MAP.put(
            PROP_AEUSSERE_ERSCHLIESSUNG,
            new StringReportProperty(PROP_AEUSSERE_ERSCHLIESSUNG, "lblAessereErschl"));
        REPORT_PROPERTY_MAP.put(
            PROP_INNERE_ERSCHLIESSUNG,
            new StringReportProperty(PROP_INNERE_ERSCHLIESSUNG, "lblInnereErschl"));
        REPORT_PROPERTY_MAP.put(PROP_ZENTRENNAEHE, new StringReportProperty(PROP_ZENTRENNAEHE, "lblZentrennaehe"));
        REPORT_PROPERTY_MAP.put(
            PROP_VERFUEGBARKEIT,
            new StringReportProperty(PROP_VERFUEGBARKEIT, "lblVerfuegbarkeit"));
        REPORT_PROPERTY_MAP.put(
            PROP_ART_DER_NUTZUNG,
            new StringReportProperty(PROP_ART_DER_NUTZUNG, "lblArtDerNutzung"));
        REPORT_PROPERTY_MAP.put(
            PROP_REVITALISIERUNG,
            new StringReportProperty(PROP_REVITALISIERUNG, "lblRevitalisierung"));
        REPORT_PROPERTY_MAP.put(
            PROP_WBPF_NACHFOLGENUTZUNG,
            new StringReportProperty(PROP_WBPF_NACHFOLGENUTZUNG, "lblWbpfNn"));
        REPORT_PROPERTY_MAP.put(
            "entwicklungsaussischten",
            new StringReportProperty(
                PROP_ENTWICKLUNGSAUSSSICHTEN,
                "entwicklungsaussischten",
                "lblEntwicklungsausssichten"));
        REPORT_PROPERTY_MAP.put(
            PROP_HANDLUNGSDRUCK,
            new StringReportProperty(PROP_HANDLUNGSDRUCK, "lblHandlungsdruck"));
        REPORT_PROPERTY_MAP.put(
            PROP_AKTIVIERBARKEIT,
            new StringReportProperty(PROP_AKTIVIERBARKEIT, "lblAktivierbarkeit"));
        REPORT_PROPERTY_MAP.put(PROP_STADTBEZIRK, new VirtualReportProperty(PROP_STADTBEZIRK, "lblStadtbezirk") {

                @Override
                protected Object calculateProperty(final CidsBean flaecheBean) {
                    return "Stadtbezirk1"; // todo: richtiger Bezirk
                }
            });
        REPORT_PROPERTY_MAP.put(PROP_GROESSE, new VirtualReportProperty(PROP_GROESSE, "lblFlaechengroesse") {

                @Override
                protected Object calculateProperty(final CidsBean flaecheBean) {
                    final Object geo = flaecheBean.getProperty("geometrie.geo_field");
                    double area = 0.0;

                    if (geo instanceof Geometry) {
                        area = ((Geometry)geo).getArea();
                    }

                    return Math.round(area * 100) / 100.0;
                }
            });
        REPORT_PROPERTY_MAP.put(PROP_EIGENTUEMER, new VirtualReportProperty(PROP_EIGENTUEMER, null) {

                @Override
                protected Object calculateProperty(final CidsBean flaecheBean) {
                    return "privat"; // todo: richtiger Eigentümer
                }
            });
        REPORT_PROPERTY_MAP.put(
            PROP_ZENTRALER_VERSORGUNGSBEREICH,
            new VirtualReportProperty(PROP_ZENTRALER_VERSORGUNGSBEREICH, null) {

                @Override
                protected Object calculateProperty(final CidsBean flaecheBean) {
                    return true; // todo: richtigen Wert ermitteln
                }
            });

        REPORT_PROPERTY_MAP.put("karte_ortho", new VirtualMapReportProperty("KARTE_ORTHO", null, false));
        REPORT_PROPERTY_MAP.put("karte_dgk", new VirtualMapReportProperty("KARTE_DGK", null, true));

        REPORT_PROPERTY_MAP.put(
            PROP_REGIONALPLAN,
            new StringListReportProperty(PROP_REGIONALPLAN, "lblRegionalplan"));
        REPORT_PROPERTY_MAP.put(
            PROP_BISHERIGE_NUTZUNG,
            new StringListReportProperty(PROP_BISHERIGE_NUTZUNG, "lblBisherigeNutzung"));
        REPORT_PROPERTY_MAP.put(
            PROP_UMGEBUNGSNUTZUNG,
            new StringListReportProperty(PROP_UMGEBUNGSNUTZUNG, "lblUmgebungsnutzung"));
        REPORT_PROPERTY_MAP.put(PROP_OEPNV_ANBINDUNG, new StringListReportProperty(PROP_OEPNV_ANBINDUNG, "lblOepnv"));
        REPORT_PROPERTY_MAP.put(
            PROP_BK_GEWERBE_INDUSTRIE,
            new ReportProperty(PROP_BK_GEWERBE_INDUSTRIE, "cbBfGewerbe"));
        REPORT_PROPERTY_MAP.put(PROP_BK_MILITAER, new ReportProperty(PROP_BK_MILITAER, "cbBfMilitaer"));
        REPORT_PROPERTY_MAP.put(PROP_BK_VERKEHR, new ReportProperty(PROP_BK_VERKEHR, "cbBfVerkehr"));
        REPORT_PROPERTY_MAP.put(
            PROP_BK_INFRASTRUKTUR_TECHNISCH,
            new ReportProperty(PROP_BK_INFRASTRUKTUR_TECHNISCH, "cbInfrastrukturTechnisch"));
        REPORT_PROPERTY_MAP.put(
            PROP_BK_INFRASTRUKTUR_SOZIAL,
            new ReportProperty(PROP_BK_INFRASTRUKTUR_SOZIAL, "cbInfrastrukturSozial"));
        REPORT_PROPERTY_MAP.put(PROP_BK_EINZELHANDEL, new ReportProperty(PROP_BK_EINZELHANDEL, "cbFbEinzelhandel"));
        REPORT_PROPERTY_MAP.put(
            PROP_BK_NUTZUNGSAUFGABE,
            new ReportProperty(PROP_BK_NUTZUNGSAUFGABE, "lblNutzungsaufgabe"));
        REPORT_PROPERTY_MAP.put(
            PROP_GEWERBE_PRODUKTIONSORIENTIERT,
            new ReportProperty(PROP_GEWERBE_PRODUKTIONSORIENTIERT, "cbNnGewerbeProdukt"));
        REPORT_PROPERTY_MAP.put(
            PROP_GNN_GEWERBE_DIENSTLEISTUNG,
            new ReportProperty(PROP_GNN_GEWERBE_DIENSTLEISTUNG, "cbGewerbeDienstleistung"));
        REPORT_PROPERTY_MAP.put(PROP_GNN_WOHNEN, new ReportProperty(PROP_GNN_WOHNEN, "cbWohnen"));
        REPORT_PROPERTY_MAP.put(PROP_GNN_FREIRAUM, new ReportProperty(PROP_GNN_FREIRAUM, "cbFreiraum"));
        REPORT_PROPERTY_MAP.put(PROP_GNN_FREIZEIT, new ReportProperty(PROP_GNN_FREIZEIT, "cbFreizeit"));
        REPORT_PROPERTY_MAP.put(PROP_GNN_EINZELHANDEL, new ReportProperty(PROP_GNN_EINZELHANDEL, "cbEinzelhandel"));
        REPORT_PROPERTY_MAP.put(PROP_GNN_SONSTIGES, new StringReportProperty(PROP_GNN_SONSTIGES, "lblGnnSonstiges"));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        for (final String key : REPORT_PROPERTY_MAP.keySet()) {
            System.out.print(", " + key);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  editor              DOCUMENT ME!
     * @param  steckbrieftemplate  DOCUMENT ME!
     */
    public static void markUsedFields(final PfPotenzialflaecheEditor editor, final CidsBean steckbrieftemplate) {
        final String fields = (String)steckbrieftemplate.getProperty("verwendete_flaechenattribute");
        final List<String> usedFields = new ArrayList<String>();

        final StringTokenizer st = new StringTokenizer(fields, ",");

        while (st.hasMoreTokens()) {
            usedFields.add(st.nextToken());
        }

        Collections.sort(usedFields);

        for (final String key : REPORT_PROPERTY_MAP.keySet()) {
            final ReportProperty rp = REPORT_PROPERTY_MAP.get(key);

            try {
                final Field labelField = PfPotenzialflaecheEditor.class.getField(rp.getEditorLabelName());
                final Object o = labelField.get(editor);

                if (o instanceof JComponent) {
                    final JComponent label = (JComponent)o;
                    if (Collections.binarySearch(usedFields, rp.getDbName()) < 0) {
                        label.setFont(label.getFont().deriveFont(Font.BOLD));
                    } else {
                        label.setFont(label.getFont().deriveFont(Font.NORMAL));
                    }
                }
            } catch (Exception ex) {
                LOG.error("Cannot find field " + rp.getEditorLabelName(), ex);
            }
        }
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class PotenzialflaecheReportParameterGenerator
            implements JasperReportDownload.JasperReportParametersGenerator {

        //~ Instance fields ----------------------------------------------------

        private CidsBean cidsBean;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PotenzialflaecheReportParameterGenerator object.
         *
         * @param  cidsBean  DOCUMENT ME!
         */
        public PotenzialflaecheReportParameterGenerator(final CidsBean cidsBean) {
            this.cidsBean = cidsBean;
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public Map generateParamters() {
            final HashMap params = new HashMap();

            for (final String dbProp : REPORT_PROPERTY_MAP.keySet()) {
                final ReportProperty rp = REPORT_PROPERTY_MAP.get(dbProp);
                rp.addParameterToMap(params, cidsBean);
            }

            return params;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class ReportProperty {

        //~ Instance fields ----------------------------------------------------

        protected final String parameterName;
        protected final String editorLabelName;
        private final String dbName;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public ReportProperty(final String parameterName, final String editorLabelName) {
            this.parameterName = parameterName.toUpperCase();
            this.dbName = parameterName;
            this.editorLabelName = editorLabelName;
        }

        /**
         * Creates a new ReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  dbName           DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public ReportProperty(final String parameterName, final String dbName, final String editorLabelName) {
            this.parameterName = parameterName.toUpperCase();
            this.dbName = dbName;
            this.editorLabelName = editorLabelName;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  the dbName
         */
        public String getDbName() {
            return dbName;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  map          DOCUMENT ME!
         * @param  flaecheBean  DOCUMENT ME!
         */
        public void addParameterToMap(final Map map, final CidsBean flaecheBean) {
            map.put(parameterName, flaecheBean.getProperty(dbName));
        }

        /**
         * DOCUMENT ME!
         *
         * @return  the editorLabel
         */
        public String getEditorLabelName() {
            return editorLabelName;
        }

        /**
         * DOCUMENT ME!
         *
         * @param   o  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        protected String toString(final Object o) {
            if (o == null) {
                return null;
            } else {
                return o.toString();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class StringReportProperty extends ReportProperty {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StringReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public StringReportProperty(final String parameterName, final String editorLabelName) {
            super(parameterName, editorLabelName);
        }

        /**
         * Creates a new StringReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  dbName           DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public StringReportProperty(final String parameterName, final String dbName, final String editorLabelName) {
            super(parameterName, dbName, editorLabelName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void addParameterToMap(final Map map, final CidsBean flaecheBean) {
            map.put(parameterName, toString(flaecheBean.getProperty(getDbName())));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public abstract static class VirtualReportProperty extends ReportProperty {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VirtualReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public VirtualReportProperty(final String parameterName, final String editorLabelName) {
            super(parameterName, editorLabelName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void addParameterToMap(final Map map, final CidsBean flaecheBean) {
            map.put(parameterName, calculateProperty(flaecheBean));
        }

        /**
         * DOCUMENT ME!
         *
         * @param   flaecheBean  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        protected abstract Object calculateProperty(CidsBean flaecheBean);
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class VirtualMapReportProperty extends VirtualReportProperty {

        //~ Static fields/initializers -----------------------------------------

        private static final String MAP_URL_DGK =
            "http://geoportal.wuppertal.de:80/deegree/wms?&VERSION=1.1.1&REQUEST=GetMap&BBOX=<cismap:boundingBox>&WIDTH=<cismap:width>&HEIGHT=<cismap:height>&SRS=EPSG:25832&FORMAT=image/png&TRANSPARENT=TRUE&BGCOLOR=0xF0F0F0&EXCEPTIONS=application/vnd.ogc.se_xml&LAYERS=abkf&STYLES=default";
        private static final String MAP_URL_ORT = "http://geoportal.wuppertal.de:80/deegree/wms"
                    + "?REQUEST=GetMap&VERSION=1.1.1&SERVICE=WMS&LAYERS=R102:luftbild201810"
                    + "&BBOX=<cismap:boundingBox>"
                    + "&SRS=EPSG:25832&FORMAT=image/png"
                    + "&WIDTH=<cismap:width>"
                    + "&HEIGHT=<cismap:height>"
                    + "&STYLES=default&EXCEPTIONS=application/vnd.ogc.se_inimage";

        //~ Instance fields ----------------------------------------------------

        private boolean isDgk;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new VirtualMapReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         * @param  isDgk            DOCUMENT ME!
         */
        public VirtualMapReportProperty(final String parameterName, final String editorLabelName, final boolean isDgk) {
            super(parameterName, editorLabelName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void addParameterToMap(final Map map, final CidsBean flaecheBean) {
            map.put(parameterName, calculateProperty(flaecheBean));
        }

        @Override
        protected Object calculateProperty(final CidsBean flaecheBean) {
            return generateOverviewMap(flaecheBean);
        }

        /**
         * DOCUMENT ME!
         *
         * @param   flaecheBean  isDgk DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public Image generateOverviewMap(final CidsBean flaecheBean) {
            try {
                final String urlBackground = (isDgk ? MAP_URL_DGK : MAP_URL_ORT);
                final Geometry geom = (Geometry)flaecheBean.getProperty("geometrie.geo_field");

                if (geom != null) {
                    final XBoundingBox boundingBox = new XBoundingBox(geom);
                    boundingBox.increase(10);
                    boundingBox.setX1(boundingBox.getX1() - 50);
                    boundingBox.setY1(boundingBox.getY1() - 50);
                    boundingBox.setX2(boundingBox.getX2() + 50);
                    boundingBox.setY2(boundingBox.getY2() + 50);

                    final HeadlessMapProvider mapProvider = new HeadlessMapProvider();
                    mapProvider.setCenterMapOnResize(true);
                    mapProvider.setBoundingBox(boundingBox);
                    final SimpleWmsGetMapUrl getMapUrl = new SimpleWmsGetMapUrl(urlBackground);
                    final SimpleWMS simpleWms = new SimpleWMS(getMapUrl);
                    mapProvider.addLayer(simpleWms);
                    final DefaultStyledFeature f = new DefaultStyledFeature();
                    f.setGeometry(geom);
                    f.setHighlightingEnabled(true);
                    f.setLinePaint(Color.RED);
                    f.setLineWidth(3);
                    mapProvider.addFeature(f);

                    return mapProvider.getImageAndWait(72, 300, 250, 150);
                } else {
                    return null;
                }
            } catch (Exception e) {
                LOG.error("Error while retrieving map", e);
                return null;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static class StringListReportProperty extends ReportProperty {

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new StringListReportProperty object.
         *
         * @param  parameterName    DOCUMENT ME!
         * @param  editorLabelName  DOCUMENT ME!
         */
        public StringListReportProperty(final String parameterName, final String editorLabelName) {
            super(parameterName, editorLabelName);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void addParameterToMap(final Map map, final CidsBean flaecheBean) {
            map.put(parameterName,
                collectionToList((Collection)flaecheBean.getProperty(getDbName())));
        }

        /**
         * DOCUMENT ME!
         *
         * @param   c  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        protected String collectionToList(final Collection c) {
            final StringBuilder sb = new StringBuilder();
            boolean firstString = true;

            for (final Object o : c) {
                if (firstString) {
                    firstString = false;
                } else {
                    sb.append(", ");
                }
                sb.append(o.toString());
            }

            return sb.toString();
        }
    }
}
