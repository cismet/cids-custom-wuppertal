/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.utils.billing;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ProductGroupAmount {

    //~ Instance fields --------------------------------------------------------

    String group;
    int amount;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProductGroupAmount object.
     */
    public ProductGroupAmount() {
    }

    /**
     * Creates a new ProductGroupAmount object.
     *
     * @param  group   DOCUMENT ME!
     * @param  amount  DOCUMENT ME!
     */
    public ProductGroupAmount(final String group, final int amount) {
        this.group = group;
        this.amount = amount;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getAmount() {
        return amount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  amount  DOCUMENT ME!
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getGroup() {
        return group;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  group  DOCUMENT ME!
     */
    public void setGroup(final String group) {
        this.group = group;
    }
}
