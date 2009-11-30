/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastblattEditor;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;
import javax.swing.JComponent;

/**
 *
 * @author srichter
 */
public class Alb_baulastblattRenderer extends Alb_baulastblattEditor implements CidsBeanRenderer {

    private String title;

    public Alb_baulastblattRenderer() {
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

    @Override
    public JComponent getTitleComponent() {
        //TODO!
        return null;
    }
}
