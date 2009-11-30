/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastEditor;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 *
 * @author srichter
 */
public class Alb_baulastRenderer extends Alb_baulastEditor implements CidsBeanRenderer {

    private String title;

    public Alb_baulastRenderer() {
        super(false);
        this.title = "";
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
        }
    }
}
