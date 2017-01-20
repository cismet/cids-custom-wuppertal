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

import de.cismet.cids.custom.utils.alkis.AlkisConf;
import de.cismet.cids.custom.utils.alkis.AlkisConstants;
import de.cismet.cids.custom.utils.alkis.VermessungsRissReportHelper;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VermessungsRissWebAccessReportHelper extends VermessungsRissReportHelper {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VermessungsRissWebAccessReportHelper object.
     *
     * @param  alkisConf  DOCUMENT ME!
     */
    private VermessungsRissWebAccessReportHelper(final AlkisConf alkisConf) {
        super(alkisConf);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static VermessungsRissReportHelper getInstance() {
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

        private static final VermessungsRissWebAccessReportHelper INSTANCE = new VermessungsRissWebAccessReportHelper(
                AlkisConstants.COMMONS);

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new LazyInitialiser object.
         */
        private LazyInitialiser() {
        }
    }
}
