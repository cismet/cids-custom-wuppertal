package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;

import de.cismet.cids.custom.objecteditors.wunda_blau.StrAdrGeplanteAdresseEditor;

/**
 * DOCUMENT ME!
 *
 * @author   sandra
 * @version  $Revision$, $Date$
 */
public class StrAdrGeplanteAdresseRenderer extends StrAdrGeplanteAdresseEditor {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StrAdrGeplanteAdresseRenderer object.
     */
    public StrAdrGeplanteAdresseRenderer() {
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
            "stradr",
            "stradrgeplanteadresse",
            1,
            "Geplante Adressen",
            1280,
            1024);
    }
}
