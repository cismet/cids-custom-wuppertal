package de.cismet.cids.custom.wunda_blau.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.middleware.types.Node;
import Sirius.server.search.CidsServerSearch;
import de.cismet.cismap.commons.BoundingBox;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author stefan
 */
public class CidsBaulastGeoSearchStatemen extends CidsServerSearch {

    public enum Result {

        BAULAST, BAULASTBLATT
    }
    //
    private String blattnummer;
    //
    private Result result;
    //
    private boolean gueltig;
    //
    private boolean belastet;
    private boolean beguenstigt;
    //
    private BoundingBox bounds;
    //
    private String gemarkung;
    private String flur;
    private String flurstueck;
    //
    private List<String> arten;

    @Override
    public Collection performServerSearch() {
        try {
            String classes = "6";
            String sql = "";
            if (result == Result.BAULASTBLATT) {
                sql = "select " + classes + " as class_id, b.id as object_id from alb_baulastblatt b where b.blattnummer ~ '^[0]*" + blattnummer + "$'";
                sql += " order by b.blattnummer";
            } else {
                sql = "select " + classes + " as class_id, l.id as object_id from alb_baulast l";
                if (blattnummer != null) {
                    sql += " where l.blattnummer ~ '^[0]*" + blattnummer + "$'";
                }

                if (gemarkung != null && gemarkung.length() > 0) {
                    if (flur != null && flur.length() > 0) {
                        if (flurstueck != null && flurstueck.length() > 0) {
                        }
                    }
                }
                sql += " order by l.blattnummer, l.laufende_nummer";
            }

            getLog().debug("search started");

            MetaService ms = (MetaService) getActiveLoaclServers().get("WUNDA_BLAU");
            ArrayList<ArrayList> result = ms.performCustomSearch(sql);

            ArrayList<Node> aln = new ArrayList<Node>();
            for (ArrayList al : result) {
                int cid = (Integer) al.get(0);
                int oid = (Integer) al.get(1);
                MetaObjectNode mon = new MetaObjectNode("WUNDA_BLAU", oid, cid);
                aln.add(mon);
            }
            return aln;
        } catch (Exception e) {
            getLog().error("Problem", e);
            return null;
        }
    }
}
