/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.MauerEditor;

/**
 *
 * @author daniel
 */
public class MauerRenderer extends MauerEditor {

    public MauerRenderer() {
        super(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void main(final String[] args) throws Exception {
        DevelopmentTools.createRendererInFrameFromRMIConnectionOnLocalhost("WUNDA_BLAU",
                "Administratoren", "admin", "kif", "mauer", 1, "Mauer", 1280, 1024);
    }
}
