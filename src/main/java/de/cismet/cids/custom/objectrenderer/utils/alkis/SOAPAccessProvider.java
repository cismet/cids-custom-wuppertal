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
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.aedsicad.aaaweb.service.alkis.catalog.ALKISCatalogServices;
import de.aedsicad.aaaweb.service.alkis.catalog.ALKISCatalogServicesServiceLocator;
import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServices;
import de.aedsicad.aaaweb.service.alkis.info.ALKISInfoServicesServiceLocator;
import de.aedsicad.aaaweb.service.alkis.search.ALKISSearchServices;
import de.aedsicad.aaaweb.service.alkis.search.ALKISSearchServicesServiceLocator;

import java.net.URL;

/**
 * TODO: Should be made (lazy) Singleton? - But check about timeouts!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class SOAPAccessProvider {

    //~ Instance fields --------------------------------------------------------

    private final String identityCard;
    private final String service;
    private final ALKISCatalogServices alkisCatalogServices;
    private final ALKISInfoServices alkisInfoService;
    private final ALKISSearchServices alkisSearchService;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SOAPAccessProvider object.
     */
    public SOAPAccessProvider() {
        this(AlkisUtil.COMMONS.USER, AlkisUtil.COMMONS.PASSWORD, AlkisUtil.COMMONS.SERVICE);
    }

    /**
     * Creates a new SOAPAccessProvider object.
     *
     * @param  identityCard  DOCUMENT ME!
     * @param  service       DOCUMENT ME!
     */
    public SOAPAccessProvider(final String identityCard, final String service) {
        this(
            identityCard,
            service,
            AlkisUtil.COMMONS.SERVER
                    + AlkisUtil.COMMONS.CATALOG_SERVICE,
            AlkisUtil.COMMONS.SERVER
                    + AlkisUtil.COMMONS.INFO_SERVICE,
            AlkisUtil.COMMONS.SERVER
                    + AlkisUtil.COMMONS.SEARCH_SERVICE);
    }

    /**
     * Creates a new SOAPAccessProvider object.
     *
     * @param  user      DOCUMENT ME!
     * @param  password  DOCUMENT ME!
     * @param  service   DOCUMENT ME!
     */
    public SOAPAccessProvider(final String user, final String password, final String service) {
        this(user + "," + password, service);
    }

    /**
     * Creates a new SOAPAccessProvider object.
     *
     * @param   identityCard    DOCUMENT ME!
     * @param   service         DOCUMENT ME!
     * @param   catalogService  DOCUMENT ME!
     * @param   infoService     DOCUMENT ME!
     * @param   searchService   DOCUMENT ME!
     *
     * @throws  IllegalStateException  DOCUMENT ME!
     */
    public SOAPAccessProvider(final String identityCard,
            final String service,
            final String catalogService,
            final String infoService,
            final String searchService) {
        this.identityCard = identityCard;
        this.service = service;
        try {
            this.alkisCatalogServices = new ALKISCatalogServicesServiceLocator().getALKISCatalogServices(new URL(
                        catalogService));
            this.alkisInfoService = new ALKISInfoServicesServiceLocator().getALKISInfoServices(new URL(infoService));
            this.alkisSearchService = new ALKISSearchServicesServiceLocator().getALKISSearchServices(new URL(
                        searchService));
        } catch (Exception ex) {
            throw new IllegalStateException("Can not create SOAPAccessProvider", ex);
        }
    }

    /**
     * Creates a new SOAPAccessProvider object.
     *
     * @param  user            DOCUMENT ME!
     * @param  password        DOCUMENT ME!
     * @param  service         DOCUMENT ME!
     * @param  catalogService  DOCUMENT ME!
     * @param  infoService     DOCUMENT ME!
     * @param  searchService   DOCUMENT ME!
     */
    public SOAPAccessProvider(final String user,
            final String password,
            final String service,
            final String catalogService,
            final String infoService,
            final String searchService) {
        this(user + "," + password, service, catalogService, infoService, searchService);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the identityCard
     */
    public String getIdentityCard() {
        return identityCard;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the service
     */
    public String getService() {
        return service;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the alkisCatalogServices
     */
    public ALKISCatalogServices getAlkisCatalogServices() {
        return alkisCatalogServices;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the alkisInfoService
     */
    public ALKISInfoServices getAlkisInfoService() {
        return alkisInfoService;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the alkisSearchService
     */
    public ALKISSearchServices getAlkisSearchService() {
        return alkisSearchService;
    }
}
