/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.Wbf_gebaeudeEditor;
import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 *
 * @author thorsten
 */
public class Wbf_gebaeudeRenderer extends Wbf_gebaeudeEditor implements CidsBeanRenderer{
    private String title;
    public Wbf_gebaeudeRenderer() {
        super(false);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }




}
