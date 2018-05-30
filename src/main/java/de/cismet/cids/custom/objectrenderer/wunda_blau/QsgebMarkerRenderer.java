/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;


import de.cismet.cids.custom.objecteditors.wunda_blau.QsgebMarkerEditor;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class QsgebMarkerRenderer extends QsgebMarkerEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new QsgebMarkerRenderer object.
     */
    public QsgebMarkerRenderer() {
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
            "qsgeb",
            "qsgeb_marker",
            1,
            "QS Gebäude",
            1280,
            1024);
    }
}
