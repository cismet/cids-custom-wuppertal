/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis;

import de.cismet.cids.custom.objectrenderer.utils.ObjectRendererUIUtils;

/**
 *
 * @author srichter
 */
public final class ProductProvider {

    public ProductProvider(String serverURL, String service, String usernameValue, String passwordValue, String serviceValue, String certificationTypeValue) {
        this.serverURL = serverURL;
        this.service = service;
        this.usernameValue = usernameValue;
        this.passwordValue = passwordValue;
        this.serviceValue = serviceValue;
        this.certificationTypeValue = certificationTypeValue;
    }

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
    private final String serverURL;
    private final String service;
    private final String usernameValue;
    private final String passwordValue;
    private final String serviceValue;
    private final String certificationTypeValue;

    public final void getPdfProduct(String productId, String objectId) {
        getProduct(productId, objectId, CONTENT_TYPE_PDF);
    }

    public final void getHtmlProduct(String productId, String objectId) {
        getProduct(productId, objectId, CONTENT_TYPE_HTML);
    }

    public final void getProduct(String productId, String objectString, String contentType) {
        final String urlString = buildURLString(productId, objectString, contentType);
        ObjectRendererUIUtils.openURL(urlString);
    }

    private final String buildURLString(String productId, String objectString, String contentType) {
        final StringBuffer urlString = new StringBuffer(serverURL).append(service);
        urlString.append(USERNAME_KEY).append(usernameValue).append("&").append(PASSWORD_KEY).append(passwordValue).append("&").append(SERVICE_KEY).append(serviceValue).append("&").append(PRODUCT_KEY).append(productId).append("&").append(CERTIFICATION_TYPE_KEY).append(certificationTypeValue).append("&").append(CONTENT_TYPE_KEY).append(contentType).append("&").append(objectString);
        return urlString.toString();
    }
}
