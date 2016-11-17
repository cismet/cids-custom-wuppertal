/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.cids.custom.objectrenderer.wunda_blau;

import de.cismet.cids.custom.objecteditors.wunda_blau.Alb_baulastEditor;
import de.cismet.cids.custom.objecteditors.wunda_blau.KkVerfahrenEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class KkVerfahrenRenderer extends KkVerfahrenEditor implements CidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

    private String title;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KkVerfahrenRenderer object.
     */
    public KkVerfahrenRenderer() {
        super(false);
        this.title = "";
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setTitle(final String title) {
        if (title != null) {
            this.title = title;
        }
    }

    @Override
    public String getTitle() {
        return title;
    }
}
