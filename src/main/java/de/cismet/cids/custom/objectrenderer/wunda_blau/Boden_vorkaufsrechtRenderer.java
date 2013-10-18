/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.Boden_vorkaufsrechtEditor;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 *
 * @author verkenis
 */
public class Boden_vorkaufsrechtRenderer extends Boden_vorkaufsrechtEditor implements CidsBeanRenderer {

    
    private String title;
    
    
    /**
     * Creates a new Alb_baulastRenderer object.
     */
    public Boden_vorkaufsrechtRenderer() {
        super(false);
        this.title = "";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        if (title != null) {
            this.title = title;
        }
    }
    
}
