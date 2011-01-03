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

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUtils;

/**
 * DOCUMENT ME!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public final class ProductProvider {

    //~ Static fields/initializers ---------------------------------------------

    private static final String USERNAME_KEY = "user=";
    private static final String PASSWORD_KEY = "password=";
    private static final String SERVICE_KEY = "service=";
    private static final String PRODUCT_KEY = "product=";
    private static final String CERTIFICATION_TYPE_KEY = "certificationType=";
    private static final String CONTENT_TYPE_KEY = "contentType=";
    public static final String CONTENT_TYPE_PDF = "pdf";
    public static final String CONTENT_TYPE_HTML = "html";
    public static final String OBJECT_ID_PREFIX = "id=";
    public static final String OBJECT_LANDPARCEL_PREFIX = "landparcel=";
//    public static final String OBJECT_POINT_PREFIX = "html";

    //~ Instance fields --------------------------------------------------------

    private final String serverURL;
    private final String service;
    private final String usernameValue;
    private final String passwordValue;
    private final String serviceValue;
    private final String certificationTypeValue;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProductProvider object.
     *
     * @param  serverURL               DOCUMENT ME!
     * @param  service                 DOCUMENT ME!
     * @param  usernameValue           DOCUMENT ME!
     * @param  passwordValue           DOCUMENT ME!
     * @param  serviceValue            DOCUMENT ME!
     * @param  certificationTypeValue  DOCUMENT ME!
     */
    public ProductProvider(final String serverURL,
            final String service,
            final String usernameValue,
            final String passwordValue,
            final String serviceValue,
            final String certificationTypeValue) {
        this.serverURL = serverURL;
        this.service = service;
        this.usernameValue = usernameValue;
        this.passwordValue = passwordValue;
        this.serviceValue = serviceValue;
        this.certificationTypeValue = certificationTypeValue;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  productId  DOCUMENT ME!
     * @param  objectId   DOCUMENT ME!
     */
    public void getPdfProduct(final String productId, final String objectId) {
        getProduct(productId, objectId, CONTENT_TYPE_PDF);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productId  DOCUMENT ME!
     * @param  objectId   DOCUMENT ME!
     */
    public void getHtmlProduct(final String productId, final String objectId) {
        getProduct(productId, objectId, CONTENT_TYPE_HTML);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productId     DOCUMENT ME!
     * @param  objectString  DOCUMENT ME!
     * @param  contentType   DOCUMENT ME!
     */
    public void getProduct(final String productId, final String objectString, final String contentType) {
        final String urlString = buildURLString(productId, objectString, contentType);
        ObjectRendererUtils.openURL(urlString);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   productId     DOCUMENT ME!
     * @param   objectString  DOCUMENT ME!
     * @param   contentType   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String buildURLString(final String productId, final String objectString, final String contentType) {
        final StringBuffer urlString = new StringBuffer(serverURL).append(service);
        urlString.append(USERNAME_KEY)
                .append(usernameValue)
                .append("&")
                .append(PASSWORD_KEY)
                .append(passwordValue)
                .append("&")
                .append(SERVICE_KEY)
                .append(serviceValue)
                .append("&")
                .append(PRODUCT_KEY)
                .append(productId)
                .append("&")
                .append(CERTIFICATION_TYPE_KEY)
                .append(certificationTypeValue)
                .append("&")
                .append(CONTENT_TYPE_KEY)
                .append(contentType)
                .append("&")
                .append(objectString);
        return urlString.toString();
    }
}
