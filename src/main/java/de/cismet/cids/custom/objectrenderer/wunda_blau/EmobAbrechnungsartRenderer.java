/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.EmobAbrechnungsartEditor;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobAbrechnungsartRenderer extends EmobAbrechnungsartEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmobAbrechnungsartRenderer object.
     */
    public EmobAbrechnungsartRenderer() {
        super(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost(
            "WUNDA_BLAU",
            "Administratoren",
            "admin",
            "emob",
            "emobabrechnungsart",
            1,
            "EmobAbrechnungsart",
            1280,
            1024);
    }
}
