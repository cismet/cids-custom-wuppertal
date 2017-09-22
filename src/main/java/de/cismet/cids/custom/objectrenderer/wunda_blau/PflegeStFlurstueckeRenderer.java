/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.PflegeStFlurstueckeEditor;

/**
 * DOCUMENT ME!
 *
 * @author   Sandra
 * @version  DOCUMENT ME!
 */
public class PflegeStFlurstueckeRenderer extends PflegeStFlurstueckeEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerRenderer object.
     */
    public PflegeStFlurstueckeRenderer() {
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
            "pflege",
            "pflegestflurstuecke",
            1,
            "Pflegeflächen",
            1280,
            1024);
    }
}
