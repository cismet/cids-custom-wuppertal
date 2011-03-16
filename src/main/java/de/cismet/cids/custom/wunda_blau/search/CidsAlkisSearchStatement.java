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
import de.aedsicad.aaaweb.service.alkis.search.ALKISSearchServices;
import de.cismet.cids.custom.objectrenderer.utils.alkis.SOAPAccessProvider;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.navigator.utils.ClassCacheMultiple;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * DOCUMENT ME!
 *
 * @author   stefan
 * @version  $Revision$, $Date$
 */
public class CidsAlkisSearchStatement extends CidsServerSearch {

//    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CidsAlkisSearchStatement.class);
    //~ Instance fields --------------------------------------------------------
    private final String name;
    private final String vorname;
    private final int alkisLandparcelClassId;
    private static final int TIMEOUT = 100000;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new CidsBaulastSearchStatement object.
     *
     * @param  searchInfo  DOCUMENT ME!
     */
    public CidsAlkisSearchStatement(final AlkisSearchInfo searchInfo) {
        final MetaClass mc = ClassCacheMultiple.getMetaClass("WUNDA_BLAU", "ALKIS_LANDPARCEL");
        if (mc != null) {
            alkisLandparcelClassId = mc.getID();
            String lengthTest = searchInfo.getName();
            name = lengthTest.length() > 0 ? lengthTest : null;
            lengthTest = searchInfo.getVorname();
            vorname = lengthTest.length() > 0 ? lengthTest : null;
        } else {
            throw new IllegalStateException("Metaclass not found for Alkis search!");
        }
    }

    //~ Methods ----------------------------------------------------------------
    @Override
    public Collection performServerSearch() {
        List<Node> result = new ArrayList<Node>();
        try {
            final SOAPAccessProvider accessProvider = new SOAPAccessProvider();
            final ALKISSearchServices searchService = accessProvider.getAlkisSearchService();
//            final ALKISInfoServices infoService = accessProvider.getAlkisInfoService();
//            aIdentityCard,aConfiguration,aSalutation,aForeName,aSurName,aBirthName, aDateOfBirth,aResidence,aAdministrativeUnitId,aMaxSearchTime
//            log.debug("ALKIS Search: Start SOAP call for owners");
            final String[] ownersIds = searchService.searchOwnersWithAttributes(accessProvider.getIdentityCard(), accessProvider.getService(), null, vorname, name, null, null, null, null, TIMEOUT);
            if (ownersIds != null) {
//            log.debug("ALKIS Search: Found " + ownersIds.length + " owners");
//            final Owner[] owners = infoService.getOwners(accessProvider.getIdentityCard(), accessProvider.getService(), ownersIds);
//            log.debug("ALKIS Search: Start SOAP call for landparcels");
                final String[] fstckIds = searchService.searchParcelsWithOwner(accessProvider.getIdentityCard(), accessProvider.getService(), ownersIds, null);
                if (fstckIds != null) {
                    final StringBuilder whereClauseBuilder = new StringBuilder(fstckIds.length * 20);
                    for (String lpAId : fstckIds) {
                        if (whereClauseBuilder.length() > 0) {
                            whereClauseBuilder.append(',');
                        }
                        final String escapedCurrentLandparcelCode = StringEscapeUtils.escapeSql(lpAId);
                        whereClauseBuilder.append('\'').append(escapedCurrentLandparcelCode).append('\'');
                    }
                    final String query = "select " + alkisLandparcelClassId + " as class_id, lp.id as object_id from alkis_landparcel lp where lp.alkis_id in (" + whereClauseBuilder + ")";
//            log.debug("ALKIS Search: Start SOAP call for landparcel infos");
//            final LandParcel[] result = infoService.getLandParcels(accessProvider.getIdentityCard(), accessProvider.getService(), fstckIds, false);
//            log.debug("ALKIS Search: SOAP calls done. Starting DB query.");
//            final StringBuilder whereClauseBuilder = new StringBuilder(result.length * 20);
//            for (LandParcel lp : result) {
//                Location lpLocation = lp.getLocation();
//                if (lpLocation != null) {
//                    if (whereClauseBuilder.length() > 0) {
//                        whereClauseBuilder.append(',');
//                    }
//                    final String escapedCurrentLandparcelCode = StringEscapeUtils.escapeSql(lpLocation.getLandParcelCode());
//                    whereClauseBuilder.append('\'').append(escapedCurrentLandparcelCode).append('\'');
//                }
//            }
//            final String query = "select " + alkisLandparcelClassId + " as class_id, lp.id as object_id from alkis_landparcel lp where lp.alkis_id in (" + whereClauseBuilder + ")";
                    getLog().info("Search:\n" + query);
                    final MetaService ms = (MetaService) getActiveLoaclServers().get("WUNDA_BLAU");
                    final List<ArrayList> resultList = ms.performCustomSearch(query);
                    for (final ArrayList al : resultList) {
                        final int cid = (Integer) al.get(0);
                        final int oid = (Integer) al.get(1);
                        final MetaObjectNode mon = new MetaObjectNode("WUNDA_BLAU", oid, cid);
                        result.add(mon);
                    }
                }
            }
        } catch (Exception e) {
            getLog().error("Problem", e);
        }
        return result;
    }
}
