/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.EmobLadestationEditor;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class EmobLadestationRenderer extends EmobLadestationEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EmobLadestationRenderer object.
     */
    public EmobLadestationRenderer() {
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
            "emobladestationen",
            1,
            "Ladestationen",
            1280,
            1024);
    }
}
