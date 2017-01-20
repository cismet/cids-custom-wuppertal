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
package de.cismet.cids.custom.reports.wunda_blau;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class BillingStatisticsReportProductBean {

    //~ Instance fields --------------------------------------------------------

    private String productname;
    private String amountOfProducts;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getProductname() {
        return productname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productname  DOCUMENT ME!
     */
    public void setProductname(final String productname) {
        this.productname = productname;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAmountOfProducts() {
        return amountOfProducts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  amountOfProducts  DOCUMENT ME!
     */
    public void setAmountOfProducts(final String amountOfProducts) {
        this.amountOfProducts = amountOfProducts;
    }
}
