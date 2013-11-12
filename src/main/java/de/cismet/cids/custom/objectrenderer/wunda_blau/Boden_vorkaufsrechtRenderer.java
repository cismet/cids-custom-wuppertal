/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.client.tools.DevelopmentTools;
import de.cismet.cids.custom.objecteditors.wunda_blau.Boden_vorkaufsrechtEditor;

/**
 *
 * @author verkenis
 */
public class Boden_vorkaufsrechtRenderer extends Boden_vorkaufsrechtEditor {

    
    
    
    /**
     * Creates a new Alb_baulastRenderer object.
     */
    public Boden_vorkaufsrechtRenderer() {
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
