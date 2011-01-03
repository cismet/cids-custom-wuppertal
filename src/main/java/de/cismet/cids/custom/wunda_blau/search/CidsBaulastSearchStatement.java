/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.search.CidsServerSearch;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;

//select distinct 666 as class_id, l.id, l.blattnummer, l.laufende_nummer as object_id from alb_baulast l
//, alb_baulast_baulastarten la, alb_baulast_art a
//, (
//select * from alb_baulast_flurstuecke_beguenstigt
//UNION
//select * from alb_baulast_flurstuecke_belastet) as fsj
//, alb_flurstueck_kicker k, flurstueck f
//, geom g
//where
//l.blattnummer ~ '^[0]*1234$'
//and loeschungsdatum is null
//and loeschungsdatum is not null
//and (
//l.id = fsj.baulast_reference and fsj.flurstueck = k.id
//and k.fs_referenz = f.id and f.umschreibendes_rechteck = g.id and g.geo_field && GeometryFromText('') and intersects(g.geo_field,GeometryFromText(''))
//and k.gemarkung = '123' and k.flur = '123' and k.zaehler = '123' and k.nenner = '123')
//and l.id = la.baulast_reference and la.baulast_art = a.id and a.baulast_art in ('art1', 'art2')
//order by blattnummer, laufende_nummer
/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class CidsBaulastSearchStatement extends CidsServerSearch {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Result {

        //~ Enum constants -----------------------------------------------------

        BAULAST, BAULASTBLATT
    }

    //~ Instance fields --------------------------------------------------------

    //
    private String blattnummer;
    //
    private Result result;
    //
    private boolean gueltig;
    private boolean ungueltig;
    //
    private boolean belastet;
    private boolean beguenstigt;
    //
    private String bounds;
    //
    private List<FlurstueckInfo> flurstuecke;
    //
    private String art;
    private final int baulastClassID;
    private final int baulastblattClassID;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CidsBaulastSearchStatement object.
     *
     * @param  searchInfo  DOCUMENT ME!
     */
    public CidsBaulastSearchStatement(final BaulastSearchInfo searchInfo) {
        MetaClass mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALB_BAULAST");
        baulastClassID = mc.getID();
        mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALB_BAULASTBLATT");
        baulastblattClassID = mc.getID();
        this.result = searchInfo.getResult();
        this.blattnummer = searchInfo.getBlattnummer();
        if (blattnummer != null) {
            blattnummer = StringEscapeUtils.escapeSql(blattnummer);
        }
        this.gueltig = searchInfo.isGueltig();
        this.ungueltig = searchInfo.isUngueltig();
        this.beguenstigt = searchInfo.isBeguenstigt();
        this.belastet = searchInfo.isBelastet();
        this.art = searchInfo.getArt();
        if (art != null) {
            art = StringEscapeUtils.escapeSql(art);
        }
        this.bounds = searchInfo.getBounds();
        if (bounds != null) {
            bounds = StringEscapeUtils.escapeSql(bounds);
        }
        this.flurstuecke = searchInfo.getFlurstuecke();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        try {
            final boolean fsSearch = (flurstuecke != null) && (flurstuecke.size() > 0) && (belastet || beguenstigt);
            final int iter = fsSearch ? flurstuecke.size() : 1;
//            boolean fsSearch = gemarkung > -1 && flur != null && zaehler != null && nenner != null;
            String query = "";
            if (result == Result.BAULASTBLATT) {
                query += "select " + baulastblattClassID
                            + " as class_id, b.id as object_id, b.blattnummer from alb_baulastblatt b where b.blattnummer in (select blattnummer from (";
            }
            for (int i = 0; i < iter; ++i) {
                if (i > 0) {
                    query += " UNION ";
                }
                query += " select " + baulastClassID
                            + " as class_id, l.id as object_id, l.blattnummer, l.laufende_nummer from alb_baulast l";
                if ((art != null) && (art.length() > 0)) {
                    query += " , alb_baulast_baulastarten la, alb_baulast_art a";
                }
                if ((belastet || beguenstigt) && (fsSearch || (bounds != null))) {
                    query += " , (";
                    if (beguenstigt) {
                        query += " select * from alb_baulast_flurstuecke_beguenstigt";
                        if (belastet) {
                            query += " UNION";
                        }
                    }
                    if (belastet) {
                        query += " select * from alb_baulast_flurstuecke_belastet";
                    }
                    query += " ) as fsj";
                    query += " , alb_flurstueck_kicker k";
                    if (bounds != null) {
                        query += " , flurstueck f, geom g";
                    }
                }
                query += " where";
                query += " 1 = 1";
                if ((blattnummer != null) && (blattnummer.length() > 0)) {
                    query += " and l.blattnummer ~ '^[0]*" + blattnummer + "$'";
                }
                if (gueltig && ungueltig) {
                } else {
                    if (gueltig) {
                        query += " and loeschungsdatum is null and geschlossen_am is null";
                    } else if (ungueltig) {
                        query += " and (loeschungsdatum is not null or geschlossen_am is not null)";
                    }
                }
                if (fsSearch || (bounds != null)) {
                    query += " and l.id = fsj.baulast_reference and fsj.flurstueck = k.id";
                }
                if (bounds != null) {
                    query += " and k.fs_referenz = f.id and f.umschreibendes_rechteck = g.id";
                    query += " and g.geo_field && GeometryFromText('" + bounds
                                + "') and intersects(g.geo_field,GeometryFromText('" + bounds + "'))";
                }
                if (fsSearch) {
                    final FlurstueckInfo fi = flurstuecke.get(i);
                    query += " and k.gemarkung = '" + fi.gemarkung + "' and k.flur = '"
                                + StringEscapeUtils.escapeSql(fi.flur) + "' and k.zaehler = '"
                                + StringEscapeUtils.escapeSql(fi.zaehler) + "' and k.nenner = '"
                                + StringEscapeUtils.escapeSql(fi.nenner) + "'";
                }
                if ((art != null) && (art.length() > 0)) {
                    query += " and l.id = la.baulast_reference and la.baulast_art = a.id and a.baulast_art = '" + art
                                + "'";
                }
            }
            if (result == Result.BAULASTBLATT) {
                query += " )as x)";
                query += " group by b.blattnummer, class_id, object_id";
                query += " order by b.blattnummer";
            } else {
                query += " group by blattnummer, laufende_nummer, class_id, object_id";
                query += " order by blattnummer, laufende_nummer";
            }

            getLog().info("Search:\n" + query);
            final MetaService ms = (MetaService)getActiveLoaclServers().get("WUNDA_BLAU");
            final List<ArrayList> resultList = ms.performCustomSearch(query);
            final List<Node> aln = new ArrayList<Node>();
            for (final ArrayList al : resultList) {
                final int cid = (Integer)al.get(0);
                final int oid = (Integer)al.get(1);
                final MetaObjectNode mon = new MetaObjectNode("WUNDA_BLAU", oid, cid);
                aln.add(mon);
            }
            return aln;
        } catch (Exception e) {
            getLog().error("Problem", e);
            return null;
        }
    }
}
