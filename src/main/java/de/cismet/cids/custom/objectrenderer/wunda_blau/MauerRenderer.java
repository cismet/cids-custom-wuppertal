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

import de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class MauerRenderer extends MauerEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerRenderer object.
     */
    public MauerRenderer() {
        this(ClientConnectionContext.createDeprecated());
    }

    /**
     * Creates a new MauerRenderer object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public MauerRenderer(final ClientConnectionContext connectionContext) {
        super(false, connectionContext);
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
            "kif",
            "mauer",
            1,
            "Mauer",
            1280,
            1024);
    }
}
