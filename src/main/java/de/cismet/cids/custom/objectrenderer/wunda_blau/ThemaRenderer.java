/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.ThemaEditor;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 *
 * @author thorsten
 */
public class ThemaRenderer extends ThemaEditor implements CidsBeanRenderer{
    private org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(this.getClass());
    private String title="";
    public ThemaRenderer() {
        super(false);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }


}
