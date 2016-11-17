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

import de.cismet.cids.custom.objecteditors.wunda_blau.KkKompensationEditor;

import de.cismet.cids.tools.metaobjectrenderer.CidsBeanRenderer;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class KkKompensationRenderer extends KkKompensationEditor implements CidsBeanRenderer {

    //~ Instance fields --------------------------------------------------------

    private String title;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new KkKompensationRenderer object.
     */
    public KkKompensationRenderer() {
        super(false);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getTitle() {
        return this.title = title;
    }

    @Override
    public void setTitle(final String title) {
        if (title != null) {
            this.title = title;
        }
    }
}
