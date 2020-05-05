/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.butler;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public final class CoordWrapper {

    //~ Instance fields --------------------------------------------------------

    private double middleE;
    private double middleN;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CoordWrapper object.
     *
     * @param  middleE  DOCUMENT ME!
     * @param  middleN  DOCUMENT ME!
     */
    public CoordWrapper(final double middleE, final double middleN) {
        this.middleE = middleE;
        this.middleN = middleN;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMiddleE() {
        return middleE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  middleE  DOCUMENT ME!
     */
    public void setMiddleE(final double middleE) {
        this.middleE = middleE;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getMiddleN() {
        return middleN;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  middleN  DOCUMENT ME!
     */
    public void setMiddleN(final double middleN) {
        this.middleN = middleN;
    }
}
