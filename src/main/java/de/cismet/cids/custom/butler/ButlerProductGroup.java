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

import de.cismet.cids.custom.utils.butler.ButlerFormat;
import de.cismet.cids.custom.utils.butler.ButlerProduct;
import de.cismet.cids.custom.utils.butler.ButlerResolution;

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
    ArrayList<ButlerFormat> butlerFormats;
    ArrayList<ButlerResolution> butlerResolutions;

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
    public ArrayList<ButlerFormat> getButlerFormats() {
        return butlerFormats;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butlerFormats  DOCUMENT ME!
     */
    public void setButlerFormats(final ArrayList<ButlerFormat> butlerFormats) {
        this.butlerFormats = butlerFormats;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerResolution> getButlerResolutions() {
        return butlerResolutions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butlerResolution  DOCUMENT ME!
     */
    public void setButlerResolutions(final ArrayList<ButlerResolution> butlerResolution) {
        this.butlerResolutions = butlerResolution;
    }

    @Override
    public String toString() {
        return key;
    }
}
