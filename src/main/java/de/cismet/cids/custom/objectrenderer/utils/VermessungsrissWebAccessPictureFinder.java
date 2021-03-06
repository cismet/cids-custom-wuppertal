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
package de.cismet.cids.custom.objectrenderer.utils;

import de.cismet.cids.custom.objectrenderer.utils.alkis.ClientAlkisConf;
import de.cismet.cids.custom.utils.alkis.VermessungsrissPictureFinder;

import de.cismet.security.WebAccessManager;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class VermessungsrissWebAccessPictureFinder extends VermessungsrissPictureFinder {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungsrissWebAccessPictureFinder object.
     */
    public VermessungsrissWebAccessPictureFinder() {
        super(WebAccessManager.getInstance(), ClientAlkisConf.getInstance());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VermessungsrissWebAccessPictureFinder getInstance() {
        return LazyInitialiser.INSTANCE;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private static final class LazyInitialiser {

        //~ Static fields/initializers -----------------------------------------

        private static final VermessungsrissWebAccessPictureFinder INSTANCE =
            new VermessungsrissWebAccessPictureFinder();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
