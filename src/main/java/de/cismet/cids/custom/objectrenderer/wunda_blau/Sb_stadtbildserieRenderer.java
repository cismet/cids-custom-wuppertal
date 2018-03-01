/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.Sb_stadtbildserieEditor;

import de.cismet.cids.server.connectioncontext.ClientConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   Gilles Baatz
 * @version  $Revision$, $Date$
 */
public class Sb_stadtbildserieRenderer extends Sb_stadtbildserieEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Sb_stadtbildserieRenderer object.
     */
    public Sb_stadtbildserieRenderer() {
        this(ClientConnectionContext.createDeprecated());
    }

    /**
     * Creates a new Sb_stadtbildserieRenderer object.
     *
     * @param  connectionContext  DOCUMENT ME!
     */
    public Sb_stadtbildserieRenderer(final ClientConnectionContext connectionContext) {
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
            "sb_stadtbildserie",
            161078, // id 161078 high res, id 18 = interval
            "Sb_stadtbildserieRenderer",
            1280,
            1024);
    }
}
