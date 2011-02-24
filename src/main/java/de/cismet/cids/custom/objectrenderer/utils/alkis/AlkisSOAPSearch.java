/*
 *  Copyright (C) 2011 stefan
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.aedsicad.aaaweb.service.alkis.catalog.ALKISCatalogServices;
import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.alkis.search.ALKISSearchServices;
import de.aedsicad.aaaweb.service.util.LandParcel;
import de.aedsicad.aaaweb.service.util.Owner;
import java.rmi.RemoteException;

/**
 *
 * @author stefan
 */
public class AlkisSOAPSearch {

    public static void main(String[] args) throws RemoteException {
        SOAPAccessProvider access = new SOAPAccessProvider();
        ALKISSearchServices search = access.getAlkisSearchService();
        ALKISCatalogServices catalog = access.getAlkisCatalogServices();
        ALKISInfoServices info = access.getAlkisInfoService();
        //aIdentityCard,aConfiguration,aSalutation,aForeName,aSurName,aBirthName, aDateOfBirth,aResidence,aAdministrativeUnitId,aMaxSearchTime
        String[] ownersIds = search.searchOwnersWithAttributes(access.getIdentityCard(), access.getService(), null, "Lothar", "Fisch", null, null, null, null, 100000);
//        String[] ownersIds = search.searchOwnersWithAttributes(access.getIdentityCard(), access.getService(), null, null, "Engemann", null, null, null, null, 10000);

        Owner[] owners = info.getOwners(access.getIdentityCard(), access.getService(), ownersIds);
        for (Owner o : owners) {
            System.out.println(o.getForeName() + " " + o.getSurName());
        }
        String[] fstckIds = search.searchParcelsWithOwner(access.getIdentityCard(), access.getService(), ownersIds, null);
        //forceFullInfo = true
        LandParcel[] result = info.getLandParcels(access.getIdentityCard(), access.getService(), fstckIds, true);
        for(LandParcel lp : result) {
            System.out.println(lp.getLocation().getLandParcelCode());
        }
    }
}
