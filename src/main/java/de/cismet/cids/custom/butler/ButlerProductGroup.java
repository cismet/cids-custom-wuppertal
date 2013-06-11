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

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerProductGroup {

    //~ Instance fields --------------------------------------------------------

    String key;
    ArrayList<ButlerProduct> butlerProducts;
    ArrayList<ButlerFormats> butlerFormats;
    ArrayList<ButlerResolution> butlerResolution;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getKey() {
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key  DOCUMENT ME!
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerProduct> getButlerProducts() {
        return butlerProducts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butlerProducts  DOCUMENT ME!
     */
    public void setButlerProducts(final ArrayList<ButlerProduct> butlerProducts) {
        this.butlerProducts = butlerProducts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerFormats> getButlerFormats() {
        return butlerFormats;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butlerFormats  DOCUMENT ME!
     */
    public void setButlerFormats(final ArrayList<ButlerFormats> butlerFormats) {
        this.butlerFormats = butlerFormats;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerResolution> getButlerResolution() {
        return butlerResolution;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butlerResolution  DOCUMENT ME!
     */
    public void setButlerResolution(final ArrayList<ButlerResolution> butlerResolution) {
        this.butlerResolution = butlerResolution;
    }

    @Override
    public String toString() {
        return key;
    }
}
