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

/**
 * TODO: implement later when specs are clear! Add methods for List<String> objectIds!
 *
 * @author   srichter
 * @version  $Revision$, $Date$
 */
public interface ProductProviderInterface {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   productId  DOCUMENT ME!
     * @param   objectId   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getPdfProductUrl(String productId, String objectId);

    /**
     * DOCUMENT ME!
     *
     * @param   productId  DOCUMENT ME!
     * @param   objectId   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    String getHtmlProductUrl(String productId, String objectId);
}
