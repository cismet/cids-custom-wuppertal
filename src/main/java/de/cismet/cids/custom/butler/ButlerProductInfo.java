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
package de.cismet.cids.custom.butler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.openide.util.Exceptions;

import java.io.IOException;

import java.util.ArrayList;

import de.cismet.cids.custom.objectrenderer.utils.billing.BillingInfo;
import de.cismet.cids.custom.objectrenderer.utils.billing.Product;
import de.cismet.cids.custom.objectrenderer.utils.billing.ProductGroup;
import de.cismet.cids.custom.objectrenderer.utils.billing.Usage;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerProductInfo {

    //~ Instance fields --------------------------------------------------------

    ArrayList<ButlerProductGroup> productGroups;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerProductGroup> getProductGroups() {
        return productGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productGroups  DOCUMENT ME!
     */
    public void setProductGroups(final ArrayList<ButlerProductGroup> productGroups) {
        this.productGroups = productGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        try {
            final ObjectMapper mapper = new ObjectMapper();

            final ButlerProductInfo tester = mapper.readValue(ButlerProductInfo.class.getResourceAsStream(
                        "/de/cismet/cids/custom/butler/productDescription.json"),
                    ButlerProductInfo.class);
            System.out.println(tester.getProductGroups());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
