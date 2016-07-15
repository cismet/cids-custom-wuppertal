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
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.BodenVorkaufsrechtEditor;

/**
 * DOCUMENT ME!
 *
 * @author   verkenis
 * @version  $Revision$, $Date$
 */
public class BodenVorkaufsrechtRenderer extends BodenVorkaufsrechtEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Alb_baulastRenderer object.
     */
    public BodenVorkaufsrechtRenderer() {
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
            "",
            "BODEN_VORKAUFSRECHT",
            20,
            "Boden Vorkaufsrech",
            1000,
            800);
    }
}
