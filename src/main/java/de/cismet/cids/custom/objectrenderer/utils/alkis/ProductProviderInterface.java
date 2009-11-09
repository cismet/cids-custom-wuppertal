/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.utils.alkis;

/**
 * TODO: implement later when specs are clear! Add methods for
 * List<String> objectIds!
 * @author srichter
 */
public interface ProductProviderInterface {

    public String getPdfProductUrl(String productId, String objectId);

    public String getHtmlProductUrl(String productId, String objectId);
}
