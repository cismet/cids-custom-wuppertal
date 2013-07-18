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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openide.util.Exceptions;

import java.io.IOException;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ButlerProductInfo {

    //~ Instance fields --------------------------------------------------------

    ArrayList<ButlerProductGroup> butler1ProductGroups;
    ArrayList<ButlerProductGroup> butler2ProductGroups;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerProductGroup> getButler1ProductGroups() {
        return butler1ProductGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  productGroups  DOCUMENT ME!
     */
    public void setButler1ProductGroups(final ArrayList<ButlerProductGroup> productGroups) {
        this.butler1ProductGroups = productGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrayList<ButlerProductGroup> getButler2ProductGroups() {
        return butler2ProductGroups;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  butler2ProductGroups  DOCUMENT ME!
     */
    public void setButler2ProductGroups(final ArrayList<ButlerProductGroup> butler2ProductGroups) {
        this.butler2ProductGroups = butler2ProductGroups;
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
            System.out.println(tester.getButler1ProductGroups());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
